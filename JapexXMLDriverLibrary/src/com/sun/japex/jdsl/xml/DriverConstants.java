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

public final class DriverConstants {
    public static final String STRING_INTERNING_PROPERTY = "jdsl.stringInterning"; 
    
    public static final String DO_NOT_REPORT_SIZE = "jdsl.doNotReportSize";
    
    public static final String INDEXED_CONTENT_PROPERTY = "jdsl.indexedContent";
    public static final String INDEXED_CONTENT_PROPERTY_VALUE_DEFAULT = "default";
    public static final String INDEXED_CONTENT_PROPERTY_VALUE_FULL = "full"; 
    public static final String INDEXED_CONTENT_PROPERTY_VALUE_NONE = "none";
    
    public static final String CHARACTER_CONTENT_CHUNK_SIZE_LIMIY_PROPERTY = "jdsl.characterContentChunkSizeLimit"; 
    public static final String ATTRIBUTE_VALUE_SIZE_LIMIT_PROPERTY = "jdsl.attributeValueSizeLimit"; 

    public static final String EXTERNAL_VOCABULARY_PROPERTY = "jdsl.externalVocabulary";
    
    public static final String DEFER_NODE_EXPANSION = "jdsl.deferNodeExpansion";
    public static final String TRAVERSE_NODES = "jdsl.traverseNode";
    
    public static final String GZIP_PROPERTY = "jdsl.GZIP";
}
