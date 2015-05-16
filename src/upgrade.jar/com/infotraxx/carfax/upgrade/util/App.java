package com.infotraxx.carfax.upgrade.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;

/**
 * Provides system services.
 * Locates system, application, and data directories.
 * Loads build properties.
 * Reads config file.
 * Starts and stops the logger.
 * Provides a central point for the application to start and stop.
 * @author Ed Jenkins
 */
public class App
{

    /**
     * Root Logger.
     */
    private static Logger logger = Logger.getRootLogger();

    /**
     * Date format.
     */
    public static final String DATE_FORMAT = "MM/dd/yyyy";

    /**
     * Time format.
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * Date/time format.
     */
    public static final String DATE_TIME_FORMAT = "MM/dd/yyyy HH:mm:ss";

    /**
     * DateStamp format.
     */
    public static final String DATE_STAMP_FORMAT = "yyyy.MM.dd.HH.mm.ss";

    /**
     * The log format.
     */
    public static final String LOG_FORMAT = "%d{MM/dd/yyyy},%d{HH:mm:ss},%p,%c,%L,%M,%m%n";

    /**
     * Buffer size.
     */
    private static final int BUFFER_SIZE = 64 * 1024;

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * The name of the resource file we will read from.
     */
    private static final String BUILD_PROPERTIES = "res/build.properties";

    /**
     * The properties file.
     */
    private static SuperProperties p = new SuperProperties();

    /**
     * The default directory.
     */
    public static final String DEFAULT_DIR = ".";

    /**
     * Current Directory.
     * Example: "D:".
     */
    public static final String CURRENT_DIR = "CURRENT_DIR";

    /**
     * System Directory.
     * Example: "C:\WINNT".
     */
    public static final String SYSTEM_DIR = "SYSTEM_DIR";

    /**
     * System32 Directory.
     * Example: "C:\WINNT\system32".
     */
    public static final String SYSTEM32_DIR = "SYSTEM32_DIR";

    /**
     * Program Directory.
     * Example: "C:\Program Files\My App".
     */
    public static final String PROGRAM_DIR = "PROGRAM_DIR";

    /**
     * Log Directory.
     * Example: "C:\Program Files\My App".
     */
    public static final String LOG_DIR = "LOG_DIR";

    /**
     * Icon Directory.
     * Example: "C:\Documents and Settings\All Users\Start Menu\Programs\My App".
     */
    public static final String ICON_DIR = "ICON_DIR";

    /**
     * Home Directory.
     * Example: "C:\Documents and Settings\ejenkins".
     */
    public static final String HOME_DIR = "HOME_DIR";

    /**
     * Temp Directory.
     * Example: "C:\Documents and Settings\ejenkins\Local Settings\Temp".
     */
    public static final String TEMP_DIR = "TEMP_DIR";

    /**
     * User Data Directory.
     * Example: "C:\Documents and Settings\ejenkin6\Application Data\My App".
     */
    public static final String USER_DATA_DIR = "USER_DATA_DIR";

    /**
     * Application Data Directory.
     * Example: "C:\Documents and Settings\All Users\Application Data\My App".
     */
    public static final String APP_DATA_DIR = "APP_DATA_DIR";

    /**
     * The directory map.
     * The keys are the *_DIR constants.
     */
    private static final TreeMap<String, String> tmDirs = new TreeMap<String, String>();

    /**
     * Initialized flag.
     */
    private static boolean booInitialized = false;

    /**
     * Constructor.
     */
    public App()
    {
    }

    /**
     * Gets the Initialized flag.
     * Used to prevent logging before the logger has been initialized.
     * @return true if the logger has been initialized or false if not.
     */
    public static boolean isInitialized()
    {
        return booInitialized;
    }

    /**
     * Gets the current date as a string.
     * @return the current date as a string.
     */
    public static String getDateString()
    {
        // Get the current date.
        Date d = new Date();
        // Format it.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String s = sdf.format(d);
        // Return result.
        return s;
    }

    /**
     * Gets the current time as a string.
     * @return the current time as a string.
     */
    public static String getTimeString()
    {
        // Get the current date.
        Date d = new Date();
        // Format it.
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        String s = sdf.format(d);
        // Return result.
        return s;
    }

    /**
     * Gets the current date and time as a string.
     * @return the current date and time as a string.
     */
    public static String getDateTimeString()
    {
        // Get the current date.
        Date d = new Date();
        // Format it.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        String s = sdf.format(d);
        // Return result.
        return s;
    }

    /**
     * Formats a date.
     * @param pDate the date to be formatted.
     * @return the formatted date.
     */
    public static String formatDate(long pDate)
    {
        // Get the date.
        Date d = new Date(pDate);
        // Format it.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String s = sdf.format(d);
        // Return result.
        return s;
    }

    /**
     * Formats a time.
     * @param pDate the date to be formatted.
     * @return the formatted time.
     */
    public static String formatTime(long pDate)
    {
        // Get the date.
        Date d = new Date(pDate);
        // Format it.
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        String s = sdf.format(d);
        // Return result.
        return s;
    }

    /**
     * Formats a date and time.
     * @param pDate the date to be formatted.
     * @return the formatted date and time.
     */
    public static String formatDateTime(long pDate)
    {
        // Get the date.
        Date d = new Date(pDate);
        // Format it.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        String s = sdf.format(d);
        // Return result.
        return s;
    }

    /**
     * Formats a date and time.
     * @param pDate the date to be formatted.
     * @return the formatted date and time.
     */
    public static String formatDateStamp(long pDate)
    {
        // Get the date.
        Date d = new Date(pDate);
        // Format it.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_STAMP_FORMAT);
        String s = sdf.format(d);
        // Return result.
        return s;
    }

    /**
     * Gets the current date and time as a string.
     * @return the current date and time as a string.
     */
    public static String getDateStampString()
    {
        // Get the current date.
        Date d = new Date();
        // Format it.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_STAMP_FORMAT);
        String s = sdf.format(d);
        // Return result.
        return s;
    }

    /**
     * Gets the name of the local host.
     * @return the name of the local host.
     */
    public static String getLocalHostName()
    {
        // Create return variable.
        String s = null;
        // Create temp variables.
        InetAddress ia;
        // Get the local host name.
        try
        {
            ia = InetAddress.getLocalHost();
            s = ia.getHostName();
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
            s = "localhost";
        }
        // Return result.
        return s;
    }

    /**
     * Starts the application by performing common startup tasks.
     * - parses the command line
     * - reads build-time properties
     * - locates all directories that may be used
     * - initializes the logger
     * - reads the config file
     * @param args command-line arguments.
     */
    public static void start(String[] args)
    {
        // Get started.
        p.load(BUILD_PROPERTIES);
        CommandLine.parse(args);
        App.findDirectories();
        App.initializeLogger();
        booInitialized = true;
        // Say "Hi".
        logger.info("started");
        // Read config files.
        Config.read();
    }

    /**
     * Locates the system, application, and data directories that the application may use.
     * Also creates directories that don't exist.
     */
    private static void findDirectories()
    {
        // CURRENT_DIR
        String strUserDir = System.getProperty("user.dir");
        String strCurrentDir = trimDir(strUserDir);
        // SYSTEM_DIR
        String strSystemDir = System.getenv("SystemRoot");
        // SYSTEM32_DIR
        StringBuilder sbSystem32Dir = new StringBuilder(1024);
        sbSystem32Dir.append(strSystemDir);
        sbSystem32Dir.append(File.separator);
        sbSystem32Dir.append("system32");
        String strSystem32Dir = sbSystem32Dir.toString();
        // PROGRAM_DIR
        StringBuilder sbProgramDir = new StringBuilder(1024);
        sbProgramDir.append(System.getenv("ProgramFiles"));
        sbProgramDir.append(File.separator);
        sbProgramDir.append(App.getValue("app.name.long"));
        String strProgramDir = sbProgramDir.toString();
        // LOG_DIR
        String strLogDir = new String(strProgramDir);
        // ICON_DIR
        StringBuilder sbIconDir = new StringBuilder(1024);
        sbIconDir.append(System.getenv("ALLUSERSPROFILE"));
        sbIconDir.append(File.separator);
        sbIconDir.append("Start Menu");
        sbIconDir.append(File.separator);
        sbIconDir.append("Programs");
        sbIconDir.append(File.separator);
        sbIconDir.append(App.getValue("app.name.long"));
        String strIconDir = sbIconDir.toString();
        // HOME_DIR
        String strHomeDir = System.getProperty("user.home");
        // TEMP_DIR
        StringBuilder sbTempDir = new StringBuilder(1024);
        sbTempDir.append(System.getProperty("java.io.tmpdir"));
        sbTempDir.append(File.separator);
        sbTempDir.append(App.getValue("app.name.long"));
        String strTempDir = sbTempDir.toString();
        // USER_DATA_DIR
        StringBuilder sbUserDataDir = new StringBuilder(1024);
        sbUserDataDir.append(System.getProperty("user.home"));
        sbUserDataDir.append(File.separator);
        sbUserDataDir.append("Application Data");
        sbUserDataDir.append(File.separator);
        sbUserDataDir.append(App.getValue("app.name.long"));
        String strUserDataDir = sbUserDataDir.toString();
        // APP_DATA_DIR
        StringBuilder sbAppDataDir = new StringBuilder(1024);
        sbAppDataDir.append(System.getenv("ALLUSERSPROFILE"));
        sbAppDataDir.append(File.separator);
        sbAppDataDir.append("Application Data");
        sbAppDataDir.append(File.separator);
        sbAppDataDir.append(App.getValue("app.name.long"));
        String strAppDataDir = sbAppDataDir.toString();
        // Add directories to the map.
        tmDirs.put(App.CURRENT_DIR, strCurrentDir);
        tmDirs.put(App.SYSTEM_DIR, strSystemDir);
        tmDirs.put(App.SYSTEM32_DIR, strSystem32Dir);
        tmDirs.put(App.PROGRAM_DIR, strProgramDir);
        tmDirs.put(App.LOG_DIR, strLogDir);
        tmDirs.put(App.ICON_DIR, strIconDir);
        tmDirs.put(App.HOME_DIR, strHomeDir);
        tmDirs.put(App.TEMP_DIR, strTempDir);
        tmDirs.put(App.USER_DATA_DIR, strUserDataDir);
        tmDirs.put(App.APP_DATA_DIR, strAppDataDir);
        // Create directories that don't exist.
        Set set = tmDirs.keySet();
        Iterator i = set.iterator();
        while(i.hasNext())
        {
            String k = (String)i.next();
            String v = tmDirs.get(k);
            App.createDir(v);
        }
    }

    /**
     * Initializes the logger.
     */
    private static void initializeLogger()
    {
        // Set the log format.
        PatternLayout pl = new PatternLayout();
        pl.setConversionPattern(LOG_FORMAT);
        // Log to the console.
        OutputStreamWriter osw = new OutputStreamWriter(System.out);
        ConsoleAppender appConsole = new ConsoleAppender();
        appConsole.setName("Console");
        appConsole.setThreshold(Level.ALL);
        appConsole.setLayout(pl);
        appConsole.setWriter(osw);
        logger.addAppender(appConsole);
        // Log to a file.
        StringBuilder sbFilename = new StringBuilder(STRING_BUFFER_SIZE);
        sbFilename.append(App.getDir(App.LOG_DIR));
        sbFilename.append(File.separator);
        sbFilename.append(App.getValue("app.name.short"));
        sbFilename.append(".upgrade.log");
        String strFilename = sbFilename.toString();
        RollingFileAppender appFile = new RollingFileAppender();
        appFile.setName("File");
        appFile.setThreshold(Level.ALL);
        appFile.setLayout(pl);
        appFile.setFile(strFilename);
        appFile.setAppend(false);
        appFile.setBufferedIO(false);
        appFile.setMaxBackupIndex(0);
        appFile.setMaxFileSize(App.getValue("app.log.size"));
        appFile.activateOptions();
        logger.addAppender(appFile);
        // Set the level.
        logger.setLevel(Level.DEBUG);
        // Switch from root logger to class logger.
        logger = Logger.getLogger(App.class);
    }

    /**
     * Stops the application.
     */
    public static void stop()
    {
        // Say "Bye".
        logger.info("stopped");
        // Stop logging.
        logger.setLevel(Level.OFF);
        // The TelnetAppender displays a bogus error message when the server socket gets closed.
        // Let's turn off its internal logger so that doesn't happen.
        LogLog.setInternalDebugging(false);
        LogLog.setQuietMode(true);
        // Shut down all appenders.
        LogManager.shutdown();
        // Quit.
        System.exit(0);
    }

    /**
     * Gets a value from the file.
     * @param pName a property name.
     * @return the property's value or an empty string if the property was not found.
     */
    public static String getValue(String pName)
    {
        return App.getValue(pName, "");
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
        return App.getIntValue(pName, 0);
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
        // Get the value.
        String s = p.getProperty(pName);
        // Use default value if property does not exist.
        if(s == null)
        {
            return pDefault;
        }
        // Convert to an int.
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
        return App.getLongValue(pName, 0L);
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
        // Get the value.
        String s = p.getProperty(pName);
        // Use default value if property does not exist.
        if(s == null)
        {
            return pDefault;
        }
        // Convert to an int.
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
     * Trims the trailing file separator, if present, from the specified path.
     * @param pDir the path to trim.
     * @return a trimmed path.
     */
    public static String trimDir(String pDir)
    {
        // Verify parameters.
        if(pDir == null)
        {
            return null;
        }
        // Create return variable.
        String strDir = pDir;
        // Trim off a trailing file separator, which may appear if running from a CD.
        if(pDir.endsWith(File.separator))
        {
            int x = pDir.length() - 1;
            strDir = pDir.substring(0, x);
        }
        // Return result.
        return strDir;
    }

    /**
     * Creates a directory if it doesn't already exist. Creates all necessary subdirectories in the path as needed.
     * @param pDir the directory to create.
     * @return true if the directory was created or already existed or false if it didn't exist and we are unable to create it.
     */
    public static boolean createDir(String pDir)
    {
        // Verify parameters.
        if(pDir == null)
        {
            return false;
        }
        // Create return variable.
        boolean b = false;
        // Create the directory.
        File f = null;
        String s = null;
        try
        {
            f = new File(pDir);
            s = f.getCanonicalPath();
            f = new File(s);
            if(f.exists() && f.isDirectory())
            {
                b = true;
            }
            else
            {
                b = f.mkdirs();
//                logger.info("Created " + s);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return b;
    }

    /**
     * Creates a directory for a file if it doesn't already exist. Creates all necessary subdirectories in the path as needed.
     * @param pFile the full path of the file.
     * @return true if the directory was created or already existed or false if it didn't exist and we are unable to create it.
     */
    public static boolean createDirForFile(String pFile)
    {
        // Verify parameters.
        if(pFile == null)
        {
            return false;
        }
        // Create return variable.
        boolean b = false;
        // Create the parent directory.
        File f = null;
        String s = null;
        try
        {
            f = new File(pFile);
            s = f.getCanonicalPath();
            f = new File(s);
            s = f.getParent();
            f = new File(s);
            s = f.getCanonicalPath();
            f = new File(s);
            if(f.exists() && f.isDirectory())
            {
                b = true;
            }
            else
            {
                b = f.mkdirs();
//                logger.info("Created " + s);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return b;
    }

    /**
     * Gets a directory.
     * @param pDir the name of the directory to get. Must be one of the *_DIR constants.
     * @return the path to the specified directory. Returns DEFAULT_DIR if pDir is invalid.
     */
    public static String getDir(String pDir)
    {
        // Get the directory.
        String strDir = tmDirs.get(pDir);
        // If it's not in the cache, return the default value,
        // which points to the current directory.
        if(strDir == null)
        {
            return DEFAULT_DIR;
        }
        // Return result.
        return strDir;
    }

    /**
     * Makes a path from an array of parts.
     * @param pParts the directories and optional filename.
     * @return a full path.
     */
    public static String makePath(String[] pParts)
    {
        // Verify parameters.
        if(pParts == null)
        {
            return "";
        }
        // Create temp variables.
        int x = 0;
        int y = pParts.length;
        int z = y - 1;
        StringBuilder sbPath = new StringBuilder(1024);
        // Sanity check.
        if(y == 0)
        {
            return "";
        }
        if(y == 1)
        {
            return pParts[0];
        }
        // Loop through the array.
        for(x=0; x<y; x++)
        {
            sbPath.append(pParts[x]);
            if(x < z)
            {
                sbPath.append(File.separator);
            }
        }
        // Get result.
        String strPath = sbPath.toString();
        // Return result.
        return strPath;
    }

}
