<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="properties">
        <xsl:text>#build.properties</xsl:text>
        <xsl:text>&#x0A;</xsl:text>
        <xsl:for-each select="property">
            <xsl:sort data-type="text" order="ascending" select="@name"/>
            <xsl:call-template name="property"/>
        </xsl:for-each>
    </xsl:template>
    <xsl:template name="property">
        <xsl:value-of select="@name"/>
        <xsl:text>=</xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>&#x0A;</xsl:text>
    </xsl:template>
</xsl:stylesheet>
