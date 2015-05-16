package com.infotraxx.carfax.setup.ui.mainwindow;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

/**
 * Event handler for the Back button.
 * @author Ed Jenkins
 */
class BackOnClickAction extends AbstractAction implements WizardUser
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(BackOnClickAction.class);

    /**
     * The wizard.
     */
    private Wizard wiz;

    /**
     * Constructor.
     */
    public BackOnClickAction()
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
        wiz.goBack();
    }

    /**
     * {@inheritDoc}
     */
    public void setWizard(Wizard pWizard)
    {
        wiz = pWizard;
    }

}
