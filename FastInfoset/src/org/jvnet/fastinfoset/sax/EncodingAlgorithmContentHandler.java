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

package org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

/** 
 * SAX2 extention handler to receive notification of encoding algorithm data.
 *
 * <p>This is an optional extension handler for SAX2. XML readers are not 
 * required to recognize this handler, and it is not part of core-only 
 * SAX2 distributions.</p>
 *
 * <p>This interface may be used with with a Fast Infoset
 * SAX parser to receive notification of encoding algorithm data specified 
 * in ITU-T Rec. X.891 | ISO/IEC 24824-1 (Fast Infoset) clause 10 and for 
 * application defined encoding algorithms specified as part of the 
 * initial vocabulary of a fast infoset document.<p>
 *
 * <p>To set the EncodingAlgorithmContentHandler for an XML reader, use the
 * {@link org.xml.sax.XMLReader#setProperty setProperty} method
 * with the property name
 * <code>URI TO BE DEFINED</code>
 * and an object implementing this interface (or null) as the value.
 * If the reader does not report primitive data types, it will throw a
 * {@link org.xml.sax.SAXNotRecognizedException SAXNotRecognizedException}</p>
 *
 * <p>To set the EncodingAlgorithmContentHandler for an Fast Infoset reader, use
 * {@link com.sun.xml.fastinfoset.api.sax.FastInfosetReader#setEncodingAlgorithmContentHandler 
 *  setEncodingAlgorithmContentHandler} method.<p>
 *
 * @version 0.1
 * @see com.sun.xml.fastinfoset.api.sax.PrimitiveTypeContentHandler
 * @see com.sun.xml.fastinfoset.api.sax.FastInfosetReader
 * @see org.xml.sax.XMLReader
 */
public interface EncodingAlgorithmContentHandler {
    /**
     * Receive notification of encoding algorithm data as an array 
     * of byte.
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * <p>Such notifications will occur for a Fast Infoset SAX parser
     * when processing encoding algorithm data.<p>
     *
     * <p>The Parser will call the method of this interface to report each 
     * encoding algorithm data. Parsers MUST return all contiguous 
     * characters in a single chunk</p>
     *
     * <p>Parsers may return all contiguous bytes in a single chunk, or 
     * they may split it into several chunks providing that the length of 
     * each chunk is of the required length to successfully apply the 
     * encoding algorithm to the chunk.</p>
     *
     * @param URI the URI of the encoding algorithm
     * @param algorithm the encoding algorithm index
     * @param b the array of byte
     * @param start the start position in the array
     * @param length the number of byte to read from the array
     * @throws org.xml.sax.SAXException any SAX exception, possibly
     *            wrapping another exception
     * @see com.sun.xml.fastinfoset.api.EncodingAlgorithmIndexes
     */    
    public void octets(String URI, int algorithm, byte[] b, int start, int length)  throws SAXException;    

    /**
     * Receive notification of encoding algorithm data as an object.
     *
     * <p>Such notifications will occur for a Fast Infoset SAX parser
     * when processing encoding algorithm data that is converted from an 
     * array of byte to an object more suitable for processing.<p>
     * 
     * @param URI the URI of the encoding algorithm
     * @param algorithm the encoding algorithm index
     * @param o the encoding algorithm object
     * @throws org.xml.sax.SAXException any SAX exception, possibly
     *            wrapping another exception
     * @see com.sun.xml.fastinfoset.api.EncodingAlgorithmIndexes
     */    
    public void object(String URI, int algorithm, Object o)  throws SAXException;    
}