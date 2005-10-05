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

package com.sun.japex.jdsl.xml;

import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.sax.VocabularyGenerator;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jvnet.fastinfoset.FastInfosetParser;
import org.w3c.dom.Document;

public abstract class BaseParserDriver extends JapexDriverBase {
    protected ByteArrayInputStream _inputStream;    
    
    public void prepare(TestCase testCase) {
        String xmlFile = testCase.getParam("xmlfile");
        if (xmlFile == null) {
            throw new RuntimeException("xmlfile not specified");
        }

        try {
            FileInputStream fis = new FileInputStream(new File(xmlFile));
         
            if (this instanceof FastInfosetParserDriver) {
                ByteArrayInputStream bais = new ByteArrayInputStream(com.sun.japex.Util.streamToByteArray(fis));
                prepareFI(bais, xmlFile);
            } else {
                BufferedInputStream bis = new BufferedInputStream(fis);
                prepareXML(bis);
            }
            
            fis.close();                      
        }  catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }        
    }    
    
    public void prepareXML(InputStream in) throws Exception {
        // Load file into byte array to factor out IO
        byte[] xmlFileByteArray = com.sun.japex.Util.streamToByteArray(in);
        _inputStream = new ByteArrayInputStream(xmlFileByteArray);
    }    
    
    public void prepareFI(ByteArrayInputStream in, String name) throws Exception {        
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser parser = spf.newSAXParser();
        
        SAXDocumentSerializer sds = new SAXDocumentSerializer();
        FastInfosetParamSetter.setParams(sds, this);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        sds.setOutputStream(baos);
        
        if (getBooleanParam(DriverConstants.EXTERNAL_VOCABULARY_PROPERTY) == true) {
            SerializerVocabulary externalSerializeVocabulary = new SerializerVocabulary();
            ParserVocabulary externalParserVocabulary = new ParserVocabulary();
            VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(externalSerializeVocabulary, externalParserVocabulary);
            vocabularyGenerator.setCharacterContentChunkSizeLimit(0);
            vocabularyGenerator.setAttributeValueSizeLimit(0);
            parser.parse(in, vocabularyGenerator);
            in.reset();
            
            String externalVocabularyURI = "file:///" + name; 
            SerializerVocabulary initialVocabulary = new SerializerVocabulary();
            initialVocabulary.setExternalVocabulary(externalVocabularyURI,
                    externalSerializeVocabulary, false);
            sds.setVocabulary(initialVocabulary);
            
            FastInfosetParser fps = ((FastInfosetParserDriver)this).getParser();
            HashMap map = new HashMap();
            map.put(externalVocabularyURI, externalParserVocabulary);
            fps.setExternalVocabularies(map);
        }
        
        parser.parse(in, sds);
        _inputStream = new ByteArrayInputStream(baos.toByteArray());
    }
    
    public Document createDocument() throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document d = builder.parse(_inputStream);
        _inputStream.reset();
        
        return d;
    }
}

