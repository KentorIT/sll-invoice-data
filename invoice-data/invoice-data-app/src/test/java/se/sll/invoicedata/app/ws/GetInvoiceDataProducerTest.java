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

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Assert;
import org.junit.Test;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedata._1.rivtabp21.GetInvoiceDataResponderInterface;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataResponse;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;

/**
 * @author muqkha
 * 
 */
public class GetInvoiceDataProducerTest {

    private final String LOGICAL_ADDRESS = "loc:TolkPortalen";

    @Test
    public void get_InvoiceData_By_Supplier_Id_success() {

        RegisterInvoiceDataProducerTest
                .getRegisterInvoiceDataService()
                .registerInvoiceData(LOGICAL_ADDRESS, createSampleInvoiceData());

        GetInvoiceDataRequest request = new GetInvoiceDataRequest();
        request.setSupplierId("SID123");

        GetInvoiceDataResponse response = getGetInvoiceDataService()
                .getInvoiceData(LOGICAL_ADDRESS, request);

        Assert.assertNotNull(response);
        Assert.assertEquals(ResultCodeEnum.OK, response.getResultCode()
                .getCode());

        Assert.assertNotNull(response.getInvoiceDataList());
        Assert.assertEquals(2, response.getInvoiceDataList().size());

    }

    @Test
    public void get_InvoiceData_On_Processed_By_supplier_id_success() {

        RegisterInvoiceDataProducerTest
                .getRegisterInvoiceDataService()
                .registerInvoiceData(LOGICAL_ADDRESS, createSampleInvoiceData());

        CreateInvoiceDataRequest invoiceEvents = new CreateInvoiceDataRequest();
        invoiceEvents.setSupplierId("SID123");

        CreateInvoiceDataProducerTest.getCreateInvoiceDataService()
                .createInvoiceData(LOGICAL_ADDRESS, invoiceEvents);

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

    @Test
    public void get_InvoiceData_Some_Processed_Some_Unprocessed_By_supplier_id_success() {

        RegisterInvoiceDataResponderInterface registerIDRInterface = RegisterInvoiceDataProducerTest
                .getRegisterInvoiceDataService();

        Event event = createSampleInvoiceData();

        registerIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, event);
        event.setEventId("EID5678");
        registerIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, event);

        CreateInvoiceDataRequest invoiceEvents = new CreateInvoiceDataRequest();
        invoiceEvents.setSupplierId("SID123");

        CreateInvoiceDataProducerTest.getCreateInvoiceDataService()
                .createInvoiceData(LOGICAL_ADDRESS, invoiceEvents);

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

    private Event createSampleInvoiceData() {
        Event event = new Event();
        event.setEventId("EID1234");
        event.setAcknowledgedBy("sign:X");
        event.setSupplierName("SNX");

        event.setAcknowledgedTime(getCurrentDate());
        event.setServiceCode("SCABCD");
        event.setPaymentResponsible("HSF");
        event.setSupplierId("SID123");
        event.setStartTime(getCurrentDate());
        event.setEndTime(getCurrentDate());

        Item item = new Item();
        item.setDescription("Item is kind of a product");
        item.setItemId("IT101");
        item.setQty(new BigDecimal(2));
        event.getItemList().add(item);

        return event;
    }

    private XMLGregorianCalendar getCurrentDate() {
        XMLGregorianCalendar calendar = null;
        try {
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    new GregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return calendar;
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
