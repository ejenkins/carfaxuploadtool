package com.infotraxx.carfax.setup.ui.systemcolorwindow;

import com.infotraxx.carfax.setup.util.App;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.log4j.Logger;

/**
 * Event handler for when the window is closing.
 * @author Ed Jenkins
 */
public class WindowClosingEvent extends WindowAdapter
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(WindowClosingEvent.class);

    /**
     * Constructor.
     */
    public WindowClosingEvent()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(WindowEvent e)
    {
        logger.debug("The window is closing.");
        App.stop();
    }

}
