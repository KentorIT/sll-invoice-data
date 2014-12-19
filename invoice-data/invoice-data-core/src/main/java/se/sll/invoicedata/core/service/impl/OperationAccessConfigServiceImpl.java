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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sll.invoicedata.core.access.Operation;
import se.sll.invoicedata.core.access.OperationAccessKey;
import se.sll.invoicedata.core.access.SupplierConfig;
import se.sll.invoicedata.core.model.entity.OperationAccessConfig;
import se.sll.invoicedata.core.model.repository.OperationAccessConfigRepository;
import se.sll.invoicedata.core.service.OperationAccessConfigService;

@Service
public class OperationAccessConfigServiceImpl implements OperationAccessConfigService {
	
	private static final Logger log = LoggerFactory.getLogger(OperationAccessConfigServiceImpl.class);
	
	@Autowired
	private OperationAccessConfigRepository operationAccessConfigRepo;
	
	private Map<OperationAccessKey, SupplierConfig> operationAccessConfigMap = null; 
	
	@Override
	public boolean hasSystemAccessToOperation(Operation operationEnum, String hsaId) {
		if (operationAccessConfigMap == null) {
			reloadOperationAccessConfig();
		}		
		OperationAccessKey key = new OperationAccessKey(operationEnum, hsaId);		
		return operationAccessConfigMap.containsKey(key);
	}
	
	@Override
	public boolean hasSupplierAccessToOperation(Operation operationEnum, String hsaId, String supplierId) {
		if (operationAccessConfigMap == null) {
			reloadOperationAccessConfig();
		}
		OperationAccessKey key = new OperationAccessKey(operationEnum, hsaId);
		return operationAccessConfigMap.get(key).hasSupplierAccess(supplierId);		
	}
	
	public void reloadOperationAccessConfig() {
		log.info("OperationAccessConfig loading/reloading operation-hsaId-supplier mapping");
		List<OperationAccessConfig> operationAccessConfigList = operationAccessConfigRepo.findAll();
		operationAccessConfigMap = new HashMap<OperationAccessKey, SupplierConfig>();
		
		for (OperationAccessConfig entity : operationAccessConfigList) {			
			SupplierConfig operationConfig = new SupplierConfig(entity.getSupplierIdConfig());
			OperationAccessKey key = new OperationAccessKey(entity.getOperationEnum(), entity.getHsaId());
			operationAccessConfigMap.put(key, operationConfig);
		}
	}
	
}
