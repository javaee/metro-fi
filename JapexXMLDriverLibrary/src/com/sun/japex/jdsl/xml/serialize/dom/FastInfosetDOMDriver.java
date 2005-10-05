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

package com.sun.japex.jdsl.xml.serialize.dom;

import com.sun.japex.TestCase;
import com.sun.japex.jdsl.xml.DriverConstants;
import com.sun.japex.jdsl.xml.FastInfosetParamSetter;
import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;
import com.sun.xml.fastinfoset.sax.VocabularyGenerator;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class FastInfosetDOMDriver extends BaseJAXPDOMDriver {
    protected DOMDocumentSerializer _fiSerializer;
    protected SerializerVocabulary _initialVocabulary;
    protected boolean _extVocab;
    
    public void initializeDriver() {
        super.initializeDriver();
        
        _fiSerializer = new DOMDocumentSerializer();
        
        FastInfosetParamSetter.setParams(_fiSerializer, this);
    }
    
    public void prepare(TestCase testCase) {
        super.prepare(testCase);
        
        _fiSerializer.setOutputStream(_outputStream);
        
        try {
            _extVocab = getBooleanParam(DriverConstants.EXTERNAL_VOCABULARY_PROPERTY);
            if (_extVocab == true) {
                String xmlFile = testCase.getParam("xmlfile");
                if (xmlFile == null) {
                    throw new RuntimeException("xmlfile not specified");
                }
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(xmlFile)));

                SAXParserFactory spf = SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                SAXParser parser = spf.newSAXParser();

                SerializerVocabulary externalSerializeVocabulary = new SerializerVocabulary();
                VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(externalSerializeVocabulary);
                vocabularyGenerator.setCharacterContentChunkSizeLimit(0);
                vocabularyGenerator.setAttributeValueSizeLimit(0);
                parser.parse(in, vocabularyGenerator);

                String externalVocabularyURI = "file:///" + xmlFile; 
                _initialVocabulary = new SerializerVocabulary();
                _initialVocabulary.setExternalVocabulary(externalVocabularyURI,
                        externalSerializeVocabulary, false);
                _fiSerializer.setVocabulary(_initialVocabulary);                
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
        
    public void run(TestCase testCase) {
        try {
            _outputStream.reset();
            if (_extVocab) {
                _initialVocabulary.clear();
            }
            _fiSerializer.serialize(_d);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}