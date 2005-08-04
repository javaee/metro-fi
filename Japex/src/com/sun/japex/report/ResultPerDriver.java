/*
 * ResultPerDriver.java
 *
 * Created on April 10, 2005, 6:12 PM
 */

package com.sun.japex.report;
import java.util.Map;
import java.util.HashMap;

/**
 * Test results per driver
 * 
 */
public class ResultPerDriver {    
    double _harmMean;
    double _geomMean;
    double _aritMean;
    Map _testResults;
    
    /** Creates a new instance of ReportPerDriver */
    public ResultPerDriver() {
        _testResults = new HashMap();
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
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        
    }
    public double getResult(String testName) {
        if (!_testResults.containsKey(testName)) return 0;
        double value = ((Double)_testResults.get(testName)).doubleValue(); 
        return value;
    }
    
}
