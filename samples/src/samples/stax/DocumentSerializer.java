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

package samples.stax;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.sax.SAXResult;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.fastinfoset.stax.StAXInputFactory;
import com.sun.xml.fastinfoset.stax.SAX2StAXWriter;

/**
 * Demonstrate using StAXDocumentSerializer to convert a XML file to FI document
 * 
 */
public class DocumentSerializer {
    XMLOutputFactory factory;
    String _xmlFile;
    Transformer _transformer;
    DocumentBuilder _docBuilder;
    DOMSource _source = null;
    SAXResult _result = null;
    ByteArrayOutputStream _baos;
    
    /** Creates a new instance of DocumentSerializer */
    public DocumentSerializer() {
        try {
            _transformer = TransformerFactory.newInstance().newTransformer();
            _docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            factory = XMLOutputFactory.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 
    public void getDOMSource(File input) {
        // Load file into byte array to factor out IO
        try {
            FileInputStream fis = new FileInputStream(input);
            Document document = _docBuilder.parse(fis);
            fis.close();
            _source = new DOMSource(document);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void getSAXResult(File output) {
        try {
            //BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(output));
            _baos = new ByteArrayOutputStream();
            XMLStreamWriter serializer = factory.createXMLStreamWriter(_baos);
            SAX2StAXWriter saxTostax = new SAX2StAXWriter(serializer);
            
            _result = new SAXResult();
            _result.setHandler(saxTostax);
            _result.setLexicalHandler(saxTostax);                
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
    public void write(File input, File output) {
        getDOMSource(input);
        getSAXResult(output);
        for (int i=1; i<6; i++) {
            _baos.reset();
            if (_source != null && _result != null) {
                try {
                    System.out.println("Transforming "+input.getName()+ " into " + output.getName());
                    _transformer.transform(_source, _result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("\ndone.");
        } else {
                System.out.println("Source or Result could not be null.");
            }  
        }
        try {
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(output));
            _baos.writeTo(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            displayUsageAndExit();
        }
        System.setProperty("javax.xml.stream.XMLOutputFactory", 
                       "com.sun.xml.fastinfoset.stax.StAXOutputFactory");
        File input = new File(args[0]);
        File ouput = new File(args[1]);
        DocumentSerializer docSerializer = new DocumentSerializer();
        docSerializer.write(input, ouput);
    }

    private static void displayUsageAndExit() {
        System.err.println("Usage: FISerializer <XML input file> <FI output file>");
        System.exit(1);        
    }
        
}
