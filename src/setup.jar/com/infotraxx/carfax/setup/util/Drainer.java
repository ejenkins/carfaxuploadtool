package com.infotraxx.carfax.setup.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * Drains the output streams from a child process.
 * See <a href="http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html" target="_new">When Runtime.exec() won't</a>
 * @author Ed Jenkins
 * @see Runner
 */
public class Drainer extends Thread
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Drainer.class);

    /**
     * The stream to drain.
     */
    private InputStream is;

    /**
     * Constructor.
     */
    public Drainer()
    {
    }

    /**
     * Sets the stream.
     * @param pStream the stream to drain.
     */
    public void setStream(InputStream pStream)
    {
        is = pStream;
    }

    /**
     * Drains the streams from a process.
     * @param pProcess the process to drain.
     */
    public static void drain(Process pProcess)
    {
        // Verify parameters.
        if(pProcess == null)
        {
            return;
        }
        InputStream isOut = null;
        InputStream isErr = null;
        try
        {
            isOut = pProcess.getInputStream();
            isErr = pProcess.getErrorStream();
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
            return;
        }
        Drainer out = new Drainer();
        Drainer err = new Drainer();
        out.setStream(isOut);
        err.setStream(isErr);
        out.start();
        err.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        // Verify state.
        if(is == null)
        {
            return;
        }
        try
        {
            // Buffer the input.
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String l = null;
            String s = null;
            // Read all input until EOF.
            while (true)
            {
                // Read a line.
                l = br.readLine();
                // Quit when the program quits.
                if(l == null)
                {
                    break;
                }
                // Sometimes a Form Feed will appear for no reason.
                // Get rid of it.
                s = l.trim();
                // Merge the program's output with our own.
                logger.debug(s);
            }
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
    }

}
