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
	 * @param event
	 *            
	 */
	void registerEvent(Event event);
	
	/**
	 * Returns all pending events for a particular supplier.
	 * @param request
	 * 
	 * @return the result list, might be empty if no none events found.
	 */
	List<RegisteredEvent> getAllPendingBusinessEvents(GetInvoiceDataRequest request);

	/**
	 * Creates an invoice data object.
	 * 
	 * @param createInvoiceDataRequest
	 * @return the invoice data reference id.
	 */
    String createInvoiceData(CreateInvoiceDataRequest createInvoiceDataRequest);
    
    /**
     * Fetches by reference id (reference id is created when createInvoice is 
     * invoked on a supplier)
     * 
     * @param referenceId
     * @return InvoiceData
     */
    InvoiceData getInvoiceDataByReferenceId(String referenceId);
    
    /**
     * Lists all invoice data.
     * 
     * @param request
     * @return List<InvoiceDataHeader>
     */
    List<InvoiceDataHeader> listAllInvoiceData(ListInvoiceDataRequest request);

    /**
	 * Returns all pending invoices for a particular supplier.
	 * @param request
	 * 
	 * @return the result list, might be empty if no none events found.
	 */
	List<InvoiceDataHeader> getAllPendingInvoiceData();
	
	/**
     * Returns max limit of events to return.
     * 
     * @return the limit.
     */
    int getEventMaxFindResultSize();

}