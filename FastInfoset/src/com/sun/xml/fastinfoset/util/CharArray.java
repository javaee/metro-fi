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


package com.sun.xml.fastinfoset.util;

public class CharArray {
    public char[] ch;
    public int start;
    public int length;
                                                                                
    protected int _hash = 0;

    protected CharArray() {
    }
    
    public CharArray(char[] _ch, int _start, int _length, boolean copy) {
        set(_ch, _start, _length, copy);
    }
    
    public void set(char[] _ch, int _start, int _length, boolean copy) {
        if (copy) {
            ch = new char[_length];
            start = 0;
            length = _length;
            System.arraycopy(_ch, _start, ch, 0, _length);
        } else {
            ch = _ch;
            start = _start;
            length = _length;
        }
    }
    
    public void cloneArray() {
        char[] _ch = new char[length];
        System.arraycopy(ch, start, _ch, 0, length);
        ch = _ch;
        start = 0;
    }
                                                                                
    public String toString() {
        return new String(ch, start, length);
    }
                                                                                
    public int hashCode() {
        if (_hash == 0) {
            for (int i = start; i < start + length; i++) {
                _hash = 31*_hash + ch[i];
            }
        }
        return _hash;
    }
                                                                                
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CharArray) {
            CharArray cha = (CharArray)obj;
            if (length == cha.length) {
                int n = length;
                int i = start;
                int j = cha.start;
                while (n-- != 0) {
                    if (ch[i++] != cha.ch[j++])
                        return false;
                }
                return true;
            }
        }
        return false;
    }
}
