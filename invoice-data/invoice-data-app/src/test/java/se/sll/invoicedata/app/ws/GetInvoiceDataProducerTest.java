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

import org.h2.tools.Server;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedata._1.rivtabp21.GetInvoiceDataResponderInterface;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataResponse;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import se.sll.invoicedata.app.TestSupport;

/**
 * @author muqkha
 * 
 */
public class GetInvoiceDataProducerTest extends TestSupport {

    private final String LOGICAL_ADDRESS = "loc:TolkPortalen";
    
    @Test
    public void get_InvoiceData_Unprocessed_Success() {
    	
    	Event event = createSampleEventData();
        RegisterInvoiceDataProducerTest
                .getRegisterInvoiceDataService()
                .registerInvoiceData(LOGICAL_ADDRESS, event);

        GetInvoiceDataRequest request = new GetInvoiceDataRequest();
        request.setSupplierId(event.getSupplierId());
        request.setPaymentResponsible(event.getPaymentResponsible());

        GetInvoiceDataResponse response = getGetInvoiceDataService()
                .getInvoiceData(LOGICAL_ADDRESS, request);

        Assert.assertNotNull(response);
        Assert.assertEquals(ResultCodeEnum.OK, response.getResultCode()
                .getCode());
        
        Assert.assertNotNull(response.getRegisteredEventList());
    }
    
    @Test
    public void get_InvoiceData_Incomplete_Request_Data_Fail() {
    	Event event = createSampleEventData();
        RegisterInvoiceDataProducerTest
                .getRegisterInvoiceDataService()
                .registerInvoiceData(LOGICAL_ADDRESS, event);

        GetInvoiceDataRequest request = new GetInvoiceDataRequest();
        request.setSupplierId(event.getSupplierId());

        GetInvoiceDataResponse response = getGetInvoiceDataService()
                .getInvoiceData(LOGICAL_ADDRESS, request);
        
        Assert.assertNotNull(response);
        Assert.assertEquals(ResultCodeEnum.ERROR, response.getResultCode()
                .getCode());
        
        request.setSupplierId("");
        request.setPaymentResponsible(event.getPaymentResponsible());
        response = getGetInvoiceDataService()
                .getInvoiceData(LOGICAL_ADDRESS, request);        
    }

    //@Test
    public void get_InvoiceData_On_Processed_By_supplier_id_success() {

        RegisterInvoiceDataProducerTest
                .getRegisterInvoiceDataService()
                .registerInvoiceData(LOGICAL_ADDRESS, createSampleEventData());

        CreateInvoiceDataRequest invoiceDataRequest = new CreateInvoiceDataRequest();
        invoiceDataRequest.setSupplierId("SID123");
        invoiceDataRequest.setCreatedBy("createdBy");

//        CreateInvoiceDataProducerTest.getCreateInvoiceDataService()
//                .createInvoiceData(LOGICAL_ADDRESS, invoiceDataRequest);

        GetInvoiceDataRequest request = new GetInvoiceDataRequest();
        request.setSupplierId("SID123");

        GetInvoiceDataResponse response = getGetInvoiceDataService()
                .getInvoiceData(LOGICAL_ADDRESS, request);

        Assert.assertNotNull(response);
        Assert.assertEquals(ResultCodeEnum.OK, response.getResultCode()
                .getCode());

        Assert.assertNotNull(response.getInvoiceDataList());
        Assert.assertEquals(0, response.getInvoiceDataList().size());
    }

   // @Test
    public void get_InvoiceData_Some_Processed_Some_Unprocessed_By_supplier_id_success() {

        RegisterInvoiceDataResponderInterface registerIDRInterface = RegisterInvoiceDataProducerTest
                .getRegisterInvoiceDataService();

        Event event = createSampleEventData();

        registerIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, event);
        event.setEventId("EID5678");
        registerIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, event);

        CreateInvoiceDataRequest invoiceDataRequest = new CreateInvoiceDataRequest();
        invoiceDataRequest.setSupplierId("SID123");
        invoiceDataRequest.setCreatedBy("createdBy");

        CreateInvoiceDataProducerTest.getCreateInvoiceDataService()
                .createInvoiceData(LOGICAL_ADDRESS, invoiceDataRequest);

        event.setEventId("EID901011");
        registerIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, event);

        // Now we have 3 events connected to same supplier of which 2 events got
        // processed!
        // One unprocessed event shall be returned on getInvoiceData

        GetInvoiceDataRequest request = new GetInvoiceDataRequest();
        request.setSupplierId("SID123");

        GetInvoiceDataResponse response = getGetInvoiceDataService()
                .getInvoiceData(LOGICAL_ADDRESS, request);

        Assert.assertNotNull(response);
        Assert.assertEquals(ResultCodeEnum.OK, response.getResultCode()
                .getCode());

        Assert.assertNotNull(response.getInvoiceDataList());
        Assert.assertEquals(1, response.getInvoiceDataList().size());
    }

    private GetInvoiceDataResponderInterface getGetInvoiceDataService() {
        GetInvoiceDataResponderInterface iGetInvoiceDataResponder = null;

        final String URL = "http://localhost:8080/invoice-data-app/ws/getInvoiceData";
        // Endpoint.publish(URL, new RegisterInvoiceDataProducer());

        try {
            URL wsdlURL = new URL(URL + "?wsdl");

            String namespaceURI = "http://ws.app.invoicedata.sll.se/";
            String serviceName = "GetInvoiceDataProducerService";

            QName serviceQN = new QName(namespaceURI, serviceName);

            Service service = Service.create(wsdlURL, serviceQN);

            iGetInvoiceDataResponder = service
                    .getPort(GetInvoiceDataResponderInterface.class);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iGetInvoiceDataResponder;
    }

}
