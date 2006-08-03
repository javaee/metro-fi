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

package com.sun.xml.fastinfoset.stax.enchanced;

import com.sun.xml.fastinfoset.EncodingConstants;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;

/**
 * Enchanced Fast Infoset StAX serializer.
 * <p>
 * This class provides additional stream-based serialization methods for the
 * case where an application is in specific control of the serialization
 * process and has the knowledge to call the enchanced methods in the required
 * order.
 * <p>
 * For example, the application may be able to perform efficient information 
 * to indexing mapping and to provide certain information in UTF-8 encoded form.
 * <p>
 * These methods may be used in conjuction with {@link StAXDocumentSerializer}
 * as long as an element fragment written using the efficient streaming methods
 * are self-contained and no sub-fragment is written using methods from 
 * {@link StAXDocumentSerializer}.
 * <p>
 * The required call sequence is as follows:
 * <pre>
 * CALLSEQUENCE    := {@link #startDocument startDocument} 
 *                    initiateElementFragment ELEMENT 
 *                    {@link #endDocument endDocument}
 *                 |  initiateElementFragment ELEMENT   // for fragment
 *
 * ELEMENT         := writeEnhancedTerminationAndMark
 *                    NAMESPACES?
 *                    ELEMENT_NAME
 *                    ATTRIBUTES? 
 *                    writeEnhancedEndStartElement
 *                    CONTENTS
 *                    writeEnhancedEndElement
 *
 * NAMESPACES      := writeEnhancedStartNamespaces
 *                    writeEnhancedNamespace* 
 *                    writeEnhancedEndNamespaces
 *
 * ELEMENT_NAME    := writeEnhancedStartElementIndexed
 *                 |  writeEnhancedStartNameLiteral
 *                 |  writeEnhancedStartElement
 * 
 * ATTRUBUTES      := writeEnhancedStartAttributes
 *                   (ATTRIBUTE_NAME writeEnhancedAttributeValue)*
 *
 * ATTRIBUTE_NAME  := writeEnhancedAttributeIndexed
 *                 |  writeEnhancedStartNameLiteral
 *                 |  writeEnhancedAttribute
 *       
 *
 * CONTENTS      := (ELEMENT | writeEnhancedText writeEnhancedOctets)*
 * </pre>
 * <p>
 * Some methods enhanced will not use the tables for the mapping of information
 * to indexes as provided by the {@link StAXDocumentSerializer}. For such methods
 * it is the responsibility of the application to manage indexes.
 */
final public class EnchancedStAXDocumentSerializer extends StAXDocumentSerializer {
    
    /**
     * Initiate enhanced streaming of an element fragment.
     * <p>
     * This method must be invoked before other enhanced other method.
     */
    public void initiateElementFragment() throws XMLStreamException {
        encodeTerminationAndCurrentElement(false);
    }
        
    /**
     * Get the next index to apply to an Element Information Item.
     * @return the index.
     */
    public int getNextElementIndex() {
        return _v.elementName.getNextIndex();
    }

    /**
     * Get the next index to apply to an Attribute Information Item.
     * @return the index.
     */
    public int getNextAttributeIndex() {
        return _v.attributeName.getNextIndex();
    }
    
    /**
     * Get the next index to apply to an [local name] of an Element or Attribute
     * Information Item.
     * @return the index.
     */
    public int getNextLocalNameIndex() {
        return _v.localName.getNextIndex();
    }

    public void writeEnhancedTerminationAndMark() throws IOException {
        encodeTermination();
        mark();
    }

    public void writeEnhancedStartElementIndexed(int type, int index) throws IOException {
        _b = type;
        encodeNonZeroIntegerOnThirdBit(index);
    }
    
    public void writeEnhancedStartElement(int type, String prefix, String localName,
            String namespaceURI) throws IOException {
        if (!encodeElement(type, namespaceURI, prefix, localName))
            encodeLiteral(type | EncodingConstants.ELEMENT_LITERAL_QNAME_FLAG,
                    namespaceURI, prefix, localName);
    }
                
    public void writeEnhancedStartNamespaces() throws IOException {
        write(EncodingConstants.ELEMENT | EncodingConstants.ELEMENT_NAMESPACES_FLAG);
    }
    
    public void writeEnhancedNamespace(String prefix, String namespaceName) 
        throws IOException {
        encodeNamespaceAttribute(prefix, namespaceName);
    }
    
    public void writeEnhancedEndNamespaces() throws IOException {
        write(EncodingConstants.TERMINATOR);
    }
    
    public void writeEnhancedStartAttributes() throws IOException {
        if (hasMark()) {
            _octetBuffer[_markIndex] |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
            resetMark();
        }
    }
        
    public void writeEnhancedAttributeIndexed(int index) throws IOException {
        encodeNonZeroIntegerOnSecondBitFirstBitZero(index);
    }
    
    public void writeEnhancedAttribute(String prefix, String namespaceURI, String localName) throws IOException {
        if (!encodeAttribute(namespaceURI, prefix, localName))
            encodeLiteral(EncodingConstants.ATTRIBUTE_LITERAL_QNAME_FLAG, 
                    namespaceURI, prefix, localName);
    }
                
    public void writeEnhancedAttributeValue(String value) throws IOException
    {
        final boolean addToTable = (value.length() < attributeValueSizeConstraint) ? true : false;
        encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable);
    }
    
    public void writeEnhancedStartNameLiteral(int type, String prefix, byte[] utf8LocalName, 
            String namespaceURI) throws IOException {
        encodeLiteralHeader(type, namespaceURI, prefix);
        encodeNonZeroOctetStringLengthOnSecondBit(utf8LocalName.length);
        write(utf8LocalName, 0, utf8LocalName.length);
    }
    
    public void writeEnhancedStartNameLiteral(int type, String prefix, int localNameIndex, 
            String namespaceURI) throws IOException {
        encodeLiteralHeader(type, namespaceURI, prefix);
        encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
    }
     
    public void writeEnhancedEndStartElement() throws IOException {
        if (hasMark()) {
            resetMark();
        } else {
            // Terminate the attributes
            _b = EncodingConstants.TERMINATOR;
            _terminate = true;
        }
    }
    
    public void writeEnhancedEndElement() throws IOException {
        encodeElementTermination();
    }
    
    public void writeEnhancedText(char[] text, int length) throws IOException {
        if (length == 0)
            return;
        
        encodeTermination();
        
        encodeCharacters(text, 0, length);
    }
    
    public void writeEnhancedText(String text) throws IOException {
        final int length = text.length();
        if (length == 0)
            return;
        
        if (length < _charBuffer.length) {
            text.getChars(0, length, _charBuffer, 0);
            encodeCharacters(_charBuffer, 0, length);
        } else {
            final char ch[] = text.toCharArray();
            encodeCharactersNoClone(ch, 0, length);
        }
    }
    
    public void writeEnhancedOctets(byte[] octets, int length) throws IOException {
        if (length == 0)
            return;
        
        encodeTermination();
        
        encodeCIIOctetAlgorithmData(EncodingAlgorithmIndexes.BASE64, octets, 0, length);
    }

    
    
    
    
    private boolean encodeElement(int type, String namespaceURI, String prefix, String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = _v.elementName.obtainEntry(localName);
        for (int i = 0; i < entry._valueIndex; i++) {
            final QualifiedName name = entry._value[i];
            if ((prefix == name.prefix || prefix.equals(name.prefix))
                    && (namespaceURI == name.namespaceName || namespaceURI.equals(name.namespaceName))) {
                _b = type;
                encodeNonZeroIntegerOnThirdBit(name.index);
                return true;
            }
        }

        entry.addQualifiedName(new QualifiedName(prefix, namespaceURI, localName, "", _v.elementName.getNextIndex()));
        return false;
    }
        
    private boolean encodeAttribute(String namespaceURI, String prefix, String localName) throws IOException {
        final LocalNameQualifiedNamesMap.Entry entry = _v.attributeName.obtainEntry(localName);
        for (int i = 0; i < entry._valueIndex; i++) {
            final QualifiedName name = entry._value[i];
            if ((prefix == name.prefix || prefix.equals(name.prefix))
                    && (namespaceURI == name.namespaceName || namespaceURI.equals(name.namespaceName))) {
                encodeNonZeroIntegerOnSecondBitFirstBitZero(name.index);
                return true;
            }
        }

        entry.addQualifiedName(new QualifiedName(prefix, namespaceURI, localName, "", _v.attributeName.getNextIndex()));
        return false;
    }

    private void encodeLiteralHeader(int type, String namespaceURI, String prefix) throws IOException {
        if (namespaceURI != "") {
            type |= EncodingConstants.LITERAL_QNAME_NAMESPACE_NAME_FLAG;
            if (prefix != "")
                type |= EncodingConstants.LITERAL_QNAME_PREFIX_FLAG;
                        
            write(type);
            if (prefix != "")
                encodeNonZeroIntegerOnSecondBitFirstBitOne(_v.prefix.get(prefix));
            encodeNonZeroIntegerOnSecondBitFirstBitOne(_v.namespaceName.get(namespaceURI));
        } else
            write(type);
    }
        
    private void encodeLiteral(int type, String namespaceURI, String prefix, String localName) throws IOException {
        encodeLiteralHeader(type, namespaceURI, prefix);
        
        final int localNameIndex = _v.localName.obtainIndex(localName);
        if (localNameIndex == -1) {
            encodeNonEmptyOctetStringOnSecondBit(localName);
        } else
            encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
    }
}