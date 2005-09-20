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

package com.sun.japex.report;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
/**
 * Test results per driver
 * 
 */
public class ResultPerDriver {    
    double _harmMean;
    double _geomMean;
    double _aritMean;
    Map _testResults;
    ArrayList _tests = new ArrayList();
    /** Creates a new instance of ReportPerDriver */
    public ResultPerDriver() {
        _testResults = new HashMap();
    }
    
    public double getMeans(String key) {
        if (key.equalsIgnoreCase(ReportConstants.ARITHMETIC_MEANS)) {
            return _aritMean;
        } else if (key.equalsIgnoreCase(ReportConstants.GEOMETIC_MEANS)) {
            return _geomMean;
        } else if (key.equalsIgnoreCase(ReportConstants.GEOMETIC_MEANS)) {
            return _harmMean;
        }
        return 0;
    }
    public void setHarmMean(String value) {
        try {
            _harmMean = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }    
    public double getHarmMean() {
        return _harmMean;
    }
    
    public void setGeomMean(String value) {
        try {
            _geomMean = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }
    public double getGeomMean() {
        return _geomMean;
    }
    
    public void setAritMean(String value) {
        try {
            _aritMean = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }
    public double getAritMean() {
        return _aritMean;
    }
    
    public void addResult(String testName, String value) {
        try {
            _testResults.put(testName, new Double(Double.parseDouble(value)));
            _tests.add(testName);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        
    }
    public double getResult(String testName) {
        if (!_testResults.containsKey(testName)) return 0;
        double value = ((Double)_testResults.get(testName)).doubleValue(); 
        return value;
    }
    
    public String[] getTests() {
        String[] tests = new String[(_tests.size())];
        _tests.toArray(tests);
        return tests;
    }
}
