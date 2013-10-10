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
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

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
	public static final String LOGICAL_ADDRESS = "loc:TolkPortalen";

	private enum SUPPLIER {
		SUPPLIER_X("Tolk.001"),
		SUPPLIER_Y("Tolk.002");
		
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
		ITEM_1("item.1", 2),
		ITEM_2("item.2", 1.5f);
		
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
		event.setHealthcareFacility(genRandomAlphaNData(10));
		event.setRefContractId("CONTRACT_1");
		event.setSupplierId(SUPPLIER.SUPPLIER_X.getId());
		event.setSupplierName(SUPPLIER.SUPPLIER_X.getName());
		
		event.setAcknowledgementId(UUID.randomUUID().toString());
		event.setAcknowledgedBy("sign:X");
		event.setAcknowledgedTime(getCurrentDate());
		event.setServiceCode("Språktolk");
		event.setPaymentResponsible("HSF");
		event.setHealthCareCommission("BVC");
		
		event.setStartTime(getCurrentDate());
		event.setEndTime(getCurrentDate());

		Item item = new Item();
		item.setDescription("Test item(product)");
		item.setItemId(ITEM.ITEM_1.getId());
		item.setQty(ITEM.ITEM_1.getQty());
		
		// set price explicitly, because the price list doesn't exits for random supplier-id.
		item.setPrice(randomPrice());

		event.getItemList().add(item);

		return event;
	}
	
	static BigDecimal randomPrice() {
	    int add = (Math.random() > 0.5d) ? 1 : -1;
        double price = 700 + (add * (Math.random() * 200.0));
        return BigDecimal.valueOf(price);
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
	
	  /**
     * Makes first character lower-case.
     * 
     * @param s input string.
     * @return s starting with lower case.
     */
    private static String initLow(String s) {
        if (s == null || s.length() == 0 || Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        char[] arr = s.toCharArray();
        arr[0] = Character.toLowerCase(arr[0]);
        return new String(arr);
    }
    
    /**
     * Returns RIV-TA service basename.
     * 
     * @param type the actual RIV-TA Web Service interface class.
     * @return the basename
     */
    private static String basename(Class<?> type) {
        final String name = type.getName().substring(type.getName().lastIndexOf('.')+1);
        return name.substring(0, name.lastIndexOf("ResponderInterface"));
    }

    

    /**
     * Use RIV-TA naming convention standard to create a service client.
     * 
     * @param type the Web Service interface class.
     * @return the actual service implementing the interface.
     */
    public static <T> T createWebServiceConsumer(Class<T> type) {
        final String basename = basename(type);
        try {
            URL wsdlURL = new URL(getWSDLURL(initLow(basename)));
            QName serviceQN = new QName(NAMESPACE_URI, basename + "ProducerService");
            Service service = Service.create(wsdlURL, serviceQN);
            return service.getPort(type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
}
