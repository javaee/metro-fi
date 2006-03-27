package com.sun.xml.analysis.frequency;

import java.util.List;
import javax.xml.namespace.QName;

/**
 * A container for frequency based lists of values in the order of decreasing
 * frequency.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class FrequencyBasedLists {
    /**
     * List of prefixes in the order of decreasing frequency
     */
    public final List<String> prefixes;
    /**
     * List of namespaces in the order of decreasing frequency
     */
    public final List<String> namespaces;
    /**
     * List of local names in the order of decreasing frequency
     */
    public final List<String> localNames;
    /**
     * List of elements in the order of decreasing frequency
     */
    public final List<QName> elements;
    /**
     * List of attributes in the order of decreasing frequency
     */
    public final List<QName> attributes;
    
    FrequencyBasedLists(List<String> p, List<String> n, List<String> l, 
            List<QName> e, List<QName> a) {
        prefixes = p;
        namespaces = n;
        localNames = l;
        elements = e;
        attributes = a;
    }
}
