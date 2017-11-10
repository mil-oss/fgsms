<%-- 
    Document   : servers
    Created on : Jan 28, 2012, 12:24:04 PM
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
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../csrf.jsp" %>
<div class="well"> 
    <h1>Servers</h1>
</div>
<div class="row-fluid">
    <div class="span12">


The following Servers are associated with the Domain <%
    out.write(Utility.encodeHTML(request.getParameter("domain")));
%><br><br>
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);


    try {
        if (Utility.stringIsNullOrEmpty(request.getParameter("domain"))) {
            response.sendRedirect("domains.jsp");
        } else {

            PCS pcsport = pl.GetPCS(application, request, response);

            GetMachinesByDomainRequestMsg req = new GetMachinesByDomainRequestMsg();
            req.setClassification(c);
            req.setDomain(request.getParameter("domain"));
            GetMachinesByDomainResponseMsg res = pcsport.getMachinesByDomain(req);

            if (res != null && !res.getHostname().isEmpty()) {
                out.write("<ul>");
                for (int i = 0; i < res.getHostname().size(); i++) {
                    out.write("<li><a href=\"javascript:loadpage('os/serverprocess.jsp?server=" +URLEncoder.encode(res.getHostname().get(i))+"&domain="+URLEncoder.encode(request.getParameter("domain")) +"','mainpane', 'infrastructure.jsp&os/domains.jsp&os/servers.jsp?domain="+URLEncoder.encode(request.getParameter("domain"))+"');\">" + Utility.encodeHTML(res.getHostname().get(i)) + "</a></li>");
                }
                out.write("</ul>");
            } else {
                out.write("No domains were returned."); 
            }
        }

    } catch (Exception ex) {
        out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
    }

%>

    </div>
</div>