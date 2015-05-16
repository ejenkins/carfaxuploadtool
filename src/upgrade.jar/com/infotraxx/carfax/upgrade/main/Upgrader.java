package com.infotraxx.carfax.upgrade.main;

import com.infotraxx.carfax.upgrade.util.App;
import com.infotraxx.carfax.upgrade.util.Pauser;
import com.infotraxx.carfax.upgrade.util.Runner;
import com.jniwrapper.win32.registry.RegistryKey;
import com.jniwrapper.win32.registry.RegistryKeyValues;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Upgrades the application.
 * @author Ed Jenkins
 */
public class Upgrader
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Upgrader.class);

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * Constructor.
     */
    public Upgrader()
    {
    }

    /**
     * Upgrades the app.
     */
    public void upgrade()
    {
        // Update the registry.
        this.writeRegistry();
        this.stopService();
        this.removeService();
        this.setAutoRun();
        this.startApp();
    }
    
    /**
     * Writes product information to the registry to help with production support.
     */
    private void writeRegistry()
    {
        // Key names.
        String strCompany = App.getValue("app.company");
        String strAppNameLong = App.getValue("app.name.long");
        // Registry keys.
        RegistryKey HKLM = RegistryKey.LOCAL_MACHINE;
        RegistryKey rkSoftware = null;
        RegistryKey rkCompany = null;
        RegistryKey rkAppNameLong = null;
        RegistryKeyValues rkv = null;
        // Values
        String strAuthor = App.getValue("app.version.info.Author");
        String strBuildDate = App.getValue("app.version.info.BuildDate");
        String strBuildName = App.getValue("app.name.short");
        String strBuildNumber = App.getValue("app.build.number");
        String strBuildVersion = App.getValue("app.build.version");
        String strBuiltBy = App.getValue("app.version.info.BuiltBy");
        String strHelpDeskPhone = App.getValue("app.helpdesk.phone.general");
        String strHelpDeskURL = App.getValue("app.helpdesk.url");
        String strPackageName = App.getValue("app.package.name");
        String strProductVersion = App.getValue("app.version.info.ProductVersion");
        String strInstalledBy = System.getProperty("user.name");
        String strInstalledOn = App.getDateTimeString();
        try
        {
            rkSoftware = HKLM.openSubKey("SOFTWARE", true);
            rkCompany = rkSoftware.createSubKey(strCompany, true);
            rkAppNameLong = rkCompany.createSubKey(strAppNameLong, true);
            rkv = rkAppNameLong.values();
            rkv.put("Author", strAuthor);
            rkv.put("BuildDate", strBuildDate);
            rkv.put("BuildName", strBuildName);
            rkv.put("BuildNumber", strBuildNumber);
            rkv.put("BuildVersion", strBuildVersion);
            rkv.put("BuiltBy", strBuiltBy);
            rkv.put("HelpDeskPhone", strHelpDeskPhone);
            rkv.put("HelpDeskURL", strHelpDeskURL);
            rkv.put("InstalledBy", strInstalledBy);
            rkv.put("InstalledOn", strInstalledOn);
            rkv.put("PackageName", strPackageName);
            rkv.put("ProductVersion", strProductVersion);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            if(rkAppNameLong != null)
            {
                rkAppNameLong.close();
            }
            if(rkCompany != null)
            {
                rkCompany.close();
            }
            if(rkSoftware != null)
            {
                rkSoftware.close();
            }
        }
        Pauser.pause(2);
    }

    /**
     * Stops the service.
     */
    private void stopService()
    {
        String strAppNameShort = App.getValue("app.name.short");
        // CWD
        String strCWD = App.getDir(App.PROGRAM_DIR);
        // EXE
        StringBuilder sbEXE = new StringBuilder(STRING_BUFFER_SIZE);
        sbEXE.append(strCWD);
        sbEXE.append(File.separator);
        sbEXE.append(strAppNameShort);
        sbEXE.append(".service.exe");
        String strEXE = sbEXE.toString();
        // INI
        StringBuilder sbINI = new StringBuilder(STRING_BUFFER_SIZE);
        sbINI.append(strCWD);
        sbINI.append(File.separator);
        sbINI.append(strAppNameShort);
        sbINI.append(".service.ini");
        String strINI = sbINI.toString();
        // Run
        Runner r = new Runner();
        r.setCWD(strCWD);
        r.setProgram(strEXE);
        r.addParameter("--stop");
        r.addParameter(strINI);
        r.setRequired(false);
        try
        {
            r.run();
            Pauser.pause(5);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
    }
    
    /**
     * Removes the service.
     */
    private void removeService()
    {
        String strAppNameShort = App.getValue("app.name.short");
        // CWD
        String strCWD = App.getDir(App.PROGRAM_DIR);
        // EXE
        StringBuilder sbEXE = new StringBuilder(STRING_BUFFER_SIZE);
        sbEXE.append(strCWD);
        sbEXE.append(File.separator);
        sbEXE.append(strAppNameShort);
        sbEXE.append(".service.exe");
        String strEXE = sbEXE.toString();
        // INI
        StringBuilder sbINI = new StringBuilder(STRING_BUFFER_SIZE);
        sbINI.append(strCWD);
        sbINI.append(File.separator);
        sbINI.append(strAppNameShort);
        sbINI.append(".service.ini");
        String strINI = sbINI.toString();
        // Run
        Runner r = new Runner();
        r.setCWD(strCWD);
        r.setProgram(strEXE);
        r.addParameter("--remove");
        r.addParameter(strINI);
        r.setRequired(false);
        try
        {
            r.run();
            Pauser.pause(5);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
    }
    
    /**
     * Sets the app to run when the user logs in.
     */
    private void setAutoRun()
    {
        // Key names.
        String strAppNameShort = App.getValue("app.name.short");
        String strAppNameLong = App.getValue("app.name.long");
        // CWD
        String strCWD = App.getDir(App.PROGRAM_DIR);
        // EXE
        StringBuilder sbEXE = new StringBuilder(STRING_BUFFER_SIZE);
        sbEXE.append(strCWD);
        sbEXE.append(File.separator);
        sbEXE.append(strAppNameShort);
        sbEXE.append(".app.exe");
        String strEXE = sbEXE.toString();
        // Registry keys.
        RegistryKey HKLM = RegistryKey.LOCAL_MACHINE;
        RegistryKey rkRun = null;
        RegistryKeyValues rkv = null;
        // Values
        try
        {
            rkRun = HKLM.openSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);
            rkv = rkRun.values();
            rkv.put(strAppNameLong, strEXE);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            if(rkRun != null)
            {
                rkRun.close();
            }
        }
        Pauser.pause(2);
    }

    /**
     * Starts the app.
     */
    private void startApp()
    {
        String strAppNameShort = App.getValue("app.name.short");
        // CWD
        String strCWD = App.getDir(App.PROGRAM_DIR);
        // EXE
        StringBuilder sbEXE = new StringBuilder(STRING_BUFFER_SIZE);
        sbEXE.append(strCWD);
        sbEXE.append(File.separator);
        sbEXE.append(strAppNameShort);
        sbEXE.append(".app.exe");
        String strEXE = sbEXE.toString();
        // Run
        Runner r = new Runner();
        r.setCWD(strCWD);
        r.setProgram(strEXE);
        r.setMode(Runner.ASYNCHRONOUS);
        try
        {
            r.run();
            Pauser.pause(5);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
    }
    
}
