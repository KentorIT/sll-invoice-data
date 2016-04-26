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

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.ResultCodeEnum;
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

	@BeforeClass
	public static void setUp() {
		viewIDRInterface = getViewInvoiceDataService();
	}

	@AfterClass
	public static void tearDown() {
		viewIDRInterface = null;
	}
	
	/**
	 * Steps:
	 * 1-4 @See {@link #testPrerequisiteStep_ViewInvoiceData()} 
	 * 5. Use the referenceId to fetch the InvoiceData;
	 * 		ReferenceId fetches InvoiceData on demand. 
	 */
	@Test
	public void testViewInvoiceData_1_Event_Result_Success() {
		
		Event event = createRandomEventData();
		String referenceId = test_PrerequisiteStep_Under_List_And_View_Invoice_data(event);
		ViewInvoiceDataRequest viewIDReq = new ViewInvoiceDataRequest();
		viewIDReq.setReferenceId(referenceId);
		
		// 5. Use the referenceId to fetch the InvoiceData;
		// 		ReferenceId fetches InvoiceData on demand.
		ViewInvoiceDataResponse viewIDResp = viewIDRInterface.viewInvoiceData(LOGICAL_ADDRESS, viewIDReq);
		
		Assert.assertNotNull(viewIDResp);
		Assert.assertNotNull(viewIDResp.getInvoiceData());
		Assert.assertNotNull(viewIDResp.getInvoiceData().getRegisteredEventList());
		Assert.assertEquals(ResultCodeEnum.OK, viewIDResp.getResultCode().getCode());
		
		Assert.assertEquals(event.getSupplierId(), viewIDResp.getInvoiceData().getSupplierId());
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
	        viewIDRInterface = createWebServiceConsumer(ViewInvoiceDataResponderInterface.class);
	    }
		return viewIDRInterface;
	}
}
