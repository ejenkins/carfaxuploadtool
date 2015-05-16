package com.infotraxx.carfax.setup.ui.mainwindow;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.accessibility.AccessibleContext;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

/**
 * Localizes components.
 * @author Ed Jenkins
 */
public class Localizer
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Localizer.class);

    /**
     * The "name" suffix for resource bundle entries.
     */
    public static final String NAME = ".name";

    /**
     * The "value" suffix for resource bundle entries.
     */
    public static final String VALUE = ".value";

    /**
     * The "key" suffix for resource bundle entries.
     */
    public static final String KEY = ".key";

    /**
     * The "index" suffix for resource bundle entries.
     */
    public static final String INDEX = ".index";

    /**
     * The "description" suffix for resource bundle entries.
     */
    public static final String DESCRIPTION = ".description";

    /**
     * A reusable StringBuffer.
     */
    private static final StringBuilder SB = new StringBuilder(1024);

    /**
     * Constructor.
     */
    public Localizer()
    {
    }

    /**
     * Gets a key from the resource bundle.
     * @param rb the resource bundle to read from.
     * @param b the base name of the resource key.
     * @return a string value from the resource bundle or an empty string if the key is not found or there is no value.
     */
    public static String getName(ResourceBundle rb, String b)
    {
        // Assemble key name.
        SB.setLength(0);
        SB.append(b);
        SB.append(NAME);
        // Get the key.
        String n = SB.toString();
        // Get the value.
        String v = rb.getString(n);
        // Make sure it's not null.
        if(v == null)
        {
            return "";
        }
        // Return result.
        return v;
    }

    /**
     * Gets a key from the resource bundle.
     * @param rb the resource bundle to read from.
     * @param b the base name of the resource key.
     * @return a string value from the resource bundle or an empty string if the key is not found or there is no value.
     */
    public static String getValue(ResourceBundle rb, String b)
    {
        // Return result.
        return Localizer.getValue(rb, b, null);
    }

    /**
     * Gets a key from the resource bundle.
     * @param rb the resource bundle to read from.
     * @param b the base name of the resource key.
     * @param o if the value you get from the key (b + e) should be treated as a format, then o is an array of parameters and I will use MessageFormat to compute the actual value. If o is null, the the value will be taken directly from (b + e) and it will not be formatted.
     * @return a string value from the resource bundle or an empty string if the key is not found or there is no value.
     */
    public static String getValue(ResourceBundle rb, String b, Object[] o)
    {
        // Assemble key name.
        SB.setLength(0);
        SB.append(b);
        SB.append(VALUE);
        // Get the key.
        String n = SB.toString();
        // Get the value.
        String p = rb.getString(n);
        String v = p.replaceAll("'", "''");
        // Make sure it's not null.
        if(v == null)
        {
            return "";
        }
        // Format it, if desired.
        if(o != null)
        {
            v = MessageFormat.format(v, o);
        }
        // Return result.
        return v;
    }

    /**
     * Gets a key from the resource bundle.
     * @param rb the resource bundle to read from.
     * @param b the base name of the resource key.
     * @return a character value from the resource bundle or '!' if the key is not found or there is no value.
     */
    public static char getKey(ResourceBundle rb, String b)
    {
        // Assemble key name.
        SB.setLength(0);
        SB.append(b);
        SB.append(KEY);
        // Get the key.
        String n = SB.toString();
        // Get the value.
        String v = rb.getString(n);
        // Create return variable.
        char r = '!';
        // Make sure it's not null.
        if(v == null)
        {
            return r;
        }
        if(v.length() < 1)
        {
            return r;
        }
        // Get the first char in the string.
        r = v.charAt(0);
        // Return result.
        return r;
    }

    /**
     * Gets something from the resource bundle.
     * @param rb the resource bundle to read from.
     * @param b the base name of the resource key.
     * @return a number from the resource bundle or -1 if the key is not found or there is no value or the value is not an integer.
     */
    public static int getIndex(ResourceBundle rb, String b)
    {
        // Assemble key name.
        SB.setLength(0);
        SB.append(b);
        SB.append(INDEX);
        // Get the key.
        String n = SB.toString();
        // Get the value.
        String v = rb.getString(n);
        // Create return variable.
        int r = -1;
        // Use default value if property does not exist.
        if(v == null)
        {
            return r;
        }
        // Convert from String to int.
        try
        {
            r = Integer.parseInt(v);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return r;
    }

    /**
     * Gets a key from the resource bundle.
     * @param rb the resource bundle to read from.
     * @param b the base name of the resource key.
     * @return a string value from the resource bundle or an empty string if the key is not found or there is no value.
     */
    public static String getDescription(ResourceBundle rb, String b)
    {
        // Assemble key name.
        SB.setLength(0);
        SB.append(b);
        SB.append(DESCRIPTION);
        // Get the key.
        String n = SB.toString();
        // Get the value.
        String v = rb.getString(n);
        // Make sure it's not null.
        if(v == null)
        {
            return "";
        }
        // Return result.
        return v;
    }

    /**
     * Localizes components.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, Component c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes menu items.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     * @param o if the value you get from the key (n + ".value") should be treated as a format, then o is an array of parameters and I will use MessageFormat to compute the actual value. If o is null, the the value will be taken directly from (n + ".value") and it will not be formatted.
     */
    public static void localize(ResourceBundle rb, JMenuItem c, String n, Object[] o)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strValue = Localizer.getValue(rb, n, o);
        char chrKey = Localizer.getKey(rb, n);
        int intIndex = Localizer.getIndex(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setText(strValue);
        c.setMnemonic(chrKey);
        try
        {
            c.setDisplayedMnemonicIndex(intIndex);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes labels.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     * @param o if the value you get from the key (n + ".value") should be treated as a format, then o is an array of parameters and I will use MessageFormat to compute the actual value. If o is null, the the value will be taken directly from (n + ".value") and it will not be formatted.
     */
    public static void localize(ResourceBundle rb, JLabel c, String n, Object[] o)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strValue = Localizer.getValue(rb, n, o);
        char chrKey = Localizer.getKey(rb, n);
        int intIndex = Localizer.getIndex(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setText(strValue);
        c.setDisplayedMnemonic(chrKey);
        try
        {
            c.setDisplayedMnemonicIndex(intIndex);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes progress bars.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     * @param o if the value you get from the key (n + ".value") should be treated as a format, then o is an array of parameters and I will use MessageFormat to compute the actual value. If o is null, the the value will be taken directly from (n + ".value") and it will not be formatted.
     */
    public static void localize(ResourceBundle rb, JProgressBar c, String n, Object[] o)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strValue = Localizer.getValue(rb, n, o);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes text fields.
     * Value will only be set if it is not empty.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     * @param o if the value you get from the key (n + ".value") should be treated as a format, then o is an array of parameters and I will use MessageFormat to compute the actual value. If o is null, the the value will be taken directly from (n + ".value") and it will not be formatted.
     */
    public static void localize(ResourceBundle rb, JTextComponent c, String n, Object[] o)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strValue = Localizer.getValue(rb, n, o);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        if(!strValue.equalsIgnoreCase(""))
        {
            c.setText(strValue);
        }
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes buttons.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     * @param o if the value you get from the key (n + ".value") should be treated as a format, then o is an array of parameters and I will use MessageFormat to compute the actual value. If o is null, the the value will be taken directly from (n + ".value") and it will not be formatted.
     */
    public static void localize(ResourceBundle rb, AbstractButton c, String n, Object[] o)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strValue = Localizer.getValue(rb, n, o);
        char chrKey = Localizer.getKey(rb, n);
        int intIndex = Localizer.getIndex(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setText(strValue);
        c.setMnemonic(chrKey);
        try
        {
            c.setDisplayedMnemonicIndex(intIndex);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes radio buttons.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     * @param o if the value you get from the key (n + ".value") should be treated as a format, then o is an array of parameters and I will use MessageFormat to compute the actual value. If o is null, the the value will be taken directly from (n + ".value") and it will not be formatted.
     */
    public static void localize(ResourceBundle rb, JRadioButton c, String n, Object[] o)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strValue = Localizer.getValue(rb, n, o);
        char chrKey = Localizer.getKey(rb, n);
        int intIndex = Localizer.getIndex(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setText(strValue);
        c.setMnemonic(chrKey);
        try
        {
            c.setDisplayedMnemonicIndex(intIndex);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes listboxes.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, JList c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes comboboxes.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, JComboBox c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes trees.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, JTree c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes tables.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, JTable c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, n);
        String strDescription = Localizer.getDescription(rb, n);
        // Set values.
        c.setName(n);
        c.setToolTipText(strDescription);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes panels.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, JPanel c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, "this");
        String strDescription = Localizer.getDescription(rb, "this");
        // Set values.
        c.setName(n);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes dialogs.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, JDialog c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, "this");
        String strValue = Localizer.getValue(rb, "this");
        String strDescription = Localizer.getDescription(rb, "this");
        // Set values.
        c.setName(n);
        c.setTitle(strValue);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes frames.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, JFrame c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, "this");
        String strValue = Localizer.getValue(rb, "this");
        String strDescription = Localizer.getDescription(rb, "this");
        // Set values.
        c.setName(n);
        c.setTitle(strValue);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

    /**
     * Localizes internal frames.
     * @param rb the resource bundle to read from.
     * @param c the component to localize.
     * @param n the base name of the resource key.
     */
    public static void localize(ResourceBundle rb, JInternalFrame c, String n)
    {
        // Get values.
        String strName = Localizer.getName(rb, "this");
        String strValue = Localizer.getValue(rb, "this");
        String strDescription = Localizer.getDescription(rb, "this");
        // Set values.
        c.setName(n);
        c.setTitle(strValue);
        AccessibleContext ac = c.getAccessibleContext();
        ac.setAccessibleName(strName);
        ac.setAccessibleDescription(strDescription);
    }

}
