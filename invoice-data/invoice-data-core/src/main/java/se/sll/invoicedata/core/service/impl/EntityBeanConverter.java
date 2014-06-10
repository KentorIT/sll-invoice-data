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

import static se.sll.invoicedata.core.service.impl.CoreUtil.copyGenericLists;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

import java.util.ArrayList;
import java.util.List;

import riv.sll.invoicedata._1.DiscountItem;
import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.entity.ItemType;

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
	static BusinessEventEntity toBusinessEventEntity(final Event event) {
		final BusinessEventEntity entity = copyProperties(event, BusinessEventEntity.class);
		for (final Item item : event.getItemList()) {
			ItemEntity itemEntity = copyProperties(item, ItemEntity.class);
			itemEntity.setItemType(ItemType.SERVICE);
			entity.addItemEntity(itemEntity);
		}

		return entity;
	}

	/**
	 * Maps BusinessEventEntity to RegisteredEvent object
	 * @param bEEntity
	 * @return RegisteredEvent
	 */
	static RegisteredEvent fromBusinessEventEntityToRegisteredEvent(final BusinessEventEntity bEEntity) {
		final RegisteredEvent rEvent = copyProperties(bEEntity, RegisteredEvent.class);
		
		rEvent.getItemList().addAll(copyItemEntityToServiceItem(bEEntity.getItemEntities()));
		rEvent.getDiscountItemList().addAll(copyItemEntityToDiscountItem(bEEntity.getItemEntities()));

		return rEvent;
	}
	
	private static List<Item> copyItemEntityToServiceItem(List<ItemEntity> itemEntityList) {
		List<Item> itemList = new ArrayList<Item>();
		for (ItemEntity itemEntity : itemEntityList) {
			if (itemEntity.getItemType() == ItemType.SERVICE) {
				itemList.add(copyProperties(itemEntity, Item.class));
			}
		}
		
		return itemList;
	}
	
	private static List<DiscountItem> copyItemEntityToDiscountItem(List<ItemEntity> itemEntityList) {
		List<DiscountItem> discountItemList = new ArrayList<DiscountItem>();
		for (ItemEntity itemEntity : itemEntityList) {
			if (itemEntity.getItemType() == ItemType.DISCOUNT) {
				DiscountItem discountItem = copyProperties(itemEntity, DiscountItem.class);
				discountItem.setDiscountInPercent(itemEntity.getQty());
				discountItem.setDiscountedPrice(itemEntity.getPrice());
				discountItemList.add(discountItem);
			}
		}
		
		return discountItemList;
	}
	
	/**
	 * Maps list of BusinessEventEntity to RegisteredEvent list
	 * @param bEEntityList
	 * @return List<RegisteredEvent>
	 */
	static List<RegisteredEvent> fromBusinessEventEntityToRegisteredEvent(
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
	static InvoiceData fromInvoiceDataEntityToInvoiceData(final InvoiceDataEntity entity) {
		return copyProperties(entity, InvoiceData.class);
	}
}
