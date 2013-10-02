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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.support.MetricType;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(objectName = "se.sll.invoicedata:name=StatusBean", description="Status information")
public class StatusBean {

    //
    private static ThreadLocal<Sample> samples = new ThreadLocal<Sample>() {
        @Override
        public Sample initialValue() {
            return new Sample();
        }
    };
    
    //
    private static Map<String, Timer> timerMap = new HashMap<String, Timer>();
    
    //
    private static Concurrency concurrency = new Concurrency();
 
    

    @ManagedMetric(category="utilization", displayName="Active (ongoing) requests", metricType=MetricType.COUNTER, unit="request")
    public long getActiveRequests() {
        return concurrency.getActiveRequests();
    }

    @ManagedMetric(category="utilization", displayName="Average response time", metricType=MetricType.GAUGE, unit="milliseconds")
    public long getAvgResponseTime() {
        int n = 0;
        int avg = 0;
        for (Timer timer : timerMap.values()) {
            avg += timer.avg();
            n++;
        }
        return (avg / n);
    }

    @ManagedOperation(description="Returns performance metrics in millisceonds for all active WSDL operations")
    public Properties getPerformanceMetrics() {
        final Properties props = new Properties();
        for (Map.Entry<String, Timer> entry : timerMap.entrySet()) {
            props.setProperty(entry.getKey(), entry.getValue().toString());
        }
        return props;
    }

    @ManagedOperation(description="Returns active service contract names, i.e. WSDL operations that have been accessed since last startup")
    public String[] getServiceNames() {
        final Set<String> set = timerMap.keySet();
        return set.toArray(new String[set.size()]);
    }
    
    //
    public void start(final String name) {
        samples.get().reset(name);
        concurrency.inc();
    }

    //
    public void stop() {
        final Sample sample = samples.get();
        final String name = sample.getName();
        final long elapsed = sample.elapsed();
        Timer timer = timerMap.get(name);
        if (timer == null) {
            timer = new Timer();
            timerMap.put(name, timer);
        }
        timer.add(elapsed);
        concurrency.dec();
    }
    
    
    /**
     * Samples processing time for one transaction.
     */
    static class Sample {
        private long t;
        private String name;

        //
        public Sample() {
        }

        //
        public void reset(final String name) {
            this.t = System.currentTimeMillis();
            this.name = name;
        }
        
        //
        public String getName() {
            return name;
        }
        
        //
        public long elapsed() {
            final long time = System.currentTimeMillis() - t;
            return (time < 0) ? 0 : time;
        }
    }

    //
    static class Timer {
        private long n;
        private long min;
        private long max;
        private long sum;

        public Timer() {
            reset();
        }

        //
        public void add(long t) {
            sum += t;
            min = Math.min(min, t);
            max = Math.max(max, t);
            n++;
        }

        //
        protected void reset() {
            n   = 0L;
            sum = 0L;
            min = Long.MAX_VALUE;
            max = Long.MIN_VALUE;
        }


        public long min() {
            return min == Long.MAX_VALUE ? 0 : min;
        }

        //
        public long max() {
            return max == Long.MIN_VALUE ? 0 : max;
        }

        //
        public long avg() {
            return (n == 0) ? 0 : (sum / n);
        }
        
        //
        public long n() {
            return n;
        }
        
        @Override
        public String toString() {
            return String.format("{ n: %d, min: %d, max: %d, avg: %d }", n(), min(), max(), avg()); 
        }
    }

    //
    static class Concurrency {
        private long activeRequests;
        
        public synchronized void inc() {
            activeRequests++;
        }
        
        public synchronized void dec() {
            activeRequests--;
        }
        
        public synchronized long getActiveRequests() {
            return activeRequests;
        }
        
    }
    
}
