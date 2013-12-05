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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * @author muqkha
 *
 */
public class CreateInvoiceDataServiceImplTest extends TestSupport {
	
	@Autowired
    private InvoiceDataService invoiceDataService;

    @Test (expected = InvoiceDataServiceException.class)
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_Result_Fail() {
        final CreateInvoiceDataRequest ie = new CreateInvoiceDataRequest();
        invoiceDataService.createInvoiceData(ie);
    }

    @Test (expected = InvoiceDataServiceException.class)
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_Invalid_Supplier() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        final CreateInvoiceDataRequest createReq = new CreateInvoiceDataRequest();
        createReq.setSupplierId(e.getSupplierId() + "_invalid");
        createReq.setPaymentResponsible(e.getPaymentResponsible());
        createReq.setCreatedBy("testCreateInvoiceData_With_Invalid_Supplier");
        createReq.getAcknowledgementIdList().add(e.getAcknowledgementId());
        invoiceDataService.createInvoiceData(createReq);
    }

    @Test (expected = InvoiceDataServiceException.class)
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_Invalid_PaymentResponsible() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        final CreateInvoiceDataRequest createReq = new CreateInvoiceDataRequest();
        createReq.setSupplierId(e.getSupplierId());
        createReq.setPaymentResponsible(e.getPaymentResponsible() + "_invalid");
        createReq.setCreatedBy("testCreateInvoiceData_With_Invalid_PaymentResponsible");
        createReq.getAcknowledgementIdList().add(e.getAcknowledgementId());
        invoiceDataService.createInvoiceData(createReq);
    }

    @Test (expected = InvoiceDataServiceException.class)
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_Duplicate_AckList() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        final CreateInvoiceDataRequest createReq = new CreateInvoiceDataRequest();
        createReq.setSupplierId(e.getSupplierId());
        createReq.setPaymentResponsible(e.getPaymentResponsible());
        createReq.setCreatedBy("testCreateInvoiceData_Duplicate_AckList");
        createReq.getAcknowledgementIdList().add(e.getAcknowledgementId());
        createReq.getAcknowledgementIdList().add(e.getAcknowledgementId());
        invoiceDataService.createInvoiceData(createReq);
    }
    
}
