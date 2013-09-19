/**
 *  Copyright (c) 2013 SLL <http://sll.se/>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package se.sll.invoicedata.core.model.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
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
        
        getInvoiceDataRepository().save(e);
        getInvoiceDataRepository().flush();
        
        final List<InvoiceDataEntity> l = getInvoiceDataRepository().findBySupplierId(e.getSupplierId());
        
        assertNotNull(l);
        assertEquals(1, l.size());      
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
        
        final InvoiceDataEntity saved = getInvoiceDataRepository().save(e);
        getInvoiceDataRepository().flush();
        
        final String expected = String.format("%s.%06d", "supplierId", saved.getId());
        
        assertEquals(expected, saved.getReferenceId());
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
        bePending.setId("anotherEventId");
        bePending.setSupplierId(ie.getSupplierId());
        getBusinessEventRepository().save(bePending);
                
        getInvoiceDataRepository().save(ie);
        getInvoiceDataRepository().flush();
        
        List<InvoiceDataEntity> l = getInvoiceDataRepository().findBySupplierId(ie.getSupplierId());
        
        assertEquals(1, l.size());
        
        assertEquals(1, l.get(0).getBusinessEventEntities().size());
        
        // should be one pending left
        assertEquals(1, getBusinessEventRepository().findBySupplierIdAndPendingIsTrue(ie.getSupplierId()).size());
    }    
}
