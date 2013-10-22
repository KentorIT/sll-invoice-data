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

package se.sll.invoicedata.core.service;

import java.util.List;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
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
	 * @param request TODO
	 * 
	 * @return the result list, might be empty if no none events found.
	 */
	List<RegisteredEvent> getAllUnprocessedBusinessEvents(
			GetInvoiceDataRequest request);


	/**
	 * Fetches all invoiced data for a using criteria:
	 * supplierId OR paymentResponsible OR date range 
	 * 
	 * @param supplierId
	 * @param paymentResponsible
	 * @return
	 */
	List<InvoiceDataHeader> getAllInvoicedData(GetInvoiceDataRequest request);

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
    
    /**
     * 
     * @param request
     * @return
     */
    List<InvoiceDataHeader> listAllInvoiceData(ListInvoiceDataRequest request);
}