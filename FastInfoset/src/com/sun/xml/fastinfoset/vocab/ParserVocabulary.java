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
import com.sun.xml.fastinfoset.util.PrefixArray;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.StringArray;
import com.sun.xml.fastinfoset.util.ValueArray;

public class ParserVocabulary extends Vocabulary {
    public static final String IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.IdentifyingStringTable.maximumItems";
    public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumItems";
    public static final String NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS_PEOPERTY = 
        "com.sun.xml.fastinfoset.vocab.ParserVocabulary.NonIdentifyingStringTable.maximumCharacters";

    protected static int IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS;
    protected static int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS; 
    protected static int NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS;
    
    static {
        IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = 
                getIntegerValueFromProperty(IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY);
        NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS = 
                getIntegerValueFromProperty(NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS_PEOPERTY);
        NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS = 
                getIntegerValueFromProperty(NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS_PEOPERTY);
    }
    
    private static int getIntegerValueFromProperty(String property) {
        String value = System.getProperty(property);
        if (value == null) {
            return Integer.MAX_VALUE;
        }
        
        try {
            return Math.max(Integer.parseInt(value), ValueArray.DEFAULT_CAPACITY);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
    
    public final CharArrayArray restrictedAlphabet = new CharArrayArray(ValueArray.DEFAULT_CAPACITY, 256);
    public final StringArray encodingAlgorithm = new StringArray(ValueArray.DEFAULT_CAPACITY, 256);

    public final StringArray namespaceName;
    public final PrefixArray prefix;
    public final StringArray localName;
    public final StringArray otherNCName ;
    public final StringArray otherURI;
    public final StringArray attributeValue;
    public final CharArrayArray otherString;

    public final ContiguousCharArrayArray characterContentChunk;

    public final QualifiedNameArray elementName;
    public final QualifiedNameArray attributeName;

    public final ValueArray[] tables = new ValueArray[12];
    
    protected SerializerVocabulary _readOnlyVocabulary;
    
    /** Creates a new instance of ParserVocabulary */
    public ParserVocabulary() {
        namespaceName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        prefix = new PrefixArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        localName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        otherNCName = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        otherURI = new StringArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        attributeValue = new StringArray(ValueArray.DEFAULT_CAPACITY, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        otherString = new CharArrayArray(ValueArray.DEFAULT_CAPACITY, NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);

        characterContentChunk = new ContiguousCharArrayArray(ValueArray.DEFAULT_CAPACITY, 
                NON_IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS, 
                ContiguousCharArrayArray.INITIAL_CHARACTER_SIZE, 
                NON_IDENTIFYING_STRING_TABLE_MAXIMUM_CHARACTERS);

        elementName = new QualifiedNameArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);
        attributeName = new QualifiedNameArray(ValueArray.DEFAULT_CAPACITY, IDENTIFYING_STRING_TABLE_MAXIMUM_ITEMS);

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
    
    public void setReferencedVocabulary(String referencedVocabularyURI, ParserVocabulary referencedVocabulary, boolean clear) {
        if (!referencedVocabularyURI.equals(getExternalVocabularyURI())) {
            setInitialReadOnlyVocabulary(false);
            setExternalVocabularyURI(referencedVocabularyURI);
            setReadOnlyVocabulary(referencedVocabulary, clear);
        }
    }
    
    public void clear() {
        for (int i = 0; i < tables.length; i++) {
            tables[i].clear();
        }        
    }
}
