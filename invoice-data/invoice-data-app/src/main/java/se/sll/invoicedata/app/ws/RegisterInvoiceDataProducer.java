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

package se.sll.invoicedata.app.ws;


import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import riv.sll.invoicedata.registerinvoicedataresponder._1.ObjectFactory;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponse;

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
                getInvoiceDataService().registerEvent(parameters);
            }
        }));
        
        return response;
    }
    
}
