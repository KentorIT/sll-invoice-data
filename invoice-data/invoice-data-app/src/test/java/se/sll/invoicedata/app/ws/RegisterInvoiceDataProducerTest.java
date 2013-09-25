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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Assert;
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

	private final String LOGICAL_ADDRESS = "loc:TolkPortalen";
	
	@Test
	public void registerInvoiceData_normal_test_result_pass() {

		RegisterInvoiceDataResponse response = getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, createSampleEventData());
		
		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be OK in this case: " + response.getResultCode().getMessage(), ResultCodeEnum.OK, response.getResultCode().getCode());	

	}
	
	@Test(expected = SOAPFaultException.class)
	public void registerInvoiceData_without_Items_result_fail() {
		
		Event invoiceData = createSampleEventData();
		invoiceData.setItemList(null);
		
		getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);
	}
	
	@Test
	public void registerInvoiceData_with_incorrect_qty_fail() {
		
		Event invoiceData = createSampleEventData();
		invoiceData.getItemList().get(0).setQty(new BigDecimal(-2));
						
		RegisterInvoiceDataResponse response = getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);
		
		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be ERROR in this case: " + response.getResultCode().getMessage(), ResultCodeEnum.ERROR, response.getResultCode().getCode());	
	}
	
	@Test
	public void registerInvoiceData_with_incorrect_start_end_time_fail() {
		
		Event invoiceData = createSampleEventData();
		Calendar startTime = getCurrentDate().toGregorianCalendar();
		startTime.add(Calendar.DAY_OF_MONTH, +1);
		
		invoiceData.setStartTime(toXMLGregorianCalendar(startTime.getTime()));
		invoiceData.setEndTime(getCurrentDate());
						
		RegisterInvoiceDataResponse response = getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);
		
		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be ERROR in this case: " + response.getResultCode().getMessage(), ResultCodeEnum.ERROR, response.getResultCode().getCode());	
	}
	
	@Test(expected = SOAPFaultException.class)
	public void registerInvoiceData_with_empty_invoicedata_result_exception() {
		
		Event event = new Event();		
		getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, event);		
	}
		

	static RegisterInvoiceDataResponderInterface getRegisterInvoiceDataService() {
		RegisterInvoiceDataResponderInterface iRegisterInvoiceDataResponder = null;

		final String URL = "http://localhost:8080/invoice-data-app/ws/registerInvoiceData";
		// Endpoint.publish(URL, new RegisterInvoiceDataProducer());

		try {
			URL wsdlURL = new URL(URL + "?wsdl");

			String namespaceURI = "http://ws.app.invoicedata.sll.se/";
			String serviceName = "RegisterInvoiceDataProducerService";

			QName serviceQN = new QName(namespaceURI, serviceName);

			Service service = Service.create(wsdlURL, serviceQN);
			
			iRegisterInvoiceDataResponder = service
					.getPort(RegisterInvoiceDataResponderInterface.class);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iRegisterInvoiceDataResponder;
	}
}
