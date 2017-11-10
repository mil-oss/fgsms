# FGSMS Java Persistent Agent

Please report problems at:
https://github.com/mil-oss/fgsms/issues

Website and documentation:
http://mil-oss.github.io/fgsms/

This agent is designed to pick up the slack from the java web service agents for when either
operating in disconnected mode, store, store and persist mode or when the 
fgsms service is offline or not reachable. It's job is simple: watch a specific folder for content
then periodically try to connect to the mother ship. Once connected, it will effectively purge
the local cache.

To install: 
Run one of the JavaPersistentAgentServiceInstall.cmd scripts, match your CPU architecture