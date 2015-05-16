package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.Fonts;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

/**
 * The navigation panel.
 * @author Ed Jenkins
 */
class NavPanel extends JPanel implements Localized, NavButtons, WizardUser
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(NavPanel.class);

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * The Back button.
     */
    private JButton btnBack = new JButton();

    /**
     * The Next button.
     */
    private JButton btnNext = new JButton();

    /**
     * The Cancel button.
     */
    private JButton btnCancel = new JButton();

    /**
     * Event handler for the Back button.
     */
    private BackOnClickAction actBackOnClick = new BackOnClickAction();

    /**
     * Event handler for the Next button.
     */
    private NextOnClickAction actNextOnClick = new NextOnClickAction();

    /**
     * Event handler for the Cancel button.
     */
    private CancelOnClickAction actCancelOnClick = new CancelOnClickAction();

    /**
     * The wizard.
     */
    private Wizard wiz;

    /**
     * The Finished flag.
     */
    private boolean booFinished;

    /**
     * Constructor.
     */
    public NavPanel()
    {

        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        btnBack.setFont(Fonts.getFont(Fonts.BUTTONS));
        btnNext.setFont(Fonts.getFont(Fonts.BUTTONS));
        btnCancel.setFont(Fonts.getFont(Fonts.BUTTONS));
        btnBack.addActionListener(actBackOnClick);
        btnNext.addActionListener(actNextOnClick);
        btnCancel.addActionListener(actCancelOnClick);
        JPanel pnlNav = new JPanel();
        pnlNav.setLayout(new GridLayout(1, 3));
        pnlNav.setOpaque(false);
        pnlNav.add(btnBack);
        pnlNav.add(btnNext);
        pnlNav.add(btnCancel);
        this.add(pnlNav, BorderLayout.EAST);
    }

    /**
     * {@inheritDoc}
     */
    public void localize(Locale pLocale)
    {
        // Load resource bundle.
        rb = ResourceBundle.getBundle(this.getClass().getName(), pLocale);
        // Localize components.
        Localizer.localize(rb, this, "NavPanel");
        Localizer.localize(rb, btnBack, "btnBack", null);
        Localizer.localize(rb, btnNext, "btnNext", null);
        Localizer.localize(rb, btnCancel, "btnCancel", null);
        btnBack.setIcon(App.getIcon("res/img/www.everaldo.com/player_rew.png", rb.getString("btnBack.description")));
        btnNext.setIcon(App.getIcon("res/img/www.everaldo.com/player_play.png", rb.getString("btnNext.description")));
        btnCancel.setIcon(App.getIcon("res/img/www.everaldo.com/player_stop.png", rb.getString("btnCancel.description")));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBackEnabled()
    {
        return btnBack.isEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void setBackEnabled(boolean pEnabled)
    {
        btnBack.setEnabled(pEnabled);
        if(pEnabled == true)
        {
            btnBack.requestFocusInWindow();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNextEnabled()
    {
        return btnNext.isEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void setNextEnabled(boolean pEnabled)
    {
        btnNext.setEnabled(pEnabled);
        if(pEnabled == true)
        {
            btnNext.requestFocusInWindow();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setFinished()
    {
        booFinished = true;
        Localizer.localize(rb, btnCancel, "btnFinish", null);
        btnCancel.setIcon(App.getIcon("res/img/www.everaldo.com/player_eject.png", rb.getString("btnFinish.value")));
    }

    /**
     * {@inheritDoc}
     */
    public void setNotFinished()
    {
        booFinished = false;
        Localizer.localize(rb, btnCancel, "btnCancel", null);
        btnCancel.setIcon(App.getIcon("res/img/www.everaldo.com/player_stop.png", rb.getString("btnCancel.value")));
    }

    /**
     * {@inheritDoc}
     */
    public void setWizard(Wizard pWizard)
    {
        this.wiz = pWizard;
        this.actBackOnClick.setWizard(wiz);
        this.actNextOnClick.setWizard(wiz);
        this.actCancelOnClick.setWizard(wiz);
    }

}
