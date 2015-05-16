package com.infotraxx.carfax.server.cron;

import com.infotraxx.carfax.server.config.Security;
import com.infotraxx.carfax.server.util.App;
import com.infotraxx.carfax.server.util.SFTP;
import com.infotraxx.carfax.server.util.XML;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.UnableToInterruptJobException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Downloads security.xml.
 * @author Ed Jenkins
 */
public class SFTPDownloadSecurityJob implements InterruptableJob
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SFTPDownloadSecurityJob.class);

    /**
     * Name.
     */
    public static final String NAME = "sftp.download.security.job";

    /**
     * Description.
     */
    public static final String DESCRIPTION = "Downloads security.xml (if available) to a temporary directory and parses it.  If the file is well-formed and in the proper format, data is copied to the security.xml file that the server uses.  Changes take effect immediately.";

    /**
     * The file this job will download.
     */
    public static final String FILENAME = "security.xml";

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * SFTP client.
     */
    private SFTP sftp = null;

    /**
     * Run flag.  Used to support InterruptableJob.
     */
    private boolean RUN_FLAG = true;

    /**
     * Constructor.
     */
    public SFTPDownloadSecurityJob()
    {
    }

    /**
     * Schedules the job.
     * @param s the scheduler.
     */
    public static void schedule(Scheduler s)
    {
        // Verify parameters.
        if(s == null)
        {
            return;
        }
        try
        {
            // Create a job.
            JobDetail d = new JobDetail();
            d.setName(SFTPDownloadSecurityJob.NAME);
            d.setGroup(Scheduler.DEFAULT_GROUP);
            d.setJobClass(SFTPDownloadSecurityJob.class);
            d.setDescription(SFTPDownloadSecurityJob.DESCRIPTION);
            // Create a trigger.
            GregorianCalendar g = new GregorianCalendar();
            g.add(Calendar.MINUTE, 2);
            Date n = g.getTime();
            Trigger t = TriggerUtils.makeHourlyTrigger();
            t.setName(SFTPDownloadSecurityJob.NAME);
            t.setGroup(Scheduler.DEFAULT_GROUP);
            t.setJobName(SFTPDownloadSecurityJob.NAME);
            t.setJobGroup(Scheduler.DEFAULT_GROUP);
            t.setDescription("Runs every hour, starting 2 minutes from when the scheduler is started.");
            t.setStartTime(n);
            // Schedule the job.
            s.scheduleJob(d, t);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
    }

    /**
     * Runs the job.
     * @param context the context in which the job is run.
     * @throws JobExecutionException if an error occurs.
     */
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        // Begin
        logger.debug("job begin");
        long lngBegin = System.currentTimeMillis();
        long lngEnd = 0L;
        long lngElapsed = 0L;
        // Filename
        StringBuilder sbFilename = new StringBuilder(STRING_BUFFER_SIZE);
        sbFilename.append(App.getDir(App.TEMP_DIR));
        sbFilename.append(File.separator);
        sbFilename.append(FILENAME);
        String strFilename = sbFilename.toString();
        // Delete local copy if it exists.
        File f = null;
        boolean booExists = false;
        try
        {
            f = new File(strFilename);
            booExists = f.exists();
            if(booExists)
            {
                f.delete();
            }
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
            lngEnd = System.currentTimeMillis();
            lngElapsed = lngEnd - lngBegin;
            logger.debug("job end");
            logger.debug("elapsed time = " + lngElapsed + "ms");
            return;
        }
        // Respond to interrupt signal when required.
        if(RUN_FLAG == false)
        {
            logger.info("cancelling...");
            lngEnd = System.currentTimeMillis();
            lngElapsed = lngEnd - lngBegin;
            logger.debug("job end");
            logger.debug("elapsed time = " + lngElapsed + "ms");
            return;
        }
        // Download the file.
        sftp = new SFTP();
        sftp.download(FILENAME, strFilename);
        sftp.delete(FILENAME);
        sftp = null;
        // Respond to interrupt signal when required.
        if(RUN_FLAG == false)
        {
            logger.info("cancelling...");
            lngEnd = System.currentTimeMillis();
            lngElapsed = lngEnd - lngBegin;
            logger.debug("job end");
            logger.debug("elapsed time = " + lngElapsed + "ms");
            return;
        }
        // See if we downloaded it.
        try
        {
            booExists = f.exists();
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
            lngEnd = System.currentTimeMillis();
            lngElapsed = lngEnd - lngBegin;
            logger.debug("job end");
            logger.debug("elapsed time = " + lngElapsed + "ms");
            return;
        }
        if(booExists == false)
        {
            logger.info("skipping...");
            lngEnd = System.currentTimeMillis();
            lngElapsed = lngEnd - lngBegin;
            logger.debug("job end");
            logger.debug("elapsed time = " + lngElapsed + "ms");
            return;
        }
        // Create temp variables.
        int x = 0;
        int y = 0;
        // Reset values.
        String strHost = App.getValue("app.default.host");
        String strPort = App.getValue("app.default.port");
        String strUsername = "";
        String strPassword = "";
        // Read file.
        Document doc = XML.read(strFilename);
        if(doc == null)
        {
            lngEnd = System.currentTimeMillis();
            lngElapsed = lngEnd - lngBegin;
            logger.debug("job end");
            logger.debug("elapsed time = " + lngElapsed + "ms");
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
        Security.read();
        Security.setHost(strHost);
        Security.setPort(strPort);
        Security.setUsername(strUsername);
        Security.setPassword(strPassword);
        Security.write();
        // End
        lngEnd = System.currentTimeMillis();
        lngElapsed = lngEnd - lngBegin;
        logger.debug("job end");
        logger.debug("elapsed time = " + lngElapsed + "ms");
    }

    /**
     * Stops the job.
     * @throws UnableToInterruptJobException if unable to interrupt.
     */
    public void interrupt() throws UnableToInterruptJobException
    {
        logger.debug("job cancel");
        RUN_FLAG = false;
        if(sftp != null)
        {
            sftp.interrupt();
        }
    }

}
