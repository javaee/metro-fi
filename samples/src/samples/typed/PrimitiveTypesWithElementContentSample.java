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
package samples.typed;

import com.sun.xml.fastinfoset.sax.SAXDocumentParser;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.jvnet.fastinfoset.sax.FastInfosetDefaultHandler;
import org.jvnet.fastinfoset.sax.FastInfosetReader;
import org.jvnet.fastinfoset.sax.FastInfosetWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Examples of encoding/decoding primitive types as content of an element
 * when using the Fast Infoset SAX serializers and parsers.
 *
 * <p>
 * The {@link org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler} is used 
 * to encode and decode primitive types.
 * @author Paul.Sandoz@Sun.Com
 */
public class PrimitiveTypesWithElementContentSample {

    /**
     * Create an FI document.
     */
    byte[] createFIDocument() throws SAXException {
        // Instantiate a new FastInfosetWriter
        FastInfosetWriter fiw = new SAXDocumentSerializer();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        fiw.setOutputStream(baos);
        
        // Serialize the infoset
        fiw.startDocument();
            fiw.startElement("", "root", "root", null);
                fiw.startElement("", "bytes", "bytes", null);
                    byte[] bytes = {1, 2, 3, 4};
                    // Write an array of bytes using the PrimitiveTypeContentHandler
                    fiw.bytes(bytes, 0, bytes.length);
                fiw.endElement("", "bytes", "bytes");
                fiw.startElement("", "integers", "integers", null);
                    int[] ints = {1, 2, 3, 4};
                    // Write an array of integers using the PrimitiveTypeContentHandler
                    fiw.ints(ints, 0, ints.length);
                fiw.endElement("", "integers", "integers");
                fiw.startElement("", "floats", "floats", null);
                    float[] floats = {1.0f, 2.0f, 3.0f, 4.0f};
                    // Write an array of floats using the PrimitiveTypeContentHandler
                    fiw.floats(floats, 0, floats.length);
                fiw.endElement("", "floats", "floats");
            fiw.endElement("", "root", "root");
        fiw.endDocument();
        
        return baos.toByteArray();
    }

    /**
     * Parse an FI document using the 
     * {@link org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler}.
     */
    void parseFIDocumentUsingPTC(InputStream in) throws Exception {
        FastInfosetReader r = new SAXDocumentParser();

        // Create a handler that will receive events for primitive types
        FastInfosetDefaultHandler h = new FastInfosetDefaultHandler() {
            public void bytes(byte[] bs, int start, int length) throws SAXException {
                for (int i = 0; i < length; i++) {
                    if (i > 0) System.out.print(" ");
                    System.out.print(bs[start + i]);
                }
                System.out.println();
            }

            public void ints(int[] is, int start, int length) throws SAXException {
                for (int i = 0; i < length; i++) {
                    if (i > 0) System.out.print(" ");
                    System.out.print(is[start + i]);
                }
                System.out.println();
            }

            public void floats(float[] fs, int start, int length) throws SAXException {
                for (int i = 0; i < length; i++) {
                    if (i > 0) System.out.print(" ");
                    System.out.print(fs[start + i]);
                }
                System.out.println();
            }            
        };
        
        r.setContentHandler(h);
        r.setPrimitiveTypeContentHandler(h);
        
        r.parse(in);
    }
    
    /**
     * Parse an FI document using the the {@link org.xml.sax.ContentHandler}
     * <p>
     * Since the {@link org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler}
     * is not used all algorithm data encoded in binary form will be converted
     * to lexical form.
     * <p>
     * In this example the bytes have to be base64 encoded to convery to lexical
     * form.
     */
    void parseFIDocument(InputStream in) throws Exception {
        FastInfosetReader r = new SAXDocumentParser();

        // Create a handler that will receive events for primitive types
        ContentHandler h = new DefaultHandler() {
            public void characters (char ch[], int start, int length) throws SAXException {
                String s = new String(ch, start, length);
                System.out.println(s);
            }
        };
        
        r.setContentHandler(h);
        
        r.parse(in);
    }
    
    public static void main(String[] args) throws Exception {
        PrimitiveTypesWithElementContentSample pt = new PrimitiveTypesWithElementContentSample();
        
        byte[] b = pt.createFIDocument();
        pt.parseFIDocumentUsingPTC(new ByteArrayInputStream(b));
        pt.parseFIDocument(new ByteArrayInputStream(b));
    }
}