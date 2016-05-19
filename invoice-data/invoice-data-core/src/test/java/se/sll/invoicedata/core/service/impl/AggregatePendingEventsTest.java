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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.support.TestSupport;
import se.sll.invoicedata.core.util.CoreUtil;

import static org.junit.Assert.assertEquals;

/**
 * @author muqkha
 *
 */
public class AggregatePendingEventsTest extends TestSupport {
	
	@Autowired
    private InvoiceDataService invoiceDataService;
	
	@Autowired
    private BusinessEventRepository businessEventRepository;
	
	@Autowired
	private AggregatePendingEvents aggregatePendingEvents;
	
	@Test
    @Transactional
    @Rollback(true)
    public void testMigrating_ExistingEventsFrom_To_Version2() {
		BusinessEventEntity e1 = createSampleBusinessEventEntity();
        e1.setEventId(UUID.randomUUID().toString());
        businessEventRepository.save(e1);
        
        BusinessEventEntity e2 = createSampleBusinessEventEntity();
        e2.setEventId(UUID.randomUUID().toString());
        businessEventRepository.save(e2);
        
        assertEquals(2, businessEventRepository.findAll().size());
        aggregatePendingEvents.aggregateExistingPendingEventsToInvoiceDataDraftVersion();
        List<InvoiceDataHeader> pendingInvoiceDataList = invoiceDataService.getAllPendingInvoiceData();
        assertEquals(1, pendingInvoiceDataList.size());
    }
}
