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


package com.sun.xml.fastinfoset;

import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayArray;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.StringArray;
import com.sun.xml.fastinfoset.vocab.ParserVocabulary;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jvnet.fastinfoset.FastInfosetException;

public abstract class Decoder {
    public static final int EA_NONE                     = 0;
    public static final int EA_GENERIC                  = 1;
    public static final int EA_PRIMITIVE                = 2;
    public static final int EA_PRIMITIVE_APPLICATION    = EA_GENERIC | EA_PRIMITIVE;
    
    protected InputStream _s;
    
    protected Map _externalVocabularies;
    
    protected ParserVocabulary _v;

    protected List _notations;

    protected List _unparsedEntities;
    
    protected int _b;
    
    protected boolean _terminate;
    
    protected boolean _doubleTerminate;

    protected boolean _elementWithAttributesNoChildrenTermination;

    protected boolean _addToTable;
    
    protected int _integer;
    
    protected int _identifier;
    
    protected byte[] _octetBuffer = new byte[1024];

    protected int _octetBufferStart;

    protected int _octetBufferOffset;

    protected int _octetBufferEnd;
    
    protected int _octetBufferLength;
    
    protected char[] _charBuffer = new char[512];
    
    protected int _charBufferLength;

    /** Creates a new instance of Decoder */
    public Decoder() {
        // TODO move the initial vocabulary outside of the parser
        _v = new ParserVocabulary();
    }

    public void reset() {
        _terminate = _doubleTerminate = _elementWithAttributesNoChildrenTermination = false;
    }

    
    public void setInputStream(InputStream s) {
        _s = s;
        _octetBufferOffset = 0;
        _octetBufferEnd = 0;
        _v.clear();
    }
        
    public final void decodeDII() throws FastInfosetException, IOException {        
        final int b = read();
        if (b == EncodingConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG) {
            decodeInitialVocabulary();
        } else if (b != 0) {
            throw new IOException("Optional values (other than initial vocabulary) of DII not supported");
        }        
    }
    
    public final void decodeInitialVocabulary() throws FastInfosetException, IOException {
        // First 5 optionals of 13 bit optional field
        int b = read();
        // Next 8 optionals of 13 bit optional field
        int b2 = read();
        
        // Optimize for the most common case
        if (b == EncodingConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG && b2 == 0) {
            decodeExternalVocabularyURI();
            return;
        }
        
        if ((b & EncodingConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG) > 0) {            
            decodeExternalVocabularyURI();
        }
        
        if ((b & EncodingConstants.INITIAL_VOCABULARY_RESTRICTED_ALPHABETS_FLAG) > 0) {            
            decodeTableItems(_v.restrictedAlphabet);
        }
        
        if ((b & EncodingConstants.INITIAL_VOCABULARY_ENCODING_ALGORITHMS_FLAG) > 0) {
            decodeTableItems(_v.encodingAlgorithm);
        }
        
        if ((b & EncodingConstants.INITIAL_VOCABULARY_PREFIXES_FLAG) > 0) {
            decodeTableItems(_v.prefix);
        }
        
        if ((b & EncodingConstants.INITIAL_VOCABULARY_NAMESPACE_NAMES_FLAG) > 0) {
            decodeTableItems(_v.namespaceName);
        }
        
        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_LOCAL_NAMES_FLAG) > 0) {
            decodeTableItems(_v.localName);
        }
        
        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_OTHER_NCNAMES_FLAG) > 0) {
            decodeTableItems(_v.otherNCName);
        }
        
        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_OTHER_URIS_FLAG) > 0) {
            decodeTableItems(_v.otherURI);
        }
        
        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_ATTRIBUTE_VALUES_FLAG) > 0) {
            decodeTableItems(_v.attributeValue);
        }
        
        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_CONTENT_CHARACTER_CHUNKS_FLAG) > 0) {
            decodeTableItems(_v.characterContentChunk);
        }
        
        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_OTHER_STRINGS_FLAG) > 0) {
            decodeTableItems(_v.otherString);
        }
        
        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_ELEMENT_NAME_SURROGATES_FLAG) > 0) {
            decodeTableItems(_v.elementName);
        }
        
        if ((b2 & EncodingConstants.INITIAL_VOCABULARY_ATTRIBUTE_NAME_SURROGATES_FLAG) > 0) {            
            decodeTableItems(_v.attributeName);
        }
    }

    public void decodeExternalVocabularyURI() throws FastInfosetException, IOException {
        if (_externalVocabularies == null) {
            throw new IOException("No external vocabularies registered");
        }
        
        String externalVocabularyURI = decodeNonEmptyOctetStringOnSecondBitAsUtf8String();
        ParserVocabulary externalVocabulary = 
            (ParserVocabulary) _externalVocabularies.get(externalVocabularyURI);
        if (externalVocabulary == null) {
            throw new FastInfosetException("External vocabulary referenced by \"" + externalVocabularyURI + "\" is not registered");
        }

        try {
            _v.setReferencedVocabulary(new URI(externalVocabularyURI), externalVocabulary, false);
        } catch (URISyntaxException e) {
            throw new FastInfosetException("URISyntaxException", e);
        }
    }
    
    public final void decodeTableItems(StringArray array) throws FastInfosetException, IOException {
        for (int i = 0; i < decodeIntegerTableItems(); i++) {
            array.add(decodeNonEmptyOctetStringOnSecondBitAsUtf8String());
        }
    }

    public final void decodeTableItems(CharArrayArray array) throws FastInfosetException, IOException {
        for (int i = 0; i < decodeIntegerTableItems(); i++) {
            switch(decodeNonIdentifyingStringOnFirstBit()) {
                case NISTRING_STRING:
                    array.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    break;
                default:
                    throw new FastInfosetException("Illegal state for decoding of EncodedCharacterString");
            }
        }
    }
    
    public final void decodeTableItems(QualifiedNameArray array) throws FastInfosetException, IOException {
        for (int i = 0; i < decodeIntegerTableItems(); i++) {
            final int b = read();

            final String prefix = ((b & EncodingConstants.NAME_SURROGATE_PREFIX_FLAG) > 0) 
                        ? _v.prefix.get(decodeIntegerIndexOnSecondBit()) : "";
            final String namespaceName = ((b & EncodingConstants.NAME_SURROGATE_NAME_FLAG) > 0) 
                        ? _v.namespaceName.get(decodeIntegerIndexOnSecondBit()) : "";
            if (namespaceName == "" && prefix != "") {
                throw new FastInfosetException("Name surrogate prefix is present when namespace name is absent");
            }

            final String localName = _v.localName.get(decodeIntegerIndexOnSecondBit());
            
            QualifiedName qualifiedName = new QualifiedName(prefix, namespaceName, localName);
            array.add(qualifiedName);
        }
    }
    
    public final int decodeIntegerTableItems() throws IOException {
        final int b = read();
        if (b < 128) {
            return b;
        } else {
            return ((b & 0x0F) << 16) | (read() << 8) | read();
        }
    }
    
    public final void decodeNotations() throws FastInfosetException, IOException {
        if (_notations == null) {
            _notations = new ArrayList();
        } else {
            _notations.clear();
        }
        
        int b = read();
        while ((b & EncodingConstants.NOTATIONS_MASK) == EncodingConstants.NOTATIONS) {
            String name = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);

            String system_identifier = ((_b & EncodingConstants.NOTATIONS_SYSTEM_IDENTIFIER_FLAG) > 0) 
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
            String public_identifier = ((_b & EncodingConstants.NOTATIONS_PUBLIC_IDENTIFIER_FLAG) > 0) 
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";

            Notation notation = new Notation(name, system_identifier, public_identifier);
            _notations.add(notation);
            
            b = read();
        }
        if (b != EncodingConstants.TERMINATOR) {
            throw new FastInfosetException("Notation IIs not terminated correctly");
        }
    }
    
    public final void decodeUnparsedEntities() throws FastInfosetException, IOException {
        if (_unparsedEntities == null) {
            _unparsedEntities = new ArrayList();
        } else {
            _unparsedEntities.clear();
        }
        
        int b = read();
        while ((b & EncodingConstants.UNPARSED_ENTITIES_MASK) == EncodingConstants.UNPARSED_ENTITIES) {
            String name = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
            String system_identifier = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI);

            String public_identifier = ((_b & EncodingConstants.UNPARSED_ENTITIES_PUBLIC_IDENTIFIER_FLAG) > 0) 
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";

            String notation_name = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);

            UnparsedEntity unparsedEntity = new UnparsedEntity(name, system_identifier, public_identifier, notation_name);
            _unparsedEntities.add(unparsedEntity);
            
            b = read();
        }
        if (b != EncodingConstants.TERMINATOR) {
            throw new FastInfosetException("Unparsed entities not terminated correctly");
        }
    }
    
    public final void decodeVersion() throws FastInfosetException, IOException {
        switch(decodeNonIdentifyingStringOnFirstBit()) {
        case NISTRING_STRING:
            if (_addToTable) {
                _v.otherString.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
            }
            break;
        case NISTRING_ENCODING_ALGORITHM:
            throw new IOException("Processing II with encoding algorithm decoding not supported");                        
        case NISTRING_INDEX:
            break;
        case NISTRING_EMPTY_STRING:
            break;
        }
    }
    
    public static final int NISTRING_STRING              = 0;
    public static final int NISTRING_INDEX               = 1;
    public static final int NISTRING_ENCODING_ALGORITHM  = 2;
    public static final int NISTRING_EMPTY_STRING        = 3;
        
    /*
     * C.14
     * decodeNonIdentifyingStringOnFirstBit
     */
    public final int decodeNonIdentifyingStringOnFirstBit() throws FastInfosetException, IOException {        
        final int b = read();
        switch(DecoderStateTables.NISTRING[b]) {
            case DecoderStateTables.NISTRING_UTF8_SMALL_LENGTH:
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                _octetBufferLength = (b & EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_MASK) + 1;
                decodeUtf8StringAsCharBuffer();
                return NISTRING_STRING;
            case DecoderStateTables.NISTRING_UTF8_MEDIUM_LENGTH:
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT;
                decodeUtf8StringAsCharBuffer();
                return NISTRING_STRING;
            case DecoderStateTables.NISTRING_UTF8_LARGE_LENGTH:
            {
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
                decodeUtf8StringAsCharBuffer();
                return NISTRING_STRING;
            }
            case DecoderStateTables.NISTRING_UTF16_SMALL_LENGTH:
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                _octetBufferLength = (b & EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_MASK) + 1;
                decodeUtf16StringAsCharBuffer();
                return NISTRING_STRING;
            case DecoderStateTables.NISTRING_UTF16_MEDIUM_LENGTH:
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT;
                decodeUtf16StringAsCharBuffer();
                return NISTRING_STRING;
            case DecoderStateTables.NISTRING_UTF16_LARGE_LENGTH:
            {
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
                decodeUtf16StringAsCharBuffer();
                return NISTRING_STRING;
            }
            case DecoderStateTables.NISTRING_RA:
            {
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                // Decode resitricted alphabet integer
                _identifier = (b & 0x0F) << 4;
                final int b2 = read();
                _identifier |= (b2 & 0xF0) >> 4;
    
                decodeOctetsOfNonIdentifyingStringOnFirstBit(b2);
                // TODO obtain restricted alphabet given _identifier value
                decodeRAOctetsAsCharBuffer(null);
                return NISTRING_STRING;                
            }
            case DecoderStateTables.NISTRING_EA:
            {
                _addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                // Decode encoding algorithm integer
                _identifier = (b & 0x0F) << 4;
                final int b2 = read();
                _identifier |= (b2 & 0xF0) >> 4;
    
                decodeOctetsOfNonIdentifyingStringOnFirstBit(b2);
                return NISTRING_ENCODING_ALGORITHM;
            }
            case DecoderStateTables.NISTRING_INDEX_SMALL:
                _integer = b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
                return NISTRING_INDEX;
            case DecoderStateTables.NISTRING_INDEX_MEDIUM:
                _integer = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read()) 
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                return NISTRING_INDEX;
            case DecoderStateTables.NISTRING_INDEX_LARGE:
                _integer = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read()) 
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                return NISTRING_INDEX;
            case DecoderStateTables.NISTRING_EMPTY:
                return NISTRING_EMPTY_STRING;
            default:
                throw new FastInfosetException("Illegal state when decoding non identifying string");
        }
    }    
    
    public final void decodeOctetsOfNonIdentifyingStringOnFirstBit(int b) throws FastInfosetException, IOException {
        // Remove lower bits of restricted alphabet or encoding algorithm integer 
        b &= 0x0F;
        // Reuse UTF8 length states
        switch(DecoderStateTables.NISTRING[b]) {
            case DecoderStateTables.NISTRING_UTF8_SMALL_LENGTH:
                _octetBufferLength = b + 1;
                break;
            case DecoderStateTables.NISTRING_UTF8_MEDIUM_LENGTH:
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT;
                break;
            case DecoderStateTables.NISTRING_UTF8_LARGE_LENGTH:
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
                break;
            default:
                throw new FastInfosetException("Illegal state when decoding octets");
        }
        _octetBufferStart = _octetBufferOffset;
        ensureOctetBufferSize();
        _octetBufferOffset += _octetBufferLength;
    }

    public final void decodeOctetsOfNonIdentifyingStringOnThirdBit(int b) throws FastInfosetException, IOException {
        // Remove lower bits of restricted alphabet or encoding algorithm integer 
        b &= 0x02;
        // Reuse UTF8 length states, necessary to mask with CII identifier bits
        switch(DecoderStateTables.EII[b | 0x80]) {
            case DecoderStateTables.CII_UTF8_SMALL_LENGTH:
                _octetBufferLength = b + 1;
                break;
            case DecoderStateTables.CII_UTF8_MEDIUM_LENGTH:
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT;
                break;
            case DecoderStateTables.CII_UTF8_LARGE_LENGTH:
                _octetBufferLength = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength += EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
                break;
            default:
                throw new FastInfosetException("Illegal state when decoding octets");
        }
        _octetBufferStart = _octetBufferOffset;
        ensureOctetBufferSize();
        _octetBufferOffset += _octetBufferLength;
    }
    
    /*
     * C.13
     */
    public final String decodeIdentifyingNonEmptyStringOnFirstBit(StringArray table) throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING[b]) {
            case DecoderStateTables.ISTRING_SMALL_LENGTH:
            {
                _octetBufferLength = b + 1;
                final String s = decodeUtf8StringAsString();
                table.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_MEDIUM_LENGTH:
            {
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT;
                final String s = decodeUtf8StringAsString();
                table.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_LARGE_LENGTH:
            {
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT;
                final String s = decodeUtf8StringAsString();
                table.add(s);
                return s;
            }
            case DecoderStateTables.ISTRING_INDEX_SMALL:
                return table.get(b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK);
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
            {
                final int index = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read()) 
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                return table.get(index);
            }
            case DecoderStateTables.ISTRING_INDEX_LARGE:
            {
                final int index = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read()) 
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                return table.get(index);
            }
            default:
                throw new FastInfosetException("Illegal state when decoding identifying string on first bit");
        }
    }
    
    /*
     * C.22
     */
    public final String decodeNonEmptyOctetStringOnSecondBitAsUtf8String() throws FastInfosetException, IOException {
        decodeNonEmptyOctetStringOnSecondBitAsUtf8CharArray();
        return new String(_charBuffer, 0, _charBufferLength);
    }

    /*
     * C.22
     */
    public final void decodeNonEmptyOctetStringOnSecondBitAsUtf8CharArray() throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING[b]) {
            case DecoderStateTables.ISTRING_SMALL_LENGTH:
                _octetBufferLength = b + 1;
                break;
            case DecoderStateTables.ISTRING_MEDIUM_LENGTH:
                _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT;
                break;
            case DecoderStateTables.ISTRING_LARGE_LENGTH:
            {
                final int length = (read() << 24) |
                    (read() << 16) |
                    (read() << 8) |
                    read();
                _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT;
                break;
            }
            case DecoderStateTables.ISTRING_INDEX_SMALL:
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
            case DecoderStateTables.ISTRING_INDEX_LARGE:
            default:
                throw new FastInfosetException("Illegal state when decoding non empty octet string on second bit");
        }
        decodeUtf8StringAsCharBuffer();
    }

    /*
     * C.25
     */
    public final int decodeIntegerIndexOnSecondBit() throws FastInfosetException, IOException {
        final int b = read();
        switch(DecoderStateTables.ISTRING[b]) {                
            case DecoderStateTables.ISTRING_INDEX_SMALL:
                return b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK;
            case DecoderStateTables.ISTRING_INDEX_MEDIUM:
                return (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read()) 
                    + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
            case DecoderStateTables.ISTRING_INDEX_LARGE:
                return (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read()) 
                    + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
            case DecoderStateTables.ISTRING_SMALL_LENGTH:
            case DecoderStateTables.ISTRING_MEDIUM_LENGTH:
            case DecoderStateTables.ISTRING_LARGE_LENGTH:
            default:
                throw new FastInfosetException("Illegal state when decoding index on second bit");
        }
    }
    
    public final void decodeHeader() throws FastInfosetException, IOException {
        if (!_isFastInfosetDocument()) {
            throw new FastInfosetException("Input stream is not a fast infoset document");
        }
    }
    
    public final void decodeRAOctetsAsCharBuffer(char[] restrictedAlphabet) throws IOException {
        throw new UnsupportedOperationException("Restricted alphabet decoding not implemented");
    }

    public final String decodeRAOctetsAsString(char[] restrictedAlphabet) throws IOException {
        decodeRAOctetsAsCharBuffer(null);        
        return new String(_charBuffer, 0, _charBufferLength);
    }
    
    public final void decodeUtf8StringAsCharBuffer() throws IOException {    
        ensureOctetBufferSize();
        decodeUtf8StringIntoCharBuffer();
    }

    public final String decodeUtf8StringAsString() throws IOException {
        decodeUtf8StringAsCharBuffer();        
        return new String(_charBuffer, 0, _charBufferLength);
    }

    public final void decodeUtf16StringAsCharBuffer() throws IOException {    
        ensureOctetBufferSize();
        decodeUtf16StringIntoCharBuffer();
    }

    public final String decodeUtf16StringAsString() throws IOException {
        decodeUtf16StringAsCharBuffer();        
        return new String(_charBuffer, 0, _charBufferLength);
    }
    
    public final void ensureOctetBufferSize() throws IOException {
        if (_octetBufferEnd < (_octetBufferOffset + _octetBufferLength)) {
            final int bytesRemaining = _octetBufferEnd - _octetBufferOffset;

            if (_octetBuffer.length < _octetBufferLength) {
                byte[] newOctetBuffer = new byte[_octetBufferLength];
                System.arraycopy(_octetBuffer, _octetBufferOffset, newOctetBuffer, 0, bytesRemaining);
                _octetBuffer = newOctetBuffer;
            } else {
                System.arraycopy(_octetBuffer, _octetBufferOffset, _octetBuffer, 0, bytesRemaining);
            }
            _octetBufferOffset = 0;
            
            final int bytesRead = _s.read(_octetBuffer, bytesRemaining, _octetBuffer.length - bytesRemaining);            
            if (bytesRead < 0) {
                throw new EOFException("Unexpeceted EOF");
            }
                        
            if (bytesRead < _octetBufferLength - bytesRemaining) {
                // TODO keep reading until require bytes have been obtained
                throw new IOException("Full bytes not read");
            }
            
            _octetBufferEnd = bytesRemaining + bytesRead;
        }
    }
    
    public final void decodeUtf8StringIntoCharBuffer() {        
        _charBufferLength = 0;
        if (_charBuffer.length < _octetBufferLength) {
            _charBuffer = new char[_octetBufferLength];
        }

        final int end = _octetBufferLength + _octetBufferOffset;
        while (end != _octetBufferOffset) {
            int c = _octetBuffer[_octetBufferOffset++] & 0xFF;

            if ((c & 0x80) == 0) {         // up to 7 bits
                _charBuffer[_charBufferLength++] = (char) c;
            }
            else if ((c & 0x20) == 0) {    // up to 11 bits
                _charBuffer[_charBufferLength++] = (char) (
                    ((c & 0x1F) << 6) 
                    | (_octetBuffer[_octetBufferOffset++] & 0x3F));
            }
            else {                         // up to 16 bits
                _charBuffer[_charBufferLength++] = (char) (
                    (c & 0x0F) << 12
                    | (_octetBuffer[_octetBufferOffset++] & 0x3F) << 6
                    | (_octetBuffer[_octetBufferOffset++] & 0x3F));
            }
        }
    }

    public final void decodeUtf16StringIntoCharBuffer() {
        throw new UnsupportedOperationException("UTF-16 decoding not implemented");
    }

    protected final int read() throws IOException {
        if (_octetBufferOffset < _octetBufferEnd) {
            return _octetBuffer[_octetBufferOffset++] & 0xFF;
        } else {
            _octetBufferEnd = _s.read(_octetBuffer);
            if (_octetBufferEnd < 0) {
                throw new EOFException("Unexpeceted EOF");
            }
            
            _octetBufferOffset = 1;
            return _octetBuffer[0] & 0xFF;
        }
    }

    protected final boolean _isFastInfosetDocument() throws IOException {
        // TODO
        // Check for <?xml declaration with 'finf' encoding
        
        if (read() != (EncodingConstants.HEADER[0] & 0xFF) ||
                read() != (EncodingConstants.HEADER[1] & 0xFF) ||
                read() != (EncodingConstants.HEADER[2] & 0xFF) ||
                read() != (EncodingConstants.HEADER[3] & 0xFF)) {
            return false;
        }
        
        // TODO 
        return true;
    }

    
    static public boolean isFastInfosetDocument(InputStream s) throws IOException {
        // TODO
        // Check for <?xml declaration with 'finf' encoding
        
        final byte[] header = new byte[4];
        s.read(header);
        if (header[0] != EncodingConstants.HEADER[0] ||
                header[1] != EncodingConstants.HEADER[1] ||
                header[2] != EncodingConstants.HEADER[2] ||
                header[3] != EncodingConstants.HEADER[3]) {
            return false;
        }
        
        // TODO 
        return true;
    }


/*                                             
    protected final static int CHAR_POOL_CHAR_ARRAY_SIZE = 256;

    protected char[][] _charPool = new char[4][];

    protected char[] _charPoolCharArray;

    protected int _charPoolIndex;

    protected int _charPoolCharArrayStart;

    protected int _charPoolCharArrayLength;

    protected int _charPoolCharArrayOffset;

    protected CharArray[] _charArrayPool = new CharArray[256];

    protected int _charArrayPoolIndex;

    public Decoder() {
        // TODO move the initial vocabulary outside of the parser
        _v = new ParserVocabulary();
                                                                                                                      
        for (int i = 0; i < _charPool.length; i++) {
            _charPool[i] = new char[CHAR_POOL_CHAR_ARRAY_SIZE];
        }
                                                                                                                      
    }

    public void reset() {
        _terminate = _doubleTerminate = _elementWithAttributesNoChildrenTermination = false;
        _octetBufferOffset = 0;
        _octetBufferEnd = 0;
                                                                                                                      
        _charPoolIndex = 0;
        _charPoolCharArrayOffset = 0;
        _charPoolCharArray = _charPool[0];
                                                                                                                      
        _charArrayPoolIndex = 0;
    }


    // Code for decoding CII that is indexed or note

                if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                    if (_octetBufferLength < 16) {
                        decodeUtf8StringIntoCharPool();
                        _v.characterContentChunk.add(getCharArray(_charPoolCharArray, _charPoolCharArrayStart, _charPoolCharArrayLength));
                        processCII(_charPoolCharArray, _charPoolCharArrayStart, _charPoolCharArrayLength);
                    } else {
                        decodeUtf8StringAsCharBuffer();
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, false));
                        processCII(_charBuffer, 0, _charBufferLength);
                    }
                } else {
                    decodeUtf8StringAsCharBuffer();
                    processCII(_charBuffer, 0, _charBufferLength);
                }

    public CharArray getCharArray(char[] ch, int start, int offset) {
        if (_charArrayPoolIndex == _charArrayPool.length) {
            CharArray[] charArrayPool = new CharArray[_charArrayPoolIndex * 3 / 2];
            System.arraycopy(_charArrayPool, 0, charArrayPool, 0, _charArrayPoolIndex);
            _charArrayPool = charArrayPool;
        }
        CharArray ca = _charArrayPool[_charArrayPoolIndex++];
        if (ca == null) {
            ca = new CharArray(ch, start, offset, false);
            _charArrayPool[_charArrayPoolIndex - 1] = ca;
        } else {
            ca.set(ch, start, offset, false);
        }
                                                                                                                         
        return ca;
    }

    public final void decodeUtf8StringIntoCharPool() throws IOException {
        ensureOctetBufferSize();
                                                                                                                      
        if (_charPoolCharArrayOffset + _octetBufferLength >= CHAR_POOL_CHAR_ARRAY_SIZE) {
            if (++_charPoolIndex == _charPool.length) {
                char[][] charPool = new char[_charPoolIndex * 3 / 2][];
                System.arraycopy(_charPool, 0, charPool, 0, _charPoolIndex);
                _charPool = charPool;
                for (int i = _charPoolIndex; i < _charPool.length; i++) {
                    _charPool[i] = new char[CHAR_POOL_CHAR_ARRAY_SIZE];
                }
            }
            _charPoolCharArrayOffset = 0;
            _charPoolCharArray = _charPool[_charPoolIndex];
        }
                                                                                                                      
        _charPoolCharArrayStart = _charPoolCharArrayOffset;
        _charPoolCharArrayOffset = decodeUtf8StringIntoCharPool(_charPoolCharArray, _charPoolCharArrayOffset);
        _charPoolCharArrayLength = _charPoolCharArrayOffset - _charPoolCharArrayStart;
    }

    public final int decodeUtf8StringIntoCharPool(char charBuffer[], int charOffset) {
        final int end = _octetBufferLength + _octetBufferOffset;
        while (end != _octetBufferOffset) {
            final int c = _octetBuffer[_octetBufferOffset++] & 0xFF;
                                                                                                                      
            if ((c & 0x80) == 0) {         // up to 7 bits
                charBuffer[charOffset++] = (char) c;
            }
            else if ((c & 0x20) == 0) {    // up to 11 bits
                charBuffer[charOffset++] = (char) (
                    ((c & 0x1F) << 6)
                    | (_octetBuffer[_octetBufferOffset++] & 0x3F));
            }
            else {                         // up to 16 bits
                charBuffer[charOffset++] = (char) (
                    (c & 0x0F) << 12
                    | (_octetBuffer[_octetBufferOffset++] & 0x3F) << 6
                    | (_octetBuffer[_octetBufferOffset++] & 0x3F));
            }
        }
        return charOffset;
    }

*/
}
