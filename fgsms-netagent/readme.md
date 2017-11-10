# FGSMS's .NET Agents

This article is developer focused on getting everything compiled and tested.

## Prereqs

- .NET 4.0
- Windows SDK 7a or newer
- Nuget CLI https://nuget.org/nuget.exe https://www.nuget.org/ and on the current path
- WSE 2.0 sp3 (auto resolved by nuget)
- WSE 3.0 (auto resolved by nuget)

- NUnit - runs unit tests (auto resolved by nuget)
- Microsoft SharePoint 2007 libraries (optional for Web Part support) (auto resolved by nuget)
- Choice of
	- Visual Studio 2010 or newer, currently running 2015 Community Edition
	- OR
	- Visual Studio isolated shell
		AND 
		- BuildTools_full
		- set the system environment variable to the correct msbuild path location 
		- VSToolsPat=C:\Program Files (x86)\MSBuild\Microsoft\VisualStudio\v14.0\</VSToolsPath

	
## Steps you'll need to do get this building in your environment.

- Install the prerequists. If you only have the community edition of Visual Studio, then you won't be able to build the msi installer. Everything else should build
- Run `nuget restore` http://stackoverflow.com/a/23935892/1203182 https://nuget.org/nuget.exe
- Make your own pfx file
- Attach it to the project with visual studio (or edit the .csproj file for FGSMS.NETAgent)
- Build the .NET Agent.
- Use `sn.exe -T FGSMS.NETAgent.dll` to capture to signed key
- Now the painful part, visit all of the configuration files (end in .config) within this directory and update all the references to use the correct public key token
- If you want the SharePoint webpart to work, you'll also need to edit the stuff in the `solution` folder.
- Now you can build the rest of the projects.
- Build with msbuild.exe or with Visual Studio

## Making the test apps function

OK, so how does everything work (without the automagical installer)?

Alright, we need 3 things
 - the DLL binary deployed or on the .NET equivalent of the classpath
 - Configuration settings for FGSMS (via appSettings)
 - Configuration that applies the behavior/filter/agent into whatever you want to monitor.
 
### Binary deploy
 
There are two ways to deploy the .NET Agent, via the GAC and 
individually with applications. The GAC is preferable since it's 
less work, however it can be a pain for development (getting older cached 
versions loaded makes debugging harder.

#### GAC deploy

**Must with with elevated permissions**

To deploy to the GAC, using `gacutil` which is included with the .NET framework

The gacutil is usually located in one of the following locations

- %ProgramFiles%\Microsoft Visual Studio .NET 2003\SDK\v1.1\Bin\gacutil.exe
- %ProgramFiles(x86)%\Microsoft SDKs\Windows\vXXX\Bin
- %ProgramFiles%\Microsoft SDKs\Windows\vXXX\Bin
- %windir%\Microsoft.NET\Framework\vXXXX
- %windir%\Microsoft.NET\Framework64\vXXXX

````
C:\projects\fgsms\fgsms.NETAgent\FGSMS.NETAgent\bin\Debug>gacutil -i FGSMS.NETAgent.dll
Microsoft (R) .NET Global Assembly Cache Utility.  Version 4.0.30319.0
Copyright (c) Microsoft Corporation.  All rights reserved.

Assembly successfully added to the cache
````

Next, let's grab the signing key hash. We'll need it later. We'll use the Strong Name utility,
which is usually located where the the gacutil is.

````
C:\projects\fgsms\fgsms.NETAgent\FGSMS.NETAgent\bin\Debug>sn -T FGSMS.NETAgent.dll

Microsoft (R) .NET Framework Strong Name Utility  Version 4.0.30319.0
Copyright (c) Microsoft Corporation.  All rights reserved.

Public key token is 3152d5a608059f56
````

Make note of the public key token hex string.


#### Web App deploy

Just copy the FGSMS dll into the bin folder. That's usually enough

#### Standalone Application

Add a reference to FGSMS.NETAgent.dll and continue on

### Configuration settings

The FGSMS .NET Agent needs a bunch of settings in order to operate. Most are optional. For the most part, these
settings mirror the Java agent's settings. There's multiple ways to apply the settings.

There is a sample that works with the Java endpoints located in `FGSMS.NETInstaller/current.config`.
The goal here is to merge that file with however you decide to use FGSMS.

#### Global appSettings

Applying the settings to the System `machine.config` file is the easiest. They are located within the following path(s). There may be
more than one so you'll have to apply the settins twice or more depending on how many versions of .NET are installed

- %windir%\Microsoft.NET\Framework\v2.XXXX\machine.config
- %windir%\Microsoft.NET\Framework64\v2.XXXX\machine.config
- %windir%\Microsoft.NET\Framework\v4.XXXX\machine.config
- %windir%\Microsoft.NET\Framework64\v4.XXXX\machine.config



#### Global Agent Deploy

Super useful for monitoring all .NET based things on the computer