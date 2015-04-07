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

package se.sll.invoicedata.app.ws;

import riv.sll.invoicedata.listinvoicedata._1.rivtabp21.ListInvoiceDataResponderInterface;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataResponse;
import riv.sll.invoicedata.listinvoicedataresponder._1.ObjectFactory;
import se.sll.invoicedata.core.access.Operation;

/**
 * 
 * Returns all events for a specific invoice data object.
 * 
 * @author muqkha
 */
public class ListInvoiceDataProducer extends AbstractProducer implements ListInvoiceDataResponderInterface {

    static final ObjectFactory objectFactory = new ObjectFactory();

	@Override
    public ListInvoiceDataResponse listInvoiceData(final String logicalAddress,
            final ListInvoiceDataRequest parameters) {              
    
		final ListInvoiceDataResponse listIDataResponse = objectFactory.createListInvoiceDataResponse();
		
		listIDataResponse.setResultCode(fulfill(new Runnable() {
			@Override
			public void run() {
				throwExceptionIfSystemHasNoAccessToOperation(Operation.LIST_INVOICE_DATA);
				throwExceptionIfSupplierHasNoAccessToOperation(Operation.LIST_INVOICE_DATA, parameters.getSupplierId());
			    listIDataResponse.getInvoiceDataList().addAll(getInvoiceDataService().listAllInvoiceData(parameters));                
			}
		}));
		
		return listIDataResponse;
	}

}
