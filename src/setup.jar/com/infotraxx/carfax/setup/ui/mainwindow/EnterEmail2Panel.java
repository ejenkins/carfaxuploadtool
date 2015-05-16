package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.install.Authenticator;
import com.infotraxx.carfax.setup.install.Security;
import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.Config;
import com.infotraxx.carfax.setup.util.Fonts;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

/**
 * The Enter Email 2 panel.
 * This is a data entry screen.
 * @author Ed Jenkins
 */
class EnterEmail2Panel extends JPanel implements Localized, WizardNavigator
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(EnterEmail2Panel.class);

    /**
     * Icon.
     */
    public static final String ICON = "res/img/www.everaldo.com/InternetConnectionTools.png";

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * The ID.
     */
    static final String ID = "EnterEmail2Panel";

    /**
     * The title.
     */
    private String strTitle;

    /**
     * The previous panel.
     */
    private String strPrevious;

    /**
     * The next panel.
     */
    private String strNext = EnterReadyPanel.ID;

    /**
     * Icon.
     */
    private JLabel lblIcon = new JLabel();

    /**
     * The text area.
     */
    private JEditorPane jepInstructions = new JEditorPane();
    
    /**
     * Constructor.
     */
    public EnterEmail2Panel()
    {
        // Setup this panel.
        this.setBorder(new EmptyBorder(10, 0, 10, 10));
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        // Setup the icon.
        lblIcon.setOpaque(false);
        JPanel pnlIconInner = new JPanel();
        pnlIconInner.setLayout(new BorderLayout());
        pnlIconInner.setOpaque(false);
        pnlIconInner.add(lblIcon, BorderLayout.WEST);
        JPanel pnlIconOuter = new JPanel();
        pnlIconOuter.setLayout(new BorderLayout());
        pnlIconOuter.setOpaque(false);
        pnlIconOuter.add(pnlIconInner, BorderLayout.NORTH);
        // Setup the text area.
        jepInstructions.setEditable(false);
        jepInstructions.setFont(Fonts.getFont(Fonts.INSTRUCTIONS));
        jepInstructions.setOpaque(false);
        jepInstructions.setFocusable(false);
        jepInstructions.setContentType("text/html");
        // Lay it out.
        JPanel pnlNorth = new JPanel();
        pnlNorth.setLayout(new BorderLayout());
        pnlNorth.setOpaque(false);
        pnlNorth.add(pnlIconOuter, BorderLayout.WEST);
        pnlNorth.add(jepInstructions, BorderLayout.CENTER);
        JPanel pnlGrid = new JPanel();
        pnlGrid.setBorder(new EmptyBorder(16, 0, 0, 0));
        pnlGrid.setLayout(new GridLayout(0, 2, 4, 4));
        pnlGrid.setOpaque(false);
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BorderLayout());
        pnlContent.setOpaque(false);
        pnlContent.add(pnlGrid, BorderLayout.NORTH);
        JPanel pnlLayout = new JPanel();
        pnlLayout.setLayout(new BorderLayout());
        pnlLayout.setOpaque(false);
        pnlLayout.add(pnlNorth, BorderLayout.NORTH);
        pnlLayout.add(pnlContent, BorderLayout.CENTER);
        // Add components to the panel.
        this.add(pnlLayout, BorderLayout.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    public void localize(Locale pLocale)
    {
        // Load resource bundle.
        rb = ResourceBundle.getBundle(this.getClass().getName(), pLocale);
        //  Set title.
        strTitle = rb.getString("this.value");
        // Localize components.
        String[] fi = Fonts.getFontInfo(Fonts.INSTRUCTIONS);
        String[] o = { fi[0], fi[1] };
        Localizer.localize(rb, this, ID);
        Localizer.localize(rb, lblIcon, "lblIcon", null);
        Localizer.localize(rb, jepInstructions, "jepInstructions", o);
        lblIcon.setIcon(App.getIcon(ICON, rb.getString("lblIcon.description")));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBackEnabled()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNextEnabled()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getID()
    {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle()
    {
        return strTitle;
    }

    /**
     * {@inheritDoc}
     */
    public String getPrevious()
    {
        return strPrevious;
    }

    /**
     * {@inheritDoc}
     */
    public void setPrevious(String pID)
    {
        strPrevious = pID;
    }

    /**
     * {@inheritDoc}
     */
    public String getNext()
    {
        return strNext;
    }

    /**
     * {@inheritDoc}
     */
    public void setNext(String pID)
    {
        strNext = pID;
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
    }

    /**
     * {@inheritDoc}
     */
    public void doSomething(Wizard pWizard)
    {
    }

    /**
     * {@inheritDoc}
     */
    public void saveConfig()
    {
        strNext = EnterReadyPanel.ID;
        String strEmail = Config.getValue("email");
        Authenticator a = new Authenticator();
        try
        {
            a.authenticate(strEmail);
            String strUsername = a.getUsername();
            String strPassword = a.getPassword();
            TreeMap tmVariables = a.getVariables();
            Security.read();
            Security.setUsername(strUsername);
            Security.setPassword(strPassword);
            Security.addVariables(tmVariables);
            Security.write();
            Config.setValue("configuration.method", "email");
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
//            Config.setValue("error.message", ex.getMessage());
            strNext = EnterLoginPanel.ID;
        }
    }

}
