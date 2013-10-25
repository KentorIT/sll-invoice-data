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

import java.io.Serializable;

/**
 * Keeps track of elapsed time.
 * 
 * @author Peter
 */
public class Timer implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Number of requests.
     * @serial
     */
    private long n;
    /**
     * Min time in millis.
     * @serial
     */
    private long min;
    /**
     * Max time in millis.
     * @serial
     */
    private long max;
    /**
     * Total time in millis.
     * @serial
     */
    private long sum;
    /**
     * Name of this timer.
     * @serial
     */
    private String name;

    public Timer(String name) {
        this.name = name;
        reset();
    }
    
    //
    public String name() {
        return name;
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
        return (min == Long.MAX_VALUE) ? 0 : min;
    }

    //
    public long max() {
        return (max == Long.MIN_VALUE) ? 0 : max;
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
        return String.format("{ name: \"%s\", n: %d, min: %d, max: %d, avg: %d }", name(), n(), min(), max(), avg()); 
    }
}
