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
		getIDReq.setPaymentResponsible(event.getPaymentResponsible());
		
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
		viewIDReq.setReferenceId(referenceId);
		
		// 5. Use the referenceId to fetch the InvoiceData;
		// 		ReferenceId fetches InvoiceData on demand.
		ViewInvoiceDataResponse viewIDResp = viewIDRInterface.viewInvoiceData(LOGICAL_ADDRESS, viewIDReq);
		
		Assert.assertNotNull(viewIDResp);
		Assert.assertNotNull(viewIDResp.getInvoiceData());
		Assert.assertNotNull(viewIDResp.getInvoiceData().getEventList());
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
		viewIDReq.setReferenceId("");
		ViewInvoiceDataResponse viewIDResp = viewIDRInterface.viewInvoiceData(LOGICAL_ADDRESS, viewIDReq);
		
		Assert.assertNotNull(viewIDResp);
		Assert.assertEquals(ResultCodeEnum.ERROR, viewIDResp.getResultCode().getCode());
	}
	
	public static ViewInvoiceDataResponderInterface getViewInvoiceDataService() {
	    if (viewIDRInterface == null) {
	        viewIDRInterface = createService(ViewInvoiceDataResponderInterface.class);
	    }
		return viewIDRInterface;
	}
}
