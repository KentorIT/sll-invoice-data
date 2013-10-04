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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

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
        return CoreUtil.toXMGregorianCalendar(cal);    
    }
    
    //
    XMLGregorianCalendar setRandomTime(GregorianCalendar cal) {
        int h = random(24);
        int m = random(60);
        cal.set(GregorianCalendar.HOUR_OF_DAY, h);
        cal.set(GregorianCalendar.MINUTE, m);
        return CoreUtil.toXMGregorianCalendar(cal);    
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
 
