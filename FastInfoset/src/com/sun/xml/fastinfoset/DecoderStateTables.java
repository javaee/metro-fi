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


package com.sun.xml.fastinfoset;

public class DecoderStateTables {
    private static int RANGE_INDEX_END      = 0;
    private static int RANGE_INDEX_VALUE    = 1;

    public final static int STATE_ILLEGAL                   = 255;
    public final static int STATE_UNSUPPORTED               = 254;

    // EII child states
    public final static int EII_NO_AIIS_INDEX_SMALL         = 0;
    public final static int EII_AIIS_INDEX_SMALL            = 1;
    public final static int EII_INDEX_MEDIUM                = 2;
    public final static int EII_INDEX_LARGE                 = 3;
    public final static int EII_NAMESPACES                  = 4;
    public final static int EII_LITERAL                     = 5;
    public final static int CII_UTF8_SMALL_LENGTH           = 6;
    public final static int CII_UTF8_MEDIUM_LENGTH          = 7;
    public final static int CII_UTF8_LARGE_LENGTH           = 8;
    public final static int CII_EA                          = 9;
    public final static int CII_RA_SMALL_LENGTH             = 10;
    public final static int CII_RA_MEDIUM_LENGTH            = 11;
    public final static int CII_RA_LARGE_LENGTH             = 12;
    public final static int CII_UTF16_SMALL_LENGTH          = 13;
    public final static int CII_UTF16_MEDIUM_LENGTH         = 14;
    public final static int CII_UTF16_LARGE_LENGTH          = 15;
    public final static int CII_INDEX_SMALL                 = 16;
    public final static int CII_INDEX_MEDIUM                = 17;
    public final static int CII_INDEX_LARGE                 = 18;
    public final static int CII_INDEX_LARGE_LARGE            = 19;    
    public final static int COMMENT_II                      = 20;
    public final static int PROCESSING_INSTRUCTION_II       = 21;
    public final static int TERMINATOR_SINGLE               = 22;
    public final static int TERMINATOR_DOUBLE               = 23;

    public static int[] DII = new int[256];
    
    private static int[][] DII_RANGES = {
        // EII

        // %00000000 to %00011111  EII no attributes small index
        { 0x1F, EII_NO_AIIS_INDEX_SMALL },

        // %00100000 to %00100111  EII medium index
        { 0x27, EII_INDEX_MEDIUM },

        // %00101000 to %00101111  EII large index
        // %00110000  EII very large index
        // %00101000 to %00110000
        { 0x30, EII_INDEX_LARGE },

        // %00110001 to %00110111  ILLEGAL
        { 0x37, STATE_ILLEGAL },
        
        // %00111000  EII namespaces
        { 0x38, EII_NAMESPACES },

        // %00111001 to %00111011  ILLEGAL
        { 0x3B, STATE_ILLEGAL },

        // %00111100  EII literal (no prefix, no namespace)
        { 0x3C, EII_LITERAL },

        // %00111101  EII literal (no prefix, namespace)
        { 0x3D, EII_LITERAL },

        // %00111110  ILLEGAL
        { 0x3E, STATE_ILLEGAL },

        // %00111111  EII literal (prefix, namespace)
        { 0x3F, EII_LITERAL },

        // %01000000 to %01011111  EII attributes small index
        { 0x5F, EII_AIIS_INDEX_SMALL },

        // %01100000 to %01100111  EII medium index
        { 0x67, EII_INDEX_MEDIUM },
        
        // %01101000 to %01101111  EII large index
        // %01110000  EII very large index
        // %01101000 to %01110000
        { 0x70, EII_INDEX_LARGE },
        
        // %01110001 to %01110111  ILLEGAL
        { 0x77, STATE_ILLEGAL },
        
        // %01111000  EII attributes namespaces
        { 0x78, EII_NAMESPACES },

        // %01111001 to %01111011  ILLEGAL
        { 0x7B, STATE_ILLEGAL },

        // %01111100  EII attributes literal (no prefix, no namespace)
        { 0x7C, EII_LITERAL },

        // %01111101  EII attributes literal (no prefix, namespace)
        { 0x7D, EII_LITERAL },

        // %01111110  ILLEGAL
        { 0x7E, STATE_ILLEGAL },

        // %01111111  EII attributes literal (prefix, namespace)
        { 0x7F, EII_LITERAL },
        
        // %10000000 to %10111111
        { 0xE0, STATE_ILLEGAL },
        
        // %11100001 processing instruction
        { 0xE1, PROCESSING_INSTRUCTION_II },

        // %11100010 comment
        { 0xE2, COMMENT_II},

        // %111000011 to %11101111
        { 0xEF, STATE_ILLEGAL },
        
        // Terminators
        
        // %11110000  single terminator
        { 0xF0, TERMINATOR_SINGLE },

        // %11110000 to %11111110 ILLEGAL
        { 0xFE, STATE_ILLEGAL },

        // %11111111  double terminator
        { 0xFF, TERMINATOR_DOUBLE }
    };
    
    public static int[] EII = new int[256];
    
    private static int[][] EII_RANGES = {
        // EII

        // %00000000 to %00011111  EII no attributes small index
        { 0x1F, EII_NO_AIIS_INDEX_SMALL },

        // %00100000 to %00100111  EII medium index
        { 0x27, EII_INDEX_MEDIUM },

        // %00101000 to %00101111  EII large index
        // %00110000  EII very large index
        // %00101000 to %00110000
        { 0x30, EII_INDEX_LARGE },

        // %00110001 to %00110111  ILLEGAL
        { 0x37, STATE_ILLEGAL },
        
        // %00111000  EII namespaces
        { 0x38, EII_NAMESPACES },

        // %00111001 to %00111011  ILLEGAL
        { 0x3B, STATE_ILLEGAL },

        // %00111100  EII literal (no prefix, no namespace)
        { 0x3C, EII_LITERAL },

        // %00111101  EII literal (no prefix, namespace)
        { 0x3D, EII_LITERAL },

        // %00111110  ILLEGAL
        { 0x3E, STATE_ILLEGAL },

        // %00111111  EII literal (prefix, namespace)
        { 0x3F, EII_LITERAL },

        // %01000000 to %01011111  EII attributes small index
        { 0x5F, EII_AIIS_INDEX_SMALL },

        // %01100000 to %01100111  EII medium index
        { 0x67, EII_INDEX_MEDIUM },
        
        // %01101000 to %01101111  EII large index
        // %01110000  EII very large index
        // %01101000 to %01110000
        { 0x70, EII_INDEX_LARGE },
        
        // %01110001 to %01110111  ILLEGAL
        { 0x77, STATE_ILLEGAL },
        
        // %01111000  EII attributes namespaces
        { 0x78, EII_NAMESPACES },

        // %01111001 to %01111011  ILLEGAL
        { 0x7B, STATE_ILLEGAL },

        // %01111100  EII attributes literal (no prefix, no namespace)
        { 0x7C, EII_LITERAL },

        // %01111101  EII attributes literal (no prefix, namespace)
        { 0x7D, EII_LITERAL },

        // %01111110  ILLEGAL
        { 0x7E, STATE_ILLEGAL },

        // %01111111  EII attributes literal (prefix, namespace)
        { 0x7F, EII_LITERAL },
        
        // CII
        
        // %10000000 to %10000001  CII UTF-8 no add to table small length
        { 0x81, CII_UTF8_SMALL_LENGTH },

        // %10000010  CII UTF-8 no add to table medium length
        { 0x82, CII_UTF8_MEDIUM_LENGTH },

        // %10000011  CII UTF-8 no add to table large length
        { 0x83, CII_UTF8_LARGE_LENGTH },

        // %10000100 to %10000101  CII UTF-16 no add to table small length
        { 0x85, CII_UTF16_SMALL_LENGTH },

        // %10000110  CII UTF-16 no add to table medium length
        { 0x86, CII_UTF16_MEDIUM_LENGTH },

        // %10000111  CII UTF-16 no add to table large length
        { 0x87, CII_UTF16_LARGE_LENGTH },

        // %10001000 to %10001001  CII RA no add to table small length
        { 0x89, CII_RA_SMALL_LENGTH },

        // %10001010  CII RA no add to table medium length
        { 0x8A, CII_RA_MEDIUM_LENGTH },

        // %10001011  CII RA no add to table large length
        { 0x8B, CII_RA_LARGE_LENGTH },

        // %10001100 to %10001111  CII EA no add to table
        { 0x8F, CII_EA },
                
        // %10010000 to %10010001  CII add to table small length
        { 0x91, CII_UTF8_SMALL_LENGTH },

        // %10010010  CII add to table medium length
        { 0x92, CII_UTF8_MEDIUM_LENGTH },

        // %10010011  CII add to table large length
        { 0x93, CII_UTF8_LARGE_LENGTH },
        
        // %10010100 to %10010101  CII UTF-16 add to table small length
        { 0x95, CII_UTF16_SMALL_LENGTH },

        // %10010110  CII UTF-16 add to table medium length
        { 0x96, CII_UTF16_MEDIUM_LENGTH },

        // %10010111  CII UTF-16 add to table large length
        { 0x97, CII_UTF16_LARGE_LENGTH },

        // %10011000 to %10011001  CII RA add to table small length
        { 0x99, CII_RA_SMALL_LENGTH },

        // %10011010  CII RA add to table medium length
        { 0x9A, CII_RA_MEDIUM_LENGTH},

        // %10011011  CII RA add to table large length
        { 0x9B, CII_RA_LARGE_LENGTH },

        // %10011100 to %10011111  CII EA no add to table
        { 0x9F, CII_EA },

            // %101100xx
            // %101101xx
            // %101110xx
        
        // %10100000 to %10101111  CII small index
        { 0xAF, CII_INDEX_SMALL },
        
        // %10110000 to %10110011  CII medium index
        { 0xB3, CII_INDEX_MEDIUM },

        // %10110100 to %10110111  CII large index
        { 0xB7, CII_INDEX_LARGE },

        // %10111000  CII very large index
        { 0xB8, CII_INDEX_LARGE_LARGE },
        
        // %10111001 to %10111111  ILLEGAL
        { 0xBF, STATE_ILLEGAL },
        
	// Other IIs

	// TODO
        // %11000000 to %11100000
        { 0xE0, STATE_UNSUPPORTED },
        
        // %11100001 processing instruction
        { 0xE1, PROCESSING_INSTRUCTION_II },

        // %11100010 comment
        { 0xE2, COMMENT_II},

        // TODO
        // %111000011 to %11101111
        { 0xEF, STATE_UNSUPPORTED },
        
        // Terminators
        
        // %11110000  single terminator
        { 0xF0, TERMINATOR_SINGLE },

        // %11110000 to %11111110 ILLEGAL
        { 0xFE, STATE_ILLEGAL },

        // %11111111  double terminator
        { 0xFF, TERMINATOR_DOUBLE }
    };

    
    // AII states
    public final static int AII_INDEX_SMALL                 = 0;
    public final static int AII_INDEX_MEDIUM                = 1;
    public final static int AII_INDEX_LARGE                 = 2;
    public final static int AII_LITERAL                     = 3;
    public final static int AII_TERMINATOR_SINGLE           = 4;
    public final static int AII_TERMINATOR_DOUBLE           = 5;

    public static int[] AII = new int[256];

    private static int[][] AII_RANGES = {
        // %00000000 to %00111111  AII small index
        { 0x3F, AII_INDEX_SMALL },

        // %01000000 to %01011111  AII medium index
        { 0x5F, AII_INDEX_MEDIUM },
        
        // %01100000 to %01101111  AII large index
        { 0x6F, AII_INDEX_LARGE },

        // %01110000 to %01110111  ILLEGAL
        { 0x77, STATE_ILLEGAL },

        // %01111000  AII literal (no prefix, no namespace)
        // %01111001  AII literal (no prefix, namespace)
        { 0x79, AII_LITERAL },
        
        // %01111010  ILLEGAL
        { 0x7A, STATE_ILLEGAL },
        
        // %01111011  AII literal (prefix, namespace)
        { 0x7B, AII_LITERAL },
        
        // %10000000 to %11101111  ILLEGAL
        { 0xEF, STATE_ILLEGAL },

        // Terminators
        
        // %11110000  single terminator
        { 0xF0, AII_TERMINATOR_SINGLE },

        // %11110000 to %11111110 ILLEGAL
        { 0xFE, STATE_ILLEGAL },

        // %11111111  double terminator
        { 0xFF, AII_TERMINATOR_DOUBLE }
    };
    
    
    // AII value states
    public final static int NISTRING_UTF8_SMALL_LENGTH     = 0;
    public final static int NISTRING_UTF8_MEDIUM_LENGTH    = 1;
    public final static int NISTRING_UTF8_LARGE_LENGTH     = 2;
    public final static int NISTRING_EA                    = 3;
    public final static int NISTRING_RA_SMALL_LENGTH       = 4;
    public final static int NISTRING_RA_MEDIUM_LENGTH      = 5;
    public final static int NISTRING_RA_LARGE_LENGTH       = 6;
    public final static int NISTRING_UTF16_SMALL_LENGTH    = 7;
    public final static int NISTRING_UTF16_MEDIUM_LENGTH   = 8;
    public final static int NISTRING_UTF16_LARGE_LENGTH    = 9;
    public final static int NISTRING_INDEX_SMALL           = 10;
    public final static int NISTRING_INDEX_MEDIUM          = 11;
    public final static int NISTRING_INDEX_LARGE           = 12;
    public final static int NISTRING_EMPTY                 = 13;

    public static int[] NISTRING = new int[256];

    private static int[][] NISTRING_RANGES = {
        // UTF-8
        
        // %00000000 to %00000111  UTF-8 no add to table small length
        { 0x07, NISTRING_UTF8_SMALL_LENGTH },
        
        // %00001000  UTF-8 no add to table medium length
        { 0x08, NISTRING_UTF8_MEDIUM_LENGTH },

        // %00001001 to %00001011 ILLEGAL
        { 0x0B, STATE_ILLEGAL },
        
        // %00001100  UTF-8 no add to table large length
        { 0x0C, NISTRING_UTF8_LARGE_LENGTH },
        
        // %00001101 to %00001111 ILLEGAL
        { 0x0F, STATE_ILLEGAL },
        
        // UTF-16

        // %00010000 to %00010111  UTF-16 no add to table small length
        { 0x17, NISTRING_UTF16_SMALL_LENGTH },
        
        // %00001000  UTF-16 no add to table medium length
        { 0x18, NISTRING_UTF16_MEDIUM_LENGTH },

        // %00011001 to %00011011 ILLEGAL
        { 0x1B, STATE_ILLEGAL },
        
        // %00011100  UTF-16 no add to table large length
        { 0x1C, NISTRING_UTF16_LARGE_LENGTH },
        
        // %00011101 to %00011111 ILLEGAL
        { 0x1F, STATE_ILLEGAL },
        
        // Restricted alphabet
        
        // %00100000 to %00100111  RA no add to table small length
        { 0x27, NISTRING_RA_SMALL_LENGTH },
        
        // %00101000  RA no add to table medium length
        { 0x28, NISTRING_RA_MEDIUM_LENGTH },

        // %00101001 to %00101011 ILLEGAL
        { 0x2B, STATE_ILLEGAL },
        
        // %00101100  RA no add to table large length
        { 0x2C, NISTRING_RA_LARGE_LENGTH },
        
        // %00101101 to %00101111 ILLEGAL
        { 0x2F, STATE_ILLEGAL },

        // Encoding algorithm

        // %00110000 to %00111111  EA no add to table
        { 0x3F, NISTRING_EA },        

        // UTF-8 add to table
        
        // %01000000 to %01000111  UTF-8 add to table small length
        { 0x47, NISTRING_UTF8_SMALL_LENGTH },
        
        // %01001000  UTF-8 add to table medium length
        { 0x48, NISTRING_UTF8_MEDIUM_LENGTH },

        // %01001001 to %01001011 ILLEGAL
        { 0x4B, STATE_ILLEGAL },
        
        // %01001100  UTF-8 add to table large length
        { 0x4C, NISTRING_UTF8_LARGE_LENGTH },
        
        // %01001101 to %01001111 ILLEGAL
        { 0x4F, STATE_ILLEGAL },
        
        // UTF-16 add to table

        // %01010000 to %01010111  UTF-16 add to table small length
        { 0x57, NISTRING_UTF16_SMALL_LENGTH },
        
        // %01001000  UTF-16 add to table medium length
        { 0x58, NISTRING_UTF16_MEDIUM_LENGTH },

        // %01011001 to %01011011 ILLEGAL
        { 0x5B, STATE_ILLEGAL },
        
        // %01011100  UTF-16 add to table large length
        { 0x5C, NISTRING_UTF16_LARGE_LENGTH },
        
        // %01011101 to %01011111 ILLEGAL
        { 0x5F, STATE_ILLEGAL },
        
        // Restricted alphabet add to table
        
        // %01100000 to %01100111  RA add to table small length
        { 0x67, NISTRING_RA_SMALL_LENGTH },
        
        // %01101000  RA add to table medium length
        { 0x68, NISTRING_RA_MEDIUM_LENGTH },

        // %01101001 to %01101011 ILLEGAL
        { 0x6B, STATE_ILLEGAL },
        
        // %01101100  RA add to table large length
        { 0x6C, NISTRING_RA_LARGE_LENGTH },
        
        // %01101101 to %01101111 ILLEGAL
        { 0x6F, STATE_ILLEGAL },

        // Encoding algorithm add to table

        // %01110000 to %01111111  EA add to table
        { 0x7F, NISTRING_EA },
                        
        // Index

        // %10000000 to %10111111 index small
        { 0xBF, NISTRING_INDEX_SMALL },

        // %11000000 to %11011111 index medium
        { 0xDF, NISTRING_INDEX_MEDIUM },

        // %11100000 to %11101111 index large
        { 0xEF, NISTRING_INDEX_LARGE },

        // %11110000 to %11111110 ILLEGAL
        { 0xFE, STATE_ILLEGAL },

        // %11111111 Empty value
        { 0xFF, NISTRING_EMPTY },
    };

    
    public final static int ISTRING_SMALL_LENGTH        = 0;
    public final static int ISTRING_MEDIUM_LENGTH       = 1;
    public final static int ISTRING_LARGE_LENGTH        = 2;
    public final static int ISTRING_INDEX_SMALL         = 3;
    public final static int ISTRING_INDEX_MEDIUM        = 4;
    public final static int ISTRING_INDEX_LARGE         = 5;

    public static int[] ISTRING = new int[256];
    
    private static int[][] ISTRING_RANGES = {
        // %00000000 to %00111111 small length
        { 0x3F, ISTRING_SMALL_LENGTH },

        // %01000000 medium length
        { 0x40, ISTRING_MEDIUM_LENGTH },

        // %01000001 to %01011111 ILLEGAL
        { 0x5F, STATE_ILLEGAL },

        // %01100000 large length
        { 0x60, ISTRING_LARGE_LENGTH },

        // %01100001 to %01111111 ILLEGAL
        { 0x7F, STATE_ILLEGAL },

        // %10000000 to %10111111 index small
        { 0xBF, ISTRING_INDEX_SMALL },

        // %11000000 to %11011111 index medium
        { 0xDF, ISTRING_INDEX_MEDIUM },

        // %11100000 to %11101111 index large
        { 0xEF, ISTRING_INDEX_LARGE },

        // %11110000 to %11111111 ILLEGAL
        { 0xFF, STATE_ILLEGAL },
    };
    
    private static void constructTable(int[] table, int[][] ranges) {
        int start = 0x00;
        for (int range = 0; range < ranges.length; range++) {
            int end = ranges[range][RANGE_INDEX_END];
            int value = ranges[range][RANGE_INDEX_VALUE];
            for (int i = start; i<= end; i++) {
                table[i] = value;
            }
            start = end + 1;
        }
    }

    static {
        // EII
        constructTable(DII, DII_RANGES);

        // EII
        constructTable(EII, EII_RANGES);
        
        // AII
        constructTable(AII, AII_RANGES);        

        // AII Value
        constructTable(NISTRING, NISTRING_RANGES);        

        // Identifying string
        constructTable(ISTRING, ISTRING_RANGES);        
    }
    
    private DecoderStateTables() {
    }
}
