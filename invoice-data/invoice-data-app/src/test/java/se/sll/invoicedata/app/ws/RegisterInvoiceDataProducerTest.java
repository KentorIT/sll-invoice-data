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

import java.math.BigDecimal;
import java.util.Calendar;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ui.context.Theme;

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
	
	@Test
	public void registerInvoiceData_concurrent_updates() {
        final Thread[] threads = new Thread[5];
        final Event event = createRandomEventData();
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int n = 0; n < 50; n++) {
                        RegisterInvoiceDataResponse response = regIDRInterface
                                .registerInvoiceData(LOGICAL_ADDRESS, event);
                        Assert.assertEquals("Result code should be OK in this case: "
                                + response.getResultCode().getMessage(), ResultCodeEnum.OK,
                                response.getResultCode().getCode());
                   }
                }
            });
            threads[i].start();
        }

        for (final Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
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
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
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
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
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
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
				response.getResultCode().getCode());

		invoiceData.setEventId(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
				response.getResultCode().getCode());

		invoiceData.setHealthCareCommission(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
				response.getResultCode().getCode());

		invoiceData.setPaymentResponsible(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
				response.getResultCode().getCode());

		invoiceData.setServiceCode(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
				response.getResultCode().getCode());

		invoiceData.setSupplierId(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
				response.getResultCode().getCode());

		invoiceData.setSupplierName(emptyStr);
		response = regIDRInterface.registerInvoiceData(LOGICAL_ADDRESS,
				invoiceData);
		Assert.assertEquals("Result code should be ERROR in this case: "
				+ response.getResultCode().getMessage(), ResultCodeEnum.REQUEST_ERROR,
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
