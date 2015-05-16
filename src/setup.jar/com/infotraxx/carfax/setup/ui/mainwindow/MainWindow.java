package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.util.App;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemColor;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

/**
 * This is the main window.
 * @author Ed Jenkins
 */
public class MainWindow extends JFrame implements Localized
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(MainWindow.class);

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * Set the default window size to 768x568.
     */
    public static final Dimension dimSize = new Dimension(768, 568);

    /**
     * The banner panel.
     */
    BannerPanel pnlBanner = new BannerPanel();

    /**
     * The wizard panel.
     */
    WizardPanel pnlWizard = new WizardPanel();

    /**
     * Event handler for when the user closes MainWindow.
     */
    WindowClosingEvent evtWindowClosingEvent = new WindowClosingEvent();

    /**
     * Constructor.
     */
    public MainWindow()
    {
        // Get 16x16 icon.
        StringBuilder sbURL16 = new StringBuilder(1024);
        sbURL16.append("res/icons/");
        sbURL16.append(App.getValue("app.name.short"));
        sbURL16.append(".16.png");
        String strURL16 = sbURL16.toString();
        Image ico16 = App.getImage(strURL16);
        // Get 32x32 icon.
        StringBuilder sbURL32 = new StringBuilder(1024);
        sbURL32.append("res/icons/");
        sbURL32.append(App.getValue("app.name.short"));
        sbURL32.append(".32.png");
        String strURL32 = sbURL32.toString();
        Image ico32 = App.getImage(strURL32);
        // Set the icons.
        Vector<Image> vecIcons = new Vector<Image>();
        if(ico16 != null)
        {
            vecIcons.add(ico16);
        }
        if(ico32 != null)
        {
            vecIcons.add(ico32);
        }
        if(vecIcons.size() > 0)
        {
            this.setIconImages(vecIcons);
        }
        // Hook up the close button.
        this.addWindowListener(evtWindowClosingEvent);
        // Initialize the content pane.
        JPanel cp = new JPanel(new BorderLayout());
        cp.setBorder(new EmptyBorder(10, 10, 10, 10));
        cp.setBackground(SystemColor.activeCaptionBorder);
        this.setContentPane(cp);
        cp.add(pnlBanner, BorderLayout.WEST);
        cp.add(pnlWizard, BorderLayout.CENTER);
        // Center the window on the screen.
        this.pack();
        this.setLocationRelativeTo(null);
    }

    /**
     * {@inheritDoc}
     */
    public void localize(Locale pLocale)
    {
        // Load resource bundle.
        rb = ResourceBundle.getBundle(this.getClass().getName(), pLocale);
        // Localize components.
        Localizer.localize(rb, this, "MainWindow");
        pnlBanner.localize(pLocale);
        pnlWizard.localize(pLocale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMinimumSize()
    {
        return dimSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize()
    {
        return dimSize;
    }

}
