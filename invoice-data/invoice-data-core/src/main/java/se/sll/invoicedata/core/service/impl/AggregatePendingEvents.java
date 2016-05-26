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

import static se.sll.invoicedata.core.pojo.mapping.LocalMapper.createDraftVersionOfInvoiceData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.service.dto.ServiceResponse;

/**
 * @author muqkha
 *
 */
@Service
@Transactional
public class AggregatePendingEvents {
	
	private static final Logger TX_LOG = LoggerFactory.getLogger("TX-API");
	private static final Logger LOG = LoggerFactory.getLogger(AggregatePendingEvents.class);
	
	@Autowired
    private BusinessEventRepository businessEventRepository;
	
	@Autowired
    private InvoiceDataRepository invoiceDataRepository;
	
	public List<ServiceResponse> aggregateExistingPendingEventsToInvoiceDataDraftVersion() {
		TX_LOG.info("connecting existing events to a draft version of invoice, starting with aggregating pending events");
		List<InvoiceDataEntity> pendingInvoiceList = new ArrayList<InvoiceDataEntity>(aggregateEventsToPendingInvoiceData().values());
		for (InvoiceDataEntity entity : pendingInvoiceList) {
			invoiceDataRepository.save(entity);
		}
		invoiceDataRepository.flush();
		TX_LOG.info("All existing invoicedata events are now connected to a draft version of invoicedata");
		return getServiceResponse(pendingInvoiceList);
	}
	
	private Map<String, InvoiceDataEntity> aggregateEventsToPendingInvoiceData() {
		List<BusinessEventEntity> existingPendingEvents = businessEventRepository.findByPendingIsTrueAndInvoiceDataIsNull();
		LOG.info("Found total pending items to be " + existingPendingEvents.size());
		Map<String, InvoiceDataEntity> eventsMap = new HashMap<String, InvoiceDataEntity>();

		for (BusinessEventEntity businessEventEntity : existingPendingEvents) {
			String key = generateKey(businessEventEntity);
			if (!eventsMap.containsKey(key)) {
				InvoiceDataEntity newInvoiceDataEntity = createDraftVersionOfInvoiceData(businessEventEntity);
				eventsMap.put(key, newInvoiceDataEntity);
				LOG.info("A new draft invoice is created " + newInvoiceDataEntity.logInfo());
			}
			InvoiceDataEntity invoiceDataEntity = eventsMap.get(key);
			invoiceDataEntity.addBusinessEventEntity(businessEventEntity);
		}
		return eventsMap;
	}
	
	private String generateKey(BusinessEventEntity businessEventEntity) {
		StringBuffer key = new StringBuffer();
		key.append(businessEventEntity.getSupplierId()).
			append(businessEventEntity.getPaymentResponsible()).
			append(businessEventEntity.getPaymentResponsible());
		return key.toString();
	}
	
	private List<ServiceResponse> getServiceResponse(final List<InvoiceDataEntity> pendingInvoiceList) {
		List<ServiceResponse> serviceRespList = new ArrayList<ServiceResponse>();
		for (InvoiceDataEntity entity : pendingInvoiceList) {
			final ServiceResponse serviceResponse = new ServiceResponse();
			serviceResponse.setId(entity.getId());
			serviceResponse.setStatus("OK");
			serviceResponse.setMessage(entity.logInfo());
			serviceRespList.add(serviceResponse);
		}
		return serviceRespList;
	}
}
