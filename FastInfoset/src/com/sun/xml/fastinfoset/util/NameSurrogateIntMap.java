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

import com.sun.xml.fastinfoset.NameSurrogate;

public class NameSurrogateIntMap extends KeyIntMap {
    NameSurrogateIntMap _readOnlyMap;
    
    static class Entry extends BaseEntry {
        final NameSurrogate _key;
        Entry _next;
        
        public Entry(NameSurrogate key, int hash, int value, Entry next) {
            super(hash, value);
            _key = key;
            _next = next;
        }
    }
    
    private Entry[] _table;
    
    public NameSurrogateIntMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);

        _table = new Entry[_capacity];
    }
    
    public NameSurrogateIntMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public NameSurrogateIntMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public void clear() {
        for (int i = 0; i < _table.length; i++) {
            _table[i] = null;
        }
        _size = 0;
    }

    public void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
        if (!(readOnlyMap instanceof NameSurrogateIntMap)) {
            throw new IllegalArgumentException("Illegal class: "
                + readOnlyMap);
        }       
        
        setReadOnlyMap((NameSurrogateIntMap)readOnlyMap, clear);
    }

    public void setReadOnlyMap(NameSurrogateIntMap readOnlyMap, boolean clear) {
        _readOnlyMap = readOnlyMap;
        if (_readOnlyMap != null) {
            _readOnlyMapSize = _readOnlyMap.size();
            
            if (clear) {
                clear();
            }
        } else {
            _readOnlyMapSize = 0;
        }
    }

    public int obtainIndex(NameSurrogate key) {
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

    public void add(NameSurrogate key) {
        final int hash = hashHash(key.hashCode());
        final int tableIndex = indexFor(hash, _table.length);
        addEntry(key, hash, _size + _readOnlyMapSize, tableIndex);
    }
    
    public int get(NameSurrogate key) {
        return get(key, hashHash(key.hashCode()));
    }
    
    private int get(NameSurrogate key, int hash) {
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
                
        return -1;
    }


    protected void addEntry(NameSurrogate key, int hash, int value, int bucketIndex) {
	Entry e = _table[bucketIndex];
        _table[bucketIndex] = new Entry(key, hash, value, e);
        if (_size++ >= _threshold) {
            resize(2 * _table.length);
        }
    }
    
    protected void resize(int newCapacity) {
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

    void transfer(Entry[] newTable) {
        Entry[] src = _table;
        for (int j = 0; j < src.length; j++) {
            Entry e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    Entry next = e._next;
                    int i = indexFor(e._hash, _capacity);  
                    e._next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }
    
    static int hash(NameSurrogate key) {
        return hashHash(key.hashCode());
    }    
    
    boolean eq(NameSurrogate x, NameSurrogate y) {
        return x == y || x.equals(y);
    }
}
