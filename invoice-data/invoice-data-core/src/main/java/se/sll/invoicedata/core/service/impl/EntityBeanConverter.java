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

/**
 * 
 */
package se.sll.invoicedata.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;

/**
 * @author muqkha
 * 
 */
public class EntityBeanConverter extends CoreUtil {

	/**
	 * Maps XML JAXB object to an entity bean.
	 * 
	 * @param event the JAXB object.
	 * @return the entity bean.
	 */
	static BusinessEventEntity toEntity(final Event event) {
		final BusinessEventEntity entity = copyProperties(
				new BusinessEventEntity(), event, Event.class);
		for (final Item item : event.getItemList()) {
			entity.addItemEntity(copyProperties(new ItemEntity(), item,
					Item.class));
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
		InvoiceDataHeader iDHeader = new InvoiceDataHeader();
		copyProperties(iDHeader, entity, InvoiceDataEntity.class);
		// Need to manually set reference id since entity has no setter method
		// for referenceId
		iDHeader.setReferenceId(entity.getReferenceId());
		iDHeader.setTotalAmount(entity.getTotalAmount());

		return iDHeader;
	}
	
	/**
	 * 
	 * @param entityList
	 * @return
	 */
	static List<InvoiceDataHeader> fromIEntity(final List<InvoiceDataEntity> entityList) {
		List<InvoiceDataHeader> iDHeaderList = new ArrayList<InvoiceDataHeader>();
		for (final InvoiceDataEntity iDE : entityList) {
			iDHeaderList.add(fromEntity(iDE));
		}
		return iDHeaderList;
	}
	
	/**
	 * 
	 * @param bEEntity
	 * @return
	 */
	static RegisteredEvent fromEntity(final BusinessEventEntity bEEntity) {
		RegisteredEvent rEvent = new RegisteredEvent();
		rEvent.setEventId(bEEntity.getEventId());
		rEvent.setTotalAmount(bEEntity.getTotalAmount());
		rEvent.setCredit(bEEntity.isCredit());
		copyProperties(rEvent, bEEntity, BusinessEventEntity.class);

		List<Item> itemList = new ArrayList<Item>();
		copyGenericLists(itemList, bEEntity.getItemEntities(), Item.class,
				ItemEntity.class);

		rEvent.setItemList(itemList);

		return rEvent;
	}
	
	/**
	 * 
	 * @param bEEntityList
	 * @return
	 */
	static List<RegisteredEvent> fromBEntity(
			final List<BusinessEventEntity> bEEntityList) {
		List<RegisteredEvent> registeredEventList = new ArrayList<RegisteredEvent>();
		for (final BusinessEventEntity bEEntity : bEEntityList) {
			registeredEventList.add(fromEntity(bEEntity));

		}
		return registeredEventList;
	}
}
