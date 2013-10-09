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
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

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
import riv.sll.invoicedata.listinvoicedata._1.rivtabp21.ListInvoiceDataResponderInterface;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataResponse;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponse;
import se.sll.invoicedata.app.TestSupport;

/**
 * @author muqkha
 *
 */
public class ListInvoiceDataProducerTest extends TestSupport {

	private static ListInvoiceDataResponderInterface listIDRInterface;
	private static Event event;
	private static String referenceId;

	@BeforeClass
	public static void setUp() {
		listIDRInterface = getListInvoiceDataService();
		referenceId = testPrerequisiteStep_ListInvoiceData();
	}

	@AfterClass
	public static void tearDown() {
		listIDRInterface = null;
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
	private static String testPrerequisiteStep_ListInvoiceData() {
		
		//1. Register event 
		event = createRandomEventData();
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
	
	@Test
	public void testListInvoiceData_With_SupplierId_Result_Success() {
		ListInvoiceDataRequest request = new ListInvoiceDataRequest();
		request.setSupplierId(event.getSupplierId());
		request.setFromDate(getCurrentDate());
		request.setToDate(getCurrentDate());
		request.setPaymentResponsible(event.getPaymentResponsible());
		
		ListInvoiceDataResponse invoiceDataResponse = listIDRInterface.listInvoiceData(LOGICAL_ADDRESS, request);
		
		Assert.assertNotNull(invoiceDataResponse);
		
		Assert.assertEquals(ResultCodeEnum.OK, invoiceDataResponse.getResultCode().getCode());
		
		Assert.assertNotNull(invoiceDataResponse.getInvoiceDataList());
		
	}
	

	static ListInvoiceDataResponderInterface getListInvoiceDataService() {
		
		ListInvoiceDataResponderInterface iListInvoiceDataResponder = null;		
		try {
			URL wsdlURL = new URL(getWSDLURL("listInvoiceData"));
			String serviceName = "ListInvoiceDataProducerService";
			QName serviceQN = new QName(NAMESPACE_URI, serviceName);

			Service service = Service.create(wsdlURL, serviceQN);
			iListInvoiceDataResponder = service
					.getPort(ListInvoiceDataResponderInterface.class);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iListInvoiceDataResponder;
	}
}
