package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.util.App;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

/**
 * Event handler for the Cancel/Finish button.
 * @author Ed Jenkins
 */
class CancelOnClickAction extends AbstractAction implements WizardUser
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(CancelOnClickAction.class);

    /**
     * The wizard.
     */
    private Wizard wiz;

    /**
     * Constructor.
     */
    public CancelOnClickAction()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e)
    {
        if(wiz == null)
        {
            return;
        }
        App.stop();
    }

    /**
     * {@inheritDoc}
     */
    public void setWizard(Wizard pWizard)
    {
        wiz = pWizard;
    }

}
