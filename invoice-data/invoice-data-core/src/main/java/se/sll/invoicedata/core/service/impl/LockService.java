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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class LockService {
    
    private Set<String> locks = new HashSet<String>();

    /**
     * Acquires a lock for name.
     * 
     * @param name the unique resource name to lock.
     * @return true if a lock has been acquired, otherwise false.
     */
    public synchronized boolean acquire(final String name) {
        return locks.add(name);
    }

    /**
     * Acquires a lock for names.
     * 
     * @param names the unique resource names to lock.
     * @return true if a lock has been acquired for all, otherwise false.
     */
    public synchronized boolean acquire(final List<String> names) {
        for (final String name : names) {
            if (locks.contains(name)) {
                return false;
            }
        }
        return locks.addAll(names);
    }

    
    /**
     * Releases a lock
     * 
     * @param name the unique resource name of the lock to remove.
     */
    public synchronized void release(final String name) {
        locks.remove(name);
    }

    /**
     * Releases locks
     * 
     * @param names the unique resource names of the locks to remove.
     */
    public synchronized void release(final List<String> names) {
        locks.removeAll(names);
    }

}
