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
package se.sll.invoicedata.app.rs.impl;

import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import se.sll.invoicedata.app.rs.AggregatePendingEventsProducer;
import se.sll.invoicedata.core.service.dto.ServiceResponse;
import se.sll.invoicedata.core.service.impl.AggregatePendingEvents;

/**
 * @author muqkha
 *
 */
public class AggregatePendingEventsProducerImpl implements AggregatePendingEventsProducer {
	
	@Autowired
    private AggregatePendingEvents aggregatePendingEvents;
	
	@Override
	public Response migrateToSupportVersion2() {
		List<ServiceResponse> serviceRespList = aggregatePendingEvents.
				aggregateExistingPendingEventsToInvoiceDataDraftVersion();
		//return Response.ok(pendingInvoiceList).build();
		return Response.ok(new GenericEntity<List<ServiceResponse>>(serviceRespList) {}).build();
	}

}
