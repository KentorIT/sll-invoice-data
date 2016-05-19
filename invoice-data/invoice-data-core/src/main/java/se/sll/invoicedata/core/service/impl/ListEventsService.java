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
import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.pojo.mapping.EntityBeanConverter;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.util.CoreUtil;

/**
 * @author muqkha
 *
 */
@Service
@Transactional
public class ListEventsService extends ValidationService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ListEventsService.class);
	
	@Value("${event.maxFindResultSize:30000}")
    private int eventMaxFindResultSize;
	
	@Autowired
    private BusinessEventRepository businessEventRepository;
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public List<RegisteredEvent> getAllPendingBusinessEvents(GetInvoiceDataRequest request) {
        mandatory(request.getSupplierId(), "supplierId");
        LOG.debug(request.getSupplierId() + " from: " + request.getFromDate() + " to: " + request.getToDate());

        List<BusinessEventEntity> businessEventLists = fetchAndFilterBusinessEvents(request);
      
        if (businessEventLists.size() >= eventMaxFindResultSize) {
            throw InvoiceDataErrorCodeEnum.LIMIT_ERROR.createException(eventMaxFindResultSize, "please narrow down search criterias");
        }
        
        //No requirement to fetch list sorted by date
        return EntityBeanConverter.processBusinessEventEntitiesToRegisteredEvent(businessEventLists);
    }
	
	private List<BusinessEventEntity> fetchAndFilterBusinessEvents(final GetInvoiceDataRequest request) {
		final Date dateFrom = CoreUtil.floorDate(CoreUtil.toDate(request.getFromDate(), CoreUtil.MIN_DATE));
        final Date dateTo = CoreUtil.ceilDate(CoreUtil.toDate(request.getToDate(), CoreUtil.MAX_DATE));
        
        final PageRequest pageRequest = new PageRequest(0, eventMaxFindResultSize+1);
        List<BusinessEventEntity> businessEventEntityList = businessEventRepository.
        		findBySupplierIdAndPendingIsTrueAndStartTimeBetween(request.getSupplierId(), dateFrom, dateTo, pageRequest);
        Iterator<BusinessEventEntity> iterator = businessEventEntityList.iterator();
        
        while (iterator.hasNext()) {
        	BusinessEventEntity entity = iterator.next();
			if (CoreUtil.isNotEmpty(request.getPaymentResponsible()) && 
					CoreUtil.notEqualsToIgnoreCase(entity.getPaymentResponsible(), request.getPaymentResponsible())) {
				iterator.remove();
			} else if (CoreUtil.isNotEmpty(request.getCostCenter()) && 
					CoreUtil.notEqualsToIgnoreCase(entity.getCostCenter(), request.getCostCenter())) {
				iterator.remove();
			}
		}
        return businessEventEntityList;
	}
	
	public int getEventMaxFindResultSize() {
        return eventMaxFindResultSize;
    }
}
