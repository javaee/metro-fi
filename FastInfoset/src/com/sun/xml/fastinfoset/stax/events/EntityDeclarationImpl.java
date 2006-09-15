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

import javax.xml.stream.events.EntityDeclaration;


public class EntityDeclarationImpl extends EventBase implements EntityDeclaration {
    private String _publicId;
    private String _systemId;
    private String _baseURI;
    private String _entityName;
    private String _replacement;
    private String _notationName;
    
    /** Creates a new instance of EntityDeclarationImpl */
    public EntityDeclarationImpl() {
        init();
    }
    
    public EntityDeclarationImpl(String entityName , String replacement){
        init();
        _entityName = entityName;
        _replacement = replacement;
    }
    
    /**
    * The entity's public identifier, or null if none was given
    * @return the public ID for this declaration or null
    */    
    public String getPublicId(){
        return _publicId;
    }

    /**
    * The entity's system identifier.
    * @return the system ID for this declaration or null
    */
    public String getSystemId(){
        return _systemId;
    }    

    /**
    * The entity's name
    * @return the name, may not be null
    */
    public String getName(){
        return _entityName;
    }
    
    /**
    * The name of the associated notation.
    * @return the notation name
    */
    public String getNotationName() {
        return _notationName;
    }

    /**
    * The replacement text of the entity.
    * This method will only return non-null
    * if this is an internal entity.
    * @return null or the replacment text
    */
    public String getReplacementText() {
        return _replacement;
    }

    /**
    * Get the base URI for this reference
    * or null if this information is not available
    * @return the base URI or null
    */
    public String getBaseURI() {
        return _baseURI;
    }

    public void setPublicId(String publicId) {
        _publicId = publicId;
    }
    
    public void setSystemId(String systemId) {
        _systemId = systemId;
    }
    
    public void setBaseURI(String baseURI) {
        _baseURI = baseURI;
    }
    
    public void setName(String entityName){
        _entityName = entityName;
    }    
    
    public void setReplacementText(String replacement){
        _replacement = replacement;
    }
        
    public void setNotationName(String notationName){
        _notationName = notationName;
    }
        
    protected void init(){
        setEventType(ENTITY_DECLARATION);
    }
}
