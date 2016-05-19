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

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * This class handles 4 possible stages of an BusinessEventEntity
 * 
 * 1. newRegisteredEntity - New Event (Yet to be registered/saved in the database)
 * 2. existingRegisteredEntity - Existing registered entity in the database (pending=True)
 * 3. existingProcessedRegisteredEntity - Registered event which is already processed. (pending=Null)
 * 4. newCreditEntityFromProcessedEntity - Copy of the registered event which is already processed 
 * 			but when resent(sent again with same event_id will be marked as creditEntity (credit=true)
 * @author muqkha
 */
public class BusinessEventEntityState {
	
	private String eventId;
	
	private BusinessEventEntity newRegisteredEntity;
	private BusinessEventEntity existingRegisteredEntity;
	private BusinessEventEntity existingProcessedRegisteredEntity;
	private BusinessEventEntity newCreditEntityFromProcessedEntity;
	
	public BusinessEventEntityState(String eventId) {
		if (CoreUtil.isNotEmpty(eventId)) {
			setEventId(eventId);
		} else {
			throw InvoiceDataErrorCodeEnum.ILLEGAL_STATE_EVENT_ID_OR_EVENT_CANNOT_BE_NULL.createException("Cannot be instantiated with eventId: '" + eventId + "'");
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
	
	public BusinessEventEntity getNewCreditEntityFromProcessedEntity() {
		return newCreditEntityFromProcessedEntity;
	}
	public void setNewCreditEntityFromProcessedEntity(BusinessEventEntity newCreditEntityFromProcessedEntity) {
		this.newCreditEntityFromProcessedEntity = newCreditEntityFromProcessedEntity;
	}

	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	public boolean hasExistingRegisteredEvent() {
		return getExistingRegisteredEntity() != null;
	}
	
	public boolean hasCreditEntity() {
		return getExistingProcessedRegisteredEntity() != null;
	}
}
