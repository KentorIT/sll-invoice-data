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

package se.sll.invoicedata.core.service;

import java.util.List;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;

/**
 * Defines Invoice Data service API.
 * 
 * @author Peter
 *
 */
public interface InvoiceDataService {
    
    /**
     * Registers a business event.
     * 
     * @param buinsessEventEntity the business event entity.
     */
    void registerBusinessEvent(BusinessEventEntity businessEventEntity);
    
    /**
     * Returns a business event by id.
     * 
     * @param eventId the event id.
     * @return the business event with the given id or null if none found.
     */
    BusinessEventEntity getBusinessEvent(String eventId);
    
    
    /**
     * Returns all unprocessed events for a particular supplier.
     * 
     * @param supplierId the supplier id.
     * @return the result list, might be empty if no none events found.
     */
    List<BusinessEventEntity> getAllUnprocessedBusinessEvents(String supplierId, String paymentResponsible);
    
    /**
     * Creates invoice data for a particular supplier.
     * @param supplierId
     * @return
     */
    void createInvoiceData(String supplierId);
    
    /**
     * Returns all pending business entities by event ids.
     * 
     * @param eventIdList the list of event ids.
     * @return the list, exactly matching the number of ids.
     * 
     * @throws IllegalArgumentException when the number of ids doesn't match expected result.
     */
    List<BusinessEventEntity> getPendingBusinessEntities(String supplierId, List<String> eventIdList);
    
    /**
     * Creates an invoice data.
     * 
     * @param invoiceDataEntity the entity to create.
     */
    void registerInvoiceData(InvoiceDataEntity invoiceDataEntity);
    
    /**
     * Fetches all invoiced data for a particular supplier and payee
     * @param supplierId
     * @param paymentResponsible
     * @return
     */
	List<InvoiceDataEntity> getAllInvoicedData(String supplierId,
			String paymentResponsible);
}