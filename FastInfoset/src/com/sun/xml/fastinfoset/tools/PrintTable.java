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


package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.NameSurrogate;
import com.sun.xml.fastinfoset.QualifiedName;
import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.sun.xml.fastinfoset.sax.VocabularyGenerator;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.NameSurrogateIntMap;
import com.sun.xml.fastinfoset.util.StringIntMap;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class PrintTable {
    
    /** Creates a new instance of PrintTable */
    public PrintTable() {
    }
    
    public static void printVocabulary(SerializerVocabulary vocabulary) {
        printMap("Attribute Name Table", vocabulary.attributeName);
        printMap("Attribute Value Table", vocabulary.attributeValue);
        printMap("Character Content Chunk Table", vocabulary.characterContentChunk);
        printMap("Element Name Table", vocabulary.elementName);
        printMap("Local Name Table", vocabulary.localName);
        printMap("Namespace Name Table", vocabulary.namespaceName);
        printMap("Other NCName Table", vocabulary.otherNCName);
        printMap("Other String Table", vocabulary.otherString);
        printMap("Other URI Table", vocabulary.otherURI);
        printMap("Prefix Table", vocabulary.prefix);
    }
    
    public static void printMap(String title, StringIntMap m) {
        System.out.println(title);

        String[] keys = m.getKeys();
        for (int i = 0; i < m.getKeysSize(); i++) {
            System.out.println("" + (i + 1) + ": " + keys[i]);
        }        
    }

    public static void printMap(String title, LocalNameQualifiedNamesMap m) {
        System.out.println(title);

        List names = new ArrayList();
        
        QualifiedName[] entries = m.getQualifiedNames();
        for (int i = 0; i < entries.length; i++) {
            names.add(entries[i]);
        }
        
        Collections.sort(names, new Comparator() {
                public int compare(Object o1, Object o2) {
                    QualifiedName n1 = (QualifiedName)o1;
                    QualifiedName n2 = (QualifiedName)o2;
                    return n1.index - n2.index;
                }
            }
        );
        
        Iterator i = names.iterator();
        while (i.hasNext()) {
            QualifiedName name = (QualifiedName)i.next();
            System.out.println("" + (name.index + 1) + ": " + 
                    "{" + name.namespaceName + "}" + 
                    name.prefix + ":" + name.localName);
        }
    }
    
    public static void printMap(String title, CharArrayIntMap m) {
        System.out.println(title);

        CharArray[] keys = m.getKeys();
        for (int i = 0; i < m.getKeysSize(); i++) {
            System.out.println("" + (i + 1) + ": \"" + keys[i] + "\"");
        }        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);

            SAXParser saxParser = saxParserFactory.newSAXParser();

            SerializerVocabulary referencedVocabulary = new SerializerVocabulary(true);
        
            VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(referencedVocabulary);
            File f = new File(args[0]);
            saxParser.parse(f, vocabularyGenerator);
                        
            printVocabulary(referencedVocabulary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
