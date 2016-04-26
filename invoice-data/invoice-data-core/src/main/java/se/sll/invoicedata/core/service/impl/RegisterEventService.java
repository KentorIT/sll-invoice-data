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

import static se.sll.invoicedata.core.pojo.mapping.LocalMapper.copyToInvoiceDataEntity;
import static se.sll.invoicedata.core.util.CoreUtil.copyProperties;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.DiscountItem;
import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.ReferenceItem;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.DiscountItemEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.entity.ReferenceItemEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.pojo.mapping.EntityBeanConverter;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.RatingService;
import se.sll.invoicedata.core.service.impl.register.BusinessEventEntityState;
import se.sll.invoicedata.core.util.CoreUtil;

@Service
@Transactional
public class RegisterEventService extends ValidationService {
	
	private static final Logger TX_LOG = LoggerFactory.getLogger("TX-API");
	private static final Logger log = LoggerFactory.getLogger(RegisterEventService.class);
	
	@Autowired
    private RatingService ratingService;
	
	@Autowired
    private BusinessEventRepository businessEventRepository;
	
	@Autowired
    private InvoiceDataRepository invoiceDataRepository;
	
	
	
	/**
	 * Used by RegisterEvent /v1 and /v2 (v2 calls v1)
	 * @param event
	 * @return
	 */
	public synchronized String registerEventAsBusinessEntity(Event event) {
		validateForAnyDuplicateDiscountItems(event);
    	final BusinessEventEntity businessEventEntity = EntityBeanConverter.toBusinessEventEntity(event);
    	validateBusinessEventWithItemList(businessEventEntity);
    	addDiscountItemsToBusinessEventEntity(businessEventEntity, event.getDiscountItemList());
    	
    	final BusinessEventEntityState entityState = processRequestForEventRegistration(businessEventEntity);
        registerBusinessEvent(entityState, event.getDiscountItemList());
        InvoiceDataEntity invoiceDataEntity = getExistingInvoiceIfAvailableOrCreateNew(event);
		invoiceDataEntity.addBusinessEventEntity(businessEventEntity);
		
		if (entityState.hasCreditEntity()) {
			BusinessEventEntity creditEntity = getPreviouslyProcessedEntityAsCreditEntity(entityState);
			invoiceDataEntity.addBusinessEventEntity(creditEntity);
		}
		
		final InvoiceDataEntity saved = invoiceDataRepository.save(invoiceDataEntity);
        invoiceDataRepository.flush();
        return saved.getReferenceId();
	}
	
	private BusinessEventEntityState processRequestForEventRegistration(final BusinessEventEntity businessEventEntity) {
		BusinessEventEntityState entityState = new BusinessEventEntityState(businessEventEntity.getEventId());
		entityState.setNewRegisteredEntity(businessEventEntity);
		entityState.setExistingRegisteredEntity(businessEventRepository.findByEventIdAndPendingIsTrueAndCreditIsNull(entityState.getEventId()));
		entityState.setExistingProcessedRegisteredEntity(businessEventRepository.findByEventIdAndPendingIsNullAndCreditedIsNullAndCreditIsNull(entityState.getEventId()));
		
		return entityState;
	}
	
	private InvoiceDataEntity getExistingInvoiceIfAvailableOrCreateNew(final Event event) {
		
		List<InvoiceDataEntity> invoiceDataEntityList = invoiceDataRepository.
				findBySupplierIdAndPaymentResponsibleAndCostCenterAndPendingIsTrue(event.getSupplierId(), event.getPaymentResponsible(), event.getCostCenter());
		
		InvoiceDataEntity invoiceDataEntity = null;
		
		if (invoiceDataEntityList.size() == 1) {
			invoiceDataEntity = invoiceDataEntityList.get(0);
		} else if (invoiceDataEntityList.isEmpty()) {
			invoiceDataEntity = copyToInvoiceDataEntity(event);
		} else if (invoiceDataEntityList.size() > 1) {
			throw InvoiceDataErrorCodeEnum.ILLEGAL_STATE_DUPLICATE_DRAFT_VERSIONS_OF_INVOICES.createException("Contact system administrator");
		}
		return invoiceDataEntity;
	}
	
	private void registerBusinessEvent(final BusinessEventEntityState entityState, List<DiscountItem> discountItemList) {
        rate(entityState.getNewRegisteredEntity(), discountItemList);

        if (entityState.getExistingRegisteredEntity() != null) {
            TX_LOG.info("Deleting previous event(id:" + entityState.getEventId() + "), acknowledgementId: " + 
            		entityState.getExistingRegisteredEntity().getAcknowledgementId() 
                    + " to register the updated event with acknowledgementId:" + entityState.getNewRegisteredEntity().getAcknowledgementId());
            entityState.getExistingRegisteredEntity().getInvoiceData().removeBusinessEventEntity(entityState.getExistingRegisteredEntity());
            delete(entityState.getExistingRegisteredEntity());
        }
        TX_LOG.info("Registered an event(id:" + entityState.getEventId() + "), acknowledgementId:" + entityState.getNewRegisteredEntity().getAcknowledgementId());
        save(entityState.getNewRegisteredEntity());
    }
	
	private BusinessEventEntity getPreviouslyProcessedEntityAsCreditEntity(final BusinessEventEntityState entityState) {
		BusinessEventEntity creditEntity = null;
		
		if (entityState.getExistingProcessedRegisteredEntity() != null) {
            TX_LOG.info("Event already exists! A credit/debit will be triggered on the invoiced data");
            creditEntity = createCreditEntity(entityState.getExistingProcessedRegisteredEntity());
            save(entityState.getExistingProcessedRegisteredEntity(), creditEntity);
        }
		
		return creditEntity;
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
        for (ItemEntity itemEntity : businessEventEntity.getItemEntities()) {
        	
        	if (itemEntity.getPrice() == null) {
        		BigDecimal calculatedPrice = ratingService.rate(itemEntity);
        		itemEntity.setPrice(calculatedPrice);        		     		
        	}
        }
        
        return businessEventEntity;
    }
    
    private void addDiscountItemsToBusinessEventEntity(BusinessEventEntity businessEventEntity, List<DiscountItem> discountItemList) {
    	if (CoreUtil.ifDiscountItemExists(discountItemList)) {
	    	for (DiscountItem discountItem : discountItemList) {
	    		DiscountItemEntity discountItemEntity = CoreUtil.copyProperties(discountItem, DiscountItemEntity.class);
	    		for (ReferenceItem referenceItem : discountItem.getReferenceItemList()) {
	    			ReferenceItemEntity referenceItemEntity = CoreUtil.copyProperties(referenceItem, ReferenceItemEntity.class);
	    			discountItemEntity.addReferenceItemEntity(referenceItemEntity);
	    		}
	    		businessEventEntity.addDiscountItemEntity(discountItemEntity);    		
	    	}
    	}
    }
    
    /**
     * Step 1: Copy properties from the old entity which is already invoiced
     * Step 2: Mark it as to be credit (see InvoiceData calcDerviedValues())
     * Step 3: Set credited to true on old entity
     * @param creditCandidate
     * @return
     */
    BusinessEventEntity createCreditEntity(
			final BusinessEventEntity creditCandidate) {
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
		return creditEntity;
	}
    
    private void delete(BusinessEventEntity... entities) {
        businessEventRepository.delete(Arrays.asList(entities));
        log.debug("deleted = {}", entities);
    }
    
    private void save(BusinessEventEntity... entities) {
        businessEventRepository.save(Arrays.asList(entities));
        log.debug("saved = {}", entities);
    }
}
