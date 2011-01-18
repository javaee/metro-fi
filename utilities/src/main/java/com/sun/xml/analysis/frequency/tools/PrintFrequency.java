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
package com.sun.xml.analysis.frequency.tools;

import com.sun.xml.analysis.frequency.*;
import com.sun.xml.fastinfoset.tools.PrintTable;
import com.sun.xml.fastinfoset.vocab.frequency.VocabularyGenerator;
import java.io.File;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * A simple class that prints out in the order of descreasing frequency
 * information items declared in a schema and occuring 0 or more times in 
 * a set of infosets.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public class PrintFrequency {
    
    /**
     * @param args the command line arguments. arg[0] is the path to a schema,
     * args[1] to args[n] are the paths to XML documents.
     */
    public static void main(String[] args) throws Exception {
        SchemaProcessor sp = new SchemaProcessor(new File(args[0]).toURL(), true, false);
        sp.process();
        
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser p = spf.newSAXParser();
        
        FrequencyHandler fh = new FrequencyHandler(sp);
        for (int i = 1; i < args.length; i++) {
            p.parse(new File(args[i]), fh);
        }
        fh.generateQNamesWithPrefix();
        
        FrequencyBasedLists l = fh.getLists();
        System.out.println("Prefixes");
        for (String s : l.prefixes) {
            System.out.println(s);
        }
        
        System.out.println("Local names: " + l.localNames.size());
        for (String s : l.localNames) {
            System.out.println(s);
        }
        
        int i = 0;
        System.out.println("Elements: " + l.elements.size());
        for (QName q : l.elements) {
            System.out.println(q.getPrefix() + " " + q + " " + (i++));
        }
        
        System.out.println("Attributes" + l.attributes.size());
        for (QName q : l.attributes) {
            System.out.println(q.getPrefix() + " " + q);
        }
    }
}
