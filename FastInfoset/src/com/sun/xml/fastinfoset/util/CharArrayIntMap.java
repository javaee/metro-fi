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

import com.sun.xml.fastinfoset.CommonResourceBundle;

public class CharArrayIntMap extends KeyIntMap {

    private CharArrayIntMap _readOnlyMap;

    // Total character count of Map
    protected int _totalCharacterCount;
    
    static class Entry extends BaseEntry {
        final char[] _ch;
        final int _start;
        final int _length;
        Entry _next;
        
        public Entry(char[] ch, int start, int length, int hash, int value, Entry next) {
            super(hash, value);
            _ch = ch;
            _start = start;
            _length = length;
            _next = next;
        }
        
        public final boolean equalsCharArray(char[] ch, int start, int length) {
            if (_length == length) {
                int n = _length;
                int i = _start;
                int j = start;
                while (n-- != 0) {
                    if (_ch[i++] != ch[j++])
                        return false;
                }
                return true;
            }

            return false;
        }
        
    }
    
    private Entry[] _table;
    
    public CharArrayIntMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);

        _table = new Entry[_capacity];        
    }
    
    public CharArrayIntMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    
    public CharArrayIntMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public final void clear() {
        for (int i = 0; i < _table.length; i++) {
            _table[i] = null;
        }
        _size = 0;
        _totalCharacterCount = 0;
    }

    public final void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
        if (!(readOnlyMap instanceof CharArrayIntMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
                    getString("message.illegalClass", new Object[]{readOnlyMap}));
        }       
        
        setReadOnlyMap((CharArrayIntMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(CharArrayIntMap readOnlyMap, boolean clear) {
        _readOnlyMap = readOnlyMap;
        if (_readOnlyMap != null) {
            _readOnlyMapSize = _readOnlyMap.size();
            
            if (clear) {
                clear();
            }
        }  else {
            _readOnlyMapSize = 0;
        }     
    }
    
    public final int obtainIndex(char[] ch, int start, int length, boolean clone) {
        final int hash = hashHash(CharArray.hashCode(ch, start, length));
        
        if (_readOnlyMap != null) {
            final int index = _readOnlyMap.get(ch, start, length, hash);
            if (index != -1) {
                return index;
            }
        }
        
        final int tableIndex = indexFor(hash, _table.length);
        for (Entry e = _table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && e.equalsCharArray(ch, start, length)) {
                return e._value;
            }
        }

        if (clone) {
            char[] chClone = new char[length];
            System.arraycopy(ch, start, chClone, 0, length);

            ch = chClone;
            start = 0;
        }
        
        addEntry(ch, start, length, hash, _size + _readOnlyMapSize, tableIndex);        
        return NOT_PRESENT;
    }
    
    public final int getTotalCharacterCount() {
        return _totalCharacterCount;
    }

    private final int get(char[] ch, int start, int length, int hash) {
        if (_readOnlyMap != null) {
            final int i = _readOnlyMap.get(ch, start, length, hash);
            if (i != -1) {
                return i;
            }
        }

        final int tableIndex = indexFor(hash, _table.length);
        for (Entry e = _table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && e.equalsCharArray(ch, start, length)) {
                return e._value;
            }
        }
                
        return -1;
    }

    private final void addEntry(char[] ch, int start, int length, int hash, int value, int bucketIndex) {
	Entry e = _table[bucketIndex];
        _table[bucketIndex] = new Entry(ch, start, length, hash, value, e);
        _totalCharacterCount += length;
                if (_size++ >= _threshold) {
            resize(2 * _table.length);
        }        
    }
    
    private final void resize(int newCapacity) {
        _capacity = newCapacity;
        Entry[] oldTable = _table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            _threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[_capacity];
        transfer(newTable);
        _table = newTable;
        _threshold = (int)(_capacity * _loadFactor);        
    }

    private final void transfer(Entry[] newTable) {
        Entry[] src = _table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            Entry e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    Entry next = e._next;
                    int i = indexFor(e._hash, newCapacity);  
                    e._next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }        
}
