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

import static se.sll.invoicedata.core.pojo.mapping.LocalMapper.copyToInvoiceDataEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata.createinvoicedataresponder._1.CreateInvoiceDataRequest;
import se.sll.invoicedata.core.jmx.StatusBean;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
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
    private InvoiceDataRepository invoiceDataRepository;
	
	@Autowired
    private BusinessEventRepository businessEventRepository;
	
	@Value("${createInvoice.batch.items:2000}")
    private int maxListSize;
	
	private ThreadGroup threadGroup = new ThreadGroup("CREATE_INVOICE_DATA");
	
	public boolean isBatchRequest(final CreateInvoiceDataRequest createInvoiceDataRequest) {
		boolean isBatch = (createInvoiceDataRequest.getAcknowledgementIdList().size() > maxListSize);
		TX_LOG.debug("@createInvoiceData: Is it a batch request?" + isBatch);
		return isBatch;
	}
	
	String createInvoiceWithAmount(final CreateInvoiceDataRequest createInvoiceDataRequest) {
		final InvoiceDataEntity invoiceDataEntity = copyToInvoiceDataEntity(createInvoiceDataRequest);
        final List<BusinessEventEntity> entities = findByAcknowledgementIdInAndPendingIsTrue(createInvoiceDataRequest.getAcknowledgementIdList());

        getValidInvoiceDataEntity(createInvoiceDataRequest, invoiceDataEntity, entities);

        final InvoiceDataEntity saved  = save(invoiceDataEntity);

        return saved.getReferenceId();
	}
	
	
	synchronized String createInvoiceDataInBatch(final CreateInvoiceDataRequest createInvoiceDataRequest) {
		 TX_LOG.debug("@CreateInvoiceData: Active threads: " + threadGroup.activeCount());
		 if (threadGroup.activeCount() > 0) {
			 throw InvoiceDataErrorCodeEnum.SYSTEM_BUSY_WITH_CREATE_INVOICE_REQUEST.createException("Requested to try again later");
		 }
		 
		 final InvoiceDataEntity saved  = createInvoiceWithoutAmount(createInvoiceDataRequest);           
         
         Thread thread = new Thread(threadGroup, UUID.randomUUID().toString()) {
             public void run() {
             	createFinalInvoice(createInvoiceDataRequest, saved.getId());
             }
         };
         
         thread.start();            
         return saved.getReferenceId();		
	}
	
	public InvoiceDataEntity createInvoiceWithoutAmount(CreateInvoiceDataRequest createInvoiceDataRequest) {
    	final InvoiceDataEntity invoiceDataEntity = copyToInvoiceDataEntity(createInvoiceDataRequest);
        Long actual = countBySupplierIdAndPaymentResponsibleAndAckIdAndPendingIsTrue(createInvoiceDataRequest);
        int expected = createInvoiceDataRequest.getAcknowledgementIdList().size();
        
        if (actual != expected) {
        	String msg = "given event list doesn't match database state! entities available: " + actual + ", request contains: " + expected + 
        			". Check supplierId and make sure acknowledgementId contains items which were not processed earlier";
		    throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException(msg); 
		}            
        return save(invoiceDataEntity); 
    }
    
    public void createFinalInvoice(CreateInvoiceDataRequest createInvoiceDataRequest, Long id) {
    	final List<BusinessEventEntity> entities = findByAcknowledgementIdInAndPendingIsTrue(createInvoiceDataRequest.getAcknowledgementIdList());            		
		InvoiceDataEntity invoiceDataEntity = invoiceDataRepository.findOne(id);            
        getValidInvoiceDataEntity(createInvoiceDataRequest, invoiceDataEntity, entities);                    
        update(invoiceDataEntity);                    
        TX_LOG.info("@processCreateInvoiceData: Successfully thread cycle completed : Invoice generated for supplier " + invoiceDataEntity.getSupplierId());
    }
    
    private Long countBySupplierIdAndPaymentResponsibleAndAckIdAndPendingIsTrue(CreateInvoiceDataRequest createInvoiceDataRequest) {
        statusBean.start("InvoiceDataService.countByAcknowledgementIdInAndPendingIsTrue()");
        try {
        	Long count = 0L;
        	int fromIndex = 0;
        	int toIndex = 0;
        	List<String> ackIdList = createInvoiceDataRequest.getAcknowledgementIdList();
        	
        	while (fromIndex != ackIdList.size()) {
        		toIndex += getNextBatch(ackIdList.size(), fromIndex, toIndex);
        		count += businessEventRepository.countBySupplierIdAndPaymentResponsibleAndAckIdAndPendingIsTrue(createInvoiceDataRequest.getSupplierId(), createInvoiceDataRequest.getPaymentResponsible(), ackIdList.subList(fromIndex, toIndex));
        		fromIndex = toIndex;
        	}        	
        	return count;
        } finally {
            statusBean.stop();
        }
    }
    
    private InvoiceDataEntity save(InvoiceDataEntity invoiceDataEntity) {
        statusBean.start("InvoiceDataService.save()");
        try {
            final InvoiceDataEntity saved = invoiceDataRepository.save(invoiceDataEntity);
            invoiceDataRepository.flush();
            return saved;
        } finally {
            statusBean.stop();  
        }
    }
    
    private InvoiceDataEntity update(InvoiceDataEntity invoiceDataEntity) {
        statusBean.start("InvoiceDataService.update()");
        try {
        	TX_LOG.info("BusinessEventEntity items: " + invoiceDataEntity.getBusinessEventEntities().size());
        	invoiceDataEntity.calcDerivedValues();
            final InvoiceDataEntity saved = invoiceDataRepository.save(invoiceDataEntity);
            invoiceDataRepository.flush();
            return saved;
        } finally {
            statusBean.stop();  
        }
    }
    
    private List<BusinessEventEntity> findByAcknowledgementIdInAndPendingIsTrue(final List<String> list) {
        statusBean.start("InvoiceDataService.findByAcknowledgementIdInAndPendingIsTrue()");
        try {
        	List<BusinessEventEntity> bEventEntityList = new ArrayList<BusinessEventEntity>();
        	int fromIndex = 0;
        	int toIndex = 0;
        	while (fromIndex != list.size()) {
        		toIndex += getNextBatch(list.size(), fromIndex, toIndex);
        		bEventEntityList.addAll(businessEventRepository.findByAcknowledgementIdInAndPendingIsTrue(list.subList(fromIndex, toIndex)));
        		fromIndex = toIndex;
        	}
            return bEventEntityList;
        } finally {
            statusBean.stop();
        }
    }
    
    private int getNextBatch(int itemsInList, int fromIndex, int toIndex) {
    	return (maxListSize < (itemsInList - fromIndex)) ? (maxListSize) : (itemsInList - toIndex);
    }
    
    InvoiceDataEntity getValidInvoiceDataEntity(
			CreateInvoiceDataRequest createInvoiceDataRequest,
			final InvoiceDataEntity invoiceDataEntity,
			final List<BusinessEventEntity> entities) {
		int actual = 0;
		for (final BusinessEventEntity entity : entities) {        	
		    invoiceDataEntity.addBusinessEventEntity(validate(entity, createInvoiceDataRequest));
		    actual++;
		}
		final int expected = createInvoiceDataRequest.getAcknowledgementIdList().size();
		if (expected != actual) {
		    throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("given event list doesn't match database state! entities available: " + actual + ", request contains: " + expected); 
		}
		
		return validate(invoiceDataEntity);
	}

}
