## App Settings


````
    <appSettings>
    <add key="org.miloss.fgsms.DCS.URL" value="http://localhost:8888/fgsmsServices/services/DCS"/>
    <add key="org.miloss.fgsms.PCS.URL" value="http://localhost:8888/fgsmsServices/services/PCS"/>
    <add key="org.miloss.fgsms.SS.URL" value="http://localhost:8888/fgsmsServices/services/SS"/>

    <!-- can be INFO, DEBUG, WARN, ERROR-->
    <add key="org.miloss.fgsms.loglevel" value="WARN"/>
    
    <add key="org.miloss.fgsms.IgnoreList" value="?"/>
    <!-- can be ONLINE or OFFLINE
    OFFLINE = all transactions are stored to disk encrypted. These can be sent back later using the persistent storage agent
    ONLINE = normal operation
    -->
    <add key="org.miloss.fgsms.agent.operatingmode" value="ONLINE"/>
    <add key="org.miloss.fgsms.message.processor.dead.message.queue.duration" value="60000"/>
    <!-- can be None usernamePassword, or PKI -->
    <add key="org.miloss.fgsms.agent.AuthMode" value="usernamePassword"/>

    <!-- search machine store, personal/my for serial number, must have private key, key must be accessible by the current context-->
    <add key="org.miloss.fgsms.agent.AuthModePKIInfo" value="0414"/>
    <add key="org.miloss.fgsms.agent.AuthModeUsername" value="fgsmsagent"/>
    <add key="org.miloss.fgsms.agent.AuthModePassword" value="bZPq3oiQev2VZmzeE5W3aA=="/>
    <add key="org.miloss.fgsms.agent.dependencyinjection.enabled" value="true"/>
    <!-- can be HOLD = Hold the data in memory,
                PURGE = throw it away
                HOLDPERSIST = encrypt and store it to disk
                -->
    <add key="org.miloss.fgsms.agent.unavailablebehavior" value="HOLDPERSIST"/>
    <!-- everyone should be able to write to this folder since this agent can run under any user prinicple-->
    <add key="org.miloss.fgsms.agent.persistlocation" value="c:\temp\fgsms"/>
    <add key="org.miloss.fgsms.agent.policyconfigurationservice.algorithm" value="FAILOVER"/>
    <add key="org.miloss.fgsms.agent.policyconfigurationservice.retry" value="1"/>
    <add key="org.miloss.fgsms.agent.datacollectorservice.algorithm" value="FAILOVER"/>
    <add key="org.miloss.fgsms.agent.datacollectorservice.retry" value="1"/>

    <add key="org.miloss.fgsms.discovery.interval" value="60000"/>
    <add key="org.miloss.fgsms.discovery.dns.enabled" value="false"/>
    <add key="org.miloss.fgsms.discovery.uddi.enabled" value="false"/>
    <add key="org.miloss.fgsms.discovery.uddi.inquiry.url" value="http://localhost:8080/juddiv3/services/inquiry"/>
    <add key="org.miloss.fgsms.discovery.uddi.inquiry.authrequired" value="false"/>
    <add key="org.miloss.fgsms.discovery.uddi.security.url" value="http://localhost:8080/juddiv3/services/security"/>
    <add key="org.miloss.fgsms.discovery.uddi.security.username" value="root"/>
    <add key="org.miloss.fgsms.discovery.uddi.security.password" value="BxM/31xapyWiYvr1RxR0bA=="/>
    <!-- options
    EndpointBindingKey, loads from a BindingTemplate Key, this must be the unique identifier for the binding template containing 0 or more endpoints
    EndpointKeyWord, attempts to find services by name, aka the display name
    ServiceEntityKey loads from a BusinessService key, this must be the unique identifier for the business service containing 0 or more binding templates with 0 or more endpoints
    -->
    <add key="org.miloss.fgsms.discovery.uddi.lookup.findType" value="EndpointKeyWord"/>
    <add key="org.miloss.fgsms.discovery.uddi.lookup.dcs" value="uddi:juddi.apache.org:6843d553-b512-4a6b-9632-9075392b95a2"/>
    <add key="org.miloss.fgsms.discovery.uddi.lookup.pcs" value="fgsms.PCS"/>


  </appSettings>
````

## Monitoring WCF Services
  
Locate the following section:
````
  <system.serviceModel>
    <extensions>
      <behaviorExtensions>
````
And add the following element:

`````
<add name="FGSMS.Agent" type="org.miloss.fgsms.agent.wcf.AgentWCFServiceBehaviorElement, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken=eb2f31c22dc30917"/>
````

Locate the following section:

````
  <system.serviceModel>
    <extensions>
      <endpointExtensions>
````

And add the following element:

````
    <add name="FGSMS.ClientAgent" type="org.miloss.fgsms.agent.wcf.AgentWCFClientEndpointBehaviorElement, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken=eb2f31c22dc30917"/>
````

Locate the following section:

````
<system.serviceModel>
  <commonBehaviors>
    <endpointBehaviors>
	<FGSMS.ClientAgent/>
    </endpointBehaviors>
    <serviceBehaviors>
	<FGSMS.Agent/>
    </serviceBehaviors>
  </commonBehaviors>
````

All .NET WCF based services and clients should now be monitored.


##	Monitoring a specific service

Locate the following section:

````
  <system.serviceModel>
    <extensions>
      <behaviorExtensions>
````

And add the following element:

````
<add name="FGSMS.Agent" type="org.miloss.fgsms.agent.wcf.AgentWCFServiceBehaviorElement, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken=eb2f31c22dc30917"/>
````

Locate the following section:

````
  <system.serviceModel>
    <extensions>
      <endpointExtensions>
````

And add the following element:

````
    <add name="FGSMS.ClientAgent" type="org.miloss.fgsms.agent.wcf.AgentWCFClientEndpointBehaviorElement, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken=eb2f31c22dc30917"/>
````

Locate and merge the following settings. Consult the .NET configuration information at Microsoft’s MSDN web site for further guidance.

````
<system.serviceModel>
  <behaviors>
    <endpointBehaviors>			<!-- for calling a service -->
<behavior name="clientbehavior1">
		<FGSMS.ClientAgent/>
</behavior>

    </endpointBehaviors>
    <serviceBehaviors>
<behavior name="servicebehavior1">
		<FGSMS.Agent/>		<!--  for when this service is called -->
</behavior>
    </serviceBehaviors>
  </commonBehaviors>
````

Locate the following section:

````
  <system.serviceModel>
    <extensions>
      <behaviorExtensions>
````

And add the following elements:

````
<add name="FGSMS.Agent" type="FGSMS.Agents.AgentWCF.AgentWCFServiceBehaviorElement, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken=eb2f31c22dc30917"/>
<add name="FGSMS.ClientAgent" type="FGSMS.Agents.AgentWCF.AgentWCFClientEndpointBehaviorElement, FGSMS.NETAgent, Version=1.0.0.0, Culture=neutral, PublicKeyToken=eb2f31c22dc30917"/>

````

This service should now be monitored.

## .NET Agents

Copy (by drag and drop) the file named FGSMS.NETAgent.dll into the %system root%\assembly folder, usually c:\windows\assembly or using the gacutil

### To monitor all ASP.NET services and clients on a given machine


Modify the machine.config files located in 

- %systemroot%\Microsoft.NET\Framework\v2.0.xxx\CONFIG\machine.config
- %systemroot%\Microsoft.NET\Framework\v4.0.xxx\Config\machine.confg
- %systemroot%\Microsoft.NET\Framework64\v2.0.xxx\CONFIG\machine.config
- %systemroot%\Microsoft.NET\Framework64\v4.0.xxx\Config\machine.confg


Locate the following section, it may not exist and you may have to add it.

````
<system.web>
  <webServices>
   <soapExtensionTypes>
````

Add the following item:

````
     <add type="FGSMS.Agents.AgentSoapExtension, FGSMS.Agents, Version=1.0.0.0, Culture=neutral, PublicKeyToken= eb2f31c22dc30917" priority="1" group="High"/>
   </soapExtensionTypes>
   </webServices>
````

All .NET ASP.NET based services should now be monitored.

### To monitor a specific ASP.NET service or client on a given machine.

Modify the app.config or web.config file.

Locate the following section, it may not exist and you may have to add it.

````
<system.web>
  <webServices>
   <soapExtensionTypes>
````

Add the following item:

````
     <add type="FGSMS.Agents.AgentSoapExtension, FGSMS.Agents, Version=1.0.0.0, Culture=neutral, PublicKeyToken="eb2f31c22dc30917" priority="1" group="High"/>
   </soapExtensionTypes>
   </webServices>
````
