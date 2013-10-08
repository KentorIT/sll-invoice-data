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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.sll.invoicedata.core.service.PriceListService;
import se.sll.invoicedata.core.service.dto.Price;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.support.TestSupport;

public class PriceListServiceImplTest extends TestSupport {

    @Autowired
    private PriceListService priceListService;
    
    @Test
    public void testSave_success() {
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
        
        priceListService.savePriceLists(Collections.singletonList(priceList));
        
        assertEquals(1, priceListService.getPriceLists().size());        
    }
    
    
}
