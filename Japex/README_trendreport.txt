Japex -- TrendReport


TrendReport generates Time-Series charts based on Japex reports. TrendReport version 0.1 was designed to use outside script to drive full trend report. The program itself can only create one chart at a time. TrendReport 0.2 update on top ofthe version 0.1 to support generating complete trend report. Both interfaces are supported with this version.

1. version 0.1
TrendReport title reportPath outputPath date offset [driver] [testcases] [-O/OVERWRITE]
    where:
    title -- name of the chart to be generated
    reportPath -- path where the TrendReport tool will search for any Japex reports
    outputPath -- path where charts and html index page will be saved.
    date -- a specific date in format "yyyy-MM-dd", including "Today", 
            from or to which a trend report shall be made
    offset -- days, weeks, months or years from/to the above date a trend report will be created. 
            Supports format: xD where x is a positive or negative integer, and D indicates Days
            Similarily, xW, xM and xY are also support where W=Week, M=Month, and Y=Year
    driver -- optional. Name of a driver for which trend report is to be generated. All drivers if not specified.
    testcase(s) -- specific test(s) for a driver for which a trend report will be created. Use keyword "all" 
                to display all testcases. If testcases are not specified, the tool will generate
                a means chart.
 
    options:
    -O or -Overwrite -- overwrite existing report under "outputPath"



2. version 0.2
In addition to the arguments supported in version 0.1, the followings have been added:
TrendReport title reportPath outputPath date offset [-d {driver}] [-m {means}] [-t {test}] [-H/HISTORY] [-O/OVERWRITE]
    -d {driver} -- support multiple drivers in a format {driver1, driver2...}. For example, adding Arithmatic means for FI/XML drivers on one chart
    -m {means} -- This is so that means may be displayed on separate charts. Will support keyword "all" to display all three means
    -t {test} -- specific test(s) in a driver for which a trend report will be created. Use keyword "all" 
                to display all testcases

if none of the switches are specified, TrendReport generates a report containing three charts. Each chart represents a "means" for all drivers. **This update supports only this feature. To use the feature, edit /bin/trendreport_default.sh by replacing the variables with your own settings and then execute the script.


