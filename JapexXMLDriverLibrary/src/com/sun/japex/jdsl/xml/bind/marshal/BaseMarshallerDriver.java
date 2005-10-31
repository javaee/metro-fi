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

package com.sun.japex.jdsl.xml.bind.marshal;

import java.io.*;
import java.net.URLClassLoader;

import com.sun.japex.jdsl.xml.TestCaseUtil;
import static com.sun.japex.Util.*;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public abstract class BaseMarshallerDriver extends JapexDriverBase {
    protected Marshaller _marshaller;
    protected Unmarshaller _unmarshaller;
    protected Object _bean;
    protected ByteArrayOutputStream _outputStream;
    
    protected static boolean printManifest = true;
    
    public void initializeDriver() {
        if (printManifest) {
            getTestSuite().setParam("jaxb-impl.manifest",
                getManifestAsString((URLClassLoader) getClass().getClassLoader(), 
                                    "jaxb-impl.jar"));
            printManifest = false;
        }
    }
            
    public void prepare(TestCase testCase) {
        String xmlFile = TestCaseUtil.getXmlFile(testCase);
        
        try {            
            // Get JAXB unmarshaller
             JAXBContext jc = JAXBContext.newInstance(testCase.getParam("contextPath"));
            _marshaller = jc.createMarshaller();
            _unmarshaller = jc.createUnmarshaller();
        
            // Unmarshall bean and prepare output stream
            _bean = _unmarshaller.unmarshal(new FileInputStream(xmlFile));
            _outputStream = new ByteArrayOutputStream();
        } 
        catch (Exception e) {
            System.err.println(xmlFile);
            e.printStackTrace();
            System.exit(-1);
        }
    }    
}
