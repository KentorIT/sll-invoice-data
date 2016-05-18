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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.support.TestSupport;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * @author muqkha
 *
 */
public class InvoicedataServiceIntegrationTest extends TestSupport {
	
	@Autowired
    private InvoiceDataService invoiceDataService;
	
	/**
	 * This is a very crucial part of the applications business logic.
	 * 1. A event is registered with a single item with an amount 350/- in 2 qty (350*2)
	 * 2. Then a request for generation of invoice is received by the application
	 * 3. Invoice generated with id:1 and with an amount of 700/-
	 * 4. Upon discovering the mistake, the event with the same event-id is resent
	 Note: Always send with same event-id is correction is to be made
	 * 5. A check is made to see if the old invoice still is not effected.
	 * 6. Using GetInvoiceData to fetch the pending events
	 * 7. A check is made to see that two of them exists
	 * a. With a. credit:false and b. with credit:true
	 * 8. Again a invoice is generated, maybe next month or so.
	 * 9. Final check is made that the generated invoice is of -350/- (already paid so -)
	 */
	@Test
    @Transactional
    @Rollback(true)
    public void testIntegration_Credit_Logic() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
       
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        String referenceId = invoiceDataService.createInvoiceData(createReq);

        InvoiceData invoiceData = invoiceDataService.getInvoiceDataByReferenceId(referenceId);

        assertNotNull(invoiceData);
        assertNotNull(invoiceData.getRegisteredEventList());
        assertNotNull(invoiceData.getRegisteredEventList().get(0).getItemList().get(0).getItemId());
        assertEquals(700, invoiceData.getTotalAmount().intValue());
        
        //Register again the same event, change qty
        e.getItemList().get(0).setQty(new BigDecimal(1));
        invoiceDataService.registerEvent(e);
        //check again if the invoice is intact
        invoiceData = invoiceDataService.getInvoiceDataByReferenceId(referenceId);
        assertNotNull(invoiceData);
        assertEquals(700, invoiceData.getTotalAmount().intValue());
        
        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());

        final List<RegisteredEvent> regEventList = invoiceDataService
                .getAllPendingBusinessEvents(getIDRequest);
        //One with credit:true (700) other with credit:false (350) and old one remains untouched
        assertEquals(2+1, regEventList.size());
        
        String referenceIdNew = invoiceDataService.createInvoiceData(createReq);
        invoiceData = invoiceDataService.getInvoiceDataByReferenceId(referenceIdNew);
        assertNotNull(invoiceData);
        assertEquals(-350, invoiceData.getTotalAmount().intValue());
    }
	
	@Test
    @Transactional
    @Rollback(true)
    public void testIntegration_ViewInvoiceData_Check_StartTime_And_EndTime() {
    	Event e1 = createSampleEvent();
        e1.setStartTime(CoreUtil.getCustomDate(05, 05));
        e1.setEndTime(CoreUtil.getCustomDate(05, 06));
        e1.setEventId(UUID.randomUUID().toString());
        invoiceDataService.registerEvent(e1);
        
        Event e2 = createSampleEvent();
        e2.setStartTime(CoreUtil.getCustomDate(01, 01));
        e2.setEndTime(CoreUtil.getCustomDate(01, 02));
        e2.setEventId(UUID.randomUUID().toString());
        invoiceDataService.registerEvent(e2);
        
        Event e3 = createSampleEvent();
        e3.setStartTime(CoreUtil.getCustomDate(07, 07));
        e3.setEndTime(CoreUtil.getCustomDate(07, 07));
        e3.setEventId(UUID.randomUUID().toString());
        invoiceDataService.registerEvent(e3);
        
        Event e4 = createSampleEvent();
        e4.setStartTime(CoreUtil.getCustomDate(02, 02));
        e4.setEndTime(CoreUtil.getCustomDate(02, 07));
        e4.setEventId(UUID.randomUUID().toString());
        invoiceDataService.registerEvent(e4);
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e1);
        String referenceId= invoiceDataService.createInvoiceData(createReq);
        assertNotNull(referenceId);

        InvoiceData invoiceData = invoiceDataService.getInvoiceDataByReferenceId(referenceId);

        assertNotNull(invoiceData);
        assertNotNull(invoiceData.getRegisteredEventList());
        assertEquals(4, invoiceData.getRegisteredEventList().size());
        assertNotNull(invoiceData.getRegisteredEventList().get(0).getItemList().get(0).getItemId());
        
        assertEquals(2800, invoiceData.getTotalAmount().intValue());
        
        //check start time and end time
        assertEquals(e2.getStartTime(), invoiceData.getStartDate());
        assertEquals(e3.getEndTime(), invoiceData.getEndDate());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testIntegration_ViewInvoiceData_Register_Update_Events_Check_Total_Amount() {
    	Event e1 = createSampleEvent();
        e1.setStartTime(CoreUtil.getCustomDate(05, 05));
        e1.setEndTime(CoreUtil.getCustomDate(05, 06));
        e1.setEventId(UUID.randomUUID().toString());
        invoiceDataService.registerEvent(e1);
        
        Event e2 = createSampleEvent();
        e2.setStartTime(CoreUtil.getCustomDate(01, 01));
        e2.setEndTime(CoreUtil.getCustomDate(01, 02));
        e2.setEventId(UUID.randomUUID().toString());
        invoiceDataService.registerEvent(e2);
        
        Event e3 = createSampleEvent();
        e3.setStartTime(CoreUtil.getCustomDate(07, 07));
        e3.setEndTime(CoreUtil.getCustomDate(07, 07));
        //Updating event 2
        e3.setEventId(e2.getEventId());
        e3.getItemList().get(0).setPrice(e2.getItemList().get(0).getPrice().multiply(new BigDecimal(10)));
        invoiceDataService.registerEvent(e3);
        
        Event e4 = createSampleEvent();
        e4.setStartTime(CoreUtil.getCustomDate(02, 02));
        e4.setEndTime(CoreUtil.getCustomDate(02, 07));
        e4.setEventId(UUID.randomUUID().toString());
        invoiceDataService.registerEvent(e4);
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e1);
        String referenceId= invoiceDataService.createInvoiceData(createReq);
        assertNotNull(referenceId);

        InvoiceData invoiceData = invoiceDataService.getInvoiceDataByReferenceId(referenceId);

        assertNotNull(invoiceData);
        assertNotNull(invoiceData.getRegisteredEventList());
        assertEquals(3, invoiceData.getRegisteredEventList().size());
        assertNotNull(invoiceData.getRegisteredEventList().get(0).getItemList().get(0).getItemId());
        
        assertEquals(1400+7000, invoiceData.getTotalAmount().intValue());
        
        //check start time and end time
        assertEquals(e4.getStartTime(), invoiceData.getStartDate());
        assertEquals(e3.getEndTime(), invoiceData.getEndDate());
    }

}
