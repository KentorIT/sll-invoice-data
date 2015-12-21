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

import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.createinvoicedata._1.rivtabp21.CreateInvoiceDataResponderInterface;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataResponse;
import riv.sll.invoicedata.createinvoicedataresponder._1.ObjectFactory;
import se.sll.invoicedata.core.access.Operation;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;

/**
 * Creates invoice data.
 * 
 * @author muqkha
 */
public class CreateInvoiceDataProducer extends AbstractProducer implements CreateInvoiceDataResponderInterface {
	
    static final ObjectFactory objectFactory = new ObjectFactory();
    
    @Override
    public CreateInvoiceDataResponse createInvoiceData(final String logicalAddress,
            final CreateInvoiceDataRequest parameters) {
        

        final CreateInvoiceDataResponse createInvoiceDataResp = objectFactory.createCreateInvoiceDataResponse();
        
        createInvoiceDataResp.setResultCode(fulfill(new Runnable() {
            @Override
            public void run() {
            	throwExceptionIfSystemHasNoAccessToOperation(Operation.CREATE_INVOICE_DATA); 
            	throwExceptionIfSupplierHasNoAccessToOperation(Operation.CREATE_INVOICE_DATA, parameters.getSupplierId());
                createInvoiceDataResp.setReferenceId(getInvoiceDataService().createInvoiceData(parameters));
            }
        }, setResultCode()));
        
        return createInvoiceDataResp;        
    }
    
    ResultCode setResultCode() {
    	ResultCode rc = new ResultCode();
    	rc.setCode(ResultCodeEnum.INFO);
    	rc.setApplicationStatusCode(InvoiceDataErrorCodeEnum.SYSTEM_BUSY_WITH_CREATE_INVOICE_REQUEST.getCode());
    	rc.setMessage("Request received, invoice is in the process of generation");
    	return rc;
    }
}
