/*
 * FIME (Fast Infoset ME) software ("Software")
 *
 * Copyright, 2005 Sun Microsystems, Inc. All Rights Reserved.
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


package com.sun.xml.fime.stax;


import java.io.IOException;
import java.io.OutputStream;
import java.util.EmptyStackException;
import java.util.Enumeration;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.helpers.NamespaceSupport;

import com.sun.xml.fime.Encoder;
import com.sun.xml.fime.EncodingConstants;
import com.sun.xml.fime.jvnet.EncodingAlgorithmIndexes;
import com.sun.xml.fime.util.MessageCenter;

public class StAXDocumentSerializer extends Encoder implements XMLStreamWriter {
    protected StAXManager _manager;
    
    protected String _encoding;
    /**
     * Local name of current element.
     */
    protected String _currentLocalName;
    
    /**
     * Namespace of current element.
     */
    protected String _currentUri;
    
    /**
     * Prefix of current element.
     */
    protected String _currentPrefix;

   /**
     * This flag indicates when there is a pending start element event.
     */
    protected boolean _inStartElement = false;
    
    /**
     * This flag indicates if the current element is empty.
     */
    protected boolean _isEmptyElement = false;

    /**
     * List of attributes qnames and values defined in the current element.
     */
    protected String[] _attributesArray = new String[4 * 16];
    protected int _attributesArrayIndex = 0;
    
    /**
     * Mapping between uris and prefixes.
     */
    protected NamespaceSupport _nsSupport = new NamespaceSupport();
    
    protected boolean[] _nsSupportContextStack = new boolean[32];
    protected int _stackCount = -1;    
    
    protected NamespaceContext _nsContext = new NamespaceContextImpl();

    /**
     * List of namespaces defined in the current element.
     */
    protected String[] _namespacesArray = new String[2 * 8];
    protected int _namespacesArrayIndex = 0;
    
    public StAXDocumentSerializer() {    
    }
    
    public StAXDocumentSerializer(OutputStream outputStream) {
        setOutputStream(outputStream);
    }

    public StAXDocumentSerializer(OutputStream outputStream, StAXManager manager) {
        setOutputStream(outputStream);
        _manager = manager;
    }
    
    public void reset() {
        super.reset();
        
        _attributesArrayIndex = 0;
        _namespacesArrayIndex = 0;
        _nsSupport.reset();
        _stackCount = -1;
                
        _currentUri = _currentPrefix = null;
        _currentLocalName = null;
        
        _inStartElement = _isEmptyElement = false;
    }
    
    // -- XMLStreamWriter Interface -------------------------------------------
            
    public void writeStartDocument() throws XMLStreamException {
        writeStartDocument("finf", "1.0");
    }
    
    public void writeStartDocument(String version) throws XMLStreamException {
        writeStartDocument("finf", version);
    }
    
    public void writeStartDocument(String encoding, String version)
        throws XMLStreamException
    {
        reset();
        
        try {
            encodeHeader(false);
            encodeInitialVocabulary();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeEndDocument() throws XMLStreamException {
        // Need to flush a pending empty element?
        if (_inStartElement) {
//            encodeTerminationAndCurrentElement();
        }

        try {
            // TODO
            // Use nsSupport to terminate all elements not terminated
            // by writeEndElement
            
            
            encodeDocumentTermination();
        } 
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void close() throws XMLStreamException {
        reset();
    }
    
    public void flush() throws XMLStreamException {
        try {
            _s.flush();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeStartElement(String localName)
        throws XMLStreamException
    {
        // TODO is it necessary for FI to obtain the default namespace in scope?
        writeStartElement("", localName, "");
    }
    
    public void writeStartElement(String namespaceURI, String localName)
        throws XMLStreamException
    {
        writeStartElement(getPrefix(namespaceURI), localName, namespaceURI);
    }
    
    public void writeStartElement(String prefix, String localName,
        String namespaceURI) throws XMLStreamException
    {
        encodeTerminationAndCurrentElement(false);
              
        _inStartElement = true;
        _isEmptyElement = false;

        _currentLocalName = localName;
        _currentPrefix = prefix;
        _currentUri = namespaceURI;

        _stackCount++;
        if (_stackCount == _nsSupportContextStack.length) {
            boolean[] nsSupportContextStack = new boolean[_stackCount * 2];
            System.arraycopy(_nsSupportContextStack, 0, nsSupportContextStack, 0, _nsSupportContextStack.length);
            _nsSupportContextStack = nsSupportContextStack;
        }
        
        _nsSupportContextStack[_stackCount] = false;
        // _nsSupport.pushContext();
    }
    
    public void writeEmptyElement(String localName)
        throws XMLStreamException
    {
        writeEmptyElement("", localName, "");
    }
    
    public void writeEmptyElement(String namespaceURI, String localName)
        throws XMLStreamException
    {
        writeEmptyElement(getPrefix(namespaceURI), localName, namespaceURI);
    }
    
    public void writeEmptyElement(String prefix, String localName, 
        String namespaceURI) throws XMLStreamException
    {
        encodeTerminationAndCurrentElement(false);
        
        _isEmptyElement = _inStartElement = true;
        
        _currentLocalName = localName;
        _currentPrefix = prefix;
        _currentUri = namespaceURI;
        
        _stackCount++;
        if (_stackCount == _nsSupportContextStack.length) {
            boolean[] nsSupportContextStack = new boolean[_stackCount * 2];
            System.arraycopy(_nsSupportContextStack, 0, nsSupportContextStack, 0, _nsSupportContextStack.length);
            _nsSupportContextStack = nsSupportContextStack;
        }
        
        _nsSupportContextStack[_stackCount] = false;
        //_nsSupport.pushContext();
    }
        
    public void writeEndElement() throws XMLStreamException {
        if (_inStartElement) {
            encodeTerminationAndCurrentElement(false);
        }
            
        try {            
            encodeElementTermination();
            if (_nsSupportContextStack[_stackCount--] == true) {
                _nsSupport.popContext();
            }
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
        catch (EmptyStackException e) {
            throw new XMLStreamException(e);
        }
    }

    
    public void writeAttribute(String localName, String value)
        throws XMLStreamException
    {
        writeAttribute("", "", localName, value);
    }
    
    public void writeAttribute(String namespaceURI, String localName,
        String value) throws XMLStreamException
    {
        String prefix = "";
        
        // Find prefix for attribute, ignoring default namespace
        if (namespaceURI.length() > 0) {            
            prefix = _nsSupport.getPrefix(namespaceURI);

            // Undeclared prefix or ignorable default ns?
            if (prefix == null || prefix.length() == 0) {
                // Workaround for BUG in SAX NamespaceSupport helper
                // which incorrectly defines namespace declaration URI
                if (namespaceURI == EncodingConstants.XMLNS_NAMESPACE_NAME || 
                        namespaceURI.equals(EncodingConstants.XMLNS_NAMESPACE_NAME)) {
                    // TODO
                    // Need to check carefully the rule for the writing of
                    // namespaces in StAX. Is it safe to ignore such 
                    // attributes, as declarations will be made using the
                    // writeNamespace method
                    return;
                }
                throw new XMLStreamException(MessageCenter.getString("message.URIUnbound", new Object[]{namespaceURI}));
            }
        }
        writeAttribute(prefix, namespaceURI, localName, value);
    }
        
    public void writeAttribute(String prefix, String namespaceURI,
        String localName, String value) throws XMLStreamException
    {
        if (!_inStartElement) {
            throw new IllegalStateException(MessageCenter.getString("message.attributeWritingNotAllowed"));
        }

        // TODO
        // Need to check carefully the rule for the writing of
        // namespaces in StAX. Is it safe to ignore such 
        // attributes, as declarations will be made using the
        // writeNamespace method
        if (namespaceURI == EncodingConstants.XMLNS_NAMESPACE_NAME || 
                namespaceURI.equals(EncodingConstants.XMLNS_NAMESPACE_NAME)) {
            return;
        }

        if (_attributesArrayIndex == _attributesArray.length) {
            final String[] attributesArray = new String[_attributesArrayIndex * 2];
            System.arraycopy(_attributesArray, 0, attributesArray, 0, _attributesArrayIndex);
            _attributesArray = attributesArray;
        }
        
        _attributesArray[_attributesArrayIndex++] = namespaceURI;
        _attributesArray[_attributesArrayIndex++] = prefix;
        _attributesArray[_attributesArrayIndex++] = localName;
        _attributesArray[_attributesArrayIndex++] = value;
    }
    
    public void writeNamespace(String prefix, String namespaceURI)
        throws XMLStreamException
    {
        if (prefix == null || prefix.length() == 0 || prefix.equals(EncodingConstants.XMLNS_NAMESPACE_PREFIX)) {
            writeDefaultNamespace(namespaceURI);
        }
        else {
            if (!_inStartElement) {
                throw new IllegalStateException(MessageCenter.getString("message.attributeWritingNotAllowed"));
            }
            
            if (_namespacesArrayIndex == _namespacesArray.length) {
                final String[] namespacesArray = new String[_namespacesArrayIndex * 2];
                System.arraycopy(_namespacesArray, 0, namespacesArray, 0, _namespacesArrayIndex);
                _namespacesArray = namespacesArray;
            }
            
            _namespacesArray[_namespacesArrayIndex++] = prefix;
            _namespacesArray[_namespacesArrayIndex++] = namespaceURI;
        }
    }
    
    public void writeDefaultNamespace(String namespaceURI)
        throws XMLStreamException
    {
        if (!_inStartElement) {
            throw new IllegalStateException(MessageCenter.getString("message.attributeWritingNotAllowed"));
        }
        
        if (_namespacesArrayIndex == _namespacesArray.length) {
            final String[] namespacesArray = new String[_namespacesArrayIndex * 2];
            System.arraycopy(_namespacesArray, 0, namespacesArray, 0, _namespacesArrayIndex);
            _namespacesArray = namespacesArray;
        }

        _namespacesArray[_namespacesArrayIndex++] = "";
        _namespacesArray[_namespacesArrayIndex++] = namespaceURI;
    }
    
    public void writeComment(String data) throws XMLStreamException {
        try {
            encodeTerminationAndCurrentElement(true);

            // TODO: avoid array copy here
            encodeComment(data.toCharArray(), 0, data.length());
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeProcessingInstruction(String target)
        throws XMLStreamException
    {
        writeProcessingInstruction(target, "");
    }
    
    public void writeProcessingInstruction(String target, String data)
        throws XMLStreamException
    {
        try {
            encodeTerminationAndCurrentElement(true);

            encodeProcessingInstruction(target, data);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeCData(String data) throws XMLStreamException {
        throw new RuntimeException(MessageCenter.getString("message.notImplemented"));
   }
    
    public void writeDTD(String dtd) throws XMLStreamException {
        throw new RuntimeException(MessageCenter.getString("message.notImplemented"));
    }
    
    public void writeEntityRef(String name) throws XMLStreamException {
        throw new RuntimeException(MessageCenter.getString("message.notImplemented"));
    }
        
    public void writeCharacters(String text) throws XMLStreamException {
         try {
            final int length = text.length();
            if (length == 0) {
                return;
            } else if (length < _charBuffer.length) {
                encodeTerminationAndCurrentElement(true);
                
                text.getChars(0, length, _charBuffer, 0);
                encodeCharacters(_charBuffer, 0, length);
            } else {
                encodeTerminationAndCurrentElement(true);
                
                final char ch[] = text.toCharArray();
                encodeCharactersNoClone(ch, 0, length);
            }
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public void writeCharacters(char[] text, int start, int len)
        throws XMLStreamException
    {
         try {
            if (len == 0) {
                return;
            }

            encodeTerminationAndCurrentElement(true);

            encodeCharacters(text, start, len);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return _nsSupport.getPrefix(uri);
    }
    
    public void setPrefix(String prefix, String uri) 
        throws XMLStreamException 
    {
        if (_stackCount > -1 && _nsSupportContextStack[_stackCount] == false) {
            _nsSupportContextStack[_stackCount] = true;
            _nsSupport.pushContext();
        }
        
        _nsSupport.declarePrefix(prefix, uri);
    }
    
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        setPrefix("", uri);
    }
    
    /**
     * Sets the current namespace context for prefix and uri bindings.
     * This context becomes the root namespace context for writing and
     * will replace the current root namespace context.  Subsequent calls
     * to setPrefix and setDefaultNamespace will bind namespaces using
     * the context passed to the method as the root context for resolving
     * namespaces.  This method may only be called once at the start of
     * the document.  It does not cause the namespaces to be declared.
     * If a namespace URI to prefix mapping is found in the namespace
     * context it is treated as declared and the prefix may be used
     * by the StreamWriter.
     * @param context the namespace context to use for this writer, may not be null
     * @throws XMLStreamException
     */
    public void setNamespaceContext(NamespaceContext context)
        throws XMLStreamException 
    {           
        throw new RuntimeException("setNamespaceContext");
    }
    
    public NamespaceContext getNamespaceContext() {
        return _nsContext;
    }
    
    public Object getProperty(java.lang.String name) 
        throws IllegalArgumentException 
    {
        if (_manager != null) {
            return _manager.getProperty(name);
        }
        return null;
    }

    public void setManager(StAXManager manager) {
        _manager = manager;
    }
    
    public void setEncoding(String encoding) {
        _encoding = encoding;
    }
    
    protected class NamespaceContextImpl implements NamespaceContext {
        public final String getNamespaceURI(String prefix) {
            return _nsSupport.getURI(prefix);
        }
  
        public final String getPrefix(String namespaceURI) {
            return _nsSupport.getPrefix(namespaceURI);
        }

        public final Enumeration getPrefixes(String namespaceURI) {
            final Enumeration e = _nsSupport.getPrefixes(namespaceURI);
            
            return new Enumeration() {
                    public boolean hasMoreElements() {
                        return e.hasMoreElements();
                    }

                    public Object nextElement() {
                        return e.nextElement();
                    }
                };
        }
    }

    public void writeOctets(byte[] b, int start, int len)
        throws XMLStreamException
    {
         try {
            if (len == 0) {
                return;
            }

            encodeTerminationAndCurrentElement(true);

            encodeCIIOctetAlgorithmData(EncodingAlgorithmIndexes.BASE64, b, start, len);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    protected void encodeTerminationAndCurrentElement(boolean terminateAfter) throws XMLStreamException {
        try {
            encodeTermination();
            
            if (_inStartElement) {

                _b = EncodingConstants.ELEMENT;
                if (_attributesArrayIndex > 0) {
                    _b |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
                }

                // Encode namespace decls associated with this element
                if (_namespacesArrayIndex > 0) {
                    write(_b | EncodingConstants.ELEMENT_NAMESPACES_FLAG);
                    for (int i = 0; i < _namespacesArrayIndex;) {
                        encodeNamespaceAttribute(_namespacesArray[i++], _namespacesArray[i++]);
                    }
                    _namespacesArrayIndex = 0;
                    
                    write(EncodingConstants.TERMINATOR);

                    _b = 0;
                }

                // Encode element and its attributes
                encodeElementQualifiedNameOnThirdBit(_currentUri, _currentPrefix, _currentLocalName);

                for (int i = 0; i < _attributesArrayIndex;) {
                    encodeAttributeQualifiedNameOnSecondBit(
                            _attributesArray[i++], _attributesArray[i++], _attributesArray[i++]);

                    final String value = _attributesArray[i];
                    _attributesArray[i++] = null;
                    final boolean addToTable = (value.length() < _v.attributeValueSizeConstraint) ? true : false;
                    encodeNonIdentifyingStringOnFirstBit(value, _v.attributeValue, addToTable);
                    
                    _b = EncodingConstants.TERMINATOR;
                    _terminate = true;
                }
                _attributesArrayIndex = 0;
                _inStartElement = false;

                if (_isEmptyElement) {
                    encodeElementTermination();
                    if (_nsSupportContextStack[_stackCount--] == true) {
                        _nsSupport.popContext();
                    }
                    
                    _isEmptyElement = false;
                }
                
                if (terminateAfter) {
                    encodeTermination();
                }
            }
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }    
}