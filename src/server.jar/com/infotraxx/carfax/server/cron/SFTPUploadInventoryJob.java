package com.infotraxx.carfax.server.cron;

import com.infotraxx.carfax.server.util.App;
import com.infotraxx.carfax.server.util.SFTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

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
 * Scans local drives and uploads a list of files to an SFTP server.
 * @author Ed Jenkins
 */
public class SFTPUploadInventoryJob implements InterruptableJob
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SFTPUploadInventoryJob.class);

    /**
     * Name.
     */
    public static final String NAME = "sftp.upload.inventory.job";

    /**
     * Description.
     */
    public static final String DESCRIPTION = "Scans local drives and uploads a list of files to an SFTP server.";

    /**
     * The file this job will create and upload.
     */
    public static final String FILENAME = "inventory.txt";

    /**
     * The file to look for on the server.
     */
    public static final String SCAN_TXT = "scan.txt";

    /**
     * Buffer size.
     */
    public static final int BUFFER_SIZE = 1024 * 1024;

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
    public SFTPUploadInventoryJob()
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
            d.setName(SFTPUploadInventoryJob.NAME);
            d.setGroup(Scheduler.DEFAULT_GROUP);
            d.setJobClass(SFTPUploadInventoryJob.class);
            d.setDescription(SFTPUploadInventoryJob.DESCRIPTION);
            // Create a trigger.
            GregorianCalendar g = new GregorianCalendar();
            g.add(Calendar.MINUTE, 8);
            Date n = g.getTime();
            Trigger t = TriggerUtils.makeHourlyTrigger();
            t.setName(SFTPUploadInventoryJob.NAME);
            t.setGroup(Scheduler.DEFAULT_GROUP);
            t.setJobName(SFTPUploadInventoryJob.NAME);
            t.setJobGroup(Scheduler.DEFAULT_GROUP);
            t.setDescription("Runs every hour, starting 8 minutes from when the scheduler is started.");
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
     * Checks to see if a scan has been requested.
     * @return true if a scan has been requested or false if not.
     */
    public boolean check()
    {
        // Filename.
        StringBuilder sbFilename = new StringBuilder(STRING_BUFFER_SIZE);
        sbFilename.append(App.getDir(App.PROGRAM_DIR));
        sbFilename.append(File.separator);
        sbFilename.append(SCAN_TXT);
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
            return false;
        }
        // Download the file.
        SFTP sftp = new SFTP();
        sftp.download(SCAN_TXT, strFilename);
        sftp.delete(SCAN_TXT);
        sftp = null;
        // See if we downloaded it.
        try
        {
            booExists = f.exists();
            if(booExists)
            {
                f.delete();
            }
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
            return false;
        }
        if(booExists == false)
        {
            logger.debug("skipping...");
            return false;
        }
        return true;
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
        boolean booCheck = this.check();
        if(booCheck == false)
        {
            return;
        }
        // Create new file.
        StringBuilder sbFilename = new StringBuilder(STRING_BUFFER_SIZE);
        sbFilename.append(App.getDir(App.PROGRAM_DIR));
        sbFilename.append(File.separator);
        sbFilename.append(FILENAME);
        String strFilename = sbFilename.toString();
        File f = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try
        {
            f = new File(strFilename);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw, BUFFER_SIZE);
            pw = new PrintWriter(bw);
            // Get list of drives.
            File root = null;
            File[] roots = File.listRoots();
            int x = 0;
            int y = roots.length;
            // Loop through the drives.
            for(x=0; x<y; x++)
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
                root = roots[x];
                String strRoot = root.getPath();
                strRoot = strRoot.substring(0, 2);
                StringBuilder sbRootDir = new StringBuilder(STRING_BUFFER_SIZE);
                sbRootDir.append(strRoot);
                sbRootDir.append(File.separator);
                String strRootDir = sbRootDir.toString();
                //  Filter out A: and B:.
                //  If you don't, then Windows will display an error message
                //  if the drive exists and there is no disk in the drive.
                if(strRoot.equalsIgnoreCase("A:"))
                {
                    continue;
                }
                if(strRoot.equalsIgnoreCase("B:"))
                {
                    continue;
                }
                //  Filter out CD-ROM drives and drives that are disconnected.
                File rootDir = new File(strRootDir);
                if(rootDir.canWrite() == false)
                {
                    continue;
                }
                this.addFiles(strRootDir, pw);
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
        finally
        {
            if(pw != null)
            {
                try
                {
                    pw.flush();
                    pw.close();
                }
                catch(Exception ex)
                {
                }
            }
        }
        // Upload the file.
        sftp = new SFTP();
        sftp.upload(strFilename, FILENAME);
        sftp = null;
        lngEnd = System.currentTimeMillis();
        lngElapsed = lngEnd - lngBegin;
        logger.debug("job end");
        logger.debug("elapsed time = " + lngElapsed + "ms");
    }

    /**
     * Adds filenames to the document.
     * @param dir the directory to scan.
     * @param pw the print writer to write to.
     */
    private void addFiles(String dir, PrintWriter pw)
    {
        // Verify parameters.
        if(dir == null)
        {
            return;
        }
        if(pw == null)
        {
            return;
        }
        // Respond to interrupt signal when required.
        if(RUN_FLAG == false)
        {
            return;
        }
        // Get a list of files in this directory, sort it, and recursively process each entry.
        File filDir = new File(dir);
        if(!filDir.isDirectory())
        {
            logger.error("This is not a directory:  " + dir);
            return;
        }
        String[] strFiles = filDir.list();
        if(strFiles == null)
        {
            logger.error("Error reading directory:  " + dir);
            return;
        }
        List<String> l = (List<String>)Arrays.asList(strFiles);
        TreeSet<String> ts = new TreeSet<String>();
        ts.addAll(l);
        Iterator i = ts.iterator();
        while(i.hasNext())
        {
            // Respond to interrupt signal when required.
            if(RUN_FLAG == false)
            {
                logger.info("cancelling...");
                return;
            }
            String strFilename = (String)i.next();
            if(strFilename.equals("."))
            {
                continue;
            }
            if(strFilename.equals(".."))
            {
                continue;
            }
            StringBuilder sbFullName = new StringBuilder(STRING_BUFFER_SIZE);
            sbFullName.append(dir);
            if(!dir.endsWith(File.separator))
            {
                sbFullName.append(File.separator);
            }
            sbFullName.append(strFilename);
            String strFullName = sbFullName.toString();
            logger.debug("strFullName=" + strFullName);
            File f = new File(strFullName);
            if(f.isDirectory())
            {
                this.addFiles(strFullName, pw);
            }
            else
            {
                // Add a file.
                pw.println(strFullName);
            }
        }
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
