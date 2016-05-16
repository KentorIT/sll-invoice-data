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
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.support.ExceptionCodeMatches;
import se.sll.invoicedata.core.support.TestSupport;
import se.sll.invoicedata.core.util.CoreUtil;

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
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.VALIDATION_ERROR));
        
        invoiceDataService.getInvoiceDataByReferenceId(referenceId);		
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testGetInvoiceDataBy_Invalid_ReferenceId() {
        //Invalid referenceId
        String referenceId = "supplierId.0000x";
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.VALIDATION_ERROR));
        
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
    
    @Test
    @Transactional
    @Rollback(true)
    public void testViewInvoiceData_Check_StartTime_And_EndTime() {
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
    public void testViewInvoiceData_Register_Update_Events_Check_Total_Amount() {
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
