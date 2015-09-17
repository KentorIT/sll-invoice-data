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

package se.sll.invoicedata.core.jmx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.model.repository.BusinessEventRepository;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.util.TestDataHelperService;

/**
 * JMX Bean with to expose test helper functions, i.e. create test data and clean-up database, .... <p>
 * 
 * Available when spring profile test has been activated.
 * 
 * @author Peter
 *
 */
@Component
@ManagedResource(objectName = "se.sll.invoicedata:name=TestBean", description="Test data utilities, only available when spring profile test has been activated")
@Profile(value = "test")
public class TestBean {
    
    @Autowired
    private BusinessEventRepository businessEventRepository;
    @Autowired
    private InvoiceDataRepository invoiceDataRepository;
    @Autowired
    private TestDataHelperService testDataHelperService;
    
    @ManagedOperation(description="Deletes all database records")
    @Transactional
    public void cleanDatabase() {
        invoiceDataRepository.deleteAll();
        businessEventRepository.deleteAll();
    }
    
    @ManagedOperation(description="Deletes existing data and creates new testdata. Returns the number of genereated events")
    public int generateTestData() {
        cleanDatabase();
        return testDataHelperService.generateTestData();
    }
    
    @ManagedAttribute(description="Sets the number of daily events when generating test data")
    public void setDailyEvents(@ManagedOperationParameter(name="dailyEvents", description="Indicates number of events to generate for each day") int dailyEvents) {
        testDataHelperService.setDailyEvents(dailyEvents);
    }

    @ManagedAttribute(description="Returns the number of daily events when generating test data")
    public int getDailyEvents() {
        return testDataHelperService.getDailyEvents();
    }

    @ManagedAttribute(description="Sets the number of months to generate when generating test data")
    public void setMonths(@ManagedOperationParameter(name="months", description="Indicates number of past months to generate test data for") int months) {
        testDataHelperService.setMonths(months);
    }

    @ManagedAttribute(description="Returns number of months when generating test data")
    public int getMonths() {
        return testDataHelperService.getMonths();
    }

}
