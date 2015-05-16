@echo off
cls
setlocal

rem set drive and directory
set DRV=%~d0
set DIR=%~dp0
set DIR=%DIR:~0,-1%
set FQP=%~f0

rem set component names
set JDK_NAME=jdk1.6.0_14
set ANT_NAME=apache-ant-1.7.0
set YDOC_NAME=ydoc-2.2_04-jdk1.5

rem set home directories
call :fqp %DIR%\bin\%JDK_NAME%
set JAVA_HOME=%FQP%
call :fqp %DIR%\bin\%ANT_NAME%
set ANT_HOME=%FQP%

rem set the path
set PATH=.;%JAVA_HOME%\bin;%ANT_HOME%\bin;C:\Perl\bin

rem set command-line arguments
set CP=.
set CP=%CP%;%ANT_HOME%\lib\ant-launcher.jar
set CP=%CP%;%DIR%\bin\jdepend-2.9\lib\jdepend-2.9.jar
set MEM_MAX=64m
set MEM_MIN=32m
set MAIN=org.apache.tools.ant.launch.Launcher

rem parse command line
set ARG=%1
if "%1" equ "-?" set ARG=-projecthelp

rem build
java -cp "%CP%" -Xms%MEM_MIN% -Xmx%MEM_MAX% -Xincgc %MAIN% %ARG%
goto :eof

:fqp
rem gets a fully-qualified path
rem @param %1 the path to qualify
rem sets the FQP variable to the fully-qualified path of %1
if "%1" equ "" goto :eof
set FQP=%~f1%
goto :eof
