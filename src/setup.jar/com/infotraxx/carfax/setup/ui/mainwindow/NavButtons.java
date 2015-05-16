package com.infotraxx.carfax.setup.ui.mainwindow;

/**
 * Enables and disables navigation buttons.
 * @author Ed Jenkins
 */
interface NavButtons
{

    /**
     * Gets whether the back button is enabled.
     * @return true if the back button is enabled or false if not.
     */
    public boolean isBackEnabled();

    /**
     * Sets whether the back button should be enabled.
     * @param pEnabled true to enable or false to disable.
     */
    public void setBackEnabled(boolean pEnabled);

    /**
     * Gets whether the next button is enabled.
     * @return true if the next button is enabled or false if not.
     */
    public boolean isNextEnabled();

    /**
     * Sets whether the next button should be enabled.
     * @param pEnabled true to enable or false to disable.
     */
    public void setNextEnabled(boolean pEnabled);

    /**
     * Indicates that the wizard is finished and the text of the cancel button should change from "Cancel" to "Finished".
     */
    public void setFinished();

    /**
     * Indicates that the wizard is not finished and the text of the cancel button should change from "Finished" to "Cancel".
     */
    public void setNotFinished();

}
