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
package se.sll.invoicedata.core.pojo.mapping;

import static se.sll.invoicedata.core.util.CoreUtil.copyProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import riv.sll.invoicedata._1.DiscountItem;
import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.DiscountItemEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.entity.ReferenceItemEntity;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * @author muqkha
 * 
 */
public class EntityBeanConverter {

	/**
	 * Maps XML JAXB object to an entity bean.
	 * 
	 * @param event the JAXB object.
	 * @return the entity bean.
	 */
	public static BusinessEventEntity toBusinessEventEntity(final Event event) {
		final BusinessEventEntity businessEventEntity = copyProperties(event, BusinessEventEntity.class);
		for (final Item item : event.getItemList()) {
			businessEventEntity.addItemEntity(copyProperties(item, ItemEntity.class));
		}

		return businessEventEntity;
	}
	
	private static boolean getValue(Boolean b) {
		return b == null ? false : b.booleanValue();
	}
	
	/**
	 * This method was rewritten to gain performance since the DOZER API was inconsistent w.r.t performance   
	 * Maps BusinessEventEntity to RegisteredEvent object
	 * @param businessEventEntity
	 * @return RegisteredEvent
	 */
	public static RegisteredEvent fromBusinessEventEntityToRegisteredEvent(final BusinessEventEntity businessEventEntity) {
		final RegisteredEvent registeredEvent = new RegisteredEvent();
		registeredEvent.setAcknowledgedBy(businessEventEntity.getAcknowledgedBy());
		registeredEvent.setAcknowledgedTime(CoreUtil.toXMLGregorianCalendar(businessEventEntity.getAcknowledgedTime()));
		registeredEvent.setAcknowledgementId(businessEventEntity.getAcknowledgementId());
		registeredEvent.setCredit(getValue(businessEventEntity.getCredit()));
		registeredEvent.setEndTime(CoreUtil.toXMLGregorianCalendar(businessEventEntity.getEndTime()));
		registeredEvent.setEventId(businessEventEntity.getEventId());
		registeredEvent.setHealthCareCommission(businessEventEntity.getHealthCareCommission());
		registeredEvent.setHealthcareFacility(businessEventEntity.getHealthcareFacility());
		registeredEvent.setId(businessEventEntity.getId());
		registeredEvent.setPaymentResponsible(businessEventEntity.getPaymentResponsible());
		registeredEvent.setRefContractId(businessEventEntity.getRefContractId());
		registeredEvent.setServiceCode(businessEventEntity.getServiceCode());
		registeredEvent.setStartTime(CoreUtil.toXMLGregorianCalendar(businessEventEntity.getStartTime()));
		registeredEvent.setSupplierId(businessEventEntity.getSupplierId());
		registeredEvent.setSupplierName(businessEventEntity.getSupplierName());
		registeredEvent.setCostCenter(businessEventEntity.getCostCenter());
		
		Map<String, ItemEntity> serviceItemMap = new HashMap<String, ItemEntity>();
		
		BigDecimal amount = BigDecimal.valueOf(0.0);
		for (ItemEntity itemEntity : businessEventEntity.getItemEntities()) {
			Item item = new Item();
			item.setDescription(itemEntity.getDescription());
			item.setItemId(itemEntity.getItemId());
			item.setPrice(itemEntity.getPrice());
			item.setQty(itemEntity.getQty());
			
			amount = amount.add(itemEntity.getPrice().multiply(itemEntity.getQty()));
			
			serviceItemMap.put(itemEntity.getItemId(), itemEntity);
			registeredEvent.getItemList().add(item);
		}
		
		TreeSet<DiscountItemEntity> discountItemSet = new TreeSet<DiscountItemEntity>(businessEventEntity.getDiscountItemEntities());
		for (DiscountItemEntity discountItemEntity : discountItemSet) {
			DiscountItem discountItem = new DiscountItem();
			discountItem.setDescription(discountItemEntity.getDescription());
			discountItem.setDiscountInPercentage(discountItemEntity.getDiscountInPercentage());
			discountItem.setOrderOfDiscount(discountItemEntity.getOrderOfDiscount());
			
			BigDecimal discountAmount = BigDecimal.valueOf(0.0);
			
			for (ReferenceItemEntity referenceItemEntity : discountItemEntity.getReferenceItemEntities()) {
	    		ItemEntity itemEntity = serviceItemMap.get(referenceItemEntity.getRefItemId());
	    		BigDecimal priceBeforeDiscount = itemEntity.getPrice().multiply(new BigDecimal(referenceItemEntity.getQty()));
	    		BigDecimal priceAfterDiscount = (priceBeforeDiscount.multiply(new BigDecimal(discountItemEntity.getDiscountInPercentage()))).divide(new BigDecimal(100));
	    		discountAmount = discountAmount.add(priceAfterDiscount);
	    	}
			
			discountItem.setDiscountedPrice(discountAmount);
			amount = amount.subtract(discountAmount);
			registeredEvent.getDiscountItemList().add(discountItem);
		}
		amount = amount.setScale(2, RoundingMode.HALF_UP);
		registeredEvent.setTotalAmount(amount);
		
		return registeredEvent;
	}
	
	/**
	 * Maps list of BusinessEventEntity to RegisteredEvent list
	 * @param bEEntityList
	 * @return List<RegisteredEvent>
	 */
	public static List<RegisteredEvent> processBusinessEventEntitiesToRegisteredEvent(
			final List<BusinessEventEntity> bEEntityList) {
		List<RegisteredEvent> registeredEventList = new ArrayList<RegisteredEvent>(bEEntityList.size());
		for (final BusinessEventEntity bEEntity : bEEntityList) {
			registeredEventList.add(fromBusinessEventEntityToRegisteredEvent(bEEntity));

		}
		return registeredEventList;
	}
	
	/**
	 * Maps InvoiceDataEntity to InvoiceData object
	 * @param entity
	 * @return InvoiceData
	 */
	public static InvoiceData fromInvoiceDataEntityToInvoiceData(final InvoiceDataEntity entity) {
		return copyProperties(entity, InvoiceData.class);
	}
}
