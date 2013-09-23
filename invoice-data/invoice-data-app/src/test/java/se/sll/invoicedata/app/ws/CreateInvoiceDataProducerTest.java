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

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import riv.sll.invoicedata.createinvoicedata._1.rivtabp21.CreateInvoiceDataResponderInterface;

/**
 * @author muqkha
 *
 */
public class CreateInvoiceDataProducerTest {
    
    static CreateInvoiceDataResponderInterface getCreateInvoiceDataService() {
        CreateInvoiceDataResponderInterface iCreateInvoiceDataResponder = null;

        final String URL = "http://localhost:8080/invoice-data-app/ws/createInvoiceData";
        // Endpoint.publish(URL, new RegisterInvoiceDataProducer());

        try {
            URL wsdlURL = new URL(URL + "?wsdl");

            String namespaceURI = "http://ws.app.invoicedata.sll.se/";
            String serviceName = "CreateInvoiceDataProducerService";

            QName serviceQN = new QName(namespaceURI, serviceName);

            Service service = Service.create(wsdlURL, serviceQN);
            
            iCreateInvoiceDataResponder = service
                    .getPort(CreateInvoiceDataResponderInterface.class);
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iCreateInvoiceDataResponder;
    }

}
