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


package samples.sax;


import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.sax.SAXResult;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;


/**
 *
 * Demonstrate the use of SAXDocumentSerializer to convert xml file to FastInfoset document
 */
public class FISerializer {
    Transformer _transformer;
    DocumentBuilder _docBuilder;
    DOMSource _source = null;
    SAXResult _result = null;
    
    /** Creates a new instance of DocumentSerializer */
    public FISerializer() {
        try {
            _transformer = TransformerFactory.newInstance().newTransformer();
            _docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(output));
            SAXDocumentSerializer serializer = new SAXDocumentSerializer();
            serializer.setOutputStream(fos);
            
            _result = new SAXResult();
            _result.setHandler(serializer);
            _result.setLexicalHandler(serializer);                
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    public void write(File input, File output) {
        getDOMSource(input);
        getSAXResult(output);
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
    
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 4) {
            displayUsageAndExit();
        }
        
        try {
            //XML input file, such as ./data/inv100.xml
            File input = new File(args[0]);
            //FastInfoset output file, such as ./data/inv100_sax.finf. 
            File ouput = new File(args[1]);
            FISerializer docSerializer = new FISerializer();
            docSerializer.write(input, ouput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayUsageAndExit() {
        System.err.println("Usage: FISerializer <XML input file> <FI output file>");
        System.exit(1);        
    }
    
}
