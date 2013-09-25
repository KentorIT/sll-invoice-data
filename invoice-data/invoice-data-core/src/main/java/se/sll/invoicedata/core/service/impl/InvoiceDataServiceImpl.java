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
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.RatingService;


@Service
@Transactional
public class InvoiceDataServiceImpl implements InvoiceDataService {

    @Autowired
    private BusinessEventRepository businessEventRepository;
    
    @Autowired
    private InvoiceDataRepository invoiceDataRepository;
    
    @Autowired
    private RatingService ratingService;

    
    private InvoiceDataServiceImpl save(BusinessEventEntity... entities) {
        businessEventRepository.save(Arrays.asList(entities));
        return this;
    }

    private InvoiceDataServiceImpl remove(BusinessEventEntity... entities) {
        businessEventRepository.save(Arrays.asList(entities));
        return this;
    }

    @Override
    public void registerBusinessEvent(BusinessEventEntity newEntity) {
        rate(validate(newEntity));
        final BusinessEventEntity oldEntity = getBusinessEvent(newEntity.getEventId());
        
        if (oldEntity == null) {
            save(newEntity);
        } else if (oldEntity.isPending()) {
            remove(oldEntity).save(newEntity);
        } else if (!oldEntity.isCredited()) {
            oldEntity.setCredited(true);
            // create credit event.
            final BusinessEventEntity creditEntity = CoreUtil.copyProperties(new BusinessEventEntity(), oldEntity, BusinessEventEntity.class);
            creditEntity.setCredit(true);
            creditEntity.setInvoiceData(null);
            for (ItemEntity itemEntity : oldEntity.getItemEntities()) {
                creditEntity.addItemEntity(CoreUtil.copyProperties(new ItemEntity(), itemEntity, ItemEntity.class));
            }
            save(oldEntity, newEntity, creditEntity);
        } else {
            throw new IllegalStateException("Unpredicted database state detected, for business event: " + newEntity.getEventId());
        }
    }

    @Override
    public BusinessEventEntity getBusinessEvent(String eventId) {
        Sort sort = new Sort(Sort.Direction.DESC, "createdTimestamp");
        List<BusinessEventEntity> list = businessEventRepository.findByEventIdAndCreditIsNull(eventId, sort);
        return (list.size() == 0) ? null : list.get(0);
    }
    
    
    @Override
    public List<BusinessEventEntity> getAllUnprocessedBusinessEvents(
            String supplierId, String paymentResponsible) {
        return businessEventRepository.findBySupplierIdAndPaymentResponsibleAndPendingIsTrue(
                validate(supplierId, "supplierId"), 
                validate(paymentResponsible, "paymentResponsible"));
    }
    
    protected String validate(final String data, final String field) {
        mandatory(data, field);
        return data;
    }
    
    @Override
    public List<InvoiceDataEntity> getAllInvoicedData(String supplierId, String paymentResponsible) {        
        return invoiceDataRepository.findBySupplierIdAndPaymentResponsible(
                validate(supplierId, "supplierId"), 
                validate(paymentResponsible, "paymentResponsible"));
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
     * Rates all items of a {@link BusinessEventEntity}
     * 
     * @param businessEventEntity the business event.
     * @return the rated business event, i.e. price has been set to all items.
     */
    protected BusinessEventEntity rate(BusinessEventEntity businessEventEntity) {
        for (ItemEntity itemEntity : validate(businessEventEntity).getItemEntities()) {
            itemEntity.setPrice(ratingService.rate(itemEntity));
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

    @Override
    public void createInvoiceData(String supplierId) {
        List<BusinessEventEntity> businesEventList = businessEventRepository.findBySupplierIdAndPendingIsTrue(supplierId);
        for (BusinessEventEntity bEE : businesEventList) {
            final InvoiceDataEntity iDE = new InvoiceDataEntity();
            iDE.setCreatedBy(bEE.getAcknowledgedBy());
            iDE.setSupplierId(bEE.getSupplierId());
            
            iDE.addBusinessEventEntity(bEE);
            invoiceDataRepository.save(iDE);
        }
        
    }

    @Override
    public List<BusinessEventEntity> getPendingBusinessEntities(
            String supplierId, List<String> eventIdList) {
        final List<BusinessEventEntity> list = businessEventRepository.findBySupplierIdAndEventIdInAndPendingIsTrue(supplierId, eventIdList);
        if (list.size() != eventIdList.size()) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("The provided list of events doesn't match database state");
        }
        return list;
    }

    @Override
    public void registerInvoiceData(InvoiceDataEntity invoiceDataEntity) {
        invoiceDataRepository.save(invoiceDataEntity);
    }

}
