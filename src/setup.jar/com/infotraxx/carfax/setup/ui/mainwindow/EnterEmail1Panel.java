package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.Config;
import com.infotraxx.carfax.setup.util.Fonts;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

/**
 * The Enter Email 1 panel.
 * This is a data entry screen.
 * @author Ed Jenkins
 */
class EnterEmail1Panel extends JPanel implements Localized, WizardNavigator
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(EnterEmail1Panel.class);

    /**
     * Icon.
     */
    public static final String ICON = "res/img/www.everaldo.com/xfmail.png";

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * The ID.
     */
    static final String ID = "EnterEmail1Panel";

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
    private String strNext = EnterEmail2Panel.ID;

    /**
     * Icon.
     */
    private JLabel lblIcon = new JLabel();

    /**
     * The text area.
     */
    private JEditorPane jepInstructions = new JEditorPane();
    
    /**
     * Email.
     */
    private JLabel lblEmail = new JLabel();

    /**
     * Email.
     */
    private JTextField txtEmail = new JTextField();

    /**
     * Cool focus adapter.
     */
    private CoolFocusAdapter cfa = new CoolFocusAdapter();
    
    /**
     * Cool key adapter.
     */
//    private CoolKeyAdapter cka = new CoolKeyAdapter();

    /**
     * Constructor.
     */
    public EnterEmail1Panel()
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
        // Setup the data entry fields.
        lblEmail.setFont(Fonts.getFont(Fonts.LABELS));
        lblEmail.setHorizontalAlignment(SwingConstants.RIGHT);
        txtEmail.setBackground(CoolFocusAdapter.INACTIVE);
        txtEmail.setFont(Fonts.getFont(Fonts.FIELDS));
        txtEmail.addFocusListener(cfa);
//        txtEmail.addKeyListener(cka);
        // Set accessibility.
        lblEmail.setLabelFor(txtEmail);
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
        pnlGrid.add(lblEmail);
        pnlGrid.add(txtEmail);
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
        Localizer.localize(rb, lblEmail, "lblEmail", null);
        Localizer.localize(rb, txtEmail, "txtEmail", null);
        lblIcon.setIcon(App.getIcon(ICON, rb.getString("lblIcon.description")));
        // Set default values.
        String strEmail = Config.getValue("email");
        txtEmail.setText(strEmail);
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
//        cka.setWizard(pWizard);
        txtEmail.requestFocusInWindow();
    }

    /**
     * {@inheritDoc}
     */
    public void saveConfig()
    {
        strNext = EnterEmail2Panel.ID;
        String strEmail = txtEmail.getText();
        if(strEmail.equalsIgnoreCase(""))
        {
            Config.setValue("error.message", "Please enter an email address.");
            strNext = EnterErrorPanel.ID;
            return;
        }
        int intLen = strEmail.length();
        if(intLen < 6)
        {
            Config.setValue("error.message", "Please enter a valid email address.");
            strNext = EnterErrorPanel.ID;
            return;
        }
        if(!strEmail.contains("@"))
        {
            Config.setValue("error.message", "Please enter a valid email address.");
            strNext = EnterErrorPanel.ID;
            return;
        }
        if(!strEmail.contains("."))
        {
            Config.setValue("error.message", "Please enter a valid email address.");
            strNext = EnterErrorPanel.ID;
            return;
        }
        Config.setValue("email", strEmail);
    }

}
