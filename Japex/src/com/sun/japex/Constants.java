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

public class Constants {

    // Global input parameters
    public static final String CLASS_PATH        = "japex.classPath";
    public static final String NUMBER_OF_THREADS = "japex.numberOfThreads";
    public static final String RUNS_PER_DRIVER   = "japex.runsPerDriver";
    
    // Global output parameters
    public static final String VERSION         = "japex.version";
    public static final String VERSION_VALUE   = "0.1";
    public static final String OS_NAME         = "japex.osName";
    public static final String OS_ARCHITECTURE = "japex.osArchitecture";
    public static final String DATE_TIME       = "japex.dateTime";
    public static final String VM_INFO         = "japex.vmInfo";
    public static final String RESULT_UNIT     = "japex.resultUnit";
    
    // Driver input parameters        
    public static final String DRIVER_CLASS = "japex.driverClass";
    
    // Driver output parameters
    public static final String RESULT_ARIT_MEAN = "japex.resultAritMean";
    public static final String RESULT_GEOM_MEAN = "japex.resultGeomMean";
    public static final String RESULT_HARM_MEAN = "japex.resultHarmMean";
    public static final String RESULT_ARIT_MEAN_STDDEV = "japex.resultAritMeanStddev";
    public static final String RESULT_GEOM_MEAN_STDDEV = "japex.resultGeomMeanStddev";
    public static final String RESULT_HARM_MEAN_STDDEV = "japex.resultHarmMeanStddev";
    
    // Global/Testcase input parameters
    public static final String RUN_ITERATIONS    = "japex.runIterations";
    public static final String WARMUP_ITERATIONS = "japex.warmupIterations";
    public static final String WARMUP_TIME       = "japex.warmupTime";
    public static final String RUN_TIME          = "japex.runTime";
    public static final String STDDEV_THRESHOLD  = "japex.stddevThreshold";
    public static final String MIN_WARMUP_ITERATIONS = "japex.minWarmupIterations";
    public static final String MAX_WARMUP_ITERATIONS = "japex.maxWarmupIterations";
    
    // Testcase output parameters
    public static final String ACTUAL_RUN_ITERATIONS = "japex.actualRunIterations";
    public static final String ACTUAL_WARMUP_ITERATIONS = "japex.actualWarmupIterations";    
    public static final String ACTUAL_RUN_TIME      = "japex.actualRunTime";
    public static final String ACTUAL_WARMUP_TIME   = "japex.actualWarmupTime";
    public static final String ACTUAL_PREPARE_TIME  = "japex.actualPrepareTime";        
    public static final String RESULT_VALUE         = "japex.resultValue";
    public static final String RESULT_VALUE_STDDEV  = "japex.resultValueStddev";
    
    // Default values
    public static final String DEFAULT_WARMUP_ITERATIONS = "300";
    public static final String DEFAULT_RUN_ITERATIONS    = "300";
    public static final String DEFAULT_NUMBER_OF_THREADS = "1";
    public static final String DEFAULT_RUNS_PER_DRIVER   = "1";
}
