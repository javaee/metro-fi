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

package com.sun.xml.fastinfoset.utilities.tools;

import com.sun.xml.analysis.types.SchemaProcessor;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.streambuffer.FastInfosetWriterSAXBufferProcessor;
import com.sun.xml.fastinfoset.streambuffer.TypedSAXBufferCreator;
import com.sun.xml.stream.buffer.XMLStreamBuffer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;

/**
 * Convert a XML document to a fast infoset document using a schema.
 * <p>
 * The schema is used to ascertain the data type of text content or
 * attribute values such that the lexical value of either can be
 * converted to a binary form that is compatible with the Fast Infoset
 * Encoding Algorithms and Restricted Alphabets.
 */
public class XML_SAX_TYPED_FI {
    
    public void parse(String[] args) throws Exception {
        String schemaLocation = null;
        InputStream in = null;
        OutputStream out = null;
        
        if (args.length == 1) {
            schemaLocation = args[0];
            in = new BufferedInputStream(System.in);
            out = new BufferedOutputStream(System.out);
        } else if (args.length == 2) {
            schemaLocation = args[0];
            in = new BufferedInputStream(new FileInputStream(args[1]));
            out = new BufferedOutputStream(System.out);
        } else if (args.length == 3) {
            schemaLocation = args[0];
            in = new BufferedInputStream(new FileInputStream(args[1]));
            out = new BufferedOutputStream(new FileOutputStream(args[2]));
        } else {
            throw new IllegalArgumentException("Incorrect arguments: schema <in> <out>");
        }
        
        parse(schemaLocation, in, out);
    }
    
    public void parse(String schemaLocation, InputStream xml, OutputStream fi) throws Exception {
        XMLStreamBuffer source = createBufferFromXMLDocument(schemaLocation, xml);

        FastInfosetWriterSAXBufferProcessor processor = new FastInfosetWriterSAXBufferProcessor();
        processor.setXMLStreamBuffer(source);
        
        SAXDocumentSerializer serializer = getSerializer(fi);
        processor.process(serializer);
    }
    
    private SAXParser getParser() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);
        return saxParserFactory.newSAXParser(); 
    }
    
    private SAXDocumentSerializer getSerializer(OutputStream fi) {
        SAXDocumentSerializer documentSerializer = new SAXDocumentSerializer();
        documentSerializer.setOutputStream(fi);
        return documentSerializer;
    }
    
    private XMLStreamBuffer createBufferFromXMLDocument(String schemaLocation, InputStream xml) 
            throws Exception {
        XMLReader reader = getParser().getXMLReader();

        if (schemaLocation != null) {
            SchemaProcessor sp = new SchemaProcessor(new File(schemaLocation).toURL());
            sp.process();

            // Create buffer from XML document, convert lexical space to
            // value space
            return TypedSAXBufferCreator.createNewBufferFromXMLReader(
                    sp.getElementToXSDataTypeMap(),
                    sp.getAttributeToXSDataTypeMap(),
                    reader,
                    xml);
        } else {
            return XMLStreamBuffer.createNewBufferFromXMLReader(
                    reader,
                    xml);
        }
    }
    
    public static void main(String[] args) throws Exception {
        XML_SAX_TYPED_FI s = new XML_SAX_TYPED_FI();
        s.parse(args);
    }
}
