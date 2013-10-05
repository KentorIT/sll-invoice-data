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

package se.sll.invoicedata.core.service.impl;

import static org.junit.Assert.assertEquals;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyGenericLists;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

import org.junit.Test;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * Unit tests AppUtil.
 * 
 * 
 * @author Peter
 *
 */
public class CoreUtilTest extends TestSupport {

    @Test
    public void testAppUtil_copyProperties() {
        BusinessEventEntity be = createSampleBusinessEventEntity();
        be.addItemEntity(createSampleItemEntity());
        
        Event e = copyProperties(be, Event.class);
        
        final RegisteredEvent e2 =  copyProperties(be, RegisteredEvent.class);     
        
        assertEquals(e.getEventId(), e2.getEventId());
        assertEquals(e.getHealthCareCommission(), e2.getHealthCareCommission());
        assertEquals(e.getPaymentResponsible(), e2.getPaymentResponsible());
        assertEquals(e.getServiceCode(), e2.getServiceCode());
        assertEquals(e.getSupplierId(), e2.getSupplierId());
        assertEquals(e.getSupplierName(), e2.getSupplierName());
        assertEquals(e.getStartTime(), e2.getStartTime());
        assertEquals(e.getEndTime(), e2.getEndTime());        
    }

    @Test
    public void testAppUtil_copyProperties2() {
        copyProperties(new String(), String.class);        
        copyProperties(new String(), String.class);        
    }

    @Test
    public void testSupport_copy_from_event_to_businessEntity() {
        BusinessEventEntity be = createSampleBusinessEventEntity();
        be.addItemEntity(createSampleItemEntity());
        
        final Event e = copyProperties(be, Event.class);
        // TODO: asserts
        
        assertEquals(0, e.getItemList().size());
        
        copyGenericLists(e.getItemList(), be.getItemEntities(), Item.class);
        
        assertEquals(1, e.getItemList().size());
        
        final Item item = e.getItemList().get(0);
        final ItemEntity entity = be.getItemEntities().get(0);
        
        assertEquals(entity.getItemId(), item.getItemId());
        assertEquals(entity.getDescription(), item.getDescription());
        assertEquals(entity.getPrice(), item.getPrice());
        assertEquals(entity.getQty(), item.getQty());
    }
}
