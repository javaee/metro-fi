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

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import com.sun.org.apache.xml.internal.utils.DOMBuilder;
import com.sun.xml.fastinfoset.sax.Features;
import com.sun.xml.fastinfoset.sax.Properties;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;



public class JAXPSAXXercesDOMBuilderDriver extends JapexDriverBase {
    ByteArrayInputStream _inputStream;
    InputSource _inputSource;
    
    SAXParser _parser;
    XMLReader _reader;
    DocumentBuilder _db;
    
    public void initializeDriver() {
    }   


    public void prepare(TestCase testCase) {
        String xmlFile = testCase.getParam("xmlfile");
        if (xmlFile == null) {
            throw new RuntimeException("xmlfile not specified");
        }
        
        // Load file into byte array to factor out IO
        try {
	    SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setFeature(Features.NAMESPACE_PREFIXES_FEATURE, true);
            _parser = spf.newSAXParser();
            _reader = _parser.getXMLReader();
            
            FileInputStream fis = new FileInputStream(new File(xmlFile));
            byte[] xmlFileByteArray = com.sun.japex.Util.streamToByteArray(fis);
            _inputStream = new ByteArrayInputStream(xmlFileByteArray);
            fis.close();
            _inputSource = new InputSource(_inputStream);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            _db = dbf.newDocumentBuilder();
        }
        catch (Exception e) {
        }
    }

    public void warmup(TestCase testCase) {
        try {
            _inputStream.reset();
            DOMBuilder db = new DOMBuilder(_db.newDocument());
            _reader.setContentHandler(db);
            _reader.setProperty(Properties.LEXICAL_HANDLER_PROPERTY, db);
            _reader.parse(_inputSource);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run(TestCase testCase) {
        try {
            _inputStream.reset();
            DOMBuilder db = new DOMBuilder(_db.newDocument());
            _reader.setContentHandler(db);
            _reader.setProperty(Properties.LEXICAL_HANDLER_PROPERTY, db);
            _reader.parse(_inputSource);
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
