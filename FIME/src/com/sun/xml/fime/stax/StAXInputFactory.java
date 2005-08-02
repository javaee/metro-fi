/*
 * FIME (Fast Infoset ME) software ("Software")
 *
 * Copyright, 2005 Sun Microsystems, Inc. All Rights Reserved.
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


package com.sun.xml.fime.stax;


import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.sun.xml.fime.util.MessageCenter;

public class StAXInputFactory extends XMLInputFactory {    
    //List of supported properties and default values.
    private StAXManager _manager = new StAXManager(StAXManager.CONTEXT_READER) ;
    
    public StAXInputFactory() {
    }
    
    public static XMLInputFactory newInstance() {
        return XMLInputFactory.newInstance();
    }

  /**
   * Create a new XMLStreamReader from a reader
   * @param reader the XML data to read from
   * @throws XMLStreamException 
   */
    public XMLStreamReader createXMLStreamReader(Reader xmlfile) throws XMLStreamException {
        throw new RuntimeException("Not supported");
    }    
    
    public XMLStreamReader createXMLStreamReader(InputStream s) throws XMLStreamException {
        return new StAXDocumentParser(s, _manager);
    }
    
    /** Get the value of a feature/property from the underlying implementation
     * @param name The name of the property (may not be null)
     * @return The value of the property
     * @throws IllegalArgumentException if the property is not supported
     */
    public Object getProperty(String name) throws IllegalArgumentException {
        if(name == null){
            throw new IllegalArgumentException(MessageCenter.getString("message.nullPropertyName"));
        }
        if(_manager.containsProperty(name))
            return _manager.getProperty(name);
        throw new IllegalArgumentException(MessageCenter.getString("message.propertyNotSupported", new Object[]{name}));
    }
    
    /** Query the set of Properties that this factory supports.
     *
     * @param name The name of the property (may not be null)
     * @return true if the property is supported and false otherwise
     */
    public boolean isPropertySupported(String name) {
        if(name == null)
            return false ;
        else
            return _manager.containsProperty(name);
    }
    
    /** Allows the user to set specific feature/property on the underlying implementation. The underlying implementation
     * is not required to support every setting of every property in the specification and may use IllegalArgumentException
     * to signal that an unsupported property may not be set with the specified value.
     * @param name The name of the property (may not be null)
     * @param value The value of the property
     * @throws IllegalArgumentException if the property is not supported
     */
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        _manager.setProperty(name,value);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader, boolean requiringXMLDeclaration) throws XMLStreamException {
        throw new RuntimeException("Not supported");
    }

    public XMLStreamReader createXMLStreamReader(InputStream s, boolean requiringXMLDeclaration) throws XMLStreamException {
        return new StAXDocumentParser(s, _manager);
    }
    
}
