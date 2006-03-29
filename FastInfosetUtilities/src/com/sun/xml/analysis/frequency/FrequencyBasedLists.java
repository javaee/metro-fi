/*
 * Fast Infoset ver. 0.1 software ("Software")
 * 
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved. 
 * 
 * Software is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations.
 * 
 *    Sun supports and benefits from the global community of open source
 * developers, and thanks the community for its important contributions and
 * open standards-based technology, which Sun has adopted into many of its
 * products.
 * 
 *    Please note that portions of Software may be provided with notices and
 * open source licenses from such communities and third parties that govern the
 * use of those portions, and any licenses granted hereunder do not alter any
 * rights and obligations you may have under such open source licenses,
 * however, the disclaimer of warranty and limitation of liability provisions
 * in this License will apply to all Software in this distribution.
 * 
 *    You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 */ 
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
    /**
     * List of text content values in the order of decreasing frequency
     */
    public final List<String> textContentValues;
    /**
     * List of attribute values in the order of decreasing frequency
     */
    public final List<String> attributeValues;
    
    
    FrequencyBasedLists(List<String> p, List<String> n, List<String> l, 
            List<QName> e, List<QName> a,
            List<String> tcv, List<String> av) {
        prefixes = p;
        namespaces = n;
        localNames = l;
        elements = e;
        attributes = a;
        textContentValues = tcv;
        attributeValues = av;
    }
}
