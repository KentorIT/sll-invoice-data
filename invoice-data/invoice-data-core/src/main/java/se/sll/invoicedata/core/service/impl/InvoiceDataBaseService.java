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

/**
 * 
 */
package se.sll.invoicedata.core.service.impl;

import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import riv.sll.invoicedata._1.DiscountItem;
import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.ReferenceItem;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;

/**
 * @author muqkha
 *
 */
public class InvoiceDataBaseService {
	
	void mandatory(final String s, final String field) {
		if (s == null || s.length() == 0) {
			throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException(field);
		}
	}

	void mandatory(final Object s, final String field) {
		if (s == null) {
			throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException(field);
		}
	}

	private String validate(final String data, final String field) {
        mandatory(data, field);
        return data;
    }
	
	void validateForAnyDuplicateDiscountItems(final Event event) {
		
		if (CoreUtil.ifDiscountItemExists(event.getDiscountItemList())) {
			for (DiscountItem discountItem : event.getDiscountItemList()) {
				Collections.sort(discountItem.getReferenceItemList(), new Comparator<ReferenceItem>() {
					public int compare(ReferenceItem refItem1, ReferenceItem refItem2) {
						if (refItem1.getRefItemId().equals(refItem2.getRefItemId())) {
							throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("event.discountItems, duplicate discount items identified. Check item id");
						}
						return refItem1.getRefItemId().compareTo(refItem2.getRefItemId());
				    }
				});					
			}
		}
	}
       
    /**
     * Validates business entity.
     * 
     * @param businessEventEntity the entity.
     * @return the same entity reference as passed as argument.
     */
    BusinessEventEntity validateBusinessEventWithItemList(final BusinessEventEntity businessEventEntity) {

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
        
        validateItemListForNullOrEmpty(businessEventEntity.getItemEntities());
        validateEachFieldInItemList(businessEventEntity.getItemEntities());
        validateIfItemsNotDuplicate(new ArrayList<ItemEntity>(businessEventEntity.getItemEntities()));

        return businessEventEntity;
    }
    
    void validate(final CreateInvoiceDataRequest createInvoiceDataRequest) {
    	mandatory(createInvoiceDataRequest.getSupplierId(), "supplierId");
    	mandatory(createInvoiceDataRequest.getPaymentResponsible(), "paymentResponsible");
    	mandatory(createInvoiceDataRequest.getCreatedBy(), "createdBy");
    	mandatory(createInvoiceDataRequest.getAcknowledgementIdList(), "acknowledgementIdList");
    }
    
    private void validateItemListForNullOrEmpty(final List<ItemEntity> items) {
    	if (items == null || items.isEmpty()) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("event.items");            
        }
    }

	private void validateEachFieldInItemList(final List<ItemEntity> items) {
				
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
	}
	
	private void validateIfItemsNotDuplicate(final List<ItemEntity> itemEntityList) {
		Collections.sort(itemEntityList, new Comparator<ItemEntity>() {
			public int compare(ItemEntity itemEntity1, ItemEntity itemEntity2) {
				if (itemEntity1.getItemId().equals(itemEntity2.getItemId())) {
					throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("event.discountitems, duplicate items identified. Check item ids");
				}
				return itemEntity1.getItemId().compareTo(itemEntity2.getItemId());
		    }
		});		
	}

    InvoiceDataEntity validate(InvoiceDataEntity invoiceDataEntity) {
        mandatory(invoiceDataEntity.getCreatedBy(), "invoiceData.createdBy");
        mandatory(invoiceDataEntity.getPaymentResponsible(), "invoiceData.paymentResponsible");
        mandatory(invoiceDataEntity.getSupplierId(), "invoiceData.supplierId");
        if (invoiceDataEntity.getBusinessEventEntities().size() == 0) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("invoiceData.events");            
        }

        return invoiceDataEntity;
    }
    
    BusinessEventEntity validate(BusinessEventEntity entity, CreateInvoiceDataRequest createInvoiceDataRequest) {
        if (!entity.getSupplierId().equalsIgnoreCase(createInvoiceDataRequest.getSupplierId())) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("acknowledgementId is not a part of the same supplier: " + createInvoiceDataRequest.getSupplierId());
        } else if (!entity.getPaymentResponsible().equalsIgnoreCase(createInvoiceDataRequest.getPaymentResponsible())) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("acknowledgementId is not a part of the same paymentResponsible: " + createInvoiceDataRequest.getPaymentResponsible());
        }

        return entity;
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
    
	InvoiceDataEntity getValidInvoiceDataEntity(
			CreateInvoiceDataRequest createInvoiceDataRequest,
			final InvoiceDataEntity invoiceDataEntity,
			final List<BusinessEventEntity> entities) {
		int actual = 0;
		for (final BusinessEventEntity entity : entities) {        	
		    invoiceDataEntity.addBusinessEventEntity(validate(entity, createInvoiceDataRequest));
		    actual++;
		}
		final int expected = createInvoiceDataRequest.getAcknowledgementIdList().size();
		if (expected != actual) {
		    throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("given event list doesn't match database state! entities available: " + actual + ", request contains: " + expected); 
		}
		
		return validate(invoiceDataEntity);
	}
	
	InvoiceData getInvoiceData(final String referenceId, final InvoiceDataEntity invoiceDataEntity) {
		if (invoiceDataEntity == null) {
            throw InvoiceDataErrorCodeEnum.NOTFOUND_ERROR.createException("invoice data", referenceId); 		    
        }

        final InvoiceData invoiceData = EntityBeanConverter.fromInvoiceDataEntityToInvoiceData(invoiceDataEntity);
        final List<BusinessEventEntity> bEEList = invoiceDataEntity.getBusinessEventEntities();
        for (final BusinessEventEntity businessEventEntity : bEEList) {
            invoiceData.getRegisteredEventList().add(EntityBeanConverter.fromBusinessEventEntityToRegisteredEvent(businessEventEntity));
        }
        
		return invoiceData;
	}
	
	Long extractId(final String referenceId) {
		Long id = Long.MIN_VALUE;
        try {
            id = Long.valueOf(validate(referenceId, "referenceId"));
        } catch (NumberFormatException nfException) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("referenceId has invalid format:" + referenceId); 
        }
        
		return id;
	}

	List<InvoiceDataHeader> getInvoiceDataHeader(List<InvoiceDataEntity> invoiceDataEntityList) {
		
		final List<InvoiceDataHeader> invoiceDataList = new ArrayList<InvoiceDataHeader>(invoiceDataEntityList.size());
		
		for (final InvoiceDataEntity iDataEntity : invoiceDataEntityList) {
		    invoiceDataList.add(CoreUtil.copyProperties(iDataEntity, InvoiceDataHeader.class));
		}

		return invoiceDataList;
	}

}
