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
/*
 * The original design of TrendReport was to create one chart at a time, e.g. to
   create a full report, TrendReport would have to executed multiple times using 
 * TrendDataset. The new feature request is for this class to generate full
 * report in accordance with report type. 
 */
public class ReportGenerator {
    TrendReportParams _params;
    Map[] _japexTestResults;
    Date[] _dates;
    boolean _hasReports = true;
    /** Creates a new instance of ReportFactory */
    public ReportGenerator(TrendReportParams params, ParseReports japexReports) {
        _params = params;
        _japexTestResults = japexReports.getReports();
        if (_japexTestResults != null) {
            _dates = japexReports.getDates();
        } else {
            _hasReports = false;
        }
        
    }
    
    
    public boolean createReport() {
        if (!_hasReports) 
            return false;
        
        switch (_params.reportType()) {
            case ReportConstants.REPORT_DEFAULT:
                defaultReport();
                break;
        }
        return true;
    }
    
    //one means for all drivers
    private void defaultReport() {
        TimeSeries means, aritMeans, geomMeans, harmMeans;
        TimeSeriesCollection aritDataset = new TimeSeriesCollection();
        TimeSeriesCollection geomDataset = new TimeSeriesCollection();
        TimeSeriesCollection harmDataset = new TimeSeriesCollection();
        ResultPerDriver result = null;
        GregorianCalendar cal = new GregorianCalendar();
        String[] drivers = _params.driver();
        for (int ii = 0; ii< drivers.length; ii++) {
            aritMeans = new TimeSeries("Arithmetic Means for "+drivers[ii], Day.class);
            geomMeans = new TimeSeries("Geometric Means for "+drivers[ii], Day.class);
            harmMeans = new TimeSeries("Harmonic Means for "+drivers[ii], Day.class);
            for (int i = 0; i < _japexTestResults.length; i++) {
                cal.setTime(_dates[i]);
                int day = cal.get(cal.DAY_OF_MONTH);
                int month = cal.get(cal.MONTH) + 1; //month starts at 0!
                int year = cal.get(cal.YEAR);
                result = (ResultPerDriver)_japexTestResults[i].get(drivers[ii]);
                //check if there's data. Japex reports may contain different drivers
                if (result != null) { 
                    aritMeans.add(new Day(day, month, year), result.getAritMean());
                    geomMeans.add(new Day(day, month, year), result.getGeomMean());
                    harmMeans.add(new Day(day, month, year), result.getHarmMean());
                }
            }
            if (result != null) {
                aritDataset.addSeries(aritMeans);
                geomDataset.addSeries(geomMeans);
                harmDataset.addSeries(harmMeans);
            }
        }
        
        IndexPage indexPage = new IndexPage(_params, true);
        _params.setTitle("Arithmetic Means");
        saveChart("Arithmetic Means", aritDataset, "ArithmeticMeans.jpg", 700, 400);
        indexPage.updateContent("ArithmeticMeans.jpg");
        saveChart("Geometric Means", geomDataset, "GeometricMeans.jpg", 700, 400);
        _params.setTitle("Geometric Means");
        indexPage.updateContent("GeometricMeans.jpg");
        saveChart("Harmonic Means", harmDataset, "HarmonicMeans.jpg", 700, 400);
        _params.setTitle("Harmonic Means");
        indexPage.updateContent("HarmonicMeans.jpg");
        indexPage.writeContent();
    }
    private void saveChart(String title, TimeSeriesCollection dataset, 
                           String fileName, int width, int height) {
        JFreeChart chart = LineChart.createChart(title, dataset);
        try {
            // Converts chart in JPEG file named [name].jpg            
            String fileSep = System.getProperty("file.separator");

            File file = new File(_params.outputPath());
            if (!file.exists()) {
                file.mkdirs();
            }
            ChartUtilities.saveChartAsJPEG(new File(_params.outputPath()+fileSep+fileName), 
                    chart, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
