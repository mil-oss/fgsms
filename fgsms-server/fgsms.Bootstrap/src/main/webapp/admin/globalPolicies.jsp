<%-- 
    Document   : globalPolicies
    Created on : Jan 6, 2011, 8:46:24 PM
    Author     : Alex
--%> 


<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
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

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<%     SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");

     ProxyLoader pl = ProxyLoader.getInstance(application);

     try {

          PCS pcsport = pl.GetPCS(application, request, response);

          if (request.getMethod().equalsIgnoreCase("post")) {
               SetGlobalPolicyRequestMsg req = new SetGlobalPolicyRequestMsg();
               req.setClassification(c);
               GlobalPolicy gal = new GlobalPolicy();
               gal.setRecordedMessageCap(Integer.parseInt(request.getParameter("messagecap")));
               gal.setPolicyRefreshRate(Helper.StringToDuration(request.getParameter("polrefreshrate")));
               if (!Utility.stringIsNullOrEmpty(request.getParameter("agentsenable"))) {
                    gal.setAgentsEnabled(true);
               } else {
                    gal.setAgentsEnabled(false);
               }
               req.setPolicy(gal);
               pcsport.setGlobalPolicy(req);
               out.write("Saved");

          } else {
%>

<div class="well">
    <h1>Global Policies</h1>
</div>
<div class="row-fluid">
    <div class="span12"> 

        <%
             GetGlobalPolicyRequestMsg req = new GetGlobalPolicyRequestMsg();
             req.setClassification(c);
             GetGlobalPolicyResponseMsg res = pcsport.getGlobalPolicy(req);
             if (res != null && res.getPolicy() != null) {
                  out.write("<table class=\"table\"><tr><th>Parameter</th><th>Current Value</th><th>New Value</th></tr>");
                  out.write("<tr><td>Recorded message cap (<a href=\"javascript:ShowHelp('messagecap');\">help</a>)</td><td>" + res.getPolicy().getRecordedMessageCap() + " bytes</td><td>"
                          + "<input type=text size=7 name=messagecap value=" + res.getPolicy().getRecordedMessageCap() + "></td></tr>");
                  out.write("<tr><td>Policy Refresh Rate (<a href=\"javascript:ShowHelp('timeformats');\">help</a>)</td><td>" + Helper.DurationToString(res.getPolicy().getPolicyRefreshRate()) + "</td><td>"
                          + "<input type=text size=20 name=polrefreshrate value=\"" + Helper.DurationToString(res.getPolicy().getPolicyRefreshRate()) + "\"></td></tr>");
                  out.write("<tr><td>UDDI Publish Rate (<a href=\"javascript:ShowHelp('timeformats');\">help</a>)</td><td>" + Helper.DurationToString(res.getPolicy().getUDDIPublishRate()) + "</td><td>This parameter is now set in General Settings"
                          //        + "<input type=text size=20 name=uddirate value=\"" + Helper.DurationToString(res.getPolicy().getUDDIPublishRate()) + "\">"
                          + "</td></tr>");
                  out.write("<tr><td>Agents Enabled (<a href=\"javascript:ShowHelp('agentsenabled');\">help</a>)</td><td>" + res.getPolicy().isAgentsEnabled() + "</td><td><input type=checkbox  name=agentsenable ");
                  if (res.getPolicy().isAgentsEnabled()) {
                       out.write("checked");
                  }
                  out.write("></td></tr></table>");
                  out.write("<br><input type=button value=\"Save Global Policy\" onclick=\"javascript:postBack('submit','admin/globalPolicies.jsp');\">");
             }
        %>

        <br><br>
        Description: FGSMS uses a variety of parameters that are globally defined, that means that these settings apply to all components of FGSMS, included the GUI, Agents, and Processes such as the Bueller.<Br>
        In order to change these, you'll need to have Site Administration privileges (also referred to as Global Administration privilege).
        <ul>
            <li>Recorded message cap - When recording or auditing a web service, all transactions are logged in FGSMS. Due to technical limitations and performance reasons, this parameter can be used to provide a ceiling of how much data is actually recorded.</li>
            <li>Policy Refresh Rate - this represents how often FGSMS agents will renew their cached copies of service policies. The higher the time range, the less bandwidth agents will use, however changes to policies will take longer to be applied.</li>
            <li>UDDI Publish Rate - this represents how often the UDDI Publisher will attempt to publish performance data, provide the UDDI Publisher is configured and operating and that there are service policies with "Data Federation" items selected.</li>

        </ul>


    </div>
</div>

<%
          }
     } catch (Exception ex) {
          out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font>");
          LogHelper.getLog().log(Level.ERROR, "Error from globalPolicies.jsp", ex);
     }
%>

<%

%>