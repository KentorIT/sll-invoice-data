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
import se.sll.invoicedata.core.service.impl.TestDataHelperService;

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
