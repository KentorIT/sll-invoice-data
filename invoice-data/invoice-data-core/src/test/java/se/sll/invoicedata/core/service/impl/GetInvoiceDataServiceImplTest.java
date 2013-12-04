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

        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());

        final List<RegisteredEvent> regEventList = invoiceDataService
                .getAllUnprocessedBusinessEvents(getIDRequest);

        assertNotNull(regEventList);
        assertEquals(1, regEventList.size());
        assertEquals(e.getSupplierName(), regEventList.get(0).getSupplierName());
        assertEquals(e.getAcknowledgedBy(), regEventList.get(0).getAcknowledgedBy());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testGetInvoiceData() {

        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
        final List<RegisteredEvent> l = invoiceDataService.getAllUnprocessedBusinessEvents(getIDRequest);
        final RegisteredEvent f = l.get(0);

        assertEquals(e.getEventId(), f.getEventId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getAcknowledgedBy(), f.getAcknowledgedBy());
    }
    

    @Test(expected = InvoiceDataServiceException.class)
    @Transactional
    @Rollback(true)
    public void testGetInvoiceData_Over_Size_Limit() {

        final int max = invoiceDataService.getEventMaxFindResultSize();
        final Event e = createSampleEvent();
        for (int i = 0; i < max; i++) {
            e.setEventId("eventtest." + i);
            invoiceDataService.registerEvent(e);
        }

        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
        invoiceDataService.getAllUnprocessedBusinessEvents(getIDRequest);
    }

}
