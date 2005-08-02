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

import com.sun.xml.fime.jvnet.FastInfosetException;


public class DuplicateAttributeVerifier {
    public static final int MAP_SIZE = 256;
    
    public int _currentIteration;
    
    private static class Entry {
        private int iteration;
        private int value;
        
        private Entry hashNext;
        
        private Entry poolNext;
    }
    
    private Entry[] _map;
       
    public final Entry _poolHead;
    public Entry _poolCurrent;
    private Entry _poolTail;
    
    
    public DuplicateAttributeVerifier() {
        _poolTail = _poolHead = new Entry();
    }
    
    public final void clear() {
        _currentIteration = 0;

        Entry e = _poolHead;
        while (e != null) {
            e.iteration = 0;
            e = e.poolNext;
        }
        
        reset();
    }
    
    public final void reset() {
        _poolCurrent = _poolHead;
    }
    
    private final void increasePool(int capacity) {
        if (_map == null) {
            _map = new Entry[MAP_SIZE];
            _poolCurrent = _poolHead;
        } else {
            final Entry tail = _poolTail;
            for (int i = 0; i < capacity; i++) {
                final Entry e = new Entry();
                _poolTail.poolNext = e;
                _poolTail = e;
            }

            _poolCurrent = tail.poolNext;
        }
    }
    
    public final void checkForDuplicateAttribute(int hash, int value) throws FastInfosetException {
        if (_poolCurrent == null) {
            increasePool(16);
        }
        
        // Get next free entry
        final Entry newEntry = _poolCurrent;
        _poolCurrent = _poolCurrent.poolNext;

        final Entry head = _map[hash];
        if (head == null || head.iteration < _currentIteration) {
            newEntry.hashNext = null;
            _map[hash] = newEntry;
            newEntry.iteration = _currentIteration;
            newEntry.value = value;
        } else {
            Entry e = head;
            do {
                if (e.value == value) {
                    reset();
                    throw new FastInfosetException(MessageCenter.getString("message.duplicateAttribute"));
                }
            } while ((e = e.hashNext) != null);
            
            newEntry.hashNext = head;
            _map[hash] = newEntry;
            newEntry.iteration = _currentIteration;
            newEntry.value = value;
        }        
    }
}
