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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.Week;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.Spacer;
import org.jfree.util.UnitType;

import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.ArrayList;

public class TrendDataset {
    static final String FIDRIVER = "TestFIDriver";
    static final String XMLDRIVER ="TestXMLDriver";
    TimeSeriesCollection _dataset;
    
    /** Creates a new instance of TrendDataset */
    public TrendDataset(TrendReportParams params, ParseReports testReports) {
        Map[] reports = testReports.getReports();
        if (reports != null) {
            Date[] dates = testReports.getDates();
            getDataset(params, reports, dates);
        }
    }
    void getMeansDataset(ArrayList drivers, Map[] reports, Date[] dates) {
        TimeSeries aritMeans, geomMeans, harmMeans;
        ResultPerDriver result = null;
        GregorianCalendar cal = new GregorianCalendar();
        for (int ii = 0; ii< drivers.size(); ii++) {
            //if [testcase] is not specified, output means
            aritMeans = new TimeSeries("Arithmetic Means", Day.class);
            geomMeans = new TimeSeries("Geometric Means", Day.class);
            harmMeans = new TimeSeries("Harmonic Means", Day.class);
            for (int i = 0; i < reports.length; i++) {
                cal.setTime(dates[i]);
                int day = cal.get(cal.DAY_OF_MONTH);
                int month = cal.get(cal.MONTH) + 1; //month starts at 0!
                int year = cal.get(cal.YEAR);
//        System.out.println(day + "-" + month + "-" + year);                    
                result = (ResultPerDriver)reports[i].get(drivers.get(ii));
//    System.out.println("AritMean="+result.getAritMean());            
//    System.out.println("GeomMean="+result.getGeomMean());            
//    System.out.println("HarmMean="+result.getHarmMean());            
                aritMeans.add(new Day(day, month, year), result.getAritMean());
                geomMeans.add(new Day(day, month, year), result.getGeomMean());
                harmMeans.add(new Day(day, month, year), result.getHarmMean());
            }
            _dataset.addSeries(aritMeans);
            _dataset.addSeries(geomMeans);
            _dataset.addSeries(harmMeans);
        }
        
    }
    void getTestsDataset(ArrayList drivers, TrendReportParams params, 
            Map[] reports, Date[] dates) {
        TimeSeries[] testcases;                                
        GregorianCalendar cal = new GregorianCalendar();
        ResultPerDriver result = null;
        String[] tests = params.test();        
        
        for (int ii = 0; ii< drivers.size(); ii++) {
            ArrayList ts = new ArrayList();
            for(int i=0; i<tests.length; i++) {
                ts.add(new TimeSeries(tests[i], Day.class));
            }
            testcases = new TimeSeries[ts.size()];
            ts.toArray(testcases);
            //testcases = (TimeSeries[])ts.toArray();
            for (int i = 0; i < reports.length; i++) {
                cal.setTime(dates[i]);
                int day = cal.get(cal.DAY_OF_MONTH);
                int month = cal.get(cal.MONTH) + 1; //month starts at 0!
                int year = cal.get(cal.YEAR);
                result = (ResultPerDriver)reports[i].get(drivers.get(ii));
                for(int j=0; j<tests.length; j++) {
                    if (result.getResult(tests[j])!=0) testcases[j].add(new Day(day, month, year), result.getResult(tests[j]));
                }                    
            }
            for(int j=0; j<tests.length; j++) {
                _dataset.addSeries(testcases[j]);
            }
        }
        
    }    
    void getDataset(TrendReportParams params, Map[] reports, Date[] dates) {
        ArrayList drivers = new ArrayList();        
        TimeSeries[] testcases;
        //if [driver] is not specified, output both drivers
        if (!params.isDriverSpecified()) {
            drivers.add(FIDRIVER);
            drivers.add(XMLDRIVER);
        } else {
            drivers.add(params.driver());
        }
        _dataset = new TimeSeriesCollection();
        if (!params.isTestSpecified()) {
            getMeansDataset(drivers, reports, dates);
        } else {
            getTestsDataset(drivers, params, reports, dates);
        }                        
        
        _dataset.setDomainIsPointsInTime(true);
        
    }
    public XYDataset getDataset() {
        return _dataset;
    }    
}
