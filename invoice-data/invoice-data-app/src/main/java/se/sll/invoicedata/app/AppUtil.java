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

package se.sll.invoicedata.app;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.WordUtils;

public class AppUtil {

    static HashSet<String> stopMethodSet = new HashSet<String>();
    static Map<Class<?>, List<Field>> fieldCacheMap = Collections.synchronizedMap(new HashMap<Class<?>, List<Field>>());

    private static final DatatypeFactory datatypeFactory;
    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Init Error!", e);
        }
    }

    //
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        if (date == null) {
            return null;
        }
        final GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(date);
        return datatypeFactory.newXMLGregorianCalendar(gCal);
    }

    public static Date toDate(XMLGregorianCalendar cal) {
        return cal.toGregorianCalendar().getTime();
    }

    //
    public static <T> T copyFields(T to, Object from, Class<?> spec) {

        List<Field> list = fieldCacheMap.get(spec);
        if (list == null) {
            list = allFields(spec);
            fieldCacheMap.put(spec, list);
        }
        
        for (final Field field: list) {
            copyField(to, from, field);
        }
        
        return to;
    }

    //
    private static Method getMethodByName(Class<?> clazz, String name, Class<?> type) {
        final String key = clazz.getName() + "." + name;
        if (!stopMethodSet.contains(key)) {
            try {
                return (type == null) ? clazz.getMethod(name) : clazz.getMethod(name, type);
            } catch (NoSuchMethodException e) {
                stopMethodSet.add(key);
            }
        }
        return null;
    }

    //
    private static Method getGetMethod(Class<?> clazz, Field field) {
        final String name = "get" + WordUtils.capitalize(field.getName());
        return getMethodByName(clazz, name, null);

    }

    //
    private static Method getSetMethod(Class<?> clazz, Field field, Class<?> type) throws SecurityException {        
        final String name = "set" + WordUtils.capitalize(field.getName());
        return getMethodByName(clazz, name, type);
    }

    //
    private static void copyField(Object to, Object from, Field field) {
        try {
            final Method getMethod = getGetMethod(from.getClass(), field);
            if (getMethod == null) {
                return;
            }
            Object value = getMethod.invoke(from);
            Class<?> type = field.getType();

            //
            // special handling of generic type conversion between externally published XMLJAXB types 
            // and Internal data-types, today it's about dates only
            //
            if (!to.getClass().equals(from.getClass())) {
                if (value instanceof Date) {
                    value = toXMLGregorianCalendar((Date)value);
                    type = XMLGregorianCalendar.class;
                } else if (value instanceof XMLGregorianCalendar) {
                    value = toDate((XMLGregorianCalendar)value);
                    type = Date.class;
                }
            }
            
            final Method setMethod = getSetMethod(to.getClass(), field, type);
            if (setMethod != null) {
                setMethod.invoke(to, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to copy object field: " + field, e);
        }
    }


    //
    private static List<Field> allFields(Class<?> clazz) {
        final List<Field> list = new ArrayList<Field>();
        while (clazz != null && clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                final int mod = f.getModifiers();
                if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod) && !Modifier.isNative(mod) && !f.getType().equals(List.class)) {
                    list.add(f);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }
}
