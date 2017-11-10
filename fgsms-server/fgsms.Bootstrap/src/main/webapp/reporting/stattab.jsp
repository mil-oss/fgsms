<%-- 
    Document   : stattab 
    Created on : Dec 15, 2011, 5:38:48 PM
    Author     : Administrator
--%>


<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.PolicyType"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.status.*"%>

<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="javax.xml.datatype.Duration"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormatSymbols"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>

<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd"> 
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Transactional Service Statistics</h1>
    <p>Performance statistics by method or action of a web service.</p>
</div>
<div class="row-fluid">
    <div class="span12">

 
        <script>
            $(function () {
                window.console && console.log('tabify');
                $("#tabs").tabs();
            });
        </script>

        <%    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);
            if (Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
                response.sendRedirect("index.jsp");
                return;
            }
            try {

                DataAccessService dasport = pl.GetDAS(application, request, response);

                GetQuickStatsRequestMsg req2 = new GetQuickStatsRequestMsg();
                req2.setClassification(c);
                req2.setUri(request.getParameter("uri"));

                GetQuickStatsResponseMsg res2 = dasport.getQuickStats(req2);

                DatatypeFactory fac = DatatypeFactory.newInstance();
                if (res2 != null && !res2.getQuickStatWrapper().isEmpty()) {
                    out.write("<h2>" + Utility.encodeHTML(request.getParameter("uri")) + "</h2>");
                    out.write("<div id=\"tabs\"><ul>");

                    //output headers
                    for (int i = 0; i < res2.getQuickStatWrapper().size(); i++) {
                        out.write("<li><a href=\"#tabs-" + (i + 1) + "\">" + Helper.ToShortActionString(res2.getQuickStatWrapper().get(i).getAction()) + "</a></li>");
                    }
                    out.write("</ul>");

                    //output data
                    for (int i2 = 0; i2 < res2.getQuickStatWrapper().size(); i2++) {

                        out.write("<div id=\"tabs-" + (i2 + 1) + "\">    <p>");
                        out.write(Utility.encodeHTML(res2.getQuickStatWrapper().get(i2).getAction()));
                        out.write("<table  border=1><tr>"
                                + "<th>Range(min)</th>"
                                + "<th>Availability</th>"
                                + "<th>Avg Res(ms)</th>"
                                + "<th>Success</th>"
                                + "<th>Faults</th>"
                                + "<th>SLA Fault</th>"
                                + "<th>MTBF</th>"
                                + "<th>Max Req Size</th>"
                                + "<th>Max Res Size</th>"
                                + "<th>Max Res (ms)</th>"
                                + "<th>Updated at </th></tr>");
                        DecimalFormat myFormatter = new DecimalFormat("###.##");
                        for (int k = 0; k < res2.getQuickStatWrapper().get(i2).getQuickStatData().size(); k++) {
                            out.write("<tr>");
                            Duration d = fac.newDuration(res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getTimeInMinutes().longValue() * 60 * 1000);
                            out.write("<td>" + Helper.DurationToString(d));
                            //out.write("<td>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getTimeInMinutes().toString());
                            out.write("</td><td>");
                            out.write(myFormatter.format(res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAvailabilityPercentage()));
                            out.write("%</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageResponseTime());
                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSuccessCount());
                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getFailureCount());
                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSLAViolationCount());
                            out.write("</td><td align=center>" + Helper.DurationToString(res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getMTBF()));
                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getMaximumRequestSize());
                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getMaximumResponseSize());
                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getMaximumResponseTime());
                            out.write("</td><td");

                            long diff = System.currentTimeMillis() - res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getUpdatedAt().getTimeInMillis();;
                            if (diff > (60 * 60 * 1000)) {
                                out.write(" class=alert");
                            } else if (diff > (10 * 60 * 1000)) {
                                out.write(" class=warn");
                            }
                            out.write(">" + Helper.DurationToString(fac.newDuration(diff)));
                            out.write("</td></tr>");

                        }
                        out.write("</table></p></div>");

                    }
                    out.write("</div>");
                }
            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getMessage());
            }
        %>

    </div>
</div>






