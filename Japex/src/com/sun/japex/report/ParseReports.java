/*
 * ParseReport.java
 *
 * Created on April 8, 2005, 10:19 PM
 */

package com.sun.japex.report;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory; 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser; 

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.HashMap;

public class ParseReports {
    ArrayList _reports = new ArrayList();
    ArrayList _dates = new ArrayList();
    boolean hasReport = false;
    
    /** Creates a new instance of ParseReport */
    public ParseReports(TrendReportParams params) {
        File cwd = new File(params.reportPath());
        ReportFilter filter =new ReportFilter(params.dateFrom(), params.dateTo());
        File[] reportDirs = cwd.listFiles(filter);
        Arrays.sort(reportDirs, new DateComparator());
        
        String separator = System.getProperty("file.separator");
        ReportDataParser handler = null;
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            GregorianCalendar cal = new GregorianCalendar();
            int lastDay=0, lastMonth=0, lastYear=0;
        
            for (int i = 0; i < reportDirs.length; i++) {
                File file = new File(reportDirs[i].getAbsolutePath()+separator+"report.xml"); 
                if (file.exists()) {
                    Date date = new Date(reportDirs[i].lastModified());
                    cal.setTime(date);
                    int day = cal.get(cal.DAY_OF_MONTH);
                    int month = cal.get(cal.MONTH);
                    int year = cal.get(cal.YEAR);
                    //add one report per day
                    if (day==lastDay && month==lastMonth && year==lastYear) {
                        //skip
                    } else {
                        handler = new ReportDataParser(params);
                        saxParser.parse(file, handler);
                        Map report = (Map)handler.getReports();
                        if (report != null) {
                            _reports.add(report);
                            _dates.add(date);
                            hasReport = true;
                            lastDay = day;
                            lastMonth = month;
                            lastYear = year;
                        }
                    }
                }
            }
        
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
    }
    
    public Map[] getReports() {
        if (!hasReport) return null;
        Map[] reports = new HashMap[_reports.size()];
        reports = (Map[])_reports.toArray(reports);
        return reports;        
    }
    
    public Date[] getDates() {
        if (!hasReport) return null;
        Date[] dates = new Date[_reports.size()];
        dates = (Date[])_dates.toArray(dates);
        return dates;
    }
}
