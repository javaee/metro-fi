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


package com.sun.xml.fastinfoset.dom;

import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.DecoderStateTables;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayString;
import java.io.IOException;
import java.io.InputStream;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import org.jvnet.fastinfoset.FastInfosetException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class DOMDocumentParser extends Decoder {
    protected Document _document;
    
    protected Node _currentNode;
    
    protected Element _currentElement;

    protected Attr[] _namespaceAttributes = new Attr[8];

    protected int _namespaceAttributesIndex;
    
    public void parse(Document d, InputStream s) throws FastInfosetException, IOException {        
        _currentNode = _document = d;
        _namespaceAttributesIndex = 0;
                    
        parse(s);        
    }
    
    protected final void parse(InputStream s) throws FastInfosetException, IOException {
        setInputStream(s);
        parse();
    }
    
    protected final void parse() throws FastInfosetException, IOException {
        reset();
        decodeHeader();                                                                                
        processDII();
    }
    
    protected final void processDII() throws FastInfosetException, IOException {        
        _b = read();
        if (_b > 0) {
            processDIIOptionalProperties();
        }
        
        // Decode one Document Type II, Comment IIs, PI IIs and one EII
        boolean firstElementHasOccured = false;
        boolean documentTypeDeclarationOccured = false;
        while(!_terminate || !firstElementHasOccured) {
            _b = read();
            switch(DecoderStateTables.DII[_b]) {
                case DecoderStateTables.EII_NO_AIIS_INDEX_SMALL:
                    processEII(_v.elementName.get(_b), false);
                    firstElementHasOccured = true;
                    break;
                case DecoderStateTables.EII_AIIS_INDEX_SMALL:
                    processEII(_v.elementName.get(_b & EncodingConstants.INTEGER_3RD_BIT_SMALL_MASK), true);
                    firstElementHasOccured = true;
                    break;
                case DecoderStateTables.EII_INDEX_MEDIUM:
                    processEII(decodeEIIIndexMedium(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    firstElementHasOccured = true;
                    break;
                case DecoderStateTables.EII_INDEX_LARGE:
                    processEII(decodeEIIIndexLarge(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    firstElementHasOccured = true;
                    break;
                case DecoderStateTables.EII_LITERAL:
                    processEII(processEIILiteral(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    firstElementHasOccured = true;
                    break;
                case DecoderStateTables.EII_NAMESPACES:
                    processEIIWithNamespaces();
                    firstElementHasOccured = true;
                    break;
                case DecoderStateTables.DOCUMENT_TYPE_DECLARATION_II:
                {
                    if (documentTypeDeclarationOccured) {
                        throw new FastInfosetException("A second occurence of a Document Type Declaration II is present");
                    }
                    documentTypeDeclarationOccured = true;

                    String system_identifier = ((_b & EncodingConstants.DOCUMENT_TYPE_SYSTEM_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : null;
                    String public_identifier = ((_b & EncodingConstants.DOCUMENT_TYPE_PUBLIC_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : null;
                    
                    _b = read();
                    while (_b == EncodingConstants.PROCESSING_INSTRUCTION) {
                        switch(decodeNonIdentifyingStringOnFirstBit()) {
                            case NISTRING_STRING:
                                final String data = new String(_charBuffer, 0, _charBufferLength);
                                if (_addToTable) {
                                    _v.otherString.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                                }
                                break;
                            case NISTRING_ENCODING_ALGORITHM:
                                throw new FastInfosetException("Processing II with encoding algorithm decoding not supported");                        
                            case NISTRING_INDEX:
                                break;
                            case NISTRING_EMPTY_STRING:
                                break;
                        }
                        _b = read();
                    }
                    if ((_b & EncodingConstants.TERMINATOR) != EncodingConstants.TERMINATOR) {
                        throw new FastInfosetException("Processing instruction IIs of Document Type Declaraion II not terminated correctly");
                    }
                    if (_b == EncodingConstants.DOUBLE_TERMINATOR) {
                        _terminate = true;
                    }
                    
                    _notations.clear();
                    _unparsedEntities.clear();
                    /*
                     * TODO
                     * Report All events associated with DTD, PIs, notations etc
                     */
                    break;
                }
                case DecoderStateTables.COMMENT_II:
                    processCommentII();
                    break;
                case DecoderStateTables.PROCESSING_INSTRUCTION_II:
                    processProcessingII();
                    break;
                case DecoderStateTables.TERMINATOR_DOUBLE:                    
                    _doubleTerminate = true; 
                case DecoderStateTables.TERMINATOR_SINGLE:
                    _terminate = true;
                    break;
                default:
                    throw new FastInfosetException("Illegal state when decoding a child of a DII");
            }
        }

        // Decode any remaining Comment IIs, PI IIs
        while(!_terminate) {
            _b = read();
            switch(DecoderStateTables.DII[_b]) {
                case DecoderStateTables.COMMENT_II:
                    processCommentII();
                    break;
                case DecoderStateTables.PROCESSING_INSTRUCTION_II:
                    processProcessingII();
                    break;
                case DecoderStateTables.TERMINATOR_DOUBLE:                    
                    _doubleTerminate = true; 
                case DecoderStateTables.TERMINATOR_SINGLE:
                    _terminate = true;
                    break;
                default:
                    throw new FastInfosetException("Illegal state when decoding a child of an DII");
            }
        }

    }

    protected final void processDIIOptionalProperties() throws FastInfosetException, IOException {        
        if ((_b & EncodingConstants.DOCUMENT_ADDITIONAL_DATA_FLAG) > 0) {
            // decodeAdditionalData();
        }
        
        if ((_b & EncodingConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG) > 0) {
            decodeInitialVocabulary();
        }

        if ((_b & EncodingConstants.DOCUMENT_NOTATIONS_FLAG) > 0) {
            decodeNotations();
            // TODO Report notations
        }

        if ((_b & EncodingConstants.DOCUMENT_UNPARSED_ENTITIES_FLAG) > 0) {
            decodeUnparsedEntities();
            // TODO Report unparsed entities
        }

        if ((_b & EncodingConstants.DOCUMENT_CHARACTER_ENCODING_SCHEME) > 0) {
            // decodeCharacterEncodingScheme();
        }
        
        if ((_b & EncodingConstants.DOCUMENT_STANDALONE_FLAG) > 0) {
            boolean standalone = (read() > 0) ? true : false ;
            /*
             * TODO 
             * how to report the standalone flag?
             */
        }

        if ((_b & EncodingConstants.DOCUMENT_VERSION_FLAG) > 0) {
            switch(decodeNonIdentifyingStringOnFirstBit()) {
                case NISTRING_STRING:
                    if (_addToTable) {
                        _v.otherString.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    break;
                case NISTRING_ENCODING_ALGORITHM:
                    throw new FastInfosetException("Processing II with encoding algorithm decoding not supported");                        
                case NISTRING_INDEX:
                    break;
                case NISTRING_EMPTY_STRING:
                    break;
            }
            /*
             * TODO 
             * how to report the document version?
             */
        }
    }

    protected final void processEII(QualifiedName name, boolean hasAttributes) throws FastInfosetException, IOException {
        final Node parentCurrentNode = _currentNode;
        
        _currentNode = _currentElement = createElement(name.namespaceName, name.qName, name.localName);
        
        if (_namespaceAttributesIndex > 0) {
            for (int i = 0; i < _namespaceAttributesIndex; i++) {
                _currentElement.setAttributeNode(_namespaceAttributes[i]);
                _namespaceAttributes[i] = null;
            }
            _namespaceAttributesIndex = 0;
        }
        
        if (hasAttributes) {
            processAIIs();
        }
        
        parentCurrentNode.appendChild(_currentElement);

        while(!_terminate) {
            _b = read();
            switch(DecoderStateTables.EII[_b]) {
                case DecoderStateTables.EII_NO_AIIS_INDEX_SMALL:
                    processEII(_v.elementName.get(_b), false);
                    break;
                case DecoderStateTables.EII_AIIS_INDEX_SMALL:
                    processEII(_v.elementName.get(_b & EncodingConstants.INTEGER_3RD_BIT_SMALL_MASK), true);
                    break;
                case DecoderStateTables.EII_INDEX_MEDIUM:
                    processEII(decodeEIIIndexMedium(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                case DecoderStateTables.EII_INDEX_LARGE:
                    processEII(decodeEIIIndexLarge(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                case DecoderStateTables.EII_LITERAL:
                    processEII(processEIILiteral(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                case DecoderStateTables.EII_NAMESPACES:
                    processEIIWithNamespaces();
                    break;
                case DecoderStateTables.CII_UTF8_SMALL_LENGTH:
                {
                    _octetBufferLength = (_b & EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_MASK) 
                        + 1;
                    String v = decodeUtf8StringAsString();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(_charBuffer, _charBufferLength);
                    }
                    
                    _currentNode.appendChild(_document.createTextNode(v));
                    break;
                }
                case DecoderStateTables.CII_UTF8_MEDIUM_LENGTH:
                {
                    _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT;
                    String v = decodeUtf8StringAsString();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(_charBuffer, _charBufferLength);
                    }
                    
                    _currentNode.appendChild(_document.createTextNode(v));
                    break;
                }
                case DecoderStateTables.CII_UTF8_LARGE_LENGTH:
                {
                    _octetBufferLength = (read() << 24) |
                        (read() << 16) |
                        (read() << 8) |
                        read();
                    _octetBufferLength += EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
                    String v = decodeUtf8StringAsString();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(_charBuffer, _charBufferLength);
                    }
                    
                    _currentNode.appendChild(_document.createTextNode(v));
                    break;
                }
                case DecoderStateTables.CII_UTF16_SMALL_LENGTH:
                {
                    _octetBufferLength = (_b & EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_MASK) 
                        + 1;
                    String v = decodeUtf16StringAsString();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(_charBuffer, _charBufferLength);
                    }
                    
                    _currentNode.appendChild(_document.createTextNode(v));
                    break;
                }
                case DecoderStateTables.CII_UTF16_MEDIUM_LENGTH:
                {
                    _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT;
                    String v = decodeUtf16StringAsString();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(_charBuffer, _charBufferLength);
                    }
                    
                    _currentNode.appendChild(_document.createTextNode(v));
                    break;
                }
                case DecoderStateTables.CII_UTF16_LARGE_LENGTH:
                {
                    _octetBufferLength = (read() << 24) |
                        (read() << 16) |
                        (read() << 8) |
                        read();
                    _octetBufferLength += EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
                    String v = decodeUtf16StringAsString();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(_charBuffer, _charBufferLength);
                    }
                    
                    _currentNode.appendChild(_document.createTextNode(v));
                    break;
                }
                case DecoderStateTables.CII_RA:
                {
                    final boolean addToTable = (_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0;
                    
                    // Decode resitricted alphabet integer
                    _identifier = (_b & 0x02) << 6;
                    _b = read();
                    _identifier |= (_b & 0xFC) >> 2;

                    decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(_b);
                    
                    String v = decodeRestrictedAlphabetAsString();                    
                    if (addToTable) {
                        _v.characterContentChunk.add(_charBuffer, _charBufferLength);
                    }
                    
                    _currentNode.appendChild(_document.createTextNode(v));
                    break;
                }
                case DecoderStateTables.CII_EA:
                {
                    if ((_b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0) {
                        throw new EncodingAlgorithmException("Add to table not supported for Encoding algorithms");
                    }

                    // Decode encoding algorithm integer
                    _identifier = (_b & 0x02) << 6;
                    _b = read();
                    _identifier |= (_b & 0xFC) >> 2;
                    
                    decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(_b);
                    final String s = convertEncodingAlgorithmDataToCharacters();
                    _currentNode.appendChild(_document.createTextNode(s));
                    break;
                }
                case DecoderStateTables.CII_INDEX_SMALL:
                {
                    final CharArray ca = _v.characterContentChunk.get(_b & EncodingConstants.INTEGER_4TH_BIT_SMALL_MASK);
                    
                    _currentNode.appendChild(_document.createTextNode(ca.toString()));
                    break;
                }
                case DecoderStateTables.CII_INDEX_MEDIUM:
                {
                    final int index = (((_b & EncodingConstants.INTEGER_4TH_BIT_MEDIUM_MASK) << 8) | read())
                        + EncodingConstants.INTEGER_4TH_BIT_SMALL_LIMIT;
                    final String s = _v.characterContentChunk.get(index).toString();
                    
                    _currentNode.appendChild(_document.createTextNode(s));
                    break;
                }
                case DecoderStateTables.CII_INDEX_LARGE:
                {
                    int index = ((_b & EncodingConstants.INTEGER_4TH_BIT_LARGE_MASK) << 16) |
                        (read() << 8) |
                        read();
                    index += EncodingConstants.INTEGER_4TH_BIT_MEDIUM_LIMIT;
                    final String s = _v.characterContentChunk.get(index).toString();
                    
                    _currentNode.appendChild(_document.createTextNode(s));
                    break;
                }
                case DecoderStateTables.CII_INDEX_LARGE_LARGE:
                {
                    int index = (read() << 16) | 
                        (read() << 8) |
                        read();
                    index += EncodingConstants.INTEGER_4TH_BIT_LARGE_LIMIT;
                    final String s = _v.characterContentChunk.get(index).toString();
                    
                    _currentNode.appendChild(_document.createTextNode(s));
                    break;
                }                       
                case DecoderStateTables.COMMENT_II:
                    processCommentII();
                    break;
                case DecoderStateTables.PROCESSING_INSTRUCTION_II:
                    processProcessingII();
                    break;
                case DecoderStateTables.UNEXPANDED_ENTITY_REFERENCE_II:
                {
                    String entity_reference_name = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
                    
                    String system_identifier = ((_b & EncodingConstants.UNEXPANDED_ENTITY_SYSTEM_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : null;
                    String public_identifier = ((_b & EncodingConstants.UNEXPANDED_ENTITY_PUBLIC_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : null;

                    // TODO create Node
                    break;
                }
                case DecoderStateTables.TERMINATOR_DOUBLE:                    
                    _doubleTerminate = true; 
                case DecoderStateTables.TERMINATOR_SINGLE:
                    _terminate = true;
                    break;
                default:
                    throw new FastInfosetException("Illegal state when decoding a child of an EII");
            }
        }

        _terminate = _doubleTerminate;
        _doubleTerminate = false;
        
        _currentNode = parentCurrentNode;
    }

    protected final void processEIIWithNamespaces() throws FastInfosetException, IOException {
        final boolean hasAttributes = (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0;
                
        int b = read();
        while ((b & EncodingConstants.NAMESPACE_ATTRIBUTE_MASK) == EncodingConstants.NAMESPACE_ATTRIBUTE) {
            // NOTE a prefix without a namespace name is an undeclaration
            // of the namespace bound to the prefix
            // TODO need to investigate how the startPrefixMapping works in
            // relation to undeclaration

            // Prefix
            final String prefix = ((b & EncodingConstants.NAMESPACE_ATTRIBUTE_PREFIX_FLAG) > 0) 
                ? decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(_v.prefix) : null;

            // Namespace name
            final String namespaceName = ((b & EncodingConstants.NAMESPACE_ATTRIBUTE_NAME_FLAG) > 0) 
                ? decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(_v.namespaceName) : null;
            
            if (_namespaceAttributesIndex == _namespaceAttributes.length) {
                final Attr[] a = new Attr[_namespaceAttributesIndex * 3 / 2 + 1];
                System.arraycopy(_namespaceAttributes, 0, a, 0, _namespaceAttributesIndex);
                _namespaceAttributes = a;
            }

            Attr a;
            if (prefix != null) {
                a = createAttribute(
                        "http://www.w3.org/2000/xmlns/",
                        "xmlns:" + prefix, 
                        prefix);
            } else {
                a = createAttribute(
                        "http://www.w3.org/2000/xmlns/",
                        "xmlns", 
                        "xmlns");
            }
            a.setValue (namespaceName);    
            _namespaceAttributes[_namespaceAttributesIndex++] = a;
            
            b = read();
        }
        if (b != EncodingConstants.TERMINATOR) {
            throw new IOException("Namespace names of EII not terminated correctly");
        }

        _b = read();
        if (hasAttributes) {
            // Re-flag attribute flag
            // This is so the EII table can be reused.
            _b |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
        }

        switch(DecoderStateTables.EII[_b]) {
            case DecoderStateTables.EII_NO_AIIS_INDEX_SMALL:
                processEII(_v.elementName.get(_b), false);
                break;
            case DecoderStateTables.EII_AIIS_INDEX_SMALL:
                processEII(_v.elementName.get(_b & EncodingConstants.INTEGER_3RD_BIT_SMALL_MASK), true);
                break;
            case DecoderStateTables.EII_INDEX_MEDIUM:
                processEII(decodeEIIIndexMedium(), hasAttributes);
                break;
            case DecoderStateTables.EII_INDEX_LARGE:
                processEII(decodeEIIIndexLarge(), hasAttributes);
                break;
            case DecoderStateTables.EII_LITERAL:
                processEII(processEIILiteral(), hasAttributes);
                break;
            default:
                throw new IOException("Illegal state when decoding EII after the namespace AIIs");
        }
    }

    protected final QualifiedName processEIILiteral() throws FastInfosetException, IOException {
        final String prefix = ((_b & EncodingConstants.LITERAL_QNAME_PREFIX_FLAG) > 0) 
            ? decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(_v.prefix) : null;
        final String namespaceName = ((_b & EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG) > 0) 
            ? decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(_v.namespaceName) : null;
        final String localName = decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName);

        final QualifiedName qualifiedName = new QualifiedName(prefix, namespaceName, localName);
        _v.elementName.add(qualifiedName);
        return qualifiedName;
    }

    protected final void processAIIs() throws FastInfosetException, IOException {
        QualifiedName name;
        int b;
        String value;
        
        do {
            // AII qualified name
            b = read();
            switch (DecoderStateTables.AII[b]) {
                case DecoderStateTables.AII_INDEX_SMALL:
                    name = _v.attributeName.get(b);
                    break;
                case DecoderStateTables.AII_INDEX_MEDIUM:
                {
                    final int i = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read()) 
                        + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;            
                    name = _v.attributeName.get(i);
                    break;
                }
                case DecoderStateTables.AII_INDEX_LARGE:
                {
                    final int i = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read()) 
                        + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                    name = _v.attributeName.get(i);
                    break;
                }
                case DecoderStateTables.AII_LITERAL:
                {
                    final String prefix = ((b & EncodingConstants.LITERAL_QNAME_PREFIX_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(_v.prefix) : null;
                    final String namespaceName = ((b & EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(_v.namespaceName) : null;
                    final String localName = decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName);

                    name = new QualifiedName(prefix, namespaceName, localName);
                    _v.attributeName.add(name);
                    break;
                }
                case DecoderStateTables.AII_TERMINATOR_DOUBLE:                    
                    _doubleTerminate = true;
                case DecoderStateTables.AII_TERMINATOR_SINGLE:
                    _terminate = true;
                    // AIIs have finished break out of loop
                    continue;
                default:
                    throw new IOException("Illegal state when decoding AIIs");
            }

            Attr a = createAttribute(
                        name.namespaceName,
                        name.qName, 
                        name.localName);
                        
            // [normalized value] of AII
            
            b = read();
            switch(DecoderStateTables.NISTRING[b]) {
                case DecoderStateTables.NISTRING_UTF8_SMALL_LENGTH:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    _octetBufferLength = (b & EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_MASK) + 1;
                    value = decodeUtf8StringAsString();
                    if (addToTable) {
                        _v.attributeValue.add(value);
                    }
                    
                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_UTF8_MEDIUM_LENGTH:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT;
                    value = decodeUtf8StringAsString();
                    if (addToTable) {
                        _v.attributeValue.add(value);
                    }
                    
                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_UTF8_LARGE_LENGTH:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    final int length = (read() << 24) |
                        (read() << 16) |
                        (read() << 8) |
                        read();
                    _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
                    value = decodeUtf8StringAsString();
                    if (addToTable) {
                        _v.attributeValue.add(value);
                    }

                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_UTF16_SMALL_LENGTH:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    _octetBufferLength = (b & EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_MASK) + 1;
                    value = decodeUtf16StringAsString();
                    if (addToTable) {
                        _v.attributeValue.add(value);
                    }
                    
                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_UTF16_MEDIUM_LENGTH:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_SMALL_LIMIT;
                    value = decodeUtf16StringAsString();
                    if (addToTable) {
                        _v.attributeValue.add(value);
                    }
                    
                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_UTF16_LARGE_LENGTH:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    final int length = (read() << 24) |
                        (read() << 16) |
                        (read() << 8) |
                        read();
                    _octetBufferLength = length + EncodingConstants.OCTET_STRING_LENGTH_5TH_BIT_MEDIUM_LIMIT;
                    value = decodeUtf16StringAsString();
                    if (addToTable) {
                        _v.attributeValue.add(value);
                    }

                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_RA:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    // Decode resitricted alphabet integer
                    _identifier = (b & 0x0F) << 4;
                    b = read();
                    _identifier |= (b & 0xF0) >> 4;
                    
                    decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    
                    value = decodeRestrictedAlphabetAsString();
                    if (addToTable) {
                        _v.attributeValue.add(value);
                    }

                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_EA:
                {
                    if ((b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0) {
                        throw new EncodingAlgorithmException("Add to table not supported for Encoding algorithms");
                    }

                    _identifier = (b & 0x0F) << 4;
                    b = read();
                    _identifier |= (b & 0xF0) >> 4;

                    decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    value = convertEncodingAlgorithmDataToCharacters();
                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_INDEX_SMALL:
                    value = _v.attributeValue.get(b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK);

                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                case DecoderStateTables.NISTRING_INDEX_MEDIUM:
                {
                    final int index = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read()) 
                        + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                    value = _v.attributeValue.get(index);

                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_INDEX_LARGE:
                {
                    final int index = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read()) 
                        + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                    value = _v.attributeValue.get(index);

                    a.setValue(value);                
                    _currentElement.setAttributeNode(a);
                    break;
                }
                case DecoderStateTables.NISTRING_EMPTY:
                    a.setValue("");                
                    _currentElement.setAttributeNode(a);
                    break;
                default:
                    throw new IOException("Illegal state when decoding AII value");
            }
            
        } while (!_terminate);
        
        _terminate = _doubleTerminate;
        _doubleTerminate = false;
    }
        
    protected final void processCommentII() throws FastInfosetException, IOException {
        switch(decodeNonIdentifyingStringOnFirstBit()) {
            case NISTRING_STRING:
            {
                final String s = new String(_charBuffer, 0, _charBufferLength);
                if (_addToTable) {
                    _v.otherString.add(new CharArrayString(s, false));
                }
                
                _currentNode.appendChild(_document.createComment(s));
                break;
            }
            case NISTRING_ENCODING_ALGORITHM:
                throw new IOException("Comment II with encoding algorithm decoding not supported");                        
            case NISTRING_INDEX:
            {
                final String s = _v.otherString.get(_integer).toString();

                _currentNode.appendChild(_document.createComment(s));
                break;
            }
            case NISTRING_EMPTY_STRING:
                _currentNode.appendChild(_document.createComment(""));
                break;
        }        
    }

    protected final void processProcessingII() throws FastInfosetException, IOException {
        final String target = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);

        switch(decodeNonIdentifyingStringOnFirstBit()) {
            case NISTRING_STRING:
            {
                final String data = new String(_charBuffer, 0, _charBufferLength);
                if (_addToTable) {
                    _v.otherString.add(new CharArrayString(data, false));
                }

                _currentNode.appendChild (_document.createProcessingInstruction (target, data));
                break;
            }
            case NISTRING_ENCODING_ALGORITHM:
                throw new IOException("Processing II with encoding algorithm decoding not supported");                        
            case NISTRING_INDEX:
            {
                final String data = _v.otherString.get(_integer).toString();
                
                _currentNode.appendChild (_document.createProcessingInstruction (target, data));
                break;
            }
            case NISTRING_EMPTY_STRING:
                _currentNode.appendChild (_document.createProcessingInstruction (target, ""));
                break;
        }
    }
    
    protected Element createElement(String namespaceName, String qName, String localName) {
        return _document.createElementNS(namespaceName, qName);
    }
    
    protected Attr createAttribute(String namespaceName, String qName, String localName) {
        return _document.createAttributeNS(namespaceName, qName);
    }

    protected String convertEncodingAlgorithmDataToCharacters() throws FastInfosetException, IOException {
        StringBuffer buffer = new StringBuffer();
        if (_identifier <= EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END) {
            Object array = BuiltInEncodingAlgorithmFactory.table[_identifier].
                decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
            BuiltInEncodingAlgorithmFactory.table[_identifier].convertToCharacters(array,  buffer);
        } else if (_identifier >= EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START) {
            final String URI = _v.encodingAlgorithm.get(_identifier - EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START);
            final EncodingAlgorithm ea = (EncodingAlgorithm)_registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                final Object data = ea.decodeFromBytes(_octetBuffer, _octetBufferStart, _octetBufferLength);
                ea.convertToCharacters(data, buffer);
            } else {
                throw new EncodingAlgorithmException(
                        "Document contains application-defined encoding algorithm data that cannot be reported");
            }
        }
        return buffer.toString();
    }
    
}
