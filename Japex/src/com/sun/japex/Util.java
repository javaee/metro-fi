/*
 * Japex ver. 0.1 software ("Software")
 * 
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This Software is distributed under the following terms:
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc., 'Java', 'Java'-based names,
 * nor the names of contributors may be used to endorse or promote products
 * derived from this Software without specific prior written permission.
 * 
 * The Software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE
 * AS A RESULT OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE
 * LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED
 * AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

package com.sun.japex;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class Util {
    
    static final int KB = 1024;
    static final String spaces = "                                        ";
    
    static Method currentTime;
    static boolean unitIsMillis;
    
    static {
        // Use nanoTime() if available - JDK 1.5
        try {
            currentTime = System.class.getMethod("nanoTime", null);
            unitIsMillis = false;
        }
        catch (NoSuchMethodException e) {
            try {
                currentTime = System.class.getMethod("currentTimeMillis", null);
                unitIsMillis = true;
            }
            catch (NoSuchMethodException ee) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    
    /** Creates a new instance of Util */
    public Util() {
    }

    static String getSpaces(int length) {
        return spaces.substring(0, length);
    }
    
    static public byte[] streamToByteArray(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8 * KB);
        int c;
        try {
            while ((c = is.read()) != -1) {
                bos.write(c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }
        
    public static long parseDuration(String duration) {
        try {
            int length = duration.length();
            switch (length) {
                case 1:
                case 2:
                    // S?S
                    return Integer.parseInt(duration.substring(0, length))
                           * 1000;    
                case 5:
                    if (duration.charAt(2) == ':') { 
                        // MM:SS
                        return Integer.parseInt(duration.substring(0, 2))
                               * 60 * 1000 +
                               Integer.parseInt(duration.substring(3, 5))
                               * 1000;    
                    }
                    break;
                case 8:
                    // HH:MM:SS
                    if (duration.charAt(2) == ':' && duration.charAt(5) == ':') { 
                        return Integer.parseInt(duration.substring(0, 2)) 
                               * 60 * 60 * 1000 +
                               Integer.parseInt(duration.substring(3, 5))
                               * 60 * 1000 +
                               Integer.parseInt(duration.substring(6, 8))
                               * 1000;    
                    }
                    break;
            }
        }
        catch (NumberFormatException e) {
            // Falls through
        }
        throw new RuntimeException("Duration '" + duration 
            + "' does not conform to pattern '((HH:)?MM:)?SS'");
    }        
    
    public static long currentTimeNanos() {
        try {
            long t = ((Number) currentTime.invoke(null, null)).longValue();
            return unitIsMillis ? millisToNanos(t) : t;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return 0;
    }
    
    public static long currentTimeMillis() {
        try {
            long t = ((Number) currentTime.invoke(null, null)).longValue();
            return unitIsMillis ? t : (long) nanosToMillis(t);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return 0;
    }
    
    public static long millisToNanos(long millis) {
        return millis * 1000000L;
    }
    
    public static double nanosToMillis(long nanos) {
        return nanos / 1000000.0;
    }
    
    public static double standardDev(double[] sample) {
        // Compute sample's mean
        double mean = 0.0;
        for (int i = 0; i < sample.length; i++) {
            mean += sample[i];
        }
        mean /= sample.length;
        
        // Compute biased variance
        double variance = 0.0;
        for (int i = 0; i < sample.length; i++) {
            variance += (sample[i] - mean) * (sample[i] - mean);
        }
        variance /= sample.length;
        
        // Return standard deviation
        return Math.sqrt(variance);
    }
    
}
