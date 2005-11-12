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

import java.util.*;
import java.text.*;


public class TestSuiteImpl extends ParamsImpl implements TestSuite {
    
    String _name;
    List _driverInfo = new ArrayList();
    
    /**
     * Creates a new instance of TestSuiteImpl from a JAXB-generated
     * object. In essence, this constructor implements a mapping
     * between the JAXB object model and the internal object model
     * used in Japex.
     */
    public TestSuiteImpl(com.sun.japex.testsuite.TestSuite ts) {
        _name = ts.getName();
        
        // Set global properties by traversing JAXB's model
        List params = ts.getParam();
        final String pathSep = System.getProperty("path.separator");
        List classPathURLs = new ArrayList();
        
        if (params != null) {
            Iterator it = params.iterator();
            while (it.hasNext()) {
                com.sun.japex.testsuite.ParamType pt = 
                    (com.sun.japex.testsuite.ParamType) it.next();
                String name = pt.getName();
                String value = pt.getValue();
                String oldValue = getParam(name);
                
                // If japex.classPath, append to existing value
                setParam(name, 
                    name.equals(Constants.CLASS_PATH) && oldValue != null ?
                    (oldValue + pathSep + value) : value);
            }
        }
        
        // Override config props using system properties
        readAndSetSystemProperties();
        
        // Set default global params if necessary
        if (!hasParam(Constants.WARMUP_TIME) && 
            !hasParam(Constants.WARMUP_ITERATIONS))
        {
            setParam(Constants.WARMUP_ITERATIONS, 
                     Constants.DEFAULT_WARMUP_ITERATIONS);    
        }
        if (!hasParam(Constants.RUN_TIME) && 
            !hasParam(Constants.RUN_ITERATIONS))
        {
            setParam(Constants.RUN_ITERATIONS, 
                     Constants.DEFAULT_RUN_ITERATIONS);    
        }        
        
        // Check output directory
        if (!hasParam(Constants.REPORTS_DIRECTORY)) {
            setParam(Constants.REPORTS_DIRECTORY, 
                     Constants.DEFAULT_REPORTS_DIRECTORY);    
        }
        
        // Check chart type
        if (!hasParam(Constants.CHART_TYPE)) {
            setParam(Constants.CHART_TYPE, 
                     Constants.DEFAULT_CHART_TYPE);    
        }
        else {
            String chartType = getParam(Constants.CHART_TYPE);
            if (!chartType.equalsIgnoreCase("barchart") && 
                !chartType.equalsIgnoreCase("scatterchart") &&
                !chartType.equalsIgnoreCase("linechart")) 
            {
                throw new RuntimeException(
                    "Parameter 'japex.chartType' must be set to " +
                    "'barchart', 'scatterchart' or 'linechart'");
            }
        }
        
        // Check result axis
        if (!checkResultAxis(Constants.RESULT_AXIS) ||
            !checkResultAxis(Constants.RESULT_AXIS_X))
        {
                throw new RuntimeException(
                    "Parameter 'japex.resultAxis' and 'japex.resultAxisX' " +
                    "must be set to either 'normal' or 'logarithmic'");            
        }
        
        // Check number of threads 
        if (!hasParam(Constants.NUMBER_OF_THREADS)) {
            setParam(Constants.NUMBER_OF_THREADS, 
                     Constants.DEFAULT_NUMBER_OF_THREADS);    
        }
        else {
            int nOfThreads = getIntParam(Constants.NUMBER_OF_THREADS);
            if (nOfThreads < 1) {
                throw new RuntimeException(
                    "Parameter 'japex.numberOfThreads' must be at least 1");
            }
        }
        
        // Check runs per driver
        if (!hasParam(Constants.RUNS_PER_DRIVER)) {
            setParam(Constants.RUNS_PER_DRIVER, 
                     Constants.DEFAULT_RUNS_PER_DRIVER);    
        }
        int runsPerDriver = getIntParam(Constants.RUNS_PER_DRIVER);
        if (runsPerDriver < 1) {
            throw new RuntimeException(
                "Parameter 'japex.runsPerDriver' must be at least 1");
        }
        
        // Check include warmup run - default true if runsPerDriver > 1
        boolean includeWarmupRun = (runsPerDriver > 1);
        if (!hasParam(Constants.INCLUDE_WARMUP_RUN)) {
            setBooleanParam(Constants.INCLUDE_WARMUP_RUN, includeWarmupRun); 
        }
        else {
            includeWarmupRun = getBooleanParam(Constants.INCLUDE_WARMUP_RUN);
        }
        // Increment runsPerDriver to accomodate warmup run
        if (includeWarmupRun) {
            setIntParam(Constants.RUNS_PER_DRIVER, runsPerDriver + 1);
        }
        
        // Set other global params
        setParam(Constants.VERSION, Constants.VERSION_VALUE);
        setParam(Constants.OS_NAME, System.getProperty("os.name"));
        setParam(Constants.OS_ARCHITECTURE, System.getProperty("os.arch"));
        DateFormat df = new SimpleDateFormat("dd MMM yyyy/HH:mm:ss z");
        setParam(Constants.DATE_TIME, df.format(Japex.TODAY));
        setParam(Constants.VM_INFO,
            System.getProperty("java.vendor") + " " + 
            System.getProperty("java.vm.version"));
        
        // Create and populate list of drivers
        Iterator it = ts.getDriver().iterator();
        while (it.hasNext()) {
            com.sun.japex.testsuite.TestSuite.Driver dt = 
                (com.sun.japex.testsuite.TestSuite.Driver) it.next();
            
            // Create new DriverImpl
            DriverImpl driverInfo = new DriverImpl(dt.getName(), 
                dt.isNormal(), this);
            
            // Copy params from JAXB object to Japex object
            Iterator driverParamsIt = dt.getParam().iterator();
            while (driverParamsIt.hasNext()) {
                com.sun.japex.testsuite.ParamType pt = 
                    (com.sun.japex.testsuite.ParamType) driverParamsIt.next();
                driverInfo.setParam(pt.getName(), pt.getValue());
            }

            // If japex.driverClass not specified, use the driver's name
            if (!driverInfo.hasParam(Constants.DRIVER_CLASS)) {
                driverInfo.setParam(Constants.DRIVER_CLASS, dt.getName());
            }          

            _driverInfo.add(driverInfo);
        }
        
        // Create and populate list of test cases
        TestCaseArrayList testCases = new TestCaseArrayList();
        it = ts.getTestCase().iterator();
        while (it.hasNext()) {
            com.sun.japex.testsuite.TestSuite.TestCase tc = 
                (com.sun.japex.testsuite.TestSuite.TestCase) it.next();
            
            // Create new TestCaseImpl
            TestCaseImpl testCase = new TestCaseImpl(tc.getName(), this);
            
            // Copy params from JAXB object to Japex object
            Iterator itParams = tc.getParam().iterator();
            while (itParams.hasNext()) {
                com.sun.japex.testsuite.ParamType pt = 
                    (com.sun.japex.testsuite.ParamType) itParams.next();
                testCase.setParam(pt.getName(), pt.getValue());
            }
            
            testCases.add(testCase);
        }
        
        // Set list of test cases and number of runs on each driver
        it = _driverInfo.iterator();
        while (it.hasNext()) {
            DriverImpl di = (DriverImpl) it.next();
            di.setTestCases(testCases);
        }
    }
    
    private boolean checkResultAxis(String paramName) {
        if (hasParam(paramName)) {
            String value = getParam(paramName);
            return value.equalsIgnoreCase("normal") ||
                   value.equalsIgnoreCase("logarithmic");
        }
        else {
            setParam(paramName, Constants.DEFAULT_RESULT_AXIS);
        }
        return true;
    }
    
    /**
     * System properties that start with "japex." can be used to override
     * global params of the same name from the config file. If the value
     * of the system property is "", then it is ignored.
     */
    private void readAndSetSystemProperties() {
        Properties sysProps = System.getProperties();
        
        for (Iterator i = sysProps.keySet().iterator(); i.hasNext();) {
            String name = (String) i.next();
            if (name.startsWith("japex.")) {
                String value = sysProps.getProperty(name);
                if (value.length() > 0) {
                    setParam(name, value);
                }
            }
        }
    }
    
    public String getName() {
        return _name;        
    }
    
    public List getDriverInfoList() {
        return _driverInfo;
    }
    
    public void serialize(StringBuffer report) {
        report.append("<testSuiteReport name=\"" + _name 
            + "\" xmlns=\"http://www.sun.com/japex/testSuiteReport\">\n");      

        serialize(report, 2);
        
        // Iterate through each class (aka driver)
        Iterator jdi = _driverInfo.iterator();
        while (jdi.hasNext()) {
            DriverImpl di = (DriverImpl) jdi.next();
            di.serialize(report, 2);
        }
                    
        report.append("</testSuiteReport>\n");
    }
    
}
