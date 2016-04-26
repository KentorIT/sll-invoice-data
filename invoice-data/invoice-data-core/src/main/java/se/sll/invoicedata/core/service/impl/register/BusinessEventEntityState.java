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
package se.sll.invoicedata.core.service.impl.register;

import org.springframework.beans.factory.annotation.Autowired;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * This class handles 3 stages of an Event
 * 
 * 1. newRegisteredEntity - New Event - New Entity
 * 2. existingRegisteredEntity - Existing registered event/entity
 * 3. existingProcessedRegisteredEntity registered event which is already processed.
 * @author muqkha
 */
public class BusinessEventEntityState {
	
	private String eventId;
	
	private BusinessEventEntity newRegisteredEntity;
	private BusinessEventEntity existingRegisteredEntity;
	private BusinessEventEntity existingProcessedRegisteredEntity;
	
	public BusinessEventEntityState(String eventId) {
		if (CoreUtil.isNotEmpty(eventId)) {
			setEventId(eventId);
		} else {
			throw InvoiceDataErrorCodeEnum.ILLEGAL_STATE_EVENT_ID_OR_EVENT_CANNOT_BE_NULL.createException("Contact system administrator");
		}
	}
	
	public BusinessEventEntity getNewRegisteredEntity() {
		return newRegisteredEntity;
	}
	public void setNewRegisteredEntity(BusinessEventEntity newRegisteredEntity) {
		this.newRegisteredEntity = newRegisteredEntity;
	}
	public BusinessEventEntity getExistingRegisteredEntity() {
		return existingRegisteredEntity;
	}
	public void setExistingRegisteredEntity(BusinessEventEntity existingRegisteredEntity) {
		this.existingRegisteredEntity = existingRegisteredEntity;
	}
	public BusinessEventEntity getExistingProcessedRegisteredEntity() {
		return existingProcessedRegisteredEntity;
	}
	public void setExistingProcessedRegisteredEntity(BusinessEventEntity existingProcessedRegisteredEntity) {
		this.existingProcessedRegisteredEntity = existingProcessedRegisteredEntity;
	}

	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	public boolean hasCreditEntity() {
		return getExistingProcessedRegisteredEntity() != null;
	}
}
