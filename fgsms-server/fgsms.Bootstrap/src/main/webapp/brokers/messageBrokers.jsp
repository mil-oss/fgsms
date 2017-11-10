<%-- 
    Document   : messageBrokers.jsp
    Created on : May 6, 2011, 2:52:53 PM
    Author     : Alex
--%> 


<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.GetBrokerListResponse"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.GetBrokerListRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.ElevateSecurityLevelRequestMsg"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyResponseMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.PCS"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Properties"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="javax.xml.bind.JAXBElement"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ReportTypeContainer"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ArrayOfReportTypeContainer"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToHTMLResponseMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ExportDataRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ReportingService_Service"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ReportingService"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Calendar"%> 
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ReportType"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.GetMonitoredServiceListRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService_Service"%>
<%@page import="java.net.URL"%>
<% String thispage = "'brokers/messageBrokers.jsp'";
%>

<%

    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);


    DataAccessService das = pl.GetDAS(application, request, response);

//if postback, validate form, then set
    if (request.getMethod().equalsIgnoreCase("POST")) {
    }


%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Message Brokers</h1>
</div>
<div class="row-fluid">
    <div class="span12">




        This page provides a listing of registered message brokers, their current status, and a listing of general statistics as of the late reported data.
        Message broker registration is automatic after the appropriate FGSMS Broker agent has been deployed.<br><br>

        <%
            try {
                GetBrokerListRequestMsg req = new GetBrokerListRequestMsg();
                req.setClassification(c);
                GetBrokerListResponseMsg res = das.getBrokerList(req);
                DatatypeFactory df = DatatypeFactory.newInstance();
                if (res != null && !res.getBrokerList().isEmpty()) {
                    out.write("<table class=\"table\"><tr>"
                            + "<th>URI</th>"
                            + "<th>Last Checkin</th>"
                            + "<th>Operational?</th>"
                            + "<th>Topics/Queues</th>"
                            + "<th>Active Consumer</th>"
                            + "<th>Queue Depth</th>"
                            + "<th>Msg In</th>"
                            + "<th>Msg Out</th>"
                            + "<th>Msg Dropped</th>"
                            + "<th>Bytes in</th>"
                            + "<th>Bytes out</th>"
                            + "<th>Bytes dropped</th></tr>");
                    for (int i = 0; i < res.getBrokerList().size(); i++) {
                        try {
                            out.write("<tr><td>"
                                    + "<a class=btn href=\"javascript:loadpage('brokers/messageBrokerDetail.jsp?uri=" + URLEncoder.encode(res.getBrokerList().get(i).getUri()) + "','mainpane'," + thispage + ");\">" + Utility.encodeHTML(res.getBrokerList().get(i).getUri()) + "</a>"
                                    + "<br><a class=btn  href=\"javascript:loadpage('brokers/messageBrokerProfile.jsp?uri=" + URLEncoder.encode(res.getBrokerList().get(i).getUri()) + "','mainpane'," + thispage + ");\">Profile</a> | "
                                    + "<a class=btn  href=\"javascript:loadpage('brokers/messageBrokerHistory.jsp?uri=" + URLEncoder.encode(res.getBrokerList().get(i).getUri()) + "','mainpane'," + thispage + ");\">History</a> | "
                                    + "<a class=btn  href=\"javascript:loadpage('profile/permission.jsp?url=" + URLEncoder.encode(res.getBrokerList().get(i).getUri()) + "','mainpane'," + thispage + ");\">Permissions</a></td><td>");
                            GregorianCalendar time = (GregorianCalendar) res.getBrokerList().get(i).getLastCheckIn();
                            long diff = System.currentTimeMillis() - time.getTimeInMillis();


                            //if diff is over an hour, flag as red
                            if (diff > (60 * 60 * 1000)) {
                                out.write("<font color=#FF0000>");
                            } else if (diff > (10 * 60 * 1000)) {
                                //if diff is over 10 minutes, flag as orange
                                out.write("<font color=#FF7D40>");
                            } else {
                                out.write("<font>");
                            }
                            out.write(Utility.durationLargestUnitToString(df.newDuration(diff))
                                    //                out.write(res.getBrokerList().get(i).getLastCheckIn().toString()
                                    + "</font></td>");
                            if (res.getBrokerList().get(i).isOperational()) {
                                out.write("<td style=\"background-lime\">");
                            } else {
                                out.write("<td style=\"background-color:red\">");
                            }
                            out.write(res.getBrokerList().get(i).isOperational() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getNumberOfQueuesTopics() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getTotalactiveconsumers() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getTotalqueuedepth() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getTotalmessagesent() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getTotalmessagesrecieved() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getTotalmessagesdropped() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getTotalbytesin() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getTotalbytesout() + "</td><td>");
                            out.write(res.getBrokerList().get(i).getTotalbytesdropped() + "</td></tr>");
                        } catch (Exception ex) {
                        }
                    }
                } else {
                    out.write("<br>No results were returned. It's possible that there's no brokers that you have access to.");
                }
            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getLocalizedMessage());
            }
        %>
    </div>
</div>
