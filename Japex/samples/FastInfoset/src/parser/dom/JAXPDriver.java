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

package parser.dom;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;


import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.Document;
import com.sun.japex.*;


public class JAXPDriver extends JapexDriverBase {
    ByteArrayInputStream _inputStream;

    Transformer _transformer;
    SAXSource _source;
    
    /** Creates a new instance of JAXPDriver */
    public JAXPDriver() {
    }

    public void initializeDriver() {
        try {
            _transformer = TransformerFactory.newInstance().newTransformer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   


    public void prepare(TestCase testCase) {
        String xmlFile = testCase.getParam("xmlfile");
        if (xmlFile == null) {
            throw new RuntimeException("xmlfile not specified");
        }
        
        // Load file into byte array to factor out IO
        try {
            // TODO must use URL here
            FileInputStream fis = new FileInputStream(new File(xmlFile));
            byte[] xmlFileByteArray = com.sun.japex.Util.streamToByteArray(fis);
            _inputStream = new ByteArrayInputStream(xmlFileByteArray);
            fis.close();
            
            _source = new SAXSource(new InputSource(_inputStream));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void warmup(TestCase testCase) {
        try {
            DOMResult result = new DOMResult();
            _inputStream.reset();
            _transformer.transform(_source, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run(TestCase testCase) {
        try {
            DOMResult result = new DOMResult();
            _inputStream.reset();
            _transformer.transform(_source, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void finish(TestCase testCase) {
    }
    
    public void terminateDriver() {
    }      
}
