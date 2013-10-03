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

    static final ObjectFactory factory = new ObjectFactory();

	@Override
	public ViewInvoiceDataResponse viewInvoiceData(final String logicalAddress,
			final ViewInvoiceDataRequest parameters) {		
		
        final ViewInvoiceDataResponse viewIDataResponse = factory.createViewInvoiceDataResponse();

        viewIDataResponse.setResultCode(invoke(new Runnable() {
            @Override
            public void run() {
                viewIDataResponse.setInvoiceData(getInvoiceDataService().getInvoiceDataByReferenceId(parameters.getReferenceId()));
            }
        }));
        return viewIDataResponse;
	}

}
