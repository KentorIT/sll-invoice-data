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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.sll.invoicedata.core.jmx.StatusBean;
import se.sll.invoicedata.core.model.entity.InvoiceDataEntity;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;
import se.sll.invoicedata.core.service.HSAToSupplierMappingService;

/**
 * Runs service batch jobs.
 * 
 * @author Peter
 *
 */
@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private InvoiceDataRepository invoiceDataRepository;
    
    @Autowired
    private HSAToSupplierMappingService authorizationService;

    @Autowired
    private StatusBean statusBean;

    @Value("${invoicedata.monthTTL:0}")
    private int invoiceDataTTL;

    @Scheduled(cron="${job.cron}")
    public void batchJob() {
        log.info("Start batch job");
        
        //authorizationService.loadServiceCodeSupplierRelation();
        
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, (-1 * invoiceDataTTL));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        statusBean.start("JobService.removeOldData()");
        try {
            removeOldData(cal.getTime());
        } finally {
            statusBean.stop();
        }
    }

    @Transactional
    private void removeOldData(final Date maxDate) {
        if (invoiceDataTTL <= 0) {
            log.info("Parameter invoicedata.monthTTL is zero, i.e. life-cycle management of old data has been disabled");
            return;
        }
        final List<InvoiceDataEntity> list = invoiceDataRepository.findByEndDateLessThan(maxDate);
        log.info("Remove invoice data older than: {}, count: {}", maxDate, list.size());
        invoiceDataRepository.delete(list);
    }

}
