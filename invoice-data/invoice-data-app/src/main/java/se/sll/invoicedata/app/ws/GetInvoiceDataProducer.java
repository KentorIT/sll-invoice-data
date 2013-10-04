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

import riv.sll.invoicedata.getinvoicedata._1.rivtabp21.GetInvoiceDataResponderInterface;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataResponse;
import riv.sll.invoicedata.getinvoicedataresponder._1.ObjectFactory;

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

        response.setResultCode(invoke(new Runnable() {
            @Override
            public void run() {
                //Fetching unprocessed events with price
                response.getRegisteredEventList().addAll(getInvoiceDataService()
                        .getAllUnprocessedBusinessEvents(request.getSupplierId(), request.getPaymentResponsible()));

                //Fetching invoiced data            
                response.getInvoiceDataList().addAll(getInvoiceDataService()
                        .getAllInvoicedData(request.getSupplierId(), request.getPaymentResponsible()));               
            }
        }));
        
        return response;
    }

}
