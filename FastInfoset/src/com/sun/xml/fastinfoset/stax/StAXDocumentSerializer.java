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

import com.sun.xml.fastinfoset.Encoder;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.util.QualifiedNameArray;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.helpers.NamespaceSupport;

public class StAXDocumentSerializer extends Encoder implements XMLStreamWriter {
    
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
    protected ArrayList _attributes = new ArrayList();

    /**
     * Mapping between uris and prefixes.
     */
    protected NamespaceSupport _nsSupport = new NamespaceSupport();
    
    /**
     * List of namespaces defined in the current element.
     */
    protected QualifiedNameArray _namespaces = new QualifiedNameArray(4);
    
    
    public StAXDocumentSerializer() {
    }

    public void setOutputStream(OutputStream outputStream) {
        super.setOutputStream(outputStream);
        reset();
    }

    public void reset() {        
        _attributes.clear();
        _namespaces.clear();
        _nsSupport.reset();
        
        try {
            setPrefix("xml", "http://www.w3.org/XML/1998/namespace");
        }
        catch (XMLStreamException e) {
            // falls through
        }
        
        _currentUri = _currentPrefix = null;
        _currentLocalName = null;    
    }
    
    // -- XMLStreamWriter Interface -------------------------------------------
            
    /**
     * Write the XML Declaration. Defaults the XML version to 1.0, and the encoding to utf-8
     * @throws XMLStreamException
     */
    public void writeStartDocument() throws XMLStreamException {
        writeStartDocument("finf", "1.0");
    }
    
    /**
     * Write the XML Declaration. Defaults the XML version to 1.0
     * @param version version of the xml document
     * @throws XMLStreamException
     */
    public void writeStartDocument(String version) throws XMLStreamException {
        writeStartDocument("finf", version);
    }
    
    /**
     * Write the XML Declaration.  Note that the encoding parameter does
     * not set the actual encoding of the underlying output.  That must
     * be set when the instance of the XMLStreamWriter is created using the
     * XMLOutputFactory
     * @param encoding encoding of the xml declaration
     * @param version version of the xml document
     * @throws XMLStreamException
     */
    public void writeStartDocument(String encoding, String version)
        throws XMLStreamException
    {
        try {
            encodeHeader(false);
            encodeInitialVocabulary();
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    /**
     * Closes any start tags and writes corresponding end tags.
     * @throws XMLStreamException
     */
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
    
    /**
     * Close this writer and free any resources associated with the
     * writer.  This must not close the underlying output stream.
     * @throws XMLStreamException
     */
    public void close() throws XMLStreamException {
        reset();
    }
    
    /**
     * Write any cached data to the underlying output mechanism.
     * @throws XMLStreamException
     */
    public void flush() throws XMLStreamException {
        try {
            _s.flush();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    /**
     * Writes a start tag to the output.  All writeStartElement methods
     * open a new scope in the internal namespace context.  Writing the
     * corresponding EndElement causes the scope to be closed.
     * @param localName local name of the tag, may not be null
     * @throws XMLStreamException
     */
    public void writeStartElement(String localName)
        throws XMLStreamException
    {
        // TODO is it necessary for FI to obtain the default namespace in scope?
        writeStartElement("", localName, "");
    }
    
    /**
     * Writes a start tag to the output
     * @param namespaceURI the namespaceURI of the prefix to use, may not be null
     * @param localName local name of the tag, may not be null
     * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
     * javax.xml.stream.isPrefixDefaulting has not been set to true
     */
    public void writeStartElement(String namespaceURI, String localName)
        throws XMLStreamException
    {
        writeStartElement(getPrefix(namespaceURI), localName, namespaceURI);
    }
    
    /**
     * Writes a start tag to the output
     * @param localName local name of the tag, may not be null
     * @param prefix the prefix of the tag, may not be null
     * @param namespaceURI the uri to bind the prefix to, may not be null
     * @throws XMLStreamException
     */
    public void writeStartElement(String prefix, String localName,
        String namespaceURI) throws XMLStreamException
    {
        encodeTerminationAndCurrentElement(false);
              
        _inStartElement = true;
        _isEmptyElement = false;

        _currentLocalName = localName;
        _currentPrefix = prefix;
        _currentUri = namespaceURI;
        
        _nsSupport.pushContext();
    }
    
    /**
     * Writes an empty element tag to the output
     * @param localName local name of the tag, may not be null
     * @throws XMLStreamException
     */
    public void writeEmptyElement(String localName)
        throws XMLStreamException
    {
        writeEmptyElement("", localName, "");
    }
    
    /**
     * Writes an empty element tag to the output
     * @param namespaceURI the uri to bind the tag to, may not be null
     * @param localName local name of the tag, may not be null
     * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
     * javax.xml.stream.isPrefixDefaulting has not been set to true
     */
    public void writeEmptyElement(String namespaceURI, String localName)
        throws XMLStreamException
    {
        writeEmptyElement(getPrefix(namespaceURI), localName, namespaceURI);
    }
    
    /**
     * Writes an empty element tag to the output
     * @param prefix the prefix of the tag, may not be null
     * @param localName local name of the tag, may not be null
     * @param namespaceURI the uri to bind the tag to, may not be null
     * @throws XMLStreamException
     */
    public void writeEmptyElement(String prefix, String localName, 
        String namespaceURI) throws XMLStreamException
    {
        encodeTerminationAndCurrentElement(false);
        
        _isEmptyElement = _inStartElement = true;
        
        _currentLocalName = localName;
        _currentPrefix = prefix;
        _currentUri = namespaceURI;
        
        _nsSupport.pushContext();
    }
        
    /**
     * Writes an end tag to the output relying on the internal
     * state of the writer to determine the prefix and local name
     * of the event.
     * @throws XMLStreamException
     */
    public void writeEndElement() throws XMLStreamException {
        if (_inStartElement) {
            encodeTerminationAndCurrentElement(false);
        }
            
        try {            
            encodeElementTermination();
            _nsSupport.popContext();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
        catch (EmptyStackException e) {
            throw new XMLStreamException(e);
        }
    }

    
    /**
     * Writes an attribute to the output stream without
     * a prefix.
     * @param localName the local name of the attribute
     * @param value the value of the attribute
     * @throws IllegalStateException if the current state does not allow Attribute writing
     * @throws XMLStreamException
     */
    public void writeAttribute(String localName, String value)
        throws XMLStreamException
    {
        writeAttribute("", "", localName, value);
    }
    
    /**
     * Writes an attribute to the output stream
     * @param namespaceURI the uri of the prefix for this attribute
     * @param localName the local name of the attribute
     * @param value the value of the attribute
     * @throws IllegalStateException if the current state does not allow Attribute writing
     * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
     * javax.xml.stream.isPrefixDefaulting has not been set to true
     */
    public void writeAttribute(String namespaceURI, String localName,
        String value) throws XMLStreamException
    {
        String prefix = "";
        
        // Find prefix for attribute, ignoring default namespace
        if (namespaceURI.length() > 0) {
            prefix = _nsSupport.getPrefix(namespaceURI);

            // Undeclared prefix or ignorable default ns?
            if (prefix == null || prefix.length() == 0) {
                throw new XMLStreamException("URI '" + namespaceURI 
                    + "' is unbound for this attribute");
            }
        }
        writeAttribute(prefix, namespaceURI, localName, value);
    }
    
    /**
     * Writes an attribute to the output stream
     * @param prefix the prefix for this attribute
     * @param namespaceURI the uri of the prefix for this attribute
     * @param localName the local name of the attribute
     * @param value the value of the attribute
     * @throws IllegalStateException if the current state does not allow Attribute writing
     * @throws XMLStreamException if the namespace URI has not been bound to a prefix and
     * javax.xml.stream.isPrefixDefaulting has not been set to true
     */
    
    public void writeAttribute(String prefix, String namespaceURI,
        String localName, String value) throws XMLStreamException
    {
        if (!_inStartElement) {
            throw new IllegalStateException("Current state does not allow attribute writing");
        }

        _attributes.add(new QualifiedName(prefix, namespaceURI, localName, ""));
        _attributes.add(value);
    }
    
    /**
     * Writes a namespace to the output stream
     * If the prefix argument to this method is the empty string,
     * "xmlns", or null this method will delegate to writeDefaultNamespace
     *
     * @param prefix the prefix to bind this namespace to
     * @param namespaceURI the uri to bind the prefix to
     * @throws IllegalStateException if the current state does not allow Namespace writing
     * @throws XMLStreamException
     */
    public void writeNamespace(String prefix, String namespaceURI)
        throws XMLStreamException
    {
        if (prefix == null || prefix.length() == 0 || prefix.equals("xmlns")) {
            writeDefaultNamespace(namespaceURI);
        }
        else {
            if (!_inStartElement) {
                throw new IllegalStateException("Current state does not allow attribute writing");
            }           
            _namespaces.add(new QualifiedName(prefix, namespaceURI));
        }
    }
    
    /**
     * Writes the default namespace to the stream
     * @param namespaceURI the uri to bind the default namespace to
     * @throws IllegalStateException if the current state does not allow Namespace writing
     * @throws XMLStreamException
     */
    public void writeDefaultNamespace(String namespaceURI)
        throws XMLStreamException
    {
        if (!_inStartElement) {
            throw new IllegalStateException("Current state does not allow attribute writing");
        }
        _namespaces.add(new QualifiedName("", namespaceURI));
    }
    
    /**
     * Writes an xml comment with the data enclosed
     * @param data the data contained in the comment, may be null
     * @throws XMLStreamException
     */
    public void writeComment(String data) throws XMLStreamException {
        try {
            if (data.length() == 0) {
                return;
            }

            encodeTerminationAndCurrentElement(true);

            // TODO: avoid array copy here
            encodeComment(data.toCharArray(), 0, data.length());
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    /**
     * Writes a processing instruction
     * @param target the target of the processing instruction, may not be null
     * @throws XMLStreamException
     */
    public void writeProcessingInstruction(String target)
        throws XMLStreamException
    {
        writeProcessingInstruction(target, "");
    }
    
    /**
     * Writes a processing instruction
     * @param target the target of the processing instruction, may not be null
     * @param data the data contained in the processing instruction, may not be null
     * @throws XMLStreamException
     */
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
    
    /**
     * Writes a CData section
     * @param data the data contained in the CData Section, may not be null
     * @throws XMLStreamException
     */
    public void writeCData(String data) throws XMLStreamException {
        throw new UnsupportedOperationException("Not implemented");
   }
    
    /**
     * Write a DTD section.  This string represents the entire doctypedecl production
     * from the XML 1.0 specification.
     *
     * @param dtd the DTD to be written
     * @throws XMLStreamException
     */
    public void writeDTD(String dtd) throws XMLStreamException {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    /**
     * Writes an entity reference
     * @param name the name of the entity
     * @throws XMLStreamException
     */
    public void writeEntityRef(String name) throws XMLStreamException {
        throw new UnsupportedOperationException("Not implemented");
    }
        
    /**
     * Write text to the output
     * @param text the value to write
     * @throws XMLStreamException
     */
    public void writeCharacters(String text) throws XMLStreamException {
         try {
            if (text.length() == 0) {
                return;
            }
            
            encodeTerminationAndCurrentElement(true);

            encodeCharactersNoClone(text.toCharArray(), 0, text.length());
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    /**
     * Write text to the output
     * @param text the value to write
     * @param start the starting position in the array
     * @param len the number of characters to write
     * @throws XMLStreamException
     */
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

    /**
     * Gets the prefix the uri is bound to
     * @return the prefix or null
     * @throws XMLStreamException
     */
    public String getPrefix(String uri) throws XMLStreamException {
        return _nsSupport.getPrefix(uri);
    }
    
    /**
     * Sets the prefix the uri is bound to.  This prefix is bound
     * in the scope of the current START_ELEMENT / END_ELEMENT pair.
     * If this method is called before a START_ELEMENT has been written
     * the prefix is bound in the root scope.
     * @param prefix the prefix to bind to the uri, may not be null
     * @param uri the uri to bind to the prefix, may be null
     * @throws XMLStreamException
     */
    public void setPrefix(String prefix, String uri) 
        throws XMLStreamException 
    {
        _nsSupport.declarePrefix(prefix, uri);
    }
    
    /**
     * Binds a URI to the default namespace
     * This URI is bound
     * in the scope of the current START_ELEMENT / END_ELEMENT pair.
     * If this method is called before a START_ELEMENT has been written
     * the uri is bound in the root scope.
     * @param uri the uri to bind to the default namespace, may be null
     * @throws XMLStreamException
     */
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
        throw new UnsupportedOperationException("Not implemented");
    }
    
    /**
     * Returns the current namespace context.
     * @return the current NamespaceContext
     */
    public NamespaceContext getNamespaceContext() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    /**
     * Get the value of a feature/property from the underlying implementation
     * @param name The name of the property, may not be null
     * @return The value of the property
     * @throws IllegalArgumentException if the property is not supported
     * @throws NullPointerException if the name is null
     */
    public Object getProperty(java.lang.String name) 
        throws IllegalArgumentException 
    {
        throw new UnsupportedOperationException("Not implemented");
    }


    private void encodeTerminationAndCurrentElement(boolean terminateAfter) throws XMLStreamException {
        try {
            encodeTermination();
            
            if (_inStartElement) {

                _b = EncodingConstants.ELEMENT;
                if (_attributes.size() > 0) {
                    _b |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
                }

                // Encode namespace decls associated with this element
                if (_namespaces.size() > 0) {
                    _s.write(_b | EncodingConstants.ELEMENT_NAMESPACES_FLAG);
                    for (int i = 0; i < _namespaces.size(); i++) {
                        QualifiedName name = _namespaces.get(i);
                        encodeNamespaceAttribute(name.prefix, name.namespaceName);
                    }
                    _s.write(EncodingConstants.TERMINATOR);

                    _namespaces.clear();

                    _b = 0;
                }

                // Encode element and its attributes
                encodeElementQualifiedNameOnThirdBit(_currentUri, _currentPrefix, _currentLocalName);

                for (int i = 0; i < _attributes.size();) {
                    QualifiedName name = (QualifiedName)_attributes.get(i++);
                    String value = (String)_attributes.get(i++);
                    encodeAttributeQualifiedNameAndValueOnSecondBit(name.namespaceName, name.prefix, name.localName, value);
                    _b = EncodingConstants.TERMINATOR;
                    _terminate = true;
                }
                _attributes.clear();
                _inStartElement = false;

                if (terminateAfter) {
                    encodeTermination();
                }
            }
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    private void encodeAndPushCurrentElement() 
        throws XMLStreamException 
    {
        try {
            encodeTermination();
            
            _b = EncodingConstants.ELEMENT;
            if (_attributes.size() > 0) {
                _b |= EncodingConstants.ELEMENT_ATTRIBUTE_FLAG;
            }
            
            // Encode namespace decls associated with this element
            if (_namespaces.size() > 0) {
                _s.write(_b | EncodingConstants.ELEMENT_NAMESPACES_FLAG);
                for (int i = 0; i < _namespaces.size(); i++) {
                    QualifiedName name = _namespaces.get(i);
                    encodeNamespaceAttribute(name.prefix, name.namespaceName);
                }
                _namespaces.clear();

                _b = 0;
            }
                        
            // Encode element and its attributes
            encodeElementQualifiedNameOnThirdBit(_currentUri, _currentPrefix, _currentLocalName);

            for (int i = 0; i < _attributes.size();) {
                QualifiedName name = (QualifiedName)_attributes.get(i++);
                String value = (String)_attributes.get(i++);
                encodeAttributeQualifiedNameAndValueOnSecondBit(name.namespaceName, name.prefix, name.localName, value);
                _b = EncodingConstants.TERMINATOR;
                _terminate = true;
            }
            _attributes.clear();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
}



