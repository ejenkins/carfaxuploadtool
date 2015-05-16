package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.Fonts;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * The Banner panel.
 * @author Ed Jenkins
 */
class BannerPanel extends GradientPanel implements Localized
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(BannerPanel.class);

    /**
     * Icon.
     */
    public static final String ICON = "res/img/www.everaldo.com/smserver.png";

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * Icon.
     */
    private JLabel lblIcon = new JLabel();

    /**
     * Version Line 1.
     */
    private JLabel lblVersionLine1 = new JLabel();

    /**
     * Version Line 2.
     */
    private JLabel lblVersionLine2 = new JLabel();

    /**
     * Version Line 3.
     */
    private JLabel lblVersionLine3 = new JLabel();

    /**
     * Constructor.
     */
    public BannerPanel()
    {
        // Initialize
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        // Icon
        lblIcon.setOpaque(false);
        // Version.
        lblVersionLine1.setFont(Fonts.getFont(Fonts.NOTES));
        lblVersionLine1.setForeground(SystemColor.activeCaptionText);
        lblVersionLine2.setFont(Fonts.getFont(Fonts.NOTES));
        lblVersionLine2.setForeground(SystemColor.activeCaptionText);
        lblVersionLine3.setFont(Fonts.getFont(Fonts.NOTES));
        lblVersionLine3.setForeground(SystemColor.activeCaptionText);
        // Set version info.
//        lblVersionLine1.setText(App.getValue("app.version.label.line1"));
//        lblVersionLine2.setText(App.getValue("app.version.label.line2"));
//        lblVersionLine3.setText(App.getValue("app.version.label.line3"));
        // Box it up.
        Box box = new Box(BoxLayout.PAGE_AXIS);
        box.add(lblVersionLine1);
        box.add(lblVersionLine2);
        box.add(lblVersionLine3);
        // Add components to the panel.
        JPanel pnlVersion = new JPanel();
        pnlVersion.setLayout(new BorderLayout());
        pnlVersion.setOpaque(false);
        pnlVersion.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pnlVersion.add(box, BorderLayout.SOUTH);
        this.add(lblIcon, BorderLayout.NORTH);
        this.add(pnlVersion, BorderLayout.SOUTH);
    }

    /**
     * {@inheritDoc}
     */
    public void localize(Locale pLocale)
    {
        // Load resource bundle.
        rb = ResourceBundle.getBundle(this.getClass().getName(), pLocale);
        // Localize components.
        Localizer.localize(rb, this, "BannerPanel");
        Localizer.localize(rb, lblIcon, "lblIcon", null);
        String strVersionLine1 = App.getValue("app.version.label.line1");
        String strVersionLine2 = App.getValue("app.version.label.line2");
        String strVersionLine3 = App.getValue("app.version.label.line3");
        String[] o1 = { strVersionLine1 };
        String[] o2 = { strVersionLine2 };
        String[] o3 = { strVersionLine3 };
        Localizer.localize(rb, lblIcon, "lblIcon", null);
        Localizer.localize(rb, lblVersionLine1, "lblVersionLine1", o1);
        Localizer.localize(rb, lblVersionLine2, "lblVersionLine1", o2);
        Localizer.localize(rb, lblVersionLine3, "lblVersionLine1", o3);
        lblIcon.setIcon(App.getIcon(ICON, rb.getString("lblIcon.description")));
    }

}
