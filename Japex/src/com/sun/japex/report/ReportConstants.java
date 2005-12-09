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

public class ReportConstants {
    public static final int TRENDREPORT_VERSION_01 = 1;
    public static final int TRENDREPORT_VERSION_02 = 2;
    
    public static final int REPORT_UNKNOWN = 0;
    public static final int REPORT_DEFAULT = 1;  //all drivers, all means, one means per chart
    public static final int REPORT_ALLDRIVERS = 2; //use DEFAULT for means plus all tests, one testcase per chart
    public static final int REPORT_ALLDRIVERS_ALLMEANS = 3; //-d or not, -m, all means/drivers on one chart, smart grouping testcases
    public static final int REPORT_ONEDRIVER = 4; //all means specified on one chart, smart grouping testcases specified
    public static final int REPORT_ALLTESTS = 5; //smart grouping testcases
    public static final int REPORT_TESTS = 6; //all tests specified on one chart
    
    
    public static final String ARITHMETIC_MEANS = "Arithmetic";
    public static final String GEOMETIC_MEANS = "Geometic";
    public static final String HARMONIC_MEANS = "Harmonic";
    
    public static final String KEYWORD_ALL = "all";
    
    public static final String DEFAULT_STARTDATE = "TODAY";
    public static final String DEFAULT_DATEOFFSET = "-1Y";
    
}
