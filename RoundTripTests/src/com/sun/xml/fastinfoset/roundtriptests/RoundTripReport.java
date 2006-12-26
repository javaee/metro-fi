/*
 * Fast Infoset Round Trip Test software ("Software")
 *
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Software is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations.
 *
 *    Sun supports and benefits from the global community of open source
 * developers, and thanks the community for its important contributions and
 * open standards-based technology, which Sun has adopted into many of its
 * products.
 *
 *    Please note that portions of Software may be provided with notices and
 * open source licenses from such communities and third parties that govern the
 * use of those portions, and any licenses granted hereunder do not alter any
 * rights and obligations you may have under such open source licenses,
 * however, the disclaimer of warranty and limitation of liability provisions
 * in this License will apply to all Software in this distribution.
 *
 *    You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 */

package com.sun.xml.fastinfoset.roundtriptests;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class RoundTripReport {
    static final int INDEX_HOME = 0;    //e.g. /projects/RountTripTests/data
    static final int INDEX_REPORT = 1;    //e.g.   report/report.html
    static final int INDEX_TESTCASE = 2;    //e.g 011.xml
    static final int INDEX_TESTCASEPATH = 3;  //e.g. /projects/RountTripTests/data/xmlconf/xmltest
    static final int INDEX_TESTNAME = 4;  //e.g. saxroundtrip
    static final int INDEX_RESULT = 5;
    static final String COUNT_DEFAULT = "N/A";
    static final String COUNT_SAXPASSED = "<!--saxroundtrip_passed-->";
    static final String COUNT_SAXFAILED = "<!--saxroundtrip_failed-->";
    static final String COUNT_STAXPASSED = "<!--staxroundtrip_passed-->";
    static final String COUNT_STAXFAILED = "<!--staxroundtrip_failed-->";
    static final String COUNT_DOMPASSED = "<!--domroundtrip_passed-->";
    static final String COUNT_DOMFAILED = "<!--domroundtrip_failed-->";
    static final String COUNT_DOMSAXPASSED = "<!--domsaxroundtrip_passed-->";
    static final String COUNT_DOMSAXFAILED = "<!--domsaxroundtrip_failed-->";
    static final String COUNT_SAXSTAXPASSED = "<!--saxstaxdiff_passed-->";
    static final String COUNT_SAXSTAXFAILED = "<!--saxstaxdiff_failed-->";
    static final String RESULT_PASSED = "passed";
    static final String RESULT_FAILED = "failed";
    static final String REPORTCOUNT_TOTAL = "<!--{TOTAL}-->";
    static final String REPORT_NEWROW = "<!--{new row}-->";
    static final String TEST_SAX = "saxroundtrip";
    static final String TEST_STAX = "staxroundtrip";
    static final String TEST_DOM = "domroundtrip";
    static final String TEST_DOMSAX = "domsaxroundtrip";
    static final String TEST_SAXSTAX = "saxstaxdiff";
    
    /** Creates a new instance of RoundTripReport */
    public RoundTripReport() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length != 6) {
            displayUsageAndExit(args);
        }
        new RoundTripReport().report(args);
        
        
    }
    public void report(String[] args) {
        try {
            //String filename = args[INDEX_REPORT];
            String filename = args[INDEX_HOME]+"/"+args[INDEX_REPORT];
            String content = reportContent(filename, args);
            OutputStreamWriter osr = new OutputStreamWriter(
                    new FileOutputStream(
                    new File(filename)));
            osr.write(content);
            osr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String reportContent(String filename, String[] args) {
        File file = new File(filename);
        StringBuffer content = new StringBuffer();
        if (file.exists()) {
            content.append(readFromFile(file));
        } else {
            content.append(getTemplate());
        }
        
        //count test result
        countIncrement("<!--"+args[INDEX_TESTNAME]+"_"+args[INDEX_RESULT]+"-->", content);
        int testcaseStart = content.indexOf(args[INDEX_TESTCASE]);
//        if (testcaseStart < 0) {
//            countIncrement(REPORTCOUNT_TOTAL, content);
//        }
        //
        int start = 0;
        int end = 0;
        String newrow = null;

        if (args[INDEX_RESULT].equals(RESULT_FAILED)) {
            if (testcaseStart < 0) {
                newrow = "<tr><td><a href="+args[INDEX_TESTCASEPATH].substring(args[INDEX_HOME].length()+1)+"/"+args[INDEX_TESTCASE]+">"+
                        args[INDEX_TESTCASE]+"</a></td>\n"+
                        "<td><!--"+TEST_SAX+"--></td>\n"+
                        "<td><!--"+TEST_STAX+"--></td>\n"+
                        "<td><!--"+TEST_DOM+"--></td>\n"+
                        "<td><!--"+TEST_DOMSAX+"--></td>\n"+
                        "<td><!--"+TEST_SAXSTAX+"--></td></tr>\n"+
                        REPORT_NEWROW+"\n";
                start = content.indexOf(REPORT_NEWROW);
                end = start + REPORT_NEWROW.length();
                content.replace(start, end, newrow);
                testcaseStart = start;
            }
            
            String testname = "<!--"+args[INDEX_TESTNAME]+"-->";
            start = content.indexOf(testname, testcaseStart);
            if (start>0) {
                end = start + testname.length();
                content.replace(start, end, args[INDEX_RESULT]);
            }
        }
        return content.toString();
    }
    
    private String readFromFile(File file) {
        StringBuffer sb = new StringBuffer();
        try {
            FileInputStream fstream = new FileInputStream(file);
            
            // Convert our input stream to a
            // DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            
            while (in.available() !=0) {
                sb.append(in.readLine());
            }
            
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    private boolean countIncrement(String tag, StringBuffer content) {
        try {
            int start = content.indexOf(tag);
            int end = content.indexOf(tag, start+1);
            
            String temp = content.substring(start+tag.length(), end);
            int count = 1;
            if (!temp.equals(COUNT_DEFAULT)) {
                count = Integer.parseInt(temp) + 1;
            }
            temp = tag + count + tag;
            content.replace(start, end+tag.length(), temp);
            return true;
        } catch (Exception e) {
            return false;
        }
        
    }
    private String getTemplate() {
        StringBuffer template = new StringBuffer();
        template.append("<html>\n<body>\n<b>Roundtrip Tests</b><br><br>");
        template.append("<b>Summary</b>");
        template.append("<table width=\"80%\" border=1>\n");
        template.append("<tr><th></th><th>SAX</th><th>StAX</th><th>DOM</th><th>DOM-SAX</th><th>SAX-StAX</th></tr>\n");
        template.append("<tr><td>Passed</td>\n"+
                "<td>"+COUNT_SAXPASSED+COUNT_DEFAULT+COUNT_SAXPASSED+"</td>\n"+
                "<td>"+COUNT_STAXPASSED+COUNT_DEFAULT+COUNT_STAXPASSED+"</td>\n"+
                "<td>"+COUNT_DOMPASSED+COUNT_DEFAULT+COUNT_DOMPASSED+"</td>\n"+
                "<td>"+COUNT_DOMSAXPASSED+COUNT_DEFAULT+COUNT_DOMSAXPASSED+"</td>\n"+
                "<td>"+COUNT_SAXSTAXPASSED+COUNT_DEFAULT+COUNT_SAXSTAXPASSED+"</td></tr>\n");
        template.append("<tr><td>Failed</td>\n"+
                "<td>"+COUNT_SAXFAILED+COUNT_DEFAULT+COUNT_SAXFAILED+"</td>\n"+
                "<td>"+COUNT_STAXFAILED+COUNT_DEFAULT+COUNT_STAXFAILED+"</td>\n"+
                "<td>"+COUNT_DOMFAILED+COUNT_DEFAULT+COUNT_DOMFAILED+"</td>\n"+
                "<td>"+COUNT_DOMSAXFAILED+COUNT_DEFAULT+COUNT_DOMSAXFAILED+"</td>\n"+
                "<td>"+COUNT_SAXSTAXFAILED+COUNT_DEFAULT+COUNT_SAXSTAXFAILED+"</td></tr>\n");
        template.append("<tr><td>Total</td>\n"+
                "<td>"+REPORTCOUNT_TOTAL+COUNT_DEFAULT+REPORTCOUNT_TOTAL+"</td>\n"+
                "<td></td>\n"+
                "<td></td>\n"+
                "<td></td>\n"+
                "<td></td></tr>\n");
        template.append(REPORTCOUNT_TOTAL);
        template.append("</table>\n");
        template.append("<br><b>Failed List</b><br><table width=\"80%\" border=1>\n");
        template.append("<tr><th>Name of Testcase</th><th>SAX</th><th>StAX</th><th>DOM</th><th>DOM-SAX</th><th>SAX-StAX</th></tr>\n");
        template.append(REPORT_NEWROW);
        template.append("</table>\n");
        template.append("</body>\n</html>");
        return template.toString();
    }
    
    private static void displayUsageAndExit(String[] args) {
        System.err.println("Usage: RoundTripReport TS_HOME reportPath testcase_filename testcase_path testname testresult");
        System.err.println("Your input:");
        System.err.println("Number of arguments: "+args.length);
        for(int i=0; i<args.length; i++) {
            System.err.println("args["+i+"]="+args[i]);
        }
        System.err.println("Example: RoundTripReport /projects/fi/RoundTripTests/data xmlts_report.html 011.xml /projects/fi/RoundTripTests/xmltest/valid saxroundtrip failed");
        System.exit(1);
    }
    
}
