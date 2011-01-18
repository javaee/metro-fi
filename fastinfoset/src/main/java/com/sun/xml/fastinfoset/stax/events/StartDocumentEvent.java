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

package com.sun.xml.fastinfoset.stax.events ;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.StartDocument;

import com.sun.xml.fastinfoset.stax.events.XMLConstants;

public class StartDocumentEvent extends EventBase implements StartDocument {
    
    protected String _systemId;
    protected String _encoding = XMLConstants.ENCODING; //default
    protected boolean _standalone = true;
    protected String _version = XMLConstants.XMLVERSION;
    private boolean _encodingSet = false;
    private boolean _standaloneSet = false;
    
    public void reset() {
        _encoding = XMLConstants.ENCODING;
        _standalone = true;
        _version = XMLConstants.XMLVERSION;
        _encodingSet = false;
        _standaloneSet=false;
    }
    public StartDocumentEvent() {
        this(null ,null);
    }
    
    public StartDocumentEvent(String encoding){
        this(encoding, null);
    }
    
    public StartDocumentEvent(String encoding, String version){
        if (encoding != null) {
            _encoding = encoding;
            _encodingSet = true;
        }
        if (version != null)
            _version = version;
        setEventType(XMLStreamConstants.START_DOCUMENT);
    }
    
    
    // ------------------- methods defined in StartDocument -------------------------
    /**
    * Returns the system ID of the XML data
    * @return the system ID, defaults to ""
    */
    public String getSystemId() {
        return super.getSystemId();
    }
    
    /**
    * Returns the encoding style of the XML data
    * @return the character encoding, defaults to "UTF-8"
    */
    public String getCharacterEncodingScheme() {
        return _encoding;
    }
    /**
    * Returns true if CharacterEncodingScheme was set in 
    * the encoding declaration of the document
    */
    public boolean encodingSet() {
        return _encodingSet;
    }
    

  /**
   * Returns if this XML is standalone
   * @return the standalone state of XML, defaults to "no"
   */
    public boolean isStandalone() {
        return _standalone;
    }
    /**
    * Returns true if the standalone attribute was set in 
    * the encoding declaration of the document.
    */
    public boolean standaloneSet() {
        return _standaloneSet;
    }
    
  /**
   * Returns the version of XML of this XML stream
   * @return the version of XML, defaults to "1.0"
   */
    public String getVersion() {
        return _version;
    }
    // ------------------- end of methods defined in StartDocument -------------------------
    
    public void setStandalone(boolean standalone) {
        _standaloneSet = true;
        _standalone = standalone;
    }
    
    public void setStandalone(String s) {
        _standaloneSet = true;
        if(s == null) {
            _standalone = true;
            return;
        }
        if(s.equals("yes"))
            _standalone = true;
        else
            _standalone = false;
    }
    
        
    public void setEncoding(String encoding) {
        _encoding = encoding;
        _encodingSet = true;
    }
    
    void setDeclaredEncoding(boolean value){
        _encodingSet = value;
    }
    
    public void setVersion(String s) {
        _version = s;
    }
    
    void clear() {
        _encoding = "UTF-8";
        _standalone = true;
        _version = "1.0";
        _encodingSet = false;
        _standaloneSet = false;
    }
    
    public String toString() {
        String s = "<?xml version=\"" + _version + "\"";
        s = s + " encoding='" + _encoding + "'";
        if(_standaloneSet) {
            if(_standalone)
                s = s + " standalone='yes'?>";
            else
                s = s + " standalone='no'?>";
        } else {
            s = s + "?>";
        }
        return s;
    }
    
    public boolean isStartDocument() {
        return true;
    }
}
