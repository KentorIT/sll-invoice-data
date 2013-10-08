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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.createinvoicedata._1.rivtabp21.CreateInvoiceDataResponderInterface;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataResponse;
import riv.sll.invoicedata.getinvoicedata._1.rivtabp21.GetInvoiceDataResponderInterface;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataResponse;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponse;
import se.sll.invoicedata.app.TestSupport;

/**
 * @author muqkha
 *
 */
public class CreateInvoiceDataProducerTest extends TestSupport {

    private static CreateInvoiceDataResponderInterface createInvoiceDataResponderInterface;

    @BeforeClass
    public static void setUp() {
        createInvoiceDataResponderInterface = getCreateInvoiceDataService();
    }
    
    @AfterClass
    public static void tearDown() {
        createInvoiceDataResponderInterface = null;
    }

    //
    void register(String supplierId, String paymentResp, int n) {
        for (int i = 0; i < n; i++) {
            Event event1 = createRandomEventData();
            event1.setSupplierId(supplierId);
            event1.setPaymentResponsible(paymentResp);
            RegisterInvoiceDataResponderInterface reg = RegisterInvoiceDataProducerTest.getRegisterInvoiceDataService();
            RegisterInvoiceDataResponse resp = reg.registerInvoiceData(LOGICAL_ADDRESS, event1);
            assertEquals(ResultCodeEnum.OK, resp.getResultCode().getCode());  
        }
    }

    //
    List<RegisteredEvent> events(String supplierId, String paymentResp) {
        GetInvoiceDataResponderInterface get = GetInvoiceDataProducerTest.getGetInvoiceDataService();
        GetInvoiceDataRequest req = new GetInvoiceDataRequest();
        req.setPaymentResponsible(paymentResp);
        req.setSupplierId(supplierId);
        GetInvoiceDataResponse resp = get.getInvoiceData(LOGICAL_ADDRESS, req);
        assertEquals(ResultCodeEnum.OK, resp.getResultCode().getCode());  
        return resp.getRegisteredEventList();
    }
    
    //
    CreateInvoiceDataRequest request(String supplierId, String paymentResp) {
        CreateInvoiceDataRequest req = new CreateInvoiceDataRequest();
        req.setCreatedBy("test");
        req.setPaymentResponsible(paymentResp);
        req.setSupplierId(supplierId);
        for (RegisteredEvent e : events(supplierId, "HSF")) {
            req.getAcknowledgementIdList().add(e.getAcknowledgementId());
        }
        return req;
    }
    
    
    @Test
    public void testCreate_success() {
        final int n = 10;
        String supplierId1 = genRandomAlphaNData(5);
        register(supplierId1, "HSF", n);
        
        String supplierId2 = genRandomAlphaNData(5);
        register(supplierId2, "HSF", n);
        
        CreateInvoiceDataRequest req = request(supplierId1, "HSF");
        CreateInvoiceDataResponse resp = createInvoiceDataResponderInterface.createInvoiceData(LOGICAL_ADDRESS, req);
        assertEquals(ResultCodeEnum.OK, resp.getResultCode().getCode());
    }
    
    @Test
    public void testCreate_fail() {
        final int n = 10;
        String supplierId1 = genRandomAlphaNData(5);
        register(supplierId1, "HSF", n);
        
        CreateInvoiceDataRequest req = request(supplierId1, "HSF");
        
        // make one id invalid
        req.getAcknowledgementIdList().set(0, "zero");

        CreateInvoiceDataResponse resp = createInvoiceDataResponderInterface.createInvoiceData(LOGICAL_ADDRESS, req);
        assertEquals(ResultCodeEnum.ERROR, resp.getResultCode().getCode());
        assertNotNull(resp.getResultCode().getMessage());
        assertNull(resp.getReferenceId());
    }


    public static CreateInvoiceDataResponderInterface getCreateInvoiceDataService() {
        if (createInvoiceDataResponderInterface == null) {
            createInvoiceDataResponderInterface = createService(CreateInvoiceDataResponderInterface.class);
        }
        return createInvoiceDataResponderInterface;
    }  

}
