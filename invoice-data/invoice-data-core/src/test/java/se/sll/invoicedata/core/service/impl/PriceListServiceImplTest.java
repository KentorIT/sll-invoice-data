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

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import riv.sll.invoicedata._1.RegisteredEvent;
import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import se.sll.invoicedata.core.service.InvoiceDataService;
import se.sll.invoicedata.core.service.InvoiceDataServiceException;
import se.sll.invoicedata.core.service.PriceListService;
import se.sll.invoicedata.core.service.dto.Price;
import se.sll.invoicedata.core.service.dto.PriceList;
import se.sll.invoicedata.core.support.TestSupport;

public class PriceListServiceImplTest extends TestSupport {

    @Autowired
    private PriceListService priceListService;
    
    @Autowired
	private InvoiceDataService invoiceDataService;
    
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
	@Transactional
	@Rollback(true)
    public void testSave_success() {
        final PriceList priceList = createSamplePriceList();
        
        priceListService.savePriceLists(Collections.singletonList(priceList));
        
        assertEquals(1, priceListService.getPriceLists().size());        
    }
    
    @Test
	@Transactional
	@Rollback(true)
    public void testPrice_With_Actual_Event() {
    	final PriceList priceList = createSamplePriceList();
    	priceListService.savePriceLists(Collections.singletonList(priceList));
		//650 * 3 = 1950
		assertEquals(1950, createAndFetchRegisteredEvent(priceList.getSupplierId(), 
				priceList.getServiceCode()).getTotalAmount().intValue());
    }
    
    @Test
	@Transactional
	@Rollback(true)
    public void testChange_In_Price_With_Actual_Event() {
    	final PriceList priceList = createSamplePriceList();
    	priceListService.savePriceLists(Collections.singletonList(priceList));
    	
    	//With pre-configured price 650 * 3 = 1950
    	assertEquals(1950, createAndFetchRegisteredEvent(priceList.getSupplierId(), 
    					priceList.getServiceCode()).getTotalAmount().intValue());
    	
    	//change in price from 650 to 155,25
    	priceList.getPrices().get(0).setPrice(BigDecimal.valueOf(155.25));
    	priceListService.savePriceLists(Collections.singletonList(priceList));
    	
		//155,25 * 3 = 465,75
		assertEquals(465,75, createAndFetchRegisteredEvent(priceList.getSupplierId(), priceList.getServiceCode()).getTotalAmount().intValue());
    }
    
    private RegisteredEvent createAndFetchRegisteredEvent(String supplierId, String serviceCode) {
    	
    	
    	final Event e = createSampleEvent();
    	e.setServiceCode(serviceCode);
    	e.setSupplierId(supplierId);
    	e.getItemList().clear(); //Remove all items and all new ones
    	
		Item i1 = new Item();
		i1.setDescription("Test item");
		i1.setItemId("item.1");
		i1.setQty(BigDecimal.valueOf(3));
		e.getItemList().add(i1);
		invoiceDataService.registerEvent(e);
		
		GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
		getIDRequest.setSupplierId(e.getSupplierId());
		getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
		return invoiceDataService.getAllUnprocessedBusinessEvents(getIDRequest).get(0);
    }
    
    
    
    @Test (expected = InvoiceDataServiceException.class)
   	@Transactional
   	@Rollback(true)
    public void testPrice_With_Invalid_Servicecode_Fail() {
   	    final PriceList priceList = createSamplePriceList();
   	    priceListService.savePriceLists(Collections.singletonList(priceList));
   	
   	    final Event e = createSampleEvent();
   	    //Service code and supplier id don't match item!
   	    e.getItemList().clear(); //Remove all items and all new ones
   	
	    Item i1 = new Item();
	    i1.setDescription("Test item");
	    i1.setItemId("item.1");
	    i1.setQty(BigDecimal.valueOf(3));
	    e.getItemList().add(i1);
	    invoiceDataService.registerEvent(e);
	
	    GetInvoiceDataRequest getIDRequest = new GetInvoiceDataRequest();
	    getIDRequest.setSupplierId(e.getSupplierId());
	    getIDRequest.setPaymentResponsible(e.getPaymentResponsible());
	    RegisteredEvent rE = invoiceDataService.getAllUnprocessedBusinessEvents(getIDRequest).get(0);
	
	    //650 * 3 = 1950
	    assertEquals(1950, rE.getTotalAmount().intValue());
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
