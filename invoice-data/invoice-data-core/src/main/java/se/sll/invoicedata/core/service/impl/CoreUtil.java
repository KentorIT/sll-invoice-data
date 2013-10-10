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
    
    private static final DatatypeFactory datatypeFactory;
    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
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
        return (cal == null) ? null : cal.toGregorianCalendar().getTime();
    }
    
    /**
     * Returns a {@link XMLGregorianCalendar} date and time representation.
     * 
     * @param date the actual date and time.
     * @return the {@link XMLGregorianCalendar} representation.
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        if (date == null) {
            return null;
        }
        final GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(date);
        return datatypeFactory.newXMLGregorianCalendar(gCal);
    }
    
    public static XMLGregorianCalendar getStartDate() {
    	Calendar cal = Calendar.getInstance();
    	cal.set( Calendar.YEAR, 1970 );
    	cal.set( Calendar.MONTH, Calendar.JANUARY );
    	cal.set( Calendar.DATE, 1 );
    	
    	return toXMLGregorianCalendar(cal.getTime());
    }
    
    public static XMLGregorianCalendar getEndDate() {
    	Calendar cal = Calendar.getInstance();
    	cal.set( Calendar.DATE, cal.get(Calendar.DATE) + 1);    	
    	return toXMLGregorianCalendar(cal.getTime());
    }
    
}
