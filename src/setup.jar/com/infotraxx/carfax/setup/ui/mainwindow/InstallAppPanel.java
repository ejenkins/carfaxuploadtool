package com.infotraxx.carfax.setup.ui.mainwindow;

import com.infotraxx.carfax.setup.install.AppInstaller;
import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.Fonts;
import com.infotraxx.carfax.setup.util.ProgressReporter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

/**
 * Installs the application.
 * @author Ed Jenkins
 */
class InstallAppPanel extends JPanel implements Localized, ProgressReporter, WizardNavigator
{

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(InstallAppPanel.class);

    /**
     * Icon.
     */
    public static final String ICON = "res/img/www.everaldo.com/runit.png";

    /**
     * Resource bundle for localization.
     * @see #localize(Locale)
     */
    private ResourceBundle rb;

    /**
     * The ID.
     */
    static final String ID = "InstallAppPanel";

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
    private String strNext = InstallFinishedPanel.ID;

    /**
     * Icon.
     */
    private JLabel lblIcon = new JLabel();

    /**
     * The text area.
     */
    private JEditorPane jepInstructions = new JEditorPane();

    /**
     * The progress bar.
     */
    private JProgressBar pb = new JProgressBar();

    /**
     * The minimum value.
     */
    private int intMin;

    /**
     * The maximum value.
     */
    private int intMax;

    /**
     * The actual value.
     */
    private int intVal;

    /**
     * Constructor.
     */
    public InstallAppPanel()
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
        // Setup the progress bar.
        pb.setStringPainted(true);
        pb.setFont(Fonts.getFont(Fonts.BUTTONS));
        // Lay it out.
        JPanel pnlNorth = new JPanel();
        pnlNorth.setLayout(new BorderLayout());
        pnlNorth.setOpaque(false);
        pnlNorth.add(pnlIconOuter, BorderLayout.WEST);
        pnlNorth.add(jepInstructions, BorderLayout.CENTER);
        JPanel pnlGrid = new JPanel();
        pnlGrid.setLayout(new GridLayout());
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
        pnlLayout.add(pb, BorderLayout.SOUTH);
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
        // Set title.
        strTitle = rb.getString("this.value");
        // Localize components.
        String[] fi = Fonts.getFontInfo(Fonts.INSTRUCTIONS);
        String strAppNameLong = App.getValue("app.name.long");
        String[] o = { fi[0], fi[1], strAppNameLong };
        Localizer.localize(rb, this, ID);
        Localizer.localize(rb, lblIcon, "lblIcon", null);
        Localizer.localize(rb, jepInstructions, "jepInstructions", o);
        Localizer.localize(rb, pb, "pb", null);
        lblIcon.setIcon(App.getIcon(ICON, rb.getString("lblIcon.description")));
    }

    /**
     * {@inheritDoc}
     */
    public void report()
    {
        // Update progress bar.
        intVal++;
        pb.setValue(intVal);
    }

    /**
     * {@inheritDoc}
     */
    public void finish()
    {
        intVal = intMax;
        pb.setValue(intVal);
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
        AppInstaller i = new AppInstaller();
        i.setWizard(pWizard);
        i.setReporter(this);
        intMin = 0;
        intMax = i.getSize();
        intVal = 0;
        pb.setMinimum(intMin);
        pb.setMaximum(intMax);
        pb.setValue(intVal);
        Thread t = new Thread(i);
        t.start();
    }

    /**
     * {@inheritDoc}
     */
    public void saveConfig()
    {
    }

}
