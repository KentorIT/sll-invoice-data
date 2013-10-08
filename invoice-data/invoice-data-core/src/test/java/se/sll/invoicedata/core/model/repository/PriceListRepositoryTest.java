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

package se.sll.invoicedata.core.model.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.entity.PriceListEntity;
import se.sll.invoicedata.core.support.TestSupport;

/**
 * Unit tests.
 * 
 * @author Peter
 *
 */
public class PriceListRepositoryTest extends TestSupport {

    @Autowired
    private PriceListRepository priceListRepository;
    

    @Transactional
    @Test
    public void testInsertFind_simple() {
        final PriceListEntity priceListEntity = createSamplePriceListEntity();
        priceListRepository.save(priceListEntity);
        priceListRepository.flush();
        
        assertEquals(1, priceListRepository.count());
        
    }

    @Transactional
    @Test
    public void testInsertFind_multiple_dates() {
        Calendar cal = today();
        cal.add(Calendar.DATE, 2);
        for (int i = 3; i > 0; i--) {
            final PriceListEntity priceListEntity = createSamplePriceListEntity();
            priceListEntity.setValidFrom(cal.getTime());
            priceListRepository.save(priceListEntity);
            cal.add(Calendar.DATE, -1);
        }
        priceListRepository.flush();
        assertEquals(3, priceListRepository.count());
        
        final PriceListEntity entity = createSamplePriceListEntity();
        
        Calendar actual = Calendar.getInstance();
        List<PriceListEntity> l = priceListRepository.findBySupplierIdAndServiceCodeAndValidFromLessThanEqualOrderByValidFromDesc(entity.getSupplierId(), entity.getServiceCode(), actual.getTime());
        assertEquals(1, l.size());

        actual.add(Calendar.DATE, 1);
        l = priceListRepository.findBySupplierIdAndServiceCodeAndValidFromLessThanEqualOrderByValidFromDesc(entity.getSupplierId(), entity.getServiceCode(), actual.getTime());
        assertEquals(2, l.size());
        assertTrue(l.get(0).getValidFrom().after(l.get(1).getValidFrom()));
        

        actual.add(Calendar.DATE, 1);
        l = priceListRepository.findBySupplierIdAndServiceCodeAndValidFromLessThanEqualOrderByValidFromDesc(entity.getSupplierId(), entity.getServiceCode(), actual.getTime());        
        assertEquals(3, l.size());

        assertTrue(l.get(0).getValidFrom().after(l.get(1).getValidFrom()));
        assertTrue(l.get(1).getValidFrom().after(l.get(2).getValidFrom()));
    }
}
