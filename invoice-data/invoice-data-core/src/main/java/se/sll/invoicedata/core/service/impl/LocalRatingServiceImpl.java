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
        return BigDecimal.ZERO;
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
