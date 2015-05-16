package com.infotraxx.carfax.setup.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides a central location for specifying all fonts used by the application
 * in order to ensure consistiency among all GUI components.
 * @author Ed Jenkins
 */
public class Fonts
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Fonts.class);

    /**
     * The file containing all of the SQL commands.
     */
    public static final String FILENAME = "res/data/fonts.xml";

    /**
     * Font map.
     * This contains only the fonts that this application knows in advance that it wants to use.
     * Values are stored in fonts.xml.
     */
    private static final TreeMap<String, Font> tmFonts = new TreeMap<String, Font>();

    /**
     * The font to use for titles.
     * @see #getFont(String)
     */
    public static final String TITLES = "TITLES";

    /**
     * The font to use for instructions.
     * This is used in a JEditorPane,
     * so appropriate values for the size are determined by the HTML specification,
     * not the Font class.
     * Values are stored in fonts.xml.
     * @see #getFont(String)
     */
    public static final String INSTRUCTIONS = "INSTRUCTIONS";

    /**
     * The font to use for labels.
     * @see #getFont(String)
     */
    public static final String LABELS = "LABELS";

    /**
     * The font to use for fields.
     * @see #getFont(String)
     */
    public static final String FIELDS = "FIELDS";

    /**
     * The font to use for password fields.
     * @see #getFont(String)
     */
    public static final String PASSWORDS = "PASSWORDS";

    /**
     * The font to use for buttons.
     * @see #getFont(String)
     */
    public static final String BUTTONS = "BUTTONS";

    /**
     * The font to use for notes.
     * @see #getFont(String)
     */
    public static final String NOTES = "NOTES";

    /**
     * The font to use for core dumps.
     * This is used in a JEditorPane,
     * so appropriate values for the size are determined by the HTML specification,
     * not the Font class.
     * Values are stored in fonts.xml.
     * @see #getFont(String)
     */
    public static final String CORE = "CORE";

    /**
     * The base font to use when displaying the End User License Agreement.
     * This is used in a JEditorPane,
     * so appropriate values for the size are determined by the HTML specification,
     * not the Font class.
     * Values are stored in fonts.xml.
     * @see #getFont(String)
     */
    public static final String EULA = "EULA";

    /**
     * Read flag.
     */
    private static boolean booRead = false;

    /**
     * Constructor.
     */
    public Fonts()
    {
    }

    /**
     * Gets a list of font family names installed on this system.
     * @return a list of font family names.
     */
    public static Vector getFamilies()
    {
        // Create return variable.
        Vector<String> v = new Vector<String>();
        // Get a list of family names.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] s = ge.getAvailableFontFamilyNames();
        // Loop through them and add them to the vector.
        int x = 0;
        int y = s.length;
        for (x = 0; x < y; x++)
        {
            v.add(s[x]);
        }
        // Return result.
        return v;
    }

    /**
     * Gets a list of fonts installed on this system.
     * @return a list of fonts.
     */
    public static Vector getFonts()
    {
        // Create return variable.
        Vector<Font> v = new Vector<Font>();
        // Get a list of fonts.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] f = ge.getAllFonts();
        // Loop through them and add them to the vector.
        int x = 0;
        int y = f.length;
        for (x = 0; x < y; x++)
        {
            v.add(f[x]);
        }
        // Return result.
        return v;
    }

    /**
     * Loads fonts.xml and loads fonts into the font map.
     */
    public static void load()
    {
        // Load file.
        Document doc = XML.load(FILENAME);
        if(doc == null)
        {
            return;
        }
        // Clear the font map.
        logger.debug("Begin loading fonts.");
        tmFonts.clear();
        // Get the document element.
        Element eFonts = doc.getDocumentElement();
        // Get a list of its children.
        NodeList nlFonts = eFonts.getChildNodes();
        // Loop through them.
        int x = 0;
        int y = nlFonts.getLength();
        for(x=0; x<y; x++)
        {
            Node n = nlFonts.item(x);
            // Only process elements.
            if(n.getNodeType() != Node.ELEMENT_NODE)
            {
                continue;
            }
            // Read font data.
            Element e = (Element)n;
            String strID = e.getAttribute("id");
            String strName = e.getAttribute("name");
            String strStyle = e.getAttribute("style");
            String strSize = e.getAttribute("size");
            // Convert style.
            int intStyle = Font.PLAIN;
            if(strStyle.equalsIgnoreCase("BOLD"))
            {
                intStyle = Font.BOLD;
            }
            if(strStyle.equalsIgnoreCase("ITALIC"))
            {
                intStyle = Font.ITALIC;
            }
            if(strStyle.equalsIgnoreCase("BOLDITALIC"))
            {
                intStyle = Font.BOLD | Font.ITALIC;
            }
            // Convert size.
            int intSize = 8;
            try
            {
                intSize = Integer.parseInt(strSize);
            }
            catch(Exception ex)
            {
                logger.fatal(ex, ex);
            }
            logger.debug("Loading font for " + strID + ".");
            // Create a Font.
            Font font = new Font(strName, intStyle, intSize);
            // Save it in the cache.
            tmFonts.put(strID, font);
        }
        booRead = true;
        logger.debug("End loading fonts.");
    }

    /**
     * Gets a font.
     * @param pID the ID of the font to get.
     * @return a font.
     * @see #TITLES
     * @see #INSTRUCTIONS
     * @see #LABELS
     * @see #FIELDS
     * @see #BUTTONS
     * @see #NOTES
     * @see #CORE
     * @see #EULA
     */
    public static Font getFont(String pID)
    {
        // Verify state.
        if(booRead == false)
        {
            load();
        }
        // Create return variable.
        Font font = tmFonts.get(pID);
        // Return result;
        return font;
    }

    /**
     * Gets an array of font information.
     * @param pID the ID of the font to get.
     * @return an array of strings containing the name and size.
     * @see #TITLES
     * @see #INSTRUCTIONS
     * @see #LABELS
     * @see #FIELDS
     * @see #BUTTONS
     * @see #NOTES
     * @see #CORE
     * @see #EULA
     */
    public static String[] getFontInfo(String pID)
    {
        // Verify state.
        if(booRead == false)
        {
            load();
        }
        // Get the font.
        Font font = tmFonts.get(pID);
        if(font == null)
        {
            return null;
        }
        // Get info.
        String strName = font.getFontName();
        int intSize = font.getSize();
        String strSize = String.valueOf(intSize);
        String[] o = { strName, strSize };
        // Return result;
        return o;
    }

}
