package com.infotraxx.carfax.setup.install;

import com.infotraxx.carfax.setup.ui.mainwindow.Wizard;
import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.Pauser;
import com.infotraxx.carfax.setup.util.ProgressReporter;
import com.infotraxx.carfax.setup.util.Runner;
import com.jniwrapper.win32.registry.RegistryKey;
import com.jniwrapper.win32.registry.RegistryKeyValues;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Installs the application.
 * @author Ed Jenkins
 */
public class AppInstaller implements Installer
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(AppInstaller.class);

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * The wizard.
     */
    private Wizard wizard;

    /**
     * Progress reporter.
     */
    private ProgressReporter reporter;

    /**
     * Constructor.
     */
    public AppInstaller()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void setWizard(Wizard pWizard)
    {
        wizard = pWizard;
    }

    /**
     * {@inheritDoc}
     */
    public void setReporter(ProgressReporter pReporter)
    {
        reporter = pReporter;
    }

    /**
     * {@inheritDoc}
     */
    public int getSize()
    {
        // Create return variable.
        int r = 0;
        r++; // Add registry entries.
        r++; // Stop the service.
        r++; // Remove the service.
//        r++; // Install the service.
//        r++; // Start the service.
        r++; // Set AutoRun.
        r++; // Start the app.
        // Return result.
        return r;
    }
    
    /**
     * {@inheritDoc}
     */
    public void run()
    {
        // Install the application.
        this.writeRegistry();
        this.stopService();
        this.removeService();
//        this.installService();
//        this.startService();
        this.setAutoRun();
        this.startApp();
        // Go to the next panel.
        if(wizard != null)
        {
            wizard.goNext();
        }
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
        if(reporter != null)
        {
            reporter.report();
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
            if(reporter != null)
            {
                reporter.report();
            }
            Pauser.pause(5);
        }
        catch(Exception ex)
        {
            if(wizard != null)
            {
                wizard.goError(ex);
            }
            return;
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
            if(reporter != null)
            {
                reporter.report();
            }
            Pauser.pause(5);
        }
        catch(Exception ex)
        {
            if(wizard != null)
            {
                wizard.goError(ex);
            }
            return;
        }
    }
    
    /**
     * Installs the service.
     */
    private void installService()
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
        r.addParameter("--install");
        r.addParameter(strINI);
        try
        {
            r.run();
            if(reporter != null)
            {
                reporter.report();
            }
            Pauser.pause(5);
        }
        catch(Exception ex)
        {
            if(wizard != null)
            {
                wizard.goError(ex);
            }
            return;
        }
    }
    
    /**
     * Starts the service.
     */
    private void startService()
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
        r.addParameter("--start");
        r.addParameter(strINI);
        try
        {
            r.run();
            if(reporter != null)
            {
                reporter.report();
            }
            Pauser.pause(5);
        }
        catch(Exception ex)
        {
            if(wizard != null)
            {
                wizard.goError(ex);
            }
            return;
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
        if(reporter != null)
        {
            reporter.report();
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
            if(reporter != null)
            {
                reporter.report();
            }
            Pauser.pause(5);
        }
        catch(Exception ex)
        {
            if(wizard != null)
            {
                wizard.goError(ex);
            }
            return;
        }
    }
    
}
