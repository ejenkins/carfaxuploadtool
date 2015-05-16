package com.infotraxx.carfax.upgrade.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

/**
 * CommandLine.
 * Parses the command line for switches, options, and arguments.
 * If a string in the argument array begins with '-', then it is a switch.
 * If it also has an '=' with characters surrounding it, then it is an option.
 * An option is a name/value pair, having an equal sign
 * as a delimiter between the name and the value.
 * If it is neither a switch nor an option, then it is an argument.
 * <pre>
 *
 *     Examples:
 *     A switch looks like this:       -debug
 *     An option looks like this:      -lang=es
 *     An argument looks like this:    file.txt
 *
 *     Example:
 *     %java MyApp -debug -lang=es file.ext
 *
 * </pre>
 * @author Ed Jenkins
 */
public class CommandLine
{

    /**
     * The original command-line arguments.
     */
    private static String[] strArgs = new String[0];

    /**
     * Switches.
     * A switch looks like "-debug".
     */
    private static Vector<String> vecSwitches = new Vector<String>();

    /**
     * Options.
     * An option looks like "-lang=fr".
     */
    private static TreeMap<String,String> tmOptions = new TreeMap<String,String>();

    /**
     * Arguments.
     * An argument looks like "file.txt".
     */
    private static Vector<String> vecArguments = new Vector<String>();

    /**
     * Constructor.
     */
    public CommandLine()
    {
    }

    /**
     * Constructor.
     * @param args command-line arguments.
     */
    public CommandLine(String[] args)
    {
        CommandLine.parse(args);
    }

    /**
     * Parses the command line.
     * @param args an array of strings.
     */
    public static void parse(String[] args)
    {
        //  Verify parameters.
        if(args == null)
        {
            return;
        }
        //  Create temp variables.
        int x = 0;
        int y = args.length;
        int z = 0;
        //  Clear caches.
        strArgs = new String[y];
        vecSwitches.clear();
        tmOptions.clear();
        vecArguments.clear();
        //  Loop through the arguments.
        for(x=0; x<y; x++)
        {
            //  Save a copy of the original arguments.
            String s = new String(args[x]);
            strArgs[x] = s;
            //  Parse the command-line.
            if(args[x].startsWith("-"))
            {
                //  See if it's an option or a switch.
                z = args[x].indexOf("=");
                if((args[x].length() > 3) && (z > 1))
                {
                    //  It's an option.
                    String key = args[x].substring(1, z);
                    String val = args[x].substring(z + 1);
                    tmOptions.put(key, val);
                }
                else
                {
                    //  It's a switch.
                    vecSwitches.add(args[x].substring(1));
                }
            }
            else
            {
                //  It's an argument.
                vecArguments.add(args[x]);
            }
        }
    }

    /**
     * Checks to see if the specified switch is on the command line.
     * @param pSwitch the switch to look for.
     * @return true if the switch exists or false if not.
     */
    public static boolean hasSwitch(String pSwitch)
    {
        //  Verify parameters.
        if(pSwitch == null)
        {
            return false;
        }
        //  Look in the collection.
        boolean b = vecSwitches.contains(pSwitch);
        //  Return result.
        return b;
    }

    /**
     * Checks to see if the specified option is on the command line.
     * @param pName the name of the option to look for.
     * @return true if the option exists or false if not.
     */
    public static boolean hasOption(String pName)
    {
        //  Verify parameters.
        if(pName == null)
        {
            return false;
        }
        //  Look in the collection.
        boolean b = tmOptions.containsKey(pName);
        //  Return result.
        return b;
    }

    /**
     * Checks to see if the specified argument is on the command line.
     * @param pArgument the argument to look for.
     * @return true if the argument exists or false if not.
     */
    public static boolean hasArgument(String pArgument)
    {
        //  Verify parameters.
        if(pArgument == null)
        {
            return false;
        }
        //  Look in the collection.
        boolean b = vecArguments.contains(pArgument);
        //  Return result.
        return b;
    }

    /**
     * Gets the original command-line arguments.
     * @return a copy of the original command-line arguments.
     */
    public static String[] getCommandLine()
    {
        //  Create temp variables.
        int x = 0;
        int y = strArgs.length;
        //  Create return variable.
        String[] ss = new String[y];
        for(x=0; x<y; x++)
        {
            String s = new String(strArgs[x]);
            ss[x] = s;
        }
        //  Return result.
        return ss;
    }

    /**
     * Gets the switches.
     * @return a copy of the switches.
     */
    public static Vector getSwitches()
    {
        //  Create return variable.
        Vector<String> v = new Vector<String>();
        //  Loop through the collection.
        Iterator i = vecSwitches.iterator();
        while(i.hasNext())
        {
            //  Get an object.
            String s = (String) i.next();
            //  Add it to the new collection.
            v.add(s);
        }
        //  Return result.
        return v;
    }

    /**
     * Gets the option names.
     * @return a copy of the option names.
     * Use getOption(String) to get the value of a certain option.
     * @see #getOption(String)
     */
    public static Vector getOptions()
    {
        //  Create return variable.
        Vector<String> v = new Vector<String>();
        //  Loop through the keys.
        Set set = tmOptions.keySet();
        Iterator i = set.iterator();
        while(i.hasNext())
        {
            //  Get one.
            String s = (String) i.next();
            //  Add it to the list.
            v.add(s);
        }
        //  Return result.
        return v;
    }

    /**
     * Gets the value of the specified option from the command line.
     * @param pName the name of the option to look for.
     * @return the value associated with the specified name
     * or null if the name does not exist.
     * @see #getOptions()
     */
    public static String getOption(String pName)
    {
        //  Look in the collection.
        String s = tmOptions.get(pName);
        //  Return result.
        return s;
    }

    /**
     * Gets the arguments.
     * @return a copy of the arguments.
     */
    public static Vector getArguments()
    {
        //  Create return variable.
        Vector<String> v = new Vector<String>();
        //  Loop through the collection.
        Iterator i = vecArguments.iterator();
        while(i.hasNext())
        {
            //  Get an object.
            String s = (String) i.next();
            //  Add it to the new collection.
            v.add(s);
        }
        //  Return result.
        return v;
    }

    /**
     * Gets the value of the specified argument.
     * @param pNumber the number of the argument to get.
     * @return the argument's value or null if the number is
     * less than zero or greater than the total number of arguments available.
     */
    public static String getArgument(int pNumber)
    {
        if(pNumber < 0)
        {
            return null;
        }
        int x = vecArguments.size();
        if(x == 0)
        {
            return null;
        }
        if(pNumber > x)
        {
            return null;
        }
        String r = vecArguments.get(pNumber);
        return r;
    }

}
