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

package com.sun.japex.report;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
 * parse report
 * 
 */
public class ReportDataParser extends DefaultHandler { 
    static final String DRIVER = "driver";
    static final String TESTCASE = "testCase";
    
    StringBuffer textBuffer;
    Map _reports;
    TrendReportParams _params; 

    ResultPerDriver _resultPerDriver = null;
    boolean _driverStart = false;
    boolean _testStart = false;
    String _currentDriverName = null;
    String _currentTestName = null;
    
    
    /** Creates a new instance of ReportData */
    public ReportDataParser(TrendReportParams params) {
        _params = params;
    }
    public Map getReports() {
        return _reports;
    }
    //===========================================================
    // SAX DocumentHandler methods
    //===========================================================

    public void startDocument()
    throws SAXException
    {
        //do nothing
    }

    public void endDocument()
    throws SAXException
    {
        //do nothing
    }

    public void startElement(String namespaceURI,
                             String sName, // simple name
                             String qName, // qualified name
                             Attributes attrs)
    throws SAXException
    {
        String eName = sName; // element name
        if ("".equals(eName)) eName = qName; // not namespaceAware
        
        if (eName.equals(DRIVER)) {
//            System.out.println("Start: " + eName);
            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); i++) {
                    String aName = attrs.getLocalName(i); // Attr name 
                    if ("".equals(aName)) aName = attrs.getQName(i);
//            System.out.println("att: " + aName + "=" +attrs.getValue(i) + "param.driver="+_params.driver());
                    
                    if (_params.isDriverSpecified()) {
                        if (aName.equals("name") && attrs.getValue(i).equals(_params.driver())) {
//System.out.println("**Driver STart *");                          
                            _driverStart = true;
                            _resultPerDriver = new ResultPerDriver();
                            _currentDriverName = attrs.getValue(i);
                        }
                    }
                }
            }
        } else if (_driverStart) {
            if (eName.equals(TESTCASE)) {
                if (attrs != null) {
                    for (int i = 0; i < attrs.getLength(); i++) {
                        String aName = attrs.getLocalName(i); // Attr name 
                        if ("".equals(aName)) aName = attrs.getQName(i);
                        if (aName.equals("name")) {
                            _currentTestName = attrs.getValue(i);
                        }
                    }
                }
                _testStart = true;
            }
        }
    }

    public void endElement(String namespaceURI,
                           String sName, // simple name
                           String qName  // qualified name
                          )
    throws SAXException
    {
        String eName = sName; // element name
        if ("".equals(eName)) eName = qName; // not namespaceAware
//        System.out.println("end element:"+eName);
        if(eName.equals(DRIVER)) {
            if (_driverStart) {
                if (_reports == null) {
                    _reports = new HashMap();                            
                }
                _reports.put(_currentDriverName, _resultPerDriver);
                _resultPerDriver = null;
                //ResultPerDriver test = (ResultPerDriver)_reports.get(_currentDriverName);
                //System.out.println(test.getAritMean());
            }            
//            System.out.println("end driver: " + eName);
            _driverStart = false;
        } else if (_driverStart) {
            if (_testStart) {
                if (eName.equals("resultValue")) {
                    _resultPerDriver.addResult(_currentTestName, textBuffer.toString());
                } else if (eName.equals(TESTCASE)) {
                    _testStart = false;
                }
            } else {
                if (eName.equals("resultHarmMean")) {
//                    System.out.println("resultHarmMean: " + textBuffer.toString());

                    _resultPerDriver.setHarmMean(textBuffer.toString());
//                    System.out.println("resultHarmMean: " + _resultPerDriver.getHarmMean());
                } else if (eName.equals("resultGeomMean")) {
                    _resultPerDriver.setGeomMean(textBuffer.toString());
                } else if (eName.equals("resultAritMean")) {
                    _resultPerDriver.setAritMean(textBuffer.toString());
                }
            }
        }
        textBuffer = null;
    }

    public void characters(char buf[], int offset, int len)
    throws SAXException
    {
        String s = new String(buf, offset, len);
        if (textBuffer == null) {
           textBuffer = new StringBuffer(s);
        } else {
           textBuffer.append(s);
        }
    }
    
}
