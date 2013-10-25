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
import java.util.Arrays;

/**
 * Keeps track of elapsed time for a given history of measurements.
 * 
 * @author Peter
 */
public class HistoryTimer extends Timer implements Serializable {
    private static final long serialVersionUID = 1L;
    private int len;
    private int ofs = 0;
    private long[] history;

    //
    public HistoryTimer(String name, int len) {
        super(name);
        this.len = len;
        this.history = new long[len];
        Arrays.fill(history, -1);
    }

    //
    public void add(long t) {
        if (ofs >= len) {
            ofs = 0;
        }
        history[ofs++] = t;
    }

    //
    public synchronized void recalc() {
        reset();
        for (int i = 0; i < len && history[i] >= 0; i++) {
            super.add(history[i]);
        }
    }
    
    @Override
    public synchronized String toString() {
        return String.format("{ name: \"%s\", history: %d, min: %d, max: %d, avg: %d }", name(), n(), min(), max(), avg()); 
    }
}
