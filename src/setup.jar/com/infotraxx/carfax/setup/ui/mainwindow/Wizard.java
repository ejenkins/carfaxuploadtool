package com.infotraxx.carfax.setup.ui.mainwindow;

/**
 * The wizard interface.
 * @author Ed Jenkins
 */
public interface Wizard
{

    /**
     * Sets whether the next button should be enabled.
     * @param pEnabled true to enable or false to disable.
     */
    public void setNextEnabled(boolean pEnabled);

    /**
     * Go to the previous panel.
     */
    public void goBack();

    /**
     * Go to the next panel.
     */
    public void goNext();

    /**
     * Go to a specific panel.
     * @param pID the ID of the panel to go to.
     */
    public void goTo(String pID);

    /**
     * Finish the wizard.
     */
    public void goFinish();

    /**
     * Indicates that the wizard is finished and the text of the cancel button should change from "Cancel" to "Finished".
     */
    public void setFinished();

    /**
     * Indicates that the wizard is not finished and the text of the cancel button should change from "Finished" to "Cancel".
     */
    public void setNotFinished();

    /**
     * Finish the wizard with an error message.
     * @param t an error message and stack trace.
     */
    public void goError(Throwable t);

}
