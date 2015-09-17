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

import static se.sll.invoicedata.core.util.CoreUtil.copyGenericLists;
import static se.sll.invoicedata.core.util.CoreUtil.copyProperties;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.PriceEntity;
import se.sll.invoicedata.core.model.entity.PriceListEntity;
import se.sll.invoicedata.core.model.repository.PriceListRepository;
import se.sll.invoicedata.core.service.PriceListService;
import se.sll.invoicedata.core.service.dto.Price;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.service.dto.ServiceResponse;

@Service
@Transactional
public class PriceListServiceImpl implements PriceListService {

    private static final Logger log = LoggerFactory.getLogger(PriceListService.class);
    
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

    //
    private PriceListEntity lookupPriceListEntity(final PriceList priceList) {
        // Lookup by id or logical key
        if (priceList.getId() != null) {
            return priceListRepository.findOne(priceList.getId());
        }
        return priceListRepository.findBySupplierIdAndServiceCodeAndValidFrom(
                priceList.getSupplierId(),
                priceList.getServiceCode(),
                priceList.getValidFrom());
    }
    
    @Override
    public ServiceResponse savePriceList(final PriceList priceList) {

        final ServiceResponse response = new ServiceResponse();

        final PriceListEntity oldPriceListEntity = lookupPriceListEntity(priceList);

        log.debug("save pricelist new: {}, old: {}", priceList, oldPriceListEntity);
        
        if (oldPriceListEntity != null) {
            priceListRepository.delete(oldPriceListEntity.getId());
            // XXX: flush is necessary in order to avoid duplicate key in index exceptions
            priceListRepository.flush();
            log.debug("delete old pricelist with id: {}", oldPriceListEntity.getId());
            response.setMessage("updated");
        } else {
            response.setMessage("created");            
        }
        
        final PriceListEntity newPriceListEntity = copyProperties(priceList, PriceListEntity.class);
        
        for (final Price price : priceList.getPrices()) {
            log.debug("price {}", price);
            newPriceListEntity.addPriceEntity(copyProperties(price, PriceEntity.class));
        }
                
        response.setId(priceListRepository.save(newPriceListEntity).getId());
        
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
