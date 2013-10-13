package se.sll.paymentresponsible.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author Peter
 *
 */
public class CodeServiceEntry extends AbstractTermItem {
    private Map<String, String> attributes = new HashMap<String, String>();
    private Map<String, List<String>> codes = new HashMap<String, List<String>>();

    public void setAttribute(String name, String value) {
        attributes.put(name, value);
    }

    public void addCode(String name, String value) {
        List<String> l = codes.get(name);
        if (l == null) {
            l = new ArrayList<String>();
            codes.put(name, l);
        }
        l.add(value);
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public List<String> getCodes(String name) {
        return codes.get(name);
    }
}