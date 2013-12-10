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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.support.MetricType;
import org.springframework.stereotype.Component;

import riv.sll.invoicedata.getinvoicedataresponder._1.GetInvoiceDataRequest;
import se.sll.invoicedata.core.model.repository.InvoiceDataRepository;

/**
 * JMX Bean to keep track of application status. <p>
 * 
 * Exposes methods to perform health checks and connection instrumentation information.
 * 
 * @author Peter
 *
 */
@Component
@ManagedResource(objectName = "se.sll.invoicedata:name=StatusBean", description="Status information")
public class StatusBean {

    private static final Logger log = LoggerFactory.getLogger(StatusBean.class);

    @Autowired
    private InvoiceDataRepository invoiceDataRepository;

    //
    private int historyLength = 1000;
    
    //
    private static ThreadLocal<Stack<Sample>> samples = new ThreadLocal<Stack<Sample>>() {
        @Override
        public Stack<Sample> initialValue() {
            return new Stack<Sample>();
        }
    };

    //
    private static Map<String, HistoryTimer> timerMap = new HashMap<String, HistoryTimer>();

    //
    private static Concurrency concurrency = new Concurrency();

    // checks database
    private void checkDatabase() {
        invoiceDataRepository.findOne(0L);
        log.info("health-check database: OK");
    }

    // checks rating
    private void checkRating() {
        log.info("health-check rating: OK");
    }

    private void checkAlloc() {
        final byte[] bytes = new byte[1024 * 1024];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)0xff;
        }
        log.info("health-check memory alloc: OK");
    }

    //
    private void checkLog() {
        final String msg = "health-check log: OK";
        log.error(msg);
        log.debug(msg);
        log.trace(msg);
        log.info(msg);
    }

    @ManagedOperation(description="Performs health check, i.e. are connections, memory, logs working as expected")
    public void healthCheck() {
        checkDatabase();
        checkRating();
        checkAlloc();
        checkLog();
    }


    @ManagedMetric(category="utilization", displayName="Active (ongoing) requests", metricType=MetricType.COUNTER, unit="request")
    public long getNumActiveRequests() {
        return concurrency.getActiveRequests();
    }

    @ManagedMetric(category="utilization", displayName="Total requests served", metricType=MetricType.COUNTER, unit="request")
    public long getNumTotalRequests() {
        return concurrency.getTotalRequests();
    }

    @ManagedOperation(description="Returns performance metrics (JSON strings) in millisceonds for all instrumented operations")
    public String[] getPerformanceMetricsAsJSON() {
        final Collection<HistoryTimer> c = timerMap.values();
        final String[] list = new String[c.size()];
        int i = 0;
        for (final HistoryTimer t : c) {
            t.recalc();
            list[i++] = t.toString();
        }
        return list;
    }

    @ManagedOperation(description="Clears sampled performance metrics (timed statistics)")
    public void clearPerformanceMetrics() {
        timerMap.clear();
    }

    @ManagedAttribute(description="Sets the length of request history for timed statistics", defaultValue="1000")
    public void setHistoryLength(@ManagedOperationParameter(name="historyLength", description="Indicates history of requests to keep/calculate averge timed statistcis") int historyLength) {
        if (historyLength > 0) {
            this.historyLength = historyLength;
        }
    }

    @ManagedAttribute(description="Returns the length of request history for timed statistics")
    public int getHistoryLength() {
        return historyLength;
    }


    @ManagedOperation(description="Returns active service contract names, i.e. WSDL operations that have been accessed since last startup")
    public String[] getServiceNames() {
        final Set<String> set = timerMap.keySet();
        return set.toArray(new String[set.size()]);
    }

    //
    public String getGUID() {
        return samples.get().peek().getGUID();
    }

    //
    public String getName() {
        return samples.get().peek().getName();
    }

    //
    public void start(final String path) {
        if (samples.get().size() == 0) {
            concurrency.inc();
        }
        samples.get().push(new Sample(path));
    }

    //
    public void stop() {
        final Sample sample = samples.get().pop();
        final String name = sample.getName();
        final long elapsed = sample.elapsed();
        HistoryTimer timer = timerMap.get(name);
        if (timer == null) {
            timer = new HistoryTimer(name, this.historyLength);
            timerMap.put(name, timer);
        }
        timer.add(elapsed);
        if (samples.get().size() == 0) {
            concurrency.dec();
        }
    }


    /**
     * Samples processing time for one transaction.
     */
    static class Sample {
        private long timestamp;
        private String name;
        private String guid;

        //
        public Sample(final String name) {
            this.timestamp = System.currentTimeMillis();
            this.name = name;
            this.guid = UUID.randomUUID().toString();
        }
        
        //
        public String getGUID() {
            return guid;
        }

        //
        public String getName() {
            return name;
        }

        //
        public long elapsed() {
            final long time = (System.currentTimeMillis() - timestamp);
            return (time < 0) ? 0 : time;
        }
    }
  
    //
    static class Concurrency {
        private long activeRequests;
        private long totalRequests;

        public synchronized void inc() {
            activeRequests++;
            totalRequests++;
        }

        public synchronized void dec() {
            activeRequests--;
        }

        public synchronized long getActiveRequests() {
            return activeRequests;
        }
        
        public synchronized long getTotalRequests() {
            return totalRequests;
        }

    }    
}
