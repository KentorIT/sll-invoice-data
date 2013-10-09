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

/**
 * 
 */
package se.sll.invoicedata.app.ws;

import riv.sll.invoicedata.listinvoicedata._1.rivtabp21.ListInvoiceDataResponderInterface;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataResponse;
import riv.sll.invoicedata.listinvoicedataresponder._1.ObjectFactory;

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
            	listIDataResponse.getInvoiceDataList().addAll(getInvoiceDataService().listAllInvoiceData(parameters));                
            }
        }));
        
        return listIDataResponse;
	}

}
