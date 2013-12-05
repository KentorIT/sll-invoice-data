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

package se.sll.invoicedata.core.model.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.service.impl.CoreUtil;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * Unit tests.
 * 
 * @author Peter
 *
 */
public class InvoiceDataRepositoryTest extends TestSupport {

    
    @Test
    @Transactional
    @Rollback(true)
    public void testInsertFind_InvocieDataEntity() {
        final InvoiceDataEntity e = createSampleInvoiceDataEntity();
        final BusinessEventEntity b = createSampleBusinessEventEntity();
        b.setSupplierId(e.getSupplierId());
        assertTrue(e.addBusinessEventEntity(b));
        
        getInvoiceDataRepository().save(e);
        getInvoiceDataRepository().flush();
        
        final List<InvoiceDataEntity> l = getInvoiceDataRepository().getInvoiceDataBySupplierIdAndPaymentResponsibleBetweenDates(e.getSupplierId(), e.getPaymentResponsible(), CoreUtil.MIN_DATE, new Date());
        
        assertNotNull(l);
        assertEquals(1, l.size());
        assertEquals(l.get(0).getStartDate(), b.getStartTime());
        assertEquals(l.get(0).getEndDate(), b.getEndTime());
    }
    
    
    @Test(expected=IllegalStateException.class)
    @Transactional
    @Rollback(true)
    public void testGetReferenceId_fail() {
        final InvoiceDataEntity e = createSampleInvoiceDataEntity();
        
        e.getReferenceId();
    }
    

    @Test
    @Transactional
    @Rollback(true)
    public void testGetReferenceId_success() {
        final InvoiceDataEntity e = createSampleInvoiceDataEntity();
        final BusinessEventEntity b = createSampleBusinessEventEntity();
        b.setSupplierId(e.getSupplierId());
        assertTrue(e.addBusinessEventEntity(b));
        final InvoiceDataEntity saved = getInvoiceDataRepository().save(e);
        getInvoiceDataRepository().flush();
        
        final String expected = String.format("%s.%04d", "supplierId", saved.getId());
        
        assertEquals(expected, saved.getReferenceId());
     }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testAggregateTotalAmount_invoice_data() {
        final InvoiceDataEntity ie = createSampleInvoiceDataEntity();
        
        final BusinessEventEntity be2 = createSampleBusinessEventEntity();
        be2.setSupplierId(ie.getSupplierId());
        be2.addItemEntity(createSampleItemEntity());
        be2.addItemEntity(createSampleItemEntity());
        assertTrue(ie.addBusinessEventEntity(be2));
        
        final BusinessEventEntity be3 = createSampleBusinessEventEntity();
        be3.setEventId("anotherid");
        be3.addItemEntity(createSampleItemEntity());
        be3.setSupplierId(ie.getSupplierId());
        assertTrue(ie.addBusinessEventEntity(be3));
        
        InvoiceDataEntity saved = getInvoiceDataRepository().save(ie);
        
        assertEquals(2100, saved.getTotalAmount().intValue());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testInsertUpdateFind_assign_business_event() {
        final InvoiceDataEntity ie = createSampleInvoiceDataEntity();
        final BusinessEventEntity be = createSampleBusinessEventEntity();

        // can only be added if supplierId matches, i.e. returns false at this stage
        assertFalse(ie.addBusinessEventEntity(be));

        // save
        // from now on it should be correct
        be.setSupplierId(ie.getSupplierId());
        getBusinessEventRepository().save(be);
        getBusinessEventRepository().flush();
        
        //
        final BusinessEventEntity beSaved = getBusinessEventRepository().findBySupplierIdAndPendingIsTrue(ie.getSupplierId()).get(0);      
        assertTrue(ie.addBusinessEventEntity(beSaved));
        
        final BusinessEventEntity bePending = createSampleBusinessEventEntity();
        bePending.setEventId("anotherEventId");
        bePending.setSupplierId(ie.getSupplierId());
        getBusinessEventRepository().save(bePending);
                
        getInvoiceDataRepository().save(ie);
        getInvoiceDataRepository().flush();
        
        List<InvoiceDataEntity> l = getInvoiceDataRepository().getInvoiceDataBySupplierIdAndPaymentResponsibleBetweenDates(be.getSupplierId(), be.getPaymentResponsible(), CoreUtil.MIN_DATE, new Date());
        
        assertEquals(1, l.size());
        
        assertEquals(1, l.get(0).getBusinessEventEntities().size());
        
        // should be one pending left
        assertEquals(1, getBusinessEventRepository().findBySupplierIdAndPendingIsTrue(ie.getSupplierId()).size());
    }    
}
