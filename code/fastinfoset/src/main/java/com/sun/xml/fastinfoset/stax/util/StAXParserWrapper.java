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

package com.sun.xml.fastinfoset.stax.util;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;


public class StAXParserWrapper implements XMLStreamReader{
    private XMLStreamReader _reader;
    
    /** Creates a new instance of StAXParserWrapper */
    public StAXParserWrapper() {
    }

    public StAXParserWrapper(XMLStreamReader reader) {
        _reader = reader;
    }
    public void setReader(XMLStreamReader reader) {
        _reader = reader;
    }
    public XMLStreamReader getReader() {
        return _reader;
    }

    public int next() throws XMLStreamException 
    {
        return _reader.next();
    }

    public int nextTag() throws XMLStreamException 
    {
        return _reader.nextTag();
    }

    public String getElementText() throws XMLStreamException 
    {
        return _reader.getElementText();
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException
    {
        _reader.require(type,namespaceURI,localName);
    }

    public boolean hasNext() throws XMLStreamException
    {
        return _reader.hasNext();
    }

    public void close() throws XMLStreamException
    {
        _reader.close();
    }

    public String getNamespaceURI(String prefix) 
    {
        return _reader.getNamespaceURI(prefix);
    }

    public NamespaceContext getNamespaceContext() {
        return _reader.getNamespaceContext();
    }

    public boolean isStartElement() {
        return _reader.isStartElement();
    }

    public boolean isEndElement() {
        return _reader.isEndElement();
    }

    public boolean isCharacters() {
    return _reader.isCharacters();
    }

    public boolean isWhiteSpace() {
        return _reader.isWhiteSpace();
    }

    public QName getAttributeName(int index) {
        return _reader.getAttributeName(index);
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, 
                               int length) throws XMLStreamException 
    {
        return _reader.getTextCharacters(sourceStart, target, targetStart, length);
    }

    public String getAttributeValue(String namespaceUri,
                                  String localName) 
    {
        return _reader.getAttributeValue(namespaceUri,localName);
    }
    public int getAttributeCount() {
        return _reader.getAttributeCount();
    }
    public String getAttributePrefix(int index) {
        return _reader.getAttributePrefix(index);
    }
    public String getAttributeNamespace(int index) {
        return _reader.getAttributeNamespace(index);
    }
    public String getAttributeLocalName(int index) {
        return _reader.getAttributeLocalName(index);
    }
    public String getAttributeType(int index) {
        return _reader.getAttributeType(index);
    }
    public String getAttributeValue(int index) {
        return _reader.getAttributeValue(index);
    }
    public boolean isAttributeSpecified(int index) {
        return _reader.isAttributeSpecified(index);
    }

    public int getNamespaceCount() {
        return _reader.getNamespaceCount();
    }
    public String getNamespacePrefix(int index) {
        return _reader.getNamespacePrefix(index);
    }
    public String getNamespaceURI(int index) {
        return _reader.getNamespaceURI(index);
    }

    public int getEventType() {
        return _reader.getEventType();
    }

    public String getText() {
        return _reader.getText();
    }

    public char[] getTextCharacters() {
        return _reader.getTextCharacters();
    }

    public int getTextStart() {
        return _reader.getTextStart();
    }

    public int getTextLength() {
        return _reader.getTextLength();
    }

    public String getEncoding() {
        return _reader.getEncoding();
    }

    public boolean hasText() {
        return _reader.hasText();
    }

    public Location getLocation() {
        return _reader.getLocation();
    }

    public QName getName() {
        return _reader.getName();
    }

    public String getLocalName() {
        return _reader.getLocalName();
    }

    public boolean hasName() {
        return _reader.hasName();
    }

    public String getNamespaceURI() {
        return _reader.getNamespaceURI();
    }

    public String getPrefix() {
        return _reader.getPrefix();
    }

    public String getVersion() {
        return _reader.getVersion();
    }

    public boolean isStandalone() {
        return _reader.isStandalone();
    }

    public boolean standaloneSet() {
        return _reader.standaloneSet();
    }

    public String getCharacterEncodingScheme() {
        return _reader.getCharacterEncodingScheme();
    }

    public String getPITarget() {
        return _reader.getPITarget();
    }

    public String getPIData() {
        return _reader.getPIData();
    }

    public Object getProperty(String name) {
        return _reader.getProperty(name);
    }    
}
