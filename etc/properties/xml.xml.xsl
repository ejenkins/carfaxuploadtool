<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" doctype-system="http://java.sun.com/dtd/properties.dtd" />
    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="properties">
        <properties>
            <comment>build.properties</comment>
            <xsl:for-each select="property">
                <xsl:sort data-type="text" order="ascending" select="@name" />
                <xsl:call-template name="property" />
            </xsl:for-each>
            <xsl:apply-templates />
        </properties>
    </xsl:template>
    <xsl:template name="property">
        <xsl:element name="entry">
            <xsl:attribute name="key">
                <xsl:value-of select="@name" />
            </xsl:attribute>
            <xsl:value-of select="normalize-space(@value)" />
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
