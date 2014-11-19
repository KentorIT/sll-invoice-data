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

import se.sll.invoicedata.core.model.entity.HSAToSupplierMappingEntity;
import se.sll.invoicedata.core.model.repository.HSAToSupplierdMappingRepository;
import se.sll.invoicedata.core.service.HSAToSupplierMappingService;

@Service
public class HSAToSupplierMappingServiceImpl implements HSAToSupplierMappingService {
	
	private static final Logger log = LoggerFactory.getLogger(HSAToSupplierMappingServiceImpl.class);
	
	@Autowired
	private HSAToSupplierdMappingRepository serviceAuthorizationRepository;
	
	private Map<String, String> serviceCodeToSupplierMap = null; 
	
	@Override
	public boolean isHSAIdMappedToSupplier(String serviceCode, String supplierId) {
		if (serviceCodeToSupplierMap == null) {
			reloadHSAIdSupplierRelation();
		}
		String config = serviceCodeToSupplierMap.get(serviceCode);		
		return config != null && (config.startsWith("*") || config.contains(supplierId));
	}
	
	public void reloadHSAIdSupplierRelation() {
		log.info("AuthorizationService loading/reloading service code supplier relation");
		List<HSAToSupplierMappingEntity> serviceAuthEntityList = serviceAuthorizationRepository.findAll();
		serviceCodeToSupplierMap = new HashMap<String, String>();
		
		for (HSAToSupplierMappingEntity entity : serviceAuthEntityList) {
			serviceCodeToSupplierMap.put(entity.getHSAId(), entity.getSupplierIdConfig());
		}
	}
}
