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

import java.util.List;

import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.jmx.StatusBean;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepositoryWithLock;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;

/**
 * @author muqkha
 *
 */
@Service
@Transactional
public class CreateInvoiceDataService extends ValidationService {
	
	private static final Logger TX_LOG = LoggerFactory.getLogger("TX-API");
	
	@Autowired
    private StatusBean statusBean;
	
	@Autowired
    private InvoiceDataRepositoryWithLock invoiceDataRepositoryWithLock;
	
    public String createInvoiceData(final CreateInvoiceDataRequest createInvoiceDataRequest) {
    	List<InvoiceDataEntity> invoiceDataEntityList = invoiceDataRepositoryWithLock.findBySupplierIdAndPaymentResponsibleAndCostCenterAndPendingIsTrue(
    			createInvoiceDataRequest.getSupplierId(), createInvoiceDataRequest.getPaymentResponsible(), createInvoiceDataRequest.getCostCenter());
    	
    	if (invoiceDataEntityList.size() > 1) {
			throw InvoiceDataErrorCodeEnum.ILLEGAL_STATE_DUPLICATE_DRAFT_VERSIONS_OF_INVOICES.createException("Contact system administrator");
		} else if (invoiceDataEntityList.isEmpty()) {
			throw InvoiceDataErrorCodeEnum.NO_PENDING_INVOICES_TO_BE_GENERATED.createException(
					createInvoiceDataRequest.getSupplierId(), createInvoiceDataRequest.getPaymentResponsible(), createInvoiceDataRequest.getCostCenter());
		}
    	
    	InvoiceDataEntity invoiceDataEntity = invoiceDataEntityList.get(0);
		invoiceDataEntity.setCreatedBy(createInvoiceDataRequest.getCreatedBy());
		invoiceDataEntity.setPending(null);
		
		validateGeneratedInvoice(invoiceDataEntity);		
        final InvoiceDataEntity saved = save(invoiceDataEntity);
        TX_LOG.info("Invoice created by: " + saved.getCreatedBy() + ", createdTime: " + saved.getCreatedTime() + ", referenceId: " + saved.getReferenceId());
        return saved.getReferenceId();
    }
    
    private InvoiceDataEntity save(InvoiceDataEntity invoiceDataEntity) {
        statusBean.start("InvoiceDataService.save()");
        try {
            final InvoiceDataEntity saved = invoiceDataRepositoryWithLock.saveAndFlush(invoiceDataEntity);
            return saved;
        } catch(HibernateOptimisticLockingFailureException | StaleObjectStateException e) {
        	throw InvoiceDataErrorCodeEnum.TECHNICAL_ERROR.createException("Failed to create invoicedata due to concurrent modification.", "Try re-sending request");
        } catch(Exception e) {
        	throw InvoiceDataErrorCodeEnum.TECHNICAL_ERROR.createException("Failed to create invoicedata due to technical error.", "Contact administrator");
        } finally {
            statusBean.stop();  
        }
    }
    
}
