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

package com.sun.xml.fastinfoset.api.sax;

import org.xml.sax.Attributes;


/**
 * Interface for a list of XML attributes that may contain encoding algorithm
 * data.
 *
 * @version 0.1
 * @see com.sun.xml.fastinfoset.api.sax.FastInfosetReader
 * @see org.xml.sax.XMLReader
 */
public interface EncodingAlgorithmAttributes extends Attributes {
    
    /**
     * Return the URI of the encoding algorithm.
     *
     * <p>If the encoding algorithm support is "None" then rull is returned.<p>
     *
     * <p> If "Generic" and "Primitive and application" then null is returned 
     * if the encoding algorithm data is for a built-in encoding algorithm and 
     * the URI is returned for an application defined encoding algorithm.<p>
     *
     * <p> If "Primitive" then null is returned.<p>
     *
     * @param index The attribute index (zero-based).
     * @return The URI.
     */
    public String getAlgorithmURI(int index);
 
    /**
     * Return the index of the encoding algorithm
     *
     * <p>If the encoding algorithm support is "None" then 0 is returned.<p>
     *
     * <p> If "Generic" and "Primitive and application" then the index of the
     * built-in or application defined encoding algorithm is returned.<p>
     *
     * <p> If "Primitive" then the index of the built-in encoding algorithm 
     * is returned, otherwise 0 is returned.<p>
     *
     * @param index The attribute index (zero-based).
     * @return The index
     * @see com.sun.xml.fastinfoset.api.EncodingAlgorithmIndexes       
     */
    public int getAlgorithmIndex(int index);
    
    /**
     * Return the data of the encoding algorithm.
     *
     * <p>If the encoding algorithm support is "None" then null is returned.<p>
     *
     * <p> If "Generic" then a byte[] object is returned for built-in and
     * application defined encoding algorithms.<p>
     *
     * <p> If "Primitive" then an object that is an array of the primitive type
     * is returned for a built-in encoding algorithm, otherwise null is
     * returned.<p>
     *
     * <p> If "Primitive and application" then an object that is an array of 
     * the primitive type is returned for a built-in encoding algorithm and a 
     * byte[] object is returned for application defined encoding algorithms.
     * <p>
     *
     * @param index The attribute index (zero-based).
     * @return The data
     * @see com.sun.xml.fastinfoset.api.EncodingAlgorithmIndexes       
     */
    public Object getAlgorithmData(int index);    
}
