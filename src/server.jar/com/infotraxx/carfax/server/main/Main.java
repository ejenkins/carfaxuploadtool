package com.infotraxx.carfax.server.main;

import com.infotraxx.carfax.server.util.App;
import com.jniwrapper.DefaultLibraryLoader;

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

    static
    {
        DefaultLibraryLoader dll = DefaultLibraryLoader.getInstance();
        dll.addPath("lib");
    }

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
        if(args == null)
        {
            App.start(args);
            return;
        }
        if(args.length == 0)
        {
            App.start(args);
            return;
        }
        String strCommand = args[0];
        if(strCommand.equalsIgnoreCase("start"))
        {
            App.start(args);
        }
        if(strCommand.equalsIgnoreCase("stop"))
        {
            App.stop();
        }
    }

}
