package com.infotraxx.carfax.server.config;

import com.infotraxx.carfax.server.util.App;
import com.infotraxx.carfax.server.util.XML;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.LineSeparator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Manages the list of ODBC resources to extract data from.
 * Uses odbc.xml.
 * @author Ed Jenkins
 */
public class ODBC
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(ODBC.class);

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * Filename.
     */
    private static String strFilename = null;

    /**
     * List of ODBC resources.
     */
    private static ArrayList<ODBCResource> alODBC = new ArrayList<ODBCResource>();

    /**
     * Constructor.
     */
    public ODBC()
    {
    }

    /**
     * Gets the filename.
     * @return the full path to the config file.
     */
    public static String getFilename()
    {
        if(strFilename == null)
        {
            // Assemble filename.
            StringBuilder sb = new StringBuilder(STRING_BUFFER_SIZE);
            sb.append(App.getDir(App.APP_DATA_DIR));
            sb.append(File.separator);
            sb.append("odbc.xml");
            strFilename = sb.toString();
        }
        // Return result.
        return strFilename;
    }

    /**
     * Reads the file.
     * If it doesn't exist, a new document is created.
     */
    public static void read()
    {
        // Create temp variables.
        int x = 0;
        int y = 0;
        // Reset values.
        ODBC.clear();
        // Read file.
        ODBC.getFilename();
        Document doc = XML.read(strFilename);
        if(doc == null)
        {
            ODBC.write();
            return;
        }
        Element eDoc = doc.getDocumentElement();
        // resource
        NodeList nlResource = eDoc.getElementsByTagName("resource");
        y = nlResource.getLength();
        for(x=0; x<y; x++)
        {
            Node nResource = nlResource.item(x);
            Element eResource = (Element)nResource;
            String strDSN = eResource.getAttribute("dsn");
            String strUsername = eResource.getAttribute("username");
            String strPassword = eResource.getAttribute("password");
            String strFilename = eResource.getAttribute("filename");
            String strDelimiter = eResource.getAttribute("delimiter");
            String strDescription = eResource.getAttribute("description");
            String strTempSQL = eResource.getTextContent();
            String strSQL = App.cleanString(strTempSQL);
            if(strDSN == null)
            {
                strDSN = "";
            }
            if(strUsername == null)
            {
                strUsername = "";
            }
            if(strPassword == null)
            {
                strPassword = "";
            }
            if(strFilename == null)
            {
                strFilename = "";
            }
            if(strDelimiter == null)
            {
                strDelimiter = ",";
            }
            if(strDescription == null)
            {
                strDescription = "";
            }
            if(strSQL == null)
            {
                strSQL = "";
            }
            ODBCResource r = new ODBCResource();
            r.dsn = strDSN;
            r.username = strUsername;
            r.password = strPassword;
            r.filename = strFilename;
            r.delimiter = strDelimiter;
            r.description = strDescription;
            r.sql = strSQL;
            alODBC.add(r);
        }
    }

    /**
     * Writes the file.
     */
    public static void write()
    {
        Document doc = XML.newDocument(null, "odbc", null, null);
        Element eDoc = doc.getDocumentElement();
        // resource
        int x = 0;
        Iterator iODBC = alODBC.iterator();
        while(iODBC.hasNext())
        {
            ODBCResource r = (ODBCResource)iODBC.next();
            Element eResource = doc.createElement("resource");
            eResource.setAttribute("dsn", r.dsn);
            eResource.setAttribute("username", r.username);
            eResource.setAttribute("password", r.password);
            eResource.setAttribute("filename", r.filename);
            eResource.setAttribute("delimiter", r.delimiter);
            eResource.setAttribute("description", r.description);
            String strSQL = App.cleanString(r.sql);
            eResource.setTextContent(strSQL);
            eDoc.appendChild(eResource);
            x++;
        }
        if(x == 0)
        {
            Element eResource = doc.createElement("resource");
            eResource.setAttribute("dsn", "example");
            eResource.setAttribute("username", "example");
            eResource.setAttribute("password", "example");
            eResource.setAttribute("filename", "example");
            eResource.setAttribute("delimiter", ",");
            eResource.setAttribute("description", "example");
            eResource.setTextContent("select 1 as example;");
            eDoc.appendChild(eResource);
        }
        // Save the file.
        ODBC.getFilename();
        XML.write(strFilename, null, null, "xml", LineSeparator.Windows, doc);
    }

    /**
     * Gets a list of ODBC resources.
     * @return a list of ODBC resources.
     */
    public static ArrayList getResources()
    {
        // Create return variable.
        ArrayList<ODBCResource> al = new ArrayList<ODBCResource>();
        // Create temp variables.
        Iterator i = alODBC.iterator();
        // Copy the list.
        while(i.hasNext())
        {
            ODBCResource r = (ODBCResource)i.next();
            al.add(r);
        }
        // Return results.
        return al;
    }

    /**
     * Clears the list.
     */
    public static void clear()
    {
        // Clear the list.
        alODBC.clear();
    }
    
    /**
     * Adds all items from the given list.
     * @param pODBC the list to add.
     */
    public static void addAll(ArrayList<ODBCResource> pODBC)
    {
        alODBC.addAll(pODBC);
    }

}
