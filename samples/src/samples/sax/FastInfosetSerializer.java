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

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;

/** <p>Writing a FI document directly with SAXDocumentSerializer.</p>
 *  This sample demonstrates the use of SAXDocumentSerializer to write out an FI document
 *  with following content:
<ns1:invoice xmlns:ns1="http://www.sun.com/schema/spidermarkexpress/sm-inv">
    <Header>
        <IssueDateTime>2003-03-13T13:13:32-08:00</IssueDateTime>
        <Identifier schemeAgencyName="ISO" schemeName="Invoice">15570720</Identifier>
        <POIdentifier schemeName="Generic" schemeAgencyName="ISO">691</POIdentifier>
    </Header>
</ns1:invoice>
 *
 * You may use tool "fitosaxtoxml" provided in the FastInfoset package to verify the result.
 */

public class FastInfosetSerializer {
    
    /** Creates a new instance of FastInfosetSerializer */
    public FastInfosetSerializer() {
    }
    
    public static void main(String[] args) {
        if (args.length != 1) {
            displayUsageAndExit();
        }

        try {
            File output = new File(args[0]);
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(output));

            //create an instance of FastInfoset SAX Serializer and set output stream
            SAXDocumentSerializer s = new SAXDocumentSerializer();
            s.setOutputStream(fos);

            AttributesHolder attributes = new AttributesHolder();        
            String temp = null;

            //start FastInfoset document
            s.startDocument();

            //<ns1:invoice xmlns:ns1="http://www.sun.com/schema/spidermarkexpress/sm-inv">
            String namespaceURI = "http://www.sun.com/schema/spidermarkexpress/sm-inv";
            String prefix = "ns1";
            String localPart = "invoice";
            
            //namespace must be indexed before calling startElement
            s.startPrefixMapping(prefix, namespaceURI);
            s.startElement(namespaceURI, localPart, "ns1:invoice", attributes);

            //  <Header>
            temp = "\n\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.startElement("", "header", "header", attributes);

            //      <IssueDateTime>2003-03-13T13:13:32-08:00</IssueDateTime>
            temp = "\n\t\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.startElement("", "IssueDateTime", "IssueDateTime", attributes);
            temp = "2003-03-13T13:13:32-08:00";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "IssueDateTime", "IssueDateTime");

            //      <Identifier schemeAgencyName="ISO" schemeName="Invoice">15570720</Identifier>
            temp = "\n\t\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            attributes.clear();
            attributes.addAttribute(new QualifiedName("", "", "schemeAgencyName", "schemeAgencyName"), "ISO");
            attributes.addAttribute(new QualifiedName("", "", "schemeName", "schemeName"), "Invoice");
            s.startElement("", "Identifier", "Identifier", attributes);
            temp = "15570720";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "Identifier", "Identifier");

            //      <POIdentifier schemeName="Generic" schemeAgencyName="ISO">691</POIdentifier>
            temp = "\n\t\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            attributes.clear();
            attributes.addAttribute(new QualifiedName("", "", "schemeName", "schemeName"), "Generic");
            attributes.addAttribute(new QualifiedName("", "", "schemeAgencyName", "schemeAgencyName"), "ISO");
            s.startElement("", "POIdentifier", "POIdentifier", attributes);
            temp = "691";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "POIdentifier", "POIdentifier");
            
            //  </Header>
            temp = "\n\t";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "header", "header");        

            //</ns1:invoice>
            temp = "\n";
            s.characters(temp.toCharArray(), 0, temp.length());
            s.endElement("", "invoice", "ns1:invoice");

            s.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void displayUsageAndExit() {
        System.err.println("Usage: ant FISAXSerializer or samples.sax.FastInfosetSerializer FI_output_file");
        System.exit(1);        
    }
}
