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

package se.sll.invoicedata.core.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.InvoiceData;
import riv.sll.invoicedata._1.InvoiceDataHeader;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import se.sll.invoicedata.core.jmx.StatusBean;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.pojo.mapping.EntityBeanConverter;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.util.CoreUtil;
import se.sll.invoicedata.core.util.LockService;

import static se.sll.invoicedata.core.pojo.mapping.LocalMapper.getInvoiceDataHeader;
/**
 * Implements invoice data service.
 * 
 * @author Peter
 *
 */
@Service
@Transactional
public class InvoiceDataServiceImpl implements InvoiceDataService {

    private static final Logger TX_LOG = LoggerFactory.getLogger("TX-API");

    @Autowired
    private LockService lock;
    
    @Autowired
    private StatusBean statusBean;
    
    @Autowired
    private RegisterEventService registerEventService;    
    @Autowired
    private InvoiceDataRepository invoiceDataRepository;
    
    @Autowired
    private ListEventsService listEventsService;    
    @Autowired
    private ListInvoiceDataService listInvoiceDataService;    
    @Autowired
    private CreateInvoiceDataService createInvoiceDataService;
    
    @Override
    public void registerEvent(Event event) {
        final String name = event.getEventId();
        
        if (!lock.acquire(name)) {
            throw InvoiceDataErrorCodeEnum.TECHNICAL_ERROR.createException("Event \"" + name + "\" currently is updated by another user");
        }
        try {
        	registerEventService.registerEventAsBusinessEntity(event);
        } finally {
            lock.release(name);
        }
    }
    
    /**
     * Service name - GetInvoiceData
     * Consumers uses this service to dry run before creating actual invoice
     */
    @Override
    public List<RegisteredEvent> getAllPendingBusinessEvents(
            GetInvoiceDataRequest request) {
    	return listEventsService.getAllPendingBusinessEvents(request);
    }
    
    @Override
    public String createInvoiceData(final CreateInvoiceDataRequest createInvoiceDataRequest) {

		TX_LOG.info("Request for CreateInvoice triggeredBy:" + createInvoiceDataRequest.getCreatedBy() + " for supplier(id:" + createInvoiceDataRequest.getSupplierId() + ")");
		createInvoiceDataService.validate(createInvoiceDataRequest);
		
		String lockId = createInvoiceDataRequest.getPaymentResponsible()+createInvoiceDataRequest.getSupplierId()+createInvoiceDataRequest.getCostCenter();
		
		if (!lock.acquire(lockId)) {
            throw InvoiceDataErrorCodeEnum.TECHNICAL_ERROR.createException("Invoicedata for this particular supplierId,paymentResponsible and costcenter is currently being updated by another user");
        }
        statusBean.start("InvoiceDataService.createInvoiceData()");
        try {            
        	return createInvoiceDataService.createInvoiceData(createInvoiceDataRequest);
        } finally {
            lock.release(lockId);
            statusBean.stop();
        }
    }
	
    @Override
    public List<InvoiceDataHeader> listAllInvoiceData(ListInvoiceDataRequest request) {
        if (CoreUtil.isEmpty(request.getSupplierId()) && CoreUtil.isEmpty(request.getPaymentResponsible())) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("supplierId or paymentResponsible");
        }
        statusBean.start("InvoiceDataService.listAllInvoiceData()");
        try {
            return getInvoiceDataHeader(listInvoiceDataService.findByCriteria(request));
        } finally {
            statusBean.stop();
        }
    }
    
    /**
     * Service name - ViewInvoiceData
     */
    @Override
    public InvoiceData getInvoiceDataByReferenceId(final String referenceId) {
    	Long id = listInvoiceDataService.extractId(referenceId);
    	statusBean.start("InvoiceDataService.getInvoiceDataByReferenceId()");
        try {
        	return getInvoiceData(referenceId, invoiceDataRepository.findOne(id));
        } finally {
            statusBean.stop();
        }
    }
    
    private InvoiceData getInvoiceData(final String referenceId, final InvoiceDataEntity invoiceDataEntity) {
		if (invoiceDataEntity == null) {
            throw InvoiceDataErrorCodeEnum.NOTFOUND_ERROR.createException("invoice data", referenceId); 		    
        } else if (invoiceDataEntity.isPending() || 
        		invoiceDataEntity.getTotalAmount() == null || 
        		invoiceDataEntity.getTotalAmount().intValue() == 0) {
        	throw InvoiceDataErrorCodeEnum.EXPECTING_CREATE_INVOICE_REQUEST_BEFORE_FETCHING.createException("Check indata");
        }

        final InvoiceData invoiceData = EntityBeanConverter.fromInvoiceDataEntityToInvoiceData(invoiceDataEntity);
        final List<BusinessEventEntity> bEEList = invoiceDataEntity.getBusinessEventEntities();
        for (final BusinessEventEntity businessEventEntity : bEEList) {
            invoiceData.getRegisteredEventList().add(EntityBeanConverter.fromBusinessEventEntityToRegisteredEvent(businessEventEntity));
        }
        
		return invoiceData;
	}
    
    public List<InvoiceDataHeader> getAllPendingInvoiceData() {
    	statusBean.start("InvoiceDataService.getAllPendingInvoiceData()");
        try {
            return getInvoiceDataHeader(listInvoiceDataService.getAllPendingInvoiceData());
        } finally {
            statusBean.stop();
        }
    }
    
    @Override
    public int getEventMaxFindResultSize() {
        return listEventsService.getEventMaxFindResultSize();
    }
    
}
