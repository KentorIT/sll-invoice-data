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

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
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
        
        assertEquals(e.getId(), f.getId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getSignedBy(), f.getSignedBy());
        assertNull(e.getCreatedTimestamp());
        assertNotNull(f.getCreatedTimestamp());   
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void testFind_BusinessEvent_By_Id() {
        
        final BusinessEventEntity e = createSampleBusinessEventEntity();
        getBusinessEventRepository().save(e);
        getBusinessEventRepository().flush();
        
        final BusinessEventEntity f = getBusinessEventRepository().findOne("event-123");
        assertNotNull(f);
        
        assertEquals(e.getId(), f.getId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getSignedBy(), f.getSignedBy());
        assertNull(e.getCreatedTimestamp());
        assertNotNull(f.getCreatedTimestamp());   
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
        
        assertEquals(e.getId(), f.getId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getSignedBy(), f.getSignedBy());
        assertNull(e.getCreatedTimestamp());
        assertNotNull(f.getCreatedTimestamp());
        assertNotNull(f.getItemEntities());
        assertEquals(e.getItemEntities().get(0).getItemId(), 
        		f.getItemEntities().get(0).getItemId());
       
    }
    
    private ItemEntity createSampleItemEntity() {
    	ItemEntity i = new ItemEntity();
    	i.setDescription("Item is kind of a product");
		i.setItemId("IT101");
		i.setQty(2.0f);
		
		return i;
	
    }
    
    private BusinessEventEntity createSampleBusinessEventEntity() {
    	BusinessEventEntity e = new BusinessEventEntity();
        e.setId("event-123");
        e.setSignedBy("Peter Larsson");
        e.setSupplierName("Dummy");
        e.setSignedTimestamp(new Date());
        e.setServiceCode("XYZ");
        e.setSupplierId("12342");
        e.setStartTimestamp(new Date());
        e.setEndTimestamp(new Date());
        
        return e;
    }
}
