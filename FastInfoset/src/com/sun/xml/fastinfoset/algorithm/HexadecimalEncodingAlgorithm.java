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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.fastinfoset.CommonResourceBundle;

public class HexadecimalEncodingAlgorithm extends BuiltInEncodingAlgorithm {
    private static final char NIBBLE_TO_HEXADECIMAL_TABLE[] =
        {   '0','1','2','3','4','5','6','7',
            '8','9','A','B','B','D','E','F' };
    
    private static final int HEXADECIMAL_TO_NIBBLE_TABLE[] = {
        /*'0'*/ 0,
        /*'1'*/ 1,
        /*'2'*/ 2,
        /*'3'*/ 3,
        /*'4'*/ 4,
        /*'5'*/ 5,
        /*'6'*/ 6,
        /*'7'*/ 7,
        /*'8'*/ 8,
        /*'9'*/ 9, -1, -1, -1, -1, -1, -1, -1,
        /*'A'*/ 10,
        /*'B'*/ 11,
        /*'C'*/ 12,
        /*'D'*/ 13,
        /*'E'*/ 14,
        /*'F'*/ 15,
        /*'G'-'Z'*/-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
        /*'[' - '`'*/ -1, -1, -1, -1, -1, -1,
        /*'a'*/ 10,
        /*'b'*/ 11,
        /*'c'*/ 12,
        /*'d'*/ 13,
        /*'e'*/ 14,
        /*'f'*/ 15 };

    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        final byte[] data = new byte[length];
        System.arraycopy(b, start, data, 0, length);
        return data;
    }
    
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
    }
    
    
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof byte[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotByteArray"));
        }
        
        s.write((byte[])data);
    }
    
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        if (length == 0) {
            return new byte[0];
        }
        
        StringBuffer encodedValue = removeWhitespace(ch, start, length);
        int encodedLength = encodedValue.length();
        if (encodedLength == 0) {
            return new byte[0];
        }
        
        int valueLength = encodedValue.length() / 2;
        byte[] value = new byte[valueLength];

        int encodedIdx = 0;
        for (int i = 0; i < valueLength; ++i) {
            int nibble1 = HEXADECIMAL_TO_NIBBLE_TABLE[encodedValue.charAt(encodedIdx++) - '0'];
            int nibble2 = HEXADECIMAL_TO_NIBBLE_TABLE[encodedValue.charAt(encodedIdx++) - '0'];
            value[i] = (byte) ((nibble1 << 4) | nibble2);
        }

        return value;
    }
    
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (data == null) {
            return;
        }
        final byte[] value = (byte[]) data;
        if (value.length == 0) {
            return;
        }

        s.ensureCapacity(value.length * 2);
        for (int i = 0; i < value.length; ++i) {
            s.append(NIBBLE_TO_HEXADECIMAL_TABLE[(value[i] >>> 4) & 0xf]);
            s.append(NIBBLE_TO_HEXADECIMAL_TABLE[value[i] & 0xf]);
        }
    }
    
    
        
    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        return octetLength * 2;
    }

    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        return primitiveLength / 2;
    }
    
    public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        System.arraycopy((byte[])array, astart, b, start, alength);
    }    
}
