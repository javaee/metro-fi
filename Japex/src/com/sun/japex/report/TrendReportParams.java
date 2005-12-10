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
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*
 *TrendReport title reportPath outputPath date offset [-d {driver}] [-m {means}] [-t {test}] [-H/HISTORY] [-O/OVERWRITE]
    where:
    title -- name of the chart to be generated
    reportPath -- path to where the report directory is
    outputPath -- path where the html report will be saved. 
    date -- a specific date in format "yyyy-MM-dd", including "Today", 
            from or to which a trend report shall be made. Default "Today"
    offset -- days, weeks, months or years from/to the above date a trend report will be created. 
            Supports format: xD where x is a positive or negative integer, and D indicates Days
            Similarily, xW, xM and xY are also support where W=Week, M=Month, and Y=Year. Default 1Y
    -d/driver driver1:driver2:... -- name of driver(s) for which a trend report is to be generated. All drivers if not specified.
    -m/means means1:means2:... -- one or more of the three means. Use keyword "all" to display all three means. All means specified will be placed on one chart.
    -t/testcases test1:test2:... -- specify test(s) for which a trend report will be created. Use keyword "all" 
                to display all testcases. 
    -gs/groupsize size -- when displaying all testcases, this parameter regulates the max number of testcases to be written on each chart. The default is 4.
 
    support version 0.1 interface:
    [driver] --
    options:
    -H or -History -- indicate that the trend report should be saved to a subdirectory in a timestamp format
    -O or -Overwrite -- overwrite existing report under "outputPath"
  
*/

public class TrendReportParams {
    static final int ARGS_TITLE = 0;
    static final int ARGS_PATH = 1;
    static final int ARGS_OUTPUTPATH = 2;
    static final int ARGS_DATE = 3;
    static final int ARGS_OFFSET = 4;
    static final int ARGS_DRIVER = 5;
    static final int ARGS_TESTCASE = 6;
        
    String _pageTitle;
    String _title;
    String _reportPath;
    String _outputPath;
    Date _from;
    Date _to;
    String[] _drivers;
    boolean _isDriverSpecified = false;
    String[] _means;
    boolean _isMeansSpecified = false;
    String[] _tests;
    boolean _isTestSpecified = false;
    boolean _overwrite = false;
    boolean _history = false;
    
    int _version = ReportConstants.TRENDREPORT_VERSION_01;
    
    private int _type = ReportConstants.REPORT_UNKNOWN;
    private int _smartGroupingSize = 4;
    private boolean _allDriverAdded = false;
    
    /** Creates a new instance of TrendReportParams */
    public TrendReportParams(String[] args) {
        args = checkOptions(args);
        _pageTitle = args[ARGS_TITLE];
        _title = args[ARGS_TITLE];
        _reportPath = args[ARGS_PATH];
        _outputPath = args[ARGS_OUTPUTPATH];
        if (_history) {
            DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
            _outputPath = _outputPath + df.format(new Date());            
        }
        
        if (args.length == 3)
            parseDates(ReportConstants.DEFAULT_STARTDATE, ReportConstants.DEFAULT_DATEOFFSET);
        else
            parseDates(args[ARGS_DATE], args[ARGS_OFFSET]);
        
        // support version 0.1
        if (_version == ReportConstants.TRENDREPORT_VERSION_01) {
            try {
                if (args.length > ARGS_DRIVER) {
                    _drivers = new String[1];
                    _drivers[0] = args[ARGS_DRIVER];
                    _isDriverSpecified = true;
                } else {
                    //use version 0.2 to handle Default condition (no driver specified
                    _version = ReportConstants.TRENDREPORT_VERSION_02;
                }
                if (args.length > ARGS_TESTCASE) {
                    ArrayList testcases = new ArrayList();
                    for (int i=ARGS_TESTCASE; i<args.length; i++) {
                        testcases.add((String)args[i]);
                    }
                    _tests = new String[(testcases.size())];
                    testcases.toArray(_tests);
                    //_tests = (String[])testcases.toArray();
                    _isTestSpecified = true;
                }
            } catch (Exception e) {
                e.printStackTrace();

            }        
        }
    }
    String[] checkOptions(String[] args) {
        ArrayList argList = new ArrayList();
        boolean isDriver = false;
        int count_driver = 0;
        boolean isMeans = false;
        boolean isTest = false;
        boolean isGroupSize = false;
        
        for (int i=0; i<args.length; i++) {
            if (isDriver) {
                _drivers = parseSwitchArg(args[i]);
                isDriver = false;
            }
            if (args[i].equalsIgnoreCase("-d") ||args[i].equalsIgnoreCase("-driver")) {
                _isDriverSpecified = true;
                _version = ReportConstants.TRENDREPORT_VERSION_02;
                isDriver = true;
            }
            if (isMeans) {
                _means = parseSwitchArg(args[i]);
                isMeans = false;
            }
            if (args[i].equalsIgnoreCase("-m")||args[i].equalsIgnoreCase("-means")) {
                _isMeansSpecified = true;
                _version = ReportConstants.TRENDREPORT_VERSION_02;
                isMeans = true;
            }
            if (isTest) {
                _tests = parseSwitchArg(args[i]);
                isTest = false;
            }
            if (args[i].equalsIgnoreCase("-t")||args[i].equalsIgnoreCase("-tests")) {
                _isTestSpecified = true;
                _version = ReportConstants.TRENDREPORT_VERSION_02;
                isTest = true;
            }
            
            if (isGroupSize) {
                _smartGroupingSize = Integer.parseInt(args[i]);
                isGroupSize = false;
            }
            if (args[i].equalsIgnoreCase("-gs")||args[i].equalsIgnoreCase("-groupsize")) {
                _version = ReportConstants.TRENDREPORT_VERSION_02;
                isGroupSize = true;
            }

            if (args[i].toUpperCase().equals("-O") || args[i].toUpperCase().equals("-OVERWRITE")) {
                _overwrite = true;
            } else if (args[i].toUpperCase().equals("-H") || args[i].toUpperCase().equals("-HISTORY")) {
                _history = true;
            } else {
                argList.add(args[i]);
            }
        }
        
        if (!_isDriverSpecified && !_isMeansSpecified && !_isTestSpecified) {
            _type = ReportConstants.REPORT_DEFAULT;
        } else if (_isDriverSpecified && !_isMeansSpecified && !_isTestSpecified) {
            if (_drivers.length == 1) {
                _type = ReportConstants.REPORT_ONEDRIVER;
            } else {
                _type = ReportConstants.REPORT_ALLDRIVERS;
            }
        } else if (_isDriverSpecified && _isMeansSpecified) {
            if (_drivers.length == 1) {
                _type = ReportConstants.REPORT_ONEDRIVER;
            } else {
                _type = ReportConstants.REPORT_ALLDRIVERS_ALLMEANS;
            }
        } else if (_isDriverSpecified && _isTestSpecified) {
            if (_tests[0].equalsIgnoreCase(ReportConstants.KEYWORD_ALL)) {
                _type = ReportConstants.REPORT_ALLTESTS;
            } else {
                _type = ReportConstants.REPORT_TESTS;
            }
        }
        
        String[] newArgs = new String[argList.size()];
        argList.toArray(newArgs);
        return newArgs;
    }
    
    String[] parseSwitchArg(String arg) {
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(arg, ":"); 
        
	while (tokenizer.hasMoreTokens()) {
            list.add(tokenizer.nextToken());
        }        
        String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }
    void parseDates(String date, String offset) {
        try {            
            Date date1 = null;
            
            if (date.toUpperCase().equals("TODAY")) {
                date1 = new Date();
            } else {
                DateFormat df= new SimpleDateFormat ("yyyy-MM-dd");
                date1 = df.parse(date);
            }
            
            int n = Integer.parseInt(offset.substring(0, offset.length()-1));
            char c = offset.toUpperCase().charAt(offset.length()-1);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date1);
            
            //GregorianCalendar cal = new GregorianCalendar();
            //cal.setTime(new Date());
            
            switch (c) {
                case 68:
                    cal.add(Calendar.DATE, n);
                    break;
                case 87:
                    cal.add(Calendar.WEEK_OF_YEAR, n);
                    break;
                case 77:
                    cal.add(Calendar.MONTH, n);
                    break;
                case 89:
                    cal.add(Calendar.YEAR, n);                    
            }
            
            if (n > 0) {
                _from = date1;
                _to = cal.getTime();
            } else {
                _from = cal.getTime();
                _to = date1;                
                cal.setTime(_to);
                cal.add(Calendar.DATE, 1);
                _to = cal.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    public int reportVersion() {
        return _version;
    }
    public int reportType() {
        return _type;
    }
    
    public void setReportType(int newType) {
        _type = newType;
    }
    public String pageTitle() {
        return _pageTitle;
    }
    public String title() {
        return _title;
    }
    public void setTitle(String title) {
        _title = title;
    }
    public String reportPath() {
        return _reportPath;
    }
    public String outputPath() {
        return _outputPath;
    }
    public Date dateFrom() {
        return _from;
    }
    public Date dateTo() {
        return _to;
    }
    public String[] driver() {
        return _drivers;
    }
    public void setDrivers(String[] drivers) {
        _drivers = drivers;
    }
    public int groupSize() {
        return _smartGroupingSize;
    }
    
    //check before trying to addDriver
    public boolean allDriversAdded() {
        return _allDriverAdded;
    }
    public void setAllDriversAdded(boolean all) {
        _allDriverAdded = all;
    }
    public void addDriver(String driver) {
        String[] drivers;
        if (_drivers == null) {
            _drivers = new String[1];
            _drivers[0] = driver;
        } else {
            if (!arrayContain(_drivers, driver)) {
                drivers = new String[(_drivers.length+1)];
                System.arraycopy(_drivers, 0, drivers, 0, _drivers.length);
                drivers[_drivers.length] = driver;
                _drivers = null;
                _drivers = drivers;
            }
            
        }
    }
    
    private boolean arrayContain(String[] array, String value) {
        for (int i=0; i<array.length; i++) {
            if (array[i].equals(value)) {
                return true;
            }
        }
        return false;
    }
    public boolean isDriverSpecified() {
        return _isDriverSpecified;
    }
    public String[] means() {
        return _means;
    }
    public boolean isMeansSpecified() {
        return _isMeansSpecified;
    }
    public String[] test() {
        return _tests;
    }
    public boolean isTestSpecified() {
        return _isTestSpecified;
    }
    public boolean overwrite() {
        return _overwrite;
    }
    public boolean history() {
        return _history;
    }
    
}
