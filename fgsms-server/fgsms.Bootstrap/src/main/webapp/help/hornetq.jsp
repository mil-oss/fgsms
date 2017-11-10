<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Monitoring Jboss HornetQ</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <ul>
            <li>A JMX URL to the server hosting HornetQ</li>
            <li>Optionally, a username and password</li>    
        </ul>
        To configure, go to Administration > General Settings. Once the URLs have been set, return to the "My Services" page, find the URL to the broker, then click on Manager.
        From the Policy Editor, click on Status Monitoring, then Set Credentials to set the username and password.<br><Br>
        In case you built your own Jboss server for FGSMS, use the following procedure.<br>
        This walkthrough will guide you to setup a JMS topic on the FGSMS server and to configure the server to publish alerts when various components go up or down or trigger any of the defined SLA rules with JMS Alert actions defined.<br><br>
        <ol><li>Create the topic (queues are also supported)
                <ul>
                    <li>Jboss 4 Only: Edit the file at server\default\deploy\jboss-messaging.sar\messaging-service.xml
                        <pre>
&lt;mbean code=&quot;org.jboss.mq.server.jmx.Topic&quot; name=&quot;jboss.mq.destination:service=Queue,name=FGSMSAlerts&quot;&gt;
    &lt;depends optional-attribute-name=&quot;DestinationManager&quot;&gt;jboss.mq:service=DestinationManager&lt;/depends&gt;
&lt;/mbean&gt;
                        </pre></li>
                    <li>Jboss 5 Only: Edit the file at server\default\deploy\messaging\messaging-service.xml
                        <pre>
&lt;mbean code=&quot;org.jboss.jms.server.destination.TopicService&quot;
 name=&quot;jboss.messaging.destination:service=Topic,name=FGSMSAlerts&quot;
 xmbean-dd=&quot;xmdesc/Topic-xmbean.xml&quot;&gt;
    &lt;depends optional-attribute-name=&quot;ServerPeer&quot;&gt;jboss.messaging:service=ServerPeer&lt;/depends&gt;
    &lt;depends&gt;jboss.messaging:service=PostOffice&lt;/depends&gt;
&lt;/mbean&gt;   
                        </pre>

                    </li>
                    <li>Jboss 6 Only: Edit the file at server\default\deploy\hornetq\hornetq-jms.xml and add the following text.
                        <pre>
    &lt;topic name=&quot;FGSMSAlerts&quot;&gt;
        &lt;entry name=&quot;topic/FGSMSAlerts&quot; /&gt;
    &lt;/topic&gt;
                        </pre>
                    </li>
                </ul>
            </li>
            <li>Add to the General Settings list the following JMSAlert parameters listed there. In most cases the default values in the table will work.

            </li>
            <li>Test by creating or editing an SLA rule and action set to include an SLA Action for JMS Alerts. Then perform some action to trigger the rule. Forcing a rule to trigger will 
                be dependent on the type of rule and the type of policy that represents whatever is being monitored. For example, if the rule is change in status, stop the service, 
                then confirm that a JMS alert was sent either via the FGSMS Jboss server logs for via a JMS subscriber such as jMeter.</li>
        </ol>


        <a href="http://www.mastertheboss.com/jboss-application-server/287-jboss-jms.html">Source</a>

    </div><!--/span-->
</div><!--/row-->
