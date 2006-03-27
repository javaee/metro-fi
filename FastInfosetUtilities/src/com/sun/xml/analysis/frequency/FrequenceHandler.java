package com.sun.xml.analysis.frequency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX-based handler to collect the frequence of occurences of properties
 * of information items in one or more infosets.
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public class FrequenceHandler extends DefaultHandler {
    private Map<String, Set<QName>> namespacesToElements = new HashMap();
    private Map<String, Set<QName>> namespacesToAttributes = new HashMap();
    
    private FrequencySet<String> prefixes = new FrequencySet<String>();
    private FrequencySet<String> namespaces = new FrequencySet<String>();
    private FrequencySet<String> localNames = new FrequencySet<String>();
    private FrequencySet<QName> elements = new FrequencySet<QName>();
    private FrequencySet<QName> attributes = new FrequencySet<QName>();

    /**
     * The default frequency handler.
     */
    public FrequenceHandler() {
    }

    /**
     * A frequency handler initiated with information generated from a
     * {@link SchemaProcessor}.
     *
     * @param sp the schema processor.
     */
    public FrequenceHandler(SchemaProcessor sp) {
        for (String s : sp.localNames) {
            localNames.add0(s);
        }
        for (String s : sp.namespaces) {
            namespaces.add0(s);
        }
        
        bucketQNamesToNamespace(sp.elements, namespacesToElements);
        bucketQNamesToNamespace(sp.attributes, namespacesToAttributes);
    }

    /**
     * Get the frequency based lists of properties of information items.
     *
     * @return the frequency based lists.
     */
    public FrequencyBasedLists getLists() {
        return  new FrequencyBasedLists(
            prefixes.createFrequencyBasedList(),
            namespaces.createFrequencyBasedList(),
            localNames.createFrequencyBasedList(),
            elements.createFrequencyBasedList(),
            attributes.createFrequencyBasedList());
    }
    
    private void bucketQNamesToNamespace(Set<QName> s, Map<String, Set<QName>> m) {
        for (QName q : s) {
            Set<QName> subs = m.get(q.getNamespaceURI());
            if (subs == null) {
                subs = new HashSet();
                m.put(q.getNamespaceURI(), subs);
            }
            subs.add(q);
        }
    }
    
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        prefixes.add(prefix);
        namespaces.add(uri);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        QName q = createQName(uri, localName, qName);
        addQName(q, namespacesToElements, elements);
        
        for (int i = 0; i < atts.getLength(); i++) {
            q = createQName(atts.getURI(i), atts.getLocalName(i), atts.getQName(i));
            addQName(q, namespacesToAttributes, attributes);
        }
    }
    
    private void addQName(QName q, Map<String, Set<QName>> m, FrequencySet<QName> fhm) {
        Set<QName> s = m.get(q.getNamespaceURI());
        if (s == null) {
            fhm.add(q);
        } else {
            // Remove the set of qNames for the namespace
            m.put(q.getNamespaceURI(), null);
            
            String prefix = q.getPrefix();
            if (prefix.length() == 0) {
                // If 'q' is in the set then remove it
                if (s.contains(q)) {
                    s.remove(q);
                }
                fhm.add(q);
                
                // Add each qName in the set to the frequence map
                for (QName qInSet : s) {
                    fhm.add0(qInSet);
                }
            } else {
                // Remove the prefix from the qName
                QName qWithoutPrefix = new QName(q.getNamespaceURI(), q.getLocalPart());
                
                // If 'qWithoutPrefix' is not in the set then 
                // add 'q' to the frequency map
                if (s.contains(qWithoutPrefix)) {
                    s.remove(qWithoutPrefix);
                }
                fhm.add(q);
                
                // Add each qName in the set to the frequence map
                // include the prefix of 'q'
                for (QName qInSet : s) {
                    fhm.add0(new QName(qInSet.getNamespaceURI(), qInSet.getLocalPart(), prefix));
                }
            }
        }
    }
    
    private QName createQName(String uri, String localName, String qName) {
        if (uri != "") {
            final int i = qName.indexOf(':');
            final String prefix = (i != -1) ? qName.substring(0, i) : "";
            return new QName(uri, localName, prefix);
        } else {
            return new QName(localName);
        }
    }
}
