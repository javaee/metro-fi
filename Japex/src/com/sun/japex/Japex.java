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

import java.io.*;
import java.text.*;
import java.util.Date;
import java.net.URL;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class Japex {
    
    public static boolean HTML_OUTPUT = true;
    public static Date TODAY = new Date();
    
    /** Creates a new instance of Japex */
    public Japex() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 4) {
            displayUsageAndExit();
        }

        // Parse command-line arguments
        String configFile = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-nohtml")) {
                HTML_OUTPUT = false;
            }
            else if (args[i].equals("-cp") || args[i].equals("-classpath")) {
                i++; // Skip, already processed
            }
            else {
                configFile = args[i];
            }
        }
        
        if (configFile == null) {
            displayUsageAndExit();
        }

        new Japex().run(configFile);
    }

    private static void displayUsageAndExit() {
        System.err.println("Usage: japex [-cp <classpath>] [-nohtml] config.xml");
        System.exit(1);        
    }
    
    public void run(String configFile) {  
        try {
            System.out.println("Running ...");
            
            // Create testsuite object from configuration file
            TestSuiteImpl testSuite = new Engine().start(configFile);
            
            // Create report directory
            String fileSep = System.getProperty("file.separator");
            DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
            String outputDir = testSuite.getParam(Constants.REPORTS_DIRECTORY) 
                + fileSep + df.format(TODAY);            

            // Generate report to string buffer
            StringBuffer report = new StringBuffer();
            testSuite.serialize(report);            

            // Output report to file
            new File(outputDir).mkdirs();
            System.out.println("Generating reports ...");
            System.out.println("  " + 
                new File(outputDir + "/" + "report.xml").toURL());
            OutputStreamWriter osr = new OutputStreamWriter(
                new FileOutputStream(
                    new File(outputDir + fileSep + "report.xml")));
            osr.write(report.toString());
            osr.close();

            // Return if no HTML needs to be output
            if (!HTML_OUTPUT) return;
            
            // Generate charts
            final String resultChart = "result.jpg";
            testSuite.generateDriverChart(outputDir + fileSep 
                + resultChart);
            final String testCaseChartBase = "testcase";
            int nOfCharts = testSuite.generateTestCaseCharts(outputDir
                + fileSep + testCaseChartBase, ".jpg");
            
            // Extend report with chart info
            StringBuffer extendedReport = new StringBuffer();
            extendedReport.append("<extendedTestSuiteReport " + 
                "xmlns=\"http://www.sun.com/japex/extendedTestSuiteReport\">\n")
                .append(" <resultChart>" + resultChart + "</resultChart>\n");
            for (int i = 0; i < nOfCharts; i++) {
                extendedReport.append(" <testCaseChart>" + 
                    testCaseChartBase + i + ".jpg" + "</testCaseChart>\n");
            }
            extendedReport.append(report);
            extendedReport.append("</extendedTestSuiteReport>\n");

            // Generate HTML report
            TransformerFactory tf = TransformerFactory.newInstance();        
            URL stylesheet = getClass().getResource("/resources/report.xsl");
            if (stylesheet != null) {
                Transformer transformer = tf.newTransformer(
                    new StreamSource(stylesheet.toExternalForm()));

                System.out.println("  " + 
                    new File(outputDir + "/" + "report.html").toURL());
                transformer.transform(
                    new StreamSource(new StringReader(extendedReport.toString())),
                    new StreamResult(new FileOutputStream(
                        new File(outputDir + fileSep + "report.html"))));
                
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
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
