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

import java.io.PrintWriter;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import samples.common.Util;

/** <p>This is a sample that demonstrates the use of FI StAX Cursor API.</p>
 *  The sample starts by creating an input stream from the input FI document. The input stream is
 *  then used to instantiating a new instance of FI StAX stream reader "StAXDocumentParser". The 
 *  rest of the code simply uses the StreamReader to loop through
 *  the document and displays event types, names and etc.
 */
public class StreamReader {
    
    /** Creates a new instance of Cursor */
    public StreamReader() {
    }
     /** Starts the sample. The sample takes a FI document that will
     * be read using StAXDocumentParser in the fastinfoset package.
     *
     *  @param args a FI document.
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 1) {
            displayUsageAndExit();
        }
        StreamReader streamReader = new StreamReader();
        streamReader.parse(args[0]);
    }
    
     /** Reads a FI document using StAXDocumentParser and displays event types and contents.
     *
     *  @param filename FI document name.
     */
    public void parse(String filename) {  
        File input = null;
        InputStream document = null;
        XMLStreamReader streamReader = null;
        try{ 
            input = new File(filename);
            document= new BufferedInputStream(new FileInputStream(filename));
            
            streamReader = new StAXDocumentParser(document);
                        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Reading "+ input.getName() + ": \n");
        long starttime = System.currentTimeMillis() ;
        try{                
            int eventType = streamReader.getEventType();

            while(streamReader.hasNext()){
                eventType = streamReader.next();
                Util.printEventType(eventType);
                Util.printName(streamReader,eventType);
                Util.printText(streamReader);
                if(streamReader.isStartElement()){
                    Util.printAttributes(streamReader);
                }
                Util.printPIData(streamReader);
                System.out.println("-----------------------------");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        long endtime = System.currentTimeMillis();
        System.out.println(" Parsing Time = " + (endtime - starttime) );
                
    }   

    private static void displayUsageAndExit() {
        System.err.println("Usage: ant StreamReader or samples.stax.StreamReader FI_file");
        System.exit(1);        
    }
        
}
