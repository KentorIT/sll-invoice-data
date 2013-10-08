/**
 *  Copyright (c) 2013 SLL <http://sll.se/>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package se.sll.invoicedata.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.RatingService;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

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
            delete(oldEntity);
        }

        save(newEntity);

        if (creditCandidate != null) {
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
            String supplierId, String paymentResponsible) {
        List<BusinessEventEntity> bEEntityList = businessEventRepository.findBySupplierIdAndPaymentResponsibleAndPendingIsTrue(
                validate(supplierId, "supplierId"), 
                validate(paymentResponsible, "paymentResponsible"));

        return EntityBeanConverter.fromBEntity(bEEntityList);
    }

    protected String validate(final String data, final String field) {
        mandatory(data, field);
        return data;
    }

    @Override
    public List<InvoiceDataHeader> getAllInvoicedData(String supplierId, String paymentResponsible) {
        List<InvoiceDataEntity> iDEntityList = invoiceDataRepository.findBySupplierIdAndPaymentResponsible(
                validate(supplierId, "supplierId"), 
                validate(paymentResponsible, "paymentResponsible"));
        return EntityBeanConverter.fromIEntity(iDEntityList);
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


    @Override
    public String createInvoiceData(CreateInvoiceDataRequest createInvoiceDataRequest) {
        final InvoiceDataEntity invoiceDataEntity = copyProperties(createInvoiceDataRequest, InvoiceDataEntity.class);

        final List<BusinessEventEntity> entities = businessEventRepository.findByAcknowledgementIdInAndPendingIsTrue(createInvoiceDataRequest.getAcknowledgementIdList());
        int actual = 0;
        for (BusinessEventEntity entity : entities) {
            invoiceDataEntity.addBusinessEventEntity(entity);
            actual++;
        }
        final int expected = createInvoiceDataRequest.getAcknowledgementIdList().size();
        if (expected != actual) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("given event list doesn't match database state: " + actual + ", expected: " + expected); 
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

        for (final BusinessEventEntity businessEventEntity : invoiceDataEntity.getBusinessEventEntities()) {
            invoiceData.getEventList().add(EntityBeanConverter.fromEntity(businessEventEntity));
        }

        return invoiceData;
    }

    @Override
    public List<InvoiceData> listAllInvoiceData(ListInvoiceDataRequest request) {

        if (request.getFromDate() != null && request.getToDate() == null) {
            request.setToDate(CoreUtil.toXMLGregorianCalendar(new Date()));
        } else if (request.getToDate() != null && request.getFromDate() == null) {
            request.setFromDate(CoreUtil.getStartDate());
        }

        List<InvoiceDataEntity> invoiceDataEntityList = invoiceDataRepository.findBetweenDates(
                request.getSupplierId(),
                request.getPaymentResponsible(),
                CoreUtil.toDate(request.getFromDate()),
                CoreUtil.toDate(request.getToDate()));

        List<InvoiceData> invoiceDataList = new ArrayList<InvoiceData>(invoiceDataEntityList.size());
        for (InvoiceDataEntity iDataEntity : invoiceDataEntityList) {
            List<RegisteredEvent> eventList = EntityBeanConverter.fromBEntity(iDataEntity.getBusinessEventEntities());

            InvoiceData invoiceData = EntityBeanConverter.fromIDEntity(iDataEntity);
            invoiceData.getEventList().addAll(eventList);
            invoiceDataList.add(invoiceData);

        }
        return invoiceDataList;
    }
}
