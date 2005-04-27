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

public class ContiguousCharArrayArray extends ValueArray {
    
    public int[] _offset;
    public int[] _length;
    
    public char[] _array;
    public int _arrayIndex;
    public int _readOnlyArrayIndex;
    
    private CharArray _charArray = new CharArray();
    
    private ContiguousCharArrayArray _readOnlyArray;
    
    public ContiguousCharArrayArray(int initialCapacity) {
        _offset = new int[initialCapacity];
        _length = new int[initialCapacity];
        _array = new char[512];
        _charArray.ch = _array;
    }

    public ContiguousCharArrayArray() {
        this(DEFAULT_CAPACITY);
    }
    
    public final void clear() {
        _arrayIndex = _readOnlyArrayIndex;
        _size = _readOnlyArraySize;
    }

    public final int getArrayIndex() {
        return _arrayIndex;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof ContiguousCharArrayArray)) {
            throw new IllegalArgumentException("Illegal class: "
                + readOnlyArray);
        }       
        
        setReadOnlyArray((ContiguousCharArrayArray)readOnlyArray, clear);
    }

    public final void setReadOnlyArray(ContiguousCharArrayArray readOnlyArray, boolean clear) {
        if (readOnlyArray != null) {
            _readOnlyArray = readOnlyArray;
            _readOnlyArraySize = readOnlyArray.getSize();
            _readOnlyArrayIndex = readOnlyArray.getArrayIndex();
            
            if (clear) {
                clear();
            }

            _charArray.ch = _array = getCompleteCharArray();
            _offset = getCompleteOffsetArray();
            _length = getCompleteLengthArray();
            _size = _readOnlyArraySize;
        }
    }

    public final char[] getCompleteCharArray() {
        if (_readOnlyArray == null) {
            return _array;
        } else {
            final char[] ra = _readOnlyArray.getCompleteCharArray();
            final char[] a = new char[_readOnlyArrayIndex + _array.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArrayIndex);
            return a;
        }
    }
    
    public final int[] getCompleteOffsetArray() {
        if (_readOnlyArray == null) {
            return _offset;
        } else {
            final int[] ra = _readOnlyArray.getCompleteOffsetArray();
            final int[] a = new int[_readOnlyArraySize + _offset.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArraySize);
            return a;
        }
    }
    
    public final int[] getCompleteLengthArray() {
        if (_readOnlyArray == null) {
            return _length;
        } else {
            final int[] ra = _readOnlyArray.getCompleteOffsetArray();
            final int[] a = new int[_readOnlyArraySize + _length.length];
            System.arraycopy(ra, 0, a, 0, _readOnlyArraySize);
            return a;
        }
    }
    
    public final CharArray get(int i) {
        _charArray.start = _offset[i];
        _charArray.length = _length[i];
        return _charArray;
   }
    
    public final void add(int o, int l) {
        if (_size == _offset.length) {
            final int[] offset = new int[_size * 3 / 2 + 1];
            System.arraycopy(_offset, 0, offset, 0, _size);
            _offset = offset;
            
            final int[] length = new int[_size * 3 / 2 + 1];
            System.arraycopy(_length, 0, length, 0, _size);
            _length = length;
        }
            
       _offset[_size] = o;
       _length[_size++] = l;
    }    
    
    public final void add(char[] c, int l) {
        if (_size == _offset.length) {
            final int[] offset = new int[_size * 3 / 2 + 1];
            System.arraycopy(_offset, 0, offset, 0, _size);
            _offset = offset;
            
            final int[] length = new int[_size * 3 / 2 + 1];
            System.arraycopy(_length, 0, length, 0, _size);
            _length = length;
        }
            
       _offset[_size] = _arrayIndex;
       _length[_size++] = l;
       
       final int arrayIndex = _arrayIndex + l;
       if (arrayIndex >= _array.length) {
           final char[] array = new char[arrayIndex * 3 / 2 + 1];
            System.arraycopy(_array, 0, array, 0, _arrayIndex);
            _charArray.ch = _array = array;
       }
       
       System.arraycopy(c, 0, _array, _arrayIndex, l);
       _arrayIndex = arrayIndex;
    }    
}
