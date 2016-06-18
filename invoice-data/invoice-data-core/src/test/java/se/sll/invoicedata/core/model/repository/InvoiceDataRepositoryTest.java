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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.support.ExceptionCodeMatches;
import se.sll.invoicedata.core.support.TestSupport;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * Unit tests.
 * 
 * @author Peter
 *
 */
public class InvoiceDataRepositoryTest extends TestSupport {
	
	@Autowired
    private InvoiceDataService invoiceDataService;
    
    @Test
    @Transactional
    @Rollback(true)
    public void testInsertFind_InvoiceDataEntity() {
        final InvoiceDataEntity e = createSamplePendingInvoiceDataEntity();
        final BusinessEventEntity b = createSampleBusinessEventEntity();
        b.setSupplierId(e.getSupplierId());
        b.setCostCenter(e.getCostCenter());
        b.setPaymentResponsible(e.getPaymentResponsible());
        assertTrue(e.addBusinessEventEntity(b));
        
        getInvoiceDataRepository().save(e);
        getInvoiceDataRepository().flush();
        
        final List<InvoiceDataEntity> l = getInvoiceDataRepository().
        		findBySupplierIdAndPaymentResponsibleAndCostCenterAndPendingIsTrue(
        				e.getSupplierId(), e.getPaymentResponsible(), e.getCostCenter());
        
        assertNotNull(l);
        assertEquals(1, l.size());
        assertEquals(b.getStartTime(), l.get(0).getStartDate());
        assertEquals(b.getEndTime(), l.get(0).getEndDate());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testGetReferenceId_Fail() {
        final InvoiceDataEntity e = createSampleInvoiceDataEntity();
        
        thrown.expect(InvoiceDataServiceException.class);
        thrown.expect(new ExceptionCodeMatches(InvoiceDataErrorCodeEnum.ILLEGAL_STATE_INVALID_INVOICEDATA_REFERENCE_ID));
        
        e.getReferenceId();
    }
    

    @Test
    @Transactional
    @Rollback(true)
    public void testGetReferenceId_Success() {
        final InvoiceDataEntity e = createSampleInvoiceDataEntity();
        final BusinessEventEntity b = createSampleBusinessEventEntity();
        b.setSupplierId(e.getSupplierId());
        assertTrue(e.addBusinessEventEntity(b));
        
        final InvoiceDataEntity saved = getInvoiceDataRepository().save(e);
        getInvoiceDataRepository().flush();
        
        final String expected = String.valueOf(saved.getId());
        
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
    public void testInsertUpdate_Find_pending_business_event() {
        final InvoiceDataEntity ie = createSamplePendingInvoiceDataEntity();
        final BusinessEventEntity be = createSampleBusinessEventEntity();

        // can only be added if supplierId matches, i.e. returns false at this stage
        assertFalse(ie.addBusinessEventEntity(be));

        // save
        // from now on it should be correct
        be.setSupplierId(ie.getSupplierId());
        getBusinessEventRepository().save(be);
        getBusinessEventRepository().flush();
        
        //Fetch again after saving
        final List<BusinessEventEntity> all = getBusinessEventRepository().findByEventIdAndCreditIsNull(be.getEventId());
        assertEquals(1, all.size());
        final BusinessEventEntity beSaved = all.get(0);
        assertTrue(ie.addBusinessEventEntity(beSaved));
        
        final BusinessEventEntity bePending = createSampleBusinessEventEntity();
        bePending.setEventId("anotherEventId");
        bePending.setSupplierId(ie.getSupplierId());
        getBusinessEventRepository().save(bePending);
                
        getInvoiceDataRepository().save(ie);
        getInvoiceDataRepository().flush();
        
        List<InvoiceDataEntity> l = getInvoiceDataRepository().findBySupplierIdAndStartDateBetween(ie.getSupplierId(), CoreUtil.MIN_DATE, new Date()); 
        
        assertEquals(1, l.size());    
	    //Note: Due to performance issues, using cascade=CascadeType.DETACH
        assertEquals(0, l.get(0).getBusinessEventEntities().size());
        
        // now there must be 2 events left, should be one pending and other attached to Invoicedata
        List<BusinessEventEntity> all2 = getBusinessEventRepository().findAll();
        assertEquals(2, all2.size());
        
        assertEquals(1, getBusinessEventRepository().findByEventIdAndCreditIsNull(bePending.getEventId()).size());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testDelete_From_Repository_Delete_From_InvoiceDataRepo_First() {
	    final Event e = createSampleEvent();
	    invoiceDataService.registerEvent(e);
	    
	    assertEquals(1, getInvoiceDataRepository().findAll().size());
	    assertEquals(1, getBusinessEventRepository().findAll().size());
	    
	    //Note: Due to performance issues, using cascade=CascadeType.DETACH
	    
	    //Will throw a exception
	    thrown.expect(DataIntegrityViolationException.class);
        
	    getInvoiceDataRepository().deleteAll();
	    
	    assertEquals(0, getInvoiceDataRepository().findAll().size());
	    assertEquals(0, getBusinessEventRepository().findAll().size());
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testDelete_From_Repository_Delete_From_BusinessEventRepo_First() {
	    final Event e = createSampleEvent();
	    invoiceDataService.registerEvent(e);
	    
	    assertEquals(1, getInvoiceDataRepository().findAll().size());
	    assertEquals(1, getBusinessEventRepository().findAll().size());
	    
	    InvoiceDataEntity invoiceDataEntity = getInvoiceDataRepository().findAll().get(0);
	    //Note: This cannot be done in this implementation, 
	    // not adding to invoiceDataEntity.businessEvents due to performance issues
	    //invoiceDataEntity.getBusinessEventEntities().get(0).setInvoiceData(null);
	    assertNotNull(invoiceDataEntity);
	    
	    List<BusinessEventEntity> all = getBusinessEventRepository().findByEventIdAndCreditIsNull(e.getEventId());
	    assertEquals(1, all.size());
	    all.get(0).setInvoiceData(null);
	    
	    getBusinessEventRepository().delete(all.get(0));
	    
	    assertEquals(1, getInvoiceDataRepository().findAll().size());
	    assertEquals(0, getBusinessEventRepository().findAll().size());
    }
}
