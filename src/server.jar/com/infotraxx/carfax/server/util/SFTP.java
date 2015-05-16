package com.infotraxx.carfax.server.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileHandle;

import com.infotraxx.carfax.server.config.Security;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Uploads and downloads files to or from an SFTP server.
 * @author Ed Jenkins
 */
public class SFTP
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SFTP.class);

    /**
     * Buffer size.
     * I would love to use a larger buffer,
     * but the Ganymed SFTP library will only use 32K or less.
     */
    public static final int BUFFER_SIZE = 32 * 1024;

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * Used to clear the buffer.
     */
    public static final byte ZERO = (byte)0;

    /**
     * Buffer.
     */
    private byte[] buffer = new byte[BUFFER_SIZE];

    /**
     * Run flag.
     */
    private boolean RUN_FLAG = true;

    /**
     * Constructor.
     */
    public SFTP()
    {
        Security.read();
    }
    
    /**
     * Connects to a server.
     * @return a Connection or null if unable to connect.
     */
    private Connection connect()
    {
        // Get configuration information.
        String strHost = Security.getHost();
        String strPort = Security.getPort();
        // Verify state.
        if(strHost.equalsIgnoreCase(""))
        {
            logger.error("Please specify a host name.");
            return null;
        }
        if(strPort.equalsIgnoreCase(""))
        {
            logger.error("Please specify a port number.");
            return null;
        }
        int intPort = 0;
        try
        {
            intPort = Integer.parseInt(strPort);
        }
        catch(Exception ex)
        {
            logger.error("Invalid port number.", ex);
            return null;
        }
        // Create return variable.
        Connection con = null;
        // Connect to the server.
        try
        {
            logger.debug("Trying to connect.");
            con = new Connection(strHost, intPort);
            con.connect();
            logger.debug("Connected.");
        }
        catch(Exception ex)
        {
            logger.error("Unable to connect.", ex);
            if(con != null)
            {
                con.close();
            }
            con = null;
        }
        // Return result.
        return con;
    }
    
    /**
     * Logs into a server using a username and password.
     * @param con a connection.
     * @return a client or null if con is null or authentication fails.
     */
    private SFTPv3Client loginWithPassword(Connection con)
    {
        // Verify parameters.
        if(con == null)
        {
            logger.error("con is null.");
            return null;
        }
        // Get configuration information.
        String strUsername = Security.getUsername();
        String strPassword = Security.getPassword();
        // Verify state.
        if(strUsername.equalsIgnoreCase(""))
        {
            logger.error("Please specify a username.");
            return null;
        }
        if(strPassword.equalsIgnoreCase(""))
        {
            logger.error("Please specify a password.");
            return null;
        }
        // Login.
        boolean booAuthenticated = false;
        try
        {
            logger.debug("Trying to login.");
            booAuthenticated = con.authenticateWithPassword(strUsername, strPassword);
        }
        catch(Exception ex)
        {
            logger.error("Unable to authenticate.", ex);
            return null;
        }
        if(booAuthenticated == false)
        {
            logger.debug("Authentication failed.");
            return null;
        }
        logger.debug("Authentication succeeded.");
        // Start an SFTP session.
        SFTPv3Client client = null;
        try
        {
            client = new SFTPv3Client(con);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
            return null;
        }
        // Return result.
        return client;
    }

    /**
     * Logs into a server.
     * @param con a connection.
     * @return a client or null if authentication fails or if unable to connect.
     */
    private SFTPv3Client login(Connection con)
    {
        // Verify parameters.
        if(con == null)
        {
            logger.error("con is null.");
            return null;
        }
        // Create return variable.
        SFTPv3Client client = null;
        // Get configuration information.
        String strUsername = Security.getUsername();
        // Check availability.
        boolean booPasswordMethodAvailable = false;
        try
        {
            booPasswordMethodAvailable = con.isAuthMethodAvailable(strUsername, "password");
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
            return null;
        }
        // Login.
        if(client == null)
        {
            if(booPasswordMethodAvailable)
            {
                client = this.loginWithPassword(con);
            }
        }
        return client;
    }

    /**
     * Uploads a file.
     * @param pSource the source filename.
     * @param pDest the destination filename.
     */
    public void upload(String pSource, String pDest)
    {
        // Verify parameters.
        if(pSource == null)
        {
            logger.error("pSource is null.");
            return;
        }
        if(pDest == null)
        {
            logger.error("pDest is null.");
            return;
        }
        // Verify source file exists.
        File f = null;
        try
        {
            f = new File(pSource);
            if(!f.exists())
            {
                logger.error(pSource + " does not exist.");
                return;
            }
            if(f.isDirectory())
            {
                logger.error(pSource + " is a directory.  I can not upload a directory.  I can only upload a file.");
                return;
            }
            if(!f.isFile())
            {
                logger.error(pSource + " is a special file.  I can not upload special files.  I can only upload normal files.");
                return;
            }
            if(!f.canRead())
            {
                logger.error("I don't have permission to read " + pSource);
                return;
            }
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
            return;
        }
        // Login.
        Connection con = this.connect();
        if(con == null)
        {
            logger.error("con is null.");
            return;
        }
        SFTPv3Client client = this.login(con);
        if(client == null)
        {
            logger.error("client is null.");
            con.close();
            return;
        }
        logger.info("uploading " + pSource);
        SFTPv3FileHandle h = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        int x = 0;
        long lngOffset = 0L;
        try
        {
            // Get more information about the file.
            String strName = f.getName();
            String strCanon = f.getCanonicalPath();
            long lngSize = f.length();
            long lngLastModified = f.lastModified();
            NumberFormat nf = NumberFormat.getIntegerInstance();
            String strSize = nf.format(lngSize);
            String strLastModified = App.formatDateTime(lngLastModified);
            boolean booCanRead = f.canRead();
            boolean booCanWrite = f.canWrite();
            boolean booCanExecute = f.canExecute();
            logger.debug("Name = " + strName);
            logger.debug("Filename = " + pSource);
            logger.debug("Canonical Filename = " + strCanon);
            logger.debug("File Size = " + strSize);
            logger.debug("Last Modified = " + strLastModified);
            logger.debug("Can Read = " + booCanRead);
            logger.debug("Can Write = " + booCanWrite);
            logger.debug("Can Execute = " + booCanExecute);
            // Clear the buffer.
            Arrays.fill(buffer, ZERO);
            // Open the input file
            fis = new FileInputStream(f);
            bis = new BufferedInputStream(fis, BUFFER_SIZE);
            // Open the output file.
            h = client.createFile(strName);
            // Copy the file.
            logger.debug("begin copy");
            while(true)
            {
                // Respond to interrupt signal when required.
                if(RUN_FLAG == false)
                {
                    logger.info("cancelling...");
                    break;
                }
                x = bis.read(buffer, 0, BUFFER_SIZE);
                if(x == -1)
                {
                    break;
                }
                client.write(h, lngOffset, buffer, 0, x);
                lngOffset += x;
            }
            logger.debug("end copy");
        }
        catch(IOException ex)
        {
            logger.error(ex, ex);
            return;
        }
        finally
        {
            if(bis != null)
            {
                try
                {
                    bis.close();
                }
                catch(Exception ex)
                {
                }
            }
            if(h != null)
            {
                try
                {
                    client.closeFile(h);
                }
                catch(Exception ex)
                {
                }
            }
            if(client != null)
            {
                client.close();
            }
            if(con != null)
            {
                con.close();
            }
        }
    }
    
    /**
     * Uploads a file.
     * @param pSource the source filename.
     * @param pDest the destination filename.
     */
    public void download(String pSource, String pDest)
    {
        // Verify parameters.
        if(pSource == null)
        {
            logger.error("pSource is null.");
            return;
        }
        if(pDest == null)
        {
            logger.error("pDest is null.");
            return;
        }
        // Login.
        Connection con = this.connect();
        if(con == null)
        {
            logger.error("con is null.");
            return;
        }
        SFTPv3Client client = this.login(con);
        if(client == null)
        {
            logger.error("client is null.");
            con.close();
            return;
        }
        logger.info("downloading " + pSource);
        SFTPv3FileHandle h = null;
        File f = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        int x = 0;
        long lngOffset = 0L;
        try
        {
            // Clear the buffer.
            Arrays.fill(buffer, ZERO);
            // Open the Input file.
            h = client.openFileRO(pSource);
            // Open the output file
            f = new File(pDest);
            fos = new FileOutputStream(f);
            bos = new BufferedOutputStream(fos, BUFFER_SIZE);
            // Copy the file.
            logger.debug("begin copy");
            while(true)
            {
                // Respond to interrupt signal when required.
                if(RUN_FLAG == false)
                {
                    logger.info("cancelling...");
                    break;
                }
                x = client.read(h, lngOffset, buffer, 0, BUFFER_SIZE);
                if(x == -1)
                {
                    break;
                }
                bos.write(buffer, 0, x);
                lngOffset += x;
            }
            logger.debug("end copy");
        }
        catch(IOException ex)
        {
            String strMessage = ex.getMessage();
            if(strMessage.contains("SSH_FX_NO_SUCH_FILE"))
            {
                logger.info("The file does not exist on the SFTP server.");
            }
            else
            {
                logger.error(ex, ex);
            }
        }
        finally
        {
            if(h != null)
            {
                try
                {
                    client.closeFile(h);
                }
                catch(Exception ex)
                {
                }
            }
            if(bos != null)
            {
                try
                {
                    bos.close();
                }
                catch(Exception ex)
                {
                }
            }
            if(client != null)
            {
                client.close();
            }
            if(con != null)
            {
                con.close();
            }
        }
    }
    
    /**
     * Deletes a file on the server.
     * @param pFilename the name of the file to delete.
     */
    public void delete(String pFilename)
    {
        // Verify parameters.
        if(pFilename == null)
        {
            logger.error("pFilename is null.");
            return;
        }
        // Login.
        Connection con = this.connect();
        if(con == null)
        {
            logger.error("con is null.");
            return;
        }
        SFTPv3Client client = this.login(con);
        if(client == null)
        {
            logger.error("client is null.");
            con.close();
            return;
        }
        logger.info("deleting " + pFilename);
        try
        {
            client.rm(pFilename);
        }
        catch(Exception ex)
        {
            String strMessage = ex.getMessage();
            if(strMessage.contains("SSH_FX_NO_SUCH_FILE"))
            {
                logger.info("The file does not exist on the SFTP server.");
            }
            else
            {
                logger.error(ex, ex);
            }
        }
        finally
        {
            if(con != null)
            {
                con.close();
            }
        }
    }

    /**
     * Stops the upload or download if one is in progress.
     */
    public void interrupt()
    {
        RUN_FLAG = false;
    }

}
