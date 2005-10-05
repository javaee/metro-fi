/*
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

package com.sun.japex.jdsl.xml.size;

import com.sun.japex.Constants;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import com.sun.japex.jdsl.xml.DriverConstants;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;


public abstract class BaseSizeDriver extends JapexDriverBase {
    protected byte[] _content;
    
    protected SAXParser createSAXParser() throws ParserConfigurationException, SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        return spf.newSAXParser();
    }
    
    protected InputStream getInputStream(String fileName) throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(fileName));
    }
    
    protected byte[] getBytesFromInputStream(InputStream in) {
        return com.sun.japex.Util.streamToByteArray(in);
    }
        
    public void finish(TestCase testCase) {
        String xmlFile = testCase.getParam("xmlfile");
        if (xmlFile == null) {
            throw new RuntimeException("xmlfile not specified");
        }
        
        try {
            serialize(getInputStream(xmlFile), xmlFile);
        
            if (getBooleanParam(DriverConstants.GZIP_PROPERTY)) {
                ByteArrayOutputStream gzipBaos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOs = new GZIPOutputStream(gzipBaos);
                gzipOs.write(_content, 0, _content.length);   
                gzipOs.finish();

                _content = gzipBaos.toByteArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        testCase.setDoubleParam(Constants.RESULT_VALUE, _content.length / 1024.0);
    }
    
    abstract protected void serialize(InputStream in, String fileName) throws Exception;
}
