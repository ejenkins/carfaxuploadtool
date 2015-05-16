package com.infotraxx.carfax.setup.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.LineSeparator;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

/**
 * XML utilities.
 * @author Ed Jenkins
 */
public class XML
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(XML.class);

    /**
     * Buffer size.
     */
    public static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * Constructor.
     */
    public XML()
    {
    }

    /**
     * Creates a new document.
     * @param pNamespace the namespace URI of the document element to create or null.
     * @param pName the qualified name of the document type to be created.
     * @param pPublic the public identifier.
     * @param pSystem the system identifier.
     * @return a new document with a document element (pName) already appended.
     */
    public static Document newDocument(String pNamespace, String pName, String pPublic, String pSystem)
    {
        // Create temp variables.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        DOMImplementation di = null;
        DocumentType dt = null;
        // Create return variable.
        Document doc = null;
        // Create a new document.
        try
        {
            dbf.setNamespaceAware(true);
            db = dbf.newDocumentBuilder();
            di = db.getDOMImplementation();
            dt = di.createDocumentType(pName, pPublic, pSystem);
            doc = di.createDocument(pNamespace, pName, dt);
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return doc;
    }

    /**
     * Parses XML data from a stream.
     * Uses the default buffer size.
     * @param pIS the input stream to read from.
     * @return a DOM document or null if unable to read from the stream.
     */
    public static Document parse(InputStream pIS)
    {
        return XML.parse(pIS, BUFFER_SIZE);
    }

    /**
     * Parses XML data from a stream.
     * @param pIS the input stream to read from.
     * @param pBufferSize the buffer size.
     * @return a DOM document or null if unable to read from the stream.
     */
    public static Document parse(InputStream pIS, int pBufferSize)
    {
        // Verify parameters.
        if(pIS == null)
        {
            return null;
        }
        // Create return variable.
        Document doc = null;
        // Create temp variables.
        BufferedInputStream bis = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try
        {
            // Read the file.
            logger.debug("Begin parsing.");
            bis = new BufferedInputStream(pIS, pBufferSize);
            dbf.setFeature("http://xml.org/sax/features/validation", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setNamespaceAware(true);
            db = dbf.newDocumentBuilder();
            doc = db.parse(bis);
            logger.debug("End parsing.");
        }
        catch(Throwable t)
        {
            logger.error("Error parsing.", t);
        }
        finally
        {
        }
        // Return result.
        return doc;
    }

    /**
     * Reads an XML file.
     * Uses the default buffer size.
     * @param pFilename the name of the file to read.
     * @return a DOM document or null if unable to read the file.
     */
    public static Document read(String pFilename)
    {
        return XML.read(pFilename, BUFFER_SIZE);
    }

    /**
     * Reads an XML file.
     * @param pFilename the name of the file to read.
     * @param pBufferSize the buffer size.
     * @return a DOM document or null if unable to read the file.
     */
    public static Document read(String pFilename, int pBufferSize)
    {
        // Verify parameters.
        if(pFilename == null)
        {
            return null;
        }
        // Create return variable.
        Document doc = null;
        // Create temp variables.
        File f = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try
        {
            // Read the file.
            logger.debug("Begin reading " + pFilename + ".");
            f = new File(pFilename);
            if(f.exists())
            {
                fis = new FileInputStream(f);
                bis = new BufferedInputStream(fis, pBufferSize);
                dbf.setFeature("http://xml.org/sax/features/validation", false);
                dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
                dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                dbf.setNamespaceAware(true);
                db = dbf.newDocumentBuilder();
                doc = db.parse(bis);
            }
            else
            {
                logger.warn(pFilename + " does not exist.");
            }
            logger.debug("End reading " + pFilename + ".");
        }
        catch(Throwable t)
        {
            logger.error("Error reading " + pFilename + ".", t);
        }
        finally
        {
        }
        // Return result.
        return doc;
    }

    /**
     * Loads an XML resource.
     * Uses the default buffer size.
     * @param pFilename the name of the file to read.
     * @return a DOM document or null if unable to read the file.
     */
    public static Document load(String pFilename)
    {
        return XML.load(pFilename, BUFFER_SIZE);
    }

    /**
     * Loads an XML resource.
     * @param pFilename the name of the file to read.
     * @param pBufferSize the buffer size.
     * @return a DOM document or null if unable to read the file.
     */
    public static Document load(String pFilename, int pBufferSize)
    {
        // Verify parameters.
        if(pFilename == null)
        {
            return null;
        }
        // Create return variable.
        Document doc = null;
        // Create temp variables.
        InputStream is = null;
        BufferedInputStream bis = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try
        {
            // Open the file.
            ClassLoader cl = XML.class.getClassLoader();
            URL url = cl.getResource(pFilename);
            is = url.openStream();
            bis = new BufferedInputStream(is, pBufferSize);
            // Read the file.
            logger.debug("Begin loading " + pFilename + ".");
            dbf.setFeature("http://xml.org/sax/features/validation", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            dbf.setNamespaceAware(true);
            db = dbf.newDocumentBuilder();
            doc = db.parse(bis);
            logger.debug("End loading " + pFilename + ".");
        }
        catch(Throwable t)
        {
            logger.error("Error loading " + pFilename + ".", t);
        }
        finally
        {
            // Close the file.
            if(bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
        }
        // Return result.
        return doc;
    }

    /**
     * Transforms a document.
     * @param pXML the XML file to transform.
     * @param pXSL the XSL file specifying the rules.
     * @return a new document or null if an error occurs.
     */
    public static Document transform(Document pXML, Document pXSL)
    {
        // Create return variable.
        Document d = null;
        // Create temp variables.
        TransformerFactory factory = null;
        Transformer transformer = null;
        DOMSource domSource = new DOMSource(pXML);
        DOMSource domXSL = new DOMSource(pXSL);
        DOMResult domResult = new DOMResult();
        // Perform the transformation.
        try
        {
            logger.debug("Begin transform.");
            factory = TransformerFactory.newInstance();
            transformer = factory.newTransformer(domXSL);
            transformer.transform(domSource, domResult);
            Node n = domResult.getNode();
            if(n instanceof Document)
            {
                d = (Document)n;
            }
            logger.debug("End transform.");
        }
        catch(Exception ex)
        {
            logger.error(ex, ex);
        }
        // Return result.
        return d;
    }

    /**
     * Writes a document to a file.
     * @param pFilename the name of the file to save.
     * @param pPublic the public identifier.
     * @param pSystem the system identifier.
     * @param pFormat "xml", "html", or "text".
     * @param pLineSeparator a constant from the LineSeparator class.
     * @param pDoc the document to save.
     * @see org.apache.xml.serialize.LineSeparator
     */
    public static void write(String pFilename, String pPublic, String pSystem, String pFormat, String pLineSeparator, Document pDoc)
    {
        // Verify parameters.
        if(pFilename == null)
        {
            return;
        }
        if(pDoc == null)
        {
            return;
        }
        String strFormat = "xml";
        if(pFormat != null)
        {
            if(pFormat.equalsIgnoreCase("html"))
            {
                strFormat = "html";
            }
            if(pFormat.equalsIgnoreCase("text"))
            {
                strFormat = "text";
            }
        }
        String strLineSeparator = LineSeparator.Unix;
        if(pLineSeparator != null)
        {
            if(pLineSeparator.equalsIgnoreCase(LineSeparator.Macintosh))
            {
                strLineSeparator = LineSeparator.Macintosh;
            }
            if(pLineSeparator.equalsIgnoreCase(LineSeparator.Web))
            {
                strLineSeparator = LineSeparator.Web;
            }
            if(pLineSeparator.equalsIgnoreCase(LineSeparator.Windows))
            {
                strLineSeparator = LineSeparator.Windows;
            }
        }
        // Create dir if needed.
        App.createDirForFile(pFilename);
        // Setup the formatter.
        OutputFormat of = new OutputFormat();
        if( (pPublic != null) || (pSystem != null) )
        {
            of.setDoctype(pPublic, pSystem);
        }
        of.setIndenting(true);
        of.setIndent(4);
        of.setLineSeparator(strLineSeparator);
        of.setLineWidth(0);
        of.setMethod(strFormat);
        of.setVersion("1.0");
        // Create temp variables.
        File f = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try
        {
            // Open the file.
            logger.debug("Begin write for " + pFilename + ".");
            f = new File(pFilename);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw, BUFFER_SIZE);
            pw = new PrintWriter(bw);
            // Setup the serializer.
            XMLSerializer xs = new XMLSerializer();
            xs.setOutputFormat(of);
            xs.setOutputCharStream(pw);
            // Save the file.
            xs.serialize(pDoc);
            logger.debug("End write for " + pFilename + ".");
        }
        catch (Exception ex)
        {
            logger.error(ex, ex);
        }
        finally
        {
            // Close the stream.
            if(pw != null)
            {
                try
                {
                    pw.close();
                }
                catch (Exception ex)
                {
                    logger.error(ex, ex);
                }
            }
        }
    }

}
