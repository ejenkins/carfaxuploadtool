package com.infotraxx.carfax.setup.ui.mainwindow;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

/**
 * Event handler for the Next button.
 * @author Ed Jenkins
 */
class NextOnClickAction extends AbstractAction implements WizardUser
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(NextOnClickAction.class);

    /**
     * The wizard.
     */
    private Wizard wiz;

    /**
     * Constructor.
     */
    public NextOnClickAction()
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
        wiz.goNext();
    }

    /**
     * {@inheritDoc}
     */
    public void setWizard(Wizard pWizard)
    {
        wiz = pWizard;
    }

}
