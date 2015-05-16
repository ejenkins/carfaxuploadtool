package com.infotraxx.carfax.setup.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * Copies files and directories and unzips files.
 * @author Ed Jenkins
 */
public class Files
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Files.class);

    /**
     * Buffer size.
     */
    public static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * Constructor.
     */
    public Files()
    {
    }
    
    /**
     * Determines whether two filenames refer to the same file or not.
     * @param pSource the source filename.
     * @param pDest the destination filename.
     * @return true if the two filenames refer to the same file or false if not.
     * Also returns false if either parameter is null.
     */
    public static boolean isSame(String pSource, String pDest)
    {
        // Verify parameters.
        if(pSource == null)
        {
            return false;
        }
        if(pDest == null)
        {
            return false;
        }
        // Create return variable.
        boolean r = false;
        // Compare the filenames.
        File f1 = new File(pSource);
        File f2 = new File(pDest);
        r = f1.equals(f2);
        // Return result.
        return r;
    }

    /**
     * Copies files.
     * @param pSource the source filename.
     * @param pDest the destination filename.
     * @param pOverwrite true to overwrite or false to skip it if it exists.
     * @param pReporter a progress reporter.
     */
    public static void copyFile(String pSource, String pDest, boolean pOverwrite, ProgressReporter pReporter)
    {
        // Verify parameters.
        if(pSource == null)
        {
            return;
        }
        if(pDest == null)
        {
            return;
        }
//        logger.debug("source=" + pSource);
//        logger.debug("dest=" + pDest);
        if(Files.isSame(pSource, pDest))
        {
            logger.warn("Skipping " + pSource);
            return;
        }
        // Create temp variables.
        File filSource = null;
        FileInputStream fis = null;
        FileChannel fcSource = null;
        File filDest = null;
        FileOutputStream fos = null;
        FileChannel fcDest = null;
        try
        {
            // Open the destination file.
            filDest = new File(pDest);
            if(filDest.exists() && (pOverwrite == false))
            {
                // Report progress.
                if(pReporter != null)
                {
                    pReporter.report();
                }
                logger.debug("Skipping because destination file exists and overwrite flag is false.");
                return;
            }
            // Open the source file.
            filSource = new File(pSource);
            if(!filSource.exists())
            {
                logger.debug("Skipping because source file does not exist.");
                return;
            }
            fis = new FileInputStream(filSource);
            fcSource = fis.getChannel();
            // Find out how big it is.
            long lngLen = filSource.length();
//            logger.debug("size=" + lngLen);
            // Create destination directory.
            File filParent = filDest.getParentFile();
            if(filParent != null)
            {
                filParent.mkdirs();
            }
            fos = new FileOutputStream(filDest);
            fcDest = fos.getChannel();
            // Copy data.
            fcSource.transferTo(0, lngLen, fcDest);
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
            return;
        }
        finally
        {
            // Close the source file.
            if(fcSource != null)
            {
                try
                {
                    fcSource.close();
                }
                catch (Exception ex)
                {
                    logger.error(ex, ex);
                    return;
                }
            }
            // Close the destination file.
            if(fcDest != null)
            {
                try
                {
                    fcDest.close();
                }
                catch (Exception ex)
                {
                    logger.error(ex, ex);
                    return;
                }
            }
        }
        // Set date stamp.
        long lngDate = 0L;
        if(filSource != null)
        {
            if(filSource.exists() == true)
            {
                lngDate = filSource.lastModified();
            }
        }
        if(filDest != null)
        {
            if(filDest.exists() == true)
            {
                if(lngDate != 0L)
                {
                    filDest.setLastModified(lngDate);
                }
            }
        }
        // Report progress.
        if(pReporter != null)
        {
            pReporter.report();
        }
        // Log it.
/*
        StringBuilder sbLog = new StringBuilder(1024);
        sbLog.append("Copied from ");
        sbLog.append("\"");
        sbLog.append(pSource);
        sbLog.append("\"");
        sbLog.append(" to ");
        sbLog.append("\"");
        sbLog.append(pDest);
        sbLog.append("\"");
        sbLog.append(".");
        String strLog = sbLog.toString();
        logger.debug(strLog);
*/
    }

    /**
     * Gets a count of all files and folders under the specified source directory.
     * @param pSourceDir the source directory to scan.
     * @return the number of files and folders contained in the source directory.
     */
    public static int getDirFileCount(String pSourceDir)
    {
        // Create return variable.
        int r = 0;
        // Verify parameters.
        if(pSourceDir == null)
        {
            return 0;
        }
        // Create temp variables.
        File filSourceDir = null;
        try
        {
            // Make sure the source exists and is a directory.
            filSourceDir = new File(pSourceDir);
            if(!filSourceDir.exists())
            {
                return 0;
            }
            if(!filSourceDir.isDirectory())
            {
                return 0;
            }
            // List all files in the directory.
            String[] strFiles = filSourceDir.list();
            int x = 0;
            int y = strFiles.length;
            // Loop through each file.
            for (x = 0; x < y; x++)
            {
                // Get source filename.
                StringBuilder sbSource = new StringBuilder(1024);
                sbSource.append(pSourceDir);
                sbSource.append(File.separator);
                sbSource.append(strFiles[x]);
                String strSource = sbSource.toString();
                // See if it is a file or a directory.
                File filSource = new File(strSource);
                if(filSource.isDirectory())
                {
                    r += getDirFileCount(strSource);
                }
                r++;
            }
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return r;
    }

    /**
     * Copies a directory.
     * @param pSourceDir the source directory.
     * @param pDestDir the destination directory.
     * @param pOverwrite true to overwrite or false to skip it if it exists.
     * @param pReporter a progress reporter.
     */
    public static void copyDirectory(String pSourceDir, String pDestDir, boolean pOverwrite, ProgressReporter pReporter)
    {
        // Verify parameters.
        if(pSourceDir == null)
        {
            return;
        }
        if(pDestDir == null)
        {
            return;
        }
        // Log it.
/*
        StringBuilder sbLog = new StringBuilder(1024);
        sbLog.append("Copying from ");
        sbLog.append("\"");
        sbLog.append(pSourceDir);
        sbLog.append("\"");
        sbLog.append(" to ");
        sbLog.append("\"");
        sbLog.append(pDestDir);
        sbLog.append("\"");
        sbLog.append(".");
        String strLog = sbLog.toString();
        logger.info(strLog);
*/
        if(Files.isSame(pSourceDir, pDestDir))
        {
            logger.warn("Skipping " + pSourceDir);
            return;
        }
        // Create temp variables.
        File filSourceDir = null;
        File filDestDir = null;
        try
        {
            // Make sure the source exists and is a directory.
            filSourceDir = new File(pSourceDir);
            if(!filSourceDir.exists())
            {
                return;
            }
            if(!filSourceDir.isDirectory())
            {
                return;
            }
            // Create destination directory.
            filDestDir = new File(pDestDir);
            filDestDir.mkdirs();
            // Make sure the destination exists and is a directory.
            if(!filDestDir.exists())
            {
                return;
            }
            if(!filDestDir.isDirectory())
            {
                return;
            }
            // List all files in the directory.
            String[] strFiles = filSourceDir.list();
            int x = 0;
            int y = strFiles.length;
            // Loop through each file.
            for (x = 0; x < y; x++)
            {
                // Get source filename.
                StringBuilder sbSource = new StringBuilder(1024);
                sbSource.append(pSourceDir);
                sbSource.append(File.separator);
                sbSource.append(strFiles[x]);
                String strSource = sbSource.toString();
                // Get destination filename.
                StringBuilder sbDest = new StringBuilder(1024);
                sbDest.append(pDestDir);
                sbDest.append(File.separator);
                sbDest.append(strFiles[x]);
                String strDest = sbDest.toString();
                // See if it is a file or a directory.
                File filSource = new File(strSource);
                if(filSource.isDirectory())
                {
                    // Copy the directory.
                    copyDirectory(strSource, strDest, pOverwrite, pReporter);
                    // Report progress.
                    if(pReporter != null)
                    {
                        pReporter.report();
                    }
                }
                else
                {
                    // Copy the file.
                    Files.copyFile(strSource, strDest, pOverwrite, pReporter);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
            return;
        }
        finally
        {
            // Set date stamp.
            long lngDate = 0L;
            if(filSourceDir != null)
            {
                if(filSourceDir.exists() == true)
                {
                    lngDate = filSourceDir.lastModified();
                }
            }
            if(filDestDir != null)
            {
                if(filDestDir.exists() == true)
                {
                    if(lngDate != 0L)
                    {
                        filDestDir.setLastModified(lngDate);
                    }
                }
            }
        }
    }

    /**
     * Unzips a single file from a zip file.
     * @param pSourceZip the source zip file.
     * @param pSourceFile the file inside the source zip file.
     * @param pDestFile the destination file.
     * @param pOverwrite true to overwrite or false to skip it if it exists.
     * @param pReporter a progress reporter.
     */
    public static void unzipFile(String pSourceZip, String pSourceFile, String pDestFile, boolean pOverwrite, ProgressReporter pReporter)
    {
        // Verify parameters.
        if(pSourceZip == null)
        {
            return;
        }
        if(pSourceFile == null)
        {
            return;
        }
        if(pDestFile == null)
        {
            return;
        }
        // Create temp variables.
        ZipFile zf = null;
        long lngDate = 0L;
        InputStream is = null;
        BufferedInputStream bis = null;
        File filDest = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try
        {
            // Open the destination file.
            App.createDirForFile(pDestFile);
            filDest = new File(pDestFile);
            if(filDest.exists() && (pOverwrite == false))
            {
                // Report progress.
                if(pReporter != null)
                {
                    pReporter.report();
                }
                return;
            }
            // Open the zip file.
            zf = new ZipFile(pSourceZip);
            // Open the source file.
            ZipEntry ze = zf.getEntry(pSourceFile);
            if(ze != null)
            {
                // Get date stamp.
                lngDate = ze.getTime();
                // Open the source file.
                is = zf.getInputStream(ze);
                bis = new BufferedInputStream(is, BUFFER_SIZE);
            }
            fos = new FileOutputStream(filDest);
            bos = new BufferedOutputStream(fos, BUFFER_SIZE);
            byte[] b = new byte[BUFFER_SIZE];
            int x = 0;
            // Copy data.
            while (true)
            {
                x = bis.read(b, 0, BUFFER_SIZE);
                if(x == -1)
                {
                    break;
                }
                bos.write(b, 0, x);
            }
            // Flush the output.
            bos.flush();
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
            return;
        }
        finally
        {
            // Close the zip file.
            if(zf != null)
            {
                try
                {
                    zf.close();
                }
                catch (Exception ex)
                {
                    logger.error(ex, ex);
                    return;
                }
            }
            // Close the destination file.
            if(bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (Exception ex)
                {
                    logger.error(ex, ex);
                    return;
                }
            }
        }
        // Set date stamp.
        if(filDest != null)
        {
            if(filDest.exists() == true)
            {
                if(lngDate > 0L)
                {
                    filDest.setLastModified(lngDate);
                }
            }
        }
        // Log it.
/*
        StringBuilder sbLog = new StringBuilder(1024);
        sbLog.append("Unzipped from ");
        sbLog.append("\"");
        sbLog.append(pSourceZip);
        sbLog.append("!");
        sbLog.append(pSourceFile);
        sbLog.append("\"");
        sbLog.append(" to ");
        sbLog.append("\"");
        sbLog.append(pDestFile);
        sbLog.append("\"");
        sbLog.append(".");
        String strLog = sbLog.toString();
        logger.info(strLog);
*/
        // Report progress.
        if(pReporter != null)
        {
            pReporter.report();
        }
    }

    /**
     * Gets a count of all files and folders in a ZIP file.
     * @param pZipFile the file to scan.
     * @return the number of files and folders contained in the ZIP file.
     */
    public static int getZipFileCount(String pZipFile)
    {
        // Create return variable.
        int r = 0;
        // Verify parameters.
        if(pZipFile == null)
        {
            return 0;
        }
        // Create temp variables.
        ZipFile zf = null;
        // Count the number of entries.
        try
        {
            zf = new ZipFile(pZipFile);
            r = zf.size();
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            // Close the zip file.
            if(zf != null)
            {
                try
                {
                    zf.close();
                }
                catch (Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
        }
        // Return result.
        return r;
    }

    /**
     * Unzips all files from a zip file.
     * @param pSourceZip the source zip file.
     * @param pDestDir the destination directory.
     * @param pOverwrite true to overwrite or false to skip it if it exists.
     * @param pReporter a progress reporter.
     */
    public static void unzipFiles(String pSourceZip, String pDestDir, boolean pOverwrite, ProgressReporter pReporter)
    {
        // Verify parameters.
        if(pSourceZip == null)
        {
            return;
        }
        if(pDestDir == null)
        {
            return;
        }
        // Log it.
/*
        StringBuilder sbLog = new StringBuilder(1024);
        sbLog.append("Unzipping from ");
        sbLog.append("\"");
        sbLog.append(pSourceZip);
        sbLog.append("\"");
        sbLog.append(" to ");
        sbLog.append("\"");
        sbLog.append(pDestDir);
        sbLog.append("\"");
        sbLog.append(".");
        String strLog = sbLog.toString();
        logger.info(strLog);
*/
        // Create temp variables.
        ZipFile zf = null;
        ZipEntry ze = null;
        long lngDate = 0L;
        InputStream is = null;
        BufferedInputStream bis = null;
        String strSourceFile = null;
        StringBuilder sb = new StringBuilder(1024);
        String s = null;
        File f = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        byte[] b = new byte[BUFFER_SIZE];
        try
        {
            // Open the zip file.
            zf = new ZipFile(pSourceZip);
            // Get a list of files it contains.
            Enumeration e = zf.entries();
            // Loop through the files.
            while (e.hasMoreElements())
            {
                // Get a file.
                ze = (ZipEntry) e.nextElement();
                // Get name.
                strSourceFile = ze.getName();
                // Get date stamp.
                lngDate = ze.getTime();
                // Assemble destination filename.
                sb.setLength(0);
                sb.append(pDestDir);
                sb.append(File.separator);
                sb.append(strSourceFile);
                s = sb.toString();
                // Copy the file.
                if(ze.isDirectory())
                {
                    // Create the directory.
                    App.createDir(s);
                }
                else
                {
                    // Open the destination file.
                    App.createDirForFile(s);
                    f = new File(s);
                    s = f.getCanonicalPath();
                    f = new File(s);
                    if(f.exists() && (pOverwrite == false))
                    {
                        // Report progress.
                        if(pReporter != null)
                        {
                            pReporter.report();
                        }
                        continue;
                    }
                    fos = new FileOutputStream(f);
                    bos = new BufferedOutputStream(fos, BUFFER_SIZE);
                    // Open the source file.
                    is = zf.getInputStream(ze);
                    bis = new BufferedInputStream(is, BUFFER_SIZE);
                    // Copy data.
                    int x = 0;
                    while (true)
                    {
                        x = bis.read(b, 0, BUFFER_SIZE);
                        if(x == -1)
                        {
                            break;
                        }
                        bos.write(b, 0, x);
                    }
                    // Flush the output.
                    bos.flush();
                    // Close the files.
                    bis.close();
                    bos.close();
                    // Set date stamp.
                    if(f != null)
                    {
                        if(f.exists() == true)
                        {
                            if(lngDate > 0L)
                            {
                                f.setLastModified(lngDate);
                            }
                        }
                    }
/*
                    // Log it.
                    StringBuilder sbLog = new StringBuilder(1024);
                    sbLog.append("Unzipped from ");
                    sbLog.append("\"");
                    sbLog.append(pSourceZip);
                    sbLog.append("!");
                    sbLog.append(strSourceFile);
                    sbLog.append("\"");
                    sbLog.append(" to ");
                    sbLog.append("\"");
                    sbLog.append(s);
                    sbLog.append("\"");
                    sbLog.append(".");
                    String strLog = sbLog.toString();
                    logger.debug(strLog);
*/
                }
                // Report progress.
                if(pReporter != null)
                {
                    pReporter.report();
                }
            }
            // Get the list of files again.
            // We need to reset the date stamp on directories.
            Enumeration ed = zf.entries();
            // Loop through the files.
            while (ed.hasMoreElements())
            {
                // Get a file.
                ze = (ZipEntry) ed.nextElement();
                // Only deal with directories. Ignore files.
                if(!ze.isDirectory())
                {
                    continue;
                }
                // Get name.
                strSourceFile = ze.getName();
                // Get date stamp.
                lngDate = ze.getTime();
                // Assemble destination filename.
                sb.setLength(0);
                sb.append(pDestDir);
                sb.append(File.separator);
                sb.append(strSourceFile);
                s = sb.toString();
                // Create the directory.
                f = new File(s);
                s = f.getCanonicalPath();
                f = new File(s);
                // Set date stamp.
                if(lngDate > 0L)
                {
                    f.setLastModified(lngDate);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            // Close the zip file.
            if(zf != null)
            {
                try
                {
                    zf.close();
                }
                catch (Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
        }
    }

    /**
     * Recursively deletes files and folders.
     * @param pFile the file or folder to delete.
     */
    public static void delete(String pFile)
    {
        // Verify parameters.
        if (pFile == null)
        {
            return;
        }
        // Create temp variables.
        int x = 0;
        int y = 0;
        try
        {
            // Get a file reference.
            File f = new File(pFile);
            // See if it exists.
            if (f.exists() == false)
            {
                return;
            }
            // If it's a directory, delete all files under it first.
            if (f.isDirectory())
            {
                // Get a list of all files.
                String[] strFiles = f.list();
                y = strFiles.length;
                // Loop throught the list.
                for (x = 0; x < y; x++)
                {
                    // Assemble the full filename.
                    StringBuilder sb = new StringBuilder(1024);
                    sb.append(pFile);
                    sb.append(File.separator);
                    sb.append(strFiles[x]);
                    String strFile = sb.toString();
                    // Delete everything in the list.
                    delete(strFile);
                }
            }
            // Delete it.
            f.delete();
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
    }

}
