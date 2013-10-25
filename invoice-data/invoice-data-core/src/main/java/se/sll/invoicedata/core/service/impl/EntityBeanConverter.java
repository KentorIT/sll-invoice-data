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

import java.util.ArrayList;
import java.util.List;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyGenericLists;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

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
	static BusinessEventEntity toEntity(final Event event) {
		final BusinessEventEntity entity = copyProperties(event, BusinessEventEntity.class);
		for (final Item item : event.getItemList()) {
			entity.addItemEntity(copyProperties(item, ItemEntity.class));
		}

		return entity;
	}

	/**
	 * Maps XML JAXB object to an entity bean.
	 * 
	 * @param event the JAXB object.
	 * @return the entity bean.
	 */
	static InvoiceDataHeader fromEntity(final InvoiceDataEntity entity) {
		final InvoiceDataHeader iDHeader = copyProperties(entity, InvoiceDataHeader.class);
		return iDHeader;
	}
	
	/**
	 * Maps InvoiceDataEntity to InvoiceDataHeader object
	 * @param entityList
	 * @return List<InvoiceDataHeader>
	 */
	static List<InvoiceDataHeader> fromIDEntity(final List<InvoiceDataEntity> entityList) {
		List<InvoiceDataHeader> iDHeaderList = new ArrayList<InvoiceDataHeader>(entityList.size());
		for (final InvoiceDataEntity iDE : entityList) {
			iDHeaderList.add(fromEntity(iDE));
		}
		return iDHeaderList;
	}
	
	/**
	 * Maps BusinessEventEntity to RegisteredEvent object
	 * @param bEEntity
	 * @return RegisteredEvent
	 */
	static RegisteredEvent fromEntity(final BusinessEventEntity bEEntity) {
		final RegisteredEvent rEvent = copyProperties(bEEntity, RegisteredEvent.class);

		copyGenericLists(rEvent.getItemList(), bEEntity.getItemEntities(), Item.class);

		return rEvent;
	}
	
	/**
	 * Maps list of BusinessEventEntity to RegisteredEvent list
	 * @param bEEntityList
	 * @return List<RegisteredEvent>
	 */
	static List<RegisteredEvent> fromBEntity(
			final List<BusinessEventEntity> bEEntityList) {
		List<RegisteredEvent> registeredEventList = new ArrayList<RegisteredEvent>(bEEntityList.size());
		for (final BusinessEventEntity bEEntity : bEEntityList) {
			registeredEventList.add(fromEntity(bEEntity));

		}
		return registeredEventList;
	}
	
	/**
	 * Maps InvoiceDataEntity to InvoiceData object
	 * @param entity
	 * @return InvoiceData
	 */
	static InvoiceData fromIDEntity(final InvoiceDataEntity entity) {
		InvoiceData iData = copyProperties(entity, InvoiceData.class);

		return iData;
	}
}
