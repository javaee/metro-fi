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

import com.sun.xml.fastinfoset.QualifiedName;
import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.sun.xml.fastinfoset.tools.VocabularyGenerator;
import com.sun.xml.fastinfoset.util.CharArrayArray;
import com.sun.xml.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.fastinfoset.util.PrefixArray;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.StringArray;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;


public class PrintTable {
    
    /** Creates a new instance of PrintTable */
    public PrintTable() {
    }
    
    public static void printVocabulary(ParserVocabulary vocabulary) {
        printArray("Attribute Name Table", vocabulary.attributeName);
        printArray("Attribute Value Table", vocabulary.attributeValue);
        printArray("Character Content Chunk Table", vocabulary.characterContentChunk);
        printArray("Element Name Table", vocabulary.elementName);
        printArray("Local Name Table", vocabulary.localName);
        printArray("Namespace Name Table", vocabulary.namespaceName);
        printArray("Other NCName Table", vocabulary.otherNCName);
        printArray("Other String Table", vocabulary.otherString);
        printArray("Other URI Table", vocabulary.otherURI);
        printArray("Prefix Table", vocabulary.prefix);
    }
    
    public static void printArray(String title, StringArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }        
    }

    public static void printArray(String title, PrefixArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }        
    }
    
    public static void printArray(String title, CharArrayArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }        
    }

    public static void printArray(String title, ContiguousCharArrayArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            System.out.println("" + (i + 1) + ": " + a.getString(i));
        }        
    }

    public static void printArray(String title, QualifiedNameArray a) {
        System.out.println(title);

        for (int i = 0; i < a.getSize(); i++) {
            QualifiedName name = a.getArray()[i];
            System.out.println("" + (name.index + 1) + ": " + 
                    "{" + name.namespaceName + "}" + 
                    name.prefix + ":" + name.localName);
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

            ParserVocabulary referencedVocabulary = new ParserVocabulary();
        
            VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(referencedVocabulary);
            File f = new File(args[0]);
            saxParser.parse(f, vocabularyGenerator);
                        
            printVocabulary(referencedVocabulary);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
