/**
+
+
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.support.ExceptionCodeMatches;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * @author muqkha
 *
 */
public class GetInvoiceDataServiceImplTest extends TestSupport {
	
    @Autowired
    private InvoiceDataService invoiceDataService;

    @Test
    @Transactional
    @Rollback(true)
    public void testGetAllUnprocessedBusinessEvents() {

        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        final List<RegisteredEvent> regEventList = getRegisteredEventList(e);

        assertNotNull(regEventList);
        assertEquals(1, regEventList.size());
        assertEquals(e.getSupplierName(), regEventList.get(0).getSupplierName());
        assertEquals(e.getAcknowledgedBy(), regEventList.get(0).getAcknowledgedBy());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testGetAllUnprocessedEvents_By_Only_Supplier_Id() {

        final Event e = createSampleEvent();
        e.setEventId("event-id-1");
        e.setPaymentResponsible("payment-responsible-1");
        invoiceDataService.registerEvent(e);
        
        e.setEventId("event-id-2");
        e.setPaymentResponsible("payment-responsible-2");
        invoiceDataService.registerEvent(e);
        
        //Registered 2 events with same supplierid and different payment responsible

        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        
        final List<RegisteredEvent> allUnprocessedEvents = invoiceDataService.getAllPendingBusinessEvents(getIDRequest);
        assertEquals (2, allUnprocessedEvents.size());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testGetAllUnprocessedEvents_Filter_By_SupplierId_And_PaymentResponsible() {

        final Event e = createSampleEvent();
        e.setEventId("event-id-1");
        e.setPaymentResponsible("payment-responsible-1");
        invoiceDataService.registerEvent(e);
        
        e.setEventId("event-id-2");
        e.setPaymentResponsible("payment-responsible-2");
        invoiceDataService.registerEvent(e);
        
        //Registered 2 events with same supplierid and different payment responsible
        //But fetch by only one payment responsible
        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
        
        final List<RegisteredEvent> allUnprocessedEvents = invoiceDataService.getAllPendingBusinessEvents(getIDRequest);
        assertEquals (1, allUnprocessedEvents.size());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testGetAllUnprocessedEvents_Filter_By_SupplierId_And_CostCenter() {

        final Event e = createSampleEvent();
        e.setEventId("event-id-1");
        e.setCostCenter("cost-center-1");
        invoiceDataService.registerEvent(e);
        
        e.setEventId("event-id-2");
        e.setCostCenter("cost-center-2");
        invoiceDataService.registerEvent(e);
        
        //Registered 2 events with same supplier id and different cost center's
        //But fetch by only one cost center
        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setCostCenter(e.getCostCenter());
        
        final List<RegisteredEvent> allUnprocessedEvents = invoiceDataService.getAllPendingBusinessEvents(getIDRequest);
        assertEquals (1, allUnprocessedEvents.size());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testGetAllUnprocessedBusinessEvents_By_SupplierId_PaymentResponsible_And_CostCenter() {

        final Event e = createSampleEvent();
        e.setCostCenter("cost-center-1");
        invoiceDataService.registerEvent(e);

        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
        getIDRequest.setCostCenter(e.getCostCenter());
        
        final List<RegisteredEvent> regEventList = invoiceDataService
                .getAllPendingBusinessEvents(getIDRequest);

        assertNotNull(regEventList);
        assertEquals(1, regEventList.size());
        assertEquals(e.getSupplierName(), regEventList.get(0).getSupplierName());
        assertEquals(e.getAcknowledgedBy(), regEventList.get(0).getAcknowledgedBy());
        assertEquals(e.getPaymentResponsible(), regEventList.get(0).getPaymentResponsible());
        assertEquals(e.getCostCenter(), regEventList.get(0).getCostCenter());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testGetAllUnprocessedEvents_Over_Size_Limit() {
    	final int max = invoiceDataService.getEventMaxFindResultSize();
        final Event e = createSampleEvent();
        for (int i = 0; i < max; i++) {
            e.setEventId("eventtest." + i);
            invoiceDataService.registerEvent(e);
        }
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(1004));
        
        final List<RegisteredEvent> regEventList = getRegisteredEventList(e);
        assertNotNull(regEventList);
    }

}
