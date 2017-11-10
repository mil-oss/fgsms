<%-- 
Document   : machinePerformance
Created on : Feb 5, 2012, 11:29:32 AM
Author     : Administrator
--%>

<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
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
    <h1>Process Performance Information</h1>
</div>
<div class="row-fluid">
    <div class="span2">
 
        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);
            try {
                DataAccessService dasport = pl.GetDAS(application, request, response);
                if (Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
                    response.sendRedirect("index.jsp");
                }
                GetMostRecentProcessDataRequestMsg req = new GetMostRecentProcessDataRequestMsg();
                req.setUri(request.getParameter("uri"));
                req.setClassification(c);
                GetMostRecentProcessDataResponseMsg res = dasport.getMostRecentProcessData(req);
                if (res != null && res.getPerformanceData() != null) {

                    DecimalFormat nf = new DecimalFormat("###.##");
                    out.write("<h1>" + Utility.encodeHTML(res.getPerformanceData().getUri()) + "</h1>");

                    out.write("<table class=\"table\">");
                    //+ "    <tr><td>Machine Hostname</td><td>" + Utility.encodeHTML(res.getPerformanceData().getHostname()) + "</td></tr>");
                    //               out.write("<tr><td>Domain</td><td>" + Utility.encodeHTML(res.getMachineData().getDomainName()) + "</td></tr>");
                    //  out.write("<tr><td>Policy URI</td><td>"+ "</td></tr>");
                    out.write("<tr><td>Operational</td><td>" + res.getPerformanceData().isOperationalstatus() + "</td></tr>");
                    out.write("<tr><td>Status Message</td><td>" + Utility.encodeHTML(res.getPerformanceData().getStatusmessage()) + "</td></tr>");

                    if (res.getPerformanceData().getBytesusedMemory() != null) {
                        out.write("<tr><td>Memory in use (bytes)</td><td>" + res.getPerformanceData().getBytesusedMemory() + "</td></tr>");


                    }

                    if (res.getPerformanceData().getNumberofActiveThreads() != null) {
                        out.write("<tr><td>Active Threads </td><td>" + res.getPerformanceData().getNumberofActiveThreads() + "</td></tr>");
                    }
                    {
                        out.write("<tr><td>Open Files</td><td>" + res.getPerformanceData().getOpenFileHandles() + "</td></tr>");
                    }
                    if (res.getPerformanceData().getPercentusedCPU() != null) {
                        out.write("<tr><td>CPU</td><td>" + res.getPerformanceData().getPercentusedCPU() + "</td></tr>");
                    }
                    if (res.getPerformanceData().getTimestamp() != null) {
                        long diff = System.currentTimeMillis() - res.getPerformanceData().getTimestamp().getTimeInMillis();
                        DatatypeFactory df = DatatypeFactory.newInstance();

                        out.write("<tr><td>Last Updated At</td><td>" + Utility.formatDateTime(res.getPerformanceData().getTimestamp()) + " "
                                + Utility.durationToString(df.newDuration(diff))
                                + "</td></tr>");
                    }
                }
            } catch (Exception ex) {
                out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());

            }


        %>

    </div>
    <div class="span10" style="min-width: 600px">
        <div id="percentscale" style="width:600px; height:300px">

        </div>   
        <div id="threadsfilecharts"  style="width:600px; height:300px">

        </div>





    </div>


</div>
<script type="text/javascript">
    var since = Date.now() - (60 *60 * 1000);
    createProcessCharts('<%=StringEscapeUtils.escapeEcmaScript(request.getParameter("uri"))%>','precentsale','threadsfilecharts',since);
</script>