<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:rep="http://www.sun.com/japex/testSuiteReport"
                xmlns:extrep="http://www.sun.com/japex/extendedTestSuiteReport"
                version='1.0'>

<xsl:template match="rep:testSuiteReport">
   <html xmlns="http://www.w3.org/1999/xhtml">
   <link href="report.css" type="text/css" rel="stylesheet"/>
   <head><h1>Japex Report: <xsl:value-of select="@name"/></h1></head>
   <body>
      <ul>
      <xsl:for-each select="*[not(@name)]">
          <xsl:sort select="name()"/>
          <li><xsl:value-of select="name()"/>
              <xsl:text>: </xsl:text>
              <xsl:value-of select="."/></li>
      </xsl:for-each>
      </ul>

      <!-- Generate result summary section -->
      <xsl:call-template name="resultsSummary"/>
      
      <br/><br/>
      <center><img src="{/*/extrep:resultChart}"/></center>
      <br/><br/>
      
      <!-- Generate detailed result per driver -->
      <xsl:for-each select="rep:driver">
        <xsl:call-template name="resultsPerDriver"/>
      </xsl:for-each>
      
      <xsl:for-each select="/*/extrep:testCaseChart">
          <br/><br/>
          <center><img src="{.}"/></center>
          <br/><br/>
      </xsl:for-each>
      
      <br/>
      <small>
        <hr/><i>
        <font size="-2">
        Author: Santiago Pericas-Geertsen (Sun Microsystems)
        </font></i>
      </small>
  </body>   
  </html>
</xsl:template>

<xsl:template name="resultsSummary">
    <h2>Result Summary 
    (<xsl:value-of select="/*/rep:testSuiteReport/rep:resultUnit"/>)</h2>
    
    <table width="80%" border="1">
        <thead>
	       <tr><th width="15%"><b>driver</b></th>
                   <xsl:for-each select="rep:driver[1]/*[not(@name)]">
                       <xsl:sort select="name()"/>
                      <th><b><xsl:value-of select="name()"/></b></th>
                   </xsl:for-each>
               </tr>
        </thead>
        <tbody>
            <xsl:for-each select="rep:driver">
	       <tr><td align="right">
                      <xsl:value-of select="@name"/></td>
                   <xsl:for-each select="*[not(@name)]">
                       <xsl:sort select="name()"/>
                       <td align="right"><xsl:value-of select="."/></td>
                   </xsl:for-each>
               </tr>
            </xsl:for-each>
        </tbody>
    </table>
</xsl:template>

<xsl:template name="resultsPerDriver">
    <h2>Driver: <xsl:value-of select="@name"/></h2>
    
    <table width="80%" border="1">
        <thead>
	       <tr><th><b>testCase</b></th>
                   <xsl:for-each select="rep:testCase[1]/*">
                      <xsl:sort select="name()"/>
                      <th><b><xsl:value-of select="name()"/></b></th>
                   </xsl:for-each>
               </tr>
        </thead>
        <tbody>
            <xsl:for-each select="rep:testCase">
	       <tr><td align="right">
                      <xsl:value-of select="@name"/></td>
                   <xsl:for-each select="*">
                       <xsl:sort select="name()"/>
                       <td align="right"><xsl:value-of select="."/></td>
                   </xsl:for-each>
               </tr>
            </xsl:for-each>
        </tbody>
    </table>
</xsl:template>

<xsl:template match="text()"/>

</xsl:stylesheet>
