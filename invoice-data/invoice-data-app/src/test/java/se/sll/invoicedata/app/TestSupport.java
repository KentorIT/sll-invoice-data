/**
 * Copyright (c) 2013 SLL, <http://sll.se>
 *
 * This file is part of Invoice-Data.
 *
 *     Invoice Data is free software: you can redistribute it and/or modify
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

package se.sll.invoicedata.app;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
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
	private static final String[] PAYMENT_RESPONSIBLE = {"HSF", "FSH", "SHF"};
	
	private enum SUPPLIER {
		SUPPLIER("Tolk.001");
		
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
		event.setSupplierId(SUPPLIER.SUPPLIER.getId());
		event.setSupplierName(SUPPLIER.SUPPLIER.getName());
		
		event.setAcknowledgementId(UUID.randomUUID().toString());
		event.setAcknowledgedBy("sign:X");
		event.setAcknowledgedTime(getCurrentDate());
		event.setServiceCode("Språktolk");
		event.setPaymentResponsible(getPayee());
		event.setHealthCareCommission("BVC");
		
		event.setStartTime(getRandomStartTime());
		event.setEndTime(getRandomEndTime(event.getStartTime()));

		Item item = new Item();
		item.setDescription("Test item(product)");
		item.setItemId(ITEM.ITEM_1.getId());
		item.setQty(ITEM.ITEM_1.getQty());
		
		// set price explicitly, because the price list doesn't exits for random supplier-id.
		item.setPrice(randomPrice());

		event.getItemList().add(item);
		
	
		return event;
	}
	
	private void print(Event event) {
		System.out.println("AcknowledgementId: " + event.getAcknowledgementId() + ", Event id: " + 
				event.getEventId() + ", supplierId: " + event.getSupplierId() + ", paymentResponsible: " + 
				event.getPaymentResponsible() + ", startTime: " + CoreUtil.toDate(event.getStartTime()) + 
				", endTime: " + CoreUtil.toDate(event.getEndTime()));		
	}
	
	private static XMLGregorianCalendar getRandomStartTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + getRandomInt(0, 7));
		cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + getRandomInt(0, 3));		
		return toXMLGregorianCalendar(cal.getTime());		
	}
	
	private static XMLGregorianCalendar getRandomEndTime(XMLGregorianCalendar startTime) {
		Calendar cal = startTime.toGregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + getRandomInt(0, 5));
		return toXMLGregorianCalendar(cal.getTime());
	}
	
	/**
	 * Creates test data to be used while testing
	 * 
	 * AcknowledgementId is the unique part of every event.
	 * There can be events with same eventId, supplierId,
	 * paymentResponsible
	 * 
	 * 
	 */
	protected static Event[] createTestData() {
		//Acknowledgement id shall always be different for the 
		//events being generated in reality and in test		
		Event event1 = createRandomEventData();
		Event event2 = CoreUtil.copyProperties(event1, Event.class);
		CoreUtil.copyGenericLists(event2.getItemList(), event1.getItemList(), Item.class);
		event2.setAcknowledgementId(UUID.randomUUID().toString());
		
		//When supplier differs eventually event_id differs
		Event event3 = createRandomEventData();
		//same payment responsible
		event3.setPaymentResponsible(event1.getPaymentResponsible());
		
		//independent event!
		Event event4 = createRandomEventData();
		
		return new Event[] { event1, event2, event3, event4 };				
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
	
	private static int getRandomInt(int min, int max) {
	    return new Random().nextInt((max - min) + 1) + min;
	}
	
	private static String getPayee() {
		return PAYMENT_RESPONSIBLE[getRandomInt(0,2)];
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
