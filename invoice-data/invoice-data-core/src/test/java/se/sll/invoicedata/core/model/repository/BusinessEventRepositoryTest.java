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
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * Unit tests.
 * 
 * @author Peter
 */
public class BusinessEventRepositoryTest extends TestSupport {

    @Test
    @Transactional
    @Rollback(true)
    public void testInsertFind_BusinessEventEntity() {
        
        final BusinessEventEntity e = createSampleBusinessEventEntity();
        getBusinessEventRepository().save(e);
        getBusinessEventRepository().flush();
        
        final List<BusinessEventEntity> all = getBusinessEventRepository().findAll();
        assertNotNull(all);
        assertEquals(1, all.size());
        
        final BusinessEventEntity f = all.get(0);
        
        assertEquals(e.getEventId(), f.getEventId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getAcknowledgedBy(), f.getAcknowledgedBy());
        assertNotNull(f.getCreatedTimestamp());   
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testFind_BusinessEvent_By_Id() {
                
        final BusinessEventEntity e = createSampleBusinessEventEntity();
        getBusinessEventRepository().save(e);
        getBusinessEventRepository().flush();
        
        final BusinessEventEntity f = getBusinessEventRepository().findByEventIdAndPendingIsTrueAndCreditIsNull("event-123");
        assertNotNull(f);
        
        assertEquals(e.getEventId(), f.getEventId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getAcknowledgedBy(), f.getAcknowledgedBy());
        assertNotNull(f.getCreatedTimestamp());   
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testFind_BusinessEvent_By_Supplier_Id() {
        final BusinessEventEntity e = createSampleBusinessEventEntity();
        getBusinessEventRepository().save(e);
        getBusinessEventRepository().flush();
        
        List<BusinessEventEntity> l = getBusinessEventRepository().findBySupplierIdAndPendingIsTrue("12342");
        
        assertNotNull(l);
        assertEquals(1, l.size());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testInsertFind_ItemEntity() {
        
        final BusinessEventEntity e = createSampleBusinessEventEntity();
        e.addItemEntity(createSampleItemEntity());
        
        getBusinessEventRepository().save(e);
        getBusinessEventRepository().flush();
        
        final List<BusinessEventEntity> all = getBusinessEventRepository().findAll();
        assertNotNull(all);
        assertEquals(1, all.size());
        
        final BusinessEventEntity f = all.get(0);
        
        assertEquals(e.getEventId(), f.getEventId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getAcknowledgedBy(), f.getAcknowledgedBy());
        assertNotNull(f.getCreatedTimestamp());
        assertNotNull(f.getItemEntities());
        assertEquals(e.getItemEntities().get(0).getItemId(), 
        		f.getItemEntities().get(0).getItemId());
       
    }
    
}
