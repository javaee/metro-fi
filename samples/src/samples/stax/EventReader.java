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
/*
 * Demonstrate using StAX event API
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.FileReader;
import javax.xml.stream.*;
import javax.xml.stream.events.* ;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.fastinfoset.stax.StAXInputFactory;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;


/**
 *
 * Demonstrate the use of Event API
 */
public class EventReader{
    protected XMLInputFactory factory = null;
    private File input;
    InputStream document = null;
    XMLStreamReader streamReader = null;
    
    /** Creates a new instance of EventReader */
    public EventReader() {
        init();
    }
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            displayUsageAndExit();
        }
        System.setProperty("javax.xml.stream.XMLInputFactory", 
                       "com.sun.xml.fastinfoset.stax.StAXInputFactory");
        System.setProperty("javax.xml.stream.XMLEventFactory", 
                       "com.sun.xml.fastinfoset.stax.StAXEventFactory");
        EventReader eventReader = new EventReader();
        try {
            eventReader.testFinfInput(args[0]);
            eventReader.testXMLInput(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void init() {
        factory = XMLInputFactory.newInstance();
    }
    public void testFinfInput(String filename) throws Exception {
        input = new File(filename);
        document= new BufferedInputStream(new FileInputStream(input));

        factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(document);
        int count = 0;
        System.out.println("Reading "+ input.getName() + ": \n");
        while(r.hasNext()) {
            count++;
            XMLEvent e = r.nextEvent();
            System.out.println(count + ": " + Util.getEventTypeString(e.getEventType()) + " " + e.toString());
        }
    }
    
    /** test creating EventReader with a XML file in a form of a IO Reader
     *
     */
    public void testXMLInput(String filename) throws Exception {
        input = new File(filename);
        FileReader document= new FileReader(input);

        factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(document);
        int count = 0;
        System.out.println("Reading "+ input.getName() + ": \n");
        while(r.hasNext()) {
            count++;
            XMLEvent e = r.nextEvent();
            if (e.getEventType()==XMLStreamConstants.CHARACTERS)
                System.out.println(count + ": " + Util.getEventTypeString(e.getEventType()) + " [" + e.toString()+ "]");
            else
                System.out.println(count + ": " + Util.getEventTypeString(e.getEventType()) + " " + e.toString());
        }
        
    }

    private static void displayUsageAndExit() {
        System.err.println("Usage: EventReader <finf file> <xml file>");
        System.exit(1);        
    }
        
}
