package com.infotraxx.carfax.server.cron;

import com.infotraxx.carfax.server.config.ODBC;
import com.infotraxx.carfax.server.config.ODBCResource;
import com.infotraxx.carfax.server.config.Security;
import com.infotraxx.carfax.server.util.App;
import com.infotraxx.carfax.server.util.SFTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
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

/**
 * Extracts data from an ODBC database and uploads it to an SFTP server.
 * @author Ed Jenkins
 */
public class SFTPUploadODBCJob implements InterruptableJob
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SFTPUploadODBCJob.class);

    /**
     * Name.
     */
    public static final String NAME = "sftp.upload.odbc.job";

    /**
     * Description.
     */
    public static final String DESCRIPTION = "Extracts data from ODBC databases and uploads it to an SFTP server.";

    /**
     * Buffer size.
     */
    private static final int BUFFER_SIZE = 64 * 1024;

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * Driver.
     */
    private static final String strDriver = "sun.jdbc.odbc.JdbcOdbcDriver";

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
    public SFTPUploadODBCJob()
    {
        // Load driver.
        try
        {
            Class.forName(strDriver);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
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
            d.setName(SFTPUploadODBCJob.NAME);
            d.setGroup(Scheduler.DEFAULT_GROUP);
            d.setJobClass(SFTPUploadODBCJob.class);
            d.setDescription(SFTPUploadODBCJob.DESCRIPTION);
            // Create a trigger.
            GregorianCalendar g = new GregorianCalendar();
            g.add(Calendar.MINUTE, 6);
            Date n = g.getTime();
            Trigger t = TriggerUtils.makeHourlyTrigger();
            t.setName(SFTPUploadODBCJob.NAME);
            t.setGroup(Scheduler.DEFAULT_GROUP);
            t.setJobName(SFTPUploadODBCJob.NAME);
            t.setJobGroup(Scheduler.DEFAULT_GROUP);
            t.setDescription("Runs every hour, starting 6 minutes from when the scheduler is started.");
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
        Security.read();
        Security.write();
        ODBC.read();
        ODBC.write();
        ArrayList al = ODBC.getResources();
        Iterator i = al.iterator();
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
            ODBCResource r = (ODBCResource)i.next();
            if(r.dsn.equalsIgnoreCase("example"))
            {
                continue;
            }
            execute(r);
        }
        lngEnd = System.currentTimeMillis();
        lngElapsed = lngEnd - lngBegin;
        logger.debug("job end");
        logger.debug("elapsed time = " + lngElapsed + "ms");
    }

    /**
     * Executes the SQL command for the specified ODBC resource and uploads the results.
     * @param r the ODBC resource to work with.
     */
    private void execute(ODBCResource r)
    {
        // Verify parameters.
        if(r == null)
        {
            logger.error("r is null");
            return;
        }
        if(r.dsn.equalsIgnoreCase(""))
        {
            logger.error("r.dsn is empty");
            return;
        }
        if(r.filename.equalsIgnoreCase(""))
        {
            logger.error("r.filename is empty");
            return;
        }
        if(r.delimiter.equalsIgnoreCase(""))
        {
            r.delimiter = ",";
        }
        if(r.sql.equalsIgnoreCase(""))
        {
            logger.error("r.sql is empty");
            return;
        }
        String dsn = r.dsn;
        String username = r.username;
        String password = r.password;
        String filename = new String(r.filename);
        String delimiter = r.delimiter;
        String sql = new String(r.sql);
        logger.debug("dsn=" + dsn);
        // Expand variables in filename.
        logger.debug("raw filename=" + filename);
        TreeMap tmVariablesForFilename = Security.getVariables();
        Set setVariablesForFilename = tmVariablesForFilename.keySet();
        Iterator iVariablesForFilename = setVariablesForFilename.iterator();
        while(iVariablesForFilename.hasNext())
        {
            String strName = (String)iVariablesForFilename.next();
            String strValue = (String)tmVariablesForFilename.get(strName);
            filename = filename.replace(strName, strValue);
        }
        logger.debug("refined filename=" + filename);
        // Build URL.
        StringBuilder sbURL = new StringBuilder(STRING_BUFFER_SIZE);
        sbURL.append("jdbc");
        sbURL.append(":");
        sbURL.append("odbc");
        sbURL.append(":");
        sbURL.append(dsn);
        if(!username.equals(""))
        {
            sbURL.append(";UID=");
            sbURL.append(username);
        }
        if(!password.equals(""))
        {
            sbURL.append(";PWD=");
            sbURL.append(password);
        }
        String strURL = sbURL.toString();
        // Expand variables in SQL.
        logger.debug("url=" + strURL);
        logger.debug("raw sql=" + sql);
        TreeMap tmVariablesForURL = Security.getVariables();
        Set setVariablesForURL = tmVariablesForURL.keySet();
        Iterator iVariablesForURL = setVariablesForURL.iterator();
        while(iVariablesForURL.hasNext())
        {
            String strName = (String)iVariablesForURL.next();
            String strValue = (String)tmVariablesForURL.get(strName);
            sql = sql.replace(strName, strValue);
        }
        logger.debug("refined sql=" + sql);
        // Create temp variables.
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        File f = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        int x = 0;
        int y = 0;
        int z = 0;
        int intType = 0;
        boolean booError = false;
        try
        {
            // Login.
            con = DriverManager.getConnection(strURL);
            logger.debug("Connected");
            // Execute the SQL statement.
            ps = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            rsmd = rs.getMetaData();
            y = rsmd.getColumnCount();
            // Open output file.
            f = new File(filename);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw, BUFFER_SIZE);
            pw = new PrintWriter(bw);
            // Export the data.
            while(rs.next())
            {
                z++;
                // Print header.
                if(z == 1)
                {
                    for(x=1; x<=y; x++)
                    {
                        String strLabel = rsmd.getColumnLabel(x);
                        pw.print(strLabel);
                        if(x < y)
                        {
                            pw.print(delimiter);
                        }
                    }
                    pw.println();
                }
                for(x=1; x<=y; x++)
                {
                    String strTempValue = rs.getString(x);
                    if(strTempValue == null)
                    {
                        if(x == y)
                        {
                            pw.println();
                        }
                        else
                        {
                            pw.print(delimiter);
                        }
                        continue;
                    }
                    String strValue = App.cleanString(strTempValue);
                    intType = rsmd.getColumnType(x);
                    switch(intType)
                    {
                        case Types.BIT:
                        {
                            pw.print(strValue);
                            break;
                        }
                        case Types.INTEGER:
                        {
                            pw.print(strValue);
                            break;
                        }
                        case Types.BIGINT:
                        {
                            pw.print(strValue);
                            break;
                        }
                        case Types.FLOAT:
                        {
                            pw.print(strValue);
                            break;
                        }
                        case Types.REAL:
                        {
                            pw.print(strValue);
                            break;
                        }
                        default:
                        {
                            pw.print("\"");
                            pw.print(strValue);
                            pw.print("\"");
                            break;
                        }
                    }
                    if(x == y)
                    {
                        pw.println();
                    }
                    else
                    {
                        pw.print(delimiter);
                    }
                }
            }
        }        
        catch(Exception ex)
        {
            booError = true;
            String strMessage = ex.getMessage();
            if(strMessage.contains("Data source name not found"))
            {
                logger.error("The DSN does not exist.");
            }
            else
            {
                logger.error(ex, ex);
            }
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
            if(ps != null)
            {
                try
                {
                    ps.close();
                }
                catch(Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch(Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
            if(pw != null)
            {
                try
                {
                    pw.close();
                }
                catch(Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
        }
        // Upload the file.
        if(booError)
        {
            logger.debug("Not going to upload " + filename);
        }
        else
        {
            if(z == 1)
            {
                logger.debug("Wrote " + z + " record to " + filename);
            }
            else
            {
                logger.debug("Wrote " + z + " records to " + filename);
            }
            sftp = new SFTP();
            sftp.upload(filename, filename);
            sftp = null;
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
