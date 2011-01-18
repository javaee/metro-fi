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

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.events.StAXEventReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import samples.common.Util;


/** <p>This is a sample that demonstrates the use of FI StAX Event Iterator API.</p>
 *  The sample starts by creating an input stream from the input FI document. The input stream is used
 *  to instantiate a stream reader as does in the StreamReader sample. An event reader (StAXEventReader)
 *  is created using the stream reader isntance. With the event reader, the program iterates through all
 *  of the events in the input FI document and displays event types and content.
 */
public class StAXEventReadingSample{
    private File input;
    InputStream document = null;
    XMLStreamReader streamReader = null;
    
    /** Creates a new instance of EventReader */
    public StAXEventReadingSample() {
    }
    /** Starts the sample. The sample takes a FI document filename that will
     * be read using StAXEventReader.
     *
     *  @param args a FI document.
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            displayUsageAndExit();
        }
        StAXEventReadingSample eventReader = new StAXEventReadingSample();
        try {
            eventReader.readFIDoc(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Reads a FI document using fastinfoset.StAXEventReader and displays all of the events in the document.
     *
     *  @param filename FI document name.
     */
    public void readFIDoc(String filename) throws Exception {
        input = new File(filename);
        document= new BufferedInputStream(new FileInputStream(input));
        
        XMLStreamReader streamReader = new StAXDocumentParser(document);
        XMLEventReader r = new StAXEventReader(streamReader);
        int count = 0;
        System.out.println("Reading "+ input.getName() + ": \n");
        while(r.hasNext()) {
            count++;
            XMLEvent e = r.nextEvent();
            System.out.println(count + ": " + Util.getEventTypeString(e.getEventType()) + " " + e.toString());
        }
    }
    
    private static void displayUsageAndExit() {
        System.err.println("Usage: ant EventReader or samples.stax.EventReader FI_file");
        System.exit(1);
    }
    
}
