package com.infotraxx.carfax.upgrade.util;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Maintains configuration information.
 * @author Ed Jenkins
 */
public class Config
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Config.class);

    /**
     * Buffer size.
     */
    public static final int BUFFER_SIZE = 8 * 1024;

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * Filename.
     */
    private static String strFilename = null;

    /**
     * The properties file.
     */
    private static SuperProperties p = new SuperProperties();

    /**
     * Read flag.
     */
    private static boolean booRead = false;

    /**
     * Constructor.
     */
    public Config()
    {
    }

    /**
     * Gets the filename.
     * @return the full path to the config file.
     */
    private static String getFilename()
    {
        if(strFilename == null)
        {
            // Assemble filename.
            StringBuilder sb = new StringBuilder(STRING_BUFFER_SIZE);
            sb.append(App.getDir(App.APP_DATA_DIR));
            sb.append(File.separator);
            sb.append("setup.ini");
            strFilename = sb.toString();
        }
        // Return result.
        return strFilename;
    }

    /**
     * Reads the file.
     */
    public static void read()
    {
        String strFile = Config.getFilename();
        p.read(strFile);
        // Set read flag.
        booRead = true;
    }

    /**
     * Writes the file.
     */
    private static void write()
    {
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        String strFile = Config.getFilename();
        p.write(strFile);
    }

    /**
     * Gets a value from the file.
     * @param pName a property name.
     * @return the property's value or an empty string if the property was not found.
     */
    public static String getValue(String pName)
    {
        return Config.getValue(pName, "");
    }

    /**
     * Gets a value from the file.
     * @param pName a property name.
     * @param pDefault the default value.
     * @return the property's value or the default value if the property was not found.
     */
    public static String getValue(String pName, String pDefault)
    {
        // Verify parameters.
        if(pName == null)
        {
            return null;
        }
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        // Get the value.
        String s = p.getProperty(pName);
        // Use default value if property does not exist.
        if(s == null)
        {
            return pDefault;
        }
        // Return result.
        return s;
    }

    /**
     * Gets an integer value from the file.
     * @param pName a property name.
     * @return the property's value or zero if the property was not found or the value is not an integer.
     */
    public static int getIntValue(String pName)
    {
        return Config.getIntValue(pName, 0);
    }

    /**
     * Gets an integer value from the file.
     * @param pName a property name.
     * @param pDefault the default value.
     * @return the property's value or the default value if the property was not found or the value is not an integer.
     */
    public static int getIntValue(String pName, int pDefault)
    {
        // Verify parameters.
        if(pName == null)
        {
            return pDefault;
        }
        // Create return variable.
        int i = pDefault;
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        // Get the value.
        String s = p.getProperty(pName);
        // Use default value if property does not exist.
        if(s == null)
        {
            return pDefault;
        }
        // Convert from String to int.
        try
        {
            i = Integer.parseInt(s);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return i;
    }

    /**
     * Gets a long value from the file.
     * @param pName a property name.
     * @return the property's value or zero if the property was not found or the value is not an integer.
     */
    public static long getLongValue(String pName)
    {
        return Config.getLongValue(pName, 0L);
    }

    /**
     * Gets a long value from the file.
     * @param pName a property name.
     * @param pDefault the default value.
     * @return the property's value or the default value if the property was not found or the value is not an integer.
     */
    public static long getLongValue(String pName, long pDefault)
    {
        // Verify parameters.
        if(pName == null)
        {
            return pDefault;
        }
        // Create return variable.
        long l = pDefault;
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        // Get the value.
        String s = p.getProperty(pName);
        // Use default value if property does not exist.
        if(s == null)
        {
            return pDefault;
        }
        // Convert from String to int.
        try
        {
            l = Long.parseLong(s);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return l;
    }

    /**
     * Gets a value from the file.
     * @param pName a property name.
     * @return true if the property was found and is "Y" or false if not.
     */
    public static boolean getBooleanValue(String pName)
    {
        // Verify parameters.
        if(pName == null)
        {
            return false;
        }
        // Create return variable.
        boolean r = false;
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        // Get the value.
        String s = p.getProperty(pName);
        // Use default value if property does not exist.
        if(s == null)
        {
            return false;
        }
        if(s.equalsIgnoreCase("Y"))
        {
            r = true;
        }
        // Return result.
        return r;
    }

    /**
     * Sets a value and saves the file.
     * @param pName the property's name.
     * @param pValue the property's value.
     * If pValue is null, the property will be removed.
     */
    public static void setValue(String pName, String pValue)
    {
        // Verify parameters.
        if(pName == null)
        {
            return;
        }
        if(pName.equals(""))
        {
            return;
        }
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        // Normalize the value.
        if(pValue == null)
        {
            p.remove(pName);
        }
        else
        {
            p.setProperty(pName, pValue);
        }
        logger.debug(pName + "=" + pValue);
        Config.write();
    }

    /**
     * Sets an integer value and saves the file.
     * @param pName the property's name.
     * @param pValue the property's value.
     */
    public static void setIntValue(String pName, int pValue)
    {
        // Verify parameters.
        if(pName == null)
        {
            return;
        }
        if(pName.equals(""))
        {
            return;
        }
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        // Normalize the value.
        String s = Integer.toString(pValue);
        p.setProperty(pName, s);
        logger.debug(pName + "=" + s);
        Config.write();
    }

    /**
     * Sets a long value and saves the file.
     * @param pName the property's name.
     * @param pValue the property's value.
     */
    public static void setLongValue(String pName, long pValue)
    {
        // Verify parameters.
        if(pName == null)
        {
            return;
        }
        if(pName.equals(""))
        {
            return;
        }
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        // Normalize the value.
        String s = Long.toString(pValue);
        p.setProperty(pName, s);
        logger.debug(pName + "=" + s);
        Config.write();
    }

    /**
     * Sets a boolean value and saves the file.
     * @param pName the property's name.
     * @param pValue the property's value.
     */
    public static void setBooleanValue(String pName, boolean pValue)
    {
        // Verify parameters.
        if(pName == null)
        {
            return;
        }
        if(pName.equals(""))
        {
            return;
        }
        // Read the file if it has not been read yet.
        if(booRead == false)
        {
            Config.read();
        }
        // Normalize the value.
        String s = "N";
        if(pValue == true)
        {
            s = "Y";
        }
        p.setProperty(pName, s);
        logger.debug(pName + "=" + s);
        Config.write();
    }

}
