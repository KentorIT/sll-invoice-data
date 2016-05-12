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
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.support.ExceptionCodeMatches;
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
    public void testCreateInvoiceData_With_Null_Indata_Result_Fail() {
        final CreateInvoiceDataRequest ie = new CreateInvoiceDataRequest();
        invoiceDataService.createInvoiceData(ie);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_SupplierId_PaymentResponsible_And_CostCenter() {
        final Event e = createSampleEvent();        
        invoiceDataService.registerEvent(e);

        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        String referenceId = invoiceDataService.createInvoiceData(createReq);
        assertNotNull(referenceId);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_SupplierId_PaymentResponsible_And_NullCostCenter() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.VALIDATION_ERROR));
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        createReq.setCostCenter(null);
        String referenceId = invoiceDataService.createInvoiceData(createReq);
        assertNotNull(referenceId);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_SupplierId_CostCenter_And_NullPaymentResponsible() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.VALIDATION_ERROR));
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        createReq.setPaymentResponsible(null);
        String referenceId = invoiceDataService.createInvoiceData(createReq);
        assertNotNull(referenceId);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_CostCenter_And_PaymentResponsible_And_NullSupplierId() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.VALIDATION_ERROR));
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        createReq.setSupplierId(null);
        String referenceId = invoiceDataService.createInvoiceData(createReq);
        assertNotNull(referenceId);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void test_CreateInvoiceData_Use_Case_With_Two_Different_CostCenters_But_Same_SupplierId_And_PaymentResponsible() {
        final Event e1 = createSampleEvent();
        e1.setEventId("event-id-1");
        e1.setCostCenter(e1.getCostCenter() + "-1");
        invoiceDataService.registerEvent(e1);
        
        final Event e2 = createSampleEvent();
        e2.setEventId("event-id-2");
        e2.setCostCenter(e1.getCostCenter() + "-1");
        invoiceDataService.registerEvent(e2);

        final CreateInvoiceDataRequest createReq_1 = getCreateInvoiceDataRequestFromPassedEvent(e1);
        String referenceId_1 = invoiceDataService.createInvoiceData(createReq_1);
        assertNotNull(referenceId_1);
        
        final CreateInvoiceDataRequest createReq_2 = getCreateInvoiceDataRequestFromPassedEvent(e2);
        String referenceId_2 = invoiceDataService.createInvoiceData(createReq_2);
        assertNotNull(referenceId_2);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_Other_SupplierId_Should_Throw_Exception() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.NO_PENDING_INVOICES_TO_BE_GENERATED));
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        createReq.setSupplierId(e.getSupplierId() + "_other");
        invoiceDataService.createInvoiceData(createReq);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_Other_PaymentResponsible_Should_Throw_Exception() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.NO_PENDING_INVOICES_TO_BE_GENERATED));
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        createReq.setPaymentResponsible(e.getPaymentResponsible() + "_other");
        invoiceDataService.createInvoiceData(createReq);
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_Other_CostCenter_Should_Throw_Exception() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.NO_PENDING_INVOICES_TO_BE_GENERATED));
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        createReq.setCostCenter(e.getCostCenter() + "_other");
        invoiceDataService.createInvoiceData(createReq);
    }
}
