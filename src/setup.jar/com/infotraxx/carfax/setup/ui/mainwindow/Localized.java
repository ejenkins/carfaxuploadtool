package com.infotraxx.carfax.setup.ui.mainwindow;

import java.util.Locale;

/**
 * The localized interface.
 * All classes that implement this interface will be localized.
 * All text values are read from a file instead of being hard-coded.
 * @author Ed Jenkins
 */
public interface Localized
{

    /**
     * Localizes the component.
     * @param pLocale the locale to use to find a ResourceBundle.
     */
    public void localize(Locale pLocale);

}
