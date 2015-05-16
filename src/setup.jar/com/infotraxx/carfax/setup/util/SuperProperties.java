package com.infotraxx.carfax.setup.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * Extends Properties to store them in sorted order
 * and supports numbered properties.
 * @author Ed Jenkins
 */
public class SuperProperties extends Properties
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SuperProperties.class);

    /**
     * Use an 8K buffer.
     */
    public static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * A table of hex digits.
     */
    private static final char[] hexDigit =
    {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
    };

    /**
     * A map of key names and counts.
     */
    private TreeMap<String, Integer> tmKeys = new TreeMap<String, Integer>();

    /**
     * Constructor.
     */
    public SuperProperties()
    {
    }

    /**
     * Loads a resource.
     * @param pName the name of the resource to load.
     */
    public synchronized void load(String pName)
    {
        if(pName == null)
        {
            return;
        }
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            if(App.isInitialized())
            {
                logger.debug("Begin load " + pName);
            }
            Class c = this.getClass();
            ClassLoader cl = c.getClassLoader();
            URL url = cl.getResource(pName);
            String strURL = url.toString();
            is = url.openStream();
            bis = new BufferedInputStream(is, BUFFER_SIZE);
            this.clear();
            tmKeys.clear();
            this.load(bis);
            if(App.isInitialized())
            {
                logger.debug("End load " + pName);
            }
        }
        catch(Exception ex)
        {
            if(App.isInitialized())
            {
                logger.error(ex, ex);
            }
        }
        finally
        {
            if(bis != null)
            {
                try
                {
                    bis.close();
                }
                catch(Exception ex)
                {
                    if(App.isInitialized())
                    {
                        logger.error(ex, ex);
                    }
                }
            }
        }
    }

    /**
     * Reads a file.
     * @param pName the name of the file to read.
     */
    public synchronized void read(String pName)
    {
        if(pName == null)
        {
            return;
        }
        File f = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try
        {
            f = new File(pName);
            if(!f.exists())
            {
                return;
            }
            if(App.isInitialized())
            {
                logger.debug("Begin read " + pName);
            }
            fis = new FileInputStream(f);
            bis = new BufferedInputStream(fis, BUFFER_SIZE);
            this.clear();
            tmKeys.clear();
            this.load(bis);
            if(App.isInitialized())
            {
                logger.debug("End read " + pName);
            }
        }
        catch (Exception ex)
        {
            if(App.isInitialized())
            {
                logger.error(ex, ex);
            }
        }
        finally
        {
            if(bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (Exception ex)
                {
                    if(App.isInitialized())
                    {
                        logger.error(ex, ex);
                    }
                }
            }
        }
    }

    /**
     * Write a file.
     * @param pName the name of the file to write.
     */
    public synchronized void write(String pName)
    {
        if(pName == null)
        {
            return;
        }
        File f = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try
        {
            if(App.isInitialized())
            {
                logger.debug("Begin write " + pName);
            }
            f = new File(pName);
            if(!f.exists())
            {
                App.createDirForFile(pName);
            }
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos, BUFFER_SIZE);
            this.store(bos, null);
            if(App.isInitialized())
            {
                logger.debug("End write " + pName);
            }
        }
        catch (Exception ex)
        {
            if(App.isInitialized())
            {
                logger.error(ex, ex);
            }
        }
        finally
        {
            if(bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (Exception ex)
                {
                    if(App.isInitialized())
                    {
                        logger.error(ex, ex);
                    }
                }
            }
        }
    }

    /**
     * Gets a list of values from a numbered property.
     * @param key the base name of the properties to read.
     * @return a list of values.
     */
    public ArrayList getNumberedProperty(String key)
    {
        // Create return variable.
        ArrayList<String> r = new ArrayList<String>();
        // Verify parameters.
        if(key == null)
        {
            return r;
        }
        // Get values.
        Enumeration e = super.propertyNames();
        while(e.hasMoreElements())
        {
            String strKey = (String)e.nextElement();
            if(strKey.startsWith(key))
            {
                r.add(strKey);
            }
        }
        // Return result.
        return r;
    }

    /**
     * Sets a property.
     * The property name ("key") will have a number appended to it.
     * Each time this method is called with the same key, the number will be incremented.
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to key.
     */
    public void setNumberedProperty(String key, String value)
    {
        // Verify parameters.
        if(key == null)
        {
            return;
        }
        // Get next number.
        Integer IOld = tmKeys.get(key);
        if(IOld == null)
        {
            IOld = new Integer(0);
        }
        int iOld = IOld.intValue();
        int iNew = iOld + 1;
        Integer INew = new Integer(iNew);
        tmKeys.put(key, INew);
        // Create new key.
        String strNumber = INew.toString();
        StringBuilder sbKey = new StringBuilder(STRING_BUFFER_SIZE);
        sbKey.append(key);
        sbKey.append(".");
        sbKey.append(strNumber);
        String strKey = sbKey.toString();
        // Set property.
        super.setProperty(strKey, value);
    }

    /**
     * Removes all occurrences of a named property.
     * @param key the base name of the properties to remove.
     */
    public void removeNumberedProperty(String key)
    {
        // Verify parameters.
        if(key == null)
        {
            return;
        }
        // Build a list of keys to be removed.
        ArrayList<String> alKeys = new ArrayList<String>();
        Enumeration e = super.propertyNames();
        while(e.hasMoreElements())
        {
            String strKey = (String)e.nextElement();
            if(strKey.startsWith(key))
            {
                alKeys.add(strKey);
            }
        }
        // Remove the selected properties.
        Iterator i = alKeys.iterator();
        while(i.hasNext())
        {
            String s = (String)i.next();
            super.remove(s);
        }
        // Remove the key count.
        tmKeys.remove(key);
    }

    /**
     * Converts a nibble to a hex character.
     * @param nibble the nibble to convert.
     * @return a hex character.
     */
    private static char toHex(int nibble)
    {
        return hexDigit[(nibble & 0xF)];
    }

    /**
     * Escapes special characters and converts unicode characters.
     * @param pString the string to escape
     * @param pSpace true to escape a space character or false to not escape it.
     * @return a copy of the string with special characters escaped.
     */
    private String escape(String pString, boolean pSpace)
    {
        int len = pString.length();
        int bufLen = len * 2;
        if(bufLen < 0)
        {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder outBuffer = new StringBuilder(bufLen);
        for(int x=0; x<len; x++)
        {
            char aChar = pString.charAt(x);
            switch(aChar)
            {
                case ' ':
                {
                    if(x == 0 || pSpace)
                    {
                        outBuffer.append('\\');
                    }
                    outBuffer.append(' ');
                    break;
                }
                case '\t':
                {
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                }
                case '\n':
                {
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                }
                case '\r':
                {
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                }
                case '\f':
                {
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                }
                case '!':
                {
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                }
                case '#':
                {
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                }
                case ':':
                {
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                }
                case '=':
                {
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                }
                case '\\':
                {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    break;
                }
                default:
                {
                    if((aChar < 0x0020) || (aChar > 0x007E))
                    {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >>  8) & 0xF));
                        outBuffer.append(toHex((aChar >>  4) & 0xF));
                        outBuffer.append(toHex( aChar        & 0xF));
                    }
                    else
                    {
                        outBuffer.append(aChar);
                    }
                }
            }
        }
        return outBuffer.toString();
    }

    /**
     * Gets all values, sorted by keys.
     * @return a map of all keys and values.
     */
    public synchronized TreeMap<String,String> getAll()
    {
        // Create return variable.
        TreeMap<String,String> tm = new TreeMap<String,String>();
        // Copy values.
        Enumeration eKeys = keys();
        while(eKeys.hasMoreElements())
        {
            String strOldKey = (String)eKeys.nextElement();
            String strOldVal = (String)get(strOldKey);
            String strNewKey = strOldKey.trim();
            String strNewVal = strOldVal.trim();
            tm.put(strNewKey, strNewVal);
        }
        // Return result.
        return tm;
    }

    /**
     * Stores the properties in sorted order.
     * @param out an output stream.
     * @param comments a description of the property list.
     * @exception IOException if writing this property list to the specified output stream throws an <tt>IOException</tt>.
     */
    @Override
    public synchronized void store(OutputStream out, String comments) throws IOException
    {
        // Create a new TreeMap and copy all properties to it.
        TreeMap<String,String> tm = new TreeMap<String,String>();
        Enumeration eKeys = keys();
        while(eKeys.hasMoreElements())
        {
            String strOldKey = (String)eKeys.nextElement();
            String strOldVal = (String)get(strOldKey);
            String strNewKey = strOldKey.trim();
            String strNewVal = strOldVal.trim();
            tm.put(strNewKey, strNewVal);
        }
        // Buffer the output stream.
        OutputStreamWriter ow = new OutputStreamWriter(out, "8859_1");
        BufferedWriter bw = new BufferedWriter(ow, BUFFER_SIZE);
        // Write comments.
        if(comments != null)
        {
            bw.write("#");
            bw.write(comments);
            bw.newLine();
        }
        // Write date.
        Date d = new Date();
        String strDate = d.toString();
        bw.write("#");
        bw.write(strDate);
        bw.newLine();
        // Loop through the sorted keys and write the data.
        Set set = tm.keySet();
        Iterator i = set.iterator();
        while(i.hasNext())
        {
            String strKey = (String)i.next();
            String strVal = (String)get(strKey);
            strKey = escape(strKey, true);
            strVal = escape(strVal, false);
            bw.write(strKey);
            bw.write("=");
            bw.write(strVal);
            bw.newLine();
        }
        // Flush the stream.
        bw.flush();
    }

}
