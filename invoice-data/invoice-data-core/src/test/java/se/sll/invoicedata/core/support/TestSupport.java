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

package se.sll.invoicedata.core.support;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;

/**
 * Abstracts JUnit and Spring configuration stuff, and is intended to extend
 * all test classes.
 * 
 * @author Peter
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/invoice-data-service.xml")
@ActiveProfiles(profiles={"db-embedded","test"}, inheritProfiles=true)
public abstract class TestSupport {

    @Autowired
    private BusinessEventRepository businessEventRepository;
    @Autowired
    private InvoiceDataRepository invoiceDataRepository;

    protected BusinessEventRepository getBusinessEventRepository() {
        return businessEventRepository;
    }

    protected InvoiceDataRepository getInvoiceDataRepository() {
        return invoiceDataRepository;
    }

    protected ItemEntity createSampleItemEntity() {
    	ItemEntity i = new ItemEntity();
    	i.setDescription("Item is kind of a product");
		i.setItemId("IT101");
		i.setQty(BigDecimal.valueOf(2.0));
		i.setPrice(BigDecimal.valueOf(350.0));
		
		return i;
	
    }
    
    protected BusinessEventEntity createSampleBusinessEventEntity() {
    	BusinessEventEntity e = new BusinessEventEntity();
        e.setId("event-123");
        e.setAcknowledgedBy("Peter Larsson");
        e.setSupplierName("Dummy");
        e.setAcknowledgedTime(new Date());
        e.setHealthCareCommission("HSF");
        e.setServiceCode("XYZ");
        e.setPaymentResponsible("HSF");
        e.setSupplierId("12342");
        e.setStartTime(new Date());
        e.setEndTime(new Date());
        
        return e;
    }

    protected InvoiceDataEntity createSampleInvoiceDataEntity() {
        final InvoiceDataEntity e = new InvoiceDataEntity();
        
        e.setSupplierId("supplierId");
        e.setCreatedBy("createdBy");
    
        return e;
    }
}
