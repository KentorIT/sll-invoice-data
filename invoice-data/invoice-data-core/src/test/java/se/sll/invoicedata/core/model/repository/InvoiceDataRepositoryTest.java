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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * Unit tests.
 * 
 * @author Peter
 *
 */
public class InvoiceDataRepositoryTest extends TestSupport {

    @Autowired
    private InvoiceDataRepository invoiceDataRepository;
    
    @Test
    @Transactional
    @Rollback(true)
    public void testInsertFind_InvocieDataEntity() {
        final InvoiceDataEntity e = createSampleInvoiceDataEntity();
        
        invoiceDataRepository.save(e);
        invoiceDataRepository.flush();
        
        final List<InvoiceDataEntity> l = invoiceDataRepository.findBySupplierId(e.getSupplierId());
        
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
        
        final InvoiceDataEntity saved = invoiceDataRepository.save(e);
        invoiceDataRepository.flush();
        
        final String expected = String.format("%s.%06d", "supplierId", saved.getId());
        
        assertEquals(expected, saved.getReferenceId());
     }

    
    static InvoiceDataEntity createSampleInvoiceDataEntity() {
        final InvoiceDataEntity e = new InvoiceDataEntity();
        
        e.setSupplierId("supplierId");
        e.setCreatedBy("createdBy");
    
        return e;
    }
}
