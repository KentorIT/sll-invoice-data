/**
 * Copyright (c) 2013 SLL, <http://sll.se>
 *
 * This file is part of Invoice-Data.
 *
 *     Invoice Data is free software: you can redistribute it and/or modify
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riv.sll.invoicedata._1.Event;
import riv.sll.invoicedata._1.Item;
import se.sll.invoicedata.core.service.InvoiceDataService;

/**
 * Generates test-data.
 * 
 * 
 * @author Peter
 *
 */
@Service
@Profile(value = "test")
public class TestDataHelperService {
    private static final DatatypeFactory datatypeFactory;
    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Init Error!", e);
        }
    }

    @Autowired
    private InvoiceDataService invoiceDataService;
  
    @Value("${testdata.months:3}")
    private int months;

    @Value("${testdata.dailyEvents:50}")
    private int dailyEvents;

    
    public int generateTestData() {
        return generateTestData0();
    }
    
    @Transactional    
    int generateTestData0() {
        GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();
        cal.set(GregorianCalendar.SECOND, 0);
        cal.set(GregorianCalendar.MILLISECOND, 0);

        cal.add(GregorianCalendar.MONTH, -getMonths());
        
        int n = 0;
        for (int m = 0; m < getMonths(); m++) {
            cal.add(GregorianCalendar.MONTH, 1);
            n += generateEvents(cal);            
        }
        return n;        
    }
    
    
    /**
     * Returns a {@link XMLGregorianCalendar} date and time representation.
     * 
     * @param cal the actual date and time represented as {@link GregorianCalendar}.
     * @return the {@link XMLGregorianCalendar} representation.
     */    
    public static XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar cal) {
        return (cal == null) ? null : datatypeFactory.newXMLGregorianCalendar(cal);
    }
    
    //
    int generateEvents(GregorianCalendar cal) {
        cal.set(GregorianCalendar.DATE, 1);
        int n = 0;
        int m = cal.get(GregorianCalendar.MONTH);
        while (m <= cal.get(GregorianCalendar.MONTH)) {
            cal.add(GregorianCalendar.DATE, 1);
            n += createEvents(cal);
        }
        return n;
    }
    
    
    //
    int random(int max) {
        return (int)(Math.random() * max);
    }
    
    //
    int createEvents(GregorianCalendar cal) {
        final int n = getDailyEvents();
        for (int i = 0; i < n; i++) {
            Event e = new Event();
            e.setEventId(UUID.randomUUID().toString());
            e.setHealthcareFacility("HSA_ID_001");
            e.setRefContractId("CONTRACT_1");
            e.setAcknowledgedBy(rollingText(i, "per", "lena", "eva", "lisa", "hans", "greta", "unknwon"));
            e.setStartTime(setRandomTime(cal));
            e.setStartTime(addRandomTime(cal));
            e.setEndTime(addRandomTime(cal));
            e.setAcknowledgedTime(addRandomTime(cal));
            e.setHealthCareCommission(randomText("BVC", "Ögon", "Öron", "Medicin", "Akut"));
            e.setServiceCode("Språktolk");
            e.setPaymentResponsible(rollingText(i, "HSF", "TioHundra"));
            e.setSupplierId(rollingText(i, "Tolk A", "Tolk B", "Tolk C", "Tolk D"));
            e.setSupplierName("Name is not of interest at this time");
            createItems(e.getItemList());
            invoiceDataService.registerEvent(e);
        }
        return n;
    }
    
    //
    int createItems(List<Item> list) {
        final int n = random(10) + 1;
        for (int i = 0; i < n; i++) {
            Item item = new Item();
            item.setDescription("Oridnary item description");
            item.setQty(BigDecimal.valueOf((Math.random()*5)+1.0d).setScale(2, BigDecimal.ROUND_CEILING));
            item.setItemId("item-" + i);
            list.add(item);
        }
        return n;
    }
        
    //
    String rollingText(int num, String... texts) {
        return texts[num % texts.length];
    }
    
    //
    String randomText(String... texts) {
        return texts[random(texts.length)];        
    }
    
    //
    XMLGregorianCalendar addRandomTime(GregorianCalendar cal) {
        int h = random(3);
        int m = random(60);
        cal.add(GregorianCalendar.HOUR_OF_DAY, h);
        cal.add(GregorianCalendar.MINUTE, m);
        return toXMLGregorianCalendar(cal);    
    }
    
    //
    XMLGregorianCalendar setRandomTime(GregorianCalendar cal) {
        int h = random(24);
        int m = random(60);
        cal.set(GregorianCalendar.HOUR_OF_DAY, h);
        cal.set(GregorianCalendar.MINUTE, m);
        return toXMLGregorianCalendar(cal);    
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public int getDailyEvents() {
        return dailyEvents;
    }

    public void setDailyEvents(int dailyEvents) {
        this.dailyEvents = dailyEvents;
    }
}
 
