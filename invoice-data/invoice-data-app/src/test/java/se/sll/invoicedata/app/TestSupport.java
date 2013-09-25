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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.app.AppUtil;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;

/**
 * Testing base class.
 * 
 * @author Peter
 */
public abstract class TestSupport extends AppUtil {

    
    public static RegisteredEvent createSampleEventData() {
        RegisteredEvent event = new RegisteredEvent();
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
        event.setTotalAmount(BigDecimal.valueOf(0));
        
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
