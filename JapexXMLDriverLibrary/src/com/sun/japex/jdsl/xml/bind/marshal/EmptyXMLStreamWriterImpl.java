/*
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

package com.sun.japex.jdsl.xml.bind.marshal;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;


public class EmptyXMLStreamWriterImpl implements javax.xml.stream.XMLStreamWriter {
    private NamespaceContext namespaceContext = null;
    
    public EmptyXMLStreamWriterImpl () {
    }
    
    public void close () throws XMLStreamException {
    }
    
    public void flush () throws XMLStreamException {
    }
    
    public javax.xml.namespace.NamespaceContext getNamespaceContext () {
        return namespaceContext;
    }
    
    public String getPrefix (String namespaceURI) throws XMLStreamException {
        String prefix = null;
        if(this.namespaceContext != null){
            prefix = namespaceContext.getPrefix (namespaceURI);
        }
        return prefix;
    }
    
    public Object getProperty (String str) throws IllegalArgumentException {
        throw new UnsupportedOperationException ();
    }
    
    public void setDefaultNamespace (String str) throws XMLStreamException {
    }
    
    public void setNamespaceContext (javax.xml.namespace.NamespaceContext namespaceContext) throws XMLStreamException {
        this.namespaceContext = namespaceContext;
    }
    
    public void setPrefix (String str, String str1) throws XMLStreamException {
    }
    
    public void writeAttribute (String localName, String value) throws XMLStreamException {
    }
    
    public void writeAttribute (String namespaceURI,String localName,String value)throws XMLStreamException {
    }
    
    public void writeAttribute (String prefix,String namespaceURI,String localName,String value)throws XMLStreamException {        
    }
    
    public void writeCData (String data) throws XMLStreamException {        
    }
    
    public void writeCharacters (String charData) throws XMLStreamException {        
    }
    
    public void writeCharacters (char[] values, int param, int param2) throws XMLStreamException {        
    }
    
    public void writeComment (String str) throws XMLStreamException {        
    }
    
    public void writeDTD (String str) throws XMLStreamException {
    }
    
    public void writeDefaultNamespace (String namespaceURI) throws XMLStreamException {        
    }
    
    public void writeEmptyElement (String localName) throws XMLStreamException {
    }
    
    public void writeEmptyElement (String namespaceURI, String localName) throws XMLStreamException {
    }
    
    public void writeEmptyElement (String prefix, String localName, String namespaceURI) throws XMLStreamException {
    }
    
    public void writeEndDocument () throws XMLStreamException {
    }
    
    public void writeEndElement () throws XMLStreamException {
    }
    
    public void writeEntityRef (String str) throws XMLStreamException {
    }
    
    public void writeNamespace (String prefix, String namespaceURI) throws XMLStreamException {
    }
    
    public void writeProcessingInstruction (String str) throws XMLStreamException {
    }
    
    public void writeProcessingInstruction (String str, String str1) throws XMLStreamException {
    }
    
    public void writeStartDocument () throws XMLStreamException {
    }
    
    public void writeStartDocument (String version) throws XMLStreamException {
    }
    
    public void writeStartDocument (String encoding, String version) throws XMLStreamException {
    }
    
    public void writeStartElement (String localName) throws XMLStreamException {
    }
    
    public void writeStartElement (String namespaceURI, String localName) throws XMLStreamException {
    }
    
    public void writeStartElement (String prefix, String localName, String namespaceURI) throws XMLStreamException {        
    }
}
