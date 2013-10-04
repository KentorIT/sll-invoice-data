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

package se.sll.invoicedata.app;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import se.sll.invoicedata.core.service.impl.CoreUtil;

/**
 * Testing base class.
 * 
 * @author Peter
 */
public abstract class TestSupport extends CoreUtil {

    
    public static Event createSampleEventData() {
    	Event event = new Event();
        event.setEventId("EID1234");
        event.setAcknowledgedBy("sign:X");
        event.setSupplierName("SNX");
        
        event.setAcknowledgedTime(getCurrentDate());
        event.setServiceCode("SCABCD");
        event.setPaymentResponsible("HSF");
        event.setHealthCareCommission("BVC");
        event.setSupplierId("SID123");
        event.setStartTime(getCurrentDate());
        event.setEndTime(getCurrentDate());
               
        Item item = new Item();
        item.setDescription("Item is kind of a product");
        item.setItemId("IT101");
        item.setQty(new BigDecimal(2));
        
        event.getItemList().add(item);
        
        return event;
    }
    
    public static XMLGregorianCalendar getCurrentDate() {
        return toXMLGregorianCalendar(new Date());
    }
}
