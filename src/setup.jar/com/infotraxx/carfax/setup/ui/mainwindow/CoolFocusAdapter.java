package com.infotraxx.carfax.setup.ui.mainwindow;

import java.awt.Component;
import java.awt.SystemColor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Cool Focus Adapter.
 * Changes background colour for active text fields.
 * @author Ed Jenkins
 */
class CoolFocusAdapter extends FocusAdapter
{

    /**
     * The background colour for active fields.
     */
    public static final SystemColor ACTIVE = SystemColor.inactiveCaptionText;

    /**
     * The background colour for inactive fields.
     */
    public static final SystemColor INACTIVE = SystemColor.activeCaptionText;

    /**
     * Constructor.
     */
    CoolFocusAdapter()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusGained(FocusEvent e)
    {
        if(e.isTemporary())
        {
            return;
        }
        Component c = (Component)e.getSource();
        c.setBackground(ACTIVE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusLost(FocusEvent e)
    {
        if(e.isTemporary())
        {
            return;
        }
        Component c = (Component)e.getSource();
        c.setBackground(INACTIVE);
    }

}
