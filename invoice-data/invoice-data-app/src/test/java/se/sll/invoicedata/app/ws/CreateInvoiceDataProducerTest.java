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

/**
 * 
 */
package se.sll.invoicedata.app.ws;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

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

    void register(String supplierId, String paymentResp, String costCenter, int n) {
        for (int i = 0; i < n; i++) {
            Event event1 = createRandomEventData();
            event1.setSupplierId(supplierId);
            event1.setPaymentResponsible(paymentResp);
            event1.setCostCenter(costCenter);
            RegisterInvoiceDataResponderInterface reg = RegisterInvoiceDataProducerTest.getRegisterInvoiceDataService();
            RegisterInvoiceDataResponse resp = reg.registerInvoiceData(LOGICAL_ADDRESS, event1);
            assertEquals(ResultCodeEnum.OK, resp.getResultCode().getCode());  
        }
    }

    List<RegisteredEvent> events(String supplierId, String paymentResp) {
        GetInvoiceDataResponderInterface get = GetInvoiceDataProducerTest.getGetInvoiceDataService();
        GetInvoiceDataRequest req = new GetInvoiceDataRequest();
        req.setPaymentResponsible(paymentResp);
        req.setSupplierId(supplierId);
        GetInvoiceDataResponse resp = get.getInvoiceData(LOGICAL_ADDRESS, req);
        assertEquals(ResultCodeEnum.OK, resp.getResultCode().getCode());  
        return resp.getRegisteredEventList();
    }
    
    @Test
    public void testCreate_Invoicedata_Pass() {
        final int n = 10;
        final String costCenter = "cost-center";
        
        String supplierId1 = genRandomAlphaNData(5);
        register(supplierId1, "HSF", costCenter, n);
        
        String supplierId2 = genRandomAlphaNData(5);
        register(supplierId2, "HSF", costCenter,  n);
        
        CreateInvoiceDataRequest req = getCreateInvoiceRequest(supplierId1, "HSF", costCenter);
        CreateInvoiceDataResponse resp = createInvoiceDataResponderInterface.createInvoiceData(LOGICAL_ADDRESS, req);
        assertEquals(ResultCodeEnum.INFO, resp.getResultCode().getCode());
    }
    
    @Test
    public void testCreate_Invoicedata_With_Missing_Indata_Fail() {
        final int n = 10;
        final String costCenter = "cost-center";
        String supplierId1 = genRandomAlphaNData(5);
        register(supplierId1, "HSF", costCenter, n);
        
        thrown.expect(SOAPFaultException.class);
        
        CreateInvoiceDataRequest req = getCreateInvoiceRequest(supplierId1, "HSF", null);
        createInvoiceDataResponderInterface.createInvoiceData(LOGICAL_ADDRESS, req);
    }


    public static CreateInvoiceDataResponderInterface getCreateInvoiceDataService() {
        if (createInvoiceDataResponderInterface == null) {
            createInvoiceDataResponderInterface = createWebServiceConsumer(CreateInvoiceDataResponderInterface.class);
        }
        return createInvoiceDataResponderInterface;
    }  

}
