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


public class FloatEncodingAlgorithm extends IEEE754FloatingPointEncodingAlgorithm {
    
    public FloatEncodingAlgorithm() {
    }

    public final Object decodeFromBytes(byte[] b, int start, int length) {
        if (length % FLOAT_SIZE != 0) {
            throw new IllegalArgumentException("'length' is not a multiple of " +
                    FLOAT_SIZE + 
                    " bytes correspond to the size of the IEEE 754 floating-point \"single format\"");
        }

        float[] data = new float[length / FLOAT_SIZE];
        decodeFromBytesToFloatArray(data, 0, b, start, length);
        
        return data;
    }
    
    public final byte[] encodeToBytes(Object data, byte[] b, int start) {
        if (!(data instanceof float[])) {
            throw new IllegalArgumentException("'data' not an instance of float[]");
        }

        final float[] fdata = (float[])data;

        final int encodedSize = fdata.length * FLOAT_SIZE;
        if (encodedSize > (b.length - start)) {
            b = new byte[encodedSize];
            start = 0;
        }

        encodeToBytes(fdata, 0, fdata.length, b, start);
        return b;
    }
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final ArrayList fValues = new ArrayList();
        
        matchWhiteSpaceDelimnatedWords(cb, 
                new WordListener () {
                    public void word(int start, int end) {
                        String fStringValue = cb.subSequence(start, end).toString();
                        fValues.add(Float.valueOf(fStringValue));
                    }
                }
        );
        
        float[] fdata = new float[fValues.size()];
        for (int i = 0; i < fdata.length; i++) {
            fdata[i] = ((Float)fValues.get(i)).floatValue();
        }
        
        return fdata;
    }
    
    public final char[] convertToCharacters(Object data, char ch[], int start) {
        if (!(data instanceof float[])) {
            throw new IllegalArgumentException("'data' not an instance of float[]");
        }

        final float[] fdata = (float[])data;

        if (fdata.length * FLOAT_MAX_CHARACTER_SIZE > (ch.length - start)) {
            ch = new char[fdata.length * FLOAT_MAX_CHARACTER_SIZE];
            start = 0;
        }

        return convertToCharacters(fdata, ch, start);
    }
    
    public final void encodeToBytes(float[] data, int fstart, int flength, byte[] b, int start) {
        final int fend = fstart + flength;
        for (int i = fstart; i < fend; i++) {
            final int bits = Float.floatToIntBits(data[i]);
            b[start++] = (byte)((bits >>> 24) & 0xFF);
            b[start++] = (byte)((bits >>> 16) & 0xFF);
            b[start++] = (byte)((bits >>>  8) & 0xFF);
            b[start++] = (byte)((bits >>>  0) & 0xFF);
        }
    }

    public final void decodeFromBytesToFloatArray(float[] data, int fstart, byte[] b, int start, int length) {
        final int size = length / FLOAT_SIZE;
        for (int i = 0; i < size; i++) {
            final int bits = (b[start++] << 24) | (b[start++]<< 16) | (b[start++] << 8) | b[start++];
            data[fstart++] = Float.intBitsToFloat(bits);
        }
    }
    
    public final char[] convertToCharacters(float[] fdata, char ch[], int start) {
        final CharBuffer buffer = CharBuffer.wrap(ch, start, ch.length - start);
        for (int i = 0; i < fdata.length; i++) {
            buffer.put(Float.toString(fdata[i]));            
            if (i != fdata.length) {
                buffer.put(' ');
            }
        }
        
        return ch;
    }

}
