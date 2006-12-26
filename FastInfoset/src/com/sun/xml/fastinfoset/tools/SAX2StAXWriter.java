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
import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAX2StAXWriter extends DefaultHandler implements LexicalHandler {
        
    /**
     * XML stream writer where events are pushed.
     */
    XMLStreamWriter _writer;
    
    /**
     * List of namespace decl for upcoming element.
     */
    ArrayList _namespaces = new ArrayList();
    
    public SAX2StAXWriter(XMLStreamWriter writer) {
        _writer = writer;
    }
    
    public XMLStreamWriter getWriter() {
        return _writer;
    }
    
    public void startDocument() throws SAXException {
        try {
            _writer.writeStartDocument();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    public void endDocument() throws SAXException {
        try {
            _writer.writeEndDocument();
            _writer.flush();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    public void characters(char[] ch, int start, int length) 
        throws SAXException 
    {
        try {
            _writer.writeCharacters(ch, start, length);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }    
    
    public void startElement(String namespaceURI, String localName, 
        String qName, Attributes atts) throws SAXException 
    {
        try {
            int k = qName.indexOf(':');
            String prefix = (k > 0) ? qName.substring(0, k) : "";
            _writer.writeStartElement(prefix, localName, namespaceURI);
            
            int length = _namespaces.size();
            for (int i = 0; i < length; i++) {
                QualifiedName nsh = (QualifiedName) _namespaces.get(i);
                _writer.writeNamespace(nsh.prefix, nsh.namespaceName);
            }
            _namespaces.clear();
            
            length = atts.getLength();
            for (int i = 0; i < length; i++) {
                _writer.writeAttribute(atts.getURI(i), 
                                       atts.getLocalName(i),
                                       atts.getValue(i));                                       
            }
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }                
    }
    
    public void endElement(String namespaceURI, String localName, 
        String qName) throws SAXException 
    {
        try {
            _writer.writeEndElement();
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
            throw new SAXException(e);
        }        
    }
    
    public void startPrefixMapping(String prefix, String uri) 
        throws SAXException 
    {
    // Commented as in StAX NS are declared for current element?
    // now _writer.setPrefix() is invoked in _writer.writeNamespace()
//        try {
//            _writer.setPrefix(prefix, uri);
            _namespaces.add(new QualifiedName(prefix, uri));
//        }
//        catch (XMLStreamException e) {
//            throw new SAXException(e);
//        }
    }
    
    public void endPrefixMapping(String prefix) throws SAXException {
    }
    
    public void ignorableWhitespace(char[] ch, int start, int length) 
        throws SAXException 
    {
        characters(ch, start, length);
    }
    
    public void processingInstruction(String target, String data) 
        throws SAXException 
    {
        try {
            _writer.writeProcessingInstruction(target, data);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }
    
    public void setDocumentLocator(Locator locator) {
    }
    
    public void skippedEntity(String name) throws SAXException {
    }
    
    public void comment(char[] ch, int start, int length) 
        throws SAXException 
    {
        try {
            _writer.writeComment(new String(ch, start, length));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }        
    }    
    
    public void endCDATA() throws SAXException {
    }
    
    public void endDTD() throws SAXException {
    }
    
    public void endEntity(String name) throws SAXException {
    }
    
    public void startCDATA() throws SAXException {
    }
    
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }
    
    public void startEntity(String name) throws SAXException {
    }
    
}

