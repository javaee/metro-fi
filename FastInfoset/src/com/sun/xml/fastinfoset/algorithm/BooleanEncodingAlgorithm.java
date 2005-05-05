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
import org.jvnet.fastinfoset.EncodingAlgorithmException;

/**
 * An encoder for handling boolean values.  Suppports the builtin BOOLEAN encoder.
 *
 * @author Alan Hudson
 * @version
 */

public class BooleanEncodingAlgorithm extends BuiltInEncodingAlgorithm {
	/** Table for setting a particular bit of a byte */
	private int[] BIT_TABLE = {
	   1 << 0,
	   1 << 1,
	   1 << 2,
	   1 << 3,
	   1 << 4,
	   1 << 5,
	   1 << 6,
	   1 << 7};

    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {

        boolean[] data = new boolean[getPrimtiveLengthFromOctetLength(length)];
        decodeFromBytesToBooleanArray(data, 0, b, start, length);

        return data;
    }

    public final void decodeFromBytesToBooleanArray(boolean[] sdata, int istart, byte[] b, int start, int length) {
        // if length is number of bytes then we can't create the right length array.  How many bits are there?
        // or is it ok to have some extra false values?

    }

    public final Object decodeFromInputStream(InputStream s) throws IOException {
        throw new UnsupportedOperationException("Decode Bytes Not implemented");
    }


    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof boolean[])) {
            throw new IllegalArgumentException("'data' not an instance of boolean[]");
        }

		boolean[] bdata = (boolean[]) data;
		int numBytes = (int) Math.ceil(bdata.length / 8);
		int numBits = bdata.length;

		int byteNum = 0;
		int bitNum = 0;

		// Use an int to avoid signed byte issues
		int tmp = 0;

		for(int i = 0; i < numBits; i++) {
		    if (bdata[i])
				tmp |= tmp & BIT_TABLE[bitNum];

			bitNum++;

			if (bitNum == 8) {
				byteNum++;
				bitNum = 0;
				s.write((byte)tmp);
				tmp = 0;
			}
		}
    }

    public final Object convertFromCharacters(char[] ch, int start, int length) {
        if (length == 0) {
            return new boolean[0];
        }

        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List booleanList = new ArrayList();

        matchWhiteSpaceDelimnatedWords(cb,
                new WordListener() {
            public void word(int start, int end) {
                if (cb.charAt(start) == 't')
                    booleanList.add(Boolean.TRUE);
                else
                	booleanList.add(Boolean.FALSE);
            }
        }
        );

        return generateArrayFromList(booleanList);
    }

    public final void convertToCharacters(Object data, StringBuffer s) {
        if (data == null) {
            return;
        }
        final boolean[] value = (boolean[]) data;
        if (value.length == 0) {
            return;
        }

		// Insure conservately as all false
        s.ensureCapacity(value.length * 5);

        for (int i = 0; i < value.length; ++i) {
        	if (value[i])
        	    s.append("true");
        	else
        		s.append("false");
        }
    }

    public int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
       // Not sure how to implement.  How do we account for unused bits.

       return 0;
    }

    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
    	return 0;
    }

    public void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        if (!(array instanceof boolean[])) {
            throw new IllegalArgumentException("'data' not an instance of boolean[]");
        }

        encodeToBytesFromBooleanArray((boolean[])array,astart,alength,b,start);
	}

    public void encodeToBytesFromBooleanArray(boolean[] array, int astart, int alength, byte[] b, int start) {
		boolean[] bdata = (boolean[]) array;
		int numBytes = (int) Math.ceil(alength / 8);
		int numBits = alength;

		int byteNum = 0;
		int bitNum = 0;

		// Use an int to avoid signed byte issues
		int tmp = 0;

		for(int i = start; i < start + numBits; i++) {
		    if (bdata[i])
				tmp |= tmp & BIT_TABLE[bitNum];

			bitNum++;

			if (bitNum == 8) {
				b[byteNum + start] = (byte) tmp;
				byteNum++;
				bitNum = 0;
				tmp = 0;
			}
		}
    }

	/**
	 * Generate a boolean array from a list of Booleans.
	 *
	 * @param array The array
	 */
    private final boolean[] generateArrayFromList(List array) {
        boolean[] bdata = new boolean[array.size()];
        for (int i = 0; i < bdata.length; i++) {
            bdata[i] = ((Boolean)array.get(i)).booleanValue();
        }

        return bdata;
    }

}
