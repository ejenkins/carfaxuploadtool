package com.infotraxx.carfax.server.config;

import com.infotraxx.carfax.server.util.App;
import com.infotraxx.carfax.server.util.XML;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.LineSeparator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Manages the list of files to be uploaded.
 * Uses files.xml.
 * @author Ed Jenkins
 */
public class Files
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Files.class);

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * Filename.
     */
    private static String strFilename = null;

    /**
     * List of files and the last modified date.
     */
    private static TreeMap<String,Long> tmFiles = new TreeMap<String,Long>();

    /**
     * Constructor.
     */
    public Files()
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
            sb.append("files.xml");
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
        Files.clear();
        // Read file.
        Files.getFilename();
        Document doc = XML.read(strFilename);
        if(doc == null)
        {
            Files.write();
            return;
        }
        Element eDoc = doc.getDocumentElement();
        // file
        NodeList nlFile = eDoc.getElementsByTagName("file");
        y = nlFile.getLength();
        for(x=0; x<y; x++)
        {
            Node nFile = nlFile.item(x);
            Element eFile = (Element)nFile;
            String strTempFile = eFile.getTextContent();
            String strFile = strTempFile.trim();
            String strModified = eFile.getAttribute("modified");
            Long L = new Long(0L);
            if(strModified != null)
            {
                try
                {
                    L = Long.valueOf(strModified);
                }
                catch(Exception ex)
                {
                }
            }
            tmFiles.put(strFile, L);
        }
    }

    /**
     * Writes the file.
     */
    public static void write()
    {
        Document doc = XML.newDocument(null, "files", null, null);
        Element eDoc = doc.getDocumentElement();
        // file
        Set setFiles = tmFiles.keySet();
        Iterator iFiles = setFiles.iterator();
        while(iFiles.hasNext())
        {
            String s = (String)iFiles.next();
            Long L = tmFiles.get(s);
            String l = L.toString();
            long lngDateStamp = L.longValue();
            String strDateStamp = App.formatDateStamp(lngDateStamp);
            Element eFile = doc.createElement("file");
            eFile.setTextContent(s);
            eFile.setAttribute("modified", l);
            eFile.setAttribute("datestamp", strDateStamp);
            eDoc.appendChild(eFile);
        }
        // Save the file.
        Files.getFilename();
        XML.write(strFilename, null, null, "xml", LineSeparator.Windows, doc);
    }

    /**
     * Gets the number of files in the list.
     * @return the number of files in the list.
     */
    public static int getFileCount()
    {
        return tmFiles.size();
    }

    /**
     * Gets a list of files.
     * @return a list of files.
     */
    public static Vector getFiles()
    {
        // Create return variable.
        Vector<String> v = new Vector<String>();
        // Create temp variables.
        Set set = tmFiles.keySet();
        Iterator i = set.iterator();
        // Copy the list.
        while(i.hasNext())
        {
            String s1 = (String)i.next();
            String s2 = new String(s1);
            v.add(s2);
        }
        // Return results.
        return v;
    }

    /**
     * Gets the last modified time stamp of the specified file.
     * This is the timestamp the file had when it was last uploaded
     * @param pFilename the name of the file to look at.
     * @return the last modified time stamp or zero if pFilename is null,
     * pFilename is not in the list, or the file has not been uploaded.
     */
    public static long getModified(String pFilename)
    {
        // Create return variable.
        long r = 0L;
        // Verify parameters.
        if(pFilename == null)
        {
            return r;
        }
        // Make sure the file exists in the list.
        if(!tmFiles.containsKey(pFilename))
        {
            return r;
        }
        // Get time stamp.
        Long L = (Long)tmFiles.get(pFilename);
        r = L.longValue();
        // Return result.
        return r;
    }

    /**
     * Gets the last modified time stamp of the specified file.
     * This is the file's actual time stamp value.
     * @param pFilename the name of the file to look at.
     * @return the last modified time stamp or zero if pFilename is null or pFilename does not exist.
     */
    public static long lastModified(String pFilename)
    {
        // Create return variable.
        long r = 0L;
        // Get last modified time stamp.
        File f = null;
        try
        {
            f = new File(pFilename);
            if(f.exists())
            {
                r = f.lastModified();
            }
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return results.
        return r;
    }

    /**
     * Adds pFilename to the list.
     * Sets it's cached timestamp to zero.
     * If pFilename is already in the list, it is updated.
     * @param pFilename the name of the file to add.
     */
    public static void addFile(String pFilename)
    {
        Files.addFile(pFilename, 0L);
    }

    /**
     * Adds pFilename to the list.
     * Sets it's cached timestamp to zero.
     * If pFilename is already in the list, it is updated.
     * @param pFilename the name of the file to add.
     * @param pModified the last modified date.
     */
    public static void addFile(String pFilename, long pModified)
    {
        Long l = new Long(pModified);
        tmFiles.put(pFilename, l);
    }

    /**
     * Removes a file from the list.
     * @param pFilename the name of the file to add.
     */
    public static void removeFile(String pFilename)
    {
        if(!tmFiles.containsKey(pFilename))
        {
            return;
        }
        tmFiles.remove(pFilename);
    }
    
    /**
     * Clears the list.
     */
    public static void clear()
    {
        // Clear the list.
        tmFiles.clear();
        // Always send the server log file.
        StringBuilder sbLog = new StringBuilder(STRING_BUFFER_SIZE);
        sbLog.append(App.getDir(App.LOG_DIR));
        sbLog.append(File.separator);
        sbLog.append(App.getValue("app.name.short"));
        sbLog.append(".server.log");
        String strLog = sbLog.toString();
        Files.addFile(strLog);
    }

}
