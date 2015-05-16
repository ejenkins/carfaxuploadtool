@echo off
setlocal

set app.name.short=CARFAX
set app.name.long=CARFAX Upload Tool

set stop=%ProgramFiles%\%app.name.long%\%app.name.short%.sm.stop.exe
if exist "%stop%" "%stop%"

set remove=%ProgramFiles%\%app.name.long%\%app.name.short%.sm.remove.exe
if exist "%remove%" "%remove%"

set pf=%ProgramFiles%\%app.name.long%
if exist "%pf%" rd /s /q "%pf%"

rem set odf=%ALLUSERSPROFILE%\Application Data\%app.name.long%
rem if exist "%odf%" rd /s /q "%odf%"

set mdf=%USERPROFILE%\Application Data\%app.name.long%
if exist "%mdf%" rd /s /q "%mdf%"

set sm=%ALLUSERSPROFILE%\Start Menu\Programs\%app.name.long%
if exist "%sm%" rd /s /q "%sm%"
