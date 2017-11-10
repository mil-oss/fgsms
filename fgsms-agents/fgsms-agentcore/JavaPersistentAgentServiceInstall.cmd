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

rem Batch script for defining the ProcrunService (JVM and Java versions)

rem Copy this file and ProcrunService.jar into the same directory as prunsrv (or adjust the paths below)

setlocal

rem The service names (make sure they does not clash with an existing service)
set SERVICE_JAVA=fgsmsJavaPersistentAgent

rem my location
set MYPATH=%~dp0

rem location of Prunsrv
set PATH_PRUNSRV=%MYPATH%
set PR_LOGPATH=%PATH_PRUNSRV%
rem location of jarfile
set PATH_JAR=%MYPATH%

rem Allow prunsrv to be overridden
if "%PRUNSRV%" == "" set PRUNSRV=%PATH_PRUNSRV%prunsrv

rem Install the 2 services

echo Installing %SERVICE_JAVA%
"%PRUNSRV%" //DS//%SERVICE_JAVA%
"%PRUNSRV%" //IS//%SERVICE_JAVA%

echo Setting the parameters for %SERVICE_JAVA%
"%PRUNSRV%" //US//%SERVICE_JAVA% --Jvm=auto --StdOutput auto --StdError auto   --Startup=auto  ^
--Classpath="%PATH_JAR%fgsms.AgentCore-7.0.0-SNAPSHOT-jar-with-dependencies.jar"; ^
--StartMode=jvm --StartClass=org.miloss.fgsms.agentcore.PersistentStorage --StartMethod start ^
 --StopMode=jvm  --StopClass=org.miloss.fgsms.agentcore.PersistentStorage  --StopMethod stop  ^
 --Description="fgsms Persistent Storage Agent for Java Web Services"  ^
 ++JvmOptions=-Dorg.miloss.fgsms.agentConfigFileOverride=%PATH_JAR%fgsms-agent.properties 
 
rem  --StartParams=start ^
echo Installation of %SERVICE_JAVA% is complete
echo Finished
