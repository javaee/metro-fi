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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import com.sun.xml.fastinfoset.CommonResourceBundle;

public class StAX2SAXReader {
    
    /**
     * Content handler where events are pushed.
     */
    ContentHandler _handler;
    
    /**
     * Lexical handler to report lexical events.
     */
    LexicalHandler _lexicalHandler;
    
    /**
     * XML stream reader where events are pulled.
     */
    XMLStreamReader _reader;
    
    public StAX2SAXReader(XMLStreamReader reader, ContentHandler handler) {
        _handler = handler;
        _reader = reader;
    }
    
    public StAX2SAXReader(XMLStreamReader reader) {
        _reader = reader;
    }

    public void setContentHandler(ContentHandler handler) {
        _handler = handler;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        _lexicalHandler = lexicalHandler;
    }
        
    public void adapt() throws XMLStreamException, SAXException {
        QName qname;
        String prefix, localPart;
        AttributesImpl attrs = new AttributesImpl();
        char[] buffer;
        int nsc;
        int nat;
        
        _handler.startDocument();
        
        try {
            
            while (_reader.hasNext()) {
                int event = _reader.next();


                switch(event) {
                case  XMLStreamConstants.START_ELEMENT: {
                    // Report namespace events first
                    nsc = _reader.getNamespaceCount();
                    for (int i = 0; i < nsc; i++) {
                        _handler.startPrefixMapping(_reader.getNamespacePrefix(i), 
                            _reader.getNamespaceURI(i));
                    }

                    // Collect list of attributes
                    attrs.clear();
                    nat = _reader.getAttributeCount();
                    for (int i = 0; i < nat; i++) {
                        QName q = _reader.getAttributeName(i);
                        String qName = _reader.getAttributePrefix(i);
                        if (qName == null || qName == "") {
                            qName = q.getLocalPart();
                        } else {
                            qName = qName + ":" +  q.getLocalPart();
                        }
                        attrs.addAttribute(_reader.getAttributeNamespace(i),
                                           q.getLocalPart(),
                                           qName,
                                           _reader.getAttributeType(i), 
                                           _reader.getAttributeValue(i));
                    }

                    // Report start element
                    qname = _reader.getName();
                    prefix = qname.getPrefix();
                    localPart = qname.getLocalPart();

                    _handler.startElement(_reader.getNamespaceURI(),
                                          localPart, 
                                          (prefix.length() > 0) ? 
                                              (prefix + ":" + localPart) : localPart, 
                                          attrs);
                    break;
                }
                case  XMLStreamConstants.END_ELEMENT: {
                    // Report end element
                    qname = _reader.getName();
                    prefix = qname.getPrefix();
                    localPart = qname.getLocalPart();

                    _handler.endElement(_reader.getNamespaceURI(),
                                        localPart, 
                                        (prefix.length() > 0) ?
                                            (prefix + ":" + localPart) : localPart); 

                    // Report end namespace events
                    nsc = _reader.getNamespaceCount();
                    for (int i = 0; i < nsc; i++) {
                        _handler.endPrefixMapping(_reader.getNamespacePrefix(i));
                    }
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                    _handler.characters(_reader.getTextCharacters(), _reader.getTextStart(), _reader.getTextLength());
                    break;
                case XMLStreamConstants.COMMENT:
                    _lexicalHandler.comment(_reader.getTextCharacters(), _reader.getTextStart(), _reader.getTextLength());
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    _handler.processingInstruction(_reader.getPITarget(), _reader.getPIData());
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    break;
                default:
                    throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.StAX2SAXReader", new Object[]{Integer.valueOf(event)}));
                } // switch
            }
        }
        catch (XMLStreamException e) {
            _handler.endDocument();     // flush whatever we have
            throw e;
        }
        
        _handler.endDocument();
    }

}

