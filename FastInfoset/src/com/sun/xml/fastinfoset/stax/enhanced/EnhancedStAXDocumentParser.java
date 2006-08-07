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

package com.sun.xml.fastinfoset.stax.enhanced;

import com.sun.xml.fastinfoset.DecoderStateTables;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;

/**
 * Enhanced Fast Infoset StAX parser.
 */
final public class EnhancedStAXDocumentParser extends StAXDocumentParser {
    /**
     * Peek at the next event.
     * 
     * @return the event, which will be the same as that returned from 
     *         {@link #next}.
     */
    public int peekNext() throws XMLStreamException {
        try {
            switch(DecoderStateTables.EII[peek()]) {
                case DecoderStateTables.EII_NO_AIIS_INDEX_SMALL:
                case DecoderStateTables.EII_AIIS_INDEX_SMALL:
                case DecoderStateTables.EII_INDEX_MEDIUM:
                case DecoderStateTables.EII_INDEX_LARGE:
                case DecoderStateTables.EII_LITERAL:
                case DecoderStateTables.EII_NAMESPACES:
                    return START_ELEMENT;
                case DecoderStateTables.CII_UTF8_SMALL_LENGTH:
                case DecoderStateTables.CII_UTF8_MEDIUM_LENGTH:
                case DecoderStateTables.CII_UTF8_LARGE_LENGTH:
                case DecoderStateTables.CII_UTF16_SMALL_LENGTH:
                case DecoderStateTables.CII_UTF16_MEDIUM_LENGTH:
                case DecoderStateTables.CII_UTF16_LARGE_LENGTH:
                case DecoderStateTables.CII_RA:
                case DecoderStateTables.CII_EA:
                case DecoderStateTables.CII_INDEX_SMALL:
                case DecoderStateTables.CII_INDEX_MEDIUM:
                case DecoderStateTables.CII_INDEX_LARGE:
                case DecoderStateTables.CII_INDEX_LARGE_LARGE:
                    return CHARACTERS;
                case DecoderStateTables.COMMENT_II:
                    return COMMENT;
                case DecoderStateTables.PROCESSING_INSTRUCTION_II:
                    return PROCESSING_INSTRUCTION;
                case DecoderStateTables.UNEXPANDED_ENTITY_REFERENCE_II:
                    return ENTITY_REFERENCE;
                case DecoderStateTables.TERMINATOR_DOUBLE:
                case DecoderStateTables.TERMINATOR_SINGLE:
                    return (_stackCount != -1) ? END_ELEMENT : END_DOCUMENT;
                default:
                    throw new FastInfosetException(
                            CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
            }
        } catch (IOException e) {
            throw new XMLStreamException(e);
        } catch (FastInfosetException e) {
            throw new XMLStreamException(e);
        }
    }
    
    // Faster access methods without checks
    
    public int _getNamespaceCount() {
        return (_currentNamespaceAIIsEnd > 0) ? (_currentNamespaceAIIsEnd - _currentNamespaceAIIsStart) : 0;
    }
    
    public String _getLocalName() {
        return _qualifiedName.localName;
    }
        
    public String _getNamespaceURI() {
        return _qualifiedName.namespaceName;
    }
    
    public String _getPrefix() {
        return _qualifiedName.prefix;
    }
    
    public char[] _getTextCharacters() {
        return _characters;
    }
    
    public int _getTextStart() {
        return _charactersOffset;
    }
    
    public int _getTextLength() {
        return _charBufferLength;
    }
    
}
