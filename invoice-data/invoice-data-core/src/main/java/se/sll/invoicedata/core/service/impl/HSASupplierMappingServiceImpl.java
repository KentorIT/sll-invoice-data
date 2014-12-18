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

import se.sll.invoicedata.core.model.entity.HSASupplierMappingEntity;
import se.sll.invoicedata.core.model.repository.HSASupplierMappingRepository;
import se.sll.invoicedata.core.service.HSASupplierMappingService;

@Service
public class HSASupplierMappingServiceImpl implements HSASupplierMappingService {
	
	private static final Logger log = LoggerFactory.getLogger(HSASupplierMappingServiceImpl.class);
	
	@Autowired
	private HSASupplierMappingRepository serviceAuthorizationRepository;
	
	private Map<String, String> serviceCodeToSupplierMap = null; 
	
	@Override
	public boolean isHSAIdMappedToSupplier(String hsaID, String supplierId) {
		if (serviceCodeToSupplierMap == null) {
			reloadHSAIdSupplierRelation();
		}
		String config = serviceCodeToSupplierMap.get(hsaID);		
		return config != null && (config.startsWith("*") || config.contains(supplierId));
	}
	
	public void reloadHSAIdSupplierRelation() {
		log.info("AuthorizationService loading/reloading service code supplier relation");
		List<HSASupplierMappingEntity> serviceAuthEntityList = serviceAuthorizationRepository.findAll();
		serviceCodeToSupplierMap = new HashMap<String, String>();
		
		for (HSASupplierMappingEntity entity : serviceAuthEntityList) {
			serviceCodeToSupplierMap.put(entity.getHSAId(), entity.getSupplierIdConfig());
		}
	}
}
