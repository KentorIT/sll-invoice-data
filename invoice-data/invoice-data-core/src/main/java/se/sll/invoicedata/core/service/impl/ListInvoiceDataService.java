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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import riv.sll.invoicedata.listinvoicedataresponder._1.ListInvoiceDataRequest;
import se.sll.invoicedata.core.jmx.StatusBean;
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.pojo.mapping.EntityBeanConverter;
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
    private BusinessEventRepository businessEventRepository;
	
	@Autowired
    private InvoiceDataRepository invoiceDataRepository;

	@Value("${event.maxFindResultSize:30000}")
    private int eventMaxFindResultSize;
	/*
	public List<RegisteredEvent> getAllUnprocessedBusinessEvents(
            GetInvoiceDataRequest request) {

        mandatory(request.getSupplierId(), "supplierId");

        List<BusinessEventEntity> bEEntityList;
        
        log.debug(request.getSupplierId() + " from: " + request.getFromDate() + " to: " + request.getToDate());

        final Date dateFrom = CoreUtil.floorDate(CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE));
        final Date dateTo = CoreUtil.ceilDate(CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE));

       // max size
        final PageRequest pageRequest = new PageRequest(0, eventMaxFindResultSize+1);

        if (CoreUtil.isEmpty(request.getPaymentResponsible())) {
            bEEntityList = businessEventRepository.findBySupplierIdAndPendingIsTrueAndStartTimeBetween(
                    request.getSupplierId(), dateFrom, dateTo, pageRequest);
        } else {
            bEEntityList = businessEventRepository.
                    findBySupplierIdAndPendingIsTrueAndPaymentResponsibleAndStartTimeBetween(
                            request.getSupplierId(), request.getPaymentResponsible(),
                            dateFrom, dateTo, pageRequest);
        }
        
        if (bEEntityList.size() >= eventMaxFindResultSize) {
            throw InvoiceDataErrorCodeEnum.LIMIT_ERROR.createException(eventMaxFindResultSize, "please narrow down search criterias");
        }
        
        //No requirement to fetch list sorted by date
        return EntityBeanConverter.processBusinessEventEntitiesToRegisteredEvent(bEEntityList);
    }*/
	
	public List<RegisteredEvent> getAllUnprocessedBusinessEvents(GetInvoiceDataRequest request) {

        mandatory(request.getSupplierId(), "supplierId");

        LOG.debug(request.getSupplierId() + " from: " + request.getFromDate() + " to: " + request.getToDate());

        List<BusinessEventEntity> bEEntityList = fetchAndFilterBusinessEvents(request);
      
        if (bEEntityList.size() >= eventMaxFindResultSize) {
            throw InvoiceDataErrorCodeEnum.LIMIT_ERROR.createException(eventMaxFindResultSize, "please narrow down search criterias");
        }
        
        //No requirement to fetch list sorted by date
        return EntityBeanConverter.processBusinessEventEntitiesToRegisteredEvent(bEEntityList);
    }
	
	private List<BusinessEventEntity> fetchAndFilterBusinessEvents(final GetInvoiceDataRequest request) {
		// max size
        final PageRequest pageRequest = new PageRequest(0, eventMaxFindResultSize+1);
        
        final Date dateFrom = CoreUtil.floorDate(CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE));
        final Date dateTo = CoreUtil.ceilDate(CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE));
        
		List<InvoiceDataEntity> invoiceEntityList = invoiceDataRepository.findBySupplierIdAndStartDateBetween(
        		request.getSupplierId(), dateFrom, dateTo, pageRequest);
		
		Iterator<InvoiceDataEntity> iterator = invoiceEntityList.iterator();
		List<BusinessEventEntity> bEEntityList = new ArrayList<BusinessEventEntity>();
		
		while (iterator.hasNext()) {
			InvoiceDataEntity entity = iterator.next();
			if (CoreUtil.isNotEmpty(request.getPaymentResponsible()) && 
					CoreUtil.notEqualsToIgnoreCase(entity.getPaymentResponsible(), request.getPaymentResponsible())) {
				iterator.remove();
			} else if (CoreUtil.isNotEmpty(request.getCostCenter()) && 
					CoreUtil.notEqualsToIgnoreCase(entity.getCostCenter(), request.getCostCenter())) {
				iterator.remove();
			}
		}
			
		for (InvoiceDataEntity entity : invoiceEntityList) {			
			bEEntityList.addAll(entity.getBusinessEventEntities());
		}
		
		return bEEntityList;
	}
	
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
        	invoiceDataEntityList = invoiceDataRepository.findAll();
        	invoiceDataEntityList = invoiceDataRepository.findBySupplierIdAndPendingIsFalseAndStartDateBetween(
        			request.getSupplierId(), dateFrom, dateTo);
        } else if (CoreUtil.isNotEmpty(request.getPaymentResponsible())) {
        	invoiceDataEntityList = invoiceDataRepository.findByPaymentResponsibleAndPendingIsFalseAndStartDateBetween(
        			request.getPaymentResponsible(), dateFrom, dateTo);
        }
        return invoiceDataEntityList;
	}
	
	protected List<InvoiceDataEntity> getAllPendingInvoiceData() {
        return invoiceDataRepository.findByPendingIsTrue();
	}
	/*
    public List<InvoiceDataEntity> findByCriteria(ListInvoiceDataRequest request) {

        statusBean.start("InvoiceDataService.findByCriteria()");
        try {
            final Date dateFrom = CoreUtil.floorDate(CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE));
            final Date dateTo = CoreUtil.ceilDate(CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE));
            
            List<InvoiceDataEntity> invoiceDataEntityList = new ArrayList<InvoiceDataEntity>();

            if (request.getSupplierId() != null && request.getPaymentResponsible() != null) {            	
                invoiceDataEntityList = invoiceDataRepository.getInvoiceDataBySupplierIdAndPaymentResponsibleBetweenDates(
                        request.getSupplierId(),
                        request.getPaymentResponsible(),
                        dateFrom, dateTo);
            } else  if (request.getSupplierId() != null) {            	
                invoiceDataEntityList = invoiceDataRepository.getInvoiceDataBySupplierIdBetweenDates(
                        request.getSupplierId(),
                        dateFrom, dateTo);
            } else if (request.getPaymentResponsible() != null) {
                invoiceDataEntityList = invoiceDataRepository.getInvoiceDataByPaymentResponsibleBetweenDates(
                        request.getPaymentResponsible(),
                        dateFrom, dateTo);
            }
            return invoiceDataEntityList;
        } finally {
            statusBean.stop();
        }
    }*/
    
    Long extractId(final String referenceId) {
		Long id = Long.MIN_VALUE;
        try {
            id = Long.valueOf(validate(referenceId, "referenceId"));
        } catch (NumberFormatException nfException) {
            throw InvoiceDataErrorCodeEnum.VALIDATION_ERROR.createException("referenceId has invalid format:" + referenceId); 
        }
        
		return id;
	}
    
    public int getEventMaxFindResultSize() {
        return eventMaxFindResultSize;
    }
}
