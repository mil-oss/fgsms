# Deployment Planning

In order to plan out a successful deployment of FGSMS, you should use the following guidelines for making several key decisions prior to attempting a deployment of FGSMS.

## Scaling

FGSMS is designed to be scalable for all web facing components, specifically, the web interface, agents, and services.

Database scaling such as clustering is supportable via out of the box solutions for PostreSQL. Database mirroring or failover capabilities is also supported by all components, however the database administrator is responsible for the configuration of such a scenario and is not covered in this guide. This configuration is referred to as “Secondary” database connections.

For transactional services, FGSMS’s Data Collector Service is by far the busiest and is thus a critical component for keeping all of the agents operational. It is included within the FGSMS.Services.war file. There are many different parameters that determine the expected quantity of web service transactions. In general, FGSMS’s DCS can write data to the database at approximately 28ms per transaction (message logging off) on a conventional hard drive.

FIXME - we now have automated benchmarking. Quick summary from the last check
- Alex's dev environment - Java Data Collector + Java Web service agent (fgsms.AgentCore.jar) with the Java Message Processor was able to transmit and record about 1400 transactions per second on the high end. This was on an 8 core machine, Windows 10, 8 GB ram, with a reasonably priced solid state drive. All components were running on the same machine.
- Travis CI's dev environment - Java Data Collector + Java Web service agent (fgsms.AgentCore.jar) with the Java Message Processor was able to transmit and record about 400 transactions per second on the high end. This was on Travis CI's virtual machine, which was last known to to run on Amazon Web Services with 4 CPU cores and 3GB ram. Hardware details are unknown.

These figures are a highly dependent on the server’s capabilities.

## Authentication scenarios

FGSMS’s components support two different authentication scenarios to meet most operational and demonstrative needs (CAC/PKI, and Username/Password). The following sections outline the basics of each scenario to help you (the person integrating FGSMS into your infrastructure) decide which is the most appropriate. 

Due to the distributed nature of FGSMS, the following authentication links must be satisfied:

 * Your web browser to Web Interface
 * Web Interface to Web Services
 * Agents to Web Services

Whichever the authentication mode you select, it must be homogenous across all FGSMS components within each realm. 
This means that all agents, services, web gui, and services must be configured to run in the same authentication mode (auxiliary services are not affected by this).

Note, it is possible to have one collection of FGSMS components running CAC/PKI and another collection running Username/Password, each sharing the same databases. 

## Common Access Card and Public Key Infrastructure (CAC and PKI)

FGSMS runs in Tomcat/Jboss and therefore in order to use CAC/PKI authentication, the following conditions must be met.
All connections to Tomcat/Jboss must be SSL
All connections to Tomcat/Jboss must require a client certificate
The preconfigured Tomcat/Jboss included with FGSMS’s distribution package has port 8443 for SSL and port 8888 for non-SSL access, configured for username and passwords.

FGSMS’s Web GUI must be reconfigured for both logins to the GUI and for logins to the services by editing the web.xml and jboss-web.xml files AND /META-INF/config.properties file.

FGSMS’s Web Services must be reconfigured for CAC/PKI by editing the web.xml and jboss-web.xml files.

FGSM’s Java agents use the file FGSM.AgentCore.jar, it must also be updated when changing authentication modes.

All referenced files include samples and comments within the specific configuration files.

## Username and Password

FGSMS runs in Tomcat/Jboss and therefore Username and Password authentication can be enabled by any login module for Tomcat/Jboss that enables HTTP BASIC authentication over SSL. User stores can be LDAP, a SQL database or a properties file (or any other Tomcat/Jboss supported authentication mechanism). HTTP BASIC is requirement. User credential verification happens at the FGSMS Services WAR file.

Why is basic required? Simply because most SOAP frameworks do not support it, unfortunately.

FGSMS’s Web GUI must be reconfigured for both logins to the GUI and for logins to the services by editing the web.xml and jboss-web.xml files AND /META-INF/config.properties file.

FGSMS’s Web Services must be reconfigured for CAC/PKI by editing the web.xml and jboss-web.xml files.

FGSMS’s agent configuration files must also be updated when changing authentication modes. .NET Agents use a similar configuration file.

All referenced files include samples and comments within the specific configuration files.


# Scaling FGSMS for high availability

FGSMS can scale as wide as it needs to be with a few exceptions. The primary component is the database and as such, it is the most important part to consider when up scaling FGSM. Mirroring (failover) and clustering is supported.

The all of the components of FGSMS can be scaled and/or load balanced through several different mechanisms.

 * Database, FGSMS supports both primary and failover/mirroring configurations
 * Web Services, multiple instances of the web services can be setup of different Jboss servers. In addition, FGSMS’s agents support several different algorithms which support accessing multiple URLs for FGSMS’s web services.
 * Web GUI, multiple instances of the Web GUI can be setup on different application servers.
 * Auxiliary Services (bundled within the Service war), multiple instances of the Auxiliary Services war can be setup of different app servers so long as their instances have the Quartz engine configured for clustering. See the Quartz user’s guide

