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

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

/** <p>Writing a FI document directly with StAXDocumentSerializer.</p>
 *  This sample demonstrates the use of StAXDocumentSerializer to write out an FI document
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

            //create an instance of FastInfoset StAX Serializer and set output stream
            StAXDocumentSerializer s = new StAXDocumentSerializer();
            s.setOutputStream(fos);

            AttributesHolder attributes = new AttributesHolder();        
            attributes.clear();

            String temp = null;

            //start FastInfoset document
            s.writeStartDocument();

            //<ns1:invoice xmlns:ns1="http://www.sun.com/schema/spidermarkexpress/sm-inv">
            String namespaceURI = "http://www.sun.com/schema/spidermarkexpress/sm-inv";
            String prefix = "ns1";
            String localPart = "invoice";
            
            //namespace must be indexed before calling startElement
            
            s.writeStartElement(prefix, localPart, namespaceURI);
            s.writeNamespace(prefix, namespaceURI);
            s.setPrefix(prefix, namespaceURI);

            //  <Header>
            s.writeCharacters("\n\t");
            s.writeStartElement("header");

            //      <IssueDateTime>2003-03-13T13:13:32-08:00</IssueDateTime>
            s.writeCharacters("\n\t\t");
            s.writeStartElement("IssueDateTime");
            s.writeCharacters("2003-03-13T13:13:32-08:00");
            s.writeEndElement();

            //      <Identifier schemeAgencyName="ISO" schemeName="Invoice">15570720</Identifier>
            s.writeCharacters("\n\t\t");
            s.writeStartElement("Identifier");
            s.writeAttribute("schemeAgencyName", "ISO");
            s.writeAttribute("schemeName", "Invoice");
            s.writeCharacters("15570720");
            s.writeEndElement();

            //      <POIdentifier schemeName="Generic" schemeAgencyName="ISO">691</POIdentifier>
            s.writeCharacters("\n\t\t");
            s.writeStartElement("POIdentifier");
            s.writeAttribute("schemeName", "Generic");
            s.writeAttribute("schemeAgencyName", "ISO");
            s.writeCharacters("691");
            s.writeEndElement();
            
            //  </Header>
            s.writeCharacters("\n\t");
            s.writeEndElement();        

            //</ns1:invoice>
            s.writeCharacters("\n");
            s.writeEndElement();

            s.writeEndDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void displayUsageAndExit() {
        System.err.println("Usage: ant FIStAXSerializer or samples.stax.FastInfosetSerializer FI_output_file");
        System.exit(1);        
    }
}
