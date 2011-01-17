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

package samples.transform;

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.tools.SAX2StAXWriter;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;

/** <p>Serializes an XML input stream into FI document using
 *  StAX document serializer defined in the fastinfoset.stax package.</p>
 *  The sample demonstrates how to use StAXDocumentSerializer as a SAX handler and JAXP
 *  transformer to convert an XML file into a FI document. As shown in the sample,
 *  transforming a DOM source to SAX Result involves very little coding. However, the process
 *  may not be efficient due to the construction of DOM source.
 *
 *  In the sample, a DOMSource is constructed out of an XML file input (see method getDOMSource)
 *  and a SAXResult is instantiated using an instance of SAX2StAXWriter as handlers (see method
 *  getSAXResult). Utility class
 *  SAX2StAXWriter extends DefaultHandler and implements LexicalHandler, which allows it
 *  to be used to handle SAX events. The source and result are then used by the JAXP transformer
 *  to transform the XML file to FI document which was passed in as OutputStream for the StAX
 *  serializer object. The XML inputstream is first transformed into a ByteArrayOutputStream
 *  that may be used for other processing. In this sample, we simply write it out into a file.<br>
 *
 */

public class XMLToFastInfosetStAXSerializer {
    String _xmlFile;
    Transformer _transformer;
    DocumentBuilder _docBuilder;
    DOMSource _source = null;
    SAXResult _result = null;
    ByteArrayOutputStream _baos;
    
    /** Creates a new instance of DocumentSerializer */
    public XMLToFastInfosetStAXSerializer() {
        try {
            _transformer = TransformerFactory.newInstance().newTransformer();
            _docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Construct a DOMSource with a file.
     *
     *  @param input the XML file input
     */
    void getDOMSource(File input) {
        try {
            FileInputStream fis = new FileInputStream(input);
            Document document = _docBuilder.parse(fis);
            fis.close();
            _source = new DOMSource(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /** Initialize a SAXResult and set its handers.
     *
     *  @param output FI document output
     */
    void getSAXResult(File output) {
        try {
            _baos = new ByteArrayOutputStream();
            XMLStreamWriter serializer = new StAXDocumentSerializer(_baos);
            SAX2StAXWriter saxTostax = new SAX2StAXWriter(serializer);
            
            _result = new SAXResult();
            _result.setHandler(saxTostax);
            _result.setLexicalHandler(saxTostax);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Transform an XML file into a FI document.
     *
     *  @param input an XML file input
     *  @param output the FI document output
     */
    public void write(File input, File output) {
        getDOMSource(input);
        getSAXResult(output);
        if (_source != null && _result != null) {
            try {
                System.out.println("Transforming "+input.getName()+ " into " + output.getName());
                //Transform the XML inputstream into ByteArrayOutputStream
                _transformer.transform(_source, _result);
                
                //the ByteArrayOutputStream may be used for other processing
                //in this sample, we simply write it out to a file
                BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(output));
                _baos.writeTo(fos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("\ndone.");
        } else {
            System.out.println("Source or Result could not be null.");
        }
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** Starts the sample.
     *
     *  @param args XML input file name and FI output file name
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            displayUsageAndExit();
        }
        File input = new File(args[0]);
        File ouput = new File(args[1]);
        XMLToFastInfosetStAXSerializer docSerializer = new XMLToFastInfosetStAXSerializer();
        docSerializer.write(input, ouput);
    }
    
    private static void displayUsageAndExit() {
        System.err.println("Usage: ant FIStAXSerializer or samples.stax.FISerializer XML_input_file> FI_output_file");
        System.exit(1);
    }
    
}
