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
 * A general interface for serializers of fast infoset documents.
 *
 * <p>
 * This interface contains common methods that are not specific to any
 * API associated with the serialization of XML Infoset to fast infoset
 * documents.
 * 
 * @author Paul.Sandoz@Sun.Com
 */
public interface FastInfosetSerializer {
    /**
     * The default value for the limit on the size of character content chunks
     * that will be indexed.
     */
    public final static int CHARACTER_CONTENT_CHUNK_SIZE_CONSTRAINT = 7;
    
    /**
     * The default value for the limit on the size of attribute values
     * that will be indexed.
     */
    public final static int ATTRIBUTE_VALUE_SIZE_CONSTRAINT = 7;
    
    /**
     * The character encoding scheme string for UTF-8.
     */
    public static final String UTF_8 = "UTF-8";
    
    /**
     * The character encoding scheme string for UTF-16BE.
     */
    public static final String UTF_16BE = "UTF-16BE";
    
    /**
     * Sets the character encoding scheme.
     *
     * The character encoding can be either UTF-8 or UTF-16BE for the
     * the encoding of chunks of CIIs, the [normalized value]
     * property of attribute information items, comment information
     * items and processing instruction information items.
     *
     * @param characterEncodingScheme The set of registered algorithms.
     */
    public void setCharacterEncodingScheme(String characterEncodingScheme);
    
    /**
     * Gets the character encoding scheme.
     *
     * @return The character encoding scheme.
     */
    public String getCharacterEncodingScheme();
    
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
     * Sets the limit on the size of character content chunks
     * that will be indexed.
     *
     * @param size The character content chunk size limit. Any chunk less
     * that a length of size limit will be indexed.
     */
    public void setCharacterContentChunkSizeLimit(int size);
    
    /**
     * Gets the limit on the size of character content chunks
     * that will be indexed.
     *
     * @return The character content chunk size limit.
     */
    public int getCharacterContentChunkSizeLimit();

    /**
     * Sets the limit on the size of attribute values
     * that will be indexed.
     *
     * @param size The attribute value size limit. Any value less
     * that a length of size limit will be indexed.
     */
    public void setAttributeValueSizeLimit(int size);
    
    /**
     * Gets the limit on the size of attribute values
     * that will be indexed.
     *
     * @return The attribute value size limit.
     */
    public int getAttributeValueSizeLimit();

    /**
     * Set the external vocabulary that shall be used when serializing.
     * 
     * @param v the vocabulary. 
     */
    public void setExternalVocabulary(ExternalVocabulary v);    
}