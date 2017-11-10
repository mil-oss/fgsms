<%-- 
    Document   : serviceHosts
    Created on : Jan 6, 2011, 8:47:17 PM
    Author     : Alex
--%> 

<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.Utility"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Service Hosts</h1>
</div> 
<div class="row-fluid">
    <div class="span12">


This page represented all servers that have had an FGSMS agent installed and successfully monitored a service transaction.
This page is useful for analyzing invocation load across a set of servers.<br />


<br>
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);

    try {

        DataAccessService dasport = pl.GetDAS(application, request, response);
        
        GetServiceHostListRequestMsg req = new GetServiceHostListRequestMsg();
        req.setClassification(c);
        GetServiceHostListResponseMsg res = dasport.getServiceHostList(req);
        DecimalFormat nf = new DecimalFormat("###.##");
        if (res != null && res.getHosts() != null && res.getHosts() != null
                && res.getHosts().getHostInstanceStats() != null
                && res.getHosts().getHostInstanceStats().size() > 0) {
            out.write("<table  class=\"table\"><tr><th>Host</th><th>Transactions</th><th>%</th><th>Performance</th></tr>");
            int total = 0;
            for (int i = 0; i < res.getHosts().getHostInstanceStats().size(); i++) {
                total += res.getHosts().getHostInstanceStats().get(i).getRecordedTransactionCount();
            }
            for (int i = 0; i < res.getHosts().getHostInstanceStats().size(); i++) {
                double d = (double) ((double) res.getHosts().getHostInstanceStats().get(i).getRecordedTransactionCount() / (double) total * (double) 100);
                out.write("<tr><td>" + res.getHosts().getHostInstanceStats().get(i).getHost() + "</td><td>"
                        + res.getHosts().getHostInstanceStats().get(i).getRecordedTransactionCount()
                        + "</td><td>" + nf.format(d) + "%</td>"
                        + "<td><a href=\"javascript:loadpage('admin/hostPerformance.jsp?Host=" + URLEncoder.encode(res.getHosts().getHostInstanceStats().get(i).getHost()) + "','mainpane');\">View</a></td></tr>");
            }
            out.write("</table>");
            out.write("<br>Warning, it may take some time for the performance link to load.");
        }
    } catch (Exception ex) {
        out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());

    }


%>


    </div>
</div>