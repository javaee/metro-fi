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


package com.sun.xml.fastinfoset.sax;

import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.DecoderStateTables;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import org.jvnet.fastinfoset.ReferencedVocabulary;
import org.jvnet.fastinfoset.Vocabulary;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import org.jvnet.fastinfoset.sax.FastInfosetReader;
import org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayString;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXDocumentParser extends Decoder implements FastInfosetReader {

    public static final String STRING_INTERNING_FEATURE_PROPERTY =
        "com.sun.xml.fast-infoset.property.feature.string-interning";
                                                                                
    protected static boolean _stringInterningFeatureProperty = true;
                                                                                
    static {
        String p = System.getProperty(STRING_INTERNING_FEATURE_PROPERTY,
            "true");
        _stringInterningFeatureProperty = Boolean.valueOf(p).booleanValue();
    }
    
    protected boolean _stringInterningFeature = _stringInterningFeatureProperty;                                                                                

    protected boolean _namespacePrefixesFeature = false;

    /**
     * Reference to entity resolver.
     */
    protected EntityResolver _entityResolver;
                                                                                
    /**
     * Reference to dtd handler.
     */
    protected DTDHandler _dtdHandler;
                                                                                
    /**
     * Reference to content handler.
     */
    protected ContentHandler _contentHandler;
                                                                                
    /**
     * Reference to error handler.
     */
    protected ErrorHandler _errorHandler;
                                                                                
    /**
     * Reference to lexical handler.
     */
    protected LexicalHandler _lexicalHandler;
    
    protected AttributesHolder _attributes = new AttributesHolder();
    
    protected String[] _namespaceAIIs = new String[16];
    
    protected int _namespaceAIIsIndex;

    protected boolean _clearAttributes = false;
    
    /** Creates a new instance of DocumetParser2 */
    public SAXDocumentParser() {
        DefaultHandler handler = new DefaultHandler();
                                                                                
        _entityResolver = handler;
        _dtdHandler = handler;
        _contentHandler = handler;
        _errorHandler = handler;
        _lexicalHandler = null;
    }

    // XMLReader interface
    
    public boolean getFeature(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(Features.NAMESPACES_FEATURE)) {
            return true;
        } else if (name.equals(Features.NAMESPACE_PREFIXES_FEATURE)) {
            return _namespacePrefixesFeature;
        } else if (name.equals(Features.STRING_INTERNING_FEATURE)) {
            return _stringInterningFeature;
        } else {
            throw new SAXNotRecognizedException(
                "Feature not supported: " + name);
        }
    }
                                                                                
    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(Features.NAMESPACES_FEATURE)
                && value == false) {
            throw new SAXNotSupportedException(name + ":" + value);
        } else if (name.equals(Features.NAMESPACE_PREFIXES_FEATURE)) {
            _namespacePrefixesFeature = value;
        } else if (name.equals(Features.STRING_INTERNING_FEATURE)) {
            _stringInterningFeature = value;
        } else {
            throw new SAXNotRecognizedException(
                "Feature not supported: " + name);
        }
    }
                                                                                
    public Object getProperty(String name)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(Properties.LEXICAL_HANDLER_PROPERTY)) {
            return _lexicalHandler;
        } else if (name.equals(Properties.EXTERNAL_VOCABULARIES_PROPERTY)) {
          return _externalVocabularies;  
        } else {
            throw new SAXNotRecognizedException("Property not supported: " +
                name);
        }
    }
                                                                                
    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals(Properties.LEXICAL_HANDLER_PROPERTY)) {
            _lexicalHandler = (LexicalHandler)value;
        } else if (name.equals(Properties.EXTERNAL_VOCABULARIES_PROPERTY)) {
            // _externalVocabularies = (Map<String, ParserVocabulary>)value;
            _externalVocabularies = (Map)value;
        } else {
            throw new SAXNotRecognizedException("Property not supported: " +
                name);
        }
    }
                                                                                
    public void setEntityResolver(EntityResolver resolver) {
        _entityResolver = resolver;
    }
                                                                                
    public EntityResolver getEntityResolver() {
        return _entityResolver;
    }
                                                                                
    public void setDTDHandler(DTDHandler handler) {
        _dtdHandler = handler;
    }
                                                                                
    public DTDHandler getDTDHandler() {
        return _dtdHandler;
    }
    public void setContentHandler(ContentHandler handler) {
        _contentHandler = handler;
    }
                                                                                
    public ContentHandler getContentHandler() {
        return _contentHandler;
    }
                                                                                                                                                                
    public void setErrorHandler(ErrorHandler handler) {
        _errorHandler = handler;
    }
                                                                                
    public ErrorHandler getErrorHandler() {
        return _errorHandler;
    }
                                                                                
    public void parse(InputSource input) throws IOException, SAXException {
        InputStream s = input.getByteStream();
        if (s == null) {
            String systemId = input.getSystemId();
            if (systemId == null) {
                throw new SAXException("InputSource must include a byte stream or a system ID");
            }
            parse(systemId);
        }
        else {
            parse(s);
        }
    }
    
    public void parse(String systemId) throws IOException, SAXException {
        systemId = SystemIdResolver.getAbsoluteURI(systemId);
        parse(new URL(systemId).openStream());
    }


    // FastInfosetReader
    
    public final void parse(InputStream s) throws IOException, SAXException {
        setInputStream(s);
        parse();
    }
    
    public void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler handler) {
        throw new UnsupportedOperationException();
    }

    public EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler() {
        throw new UnsupportedOperationException();
    }

    public void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler handler) {
        throw new UnsupportedOperationException();
    }

    public PrimitiveTypeContentHandler getPrimitiveTypeContentHandler() {
        throw new UnsupportedOperationException();
    }

    
    // VocabularyReader
    
    public void setExternalVocabularies(Map referencedVocabualries) {
        throw new UnsupportedOperationException();
    }
    
    public void setDynamicVocabulary(Vocabulary v) {
        throw new UnsupportedOperationException();
    }

    public ReferencedVocabulary getExternalVocabulary() {
        throw new UnsupportedOperationException();
    }

    public Vocabulary getIntitialVocabulary() {
        throw new UnsupportedOperationException();
    }

    public Vocabulary getDynamicVocabulary() {
        throw new UnsupportedOperationException();
    }

    public Vocabulary getFinalVocabulary() { 
        throw new UnsupportedOperationException();
    }

    
    public final void parse() throws IOException {
        reset();
        decodeHeader();                                                                                
        processDII();
    }
    
    protected final void processDII() throws IOException {
        try {
            _contentHandler.startDocument();
        } catch (SAXException e) {
            throw new IOException("processDII");
        }

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
                    processEII(processEIIIndexMedium(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    firstElementHasOccured = true;
                    break;
                case DecoderStateTables.EII_INDEX_LARGE:
                    processEII(processEIIIndexLarge(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
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
                        throw new IOException("A second occurence of a Document Type Declaration II is present");
                    }
                    documentTypeDeclarationOccured = true;

                    String system_identifier = ((_b & EncodingConstants.DOCUMENT_TYPE_SYSTEM_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
                    String public_identifier = ((_b & EncodingConstants.DOCUMENT_TYPE_PUBLIC_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
                    
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
                                throw new IOException("Processing II with encoding algorithm decoding not supported");                        
                            case NISTRING_INDEX:
                                break;
                            case NISTRING_EMPTY_STRING:
                                break;
                        }
                        _b = read();
                    }
                    if ((_b & EncodingConstants.TERMINATOR) != EncodingConstants.TERMINATOR) {
                        throw new IOException("Processing instruction IIs of Document Type Declaraion II not terminated correctly");
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
                    throw new IOException("Illegal state when decoding a child of an EII");
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
                    throw new IOException("Illegal state when decoding a child of an EII");
            }
        }

        try {
            _contentHandler.endDocument();
        } catch (SAXException e) {
            throw new IOException("processDII");
        }        
    }

    protected final void processDIIOptionalProperties() throws IOException {        
        if ((_b & EncodingConstants.DOCUMENT_INITIAL_VOCABULARY_FLAG) > 0) {
            decodeInitialVocabulary();
        }

        if ((_b & EncodingConstants.DOCUMENT_NOTATIONS_FLAG) > 0) {
            decodeNotations();
            /*
                try {
                    _dtdHandler.notationDecl(name, public_identifier, system_identifier);
                } catch (SAXException e) {
                    throw new IOException("NotationsDeclarationII");
                }
            */
        }

        if ((_b & EncodingConstants.DOCUMENT_UNPARSED_ENTITIES_FLAG) > 0) {
            decodeUnparsedEntities();
            /*
                try {
                    _dtdHandler.unparsedEntityDecl(name, public_identifier, system_identifier, notation_name);
                } catch (SAXException e) {
                    throw new IOException("UnparsedEntitiesII");
                }
            */
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
                    throw new IOException("Processing II with encoding algorithm decoding not supported");                        
                case NISTRING_INDEX:
                    break;
                case NISTRING_EMPTY_STRING:
                    break;
            }
            /*
             * TODO 
             * how to report the standalone flag?
             */
        }
    }
    
    protected final void processEII(QualifiedName name, boolean hasAttributes) throws IOException {
        if (hasAttributes) {
            processAIIs();
        }
        
        try {
            _contentHandler.startElement(name.namespaceName, name.localName, name.qName, _attributes);
        } catch (SAXException e) {
            e.printStackTrace();
            throw new IOException("processEII");
        }

        if (_clearAttributes) {
            _attributes.clear();
            _clearAttributes = false;
        }
        
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
                    processEII(processEIIIndexMedium(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                case DecoderStateTables.EII_INDEX_LARGE:
                    processEII(processEIIIndexLarge(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                case DecoderStateTables.EII_LITERAL:
                    processEII(processEIILiteral(), (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                case DecoderStateTables.EII_NAMESPACES:
                    processEIIWithNamespaces();
                    break;
                case DecoderStateTables.CII_UTF8_SMALL_LENGTH:
                    _octetBufferLength = (_b & EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_MASK) 
                        + 1;
                    decodeUtf8StringAsCharBuffer();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    try {
                        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                case DecoderStateTables.CII_UTF8_MEDIUM_LENGTH:
                    _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT;
                    decodeUtf8StringAsCharBuffer();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    try {
                        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                case DecoderStateTables.CII_UTF8_LARGE_LENGTH:
                    _octetBufferLength = (read() << 24) |
                        (read() << 16) |
                        (read() << 8) |
                        read();
                    _octetBufferLength += EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
                    decodeUtf8StringAsCharBuffer();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    try {
                        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                case DecoderStateTables.CII_UTF16_SMALL_LENGTH:
                    _octetBufferLength = (_b & EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_MASK) 
                        + 1;
                    decodeUtf16StringAsCharBuffer();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    try {
                        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                case DecoderStateTables.CII_UTF16_MEDIUM_LENGTH:
                    _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT;
                    decodeUtf16StringAsCharBuffer();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    try {
                        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                case DecoderStateTables.CII_UTF16_LARGE_LENGTH:
                    _octetBufferLength = (read() << 24) |
                        (read() << 16) |
                        (read() << 8) |
                        read();
                    _octetBufferLength += EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
                    decodeUtf16StringAsCharBuffer();
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    try {
                        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                case DecoderStateTables.CII_RA:
                {
                    // Decode resitricted alphabet integer
                    _identifier = (_b & 0x02) << 6;
                    final int b2 = read();
                    _identifier |= (b2 & 0xFC) >> 2;

                    decodeOctetsOfNonIdentifyingStringOnThirdBit(b2);
                    // TODO obtain restricted alphabet given _identifier value
                    decodeRAOctetsAsCharBuffer(null);                    
                    if ((_b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    try {
                        _contentHandler.characters(_charBuffer, 0, _charBufferLength);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                }
                case DecoderStateTables.CII_EA:
                {
                    if ((_b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0) {
                        throw new IOException("Add to table not supported for Encoding algorithms");
                    }

                    // Decode encoding algorithm integer
                    _identifier = (_b & 0x02) << 6;
                    final int b2 = read();
                    _identifier |= (b2 & 0xFC) >> 2;

                    decodeOctetsOfNonIdentifyingStringOnThirdBit(b2);
                    
                    if (_identifier <= EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END) {
                        // Built-in algorithms
                    } else if (_identifier >= EncodingConstants.ENCODING_ALGORITHM_APPLICATION_START) {
                        // Application-defined algorithms
                    } else {
                        // Reserved built-in algorithms for future use
                        // TODO should use sax property to decide if event will be
                        // reported, allows for support through handler if required.
                        throw new IOException("Encoding algorithm identifiers 10 up to and including 31 are reserved for future use");
                    }
                    throw new IOException("Encoding algorithms for CIIs not yet implemented");
                }
                case DecoderStateTables.CII_INDEX_SMALL:
                {
                    final CharArray ca = _v.characterContentChunk.get(_b & EncodingConstants.INTEGER_4TH_BIT_SMALL_MASK);
                    
                    try {
                        _contentHandler.characters(ca.ch, ca.start, ca.length);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                }
                case DecoderStateTables.CII_INDEX_MEDIUM:
                {
                    final int index = (((_b & EncodingConstants.INTEGER_4TH_BIT_MEDIUM_MASK) << 8) | read())
                        + EncodingConstants.INTEGER_4TH_BIT_SMALL_LIMIT;
                    final CharArray ca = _v.characterContentChunk.get(index);
                    
                    try {
                        _contentHandler.characters(ca.ch, ca.start, ca.length);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                }
                case DecoderStateTables.CII_INDEX_LARGE:
                {
                    int index = ((_b & EncodingConstants.INTEGER_4TH_BIT_LARGE_MASK) << 16) |
                        (read() << 8) |
                        read();
                    index += EncodingConstants.INTEGER_4TH_BIT_MEDIUM_LIMIT;
                    final CharArray ca = _v.characterContentChunk.get(index);
                    
                    try {
                        _contentHandler.characters(ca.ch, ca.start, ca.length);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
                    break;
                }
                case DecoderStateTables.CII_INDEX_LARGE_LARGE:
                {
                    int index = (read() << 16) | 
                        (read() << 8) |
                        read();
                    index += EncodingConstants.INTEGER_4TH_BIT_LARGE_LIMIT;
                    final CharArray ca = _v.characterContentChunk.get(index);
                    
                    try {
                        _contentHandler.characters(ca.ch, ca.start, ca.length);
                    } catch (SAXException e) {
                        throw new IOException("processCII");
                    }
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
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
                    String public_identifier = ((_b & EncodingConstants.UNEXPANDED_ENTITY_PUBLIC_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";

                    try {
                        /*
                         * TODO
                         * Need to verify if the skippedEntity method:
                         * http://java.sun.com/j2se/1.4.2/docs/api/org/xml/sax/ContentHandler.html#skippedEntity(java.lang.String)
                         * is the correct method to call. It appears so but a more extensive
                         * check is necessary.
                         */
                        _contentHandler.skippedEntity(entity_reference_name);
                    } catch (SAXException e) {
                        throw new IOException("processUnexpandedEntityReferenceII");
                    }
                    break;
                }
                case DecoderStateTables.TERMINATOR_DOUBLE:                    
                    _doubleTerminate = true; 
                case DecoderStateTables.TERMINATOR_SINGLE:
                    _terminate = true;
                    break;
                default:
                    throw new IOException("Illegal state when decoding a child of an EII");
            }
        }

        _terminate = _doubleTerminate;
        _doubleTerminate = false;
        
        try {
            _contentHandler.endElement(name.namespaceName, name.localName, name.qName);
        } catch (SAXException e) {
            throw new IOException("processEII");
        }
    }

    protected final void processEIIWithNamespaces() throws IOException {
        final boolean hasAttributes = (_b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0;

        _clearAttributes = (_namespacePrefixesFeature ) ? true : false;
                
        int start = _namespaceAIIsIndex;
        int b = read();
        while ((b & EncodingConstants.NAMESPACE_ATTRIBUTE_MASK) == EncodingConstants.NAMESPACE_ATTRIBUTE) {
            // NOTE a prefix without a namespace name is an undeclaration
            // of the namespace bound to the prefix
            // TODO need to investigate how the startPrefixMapping works in
            // relation to undeclaration

            if (_namespaceAIIsIndex == _namespaceAIIs.length) {
                final String[] namespaceAIIs = new String[_namespaceAIIsIndex * 2];
                System.arraycopy(_namespaceAIIs, 0, namespaceAIIs, 0, _namespaceAIIsIndex);
                _namespaceAIIs = namespaceAIIs;
            }

            // Prefix
            _namespaceAIIs[_namespaceAIIsIndex++] = ((b & EncodingConstants.NAMESPACE_ATTRIBUTE_PREFIX_FLAG) > 0) 
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.prefix) : "";

            // Namespace name
            _namespaceAIIs[_namespaceAIIsIndex++] = ((b & EncodingConstants.NAMESPACE_ATTRIBUTE_NAME_FLAG) > 0) 
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.namespaceName) : "";
            
            if (_namespacePrefixesFeature) {
                final String prefix = _namespaceAIIs[_namespaceAIIsIndex - 2];
                final String namespaceName = _namespaceAIIs[_namespaceAIIsIndex - 1];
                
                // Add the namespace delcaration as an attribute
                if (prefix != "") {
                    _attributes.addAttribute(new QualifiedName(
                                "xmlns", 
                                "http://www.w3.org/2000/xmlns/", 
                                prefix), 
                            namespaceName);
                } else {
                    _attributes.addAttribute(new QualifiedName(
                                "", 
                                "http://www.w3.org/2000/xmlns/", 
                                "xmlns",
                                "xmlns"), 
                            namespaceName);
                }
            }
            
            b = read();
        }
        if (b != EncodingConstants.TERMINATOR) {
            throw new IOException("Namespace names of EII not terminated correctly");
        }
        int end = _namespaceAIIsIndex;

        try {
            for (int i = start; i < end;) {
                _contentHandler.startPrefixMapping(_namespaceAIIs[i++], _namespaceAIIs[i++]);
            }
        } catch (SAXException e) {
            throw new IOException("processStartNamespaceAII");
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
                processEII(processEIIIndexMedium(), hasAttributes);
                break;
            case DecoderStateTables.EII_INDEX_LARGE:
                processEII(processEIIIndexLarge(), hasAttributes);
                break;
            case DecoderStateTables.EII_LITERAL:
                processEII(processEIILiteral(), hasAttributes);
                break;
            default:
                throw new IOException("Illegal state when decoding EII after the namespace AIIs");
        }

        try {
            for (int i = start; i < end; i += 2) {
                _contentHandler.endPrefixMapping(_namespaceAIIs[i]);
            }
            _namespaceAIIsIndex = start;
        } catch (SAXException e) {
            throw new IOException("processStartNamespaceAII");
        }
    }
    
    protected final QualifiedName processEIIIndexMedium() throws IOException {
        final int i = (((_b & EncodingConstants.INTEGER_3RD_BIT_MEDIUM_MASK) << 8) | read())
            + EncodingConstants.INTEGER_3RD_BIT_SMALL_LIMIT;
        return _v.elementName.get(i);
    }

    protected final QualifiedName processEIIIndexLarge() throws IOException {
        int i;
        if ((_b & 0x10) > 0) {
            // EII large index
            i = (((_b & EncodingConstants.INTEGER_3RD_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                + EncodingConstants.INTEGER_3RD_BIT_MEDIUM_LIMIT;
        } else {
            // EII large large index
            i = (((read() & EncodingConstants.INTEGER_3RD_BIT_LARGE_LARGE_MASK) << 16) | (read() << 8) | read()) 
                + EncodingConstants.INTEGER_3RD_BIT_LARGE_LIMIT;
        }
        return _v.elementName.get(i);
    }

    protected final QualifiedName processEIILiteral() throws IOException {
        final String prefix = ((_b & EncodingConstants.LITERAL_QNAME_PREFIX_FLAG) > 0) 
            ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.prefix) : "";
        final String namespaceName = ((_b & EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG) > 0) 
            ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.namespaceName) : "";
        final String localName = decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName);

        final QualifiedName qualifiedName = new QualifiedName(prefix, namespaceName, localName);
        _v.elementName.add(qualifiedName);
        return qualifiedName;
    }
    
    protected final void processAIIs() throws IOException {
        QualifiedName name;
        int b;
        String value;
        
        _clearAttributes = true;

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
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.prefix) : "";
                    final String namespaceName = ((b & EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.namespaceName) : "";
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
                    
                    _attributes.addAttribute(name, value);
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
                    
                    _attributes.addAttribute(name, value);
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

                    _attributes.addAttribute(name, value);
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
                    
                    _attributes.addAttribute(name, value);
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
                    
                    _attributes.addAttribute(name, value);
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

                    _attributes.addAttribute(name, value);
                    break;
                }
                case DecoderStateTables.NISTRING_RA:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    // Decode resitricted alphabet integer
                    _identifier = (b & 0x0F) << 4;
                    b = read();
                    _identifier |= (b & 0xF0) >> 4;
                    
                    decodeOctetsOfNonIdentifyingStringOnFirstBit(b);
                    // TODO obtain restricted alphabet given _identifier value
                    value = decodeRAOctetsAsString(null);
                    if (addToTable) {
                        _v.attributeValue.add(value);
                    }

                    _attributes.addAttribute(name, value);
                    break;
                }
                case DecoderStateTables.NISTRING_EA:
                {
                    final boolean addToTable = (b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    // Decode encoding algorithm integer
                    _identifier = (b & 0x0F) << 4;
                    b = read();
                    _identifier |= (b & 0xF0) >> 4;

                    decodeOctetsOfNonIdentifyingStringOnFirstBit(b);
                    throw new IOException("Encoding algorithms not supported for [normalized value] property of AII");
                }
                case DecoderStateTables.NISTRING_INDEX_SMALL:
                    value = _v.attributeValue.get(b & EncodingConstants.INTEGER_2ND_BIT_SMALL_MASK);

                    _attributes.addAttribute(name, value);
                    break;
                case DecoderStateTables.NISTRING_INDEX_MEDIUM:
                {
                    final int index = (((b & EncodingConstants.INTEGER_2ND_BIT_MEDIUM_MASK) << 8) | read()) 
                        + EncodingConstants.INTEGER_2ND_BIT_SMALL_LIMIT;
                    value = _v.attributeValue.get(index);

                    _attributes.addAttribute(name, value);
                    break;
                }
                case DecoderStateTables.NISTRING_INDEX_LARGE:
                {
                    final int index = (((b & EncodingConstants.INTEGER_2ND_BIT_LARGE_MASK) << 16) | (read() << 8) | read()) 
                        + EncodingConstants.INTEGER_2ND_BIT_MEDIUM_LIMIT;
                    value = _v.attributeValue.get(index);

                    _attributes.addAttribute(name, value);
                    break;
                }
                case DecoderStateTables.NISTRING_EMPTY:
                    _attributes.addAttribute(name, "");
                    break;
                default:
                    throw new IOException("Illegal state when decoding AII value");
            }
            
        } while (!_terminate);
        
        _terminate = _doubleTerminate;
        _doubleTerminate = false;
    }

    protected final void processCommentII() throws IOException {
        switch(decodeNonIdentifyingStringOnFirstBit()) {
            case NISTRING_STRING:
                if (_addToTable) {
                    _v.otherString.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                }
                
                try {
                    _lexicalHandler.comment(_charBuffer, 0, _charBufferLength);
                } catch (SAXException e) {
                    throw new IOException("processCommentII");
                }
                break;
            case NISTRING_ENCODING_ALGORITHM:
                throw new IOException("Comment II with encoding algorithm decoding not supported");                        
            case NISTRING_INDEX:
                final CharArray ca = _v.otherString.get(_integer);

                try {
                    _lexicalHandler.comment(ca.ch, ca.start, ca.length);
                } catch (SAXException e) {
                    throw new IOException("processCommentII");
                }
                break;
            case NISTRING_EMPTY_STRING:
                try {
                    _lexicalHandler.comment(_charBuffer, 0, 0);
                } catch (SAXException e) {
                    throw new IOException("processCommentII");
                }
                break;
        }        
    }

    protected final void processProcessingII() throws IOException {
        final String target = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);

        switch(decodeNonIdentifyingStringOnFirstBit()) {
            case NISTRING_STRING:
                final String data = new String(_charBuffer, 0, _charBufferLength);
                if (_addToTable) {
                    _v.otherString.add(new CharArrayString(data));
                }
                try {
                    _contentHandler.processingInstruction(target, data);
                } catch (SAXException e) {
                    throw new IOException("processProcessingII");
                }
                break;
            case NISTRING_ENCODING_ALGORITHM:
                throw new IOException("Processing II with encoding algorithm decoding not supported");                        
            case NISTRING_INDEX:
                try {
                    _contentHandler.processingInstruction(target, _v.otherString.get(_integer).toString());
                } catch (SAXException e) {
                    throw new IOException("processProcessingII");
                }
                break;
            case NISTRING_EMPTY_STRING:
                try {
                    _contentHandler.processingInstruction(target, "");
                } catch (SAXException e) {
                    throw new IOException("processProcessingII");
                }
                break;
        }
    }
    
}
