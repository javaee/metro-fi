<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<!--$Id: fsdb2db.xsl,v 1.1 2005-05-27 22:23:14 joehw Exp $-->
<!--Enhancement of DocBook input instance assuming editing conventions
    adopted by UBL Forms Processing Subcommittee (FPSC).  This will
    collect editorial comments in a summary, title table row columns
    and inject meaningful break characters into XPath addresses.-->

<!--track the id of all editorial notes-->
<xsl:key name="notes" match="note[ @role='editorial' ]" use="@id"/>

<!--preserve document element and add a section at end if there exist any
    editorial notes-->
<xsl:template match="/*">
  <xsl:copy>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/>
    <xsl:variable name="notes" select="//note[@role='editorial']"/>
    <xsl:if test="$notes">
      <section>
        <title>Editorial note summary</title>
          <orderedlist>
            <xsl:for-each select="$notes">
              <listitem>
                <para>
                  <emphasis>
                    <xsl:variable name="ancestor-id" 
                      select="ancestor::section[1]/@id"/>
                    <xref linkend="{$ancestor-id}">
                      <xsl:if test="not($ancestor-id)">
                        <xsl:attribute name="linkend">
                          <xsl:value-of 
                            select="generate-id( ancestor::section[1] )"/>
                        </xsl:attribute>
                      </xsl:if>
                    </xref>
                  </emphasis>
                  <xsl:text> - </xsl:text>
                  <xref linkend="{@id}">
                    <xsl:if test="not(@id)">
                      <xsl:attribute name="linkend">
                        <xsl:value-of select="generate-id(.)"/>
                      </xsl:attribute>
                    </xsl:if>
                  </xref>
                </para>
                <xsl:apply-templates/>
              </listitem>
            </xsl:for-each>
          </orderedlist>
      </section>
    </xsl:if>
  </xsl:copy>
</xsl:template>

<!--handle an editorial note-->
<xsl:template match="note[@role='editorial']">
  <note id="{@id}" role='editorial'>
    <xsl:if test="not(@id)">
      <xsl:attribute name="id">
        <xsl:value-of select="generate-id(.)"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/>
  </note>
</xsl:template>

<!--id any section not already id'ed, so that editorial note hyperlinks
    have a point to point to-->
<xsl:template match="section[not(@id)]">
  <section id="{generate-id(.)}">
    <xsl:apply-templates select="@*|node()"/>
  </section>
</xsl:template>

<!--add a title row for columns-->
<xsl:template match="table[@role='xpath']/tgroup/tbody">
  <tbody>
    <xsl:copy-of select="@*"/>
    <row>
      <entry>
        <xsl:text>XPath address</xsl:text>
        <xsl:if test="count(row)>1">es</xsl:if>
      </entry>
    </row>
    <xsl:apply-templates/>
  </tbody>
</xsl:template>

<!--inject Unicode breaking and non-breaking characters to enhance display
    of XPath location paths-->
<xsl:template match="entry[@role='xpath']/literal">
  <xsl:variable name="dashes">
    <xsl:call-template name="replace">
      <xsl:with-param name="string" select="."/>
      <xsl:with-param name="old" select="'-'"/>
      <xsl:with-param name="new" select="'&#x2011;'"/><!--non-breakin hyphen-->
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="slashes">
    <xsl:call-template name="replace">
      <xsl:with-param name="string" select="string($dashes)"/>
      <xsl:with-param name="old" select="'/'"/>
      <xsl:with-param name="new" select="'/&#x200b;'"/><!--zero-width space-->
    </xsl:call-template>
  </xsl:variable>
  <literal>
    <xsl:value-of select="string($slashes)"/>
  </literal>
</xsl:template>

<xsl:template match="entry[@role='groups']"/>
<xsl:template match="entry[@role='rows']"/>
<xsl:template match="entry[@role='cols']"/>

<!--========================================================================-->

<!--replace all occurences of $old by $new in $string-->
<xsl:template name="replace">
  <xsl:param name="string"/>
  <xsl:param name="old" select="'????????'"/>
  <xsl:param name="new"/>
  <xsl:choose>
    <xsl:when test="contains( $string, $old )">
      <xsl:value-of select="substring-before( $string, $old )"/>
      <xsl:value-of select="$new"/>
      <xsl:call-template name="replace">
        <xsl:with-param name="string" 
                        select="substring-after( $string, $old )"/>
        <xsl:with-param name="old" select="$old"/>
        <xsl:with-param name="new" select="$new"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$string"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--========================================================================-->

<!--node identity transformation-->
<xsl:template match="@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
