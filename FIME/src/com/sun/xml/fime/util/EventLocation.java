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

package com.sun.xml.fime.util;

import javax.xml.stream.Location;


public class EventLocation implements Location{
    String _systemId = null;
    String _publicId = null;
    int _column = -1;
    int _line = -1;
    int _charOffset = -1;
    
    EventLocation() {
    }
    
    //explicitly create a nil location
    public static Location getNilLocation() {
        return new EventLocation();
    }
    /**
    * Return the line number where the current event ends,
    * returns -1 if none is available.
    * @return the current line number
    */
    public int getLineNumber(){
        return _line;
    }
    /**
    * Return the column number where the current event ends,
    * returns -1 if none is available.
    * @return the current column number
    */
    public int getColumnNumber() {
        return _column;
    }
    
  /**
   * Return the byte or character offset into the input source this location
   * is pointing to. If the input source is a file or a byte stream then 
   * this is the byte offset into that stream, but if the input source is 
   * a character media then the offset is the character offset. 
   * Returns -1 if there is no offset available.
   * @return the current offset
   */
    public int getCharacterOffset(){
        return _charOffset;
    }
    
    /**
    * Returns the public ID of the XML
    * @return the public ID, or null if not available
    */
    public String getPublicId(){
        return _publicId;
    }
    
  /**
   * Returns the system ID of the XML
   * @return the system ID, or null if not available
   */
    public String getSystemId(){
        return _systemId;
    }
    
    public void setLineNumber(int line) {
        _line = line;
    }
    public void setColumnNumber(int col) {
        _column = col;
    }
    public void setCharacterOffset(int offset) {
        _charOffset = offset;
    }
    public void setPublicId(String id) {
        _publicId = id;
    }
    public void setSystemId(String id) {
        _systemId = id;
    }
    
    public String toString(){
        StringBuffer sbuffer = new StringBuffer() ;
        sbuffer.append("Line number = " + _line);
        sbuffer.append("\n") ;
        sbuffer.append("Column number = " + _column);
        sbuffer.append("\n") ;
        sbuffer.append("System Id = " + _systemId);
        sbuffer.append("\n") ;
        sbuffer.append("Public Id = " + _publicId);
        sbuffer.append("\n") ;
        sbuffer.append("CharacterOffset = " + _charOffset);
        sbuffer.append("\n") ;
        return sbuffer.toString();
    }
    
}

