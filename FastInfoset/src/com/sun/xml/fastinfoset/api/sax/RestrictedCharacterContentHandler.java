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

import org.xml.sax.SAXException;

/** 
 * SAX2 extention handler to receive notification of character data that
 * is restricted to a subset of characters.
 *
 * <p>This is an optional extension handler for SAX2. XML readers are not 
 * required to recognize this handler, and it is not part of core-only 
 * SAX2 distributions.</p>
 *
 * <p>This interface may be used with with a Fast Infoset
 * SAX parser to receive notification of data encoded using the
 * restricted alphabets and built-in encoding algorithms specified 
 * in ITU-T Rec. X.891 | ISO/IEC 24824-1 (Fast Infoset), clauses 9 and
 * 10.<p>
 *
 * <p>To set the RestrictedCharacterContentHandler for an XML reader, use the
 * {@link org.xml.sax.XMLReader#setProperty setProperty} method
 * with the property name
 * <code>URI TO BE DEFINED</code>
 * and an object implementing this interface (or null) as the value.
 * If the reader does not report primitive data types, it will throw a
 * {@link org.xml.sax.SAXNotRecognizedException SAXNotRecognizedException}</p>
 *
 * <p>The Parser will call methods of this interface to report each 
 * chunk of character data that is restricted, for example hexadecimal
 * characters or numeric characters. Parsers may return all contiguous characters
 * in a single chunk, or they may split it into several chunks</p>
 *
 * @version 0.1
 * @see org.xml.sax.XMLReader
 * @see org.xml.sax.ContentHandler
 */
public interface RestrictedCharacterContentHandler {
    /**
     * Receive notification of character data restricted to hexadecimal
     * characters.
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * <p>The character data is restricted to sixteen ISO/IEC 10646 
     * characters: DIGIT ZERO to DIGIT NINE, LATIN CAPTIAL LETTER A
     * to LATIN CAPTIAL LETTER F.<p>
     *
     * <p>Such notifications will occur for a Fast Infoset SAX parser
     * when processing data encoded using the "hexadicmal" encoding
     * algorithm, see subclause 10.2.<p>
     *
     * @param ch the array of char
     * @param start the start position in the array
     * @param length the number of char to read from the array
     * @throws org.xml.sax.SAXException any SAX exception, possibly
     *            wrapping another exception
     */    
    void hexadecimalCharacters(char[] ch, int start, int length) throws SAXException;
    
    /**
     * Receive notification of character data restricted to numeric
     * characters.
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * <p>The character data is restricted to fifteen ISO/IEC 10646 
     * characters: DIGIT ZERO to DIGIT NINE, HYPHEN MINUS, PLUS SIGN, 
     * FULL STOP, LATIN SMALL LETTER E, SPACE.<p>
     *
     * <p>Such notifications will occur for a Fast Infoset SAX parser
     * when processing data encoded using the "numeric" restricted
     * alphabet, see subclause 9.1.<p>
     *
     * @param ch the array of char
     * @param start the start position in the array
     * @param length the number of char to read from the array
     * @throws org.xml.sax.SAXException any SAX exception, possibly
     *            wrapping another exception
     */    
    void numericCharacters(char[] ch, int start, int length)  throws SAXException;
    
    /**
     * Receive notification of character data restricted to date
     * time characters.
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * <p>The character data is restricted to fifteen ISO/IEC 10646 
     * characters: DIGIT ZERO to DIGIT NINE, HYPHEN MINUS, COLON, 
     * LATIN CAPTIAL LETTER T, LATIN CAPTIAL LETTER Z, SPACE.<p>
     *
     * <p>Such notifications will occur for a Fast Infoset SAX parser
     * when processing data encoded using the "date and time" restricted
     * alphabet, see subclause 9.2<p>
     *
     * @param ch the array of char
     * @param start the start position in the array
     * @param length the number of char to read from the array
     * @throws org.xml.sax.SAXException any SAX exception, possibly
     *            wrapping another exception
     */    
    void dateTimeCharacters(char[] ch, int start, int length)  throws SAXException;    
}
