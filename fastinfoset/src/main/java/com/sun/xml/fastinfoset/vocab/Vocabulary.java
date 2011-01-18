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


public abstract class Vocabulary {
    public static final int RESTRICTED_ALPHABET = 0;
    public static final int ENCODING_ALGORITHM = 1;
    public static final int PREFIX = 2;
    public static final int NAMESPACE_NAME = 3;
    public static final int LOCAL_NAME = 4;
    public static final int OTHER_NCNAME = 5;
    public static final int OTHER_URI = 6;
    public static final int ATTRIBUTE_VALUE = 7;
    public static final int OTHER_STRING = 8;
    public static final int CHARACTER_CONTENT_CHUNK = 9;
    public static final int ELEMENT_NAME = 10;
    public static final int ATTRIBUTE_NAME = 11;
        
    protected boolean _hasInitialReadOnlyVocabulary;
    
    protected String _referencedVocabularyURI;
    
    public boolean hasInitialVocabulary() {
        return _hasInitialReadOnlyVocabulary;
    }

    protected void setInitialReadOnlyVocabulary(boolean hasInitialReadOnlyVocabulary) {
        _hasInitialReadOnlyVocabulary = hasInitialReadOnlyVocabulary;
    }
    
    public boolean hasExternalVocabulary() {
        return _referencedVocabularyURI != null;
    }
    
    public String getExternalVocabularyURI() {
        return _referencedVocabularyURI;
    }
    
    protected void setExternalVocabularyURI(String referencedVocabularyURI) {
        _referencedVocabularyURI = referencedVocabularyURI;
    }

}
