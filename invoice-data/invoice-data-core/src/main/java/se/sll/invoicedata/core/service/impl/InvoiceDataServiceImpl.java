/**
 * Copyright (c) 2013 SLL. <http://sll.se>
 *
 * This file is part of Invoice-Data.
 *
 *     Invoice-Data is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Invoice-Data is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Invoice-Data.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */

package se.sll.invoicedata.core.service.impl;

import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.DiscountItem;
import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import se.sll.invoicedata.core.jmx.StatusBean;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.entity.ItemType;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.RatingService;

/**
 * Implements invoice data service.
 * 
 * @author Peter
 *
 */
@Service
@Transactional
public class InvoiceDataServiceImpl extends InvoiceDataBaseService implements InvoiceDataService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceDataService.class);
    private static final Logger TX_LOG = LoggerFactory.getLogger("TX-API");

    @Value("${event.maxFindResultSize:30000}")
    private int eventMaxFindResultSize;

    @Autowired
    private BusinessEventRepository businessEventRepository;

    @Autowired
    private InvoiceDataRepository invoiceDataRepository;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private StatusBean statusBean;
    
    @Autowired
    private LockService lock;
    
    @Override
    public void registerEvent(Event event) {
        final String name = event.getEventId();
        
        if (!lock.acquire(name)) {
            throw InvoiceDataErrorCodeEnum.TECHNICAL_ERROR.createException("Event \"" + name + "\" currently is updated by another user");
        }
        try {
        	validateForAnyDuplicateDiscountItems(event);
        	final BusinessEventEntity businessEventEntity = EntityBeanConverter.toEntity(event);
            registerBusinessEvent(businessEventEntity, event.getDiscountItemList());
        } finally {
            lock.release(name);
        }
    }
    
    @Override
    public List<RegisteredEvent> getAllUnprocessedBusinessEvents(
            GetInvoiceDataRequest request) {

        mandatory(request.getSupplierId(), "supplierId");

        List<BusinessEventEntity> bEEntityList;
        
        log.debug(request.getSupplierId() + " from: " + request.getFromDate() + " to: " + request.getToDate());

        final Date dateFrom = CoreUtil.floorDate(CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE));
        final Date dateTo = CoreUtil.ceilDate(CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE));

       // max size
        final PageRequest pageRequest = new PageRequest(0, eventMaxFindResultSize+1);

        if (CoreUtil.isEmpty(request.getPaymentResponsible())) {
            bEEntityList = businessEventRepository.findBySupplierIdAndPendingIsTrueAndStartTimeBetween(
                    request.getSupplierId(), dateFrom, dateTo, pageRequest);
        } else {
            bEEntityList = businessEventRepository.
                    findBySupplierIdAndPendingIsTrueAndPaymentResponsibleAndStartTimeBetween(
                            request.getSupplierId(), request.getPaymentResponsible(),
                            dateFrom, dateTo, pageRequest);
        }
        
        if (bEEntityList.size() >= eventMaxFindResultSize) {
            throw InvoiceDataErrorCodeEnum.LIMIT_ERROR.createException(eventMaxFindResultSize, "please narrow down search criterias");
        }
        
        //No requirement to fetch list sorted by date
        return EntityBeanConverter.fromBEntity(bEEntityList);
    }
    
    @Override
    public String createInvoiceData(CreateInvoiceDataRequest createInvoiceDataRequest) {
    	
    	validate(createInvoiceDataRequest);
    	
        TX_LOG.info("Request for CreateInvoice triggeredBy:" + createInvoiceDataRequest.getCreatedBy() + " for supplier(id:" + createInvoiceDataRequest.getSupplierId() + ")"
                + ", acknowledgementIdList size:" + createInvoiceDataRequest.getAcknowledgementIdList().size());

        
        final List<String> idList = createInvoiceDataRequest.getAcknowledgementIdList();
        
        if (!lock.acquire(idList)) {
            throw InvoiceDataErrorCodeEnum.TECHNICAL_ERROR.createException("Events \"" + idList + "\" currently is updated by another user");
        }
        statusBean.start("InvoiceDataService.createInvoiceData()");
        try {
            final InvoiceDataEntity invoiceDataEntity = copyProperties(createInvoiceDataRequest, InvoiceDataEntity.class);
            final List<BusinessEventEntity> entities = findByAcknowledgementIdInAndPendingIsTrue(idList);

            getValidInvoiceDataEntity(createInvoiceDataRequest,
					invoiceDataEntity, entities);

            final InvoiceDataEntity saved  = save(invoiceDataEntity);

            return saved.getReferenceId();
        } finally {
            lock.release(idList);
            statusBean.stop();
        }
    }

    @Override
    public List<InvoiceDataHeader> listAllInvoiceData(ListInvoiceDataRequest request) {
        if (CoreUtil.isEmpty(request.getSupplierId()) && CoreUtil.isEmpty(request.getPaymentResponsible())) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("supplierId or paymentResponsible");
        }

        statusBean.start("InvoiceDataService.listAllInvoiceData()");
        try {
            return getInvoiceDataHeader(findByCriteria(request));

        } finally {
            statusBean.stop();
        }
    }

    @Override
    public InvoiceData getInvoiceDataByReferenceId(final String referenceId) {
        return getInvoiceData(referenceId, invoiceDataRepository.findOne(extractId(referenceId)));
    }
    
    //
    @Override
    public int getEventMaxFindResultSize() {
        return eventMaxFindResultSize;
    }
        
    private InvoiceDataServiceImpl save(BusinessEventEntity... entities) {
        businessEventRepository.save(Arrays.asList(entities));
        log.debug("saved = {}", entities);
        return this;
    }

    private InvoiceDataServiceImpl delete(BusinessEventEntity... entities) {
        businessEventRepository.delete(Arrays.asList(entities));
        log.debug("deleted = {}", entities);
        return this;
    }

    private void registerBusinessEvent(final BusinessEventEntity newEntity, List<DiscountItem> discountItemList) {
        rate(validateBusinessEventWithItemList(newEntity), discountItemList);
        addDiscountItemsToBusinessEventEntity(newEntity, discountItemList);

        final BusinessEventEntity oldEntity = businessEventRepository.findByEventIdAndPendingIsTrueAndCreditIsNull(newEntity.getEventId());
        final BusinessEventEntity creditCandidate = businessEventRepository.findByEventIdAndPendingIsNullAndCreditedIsNullAndCreditIsNull(newEntity.getEventId());

        if (oldEntity != null) {
            TX_LOG.info("Deleting previous event(id:" + oldEntity.getEventId() + "), acknowledgementId: " + oldEntity.getAcknowledgementId() 
                    + " to register the updated event with acknowledgementId:" + newEntity.getAcknowledgementId());
            delete(oldEntity);
        }
        TX_LOG.info("Registered an event(id:" + newEntity.getEventId() + "), acknowledgementId:" + newEntity.getAcknowledgementId());
        save(newEntity);

        if (creditCandidate != null) {
            TX_LOG.info("Event already exists! A credit/debit will be triggered on the invoiced data");
            save(creditCandidate, createCreditEntity(creditCandidate));
        }
    }

    /**
     * Rates all items of a {@link BusinessEventEntity} <p>
     * 
     * If a price already exists then rating is considered as carried out by the 
     * service consumer.
     * 
     * @param businessEventEntity the business event.
     * @return the rated business event, i.e. price has been set to all items.
     */
    private BusinessEventEntity rate(BusinessEventEntity businessEventEntity, List<DiscountItem> discountItemList) {
        for (ItemEntity itemEntity : validateBusinessEventWithItemList(businessEventEntity).getItemEntities()) {
        	
        	if (itemEntity.getPrice() == null) {
        		BigDecimal calculatedPrice = ratingService.rate(itemEntity);
        		DiscountItem discountItem = getDiscountItemById(discountItemList, itemEntity.getItemId());
        		
        		if (discountItem != null) {
        			double discount = (discountItem.getDiscountInPercent().intValue() * calculatedPrice.doubleValue()) / 100;
        			discountItem.setDiscountedPrice(new BigDecimal(discount));
        			calculatedPrice = new BigDecimal(calculatedPrice.doubleValue() - discount);
        		}
        		itemEntity.setPrice(calculatedPrice);
        	} 
        }
        
        return businessEventEntity;
    }
    
    private void addDiscountItemsToBusinessEventEntity(BusinessEventEntity businessEventEntity, List<DiscountItem> discountItemList) {
    	
    	for (DiscountItem discountItem : discountItemList) {
    		ItemEntity itemEntity = CoreUtil.copyProperties(discountItem, ItemEntity.class);
    		itemEntity.setPrice(discountItem.getDiscountedPrice());
    		itemEntity.setItemType(ItemType.DISCOUNT);
    		itemEntity.setQty(new BigDecimal(1));
    		
    		businessEventEntity.addItemEntity(itemEntity);
    	}
    }

    private List<BusinessEventEntity> findByAcknowledgementIdInAndPendingIsTrue(final List<String> list) {
        statusBean.start("InvoiceDataService.findByAcknowledgementIdInAndPendingIsTrue()");
        try {
            return businessEventRepository.findByAcknowledgementIdInAndPendingIsTrue(list);
        } finally {
            statusBean.stop();
        }
    }

    private InvoiceDataEntity save(InvoiceDataEntity invoiceDataEntity) {
        statusBean.start("InvoiceDataService.save()");
        try {
            final InvoiceDataEntity saved = invoiceDataRepository.save(invoiceDataEntity);
            invoiceDataRepository.flush();
            return saved;
        } finally {
            statusBean.stop();  
        }
    }

    /**
     * Finds by criteria: supplierId, paymentResponsible or date range
     * Date: fromDate - if null then 1970 01 01
     * toDate: - if null then current year + 100 years
     * @param request
     * @return List<InvoiceDataEntity>
     */
    private List<InvoiceDataEntity> findByCriteria(ListInvoiceDataRequest request) {

        statusBean.start("InvoiceDataService.findByCriteria()");
        try {
            final Date dateFrom = CoreUtil.floorDate(CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE));
            final Date dateTo = CoreUtil.ceilDate(CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE));
            
            List<InvoiceDataEntity> invoiceDataEntityList = new ArrayList<InvoiceDataEntity>();

            if (request.getSupplierId() != null && request.getPaymentResponsible() != null) {            	
                invoiceDataEntityList = invoiceDataRepository.getInvoiceDataBySupplierIdAndPaymentResponsibleBetweenDates(
                        request.getSupplierId(),
                        request.getPaymentResponsible(),
                        dateFrom, dateTo);
            } else  if (request.getSupplierId() != null) {            	
                invoiceDataEntityList = invoiceDataRepository.getInvoiceDataBySupplierIdBetweenDates(
                        request.getSupplierId(),
                        dateFrom, dateTo);
            } else if (request.getPaymentResponsible() != null) {
                invoiceDataEntityList = invoiceDataRepository.getInvoiceDataByPaymentResponsibleBetweenDates(
                        request.getPaymentResponsible(),
                        dateFrom, dateTo);
            }
            return invoiceDataEntityList;
        } finally {
            statusBean.stop();
        }
    }
}
