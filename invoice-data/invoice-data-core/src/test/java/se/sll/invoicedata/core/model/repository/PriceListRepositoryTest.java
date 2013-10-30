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

package se.sll.invoicedata.core.model.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
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
    @Rollback (true)
    public void testInsertFind_simple() {
        final PriceListEntity priceListEntity = createSamplePriceListEntity();
        priceListRepository.save(priceListEntity);
        priceListRepository.flush();
        
        assertEquals(1, priceListRepository.count());
        
    }
    
    @Transactional
    @Test
    public void testPriceListEntity_Equals() {
    	final PriceListEntity pl1 = createSamplePriceListEntity();
    	final PriceListEntity pl2 = createSamplePriceListEntity();
    	
    	assertFalse(pl1.equals(pl2));    	
    }
    
    @Transactional
    //@Test
    public void testAddPriceEntity_Add_Null() {
    	final PriceListEntity priceListEntity = createSamplePriceListEntity();
    	priceListEntity.addPriceEntity(null);
    }

    @Transactional
    @Test
    @Rollback (true)
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
