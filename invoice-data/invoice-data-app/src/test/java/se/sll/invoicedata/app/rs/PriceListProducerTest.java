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

package se.sll.invoicedata.app.rs;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.ResponseReader;
import org.junit.Before;
import org.junit.Test;

import se.sll.invoicedata.core.service.dto.Price;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.service.dto.ServiceResponse;



public class PriceListProducerTest {
    
    static String LIST_ENDPOINT = "http://localhost:8080/invoice-data-app/admin";
    
    private PriceListProducer priceListProducer;
    
    @Before
    public void setUp() {
        List<ResponseReader> readers = new ArrayList<ResponseReader>();
        for (Class<?> c : new Class[] { ServiceResponse.class, PriceList.class }) {
            ResponseReader reader = new ResponseReader();
            reader.setEntityClass(c);
            readers.add(reader);
        }
        
        priceListProducer = JAXRSClientFactory.create(LIST_ENDPOINT, PriceListProducer.class, readers);  
        // scratch
        for (final PriceList priceList : priceListProducer.getPriceLists()) {
            priceListProducer.deletePriceList(priceList.getId());
        }
    }
    
    @Test
    public void testFind_empty() {
        List<PriceList> list = priceListProducer.getPriceLists();
        assertEquals(0, list.size());
    }
    
    @Test
    public void testCreate_success() {
        PriceList priceList = createSamplePriceList("Tolk.001");
        Response response = priceListProducer.putPriceList(priceList);
        
        assertEquals(200, response.getStatus());
        
        ServiceResponse serviceResponse = (ServiceResponse)response.getEntity();
        assertEquals("OK", serviceResponse.getStatus());
        
    }

    @Test
    public void testCreate_null_fail() {
        PriceList priceList = createSamplePriceList("Tolk.001");
        priceList.setValidFrom(null);
        Response response = priceListProducer.putPriceList(priceList);
        assertEquals(500, response.getStatus());        
    }

    @Test
    public void testUpdate_success() {
        PriceList priceList = createSamplePriceList("Tolk.001");
        Response response = priceListProducer.putPriceList(priceList);
        assertEquals(200, response.getStatus());
        
        
        // remove a price
        int numPrices = priceList.getPrices().size();
        priceList.getPrices().remove(0);
        response = priceListProducer.putPriceList(priceList);
        assertEquals(200, response.getStatus());
        
        priceList = priceListProducer.getPriceLists().get(0);
        assertEquals(numPrices-1, priceList.getPrices().size());
    }

    private PriceList createSamplePriceList(String supplierId) {
        final PriceList priceList = new PriceList();
        priceList.setServiceCode("Språktolk");
        priceList.setSupplierId(supplierId);
        priceList.setValidFrom(Calendar.getInstance().getTime());
        
        for (int i = 0; i < 10; i++) {
            Price price = new Price();
            price.setItemId("item.1");
            price.setPrice(BigDecimal.valueOf(700.50+i));
            priceList.getPrices().add(price);
        }
        
        return priceList;
    }
    
}
