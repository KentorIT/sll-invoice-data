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

import org.junit.Test;

import riv.sll.invoicedata._1.RegisteredEvent;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;

/**
 * Unit tests AppUtil.
 * 
 * 
 * @author Peter
 *
 */
public class AppUtilTest extends TestSupport {

    @Test
    public void testAppUtil_copyProperties() {
        RegisteredEvent e = createSampleEventData();
        BusinessEventEntity be = new BusinessEventEntity();
        copyProperties(be, e, RegisteredEvent.class);
        
        final RegisteredEvent e2 =  copyProperties(new RegisteredEvent(), be, RegisteredEvent.class);        

        assertEquals(e.getEventId(), e2.getEventId());
        assertEquals(e.getHealthCareCommission(), e2.getHealthCareCommission());
        assertEquals(e.getPaymentResponsible(), e2.getPaymentResponsible());
        assertEquals(e.getServiceCode(), e2.getServiceCode());
        assertEquals(e.getSupplierId(), e2.getSupplierId());
        assertEquals(e.getSupplierName(), e2.getSupplierName());
        assertEquals(e.getStartTime(), e2.getStartTime());
        assertEquals(e.getEndTime(), e2.getEndTime());
        
    }
}
