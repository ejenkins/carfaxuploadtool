package com.infotraxx.carfax.setup.install;

import com.infotraxx.carfax.setup.util.App;
import com.infotraxx.carfax.setup.util.XML;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Authenticates a user.
 * @author Ed Jenkins
 */
public class Authenticator
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Authenticator.class);

    /**
     * Username.
     */
    private String strUsername = null;

    /**
     * Password.
     */
    private String strPassword = null;
    
    /**
     * Variables.
     */
    private TreeMap<String,String> tmVariables = new TreeMap<String,String>();

    /**
     * Consructor.
     */
    public Authenticator()
    {
    }

    /**
     * Authenticates a user.
     * @param pEmail the email address to use for authentication.
     * @throws IllegalStateException if an error occurs.
     */
    public void authenticate(String pEmail) throws IllegalStateException
    {
        // Reset values.
        strUsername = null;
        strPassword = null;
        tmVariables.clear();
        // Verify parameters.
        if(pEmail == null)
        {
            throw new IllegalStateException("Please enter an email address.");
        }
        // Create temp variables.
        String strURL = App.getValue("app.authentication.url");
        String strPackageName = App.getValue("app.package.name");
        HttpClient hc = new HttpClient();
        GetMethod gm = new GetMethod(strURL);
        gm.addRequestHeader("User-Agent", strPackageName);
        int intResult = 0;
        InputStream is = null;
        Document doc = null;
        try
        {
            NameValuePair email = new NameValuePair("email", pEmail);
            NameValuePair[] qs = {email};
            gm.setQueryString(qs);
            if(!gm.validate())
            {
                throw new IllegalStateException("Please enter a valid email address.");
            }
            intResult = hc.executeMethod(gm);
            logger.debug("intResult=" + intResult);
            if(intResult != 200)
            {
                throw new IllegalStateException("HTTP error code " + intResult);
            }
            is = gm.getResponseBodyAsStream();
            doc = XML.parse(is);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        finally
        {
            if(is != null)
            {
                try
                {
                    is.close();
                }
                catch(Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
            gm.releaseConnection();
        }
        if(doc == null)
        {
            throw new IllegalStateException("Unable to parse result.");
        }
        Element eDoc = doc.getDocumentElement();
        // ErrorMessage
        NodeList nlErrorResponse = eDoc.getElementsByTagName("ErrorResponse");
        Node nErrorResponse = nlErrorResponse.item(0);
        if(nErrorResponse != null)
        {
            Element eErrorResponse = (Element)nErrorResponse;
            NodeList nlErrorMessage = eErrorResponse.getElementsByTagName("ErrorMessage");
            Node nErrorMessage = nlErrorMessage.item(0);
            if(nErrorMessage != null)
            {
                Element eErrorMessage = (Element)nErrorMessage;
                String strTempErrorMessage = eErrorMessage.getTextContent();
                String strErrorMessage = strTempErrorMessage.trim();
                throw new IllegalStateException(strErrorMessage);
            }
        }
        // username
        NodeList nlUsername = eDoc.getElementsByTagName("username");
        Node nUsername = nlUsername.item(0);
        if(nUsername == null)
        {
            throw new IllegalStateException("Unable to get username.");
        }
        Element eUsername = (Element)nUsername;
        String strTempUsername = eUsername.getTextContent();
        strUsername = strTempUsername.trim();
        // password
        NodeList nlPassword = eDoc.getElementsByTagName("password");
        Node nPassword = nlPassword.item(0);
        if(nPassword == null)
        {
            throw new IllegalStateException("Unable to get username.");
        }
        Element ePassword = (Element)nPassword;
        String strTempPassword = ePassword.getTextContent();
        strPassword = strTempPassword.trim();
        // variable
        NodeList nlVariable = eDoc.getElementsByTagName("variable");
        int x = 0;
        int y = nlVariable.getLength();
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
    }

    /**
     * Gets the username.
     * @return the username.
     */
    public String getUsername()
    {
        return strUsername;
    }
    
    /**
     * Gets the password.
     * @return the password.
     */
    public String getPassword()
    {
        return strPassword;
    }
    
    /**
     * Gets the variables.
     */
    public TreeMap getVariables()
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

}
