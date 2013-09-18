/**
 *  Copyright (c) 2013 SLL <http://sll.se/>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package se.sll.invoicedata.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.service.InvoiceDataService;


@Service
@Transactional
public class InvoiceDataServiceImpl implements InvoiceDataService {

    @Autowired
    private BusinessEventRepository businessEventRepository;
    
    @Override
    public void registerBusinessEvent(BusinessEventEntity businessEventEntity) {
        businessEventRepository.save(businessEventEntity);
    }

	@Override
	public BusinessEventEntity getBusinessEvent(String eventId) {
		return businessEventRepository.findOne(eventId);
	}

	@Override
	public List<BusinessEventEntity> getAllUnprocessedBusinessEvents(
			String supplierName) {
		List<BusinessEventEntity> bEEntitiesBySupplier = new ArrayList<BusinessEventEntity>();
		List<BusinessEventEntity> bEventEntities = businessEventRepository.findAll();
		//TODO: Condition for unprocessed business events required!!
		for (BusinessEventEntity bEEntity : bEventEntities) {
			
			if (bEEntity.getSupplierName() != null &&
					bEEntity.getSupplierName().equalsIgnoreCase(supplierName)) {				
				bEEntitiesBySupplier.add(bEEntity);
			}
		}		
		return bEEntitiesBySupplier;
	}

}
