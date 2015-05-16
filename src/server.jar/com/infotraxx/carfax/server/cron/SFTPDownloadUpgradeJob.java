package com.infotraxx.carfax.server.cron;

import com.infotraxx.carfax.server.util.App;
import com.infotraxx.carfax.server.util.Runner;
import com.infotraxx.carfax.server.util.SFTP;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
 * Downloads upgrade.exe (if available) and executes it.
 * @author Ed Jenkins
 */
public class SFTPDownloadUpgradeJob implements InterruptableJob
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SFTPDownloadUpgradeJob.class);

    /**
     * Name.
     */
    public static final String NAME = "sftp.download.upgrade.job";

    /**
     * Description.
     */
    public static final String DESCRIPTION = "Downloads upgrade.exe (if available) and executes it so the application can upgrade itself when new features are available.";

    /**
     * The file this job will download.
     */
    public static final String FILENAME = "upgrade.exe";

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
    public SFTPDownloadUpgradeJob()
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
            d.setName(SFTPDownloadUpgradeJob.NAME);
            d.setGroup(Scheduler.DEFAULT_GROUP);
            d.setJobClass(SFTPDownloadUpgradeJob.class);
            d.setDescription(SFTPDownloadUpgradeJob.DESCRIPTION);
            // Create a trigger.
            GregorianCalendar g = new GregorianCalendar();
            g.add(Calendar.MINUTE, 1);
            Date n = g.getTime();
            Trigger t = TriggerUtils.makeHourlyTrigger();
            t.setName(SFTPDownloadUpgradeJob.NAME);
            t.setGroup(Scheduler.DEFAULT_GROUP);
            t.setJobName(SFTPDownloadUpgradeJob.NAME);
            t.setJobGroup(Scheduler.DEFAULT_GROUP);
            t.setDescription("Runs every hour, starting 1 minute from when the scheduler is started.");
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
        // Filename.
        StringBuilder sbFilename = new StringBuilder(STRING_BUFFER_SIZE);
        sbFilename.append(App.getDir(App.PROGRAM_DIR));
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
            logger.debug("skipping...");
            return;
        }
        // Run it.
        String strCWD = App.getDir(App.PROGRAM_DIR);
        Runner runner = new Runner();
        runner.setCWD(strCWD);
        runner.setProgram(strFilename);
        runner.setMode(Runner.ASYNCHRONOUS);
        runner.setRequired(false);
        try
        {
            runner.run();
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
