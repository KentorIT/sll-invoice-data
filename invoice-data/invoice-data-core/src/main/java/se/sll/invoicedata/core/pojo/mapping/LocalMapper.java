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
