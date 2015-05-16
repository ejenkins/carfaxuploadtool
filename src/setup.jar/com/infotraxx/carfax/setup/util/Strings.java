package com.infotraxx.carfax.setup.util;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;

import org.apache.log4j.Logger;

/**
 * String utilities.
 * @author Ed Jenkins
 */
public class Strings
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Strings.class);

    /**
     * A component to use for getting font metrics.
     */
    private static final Canvas canvas = new Canvas();

    /**
     * Constructor.
     */
    public Strings()
    {
    }

    /**
     * Gets the width of a string in pixels.
     * @param pString the string to measure.
     * @param pFont the font to use for calculations.
     * @return the width of the string.
     */
    public static int getWidth(String pString, Font pFont)
    {
        // Create return variable.
        int r = 0;
        // Verify parameters.
        if(pString == null)
        {
            return 0;
        }
        if(pFont == null)
        {
            return 0;
        }
        // Get metrics for the requested font.
        FontMetrics fm = canvas.getFontMetrics(pFont);
        // Measure the string.
        r = fm.stringWidth(pString);
        // Return result.
        return r;
    }

    /**
     * Normalizes a string.
     * @param pString the string to normalize.
     * @return the string or an empty string if it was null.
     */
    public static String notNull(String pString)
    {
        // Create return variable.
        String s = pString;
        // Normalize it.
        if(s == null)
        {
            s = "";
        }
        // Return result.
        return s;
    }

    /**
     * Creates a copy of a string.
     * @param pString the string to copy.
     * @return a copy of the string or an empty string if it was null.
     */
    public static String newString(String pString)
    {
        // Return an empty string if it was null.
        if(pString == null)
        {
            return "";
        }
        // If it was not null, make a copy of it.
        String s = new String(pString);
        // Return result.
        return s;
    }

    /**
     * Normalizes whitespace.
     * @param pString the string to normalize.
     * @return a string where all whitespace characters have been compressed into single spaces.
     */
    public static String normalizeWhitespace(String pString)
    {
        // Verify parameters.
        if(pString == null)
        {
            return null;
        }
        // Normalize the whitespace.
        String s = pString.replaceAll("\\s+", " ");
        // Return result.
        return s;
    }

    /**
     * Converts a String to a char. Used only for converting a key value from a ResourceBundle to a mnemonic character for a label.
     * See javax.swing.JLabel#setDisplayedMnemonic(char)
     * @param pString the string to convert.
     * @return a character representation of the string or '!' if pString is null or empty.
     */
    public static char getKey(String pString)
    {
        // Create return variable.
        char x = '!';
        // Verify parameters.
        if(pString == null)
        {
            return x;
        }
        if(pString.length() < 1)
        {
            return x;
        }
        // Convert the string to a char.
        x = pString.charAt(0);
        // Return result.
        return x;
    }

    /**
     * Converts a number from a String to an int. Used only for converting an index value from a ResourceBundle to a mnemonic index for a label.
     * See javax.swing.JLabel#setDisplayedMnemonicIndex(int)
     * @param pString the number to convert.
     * @return an integer representation of the number or -1 if pString is null or does not contain a number.
     */
    public static int getIndex(String pString)
    {
        // Create return variable.
        int x = -1;
        // Verify parameters.
        if(pString == null)
        {
            return x;
        }
        // Parse the string into a number.
        try
        {
            Integer i = new Integer(pString);
            x = i.intValue();
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return x;
    }

}
