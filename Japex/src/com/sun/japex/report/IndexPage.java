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

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class IndexPage {    
    static final String REPORT_NEWINDEX = "<!--{new index}-->";
    static final String REPORT_NEWROW = "<!--{new row}-->";
    
    TrendReportParams _params;
    String _chartName;
    /** Creates a new instance of RoundTripReport */
    public IndexPage(TrendReportParams params, String chartName) {
        _params = params;
        _chartName = chartName;
    }
    
    public void report() {
        try {
            //String filename = args[INDEX_REPORT];    
            String filename = _params.outputPath()+"/index.html"; 
            File file = new File(filename);
            String content = reportContent(file);
            OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(file));
            osr.write(content);
            osr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    public String reportContent(File file) {
        StringBuffer content = new StringBuffer();
        if (!_params.overwrite() && file.exists()) {
            try {
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                //content.append(readFromFile(file));
                byte[] b = new byte[in.available()];
                in.read(b);
                content.append(new String(b));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            content.append(getTemplate());
        }
        
        int start = 0;
        int end = 0;      
        StringBuffer newindex = new StringBuffer();
        newindex.append("<li><a href=\"#"+_params.title()+"\">"+_params.title()+"</a></li>\n");
        newindex.append(REPORT_NEWINDEX+"\n");


        StringBuffer newrow = new StringBuffer();
        newrow.append("<br><a name=\""+_params.title()+"\">\n");
        newrow.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tbody>");
        newrow.append("<tr valign=\"top\"><td width=\"90%\"><font color=\"005A9C\" size=\"5\">"+_params.title());
        newrow.append("</font></td><td align=\"right\"><a href=\"#top\"><font size=\"3\">[Top]</font></a></td></tr>");
        newrow.append("</tbody></table>\n");
        newrow.append("<ul>\n<li>Report Path: " + _params.reportPath() +  "</li>\n");
        newrow.append("<li>Output Path: " + _params.outputPath() + "</li>\n");
        newrow.append("<li>Report Period: " + _params.dateFrom() + " - " + _params.dateTo() + "</li>\n");
        if (_params.isDriverSpecified()) {
            newrow.append("<li>Driver: " + _params.driver() + "</li>\n");
        } else {
            newrow.append("<li>Driver: not specified</li>\n");            
        }
        newrow.append("<li>Testcase(s): ");
        if (_params.isTestSpecified()) {
            for(int i=0; i<_params.test().length; i++) {
                newrow.append(_params.test()[i]+" ");
            }
        } else {
            newrow.append("not specified");            
        }
        newrow.append("</li>\n</ul>\n");
        newrow.append("<table width=\"80%\" border=\"0\">");
        newrow.append("<tr><td colspan=\"2\" align=\"center\"><img src=\""+_chartName+"\"></td></tr>");
        newrow.append("</table>");
        newrow.append(REPORT_NEWROW+"\n");

        start = content.indexOf(REPORT_NEWINDEX);
        end = start + REPORT_NEWINDEX.length();
        content.replace(start, end, newindex.toString());

        start = content.indexOf(REPORT_NEWROW);
        end = start + REPORT_NEWROW.length();
        content.replace(start, end, newrow.toString());
        return content.toString();
    }
/*
    private String readFromFile(File file) {
        StringBuffer sb = new StringBuffer();
        try
        {
            FileInputStream fstream = new FileInputStream(file);

            // Convert our input stream to a
            // DataInputStream
            DataInputStream in = new DataInputStream(fstream);

            while (in.available() !=0)
            {
                    sb.append(in.readLine());
            }

            in.close();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
  
 */  
    private String getTemplate() {
        StringBuffer template = new StringBuffer();
        template.append("<html>\n<link href=\"report.css\" type=\"text/css\" rel=\"stylesheet\"/>\n");
        template.append("<head><title>Japex Trend Report</title></head>\n<body>\n");
        template.append("<a name=\"top\"><h1>Japex Trend Report</h1>\n");
        template.append("<ul>");
        template.append(REPORT_NEWINDEX);
        template.append("</ul>");
        template.append(REPORT_NEWROW);
        template.append("<small><hr/><i><font size=\"-2\"><!--datetime--></font></i></small>\n");
        template.append("</body>\n</html>");
        return template.toString();
    }
    
}
