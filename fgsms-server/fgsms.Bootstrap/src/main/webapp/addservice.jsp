<%-- 
    Document   : addservice
    Created on : Jan 12, 2012, 9:50:51 AM
    Author     : Administrator
--%> 

<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.IpAddressUtility"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="java.util.UUID"%>
<%@page import="javax.xml.bind.JAXBElement"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.PolicyType"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>

<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Calendar"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.miloss.fgsms.services.interfaces.status.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="csrf.jsp" %>

<% String parent = "admin.jsp&addservice.jsp"; %>

<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    if (request.getMethod().equalsIgnoreCase("post")) {
        if (!Utility.stringIsNullOrEmpty(request.getParameter("submitnewurl")) && !Utility.stringIsNullOrEmpty(request.getParameter("newurl"))) {
            PCS pcsport = pl.GetPCS(application, request, response);
            try {
                URL newurl = new URL(request.getParameter("newurl"));
                //if this fails, it should throw and drop the request
                String modurl = IpAddressUtility.modifyURL(request.getParameter("newurl"), true);

                DatatypeFactory fac = DatatypeFactory.newInstance();
                SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
                req.setClassification(c);
                req.setURL(modurl);
                
                TransactionalWebServicePolicy pol = new TransactionalWebServicePolicy();
                pol.setPolicyType(PolicyType.TRANSACTIONAL);
                pol.setBuellerEnabled(true);
                pol.setExternalURL(request.getParameter("newurl"));
                Utility.addStatusChangeSLA(pol);
                
                pol.setURL(modurl);
                pol.setAgentsEnabled(true);
                pol.setDomainName("unspecified");
                pol.setMachineName("unspecified");
                pol.setPolicyType(PolicyType.TRANSACTIONAL);
                pol.setDataTTL(fac.newDuration(1000 * 60 * 60 * 24 * 30L));
                pol.setFederationPolicyCollection(new FederationPolicyCollection());
                pol.setPolicyRefreshRate(fac.newDuration(0));
                req.setPolicy(pol);
                req.setURL(modurl);

                pcsport.setServicePolicy(req);
                response.sendRedirect("manage.jsp?url=" + URLEncoder.encode(modurl, Constants.CHARSET));
            } catch (Exception ex) {
                out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font><br>");
                LogHelper.getLog().log(Level.ERROR, "Error from addservice.jsp error1", ex);
            }
        }
    } else {

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<div class="well">
    <h1>Add a Service </h1>
</div>
<div class="row-fluid">
    <div class="span12">


        <script type="text/javascript">
            $(function () {

                $("input:text").resizable();
            });
        </script>

        Under normal circumstances, FGSMS's agents will automatically create a policy for a particular item, component or capability. This page can be used to manually create a policy.
        Creating a policy manually does not necessarily mean that it will be monitored by an agent. 
        If you select a "Transactional" policy type and the URL of the service is accessible by via HTTP or JMS, status updates will automatically be added and tracked by FGSMS's Status Buller.


        <%
        %>
        Administrative Permissions are required for the following actions.<br>
        <center>
            <input type="button" name="addweb" value="Add a Web Accessible Service" onclick="javascript:toggleVisibility('addservicediv')"> 
            <input type="button" name="addbroker" value="Add a Message Broker or ESB" onclick="javascript:loadpage('help/index.jsp', 'mainpane', '<%=parent%>');">
            <input type="button" name="import" value="Add a XML Policy" onclick="javascript:loadpage('profile/policyImport.jsp', 'mainpane', '<%=parent%>');"> 
        </center>
        <Br><Br>
        <div style="display: none" id="addservicediv">
            This will create a new service policy that represents some kind of transactional web service. It really can be <i><b>any HTTP/HTTPS accessible URL</b></i>. The important thing to understand is that no transactions for this service will be recorded, only it's availability. To monitor availability and transactions, you'll need either a server or client side agent installed to monitor transcations.
            Please do NOT use urls that include "localhost" and cannot be longer than 128 characters. If longer, it will be truncated.<Br><br>
            Later, you'll be able to set credentials for authentication if necessary.
            <input type="text" name="newurl" id="newurl" value="http://someurl/path" style="width: 200px; height:45px;"/>
            <input type="button" name="submitnewurl" value="Submit" onclick="javascript:postBackReRender('submitnewurl', 'addservice.jsp', 'mainpane')"> 
        </div>

    </div>
</div>

<%            }
%>