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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * @author muqkha
 *
 */
public class RegisterInvoiceDataServiceImplTest extends TestSupport {
	
	@Autowired
    private InvoiceDataService invoiceDataService;

    @Autowired
    private BusinessEventRepository businessEventRepository;
	
	@Test
    @Transactional
    @Rollback(true)
    public void testGetTotalAmount_On_Registerevent() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
        RegisteredEvent rE = invoiceDataService.getAllUnprocessedBusinessEvents(getIDRequest).get(0);
        assertEquals(700, rE.getTotalAmount().intValue());

        //Test with more items
        Item i1 = new Item();
        i1.setDescription("Test item");
        i1.setItemId("IT0x");
        i1.setPrice(BigDecimal.valueOf(150));
        i1.setQty(BigDecimal.valueOf(3));
        e.getItemList().add(i1);
        e.setAcknowledgementId(UUID.randomUUID().toString());
        invoiceDataService.registerEvent(e);

        rE = invoiceDataService.getAllUnprocessedBusinessEvents(getIDRequest).get(0);
        assertEquals(1150, rE.getTotalAmount().intValue());

    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRegister_Multiple_Events() {
        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);
        e.setAcknowledgementId(UUID.randomUUID().toString());
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
    
    @Test
    @Rollback(true)
    public void testRegister_Duplicate_Events() {		

        final Event e = createSampleEvent();
        invoiceDataService.registerEvent(e);

        performAssert(e);		
        invoiceDataService.registerEvent(e);		
        performAssert(e);		
    }

    private void performAssert(Event e) {
        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(e.getSupplierId());
        getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
        final List<RegisteredEvent> l = invoiceDataService.getAllUnprocessedBusinessEvents(getIDRequest);

        assertEquals(1, l.size());

        final RegisteredEvent f = l.get(0);
        assertEquals(e.getEventId(), f.getEventId());
        assertEquals(e.getAcknowledgementId(), f.getAcknowledgementId());
    }


    @Test (expected = InvoiceDataServiceException.class)
    @Transactional
    @Rollback(true)
    public void testRegisterEvent_With_No_Items_Result_Fail() {
        final Event e = createSampleEvent();
        e.setItemList(null);
        invoiceDataService.registerEvent(e);		
    }

    @Test (expected = InvoiceDataServiceException.class)
    @Transactional
    @Rollback(true)
    public void testRegisterEvent_With_Invalid_Item_Qty_Result_Fail() {
        final Event e = createSampleEvent();
        e.getItemList().get(0).setQty(new BigDecimal(9999999));
        invoiceDataService.registerEvent(e);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testRegisterInvoiceData_From_Pending_And_Credit() {
        final String supplierId = "test-supplier-45";
        final CreateInvoiceDataRequest ie = createInvoiceData(supplierId);

        registerEvents(supplierId,
                Arrays.asList(new String[] { "event-1", "event-2", "event-3" }));

        GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
        getIDRequest.setSupplierId(supplierId);

        final List<RegisteredEvent> l = invoiceDataService
                .getAllUnprocessedBusinessEvents(getIDRequest);
        // one credit event shall be created for each new
        assertEquals(ie.getAcknowledgementIdList().size() * 2, l.size());

        int credits = 0;
        for (final RegisteredEvent e : l) {
            if (e.isCredit()) {
                credits++;
            }
        }
        assertEquals(3, credits);
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    protected void registerEvent(final Event e) {
        invoiceDataService.registerEvent(e);
    }
    
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    protected void clean() {
        businessEventRepository.deleteAll();
        long count = businessEventRepository.count();
        assertEquals(0L, count);
    }
    
    @Transactional
    protected BusinessEventEntity getEvent(final String eventId) {
        return businessEventRepository.findByEventIdAndPendingIsTrueAndCreditIsNull(eventId);
    }

    @Test
    public void testRegisterEvent_concurrency() {        
        final Event e = createSampleEvent();
        clean();

        final Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int n = 0; n < 50; n++) {
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

        final BusinessEventEntity saved = getEvent(e.getEventId());
        assertNotNull(saved);
        assertEquals(e.getEventId(), saved.getEventId());

        businessEventRepository.deleteAll();
    }

}
