<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />
    <xsl:decimal-format decimal-separator="." grouping-separator="," />
    <xsl:template match="checkstyle">
        <html dir="ltr" lang="en" xml:lang="en">
            <head>
                <title>Checkstyle Report</title>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <meta name="author" content="Ed Jenkins" />
                <style type="text/css">body { background-color:#FFFFFF; color:#000000; font:normal 80% arial,helvetica,sanserif; margin-left: 10; margin-right: 10; } h2 { font-size:140%; font-weight:bold; margin-bottom: 5; } h3 { background: #525D76; color: white; font-size:100%; font-weight:bold; margin-bottom: 0; margin-left: 2px; margin-right: 2px; padding: 5px; text-decoration: none; } table, th, td { border: none font-size:100%; } th, td { text-align: left; vertical-align: top; } th { background: #ccc; color: black; font-weight:bold; } .a td { background: #efefef; } .b td { background: #fff; } .right { text-align: right; }</style>
            </head>
            <body>
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td>
                            <img src="checkstyle.png" alt="Checkstyle logo" />
                        </td>
                        <td>
                            <table border="0" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td class="right">
                                        <h2>CheckStyle Audit</h2>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="right">
                                        Designed for use with
                                        <a href="http://checkstyle.sourceforge.net/">CheckStyle</a>
                                        and
                                        <a href="http://jakarta.apache.org">Ant</a>
                                        .
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
                <hr size="1" />
                <xsl:apply-templates select="." mode="summary" />
                <xsl:apply-templates select="." mode="files" />
                <xsl:for-each select="file">
                    <xsl:sort select="@name" />
                    <xsl:apply-templates select="." />
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
    <xsl:template name="alternated-row">
        <xsl:attribute name="class">
            <xsl:if test="position() mod 2 = 1">a</xsl:if>
            <xsl:if test="position() mod 2 = 0">b</xsl:if>
        </xsl:attribute>
    </xsl:template>
    <xsl:template match="checkstyle" mode="summary">
        <h3>Summary</h3>
        <xsl:variable name="errors" select="count(file/error)" />
        <xsl:variable name="files" select="count(file[error])" />
        <table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
            <tr>
                <th>Errors</th>
                <th>Files</th>
            </tr>
            <tr>
                <xsl:call-template name="alternated-row" />
                <td>
                    <xsl:value-of select="$errors" />
                </td>
                <td>
                    <xsl:value-of select="$files" />
                </td>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="checkstyle" mode="files">
        <xsl:if test="file[error]">
            <h3>Files</h3>
            <table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
                <tr>
                    <th>Errors</th>
                    <th>File</th>
                </tr>
                <xsl:for-each select="file[error]">
                    <xsl:sort select="@name" />
                    <xsl:variable name="errors" select="count(error)" />
                    <tr>
                        <xsl:call-template name="alternated-row" />
                        <td>
                            <a href="#{translate(@name, '/:\', '___')}">
                                <xsl:value-of select="$errors" />
                            </a>
                        </td>
                        <td>
                            <a href="#{translate(@name, '/:\', '___')}">
                                <xsl:value-of select="@name" />
                            </a>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </xsl:if>
    </xsl:template>
    <xsl:template match="file[error]">
        <a name="{translate(@name, '/:\', '___')}" />
        <h3>
            File
            <xsl:value-of select="@name" />
        </h3>
        <table class="log" border="0" cellpadding="5" cellspacing="2" width="100%">
            <tr>
                <th>Line</th>
                <th>Description</th>
            </tr>
            <xsl:for-each select="error">
                <tr>
                    <xsl:call-template name="alternated-row" />
                    <td>
                        <xsl:value-of select="@line" />
                    </td>
                    <td>
                        <xsl:value-of select="@message" />
                    </td>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>
</xsl:stylesheet>
