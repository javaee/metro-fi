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


package com.sun.xml.fastinfoset.stax;

import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.DecoderStateTables;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayString;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.fastinfoset.util.XMLChar;
import com.sun.xml.fastinfoset.util.EventLocation;
import java.io.IOException;
import java.io.InputStream;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StAXDocumentParser extends Decoder implements XMLStreamReader {
    /**
     * Current event
     */
    protected int _eventType;

    /**
     * Flag indicating processing of a DII and children
     */
    protected boolean _processingDII;
    
    /**
     * Stack of elements.
     */
    protected ElementHolder[] _elementStack = new ElementHolder[32];
    protected int _elementStackCount = -1;

    /**
     * Namespaces associated with START_ELEMENT or END_ELEMENT.
     */
    protected QualifiedNameArray _namespaces;

    /**
     * Qualified name associated with START_ELEMENT or END_ELEMENT.
     */
    protected QualifiedName _qualifiedName;

    /**
     * List of attributes
     */
    protected AttributesHolder _attributes = new AttributesHolder();

    /**
     * Characters associated with event.
     */
    protected char[] _characters;
    protected int _charactersOffset;
    protected int _charactersLength;
        
    /**
     * State for processing instruction
     */
    protected String _piTarget;
    protected String _piData;

    /**
     * Mapping between prefixes and URIs.
     */
    protected Map _prefixMap = new HashMap();

    protected StAXManager _manager;
    
    public StAXDocumentParser() {
        _eventType = START_DOCUMENT;
    }
    public StAXDocumentParser(InputStream s) {
        this();
        setInputStream(s);
    }
    public StAXDocumentParser(InputStream s, StAXManager manager) {
        this(s);
        _manager = manager;
    }
    
    public void reset() {
        super.reset();
        
        _eventType = START_DOCUMENT;

        _prefixMap.clear();
        pushNamespaceDecl(new QualifiedName("", ""));
        pushNamespaceDecl(new QualifiedName("xml", "http://www.w3.org/XML/1998/namespace"));
    }
    
    // -- XMLStreamReader Interface -------------------------------------------
    
    public Object getProperty(java.lang.String name)
        throws java.lang.IllegalArgumentException 
    {
        if (_manager != null) {
            return _manager.getProperty(name);
        }
        return null;
    }
    
    public int next() throws XMLStreamException {
        try {
            // TODO require logic for processing items 
            // between START_DOCUMENT and START_ELEMENT and
            // the between last END_ELEMENT and END_DOCUMENT

            // TODO use 'internal state' to avoid unecessary checks
            switch (_eventType) {
                case START_DOCUMENT:
                    decodeHeader();
                    decodeDII();
                    break;
                case START_ELEMENT:    
                    // Check for EII with no attributes or children termination
                    if (_elementWithAttributesNoChildrenTermination) {
                        _terminate = false;
                        _elementWithAttributesNoChildrenTermination = false;

                        // Pop element
                        ElementHolder eh = _elementStack[_elementStackCount];
                        _elementStack[_elementStackCount--] = null;

                        // Set to popped element
                        _namespaces = eh.namespaces;
                        _qualifiedName = eh.qualifiedName;
                        return _eventType = END_ELEMENT;
                    }
                    break;
                case END_DOCUMENT:
                    throw new NoSuchElementException("No more events to report (EOF).");
                case END_ELEMENT:
                    // Undeclare namespaces
                    if (_namespaces != null) {
                        for (int i = 0; i < _namespaces.getSize(); i++) {
                            popNamespaceDecl(_namespaces.getArray()[i].prefix);
                        }
                    }

                    // Check for double EII or DII termination
                    if (_terminate) {
                        if (_elementStackCount == -1) {
                            return _eventType = END_DOCUMENT;
                        }

                        _terminate = false;

                        // Pop element
                        ElementHolder eh = _elementStack[_elementStackCount];
                        _elementStack[_elementStackCount--] = null;

                        // Set to popped element
                        _namespaces = eh.namespaces;
                        _qualifiedName = eh.qualifiedName;
                        return _eventType = END_ELEMENT;
                    }
                    break;
            }
            
            // Reset internal state
            _characters = null;
            _namespaces = null;
            
            // Process information item
            int b = read();            
            switch(DecoderStateTables.EII[b]) {
                case DecoderStateTables.EII_NO_AIIS_INDEX_SMALL:
                    processEII(_v.elementName.get(b), false);
                    break;
                case DecoderStateTables.EII_AIIS_INDEX_SMALL:
                    processEII(_v.elementName.get(b & EncodingConstants.INTEGER_3RD_BIT_SMALL_MASK), true);
                    break;
                case DecoderStateTables.EII_INDEX_MEDIUM:
                {
                    final int i = (((b & EncodingConstants.INTEGER_3RD_BIT_MEDIUM_MASK) << 8) | read())
                        + EncodingConstants.INTEGER_3RD_BIT_SMALL_LIMIT;
                    processEII(_v.elementName.get(i), (b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                }
                case DecoderStateTables.EII_INDEX_LARGE:
                {
                    int i;
                    if ((b & 0x10) > 0) {
                        // EII large index
                        i = (((b & EncodingConstants.INTEGER_3RD_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                            + EncodingConstants.INTEGER_3RD_BIT_MEDIUM_LIMIT;
                    } else {
                        // EII large large index
                        i = (((read() & EncodingConstants.INTEGER_3RD_BIT_LARGE_LARGE_MASK) << 16) | (read() << 8) | read()) 
                            + EncodingConstants.INTEGER_3RD_BIT_LARGE_LIMIT;
                    }
                    processEII(_v.elementName.get(i), (b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                }
                case DecoderStateTables.EII_LITERAL:
                {
                    final String prefix = ((b & EncodingConstants.LITERAL_QNAME_PREFIX_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.prefix) : "";
                    final String namespaceName = ((b & EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.namespaceName) : "";
                    final String localName = decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName);

                    final QualifiedName qualifiedName = new QualifiedName(prefix, namespaceName, localName);
                    _v.elementName.add(qualifiedName);
                    processEII(qualifiedName, (b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                }
                case DecoderStateTables.EII_NAMESPACES:
                    processEIIWithNamespaces((b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                    break;
                case DecoderStateTables.CII_UTF8_SMALL_LENGTH:
                    _octetBufferLength = (b & EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_MASK) 
                        + 1;
                    decodeUtf8StringAsCharBuffer();
                    if ((b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    _eventType = CHARACTERS;
                    _characters = _charBuffer;
                    _charactersOffset = 0;
                    _charactersLength = _charBufferLength;
                    break;
                case DecoderStateTables.CII_UTF8_MEDIUM_LENGTH:
                    _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT;
                    decodeUtf8StringAsCharBuffer();
                    if ((b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    _eventType = CHARACTERS;
                    _characters = _charBuffer;
                    _charactersOffset = 0;
                    _charactersLength = _charBufferLength;
                    break;
                case DecoderStateTables.CII_UTF8_LARGE_LENGTH:
                    _octetBufferLength = (read() << 24) |
                        (read() << 16) |
                        (read() << 8) |
                        read();
                    _octetBufferLength += EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
                    decodeUtf8StringAsCharBuffer();
                    if ((b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    _eventType = CHARACTERS;
                    _characters = _charBuffer;
                    _charactersOffset = 0;
                    _charactersLength = _charBufferLength;
                    break;
                case DecoderStateTables.CII_UTF16_SMALL_LENGTH:
                    _octetBufferLength = (b & EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_MASK) 
                        + 1;
                    decodeUtf16StringAsCharBuffer();
                    if ((b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    _eventType = CHARACTERS;
                    _characters = _charBuffer;
                    _charactersOffset = 0;
                    _charactersLength = _charBufferLength;
                    break;
                case DecoderStateTables.CII_UTF16_MEDIUM_LENGTH:
                    _octetBufferLength = read() + EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_SMALL_LIMIT;
                    decodeUtf16StringAsCharBuffer();
                    if ((b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    _eventType = CHARACTERS;
                    _characters = _charBuffer;
                    _charactersOffset = 0;
                    _charactersLength = _charBufferLength;
                    break;
                case DecoderStateTables.CII_UTF16_LARGE_LENGTH:
                    _octetBufferLength = (read() << 24) |
                        (read() << 16) |
                        (read() << 8) |
                        read();
                    _octetBufferLength += EncodingConstants.OCTET_STRING_LENGTH_7TH_BIT_MEDIUM_LIMIT;
                    decodeUtf16StringAsCharBuffer();
                    if ((b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    _eventType = CHARACTERS;
                    _characters = _charBuffer;
                    _charactersOffset = 0;
                    _charactersLength = _charBufferLength;
                    break;
                case DecoderStateTables.CII_RA:
                {
                    // Decode resitricted alphabet integer
                    _identifier = (b & 0x02) << 6;
                    final int b2 = read();
                    _identifier |= (b2 & 0xFC) >> 2;

                    decodeOctetsOfNonIdentifyingStringOnThirdBit(b2);
                    // TODO obtain restricted alphabet given _identifier value
                    decodeRAOctetsAsCharBuffer(null);                    
                    if ((b & EncodingConstants.CHARACTER_CHUNK_ADD_TO_TABLE_FLAG) > 0) {
                        _v.characterContentChunk.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                    }
                    
                    _eventType = CHARACTERS;
                    _characters = _charBuffer;
                    _charactersOffset = 0;
                    _charactersLength = _charBufferLength;
                    break;
                }
                case DecoderStateTables.CII_EA:
                {
                    final boolean addToTable = (_b & EncodingConstants.NISTRING_ADD_TO_TABLE_FLAG) > 0;
                    // Decode encoding algorithm integer
                    _identifier = (_b & 0x02) << 6;
                    final int b2 = read();
                    _identifier |= (b2 & 0xFC) >> 2;

                    decodeOctetsOfNonIdentifyingStringOnThirdBit(b2);
                    throw new IOException("Encoding algorithms for CIIs not yet implemented");
                }
                case DecoderStateTables.CII_INDEX_SMALL:
                {
                    final CharArray ca = _v.characterContentChunk.get(b & EncodingConstants.INTEGER_4TH_BIT_SMALL_MASK);
                    
                    _eventType = CHARACTERS;
                    _characters = ca.ch;
                    _charactersOffset = ca.start;
                    _charactersLength = ca.length;
                    break;
                }
                case DecoderStateTables.CII_INDEX_MEDIUM:
                {
                    final int index = (((b & EncodingConstants.INTEGER_4TH_BIT_MEDIUM_MASK) << 8) | read())
                        + EncodingConstants.INTEGER_4TH_BIT_SMALL_LIMIT;
                    final CharArray ca = _v.characterContentChunk.get(index);
                    
                    _eventType = CHARACTERS;
                    _characters = ca.ch;
                    _charactersOffset = ca.start;
                    _charactersLength = ca.length;
                    break;
                }
                case DecoderStateTables.CII_INDEX_LARGE:
                {
                    int index = ((b & EncodingConstants.INTEGER_4TH_BIT_LARGE_MASK) << 16) |
                        (read() << 8) |
                        read();
                    index += EncodingConstants.INTEGER_4TH_BIT_MEDIUM_LIMIT;
                    final CharArray ca = _v.characterContentChunk.get(index);
                    
                    _eventType = CHARACTERS;
                    _characters = ca.ch;
                    _charactersOffset = ca.start;
                    _charactersLength = ca.length;
                    break;
                }
                case DecoderStateTables.CII_INDEX_LARGE_LARGE:
                {
                    int index = (read() << 16) | 
                        (read() << 8) |
                        read();
                    index += EncodingConstants.INTEGER_4TH_BIT_LARGE_LIMIT;
                    final CharArray ca = _v.characterContentChunk.get(index);
                    
                    _eventType = CHARACTERS;
                    _characters = ca.ch;
                    _charactersOffset = ca.start;
                    _charactersLength = ca.length;
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
                    /*
                     * TODO
                     * How does StAX report such events?
                     */
                    String entity_reference_name = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);
                    
                    String system_identifier = ((_b & EncodingConstants.UNEXPANDED_ENTITY_SYSTEM_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
                    String public_identifier = ((_b & EncodingConstants.UNEXPANDED_ENTITY_PUBLIC_IDENTIFIER_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherURI) : "";
                }
                case DecoderStateTables.TERMINATOR_DOUBLE:                    
                    _doubleTerminate = true; 
                case DecoderStateTables.TERMINATOR_SINGLE:
                    _terminate = true;
                    break;
                default:
                    throw new IOException("Illegal state when decoding a child of an EII");
            }
            
            
            if (_terminate && _elementWithAttributesNoChildrenTermination == false) {
                if (_elementStackCount == -1) {
                    return _eventType = END_DOCUMENT;
                }

                _terminate = _doubleTerminate;
                _doubleTerminate = false;

                // Pop element
                ElementHolder eh = _elementStack[_elementStackCount];
                _elementStack[_elementStackCount--] = null;

                // Set to popped element
                _namespaces = eh.namespaces;
                _qualifiedName = eh.qualifiedName;
                return _eventType = END_ELEMENT;
            } else {
                return _eventType;
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new XMLStreamException(e);
        }
    }
    
    /** Test if the current event is of the given type and if the namespace and name match the current namespace and name of the current event.
     * If the namespaceURI is null it is not checked for equality, if the localName is null it is not checked for equality.
     * @param type the event type
     * @param namespaceURI the uri of the event, may be null
     * @param localName the localName of the event, may be null
     * @throws XMLStreamException if the required values are not matched.
     */    
    public void require(int type, String namespaceURI, String localName) 
        throws XMLStreamException
    {
        if( type != _eventType)
            throw new XMLStreamException("Event type " +getEventTypeString(type)+" specified did not match with current parser event");
        if( namespaceURI != null && !namespaceURI.equals(getNamespaceURI()) )
            throw new XMLStreamException("Namespace URI " +namespaceURI+" specified did not match with current namespace URI");
        if(localName != null && !localName.equals(getLocalName()))
            throw new XMLStreamException("LocalName " +localName+" specified did not match with current local name");
        
        return;
    }

    /** Reads the content of a text-only element. Precondition:
     * the current event is START_ELEMENT. Postcondition:
     * The current event is the corresponding END_ELEMENT.
     * @throws XMLStreamException if the current event is not a START_ELEMENT or if
     * a non text element is encountered
     */
    public String getElementText() throws XMLStreamException {
        
        if(getEventType() != START_ELEMENT) {
            throw new XMLStreamException(
            "parser must be on START_ELEMENT to read next text", getLocation());
        }
        //current is StartElement, move to the next
        int eventType = next();
        return getElementText(true);
    }
    /**
     * @param startElementRead flag if start element has already been read
     */
    public String getElementText(boolean startElementRead) throws XMLStreamException {
        if (!startElementRead) {
            throw new XMLStreamException(
            "parser must be on START_ELEMENT to read next text", getLocation());            
        }
        int eventType = getEventType();
        StringBuffer content = new StringBuffer();
        while(eventType != END_ELEMENT ) {
            if(eventType == CHARACTERS
                || eventType == CDATA
                || eventType == SPACE
                || eventType == ENTITY_REFERENCE) 
            {
                content.append(getText());
            } else if(eventType == PROCESSING_INSTRUCTION
                    || eventType == COMMENT) {
                // skipping
            } else if(eventType == END_DOCUMENT) {
                throw new XMLStreamException("unexpected end of document when reading element text content");
            } else if(eventType == START_ELEMENT) {
                throw new XMLStreamException(
                "getElementText() function expects text only elment but START_ELEMENT was encountered.", getLocation());
            } else {
                throw new XMLStreamException(
                "Unexpected event type "+ getEventTypeString(eventType), getLocation());
            }
            eventType = next();
        }
        return content.toString();        
    }
    
    /** Skips any white space (isWhiteSpace() returns true), COMMENT,
     * or PROCESSING_INSTRUCTION,
     * until a START_ELEMENT or END_ELEMENT is reached.
     * If other than white space characters, COMMENT, PROCESSING_INSTRUCTION, START_ELEMENT, END_ELEMENT
     * are encountered, an exception is thrown. This method should
     * be used when processing element-only content seperated by white space. 
     * This method should
     * be used when processing element-only content because
     * the parser is not able to recognize ignorable whitespace if
     * then DTD is missing or not interpreted.
     * @return the event type of the element read
     * @throws XMLStreamException if the current event is not white space
     */
    public int nextTag() throws XMLStreamException {        
        int eventType = next();
        return nextTag(true);
    }
    /** if the current tag has already read, such as in the case EventReader's 
     * peek() has been called, the current cursor should not move before the loop
     */
    public int nextTag(boolean currentTagRead) throws XMLStreamException {
        int eventType = getEventType();
        if (!currentTagRead) {
            eventType = next();        
        }
        while((eventType == CHARACTERS && isWhiteSpace()) // skip whitespace
            || (eventType == CDATA && isWhiteSpace())
            || eventType == SPACE
            || eventType == PROCESSING_INSTRUCTION
            || eventType == COMMENT) 
        {
            eventType = next();
        }
        if (eventType != START_ELEMENT && eventType != END_ELEMENT) {
            throw new XMLStreamException("expected start or end tag", getLocation());
        }
        return eventType;
    }
        
    public boolean hasNext() throws XMLStreamException {
        return (_eventType != END_DOCUMENT);
    }
    
    public void close() throws XMLStreamException {
        reset();
    }
    
    public String getNamespaceURI(String prefix) {
        QualifiedName n = getNamespaceDecl(prefix);
        if (n == null) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix cannot be null.");
            }
            return null;  // unbound
        }
        return n.namespaceName;
    }
    
    public boolean isStartElement() {
        return (_eventType == START_ELEMENT);
    }
    
    public boolean isEndElement() {
        return (_eventType == END_ELEMENT);
    }
    
    public boolean isCharacters() {
        return (_eventType == CHARACTERS);
    }
    
    /**
     *  Returns true if the cursor points to a character data event that consists of all whitespace
     *  Application calling this method needs to cache the value and avoid calling this method again
     *  for the same event.
     * @return true if the cursor points to all whitespace, false otherwise
     */
    public boolean isWhiteSpace() {
        if(isCharacters() || (_eventType == CDATA)){
            char [] ch = this.getTextCharacters();
            int start = this.getTextStart();
            int length = this.getTextLength();
            for (int i=start; i< length;i++){
                if(!XMLChar.isSpace(ch[i])){
                    return false;
                }
            }
            return true;
        }
        return false;
        //throw new UnsupportedOperationException("Not implemented");
    }
    
    public String getAttributeValue(String namespaceURI, String localName) {
        if (_eventType != START_ELEMENT) {
            throw new IllegalStateException("Method getAttributeValue() called in invalid state");
        }
        
        // Search for the attributes in _attributes
        for (int i = 0; i < _attributes.getLength(); i++) {
            if (_attributes.getLocalName(i) == localName &&
                _attributes.getURI(i) == namespaceURI)
            {
                return _attributes.getValue(i);
            }
        }
        return null;
    }
    
    public int getAttributeCount() {
        if (_eventType != START_ELEMENT) {
            throw new IllegalStateException("Method getAttributeValue() called in invalid state");
        }      

        return _attributes.getLength();
    }
    
    public javax.xml.namespace.QName getAttributeName(int index) {
        if (_eventType != START_ELEMENT) {
            throw new IllegalStateException("Method getAttributeValue() called in invalid state");
        }      
        return _attributes.getQualifiedName(index).getQName();
    }

    public String getAttributeNamespace(int index) {
        if (_eventType != START_ELEMENT) {
            throw new IllegalStateException("Method getAttributeValue() called in invalid state");
        }      

        return _attributes.getURI(index);
    }

    public String getAttributeLocalName(int index) {
        if (_eventType != START_ELEMENT) {
            throw new IllegalStateException("Method getAttributeValue() called in invalid state");
        }      
        return _attributes.getLocalName(index);
    }
    
    public String getAttributePrefix(int index) {
        if (_eventType != START_ELEMENT) {
            throw new IllegalStateException("Method getAttributeValue() called in invalid state");
        }      
        return _attributes.getPrefix(index);
    }
    
    public String getAttributeType(int index) {
        if (_eventType != START_ELEMENT) {
            throw new IllegalStateException("Method getAttributeValue() called in invalid state");
        }      
        return _attributes.getType(index);
    }
    
    public String getAttributeValue(int index) {
        if (_eventType != START_ELEMENT) {
            throw new IllegalStateException("Method getAttributeValue() called in invalid state");
        }      
        return _attributes.getValue(index);
    }
    
    public boolean isAttributeSpecified(int index) {
        return false;   // non-validating parser
    }
    
    public int getNamespaceCount() {
        if (_eventType == START_ELEMENT || _eventType == END_ELEMENT) {
            return (_namespaces != null) ? _namespaces.getSize() : 0;
        } else {
            throw new IllegalStateException("Method getNamespaceCount() called in invalid state");        
        }
    }
    
    public String getNamespacePrefix(int index) {
        if (_eventType == START_ELEMENT || _eventType == END_ELEMENT) {
            return _namespaces.getArray()[index].prefix;
        } else {
            throw new IllegalStateException("Method getNamespacePrefix() called in invalid state");        
        }
    }
    
    public String getNamespaceURI(int index) {
        if (_eventType == START_ELEMENT || _eventType == END_ELEMENT) {
            return _namespaces.getArray()[index].namespaceName;
        } else {
            throw new IllegalStateException("Method getNamespacePrefix() called in invalid state");        
        }
    }
    
    public NamespaceContext getNamespaceContext() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public int getEventType() {
        return _eventType;
    }
    
    public String getText() {
        if (_characters == null) {
            throw new IllegalStateException("Method getText() called in invalid state");        
        }
        
        return new String(_characters, 
                          _charactersOffset, 
                          _charactersLength);
    }
    
    public char[] getTextCharacters() {
        if (_characters == null) {
            throw new IllegalStateException("Method getText() called in invalid state");        
        }
        
        return _characters;
    }

    public int getTextStart() {
        if (_characters == null) {
            throw new IllegalStateException("Method getTextStart() called in invalid state");        
        }
        
        
        return 0;
    }
    
    public int getTextLength() {
        if (_characters == null) {
            throw new IllegalStateException("Method getTextStart() called in invalid state");        
        }
        
        return _charactersLength;
    }
    
    public int getTextCharacters(int sourceStart, char[] target, 
        int targetStart, int length) throws XMLStreamException
    {
        if (_characters == null) {
            throw new IllegalStateException("Method getText() called in invalid state");        
        }

        try {
            // TODO: other cases?
            System.arraycopy(_characters, sourceStart, target, 
                targetStart, length);
            return length;
        }
        catch (IndexOutOfBoundsException e) {
            throw new XMLStreamException(e);
        }
    }
        
    public String getEncoding() {
        return "UTF-8";     // for now
    }
    
    public boolean hasText() {
        return (_characters != null);
    }
    
    public Location getLocation() {
        //location should be created in next()
        //returns a nil location for now
        return EventLocation.getNilLocation();
    }
    
    public QName getName() {
        if (_eventType == START_ELEMENT || _eventType == END_ELEMENT) {
            return _qualifiedName.getQName();
        } else {
            throw new IllegalStateException("Method getName() called in invalid state");        
        }
    }
    
    public String getLocalName() {
        if (_eventType == START_ELEMENT || _eventType == END_ELEMENT) {
             return _qualifiedName.localName;
        } else {
            throw new IllegalStateException("Method getLocalName() called in invalid state");        
        }
    }
    
    public boolean hasName() {
        return (_eventType == START_ELEMENT || _eventType == END_ELEMENT);
    }
    
    public String getNamespaceURI() {
        if (_eventType == START_ELEMENT || _eventType == END_ELEMENT) {
             return _qualifiedName.namespaceName;
        } else {
            throw new IllegalStateException("Method getNamespaceURI() called in invalid state");        
        }
    }
    
    public String getPrefix() {
        if (_eventType == START_ELEMENT || _eventType == END_ELEMENT) {
            return _qualifiedName.prefix;
        } else {
            throw new IllegalStateException("Method getPrefix() called in invalid state");        
        }
    }
    
    public String getVersion() {
        return null;
    }
    
    public boolean isStandalone() {
        return false;
    }
    
    public boolean standaloneSet() {
        return false;
    }
    
    public String getCharacterEncodingScheme() {
        return null;
    }
    
    public String getPITarget() {
        if (_eventType != PROCESSING_INSTRUCTION) {
            throw new IllegalStateException("Method getPITarget() called in invalid state");        
        }
        
        return _piTarget;
    }
    
    public String getPIData() {
        if (_eventType != PROCESSING_INSTRUCTION) {
            throw new IllegalStateException("Method getPIData() called in invalid state");        
        }
        
        return _piData;
    }


    // 
    
    protected final void processEIIWithNamespaces(boolean hasAttributes) throws IOException {        
        _namespaces = new QualifiedNameArray(4);

        int b = read();
        while ((b & EncodingConstants.NAMESPACE_ATTRIBUTE_MASK) == EncodingConstants.NAMESPACE_ATTRIBUTE) {
            // NOTE a prefix without a namespace name is an undeclaration
            // of the namespace bound to the prefix
            // TODO need to investigate how the startPrefixMapping works in
            // relation to undeclaration

            final boolean hasPrefix = (b & EncodingConstants.NAMESPACE_ATTRIBUTE_PREFIX_FLAG) > 0;
            final boolean hasNamespaceName = (b & EncodingConstants.NAMESPACE_ATTRIBUTE_NAME_FLAG) > 0;

            String prefix = (hasPrefix)
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.prefix) : "";
            String namespaceName = (hasNamespaceName) 
                ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.namespaceName) : "";

            final QualifiedName name = new QualifiedName(prefix, namespaceName);
            _namespaces.add(name);
    
            // Push namespace declarations onto the stack
            pushNamespaceDecl(name);

            b = read();
        }
        if (b != EncodingConstants.TERMINATOR) {
            throw new IOException("Namespace names of EII not terminated correctly");
        }

        b = read();
        if (hasAttributes) {
            // Re-flag attribute flag
            // This is so the EII table can be reused.
            b |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
        }

        switch(DecoderStateTables.EII[b]) {
            case DecoderStateTables.EII_NO_AIIS_INDEX_SMALL:
                processEII(_v.elementName.get(b), false);
                break;
            case DecoderStateTables.EII_AIIS_INDEX_SMALL:
                processEII(_v.elementName.get(b & EncodingConstants.INTEGER_3RD_BIT_SMALL_MASK), true);
                break;
            case DecoderStateTables.EII_INDEX_MEDIUM:
            {
                final int i = (((b & EncodingConstants.INTEGER_3RD_BIT_MEDIUM_MASK) << 8) | read())
                    + EncodingConstants.INTEGER_3RD_BIT_SMALL_LIMIT;
                processEII(_v.elementName.get(i), (b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                break;
            }
            case DecoderStateTables.EII_INDEX_LARGE:
            {
                int i;
                if ((b & 0x10) > 0) {
                    // EII large index
                    i = (((b & EncodingConstants.INTEGER_3RD_BIT_LARGE_MASK) << 16) | (read() << 8) | read())
                        + EncodingConstants.INTEGER_3RD_BIT_MEDIUM_LIMIT;
                } else {
                    // EII large large index
                    i = (((read() & EncodingConstants.INTEGER_3RD_BIT_LARGE_LARGE_MASK) << 16) | (read() << 8) | read()) 
                        + EncodingConstants.INTEGER_3RD_BIT_LARGE_LIMIT;
                }
                processEII(_v.elementName.get(i), (b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                break;
            }
            case DecoderStateTables.EII_LITERAL:
            {
                final String prefix = ((b & EncodingConstants.LITERAL_QNAME_PREFIX_FLAG) > 0) 
                    ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.prefix) : "";
                final String namespaceName = ((b & EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG) > 0) 
                    ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.namespaceName) : "";
                final String localName = decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName);

                final QualifiedName qualifiedName = new QualifiedName(prefix, namespaceName, localName);
                _v.elementName.add(qualifiedName);
                processEII(qualifiedName, (b & EncodingConstants.ELEMENT_ATTRIBUTE_FLAG) > 0);
                break;
            }
            default:
                throw new IOException("Illegal state when decoding EII after the namespace AIIs");
        }
    }
    
    protected final void processEII(QualifiedName name, boolean hasAttributes) throws IOException {
        _eventType = START_ELEMENT;
        _qualifiedName = name;

        _attributes.clear();
        if (hasAttributes) {
            processAIIs();
        }

        // Push element holder onto the stack
        _elementStackCount++;
        if (_elementStackCount == _elementStack.length) {
            ElementHolder[] elementStack = new ElementHolder[_elementStack.length * 2];
            System.arraycopy(_elementStack, 0, elementStack, 0, _elementStack.length);
            _elementStack = elementStack;
        }
        _elementStack[_elementStackCount] = new ElementHolder(_qualifiedName, _namespaces);
    }
    
    protected final void processAIIs() throws IOException {
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
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.prefix) : "";
                    final String namespaceName = ((b & EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG) > 0) 
                        ? decodeIdentifyingNonEmptyStringOnFirstBit(_v.namespaceName) : "";
                    final String localName = decodeIdentifyingNonEmptyStringOnFirstBit(_v.localName);

                    name = new QualifiedName(prefix, namespaceName, localName);
                    _v.attributeName.add(name);
                    break;
                }
                case DecoderStateTables.AII_TERMINATOR_DOUBLE:                    
                    _terminate = _doubleTerminate = _elementWithAttributesNoChildrenTermination = true;
                    continue;
                case DecoderStateTables.AII_TERMINATOR_SINGLE:
                    _terminate = true;
                    _elementWithAttributesNoChildrenTermination = false;
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
        _eventType = COMMENT;

        switch(decodeNonIdentifyingStringOnFirstBit()) {
            case NISTRING_STRING:
                if (_addToTable) {
                    _v.otherString.add(new CharArray(_charBuffer, 0, _charBufferLength, true));
                }
                
                _characters = _charBuffer;
                _charactersOffset = 0;
                _charactersLength = _charBufferLength;
                break;
            case NISTRING_ENCODING_ALGORITHM:
                throw new IOException("Comment II with encoding algorithm decoding not supported");                        
            case NISTRING_INDEX:
                final CharArray ca = _v.otherString.get(_integer);

                _characters = ca.ch;
                _charactersOffset = ca.start;
                _charactersLength = ca.length;
                break;
            case NISTRING_EMPTY_STRING:
                _characters = _charBuffer;
                _charactersOffset = 0;
                _charactersLength = 0;
                break;
        }        
    }

    protected final void processProcessingII() throws IOException {
        _eventType = PROCESSING_INSTRUCTION;

        _piTarget = decodeIdentifyingNonEmptyStringOnFirstBit(_v.otherNCName);

        switch(decodeNonIdentifyingStringOnFirstBit()) {
            case NISTRING_STRING:
                _piData = new String(_charBuffer, 0, _charBufferLength);
                if (_addToTable) {
                    _v.otherString.add(new CharArrayString(_piData));
                }
                break;
            case NISTRING_ENCODING_ALGORITHM:
                throw new IOException("Processing II with encoding algorithm decoding not supported");                        
            case NISTRING_INDEX:
                _piTarget = _v.otherString.get(_integer).toString();
                break;
            case NISTRING_EMPTY_STRING:
                _piTarget = "";
                break;
        }
    }
    
    
    
    
    public QualifiedName getNamespaceDecl(String prefix) {
        try {
            Object o = _prefixMap.get(prefix);
            if (o instanceof QualifiedName) {
                return (QualifiedName) o;
            }
            else if (o instanceof Stack) {
                return (QualifiedName) ((Stack) o).peek();
            }
        }
        catch (EmptyStackException e) {
            // falls through
        }
        return null;
    }
    
    private QualifiedName popNamespaceDecl(String prefix) {
        try { 
            Object o = _prefixMap.get(prefix);
            if (o instanceof QualifiedName) {
                _prefixMap.remove(prefix);
                return (QualifiedName) o;
            }
            else if (o instanceof Stack) {
                return (QualifiedName) ((Stack) o).pop();
            }
        }
        catch (EmptyStackException e) {
            // falls through
        }
        return null;
    }
    
    private void pushNamespaceDecl(QualifiedName nsh) {
        Object o = null;
        try {
            o = _prefixMap.get(nsh.prefix);
            if (o == null) {
                _prefixMap.put(nsh.prefix, nsh);
            }
            else if (o instanceof QualifiedName) {
                Stack s = new Stack();
                s.push(o); s.push(nsh);
                _prefixMap.put(nsh.prefix, s);
            }
            else {
                ((Stack) o).push(nsh);
            }
        }
        catch (ClassCastException e) {
            throw new RuntimeException("Malformed namespace stack.");
        }
    }

    
    public AttributesHolder getAttributes() {
        return _attributes;
    }
    
    public String getURI(String prefix) {
        QualifiedName nsHolder = getNamespaceDecl(prefix);
        return (nsHolder != null) ? nsHolder.namespaceName : null;
    }
    
    public Iterator getPrefixes() {
        return _prefixMap.keySet().iterator();
    }

    
    public void setManager(StAXManager manager) {
        _manager = manager;
    }
    final static String getEventTypeString(int eventType) {
        switch (eventType){
            case START_ELEMENT:
                return "START_ELEMENT";
            case END_ELEMENT:
                return "END_ELEMENT";
            case PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case CHARACTERS:
                return "CHARACTERS";
            case COMMENT:
                return "COMMENT";
            case START_DOCUMENT:
                return "START_DOCUMENT";
            case END_DOCUMENT:
                return "END_DOCUMENT";
            case ENTITY_REFERENCE:
                return "ENTITY_REFERENCE";
            case ATTRIBUTE:
                return "ATTRIBUTE";
            case DTD:
                return "DTD";
            case CDATA:
                return "CDATA";
        }
        return "UNKNOWN_EVENT_TYPE";
    }
           
}
