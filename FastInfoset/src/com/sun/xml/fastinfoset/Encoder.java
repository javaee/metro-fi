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

import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.fastinfoset.util.KeyIntMap;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.StringIntMap;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.FastInfosetSerializer;
import org.jvnet.fastinfoset.ReferencedVocabulary;
import org.jvnet.fastinfoset.Vocabulary;
import org.xml.sax.helpers.DefaultHandler;

public abstract class Encoder extends DefaultHandler implements FastInfosetSerializer {
    protected int _characterCount = 0;

    protected Map _registeredEncodingAlgorithms = new HashMap();

    protected SerializerVocabulary _v;

    protected boolean _vIsInternal;

    protected boolean _terminate = false;

    protected int _b;

    protected OutputStream _s;

    protected char[] _charBuffer = new char[512];

    protected byte[] _octetBuffer = new byte[1024];
    
    protected int _octetBufferIndex;
   
    protected int _markIndex = -1;

    
    // FastInfosetSerializer

    public void reset() {
        _terminate = false;
    }
    
    public void setRegisteredEncodingAlgorithms(Map algorithms) {
        _registeredEncodingAlgorithms = algorithms;
        if (_registeredEncodingAlgorithms == null) {
            _registeredEncodingAlgorithms = new HashMap();
        }
    }

    public Map getRegisteredEncodingAlgorithms() {
        return _registeredEncodingAlgorithms;
    }

    public void setExternalVocabulary(ReferencedVocabulary referencedVocabulary) {
        throw new UnsupportedOperationException();
    }

    public void setIntitialVocabulary(Vocabulary initialVocabulary) {
        throw new UnsupportedOperationException();
    }

    public void setDynamicVocabulary(Vocabulary dynamicVocabulary) {
        throw new UnsupportedOperationException();
    }

    public Vocabulary getDynamicVocabulary() {
        throw new UnsupportedOperationException();
    }

    public Vocabulary getFinalVocabulary() {
        throw new UnsupportedOperationException();
    }

    
    
    public void setOutputStream(OutputStream s) {
        _octetBufferIndex = 0;
        _markIndex = -1;
        _s = s;
    }

    public void setVocabulary(SerializerVocabulary vocabulary) {
        _v = vocabulary;
        _vIsInternal = false;
    }

    protected final void encodeHeader(boolean encodeXmlDecl) throws IOException {
        if (encodeXmlDecl) {
            _s.write(EncodingConstants.XML_DECL);
        }
        _s.write(EncodingConstants.HEADER);
    }

    protected final void encodeInitialVocabulary() throws IOException {
        if (_v == null) {
            _v = new SerializerVocabulary();
            _vIsInternal = true;
        } else if (_vIsInternal) {
            _v.clear();
        }

        if (_v.hasInitialVocabulary()) {
            _b = EncodingConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG;
            write(_b);

            SerializerVocabulary initialVocabulary = _v.getReadOnlyVocabulary();

            // TODO check for contents of vocabulary to assign bits
            if (initialVocabulary.hasExternalVocabulary()) {
                _b = EncodingConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG;
                write(_b);
                write(0);
            }

            if (initialVocabulary.hasExternalVocabulary()) {
                encodeNonEmptyOctetStringOnSecondBit(_v.getExternalVocabularyURI().toString());
            }

            // TODO check for contents of vocabulary to encode values
        } else if (_v.hasExternalVocabulary()) {
            _b = EncodingConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG;
            write(_b);

            _b = EncodingConstants.INITIAL_VOCABULARY_EXTERNAL_VOCABULARY_FLAG;
            write(_b);
            write(0);

            encodeNonEmptyOctetStringOnSecondBit(_v.getExternalVocabularyURI().toString());
        } else {
            write(0);
        }
    }

    protected final void encodeDocumentTermination() throws IOException {
        encodeElementTermination();
        encodeTermination();
        _flush();
        _s.flush();
    }

    protected final void encodeElementTermination() throws IOException {
        _terminate = true;
        switch (_b) {
            case EncodingConstants.TERMINATOR:
                _b = EncodingConstants.DOUBLE_TERMINATOR;
                break;
            case EncodingConstants.DOUBLE_TERMINATOR:
                write(EncodingConstants.DOUBLE_TERMINATOR);
            default:
                _b = EncodingConstants.TERMINATOR;
        }
    }

    protected final void encodeTermination() throws IOException {
        if (_terminate) {
            write(_b);
            _terminate = false;
        }
    }

    protected final void encodeNamespaceAttribute(String prefix, String uri) throws IOException {
        _b = EncodingConstants.NAMESPACE_ATTRIBUTE;
        if (prefix != "") {
            _b |= EncodingConstants.NAMESPACE_ATTRIBUTE_PREFIX_FLAG;
        }
        if (uri != "") {
            _b |= EncodingConstants.NAMESPACE_ATTRIBUTE_NAME_FLAG;
        }

        // NOTE a prefix with out a namespace name is an undeclaration
        // of the namespace bound to the prefix
        // TODO needs to investigate how the startPrefixMapping works in
        // relation to undeclaration

        write(_b);

        if (prefix != "") {
            encodeIdentifyingNonEmptyStringOnFirstBit(prefix, _v.prefix);
        }
        if (uri != "") {
            encodeIdentifyingNonEmptyStringOnFirstBit(uri, _v.namespaceName);
        }
    }

    protected final void encodeCharacters(char[] ch, int start, int length) throws IOException {
       _b = EncodingConstants.CHARACTER_CHUNK;

        boolean addToTable = (length < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnThirdBit(ch, start, length, _v.characterContentChunk, addToTable, true);
    }

    protected final void encodeCharactersNoClone(char[] ch, int start, int length) throws IOException {
       _b = EncodingConstants.CHARACTER_CHUNK;

        boolean addToTable = (length < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnThirdBit(ch, start, length, _v.characterContentChunk, addToTable, false);
    }

    protected final void encodeProcessingInstruction(String target, String data) throws IOException {
        write(EncodingConstants.PROCESSING_INSTRUCTION);

        // Target
        encodeIdentifyingNonEmptyStringOnFirstBit(target, _v.otherNCName);

        // Data
        boolean addToTable = (data.length() < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnFirstBit(data, _v.otherString, addToTable);
    }

    protected final void encodeComment(char[] ch, int start, int length) throws IOException {
        write(EncodingConstants.COMMENT);

        boolean addToTable = (length < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnFirstBit(ch, start, length, _v.otherString, addToTable, true);
    }

    protected final void encodeCommentNoClone(char[] ch, int start, int length) throws IOException {
        write(EncodingConstants.COMMENT);

        boolean addToTable = (length < _v.characterContentChunkSizeContraint) ? true : false;
        encodeNonIdentifyingStringOnFirstBit(ch, start, length, _v.otherString, addToTable, false);
    }

    /*
     * C.18
     */
    protected final void encodeElementQualifiedNameOnThirdBit(String namespaceURI, String prefix, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.elementName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                if ((prefix == names[i].prefix || prefix.equals(names[i].prefix))
                        && (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                    encodeNonZeroIntegerOnThirdBit(names[i].index);
                    return;
                }
            }
        }

        encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, prefix,
                localName, entry);
    }

    /*
     * C.18
     */
    protected final void encodeLiteralElementQualifiedNameOnThirdBit(String namespaceURI, String prefix, String localName,
            LocalNameQualifiedNamesMap.Entry entry) throws IOException {
        QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, "", _v.elementName.getNextIndex());
        entry.addQualifiedName(name);

        int namespaceURIIndex = KeyIntMap.NOT_PRESENT;
        int prefixIndex = KeyIntMap.NOT_PRESENT;
        if (namespaceURI != "") {
            namespaceURIIndex = _v.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == KeyIntMap.NOT_PRESENT) {
                throw new IOException("namespace URI of local name not indexed: " + namespaceURI);
            }

            if (prefix != "") {
                prefixIndex = _v.prefix.get(prefix);
                if (prefixIndex == KeyIntMap.NOT_PRESENT) {
                    throw new IOException("prefix of local name not indexed: " + prefix);
                }
            }
        }

        int localNameIndex = _v.localName.obtainIndex(localName);

        _b |= EncodingConstants.ELEMENT_LITERAL_QNAME_FLAG;
        if (namespaceURIIndex >= 0) {
            _b |= EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG;
            if (prefixIndex >= 0) {
                _b |= EncodingConstants.LITERAL_QNAME_PREFIX_FLAG;
            }
        }
        write(_b);

        if (namespaceURIIndex >= 0) {
            if (prefixIndex >= 0) {
                encodeNonZeroIntegerOnSecondBitFirstBitOne(prefixIndex);
            }
            encodeNonZeroIntegerOnSecondBitFirstBitOne(namespaceURIIndex);
        }

        if (localNameIndex >= 0) {
            encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        } else {
            encodeNonEmptyOctetStringOnSecondBit(localName);
        }
    }

    /*
     * C.17
     */
    protected final void encodeAttributeQualifiedNameOnSecondBit(String namespaceURI, String prefix, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.attributeName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; i++) {
                if ((prefix == names[i].prefix || prefix.equals(names[i].prefix))
                        && (namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                    encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                    return;
                }
            }
        }

        encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, prefix,
                localName, entry);
    }

    /*
     * C.17
     */
    protected final void encodeLiteralAttributeQualifiedNameOnSecondBit(String namespaceURI, String prefix, String localName,
                LocalNameQualifiedNamesMap.Entry entry) throws IOException {
        int namespaceURIIndex = KeyIntMap.NOT_PRESENT;
        int prefixIndex = KeyIntMap.NOT_PRESENT;
        if (namespaceURI != "") {
            namespaceURIIndex = _v.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == KeyIntMap.NOT_PRESENT) {
                throw new IOException("namespace URI of local name not indexed: " + namespaceURI);
            }
            
            if (prefix != "") {
                prefixIndex = _v.prefix.get(prefix);
                if (prefixIndex == KeyIntMap.NOT_PRESENT) {
                    throw new IOException("prefix of local name not indexed: " + prefix);
                }
            }
        }

        int localNameIndex = _v.localName.obtainIndex(localName);

        QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, "", _v.attributeName.getNextIndex());
        entry.addQualifiedName(name);

        _b = EncodingConstants.ATTRIBUTE_LITERAL_QNAME_FLAG;
        if (namespaceURI != "") {
            _b |= EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG;
            if (prefix != "") {
                _b |= EncodingConstants.LITERAL_QNAME_PREFIX_FLAG;
            }
        }

        write(_b);

        if (namespaceURIIndex >= 0) {
            if (prefixIndex >= 0) {
                encodeNonZeroIntegerOnSecondBitFirstBitOne(prefixIndex);
            }
            encodeNonZeroIntegerOnSecondBitFirstBitOne(namespaceURIIndex);
        } else if (namespaceURI != "") {
            // XML prefix and namespace name
            encodeNonEmptyOctetStringOnSecondBit("xml");
            encodeNonEmptyOctetStringOnSecondBit("http://www.w3.org/XML/1998/namespace");
        }

        if (localNameIndex >= 0) {
            encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        } else {
            encodeNonEmptyOctetStringOnSecondBit(localName);
        }
    }

    /*
     * C.14
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(String s, StringIntMap map, boolean addToTable) throws IOException {
        if (s == null || s.length() == 0) {
            // C.26 an index (first bit '1') with seven '1' bits for an empty string
            write(0xFF);
        } else {
            if (addToTable) {
                int index = map.obtainIndex(s);
                if (index == KeyIntMap.NOT_PRESENT) {
                    _b = EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG;
                    encodeEncodedCharacterStringAsUTF8OnThirdBit(s);
                } else {
                    encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
                }
            } else {
                _b = 0;
                encodeEncodedCharacterStringAsUTF8OnThirdBit(s);
            }
        }
    }

    /*
     * C.14
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(String s, CharArrayIntMap map, boolean addToTable) throws IOException {
        if (s == null || s.length() == 0) {
            // C.26 an index (first bit '1') with seven '1' bits for an empty string
            write(0xFF);
        } else {
            if (addToTable) {
                final char[] ch = s.toCharArray();
                final int length = s.length();
                int index = map.obtainIndex(ch, 0, length, false);
                if (index == KeyIntMap.NOT_PRESENT) {
                    _b = EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG;
                    encodeEncodedCharacterStringAsUTF8OnThirdBit(ch, 0, length);
                } else {
                    encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
                }
            } else {
                _b = 0;
                encodeEncodedCharacterStringAsUTF8OnThirdBit(s);
            }
        }
    }

    /*
     * C.14
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(char[] array, int start, int length, CharArrayIntMap map,
            boolean addToTable, boolean clone) throws IOException {
        if (length == 0) {
            // C.26 an index (first bit '1') with seven '1' bits for an empty string
            write(0xFF);
        } else {
            if (addToTable) {
                int index = map.obtainIndex(array, start, length, clone);
                if (index == KeyIntMap.NOT_PRESENT) {
                    _b = EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG;
                    encodeEncodedCharacterStringAsUTF8OnThirdBit(array, start, length);
                } else {
                    encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
                }
            } else {
                _b = 0;
                encodeEncodedCharacterStringAsUTF8OnThirdBit(array, start, length);
            }
        }
    }

    /*
     * C.14
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(String URI, int id, Object data) throws FastInfosetException, IOException {
        if (URI != null) {
            id = _v.encodingAlgorithm.get(URI);
            if (id == KeyIntMap.NOT_PRESENT) {
                throw new EncodingAlgorithmException("Encoding algorithm URI '" + URI + "' not a member of the encoding algorithm table");
            }
            id += EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START;

            EncodingAlgorithm ea = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                encodeAIIObjectAlgorithmData(URI, id, data, ea);
            } else {
                if (data instanceof byte[]) {
                    byte[] d = (byte[])data;
                    encodeAIIOctetAlgorithmData(id, d, 0, d.length);
                } else {
                    throw new EncodingAlgorithmException("Null encoding algorithm URI present with data that is not byte[]");
                }
            }
        } else if (id <= EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END) {
            BuiltInEncodingAlgorithm a = BuiltInEncodingAlgorithmFactory.table[id];
            switch(id) {
                case EncodingAlgorithmIndexes.HEXADECIMAL:
                    throw new UnsupportedOperationException("HEXADECIMAL");
                case EncodingAlgorithmIndexes.BASE64:
                {
                    byte[] d = (byte[])data;
                    encodeAIIOctetAlgorithmData(id, d, 0, d.length);
                    break;
                }
                case EncodingAlgorithmIndexes.SHORT:
                    short[] sd = (short[])data;
                    encodeAIIBuiltInAlgorithmData(id, sd, 0, sd.length);
                    break;
                case EncodingAlgorithmIndexes.INT:
                {
                    int[] d = (int[])data;
                    encodeAIIBuiltInAlgorithmData(id, d, 0, d.length);
                    break;
                }
                case EncodingAlgorithmIndexes.LONG:
                    throw new UnsupportedOperationException("LONG");
                case EncodingAlgorithmIndexes.BOOLEAN:
                    throw new UnsupportedOperationException("BOOLEAN");
                case EncodingAlgorithmIndexes.FLOAT:
                    float[] d = (float[])data;
                    encodeAIIBuiltInAlgorithmData(id, d, 0, d.length);
                    break;
                case EncodingAlgorithmIndexes.DOUBLE:
                    throw new UnsupportedOperationException("DOUBLE");
                case EncodingAlgorithmIndexes.UUID:
                    throw new UnsupportedOperationException("UUID");
                case EncodingAlgorithmIndexes.CDATA:
                    throw new UnsupportedOperationException("CDATA");
                default:
                    throw new EncodingAlgorithmException("Unsupported built-in encoding algorithm: " + id);
            }
        } else if (id >= EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START) {
            if (data instanceof byte[]) {
                byte[] d = (byte[])data;
                encodeAIIOctetAlgorithmData(id, d, 0, d.length);
            } else {
                throw new EncodingAlgorithmException("Null encoding algorithm URI present with data that is not byte[]");
            }
        } else {
            throw new EncodingAlgorithmException("Encoding algorithm identifiers 10 up to and including 31 are reserved for future use");
        }
    }

    protected final void encodeAIIOctetAlgorithmData(int id, byte[] d, int start, int length) throws IOException {
        // Encode identification and top four bits of encoding algorithm id
        write (EncodingConstants.NISTRING_ENCODING_ALGORITHM_FLAG |
                ((id & 0xF0) >> 4));

        // Encode bottom 4 bits of enoding algorithm id
        _b = (id & 0x0F) << 4;

        // Encode the length
        encodeNonZeroOctetStringLengthOnFifthBit(length);

        write(d, start, length);
    }

    protected final void encodeAIIObjectAlgorithmData(String URI, int id, Object data, EncodingAlgorithm ea) throws FastInfosetException, IOException {
        // Encode identification and top four bits of encoding algorithm id
        write (EncodingConstants.NISTRING_ENCODING_ALGORITHM_FLAG |
                ((id & 0xF0) >> 4));

        // Encode bottom 4 bits of enoding algorithm id
        _b = (id & 0x0F) << 4;

        _encodingBufferOutputStream.reset();
        ea.encodeToOutputStream(data, _encodingBufferOutputStream);
        encodeNonZeroOctetStringLengthOnFifthBit(_encodingBufferIndex);
        write(_encodingBuffer, _encodingBufferIndex);
    }

    protected final void encodeAIIBuiltInAlgorithmData(int id, Object o, int start, int length) throws IOException {
        // Encode identification and top four bits of encoding algorithm id
        write (EncodingConstants.NISTRING_ENCODING_ALGORITHM_FLAG |
                ((id & 0xF0) >> 4));

        // Encode bottom 4 bits of enoding algorithm id
        _b = (id & 0x0F) << 4;

        final int octetLength = BuiltInEncodingAlgorithmFactory.table[id].
                    getOctetLengthFromPrimitiveLength(length);

        encodeNonZeroOctetStringLengthOnFifthBit(octetLength);

        ensureSize(octetLength);
        BuiltInEncodingAlgorithmFactory.table[id].
                encodeToBytes(o, start, length, _octetBuffer, _octetBufferIndex);
        _octetBufferIndex += octetLength;
    }

    /*
     * C.19
     */
    protected final void encodeEncodedCharacterStringAsUTF8OnThirdBit(String s) throws IOException {
        encodeNonEmptyOctetStringOnFifthBit(s);
    }

    /*
     * C.19
     */
    protected final void encodeEncodedCharacterStringAsUTF8OnThirdBit(char[] array, int start, int length) throws IOException {
        encodeNonEmptyOctetStringOnFifthBit(array, start, length);
    }

    /*
     * C.15
     */
    protected final void encodeNonIdentifyingStringOnThirdBit(char[] array, int start, int length,
            CharArrayIntMap map, boolean addToTable, boolean clone) throws IOException {
        // length cannot be zero since sequence of CIIs has to be > 0

        if (addToTable) {
            int index = map.obtainIndex(array, start, length, clone);
            if (index == KeyIntMap.NOT_PRESENT) {
                _b |= EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG;
                encodeEncodedCharacterStringAsUTF8OnFifthBit(array, start, length);
            } else {
                _b |= 0x20;
                encodeNonZeroIntegerOnFourthBit(index);
            }
        } else {
            encodeEncodedCharacterStringAsUTF8OnFifthBit(array, start, length);
        }
    }

    protected final void encodeNonIdentifyingStringOnThirdBit(String URI, int id, Object data) throws FastInfosetException, IOException {
        if (URI != null) {
            id = _v.encodingAlgorithm.get(URI);
            if (id == KeyIntMap.NOT_PRESENT) {
                throw new EncodingAlgorithmException("Encoding algorithm URI '" + URI + "' not a member of the encoding algorithm table");
            }
            id += EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START;

            EncodingAlgorithm ea = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                encodeCIIObjectAlgorithmData(URI, id, data, ea);
            } else {
                if (data instanceof byte[]) {
                    byte[] d = (byte[])data;
                    encodeCIIOctetAlgorithmData(id, d, 0, d.length);
                } else {
                    throw new EncodingAlgorithmException("Null encoding algorithm URI present with data that is not byte[]");
                }
            }
        } else if (id <= EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END) {
            switch(id) {
                case EncodingAlgorithmIndexes.HEXADECIMAL:
                    throw new UnsupportedOperationException("HEXADECIMAL");
                case EncodingAlgorithmIndexes.BASE64:
                {
                    byte[] d = (byte[])data;
                    encodeCIIBuiltInAlgorithmData(id, d, 0, d.length);
                    break;
                }
                case EncodingAlgorithmIndexes.SHORT:
                    short[] sd = (short[])data;
                    encodeCIIBuiltInAlgorithmData(id, sd, 0, sd.length);
                    break;
                case EncodingAlgorithmIndexes.INT:
                {
                    int[] d = (int[])data;
                    encodeCIIBuiltInAlgorithmData(id, d, 0, d.length);
                    break;
                }
                case EncodingAlgorithmIndexes.LONG:
                    throw new UnsupportedOperationException("LONG");
                case EncodingAlgorithmIndexes.BOOLEAN:
                    throw new UnsupportedOperationException("BOOLEAN");
                case EncodingAlgorithmIndexes.FLOAT:
                    float[] d = (float[])data;
                    encodeCIIBuiltInAlgorithmData(id, d, 0, d.length);
                    break;
                case EncodingAlgorithmIndexes.DOUBLE:
                    throw new UnsupportedOperationException("DOUBLE");
                case EncodingAlgorithmIndexes.UUID:
                    throw new UnsupportedOperationException("UUID");
                case EncodingAlgorithmIndexes.CDATA:
                    throw new UnsupportedOperationException("CDATA");
                default:
                    throw new EncodingAlgorithmException("Unsupported built-in encoding algorithm: " + id);
            }
        } else if (id >= EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START) {
            if (data instanceof byte[]) {
                byte[] d = (byte[])data;
                encodeCIIOctetAlgorithmData(id, d, 0, d.length);
            } else {
                throw new EncodingAlgorithmException("Null encoding algorithm URI present with data that is not byte[]");
            }
        } else {
            throw new EncodingAlgorithmException("Encoding algorithm identifiers 10 up to and including 31 are reserved for future use");
        }
    }

    protected final void encodeNonIdentifyingStringOnThirdBit(String URI, int id, byte[] b, int start, int length) throws FastInfosetException, IOException {
        if (URI != null) {
            id = _v.encodingAlgorithm.get(URI);
            if (id == KeyIntMap.NOT_PRESENT) {
                throw new EncodingAlgorithmException("Encoding algorithm URI '" + URI + "' not a member of the encoding algorithm table");
            }
            id += EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START;
        }

        encodeCIIOctetAlgorithmData(id, b, start, length);
    }

    protected final void encodeCIIOctetAlgorithmData(int id, byte[] d, int start, int length) throws IOException {
        // Encode identification and top two bits of encoding algorithm id
        write (EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_ENCODING_ALGORITHM_FLAG |
                ((id & 0xC0) >> 6));

        // Encode bottom 6 bits of enoding algorithm id
        _b = (id & 0x3F) << 2;

        // Encode the length
        encodeNonZeroOctetStringLengthOnSenventhBit(length);

        write(d, start, length);
    }

    protected final void encodeCIIObjectAlgorithmData(String URI, int id, Object data, EncodingAlgorithm ea) throws FastInfosetException, IOException {
        // Encode identification and top two bits of encoding algorithm id
        write (EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_ENCODING_ALGORITHM_FLAG |
                ((id & 0xC0) >> 6));

        // Encode bottom 6 bits of enoding algorithm id
        _b = (id & 0x3F) << 2;

        _encodingBufferOutputStream.reset();
        ea.encodeToOutputStream(data, _encodingBufferOutputStream);
        encodeNonZeroOctetStringLengthOnSenventhBit(_encodingBufferIndex);
        write(_encodingBuffer, _encodingBufferIndex);
    }

    protected final void encodeCIIBuiltInAlgorithmData(int id, Object o, int start, int length) throws FastInfosetException, IOException {
        // Encode identification and top two bits of encoding algorithm id
        write (EncodingConstants.CHARACTER_CHUNK | EncodingConstants.CHARACTER_CHUNK_ENCODING_ALGORITHM_FLAG |
                ((id & 0xC0) >> 6));

        // Encode bottom 6 bits of enoding algorithm id
        _b = (id & 0x3F) << 2;

        final int octetLength = BuiltInEncodingAlgorithmFactory.table[id].
                    getOctetLengthFromPrimitiveLength(length);

        encodeNonZeroOctetStringLengthOnSenventhBit(octetLength);

        ensureSize(octetLength);
        BuiltInEncodingAlgorithmFactory.table[id].
                encodeToBytes(o, start, length, _octetBuffer, _octetBufferIndex);
        _octetBufferIndex += octetLength;
    }

    /*
     * C.20
     */
    protected final void encodeEncodedCharacterStringAsUTF8OnFifthBit(char[] array, int start, int length) throws IOException {
        encodeNonEmptyOctetStringOnSeventhBit(array, start, length);
    }

    /*
     * C.13
     */
    protected final void encodeIdentifyingNonEmptyStringOnFirstBit(String s, StringIntMap map) throws IOException {
        int index = map.obtainIndex(s);
        if (index == KeyIntMap.NOT_PRESENT) {
            // _b = 0;
            encodeNonEmptyOctetStringOnSecondBit(s);
        } else {
            // _b = 0x80;
            encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
        }
    }

    /*
     * C.22
     */
    protected final void encodeNonEmptyOctetStringOnSecondBit(String s) throws IOException {
        final int length = encodeUTF8String(s);
        encodeNonZeroOctetStringLengthOnSecondBit(length);
        write(_encodingBuffer, length);
    }

    /*
     * C.22
     */
    protected final void encodeNonZeroOctetStringLengthOnSecondBit(int length) throws IOException {
        if (length < EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT) {
            // [1, 64]
            write(length - 1);
        } else if (length < EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT) {
            // [65, 320]
            write(EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_FLAG); // 010 00000
            write(length - EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_SMALL_LIMIT);
        } else {
            // [321, 4294967296]
            write(EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_LARGE_FLAG); // 0110 0000
            length -= EncodingConstants.OCTET_STRING_LENGTH_2ND_BIT_MEDIUM_LIMIT;
            write(length >>> 24);
            write((length >> 16) & 0xFF);
            write((length >> 8) & 0xFF);
            write(length & 0xFF);
        }
    }


    /*
     * C.23
     */
    protected final void encodeNonEmptyOctetStringOnFifthBit(String s) throws IOException {
        final int length = encodeUTF8String(s);
        encodeNonZeroOctetStringLengthOnFifthBit(length);
        write(_encodingBuffer, length);
    }

    /*
     * C.23
     */
    protected final void encodeNonEmptyOctetStringOnFifthBit(char[] array, int start, int length) throws IOException {
        length = encodeUTF8String(array, start, length);
        encodeNonZeroOctetStringLengthOnFifthBit(length);
        write(_encodingBuffer, length);
    }

    /*
     * C.23
     */
    protected final void encodeNonZeroOctetStringLengthOnFifthBit(int length) throws IOException {
        if (length < EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT) {
            // [1, 8]
            write(_b | (length - 1));
        } else if (length < EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT) {
            // [9, 264]
            write(_b | EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_FLAG); // 000010 00
            write(length - EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT);
        } else {
            // [265, 4294967296]
            write(_b | EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_LARGE_FLAG); // 000011 00
            length -= EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
            write(length >>> 24);
            write((length >> 16) & 0xFF);
            write((length >> 8) & 0xFF);
            write(length & 0xFF);
        }
    }

    /*
     * C.24
     */
    protected final void encodeNonEmptyOctetStringOnSeventhBit(char[] array, int start, int length) throws IOException {
        length = encodeUTF8String(array, start, length);
        encodeNonZeroOctetStringLengthOnSenventhBit(length);
        write(_encodingBuffer, length);
    }

    /*
     * C.24
     */
    protected final void encodeNonZeroOctetStringLengthOnSenventhBit(int length) throws IOException {
        if (length < EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT) {
            // [1, 2]
            write(_b | (length - 1));
        } else if (length < EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT) {
            // [3, 258]
            write(_b | EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_FLAG); // 00000010
            write(length - EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT);
        } else {
            // [259, 4294967296]
            write(_b | EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_LARGE_FLAG); // 00000011
            length -= EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
            write(length >>> 24);
            write((length >> 16) & 0xFF);
            write((length >> 8) & 0xFF);
            write(length & 0xFF);
        }
    }

    /*
     * C.25 (with first bit set to 1)
     *
     * i is a member of the interval [0, 1048575]
     *
     * In the specification the interval is [1, 1048576]
     *
     * The first bit of the first octet is set. As specified in C.13
     */
    protected final void encodeNonZeroIntegerOnSecondBitFirstBitOne(int i) throws IOException {
        if (i < EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT) {
            // [1, 64] ( [0, 63] ) 6 bits
            write(0x80 | i);
        } else if (i < EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT) {
            // [65, 8256] ( [64, 8255] ) 13 bits
            i -= EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
            _b = (0x80 | EncodingConstants.INTEGER_2ND_BIT_MEDIUM_FLAG) | (i >> 8); // 010 00000
            // _b = 0xC0 | (i >> 8); // 010 00000
            write(_b);
            write(i & 0xFF);
        } else {
            // [8257, 1048576] ( [8256, 1048575] ) 20 bits
            i -= EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
            _b = (0x80 | EncodingConstants.INTEGER_2ND_BIT_LARGE_FLAG) | (i >> 16); // 0110 0000
            // _b = 0xE0 | (i >> 16); // 0110 0000
            write(_b);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        }
    }

    /*
     * C.25 (with first bit set to 0)
     *
     * i is a member of the interval [0, 1048575]
     *
     * In the specification the interval is [1, 1048576]
     *
     * The first bit of the first octet is set. As specified in C.13
     */
    protected final void encodeNonZeroIntegerOnSecondBitFirstBitZero(int i) throws IOException {
        if (i < EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT) {
            // [1, 64] ( [0, 63] ) 6 bits
            write(i);
        } else if (i < EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT) {
            // [65, 8256] ( [64, 8255] ) 13 bits
            i -= EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
            _b = EncodingConstants.INTEGER_2ND_BIT_MEDIUM_FLAG | (i >> 8); // 010 00000
            write(_b);
            write(i & 0xFF);
        } else {
            // [8257, 1048576] ( [8256, 1048575] ) 20 bits
            i -= EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
            _b = EncodingConstants.INTEGER_2ND_BIT_LARGE_FLAG | (i >> 16); // 0110 0000
            write(_b);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        }
    }

    /*
     * C.27
     *
     * i is a member of the interval [0, 1048575]
     *
     * In the specification the interval is [1, 1048576]
     *
     */
    protected final void encodeNonZeroIntegerOnThirdBit(int i) throws IOException {
        if (i < EncodingConstants.INTEGER_3RD_BIT_SMALL_LIMIT) {
            // [1, 32] ( [0, 31] ) 5 bits
            write(_b | i);
        } else if (i < EncodingConstants.INTEGER_3RD_BIT_MEDIUM_LIMIT) {
            // [33, 2080] ( [32, 2079] ) 11 bits
            i -= EncodingConstants.INTEGER_3RD_BIT_SMALL_LIMIT;
            _b |= EncodingConstants.INTEGER_3RD_BIT_MEDIUM_FLAG | (i >> 8); // 00100 000
            write(_b);
            write(i & 0xFF);
        } else if (i < EncodingConstants.INTEGER_3RD_BIT_LARGE_LIMIT) {
            // [2081, 526368] ( [2080, 526367] ) 19 bits
            i -= EncodingConstants.INTEGER_3RD_BIT_MEDIUM_LIMIT;
            _b |= EncodingConstants.INTEGER_3RD_BIT_LARGE_FLAG | (i >> 16); // 00101 000
            write(_b);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        } else {
            // [526369, 1048576] ( [526368, 1048575] ) 20 bits
            i -= EncodingConstants.INTEGER_3RD_BIT_LARGE_LIMIT;
            _b |= EncodingConstants.INTEGER_3RD_BIT_LARGE_LARGE_FLAG; // 00110 000
            write(_b);
            write(i >> 16);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        }
    }

    /*
     * C.28
     *
     * i is a member of the interval [0, 1048575]
     *
     * In the specification the interval is [1, 1048576]
     */
    protected final void encodeNonZeroIntegerOnFourthBit(int i) throws IOException {
        if (i < EncodingConstants.INTEGER_4TH_BIT_SMALL_LIMIT) {
            // [1, 16] ( [0, 15] ) 4 bits
            write(_b | i);
        } else if (i < EncodingConstants.INTEGER_4TH_BIT_MEDIUM_LIMIT) {
            // [17, 1040] ( [16, 1039] ) 10 bits
            i -= EncodingConstants.INTEGER_4TH_BIT_SMALL_LIMIT;
            _b |= EncodingConstants.INTEGER_4TH_BIT_MEDIUM_FLAG | (i >> 8); // 000 100 00
            write(_b);
            write(i & 0xFF);
        } else if (i < EncodingConstants.INTEGER_4TH_BIT_LARGE_LIMIT) {
            // [1041, 263184] ( [1040, 263183] ) 18 bits
            i -= EncodingConstants.INTEGER_4TH_BIT_MEDIUM_LIMIT;
            _b |= EncodingConstants.INTEGER_4TH_BIT_LARGE_FLAG | (i >> 16); // 000 101 00
            write(_b);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        } else {
            // [263185, 1048576] ( [263184, 1048575] ) 20 bits
            i -= EncodingConstants.INTEGER_4TH_BIT_LARGE_LIMIT;
            _b |= EncodingConstants.INTEGER_4TH_BIT_LARGE_LARGE_FLAG; // 000 110 00
            write(_b);
            write(i >> 16);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        }
    }

    protected final void encodeNonEmptyUTF8StringAsOctetString(int b, String s, int[] constants) throws IOException {
        final char[] ch = s.toCharArray();
        encodeNonEmptyUTF8StringAsOctetString(b, ch, 0, ch.length, constants);
    }

    protected final void encodeNonEmptyUTF8StringAsOctetString(int b, char ch[], int start, int length, int[] constants) throws IOException {
        length = encodeUTF8String(ch, start, length);
        encodeNonZeroOctetStringLength(b, length, constants);
        write(_encodingBuffer, length);
    }

    protected final void encodeNonZeroOctetStringLength(int b, int length, int[] constants) throws IOException {
        if (length < constants[EncodingConstants.OCTET_STRING_LENGTH_SMALL_LIMIT]) {
            write(b | (length - 1));
        } else if (length < constants[EncodingConstants.OCTET_STRING_LENGTH_MEDIUM_LIMIT]) {
            write(b | constants[EncodingConstants.OCTET_STRING_LENGTH_MEDIUM_FLAG]);
            write(length - constants[EncodingConstants.OCTET_STRING_LENGTH_SMALL_LIMIT]);
        } else {
            write(b | constants[EncodingConstants.OCTET_STRING_LENGTH_LARGE_FLAG]);
            length -= constants[EncodingConstants.OCTET_STRING_LENGTH_MEDIUM_LIMIT];
            write(length >>> 24);
            write((length >> 16) & 0xFF);
            write((length >> 8) & 0xFF);
            write(length & 0xFF);
        }
    }

    protected final void encodeNonZeroInteger(int b, int i, int[] constants) throws IOException {
        if (i < constants[EncodingConstants.INTEGER_SMALL_LIMIT]) {
            write(b | i);
        } else if (i < constants[EncodingConstants.INTEGER_MEDIUM_LIMIT]) {
            i -= constants[EncodingConstants.INTEGER_SMALL_LIMIT];
            write(b | constants[EncodingConstants.INTEGER_MEDIUM_FLAG] | (i >> 8));
            write(i & 0xFF);
        } else if (i < constants[EncodingConstants.INTEGER_LARGE_LIMIT]) {
            i -= constants[EncodingConstants.INTEGER_MEDIUM_LIMIT];
            write(b | constants[EncodingConstants.INTEGER_LARGE_FLAG] | (i >> 16));
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        } else if (i < EncodingConstants.INTEGER_MAXIMUM_SIZE) {
            i -= constants[EncodingConstants.INTEGER_LARGE_LIMIT];
            write(b | constants[EncodingConstants.INTEGER_LARGE_LARGE_FLAG]);
            write(i >> 16);
            write((i >> 8) & 0xFF);
            write(i & 0xFF);
        } else {
            throw new IOException("Integer > " + EncodingConstants.INTEGER_MAXIMUM_SIZE);
        }
    }







    protected final int encodeUTF8String(String s) throws IOException {
        final int length = s.length();
        if (length < _charBuffer.length) {
            s.getChars(0, length, _charBuffer, 0);
            return encodeUTF8String(_charBuffer, 0, length);
        } else {
            char[] ch = s.toCharArray();
            return encodeUTF8String(ch, 0, length);
        }
    }

    protected final void mark() throws IOException {
        _markIndex = _octetBufferIndex;
    }

    protected final void resetMark() throws IOException {
        _markIndex = -1;
    }

    protected final void write(int i) throws IOException {
        if (_octetBufferIndex < _octetBuffer.length) {
            _octetBuffer[_octetBufferIndex++] = (byte)i;
        } else {
            if (_markIndex == -1) {
                _s.write(_octetBuffer);
                _octetBufferIndex = 1;
                _octetBuffer[0] = (byte)i;
            } else {
                resize(_octetBuffer.length * 3 / 2);
                _octetBuffer[_octetBufferIndex++] = (byte)i;
            }
        }
    }

    protected final void write(byte[] b, int length) throws IOException {
        write(b, 0,  length);
    }

    protected final void write(byte[] b, int start, int length) throws IOException {
        if ((_octetBufferIndex + length) < _octetBuffer.length) {
            System.arraycopy(b, start, _octetBuffer, _octetBufferIndex, length);
            _octetBufferIndex += length;
        } else {
            if (_markIndex == -1) {
                _s.write(_octetBuffer, 0, _octetBufferIndex);
                _s.write(b, start, length);
                _octetBufferIndex = 0;
            } else {
                resize((_octetBuffer.length + length) * 3 / 2 + 1);
                System.arraycopy(b, start, _octetBuffer, _octetBufferIndex, length);
                _octetBufferIndex += length;
            }
        }
    }

    protected final void writeToBuffer(byte[] b, int length) {
        ensureSize(length);

        System.arraycopy(b, 0, _octetBuffer, _octetBufferIndex, length);
        _octetBufferIndex += length;
    }

    protected final void ensureSize(int length) {
        if ((_octetBufferIndex + length) > _octetBuffer.length) {
            resize((_octetBufferIndex + length) * 3 / 2 + 1);
        }
    }

    protected final void resize(int length) {
        byte[] b = new byte[length];
        System.arraycopy(_octetBuffer, 0, b, 0, _octetBufferIndex);
        _octetBuffer = b;
    }

    protected final void _flush() throws IOException {
        if (_octetBufferIndex > 0) {
            _s.write(_octetBuffer, 0, _octetBufferIndex);
            _octetBufferIndex = 0;
        }
    }

    protected class EncodingBufferOutputStream extends OutputStream {

        public void write(int b) throws IOException {
            if (_encodingBufferIndex < _encodingBuffer.length) {
                _encodingBuffer[_encodingBufferIndex++] = (byte)b;
            } else {
                byte newbuf[] = new byte[Math.max(_encodingBuffer.length << 1, _encodingBufferIndex)];
                System.arraycopy(_encodingBuffer, 0, newbuf, 0, _encodingBufferIndex);
                _encodingBuffer = newbuf;

                _encodingBuffer[_encodingBufferIndex++] = (byte)b;
            }
        }

        public void write(byte b[], int off, int len) throws IOException {
            if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            final int newoffset = _encodingBufferIndex + len;
            if (newoffset > _encodingBuffer.length) {
                byte newbuf[] = new byte[Math.max(_encodingBuffer.length << 1, newoffset)];
                System.arraycopy(_encodingBuffer, 0, newbuf, 0, _encodingBufferIndex);
                _encodingBuffer = newbuf;
            }
            System.arraycopy(b, off, _encodingBuffer, _encodingBufferIndex, len);
            _encodingBufferIndex = newoffset;
        }

        public int getLength() {
            return _encodingBufferIndex;
        }

        public void reset() {
            _encodingBufferIndex = 0;
        }
    }

    protected EncodingBufferOutputStream _encodingBufferOutputStream = new EncodingBufferOutputStream();

    protected byte[] _encodingBuffer = new byte[512];

    protected int _encodingBufferIndex;

    protected final void ensureEncodingBufferSizeForUtf8String(int length) {
        final int newLength = 3 * length;
        if (_encodingBuffer.length < newLength) {
            _encodingBuffer = new byte[newLength];
        }
    }

    protected final int encodeUTF8String(char[] ch, int start, int length) throws IOException {
        int byteLength = 0;

        // Make sure buffer is large enough
        ensureEncodingBufferSizeForUtf8String(length);

        final int n = start + length;
        for (int i = start; i < n; i++) {
            final int c = (int) ch[i];
            if (c <= 0x7F) {         // up to 7 bits
                _encodingBuffer[byteLength++] = (byte) c;
            }
            else if (c <= 0x7FF) {   // up to 11 bits
                _encodingBuffer[byteLength++] =
                    (byte) (0xC0 | (c >> 6));    // first 5
                _encodingBuffer[byteLength++] =
                    (byte) (0x80 | (c & 0x3F));  // second 6
            }
            else {                   // up to 16 bits
                _encodingBuffer[byteLength++] =
                    (byte) (0xE0 | (c >> 12));   // first 4
                _encodingBuffer[byteLength++] =
                    (byte) (0x80 | ((c >> 6) & 0x3F));  // second 6
                _encodingBuffer[byteLength++] =
                    (byte) (0x80 | (c & 0x3F));  // third 6
            }
            // TODO High and low surrogates
        }

        return byteLength;
    }

    public static String getPrefixFromQualifiedName(String qName) {
        int i = qName.indexOf(':');
        String prefix = "";
        if (i != -1) {
            prefix = qName.substring(0, i);
        }
        return prefix;
    }
}
