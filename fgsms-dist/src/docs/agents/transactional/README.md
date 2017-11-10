# FGSMS Agents Java Web Services

Please report problems at:
https://github.com/mil-oss/fgsms/issues

Website and documentation:
http://mil-oss.github.io/fgsms/

These agents are for Java web services. Basically, pick out the most appropriate
agent and add it to the class path of the service you want to monitor. Typically
this is an simple as adding the jar into the "lib" folder or WEB-INF/lib folder.

Next, tweak the config file as defined in the deployment guide. It's usually something
like cxf.xml, beans.xml, web.xml, a wsdd file or something similar.

Then, you'll want to add a flag to the web server's start up script. Usually it's

-Dorg.miloss.fgsms.agentConfigFileOverride=fgsms-agent.properties

and then you drop the properties file in the startup folder of the server.