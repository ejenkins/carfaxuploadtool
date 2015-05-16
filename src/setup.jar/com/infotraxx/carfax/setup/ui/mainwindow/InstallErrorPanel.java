package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.Fonts;

import java.awt.BorderLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

/**
 * The Install Error panel.
 * This is an error message screen.
 * It tells you that something went wrong during installation and Setup cannot continue.
 * @author Ed Jenkins
 */
class InstallErrorPanel extends JPanel implements Localized, WizardNavigator
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(InstallErrorPanel.class);

    /**
     * The maximum number of lines to show.
     */
    public static final int MAX_LINES = 21;

    /**
     * Icon.
     */
    public static final String ICON = "res/img/www.everaldo.com/sipphone.png";

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * The ID.
     */
    static final String ID = "InstallErrorPanel";

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
    private String strNext;

    /**
     * Icon.
     */
    private JLabel lblIcon = new JLabel();

    /**
     * The text area.
     */
    private JEditorPane jepInstructions = new JEditorPane();

    /**
     * An error message and stack trace.
     */
    private Throwable err = new IllegalStateException("A strange error occurred.");

    /**
     * Scrollbars for the error message and stack trace.
     */
    private JScrollPane jsp = new JScrollPane();

    /**
     * Constructor.
     */
    public InstallErrorPanel()
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
        jepInstructions.setFont(Fonts.getFont(Fonts.CORE));
        jepInstructions.setOpaque(false);
        jepInstructions.setFocusable(false);
        jepInstructions.setContentType("text/html");
        // Setup the scroll pane.
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setViewportView(jepInstructions);
        // Lay it out.
        JPanel pnlLayout = new JPanel();
        pnlLayout.setLayout(new BorderLayout());
        pnlLayout.setOpaque(false);
        pnlLayout.add(jsp, BorderLayout.CENTER);
        // Add components to the panel.
        this.add(pnlIconOuter, BorderLayout.WEST);
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
        String[] fi = Fonts.getFontInfo(Fonts.CORE);
        String strAppNameLong = App.getValue("app.name.long");
        Object[] o = { fi[0], fi[1], strAppNameLong, strAppNameLong };
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
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNextEnabled()
    {
        return false;
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
        // Get the error message.
        String strErrorMessage = err.getMessage();
        // Get the stack trace.
        final String at = "at ";
        final String nl = "<br/>";
        StringBuilder sbStackTrace = new StringBuilder(1024);
        StackTraceElement element = null;
        StackTraceElement[] elements = err.getStackTrace();
        int x = 0;
        int y = Math.min(elements.length, MAX_LINES);
        int z = y - 1;
        for (x = 0; x < y; x++)
        {
            element = elements[x];
            String s = element.toString();
            sbStackTrace.append(at);
            sbStackTrace.append(s);
            if (x < z)
            {
                sbStackTrace.append(nl);
            }
        }
        String strStackTrace = sbStackTrace.toString();
        // Format the message.
        String[] fi = Fonts.getFontInfo(Fonts.CORE);
        Object[] o = { fi[0], fi[1], strErrorMessage, strStackTrace };
        Localizer.localize(rb, jepInstructions, "jepInstructions", o);
    }

    /**
     * {@inheritDoc}
     */
    public void saveConfig()
    {
    }

    /**
     * Sets the error message and stack trace to display.
     * @param t the error message and stack trace.
     */
    public void setError(Throwable t)
    {
        if(t == null)
        {
            return;
        }
        err = t;
        logger.error(err, err);
    }

}
