package com.infotraxx.carfax.setup.main;

import com.infotraxx.carfax.setup.ui.mainwindow.MainWindow;
//import com.infotraxx.carfax.setup.ui.systemcolorwindow.SystemColorWindow;
import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.Config;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * Main.
 * @author Ed Jenkins
 */
public class Main
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Main.class);

    /**
     * Constructor.
     */
    public Main()
    {
    }

    /**
     * Main entry point.
     * @param args command-line arguments.
     */
    public static void main(String[] args)
    {
        // Start the app.
        App.start(args);
        // Do nothing if this is a headless server.
        if(GraphicsEnvironment.isHeadless())
        {
            logger.debug("The graphics environment is headless.");
            return;
        }
        // Use native look and feel.
        try
        {
            String strLAF = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(strLAF);
        }
        catch(Exception ex)
        {
            logger.fatal(ex, ex);
        }
        // Log system information.
        Main.logSysInfo();
        // Show the main window.
        Locale locale = Locale.getDefault();
        MainWindow winMain = new MainWindow();
        winMain.localize(locale);
        winMain.setVisible(true);
        winMain.toFront();
/*
        // Show the system color window.
        SystemColorWindow winColor = new SystemColorWindow();
        winColor.setVisible(true);
        winColor.toFront();
*/
    }

    /**
     * Logs information about the system.
     */
    private static void logSysInfo()
    {
        // SysInfo.
        Runtime r = Runtime.getRuntime();
        NumberFormat nf = NumberFormat.getIntegerInstance();
        int intProcessors = r.availableProcessors();
        long lngFreeMemory = r.freeMemory();
        long lngTotalMemory = r.totalMemory();
        long lngMaxMemory = r.maxMemory();
        String strFreeMemory = nf.format(lngFreeMemory);
        String strTotalMemory = nf.format(lngTotalMemory);
        String strMaxMemory = nf.format(lngMaxMemory);
        logger.debug("Processors=" + intProcessors);
        logger.debug("FreeMemory=" + strFreeMemory);
        logger.debug("TotalMemory=" + strTotalMemory);
        logger.debug("MaxMemory=" + strMaxMemory);
        long lngUsableDiskSpace = 0L;
        long lngFreeDiskSpace = 0L;
        long lngTotalDiskSpace = 0L;
        try
        {
            String strDir = App.getDir(App.PROGRAM_DIR);
            String strDrive = strDir.substring(0, 2);
            File f = new File(strDrive);
            lngUsableDiskSpace = f.getUsableSpace();
            lngFreeDiskSpace = f.getFreeSpace();
            lngTotalDiskSpace = f.getTotalSpace();
            String strUsableDiskSpace = nf.format(lngUsableDiskSpace);
            String strFreeDiskSpace = nf.format(lngFreeDiskSpace);
            String strTotalDiskSpace = nf.format(lngTotalDiskSpace);
            logger.debug("UsableDiskSpace=" + strUsableDiskSpace);
            logger.debug("FreeDiskSpace=" + strFreeDiskSpace);
            logger.debug("TotalDiskSpace=" + strTotalDiskSpace);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        Config.setIntValue("sysinfo.processors", intProcessors);
        Config.setLongValue("sysinfo.memory.free", lngFreeMemory);
        Config.setLongValue("sysinfo.memory.total", lngTotalMemory);
        Config.setLongValue("sysinfo.memory.max", lngMaxMemory);
        Config.setLongValue("sysinfo.disk.usable", lngUsableDiskSpace);
        Config.setLongValue("sysinfo.disk.free", lngFreeDiskSpace);
        Config.setLongValue("sysinfo.disk.total", lngTotalDiskSpace);
    }

}
