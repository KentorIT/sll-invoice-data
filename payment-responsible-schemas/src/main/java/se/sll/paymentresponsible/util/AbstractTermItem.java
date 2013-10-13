package se.sll.paymentresponsible.util;

import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 
 * @author Peter
 *
 */
public class AbstractTermItem implements Comparable<AbstractTermItem> {
    private static final Date MAX_AGE;
    static { 
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        MAX_AGE = cal.getTime();
    }

    static Date MAX_DATE = new Date(Long.MAX_VALUE);
    static Date MIN_DATE = new Date(0L);

    static DatatypeFactory datatypeFactory;
    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private String id;
    private String name;
    private Date validFrom = MIN_DATE;
    private Date validTo = MAX_DATE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = (validFrom == null) ? MIN_DATE :validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = (validTo == null) ? MAX_DATE : validTo;
    }
    
    public boolean isValid() {
        return getValidTo().after(MAX_AGE);
    }
    
    static Date toDate(String text) {
        final XMLGregorianCalendar cal = datatypeFactory.newXMLGregorianCalendar(text);
        return cal.toGregorianCalendar().getTime();    
    }

    @Override
    public int compareTo(AbstractTermItem other) {
        return getValidFrom().compareTo(other.getValidFrom());
    }
}
