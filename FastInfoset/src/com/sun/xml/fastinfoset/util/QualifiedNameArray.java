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

import com.sun.xml.fastinfoset.QualifiedName;

public class QualifiedNameArray extends ValueArray {
    
    private QualifiedName[] _array;
    
    private QualifiedNameArray _readOnlyArray;
        
    public QualifiedNameArray(int initialCapacity) {
        _array = new QualifiedName[initialCapacity];
    }

    public QualifiedNameArray() {
        this(DEFAULT_CAPACITY);
    }
    
    public final void clear() {
        for (int i = 0; i < _size; i++) {
            _array[i] = null;
        }
        _size = 0;
    }

    public final QualifiedName[] getArray() {
        return _array;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof QualifiedNameArray)) {
            throw new IllegalArgumentException("Illegal class: "
                + readOnlyArray);
        }       
        
        setReadOnlyArray((QualifiedNameArray)readOnlyArray, clear);
    }

    public final void setReadOnlyArray(QualifiedNameArray readOnlyArray, boolean clear) {
        if (readOnlyArray != null) {
            _readOnlyArray = readOnlyArray;
            _readOnlyArraySize = readOnlyArray.getSize();
                        
            if (clear) {
                clear();
            }
        }
    }
    
    public final QualifiedName get(int i) {
        if (_readOnlyArray == null) {
            return _array[i];
        } else {
            if (i < _readOnlyArraySize) {
               return _readOnlyArray.get(i); 
            } else {
                return _array[i - _readOnlyArraySize];
            }
        }
   }
    
    public final void add(QualifiedName s) {
        if (_size == _array.length) {
            final QualifiedName[] newArray = new QualifiedName[_size * 3 / 2 + 1];
            System.arraycopy(_array, 0, newArray, 0, _size);
            _array = newArray;
        }
            
       _array[_size++] = s;
    }

}
