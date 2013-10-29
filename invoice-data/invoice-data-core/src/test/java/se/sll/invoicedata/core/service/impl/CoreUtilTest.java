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

package se.sll.invoicedata.core.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyGenericLists;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

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
    
    @Test
    public void testDateConversion() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.YEAR, 999);
    	XMLGregorianCalendar xmlGregCal = CoreUtil.toXMLGregorianCalendar(cal.getTime());
    	assertEquals(999, xmlGregCal.getYear());
    	assertEquals(cal.get(Calendar.MONTH), xmlGregCal.getMonth() - 1);
    	assertEquals(cal.get(Calendar.DATE), xmlGregCal.getDay());
    	assertEquals(cal.get(Calendar.HOUR_OF_DAY), xmlGregCal.getHour());
    	assertEquals(cal.get(Calendar.MINUTE), xmlGregCal.getMinute());
    	assertEquals(cal.get(Calendar.SECOND), xmlGregCal.getSecond());
    	
    	Date date = CoreUtil.toDate(xmlGregCal);
    	assertNotNull(date);
    	Calendar newCal = Calendar.getInstance();
    	newCal.setTime(date);
    	assertEquals(cal.get(Calendar.YEAR), newCal.get(Calendar.YEAR));
    	assertEquals(cal.get(Calendar.MONTH), newCal.get(Calendar.MONTH));
    	assertEquals(cal.get(Calendar.DATE), newCal.get(Calendar.DATE));
    	assertEquals(cal.get(Calendar.HOUR), newCal.get(Calendar.HOUR));
    	assertEquals(cal.get(Calendar.MINUTE), newCal.get(Calendar.MINUTE));
    	assertEquals(cal.get(Calendar.SECOND), newCal.get(Calendar.SECOND));
    	
    }
}
