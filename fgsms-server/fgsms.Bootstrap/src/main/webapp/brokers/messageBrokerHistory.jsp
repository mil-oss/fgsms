<%-- 
    Document   : brokerHistory
    Created on : May 23, 2011, 3:32:17 PM
    Author     : Alex 
--%>


<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="java.util.Calendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);

    
    DataAccessService das = pl.GetDAS(application, request, response);
 
//if postback, validate form, then set
    if (request.getMethod().equalsIgnoreCase("POST")) {
    }

    if (Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
        response.sendRedirect("../index.jsp");
        return;
    }



%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">


<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Message Broker History</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        
This page displays the average historical information for this message broker for the past 24 hours.<br>
<%
    try {
        GetHistoricalBrokerDetails req = new GetHistoricalBrokerDetails();
        req.setGetHistoricalBrokerDetailsRequest(new GetHistoricalBrokerDetailsRequestMsg());
        req.getGetHistoricalBrokerDetailsRequest().setClassification(c);
        req.getGetHistoricalBrokerDetailsRequest().setUri(request.getParameter("uri"));
        TimeRange r = new TimeRange();
        DatatypeFactory f = DatatypeFactory.newInstance();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        r.setEnd((gcal));
        gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        gcal.add(Calendar.DATE, -1);
        r.setStart((gcal));
        req.getGetHistoricalBrokerDetailsRequest().setRange(r);
        GetHistoricalBrokerDetailsResponseMsg res = das.getHistoricalBrokerDetails(req.getGetHistoricalBrokerDetailsRequest());
%>
<table class="table">
    <tr><th>Field</th><th>Value</th></tr>
    <%
        out.write("<tr><td>Average Active Consumers</td><td>" + res.getAverageactiveconsumers() + "</td></tr>");
        out.write("<tr><td>Average Consumers</td><td>" + res.getAverageconsumers() + "</td></tr>");
        out.write("<tr><td>Average Bytes In</td><td>" + res.getAveragebytesin() + "</td></tr>");
        out.write("<tr><td>Average Bytes Out</td><td>" + res.getAveragebytesout() + "</td></tr>");
        out.write("<tr><td>Average Bytes Dropped</td><td>" + res.getAveragebytesdropped() + "</td></tr>");
        out.write("<tr><td>Average Messages Sent</td><td>" + res.getAveragemessagesent() + "</td></tr>");
        out.write("<tr><td>Average Messages Recieved</td><td>" + res.getAveragemessagesrecieved() + "</td></tr>");
        out.write("<tr><td>Average Messages Dropped</td><td>" + res.getAveragemessagesdropped() + "</td></tr>");
        out.write("<tr><td>Average Queue Depth</td><td>" + res.getAveragequeuedepth() + "</td></tr>");
    %>
</table>
<%
    } catch (Exception ex) {
        out.write("There was an error processing your request: " + ex.getLocalizedMessage());
    }
%>
    </div>
</div>