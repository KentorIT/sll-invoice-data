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

import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
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
	
	@Autowired
    private InvoiceDataRepository invoiceDataRepository;
	
    @Autowired
    private BusinessEventRepository businessEventRepository;
	
    @Test
    @Transactional
    @Rollback(true)
    public void testCreateInvoiceData_With_Null_Indata_Should_Throw_Exception() {
        final CreateInvoiceDataRequest ie = new CreateInvoiceDataRequest();
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.VALIDATION_ERROR));
        
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
    public void testCreateInvoiceData_With_Null_CostCenter_Should_Throw_Exception() {
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
    public void testCreateInvoiceData_With_Null_PaymentResponsible_Should_Throw_Exception() {
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
    public void testCreateInvoiceData_With_Null_SupplierId_Should_Throw_Exception() {
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
        e1.setCostCenter("cost-center-1");
        invoiceDataService.registerEvent(e1);
        
        final Event e2 = createSampleEvent();
        e2.setEventId("event-id-2");
        e2.setCostCenter("cost-center-2");
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
    
    private boolean sentCreateRequest;
    private void markAsSentCreateRequest() {
    	sentCreateRequest = true;
    }
    
    private int registrationRequestCount;    
    private synchronized int incrementRegistrationCount() {
    	return ++registrationRequestCount;
    }
    
    private synchronized int getRegistrationRequestCount() {
    	return registrationRequestCount;
    }
    
    private final int TOTAL_EXECUTION_ITEMS = 250;
    private final int SEND_CREATE_REQUEST_AFTER_ITEMS = TOTAL_EXECUTION_ITEMS/2;
    private final int NO_OF_THREADS = 5;
    private final int LOOP_ITEMS = (TOTAL_EXECUTION_ITEMS*20)/100;
    
    private void createInvoiceInBetweenRegistration(Event e) {
    	if (getRegistrationRequestCount() == SEND_CREATE_REQUEST_AFTER_ITEMS) {
    		markAsSentCreateRequest();
    		final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
            invoiceDataService.createInvoiceData(createReq);
    	}
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    protected synchronized void registerEvent(final Event e) {
    	e.setEventId(String.valueOf(incrementRegistrationCount()));
    	System.err.println("Regisration: " + getRegistrationRequestCount());
    	invoiceDataService.registerEvent(e);
    }
    
    @Test
    public void testRegisterEvent_Concurrent_With_CreateInvoiceData() {
    	cleanRepo();
    	
    	final Event e = createSampleEvent();
    	
    	registrationRequestCount = 0;
        final Thread[] threads = new Thread[NO_OF_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int n = 0; n < LOOP_ITEMS; n++) {
                        try {
                            registerEvent(e);
                            createInvoiceInBetweenRegistration(e);
                        } catch (InvoiceDataServiceException ex) {}
                    }
                }
            });
            threads[i].start();
        }
        
        for (final Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        System.out.println("Ran tests with following configurations: "
        		+ "TOTAL_EXECUTION_ITEMS:" + TOTAL_EXECUTION_ITEMS
        		+ ", NO_OF_THREADS:" + NO_OF_THREADS + ", LOOP_ITEMS:" + LOOP_ITEMS 
        		+ ", SEND_CREATE_REQUEST_AFTER_ITEMS:" + SEND_CREATE_REQUEST_AFTER_ITEMS);
        
        System.out.println("Total request sent: " + getRegistrationRequestCount() +
        		" vs items registered:" + businessEventRepository.findAll().size() +
        		", sentCreateRequest:" + sentCreateRequest);
        
        //Important: No events sent should be missing, all must be registered
        //Although it is important but we cannot be sure of this, due to threads
        //assertEquals(getRegistrationRequestCount(), businessEventRepository.findAll().size());

        //Important: All events must be tied to an invoice
        assertEquals(0, businessEventRepository.findByInvoiceDataIsNull().size());
        if (sentCreateRequest) {
               //There should be two events 1 pending=true and other pending=false
               assertEquals(2, invoiceDataRepository.findAll().size());
               assertEquals(1, invoiceDataRepository.findByPendingIsTrue().size());
        }
        cleanRepo();        
    }
    
    @Test
    public void testRegisterEvent_End_With_CreateInvoiceData() {
    	cleanRepo();
    	
    	final Event e = createSampleEvent();
    	
    	registrationRequestCount = 0;
        final Thread[] threads = new Thread[NO_OF_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int n = 0; n < LOOP_ITEMS; n++) {
                        try {
                            registerEvent(e);
                        } catch (InvoiceDataServiceException ex) {}
                    }
                }
            });
            threads[i].start();
        }
        
        for (final Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        System.out.println("Ran tests with following configurations: "
        		+ "TOTAL_EXECUTION_ITEMS:" + TOTAL_EXECUTION_ITEMS
        		+ ", NO_OF_THREADS:" + NO_OF_THREADS + ", LOOP_ITEMS:" + LOOP_ITEMS 
        		+ ", SEND_CREATE_REQUEST_AFTER_ITEMS:" + SEND_CREATE_REQUEST_AFTER_ITEMS);
        
        System.out.println("Total request sent: " + getRegistrationRequestCount() +
        		" vs items registered:" + businessEventRepository.findAll().size());
        
        //Important: No events sent should be missing, all must be registered
        //Although it is important but we cannot be sure of this, due to threads
        assertEquals(getRegistrationRequestCount(), businessEventRepository.findAll().size());
        
        final CreateInvoiceDataRequest createReq = getCreateInvoiceDataRequestFromPassedEvent(e);
        invoiceDataService.createInvoiceData(createReq);
        
        List<InvoiceDataEntity> all = invoiceDataRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(getRegistrationRequestCount(), businessEventRepository.findByInvoiceData(all.get(0)).size());
        assertEquals(0, invoiceDataRepository.findByPendingIsTrue().size());
        
        cleanRepo();        
    }
    
    private void cleanRepo() {
    	businessEventRepository.deleteAll();
    	invoiceDataRepository.deleteAll();
    }

}
