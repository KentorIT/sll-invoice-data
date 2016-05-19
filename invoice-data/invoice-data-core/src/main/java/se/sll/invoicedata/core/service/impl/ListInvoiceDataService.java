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
package se.sll.invoicedata.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import se.sll.invoicedata.core.jmx.StatusBean;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * @author muqkha
 *
 */
@Service
@Transactional
public class ListInvoiceDataService extends ValidationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ListInvoiceDataService.class);
	
	@Autowired
    private StatusBean statusBean;
	
	@Autowired
    private InvoiceDataRepository invoiceDataRepository;
	
	/**
     * Finds by criteria: supplierId, paymentResponsible or date range
     * Date: fromDate - if null then 1970 01 01
     * toDate: - if null then current year + 100 years
     * @param request
     * @return List<InvoiceDataEntity>
     */
	public List<InvoiceDataEntity> findByCriteria(ListInvoiceDataRequest request) {

        statusBean.start("InvoiceDataService.findByCriteria()");
        try {
        	List<InvoiceDataEntity> invoiceDataEntityList = getBySupplierIdOrPaymentResponsible(request);
        	LOG.info("@findByCriteria: Total items fetched before filtering: " + invoiceDataEntityList.size());

        	Iterator<InvoiceDataEntity> iterator = invoiceDataEntityList.iterator();
        	while (iterator.hasNext()) {
        		InvoiceDataEntity entity = iterator.next();
        		if (CoreUtil.isNotEmpty(request.getSupplierId()) && 
        				CoreUtil.notEqualsToIgnoreCase(entity.getSupplierId(), request.getSupplierId())) {
        			iterator.remove();
        		} else if (CoreUtil.isNotEmpty(request.getPaymentResponsible()) && 
        				CoreUtil.notEqualsToIgnoreCase(entity.getPaymentResponsible(), request.getPaymentResponsible())) {
        			iterator.remove();
        		} else if (CoreUtil.isNotEmpty(request.getCostCenter()) && 
        				CoreUtil.notEqualsToIgnoreCase(entity.getCostCenter(), request.getCostCenter())) {
        			iterator.remove();
        		}
        	}
        	LOG.info("@findByCriteria: After filtering items remaining: " + invoiceDataEntityList.size());
        	return invoiceDataEntityList;
        } finally {
            statusBean.stop();
        }
    }
	
	private List<InvoiceDataEntity> getBySupplierIdOrPaymentResponsible(ListInvoiceDataRequest request) {
		final Date dateFrom = CoreUtil.floorDate(CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE));
        final Date dateTo = CoreUtil.ceilDate(CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE));
        
        List<InvoiceDataEntity> invoiceDataEntityList = new ArrayList<InvoiceDataEntity>();
        
        if (CoreUtil.isNotEmpty(request.getSupplierId())) {
        	invoiceDataEntityList = invoiceDataRepository.findBySupplierIdAndPendingIsFalseAndStartDateBetween(
        			request.getSupplierId(), dateFrom, dateTo);
        	LOG.info("@getBySupplierIdOrPaymentResponsible: Items fetched by SupplierId: " + invoiceDataEntityList.size());
        } else if (CoreUtil.isNotEmpty(request.getPaymentResponsible())) {
        	invoiceDataEntityList = invoiceDataRepository.findByPaymentResponsibleAndPendingIsFalseAndStartDateBetween(
        			request.getPaymentResponsible(), dateFrom, dateTo);
        	LOG.info("@getBySupplierIdOrPaymentResponsible: Items fetched by PaymentResponsible: " + invoiceDataEntityList.size());
        }
        return invoiceDataEntityList;
	}
	
	protected List<InvoiceDataEntity> getAllPendingInvoiceData() {
        return invoiceDataRepository.findByPendingIsTrue();
	}
	
    Long extractId(final String referenceId) {
		Long id = Long.MIN_VALUE;
        try {
            id = Long.valueOf(validate(referenceId, "referenceId"));
        } catch (NumberFormatException nfException) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("referenceId has invalid format:" + referenceId); 
        }
        
		return id;
	}
    
    
}
