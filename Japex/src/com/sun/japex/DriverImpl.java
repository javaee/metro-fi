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

public class DriverImpl extends ParamsImpl implements Driver {
    
    /**
     * This driver's name.
     */
    String _name;
    
    /**
     * True if all other results should be normalized based
     * on this driver's results.
     */
    boolean _isNormal = false;
    
    /**
     * True means are already computed (avoids unnecessary 
     * re-computation).
     */
    boolean _computeMeans = true;
     
    /**
     * Java class implementing this driver.
     */
    Class _class = null;
    
    /**
     * Array of tests cases for this driver.
     */     
    TestCaseArrayList[] _testCases;
    
    /**
     * Aggregate results for this driver.
     */
    TestCaseArrayList _aggregateTestCases;
    
    public DriverImpl(String name, boolean isNormal, ParamsImpl params) {
        super(params);
        _name = name;
        _isNormal = isNormal;
    }
    
    public void setTestCases(TestCaseArrayList testCases) {
        int runsPerDriver = getIntParam(Constants.RUNS_PER_DRIVER);
        
        _testCases = new TestCaseArrayList[runsPerDriver];
        for (int i = 0; i < runsPerDriver; i++) {
            _testCases[i] = (TestCaseArrayList) testCases.clone();
        }        
        _aggregateTestCases = (TestCaseArrayList) testCases.clone();
    }
        
    private void computeMeans() {
        final int runsPerDriver = getIntParam(Constants.RUNS_PER_DRIVER);
        final int startRun = getBooleanParam(Constants.INCLUDE_WARMUP_RUN) ? 1 : 0;
        
        boolean scatter = getParam(Constants.CHART_TYPE).equals("scatterchart");
        
        // Avoid re-computing the driver's aggregates
        if (_computeMeans) {
            final int nOfTests = _testCases[0].size();

            for (int n = 0; n < nOfTests; n++) {
                double avgRunsResult = 0.0;

                double[] results = new double[runsPerDriver];
                double[] resultsX = new double[runsPerDriver];
                
                // Collect vertical results for this test
                for (int i = startRun; i < runsPerDriver; i++) {            
                    TestCaseImpl tc = (TestCaseImpl) _testCases[i].get(n);
                    results[i] = tc.getDoubleParam(Constants.RESULT_VALUE);
                    if (scatter) {
                        resultsX[i] = tc.getDoubleParam(Constants.RESULT_VALUE_X);
                    }
                }
                
                // Compute vertical average and stddev for this test
                TestCaseImpl tc = (TestCaseImpl) _aggregateTestCases.get(n);
                tc.setDoubleParam(Constants.RESULT_VALUE, 
                                  Util.arithmeticMean(results, startRun));
                if (scatter) {
                    tc.setDoubleParam(Constants.RESULT_VALUE_X, 
                                      Util.arithmeticMean(resultsX, startRun));                    
                }                
                if (runsPerDriver - startRun > 1) {
                    tc.setDoubleParam(Constants.RESULT_VALUE_STDDEV, 
                                      Util.standardDev(results, startRun));
                    if (scatter) {
                        tc.setDoubleParam(Constants.RESULT_VALUE_X_STDDEV, 
                                          Util.standardDev(resultsX, startRun));                        
                    }
                }
            }
            
            // geometric mean = (sum{i,n} x_i) / n
            double geomMeanresult = 1.0;
            // arithmetic mean = (prod{i,n} x_i)^(1/n)
            double aritMeanresult = 0.0;
            // harmonic mean inverse = sum{i,n} 1/(n * x_i)
            double harmMeanresultInverse = 0.0;
            
            // geometric mean = (sum{i,n} x_i) / n
            double geomMeanresultX = 1.0;
            // arithmetic mean = (prod{i,n} x_i)^(1/n)
            double aritMeanresultX = 0.0;
            // harmonic mean inverse = sum{i,n} 1/(n * x_i)
            double harmMeanresultXInverse = 0.0;
            
            // Compute horizontal means based on vertical means
            Iterator tci = _aggregateTestCases.iterator();
            while (tci.hasNext()) {
                TestCaseImpl tc = (TestCaseImpl) tci.next();       
                
                // Compute running means 
                double result = tc.getDoubleParam(Constants.RESULT_VALUE);
                aritMeanresult += result / nOfTests;
                geomMeanresult *= Math.pow(result, 1.0 / nOfTests);
                harmMeanresultInverse += 1.0 / (nOfTests * result);                
                if (scatter) {
                    double resultX = tc.getDoubleParam(Constants.RESULT_VALUE_X);
                    aritMeanresultX += resultX / nOfTests;
                    geomMeanresultX *= Math.pow(resultX, 1.0 / nOfTests);
                    harmMeanresultXInverse += 1.0 / (nOfTests * resultX);
                }
            }
            
            // Set driver-specific params
            setDoubleParam(Constants.RESULT_ARIT_MEAN, aritMeanresult);
            setDoubleParam(Constants.RESULT_GEOM_MEAN, geomMeanresult);
            setDoubleParam(Constants.RESULT_HARM_MEAN, 1.0 / harmMeanresultInverse);      
            if (scatter) {
                setDoubleParam(Constants.RESULT_ARIT_MEAN_X, aritMeanresultX);
                setDoubleParam(Constants.RESULT_GEOM_MEAN_X, geomMeanresultX);
                setDoubleParam(Constants.RESULT_HARM_MEAN_X, 1.0 / harmMeanresultXInverse);                      
            }
            
            // Avoid re-computing these means
            _computeMeans = false;
            
            // If number of runs is just 1, we're done
            if (runsPerDriver - startRun == 1) {
                return;
            }
            
            // geometric mean = (sum{i,n} x_i) / n
            geomMeanresult = 1.0;
            // arithmetic mean = (prod{i,n} x_i)^(1/n)
            aritMeanresult = 0.0;
            // harmonic mean inverse = sum{i,n} 1/(n * x_i)
            harmMeanresultInverse = 0.0;
            
            // geometric mean = (sum{i,n} x_i) / n
            geomMeanresultX = 1.0;
            // arithmetic mean = (prod{i,n} x_i)^(1/n)
            aritMeanresultX = 0.0;
            // harmonic mean inverse = sum{i,n} 1/(n * x_i)
            harmMeanresultXInverse = 0.0;
            
            // Compute horizontal stddevs based on vertical stddevs
            tci = _aggregateTestCases.iterator();
            while (tci.hasNext()) {
                TestCaseImpl tc = (TestCaseImpl) tci.next();       
                
                // Compute running means 
                double result = tc.getDoubleParam(Constants.RESULT_VALUE_STDDEV);
                aritMeanresult += result / nOfTests;
                geomMeanresult *= Math.pow(result, 1.0 / nOfTests);
                harmMeanresultInverse += 1.0 / (nOfTests * result);
                if (scatter) {
                    double resultX = tc.getDoubleParam(Constants.RESULT_VALUE_STDDEV);
                    aritMeanresultX += resultX / nOfTests;
                    geomMeanresultX *= Math.pow(resultX, 1.0 / nOfTests);
                    harmMeanresultXInverse += 1.0 / (nOfTests * resultX);
                }
            }
            
            // Set driver-specific params
            setDoubleParam(Constants.RESULT_ARIT_MEAN_STDDEV, aritMeanresult);
            setDoubleParam(Constants.RESULT_GEOM_MEAN_STDDEV, geomMeanresult);
            setDoubleParam(Constants.RESULT_HARM_MEAN_STDDEV, 1.0 / harmMeanresultInverse);            
            if (scatter) {
                setDoubleParam(Constants.RESULT_ARIT_MEAN_X_STDDEV, aritMeanresultX);
                setDoubleParam(Constants.RESULT_GEOM_MEAN_X_STDDEV, geomMeanresultX);
                setDoubleParam(Constants.RESULT_HARM_MEAN_X_STDDEV, 1.0 / harmMeanresultXInverse);                        
            }
        }        
    }
    
    public List getTestCases(int driverRun) {
        return _testCases[driverRun];
    }
    
    public List getAggregateTestCases() {
        computeMeans();  
        return _aggregateTestCases;
    }
    
    public String getName() {
        return _name;
    }    
    
    public boolean isNormal() {
        return _isNormal;
    }
    
    public void serialize(StringBuffer report, int spaces) {
        report.append(Util.getSpaces(spaces) 
            + "<driver name=\"" + _name + "\">\n");
        
       /*
         * Calling getAggregateTestCases() forces call to computeMeans(). This
         * is necessary before serializing driver params.
         */
        Iterator tci = getAggregateTestCases().iterator();
       
        // Serialize driver params
        super.serialize(report, spaces + 2);

        // Serialize each test case
        while (tci.hasNext()) {
            TestCaseImpl tc = (TestCaseImpl) tci.next();
            tc.serialize(report, spaces + 2);
        }            

        report.append(Util.getSpaces(spaces) + "</driver>\n");       
    }
    
}
