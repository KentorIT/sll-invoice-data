package se.sll.paymentresponsible.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class SimpleXMLElementParser {
    private String file;

    public static interface ElementMatcherCallback {
        void begin();
        void match(int code, String data);
        void end();
    }

    public SimpleXMLElementParser(String file) {
        this.file = file;
    }

    public void parse(String enclosingElementName, Map<String, Integer> nameCodeMap, ElementMatcherCallback matcher) {
        try {
            parse0(enclosingElementName, nameCodeMap, matcher);
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse0(String enclosingName, Map<String, Integer> nameCodeMap, ElementMatcherCallback matcher) throws FileNotFoundException, XMLStreamException {
        final XMLInputFactory factory = XMLInputFactory.newInstance();
        final XMLEventReader r = factory.createXMLEventReader(new FileReader(file));
        int code = 0;
        boolean match = false;
        while (r.hasNext()) {

            final XMLEvent e = r.nextEvent();

            switch (e.getEventType()) {
            case XMLEvent.START_ELEMENT:
                if (match) {
                    Integer i = nameCodeMap.get(e.asStartElement().getName().getLocalPart());
                    code = (i == null) ? Integer.MIN_VALUE : i.intValue();
                } else if (e.asStartElement().getName().getLocalPart().equals(enclosingName)) {
                    matcher.begin();
                    match = true;
                }
                break;
            case XMLEvent.CHARACTERS:
                if (match && code != Integer.MIN_VALUE) {
                    matcher.match(code, e.asCharacters().getData());
                }
                break;
            case XMLEvent.END_ELEMENT:
                if (match && e.asEndElement().getName().getLocalPart().equals(enclosingName)) {
                    match = false;
                    matcher.end();
                }
                code = 0;
                break;
            }
        }
        r.close();
    }
}
