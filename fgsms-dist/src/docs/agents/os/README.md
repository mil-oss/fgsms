# FGSMS OS Agent

Please report problems at:
https://github.com/mil-oss/fgsms/issues

Website and documentation:
http://mil-oss.github.io/fgsms/


This agent collects data on your computer and all processes running on it and
reports it to the server.

Windows:
run one of the `OSAgentServiceInstall.cmd` files, match up your architecture.

Linux:
script/rpm coming soon, but basically it's
java -Dorg.miloss.fgsms.agentConfigFileOverride=fgsms-agent.properties -jar fgsms.OSAgent.jar