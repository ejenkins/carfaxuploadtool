package com.infotraxx.carfax.server.util;

import org.apache.log4j.Logger;

/**
 * Pauses for a while.
 * @author Ed Jenkins
 */
public class Pauser
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Pauser.class);

    /**
     * One second.
     */
    public static final int SECOND = 1000;

    /**
     * Constructor.
     */
    public Pauser()
    {
    }

    /**
     * Pauses.
     * @param pSeconds the number of seconds to pause.
     */
    public static void pause(int pSeconds)
    {
        String strLog = null;
        if(pSeconds == 1)
        {
            strLog = "Pausing for " + pSeconds + " second.";
        }
        else
        {
            strLog = "Pausing for " + pSeconds + " seconds.";
        }
        logger.debug(strLog);
        long ms = pSeconds * SECOND;
        Long l = new Long(ms);
        synchronized(l)
        {
            try
            {
                l.wait(ms);
            }
            catch(Exception ex)
            {
                logger.error(ex, ex);
            }
        }
    }

}
