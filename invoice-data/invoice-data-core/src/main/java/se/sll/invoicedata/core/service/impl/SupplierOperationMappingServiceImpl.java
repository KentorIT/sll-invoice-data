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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sll.invoicedata.core.model.entity.SupplierOperationMappingEntity;
import se.sll.invoicedata.core.model.repository.SupplierOperationMappingRepository;
import se.sll.invoicedata.core.service.SupplierOperationMappingService;
import se.sll.invoicedata.core.utility.Operation;

@Service
public class SupplierOperationMappingServiceImpl implements SupplierOperationMappingService {
	
	private static final Logger log = LoggerFactory.getLogger(SupplierOperationMappingServiceImpl.class);
	
	@Autowired
	private SupplierOperationMappingRepository supplierOperationMappingRepo;
	
	private Map<String, List<Operation>> supplierIdToOperationMap = null; 
	
	@Override
	public boolean isSupplierMappedToOperation(String supplierId, Operation operationEnum) {
		boolean isConfigured = false;
		if (supplierIdToOperationMap == null) {
			reloadSupplierOperationRelation();
		}
		List<Operation> operationsList = getOperationsForSupplier(supplierId);	
		for (Operation operation : operationsList) {
			if (operation == operationEnum || operation == Operation.ALL) {
				isConfigured = true;
				break;
			}
		}
		return isConfigured;
	}
	
	public void reloadSupplierOperationRelation() {
		log.info("AuthorizationService loading/reloading service code supplier relation");
		List<SupplierOperationMappingEntity> serviceAuthEntityList = supplierOperationMappingRepo.findAll();
		supplierIdToOperationMap = new HashMap<String, List<Operation>>();
		
		for (SupplierOperationMappingEntity entity : serviceAuthEntityList) {
			List<Operation> operationList = supplierIdToOperationMap.get(entity.getSupplierId());
			
			if (operationList == null) {
				operationList = new ArrayList<Operation>();
			}
			operationList.add(entity.getOperationEnum());
			supplierIdToOperationMap.put(entity.getSupplierId(), operationList);
		}
	}
	
	private List<Operation> getOperationsForSupplier(String supplierId) {
		List<Operation> operationsList = supplierIdToOperationMap.get(supplierId);
		return (operationsList != null) ? operationsList : new ArrayList<Operation>();
	}
}
