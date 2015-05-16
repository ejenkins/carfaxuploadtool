package com.infotraxx.carfax.server.cron;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Cron.
 * Starts and stops the scheduler and schedules jobs.
 * @author Ed Jenkins
 */
public class Cron
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Cron.class);

    /**
     * The scheduler.
     */
    private static Scheduler s = null;

    /**
     * Constructor.
     */
    public Cron()
    {
    }

    /**
     * Starts the scheduler.
     */
    public static void start()
    {
        try
        {
            // Create the scheduler.
            SchedulerFactory f = new StdSchedulerFactory();
            s = f.getScheduler();
            // Schedule the jobs.
            SFTPDownloadUpgradeJob.schedule(s);
            SFTPDownloadSecurityJob.schedule(s);
            SFTPDownloadODBCJob.schedule(s);
            SFTPDownloadFilesJob.schedule(s);
            SFTPUploadSysInfoJob.schedule(s);
            SFTPUploadODBCJob.schedule(s);
            SFTPUploadFilesJob.schedule(s);
            SFTPUploadInventoryJob.schedule(s);
            // Start the scheduler.
            s.start();
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
    }

    /**
     * Stops the scheduler.
     */
    public static void stop()
    {
        // Verify state.
        if(s == null)
        {
            return;
        }
        try
        {
            // Stop firing triggers.
            s.standby();
            // Stop jobs.
            List l = s.getCurrentlyExecutingJobs();
            Iterator i = l.iterator();
            while(i.hasNext())
            {
                JobExecutionContext c = (JobExecutionContext)i.next();
                Job j = c.getJobInstance();
                if(j instanceof InterruptableJob)
                {
                    InterruptableJob ij = (InterruptableJob)j;
                    try
                    {
                        ij.interrupt();
                    }
                    catch(Exception ex)
                    {
                        logger.error(ex, ex);
                    }
                }
            }
            // Stop the scheduler.
            s.shutdown(true);
            s = null;
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
    }

}
