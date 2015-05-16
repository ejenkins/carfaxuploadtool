package com.infotraxx.carfax.upgrade.main;

import com.infotraxx.carfax.upgrade.util.App;
import com.infotraxx.carfax.upgrade.util.Config;

import java.io.File;
import java.text.NumberFormat;

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
        // Log system information.
        Main.logSysInfo();
        // Upgrade.
        Upgrader upgrader = new Upgrader();
        upgrader.upgrade();
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
