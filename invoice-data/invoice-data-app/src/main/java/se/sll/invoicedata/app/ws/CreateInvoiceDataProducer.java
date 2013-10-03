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

import riv.sll.invoicedata.createinvoicedata._1.rivtabp21.CreateInvoiceDataResponderInterface;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataResponse;
import riv.sll.invoicedata.createinvoicedataresponder._1.ObjectFactory;

/**
 * Creates invoice data.
 * 
 * @author muqkha
 */
public class CreateInvoiceDataProducer extends AbstractProducer implements CreateInvoiceDataResponderInterface {
	
    static final ObjectFactory factory = new ObjectFactory();
    
    @Override
    public CreateInvoiceDataResponse createInvoiceData(final String logicalAddress,
            final CreateInvoiceDataRequest parameters) {
        

        final CreateInvoiceDataResponse createInvoiceDataResp = factory.createCreateInvoiceDataResponse();
        
        createInvoiceDataResp.setResultCode(invoke(new Runnable() {
            @Override
            public void run() {
                createInvoiceDataResp.setReferenceId(getInvoiceDataService().createInvoiceData(parameters));                
            }
        }));
        
        return createInvoiceDataResp;        
    }

}
