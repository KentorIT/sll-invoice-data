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

package se.sll.invoicedata.app.ws;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponse;
import se.sll.invoicedata.app.TestSupport;

/**
 * @author muqkha
 */
public class RegisterInvoiceDataProducerTest extends TestSupport {


	private static RegisterInvoiceDataResponderInterface regIDRInterface;

	@BeforeClass
	public static void setUp() {
		regIDRInterface = getRegisterInvoiceDataService();
	}

	@AfterClass
	public static void tearDown() {
		regIDRInterface = null;
	}

	@Test
	public void registerInvoiceData_normal_test_result_pass() {

		RegisterInvoiceDataResponse response = regIDRInterface
				.registerInvoiceData(LOGICAL_ADDRESS, createRandomEventData());

		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be OK in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.OK,
				response.getResultCode().getCode());

	}

	@Test(expected = SOAPFaultException.class)
	public void registerInvoiceData_without_Items_result_fail() {

		Event invoiceData = createRandomEventData();
		invoiceData.setItemList(null);

		regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);
	}

	@Test
	public void registerInvoiceData_with_incorrect_qty_fail() {

		Event invoiceData = createRandomEventData();
		invoiceData.getItemList().get(0).setQty(new BigDecimal(-2));

		RegisterInvoiceDataResponse response = regIDRInterface
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);

		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());
	}

	@Test
	public void registerInvoiceData_with_incorrect_start_end_time_fail() {

		Event invoiceData = createRandomEventData();
		Calendar startTime = getCurrentDate().toGregorianCalendar();
		startTime.add(Calendar.DAY_OF_MONTH, +1);

		invoiceData.setStartTime(toXMLGregorianCalendar(startTime.getTime()));
		invoiceData.setEndTime(getCurrentDate());

		RegisterInvoiceDataResponse response = regIDRInterface
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);

		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());
	}

	@Test
	public void registerInvoiceData_with_incorrect_data_result_fail() {

		String emptyStr = new String("");
		Event invoiceData = createRandomEventData();
		invoiceData.setAcknowledgedBy(emptyStr);

		RegisterInvoiceDataResponse response = regIDRInterface
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);

		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());

		invoiceData.setEventId(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());

		invoiceData.setHealthCareCommission(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());

		invoiceData.setPaymentResponsible(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());

		invoiceData.setServiceCode(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());

		invoiceData.setSupplierId(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());

		invoiceData.setSupplierName(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.ERROR,
				response.getResultCode().getCode());

	}

	@Test(expected = SOAPFaultException.class)
	public void registerInvoiceData_with_empty_invoicedata_result_exception() {

		Event event = new Event();
		regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS, event);
	}

	public static RegisterInvoiceDataResponderInterface getRegisterInvoiceDataService() {
	    if (regIDRInterface == null) {
	        regIDRInterface = createWebServiceConsumer(RegisterInvoiceDataResponderInterface.class);
	    }
	    return regIDRInterface;
	}
}
