<%-- 
    Document   : availability
    Created on : Mar 15, 2011, 12:12:51 PM
    Author     : Alex
--%>

<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>

<%@page import="org.miloss.fgsms.presentation.*"%> 
<%@page import="java.util.Calendar"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.miloss.fgsms.services.interfaces.status.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<div class="well">
    <h1>Availability Records</h1>
</div>
<div class="row-fluid"> 
    <div class="span12">
        Showing records for the last 180 days
        <div style="float:left">
            <%
                GetOperationalStatusLogResponseMsg res = null;
                GetStatusResponseMsg res2 = null;
                Boolean currenstatus = null;
                Long currenttimestamp = null;

                SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
                ProxyLoader pl = ProxyLoader.getInstance(application);

                if (Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
                    response.sendRedirect("index.jsp");
                }
                org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory dasfac = new org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory();
                try {

                    DataAccessService dasport = pl.GetDAS(application, request, response);

                    GetOperationalStatusLogRequestMsg req = new GetOperationalStatusLogRequestMsg();
                    req.setClassification(c);
                    req.setURL(request.getParameter("url")); 
                    TimeRange now = new TimeRange();
                    long currentime = System.currentTimeMillis();
                    Calendar gc = GregorianCalendar.getInstance();
                    gc.setTimeInMillis(currentime);
                    Calendar gc2 = GregorianCalendar.getInstance();
                    gc2.setTimeInMillis(currentime);
                    //XMLGregorianCalendar nowtime = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gc);
                    gc2.add(Calendar.DATE, (-1 * 180));
                    //XMLGregorianCalendar endtime = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gc2);
                    now.setStart(gc);
                    now.setEnd(gc2);
                    req.setRange(now);


                    res = dasport.getOperationalStatusLog(req);


                    StatusService statusport = pl.GetSS(application, request, response);

                    GetStatusRequestMsg req2 = new GetStatusRequestMsg();
                    req2.setClassification(c);
                    req2.setURI(request.getParameter("url"));
                    res2 = statusport.getStatus(req2);
                    out.write("Showing Availibility Records for " + Utility.encodeHTML(request.getParameter("url")) + "<br>");
                    if (res != null && res.getOperationalRecord() != null && !res.getOperationalRecord().isEmpty()) {


              //          out.write("<Br><a href=\"performanceViewer.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "\">View Performance</a> | "
                  //              + " <a href=\"serviceprofile.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "\">View Profile</a> | "
                      //          + " <a href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(request.getParameter("url")) + "\">View Transaction Log</a><Br>");

                        out.write("<table border=1><tr><th>Timestamp</th><th>Operational</th><th>Message</th><th>Record ID</th></tr>");

                        if (res2 != null) {
                            out.write("<tr><td>" + Utility.formatDateTime(res2.getTimeStamp()) + "</td>");
                            out.write("<td>" + res2.isOperational() + "</td>");
                            out.write("<td>" + Utility.encodeHTML(res2.getMessage()) + "</td>");
                            out.write("<td>Last Known Status</td></tr>");
                        }
                        for (int i = res.getOperationalRecord().size() - 1; i >= 0; i--) {
                            out.write("<tr><td>" + Utility.formatDateTime(res.getOperationalRecord().get(i).getTimestamp()) + "</td>");
                            out.write("<td>" + res.getOperationalRecord().get(i).isOperational() + "</td>");
                            out.write("<td>" + Utility.encodeHTML(res.getOperationalRecord().get(i).getMessage()) + "</td>");
                            out.write("<td>" + res.getOperationalRecord().get(i).getID() + "</td></tr>");

                        }
                        out.write("</table>");
                    } else if ((res == null || res.getOperationalRecord() == null || res.getOperationalRecord().isEmpty())
                            && res2 != null) {
                        out.write("<table border=1><tr><th>Timestamp</th><th>Operational</th><th>Message</th><th>Record ID</th></tr>");
                        out.write("<tr>");
                        if (res2.getTimeStamp() != null) {
                            out.write("<td>" + Utility.formatDateTime(res2.getTimeStamp()) + "</td>");
                        } else {
                            out.write("<td>Unknown</td>");
                        }

                        out.write("<td>" + res2.isOperational() + "</td>");

                        out.write("<td>" + Utility.encodeHTML(res2.getMessage()) + "</td>");
                        out.write("<td>Last Known Status</td></tr></table>");

                    } else {
                        out.write("No records are available for this service.");
                    }
                } catch (Exception ex) {
                    out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font>");
                   LogHelper.getLog().log(Level.ERROR, "Error from availability.jsp", ex);
                }
            %>
        </div>
        <%
            if (res != null) {
        %>
        <script type="text/javascript">
            // Create the chart
            window.chart = new Highcharts.StockChart({
                chart: {
                    renderTo: 'chart'
                },
                yAxis: {
                   min:0 
                },
                rangeSelector: {
                    selected: 1
                },
                title: {
                    text: 'Availability'
                },
            
                series: [{
                        name: 'Availability',
                        //type: 'flags',
                        data: [
            <%
                for (int i = 0; i < res.getOperationalRecord().size(); i++) {
                    out.write("{x:" + res.getOperationalRecord().get(i).getTimestamp().getTimeInMillis() + ",");
                    out.write("y:" + (res.getOperationalRecord().get(i).isOperational() ? "1" : "0") + ",");
                    out.write("title:" + (res.getOperationalRecord().get(i).isOperational() ? "'OK'" : "'NG'") + ",");
                    out.write("color:" + (res.getOperationalRecord().get(i).isOperational() ? "'#00FF00'" : "'#FF0000'"));
                    out.write("}");
                    out.write(",");
                }
                if (!res.getOperationalRecord().isEmpty()) {
                    out.write("{x:" + res.getOperationalRecord().get(res.getOperationalRecord().size() - 1).getTimestamp().getTimeInMillis() + ",");
                    out.write("y:" + (res.getOperationalRecord().get(res.getOperationalRecord().size() - 1).isOperational() ? "1" : "0") + ",");
                    out.write("title:" + (res.getOperationalRecord().get(res.getOperationalRecord().size() - 1).isOperational() ? "'Current Status - OK'" : "'Current Status - NG'") + ",");
                    out.write("color:" + (res.getOperationalRecord().get(res.getOperationalRecord().size() - 1).isOperational() ? "'#00FF00'" : "'#FF0000'"));
                    out.write("}");
                } else {
                    //no records available
                    out.write("{x:" + System.currentTimeMillis() + ",");
                    out.write("y:" + "0" + ",");
                    out.write("title:" + "'No records available'" + ",");
                    out.write("color:" +"'#0000FF'" );
                    out.write("}");
                }
            %>

                            ]
                            ,
                            step: true,
                            tooltip: {
                                valueDecimals: 0
                            }
                        }]
                });
        
        
        </script>

        <%

            }


        %>
        <div style="float:right; height: 400px" id="chart">
        </div>

    </div>
</div>