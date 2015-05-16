package com.infotraxx.carfax.server.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * Runs programs.
 * See <a href="http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html" target="_new">When Runtime.exec() won't</a>
 * @author Ed Jenkins
 * @see Drainer
 */
public class Runner
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Runner.class);

    /**
     * We will wait for the child process to exit.
     * @see #getMode()
     * @see #setMode(boolean)
     */
    public static final boolean SYNCHRONOUS = true;

    /**
     * We will not wait for the child process to exit.
     * @see #getMode()
     * @see #setMode(boolean)
     */
    public static final boolean ASYNCHRONOUS = false;

    /**
     * The program to run.
     */
    private String strProgram;

    /**
     * The parameters for the program.
     */
    private ArrayList<String> alParameters = new ArrayList<String>();

    /**
     * The environment for the program.
     */
    private TreeMap<String, String> tmEnvironment = new TreeMap<String, String>();

    /**
     * The current working directory for the program to use.
     */
    private String strCWD;

    /**
     * Mode flag. SYNCHRONOUS or ASYNCHRONOUS.
     */
    private boolean booMode = SYNCHRONOUS;

    /**
     * Required flag.
     */
    private boolean booRequired = true;

    /**
     * Constructor.
     */
    public Runner()
    {
    }

    /**
     * Gets the program.
     * @return the program.
     */
    public String getProgram()
    {
        return strProgram;
    }

    /**
     * Sets the program.
     * @param pProgram the program. Can not be null.
     */
    public void setProgram(String pProgram)
    {
        // Verify parameters.
        if(pProgram == null)
        {
            return;
        }
        strProgram = pProgram;
        logger.debug(pProgram);
    }

    /**
     * Gets the parameters.
     * @return the parameters.
     */
    public String[] getParameters()
    {
        // Copy parameters to a string array.
        String[] r = (String[]) alParameters.toArray();
        // Return result.
        return r;
    }

    /**
     * Adds a parameter.
     * @param pParameter a parameter. Can not be null.
     */
    public void addParameter(String pParameter)
    {
        // Verify parameters.
        if(pParameter == null)
        {
            return;
        }
        // Add the parameter.
        alParameters.add(pParameter);
        logger.debug(pParameter);
    }

    /**
     * Gets the environment.
     * @return the environment.
     */
    public TreeMap getEnvironment()
    {
        // Create return variable.
        TreeMap<String, String> tm = new TreeMap<String, String>();
        // Copy data.
        Set set = tmEnvironment.keySet();
        Iterator i = set.iterator();
        while (i.hasNext())
        {
            String strKey = (String) i.next();
            String strVal = tmEnvironment.get(strKey);
            tm.put(strKey, strVal);
        }
        // Return result.
        return tm;
    }

    /**
     * Adds an environment variable for the program to use.
     * @param pName the variable's name. Can not be null.
     * @param pValue the variable's value. Can not be null.
     */
    public void addEnvironmentVariable(String pName, String pValue)
    {
        // Verify parameters.
        if(pName == null)
        {
            return;
        }
        if(pValue == null)
        {
            return;
        }
        // Add the environment variable.
        tmEnvironment.put(pName, pValue);
        logger.debug("name=" + pName + ", value=" + pValue);
    }

    /**
     * Gets the current working directory.
     * @return the current working directory.
     */
    public String getCWD()
    {
        // Return the current working directory.
        return strCWD;
    }

    /**
     * Sets the current working directory.
     * @param pDir the current working directory.
     */
    public void setCWD(String pDir)
    {
        // Normalize parameters.
        String s = null;
        if(pDir == null)
        {
            s = System.getProperty("user.dir");
        }
        else
        {
            s = pDir;
        }
        if(s == null)
        {
            s = ".";
        }
        try
        {
            File f = new File(s);
            strCWD = f.getCanonicalPath();
            logger.debug(strCWD);
        }
        catch (Exception ex)
        {
            logger.fatal(ex, ex);
        }
    }

    /**
     * Gets the mode.
     * @return true if SYNCHRONOUS or false if ASYNCHRONOUS.
     * @see #SYNCHRONOUS
     * @see #ASYNCHRONOUS
     */
    public boolean getMode()
    {
        return booMode;
    }

    /**
     * Sets the mode.
     * @param pMode must be either SYNCHRONOUS or ASYNCHRONOUS.
     * The default is SYNCHRONOUS.
     * @see #SYNCHRONOUS
     * @see #ASYNCHRONOUS
     */
    public void setMode(boolean pMode)
    {
        booMode = pMode;
        logger.debug((pMode == SYNCHRONOUS) ? "SYNCHRONOUS" : "ASYNCHRONOUS");
    }

    /**
     * Gets the required flag.
     * @return true if required or false if not.
     */
    public boolean getRequired()
    {
        return booRequired;
    }

    /**
     * Sets the required flag.
     * @param pRequired true if the process must terminate with exit code 0 or false if another exit code is OK. If this is set to true and the process returns an error code, it will be treated as a fatal exception.
     * The default is true.
     */
    public void setRequired(boolean pRequired)
    {
        booRequired = pRequired;
        logger.debug((pRequired == true) ? "true" : "false");
    }

    /**
     * Runs the program.
     * @return the return code from the child process.
     * @throws IllegalStateException if unable to run or if required is true and the return code is not zero.
     */
    public int run() throws IllegalStateException
    {
        // Create return variable.
        int intErrorLevel = -1;
        // Verify state.
        if(strProgram == null)
        {
            return intErrorLevel;
        }
        File filExists = null;
        boolean booExists = false;
        try
        {
            filExists = new File(strProgram);
            booExists = filExists.exists();
        }
        catch(Exception ex)
        {
            IllegalStateException ise = new IllegalStateException("Invalid filename:  " + strProgram);
            ise.initCause(ex);
            throw ise;
        }
        if(booExists == false)
        {
            IllegalStateException ise = new IllegalStateException(strProgram + " does not exist.");
            throw ise;
        }
        // Assemble the command line.
        int intArg = 0;
        int intParameter = 0;
        int intParameters = alParameters.size();
        String[] strCMD = new String[intParameters + 1];
        strCMD[0] = strProgram;
        for (intArg = 1, intParameter = 0; intParameter < intParameters; intArg++, intParameter++)
        {
            strCMD[intArg] = alParameters.get(intParameter);
        }
        // Print the command line.
        intParameter = 0;
        intParameters = strCMD.length;
        int intSpaces = intParameters - 1;
        StringBuilder sb = new StringBuilder(1024);
        for (intParameter = 0; intParameter < intParameters; intParameter++)
        {
            sb.append(strCMD[intParameter]);
            if(intParameter < intSpaces)
            {
                sb.append(" ");
            }
        }
        String strCommandLine = sb.toString();
        logger.debug(strCommandLine);
        // Assemble the environment.
        int x = 0;
        int y = tmEnvironment.size();
        String[] strENV = null;
        if(y > 0)
        {
            strENV = new String[y];
            Set set = tmEnvironment.keySet();
            Iterator i = set.iterator();
            while (i.hasNext())
            {
                String strKey = (String) i.next();
                String strVal = tmEnvironment.get(strKey);
                String s = strKey + "=" + strVal;
                strENV[x++] = s;
            }
        }
        // If CWD was not set, show what the default value is.
        if(strCWD == null)
        {
            setCWD(null);
        }
        // Print the mode.
        logger.debug("Mode=" + ((booMode == SYNCHRONOUS) ? "SYNCHRONOUS" : "ASYNCHRONOUS"));
        // Run it.
        try
        {
            // Create a new process.
            File filCWD = null;
            if(strCWD != null)
            {
                filCWD = new File(strCWD);
            }
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(strCMD, strENV, filCWD);
            if(booMode == SYNCHRONOUS)
            {
                // Wait for it to finish.
                Drainer.drain(p);
                intErrorLevel = p.waitFor();
                logger.debug("ERRORLEVEL=" + intErrorLevel);
                if(booRequired == true)
                {
                    if(intErrorLevel > 0)
                    {
                        throw new IllegalStateException("ERROR=" + intErrorLevel);
                    }
                }
            }
            else
            {
                // If running asynchronously, the return code will usually be zero.
                // We can not determine what the actual return code is unless we
                // wait for the process to finish.
                intErrorLevel = 0;
            }
        }
        catch (Exception ex)
        {
            IllegalStateException ise = new IllegalStateException("An error occurred while trying to run " + strProgram + ".");
            ise.initCause(ex);
            throw ise;
        }
        // Return result.
        return intErrorLevel;
    }

}
