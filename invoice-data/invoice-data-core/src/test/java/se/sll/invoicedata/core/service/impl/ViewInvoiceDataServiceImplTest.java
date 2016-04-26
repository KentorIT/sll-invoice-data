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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.support.ExceptionCodeMatches;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * @author muqkha
 *
 */
public class ViewInvoiceDataServiceImplTest extends TestSupport {
	
	@Autowired
    private InvoiceDataService invoiceDataService;

	@Test
    @Transactional
    @Rollback(true)
    public void testViewInvoiceDataByReferenceId() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        String referenceId= invoiceDataService.createInvoiceData(createReq);
        assertNotNull(referenceId);

        InvoiceData invoiceData = invoiceDataService.getInvoiceDataByReferenceId(referenceId);

        assertNotNull(invoiceData);
        assertNotNull(invoiceData.getRegisteredEventList());
        assertNotNull(invoiceData.getRegisteredEventList().get(0).getItemList().get(0).getItemId());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testGetInvoiceDataBy_Dummy_ReferenceId() {
        //Valid referenceId
        String referenceId = "supplierId.00000";
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(1002));
        
        invoiceDataService.getInvoiceDataByReferenceId(referenceId);		
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testGetInvoiceDataBy_Invalid_ReferenceId() {
        //Invalid referenceId
        String referenceId = "supplierId.0000x";
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(1002));
        
        invoiceDataService.getInvoiceDataByReferenceId(referenceId);		
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testGetInvoiceDataBy_Pending_ReferenceId() {
        //Pending referenceId
        String referenceId = "1";
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
        
        thrown.expect(InvoiceDataServiceException.class);
        //thrown.expect(new ExceptionCodeMatches(1007));
        
        invoiceDataService.getInvoiceDataByReferenceId(referenceId);		
    }
}
