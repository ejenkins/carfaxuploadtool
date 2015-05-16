package com.infotraxx.carfax.server.cron;

import com.infotraxx.carfax.server.config.ODBC;
import com.infotraxx.carfax.server.config.ODBCResource;
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
 * Downloads odbc.xml.
 * @author Ed Jenkins
 */
public class SFTPDownloadODBCJob implements InterruptableJob
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SFTPDownloadODBCJob.class);

    /**
     * Name.
     */
    public static final String NAME = "sftp.download.odbc.job";

    /**
     * Description.
     */
    public static final String DESCRIPTION = "Downloads odbc.xml.";

    /**
     * The file this job will download.
     */
    public static final String FILENAME = "odbc.xml";

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
    public SFTPDownloadODBCJob()
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
            d.setName(SFTPDownloadODBCJob.NAME);
            d.setGroup(Scheduler.DEFAULT_GROUP);
            d.setJobClass(SFTPDownloadODBCJob.class);
            d.setDescription(SFTPDownloadODBCJob.DESCRIPTION);
            // Create a trigger.
            GregorianCalendar g = new GregorianCalendar();
            g.add(Calendar.MINUTE, 3);
            Date n = g.getTime();
            Trigger t = TriggerUtils.makeHourlyTrigger();
            t.setName(SFTPDownloadODBCJob.NAME);
            t.setGroup(Scheduler.DEFAULT_GROUP);
            t.setJobName(SFTPDownloadODBCJob.NAME);
            t.setJobGroup(Scheduler.DEFAULT_GROUP);
            t.setDescription("Runs every hour, starting 3 minutes from when the scheduler is started.");
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
        ArrayList<ODBCResource> alODBC = new ArrayList<ODBCResource>();
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
        // resource
        NodeList nlResource = eDoc.getElementsByTagName("resource");
        y = nlResource.getLength();
        for(x=0; x<y; x++)
        {
            Node nResource = nlResource.item(x);
            Element eResource = (Element)nResource;
            String strDSN = eResource.getAttribute("dsn");
            String strUsername = eResource.getAttribute("username");
            String strPassword = eResource.getAttribute("password");
            String strFile = eResource.getAttribute("filename");
            String strDelimiter = eResource.getAttribute("delimiter");
            String strTempSQL = eResource.getTextContent();
            String strSQL = App.cleanString(strTempSQL);
            if(strDSN == null)
            {
                strDSN = "";
            }
            if(strUsername == null)
            {
                strUsername = "";
            }
            if(strPassword == null)
            {
                strPassword = "";
            }
            if(strFile == null)
            {
                strFile = "";
            }
            if(strDelimiter == null)
            {
                strDelimiter = ",";
            }
            if(strSQL == null)
            {
                strSQL = "";
            }
            ODBCResource r = new ODBCResource();
            r.dsn = strDSN;
            r.username = strUsername;
            r.password = strPassword;
            r.filename = strFile;
            r.delimiter = strDelimiter;
            r.sql = strSQL;
            alODBC.add(r);
        }
        ODBC.read();
        ODBC.clear();
        ODBC.addAll(alODBC);
        ODBC.write();
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
