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
import com.sun.japex.report.*;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.net.URL;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

public class TrendReport {
    String fileSep = System.getProperty("file.separator");
    
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

        try {
//            File cwd = new File(reportPath);
//            File[] reportDirs = cwd.listFiles(new ReportFilter("2005-03-18", "2005-03-22"));

            TrendReportParams params = new TrendReportParams(args);
            new TrendReport().run(params);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        System.exit(0);

        
    }

    public void run(TrendReportParams params) {
        
        try {
//            File cwd = new File(reportPath);
//            File[] reportDirs = cwd.listFiles(new ReportFilter("2005-03-18", "2005-03-22"));

            //parse reports under the Report direction
            ParseReports testReports = new ParseReports(params);

            //support version 0.1 interface
            if (params.reportVersion() == ReportConstants.TRENDREPORT_VERSION_02) {
                new ReportGenerator(params, testReports).createReport();
            } else { //version 0.1
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

                System.out.println(params.outputPath()+fileSep+chartName);
                File file = new File(params.outputPath());
                if (!file.exists()) {
                    file.mkdirs();
                }
                ChartUtilities.saveChartAsJPEG(new File(params.outputPath()+fileSep+chartName), chart1, 700, 400);
                new IndexPage(params, chartName).report();
            }
            
            copyCss(params.outputPath());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    
    
    private void copyCss(String outputDir) {
        try{
            // Copy CSS to the same directory
            URL css = getClass().getResource("/resources/report.css");
            if (css != null) {
                InputStream is = css.openStream();
                FileOutputStream fos = new FileOutputStream(
                    new File(outputDir + fileSep + "report.css"));

                int c;
                while ((c = is.read()) != -1) {
                    fos.write(c);
                }
                is.close();
                fos.close();                    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
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
