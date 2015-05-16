package com.infotraxx.carfax.upgrade.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Ports.
 * @author Ed Jenkins
 */
public class Ports
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Ports.class);

    /**
     * The lowest possible port number.
     */
    public static final int MIN_PORT = 1;

    /**
     * The highest possible port number.
     */
    public static final int MAX_PORT = 65535;

    /**
     * How long to wait to make a connection.
     */
    public static final int TIMEOUT = 1000;

    /**
     * Constructor.
     */
    public Ports()
    {
    }

    /**
     * Validates that a port number is in the valid range of 0 to 65535.
     * @param pPort the port number to test.
     * @return true if the port is valid or false if not.
     * @see #isListening(int)
     * @see #isAvailable(int)
     */
    public static boolean isValid(int pPort)
    {
        if(pPort < MIN_PORT)
        {
            return false;
        }
        if(pPort > MAX_PORT)
        {
            return false;
        }
        return true;
    }

    /**
     * Tests to see if a service is listening on a port.
     * @param pPort the port number to test.
     * @return true if a service is listening on the port or false if not.
     * @see #isValid(int)
     * @see #isAvailable(int)
     */
    public static boolean isListening(int pPort)
    {
        // Create return variable.
        boolean b = false;
        // Create temp variables.
        String strHost = null;
        InetAddress ia = null;
        InetSocketAddress isa = null;
        Socket socket = new Socket();
        // Try to connect.
        try
        {
            ia = InetAddress.getLocalHost();
            strHost = ia.getCanonicalHostName();
            if(App.isInitialized())
            {
                logger.debug("strHost=" + strHost);
                logger.debug("pPort=" + pPort);
            }
            isa = new InetSocketAddress(strHost, pPort);
            socket.connect(isa, TIMEOUT);
        }
        catch(Exception ex)
        {
        }
        b = socket.isConnected();
        if(b)
        {
            try
            {
                socket.close();
            }
            catch(Exception ex)
            {
            }
        }
        // Return result.
        return b;
    }

    /**
     * Tests to see if a service is listening on a port on a remote host.
     * @param pHost the host to test.
     * @param pPort the port number to test.
     * @return true if a service is listening on the port or false if not.
     * @see #isValid(int)
     * @see #isAvailable(String, int)
     */
    public static boolean isListening(String pHost, int pPort)
    {
        // Create return variable.
        boolean b = false;
        // Create temp variables.
        String strHost = null;
        InetSocketAddress isa = null;
        Socket socket = new Socket();
        // Try to connect.
        try
        {
            if(App.isInitialized())
            {
                logger.debug("pHost=" + pHost);
                logger.debug("pPort=" + pPort);
            }
            isa = new InetSocketAddress(pHost, pPort);
            socket.connect(isa, TIMEOUT);
        }
        catch(Exception ex)
        {
        }
        b = socket.isConnected();
        if(b)
        {
            try
            {
                socket.close();
            }
            catch(Exception ex)
            {
            }
        }
        // Return result.
        return b;
    }

    /**
     * Tests to see if a port is available.
     * @param pPort the port number to test.
     * @return true if no one is listening on the port or false if not.
     * @see #isValid(int)
     * @see #isListening(int)
     */
    public static boolean isAvailable(int pPort)
    {
        return(!(Ports.isListening(pPort)));
    }

    /**
     * Tests to see if a port is available.
     * @param pHost the host to test.
     * @param pPort the port number to test.
     * @return true if no one is listening on the port or false if not.
     * @see #isValid(int)
     * @see #isListening(String, int)
     */
    public static boolean isAvailable(String pHost, int pPort)
    {
        return(!(Ports.isListening(pHost, pPort)));
    }

}
