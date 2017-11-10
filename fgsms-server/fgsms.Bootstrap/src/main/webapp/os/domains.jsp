<%-- 
    Document   : domains
    Created on : Jan 28, 2012, 12:24:20 PM
    Author     : Administrator
--%>




<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>

<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page import="java.util.Properties"%> 
<%@page import="java.net.URL"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Domains</h1>
</div>
<div class="row-fluid">
    <div class="span12">

        This provides a list of domains that have at least one machine with an FGSMS agent running on it. Domains can be thought of as a container. In Windows, the domain is the name of the Windows Active Directory Domain. For all others, individual policies can be edited to manually associate a service within a domain.
        Click on one of the links to drill down to machine level information.<br>
        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);

            try {

                PCS pcsport = pl.GetPCS(application, request, response);

                GetDomainListRequestMsg req = new GetDomainListRequestMsg();
                req.setClassification(c);

                GetDomainListResponseMsg res = pcsport.getDomainList(req);

                if (res != null && !res.getDomains().isEmpty()) {
                    out.write("<ul>");
                    for (int i = 0; i < res.getDomains().size(); i++) {
                        out.write("<li><a href=\"javascript:loadpage('os/servers.jsp?domain=" + URLEncoder.encode(res.getDomains().get(i)) +"','mainpane', 'infrastructure.jsp&os/domains.jsp');\">" + Utility.encodeHTML(res.getDomains().get(i)) + "</a></li>");
                    }
                    out.write("</ul>");
                } else {
                    out.write("No domains were returned.");
                }

            } catch (Exception ex) {
                out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
            }

        %>
    </div>
</div>