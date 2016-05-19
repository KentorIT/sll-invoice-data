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

import java.util.ArrayList;
import java.util.List;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.util.CoreUtil;

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
	
	public static InvoiceDataEntity createDraftVersionOfInvoiceData(Event inData) {
		InvoiceDataEntity invoiceDataEntity = new InvoiceDataEntity();
		invoiceDataEntity.setCreatedBy("System"); //Not yet ready to create final invoice
		invoiceDataEntity.setPaymentResponsible(inData.getPaymentResponsible());
		invoiceDataEntity.setSupplierId(inData.getSupplierId());
		invoiceDataEntity.setCostCenter(inData.getCostCenter());
		return invoiceDataEntity;
	}
	
	public static InvoiceDataEntity createDraftVersionOfInvoiceData(BusinessEventEntity inData) {
		Event event = new Event();
		event.setPaymentResponsible(inData.getPaymentResponsible());
		event.setSupplierId(inData.getSupplierId());
		event.setCostCenter(inData.getCostCenter());
		
		return createDraftVersionOfInvoiceData(event);
	}
	
	public static List<InvoiceDataHeader> getInvoiceDataHeader(List<InvoiceDataEntity> invoiceDataEntityList) {
		final List<InvoiceDataHeader> invoiceDataList = new ArrayList<InvoiceDataHeader>(invoiceDataEntityList.size());
		for (final InvoiceDataEntity iDataEntity : invoiceDataEntityList) {
		    invoiceDataList.add(CoreUtil.copyProperties(iDataEntity, InvoiceDataHeader.class));
		}
		return invoiceDataList;
	}
}
