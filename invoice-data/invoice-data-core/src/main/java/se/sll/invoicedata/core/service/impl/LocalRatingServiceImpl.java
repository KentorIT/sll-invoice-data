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

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.BusinessEventEntity;
import se.sll.invoicedata.core.model.entity.ItemEntity;
import se.sll.invoicedata.core.model.entity.PriceEntity;
import se.sll.invoicedata.core.model.entity.PriceListEntity;
import se.sll.invoicedata.core.model.repository.PriceListRepository;
import se.sll.invoicedata.core.service.InvoiceDataErrorCodeEnum;
import se.sll.invoicedata.core.service.RatingService;

/**
 * Note: To avoid breaking the build, this implementation generates test data if no price list is found. Shall be fixed 
 *  as soon as test-data has been aligned.
 *  
 * @author Peter
 *
 */
@Service
public class LocalRatingServiceImpl implements RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingService.class);

    @Autowired
    private PriceListRepository priceListRepository;


    @Override
    @Transactional
    public BigDecimal rate(ItemEntity itemEntity) {
        final PriceListEntity priceListEntity = lookupPriceList(itemEntity.getEvent());
        if (priceListEntity != null) {
            for (final PriceEntity priceEntity : priceListEntity.getPriceEntities()) {
                if (priceEntity.getItemId().equals(itemEntity.getItemId())) {
                    log.debug("rating: found price {} for item {}", priceEntity.getPrice(), itemEntity);
                    return priceEntity.getPrice();
                }
            }
        }
        log.error("No price information (zero) found for item {}", itemEntity);
        throw InvoiceDataErrorCodeEnum.NOTFOUND_ERROR.createException("No price information (zero) found for item", itemEntity.getItemId());
    }

    //
    protected PriceListEntity lookupPriceList(BusinessEventEntity e) {
        final List<PriceListEntity> l = priceListRepository.findBySupplierIdAndServiceCodeAndValidFromLessThanEqualOrderByValidFromDesc(
                e.getSupplierId(), 
                e.getServiceCode(), 
                e.getStartTime());

        return (l.size() == 0) ? null : l.get(0);
    }

}
