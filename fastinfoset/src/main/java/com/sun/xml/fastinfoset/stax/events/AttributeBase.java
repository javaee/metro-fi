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

package com.sun.xml.fastinfoset.stax.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

import com.sun.xml.fastinfoset.stax.events.Util;


public class AttributeBase extends EventBase implements Attribute

{
    //an Attribute consists of a qualified name and value
    private QName _QName;
    private String _value;
    
    private String _attributeType = null;
    //A flag indicating whether this attribute was actually specified in the start-tag
    //of its element or was defaulted from the schema.
    private boolean _specified = false;
    
    public AttributeBase(){
        super(ATTRIBUTE);
    }

    public AttributeBase(String name, String value) {
        super(ATTRIBUTE);
        _QName = new QName(name);
        _value = value;
    }

    public AttributeBase(QName qname, String value) {
        _QName = qname;
        _value = value;
    }

    public AttributeBase(String prefix, String localName, String value) {
        this(prefix, null,localName, value, null);
    }

    public AttributeBase(String prefix, String namespaceURI, String localName,
                        String value, String attributeType) {
        if (prefix == null) prefix = "";
        _QName = new QName(namespaceURI, localName,prefix);
        _value = value;
        _attributeType = (attributeType == null) ? "CDATA":attributeType;
    }
    

    public void setName(QName name){
        _QName = name ;
    }
    
  /**
   * Returns the QName for this attribute
   */
    public QName getName() {
        return _QName;
    }
    
    public void setValue(String value){
        _value = value;
    }
    
    public String getLocalName() {
        return _QName.getLocalPart();
    }
  /**
   * Gets the normalized value of this attribute
   */
    public String getValue() {
        return _value;
    }
    
    public void setAttributeType(String attributeType){
        _attributeType = attributeType ;
    }

    /**
   * Gets the type of this attribute, default is 
   * the String "CDATA"
   * @return the type as a String, default is "CDATA"
   */
    public String getDTDType() {
        return _attributeType;
    }
    
    
  /**
   * A flag indicating whether this attribute was actually 
   * specified in the start-tag of its element, or was defaulted from the schema. 
   * @return returns true if this was specified in the start element
   */
    public boolean isSpecified() {
        return _specified ;
    }

    public void setSpecified(boolean isSpecified){
        _specified = isSpecified ;
    }
   
    
    public String toString() {
        String prefix = _QName.getPrefix();
        if (!Util.isEmptyString(prefix))
            return prefix + ":" + _QName.getLocalPart() + "='" + _value + "'";
        
        return _QName.getLocalPart() + "='" + _value + "'";
    }
    
    
}

