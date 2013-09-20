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

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Assert;
import org.junit.Test;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.ItemList;
import riv.sll.invoicedata._1.ResultCodeEnum;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceData;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceDataResponse;

/**
 * @author muqkha
 * 
 */
public class RegisterInvoiceDataProducerTest {

	private final String LOGICAL_ADDRESS = "loc:TolkPortalen";
	
	@Test
	public void registerInvoiceData_normal_test_result_pass() {

		RegisterInvoiceDataResponse response = getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, createSampleInvoiceData());
		
		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be OK in this case: " + response.getResultCode().getMessage(), ResultCodeEnum.OK, response.getResultCode().getCode());	

	}
	
	@Test(expected = SOAPFaultException.class)
	public void registerInvoiceData_without_Items_result_fail() {
		
		RegisterInvoiceData invoiceData = createSampleInvoiceData();
		invoiceData.getEvent().getItems().setItem(null);
		
		getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);
	}
	
	@Test
	public void registerInvoiceData_with_incorrect_qty_fail() {
		
		RegisterInvoiceData invoiceData = createSampleInvoiceData();
		invoiceData.getEvent().getItems().getItem().get(0).setQty(new BigDecimal(-2));
						
		RegisterInvoiceDataResponse response = getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);
		
		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be ERROR in this case: " + response.getResultCode().getMessage(), ResultCodeEnum.ERROR, response.getResultCode().getCode());	
	}
	
	@Test
	public void registerInvoiceData_with_incorrect_start_end_time_fail() {
		
		RegisterInvoiceData invoiceData = createSampleInvoiceData();
		Calendar startTime = getCurrentDate().toGregorianCalendar();
		startTime.add(Calendar.DAY_OF_MONTH, +1);
		
		invoiceData.getEvent().setStartTimestamp(getXMLGCalendar(startTime));
		invoiceData.getEvent().setEndTimestamp(getCurrentDate());
						
		RegisterInvoiceDataResponse response = getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);
		
		Assert.assertNotNull("Should not be null: OK|ERROR", response);
		Assert.assertEquals("Result code should be ERROR in this case: " + response.getResultCode().getMessage(), ResultCodeEnum.ERROR, response.getResultCode().getCode());	
	}
	
	@Test(expected = SOAPFaultException.class)
	public void registerInvoiceData_with_empty_invoicedata_result_exception() {
		
		RegisterInvoiceData invoiceData = new RegisterInvoiceData();
		Event event = new Event();
		invoiceData.setEvent(event);
		
		getRegisterInvoiceDataService()
				.registerInvoiceData(LOGICAL_ADDRESS, invoiceData);		
	}
		
	private RegisterInvoiceData createSampleInvoiceData() {
		RegisterInvoiceData invoiceData = new RegisterInvoiceData();
		Event event = new Event();
		event.setEventId("EID1234");
		event.setSignedBy("sign:X");
		event.setSupplierName("SNX");

		event.setSignedTimestamp(getCurrentDate());
		event.setServiceCode("SCABCD");
	    event.setPaymentResponsible("HSF");
		event.setSupplierId("SID123");
		event.setStartTimestamp(getCurrentDate());
		event.setEndTimestamp(getCurrentDate());

		Item item = new Item();
		item.setDescription("Item is kind of a product");
		item.setItemId("IT101");
		item.setQty(new BigDecimal(0.5));
		List<Item> items = new LinkedList<Item>();
		items.add(item);

		ItemList itemList = new ItemList();
		itemList.setItem(items);

		event.setItems(itemList);
		invoiceData.setEvent(event);
		
		return invoiceData;
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

	private XMLGregorianCalendar getCurrentDate() {
		XMLGregorianCalendar calendar = null;
		try {
			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					new GregorianCalendar());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return calendar;
	}
	
	private XMLGregorianCalendar getXMLGCalendar(Calendar date) {
		XMLGregorianCalendar calendar = null;
		try {
			calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					(GregorianCalendar) date);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return calendar;
	}

}
