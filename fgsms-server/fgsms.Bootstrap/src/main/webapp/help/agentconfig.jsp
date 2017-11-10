<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Agent Configuration</h1>
    <p>Configuring embedded FGSMS's agents (web services, operating system agents, and Redhat MRG.</p>
    <p><a class="btn btn-primary btn-large">Learn more &raquo;</a></p>
</div>
<div class="row-fluid">
    <div class="span12">
        <h2>Java based Agents</h2>
        <h3 class="well-small">Quick Overview</h3>
        <p>
            The majority of FGSMS's agents are based on Java. This document describes the basics of configuring agents for FGSMS's embedded web service agents, operating system agents, and the agent for Redhat MRG.
        </p>
        <h3 class="well-small">Where is the config file?</h3>
        <p>
            Java based agents use configuration data in the file named "fgsms-agent.properties". This properties file contains credentials, URLs and performance tuning settings to enable the agent to do its job efficiently and reliably. 
            By default, a version is included with the agent located within FGSMS.AgentCore.jar. All of the agents can also use an external by using the Java option -Dorg.miloss.fgsms.agentConfigFileOverride=path/to/config.properties or include the file fgsms-agent.properties in the classpath for the agent.
        </p>
        <h3 class="well-small">How can I integrate the agent with my service? and where can I find more information?</h3>
        <p>
            For best results, consult the documentation provided with FGSMS, such as the Installation Guide. It has some specific examples for integrating with COTS and GOTS software.
        </p>
    </div><!--/span-->
</div><!--/row-->
<div class="row-fluid">
    <div class="span12">
           <h3 class="well-small">Quick Overview</h3>
        <p>
            FGSMS's .NET agents provide monitoring for ASP.NET and WCF agents. They are both web service frameworks included with .NET.
            
            
        </p>
        <h3 class="well-small">Where is the config file?</h3>
        <p>
            The .NET agents get configuraiton information from the machine.config file. This contains credentials, URLs and performance tuning settings to enable the agent to do its job efficiently and reliably. 
            For similar and easier deployment, modify the current.config file located where the agents were installed (default is C:\Program Files (x86)\U.S. Army\FGSMS.NET Agents\), then use the configuration utility to apply the changes.
            These changes can be scripted using Active Directory for bulk deployments.
        </p>
        
          <h3 class="well-small">How can I integrate the agent with my service? and where can I find more information?</h3>
        <p>
            For best results, consult the documentation provided with FGSMS, such as the Installation Guide. 
        </p>
    </div><!--/span-->
</div>
