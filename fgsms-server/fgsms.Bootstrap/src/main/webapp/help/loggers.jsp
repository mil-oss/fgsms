<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Log Based Alerting</h1>
</div>
<div class="row-fluid">
    <div class="span12">
         FGSMS uses Log4j as a logging library. Log4j offers a wide array of functions and capabilities, including sending logging messages to a variety of outputs, including UPD log servers, Syslog, Windows Event Logs and more. <br><Br>
            FGSMS SLA's have specialized logging actions which can send messages for any SLA rule set to a specific logger. The catch is, you need to configure the logger within the FGSMS server to appropriately direct the message where it needs to go. 
            Since deployment options are flexible with FGSMS, you'll need to know where the SLA Processor runs.<br><br>

            This task requires an administrator to configure the logging settings in the Jboss container hosting FGSMS's web services and on the server hosting FGSMS's auxiliary services. The correct file varies with the version of Jboss.<br><Br>
            Jboss 4.x  - Edit server\default\conf\jboss-log4j.xml<br>
            Jboss 5.x  - Edit server\default\conf\jboss-log4j.xml<br>
            Jboss 6.x - Edit server\default\deploy\jboss-logging.xml<br><br>

            <h4>Configuring UPD Log Appender</h4>
            FGSMS.SLAProcessor.UdpLog
            <h4>Configuring Syslog Appender</h4>
            FGSMS.SLAProcessor.SysLog<br>
            http://syslog4j.org/<br><br>
            Copy the syslog4j jar file into the server/default/lib folder. Add the following to the log4j config file<Br>
            <pre>
    &lt;!-- Send SLA triggered to Syslog --&gt;
   &lt;appender name="FGSMSSYSLOG" class="org.productivity.java.syslog4j.impl.log4j.Syslog4jAppender"&gt;
     &lt;errorHandler class="org.jboss.logging.util.OnlyOnceErrorHandler"/&gt;
     &lt;param name="Facility" value="LOCAL7"/&gt;

     &lt;param name="SyslogHost" value="SYSLOGSERVER"/&gt; &lt;!-- update this with your TCP or UDP Syslog server --&gt;
     &lt;param name="Port" value="514"/&gt;
     &lt;param name="Protocol" value="tcp"/&gt;
     &lt;layout class="org.apache.log4j.PatternLayout"&gt;
       &lt;param name="ConversionPattern" value="[%d{ABSOLUTE},%c{1}] %m%n"/&gt;
     &lt;/layout&gt;
   &lt;/appender&gt;
    &lt;!-- route to a log file --&gt;
   &lt;category name="FGSMS.SLAProcessor.SysLog"&gt;
     &lt;priority value="INFO" /&gt;
     &lt;appender-ref ref="FGSMSSYSLOG"/&gt;
   &lt;/category&gt;
            </pre>
            <h4>Configuring File Appender</h4>
            FGSMS.SLAProcessor.FileLog<br><pre>
&lt;!-- A time/date based rolling appender for FGSMS SLA File Logger triggers --&gt;
   &lt;appender name="FGSMSSLA" class="org.jboss.logging.appender.DailyRollingFileAppender"&gt;
      &lt;errorHandler class="org.jboss.logging.util.OnlyOnceErrorHandler"/&gt;
      &lt;param name="File" value="${jboss.server.log.dir}/fgsmsSLA.log"/&gt;
      &lt;param name="Append" value="true"/&gt;
      &lt;!-- Rollover at midnight each day --&gt;
      &lt;param name="DatePattern" value="'.'yyyy-MM-dd"/&gt;
      &lt;layout class="org.apache.log4j.PatternLayout"&gt;
         &lt;param name="ConversionPattern" value="%d %-5p [%c] (%t) %m%n"/&gt;
      &lt;/layout&gt;
   &lt;/appender&gt;
  &lt;!-- route to a log file --&gt;
   &lt;category name="FGSMS.SLAProcessor.FileLog"&gt;
     &lt;priority value="INFO" /&gt;
     &lt;appender-ref ref="FGSMSSLA"/&gt;
   &lt;/category&gt;
            </pre>
            <h4>Configuring Windows Event Log Appender</h4>

            Place NTEventLogAppender.dll, NTEventLogAppender.amd64.dll, NTEventLogAppender.ia64.dll or NTEventLogAppender.x86.dll as appropriate in a directory that is on the PATH of the Windows system. 
            Copy NTEventLogAppender.dll to c:\windows. NTEventLogAppender.dll is a part of the log4j package and can also be found in the FGSMS distribution under the folder "log4j-eventlogs"<br>
            Add the following to the log4j config file<Br>
            <pre>
  &lt;!-- Send SLA triggered events to the Windows Event Logger --&gt;
   &lt;appender name="WINDOWS" class="org.apache.log4j.nt.NTEventLogAppender"&gt;
         &lt;errorHandler class="org.jboss.logging.util.OnlyOnceErrorHandler"/&gt;
         &lt;param name="source" value="FGSMS Alerts"/&gt;
   
         &lt;layout class="org.apache.log4j.PatternLayout"&gt;
            &lt;param name="ConversionPattern" value="%-5p %d{ISO8601} - %m%n"/&gt;
         &lt;/layout&gt;
   &lt;/appender&gt;
   &lt;!-- route to the Windows Event log --&gt;
   &lt;category name="FGSMS.SLAProcessor.EventLog"&gt;
     &lt;priority value="INFO" /&gt;
     &lt;appender-ref ref="WINDOWS"/&gt;
   &lt;/category&gt;
            </pre>
        
    </div><!--/span-->
</div><!--/row-->
