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

import se.sll.invoicedata.core.model.entity.PriceEntity;
import se.sll.invoicedata.core.model.entity.PriceListEntity;
import se.sll.invoicedata.core.model.repository.PriceListRepository;
import se.sll.invoicedata.core.service.PriceListService;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.service.dto.Price;
import se.sll.invoicedata.core.service.dto.ServiceResponse;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyGenericLists;
import static se.sll.invoicedata.core.service.impl.CoreUtil.copyProperties;

@Service
@Transactional
public class PriceListServiceImpl implements PriceListService {

    @Autowired
    private PriceListRepository priceListRepository;

    //
    static PriceList toPriceList(PriceListEntity priceListEntity) {
        if (priceListEntity == null) {
            return null;
        }
        final PriceList priceList = copyProperties(priceListEntity, PriceList.class);
        copyGenericLists(priceList.getPrices(), priceListEntity.getPriceEntities(), Price.class);
        return priceList;
    }
    
    @Override
    public List<PriceList> getPriceLists() {
        final List<PriceListEntity> source = priceListRepository.findAll();
        final List<PriceList> target = new ArrayList<PriceList>(source.size());
        for (final PriceListEntity priceListEntity : source) {
            target.add(toPriceList(priceListEntity));

        }
        return target;
    }

    @Override
    public ServiceResponse savePriceList(final PriceList priceList) {

        final ServiceResponse response = new ServiceResponse();

        PriceListEntity priceListEntity;

        // Lookup by id or logical key
        if (priceList.getId() != null) {
            priceListEntity = priceListRepository.findOne(priceList.getId());
        } else {
            priceListEntity = priceListRepository.findBySupplierIdAndServiceCodeAndValidFrom(
                    priceList.getSupplierId(),
                    priceList.getServiceCode(),
                    priceList.getValidFrom());
        }

        if (priceListEntity == null) {
            priceListEntity = copyProperties(priceList, PriceListEntity.class);
            response.setMessage("created");
        } else {
            priceListEntity.clearPriceEntities();
            response.setMessage("updated");
        }
        
        for (final Price price : priceList.getPrices()) {
            final PriceEntity priceEntity = copyProperties(price, PriceEntity.class);
            priceListEntity.addPriceEntity(priceEntity);
        }
        PriceListEntity saved = priceListRepository.save(priceListEntity);
        
        response.setId(saved.getId());
        
        return response;
    }

    @Override
    public List<ServiceResponse> savePriceLists(List<PriceList> priceLists) {
        List<ServiceResponse> responses = new ArrayList<ServiceResponse>(priceLists.size());
        for (final PriceList priceList : priceLists) {
            responses.add(savePriceList(priceList));
        }
        return responses;
    }

    @Override
    public ServiceResponse deletePriceList(final Long id) {
        final PriceListEntity priceListEntity = priceListRepository.findOne(id);
        final ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setId(id);
        if (priceListEntity != null) {
            priceListRepository.delete(priceListEntity);
            serviceResponse.setMessage("deleted");
        } else {
            serviceResponse.setMessage("not found");
        }
        return serviceResponse;
    }

    @Override
    public PriceList getPriceList(Long id) {
        return toPriceList(priceListRepository.findOne(id));
    }

}
