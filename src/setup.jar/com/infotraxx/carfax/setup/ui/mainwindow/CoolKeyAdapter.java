package com.infotraxx.carfax.setup.ui.mainwindow;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Cool Key Adapter.
 * Handles Enter and Escape keys to make the wizard go forward and backward.
 * @author Ed Jenkins
 */
class CoolKeyAdapter extends KeyAdapter implements WizardUser
{

    /**
     * The wizard.
     */
    private Wizard wiz;
    
    /**
     * One second.
     */
    private static final long SECOND = 1000;
    
    /**
     * Timer.
     */
    private long lngTimer = 0L;
    
    /**
     * Current time.
     */
    private long lngNow = 0L;
    
    /**
     * Time gap since last time user hit Enter or Escape.
     */
    private long lngGap = 0L;

    /**
     * Constructor.
     */
    CoolKeyAdapter()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if(wiz == null)
        {
            return;
        }
        int k = e.getKeyCode();
        if(k == KeyEvent.VK_ENTER)
        {
            wiz.goNext();
/*
            // Prevent accidental double-clicking.
            lngNow = System.currentTimeMillis();
            lngGap = lngNow - lngTimer;
            if(lngGap > this.SECOND)
            {
                wiz.goNext();
            }
            // Advance the timer.
            lngTimer = lngNow;
*/
        }
        if(k == KeyEvent.VK_ESCAPE)
        {
            wiz.goBack();
/*
            // Prevent accidental double-clicking.
            lngNow = System.currentTimeMillis();
            lngGap = lngNow - lngTimer;
            if(lngGap > this.SECOND)
            {
                wiz.goBack();
            }
            // Advance the timer.
            lngTimer = lngNow;
*/
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void setWizard(Wizard pWizard)
    {
        this.wiz = pWizard;
    }

}
