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

import java.util.List;

import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.ResultCode;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.getpendinginvoicedata._1.rivtabp21.GetPendingInvoiceDataResponderInterface;
import riv.sll.invoicedata.getpendinginvoicedataresponder._1.GetPendingInvoiceDataResponse;
import riv.sll.invoicedata.getpendinginvoicedataresponder._1.ObjectFactory;
import se.sll.invoicedata.core.access.Operation;

/**
 * 
 * Queries invoice data and pending business events.
 * 
 * @author muqkha
 */
public class GetPendingInvoiceDataProducer extends AbstractProducer implements GetPendingInvoiceDataResponderInterface {

    static final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public GetPendingInvoiceDataResponse getPendingInvoiceData(final String logicalAddress, Object parameters) {

        final GetPendingInvoiceDataResponse response = objectFactory.createGetPendingInvoiceDataResponse();

        response.setResultCode(fulfill(new Runnable() {
            @Override
            public void run() {
            	throwExceptionIfSystemHasNoAccessToOperation(Operation.GET_PENDING_INVOICE_DATA);
            	
            	List<InvoiceDataHeader> invoiceList = getInvoiceDataService().getAllPendingInvoiceData();
            	for (InvoiceDataHeader metaData : invoiceList) {
            		throwExceptionIfSupplierHasNoAccessToOperation(Operation.GET_PENDING_INVOICE_DATA, metaData.getSupplierId());
            	}
            	response.getInvoiceDataList().addAll(invoiceList);
                               
            }
        }, setResultCode()));
        
        return response;
    }
    
    ResultCode setResultCode() {
    	ResultCode rc = new ResultCode();
    	rc.setCode(ResultCodeEnum.OK);
    	return rc;
    }
}
