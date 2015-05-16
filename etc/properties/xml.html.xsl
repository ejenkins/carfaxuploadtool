<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />
    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="properties">
        <html dir="ltr" lang="en" xml:lang="en">
            <head>
                <title>build.properties</title>
                <meta http-equiv="content-type" content="application/xhtml+xml; charset=UTF-8" />
            </head>
            <body>
                <table border="1" cellpadding="3" cellspacing="0" width="100%">
                    <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                        <th colspan="2">
                            <font size="+2">
                                <b>build.properties</b>
                            </font>
                        </th>
                    </tr>
                    <tr bgcolor="#CCCCFF" class="TableHeadingColor">
                        <th>Name</th>
                        <th>Value</th>
                    </tr>
                    <xsl:for-each select="property">
                        <xsl:sort data-type="text" order="ascending" select="@name" />
                        <xsl:call-template name="property" />
                    </xsl:for-each>
                    <xsl:apply-templates />
                </table>
            </body>
        </html>
    </xsl:template>
    <xsl:template name="property">
        <tr bgcolor="white" class="TableRowColor">
            <td>
                <xsl:value-of select="@name" />
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="normalize-space(@value) = ''">
                        <xsl:value-of select="'&amp;'" disable-output-escaping="yes" />
                        <xsl:value-of select="'nbsp;'" disable-output-escaping="yes" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@value" />
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
