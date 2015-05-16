package com.infotraxx.carfax.server.cron;

import com.infotraxx.carfax.server.util.App;
import com.infotraxx.carfax.server.util.SFTP;
import com.infotraxx.carfax.server.util.SuperProperties;
import com.infotraxx.carfax.server.util.XML;
import com.jniwrapper.win32.registry.RegistryKey;
import com.jniwrapper.win32.registry.RegistryKeyValues;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.LineSeparator;
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

/**
 * Uploads system info to an SFTP server.
 * @author Ed Jenkins
 */
public class SFTPUploadSysInfoJob implements InterruptableJob
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(SFTPUploadSysInfoJob.class);

    /**
     * Name.
     */
    public static final String NAME = "sftp.upload.sysinfo.job";

    /**
     * Description.
     */
    public static final String DESCRIPTION = "Uploads system info to an SFTP server.";

    /**
     * The file this job will create and upload.
     */
    public static final String FILENAME = "sysinfo.xml";

    /**
     * Buffer size for strings.
     */
    private static final int STRING_BUFFER_SIZE = 1024;

    /**
     * SFTP client.
     */
    private SFTP sftp = null;

    /**
     * Run flag.
     */
    private boolean RUN_FLAG = true;

    /**
     * Constructor.
     */
    public SFTPUploadSysInfoJob()
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
            d.setName(SFTPUploadSysInfoJob.NAME);
            d.setGroup(Scheduler.DEFAULT_GROUP);
            d.setJobClass(SFTPUploadSysInfoJob.class);
            d.setDescription(SFTPUploadSysInfoJob.DESCRIPTION);
            // Create a trigger.
            GregorianCalendar g = new GregorianCalendar();
            g.add(Calendar.MINUTE, 5);
            Date n = g.getTime();
            Trigger t = TriggerUtils.makeHourlyTrigger();
            t.setName(SFTPUploadSysInfoJob.NAME);
            t.setGroup(Scheduler.DEFAULT_GROUP);
            t.setJobName(SFTPUploadSysInfoJob.NAME);
            t.setJobGroup(Scheduler.DEFAULT_GROUP);
            t.setDescription("Runs every hour, starting 5 minutes from when the scheduler is started.");
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
        // Create new document.
        StringBuilder sbXML = new StringBuilder(STRING_BUFFER_SIZE);
        sbXML.append(App.getDir(App.PROGRAM_DIR));
        sbXML.append(File.separator);
        sbXML.append(FILENAME);
        String strXML = sbXML.toString();
        String strDTD = "sysinfo.dtd";
        Document doc = XML.newDocument(null, "sysinfo", null, strDTD);
        // Add content.
        this.addGeneral(doc);
        this.addDrives(doc);
        this.addEnvironment(doc);
        this.addODBC(doc);
        this.addRegistry(doc);
        this.addSystem(doc);
        // Save the file.
        XML.write(strXML, null, strDTD, "xml", LineSeparator.Windows, doc);
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
        // Upload the file.
        sftp = new SFTP();
        sftp.upload(strXML, FILENAME);
        sftp = null;
        lngEnd = System.currentTimeMillis();
        lngElapsed = lngEnd - lngBegin;
        logger.debug("job end");
        logger.debug("elapsed time = " + lngElapsed + "ms");
    }

    /**
     * Adds the general section to the document.
     * @param doc the document to write to.
     */
    private void addGeneral(Document doc)
    {
        // Verify parameters.
        if(doc == null)
        {
            return;
        }
        // Create general element.
        Element eDoc = doc.getDocumentElement();
        Element eGeneral = doc.createElement("general");
        eDoc.appendChild(eGeneral);
        // Create temp variables.
        Runtime runtime = Runtime.getRuntime();
        NumberFormat nf = NumberFormat.getIntegerInstance();
        // Get date and time.
        String strDate = App.getDateString();
        String strTime = App.getTimeString();
        // Get memory info.
        long lngFree = runtime.freeMemory();
        long lngTotal = runtime.totalMemory();
        long lngMax = runtime.maxMemory();
        String strFree = nf.format(lngFree);
        String strTotal = nf.format(lngTotal);
        String strMax = nf.format(lngMax);
        // Get number of processors.
        int intProcessors = runtime.availableProcessors();
        String strProcessors = Integer.toString(intProcessors);
        // Get version
        String strVersion = App.getValue("app.version.info.ProductVersion");
        // Add data to file.
        Element eDate = doc.createElement("date");
        eDate.setTextContent(strDate);
        eGeneral.appendChild(eDate);
        Element eTime = doc.createElement("time");
        eTime.setTextContent(strTime);
        eGeneral.appendChild(eTime);
        Element eMemory = doc.createElement("memory");
        eMemory.setAttribute("free", strFree);
        eMemory.setAttribute("total", strTotal);
        eMemory.setAttribute("max", strMax);
        eGeneral.appendChild(eMemory);
        Element eProcessors = doc.createElement("processors");
        eProcessors.setTextContent(strProcessors);
        eGeneral.appendChild(eProcessors);
        Element eVersion = doc.createElement("version");
        eVersion.setTextContent(strVersion);
        eGeneral.appendChild(eVersion);
    }

    /**
     * Adds the drives section to the document.
     * @param doc the document to write to.
     */
    private void addDrives(Document doc)
    {
        // Verify parameters.
        if(doc == null)
        {
            return;
        }
        // Create drives element.
        Element eDoc = doc.getDocumentElement();
        Element eDrives = doc.createElement("drives");
        eDoc.appendChild(eDrives);
        // Create temp variables.
        NumberFormat nf = NumberFormat.getIntegerInstance();
        long lngUsable = 0L;
        long lngFree = 0L;
        long lngTotal = 0L;
        // Get list of drives, filter it, and add it to the file.
        File f = null;
        File[] roots = File.listRoots();
        int x = 0;
        int y = roots.length;
        for(x=0; x<y; x++)
        {
            f = roots[x];
            String strName = f.getPath();
            strName = strName.substring(0, 2);
            //  Filter out A: and B:.
            //  If you don't, then Windows will display an error message
            //  if the drive exists and there is no disk in the drive.
            if(strName.equalsIgnoreCase("A:"))
            {
                continue;
            }
            if(strName.equalsIgnoreCase("B:"))
            {
                continue;
            }
            //  Filter out CD-ROM drives and drives that are disconnected.
            if(f.canWrite() == false)
            {
                continue;
            }
            lngUsable = 0L;
            lngFree = 0L;
            lngTotal = 0L;
            lngUsable = f.getUsableSpace();
            lngFree = f.getFreeSpace();
            lngTotal = f.getTotalSpace();
            String strUsable = nf.format(lngUsable);
            String strFree = nf.format(lngFree);
            String strTotal = nf.format(lngTotal);
            Element eDrive = doc.createElement("drive");
            eDrive.setAttribute("name", strName);
            eDrive.setAttribute("usable", strUsable);
            eDrive.setAttribute("free", strFree);
            eDrive.setAttribute("total", strTotal);
            eDrives.appendChild(eDrive);
        }
    }

    /**
     * Adds the environment section to the document.
     * @param doc the document to write to.
     */
    private void addEnvironment(Document doc)
    {
        // Verify parameters.
        if(doc == null)
        {
            return;
        }
        // Create environment element.
        Element eDoc = doc.getDocumentElement();
        Element eEnvironment = doc.createElement("environment");
        eDoc.appendChild(eEnvironment);
        // Get environment variables.
        Map<String,String> mapEnvironment = System.getenv();
        TreeMap<String,String> tmEnvironment = new TreeMap<String,String>();
        tmEnvironment.putAll(mapEnvironment);
        // Add data to file.
        Set setEnvironment = tmEnvironment.keySet();
        Iterator iEnvironment = setEnvironment.iterator();
        while(iEnvironment.hasNext())
        {
            String strName = (String)iEnvironment.next();
            String strValue = (String)tmEnvironment.get(strName);
            Element e = doc.createElement("variable");
            e.setAttribute("name", strName);
            e.setTextContent(strValue);
            eEnvironment.appendChild(e);
        }
    }

    /**
     * Adds the odbc section to the document.
     * @param doc the document to write to.
     */
    private void addODBC(Document doc)
    {
        // Verify parameters.
        if(doc == null)
        {
            return;
        }
        // Create odbc element.
        Element eDoc = doc.getDocumentElement();
        Element eODBC = doc.createElement("odbc");
        eDoc.appendChild(eODBC);
        // Create temp variables.
        RegistryKey HKLM = RegistryKey.LOCAL_MACHINE;
        String kn = null;
        RegistryKey rk = null;
        RegistryKeyValues rkv = null;
        // Get a list of DSNs.
        try
        {
            kn = "SOFTWARE\\ODBC\\ODBC.INI\\ODBC Data Sources";
            logger.debug("hive=" + HKLM.toString());
            logger.debug("key=" + kn);
            rk = HKLM.openSubKey(kn, false);
            rkv = rk.values();
            Set set = rkv.keySet();
            Iterator i = set.iterator();
            while(i.hasNext())
            {
                String strName = (String)i.next();
                String strType = (String)rkv.get(strName);
                logger.debug("name=" + strName);
                logger.debug("type=" + strType);
                Element e = doc.createElement("dsn");
                e.setAttribute("name", strName);
                e.setAttribute("type", strType);
                eODBC.appendChild(e);
            }
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            if(rk != null)
            {
                rk.close();
            }
        }
    }

    /**
     * Adds the registry section to the document.
     * @param doc the document to write to.
     */
    private void addRegistry(Document doc)
    {
        // Verify parameters.
        if(doc == null)
        {
            return;
        }
        // Create registry element.
        Element eDoc = doc.getDocumentElement();
        Element eRegistry = doc.createElement("registry");
        eDoc.appendChild(eRegistry);
        // Create temp variables.
        TreeMap<String,String> tmRegistry = new TreeMap<String,String>();
        RegistryKey HKLM = RegistryKey.LOCAL_MACHINE;
        String kn = null;
        RegistryKey rk = null;
        RegistryKeyValues rkv = null;
        // BIOS
        try
        {
            kn = "HARDWARE\\DESCRIPTION\\System";
            logger.debug("hive=" + HKLM.toString());
            logger.debug("key=" + kn);
            rk = HKLM.openSubKey(kn, false);
            rkv = rk.values();
            String BIOS_SystemBiosDate = (String)rkv.get("SystemBiosDate");
            ArrayList alBIOS_SystemBiosVersion = (ArrayList)rkv.get("SystemBiosVersion");
            String BIOS_VideoBiosDate = (String)rkv.get("VideoBiosDate");
            Iterator iBIOS_SystemBiosVersion = alBIOS_SystemBiosVersion.iterator();
            StringBuilder sbBIOS_SystemBiosVersion = new StringBuilder();
            while(iBIOS_SystemBiosVersion.hasNext())
            {
                String sBIOS_SystemBiosVersion = (String)iBIOS_SystemBiosVersion.next();
                sbBIOS_SystemBiosVersion.append(sBIOS_SystemBiosVersion);
                sbBIOS_SystemBiosVersion.append(" ");
            }
            String strBIOS_SystemBiosVersion = sbBIOS_SystemBiosVersion.toString();
            String BIOS_SystemBiosVersion = strBIOS_SystemBiosVersion.trim();
            tmRegistry.put("BIOS_SystemBiosDate", BIOS_SystemBiosDate);
            tmRegistry.put("BIOS_SystemBiosVersion", BIOS_SystemBiosVersion);
            tmRegistry.put("BIOS_VideoBiosDate", BIOS_VideoBiosDate);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            if(rk != null)
            {
                rk.close();
            }
        }
        // CPU
        try
        {
            kn = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\0";
            logger.debug("hive=" + HKLM.toString());
            logger.debug("key=" + kn);
            rk = HKLM.openSubKey(kn, false);
            rkv = rk.values();
            String CPU_Identifier = (String)rkv.get("Identifier");
            Long lngCPU_MHz = (Long)rkv.get("~MHz");
            String CPU_ProcessorNameString = (String)rkv.get("ProcessorNameString");
            String CPU_VendorIdentifier = (String)rkv.get("VendorIdentifier");
            String CPU_MHz = lngCPU_MHz.toString();
            tmRegistry.put("CPU_Identifier", CPU_Identifier);
            tmRegistry.put("CPU_MHz", CPU_MHz);
            tmRegistry.put("CPU_ProcessorNameString", CPU_ProcessorNameString);
            tmRegistry.put("CPU_VendorIdentifier", CPU_VendorIdentifier);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            if(rk != null)
            {
                rk.close();
            }
        }
        // Version
        try
        {
            kn = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion";
            logger.debug("hive=" + HKLM.toString());
            logger.debug("key=" + kn);
            rk = HKLM.openSubKey(kn, false);
            rkv = rk.values();
            String Version_ProductName = (String)rkv.get("ProductName");
            String Version_CurrentVersion = (String)rkv.get("CurrentVersion");
            String Version_CSDVersion = (String)rkv.get("CSDVersion");
            tmRegistry.put("Version_ProductName", Version_ProductName);
            tmRegistry.put("Version_CurrentVersion", Version_CurrentVersion);
            tmRegistry.put("Version_CSDVersion", Version_CSDVersion);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            if(rk != null)
            {
                rk.close();
            }
        }
        // Add data to file.
        Set setRegistry = tmRegistry.keySet();
        Iterator iRegistry = setRegistry.iterator();
        while(iRegistry.hasNext())
        {
            String strName = (String)iRegistry.next();
            String strValue = (String)tmRegistry.get(strName);
            Element e = doc.createElement("entry");
            e.setAttribute("name", strName);
            e.setTextContent(strValue);
            eRegistry.appendChild(e);
        }
    }

    /**
     * Adds the system section to the document.
     * @param doc the document to write to.
     */
    private void addSystem(Document doc)
    {
        // Verify parameters.
        if(doc == null)
        {
            return;
        }
        // Create system element.
        Element eDoc = doc.getDocumentElement();
        Element eSystem = doc.createElement("system");
        eDoc.appendChild(eSystem);
        // Get system properties.
        Properties p = System.getProperties();
        SuperProperties spSystem = new SuperProperties();
        spSystem.putAll(p);
        TreeMap tmSystem = spSystem.getAll();
        // Add data to file.
        Set setSystem = tmSystem.keySet();
        Iterator iSystem = setSystem.iterator();
        while(iSystem.hasNext())
        {
            String strName = (String)iSystem.next();
            String strValue = (String)tmSystem.get(strName);
            Element e = doc.createElement("property");
            e.setAttribute("name", strName);
            e.setTextContent(strValue);
            eSystem.appendChild(e);
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
