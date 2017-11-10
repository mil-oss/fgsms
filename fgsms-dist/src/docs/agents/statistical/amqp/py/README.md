# FGSMS Agents for Apache Qpid C++ Broker and Redhat MGR

Please report problems at:
https://github.com/mil-oss/fgsms/issues

Website and documentation:
http://mil-oss.github.io/fgsms/


This agent needs the attached script (qpid-stat2) to executable chmod 755.
Then run the java agent with
java java -Dorg.miloss.fgsms.agentConfigFileOverride=fgsms-agent.properties -jar fgsms.QpidPyAgent-<VERSION>-jar-with-dependencies.jar

The jar basically periodically calls the script and parses the output, then 
reports back to the server. The script was provided with Apache Qpid and modified
to have delimitors.