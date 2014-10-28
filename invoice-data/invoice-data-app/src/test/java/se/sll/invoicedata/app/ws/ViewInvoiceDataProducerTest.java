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

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataResponse;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataResponse;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponse;
import riv.sll.invoicedata.viewinvoicedata._1.rivtabp21.ViewInvoiceDataResponderInterface;
import riv.sll.invoicedata.viewinvoicedataresponder._1.ViewInvoiceDataRequest;
import riv.sll.invoicedata.viewinvoicedataresponder._1.ViewInvoiceDataResponse;
import se.sll.invoicedata.app.TestSupport;

/**
 * @author muqkha
 *
 */
public class ViewInvoiceDataProducerTest extends TestSupport {

	private static ViewInvoiceDataResponderInterface viewIDRInterface;
	private static String supplierId;
	private static String referenceId;

	@BeforeClass
	public static void setUp() {
		viewIDRInterface = getViewInvoiceDataService();
		referenceId = testPrerequisiteStep_ViewInvoiceData();
	}

	@AfterClass
	public static void tearDown() {
		viewIDRInterface = null;
	}
	
	/**
	 * Steps:
	 * 1. Register event -> Result should be OK
	 * 2. GetInvoice -> Returns registered event list
	 * 		The list should not be empty.
	 * 3. Iterate the list and store RegisteredEventId
	 * 4. Use RegisteredEventId list to request
	 * 		CreateInvoice -> Returns a reference id for
	 * 		the created InvoiceData
	 */
	private static String testPrerequisiteStep_ViewInvoiceData() {
		
		//1. Register event 
		Event event = createRandomEventData();
		supplierId = event.getSupplierId();
		RegisterInvoiceDataResponse regIDResp = RegisterInvoiceDataProducerTest.
				getRegisterInvoiceDataService().registerInvoiceData(LOGICAL_ADDRESS, event);
		
		// 1. Register event -> Result should be OK 
		Assert.assertNotNull(regIDResp);
		Assert.assertEquals(ResultCodeEnum.OK, regIDResp.getResultCode().getCode());
		
		//2. GetInvoice		 
		GetInvoiceDataRequest getIDReq = new GetInvoiceDataRequest();
		getIDReq.setSupplierId(event.getSupplierId());
		//getIDReq.setPaymentResponsible(event.getPaymentResponsible());
		
		GetInvoiceDataResponse invoiceResp = GetInvoiceDataProducerTest.
				getGetInvoiceDataService().getInvoiceData(LOGICAL_ADDRESS, getIDReq);
		
		//2. GetInvoice -> Returns registered event list
		//		The list should not be empty.
		Assert.assertNotNull(invoiceResp);
		Assert.assertNotNull(invoiceResp.getRegisteredEventList());
		
		List<RegisteredEvent> regEventList = invoiceResp.getRegisteredEventList();
		//3. Iterate the list and store RegisteredEventId
		List<String> ackIdList = new ArrayList<String>();
		for (RegisteredEvent regEvent : regEventList) {
			ackIdList.add(regEvent.getAcknowledgementId());
		}
		
		// 4. Use RegisteredEventId list to request CeateInvoice
		CreateInvoiceDataRequest invoiceDataRequest = new CreateInvoiceDataRequest();
		invoiceDataRequest.setSupplierId(event.getSupplierId());
		invoiceDataRequest.setPaymentResponsible(event.getPaymentResponsible());
		invoiceDataRequest.setCreatedBy("test");
		invoiceDataRequest.getAcknowledgementIdList().addAll(ackIdList);

		CreateInvoiceDataResponse createIDResp = CreateInvoiceDataProducerTest.
				getCreateInvoiceDataService().createInvoiceData(LOGICAL_ADDRESS, invoiceDataRequest);
		
		// 4. Use RegisteredEventId list to request
		// 		CreateInvoice -> Returns a reference id for the created InvoiceData
		Assert.assertNotNull(createIDResp.getReferenceId());
		
		return createIDResp.getReferenceId();
	}
	
	/**
	 * Steps:
	 * 1-4 @See {@link #testPrerequisiteStep_ViewInvoiceData()} 
	 * 5. Use the referenceId to fetch the InvoiceData;
	 * 		ReferenceId fetches InvoiceData on demand. 
	 */
	@Test
	public void testViewInvoiceData_1_Event_Result_Success() {
		
		ViewInvoiceDataRequest viewIDReq = new ViewInvoiceDataRequest();
		viewIDReq.setSupplierId(supplierId);
		viewIDReq.setReferenceId(referenceId);
		
		// 5. Use the referenceId to fetch the InvoiceData;
		// 		ReferenceId fetches InvoiceData on demand.
		ViewInvoiceDataResponse viewIDResp = viewIDRInterface.viewInvoiceData(LOGICAL_ADDRESS, viewIDReq);
		
		Assert.assertNotNull(viewIDResp);
		Assert.assertNotNull(viewIDResp.getInvoiceData());
		Assert.assertNotNull(viewIDResp.getInvoiceData().getRegisteredEventList());
		Assert.assertEquals(ResultCodeEnum.OK, viewIDResp.getResultCode().getCode());
		
		Assert.assertEquals(supplierId, viewIDResp.getInvoiceData().getSupplierId());
	}
	
	@Test(expected = SOAPFaultException.class)
	public void testViewInvoiceData_Incomplete_Request_Result_Fail() {
		ViewInvoiceDataRequest viewIDReq = new ViewInvoiceDataRequest();
		viewIDRInterface.viewInvoiceData(LOGICAL_ADDRESS, viewIDReq);		
	}
	
	@Test
	public void testViewInvoiceData_Empty_Request_Result_Fail() {
		ViewInvoiceDataRequest viewIDReq = new ViewInvoiceDataRequest();
		viewIDReq.setSupplierId(supplierId);
		viewIDReq.setReferenceId("");
		ViewInvoiceDataResponse viewIDResp = viewIDRInterface.viewInvoiceData(LOGICAL_ADDRESS, viewIDReq);
		
		Assert.assertNotNull(viewIDResp);
		Assert.assertEquals(ResultCodeEnum.REQUEST_ERROR, viewIDResp.getResultCode().getCode());
	}
	
	public static ViewInvoiceDataResponderInterface getViewInvoiceDataService() {
	    if (viewIDRInterface == null) {
	        viewIDRInterface = createWebServiceConsumer(ViewInvoiceDataResponderInterface.class);
	    }
		return viewIDRInterface;
	}
}
