package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.util.Fonts;

import java.awt.BorderLayout;
import java.awt.SystemColor;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

/**
 * A panel for the title.
 * @author Ed Jenkins
 */
class TitlePanel extends GradientPanel implements Localized
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(TitlePanel.class);

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * The text to display.
     */
    private String strText = "";

    /**
     * The label.
     */
    private JLabel lblTitle = new JLabel();

    /**
     * Constructor.
     */
    public TitlePanel()
    {
        Border inner = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border outer = BorderFactory.createLoweredBevelBorder();
        Border b = BorderFactory.createCompoundBorder(outer, inner);
        this.setBorder(b);
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        lblTitle.setFont(Fonts.getFont(Fonts.TITLES));
        lblTitle.setForeground(SystemColor.text);
        lblTitle.setOpaque(false);
        this.add(lblTitle, BorderLayout.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    public void localize(Locale pLocale)
    {
        // Load resource bundle.
        rb = ResourceBundle.getBundle(this.getClass().getName(), pLocale);
        // Localize components.
        Localizer.localize(rb, this, "TitlePanel");
        Localizer.localize(rb, lblTitle, "lblTitle", null);
    }

    /**
     * Gets the text.
     * @return the text.
     */
    public String getText()
    {
        return this.strText;
    }

    /**
     * Sets the text.
     * @param pText the text.
     */
    public void setText(String pText)
    {
        this.strText = pText;
        lblTitle.setText(strText);
    }

}
