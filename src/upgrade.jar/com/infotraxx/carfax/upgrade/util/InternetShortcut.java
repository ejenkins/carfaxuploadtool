package com.infotraxx.carfax.upgrade.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

/**
 * Creates icons.
 * @author Ed Jenkins
 */
public class InternetShortcut
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(InternetShortcut.class);
    
    /**
     * Buffer size.
     */
    public static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * URL.
     */
    private String strURL = null;
    
    /**
     * IconFile.
     */
    private String strIconFile = null;
    
    /**
     * IconIndex.
     */
    private String strIconIndex = null;
    
    /**
     * Filename.
     */
    private String strFilename = null;

    /**
     * Constructor.
     */
    public InternetShortcut()
    {
    }
    
    /**
     * Gets the URL.
     * @return the URL.
     */
    public String getURL()
    {
        return strURL;
    }
    
    /**
     * Sets the URL.
     * @param pURL the URL.
     */
    public void setURL(String pURL)
    {
        strURL = pURL;
    }
    
    /**
     * Gets the IconFile.
     * @return the IconFile.
     */
    public String getIconFile()
    {
        return strIconFile;
    }
    
    /**
     * Sets the IconFile.
     * @param pIconFile the IconFile.
     */
    public void setIconFile(String pIconFile)
    {
        strIconFile = pIconFile;
    }

    /**
     * Gets the IconIndex.
     * @return the IconIndex.
     */
    public String getIconIndex()
    {
        return strIconIndex;
    }
    
    /**
     * Sets the IconIndex.
     * @param pIconIndex the IconIndex.
     */
    public void setIconIndex(String pIconIndex)
    {
        strIconIndex = pIconIndex;
    }
    
    /**
     * Gets the Filename.
     * @return the Filename.
     */
    public String getFilename()
    {
        return strFilename;
    }
    
    /**
     * Sets the Filename.
     * Must have a .url extension.
     * @param pFilename the Filename.
     */
    public void setFilename(String pFilename)
    {
        strFilename = pFilename;
    }
    
    /**
     * Saves the file.
     * <pre>
     * [InternetShortcut]<br/>
     * URL=C:\Program Files\My Program\program.exe<br/>
     * IconFile=C:\Program Files\My Program\program.exe<br/>
     * IconIndex=0<br/>
     * </pre>
     */
    public void write()
    {
        // Verify state.
        if(strFilename == null)
        {
            return;
        }
        // Create temp variables.
        File f = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try
        {
            // Create the file.
            App.createDirForFile(strFilename);
            f = new File(strFilename);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw, BUFFER_SIZE);
            pw = new PrintWriter(bw);
            pw.println("[InternetShortcut]");
            // URL
            pw.print("URL=");
            if(strURL != null)
            {
                pw.print(strURL);
            }
            pw.println();
            // IconFile
            pw.print("IconFile=");
            if(strIconFile == null)
            {
                pw.print(strURL);
            }
            else
            {
                pw.print(strIconFile);
            }
            pw.println();
            // IconIndex
            pw.print("IconIndex=");
            if(strIconIndex == null)
            {
                pw.print("0");
            }
            else
            {
                pw.print(strIconIndex);
            }
            pw.println();
            logger.debug("Created icon.");
            logger.debug("Filename=" + strFilename);
            logger.debug("Target=" + strURL);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            if(pw != null)
            {
                try
                {
                    pw.close();
                }
                catch(Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
        }
    }

}
