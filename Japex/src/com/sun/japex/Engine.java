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
import java.net.*;
import java.io.File;
import java.util.concurrent.*;

public class Engine {
    
    /**
     * The test suite being executed by this engine.
     */
    TestSuiteImpl _testSuite;

    /**
     * Thread pool used for the test's execution.
     */
    ExecutorService _threadPool;
    
    /**
     * Matrix of driver instances of size nOfThreads * runsPerDriver.
     */
    JapexDriverBase _drivers[][];
    
    /**
     * Current driver being executed.
     */
    DriverImpl _driverImpl;
    
    /**
     * Current driver run being executed.
     */
    int _driverRun;    
    
    /**
     * Used to check if all drivers (or no driver) compute a result.
     */
    Boolean _computeResult = null;
    
    /**
     * Running geometric mean = (sum{i,n} x_i) / n
     */
    double _geomMeanresult = 1.0;
    
    /**
     * Running arithmetic mean = (prod{i,n} x_i)^(1/n)
     */
    double _aritMeanresult = 0.0;
    
    /**
     * Harmonic mean inverse = sum{i,n} 1/(n * x_i)
     */
    double _harmMeanresultInverse = 0.0;
    
    public Engine() {
    }
    
    public TestSuiteImpl start(String configFile) {
        try { 
            // Load config file
            ConfigFileLoader cfl = new ConfigFileLoader(configFile);
            _testSuite = cfl.getTestSuite();

            // Print estimated running time
            if (_testSuite.hasParam(Constants.WARMUP_TIME) && 
                    _testSuite.hasParam(Constants.RUN_TIME)) 
            {
                int[] hms = estimateRunningTime(_testSuite);
                System.out.println("Estimated warmup time + run time is " +
                    (hms[0] > 0 ? (hms[0] + " hours ") : "") +
                    (hms[1] > 0 ? (hms[1] + " minutes ") : "") +
                    (hms[2] > 0 ? (hms[2] + " seconds ") : ""));                    
            }

            // Allocate a cached thread pool
            _threadPool = Executors.newCachedThreadPool();
                
            forEachDriver();                  
            
            // Shutdown thread pool -- all threads must have stopped by now
            _threadPool.shutdown();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return _testSuite;
    }        

    private void forEachDriver() {
        try {
            // Iterate through each driver
            Iterator jdi = _testSuite.getDriverInfoList().iterator();
            while (jdi.hasNext()) {                               
                _driverImpl = (DriverImpl) jdi.next();

                int nOfThreads = _driverImpl.getIntParam(Constants.NUMBER_OF_THREADS);
                int runsPerDriver = _driverImpl.getIntParam(Constants.RUNS_PER_DRIVER);
                boolean includeWarmupRun = _driverImpl.getBooleanParam(Constants.INCLUDE_WARMUP_RUN);

                // Display driver's name
                System.out.print("  " + _driverImpl.getName() + " using " + nOfThreads + " thread(s)");

                // Allocate a matrix of nOfThreads * runPerDriver size and initialize each instance
                _drivers = new JapexDriverBase[nOfThreads][runsPerDriver];
                for (int i = 0; i < nOfThreads; i++) {
                    for (int j = 0; j < runsPerDriver; j++) {
                        _drivers[i][j] = _driverImpl.getJapexDriver();   // returns fresh copy
                        _drivers[i][j].setDriver(_driverImpl);
                        _drivers[i][j].setTestSuite(_testSuite);
                        _drivers[i][j].initializeDriver();
                    }
                }

                forEachRun();
                
                // Call terminate on all driver instances
                for (int i = 0; i < nOfThreads; i++) {
                    for (int j = 0; j < runsPerDriver; j++) {
                        _drivers[i][j].terminateDriver();
                    }
                }                
                
            }   
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void forEachRun() {
        try {
            int runsPerDriver = _driverImpl.getIntParam(Constants.RUNS_PER_DRIVER);
            boolean includeWarmupRun = _driverImpl.getBooleanParam(Constants.INCLUDE_WARMUP_RUN);
            
            for (_driverRun = 0; _driverRun < runsPerDriver; _driverRun++) {
                if (includeWarmupRun) {
                    System.out.print(_driverRun == 0 ? "\n    Warmup run: "
                        : "\n    Run " + _driverRun + ": ");
                }
                else {
                    System.out.print("\n    Run " + (_driverRun + 1) + ": ");                        
                }

                // geometric mean = (sum{i,n} x_i) / n
                _geomMeanresult = 1.0;
                // arithmetic mean = (prod{i,n} x_i)^(1/n)
                _aritMeanresult = 0.0;
                // harmonic mean inverse = sum{i,n} 1/(n * x_i)
                _harmMeanresultInverse = 0.0;

                forEachTestCase();

                System.out.print(
                    "aritmean," + Util.formatDouble(_aritMeanresult) +
                    ",geommean," + Util.formatDouble(_geomMeanresult) +
                    ",harmmean," + Util.formatDouble(1.0 / _harmMeanresultInverse));

                int startRun = _driverImpl.getBooleanParam(Constants.INCLUDE_WARMUP_RUN) ? 1 : 0;
                if (runsPerDriver - startRun > 1) {
                    // Print average for all runs
                    System.out.print("\n     Avgs: ");
                    Iterator tci = _driverImpl.getAggregateTestCases().iterator();
                    while (tci.hasNext()) {
                        TestCaseImpl tc = (TestCaseImpl) tci.next();
                        System.out.print(tc.getName() + ",");                        
                        System.out.print(
                            Util.formatDouble(tc.getDoubleParam(Constants.RESULT_VALUE)) 
                            + ",");
                    }
                    System.out.print(
                        "aritmean," +
                        _driverImpl.getParam(Constants.RESULT_ARIT_MEAN) + 
                        ",geommean," +
                        _driverImpl.getParam(Constants.RESULT_GEOM_MEAN) + 
                        ",harmmean," +
                        _driverImpl.getParam(Constants.RESULT_HARM_MEAN));   

                    // Print standardDevs for all runs
                    System.out.print("\n    Stdev: ");
                    tci = _driverImpl.getAggregateTestCases().iterator();
                    while (tci.hasNext()) {
                        TestCaseImpl tc = (TestCaseImpl) tci.next();
                        System.out.print(tc.getName() + ",");                        
                        System.out.print(
                            Util.formatDouble(tc.getDoubleParam(Constants.RESULT_VALUE_STDDEV)) 
                            + ",");
                    }
                    System.out.println(
                        "aritmean," +
                        _driverImpl.getParam(Constants.RESULT_ARIT_MEAN_STDDEV) + 
                        ",geommean," +
                        _driverImpl.getParam(Constants.RESULT_GEOM_MEAN_STDDEV) + 
                        ",harmmean," +
                        _driverImpl.getParam(Constants.RESULT_HARM_MEAN_STDDEV));   
                }
                else {
                    System.out.println("");
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void forEachTestCase() {
        try {
            int nOfThreads = _driverImpl.getIntParam(Constants.NUMBER_OF_THREADS);
            
            // Get list of tests
            List tcList = _driverImpl.getTestCases(_driverRun);
            int nOfTests = tcList.size();

            // Iterate through list of test cases
            Iterator tci = tcList.iterator();
            while (tci.hasNext()) {
                long runTime = 0L;
                TestCaseImpl tc = (TestCaseImpl) tci.next();               

                if (Japex.verbose) {
                    System.out.println(tc.getName());
                }
                else {
                    System.out.print(tc.getName() + ",");
                }

                // If nOfThreads == 1, re-use this thread
                if (nOfThreads == 1) {
                    // -- Prepare phase --------------------------------------

                    _drivers[0][_driverRun].setTestCase(tc);     // tc is shared!
                    _drivers[0][_driverRun].prepare();

                    // -- Warmup phase ---------------------------------------

                    // Start timer 
                    runTime = Util.currentTimeMillis();

                    // First time call does warmup
                    _drivers[0][_driverRun].call();

                    // Stop timer
                    runTime = Util.currentTimeMillis() - runTime;

                    // Set japex.actualWarmupTime output param
                    tc.setDoubleParam(Constants.ACTUAL_WARMUP_TIME, runTime);  

                    // -- Run phase -------------------------------------------

                    // Start timer for run phase
                    runTime = Util.currentTimeMillis();

                    // Second time call does run
                    _drivers[0][_driverRun].call();
                }
                else {  // nOfThreads > 1
                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

                    // -- Prepare phase --------------------------------------

                    // Initialize driver instance with test case object do prepare
                    for (int i = 0; i < nOfThreads; i++) {
                        _drivers[i][_driverRun].setTestCase(tc);     // tc is shared!
                        _drivers[i][_driverRun].prepare();
                    }

                    // -- Warmup phase ---------------------------------------

                    // Start timer for warmup phase 
                    runTime = Util.currentTimeMillis();

                    // Fork all threads -- first time drivers will warmup
                    Future<?>[] futures = new Future<?>[nOfThreads];                            
                    for (int i = 0; i < nOfThreads; i++) {
                        futures[i] = _threadPool.submit(_drivers[i][_driverRun]);
                    }

                    // Wait for all threads to finish
                    for (int i = 0; i < nOfThreads; i++) {
                        futures[i].get();
                    }

                    // Stop timer
                    runTime = Util.currentTimeMillis() - runTime;

                    // Set japex.actualWarmupTime output param
                    tc.setDoubleParam(Constants.ACTUAL_WARMUP_TIME, runTime);  

                    // -- Run phase -------------------------------------------

                    // Start timer for run phase
                    runTime = Util.currentTimeMillis();

                    // Fork all threads -- second time drivers will run
                    for (int i = 0; i < nOfThreads; i++) {
                        futures[i] = _threadPool.submit(_drivers[i][_driverRun]);
                    }

                    // Wait for all threads to finish
                    for (int i = 0; i < nOfThreads; i++) {
                        futures[i].get();
                    }
                }

                // Stop timer
                runTime = Util.currentTimeMillis() - runTime;

                // Set japex.actualRunTime output param
                tc.setDoubleParam(Constants.ACTUAL_RUN_TIME, runTime);  

                // Finish phase                         
                for (int i = 0; i < nOfThreads; i++) {
                    _drivers[i][_driverRun].finish();
                }                    

                double result = 0.0;

                if (_computeResult == null) {
                    if (!tc.hasParam(Constants.RESULT_VALUE)) {
                        result = 
                            tc.getIntParam(Constants.ACTUAL_RUN_ITERATIONS) / 
                                (runTime / 1000.0);
                        _testSuite.setParam(Constants.RESULT_UNIT, "TPS");

                        _computeResult = Boolean.TRUE;
                        tc.setDoubleParam(Constants.RESULT_VALUE, result);
                    }
                    else {
                        result = tc.getDoubleParam(Constants.RESULT_VALUE);
                        _computeResult = Boolean.FALSE;
                    }                        
                }
                else if (_computeResult == Boolean.TRUE
                         && !tc.hasParam(Constants.RESULT_VALUE))  
                {
                    result = 
                        tc.getIntParam(Constants.ACTUAL_RUN_ITERATIONS) / 
                            (runTime / 1000.0);
                    tc.setDoubleParam(Constants.RESULT_VALUE, result);
                }
                else if (_computeResult == Boolean.FALSE
                         && tc.hasParam(Constants.RESULT_VALUE)) 
                {
                    result = tc.getDoubleParam(Constants.RESULT_VALUE);
                }
                else {
                    throw new RuntimeException(
                       "The output parameter '" + Constants.RESULT_VALUE
                       + "' must be computed by either all or none of "
                       + "the drivers for the results to be comparable");
                }            

                // Compute running means 
                _aritMeanresult += result / nOfTests;
                _geomMeanresult *= Math.pow(result, 1.0 / nOfTests);
                _harmMeanresultInverse += 1.0 / (nOfTests * result);

                // Display results for this test
                if (Japex.verbose) {
                    System.out.println("           " + tc.getParam(Constants.RESULT_VALUE));
                    System.out.print("           ");
                }
                else {
                    System.out.print(tc.getParam(Constants.RESULT_VALUE) + ",");
                    System.out.flush();
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }
    
    
    /**
     * Calculates the time of the warmup and run phases. Returns an array 
     * of size 3 with hours, minutes and seconds. Note: if japex.runsPerDriver
     * is redefined by any driver, this estimate will be off.
     */
    private int[] estimateRunningTime(TestSuiteImpl testSuite) {        
        int nOfDrivers = testSuite.getDriverInfoList().size();
        int nOfTests = ((DriverImpl) testSuite.getDriverInfoList().get(0)).getTestCases(0).size();
    
        String runTime = testSuite.getParam(Constants.RUN_TIME);
        String warmupTime = testSuite.getParam(Constants.WARMUP_TIME);

        long seconds = (long)
            (nOfDrivers * nOfTests * (Util.parseDuration(warmupTime) / 1000.0) +
            nOfDrivers * nOfTests * (Util.parseDuration(runTime) / 1000.0)) *
            testSuite.getIntParam(Constants.RUNS_PER_DRIVER);     
        
        int[] hms = new int[3];
        hms[0] = (int) (seconds / 60 / 60);
        hms[1] = (int) ((seconds / 60) % 60);
        hms[2] = (int) (seconds % 60);
        return hms;
    }
}
