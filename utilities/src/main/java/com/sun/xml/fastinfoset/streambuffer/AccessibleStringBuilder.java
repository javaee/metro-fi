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
package com.sun.xml.fastinfoset.streambuffer;

/**
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class AccessibleStringBuilder {
    
    /**
     * The value is used for character storage.
     */
    char value[];

    /** 
     * The count is the number of characters used.
     */
    int count;

    /** 
     * This no-arg constructor is necessary for serialization of subclasses.
     */
    public AccessibleStringBuilder() {
        this(32);
    }

    /** 
     * Creates an AbstractStringBuilder of the specified capacity.
     */
    public AccessibleStringBuilder(int capacity) {
        value = new char[capacity];
    }

    public int length() {
	return count;
    }

    public int capacity() {
	return value.length;
    }
    
    public void ensureCapacity(int minimumCapacity) {
	if (minimumCapacity > value.length) {
	    expandCapacity(minimumCapacity);
	}
    }
    
    void expandCapacity(int minimumCapacity) {
	int newCapacity = (value.length + 1) * 2;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minimumCapacity > newCapacity) {
	    newCapacity = minimumCapacity;
	}	
	char newValue[] = new char[newCapacity];
	System.arraycopy(value, 0, newValue, 0, count);
	value = newValue;
    }

    public void setLength(int newLength) {
	if (newLength < 0)
	    throw new StringIndexOutOfBoundsException(newLength);
	if (newLength > value.length)
	    expandCapacity(newLength);

	if (count < newLength) {
	    for (; count < newLength; count++)
		value[count] = '\0';
	} else {
            count = newLength;
        }
    }

    public AccessibleStringBuilder append(char str[]) { 
	int newCount = count + str.length;
	if (newCount > value.length)
	    expandCapacity(newCount);
        System.arraycopy(str, 0, value, count, str.length);
        count = newCount;
        return this;
    }

    public AccessibleStringBuilder append(char str[], int offset, int len) {
        int newCount = count + len;
	if (newCount > value.length)
	    expandCapacity(newCount);
	System.arraycopy(str, offset, value, count, len);
	count = newCount;
	return this;
    }

    public String toString() {
	return new String(value, 0, count);
    }

    public char[] getValue() {
        return value;
    }
}
