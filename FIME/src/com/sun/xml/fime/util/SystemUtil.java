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

import java.util.Vector;

public class SystemUtil {
    public static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
        
    }
    
    public static String getBooleanString(boolean value) {
        if (value) {
            return "true";
        } else {
            return "false";
        }
    }
    
    public static boolean getBooleanValue(String value) {
        if (value.equals("true")) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void fill(byte[] a, int fromIndex, int toIndex, byte val) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                       ") > toIndex(" + toIndex+")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > a.length) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
        for (int i = fromIndex; i < toIndex; i++) {
            a[i] = val;
        }
    }

    public static boolean isWhitespace(char ch) {
        int c = (int) ch;
        // SPACE_SEPARATOR
        // Note that 202F, 00A0, 2007 are excluded because they are non-break
        // ones.
        if ((c == 0x0020) || (c == 0x1680) || (c == 0x180E)
                || ((c >= 0x2000) && (c <= 0x2006))
                || ((c >= 0x2008) && (c <= 0x200B)) || (c == 0x205F)
                || (c == 0x3000)) {
            return true;
        }
        // LINE_SEPARATOR
        if (c == 0x2028) {
            return true;
        }
        // PARAGRAPH_SEPARATOR
        if (c == 0x2029) {
            return true;
        }
        if (((c >= 0x0009) && (c <= 0x000D))
                || ((c >= 0x001C) && (c <= 0x001F))) {
            return true;
        }
        return false;
    }

    public static String[] split(String target, String pattern) {
        // TODO implement splitting
        int start = 0;
        int index = 0;
        int length = pattern.length();
        Vector splitList = new Vector();
        while ((index = target.indexOf(pattern, start)) >= 0) {
            String split = target.substring(start, index);
            splitList.addElement(split);
            start = index + length;
            // "a b c" by " "
            // index = 1 length =1
            // next start = 2
        }
        String[] splits = new String[splitList.size()];
        for (int i = 0; i < splits.length; i++) {
            splits[i] = (String) splitList.elementAt(i);
        }
        return splits;
    }
}
