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

import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.util.KeyIntMap;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.StringIntMap;
import java.io.IOException;
import java.util.HashMap;
import org.xml.sax.SAXException;
import java.util.Map;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.RestrictedAlphabet;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.xml.sax.Attributes;

/**
 * The Fast Infoset SAX serializer that maps prefixes to user specified prefixes
 * that are specified in a namespace URI to prefix map.
 * <p>
 * This serializer will not preserve the original prefixes and this serializer
 * should not be used when prefixes need to be preserved, such as the case
 * when there are qualified names in content.
 * <p>
 * A namespace URI to prefix map is utilized such that the prefixes
 * in the map are utilized rather than the prefixes specified in
 * the qualified name for elements and attributes.
 * <p>
 * Any namespace declarations with a namespace URI that is not present in
 * the map are added.
 * <p>
 */
public class SAXDocumentSerializerWithPrefixMapping extends SAXDocumentSerializer {
    protected Map _namespaceToPrefixMapping;
    protected Map _prefixToPrefixMapping;
    protected String _lastCheckedNamespace;
    protected String _lastCheckedPrefix;
    
    protected StringIntMap _declaredNamespaces;
    
    public SAXDocumentSerializerWithPrefixMapping(Map namespaceToPrefixMapping) {
        // Use the local name to look up elements/attributes
        super(true);
        _namespaceToPrefixMapping = new HashMap(namespaceToPrefixMapping);
        _prefixToPrefixMapping = new HashMap();
        
        // Empty prefix
        _namespaceToPrefixMapping.put("", "");
        // 'xml' prefix
        _namespaceToPrefixMapping.put(EncodingConstants.XML_NAMESPACE_NAME, EncodingConstants.XML_NAMESPACE_PREFIX);
        
        _declaredNamespaces = new StringIntMap(4);
    }
    
    public final void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            if (_elementHasNamespaces == false) {
                encodeTermination();

                // Mark the current buffer position to flag attributes if necessary
                mark();
                _elementHasNamespaces = true;

                // Write out Element byte with namespaces
                write(EncodingConstants.ELEMENT | EncodingConstants.ELEMENT_NAMESPACES_FLAG);
            
                _declaredNamespaces.clear();
                _declaredNamespaces.obtainIndex(uri);
            } else {
                if (_declaredNamespaces.obtainIndex(uri) != KeyIntMap.NOT_PRESENT) {
                    final String p = getPrefix(uri);
                    if (p != null) {
                        _prefixToPrefixMapping.put(prefix, p);
                    }
                    return;   
                }
            }
            
            final String p = getPrefix(uri);
            if (p != null) {
                encodeNamespaceAttribute(p, uri);
                _prefixToPrefixMapping.put(prefix, p);
            } else {
                putPrefix(uri, prefix);
                encodeNamespaceAttribute(prefix, uri);
            }
            
        } catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }
    
    protected final void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.elementName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            if (encodeElementMapEntry(entry, namespaceURI)) return;
            // Check the entry is a member of the read only map
            if (_v.elementName.isQNameFromReadOnlyMap(entry._value[0])) {
                entry = _v.elementName.obtainDynamicEntry(localName);
                if (entry._valueIndex > 0) {
                    if (encodeElementMapEntry(entry, namespaceURI)) return;
                }
            }
        }

        encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, getPrefix(namespaceURI),
                localName, entry);            
    }
    
    protected boolean encodeElementMapEntry(LocalNameQualifiedNamesMap.Entry entry, String namespaceURI) throws IOException {
        QualifiedName[] names = entry._value;
        for (int i = 0; i < entry._valueIndex; i++) {
            if ((namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                encodeNonZeroIntegerOnThirdBit(names[i].index);
                return true;
            }
        }
        return false;
    }
    
    
    protected final void encodeAttributes(Attributes atts) throws IOException, FastInfosetException {
        boolean addToTable;
        String value;
        if (atts instanceof EncodingAlgorithmAttributes) {
            final EncodingAlgorithmAttributes eAtts = (EncodingAlgorithmAttributes)atts;
            Object data;
            String alphabet;
            for (int i = 0; i < eAtts.getLength(); i++) {
                final String uri = atts.getURI(i);
                if (encodeAttribute(uri, atts.getQName(i), atts.getLocalName(i))) {
                    data = eAtts.getAlgorithmData(i);
                    // If data is null then there is no algorithm data
                    if (data == null) {
                        value = eAtts.getValue(i);
                        addToTable = eAtts.getToIndex(i) || isAttributeValueLengthMatchesLimit(value.length());

                        alphabet = eAtts.getAlpababet(i);
                        if (alphabet == null) {
                            if (uri == "http://www.w3.org/2001/XMLSchema-instance" || 
                                    uri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                                value = convertQName(value);
                            }
                            encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable);
                        } else if (alphabet == RestrictedAlphabet.DATE_TIME_CHARACTERS) 
                            encodeNonIdentifyingStringOnFirstBit(
                                    RestrictedAlphabet.DATE_TIME_CHARACTERS_INDEX, 
                                    DATE_TIME_CHARACTERS_TABLE,
                                    value, addToTable);
                        else if (alphabet == RestrictedAlphabet.DATE_TIME_CHARACTERS) 
                            encodeNonIdentifyingStringOnFirstBit(
                                    RestrictedAlphabet.NUMERIC_CHARACTERS_INDEX, 
                                    NUMERIC_CHARACTERS_TABLE,
                                    value, addToTable);
                        else
                            encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable);

                    } else {
                        encodeNonIdentifyingStringOnFirstBit(eAtts.getAlgorithmURI(i),
                                eAtts.getAlgorithmIndex(i), data);
                    }
                }
            }
        } else {
            for (int i = 0; i < atts.getLength(); i++) {
                final String uri = atts.getURI(i);
                if (encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) {
                    value = atts.getValue(i);
                    addToTable = isAttributeValueLengthMatchesLimit(value.length());
                    
                    if (uri == "http://www.w3.org/2001/XMLSchema-instance" || 
                            uri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
                        value = convertQName(value);
                    }
                    encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable);
                }
            }
        }
        _b = EncodingConstants.TERMINATOR;
        _terminate = true;
    }
    
    private String convertQName(String qName) {
        int i = qName.indexOf(':');
        String prefix = "";
        String localName = qName;
        if (i != -1) {
            prefix = qName.substring(0, i);
            localName = qName.substring(i + 1);
        }
        
        String p = (String)_prefixToPrefixMapping.get(prefix);
        if (p != null) {
            if (p.length() == 0)
                return localName;
            else
                return p + ":" + localName;
        } else {
            return qName;
        }
    }
    
    protected final boolean encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = _v.attributeName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            if (encodeAttributeMapEntry(entry, namespaceURI)) return true;
            // Check the entry is a member of the read only map
            if (_v.attributeName.isQNameFromReadOnlyMap(entry._value[0])) {
                entry = _v.attributeName.obtainDynamicEntry(localName);
                if (entry._valueIndex > 0) {
                    if (encodeAttributeMapEntry(entry, namespaceURI)) return true;
                }
            }
        }

        return encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, getPrefix(namespaceURI),
                localName, entry);            
    }

    protected boolean encodeAttributeMapEntry(LocalNameQualifiedNamesMap.Entry entry, String namespaceURI) throws IOException {        
        QualifiedName[] names = entry._value;
        for (int i = 0; i < entry._valueIndex; i++) {
            if ((namespaceURI == names[i].namespaceName || namespaceURI.equals(names[i].namespaceName))) {
                encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                return true;
            }
        }
        return false;
    }
            
    protected final String getPrefix(String namespaceURI) {
        if (_lastCheckedNamespace == namespaceURI) return _lastCheckedPrefix;
        
        _lastCheckedNamespace = namespaceURI;
        return _lastCheckedPrefix = (String)_namespaceToPrefixMapping.get(namespaceURI);
    }
    
    protected final void putPrefix(String namespaceURI, String prefix) {
        _namespaceToPrefixMapping.put(namespaceURI, prefix);
        
        _lastCheckedNamespace = namespaceURI;
        _lastCheckedPrefix = prefix;
    }
}
