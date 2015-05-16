# Document Purpose

This document describes the files and registry keys that make up this application.

# Intended Audience

This document may be of interest to anyone who installs or uses the application.

# Program Files

All program, data, and log files are in C:\Program Files\CARFAX Upload Tool

* jdk1.6.0_02 - this directory is the Java runtime environment
* lib - all of the libraries are in this directory
* licenses - this directory has copies of license agreements for each of the libraries
* CARFAX.server.log - log file for CARFAX.server.exe, which is in the jdk and is a copy of javaw.exe
* CARFAX.server.pid - contains the process ID of CARFAX.server.exe while it is running
* CARFAX.service.exe - the Windows NT service executable that runs the server
* CARFAX.service.ini - config file for CARFAX.service.exe
* CARFAX.service.log - log file for CARFAX.service.exe
* CARFAX.service.pid - contains the process ID of CARFAX.service.exe while it is running
* CARFAX.setup.exe - the setup program
* CARFAX.setup.log - log file for the setup program
* CARFAX.sm.install.exe - installs the service
* CARFAX.sm.log - log file for the service manager utilities
* CARFAX.sm.remove.exe - removes the service
* CARFAX.sm.start.exe - starts the service
* CARFAX.sm.stop.exe - stops the service
* CARFAX-server.chm - API documentation for the server
* CARFAX-setup.chm - API documentation for the setup program
* CARFAX-sm.chm - API documentation for the service manager utilities
* readme.html - this file
* release.notes.html - release notes
* sysinfo.dtd - schema for sysinfo.xml
* sysinfo.xml - contains system information that may be by production support

# Configuration Files

The application keeps some configuration files in C:\Documents and Settings\All Users\Application Data\CARFAX Upload Tool.
To make the upgrade process smooth, the uninstaller does not remove these files.

* files.xml - contains the list of files to be uploaded
* odbc.xml - contains the list of ODBC resources to extract data from
* security.xml - contains authentication configuration information
* setup.ini - contains information used by setup.exe

# Start Menu

setup.exe puts shortcuts on the Start menu in C:\Documents and Settings\All Users\Start Menu\Programs\CARFAX Upload Tool.

# Registry Entries

setup.exe creates this key to register the server as a Windows NT service.

* HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Services\CARFAXUploadTool

setup.exe creates the following registry keys to help with production support and upgrades.

* HKEY_LOCAL_MACHINE\SOFTWARE\Infotraxx Systems, LLC\CARFAX Upload Tool

# Scheduled Jobs

The following tasks are performed once per hour, each one starting one minute apart.  All network activity is conducted over a secure connection with an SFTP server.

* SFTPDownloadUpgradeJob - Downloads upgrade.exe (if available) and executes it so the application can upgrade itself when new features are available.  When an upgrade is available, upgrade.exe will stop the service, add, remove, or modify files as needed, and start the service.  The service thens deletes upgrade.exe the next time this job is executed.  If the file is not available, nothing happens.

* SFTPDownloadSecurityJob - Downloads security.xml (if available) to a temporary directory and parses it.  If the file is well-formed and in the proper format, data is copied to the security.xml file that the server uses.  Changes take effect immediately.  If the file is not available, nothing happens.

* SFTPDownloadODBCJob - Downloads odbc.xml (if available) to a temporary directory and parses it.  If the file is well-formed and in the proper format, data is copied to the odbc.xml file that the server uses.  Changes take effect immediately.  If the file is not available, nothing happens.

SFTPDownloadFilesJob - Downloads files.xml (if available) to a temporary directory and parses it.  If the file is well-formed and in the proper format, data is copied to the files.xml file that the server uses.  Changes take effect immediately.  If the file is not available, nothing happens.

* SFTPUploadSysInfoJob - Gathers some important details about your computer that will be useful if help is needed to resolve an issue. The types of information reported include (but are not limited to) things such as:

  * number of processors installed and thier speed
  * amount of memory available
  * amount of free disk space available
  * operating system version
  * a list of available ODBC resources

* SFTPUploadODBCJob - Extracts data from ODBC databases and uploads it.

* SFTPUploadFilesJob - Uploads files.

* SFTPUploadInventoryJob - When requested, this job scans the computer to build a list of files that may be uploaded.  This information (if gathered) is used for the sole purpose of determining which files contain information that may be relevant to CARFAX or its affiliates when the filenames or locations are not known in advance.  This scan can only be performed by special request, so most of the time it will actually do nothing.
