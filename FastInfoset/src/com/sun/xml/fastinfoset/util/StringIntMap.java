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

public class StringIntMap extends KeyIntMap {
    protected static final Entry NULL_ENTRY = new Entry(null, 0, -1, null);
    
    protected StringIntMap _readOnlyMap;
    
    protected static class Entry extends BaseEntry {
        final String _key;
        Entry _next;
        
        public Entry(String key, int hash, int value, Entry next) {
            super(hash, value);
            _key = key;
            _next = next;
        }
    }
    
    protected Entry _lastEntry = NULL_ENTRY;
    
    protected Entry[] _table;
    
    protected int _index;
    
    // Total character count of Map
    protected int _totalCharacterCount;
    
    public StringIntMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);

        _table = new Entry[_capacity];
    }
    
    public StringIntMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public StringIntMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public void clear() {
        for (int i = 0; i < _table.length; i++) {
            _table[i] = null;
        }
        _lastEntry = NULL_ENTRY;
        _size = 0;
        _index = _readOnlyMapSize;
        _totalCharacterCount = 0;
    }

    public void setReadOnlyMap(KeyIntMap readOnlyMap, boolean clear) {
        if (!(readOnlyMap instanceof StringIntMap)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().
                    getString("message.illegalClass", new Object[]{readOnlyMap}));
        }       
        
        setReadOnlyMap((StringIntMap)readOnlyMap, clear);
    }
    
    public final void setReadOnlyMap(StringIntMap readOnlyMap, boolean clear) {
        _readOnlyMap = readOnlyMap;
        if (_readOnlyMap != null) {
            _readOnlyMapSize = _readOnlyMap.size();
            _index = _size + _readOnlyMapSize;
            
            if (clear) {
                clear();
            }
        }  else {
            _readOnlyMapSize = 0;
            _index = _size;
        }     
    }
    
    public final int getNextIndex() {
        return _index++;
    }
    
    public final int getIndex() {
        return _index;        
    }
    
    public final int obtainIndex(String key) {
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

        addEntry(key, hash, tableIndex);        
        return NOT_PRESENT;
    }

    public final void add(String key) {
        final int hash = hashHash(key.hashCode());
        final int tableIndex = indexFor(hash, _table.length);
        addEntry(key, hash, tableIndex);
    }

    public final int get(String key) {
        if (key == _lastEntry._key)
            return _lastEntry._value;
        
        return get(key, hashHash(key.hashCode()));
    }
    
    public final int getTotalCharacterCount() {
        return _totalCharacterCount;
    }
    
    private final int get(String key, int hash) {
        if (_readOnlyMap != null) {
            final int i = _readOnlyMap.get(key, hash);
            if (i != -1) {
                return i;
            }
        }

        final int tableIndex = indexFor(hash, _table.length);
        for (Entry e = _table[tableIndex]; e != null; e = e._next) {
            if (e._hash == hash && eq(key, e._key)) {
                _lastEntry = e;
                return e._value;
            }
        }
                
        return -1;
    }


    private final void addEntry(String key, int hash, int bucketIndex) {
	Entry e = _table[bucketIndex];
        _table[bucketIndex] = new Entry(key, hash, _index++, e);
        _totalCharacterCount += key.length();
        if (_size++ >= _threshold) {
            resize(2 * _table.length);
        }
    }
    
    protected final void resize(int newCapacity) {
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
        
    private final boolean eq(String x, String y) {
        return x == y || x.equals(y);
    }
}
