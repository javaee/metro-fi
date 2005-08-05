/*
 * TrendReport.java
 *
 * Created on April 6, 2005, 5:48 PM
 */

package com.sun.japex;
import com.sun.japex.report.*;

import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

public class TrendReport {
    
    /** Creates a new instance of TrendReport */
    public TrendReport() {
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            displayUsageAndExit(args);
        }
        String reportPath = "/projects/fws/WSTest/Java/src/reports";
        String separator = System.getProperty("file.separator");
        try {
//            File cwd = new File(reportPath);
//            File[] reportDirs = cwd.listFiles(new ReportFilter("2005-03-18", "2005-03-22"));

            TrendReportParams params = new TrendReportParams(args);
            //parse reports under the Report direction
            ParseReports testReports = new ParseReports(params);

            //get Trend datasets
            TrendDataset dataset = new TrendDataset(params, testReports);
            JFreeChart chart1 = LineChart.createChart(params.title(), dataset.getDataset());

            // Converts chart in JPEG file named chart.jpg            
            String chartName = null;
            if (!params.isTestSpecified()) {
                chartName = "means.jpg";
            } else {
                chartName = params.test()[0]+".jpg";
            }
System.out.println("chartname: "+chartName);
System.out.println("test specified: "+params.isTestSpecified());

            System.out.println(params.outputPath()+"/"+chartName);
            File file = new File(params.outputPath());
            if (!file.exists()) {
                file.mkdirs();
            }
            ChartUtilities.saveChartAsJPEG(new File(params.outputPath()+"/"+chartName), chart1, 500, 300);
            new IndexPage(params, chartName).report();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        System.exit(0);

        
    }

    private static void displayUsageAndExit(String[] args) {
        System.err.println("Usage: TrendReport Title reportPath outputPath date offset [driver] [test]");
        System.err.println("Arguments passed in: \n");
        for (int i=0; i<args.length; i++) {
            System.err.println(i + " = " + args[i]);
        }
        System.exit(1);        
    }
     
}
