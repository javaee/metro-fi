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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;



public class FloatEncodingAlgorithm extends IEEE754FloatingPointEncodingAlgorithm {
    
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
    
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        return decodeFromInputStreamToFloatArray(s);
    }
    
    
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof float[])) {
            throw new IllegalArgumentException("'data' not an instance of float[]");
        }
        
        final float[] fdata = (float[])data;
        
        encodeToOutputStreamFromFloatArray(fdata, s);
    }
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List floatList = new ArrayList();
        
        matchWhiteSpaceDelimnatedWords(cb,
                new WordListener() {
            public void word(int start, int end) {
                String fStringValue = cb.subSequence(start, end).toString();
                floatList.add(Float.valueOf(fStringValue));
            }
        }
        );
        
        return generateArrayFromList(floatList);
    }
    
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof float[])) {
            throw new IllegalArgumentException("'data' not an instance of float[]");
        }
        
        final float[] fdata = (float[])data;
        
        convertToCharactersFromFloatArray(fdata, s);
    }
    
    
    public final void decodeFromBytesToFloatArray(float[] data, int fstart, byte[] b, int start, int length) {
        final int size = length / FLOAT_SIZE;
        for (int i = 0; i < size; i++) {
            final int bits = (b[start++] << 24) | (b[start++]<< 16) | (b[start++] << 8) | b[start++];
            data[fstart++] = Float.intBitsToFloat(bits);
        }
    }
    
    public final float[] decodeFromInputStreamToFloatArray(InputStream s) throws IOException {
        final List floatList = new ArrayList();
        final byte[] b = new byte[FLOAT_SIZE];
        
        while (true) {
            int n = s.read(b);
            if (n != 4) {
                if (n == -1) {
                    break;
                }
                
                while(n != 4) {
                    final int m = s.read(b, n, FLOAT_SIZE - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            
            int bits = (b[0] << 24) | (b[1]<< 16) | (b[2] << 8) | b[3];
            floatList.add(new Float(Float.intBitsToFloat(bits)));
        }
        
        return generateArrayFromList(floatList);
    }
    
    
    public final void encodeToOutputStreamFromFloatArray(float[] fdata, OutputStream s) throws IOException {
        for (int i = 0; i < fdata.length; i++) {
            final int bits = Float.floatToIntBits(fdata[i]);
            s.write((bits >>> 24) & 0xFF);
            s.write((bits >>> 16) & 0xFF);
            s.write((bits >>> 8) & 0xFF);
            s.write(bits & 0xFF);
        }
    }
    
    public final void encodeToBytes(float[] fdata, int fstart, int flength, byte[] b, int start) {
        final int fend = fstart + flength;
        for (int i = fstart; i < fend; i++) {
            final int bits = Float.floatToIntBits(fdata[i]);
            b[start++] = (byte)((bits >>> 24) & 0xFF);
            b[start++] = (byte)((bits >>> 16) & 0xFF);
            b[start++] = (byte)((bits >>>  8) & 0xFF);
            b[start++] = (byte)((bits >>>  0) & 0xFF);
        }
    }
    
    
    public final void convertToCharactersFromFloatArray(float[] fdata, StringBuffer s) {
        for (int i = 0; i < fdata.length; i++) {
            s.append(Float.toString(fdata[i]));
            if (i != fdata.length) {
                s.append(' ');
            }
        }
    }
    
    
    public final float[] generateArrayFromList(List array) {
        float[] fdata = new float[array.size()];
        for (int i = 0; i < fdata.length; i++) {
            fdata[i] = ((Float)array.get(i)).floatValue();
        }
        
        return fdata;
    }
    
}
