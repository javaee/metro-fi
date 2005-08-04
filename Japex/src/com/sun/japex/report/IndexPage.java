/*
 * RoundTripReport.java
 *
 * Created on May 9, 2005, 9:59 PM
 */

package com.sun.japex.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class IndexPage {    
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
        if (file.exists()) {
            content.append(readFromFile(file));
        } else {
            content.append(getTemplate());
        }
        
        int start = 0;
        int end = 0;      
        StringBuffer newrow = new StringBuffer();
        newrow.append("<table width=\"100%\" border=\"0\">");
        newrow.append("<tr><th width=\"50%\"><h2>"+_params.title());
        newrow.append("</h2></th><th align=\"right\"><a href=\"#top\">Top</a></th></tr>");
        newrow.append("</table>");
        newrow.append("<ul>\n<li>Report Path: " + _params.reportPath() +  "</li>\n");
        newrow.append("<li>Oupput Path: " + _params.outputPath() + "</li>\n");
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

        start = content.indexOf(REPORT_NEWROW);
        end = start + REPORT_NEWROW.length();
        content.replace(start, end, newrow.toString());
        return content.toString();
    }

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
    
    private String getTemplate() {
        StringBuffer template = new StringBuffer();
        template.append("<html>\n<link href=\"report.css\" type=\"text/css\" rel=\"stylesheet\"/>\n");
        template.append("<head><title>Japex Trend Report</title></head>\n<body>\n");
        template.append("<a name=\"top\"><h1>Japex Trend Report</h1>\n");
        template.append(REPORT_NEWROW);
        template.append("<small><hr/><i><font size=\"-2\"><!--datetime--></font></i></small>\n");
        template.append("</body>\n</html>");
        return template.toString();
    }
    
}
