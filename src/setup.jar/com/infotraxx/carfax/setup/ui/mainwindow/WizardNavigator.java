package com.infotraxx.carfax.setup.ui.mainwindow;

/**
 * The wizard navigator interface.
 * @author Ed Jenkins
 */
interface WizardNavigator
{

    /**
     * Gets whether the back button is enabled.
     * @return true if the back button is enabled or false if not.
     */
    public boolean isBackEnabled();

    /**
     * Gets whether the next button is enabled.
     * @return true if the next button is enabled or false if not.
     */
    public boolean isNextEnabled();

    /**
     * Gets the ID of this panel.
     * @return the ID of this panel.
     */
    public String getID();

    /**
     * Gets the title of this panel.
     * @return the title of this panel.
     */
    public String getTitle();

    /**
     * Gets the ID of the previous panel.
     * @return the ID of the previous panel.
     */
    public String getPrevious();

    /**
     * Sets the ID of the previous panel.
     * @param pID the ID of the previous panel.
     */
    public void setPrevious(String pID);

    /**
     * Gets the ID of the next panel.
     * @return the ID of the next panel.
     */
    public String getNext();

    /**
     * Sets the ID of the next panel.
     * @param pID the ID of the next panel.
     */
    public void setNext(String pID);

    /**
     * Sets the focus to the component that should receive the focus when the panel is made visible.
     */
    public void setFocus();

    /**
     * Performs a task, such as installing an application or configuring a file.
     * Panels that collect information from the user should simply return.
     * Panels that provide status info to the user while performing a task should start their thing in a separate thread and return.
     * @param pWizard a reference to the wizard.
     * Use it to tell the wizard when you are ready to move on to the next screen.
     */
    public void doSomething(Wizard pWizard);

    /**
     * Saves configuration information.
     * This is also a good place to perform any logic needed to determine if you should branch off of the standard flow and go to a different panel than you would normally go to.
     * If there is any such logic, then you should always call setNext() from here, whether it be to set it to the default value or not.
     * After the WizardPanel calls this method, it will query getPrevious() or getNext() to see where to go to next, depending on which button the user pressed.
     */
    public void saveConfig();

}
