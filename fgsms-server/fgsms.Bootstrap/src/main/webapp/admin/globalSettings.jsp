<%-- 
    Document   : globalsettings
    Created on : Jan 13, 2012, 9:06:32 PM
    Author     : Administrator
--%>

<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.miloss.fgsms.common.Constants"%>


<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>

<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@include file="../csrf.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% 
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    PCS pcsport = pl.GetPCS(application, request, response);

    if (request.getMethod().equalsIgnoreCase("post")) {
        try {
            Enumeration enumera = request.getParameterNames();
            while (enumera.hasMoreElements()) {
                String s = (String) enumera.nextElement();
                if (s.startsWith("removeSetting:")) {
                    s = s.substring(14, s.length());
                    String[] items = s.split("\\|");
                    if (items.length == 2) {
                        if (!Utility.stringIsNullOrEmpty(items[0]) && !Utility.stringIsNullOrEmpty(items[1])) {
                            RemoveGeneralSettingsRequestMsg r2 = new RemoveGeneralSettingsRequestMsg();
                            r2.setClassification(c);
                            KeyNameValue remove = new KeyNameValue();
                            remove.setPropertyKey(items[0]);
                            remove.setPropertyName(items[1]);
                            r2.getKeyNameValue().add(remove);
                            try {
                                pcsport.removeGeneralSettings(r2);
                                out.write(Utility.encodeHTML(items[0]) + " : "+  Utility.encodeHTML(items[1]) +" was removed.");
                            } catch (Exception ex) {
                                out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font><br>");
                            }
                        }
                    }
                    break;
                }
            }


            /*
             * if
             * (Utility.stringIsNullOrEmpty(request.getParameter("removeSetting")))
             * { String s = request.getParameter("removeSetting"); String[]
             * items = s.split("\\|");
             *
             * }
             */

            if (!Utility.stringIsNullOrEmpty(request.getParameter("submit"))) {
                //add or replace a setting
                boolean ok = true;
                if (Utility.stringIsNullOrEmpty(request.getParameter("itemkey")) || (!Utility.stringIsNullOrEmpty(request.getParameter("itemkey")) && (Utility.stringIsNullOrEmpty(request.getParameter("itemkey").trim())))
                        || Utility.stringIsNullOrEmpty(request.getParameter("itemname")) || (!Utility.stringIsNullOrEmpty(request.getParameter("itemname")) && (Utility.stringIsNullOrEmpty(request.getParameter("itemname").trim())))
                        || Utility.stringIsNullOrEmpty(request.getParameter("itemvalue")) || (!Utility.stringIsNullOrEmpty(request.getParameter("itemvalue")) && (Utility.stringIsNullOrEmpty(request.getParameter("itemvalue").trim())))) {
                    ok = false;
                    out.write("<font color=#FF0000>You must specify a key, name and value</font><br>");
                }
                if (ok) {
                    try {
                        SetGeneralSettingsRequestMsg rr = new SetGeneralSettingsRequestMsg();
                        rr.setClassification(c);

                        KeyNameValueEnc newitem = new KeyNameValueEnc();
                        KeyNameValue data = new KeyNameValue();
                        data.setPropertyKey(request.getParameter("itemkey"));
                        data.setPropertyName(request.getParameter("itemname"));
                        data.setPropertyValue(request.getParameter("itemvalue"));
                        newitem.setKeyNameValue(data);
                        rr.getKeyNameValueEnc().add(newitem);
                        if (Utility.stringIsNullOrEmpty(request.getParameter("itemencrypt"))) {
                            newitem.setShouldEncrypt(false);
                        } else {
                            newitem.setShouldEncrypt(true);
                        }
                        pcsport.setGeneralSettings(rr);
                        out.write("Saved.");
                    } catch (Exception ex) {
                        out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font><br>");
                    }
                }

            }
        } catch (Exception ex) {
            out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font><br>");
            LogHelper.getLog().log(Level.ERROR, "Error from .jsp", ex);
        }

    } else {
%>



<div class="well">
    <h1>Global Settings</h1>
</div>
<div class="row-fluid">
    <div class="span12">

        Only FGSMS Site/Global administrators can access this page.<br>
        This page contains a list of general settings that control a number of functions of FGSMS<Br><br>
        <a class="btn btn-primary" href="javascript:ToggleHelp();">Show Help</a>
        <script>
            function ToggleHelp()
            {
                var h=document.getElementById("help");
                if (h.style.display=="")
                    h.style.display="none";
                else h.style.display="";
            }
            $(function() {
                $( "#dialog-confirm" ).dialog({
                    resizable: false,
                    autoOpen: false,
                    height:140,
                    modal: true,
                    buttons: {
                        "Delete this item": function() {
                            $( this ).dialog( "close" );
                            return true;
                        },
                        Cancel: function() {
                            $( this ).dialog( "close" );
                            return false;
                        }
                    }
                });
            });
            
        </script>

        <div id="dialog-confirm" title="Confirm Deletion?">
            <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>This will be permanently deleted and cannot be recovered. Are you sure?</p>
        </div>

        <div id="help" style="display:none">
            Starting wtih version RC6, many of the settings that control how FGSMS behaves are set via the Administration > General Settings page, which gives you a generic set/get/remove settings functions.<br><br>
            These settings are key, name, value tuples. The key is usually the component we are configuring. The name is usually the parameter we are setting and the value is something meaningful for this specific name.<br><br>
            <b>Note: Keys and Names are case sensitive</b><br><br>
            <table class="table" id="knvtable"><tr><th>Key</th><th>Name</th><th>Value(s)</th><th>Description</th></tr>

                <tr><td>Agents.Process</td><td>ReportingFrequency</td><td>30</td><td>For Process/Machine agents, this defines how often they report back information in seconds. Default, 30 seconds</td></tr>
                <!--amqp://guest:guest@test/?brokerlist='tcp://localhost:5671?ssl='true'-->
                <tr><td>AMQPAlerts</td><td>ConnectionURL</td><td>
                        <%= Utility.encodeHTML("amqp://%s:%s@clientid/test?brokerlist='tcp://localhost:5172")%><br>
                        <%= Utility.encodeHTML("amqp://%s:%s@clientid/test?brokerlist='tcp://localhost:5171?ssl='true'&ssl_verify_hostname='true'&trust_store='%s'&trust_store_password='%s'")%><br>
                        <%= Utility.encodeHTML("amqp://%s:%s@clientid/test?brokerlist='tcp://localhost:5171?ssl='true'&ssl_verify_hostname='true'&trust_store='%s'&trust_store_password='%s'&key_store='%s'&key_store_password='%s'")%>                   
                    </td><td>The URL to the AMQP broker, such as Qpid/MRG. Do not enter a username and password here as it will not be encrypted. Use the username and password named fields to set it. Keystores and trust stores use the "defaults" grouping.</td></tr>
                <tr><td>AMQPAlerts</td><td>DestinationType</td><td>topic</td><td>"Queue" or "Topic" is allowed, not case sensitive</td></tr>
                <tr><td>AMQPAlerts</td><td>Destination</td><td>FGSMSAlerts</td><td>The name of the thing we are connecting to. Omit any 'topic/' or 'queue/' prefixes. </td></tr>
                <!--<tr><td>AMQPAlerts</td><td>INITIAL_CONTEXT_FACTORY</td><td>org.apache.qpid.jndi.PropertiesFileInitialContextFactory</td><td>The class name of the AMQP JNDI context factory. The example value should always work. If it's not defined, the provided value will be used be default.</td></tr>-->
                <tr><td>AMQPAlerts</td><td>username</td><td>publishuser</td><td>Optional - Username to use for authenticating to the endpoint</td></tr>
                <tr><td>AMQPAlerts</td><td>password</td><td>publishpassword</td><td>Optional - Value must be encrypted</td></tr>

                <tr><td>Bueller</td><td>Enabled</td><td>true/false</td><td>If not defined, the default value is true. Can be used to completely disable the Status Bueller (obtains status information by pulling the wsdl of a service).</td></tr>
                <tr><td>Bueller</td><td>Interval</td><td>10000</td><td>Bueller determines the status of web services by retrieving the wsdl. Number of milliseconds to wait in between firing. Default is 10 seconds or 10000ms, min 1000ms</td></tr>
                <tr><td>Bueller</td><td>defaultUser</td><td>a valid username</td><td>Optional - The default username to use when a service requires authentication.</td></tr>
                <tr><td>Bueller</td><td>defaultPassword</td><td>a valid password</td><td>Optional - The default password to use when a service requires authentication. Required to be encrypted</td></tr>
                <tr><td>Bueller</td><td>IgnoreSSLErrors</td><td>true/false</td><td>If true, SSL certificate errors are ignored, but only if the endpoint does not require authentication, such as a username or a certificate.</td></tr>
                <tr><td>DataPruner</td><td>Interval</td><td>86400000</td><td>Number of milliseconds to wait in between firing. Default is 24 hours or 86400000 ms, min value 10000ms</td></tr>

                <tr><td>defaults</td><td>keystore</td><td>key.jks</td><td>The filename of the key stored to use for authentication, signing and encryption for alerting (excluding email), status pings and federation components. File must be in the server/default/conf folder.</td></tr>
                <tr><td>defaults</td><td>keystorepass</td><td>Must be encrypted</td><td>The password of the keystore (and key) to use for authentication, signing and encryption for alerting (excluding email), status pings and federation components.</td></tr>
                <tr><td>defaults</td><td>truststore</td><td>key.jks</td><td>The filename of the trust store to use for authentication, signing and encryption for alerting (excluding email), status pings and federation components. File must be in the server/default/conf folder.</td></tr>
                <tr><td>defaults</td><td>truststorepass</td><td>Must be encrypted</td><td>The filename of the keystore (and key) to use for authentication, signing and encryption for alerting (excluding email), status pings and federation components.</td></tr>

                <tr><td>HealthStatusCheck</td><td>Interval</td><td>10000</td><td>HealthStatusCheck determines the status of web services that implement the Health Status Check specification. If authentication is required, the Bueller default credentials will be used, or if credentials are set for a specific service, they will be used instead (See Manage Policy). Number of milliseconds to wait in between firing. Default is 10 seconds or 10000ms, min 1000ms</td></tr>

                <tr><td>HornetQAgent</td><td>Interval</td><td>30000</td><td>The interval in milliseconds for connection attempts to a HornetQ broker via JMX</td></tr>
                <tr><td>HornetQAgent</td><td>URLs</td><td>service:jmx:rmi:///jndi/rmi://localhost:12345/jmxrmi|url2|...</td><td>The JMX endpoints of a HornetQ broker, | symbol delimited. Once a connection attempt is made, a policy will be created which you can then use to set the credentials of each endpoint.</td></tr>


                <tr><td>JMSAlerts</td><td>ContextProviderUrl</td><td>jnp://localhost:1099</td><td>The Java Naming Provider. The JMS broker is located on the FGSMS server, the sample should work.</td></tr>
                <tr><td>JMSAlerts</td><td>DestinationType</td><td>topic</td><td>"Queue" or "Topic" is allowed, not case sensitive</td></tr>
                <tr><td>JMSAlerts</td><td>Destination</td><td>topic/FGSMSAlerts</td><td>The name of the thing we are connecting to.</td></tr>
                <tr><td>JMSAlerts</td><td>ConnectionFactoryLookup</td><td>/ConnectionFactory</td><td>The JNDI connection factory lookup value. In most cases, the example will work.</td></tr>
                <tr><td>JMSAlerts</td><td>INITIAL_CONTEXT_FACTORY</td><td>org.jnp.interfaces.NamingContextFactory</td><td>The class name of the JNDI context factory. In most cases, the example will work.</td></tr>
                <tr><td>JMSAlerts</td><td>URL_PKG_PREFIXES</td><td>org.jboss.naming:org.jnp.interfaces</td><td>In most cases, the example will work.</td></tr>
                <tr><td>JMSAlerts</td><td>username</td><td>publishuser</td><td>Optional - Username to use for authenticating to the endpoint</td></tr>
                <tr><td>JMSAlerts</td><td>password</td><td></td><td>Optional - Value must be encrypted</td></tr>
                <tr><td>Mercury</td><td>StatisticsURL</td><td></td><td>URL to the Mercury Statistics Service - this will publish all available metrics to Mercury http://server:port/MS/soap</td></tr>
                <tr><td>Mercury</td><td>RegistrationURL</td><td></td><td>URL to the Mercury Registration Service - this will register  all available services to Mercury http://server:port/mercury-ws/rest/register/webservice</td></tr>
                <tr><td>NTSLAProcessor</td><td>Interval</td><td>300000</td><td>Number of milliseconds to wait in between firing. Default is 5 minutes or 300000 ms. This is the non transactional SLA processor, meaning that it processes SLA rules based on time range rules.</td></tr>
                <tr><td>NTSLAProcessor</td><td>StaleDataThreshold</td><td>300000</td><td>Number of milliseconds from which to trigger a stale data alert. Default is 5 minutes or 300000 ms.</td></tr>
                <tr><td>Policy.Status</td><td>defaultRetention</td><td>2592000000</td><td>default 1 month 2592000000</td></tr>
                <tr><td>Policy.Process</td><td>defaultRetention</td><td>2592000000</td><td>default 1 month 2592000000</td></tr>
                <tr><td>Policy.Machine</td><td>defaultRetention</td><td>2592000000</td><td>default 1 month 2592000000</td></tr>
                <tr><td>Policy.Statistics</td><td>defaultRetention</td><td>2592000000</td><td>default 1 month 2592000000</td></tr>
                <tr><td>Policy.Transactional</td><td>defaultRentention</td><td>2592000000</td><td>default 1 month 2592000000</td></tr>

                <tr><td>Policy.Transactional</td><td>defaultRecordRequest</td><td>true/false</td><td>Record the request message always?</td></tr>
                <tr><td>Policy.Transactional</td><td>defaultRecordResponse</td><td>true/false</td><td>Record the response message always?</td></tr>
                <tr><td>Policy.Transactional</td><td>defaultRecordFaults</td><td>true/false</td><td>Record the both but only when faulting?</td></tr>
                <tr><td>Policy.Transactional</td><td>defaultRecordHeaders</td><td>true/false</td><td>Record transport header information? (HTTP headers)</td></tr>
                <tr><td>QpidJMXAgent</td><td>URLs</td><td>service:jmx:rmi:///jndi/rmi://hostname1:8999/jmxrmi</td><td>The JMX endpoints of a Qpid broker, | symbol delimited. Once a connection attempt is made, a policy will be created which you can then use to set the credentials of each endpoint.</td></tr>
                <tr><td>QpidJMXAgent</td><td>Interval</td><td>30000</td><td>The polling interval to use when connecting to the broker. Time in milliseconds, default is 30 secs</td></tr>
                <tr><td>SLAProcessor</td><td>RetryCount</td><td>2</td><td>Number of times to retry sending alerts in the event that the receiving entity is offline. </td></tr>
                <tr><td>SMXJMXAgent</td><td>URLs</td><td>service:jmx:rmi:///jndi/rmi://hostname1:1099/karaf-root</td><td>The JMX endpoints of an Apache ServiceMix or ActiveMQ broker, | symbol delimited. Once a connection attempt is made, a policy will be created which you can then use to set the credentials of each endpoint.</td></tr>
                <tr><td>SMXJMXAgent</td><td>Interval</td><td>30000</td><td>The polling interval to use when connecting to the broker. Time in milliseconds, default is 30 secs</td></tr>

                <tr><td>StatisticsAggregator</td><td>Interval</td><td>300000</td><td>Number of milliseconds to wait in between firing. Default is 5 minutes or 300000 ms. *Server restart required for non-clustered Quartz jobs.</td></tr>
                <tr><td>StatisticsAggregator</td><td>Periods</td><td>300000,900000,3600000</td><td>This defines the periods of time that statistics are calculated and aggregated. This field can be used to add or remove periods of time (duration in ms) that the statistics aggregate will use to tally up statistics for web services and availability data. Default is 300000,900000,3600000,86400000, which is equivalent to 5 minutes, 15 minutes, 60 minutes and 24 hours.</td></tr>
                <tr><td>UddiPublisher</td><td>Interval</td><td>300000</td><td>Number of milliseconds to wait in between firing. Default and minimum value is 5 minutes or 300000 ms</td></tr>
                <tr><td>UddiPublisher</td><td>Enabled</td><td>true/false</td><td>Enable or disable UDDI Publishing</td></tr>
                <tr><td>UddiPublisher</td><td>Username</td><td></td><td>Username for publishing to UDDI, needs to have permissions to modify any binding template item.</td></tr>
                <tr><td>UddiPublisher</td><td>password</td><td></td><td>Required to be encrypted</td></tr>
                <tr><td>UddiPublisher</td><td>AuthMode</td><td>Uddi or Http</td><td>Different UDDI servers have different authentication requirements. FGSMS supports HTTP and UDDI styles</td></tr>
                <tr><td>UddiPublisher</td><td>ClientCertRequired</td><td>true/false</td><td>Indicates if the server requires a PKI certificate for authentication. </td></tr>
                <tr><td>UddiPublisher</td><td>IgnoreSSLErrors</td><td>true/false</td><td>If true, SSL certificate errors are ignored.</td></tr>
                <tr><td>UddiPublisher</td><td>InquiryURL</td><td></td><td>Inquiry URL</td></tr>
                <tr><td>UddiPublisher</td><td>PublishURL</td><td></td><td>Publish URL</td></tr>
                <tr><td>UddiPublisher</td><td>SecurityURL</td><td></td><td>Seucirty URL</td></tr>

                <tr><td>UddiPublisher</td><td>keystore</td><td>key.jks</td><td>Optional, if not specified 'defaults' settings will be used. The filename of the key stored to use for authentication, signing and encryption for alerting (excluding email) and federation components. File must be in the server/default/conf folder.</td></tr>
                <tr><td>UddiPublisher</td><td>keystorepass</td><td>Must be encrypted</td><td>Optional, if not specified 'defaults' settings will be used. The filename of the key to use for authentication, signing and encryption for alerting (excluding email) and federation components.</td></tr>


                <tr><td>WSNAlerts</td><td>BrokerURL</td><td>http://localhost:8192/Broker/</td><td>The URL to the WSN Broker, such as ServiceMix. If using SSL, the defaults truststore and keystores will be used. PKI authentication and SSL is supported via the defaults parameters</td></tr>
                <tr><td>WSNAlerts</td><td>dialect</td><td>http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple</td><td>Optional. Certain brokers require certain values for this. ServiceMix is one of them. If not yet, the specified value is used</td></tr>
                <tr><td>WSNAlerts</td><td>Destination</td><td>topic/FGSMSAlerts</td><td>The name of the topic. If not defined, the provided example is used.</td></tr>
                <tr><td>WSNAlerts</td><td>username</td><td>publishuser</td><td>Optional - Username to use for authenticating to the endpoint</td></tr>
                <tr><td>WSNAlerts</td><td>password</td><td></td><td>Optional - Value must be encrypted</td></tr>
                <tr><td>WSNAlerts</td><td>RegisterPublisher</td><td>true/false</td><td>Default value=false. WS-Notification supports the idea of registering publishers for specific topics. If your WS-N broker supports this and requires it, set this to true. If registration fails, alert delivery will still be attempted. Only the default Destination will be registered. SLA's with destination overrides will not be registered.</td></tr>


            </table>


        </div>

        <%
            GetGeneralSettingsResponseMsg res = null;
            try {



                //get settings
                GetGeneralSettingsRequestMsg req = new GetGeneralSettingsRequestMsg();
                req.setClassification(c);
                res = pcsport.getGeneralSettings(req);
                if (res != null && !res.getKeyNameValue().isEmpty()) {
                    out.write("<h3>Current Settings</h3><table class=\"table\" id=table1 name=table1><tr><th>Key</th><th>Name</th><th>Value</th><th>Action</th></tr>");
                    for (int i = 0; i < res.getKeyNameValue().size(); i++) {
                        out.write("<tr><td>" + Utility.encodeHTML(res.getKeyNameValue().get(i).getPropertyKey()) + "</td>");
                        out.write("<td>" + Utility.encodeHTML(res.getKeyNameValue().get(i).getPropertyName()) + "</td>");
                        out.write("<td>" + Utility.encodeHTML(res.getKeyNameValue().get(i).getPropertyValue()) + "</td>"
                                + "<td><a class=\"btn btn-primary\" href=\"javascript:postBack('removeSetting:"
                                + Utility.encodeHTML(res.getKeyNameValue().get(i).getPropertyKey()) + "|"
                                + Utility.encodeHTML(res.getKeyNameValue().get(i).getPropertyName())
                                + "','admin/globalSettings.jsp');\">Remove</a></tr>");
                    }
                    out.write("</table><br>");
                } else {
                    out.write("No settings were returned.");
                }
            } catch (Exception ex) {
                out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font><br>");
               LogHelper.getLog().log(Level.ERROR, "Error from removecredentials.jsp", ex);
            }
        %>


        <h3>Add a Setting</h3>
        <table>
            <tr><td>Key</td><td><input type="text" name="itemkey" id="itemkey" /></td></tr>
            <tr><td>Name</td><td><input type="text" name="itemname" id="itemname"/></td></tr>
            <tr><td>Value (it will be visible)</td><td><input type="text" name="itemvalue"/></td></tr>
            <tr><td>Should the value by encrypted?</td><td><input type="checkbox" name="itemencrypt" name="itemencrypt"/></td></tr>
        </table>
        <a class="btn btn-primary" onclick="javascript:postBack('submit','admin/globalSettings.jsp');">Save</a><br><Br>

        <script>
            $(function() {
                $("#table1").resizable();
                $("input:text").resizable();
                
                var keys = $("table#knvtable tr");
                var rkeys=[];
                var nkeys=[];
                $.each(keys, function(data){
                    rkeys.push(keys[data].children[0].innerText);
//                    console.log(keys[data].children[0].innerText);
                    nkeys.push(keys[data].children[1].innerText);
//                    console.log(keys[data].children[1].innerText);
				
                });
                rkeys.dedupe({dedupeComparator:StringCompare, sortComparator:StringSort});
                nkeys.dedupe({dedupeComparator:StringCompare, sortComparator:StringSort});
                //dedup
                $("#itemkey").typeahead({source : rkeys });
                $("#itemname").typeahead({source : nkeys});
                
                
            });
        </script>

    </div></div>

<%

    }
%>