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


package com.sun.xml.fastinfoset.algorithm;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;


public class IntEncodingAlgorithm extends IntegerEncodingAlgorithm {
    
    public final Object decodeFromBytes(byte[] b, int start, int length) {
        if (length % INT_SIZE != 0) {
            throw new IllegalArgumentException("'length' is not a multiple of " +
                    INT_SIZE + 
                    " bytes correspond to the size of the 'int' primitive type");
        }
        
        int[] data = new int[length / INT_SIZE];
        decodeFromBytesToFloatArray(data, 0, b, start, length);
        
        return data;
    }
    
    public final byte[] encodeToBytes(Object data, byte[] b, int start) {
        if (!(data instanceof int[])) {
            throw new IllegalArgumentException("'data' not an instance of int[]");
        }

        final int[] idata = (int[])data;

        final int encodedSize = idata.length * INT_SIZE;
        if (encodedSize > (b.length - start)) {
            b = new byte[encodedSize];
            start = 0;
        }

        encodeToBytes(idata, 0, idata.length, b, start);
        return b;
    }
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final ArrayList iValues = new ArrayList();

        matchWhiteSpaceDelimnatedWords(cb, 
                new WordListener () {
                    public void word(int start, int end) {
                        String iStringValue = cb.subSequence(start, end).toString();
                        iValues.add(Integer.valueOf(iStringValue));
                    }
                }
        );
        
        int[] idata = new int[iValues.size()];
        for (int i = 0; i < idata.length; i++) {
            idata[i] = ((Float)iValues.get(i)).intValue();
        }
        
        return idata;
    }
    
    public final char[] convertToCharacters(Object data, char ch[], int start) {
        if (!(data instanceof int[])) {
            throw new IllegalArgumentException("'data' not an instance of int[]");
        }

        final int[] idata = (int[])data;

        if (idata.length * INT_MAX_CHARACTER_SIZE > (ch.length - start)) {
            ch = new char[idata.length * INT_MAX_CHARACTER_SIZE];
            start = 0;
        }

        return convertToCharacters(idata, ch, start);
    }

    
    
    public final void encodeToBytes(int[] data, int istart, int ilength, byte[] b, int start) {
        final int iend = istart + ilength;
        for (int i = istart; i < iend; i++) {
            final int bits = data[i];
            b[start++] = (byte)((bits >>> 24) & 0xFF);
            b[start++] = (byte)((bits >>> 16) & 0xFF);
            b[start++] = (byte)((bits >>>  8) & 0xFF);
            b[start++] = (byte)((bits >>>  0) & 0xFF);
        }
    }

    public final void decodeFromBytesToFloatArray(int[] data, int istart, byte[] b, int start, int length) {
        final int size = length / INT_SIZE;
        for (int i = 0; i < size; i++) {
            data[istart++] = (b[start++] << 24) | (b[start++]<< 16) | (b[start++] << 8) | b[start++];
        }
    }
    
    public final char[] convertToCharacters(int[] idata, char ch[], int start) {
        final CharBuffer buffer = CharBuffer.wrap(ch, start, ch.length - start);
        for (int i = 0; i < idata.length; i++) {
            buffer.put(Integer.toString(idata[i]));            
            if (i != idata.length) {
                buffer.put(' ');
            }
        }
        
        return ch;
    }
    
}
