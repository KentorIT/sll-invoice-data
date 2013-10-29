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
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

/**
 * Transformation to and from JAXB Beans, Entities and related data types.
 * 
 * @author Peter
 *
 */
public class CoreUtil {
    
    // dozer mapper singleton instance
    private static Mapper mapper = new DozerBeanMapper();
    
    /** Minimum date value. */
    public static Date MIN_DATE = new Date(0L);
    
    /** Maximum date value. */
    public static Date MAX_DATE = new Date(Long.MAX_VALUE);
     
    private static final DatatypeFactory datatypeFactory;
    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 9999);
            //Overwriting max date: SQL Server only accepts 9999 as max date
            MAX_DATE = calendar.getTime();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Init Error!", e);
        }
    }
    
    /**
     * Copies properties/state from one object to another. <p>
     * 
     * Maps by name to a non-static and non-final 
     * field of a given specification class then they are copied from a source to a target instance, 
     * i.e. the actual names must be identical. <br>
     *
     * Fields of type {@link List} are ignored. <p>
     * 
     * The purpose of this method is primarily to transfer state between JAXB XML objects 
     * to and from JPA entities, and therefore are typical data type conversions carried out. 
     * Lists are not traversed, i.e. it's about shallow copies only. 
     * 
     * @param source the object instance to copy state from.
     * @param targetSpec the class specifying fields to be copied,
     * @return the target object (copy).
     */
    public static <T> T copyProperties(Object source, Class<T> targetSpec) {
        return mapper.map(source, targetSpec);
    }
    
         
    /**
     * Copies lists items.
     * 
     * @param target the target list to add copies to.
     * @param source the source list.
     * @param targetSpec the target type
     * @return the target list.
     */
    public static <T, F> List<T> copyGenericLists(List<T> target, List<F> source, Class<T> targetSpec) {
        for (final F item : source) {
            target.add(copyProperties(item, targetSpec));
        }
        return target;
    }
    
    /**
     * Returns a {@link Date} date and time representation.
     * 
     * @param cal the actual date and time.
     * @return the {@link Date} representation.
     */
    public static Date toDate(XMLGregorianCalendar cal) {
        return toDate(cal, null);
    }
    

    /**
     * Returns a {@link Date} date and time representation.
     * 
     * @param cal the actual date and time.
     * @param defaultValue the default value if cal is null.
     * @return the {@link Date} representation.
     */
    public static Date toDate(XMLGregorianCalendar cal, Date defaultValue) {
    	if (cal != null) {
    		Calendar c = Calendar.getInstance();
    		c.set(Calendar.DATE, cal.getDay());
    		c.set(Calendar.MONTH, cal.getMonth() - 1);
    		c.set(Calendar.YEAR, cal.getYear());
    		return c.getTime();
    	}
        return defaultValue;
    }

    
    /**
     * Returns a {@link XMLGregorianCalendar} date and time representation.
     * 
     * @param date the actual date and time.
     * @return the {@link XMLGregorianCalendar} representation.
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        return toXMLGregorianCalendar(date, null);
    }

    /**
     * Returns a {@link XMLGregorianCalendar} date and time representation.
     * 
     * @param date the actual date and time.
     * @param defaultValue the default value if date is null.
     * 
     * @return the {@link XMLGregorianCalendar} representation.
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date, XMLGregorianCalendar defaultValue) {
        if (date == null) {
            return defaultValue;
        }
        final GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(date);
        return datatypeFactory.newXMLGregorianCalendar(gCal);
    }
      
    /**
     * Returns is the string is empty, i.e. null or an empty (zero length) string.
     * 
     * @param data the string.
     * @return true if empty, otherwise false.
     */
    public static boolean isEmpty(String data) {
    	return (data == null) || data.isEmpty();  
    }
    
}
