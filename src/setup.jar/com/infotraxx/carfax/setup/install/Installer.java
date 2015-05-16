package com.infotraxx.carfax.setup.install;

import com.infotraxx.carfax.setup.ui.mainwindow.Wizard;
import com.infotraxx.carfax.setup.util.ProgressReporter;

/**
 * Installs things.
 * @author Ed Jenkins
 */
public interface Installer extends Runnable
{

    /**
     * Sets the wizard.
     * @param pWizard the wizard.
     */
    public void setWizard(Wizard pWizard);

    /**
     * Sets the progress reporter.
     * @param pReporter a progress reporter.
     */
    public void setReporter(ProgressReporter pReporter);

    /**
     * Gets the number of files to be installed.
     * @return the number of files to be installed.
     */
    public int getSize();

}
