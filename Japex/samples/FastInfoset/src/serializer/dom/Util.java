/*
 * Japex ver. 0.1 software ("Software")
 * 
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This Software is distributed under the following terms:
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc., 'Java', 'Java'-based names,
 * nor the names of contributors may be used to endorse or promote products
 * derived from this Software without specific prior written permission.
 * 
 * The Software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE
 * AS A RESULT OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE
 * LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED
 * AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package serializer.dom;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.fastinfoset.stax.StAXInputFactory;
import com.sun.xml.fastinfoset.stax.SAX2StAXWriter;

public class Util {
    DocumentBuilder _docBuilder;
            
    public static final int STAX_SERIALIZER_RI = 1;
    public static final int STAX_SERIALIZER_FI = 2;
    public static final int STAX_SERIALIZER_SJSXP = 3;
    
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    /** Creates a new instance of Util */
    public Util() {
        init();
    }
    
    public Util(int outputFactory) {
        if (outputFactory==STAX_SERIALIZER_FI) {
            System.setProperty("javax.xml.stream.XMLOutputFactory", 
                       "com.sun.xml.fastinfoset.stax.StAXOutputFactory");        
        } else if (outputFactory==STAX_SERIALIZER_SJSXP) {
            System.setProperty("javax.xml.stream.XMLOutputFactory", 
                       "com.sun.xml.stream.ZephyrWriterFactory");                    
        }
        init();
    }
    
    void init() {
        try {
            factory = XMLOutputFactory.newInstance(); 
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            _docBuilder = dbf.newDocumentBuilder();             
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    public DOMSource getDOMSource(File input) {
        try {
            FileInputStream fis = new FileInputStream(input);
            Document document = _docBuilder.parse(fis);
            fis.close();
            return new DOMSource(document);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }    

    public SAXResult getSAXResult(OutputStream output) {
        SAXResult _result = null;
        try {
            XMLStreamWriter serializer = factory.createXMLStreamWriter(output);
            SAX2StAXWriter saxTostax = new SAX2StAXWriter(serializer);
            
            _result = new SAXResult();
            _result.setHandler(saxTostax);
            _result.setLexicalHandler(saxTostax);                
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }   
        return _result;
    }
    public StreamResult getStreamResult(OutputStream output) {
        StreamResult result = null;
        try {
            result = new StreamResult(output);            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }    
}
