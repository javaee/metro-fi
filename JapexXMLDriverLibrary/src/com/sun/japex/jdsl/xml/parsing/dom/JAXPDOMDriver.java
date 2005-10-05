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

package com.sun.japex.jdsl.xml.parsing.dom;
import com.sun.japex.TestCase;
import com.sun.japex.jdsl.xml.BaseParserDriver;
import com.sun.japex.jdsl.xml.DriverConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class JAXPDOMDriver extends BaseParserDriver {
    DocumentBuilderFactory _builderFactory;
    DocumentBuilder _builder;
    boolean _traverseNodes;

    public void initializeDriver() {
        try {
            _builderFactory = createDocumentBuilderFactory();
            _traverseNodes = getBooleanParam(DriverConstants.TRAVERSE_NODES);
            _builderFactory.setAttribute("http://apache.org/xml/features/dom/defer-node-expansion", 
                    getBooleanParam(DriverConstants.DEFER_NODE_EXPANSION));
            _builder = _builderFactory.newDocumentBuilder();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    protected DocumentBuilderFactory createDocumentBuilderFactory() {
        return DocumentBuilderFactory.newInstance();
    }
    
    public void run(TestCase testCase) {
        try {
            _inputStream.reset();
            Document d = _builder.parse(_inputStream);
            if (_traverseNodes) {
                traverse(d);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    public void traverse(Node n) {
        Object o = n.getNodeValue();
        
        if (n.hasAttributes()) {
            final NamedNodeMap nnm = n.getAttributes();
            for (int i = 0; i < nnm.getLength(); i++) {
                final Node an = nnm.item(i);
                o = an.getNodeValue();
            }
        }
        
        if (n.hasChildNodes()) {
            final NodeList nl = n.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                traverse(nl.item(i));
            }
        }
    }
}
