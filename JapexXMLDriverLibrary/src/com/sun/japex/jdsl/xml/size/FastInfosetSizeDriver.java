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

import com.sun.japex.jdsl.xml.DriverConstants;
import com.sun.japex.jdsl.xml.FastInfosetParamSetter;
import com.sun.xml.fastinfoset.sax.SAXDocumentSerializer;
import com.sun.xml.fastinfoset.sax.VocabularyGenerator;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import javax.xml.parsers.SAXParser;


public class FastInfosetSizeDriver extends BaseSizeDriver {
    SAXDocumentSerializer _ds;
    
    public void initializeDriver() {
        _ds = new SAXDocumentSerializer();
        FastInfosetParamSetter.setParams(_ds, this);
    }
        
    protected void serialize(InputStream in, String fileName) throws Exception {
        // createParser();
        if (getBooleanParam(DriverConstants.EXTERNAL_VOCABULARY_PROPERTY) == false) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            _ds.setOutputStream(baos);
            
            SAXParser parser = createSAXParser();
            parser.parse(in, _ds);
            
            _content = baos.toByteArray();
        } else {
            byte[] content = getBytesFromInputStream(in);
            SAXParser parser = createSAXParser();
            
            SerializerVocabulary externalVocabulary = new SerializerVocabulary();
            VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(externalVocabulary);
            vocabularyGenerator.setCharacterContentChunkSizeLimit(0);
            vocabularyGenerator.setAttributeValueSizeLimit(0);
            parser.parse(new ByteArrayInputStream(content), vocabularyGenerator);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SerializerVocabulary initialVocabulary = new SerializerVocabulary();
            initialVocabulary.setExternalVocabulary("file:///" + fileName,
                    externalVocabulary, false);
            _ds.setVocabulary(initialVocabulary);
            _ds.setOutputStream(baos);
            parser.parse(new ByteArrayInputStream(content), _ds);
            
            _content = baos.toByteArray();
        }
    }
    
}
