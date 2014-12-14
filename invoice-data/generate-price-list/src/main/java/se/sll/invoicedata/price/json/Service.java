/**
 * 
 */
package se.sll.invoicedata.price.json;

import java.util.ArrayList;
import java.util.List;

import se.sll.invoicedata.core.service.dto.Price;

/**
 * @author muqkha
 *
 */
public class Service {
	
	private String supplierId;
	private String supplierName;
	private String serviceCode;
	private String validFrom;
	private List<Price> prices = new ArrayList<Price>();
	
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}
	public List<Price> getPrices() {
		return prices;
	}
	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}
	
	
	
	

}
