/**
 * 
 */
package se.sll.invoicedata.price.json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author muqkha
 *
 */
public class PriceListJson {
	
	private List<Service> priceList = new ArrayList<Service>();

	public List<Service> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<Service> priceList) {
		this.priceList = priceList;
	}
	
	public void add(Service service) {
		priceList.add(service);
	}

}
