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
import java.text.DecimalFormat;
import java.net.URL;
import java.net.URLClassLoader;

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
    
    static public ByteArrayInputStream streamToByteArrayInputStream(InputStream is) {
        return new ByteArrayInputStream(streamToByteArray(is));
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
    
    public static double arithmeticMean(double[] sample) {
        return arithmeticMean(sample, 0);
    }
    
    public static double arithmeticMean(double[] sample, int start) {
        double mean = 0.0;
        for (int i = start; i < sample.length; i++) {
            mean += sample[i];
        }
        return (mean / (sample.length - start));
    }
    
    public static double standardDev(double[] sample) {
        return standardDev(sample, 0);
    }
    
    public static double standardDev(double[] sample, int start) {
        double mean = arithmeticMean(sample, start);

        // Compute biased variance
        double variance = 0.0;
        for (int i = start; i < sample.length; i++) {
            variance += (sample[i] - mean) * (sample[i] - mean);
        }
        variance /= (sample.length - start);
        
        // Return standard deviation
        return Math.sqrt(variance);
    }
    
    static DecimalFormat _decimalFormat = new DecimalFormat("0.000");
    
    public static String formatDouble(double value) {
        return _decimalFormat.format(value);   
    }
    
    public static String getManifestAsString(URLClassLoader cl, String jarBaseName) {
        try {
            Enumeration<URL> e = ((URLClassLoader) cl).findResources("META-INF/MANIFEST.MF");
            
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                String urlString = url.toString();
                
                // Have we found the right jar?
                if (urlString.indexOf(jarBaseName) > 0) {
                    StringBuilder sb = new StringBuilder();
                    int c;
                    InputStream is = url.openStream();
                    while ((c = is.read()) != -1) {
                        char ch = (char) c;
                        sb.append(Character.isWhitespace(ch) ? ' ' : ch);
                    }
                    return sb.toString();
                }
            }
            return "";
        } 
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    /**
     * Calculate group sizes for tests to avoid a very small final group. 
     * For example, calculateGroupSizes(21, 5) return { 5,5,5,3,3 } instead
     * of { 5,5,5,5,1 }.
     */
    public static int[] calculateGroupSizes(int nOfTests, int maxGroupSize) {
        if (nOfTests <= maxGroupSize) {
            return new int[] { nOfTests };
        }
        
        int[] result = new int[nOfTests / maxGroupSize + 
                               ((nOfTests % maxGroupSize > 0) ? 1 : 0)];
        
        // Var m1 represents the number of groups of size maxGroupSize
        int m1 = (nOfTests - maxGroupSize) / maxGroupSize;
        for (int i = 0; i < m1; i++) {
            result[i] = maxGroupSize;
        }
        
        // Var m2 represents the number of tests not allocated into groups
        int m2 = nOfTests - m1 * maxGroupSize;
        if (m2 <= maxGroupSize) {
            result[result.length - 1] = m2;
        }
        else {
            // Allocate last two groups
            result[result.length - 2] = (int) Math.ceil(m2 / 2.0);            
            result[result.length - 1] = m2 - result[result.length - 2];
        }
        return result;
    }
    
    //return a legal filename from a testcase title
    public static String getFilename(String testcase) {
        StringBuffer filename = new StringBuffer();
        for (int i=0; i<testcase.length(); i++) {
            char achar = testcase.charAt(i);
            if (achar == 46) { //.
                char nchar = testcase.charAt(i+1);
                filename.append("_");
                if (nchar == 47 || nchar == 92) {  //./
                    i++;
                }
            } else if (achar == 47 || achar == 92 || achar == 32) { // / \ and space
                filename.append("_");                
            } else {
                filename.append(achar);
            }
        }
        return filename.toString();
    }
    
}
