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

public class Engine {
    
    public Engine() {
    }
    
    public TestSuite start(String configFile) {
        TestSuite testSuite = null;
        Boolean computeResult = null;
                
        try {
            // Load config file
            ConfigFileLoader cfl = new ConfigFileLoader(configFile);
            testSuite = cfl.getTestSuite();
            
            // Iterate through each driver
            Iterator jdi = testSuite.getDriverInfoList().iterator();
            while (jdi.hasNext()) {                               
                DriverInfo di = (DriverInfo) jdi.next();
                
                // Display driver's name
                System.out.print("  " + di.getName());
                
                int runsPerDriver = testSuite.getIntParam(Constants.RUNS_PER_DRIVER);

                // Allocate a matrix of nOfThreads * runPerDriver size and initialize each instance
                int nOfThreads = testSuite.getIntParam(Constants.NUMBER_OF_THREADS);
                JapexDriverBase drivers[][] = new JapexDriverBase[nOfThreads][runsPerDriver];
                for (int i = 0; i < nOfThreads; i++) {
                    for (int j = 0; j < runsPerDriver; j++) {
                        drivers[i][j] = di.getJapexDriver();   // returns fresh copy
                        drivers[i][j].setTestSuite(testSuite);
                        drivers[i][j].initializeDriver();
                    }
                }
                
                for (int driverRun = 0; driverRun < runsPerDriver; driverRun++) {
                    System.out.print("\n    Run " + (driverRun + 1) + ": ");
                    
                    // Get list of tests
                    List tcList = di.getTestCases(driverRun);
                    int nOfTests = tcList.size();
                    
                    // geometric mean = (sum{i,n} x_i) / n
                    double geomMeanresult = 1.0;
                    // arithmetic mean = (prod{i,n} x_i)^(1/n)
                    double aritMeanresult = 0.0;
                    // harmonic mean inverse = sum{i,n} 1/(n * x_i)
                    double harmMeanresultInverse = 0.0;
                                
                    // Iterate through list of test cases
                    Iterator tci = tcList.iterator();
                    while (tci.hasNext()) {
                        long runTime = 0L;
                        TestCase tc = (TestCase) tci.next();               

                        System.out.print(tc.getTestName() + ",");

                        // If nOfThreads == 1, re-use this thread
                        if (nOfThreads == 1) {
                            drivers[0][driverRun].setTestCase(tc);     // tc is shared!
                            drivers[0][driverRun].prepareAndWarmup();

                            // Start timer 
                            runTime = Util.currentTimeMillis();

                            drivers[0][driverRun].run();
                        }
                        else {  // nOfThreads > 1
                            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

                            // Create one thread for each driver instance
                            Thread threads[] = new Thread[nOfThreads];
                            for (int i = 0; i < nOfThreads; i++) {
                                threads[i] = new Thread(drivers[i][driverRun]);
                                threads[i].setPriority(Thread.MAX_PRIORITY);
                            }

                            // Initialize driver instance with test case object
                            // and do prepare and warmup phases
                            for (int i = 0; i < nOfThreads; i++) {
                                drivers[i][driverRun].setTestCase(tc);     // tc is shared!
                                drivers[i][driverRun].prepareAndWarmup();
                            }

                            // Start timer 
                            runTime = Util.currentTimeMillis();

                            // Fork all threads
                            for (int i = 0; i < nOfThreads; i++) {
                                threads[i].start();
                            }

                            // Wait for all threads to finish
                            for (int i = 0; i < nOfThreads; i++) {
                                threads[i].join();
                            }
                        }

                        // Stop timer
                        runTime = Util.currentTimeMillis() - runTime;

                        // Set japex.actualRunTime output param
                        if (!testSuite.hasParam(Constants.RUN_TIME)) {
                            tc.setDoubleParam(Constants.ACTUAL_RUN_TIME, runTime);  
                        }

                        // Do finish phase
                        for (int i = 0; i < nOfThreads; i++) {
                            drivers[i][driverRun].finish(tc);
                        }                    

                        double result = 0.0;

                        if (computeResult == null) {
                            if (!tc.hasParam(Constants.RESULT_VALUE)) {
                                result = 
                                    tc.getIntParam(Constants.ACTUAL_RUN_ITERATIONS) / 
                                        (runTime / 1000.0);
                                testSuite.setParam(Constants.RESULT_UNIT, "TPS");

                                computeResult = Boolean.TRUE;
                                tc.setDoubleParam(Constants.RESULT_VALUE, result);
                            }
                            else {
                                result = tc.getDoubleParam(Constants.RESULT_VALUE);
                                computeResult = Boolean.FALSE;
                            }                        
                        }
                        else if (computeResult == Boolean.TRUE
                                 && !tc.hasParam(Constants.RESULT_VALUE))  
                        {
                            result = 
                                tc.getIntParam(Constants.ACTUAL_RUN_ITERATIONS) / 
                                    (runTime / 1000.0);
                            tc.setDoubleParam(Constants.RESULT_VALUE, result);
                        }
                        else if (computeResult == Boolean.FALSE
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
                        aritMeanresult += result / nOfTests;
                        geomMeanresult *= Math.pow(result, 1.0 / nOfTests);
                        harmMeanresultInverse += 1.0 / (nOfTests * result);
                    
                        // Display results for this test
                        System.out.print(tc.getDoubleParam(Constants.RESULT_VALUE) + ",");
                        System.out.flush();
                    }
                
                
                    System.out.print(
                        "aritmean," + Util.formatDouble(aritMeanresult) +
                        ",geommean," + Util.formatDouble(geomMeanresult) +
                        ",harmmean," + Util.formatDouble(1.0 / harmMeanresultInverse));
                    
                    // Call terminate on all driver instances
                    for (int i = 0; i < nOfThreads; i++) {
                        drivers[i][driverRun].terminateDriver();
                    }
                }
                
                if (runsPerDriver > 1) {
                    // Print average for all runs
                    System.out.print("\n     Avgs: ");
                    Iterator tci = di.getAggregateTestCases().iterator();
                    while (tci.hasNext()) {
                        TestCase tc = (TestCase) tci.next();
                        System.out.print(tc.getTestName() + ",");                        
                        System.out.print(tc.getDoubleParam(Constants.RESULT_VALUE) + ",");
                    }
                    System.out.print(
                        "aritmean," +
                        di.getDoubleParam(Constants.RESULT_ARIT_MEAN) + 
                        ",geommean," +
                        di.getDoubleParam(Constants.RESULT_GEOM_MEAN) + 
                        ",harmmean," +
                        di.getDoubleParam(Constants.RESULT_HARM_MEAN));   

                    // Print standardDevs for all runs
                    System.out.print("\n    Stdev: ");
                    tci = di.getAggregateTestCases().iterator();
                    while (tci.hasNext()) {
                        TestCase tc = (TestCase) tci.next();
                        System.out.print(tc.getTestName() + ",");                        
                        System.out.print(tc.getDoubleParam(Constants.RESULT_VALUE_STDDEV) + ",");
                    }
                    System.out.println(
                        "aritmean," +
                        di.getDoubleParam(Constants.RESULT_ARIT_MEAN_STDDEV) + 
                        ",geommean," +
                        di.getDoubleParam(Constants.RESULT_GEOM_MEAN_STDDEV) + 
                        ",harmmean," +
                        di.getDoubleParam(Constants.RESULT_HARM_MEAN_STDDEV));   
                }
                else {
                    System.out.println("");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return testSuite;
    }        
}
