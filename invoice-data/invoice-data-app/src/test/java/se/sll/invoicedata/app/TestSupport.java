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

package se.sll.invoicedata.app;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import se.sll.invoicedata.core.service.impl.CoreUtil;

/**
 * Testing base class.
 * 
 * @author Peter
 */
public abstract class TestSupport extends CoreUtil {

	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	public static final String NAMESPACE_URI = "http://ws.app.invoicedata.sll.se/";

	private enum SUPPLIER {
		SUPPLIER_X("SUPPLIER_X"),
		SUPPLIER_Y("SUPPLIER_Y");
		
		private String supplierName;
		
		SUPPLIER(String supplierName) {
			this.supplierName = supplierName;
		}
		
		public String getId() {
			return genRandomAlphaNData(5);
		}
		
		public String getName() {
			return supplierName;
		}
	}
	
	private enum ITEM {
		ITEM_1("ITEM_1", 2),
		ITEM_2("ITEM_2", 1.5f);
		
		private String itemId;
		private BigDecimal itemQty;
		
		ITEM(String itemId, float itemQty) {
			this.itemId = itemId;
			this.itemQty = new BigDecimal(itemQty);
		}
		
		public String getId() {
			return itemId;
		}
		
		public BigDecimal getQty() {
			return itemQty;
		}
		
	}
	
	public static String getWSDLURL(String serviceName) {
		return "http://localhost:8080/invoice-data-app/ws/" + serviceName + "?wsdl";
	}
	
	public static Event createRandomEventData() {
		Event event = new Event();
		event.setEventId(genRandomAlphaNData(5));
		event.setSupplierId(SUPPLIER.SUPPLIER_X.getId());
		event.setSupplierName(SUPPLIER.SUPPLIER_X.getName());

		event.setAcknowledgedBy("sign:X");
		event.setAcknowledgedTime(getCurrentDate());
		event.setServiceCode("SCABCD");
		event.setPaymentResponsible("HSF");
		event.setHealthCareCommission("BVC");
		
		event.setStartTime(getCurrentDate());
		event.setEndTime(getCurrentDate());

		Item item = new Item();
		item.setDescription("Test item(product)");
		item.setItemId(ITEM.ITEM_1.getId());
		item.setQty(ITEM.ITEM_1.getQty());

		event.getItemList().add(item);

		return event;
	}

	public static XMLGregorianCalendar getCurrentDate() {
		return toXMLGregorianCalendar(new Date());
	}

	public static String genRandomAlphaNData(int count) {
		final StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
}
