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

package org.jvnet.fastinfoset;

import java.util.Map;

/**
 * A general interface for parsers of fast infoset documents.
 *
 * <p>
 * This interface contains common methods that are not specific to any
 * API associated with the parsing of fast infoset documents.
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public interface FastInfosetParser {
    /**
     * The property name to be used for getting and setting the string 
     * interning property of a parser.
     *
     */
    public static final String STRING_INTERNING_PROPERTY = 
        "http://jvnet.org/fastinfoset/parser/properties/string-interning";

    /**
     * The property name to be used for getting and setting the buffer size
     * of a parser.
     */
    public static final String BUFFER_SIZE_PROPERTY = 
        "http://jvnet.org/fastinfoset/parser/properties/buffer-size";

    /**
     * The property name to be used for getting and setting the 
     * Map containing encoding algorithms.
     *
     */    
    public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY =
        "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
    
   /**
     * The property name to be used for getting and setting the 
     * Map containing external vocabularies.
     *
     */    
    public static final String EXTERNAL_VOCABULARIES_PROPERTY =
        "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
    
   
    /**
     * Set the string interning property.
     *
     * <p>If the string interning property is set to true then 
     * <code>String</code> objects instantiated for [namespace name], [prefix] 
     * and [local name] infoset properties will be interned using the method 
     * {@link String#intern()}.
     *
     * @param stringInterning The string interning property.
     */
    public void setStringInterning(boolean stringInterning);
    
    /**
     * Return the string interning property.
     *
     * @return The string interning property.
     */
    public boolean getStringInterning();
    
    /**
     * Set the buffer size.
     *
     * <p>The size of the buffer for parsing is set using this
     * method. Requests for sizes smaller then the current size will be ignored.
     * Otherwise the buffer will be resized when the next parse is performed.<p>
     *
     * @param bufferSize The requested buffer size.
     */
    public void setBufferSize(int bufferSize);
    
    
    /**
     * Get the buffer size.
     *
     * @return The buffer size.
     */
    public int getBufferSize();
    

    /**
     * Sets the set of registered encoding algorithms.
     *
     * @param algorithms The set of registered algorithms.
     */
    public void setRegisteredEncodingAlgorithms(Map algorithms);
    
    /**
     * Gets the set of registered encoding algorithms.
     *
     * @return The set of registered algorithms.
     */
    public Map getRegisteredEncodingAlgorithms();

    /**
     * Set the map of referenced external vocabularies.
     * <p>
     * The map (but not the keys and values) be cloned.
     *
     * @param referencedVocabualries the map of URI to vocabulary.
     */
    public void setExternalVocabularies(Map referencedVocabualries);
    
    /**
     * Get the map of referenced external vocabularies.
     *
     * @return the map of URI to vocabulary.
     * @deprecated
     *     The map returned will not be the same instance and contain
     *     the same entries as the map set by {@link #setExternalVocabularies} 
     *     method.
     */
    public Map getExternalVocabularies();
    
    /**
     * Set the parse fragments property.
     *
     * <p>If the parse fragments property is set to true then 
     * then fragments of an XML infoset may be parsed.
     *
     * @param parseFragments The parse fragments property.
     */
    public void setParseFragments(boolean parseFragments);
    
    /**
     * Return the parse fragments property.
     *
     * @return The parse fragments property.
     */
    public boolean getParseFragments();
}
