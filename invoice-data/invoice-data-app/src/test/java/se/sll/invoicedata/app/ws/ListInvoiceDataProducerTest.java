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
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import riv.sll.invoicedata._1.Event;
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
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * @author muqkha
 *
 */
public class ListInvoiceDataProducerTest extends TestSupport {

	private static ListInvoiceDataResponderInterface listIDRInterface;
	

	@BeforeClass
	public static void setUp() {
		listIDRInterface = getListInvoiceDataService();
	}

	@AfterClass
	public static void tearDown() {
		listIDRInterface = null;
	}
	
	
	@Test
	public void testListInvoiceData_With_All_InParams_Result_Success() {
		
		Event event = createRandomEventData();
		test_PrerequisiteStep_Under_List_And_View_Invoice_data(event);
		
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
	
	@Test
	public void testListAllInvoiceData_With_Different_Alternatives() {
		
		Event event = createRandomEventData();
		test_PrerequisiteStep_Under_List_And_View_Invoice_data(event);
		
		// Request with only supplier id
		ListInvoiceDataRequest invoiceListRequest = new ListInvoiceDataRequest();
		invoiceListRequest.setSupplierId(event.getSupplierId());

		ListInvoiceDataResponse invoiceDataResponse = listIDRInterface.listInvoiceData(LOGICAL_ADDRESS, invoiceListRequest);

		assertNotNull(invoiceDataResponse);
		assertEquals(event.getPaymentResponsible(), invoiceDataResponse.getInvoiceDataList().get(0).getPaymentResponsible());

		// Request with only payment responsible
		invoiceListRequest = new ListInvoiceDataRequest();
		invoiceListRequest.setPaymentResponsible(event.getPaymentResponsible());

		invoiceDataResponse = listIDRInterface.listInvoiceData(LOGICAL_ADDRESS, invoiceListRequest);

		assertNotNull(invoiceDataResponse);
		assertEquals(event.getPaymentResponsible(), invoiceDataResponse.getInvoiceDataList().get(0).getPaymentResponsible());

		// Request with both payment responsible and supplier id
		invoiceListRequest = new ListInvoiceDataRequest();
		invoiceListRequest.setSupplierId(event.getSupplierId());
		invoiceListRequest.setPaymentResponsible(event.getPaymentResponsible());

		invoiceDataResponse = listIDRInterface.listInvoiceData(LOGICAL_ADDRESS, invoiceListRequest);

		assertNotNull(invoiceDataResponse);
		assertEquals(event.getSupplierId(), invoiceDataResponse.getInvoiceDataList().get(0).getSupplierId());
		assertEquals(event.getPaymentResponsible(), invoiceDataResponse.getInvoiceDataList().get(0).getPaymentResponsible());

		// Request with only from date; fromDate (setting year to 1 year back
		// from current year)
		invoiceListRequest = new ListInvoiceDataRequest();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
		invoiceListRequest.setFromDate(CoreUtil.toXMLGregorianCalendar(cal.getTime()));
		invoiceListRequest.setSupplierId(event.getSupplierId());

		invoiceDataResponse = listIDRInterface.listInvoiceData(LOGICAL_ADDRESS, invoiceListRequest);
		
		assertNotNull(invoiceDataResponse);
		assertEquals(event.getSupplierId(), invoiceDataResponse.getInvoiceDataList().get(0).getSupplierId());

		// Request with only to date; toDate
		invoiceListRequest = new ListInvoiceDataRequest();
		invoiceListRequest.setPaymentResponsible(event.getPaymentResponsible());
		cal.set(Calendar.YEAR, 9999);
		invoiceListRequest.setToDate(CoreUtil.toXMLGregorianCalendar(cal.getTime()));

		invoiceDataResponse = listIDRInterface.listInvoiceData(LOGICAL_ADDRESS, invoiceListRequest);
		
		assertNotNull(invoiceDataResponse);
		assertEquals(event.getPaymentResponsible(), invoiceDataResponse.getInvoiceDataList().get(0).getPaymentResponsible());
	}

	static ListInvoiceDataResponderInterface getListInvoiceDataService() {
		if (listIDRInterface == null) {
		    listIDRInterface = createWebServiceConsumer(ListInvoiceDataResponderInterface.class);
		}
		return listIDRInterface;
	}
}
