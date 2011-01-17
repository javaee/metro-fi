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


package org.jvnet.fastinfoset;

import java.io.InputStream;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.sun.xml.fastinfoset.sax.SAXDocumentParser;

/**
 *  A JAXP Source implementation that supports the parsing fast
 *  infoset document for use by applications that expect a Source.
 *
 *  <P>The derivation of FISource from SAXSource is an implementation
 *  detail.<P>
 *
 *  <P>This implementation is designed for interoperation with JAXP and is not
 *  not designed with performance in mind. It is recommended that for performant
 *  interoperation alternative parser specific solutions be used.<P>
 *
 *  <P>Applications shall obey the following restrictions:
 *   <UL>
 *     <LI>The setXMLReader and setInputSource shall not be called.</LI>
 *     <LI>The XMLReader object obtained by the getXMLReader method shall
 *        be used only for parsing the InputSource object returned by
 *        the getInputSource method.</LI>
 *     <LI>The InputSource object obtained by the getInputSource method shall 
 *        be used only for being parsed by the XMLReader object returned by 
 *        the getXMLReader method.</LI>
 *   </UL>
 *  </P>
 */
public class FastInfosetSource extends SAXSource {
   
    public FastInfosetSource(InputStream inputStream) {
        super(new InputSource(inputStream));
    }

    public XMLReader getXMLReader() {
        XMLReader reader = super.getXMLReader();
        if (reader == null) {
            reader = new SAXDocumentParser();
            setXMLReader(reader);
        }
        ((SAXDocumentParser) reader).setInputStream(getInputStream());
        return reader;
    }
    
    public InputStream getInputStream() {
        return getInputSource().getByteStream();
    }
    
    public void setInputStream(InputStream inputStream) {
        setInputSource(new InputSource(inputStream));
    }
}
