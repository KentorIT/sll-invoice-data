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
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.ItemList;
import riv.sll.invoicedata.registerinvoicedata._1.rivtabp21.RegisterInvoiceDataResponderInterface;
import riv.sll.invoicedata.registerinvoicedataresponder._1.RegisterInvoiceData;

/**
 * @author muqkha
 *
 */
public class RegisterInvoiceDataProducerTest {
	
	@Test
	public void registerInvoiceData() throws DatatypeConfigurationException, MalformedURLException {
	
		final String URL = "http://localhost:8080/invoice-data-app/ws/registerInvoiceData";
		//Endpoint.publish(URL, new RegisterInvoiceDataProducer());		
		
		URL wsdlURL = new URL(URL + "?wsdl");
	    String namespaceURI = "http://ws.app.invoicedata.sll.se/";
	    String serviceName = "RegisterInvoiceDataProducerService";

	    QName serviceQN = new QName(namespaceURI, serviceName);

		
		Service service = Service.create(wsdlURL, serviceQN);
		RegisterInvoiceDataResponderInterface iRegisterInvoiceDataResponder = service.getPort(RegisterInvoiceDataResponderInterface.class);
		
		RegisterInvoiceData invoiceData = new RegisterInvoiceData();
		Event event = new Event();
		event.setEventId("event-123");
        event.setSignedBy("Peter Larsson");
        event.setSupplierName("Dummy");
        
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().
        		newXMLGregorianCalendar(new GregorianCalendar());
        
        event.setSignedTimestamp(calendar);
        event.setServiceCode("XYZ");
        event.setSupplierId("12342");
        event.setStartTimestamp(calendar);
        event.setEndTimestamp(calendar);
        
        Item item = new Item();
        item.setDescription("Test item 1");
        item.setItemId("Item_019");
        item.setQty(new BigDecimal(2));
        List<Item> items = new LinkedList<Item>();
        items.add(item);
        
        ItemList itemList = new ItemList();
        itemList.setItem(items);
        
        event.setItems(itemList);
		invoiceData.setEvent(event );
		
		String logicalAddress = "loc:TolkPortalen";
		
		iRegisterInvoiceDataResponder.registerInvoiceData(logicalAddress, invoiceData);
		
	}

}
