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


package com.sun.xml.fastinfoset.sax;

import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.DuplicateAttributeVerifier;
import com.sun.xml.fastinfoset.util.KeyIntMap;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.PrefixArray;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.StringArray;
import com.sun.xml.fastinfoset.util.StringIntMap;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class VocabularyGenerator extends DefaultHandler implements LexicalHandler {
    
    protected SerializerVocabulary _serializerVocabulary;
    protected ParserVocabulary _parserVocabulary;
    
    /** Creates a new instance of VocabularyGenerator */
    public VocabularyGenerator(SerializerVocabulary serializerVocabulary) {
        _serializerVocabulary = serializerVocabulary;
        _parserVocabulary = new ParserVocabulary();
    }

    public VocabularyGenerator(ParserVocabulary parserVocabulary) {
        _serializerVocabulary = new SerializerVocabulary();
        _parserVocabulary = parserVocabulary;
    }
    
    /** Creates a new instance of VocabularyGenerator */
    public VocabularyGenerator(SerializerVocabulary serializerVocabulary, ParserVocabulary parserVocabulary) {
        _serializerVocabulary = serializerVocabulary;
        _parserVocabulary = parserVocabulary;
    }
    
    // ContentHandler
    
    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }
    
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        addToTable(prefix, _serializerVocabulary.prefix, _parserVocabulary.prefix);
        addToTable(uri, _serializerVocabulary.namespaceName, _parserVocabulary.namespaceName);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        addToNameTable(namespaceURI, qName, localName, _serializerVocabulary.elementName, _parserVocabulary.elementName, false);
        
        for (int a = 0; a < atts.getLength(); a++) {            
            addToNameTable(atts.getURI(a), atts.getQName(a), atts.getLocalName(a), _serializerVocabulary.attributeName, _parserVocabulary.attributeName, true);
        
            String value = atts.getValue(a);
            if (value.length() < _serializerVocabulary.attributeValueSizeConstraint) {
                addToTable(value, _serializerVocabulary.attributeValue, _parserVocabulary.attributeValue);
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }
        
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (length < _serializerVocabulary.characterContentChunkSizeContraint) {
            addToCharArrayTable(new CharArray(ch, start, length, true));
        }
    }    

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }
    
    public void processingInstruction(String target, String data) throws SAXException {
    }
    
    public void setDocumentLocator(org.xml.sax.Locator locator) {
    }
    
    public void skippedEntity(String name) throws SAXException {
    }
    
       

    // LexicalHandler
    
    public void comment(char[] ch, int start, int length) throws SAXException {
    }
  
    public void startCDATA() throws SAXException {
    }
  
    public void endCDATA() throws SAXException {
    }
    
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    public void endDTD() throws SAXException {
    }
    
    public void startEntity(String name) throws SAXException {
    }

    public void endEntity(String name) throws SAXException {
    }

    
    public void addToTable(String s, StringIntMap m, StringArray a) {
        if (s == "") {
            return;
        }
        
        if (m.obtainIndex(s) == KeyIntMap.NOT_PRESENT) {
            a.add(s);
        }
    }

    public void addToTable(String s, StringIntMap m, PrefixArray a) {
        if (s == "") {
            return;
        }
        
        if (m.obtainIndex(s) == KeyIntMap.NOT_PRESENT) {
            a.add(s);
        }
    }
    
    public void addToCharArrayTable(CharArray c) {
        if (_serializerVocabulary.characterContentChunk.obtainIndex(c.ch, c.start, c.length, false) == KeyIntMap.NOT_PRESENT) {
            _parserVocabulary.characterContentChunk.add(c.ch, c.length);
        }        
    }

    public void addToNameTable(String namespaceURI, String qName, String localName, 
            LocalNameQualifiedNamesMap m, QualifiedNameArray a,
            boolean isAttribute) throws SAXException {        
        LocalNameQualifiedNamesMap.Entry entry = m.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                if ((namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                    return;
                }
            }                
        } 
        
        String prefix = getPrefixFromQualifiedName(qName);
        
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        int localNameIndex = -1;
        if (namespaceURI != "") {
            namespaceURIIndex = _serializerVocabulary.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == KeyIntMap.NOT_PRESENT) {
                throw new SAXException("namespace URI of local name not indexed");
            }
            
            if (prefix != "") {
                prefixIndex = _serializerVocabulary.prefix.get(prefix);
                if (prefixIndex == KeyIntMap.NOT_PRESENT) {
                    throw new SAXException("prefix of local name not indexed");
                }
            }
        }
        
        localNameIndex = _serializerVocabulary.localName.obtainIndex(localName);
        if (localNameIndex == KeyIntMap.NOT_PRESENT) {
            _parserVocabulary.localName.add(localName);
            localNameIndex = _parserVocabulary.localName.getSize() - 1;
        }
        QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, m.getNextIndex(), prefixIndex, namespaceURIIndex, localNameIndex);
        if (isAttribute) {
            name.createAttributeValues(DuplicateAttributeVerifier.MAP_SIZE);
        }
        entry.addQualifiedName(name);
        a.add(name);
    }
        
    public static String getPrefixFromQualifiedName(String qName) {
        int i = qName.indexOf(':');
        String prefix = "";
        if (i != -1) {
            prefix = qName.substring(0, i);
        }
        return prefix;
    }

}
