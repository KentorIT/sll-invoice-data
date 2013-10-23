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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.Range;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
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
public class InvoiceDataServiceImpl implements InvoiceDataService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceDataService.class);
    private static final Logger TRANS_LOG = LoggerFactory.getLogger("TRANS-LOG");

    @Autowired
    private BusinessEventRepository businessEventRepository;

    @Autowired
    private InvoiceDataRepository invoiceDataRepository;

    @Autowired
    private RatingService ratingService;

    //
    private InvoiceDataServiceImpl save(BusinessEventEntity... entities) {
        businessEventRepository.save(Arrays.asList(entities));
        log.debug("saved = {}", entities);
        return this;
    }

    //
    private InvoiceDataServiceImpl delete(BusinessEventEntity... entities) {
        businessEventRepository.delete(Arrays.asList(entities));
        log.debug("deleted = {}", entities);
        return this;
    }

    @Override
    public void registerEvent(Event event) {
        BusinessEventEntity newEntity = EntityBeanConverter.toEntity(event);
        registerBusinessEvent(newEntity);
    }

    //
    protected void registerBusinessEvent(final BusinessEventEntity newEntity) {
        rate(validate(newEntity));

        final BusinessEventEntity oldEntity = businessEventRepository.findByEventIdAndPendingIsTrueAndCreditIsNull(newEntity.getEventId());
        final BusinessEventEntity creditCandidate = businessEventRepository.findByEventIdAndPendingIsNullAndCreditedIsNullAndCreditIsNull(newEntity.getEventId());

        if (oldEntity != null) {
        	TRANS_LOG.info("Deleting previous event(id:" + oldEntity.getEventId() + "), acknowledgementId: " + oldEntity.getAcknowledgementId() 
        			+ " to register the updated event with acknowledgementId:" + newEntity.getAcknowledgementId());
            delete(oldEntity);
        }
        TRANS_LOG.info("Registered an event(id:" + newEntity.getEventId() + "), acknowledgementId:" + newEntity.getAcknowledgementId());
        save(newEntity);

        if (creditCandidate != null) {
    	TRANS_LOG.info("Event already exists! A credit/debit will be triggered on the invoiced data");
            final BusinessEventEntity creditEntity = copyProperties(creditCandidate, BusinessEventEntity.class);
            creditEntity.setCredit(true);
            creditEntity.setInvoiceData(null);
            creditCandidate.setCredited(true);
            for (final ItemEntity itemEntity : creditCandidate.getItemEntities()) {
                final ItemEntity copy = copyProperties(itemEntity, ItemEntity.class);
                // set parent to null to ensure acceptance by the new
                copy.setEvent(null);
                creditEntity.addItemEntity(copy);
            }            
            save(creditCandidate, creditEntity);
        }

    }

    @Override
    public List<RegisteredEvent> getAllUnprocessedBusinessEvents(
            GetInvoiceDataRequest request) {
    	
    	mandatory(request.getSupplierId(), "supplierId");

    	List<BusinessEventEntity> bEEntityList = new ArrayList<BusinessEventEntity>();
    	
    	final Date dateFrom = CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE);
    	final Date dateTo = CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE);
    	
    	if (CoreUtil.isEmpty(request.getPaymentResponsible())) {
    		bEEntityList = businessEventRepository.findBySupplierIdAndPendingIsTrueAndStartTimeBetween(
    				request.getSupplierId(), dateFrom, dateTo);
    	} else {
    		bEEntityList = businessEventRepository.
    				findBySupplierIdAndPendingIsTrueAndPaymentResponsibleAndStartTimeBetween(
    				request.getSupplierId(), request.getPaymentResponsible(),
    				dateFrom, dateTo);
    	}
    	//No requirement to fetch list sorted by date
        return EntityBeanConverter.fromBEntity(bEEntityList);
    }

    protected String validate(final String data, final String field) {
        mandatory(data, field);
        return data;
    }

    //
    private static void mandatory(final String s, final String field) {
        if (s == null || s.length() ==  0) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException(field);            
        }
    }

    //
    private static void mandatory(final Object s, final String field) {
        if (s == null) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException(field);            
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
    protected BusinessEventEntity rate(BusinessEventEntity businessEventEntity) {
        for (ItemEntity itemEntity : validate(businessEventEntity).getItemEntities()) {
            if (itemEntity.getPrice() == null) {
                itemEntity.setPrice(ratingService.rate(itemEntity));
            }
        }
        return businessEventEntity;
    }

    /**
     * Validates business entity.
     * 
     * @param businessEventEntity the entity.
     * @return the same entity reference as passed as argument.
     */
    protected BusinessEventEntity validate(final BusinessEventEntity businessEventEntity) {

        // mandatory fields according to schema
        mandatory(businessEventEntity.getEventId(), "event.eventId");
        mandatory(businessEventEntity.getHealthcareFacility(), "event.healthcareFacility");
        mandatory(businessEventEntity.getSupplierId(), "event.supplierId");
        mandatory(businessEventEntity.getSupplierName(), "event.supplierName");
        mandatory(businessEventEntity.getServiceCode(), "event.serviceCode");        
        mandatory(businessEventEntity.getPaymentResponsible(), "event.paymentResponsible");
        mandatory(businessEventEntity.getHealthCareCommission(), "event.healthCareCommission");
        mandatory(businessEventEntity.getAcknowledgedBy(), "event.acknowledgedBy");
        mandatory(businessEventEntity.getAcknowledgedTime(), "event.acknowledgedTime");
        mandatory(businessEventEntity.getAcknowledgementId(), "event.acknowledgementId");
        mandatory(businessEventEntity.getStartTime(), "event.startTime");
        mandatory(businessEventEntity.getEndTime(), "event.endTime");

        // valid time period
        if (businessEventEntity.getEndTime().before(businessEventEntity.getStartTime())) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("event.endTime is before event.startTime");            
        }

        // mandatory fields according to schema
        final List<ItemEntity> items = businessEventEntity.getItemEntities();
        if (items.size() == 0) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("event.items");            
        }

        // items
        for (final ItemEntity itemEntity : items) {
            mandatory(itemEntity.getDescription(), "item.description");
            mandatory(itemEntity.getItemId(), "item.id");
            mandatory(itemEntity.getEvent(), "item.event");
            final BigDecimal qty = itemEntity.getQty();
            if (qty.floatValue() < 0f || qty.floatValue() > 99999f) {
                throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("item.qty, out of range: " + qty.floatValue());
            }
            if (qty.scale() > 2) {
                throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("item.qty, invalid scale: " + qty.floatValue());                
            }
        }

        return businessEventEntity;
    }

    protected InvoiceDataEntity validate(InvoiceDataEntity invoiceDataEntity) {
        mandatory(invoiceDataEntity.getCreatedBy(), "invoiceData.createdBy");
        mandatory(invoiceDataEntity.getPaymentResponsible(), "invoiceData.paymentResponsible");
        mandatory(invoiceDataEntity.getSupplierId(), "invoiceData.supplierId");
        if (invoiceDataEntity.getBusinessEventEntities().size() == 0) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("invoiceData.events");            
        }

        return invoiceDataEntity;
    }
    
    private BusinessEventEntity validate(BusinessEventEntity entity, CreateInvoiceDataRequest createInvoiceDataRequest) {
    	if (!entity.getSupplierId().equalsIgnoreCase(createInvoiceDataRequest.getSupplierId())) {
    		throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("acknowledgementId is not a part of the same supplier: " + createInvoiceDataRequest.getSupplierId());
    	} else if (!entity.getPaymentResponsible().equalsIgnoreCase(createInvoiceDataRequest.getPaymentResponsible())) {
    		throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("acknowledgementId is not a part of the same paymentResponsible: " + createInvoiceDataRequest.getPaymentResponsible());
    	}
    	
    	return entity;
    }

    @Override
    public String createInvoiceData(CreateInvoiceDataRequest createInvoiceDataRequest) {
    	
    	TRANS_LOG.info("Request for CreateInvoice triggeredBy:" + createInvoiceDataRequest.getCreatedBy() + " for supplier(id:" + createInvoiceDataRequest.getSupplierId() + ")"
    			+ ", acknowledgementIdList size:" + createInvoiceDataRequest.getAcknowledgementIdList().size());
        final InvoiceDataEntity invoiceDataEntity = copyProperties(createInvoiceDataRequest, InvoiceDataEntity.class);

        final List<BusinessEventEntity> entities = businessEventRepository.findByAcknowledgementIdInAndPendingIsTrue(createInvoiceDataRequest.getAcknowledgementIdList());
        int actual = 0;
        for (BusinessEventEntity entity : entities) {        	
            invoiceDataEntity.addBusinessEventEntity(validate(entity, createInvoiceDataRequest));
            actual++;
        }
        final int expected = createInvoiceDataRequest.getAcknowledgementIdList().size();
        if (expected != actual) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("given event list doesn't match database state! entities available: " + actual + ", request contains: " + expected); 
        }
        
        validate(invoiceDataEntity);

        final InvoiceDataEntity saved = invoiceDataRepository.save(invoiceDataEntity);

        return saved.getReferenceId();
    }

    @Override
    public InvoiceData getInvoiceDataByReferenceId(final String referenceId) {

        final Long id = Long.valueOf(validate(referenceId, "referenceId").substring(referenceId.lastIndexOf('.') + 1));

        final InvoiceDataEntity invoiceDataEntity = invoiceDataRepository.findOne(id);

        if (invoiceDataEntity == null) {
            throw InvoiceDataErrorCodeEnum.NOTFOUND_ERROR.createException("invoice data", referenceId); 		    
        }

        final InvoiceData invoiceData = EntityBeanConverter.fromIDEntity(invoiceDataEntity);
        final List<BusinessEventEntity> bEEList = invoiceDataEntity.getBusinessEventEntities();
        for (final BusinessEventEntity businessEventEntity : bEEList) {
            invoiceData.getRegisteredEventList().add(EntityBeanConverter.fromEntity(businessEventEntity));
        }
        
        Range range = new Range();
        range.setStartDate(CoreUtil.toXMLGregorianCalendar(bEEList.get(0).getStartTime()));
        range.setEndDate(CoreUtil.toXMLGregorianCalendar(bEEList.get(bEEList.size() - 1).getEndTime()));
        invoiceData.setRange(range);
        
        return invoiceData;
    }

    @Override
    public List<InvoiceDataHeader> listAllInvoiceData(ListInvoiceDataRequest request) {
    	
    	if (CoreUtil.isEmpty(request.getSupplierId()) && CoreUtil.isEmpty(request.getPaymentResponsible())) {
    		throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("supplierId or paymentResponsible");
    	}
    	
    	List<InvoiceDataEntity> invoiceDataEntityList = findByCriteria(request);    	
    	List<InvoiceDataHeader> invoiceDataList = new ArrayList<InvoiceDataHeader>(invoiceDataEntityList.size());
    	
        for (InvoiceDataEntity iDataEntity : invoiceDataEntityList) {
            List<RegisteredEvent> eventList = EntityBeanConverter.fromBEntity(iDataEntity.getBusinessEventEntities());

            InvoiceDataHeader invoiceDataHeader = copyProperties(iDataEntity, InvoiceDataHeader.class);
            Range range = new Range();
            range.setStartDate(eventList.get(0).getStartTime());
            range.setEndDate(eventList.get(eventList.size() - 1).getEndTime());
            invoiceDataHeader.setRange(range);
            invoiceDataList.add(invoiceDataHeader);

        }
        return invoiceDataList;
    }
    
    /**
     * Finds by criteria: supplierId, paymentResponsible or date range
     * Date: fromDate - if null then 1970 01 01
     * toDate: - if null then current year + 100 years
     * @param request
     * @return
     */
    private List<InvoiceDataEntity> findByCriteria(ListInvoiceDataRequest request) {
    	
        final Date dateFrom = CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE);
        final Date dateTo = CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE);

    	List<InvoiceDataEntity> invoiceDataEntityList = new ArrayList<InvoiceDataEntity>();
    	
    	if (request.getSupplierId() != null && request.getPaymentResponsible() != null) {
    		invoiceDataEntityList = invoiceDataRepository.findBySupplierIdAndPaymentResponsibleAndCreatedTimeBetween(
    				request.getSupplierId(),
                    request.getPaymentResponsible(),
                    dateFrom, dateTo);
    	} else  if (request.getSupplierId() != null) {
    		invoiceDataEntityList = invoiceDataRepository.findBySupplierIdAndCreatedTimeBetween(
    				request.getSupplierId(),
                    dateFrom, dateTo);
    	} else if (request.getPaymentResponsible() != null) {
    		invoiceDataEntityList = invoiceDataRepository.findByPaymentResponsibleAndCreatedTimeBetween(
    				request.getPaymentResponsible(),
                    dateFrom, dateTo);
    	}
    	return invoiceDataEntityList;
    }
}
