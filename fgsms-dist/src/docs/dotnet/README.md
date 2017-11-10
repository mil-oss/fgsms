# .NET based capabilites for FGSMS


## Agents

### FGSMS.NETAgent.dll

Contains all .NET based monitoring capabilities including

 - WCF client and services
 - ASP.NET client and services

### FGSMS.PersistentStorageAgent.dll

The persistent storage agent for .NET. Basically when another .NET agent can't reach
the server, it can be configured to dump all recorded traffic to a given folder to 
prevent filling up the memory or dropping it. The persistent storage agent basically
watches that folder and periodically attempts to phone home. Once connectivity is 
reestablished, the local system content is then enqueued, transmitted, then deleted.

## Tools and utilities
 
## Current.config

Contains a configuration snippet of all available configuration settings for the agents

### FGSMS.Util.exe

A tool to encrypt passwords that the agent can decrypt as needed. Used for talking 
to the FGSMS server.

### MessageDecryptor

If necessary, this tool can decrypt encrypted messages stored by the persistent agent.

### FGSMS.Tools.AgentConfig

A tool who's purpose was to help you configure agents and hopefully make it easier. It's
probably not too useful and may be removed in the future.

## Demos

### org.miloss.fgsms.webparts.wsp

A collection of SharePoint webparts to demonstrate functionality, but it's probably not
ready for prime time.

### FGSMS Tray Icon for Windows

A sample tray icon (next to the clock) tool that polls FGSMS's status service. It opens
a popup when something goes offline. On Windows 10, it's very annoying due to the sound
effect that Windows insists on. This is more of a demo than anything else and needs work.



# Installing the agents

TODO. At one point we had an msi installer than did everything magically. Maybe one day
it will return.