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
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.entity.ReferenceItemEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.pojo.mapping.EntityBeanConverter;
import se.sll.invoicedata.core.service.RatingService;
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
	
	public void registerEventAsBusinessEntity(Event event) {
		validateForAnyDuplicateDiscountItems(event);
    	final BusinessEventEntity businessEventEntity = EntityBeanConverter.toBusinessEventEntity(event);
    	addDiscountItemsToBusinessEventEntity(businessEventEntity, event.getDiscountItemList());
        registerBusinessEvent(businessEventEntity, event.getDiscountItemList());
	}
	
	private void registerBusinessEvent(final BusinessEventEntity newEntity, List<DiscountItem> discountItemList) {
        rate(validateBusinessEventWithItemList(newEntity), discountItemList);

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
