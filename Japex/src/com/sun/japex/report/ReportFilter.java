/*
 * ReportFilter.java
 *
 * Created on April 6, 2005, 6:07 PM
 */

package com.sun.japex.report;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class ReportFilter implements FileFilter {
    Date _from;
    Date _to;
    
    public ReportFilter(Date from, Date to) {
        _from = from;
        _to = to;
//System.out.println("from:" + _from);        
//System.out.println("to:" + _to);        
    }
    /*
    public ReportFilter(String from, String to) {
        try {
            DateFormat df= new SimpleDateFormat ("yyyy-MM-dd");
            _from = df.parse(from);
            _to = df.parse(to);
        } catch (ParseException pe) {
            System.out.println(pe.getMessage());
        }
    }
      
     */
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            long date = pathname.lastModified();
            if (date >= _from.getTime() && date <= _to.getTime()) {
//System.out.println("Filterd: "+new Date(pathname.lastModified()));
                return true;
            }
        }
        return false;
    }
    
}
