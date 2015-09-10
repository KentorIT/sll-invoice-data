/**
 * 
 */
package se.sll.invoicedata.core.pojo.mapping;

import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;

/**
 * @author muqkha
 *
 */
public class LocalMapper {
	
	public static InvoiceDataEntity copyToInvoiceDataEntity(CreateInvoiceDataRequest inData) {
		InvoiceDataEntity invoiceDataEntity = new InvoiceDataEntity();
		invoiceDataEntity.setCreatedBy(inData.getCreatedBy());
		invoiceDataEntity.setPaymentResponsible(inData.getPaymentResponsible());
		invoiceDataEntity.setSupplierId(inData.getSupplierId());
		
		return invoiceDataEntity;
		
	}
	

}
