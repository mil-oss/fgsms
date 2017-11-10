<%-- 
    Document   : dataCollectors
    Created on : Jan 6, 2011, 8:46:13 PM
    Author     : Alex
--%>

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
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Data Collectors</h1>
    <p><a class="btn btn-primary btn-large">Learn more &raquo;</a></p>
</div>
<div class="row-fluid"> 
    <div class="span12">
        This represents a listing of machines hosting an FGSMS Data Collector Service.
        Since FGSMS monitors traffic for web services, it is also important to properly load balance
        your Data Collector Services. This page will give you a brief look at how the load is distributed across
        Data Collectors. Note: this encompasses all available transactions that FGSMS has stored.
        The results may be skewed if a new Data Collector was added recently.<br /><br>
        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");

            ProxyLoader pl = ProxyLoader.getInstance(application);
            try {

                DataAccessService dasport = pl.GetDAS(application, request, response);

                GetDataCollectorListRequestMsg req = new GetDataCollectorListRequestMsg();
                req.setClassification(c);
                DecimalFormat nf = new DecimalFormat("###.##");
                GetDataCollectorListResponseMsg res = dasport.getDataCollectorList(req);

                if (res != null && res.getHosts() != null && res.getHosts() != null
                        && res.getHosts().getHostInstanceStats() != null
                        && res.getHosts().getHostInstanceStats().size() > 0) {
                    out.write("<table class=\"table\"><tr><th>Host</th><th>Transactions</th><th>%</th></tr>");
                    int total = 0;
                    for (int i = 0; i < res.getHosts().getHostInstanceStats().size(); i++) {
                        total += res.getHosts().getHostInstanceStats().get(i).getRecordedTransactionCount();
                    }
                    for (int i = 0; i < res.getHosts().getHostInstanceStats().size(); i++) {
                        double d = (double) ((double) res.getHosts().getHostInstanceStats().get(i).getRecordedTransactionCount() / (double) total * (double) 100);
                        out.write("<tr><td>" + res.getHosts().getHostInstanceStats().get(i).getHost() + "</td><td>"
                                + res.getHosts().getHostInstanceStats().get(i).getRecordedTransactionCount()
                                + "</td><td>" + nf.format(d) + "%</td></tr>");
                    }
                    out.write("</table>");
                }
                else
                    out.write("No information was returned by the service.");
            } catch (Exception ex) {
                out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());

            }


        %>

    </div>
</div>