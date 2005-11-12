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
import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Color;
import java.io.File;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

public class ChartGenerator {
    
    TestSuiteImpl _testSuite;
    
    public ChartGenerator(TestSuiteImpl testSuite) {
        _testSuite = testSuite;        
    }
    
    public void generateDriverChart(String fileName) {
        try {
            JFreeChart chart = null;
            String chartType = _testSuite.getParam(Constants.CHART_TYPE);
            
            if (chartType.equalsIgnoreCase("barchart")) {
                chart = generateDriverBarChart();                
            }
            else if (chartType.equalsIgnoreCase("scatterchart")) {
                chart = generateDriverScatterChart();                
            }
            else if (chartType.equalsIgnoreCase("linechart")) {
                chart = generateDriverLineChart();
            }
            else {
                assert false;
            }
            chart.setAntiAlias(true);            
            ChartUtilities.saveChartAsJPEG(new File(fileName), chart, 600, 450);       
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }
    
    private JFreeChart generateDriverBarChart() {
        try {
            String resultUnit = _testSuite.getParam(Constants.RESULT_UNIT);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();           
            
            // Find first normalizer driver (if any) and adjust unit            
            DriverImpl normalizerDriver = null;            
            Iterator jdi = _testSuite.getDriverInfoList().iterator();
            while (jdi.hasNext()) {
                DriverImpl di = (DriverImpl) jdi.next();       
                if (di.isNormal()) {
                    normalizerDriver = di; 
                    resultUnit = "% of " + resultUnit;
                    break;
                }
            }
            
            // Generate charts
            jdi = _testSuite.getDriverInfoList().iterator();
            while (jdi.hasNext()) {
                DriverImpl di = (DriverImpl) jdi.next();
                              
                if (normalizerDriver != null) {
                    dataset.addValue(
                        normalizerDriver == di ? 100.0 :
                        (100.0 * di.getDoubleParam(Constants.RESULT_ARIT_MEAN) /
                         normalizerDriver.getDoubleParam(Constants.RESULT_ARIT_MEAN)),
                        di.getName(),
                        "Arithmetic Mean");
                    dataset.addValue(
                        normalizerDriver == di ? 100.0 :
                        (100.0 * di.getDoubleParam(Constants.RESULT_GEOM_MEAN) /
                         normalizerDriver.getDoubleParam(Constants.RESULT_GEOM_MEAN)),
                        di.getName(),
                        "Geometric Mean");
                    dataset.addValue(
                        normalizerDriver == di ? 100.0 :
                        (100.0 * di.getDoubleParam(Constants.RESULT_HARM_MEAN) /
                         normalizerDriver.getDoubleParam(Constants.RESULT_HARM_MEAN)),
                        di.getName(),
                        "Harmonic Mean");                    
                }
                else {
                    dataset.addValue(
                        di.getDoubleParam(Constants.RESULT_ARIT_MEAN), 
                        di.getName(),
                        "Arithmetic Mean");
                    dataset.addValue(
                        di.getDoubleParam(Constants.RESULT_GEOM_MEAN), 
                        di.getName(),
                        "Geometric Mean");
                    dataset.addValue(
                        di.getDoubleParam(Constants.RESULT_HARM_MEAN), 
                        di.getName(),
                        "Harmonic Mean");
                }
            }
            
            return ChartFactory.createBarChart3D(
                "Result Summary (" + resultUnit + ")", 
                "", resultUnit, 
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }   
    
    private JFreeChart generateDriverScatterChart() {
        try {
            DefaultTableXYDataset xyDataset = new DefaultTableXYDataset();
                        
            // Generate charts
            Iterator jdi = _testSuite.getDriverInfoList().iterator();
            for (int i = 0; jdi.hasNext(); i++) {
                DriverImpl di = (DriverImpl) jdi.next();
                                          
                XYSeries xySeries = new XYSeries(di.getName(), true, false);
                xySeries.add(di.getDoubleParam(Constants.RESULT_ARIT_MEAN_X),
                             di.getDoubleParam(Constants.RESULT_ARIT_MEAN));
                xySeries.add(di.getDoubleParam(Constants.RESULT_GEOM_MEAN_X),
                             di.getDoubleParam(Constants.RESULT_GEOM_MEAN));
                xySeries.add(di.getDoubleParam(Constants.RESULT_HARM_MEAN_X),
                             di.getDoubleParam(Constants.RESULT_HARM_MEAN));   
                xyDataset.addSeries(xySeries);
            }
                        
            String resultUnit = _testSuite.getParam(Constants.RESULT_UNIT);
            String resultUnitX = _testSuite.getParam(Constants.RESULT_UNIT_X);
            
            JFreeChart chart = ChartFactory.createScatterPlot("Result Summary", 
                resultUnitX, resultUnit, 
                xyDataset, PlotOrientation.VERTICAL, 
                true, true, false);
            
            // Set log scale depending on japex.resultAxis[_X]
            XYPlot plot = chart.getXYPlot();
            if (_testSuite.getParam(Constants.RESULT_AXIS_X).equalsIgnoreCase("logarithmic")) {
                LogarithmicAxis logAxisX = new LogarithmicAxis(resultUnitX);
                logAxisX.setAllowNegativesFlag(true);
                plot.setDomainAxis(logAxisX);   
            }
            if (_testSuite.getParam(Constants.RESULT_AXIS).equalsIgnoreCase("logarithmic")) {          
                LogarithmicAxis logAxis = new LogarithmicAxis(resultUnit);
                logAxis.setAllowNegativesFlag(true);
                plot.setRangeAxis(logAxis);   
            }            
            
            return chart;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }                
    }
    
    private JFreeChart generateDriverLineChart() {
        try {
            String resultUnit = _testSuite.getParam(Constants.RESULT_UNIT);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();           
                       
            // Generate charts
            Iterator jdi = _testSuite.getDriverInfoList().iterator();
            while (jdi.hasNext()) {
                DriverImpl di = (DriverImpl) jdi.next();
                              
                dataset.addValue(
                    di.getDoubleParam(Constants.RESULT_ARIT_MEAN), 
                    "Arithmetic Mean", 
                    di.getName());
                dataset.addValue(
                    di.getDoubleParam(Constants.RESULT_GEOM_MEAN), 
                    "Geometric Mean",
                    di.getName());
                dataset.addValue(
                    di.getDoubleParam(Constants.RESULT_HARM_MEAN), 
                    "Harmonic Mean",
                    di.getName());
            }
            
            JFreeChart chart = ChartFactory.createLineChart(
                "Result Summary (" + resultUnit + ")", 
                "", resultUnit, 
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
            
            configureLineChart(chart);            
            return chart;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }    

    public int generateTestCaseCharts(String baseName, String extension) {
        String chartType = _testSuite.getParam(Constants.CHART_TYPE);
        if (chartType.equalsIgnoreCase("barchart")) {
            return generateTestCaseBarCharts(baseName, extension);                
        }
        else if (chartType.equalsIgnoreCase("scatterchart")) {
            return generateTestCaseScatterCharts(baseName, extension);                
        }
        else if (chartType.equalsIgnoreCase("linechart")) {
            return generateTestCaseLineCharts(baseName, extension);                
        }
        else {
            assert false;
        }
        return 0;
    }
    
    private int generateTestCaseBarCharts(String baseName, String extension) {
        int nOfFiles = 0;
        final int maxGroupSize = 5;        
        List driverInfoList = _testSuite.getDriverInfoList();
        
        // Get number of tests from first driver
        final int nOfTests = 
            ((DriverImpl) driverInfoList.get(0)).getAggregateTestCases().size();
            
        int groupSizesIndex = 0;
        int[] groupSizes = calculateGroupSizes(nOfTests, maxGroupSize);
        
        try {            
            String resultUnit = _testSuite.getParam(Constants.RESULT_UNIT);
            
            // Find first normalizer driver (if any)
            DriverImpl normalizerDriver = null;
            
            Iterator jdi = driverInfoList.iterator();
            while (jdi.hasNext()) {
                DriverImpl di = (DriverImpl) jdi.next();       
                if (di.isNormal()) {
                    normalizerDriver = di; 
                    resultUnit = "% of " + resultUnit;
                    break;
                }
            }
            
            // Generate charts 
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            
            int i = 0, thisGroupSize = 0;
            for (; i < nOfTests; i++) {
                jdi = driverInfoList.iterator();
                
                while (jdi.hasNext()) {
                    DriverImpl di = (DriverImpl) jdi.next();
                    TestCaseImpl tc = (TestCaseImpl) di.getAggregateTestCases().get(i);
            
                    // User normalizer driver if defined
                    if (normalizerDriver != null) {
                        TestCaseImpl normalTc = 
                            (TestCaseImpl) normalizerDriver.getAggregateTestCases().get(i);
                        dataset.addValue(normalizerDriver == di ? 100.0 :
                                (100.0 * tc.getDoubleParam(Constants.RESULT_VALUE) /
                                 normalTc.getDoubleParam(Constants.RESULT_VALUE)),
                                di.getName(),
                                tc.getName());                                                
                    }
                    else {
                        dataset.addValue(
                            tc.getDoubleParam(Constants.RESULT_VALUE), 
                            di.getName(),
                            tc.getName());                    
                    }
                }             
                
                thisGroupSize++;
                        
                // Generate chart for this group if complete
                if (thisGroupSize == groupSizes[groupSizesIndex]) {
                    JFreeChart chart = ChartFactory.createBarChart3D(
                        "Results per Test (" + resultUnit + ")", 
                        "", resultUnit, 
                        dataset,
                        PlotOrientation.VERTICAL,
                        true, true, false);
                    
                    chart.setAntiAlias(true);
                    ChartUtilities.saveChartAsJPEG(
                        new File(baseName + Integer.toString(nOfFiles) + extension),
                        chart, 600, 450);
                    
                    nOfFiles++;
                    groupSizesIndex++;
                    thisGroupSize = 0;
                    dataset = new DefaultCategoryDataset();
                }
            }            
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
        
        return nOfFiles;
    }
    
    private int generateTestCaseScatterCharts(String baseName, String extension) {        
        int nOfFiles = 0;
        List driverInfoList = _testSuite.getDriverInfoList();
            
        try {            
            // Get number of tests from first driver
            final int nOfTests = 
                ((DriverImpl) driverInfoList.get(0)).getAggregateTestCases().size();            
            
            DefaultTableXYDataset xyDataset = new DefaultTableXYDataset();

            // Generate charts
            Iterator jdi = driverInfoList.iterator();
            for (int i = 0; jdi.hasNext(); i++) {
                DriverImpl di = (DriverImpl) jdi.next();

                XYSeries xySeries = new XYSeries(di.getName(), true, false);
                for (int j = 0; j < nOfTests; j++) {
                    TestCaseImpl tc = (TestCaseImpl) di.getAggregateTestCases().get(j);
                    xySeries.add(tc.getDoubleParam(Constants.RESULT_VALUE_X),
                                 tc.getDoubleParam(Constants.RESULT_VALUE));

                }                    
                xyDataset.addSeries(xySeries);
            }

            String resultUnit = _testSuite.getParam(Constants.RESULT_UNIT);
            String resultUnitX = _testSuite.getParam(Constants.RESULT_UNIT_X);
            
            JFreeChart chart = ChartFactory.createScatterPlot("Results Per Test", 
                resultUnitX, resultUnit, 
                xyDataset, PlotOrientation.VERTICAL, 
                true, true, false);

            // Set log scale depending on japex.resultAxis[_X]
            XYPlot plot = chart.getXYPlot();
            if (_testSuite.getParam(Constants.RESULT_AXIS_X).equalsIgnoreCase("logarithmic")) {
                LogarithmicAxis logAxisX = new LogarithmicAxis(resultUnitX);
                logAxisX.setAllowNegativesFlag(true);
                plot.setDomainAxis(logAxisX);   
            }
            if (_testSuite.getParam(Constants.RESULT_AXIS).equalsIgnoreCase("logarithmic")) {          
                LogarithmicAxis logAxis = new LogarithmicAxis(resultUnit);
                logAxis.setAllowNegativesFlag(true);
                plot.setRangeAxis(logAxis);   
            }            

            chart.setAntiAlias(true);
            ChartUtilities.saveChartAsJPEG(
                new File(baseName + Integer.toString(nOfFiles) + extension),
                chart, 600, 450);
            nOfFiles++;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
        
        return nOfFiles;
    }
    
    private int generateTestCaseLineCharts(String baseName, String extension) {        
        List driverInfoList = _testSuite.getDriverInfoList();
        
        // Get number of tests from first driver
        final int nOfTests = 
            ((DriverImpl) driverInfoList.get(0)).getAggregateTestCases().size();
            
        try {            
            String resultUnit = _testSuite.getParam(Constants.RESULT_UNIT);
                        
            // Generate charts            
            for (int i = 0; i < nOfTests; i++) {
                Iterator jdi = driverInfoList.iterator();
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                
                while (jdi.hasNext()) {
                    DriverImpl di = (DriverImpl) jdi.next();
                    TestCaseImpl tc = (TestCaseImpl) di.getAggregateTestCases().get(i);
            
                    dataset.addValue(
                         tc.getDoubleParam(Constants.RESULT_VALUE), 
                         tc.getName(),
                         di.getName());                    
                }             
                
                JFreeChart chart = ChartFactory.createLineChart(
                    "Results per Test (" + resultUnit + ")", 
                    "", resultUnit, 
                    dataset,
                    PlotOrientation.VERTICAL,
                    true, true, false);
                
                //CategoryPlot plot = chart.getCategoryPlot();
                //plot.getRenderer().setStroke(new java.awt.BasicStroke(3.0f));
                configureLineChart(chart);

                chart.setAntiAlias(true);
                ChartUtilities.saveChartAsJPEG(
                    new File(baseName + Integer.toString(i) + extension),
                    chart, 600, 450);
            }            
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
        
        return nOfTests;
    }
    
    static private void configureLineChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        
        final DrawingSupplier supplier = new DefaultDrawingSupplier(
            DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
            DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
            // Draw a small diamond 
            new Shape[] { new Polygon(new int[] {3, 0, -3, 0}, 
                                      new int[] {0, 3, 0, -3}, 4) }
        );
        plot.setDomainGridlinePaint(Color.black);
        plot.setRangeGridlinePaint(Color.black);
        plot.setDrawingSupplier(supplier);
        
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setShapesVisible(true);
        renderer.setStroke(new BasicStroke(2.0f));        
    }
    
    /**
     * Calculate group sizes for tests to avoid a very small final group. 
     * For example, calculateGroupSizes(21, 5) return { 5,5,5,3,3 } instead
     * of { 5,5,5,5,1 }.
     */
    private static int[] calculateGroupSizes(int nOfTests, int maxGroupSize) {
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

}
