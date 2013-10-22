/**
 * Copyright (c) 2013 SLL, <http://sll.se>
 *
 * This file is part of Invoice-Data.
 *
 *     Invoice Data is free software: you can redistribute it and/or modify
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

/**
 * 
 */
package se.sll.invoicedata.app.ws;

import riv.sll.invoicedata.viewinvoicedata._1.rivtabp21.ViewInvoiceDataResponderInterface;
import riv.sll.invoicedata.viewinvoicedataresponder._1.ObjectFactory;
import riv.sll.invoicedata.viewinvoicedataresponder._1.ViewInvoiceDataRequest;
import riv.sll.invoicedata.viewinvoicedataresponder._1.ViewInvoiceDataResponse;

/**
 * 
 * Returns all events for a specific invoice data object.
 * 
 * @author muqkha
 */
public class ViewInvoiceDataProducer extends AbstractProducer implements ViewInvoiceDataResponderInterface {

    static final ObjectFactory objectFactory = new ObjectFactory();

	@Override
	public ViewInvoiceDataResponse viewInvoiceData(final String logicalAddress,
			final ViewInvoiceDataRequest parameters) {		
		
        final ViewInvoiceDataResponse viewIDataResponse = objectFactory.createViewInvoiceDataResponse();

        viewIDataResponse.setResultCode(fulfill(new Runnable() {
            @Override
            public void run() {
                viewIDataResponse.setInvoiceData(getInvoiceDataService().getInvoiceDataByReferenceId(parameters.getReferenceId()));
            }
        }));
        
        return viewIDataResponse;
	}

}
