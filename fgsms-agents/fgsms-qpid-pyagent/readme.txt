FGSMS Agent for AMQP Qpid/MRG, C++ Brokers Only.

Agent must run with elevated privledges neccessary to use the script qpid-stat. The agent actually uses a modified version of it that adds delimitors to make parsing easier, qpid-stat2.

   chmod rwxr-xr-x qpid-stat2


to configure, modify the properties file within fgsms-agent.properties
	set credentials and urls of the fgsms services, optionally discovery parameters
to start

   java -Dorg.miloss.fgsms.agentConfigFileOverride=/path/to/fgsms-agent.properties -jar FGSMS.QpidPyAgent.jar