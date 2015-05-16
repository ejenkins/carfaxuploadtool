package com.infotraxx.carfax.server.config;

import org.apache.log4j.Logger;

/**
 * Value Object for holding ODBC configuration data in memory.
 * @author Ed Jenkins
 */
public class ODBCResource
{

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(ODBCResource.class);

    /**
     * Data Source Name.
     */
    public String dsn = null;

    /**
     * Username.
     */
    public String username = null;

    /**
     * Password.
     */
    public String password = null;

    /**
     * Filename.
     */
    public String filename = null;
    
    /**
     * Delimiter.
     */
    public String delimiter = null;

    /**
     * Description.
     */
    public String description = null;

    /**
     * SQL.
     */
    public String sql = null;
    
    /**
     * Constructor.
     */
    public ODBCResource()
    {
    }

}
