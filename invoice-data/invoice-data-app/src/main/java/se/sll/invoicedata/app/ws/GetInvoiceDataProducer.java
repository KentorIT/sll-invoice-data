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

import riv.sll.invoicedata.getinvoicedata._1.rivtabp21.GetInvoiceDataResponderInterface;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataResponse;
import riv.sll.invoicedata.getinvoicedataresponder._1.ObjectFactory;
import se.sll.invoicedata.core.utility.Operation;

/**
 * 
 * Queries invoice data and pending business events.
 * 
 * @author muqkha
 */
public class GetInvoiceDataProducer extends AbstractProducer implements GetInvoiceDataResponderInterface {

    static final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public GetInvoiceDataResponse getInvoiceData(final String logicalAddress,
            final GetInvoiceDataRequest request) {

        final GetInvoiceDataResponse response = objectFactory.createGetInvoiceDataResponse();

        response.setResultCode(fulfill(new Runnable() {
            @Override
            public void run() {
            	throwExceptionIfNotAuthorizedToAccessSupplier(request.getSupplierId());
            	throwExceptionIfNotAuthorizedToAccessOperation(request.getSupplierId(), Operation.GET);
                //Fetching unprocessed events with price
                response.getRegisteredEventList().addAll(getInvoiceDataService()
                        .getAllUnprocessedBusinessEvents(request));
                               
            }
        }));
        
        return response;
    }

}
