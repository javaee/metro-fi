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

import java.util.HashMap;
import java.util.Map;

public class StringIntMap extends KeyIntMap {

    StringIntMap _readOnlyMap;
    boolean _hasReadOnlyMap = false;
    
    static class Entry extends BaseEntry {
        final String _key;
        Entry _next;
        
        public Entry(String key, int hash, int value, Entry next) {
            super(hash, value);
            _key = key;
            _next = next;
        }
    }
    
    private Entry[] _table;
    private String[] _array;
    
    public StringIntMap(int initialCapacity, float loadFactor, boolean supportArray) {
        super(initialCapacity, loadFactor, supportArray);

        _table = new Entry[_capacity];
        
        if (_supportArray) {
            _array = new String[_capacity];
        }
    }
    
    public StringIntMap(int initialCapacity, boolean supportArray) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, supportArray);
    }

    public StringIntMap(boolean supportArray) {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, supportArray);
    }

    public StringIntMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, false);
    }

    public void clear() {
        for (int i = 0; i < _table.length; i++) {
            _table[i] = null;
        }
        _size = 0;

        if (_supportArray) {
            for (int i = 0; i < _arraySize; i++) {
                _array[i] = null;
            }
            _arraySize = 0;
        }
    }

    public String[] getKeys() {
        return _array;
    }
    
    public void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
        if (!(readOnlyMap instanceof StringIntMap)) {
            throw new IllegalArgumentException("Illegal class: "
                + readOnlyMap);
        }       
        
        setReadOnlyMap((StringIntMap)readOnlyMap, clear);
    }
    
    public void setReadOnlyMap(StringIntMap readOnlyMap, boolean clear) {
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
    
    public int obtainIndex(String key) {
        final int hash = hashHash(key.hashCode());
        
        if (_readOnlyMap != null) {
            final int index = _readOnlyMap.get(key, hash);
            if (index != -1) {
                return index;
            }
        }
        
        final int tableIndex = indexFor(hash, _table.length);
        for (Entry e = _table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && eq(key, e._key)) {
                return e._value;
            }
        }

        addEntry(key, hash, _size + _readOnlyMapSize, tableIndex);        
        return NOT_PRESENT;
    }

    public void add(String key) {
        final int hash = hashHash(key.hashCode());
        final int tableIndex = indexFor(hash, _table.length);
        addEntry(key, hash, _size + _readOnlyMapSize, tableIndex);
    }

    public int get(String key) {
        return get(key, hashHash(key.hashCode()));
    }
    
    private int get(String key, int hash) {
        if (_readOnlyMap != null) {
            int i = _readOnlyMap.get(key, hash);
            if (i != -1) {
                return i;
            }
        }

        final int tableIndex = indexFor(hash, _table.length);
        for (Entry e = _table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && eq(key, e._key)) {
                return e._value;
            }
        }
                
        return -1;
    }


    private void addEntry(String key, int hash, int value, int bucketIndex) {
	Entry e = _table[bucketIndex];
        _table[bucketIndex] = new Entry(key, hash, value, e);
        if (_size++ >= _threshold) {
            resize(2 * _table.length);
        }

        if (_supportArray) {
            if (_arraySize == _array.length) {
                String[] newArray = new String[_arraySize * 3 / 2 + 1];
                System.arraycopy(_array, 0, newArray, 0, _arraySize);
                _array = newArray;
            }

            _array[_arraySize++] = key;
        }
    }
    
    private void resize(int newCapacity) {
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

    private void transfer(Entry[] newTable) {
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
        
    boolean eq(String x, String y) {
        return x == y || x.equals(y);
    }

}
