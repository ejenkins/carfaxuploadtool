package com.infotraxx.carfax.server.cron;

import com.infotraxx.carfax.server.config.Files;
import com.infotraxx.carfax.server.util.SFTP;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.UnableToInterruptJobException;

/**
 * Uploads files to an SFTP server.
 * @author Ed Jenkins
 */
public class SFTPUploadFilesJob implements InterruptableJob
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SFTPUploadFilesJob.class);

    /**
     * Name.
     */
    public static final String NAME = "sftp.upload.job";

    /**
     * Description.
     */
    public static final String DESCRIPTION = "Uploads files to an SFTP server.";

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
    public SFTPUploadFilesJob()
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
            d.setName(SFTPUploadFilesJob.NAME);
            d.setGroup(Scheduler.DEFAULT_GROUP);
            d.setJobClass(SFTPUploadFilesJob.class);
            d.setDescription(SFTPUploadFilesJob.DESCRIPTION);
            // Create a trigger.
            GregorianCalendar g = new GregorianCalendar();
            g.add(Calendar.MINUTE, 7);
            Date n = g.getTime();
            Trigger t = TriggerUtils.makeHourlyTrigger();
            t.setName(SFTPUploadFilesJob.NAME);
            t.setGroup(Scheduler.DEFAULT_GROUP);
            t.setJobName(SFTPUploadFilesJob.NAME);
            t.setJobGroup(Scheduler.DEFAULT_GROUP);
            t.setDescription("Runs every hour, starting 7 minutes from when the scheduler is started.");
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
        logger.debug("job begin");
        long lngBegin = System.currentTimeMillis();
        long lngEnd = 0L;
        long lngElapsed = 0L;
        Files.read();
        Files.write();
        Vector v = Files.getFiles();
        Iterator i = v.iterator();
        while(i.hasNext())
        {
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
            String strSource = (String)i.next();
            String strDest = null;
            File f = null;
            try
            {
                f = new File(strSource);
                strDest = f.getName();
            }
            catch(Exception ex)
            {
                logger.error(ex, ex);
                continue;
            }
            if(strDest == null)
            {
                continue;
            }
            // See if we should upload it or not.
            long lngLastModified = Files.lastModified(strSource);
            long lngModified = Files.getModified(strSource);
            if(lngLastModified <= lngModified)
            {
                logger.debug("Skipping.");
                continue;
            }
            // Upload the file.
            sftp = new SFTP();
            sftp.upload(strSource, strDest);
            sftp = null;
            // Mark the timestamp.
            Files.addFile(strSource, lngLastModified);
            Files.write();
        }
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
