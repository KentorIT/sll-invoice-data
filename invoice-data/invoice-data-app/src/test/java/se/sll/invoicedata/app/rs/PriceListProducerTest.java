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
import org.junit.BeforeClass;
import org.junit.Test;

import se.sll.invoicedata.core.service.dto.Price;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.service.dto.ServiceResponse;


/**
 * Tests price-list admin REST API.
 * 
 * @author Peter
 *
 */
public class PriceListProducerTest {
    
    static String LIST_ENDPOINT = "http://localhost:8080/invoicedata-web-app/admin";
    
    private static PriceListProducer priceListProducer;
    
    /**
     * Initialize REST client.
     * 
     */
    @BeforeClass
    public static void initalize() {
        // register readers to deserialize response entities
        final List<ResponseReader> readers = new ArrayList<ResponseReader>();
        for (final Class<?> cls : new Class[] { ServiceResponse.class, PriceList.class }) {
            readers.add(new ResponseReader(cls));
        }
        // create producer
        priceListProducer = JAXRSClientFactory.create(LIST_ENDPOINT, PriceListProducer.class, readers);          
    }
    
    /**
     * Deletes all price lists before starting a test.
     */
    @Before
    public void deleteAll() {
        // deleteAll
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

    //
    protected PriceList createSamplePriceList(String supplierId) {
        final PriceList priceList = new PriceList();
        priceList.setServiceCode("Språktolk");
        priceList.setSupplierId(supplierId);
        priceList.setValidFrom(Calendar.getInstance().getTime());
        
        for (int i = 0; i < 10; i++) {
            Price price = new Price();
            price.setItemId("item." + i);
            price.setPrice(BigDecimal.valueOf(700.50+i));
            priceList.getPrices().add(price);
        }
        
        return priceList;
    }
    
}
