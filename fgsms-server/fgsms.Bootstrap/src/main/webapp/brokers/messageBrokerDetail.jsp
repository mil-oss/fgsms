<%-- 
    Document   : messageBrokerDetail
    Created on : May 9, 2011, 10:07:19 AM
    Author     : Alex
--%>


<%@page import="java.util.Calendar"%>
<%@page import="org.miloss.fgsms.common.Constants"%>

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
    //new ProxyLoader(application);
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
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Message Broker Details</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        This page displays the most up to date information for this message broker.<br>
        <%
            try {
                GetCurrentBrokerDetailsRequestMsg req = new GetCurrentBrokerDetailsRequestMsg();
                req.setClassification(c);
                req.setUrl(request.getParameter("uri"));
                GetCurrentBrokerDetailsResponseMsg res = das.getCurrentBrokerDetails(req);

                if (res != null && !res.getQueueORtopicDetails().isEmpty()) {
                    out.write("<h3>" + Utility.encodeHTML(request.getParameter("uri")) + "</h3>");
                    //<th>Agent Type</th>
                    out.write("<table class=\"table\"><tr><th>Name</th><th>type</th><th>Full Name</th><th>Active Consumers</th><th>Total Consumers</th>"
                            + "<th>Messages Out</th><th>Messages In</th><th>Dropped Messages</th><th>Queue Depth</th>"
                            + "<th>Bytes In</th><th>Bytes Out</th><th>Bytes Dropped</th><th>Recorded At</th></tr>");
                    for (int i = 0; i < res.getQueueORtopicDetails().size(); i++) {
                        out.write("<tr><td>" + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getName()) + "</td><td>");
                        out.write(Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getItemtype()) + "</td><td>");
                        out.write(Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getCanonicalname()) + "</td><td>");
                        // out.write(Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getAgenttype()) + "</td><td>");
                        out.write(res.getQueueORtopicDetails().get(i).getActiveconsumercount() + "</td><td>");
                        out.write(res.getQueueORtopicDetails().get(i).getConsumercount() + "</td><td>");
                        out.write(res.getQueueORtopicDetails().get(i).getMessagecount() + "</td><td>");
                        out.write(res.getQueueORtopicDetails().get(i).getRecievedmessagecount() + "</td><td>");
                        out.write(res.getQueueORtopicDetails().get(i).getMessagesdropped() + "</td><td>");
                        out.write(res.getQueueORtopicDetails().get(i).getQueueDepth() + "</td><td>");

                        out.write(res.getQueueORtopicDetails().get(i).getBytesin() + "</td><td>");
                        out.write(res.getQueueORtopicDetails().get(i).getBytesout() + "</td><td>");
                        out.write(res.getQueueORtopicDetails().get(i).getBytesdropped() + "</td><td>");

                        Calendar time = (GregorianCalendar) res.getQueueORtopicDetails().get(i).getTimestamp();
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
                        out.write(Utility.formatDateTime(res.getQueueORtopicDetails().get(i).getTimestamp()) + "</font></td></tr>");



                    }


                }

            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getLocalizedMessage());
            }
        %>
    </div></div>