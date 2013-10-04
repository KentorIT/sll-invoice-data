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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.support.TestSupport;

import static se.sll.invoicedata.core.service.impl.CoreUtil.*;

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
        Event e = copyProperties(new Event(), be, Event.class);
        
        final RegisteredEvent e2 =  copyProperties(new RegisteredEvent(), be, Event.class);        

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
        copyProperties(new RegisteredEvent(), new String(), String.class);        
        copyProperties(new RegisteredEvent(), new String(), String.class);        
    }

    //@Test
    public void testSupport_copy_from_event_to_businessEntity() {
        BusinessEventEntity be = createSampleBusinessEventEntity();
        Event e = copyProperties(new Event(), be, Event.class);
        
        //TODO: asserts
        List<Item> item = e.getItemList();
        List<ItemEntity> itemEntity = new ArrayList<ItemEntity>();
        copyGenericLists(itemEntity, item, ItemEntity.class, Item.class);
        
        //be.addItemEntity(itemEntity);
        
        
//        RegisteredEvent e2 = new RegisteredEvent();
//        copyFields(e2, be, RegisteredEvent.class);
//        
//        RegisteredEvent rEvent = new RegisteredEvent();
//        AppUtil.copyFields(rEvent, be, RegisteredEvent.class);
//        
//        List<Item> itemList = new ArrayList<Item>();
//        AppUtil.copyFields(itemList, be.getItemEntities(), Item.class);
//        
//        rEvent.setItemList(itemList);
        
    }
}
