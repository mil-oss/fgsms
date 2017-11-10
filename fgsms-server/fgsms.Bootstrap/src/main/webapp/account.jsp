
<%-- 
    Document   : hostPerformance
    Created on : Apr 11, 2011, 3:33:44 PM
    Author     : Alex
--%> 

<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>

<%@page import="java.beans.Encoder"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>

<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@include file="csrf.jsp" %>
<%
    SecurityWrapper sc = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    PCS pcsport = pl.GetPCS(application, request, response);

 

    org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory fac = new org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory();
    org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory dasfac = new org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory();


    if (request.getMethod().equalsIgnoreCase("post")) {
        if (!Utility.stringIsNullOrEmpty(request.getParameter("saveEmail"))) {
            try {
                SetMyEmailAddressRequestMsg req1 = new SetMyEmailAddressRequestMsg();
                req1.setClassification(sc);

                if (!Utility.stringIsNullOrEmpty(request.getParameter("newemailaddress"))) {
                    String s = request.getParameter("newemailaddress");
                    s = s.trim();
                    if (!Utility.stringIsNullOrEmpty(s)) {
                        req1.getEmail().add(s);
                    }
                }
                if (!Utility.stringIsNullOrEmpty(request.getParameter("newemailaddress1"))) {
                    String s = request.getParameter("newemailaddress1");
                    s = s.trim();
                    if (!Utility.stringIsNullOrEmpty(s)) {
                        req1.getEmail().add(s);
                    }
                }

                if (!Utility.stringIsNullOrEmpty(request.getParameter("newemailaddress2"))) {
                    String s = request.getParameter("newemailaddress2");
                    s = s.trim();
                    if (!Utility.stringIsNullOrEmpty(s)) {
                        req1.getEmail().add(s);
                    }
                }
                if (!Utility.stringIsNullOrEmpty(request.getParameter("newemailaddress3"))) {
                    String s = request.getParameter("newemailaddress3");
                    s = s.trim();
                    if (!Utility.stringIsNullOrEmpty(s)) {
                        req1.getEmail().add(s);
                    }
                }
                if (req1.getEmail().isEmpty()) {
                    response.setStatus(400);
                    out.write("You must enter at least one email address<br>");
                } else {
                    pcsport.setMyEmailAddress(req1);
                    out.write("Saved!");
                }
            } catch (Exception ex) {
                response.setStatus(400);
                out.write("There was an error processing your request: " + ex.getMessage());
            }
        } else if (!Utility.stringIsNullOrEmpty(request.getParameter("SLAAlerts"))) {
            try {
                SetAlertRegistrationsRequestMsg ra = new SetAlertRegistrationsRequestMsg();
                ra.setClassification(sc);

                String[] ids = request.getParameterValues("slaids");
                if (ids != null && ids.length > 0) {
                    for (int i = 0; i < ids.length; i++) {

                        String[] s = ids[i].split("\\|");
                        //        out.write("DEBUG " + ids[i] + "<br>" + s[0] + " " + s[1]);
                        SLAregistration t = new SLAregistration();
                        t.setSLAID(s[0]);
                        t.setServiceUri(s[1]);
                        ra.getItems().add(t);
                    }
                }
                pcsport.setAlertRegistrations(ra);
                out.write("Alert Registration Updated, a confirmation email has been dispatched.");
            } catch (Exception ex) {
                response.setStatus(400);
                out.write("There was an error processing your request: " + ex.getMessage());
            }
        } else {
            if (!Utility.stringIsNullOrEmpty(request.getParameter("truncation"))) {
                int x = 100;
                try {
                    x = Integer.parseInt(request.getParameter("truncation"));
                    if (x < 30 || x > 1000) {
                        x = 100;
                    }
                } catch (Exception ex) {
                    x = 100;
                }
                Cookie c = new Cookie("truncation", new Integer(x).toString());
                c.setMaxAge(Integer.MAX_VALUE);
                response.addCookie(c);
            }
            if (!Utility.stringIsNullOrEmpty(request.getParameter("EnableAlerts"))) {
                Cookie c = new Cookie("alertsEnabled", "true");
                c.setMaxAge(Integer.MAX_VALUE);
                response.addCookie(c);
                //  out.write("cookie set<br>");
            }
            if (!Utility.stringIsNullOrEmpty(request.getParameter("DisableAlerts"))) {
                Cookie c = new Cookie("alertsEnabled", "false");
                c.setMaxAge(Integer.MAX_VALUE);
                response.addCookie(c); 
                
                // out.write("cookie set<br>");
            }
            if (!Utility.stringIsNullOrEmpty(request.getParameter("SaveInterval"))) {
                String t = request.getParameter("interval");
                int i = 30000;
                try {
                    i = Integer.parseInt(t) * 1000;
                    if (i < 5000) {
                        i = 5000;
                    }
                } catch (Exception ex) {
                }
                Cookie c = new Cookie("alertsInterval", Integer.toString(i));
                c.setMaxAge(Integer.MAX_VALUE);
                response.addCookie(c);
                // out.write("cookie set<br>");
            }
            response.sendRedirect("index.jsp");
        }
    } else {
%>

<div class="well">
    <h1>My Account</h1>
    <p>Setup your email address, brower alerts and email alerts.</p>
</div>



<div class="row-fluid">
    <div class="span12 well well-small">Email Accounts</div>
</div>

<div class="row-fluid">
    <div class="span12">


        <%
            GetMyEmailAddressResponseMsg res = null;
            try {
                GetMyEmailAddressRequestMsg r = new GetMyEmailAddressRequestMsg();
                r.setClassification(sc);
                res = pcsport.getMyEmailAddress(r);
                if (res != null && !(res.getEmail().isEmpty())) {
                    out.write("Currently registered emal addresses are: " + Utility.encodeHTML(Utility.listStringtoString(res.getEmail())));
                } else {
                    out.write("You have not yet set your email address for alerting.");
                }

                out.write("<br>");

            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getMessage());
            }
        %>
        <br>
        To Set or Change Your Registered Email Address. All previous entries will be removed. You may register up to four email addresses.<br>
        <input type="text" name="newemailaddress" value="<%
            if (res != null && res.getEmail().size() > 0) {
                out.write(Utility.encodeHTML(res.getEmail().get(0)));
            }
               %>" ><br>
        <input type="text" name="newemailaddress1" value="<%
            if (res != null && res.getEmail().size() > 1) {
                out.write(Utility.encodeHTML(res.getEmail().get(1)));
            }
               %>" ><br>
        <input type="text" name="newemailaddress2" value="<%
            if (res != null && res.getEmail().size() > 2) {
                out.write(Utility.encodeHTML(res.getEmail().get(2)));
            }
               %>" ><br>
        <input type="text" name="newemailaddress3" value="<%
            if (res != null && res.getEmail().size() > 3) {
                out.write(Utility.encodeHTML(res.getEmail().get(3)));
            }
               %>" ><br>
        <a  class="btn btn-primary" href="javascript:postBack('saveEmail','account.jsp');">Save Changes</a>





        <%

            boolean alertsenabled = false;
            int interval = 30000;

            Cookie[] cookies = request.getCookies();

            if (cookies
                    == null || cookies.length
                    == 0) {
                //       out.write("No settings are currently configured.");
            } else {

                for (int i = 0; i < cookies.length; i++) {
                    //     out.write("Name="+cookies[i].getName() + " Domain="+cookies[i].getDomain() + "Path="+cookies[i].getPath() + "Value="+cookies[i].getValue() + 
                    //               "Comment="+ cookies[i].getComment() + "<br>");

                    if (cookies[i].getName().equalsIgnoreCase("alertsInterval")) {
                        try {
                            interval = Integer.parseInt(cookies[i].getValue());
                            if (interval < 1000) {
                                interval = 10000;
                            }
                        } catch (Exception ex) {
                            interval = 10000;
                        }
                    }
                    if (cookies[i].getName().equalsIgnoreCase("alertsEnabled")) {
                        try {
                            alertsenabled = Boolean.parseBoolean(cookies[i].getValue());
                        } catch (Exception ex) {
                            alertsenabled = false;
                        }
                    }


                }
            }

        %>
        <br><br><br>
    </div>
</div>
<div class="row-fluid">
    <div class="span12 well well-small">Browser Alerts</div>
</div>
<div class="row-fluid">
    <div class="span12">
        FGSMS supports Alerting within the browser that will notify you of service invocation failures, SLA failures, services that have gone offline, and FGSMS configuration problems, so long as you keep your browser open and authenticated. Use these buttons to enable or disable browser based alerting.<br>
        <Br>
        Browser based Alerting is currently <b id="alertstatus"><%
            if (alertsenabled) {
                out.write("enabled");
            } else {
                out.write("disabled");
            }
            %></b>.<br><Br>

        <a class="btn btn-primary" href="javascript:enableAlerts();">Enable Alerts</a><Br>
        <a class="btn btn-primary" href="javascript:disableAlerts();">Disable Alerts</a><Br>

        <script type="text/javascript">
            function enableAlerts()
            {
                setCookie('alertsEnabled','true','9999');
                $("#alertstatus").html("enabled");
                CheckAlerts();                
            }
            function disableAlerts()
            {
                setCookie('alertsEnabled','false','9999');
                $("#alertstatus").html("disabled");
                $('#alerts').hide();
                stopCheckAlerts();
            }
            function saveInterval()
            {
                var ok = false;
                try{
                    var num=  Number($("#interval").val());
                    if (num > 0)
                    {
                        num = num * 1000;
                        if (num > 0)
                        {
                            ok =true;
                            setCookie('alertsInterval',num,'9999');
                        }
                    } 
                }
                catch (ex)
                {
            
                }
                if (!ok)
                    alert("Check your value for interval. I don't think it's a valid number")                ;
            }
            function CheckAll()
            {
            
                $("form input:checkbox").attr("checked",true);
            
            }
            function CheckNone()
            {
            
                $("form input:checkbox").removeAttr("checked",true);
            
            }
        </script>
        <Br><Br>
        The alerting interval sets how often your browser will check for new alerts.<Br><br>
        Current Interval: <%= interval / 1000%> seconds.<br><br>
        Alert Interval (seconds): <input type="text" size="6" name="interval" id="interval" value="<%= interval / 1000%>"><br><br>
        <a class="btn btn-primary" href="javascript:saveInterval();">Save Interval</a><Br>
        <Br><Br>


    </div>
</div>
<div class="row-fluid">
    <div class="span12 well well-small">SLA Subscriptions</div>
</div>
<div class="row-fluid">
    <div class="span12">

        <%

            try {

                GetAlertRegistrationsRequestMsg alertsr = new GetAlertRegistrationsRequestMsg();
                alertsr.setClassification(sc);
                GetAlertRegistrationsResponseMsg alerts = pcsport.getAlertRegistrations(alertsr);
                out.write("<h2>Currently Subscribed SLAs</h2>");
                if (alerts != null && !alerts.getItems().isEmpty()) {

                    out.write("<table class=\"table\"><tr><th>Service URI</th><th>SLA ID</th></tr>");
                    for (int i = 0; i < alerts.getItems().size(); i++) {
                        out.write("<tr><td><a href=\"manage.jsp?url=" + alerts.getItems().get(i).getServiceUri() + "\">" + alerts.getItems().get(i).getServiceUri() + "</a></td>"
                                + "<td>" + alerts.getItems().get(i).getSLAID() + "</td></tr>");

                    }
                    out.write("</table>");

                } else {
                    out.write("No alertings aubscriptions are currently set.<Br><Br>");
                }

        %>
        <br><br>
        <%

                List<Plugin> rule_plugins = Helper.GetPluginList(pcsport, sc, "SLA_RULE");

                GetAvailableAlertRegistrationsRequestMsg ar = new GetAvailableAlertRegistrationsRequestMsg();
                ar.setClassification(sc);
                GetAvailableAlertRegistrationsResponseMsg as = pcsport.getAvailableAlertRegistrations(ar);
                if (as != null && !as.getServicePolicy().isEmpty()) {
                    out.write("<h2>Available SLAs</h2>");
                    out.write("The following represents all defined SLAs that are email alert enabled that you can subscribe to.<Br>");
                    out.write("<table class=\"table\"><tr><th>Display Name</th><th>URL</th><th>SLAs</th>");
                    for (int i = 0; i < as.getServicePolicy().size(); i++) {
                        //for each service
                        out.write("<tr><td>");
                        if (Utility.stringIsNullOrEmpty(as.getServicePolicy().get(i).getDisplayName())) {
                            out.write("(not defined)");
                        } else {
                            out.write(as.getServicePolicy().get(i).getDisplayName());
                        }
                        out.write("</td><td>" + as.getServicePolicy().get(i).getURL() + "</td><td>");
                        for (int k = 0; k < as.getServicePolicy().get(i).getServiceLevelAggrements().getSLA().size(); k++) {
                            if (Utility.hasEmailSLA(as.getServicePolicy().get(i).getServiceLevelAggrements().getSLA().get(k).getAction())) {
                                //output the relevant sla information
                                out.write("<input type=checkbox  name=slaids value=\"" + Utility.encodeHTML(as.getServicePolicy().get(i).getServiceLevelAggrements().getSLA().get(k).getGuid() + "|"
                                        + as.getServicePolicy().get(i).getURL()) + "\"  ");
                                if (Helper.ContainsSLAID(alerts.getItems(), as.getServicePolicy().get(i).getServiceLevelAggrements().getSLA().get(k).getGuid())) {
                                    out.write(" checked ");
                                }
                                out.write("/> ");
                                out.write("<a href=\"manage.jsp?url=" + URLEncoder.encode(as.getServicePolicy().get(i).getURL()) + "\">View Policy</a>  ");
                                //out.write(as.getServicePolicy().get(i).getServiceLevelAggrements().getValue().getSLA().get(k).getGuid() + " ");
                                out.write(Helper.BuildSLARuleData(as.getServicePolicy().get(i).getServiceLevelAggrements().getSLA().get(k).getRule(),rule_plugins) + "<br>");
                            }
                        }
                        out.write("</td></tr>");
                    }
                    out.write("</table>");
                } else {
                    out.write("There are no SLAs configured for email alerts that you have access to.");
                }


            } catch (Exception ex) {
                out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
                LogHelper.getLog().log(Level.WARN, "Error caught alertingSettings", ex);
            }
        %>
        <br>
        <a class="btn" href="javascript:CheckAll();">Check All</a>
        <a class="btn" href="javascript:CheckNone();">Check None</a>

        <a class="btn btn-primary" href="javascript:postBack('SLAAlerts','account.jsp');">Save Subscriptions</a><Br>



    </div>
</div>
<%

    }
%>