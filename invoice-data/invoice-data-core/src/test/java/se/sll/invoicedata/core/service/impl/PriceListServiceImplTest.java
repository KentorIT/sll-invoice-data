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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import se.sll.invoicedata.core.service.PriceListService;
import se.sll.invoicedata.core.service.dto.Price;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.support.TestSupport;

public class PriceListServiceImplTest extends TestSupport {

    @Autowired
    private PriceListService priceListService;
    
    @Before
    public void deleteAll() {
        for (final PriceList priceList : priceListService.getPriceLists()) {
            priceListService.deletePriceList(priceList.getId());
        }
    }
    
    @Test(expected = DataIntegrityViolationException.class)
    public void testSave_null_valid_from_date() {
        final PriceList priceList = createSamplePriceList();
        priceList.setValidFrom(null);
        priceListService.savePriceLists(Collections.singletonList(priceList));        
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testSave_null_supplier_id() {
        final PriceList priceList = createSamplePriceList();
        priceList.setSupplierId(null);
        priceListService.savePriceLists(Collections.singletonList(priceList));        
    }
    
    @Test(expected = DataIntegrityViolationException.class)
    public void testSave_null_service_code() {
        final PriceList priceList = createSamplePriceList();
        priceList.setServiceCode(null);
        priceListService.savePriceLists(Collections.singletonList(priceList));        
    }
    
    @Test(expected = DataIntegrityViolationException.class)
    public void testSave_null_item_id() {
        final PriceList priceList = createSamplePriceList();
        priceList.getPrices().get(0).setItemId(null);
        priceListService.savePriceLists(Collections.singletonList(priceList));        
    }    
    

    @Test
    public void testSave_success() {
        final PriceList priceList = createSamplePriceList();
        
        priceListService.savePriceLists(Collections.singletonList(priceList));
        
        assertEquals(1, priceListService.getPriceLists().size());        
    }
    
    
    
    @Test(expected = DataIntegrityViolationException.class)
    public void testSave_duplicate_item_id_fail() {
        final PriceList priceList = createSamplePriceList();
        
        priceList.getPrices().get(0).setItemId("item.1");
        priceList.getPrices().get(1).setItemId("item.1");
        
        priceListService.savePriceList(priceList);        
    }
    
    
    PriceList createSamplePriceList() {
        final PriceList priceList = new PriceList();
        
        priceList.setSupplierId("Tolk.001");
        priceList.setValidFrom(Calendar.getInstance().getTime());
        priceList.setServiceCode("Språktolk");
        
        Price p1 = new Price();
        p1.setItemId("item.1");
        p1.setPrice(BigDecimal.valueOf(650.00));
        
        Price p2 = new Price();
        p2.setItemId("item.2");
        p2.setPrice(BigDecimal.valueOf(650.00));

        priceList.getPrices().add(p1);
        priceList.getPrices().add(p2);

        return priceList;
    }
    
    
}
