package com.infotraxx.carfax.setup.install;

import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.XML;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.LineSeparator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Manages authentication configuration information.
 * Uses security.xml.
 * @author Ed Jenkins
 */
public class Security
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Security.class);

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * Filename.
     */
    private static String strFilename = null;

    /**
     * Host.
     */
    private static String strHost = "";

    /**
     * Port.
     */
    private static String strPort = "";

    /**
     * Username.
     */
    private static String strUsername = "";

    /**
     * Password.
     */
    private static String strPassword = "";
    
    /**
     * Variables.
     */
    private static TreeMap<String,String> tmVariables = new TreeMap<String,String>();

    /**
     * Constructor.
     */
    public Security()
    {
    }

    /**
     * Gets the filename.
     * @return the full path to the config file.
     */
    public static String getFilename()
    {
        if(strFilename == null)
        {
            // Assemble filename.
            StringBuilder sb = new StringBuilder(STRING_BUFFER_SIZE);
            sb.append(App.getDir(App.APP_DATA_DIR));
            sb.append(File.separator);
            sb.append("security.xml");
            strFilename = sb.toString();
        }
        // Return result.
        return strFilename;
    }

    /**
     * Reads the file.
     * If it doesn't exist, a new document is created.
     */
    public static void read()
    {
        // Create temp variables.
        int x = 0;
        int y = 0;
        // Reset values.
        strHost = App.getValue("app.default.host");
        strPort = App.getValue("app.default.port");
        strUsername = "";
        strPassword = "";
        tmVariables.clear();
        // Read file.
        Security.getFilename();
        Document doc = XML.read(strFilename);
        if(doc == null)
        {
            Security.write();
            return;
        }
        Element eDoc = doc.getDocumentElement();
        // host
        NodeList nlHost = eDoc.getElementsByTagName("host");
        Node nHost = nlHost.item(0);
        if(nHost != null)
        {
            Element eHost = (Element)nHost;
            String strTempHost = eHost.getTextContent();
            strHost = strTempHost.trim();
        }
        // port
        NodeList nlPort = eDoc.getElementsByTagName("port");
        Node nPort = nlPort.item(0);
        if(nPort != null)
        {
            Element ePort = (Element)nPort;
            String strTempPort = ePort.getTextContent();
            strPort = strTempPort.trim();
        }
        // username
        NodeList nlUsername = eDoc.getElementsByTagName("username");
        Node nUsername = nlUsername.item(0);
        if(nUsername != null)
        {
            Element eUsername = (Element)nUsername;
            String strTempUsername = eUsername.getTextContent();
            strUsername = strTempUsername.trim();
        }
        // password
        NodeList nlPassword = eDoc.getElementsByTagName("password");
        Node nPassword = nlPassword.item(0);
        if(nPassword != null)
        {
            Element ePassword = (Element)nPassword;
            String strTempPassword = ePassword.getTextContent();
            strPassword = strTempPassword.trim();
        }
        // variable
        NodeList nlVariable = eDoc.getElementsByTagName("variable");
        x = 0;
        y = nlVariable.getLength();
        for(x=0; x<y; x++)
        {
            Node nVariable = nlVariable.item(x);
            Element eVariable = (Element)nVariable;
            String strVariableName = eVariable.getAttribute("name");
            String strVariableValue = eVariable.getTextContent();
            if(strVariableName.equalsIgnoreCase("#example#"))
            {
                continue;
            }
            tmVariables.put(strVariableName, strVariableValue);
        }
        String strDateStampString = App.getDateStampString();
        tmVariables.put("#DateStamp#", strDateStampString);
    }

    /**
     * Writes the file.
     */
    public static void write()
    {
        Document doc = XML.newDocument(null, "security", null, null);
        Element eDoc = doc.getDocumentElement();
        // host
        Element eHost = doc.createElement("host");
        eHost.setTextContent(strHost);
        eDoc.appendChild(eHost);
        // port
        Element ePort = doc.createElement("port");
        ePort.setTextContent(strPort);
        eDoc.appendChild(ePort);
        // username
        Element eUsername = doc.createElement("username");
        eUsername.setTextContent(strUsername);
        eDoc.appendChild(eUsername);
        // password
        Element ePassword = doc.createElement("password");
        ePassword.setTextContent(strPassword);
        eDoc.appendChild(ePassword);
        // variable
        Set set = tmVariables.keySet();
        Iterator i = set.iterator();
        while(i.hasNext())
        {
            String strName = (String)i.next();
            String strValue = (String)tmVariables.get(strName);
            if(strName.equalsIgnoreCase("#example#"))
            {
                continue;
            }
            if(strName.equalsIgnoreCase("#DateStamp#"))
            {
                continue;
            }
            Element eVariable = doc.createElement("variable");
            eVariable.setAttribute("name", strName);
            eVariable.setTextContent(strValue);
            eDoc.appendChild(eVariable);
        }
        if(tmVariables.size() == 0)
        {
            Element eVariable = doc.createElement("variable");
            eVariable.setAttribute("name", "#example#");
            eVariable.setTextContent("example");
            eDoc.appendChild(eVariable);
        }
        // Save the file.
        Security.getFilename();
        XML.write(strFilename, null, null, "xml", LineSeparator.Windows, doc);
    }

    /**
     * Gets the host name.
     * @return the host name.
     */
    public static String getHost()
    {
        return strHost;
    }

    /**
     * Sets the host name.
     * @param pHost the host name.
     */
    public static void setHost(String pHost)
    {
        if(pHost == null)
        {
            strHost = "";
        }
        else
        {
            strHost = pHost;
        }
    }

    /**
     * Gets the port number.
     * @return the port number.
     */
    public static String getPort()
    {
        return strPort;
    }

    /**
     * Sets the port number.
     * @param pPort the port number.
     */
    public static void setPort(String pPort)
    {
        if(pPort == null)
        {
            strPort = "";
        }
        else
        {
            strPort = pPort;
        }
    }

    /**
     * Gets the username.
     * @return the username.
     */
    public static String getUsername()
    {
        return strUsername;
    }

    /**
     * Sets the username.
     * @param pUsername the username.
     */
    public static void setUsername(String pUsername)
    {
        if(pUsername == null)
        {
            strUsername = "";
        }
        else
        {
            strUsername = pUsername;
        }
    }

    /**
     * Gets the password.
     * @return the password.
     */
    public static String getPassword()
    {
        return strPassword;
    }

    /**
     * Sets the password.
     * @param pPassword the password.
     */
    public static void setPassword(String pPassword)
    {
        if(pPassword == null)
        {
            strPassword = "";
        }
        else
        {
            strPassword = pPassword;
        }
    }
    
    /**
     * Gets the variables.
     */
    public static TreeMap getVariables()
    {
        // Create return variable.
        TreeMap<String,String> r = new TreeMap<String,String>();
        // Copy data.
        Set set = tmVariables.keySet();
        Iterator i = set.iterator();
        while(i.hasNext())
        {
            String strName = (String)i.next();
            String strValue = (String)tmVariables.get(strName);
            r.put(strName, strValue);
        }
        // Return result.
        return r;
    }
    
    /**
     * Adds variables.
     * @param pVariables the variables to add.
     */
    public static void addVariables(TreeMap pVariables)
    {
        // Verify parameters.
        if(pVariables == null)
        {
            return;
        }
        // Add them.
        Set set = pVariables.keySet();
        Iterator i = set.iterator();
        while(i.hasNext())
        {
            String strName = (String)i.next();
            String strValue = (String)pVariables.get(strName);
            tmVariables.put(strName, strValue);
        }
    }

}
