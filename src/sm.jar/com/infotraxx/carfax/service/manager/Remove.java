package com.infotraxx.carfax.service.manager;

import com.infotraxx.carfax.service.util.App;
import com.infotraxx.carfax.service.util.Runner;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Removes the service.
 * @author Ed Jenkins
 */
public class Remove
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Remove.class);

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * Constructor.
     */
    public Remove()
    {
    }

    /**
     * Main entry point.
     * @param args command-line arguments.
     */
    public static void main(String[] args)
    {
        // Start
        App.start(args);
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
        try
        {
            r.run();
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        // Stop
        App.stop();
    }

}
