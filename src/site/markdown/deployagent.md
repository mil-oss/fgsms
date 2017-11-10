# Installation Procedures FGSMS Agents

Note: this article is dated and needs many updates.

We're currently working on an automagical installation wizard to automate most
of the stuff in this article. The info here is mostly useful as a a reference
guide.

# Java Web Service Agents

Agents are broken into both CLIENT and SERVICE agents. Only configure the agent that is needed to monitor a specific service. Client and service agents may be combined. Currently the behavior is that each time a message is witnessed, it will be recorded by each agent.

# Java Agents

From the distribution package, locate the agents folder.
Edit the file in fgsms-agent.properties
Modify the required settings
Save and close the file

## Configuration Parameters
For the latest documentation view the inline comments within the example fgsms-agent.properties within FGSMS.AgentCore.jar.

This section is for Java based FGSMS Web Service (Transactional) Agents only.



| Property  | Values | Description  
| ---       | ---    | ---          
| FGSMS.AuthMode | None, PKI, UsernamePassword | The authentication mode to the fgsms server.
| FGSMS.AuthMode.Username  | string  
| FGSMS.AuthMode.Password |  string |  Encrypted password. It can be encrypted with `java -jar fgsms.Common-XXX-with-dependencies.jar`
| FGSMS.AuthMode.PKICert  |  Not yet implemented
| message.processor.ignoreList |  string&#124;string&#124;….  | delimited URLs to be ignored delimited by the pipe symbol
| datacollectorservice.URL |  url1&#124;url2&#124;….  Execution URL to an instance of FGSMS DCS Service. Multiple values delimited by &#124;
| datacollectorservice.algorithm |  fallback, roundrobin |  "Fallback = try the URLs in order until one works. Roundrobin = iteratively loop through the list, distributing the load evenly. If one fails, try the next one until we succeed."
| datacollectorservice.retry  >= 0  
| policyconfigurationservice.URL |  url1&#124;url2&#124;…. |  Execution URL to an instance of FGSMS PCS Service. Multiple values delimited by &#124;
| policyconfigurationservice.algorithm |  fallback, roundrobin |  "Fallback = try the URLs in order until one works. Roundrobin = iteratively loop through the list, distributing the load evenly. If one fails, try the next one until we succeed."
| policyconfigurationservice.retry | >= 0  
| discovery.interval  |  30000 | if enabled, a list of URLs will be periodically loaded and dynamically ADDED to the list of above for each service. Time in milliseconds
| discovery.uddi.enabled |  True/false  
| discovery.uddi.inquiry.url  |   Optional, execution URL, overrides the WSDL location.
| discovery.uddi.inquiry.authrequired |  true/false  | Some UDDI servers allow for anonymous Inquires.
| discovery.uddi.inquiry.authmode |  http or uddi | "UDDI = Use the security endpoint for authentication. HTTP = Use HTTP style authentication"
| discovery.uddi.security.url |   Optional, execution URL, overrides the WSDL location.
| discovery.uddi.security.username |    string |
| discovery.uddi.security.password  |   Encrypted password |
| discovery.uddi.lookup.findType |  EndpointBindingKey, EndpointKeyWord, BusinessService |  "EndpointBindingKey, loads from a BindingTemplate Key, this must be the unique identifier for the binding template containing 0 or more endpoints. EndpointKeyWord  , attempts to find services by name, aka the display name. ServiceEntityKey loads from a BusinessService key, this must be the unique identifier for the business service containing 0 or more binding templates with 0 or more endpoints"
| discovery.uddi.lookup.dcs.servicename  | string |  Based on the findType, the value that we are looking for, see above.
| discovery.uddi.lookup.pcs.servicename  | string |  Based on the findType, the value that we are looking for, see above.
| message.processor.dead.message.queue.duration | number >= 10000 | Default = 600000" Time in ms in which a message is declared dead. This occurs when an request comes in, but a response is not returned
| agent.dependencyinjection.enabled | true/false |  Enables/Disables the insertion of headers into messages to track and correlate service chaining events.
| agent.unavailablebehavior |  PURGE, HOLD, HOLDPERSIST |  "PURGE= purge the queues and drop all messages until PCS/DCS is available, then resume normal operations. HOLD= hang on to the data until PCS/DCS is available again, if this container goes down, data still in the queue will be lost. HOLDPERSIST = hang on to the data until PCS/DCS is available again, if this container goes down, data will be stored in a temp file"
| agent.offlinestorage |  [path] |  Used as the Persistent storage location for metrics/message logs.

## Adding the Agent to your Java classpath

If you happen to have the source code to the component you're monitoring and you're using maven or gradle to build the system, then you just simply need to the appropriate agent as a dependency, add your config file and go.

From the distribution zip, you'll need fgsms.AgentCore-(version)-with-dependencies.jar and one of the following jars that are specific to your application.

## JbossWS JAX-WS Service Agent(FGSMS.JbossWSJAXWSAgent.jar)
The installation of JbossWS agents depends on what version of JbossWS you are using. By default Jboss AS 4.2.3 comes with JbossWS-Native 2.0, which is incompatible with FGSMS. JbossWS Native 3.1.1 should be used instead. If using JbossWS Native 3.1.1, things are much easier as it supports a global standard-endpoint-config.xml file. This file is located within the jbossws.sar/META-INF folder.

Later versions of Jboss AS use JbossWS variants which do NOT support globally defined handlers within jbossws.sar. In this case, start with the default standard-endpoint-config.xml, add the FGSMS values below, and then copy the standard-endpoint-config.xml file into the /META-INF folder of each service to needs to be monitored.

Edit the file within server\default\deploy\jbossws.sar\META-INF\standard-jaxws-endpoint-config.xml

Add the remarked sections below to the standard-endpoint-config.xml file.

```
  <endpoint-config>
    <config-name>Standard Endpoint</config-name>
    <pre-handler-chains>
      <javaee:handler-chain>
        <javaee:protocol-bindings>##SOAP11_HTTP</javaee:protocol-bindings>
<!-- insert FGSMS handler here -->
		<javaee:handler>
          <javaee:handler-name>FGSMS Agent</javaee:handler-name>
          <javaee:handler-class>org.miloss.fgsms.agents.JbossWSMonitor</javaee:handler-class>
        </javaee:handler>
<!-- insert FGSMS handler here -->
      </javaee:handler-chain>
    </pre-handler-chains>
  </endpoint-config>
```

Restart Jboss and perform some action that calls a web service within the container. You should see some output in the standard Jboss server.log related to FGSMS. Only services based on JAX-WS using the default class loader will be monitored. Any web application that uses another class loader or has JbossWS embedded into the WAR or EAR file will not automatically be monitored. Follow the same procedure as above to monitor them.

## JbossWS JAX-WS Client Agent(FGSMS.JbossWSJAXWSAgent.jar)

The installation of JbossWS agents depends on what version of JbossWS you are using. By default Jboss AS 4 comes with JbossWS-Native 2.0, which is incompatible with FGSMS. JbossWS Native 3.1.1 should be used instead. If using JbossWS Native 3.1.1, things are much easier as it supports a global standard-endpoint-config.xml file. This file is located within the jbossws.sar/META-INF folder.

Later version of Jboss AS use different variants which do NOT support globally defined handlers. In this instance, start with the default file, add the FGSMS values, and copy the standard-endpoint-config.xml file into the /META-INF folder of the service to monitored.

Edit the file within server\default\deploy\jbossws.sar\META-INF\standard-jaxws-client-config.xml

Add the remarked sections below to the standard-client-config.xml file.

```
  <client-config>
    <config-name>Standard Client</config-name>
	<post-handler-chains>
      <javaee:handler-chain>
		 <javaee:handler>
			 <javaee:handler-name>FGSMS Handler</javaee:handler-name>
			  <javaee:handler-class>org.miloss.fgsms.agents.JbossWSClientAgent</javaee:handler-class>
			</javaee:handler>
		 </javaee:handler-chain>
    </post-handler-chains>
    <feature>http://org.jboss.ws/dispatch/validate</feature>	
  </client-config>
```

Restart Jboss and perform some action that calls a web service within the container. You should see some output in the standard Jboss server.log related to FGSMS. Only services based on JAX-WS using the default class loader will be monitored. Any web application that uses another class loader or has JbossWS embedded into the WAR or EAR file will not automatically be monitored. Follow the same procedure as above to monitor them.


## Axis 1.3+ Client and Service Agent (FGSMS.Axis1xAgent.jar)
Copy the following files into the server/default/lib folder

Modify the web application containing the service that you wish to monitor server/default/deploy/warfile.war/ WEB-INF/server-config.wsdd to include the following

```
<requestFlow>
	<handler name="FGSM"	type="java:org.miloss.fgsms.agents.Axis1MonitorInbound">
	</handler>
</requestFlow>
<responseFlow>
		<handler name="FGSM" type="java:org.miloss.fgsms.agents.Axis1MonitorOutbound">
	</handler>
</responseFlow>
```

As an additional dependency, the JbossWS framework or some other kind of JAXWS framework should also be present within this container/deployment. You may have to experiment depending on the version of Jboss in use.

## Axis 2.x, Metro, Apache CXF (FGSMS.GenericJAXWSAgent.jar)
Axis 2.x is based on JAX-WS, therefore the same files and procedures hold true for JbossWS JAX-WS. The only difference is that standard-jaxws-endpoint-config.xml is NOT modified, only the server-config.wsdd is modified to include the class org.miloss.fgsms.agent.JAXWSGenericAgent (for services) and org.miloss.fgsms.agent.JAXWSGenericClientAgent  (for clients) into the handler chain.

Metro:
Within Jboss and the use of JbossWS, use the JbossWS handler, otherwise use the JAXWS Generic Agent classpath within the sun-jaxws.xml config file.

The correct classpath is 
Service: org.miloss.fgsms.agents.JAXWSGenericAgent
Client: org.miloss.fgsms.agents.JAXWSGenericClientAgent
The following is an example taken from jUDDI. The parts in bold font are the changes.

```
<bean id="FGSM" class="org.miloss.fgsms.agents.JAXWSGenericAgent" />
  <jaxws:endpoint id="inquiry" implementor="org.apache.juddi.api.impl.UDDIInquiryImpl" address="/inquiry">
    <jaxws:properties>
      <entry key="schema-validation-enabled" value="true"/>
    </jaxws:properties>
    <jaxws:handlers>
      <ref bean="FGSM" />
    </jaxws:handlers>
  </jaxws:endpoint>
```

## Apache CXF Interceptors (FGSMS.ApacheCXFAgent.jar)
Edit beans.xml or cxf.xml and modify as necessary. 
The correct classpaths are

 * org.miloss.fgsms.agents.CXFInterceptorOutClient
 * org.miloss.fgsms.agents.CXFInterceptorInClient
 * org.miloss.fgsms.agents.CXFInterceptorOutService
 * org.miloss.fgsms.agents.CXFInterceptorInService


## JbossESB (FGSMS.JbossESBAgent.jar)
JbossESB allowed you to offer a while range of protocols and functionality within the ESB via a series of Action Pipeline Processors. These are defined within each .esb file within the jboss-esb.xml file. 

The JbossESB FGSMS Action classes will enable the monitoring of a SOAP messages (and other stuff) that was sent to an ESB endpoint by inserting ActionPipeLineProcessor classes into the action chain. This can also be used to gauge the performance pretty much any ESB deployment, so long as a unique URL can be determined for that specific ESB deployment. 

Copy the previously mention files into the `server/default/lib` folder along with FGSMS.JbossESBAgent.jar 

Edit the `jboss-esb.xml` file within the ESB deployment to be monitored. Unfortunately this has to done on a per deployment basis and can't be deployed globally.


Insert the following before and after the proxied service is executed

```
	<action name="FGSMBefore"  
class="org.miloss.fgsms.agents.JbossESBProxyAction">
		<property name="http_port" value="8080" />
		<property name="https_port" value="7443" />
	</action>
	<!-- do something here 
	<action name="proxy"> snip </action>
	-->
<!--
insert the following after the proxied service is executed.	-->
	<action name="FGSMAfter"  class="org.miloss.fgsms.agents.JbossESBProxyAfterAction">
		<property name="http_port" value="8080" />
		<property name="https_port" value="7443" />
	</action>
```	
	
The action attribute `name` is arbitrary but must be unique to each ESB deployment
Fill out the property `http(s)_port` with ports that match the listening ports of your JbossESB container. JbossESB unfortunately doesn't offer a way to get the port information of the original inbound request, so we esscentially fabricate one on the fly. This is required so that A) The Bueller process can ping your endpoint and B) that the FGSMS web interface correctly displays the location of this deployment.

Restart Jboss and test. Upon the execution of something in JbossESB, logging output should be visible in the Jboss server.log file and once the service has been executed at least once, the endpoint should be listed within FGSM’s Web GUI.

## Monitoring Servlet Based Services (FGSMS.ServletFilterAgent.jar)
In certain scenarios, web services can be implemented without using JAXWS and thus they cannot be monitored using web service handlers. If the service is implemented as a J2EE Servlet (3.0 spec or higher), then the FGSMS Servlet Filter can be used. 

This is deployed by:
Add the standard agent jar files along with FGSMS.ServletFilterAgent.jar within the classpath of the item that you wish to monitor.
Modify the web.xml of the web application you wish to monitor. Use the following example to configure.

```
<filter>
 <filter-name>FGSMFilter</filter-name>
 <filter-class>org.miloss.fgsms.agents.HttpServletAgent</filter-class>
</filter>
<filter-mapping>
 <filter-name>FGSMFilter</filter-name>
 <url-pattern>/hw</url-pattern>
 <dispatcher>REQUEST</dispatcher>
 <dispatcher>FORWARD</dispatcher>
 <dispatcher>INCLUDE</dispatcher>
 <dispatcher>ERROR</dispatcher>
</filter-mapping>
<servlet>
 <servlet-name>MyWebService</servlet-name>
 <servlet-class>namespace.webservice.no2.HelloWorldPortImpl</servlet-class>
</servlet>
<servlet-mapping>
 <servlet-name> MyWebService </servlet-name>
 <url-pattern>/hw</url-pattern>
</servlet-mapping>
```

The highlighted portions are used to trigger the filter when a request comes into the servlet “MyWebService”.

There are limitations of the Servlet Filter Agent, primarily, it cannot record request and response messages.

## Monitoring JAX-WS EJB Services with JbossWS 3.4 or higher

JbossWS 3.4 or higher does not come with any predefined configuration for clients or services that are found in older versions as `“Standard-jaxws-client-config.xml”` and `“standard-jaxws-endpoint-config.xml”`.  In older versions, these can be located within the path /server/default/deploy/jbossws.sar/META-INF file and can be used to set globally included handlers.  You can copy those files into the above mentioned path.

For JbossWS 3.4, these files can also be created (a sample is included within the deployment package (in the \xml folder) for FGSM) in `/server/default/deployers/jbossws.deployer/META-INF`

In case this doesn’t work, the following are some alternative methods to monitor services.

## JbossWS-Native
Within the service to be monitored deployment file (usually a .jar or .war file) there typically is a META-INF folder. If it does not exist, create it and then create a new file within it called standard-jaxws-endpoint-config.xml with the following text.

```
<?xml version="1.0" encoding="UTF-8"?>
<jaxws-config xmlns="urn:jboss:jaxws-config:2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:javaee="http://java.sun.com/xml/ns/javaee"
  xsi:schemaLocation="urn:jboss:jaxws-config:2.0 schema/jaxws-config_2_0.xsd">
    <endpoint-config>
    <config-name>Standard Endpoint</config-name>
    <pre-handler-chains>
      <javaee:handler-chain>
        <javaee:protocol-bindings>##SOAP11_HTTP</javaee:protocol-bindings>
  		<javaee:handler>
          <javaee:handler-name>FGSMS Agent JbossWSMonitor</javaee:handler-name>
          <javaee:handler-class>org.miloss.fgsms.agents.JbossWSMonitor</javaee:handler-class>
        </javaee:handler>
      </javaee:handler-chain>
  </pre-handler-chains>
  </endpoint-config>
  </jaxws-config>
```

## JbossWS-CXF
Within the service to be monitored deployment file (usually a .jar or .war file) there typically is a META-INF folder. If it does not exist, create it and then create a new file within it called `jbossws-cxf.xml`. Use the following text as a template. The highlighted portions must be updated to match your operational needs.


```
<?xml version="1.0" encoding="UTF-8"?>
<beans
  xmlns='http://www.springframework.org/schema/beans'
  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
  xmlns:beans='http://www.springframework.org/schema/beans'
  xmlns:jaxws='http://cxf.apache.org/jaxws'
  xsi:schemaLocation='http://www.springframework.org/schema/beans
  http://www.springframework.org/schema/beans/spring-beans.xsd
  http://cxf.apache.org/jaxws
  http://cxf.apache.org/schemas/jaxws.xsd'>

    <bean id="FGSMhandler" class="org.miloss.fgsms.agents.JbossWSMonitor" />
  <!-- one or more jaxws:endpoint EJB3 declarations -->
    <jaxws:endpoint
    id='DAS'
address="http://localhost:8280/DAS/dataAccessService/DAS"
    implementor='org.miloss.fgsms.services.DAS4j.DAS4jBean'>
        <jaxws:handlers>
            <ref bean="FGSMhandler"/>
        </jaxws:handlers>
        <jaxws:invoker>
            <bean class='org.jboss.wsf.stack.cxf.InvokerEJB3'/>
        </jaxws:invoker>
    </jaxws:endpoint>
</beans>
```

# .NET Web Service Agents

## WCF Client and Service Agent
Run the included FGSMS.NETInstaller.exe from the distribution package and follow the included instructions. This will setup your machine to monitor all .NET ASP.NET and WCF based capabilities. 

```
FGSMS.NETInstaller.exe installWebServiceAgentsLibraries
FGSMS.NETInstaller.exe configure 
FGSMS.NETInstaller.exe applyConfiguration
FGSMS.NETInstaller.exe addWcfExtensions
FGSMS.NETInstaller.exe enableWcfServiceMonitor
FGSMS.NETInstaller.exe enableWcfClientMonitor
```

The following table provides descriptions for all of the possible settings.

This section is for .NET based FGSMS Web Service (Transactional) Agents only.

| Property | Values | Description
| --- | --- | ---
| FGSMS.DCS.URL | url1&#124;url2&#124; |  Execution URL to an instance of FGSMS DCS Service. Multiple values delimited by &#124;
| FGSMS.PCS.URL |  url1&#124;url2&#124; |  Execution URL to an instance of FGSMS PCS Service. Multiple values delimited by &#124;
| FGSMS.SS.URL |  url1&#124;url2&#124; | Execution URL to an instance of FGSMS SS Service. Multiple values delimited by &#124;
| FGSMS.IgnoreList | string&#124;string&#124;  | delimited URLs to be ignored
| FGSMS.message.processor.dead.message.queue.duration |  >=10000 |  time in milliseconds
| FGSMS.AgentAuthMode | None, usernamePassword, PKI  |
| FGSMS.AgentAuthModeUsername     | string 
| FGSMS.AgentAuthModePassword   |  Encrypted password
| FGSMS.agent.dependencyinjection.enabled |  true/false |  Enables/Disables the insertion of headers into messages to track and correlate service chaining events.
| FGSMS.agent.unavailablebehavior |  PURGE, HOLD, HOLDPERSIST |  PURGE= purge the queues and drop all messages until PCS/DCS is available, then resume normal operations
| FGSMS.agent.persistlocation  |   Path to a writeable folder
| FGSMS.policyconfigurationservice.algorithm | FALLBACK, ROUNDROBIN |  Fallback = try the URLs in order until one works. Roundrobin = iteratively loop through the list, distributing the load evenly. If one fails, try the next one until we succeed.
| FGSMS.policyconfigurationservice.retry | number >= 0  
| FGSMS.datacollectorservice.algorithm | FALLBACK, ROUNDROBIN |  Fallback = try the URLs in order until one works. Roundrobin = iteratively loop through the list, distributing the load evenly. If one fails, try the next one until we succeed.
| FGSMS.datacollectorservice.retry |  number >= 0  
| FGSMS.discovery.interval | 30000 |   if enabled, a list of URLs will be periodically loaded and dynamically ADDED to the list of above for each service
| FGSMS.discovery.uddi.enabled | true/false  
| FGSMS.discovery.uddi.inquiry.url |   Execution URL of UDDI Inquiry
| FGSMS.discovery.uddi.inquiry.authrequired |  true/false  
| FGSMS.discovery.uddi.security.url   |  Execution URL of Security Inquiry
| FGSMS.discovery.uddi.security.username   | string
| FGSMS.discovery.uddi.security.password  |  Encrypted password
| FGSMS.discovery.uddi.lookup.findType | EndpointBindingKey, EndpointKeyWord, ServiceEntityKey | EndpointBindingKey, loads from a BindingTemplate Key, this must be the unique identifier for the binding template containing 0 or more endpoints. EndpointKeyWord, attempts to find services by name, aka the display name. ServiceEntityKey loads from a BusinessService key, this must be the unique identifier for the business service containing 0 or more binding templates with 0 or more endpoints.
| FGSMS.discovery.uddi.lookup.dcs |   string |  Based on the findType, the value that we are looking for, see above.
| FGSMS.discovery.uddi.lookup.pcs  | string |    Based on the findType, the value that we are looking for, see above.




__Customization Note:__ In certain scenarios, some products or capabilities are very chatty, meaning that they execute or poll a web service very frequently. This can cause additional traffic, bandwidth and latency within your network. If you encounter this, add the offending service’s URL to the IgnoreList (.NET only).

## Monitoring a specific WCF service or WCF Client
Perform the following commands using the FGSMS .NET Installer

```
FGSMS.NETInstaller.exe installWebServiceAgentsLibraries
FGSMS.NETInstaller.exe configure 
FGSMS.NETInstaller.exe applyConfiguration
FGSMS.NETInstaller.exe addWcfExtensions
```

Locate and merge the following settings. Consult the .NET configuration information at Microsoft’s MSDN web site for further guidance.


```
<system.serviceModel>
  <behaviors>
    <endpointBehaviors>			<!-- for calling a service -->
<behavior name="clientbehavior1">
		<FGSMS.ClientAgent/>
</behavior>
    </endpointBehaviors>
    <serviceBehaviors>
<behavior name="servicebehavior1">
		<FGSMS.ServiceAgent/>		<!--  for when this service is called -->
</behavior>
    </serviceBehaviors>
  </behaviors >
```

This service should now be monitored.

## ASP.NET Agents

To monitor all ASP.NET services and clients on a given machine
Use the FGSMS.NET Installer

```
FGSMS.NETInstaller.exe installWebServiceAgentsLibraries
FGSMS.NETInstaller.exe configure 
FGSMS.NETInstaller.exe applyConfiguration
FGSMS.NETInstaller.exe enableASPMonitor
```

To monitor a specific ASP.NET service or client on a given machine.

```
FGSMS.NETInstaller.exe installWebServiceAgentsLibraries
FGSMS.NETInstaller.exe configure 
FGSMS.NETInstaller.exe applyConfiguration
```

Modify the app.config or web.config file.

Locate the following section, it may not exist and you may have to add it.

```
<system.web>
  <webServices>
   <soapExtensionTypes>
Add the following item:
     <add type="FGSMS.Agent.AgentSoapExtension, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken= e81ab410f7d48538" priority="1" group="High"/>
   </soapExtensionTypes>
   </webServices>
```

## Securing Connection Details
Use the included utility “FGSMS.Util.exe” to generate encrypted text of passwords and replace the generated values with those inside of the machine.config files

This uses AES encryption using a 256-bit key.

## .NET Persistent Storage Agent
The Persistent storage agent is a daemon process that will periodically check a directory for FGSMS agent data. Data is typically only written there when an agent cannot reach the FGSMS server AND it is configured for HOLDPERSIST mode. The Persistent Storage Agent is only required for use with that configuration. Install it using the FGSMS.NET Installer. 

```
FGSMS.NETInstaller.exe installWebServiceAgentsLibraries
FGSMS.NETInstaller.exe configure 
FGSMS.NETInstaller.exe applyConfiguration
FGSMS.NETInstaller.exe installPersistentWebServiceAgent
```

# Operating System Agents
FGSMS includes some extra agents that are used exclusively for monitoring the status of machines or process.

See the distribution zip\agents\os
Before installing or running, modify, if necessary the FGSMS.AgentCore.jar file to update URLs, usernames and passwords.

For Windows users, simply click on OSAgentServiceInstall.cmd and it will install and start up right away. OSAgentServiceInstallWithDEBUG.cmd may also be used for verbose logging into the same directory that the agent is located in.

# Statistical Agents (Message Brokers)
## Qpid/MRG AMQP Message Broker (Via Python Script)

Dependencies:

 * Access to the FGSMS Web Services Access to the locally hosted AMQP Qpid/MRG broker C++ Python

__Information Assurance Note:__ The AMQP Message Broker agent for QPID/MRG uses a python script. Both the Java process that runs the agent and the python script must have sufficient user privileges on the system to execute.

Before installing or running, modify, if necessary the FGSMS.AgentCore.jar file to update URLs, usernames and passwords.

Execution instructions:
`java –jar FGSMS.QpidPyAgent.jar`

See the included README.txt for more information.


# All Other Agents
All other agents run on the FGSMS server and connect remotely to the component or capability. These agents are configured via the Web GUI at http://localhost:8888/fgsmsBootstrap.




# Using Active Directory to remotely install FGSMS Agents

Microsoft’s Active Directory can be used to install the following types of FGSMS Agents:

 * .NET Web Service Agents (WCF, ASP.NET)
 * .NET Web Service Persistent Storage
 * Java Web Service Persistent Storage
 * OS Agent

Note: Java Web Service agents cannot be deployed using this mechanism.

This is a multistep procedure that will use a Group Policy Object (GPO) to create a Software Installation Policy that will deploy the above agents. Additional steps are required.  Do not just install the MSI file without reading the directions first.

## Overview of the installation

1. Create an OU
2. Create a GPO
3. Define the Software Installation Policy
4. Define/customize a Windows Start Up Script to push the configuration
5. Add computers to the OU
6. Restart the computer (or use some other means to force the script to run)

### Create an Organizational Unit

This step will create an Active Directory Organizational Unit to use a logical container for machines that you want to monitor. All machines within the OU will have the agents remotely installed to it.

TODO Pictures

### Create a Group Policy Object

This step will create and assign a GPO that affects computers in the Organization Unit

### Define the Software Installation Policy

This will cause Windows to force the install of FGSM’s agents at start up time.

Navigate to Computer Configuration > Software Installation

### Define/customize a Windows Start Up Script to push the configuration

Navigate to Computer Configuration > Windows Settings > Scripts (Startup/Shutdown)

Click Add.
Name it “FGSMS configuration push”. This is arbitrary. 
Select the script from the FGSMS distribution package in
  /scripts/ updateConfigStartUpScript.bat

This file needs to be modified to match your environment. See inline documentation within the batch file for details.

### Add Computers to the Organization Unit

The next step is to add computers to the Organization Unit. This can be done by dragging computer’s into the new OU.

### Restart the computer(s)  

The machine now needs to be restarted. This can be performed in mass using the following command from windows.
Start > Run > shutdown /i
Administrative Credentials are required.

### Disabling/Removing FGSMS Agents

There are a few different options
Go to each machine that FGSMS was installed to, open Add/Remove Programs and uninstall FGSM.
Remove the Software Installation Policy from Active Directory, selecting Remove Software when prompted.
Disable FGSMS Agents using a script, such as net stop `<service name>`
Manual Uninstall. Use this when all else fails
