package com.infotraxx.carfax.setup.ui.mainwindow;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;

/**
 * The wizard panel.
 * @author Ed Jenkins
 */
class WizardPanel extends JPanel implements Localized, Wizard
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(WizardPanel.class);

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * The title panel.
     */
    private TitlePanel pnlTitle = new TitlePanel();

    /**
     * A container for other panels.
     */
    private JPanel pnlContent = new JPanel();

    /**
     * A map of panels and their names.
     */
    private TreeMap<String, JPanel> tm = new TreeMap<String, JPanel>();

    /**
     * The layout manager for the content panel.
     */
    private CardLayout cl = new CardLayout();

    /**
     * The current panel.
     */
    private String strCurrent;

    /**
     * The nav panel.
     */
    private NavPanel pnlNav = new NavPanel();

    /**
     * A reference to the main frame.
     */
    private JFrame fraMain;

    /**
     * The error message screen.
     */
    InstallErrorPanel pnlInstallError = new InstallErrorPanel();

    /**
     * Constructor.
     */
    public WizardPanel()
    {
        // Setup this panel.
        this.setBorder(new EmptyBorder(0, 10, 0, 0));
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        // Create the data entry screens.
        EnterWelcomePanel pnlEnterWelcome = new EnterWelcomePanel();
        EnterEULAPanel pnlEnterEULA = new EnterEULAPanel();
        EnterEmail1Panel pnlEnterEmail1 = new EnterEmail1Panel();
        EnterEmail2Panel pnlEnterEmail2 = new EnterEmail2Panel();
        EnterLoginPanel pnlEnterLogin = new EnterLoginPanel();
        EnterReadyPanel pnlEnterReady = new EnterReadyPanel();
        EnterErrorPanel pnlEnterError = new EnterErrorPanel();
        // Create the installation screens.
        InstallAppPanel pnlInstallApp = new InstallAppPanel();
        InstallFinishedPanel pnlInstallFinished = new InstallFinishedPanel();
        InstallErrorPanel pnlInstallError = new InstallErrorPanel();
        // Add the content panels to the map.
        tm.put(pnlEnterWelcome.getID(), pnlEnterWelcome);
        tm.put(pnlEnterEULA.getID(), pnlEnterEULA);
        tm.put(pnlEnterEmail1.getID(), pnlEnterEmail1);
        tm.put(pnlEnterEmail2.getID(), pnlEnterEmail2);
        tm.put(pnlEnterLogin.getID(), pnlEnterLogin);
        tm.put(pnlEnterReady.getID(), pnlEnterReady);
        tm.put(pnlEnterError.getID(), pnlEnterError);
        tm.put(pnlInstallApp.getID(), pnlInstallApp);
        tm.put(pnlInstallFinished.getID(), pnlInstallFinished);
        tm.put(pnlInstallError.getID(), pnlInstallError);
        // Set the current panel.
        strCurrent = pnlEnterWelcome.getID();
        this.add(pnlTitle, BorderLayout.NORTH);
        // Setup the content panel.
        Border inner = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        Border outer = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border perimeter = BorderFactory.createEmptyBorder(10, 0, 0, 0);
        Border b1 = BorderFactory.createCompoundBorder(outer, inner);
        Border b2 = BorderFactory.createCompoundBorder(perimeter, b1);
        pnlContent.setBorder(b2);
        pnlContent.setLayout(cl);
        pnlContent.setOpaque(false);
        Set set = tm.keySet();
        Iterator i = set.iterator();
        while (i.hasNext())
        {
            String s = (String) i.next();
            JPanel pnl = tm.get(s);
            pnlContent.add(pnl, s);
        }
        this.add(pnlContent, BorderLayout.CENTER);
        // Setup the navigation panel.
        pnlNav.setBackEnabled(false);
        pnlNav.setWizard(this);
        this.add(pnlNav, BorderLayout.SOUTH);
    }

    /**
     * {@inheritDoc}
     */
    public void localize(Locale pLocale)
    {
        // Load resource bundle.
        rb = ResourceBundle.getBundle(this.getClass().getName(), pLocale);
        // Localize components.
        Localizer.localize(rb, this, "WizardPanel");
        pnlTitle.localize(pLocale);
        pnlNav.localize(pLocale);
        Set set = tm.keySet();
        Iterator i = set.iterator();
        while (i.hasNext())
        {
            String s = (String) i.next();
            JPanel pnl = tm.get(s);
            Localized l = (Localized) pnl;
            l.localize(pLocale);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void goBack()
    {
        // Get the current panel.
        JPanel pnlCurrent = tm.get(strCurrent);
        WizardNavigator wnCurrent = (WizardNavigator)pnlCurrent;
        // Make sure this operation is allowed.
        // NOTE:  You can't do this in goNext()
        // because then the Install...Panel screens
        // will freeze when done and
        // not progress on to the next screen.
        if(!wnCurrent.isBackEnabled())
        {
            return;
        }
        // Find the previous panel.
        String strPrevious = wnCurrent.getPrevious();
        JPanel pnlPrevious = tm.get(strPrevious);
        WizardNavigator wnPrevious = (WizardNavigator)pnlPrevious;
        // Don't go in circles.
        String strThis = wnCurrent.getID();
        if(strThis.equals(strPrevious))
        {
            logger.error("Circular reference found for " + strThis + ".");
            return;
        }
        // Switch to the previous panel.
        strCurrent = wnPrevious.getID();
        pnlCurrent = tm.get(strCurrent);
        wnCurrent = (WizardNavigator)pnlCurrent;
        pnlTitle.setText(wnCurrent.getTitle());
        cl.show(pnlContent, strCurrent);
        pnlNav.setBackEnabled(wnCurrent.isBackEnabled());
        pnlNav.setNextEnabled(wnCurrent.isNextEnabled());
        // Do stuff.
        wnCurrent.doSomething(this);
        // Some of the programs we spawn will cover us up.
        // Come back to the front so the user can see what's going on.
        if (fraMain == null)
        {
            Component c = this.getParent();
            while (!(c instanceof JFrame))
            {
                c = c.getParent();
            }
            fraMain = (JFrame)c;
        }
        fraMain.toFront();
    }

    /**
     * {@inheritDoc}
     */
    public void goNext()
    {
        // Get the current panel.
        JPanel pnlCurrent = tm.get(strCurrent);
        WizardNavigator wnCurrent = (WizardNavigator)pnlCurrent;
        // Save its data.
        wnCurrent.saveConfig();
        // Find the next panel.
        String strNext = wnCurrent.getNext();
        JPanel pnlNext = tm.get(strNext);
        WizardNavigator wnNext = (WizardNavigator)pnlNext;
        // Don't go in circles.
        String strThis = wnCurrent.getID();
        if(strThis.equals(strNext))
        {
            logger.error("Circular reference found for " + strThis + ".");
            return;
        }
        // Hook up the breadcrumbs.
        wnNext.setPrevious(wnCurrent.getID());
        // Switch to next panel.
        strCurrent = wnNext.getID();
        pnlCurrent = tm.get(strCurrent);
        wnCurrent = (WizardNavigator)pnlCurrent;
        pnlTitle.setText(wnCurrent.getTitle());
        cl.show(pnlContent, strCurrent);
        pnlNav.setBackEnabled(wnCurrent.isBackEnabled());
        pnlNav.setNextEnabled(wnCurrent.isNextEnabled());
        // Do stuff.
        wnCurrent.doSomething(this);
        // Some of the programs we spawn will cover us up.
        // Come back to the front so the user can see what's going on.
        if (fraMain == null)
        {
            Component c = this.getParent();
            while (!(c instanceof JFrame))
            {
                c = c.getParent();
            }
            fraMain = (JFrame)c;
        }
        fraMain.toFront();
    }

    /**
     * {@inheritDoc}
     */
    public void setNextEnabled(boolean pEnabled)
    {
        pnlNav.setNextEnabled(pEnabled);
    }

    /**
     * {@inheritDoc}
     */
    public void goTo(String pID)
    {
        // Get the current panel.
        JPanel pnlCurrent = tm.get(strCurrent);
        WizardNavigator wnCurrent = (WizardNavigator)pnlCurrent;
        // Save its data.
        wnCurrent.saveConfig();
        // Find the next panel.
        String strNext = pID;
        JPanel pnlNext = tm.get(strNext);
        WizardNavigator wnNext = (WizardNavigator)pnlNext;
        // Don't go in circles.
        String strThis = wnCurrent.getID();
        if(strThis.equals(strNext))
        {
            logger.error("Circular reference found for " + strThis + ".");
            return;
        }
        // Hook up the breadcrumbs.
        wnNext.setPrevious(wnCurrent.getID());
        // Switch to next panel.
        strCurrent = wnNext.getID();
        pnlCurrent = tm.get(strCurrent);
        wnCurrent = (WizardNavigator) pnlCurrent;
        pnlTitle.setText(wnCurrent.getTitle());
        cl.show(pnlContent, strCurrent);
        pnlNav.setBackEnabled(wnCurrent.isBackEnabled());
        pnlNav.setNextEnabled(wnCurrent.isNextEnabled());
        // Do stuff.
        wnCurrent.doSomething(this);
        // Some of the programs we spawn will cover us up.
        // Come back to the front so the user can see what's going on.
        if (fraMain == null)
        {
            Component c = this.getParent();
            while (!(c instanceof JFrame))
            {
                c = c.getParent();
            }
            fraMain = (JFrame)c;
        }
        fraMain.toFront();
    }

    /**
     * {@inheritDoc}
     */
    public void goFinish()
    {
        WizardNavigator wnCurrent = (WizardNavigator)tm.get(strCurrent);
        wnCurrent.saveConfig();
    }

    /**
     * {@inheritDoc}
     */
    public void goError(Throwable t)
    {
        pnlInstallError.setError(t);
        this.goTo(InstallErrorPanel.ID);
    }

    /**
     * {@inheritDoc}
     */
    public void setFinished()
    {
        pnlNav.setFinished();
        logger.info("Finished.");
    }

    /**
     * {@inheritDoc}
     */
    public void setNotFinished()
    {
        pnlNav.setNotFinished();
        logger.info("Not finished.");
    }

}
