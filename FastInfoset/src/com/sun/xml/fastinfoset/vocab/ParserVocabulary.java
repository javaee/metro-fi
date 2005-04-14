/*
 * Fast Infoset ver. 0.1 software ("Software")
 * 
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


package com.sun.xml.fastinfoset.vocab;

import com.sun.xml.fastinfoset.util.CharArrayArray;
import com.sun.xml.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.StringArray;
import com.sun.xml.fastinfoset.util.ValueArray;
import java.net.URI;

public class ParserVocabulary extends Vocabulary {
    public final CharArrayArray restrictedAlphabet = new CharArrayArray();
    public final StringArray encodingAlgorithm = new StringArray();

    public final StringArray namespaceName = new StringArray();
    public final StringArray prefix = new StringArray();
    public final StringArray localName = new StringArray();
    public final StringArray otherNCName = new StringArray();
    public final StringArray otherURI = new StringArray();
    public final StringArray attributeValue = new StringArray();
    public final CharArrayArray otherString = new CharArrayArray();

    public final ContiguousCharArrayArray characterContentChunk = new ContiguousCharArrayArray();

    public final QualifiedNameArray elementName = new QualifiedNameArray();
    public final QualifiedNameArray attributeName = new QualifiedNameArray();

    public final ValueArray[] tables = new ValueArray[12];
    
    protected SerializerVocabulary _readOnlyVocabulary;
    
    /** Creates a new instance of ParserVocabulary */
    public ParserVocabulary() {
        tables[RESTRICTED_ALPHABET] = restrictedAlphabet;
        tables[ENCODING_ALGORITHM] = encodingAlgorithm;
        tables[PREFIX] = prefix;
        tables[NAMESPACE_NAME] = namespaceName;
        tables[LOCAL_NAME] = localName;
        tables[OTHER_NCNAME] = otherNCName;
        tables[OTHER_URI] = otherURI;
        tables[ATTRIBUTE_VALUE] = attributeValue;
        tables[OTHER_STRING] = otherString;
        tables[CHARACTER_CONTENT_CHUNK] = characterContentChunk;
        tables[ELEMENT_NAME] = elementName;
        tables[ATTRIBUTE_NAME] = attributeName;
    }

    public ParserVocabulary(SerializerVocabulary vocab) {
        this();
        
    }
    
    void setReadOnlyVocabulary(ParserVocabulary readOnlyVocabulary, boolean clear) {
        for (int i = 0; i < tables.length; i++) {
            tables[i].setReadOnlyArray(readOnlyVocabulary.tables[i], clear);
        }
    }
    
    public void setInitialVocabulary(ParserVocabulary initialVocabulary, boolean clear) {
        setExternalVocabularyURI(null);
        setInitialReadOnlyVocabulary(true);
        setReadOnlyVocabulary(initialVocabulary, clear);
    }
    
    public void setReferencedVocabulary(URI referencedVocabularyURI, ParserVocabulary referencedVocabulary, boolean clear) {
        setInitialReadOnlyVocabulary(false);
        setExternalVocabularyURI(referencedVocabularyURI);
        setReadOnlyVocabulary(referencedVocabulary, clear);
    }
    
    public void clear() {
        for (int i = 0; i < tables.length; i++) {
            tables[i].clear();
        }        
    }
}
