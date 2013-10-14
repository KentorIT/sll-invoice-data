package se.sll.paymentresponsible.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * 
 * @author Peter
 *
 */
public class CodeServiceXMLParser {

    static final Date ONE_YEAR_BACK;
    static { 
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        ONE_YEAR_BACK = cal.getTime();
    }


    private static final String CODE_PREFIX = "c:";
    private static final String ATTR_PREFIX = "a:";
    private static final String CODE = "code";
    private static final String CODESYSTEM = "codesystem";
    private static final String CODEDVALUE = "codedvalue";
    private static final String EXTERNALLINK = "externallink";
    private static final String TYPE = "type";
    private static final String ID = "id";
    private static final String TERMITEMENTRY = "termitementry";
    private static final String ATTRIBUTE = "attribute";

    final static XMLInputFactory factory = XMLInputFactory.newInstance();

    private XMLEventReader reader;
    private Set<String> extractFilter = new HashSet<String>();
    private Map<String, QName> names = new HashMap<String, QName>();
    private CodeServiceEntryCallback codeServiceEntryCallback;
    private Date newerThan = ONE_YEAR_BACK;

    public static interface CodeServiceEntryCallback {
        void onCodeServiceEntry(CodeServiceEntry codeServiceEntry);
    }


    public CodeServiceXMLParser(String file, CodeServiceEntryCallback codeServiceEntryCallback) {
        try {
            this.reader = factory.createXMLEventReader(new FileReader(file));
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new IllegalArgumentException(e);
        }
        this.codeServiceEntryCallback = codeServiceEntryCallback;
    }

    public Date getNewerThan() {
        return newerThan;
    }

    public void setNewerThan(Date maxAge) {
        this.newerThan = maxAge;
    }

    //
    public void extractAttribute(final String attribute) {
        extractFilter.add(ATTR_PREFIX + attribute);
    }

    //
    public void extractCode(final String code) {
        extractFilter.add(CODE_PREFIX + code);
    }

    //
    public void parse() {
        if (extractFilter.size() == 0) {
            throw new IllegalArgumentException("No attributes, nor code values have been defined");
        }
        try {
            parse0();
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    //
    private boolean same(StartElement startElement, String name) {
        return startElement.getName().getLocalPart().equals(name);
    }

    //
    private boolean same(EndElement endElement, String name) {
        return endElement.getName().getLocalPart().equals(name);
    }

    //
    private QName name(final String namespaceURI, final String localPart) {
        final String key = namespaceURI + localPart;
        QName qname = names.get(key);
        if (qname == null) {
            qname = new QName(namespaceURI, localPart);
            names.put(key, qname);
        }
        return qname;
    }

    //
    private String attribute(StartElement startElement, String attrName) {
        Attribute attr = startElement.getAttributeByName(name(startElement.getName().getNamespaceURI(), attrName));
        return (attr == null) ? null : attr.getValue();
    }

    private void parse0() throws FileNotFoundException, XMLStreamException {
        while (reader.hasNext()) {
            final XMLEvent e = reader.nextEvent();
            switch (e.getEventType()) {
            case XMLEvent.START_ELEMENT:
                if (same(e.asStartElement(), TERMITEMENTRY)) {
                    final CodeServiceEntry codeServiceEntry = processCodeServiceEntry(e.asStartElement());
                    codeServiceEntry.setValidFrom(AbstractTermItem.toDate(attribute(e.asStartElement(), "begindate")));
                    codeServiceEntry.setValidTo(AbstractTermItem.toDate(attribute(e.asStartElement(), "expirationdate")));
                    if (codeServiceEntry.isNewerThan(newerThan)) {
                        codeServiceEntryCallback.onCodeServiceEntry(codeServiceEntry);
                    }
                }
                break;
            }
        }
        reader.close();
    }


    //
    private CodeServiceEntry processCodeServiceEntry(StartElement startElement) throws XMLStreamException {
        CodeServiceEntry codeServiceEntry = new CodeServiceEntry();
        codeServiceEntry.setId(attribute(startElement, ID));
        while (reader.hasNext()) {
            final XMLEvent e = reader.nextEvent();
            switch (e.getEventType()) {
            case XMLEvent.START_ELEMENT:
                if (same(e.asStartElement(), ATTRIBUTE)) {
                    final String name = attribute(e.asStartElement(), TYPE);
                    if (extractFilter.contains(ATTR_PREFIX + name)) {
                        processAttributeValue(name, codeServiceEntry);
                    } else if (EXTERNALLINK.equals(name)) {
                        processCodeValue(codeServiceEntry);
                    }
                }
                break;
            case XMLEvent.END_ELEMENT:
                if (same(e.asEndElement(), TERMITEMENTRY)) {
                    return codeServiceEntry;
                }
            }
        }
        return null;
    }

    private void processAttributeValue(String name,
            CodeServiceEntry codeServiceEntry) throws XMLStreamException {
        while (reader.hasNext()) {
            final XMLEvent e = reader.nextEvent();
            switch (e.getEventType()) {
            case XMLEvent.CHARACTERS:
                codeServiceEntry.setAttribute(name, e.asCharacters().getData());
                break;
            case XMLEvent.END_ELEMENT:
                if (same(e.asEndElement(), ATTRIBUTE)) {
                    return;
                }
                break;
            }
        }
    }


    //
    private void processCodeValue(CodeServiceEntry codeServiceEntry) throws XMLStreamException {
        while (reader.hasNext()) {
            final XMLEvent e = reader.nextEvent();
            switch (e.getEventType()) {
            case XMLEvent.START_ELEMENT:
                if (same(e.asStartElement(), CODEDVALUE)) {
                    final String codeSystem = attribute(e.asStartElement(), CODESYSTEM);
                    if (extractFilter.contains(CODE_PREFIX + codeSystem)) {
                        codeServiceEntry.addCode(codeSystem, attribute(e.asStartElement(), CODE));
                    }
                }
                break;
            case XMLEvent.END_ELEMENT:
                if (same(e.asEndElement(), CODEDVALUE)) {
                    return;
                } else if (same(e.asEndElement(), ATTRIBUTE)) {
                    return;
                }
                break;
            }
        }
    }

}
