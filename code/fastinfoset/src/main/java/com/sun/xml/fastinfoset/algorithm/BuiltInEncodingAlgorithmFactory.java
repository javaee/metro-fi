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

import com.sun.xml.fastinfoset.EncodingConstants;
import org.jvnet.fastinfoset.EncodingAlgorithmIndexes;

public final class BuiltInEncodingAlgorithmFactory {

    public final static BuiltInEncodingAlgorithm[] table =
            new BuiltInEncodingAlgorithm[EncodingConstants.ENCODING_ALGORITHM_BUILTIN_END + 1];

    public final static HexadecimalEncodingAlgorithm hexadecimalEncodingAlgorithm = new HexadecimalEncodingAlgorithm();
    
    public final static BASE64EncodingAlgorithm base64EncodingAlgorithm = new BASE64EncodingAlgorithm();

    public final static BooleanEncodingAlgorithm booleanEncodingAlgorithm = new BooleanEncodingAlgorithm();
    
    public final static ShortEncodingAlgorithm shortEncodingAlgorithm = new ShortEncodingAlgorithm();

    public final static IntEncodingAlgorithm intEncodingAlgorithm = new IntEncodingAlgorithm();

    public final static LongEncodingAlgorithm longEncodingAlgorithm = new LongEncodingAlgorithm();
    
    public final static FloatEncodingAlgorithm floatEncodingAlgorithm = new FloatEncodingAlgorithm();

    public final static DoubleEncodingAlgorithm doubleEncodingAlgorithm = new DoubleEncodingAlgorithm();
    
    public final static UUIDEncodingAlgorithm uuidEncodingAlgorithm = new UUIDEncodingAlgorithm();
    
    static {
        table[EncodingAlgorithmIndexes.HEXADECIMAL] = hexadecimalEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.BASE64] = base64EncodingAlgorithm;
        table[EncodingAlgorithmIndexes.SHORT] = shortEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.INT] = intEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.LONG] = longEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.BOOLEAN] = booleanEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.FLOAT] = floatEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.DOUBLE] = doubleEncodingAlgorithm;
        table[EncodingAlgorithmIndexes.UUID] = uuidEncodingAlgorithm;
    }
}
