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

import java.io.*;
import java.util.Properties;

import com.sun.japex.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XPP3Driver extends JapexDriverBase {
    
    String _xmlFile;
    byte[] _xmlFileByteArray;
    ByteArrayInputStream _inputStream;
    XmlPullParser _parser;
    long _bytesParsed;
    
    public XPP3Driver() {
    }

    public void initializeDriver() {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
               System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
            factory.setNamespaceAware(true);
            _parser = factory.newPullParser();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }   
    
    public void prepare(TestCase testCase) {
        _xmlFile = testCase.getParam("xmlfile");
        if (_xmlFile == null) {
            throw new RuntimeException("xmlfile not specified");
        }
        
        // Load file into byte array to factor out IO
        try {
            FileInputStream fis = new FileInputStream(new File(_xmlFile));
            _xmlFileByteArray = com.sun.japex.Util.streamToByteArray(fis);
            _inputStream = new ByteArrayInputStream(_xmlFileByteArray);
            fis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        _bytesParsed = 0;
    }
    
    public void warmup(TestCase testCase) {
        try {
            _inputStream.reset();
            _parser.setInput(_inputStream, null);   // unknown encoding
            while (_parser.next() != XmlPullParser.END_DOCUMENT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run(TestCase testCase) {
        try {
            _inputStream.reset();
            _parser.setInput(_inputStream, null);   // unknown encoding
            while (_parser.next() != XmlPullParser.END_DOCUMENT);
            
            _bytesParsed += _xmlFileByteArray.length;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void terminateDriver() {
    }
}
