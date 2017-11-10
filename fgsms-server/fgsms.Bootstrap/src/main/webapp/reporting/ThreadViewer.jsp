<%--
    Document   : ThreadViewer
    Created on : Oct 2, 2011, 9:51:32 AM
    Author     : Alex
--%> 

<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>

<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<div class="well">
    <h1>Thread Viewer</h1>
</div>
<div class="row-fluid"> 
    <div class="span12">


When FGSMS's dependency injection feature is turned on (this is configured locally at each agent instance in fgsms-agent-core.jar (java) and in the machine.config (for .NET)) it becomes possible to 
track the lifecycle of a service request when chaining occurs. Chaining, also known as 'service as a client' or multihopping, is when a service calls one or more services.<br><br>
The following represents all known transactions for the selected thread of execution.<Br>

<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);


    String tid = "";
    if (Utility.stringIsNullOrEmpty(request.getParameter("ID"))) {
        response.sendRedirect("index.jsp");
        return;
    } else {

        tid = request.getParameter("ID");
    }
    
    DataAccessService dasport = pl.GetDAS(application, request, response);

    GetThreadTransactionsRequestMsg req = new GetThreadTransactionsRequestMsg();
    req.setClassification(c);
    req.setId(tid);
    GetThreadTransactionsResponseMsg res = null;
    try {
        res = dasport.getThreadTransactions(req);
    } catch (Exception ex) {
        out.write("There was an error processing your request. " + ex.getLocalizedMessage());
    }

    boolean ok = true;
    if (res == null) {
        ok = false;
    }

    if (ok && !res.getThreads().isEmpty()) { 
        out.write("<table class=\"table table-hover\" border=1><tr><th>ID</th><th>URL</th><th>Action</th><th>Success</th><th>Response Time</th><th>Timestamp</th></tr>");
        for (int i = 0; i < res.getThreads().size(); i++) {
            out.write("<tr><td><a href=\"javascript:loadpage('reporting/SpecificTransactionLogViewer.jsp?ID=" + res.getThreads().get(i).getTransactionid() + "','mainpane');\">" + res.getThreads().get(i).getTransactionid() + "</a></td>"
                    + "<td>" + res.getThreads().get(i).getUri() + "</td>"
                    + "<td>" + Helper.ToShortActionString(res.getThreads().get(i).getAction()) + "</td>"
                    + "<td>" + res.getThreads().get(i).isSuccess() + "</td>"
                    + "<td>" + res.getThreads().get(i).getResponsetimems() + "</td>"
                    + "<td>" + Utility.formatDateTime(res.getThreads().get(i).getTimestamp()) + "</td></tr>");
        }

        out.write("</table>");

    } else {
        out.write("No results were returned.");
    }

%>



    </div>
</div>