@echo off
rem 

rem This Source Code Form is subject to the terms of the Mozilla Public
rem License, v. 2.0. If a copy of the MPL was not distributed with this
rem file, You can obtain one at http://mozilla.org/MPL/2.0/.

rem If it is not possible or desirable to put the notice in a particular
rem file, then You may include the notice in a location (such as a LICENSE
rem file in a relevant directory) where a recipient would be likely to look
rem for such a notice.

rem 
  
rem  ---------------------------------------------------------------------------
rem  US Government, Department of the Army
rem  Army Materiel Command
rem  Research Development Engineering Command
rem  Communications Electronics Research Development and Engineering Center
rem  ---------------------------------------------------------------------------

rem Batch script for removing the ProcrunService (JVM and Java versions)

setlocal

rem The service names (make sure they does not clash with an existing service)

set SERVICE_JAVA=fgsmsJavaPersistentAgent

rem my location
set MYPATH=%~dp0

rem location of Prunsrv
set PATH_PRUNSRV=%MYPATH%
set PR_LOGPATH=%PATH_PRUNSRV%
rem Allow prunsrv to be overridden
if "%PRUNSRV%" == "" set PRUNSRV=%PATH_PRUNSRV%prunsrvIA64


echo Removing %SERVICE_JAVA%
%PRUNSRV% //DS//%SERVICE_JAVA%
%PRUNSRV% //IS//%SERVICE_JAVA%
sc delete %SERVICE_JAVA%
echo Finished
