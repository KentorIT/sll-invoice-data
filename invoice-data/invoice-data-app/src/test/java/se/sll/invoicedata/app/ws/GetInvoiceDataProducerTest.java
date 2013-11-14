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

package se.sll.invoicedata.app.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
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
import se.sll.invoicedata.app.TestSupport;

/**
 * Obligatory parameters for fetching the invoice data is SUPPLIER_ID!
 * 
 * 
 * @author muqkha
 * 
 */
public class GetInvoiceDataProducerTest extends TestSupport {

	private static GetInvoiceDataResponderInterface getIDRInterface;
	private static Event[] unprocessedInvoiceData;
	
	@BeforeClass
	public static void setUp() {
		getIDRInterface = getGetInvoiceDataService();
		testPrerequisiteStep_GetInvoiceData();
	}
	
	@AfterClass
	public static void tearDown() {
		getIDRInterface = null;
	}
	
	private static void testPrerequisiteStep_GetInvoiceData() {
		unprocessedInvoiceData = createTestData();
		for (Event event : unprocessedInvoiceData) {
			RegisterInvoiceDataProducerTest.getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, event);
		}		
	}
	
	/**
	 * Fetches the registered event: 
	 * 1. Event is registered
	 * 2. Fetches data based on supplier id and payment responsible
	 * 3. Must return code OK and the registered event list
	 */
	@Test
	public void get_InvoiceData_Unprocessed_Events_Success() {
		//Fetches data based on supplier id and payment responsible
		GetInvoiceDataRequest request = new GetInvoiceDataRequest();
		request.setSupplierId(unprocessedInvoiceData[0].getSupplierId());
		request.setPaymentResponsible(unprocessedInvoiceData[0].getPaymentResponsible());

		GetInvoiceDataResponse response = getIDRInterface.getInvoiceData(LOGICAL_ADDRESS, request);
		
		//Must return code OK and the registered event list
		Assert.assertNotNull(response);
		Assert.assertEquals(ResultCodeEnum.OK, response.getResultCode().getCode());
		Assert.assertFalse(response.getRegisteredEventList().isEmpty());
	}

	@Test
	public void get_InvoiceData_Based_On_Different_Request_Parameters() {				
		//Fetch by only supplier id
		GetInvoiceDataRequest request = new GetInvoiceDataRequest();
		request.setSupplierId(unprocessedInvoiceData[0].getSupplierId());

		GetInvoiceDataResponse response = getIDRInterface.getInvoiceData(LOGICAL_ADDRESS, request);

		Assert.assertNotNull(response);
		Assert.assertEquals(ResultCodeEnum.OK, response.getResultCode().getCode());
		
		//Now request without any supplier id
		request.setSupplierId("");
		request.setPaymentResponsible(unprocessedInvoiceData[0].getPaymentResponsible());
		response = getIDRInterface.getInvoiceData(LOGICAL_ADDRESS, request);

		Assert.assertNotNull(response);
		Assert.assertFalse(ResultCodeEnum.OK == response.getResultCode()
				.getCode());
		//Not a valid request without supplier id!
	}
	
	/**
	 * Same as fetching unprocessed events
	 */
	@Test
	public void test_Get_InvoiceData_Before_CreateInvoiceData() {
		get_InvoiceData_Unprocessed_Events_Success();				
	}

	/**
	 * Steps:
	 * 1.Register
	 * 2. Get/Fetch registered event
	 * 3. Create invoice
	 * 4. Get/Fetch invoiced data 
	 */
	@Test
	public void get_InvoiceData_Before_And_After_CreateInvoiceData_Success() {
		
		//Registers an event
		Event event = createRandomEventData();
		RegisterInvoiceDataProducerTest.getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, event);

		//Fetches the registered event 
		GetInvoiceDataRequest request = new GetInvoiceDataRequest();
		request.setSupplierId(event.getSupplierId());
		request.setPaymentResponsible(event.getPaymentResponsible());

		GetInvoiceDataResponse response = getIDRInterface
				.getInvoiceData(LOGICAL_ADDRESS, request);
		
		Assert.assertNotNull(response);
		Assert.assertTrue(response.getRegisteredEventList().size() >= 1);
		
		//Registered and fetched an invoice data; 
		//Getting acknowledgement id from the registered event
		List<RegisteredEvent> regEventList = response.getRegisteredEventList();

		List<String> ackIdList = new ArrayList<String>();
		for (RegisteredEvent regEvent : regEventList) {
			ackIdList.add(regEvent.getAcknowledgementId());
		}
		
		//Create the invoice
		CreateInvoiceDataRequest invoiceDataRequest = new CreateInvoiceDataRequest();
		invoiceDataRequest.setSupplierId(event.getSupplierId());
		invoiceDataRequest.setPaymentResponsible(event.getPaymentResponsible());
		invoiceDataRequest.setCreatedBy(getClass().getName());
		invoiceDataRequest.getAcknowledgementIdList().addAll(ackIdList);

		CreateInvoiceDataProducerTest.getCreateInvoiceDataService()
				.createInvoiceData(LOGICAL_ADDRESS, invoiceDataRequest);
		
		Assert.assertEquals(ResultCodeEnum.OK, response.getResultCode().getCode());			
    }

	@Test
	public void get_InvoiceData_Before_And_After_CreateInvoiceData_Multiple_Success() {
		
		Event event = createRandomEventData();		
		RegisterInvoiceDataResponderInterface registerIDRInterface = 
				RegisterInvoiceDataProducerTest.getRegisterInvoiceDataService();
		
		//Registering two events; same supplier id
		registerIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, event);
		event.setEventId(genRandomAlphaNData(5));
		event.setAcknowledgementId(UUID.randomUUID().toString());
		registerIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, event);

		GetInvoiceDataRequest getInvoiceReq = new GetInvoiceDataRequest();
		getInvoiceReq.setSupplierId(event.getSupplierId());
		getInvoiceReq.setPaymentResponsible(event.getPaymentResponsible());

		GetInvoiceDataResponse getInvoiceResp = getIDRInterface
				.getInvoiceData(LOGICAL_ADDRESS, getInvoiceReq);
		
		Assert.assertNotNull(getInvoiceResp);
		//Two registered events back in response
		Assert.assertTrue(getInvoiceResp.getRegisteredEventList().size() >= 2);
		
		List<RegisteredEvent> regEventList = getInvoiceResp.getRegisteredEventList();
		List<String> eventRefList = new ArrayList<String>();
		for (RegisteredEvent regEvent : regEventList) {
			eventRefList.add(regEvent.getAcknowledgementId());
		}
		//Removing one item; generating only one invoice
		eventRefList.remove(0);  
		
		CreateInvoiceDataRequest invoiceDataRequest = new CreateInvoiceDataRequest();
		invoiceDataRequest.setSupplierId(event.getSupplierId());
		invoiceDataRequest.setPaymentResponsible(event.getPaymentResponsible());
		invoiceDataRequest.setCreatedBy(getClass().getName());
		invoiceDataRequest.getAcknowledgementIdList().addAll(eventRefList);

		CreateInvoiceDataResponderInterface createIDResp = CreateInvoiceDataProducerTest.getCreateInvoiceDataService();
		CreateInvoiceDataResponse createResp = createIDResp.createInvoiceData(LOGICAL_ADDRESS, invoiceDataRequest);

		Assert.assertNotNull(createResp);
		Assert.assertEquals(ResultCodeEnum.OK, getInvoiceResp.getResultCode().getCode());	
		Assert.assertNotNull(createResp.getReferenceId());
		
		getInvoiceResp = getIDRInterface.getInvoiceData(LOGICAL_ADDRESS, getInvoiceReq);
		Assert.assertNotNull(getInvoiceResp);
		//One invoiced data back in response and othe the registedEvent which is not invoiced!
		Assert.assertNotNull(getInvoiceResp.getRegisteredEventList());
	}
	
	
	
	public static GetInvoiceDataResponderInterface getGetInvoiceDataService() {
	    if (getIDRInterface == null) {
	        getIDRInterface = createWebServiceConsumer(GetInvoiceDataResponderInterface.class);
	    }
	    return getIDRInterface;
	}

}
