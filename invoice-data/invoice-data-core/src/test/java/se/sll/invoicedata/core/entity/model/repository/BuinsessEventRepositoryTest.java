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

package se.sll.invoicedata.core.entity.model.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.entity.model.BusinessEventEntity;
import se.sll.invoicedata.core.support.TestSupport;

public class BuinsessEventRepositoryTest extends TestSupport {
    

    @Test
    @Transactional
    @Rollback(true)
    public void testInsertFind() {
        BusinessEventEntity e = new BusinessEventEntity();
        e.setId("event-123");
        e.setSignedBy("Peter Larsson");
        e.setSupplierName("Dummy");
        
        getBuinsessEventRepository().save(e);
        getBuinsessEventRepository().flush();
        
        final List<BusinessEventEntity> all = getBuinsessEventRepository().findAll();
        assertNotNull(all);
        assertEquals(1, all.size());
        
        BusinessEventEntity f = all.get(0);
        
        assertEquals(e.getId(), f.getId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getSignedBy(), f.getSignedBy());
        assertNull(e.getCreatedTime());
        assertNotNull(f.getCreatedTime());
        
    }

}
