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

import java.util.List;

import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;

/**
 * DTDEvent. Notations and Entities are not used
 */
public class DTDEvent extends EventBase implements DTD{
    
    private String _dtd;
    private List _notations;
    private List _entities;
    
    /** Creates a new instance of DTDEvent */
    public DTDEvent() {
        setEventType(DTD);
    }
    
    public DTDEvent(String dtd){
        setEventType(DTD);
        _dtd = dtd;
    }
    
   /**
   * Returns the entire Document Type Declaration as a string, including
   * the internal DTD subset.
   * This may be null if there is not an internal subset.
   * If it is not null it must return the entire 
   * Document Type Declaration which matches the doctypedecl
   * production in the XML 1.0 specification
   */
   public String getDocumentTypeDeclaration() {
        return _dtd;
    }
   public void setDTD(String dtd){
        _dtd = dtd;
    }
   
   /**
   * Return a List containing the general entities, 
   * both external and internal, declared in the DTD.
   * This list must contain EntityDeclaration events.
   * @see EntityDeclaration
   * @return an unordered list of EntityDeclaration events
   */
    public List getEntities() {
        return _entities;
    }
        
   /**
   * Return a List containing the notations declared in the DTD.
   * This list must contain NotationDeclaration events. 
   * @see NotationDeclaration
   * @return an unordered list of NotationDeclaration events
   */
   public List getNotations() {
        return _notations;
    }
    
    /**
     *Returns an implementation defined representation of the DTD.
     * This method may return null if no representation is available.
     *
     */
    public Object getProcessedDTD() {
        return null;
    }

    public void setEntities(List entites){
        _entities = entites;
    }
        
    public void setNotations(List notations){
        _notations = notations;
    }
    
    public String toString(){
        return _dtd ;
    }
}
