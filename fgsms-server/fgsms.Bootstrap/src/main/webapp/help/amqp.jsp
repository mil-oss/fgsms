<%@include file="../csrf.jsp" %>
<% String parent = "help/index.jsp";%>
<div class="well">
    <h1>AMQP</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <p>FGSMS provides integration with Redhat MRG and Apache Qpid by both sending alerts through it and by monitoring queue and topic statistics. AMQP is a publish and subscribe protocol. 
        </p>
        <p>In order to send alerts through AMQP, FGSMS must know a few parameters about your Qpid/MRG installation, such as the location of the service, credentials, and authentication mode.
            In addition, one or more topics should be created for sending alerts. All alerts can be sent to a default topic and optionally, on a per service basis, routed to additional topics.
        </p>
      
        <h2 class="well-small">Configuring Qpid/MGR Monitoring (Java Implementation Only)</h2>
        <p>   You'll need the following:
        <ul>
            <li>A JMX URL to the server hosting Apache Qpid</li>
            <li>Optionally, a username and password</li>    
        </ul>
        To configure, go to Administration > General Settings and set all of the required settings for Apache Qpid monitoring. Once the URLs have been set, return to the "My Services" page, find the URL to the broker, then click on Manage.
        From the Policy Editor, click on Status Monitoring, then Set Credentials to set the username and password.
        </p>
        
        <h2 class="well-small">Configuring Qpid/MRG Monitoring (C++ Implementation Only)</h2>
        <p>To monitor this component, you need to deploy an agent on the same computer as the Qpid/MGR server. This agent will periodically report back data to the FGSMS server.
        Download the FGSMS distribution and unzip it. Look at the agents/statistical folder for AMQP Py. The FGSMS agent for Redhat MRG (and also Apache Qpid C++) using a Python script to access statistics from the broker. See the accompanying readme for details.
        </p>
    </div><!--/span-->
</div><!--/row-->
