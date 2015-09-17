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
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.support.TestSupport;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * @author muqkha
 *
 */
public class ListInvoiceDataServiceImplTest extends TestSupport {
	
	@Autowired
    private InvoiceDataService invoiceDataService;
	
	@Test (expected=InvoiceDataServiceException.class)
    @Transactional
    @Rollback(true)	
    public void testListAllInvoiceData_Result_Fail() {
        // Request with empty parameters
        ListInvoiceDataRequest invoiceListRequest = new ListInvoiceDataRequest();
        invoiceDataService.listAllInvoiceData(invoiceListRequest);
    }

	@Test
    @Transactional
    @Rollback(true)
    public void testListAllInvoiceData_With_Alternatives() {

        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        final CreateInvoiceDataRequest createReq = new CreateInvoiceDataRequest();
        createReq.setSupplierId(e.getSupplierId());
        createReq.setPaymentResponsible(e.getPaymentResponsible());
        createReq.setCreatedBy("testListAllInvoiceData_With_Alternatives");
        createReq.getAcknowledgementIdList().add(e.getAcknowledgementId());
        invoiceDataService.createInvoiceData(createReq);

        // Request with only supplier id
        ListInvoiceDataRequest invoiceListRequest = new ListInvoiceDataRequest();
        invoiceListRequest.setSupplierId(e.getSupplierId());

        assert_InvoiceDataList(invoiceListRequest, e);
        
        // Request with only payment responsible
        invoiceListRequest = new ListInvoiceDataRequest();
        invoiceListRequest.setPaymentResponsible(e.getPaymentResponsible());
        
        assert_InvoiceDataList(invoiceListRequest, e);
        
        // Request with supplierId and paymentResponsible
        invoiceListRequest = new ListInvoiceDataRequest();
        invoiceListRequest.setSupplierId(e.getSupplierId());
        invoiceListRequest.setPaymentResponsible(e.getPaymentResponsible());

        assert_InvoiceDataList(invoiceListRequest, e);

    }
	
	private void assert_InvoiceDataList(ListInvoiceDataRequest inoviceListRequest, Event e) {
		List<InvoiceDataHeader> invoiceDataList = invoiceDataService
                .listAllInvoiceData(inoviceListRequest);

        assertNotNull(invoiceDataList);
        assertEquals(e.getSupplierId(), invoiceDataList.get(0).getSupplierId());
        assertEquals(e.getPaymentResponsible(), invoiceDataList.get(0).getPaymentResponsible());
	}
    
    @Test
    @Transactional
    @Rollback(true)
    public void testListAllInvoiceData_Between_Dates() {
    	//year-11-12 : year-11-27
        Event e1 = createSampleEvent();
        e1.setStartTime(CoreUtil.getCustomDate(10, 12));
        e1.setEndTime(CoreUtil.getCustomDate(10, 27));
        e1.setEventId(UUID.randomUUID().toString());
        e1.setSupplierId("Supplier_101");
        invoiceDataService.registerEvent(e1);
        
        //year-11-06 : year-11-23
        Event e2 = createSampleEvent();
        e2.setStartTime(CoreUtil.getCustomDate(10, 06));
        e2.setEndTime(CoreUtil.getCustomDate(10, 23));
        e2.setEventId(UUID.randomUUID().toString());
        e2.setSupplierId("Supplier_102");
        invoiceDataService.registerEvent(e2);
        
        //year-11-12 : year-11-28
        Event e3 = createSampleEvent();
        e3.setStartTime(CoreUtil.getCustomDate(10, 12));
        e3.setEndTime(CoreUtil.getCustomDate(10, 28));
        e3.setEventId(UUID.randomUUID().toString());
        e3.setSupplierId("Supplier_103");
        invoiceDataService.registerEvent(e3);
        
        //year-11-02 : year-11-29
        Event e4 = createSampleEvent();
        e4.setStartTime(CoreUtil.getCustomDate(10, 02));
        e4.setEndTime(CoreUtil.getCustomDate(10, 29));
        e4.setEventId(UUID.randomUUID().toString());
        e4.setSupplierId("Supplier_104");
        invoiceDataService.registerEvent(e4);
        
        //year-11-12 : year-11-28
        Event e5 = createSampleEvent();
        e5.setStartTime(CoreUtil.getCustomDate(10, 14));
        e5.setEndTime(CoreUtil.getCustomDate(10, 14));
        e5.setEventId(UUID.randomUUID().toString());
        e5.setSupplierId("Supplier_105");
        invoiceDataService.registerEvent(e5);
        
        //assert_GetInvoiceData_Result(e1, 5);
                
        create_And_Assert_Invoice_Data(e1);
        create_And_Assert_Invoice_Data(e2);
        create_And_Assert_Invoice_Data(e3);
        create_And_Assert_Invoice_Data(e4);
        create_And_Assert_Invoice_Data(e5);
        
        // Request with only supplier id
        ListInvoiceDataRequest invoiceListRequest = new ListInvoiceDataRequest();
        invoiceListRequest.setSupplierId(e1.getSupplierId());
        invoiceListRequest.setPaymentResponsible(e1.getPaymentResponsible());
        invoiceListRequest.setFromDate(CoreUtil.getCustomDate(10, 25));
        invoiceListRequest.setToDate(CoreUtil.getCustomDate(11, 31));
        
        List<InvoiceDataHeader> invoiceDataList = invoiceDataService
                .listAllInvoiceData(invoiceListRequest);

        assertNotNull(invoiceDataList);
        assertEquals(e1.getPaymentResponsible(), invoiceDataList.get(0)
                .getPaymentResponsible());
        assertEquals(1, invoiceDataList.size());
        
        invoiceListRequest = new ListInvoiceDataRequest();
        invoiceListRequest.setPaymentResponsible(e1.getPaymentResponsible());
        invoiceListRequest.setFromDate(CoreUtil.getCustomDate(10, 25));
        invoiceListRequest.setToDate(CoreUtil.getCustomDate(11, 31));
        
        invoiceDataList = invoiceDataService
                .listAllInvoiceData(invoiceListRequest);

        assertNotNull(invoiceDataList);
        assertEquals(e1.getPaymentResponsible(), invoiceDataList.get(0)
                .getPaymentResponsible());
        
        //Created 5 but should return 3 (date range)
        assertEquals(3, invoiceDataList.size());
    }
    
    private void create_And_Assert_Invoice_Data(Event e) {
    	final CreateInvoiceDataRequest createReq = new CreateInvoiceDataRequest();
        createReq.setSupplierId(e.getSupplierId());
        createReq.setPaymentResponsible(e.getPaymentResponsible());
        createReq.setCreatedBy("testListAllInvoiceData_Between_Dates");
        createReq.getAcknowledgementIdList().add(e.getAcknowledgementId());
        
        assertNotNull(invoiceDataService.createInvoiceData(createReq));
    }
    /*
    private void assert_GetInvoiceData_Result(Event e, int size) {
    	GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());

        final List<RegisteredEvent> regEventList = invoiceDataService
                .getAllUnprocessedBusinessEvents(getIDRequest);

        assertNotNull(regEventList);
        assertEquals(size, regEventList.size());
    }*/
    
}
