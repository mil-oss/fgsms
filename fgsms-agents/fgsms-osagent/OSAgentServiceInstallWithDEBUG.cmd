@echo off
rem 
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

rem Batch script for defining the ProcrunService (JVM and Java versions)

rem Copy this file and ProcrunService.jar into the same directory as prunsrv (or adjust the paths below)

setlocal

rem The service names (make sure they does not clash with an existing service)
set SERVICE_JAVA=fgsmsOSAgent

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
"%PRUNSRV%" //US//%SERVICE_JAVA% --Jvm=auto --StdOutput auto --StdError auto ^
--Classpath=%PATH_JAR%fgsms.OSAgent.jar; ^
--StartMode=jvm --StartClass=org.miloss.fgsms.osagent.OSAgentDaemon --StartMethod start ^
 --StopMode=jvm  --StopClass=org.miloss.fgsms.osagent.OSAgentDaemon  --StopMethod stop ^
 --Description="fgsms Operating System Agent" ^
 ++JvmOptions=-Djava.library.path="%PATH_JAR%" -Dorg.miloss.fgsms.agentConfigFileOverride="%PATH_JAR%fgsms-agent.properties" ^
 --StartParams=debug
rem  --StartParams=start ^
echo Installation of %SERVICE_JAVA% is complete
echo Finished
