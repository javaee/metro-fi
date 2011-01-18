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
import com.sun.xml.fastinfoset.CommonResourceBundle;

public class QualifiedNameArray extends ValueArray {
    
    public QualifiedName[] _array;
    
    private QualifiedNameArray _readOnlyArray;
        
    public QualifiedNameArray(int initialCapacity, int maximumCapacity) {
        _array = new QualifiedName[initialCapacity];
        _maximumCapacity = maximumCapacity;
    }

    public QualifiedNameArray() {
        this(DEFAULT_CAPACITY, MAXIMUM_CAPACITY);
    }
    
    public final void clear() {
        _size = _readOnlyArraySize;
    }

    public final QualifiedName[] getArray() {
        return _array;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof QualifiedNameArray)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
                    getString("message.illegalClass", new Object[]{readOnlyArray}));
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
            
            _array = getCompleteArray();
            _size = _readOnlyArraySize;
        }
    }

    public final QualifiedName[] getCompleteArray() {
        if (_readOnlyArray == null) {
            return _array;
        } else {
            final QualifiedName[] ra = _readOnlyArray.getCompleteArray();
            final QualifiedName[] a = new QualifiedName[_readOnlyArraySize + _array.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArraySize);
            return a;
        }
    }
 
    public final QualifiedName getNext() {
        return (_size == _array.length) ? null : _array[_size];
    }
    
    public final void add(QualifiedName s) {
        if (_size == _array.length) {
            resize();
        }
            
       _array[_size++] = s;
    }

    protected final void resize() {
        if (_size == _maximumCapacity) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
        }

        int newSize = _size * 3 / 2 + 1;
        if (newSize > _maximumCapacity) {
            newSize = _maximumCapacity;
        }

        final QualifiedName[] newArray = new QualifiedName[newSize];
        System.arraycopy(_array, 0, newArray, 0, _size);
        _array = newArray;
    }

}
