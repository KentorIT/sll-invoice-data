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

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;

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
	 * @param buinsessEventEntity
	 *            the business event entity.
	 */
	void registerEvent(Event event);
		
	/**
	 * Returns all unprocessed events for a particular supplier.
	 * 
	 * @param supplierId
	 *            the supplier id.
	 * @return the result list, might be empty if no none events found.
	 */
	List<RegisteredEvent> getAllUnprocessedBusinessEvents(
			String supplierId, String paymentResponsible);


	/**
	 * Fetches all invoiced data for a particular supplier and payee
	 * 
	 * @param supplierId
	 * @param paymentResponsible
	 * @return
	 */
	List<InvoiceDataHeader> getAllInvoicedData(String supplierId,
			String paymentResponsible);

	/**
	 * Creates an invoice data object.
	 * 
	 * @param createInvoiceDataRequest
	 * @return the invoice data reference id.
	 */
    String createInvoiceData(CreateInvoiceDataRequest createInvoiceDataRequest);
    
    /**
     * 
     * @param referenceId
     * @return
     */
    InvoiceData getInvoiceDataByReferenceId(String referenceId);
    
    List<InvoiceData> listAllInvoiceData(ListInvoiceDataRequest request);
}