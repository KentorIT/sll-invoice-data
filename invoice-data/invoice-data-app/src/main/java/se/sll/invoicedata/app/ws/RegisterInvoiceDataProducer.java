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


import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import riv.sll.invoicedata.registerinvoicedataresponder._1.ObjectFactory;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponse;
import se.sll.invoicedata.core.access.Operation;

/**
 * Registers new business events.
 * 
 * @author Peter
 */
public class RegisterInvoiceDataProducer extends AbstractProducer implements RegisterInvoiceDataResponderInterface {
    static final ObjectFactory objectFactory = new ObjectFactory();
    
    @Override
    public RegisterInvoiceDataResponse registerInvoiceData(
            final String logicalAddress, final Event parameters) {
        
        final RegisterInvoiceDataResponse response = objectFactory.createRegisterInvoiceDataResponse();

        response.setResultCode(fulfill(new Runnable() {
            @Override
            public void run() {
            	throwExceptionIfSystemHasNoAccessToOperation(Operation.REGISTER_INVOICE_DATA);
            	throwExceptionIfSupplierHasNoAccessToOperation(Operation.REGISTER_INVOICE_DATA, parameters.getSupplierId());
                getInvoiceDataService().registerEvent(parameters);
            }
        }));
        
        return response;
    }
    
}
