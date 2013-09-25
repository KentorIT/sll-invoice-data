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

/**
 * 
 */
package se.sll.invoicedata.core.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * @author muqkha
 *
 */
public class InvoiceDataServiceImplTest extends TestSupport {

	@Autowired	
	private InvoiceDataService invoiceDataService;
	
	@Test
    @Rollback(true)
    public void testFind_BusinessEvent_By_Id() {
        
        final BusinessEventEntity e = createSampleBusinessEventEntity();
        e.addItemEntity(createSampleItemEntity());
        
        invoiceDataService.registerBusinessEvent(e);
        
        final BusinessEventEntity f = invoiceDataService.getBusinessEvent("event-123");
        assertNotNull(f);
        
        assertEquals(e.getEventId(), f.getEventId());
        assertEquals(e.getSupplierName(), f.getSupplierName());
        assertEquals(e.getAcknowledgedBy(), f.getAcknowledgedBy());
        assertNotNull(f.getCreatedTimestamp());   
    } 
	
	
	private void registerEvents(String supplierId, List<String> ids) {
	    for (final String id : ids) {
	        final BusinessEventEntity e = createSampleBusinessEventEntity();
	        e.setEventId(id);
	        e.setSupplierId(supplierId);
	        e.addItemEntity(createSampleItemEntity());
	        invoiceDataService.registerBusinessEvent(e);
	    }
	}
	
	
	private InvoiceDataEntity registerInvocieData(String supplierId) {
        final List<String> ids = Arrays.asList(new String[] { "event-1", "event-2", "event-3" });
        
        registerEvents(supplierId, ids);
        
        final InvoiceDataEntity ie = new InvoiceDataEntity();
        ie.setSupplierId(supplierId);
        ie.setPaymentResponsible("HSF");
        ie.setCreatedBy("test-auto");
        
        
        final List<BusinessEventEntity> l = invoiceDataService.getPendingBusinessEntities(supplierId, ids);
        assertEquals(l.size(), ids.size());
        
        for (final BusinessEventEntity e : l) {
            ie.addBusinessEventEntity(e);
        }
        
        invoiceDataService.registerInvociceData(ie);
	    
        return ie;
	}
	
    @Test
    @Transactional
    @Rollback(true)
	public void testRegisterInvocieData_From_Pending_And_Credit() {
        final String supplierId = "test-supplier-45";
        final InvoiceDataEntity ie = registerInvocieData(supplierId);
        
        registerEvents(supplierId, Arrays.asList(new String[] { "event-1", "event-2", "event-3" }));
        final List<BusinessEventEntity> l = invoiceDataService.getAllUnprocessedBusinessEvents(supplierId);
        // one credit event shall be created for each new
        assertEquals(ie.getBusinessEventEntities().size()*2, l.size());
        
        int credits = 0;
        for (final BusinessEventEntity e : l) {
            if (e.isCredit()) {
                credits++;
            }
        }
        assertEquals(3, credits);
	}
		
}
