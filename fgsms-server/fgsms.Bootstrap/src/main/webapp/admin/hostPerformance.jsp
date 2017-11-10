<%-- 
    Document   : hostPerformance
    Created on : Apr 11, 2011, 3:33:44  PM
    Author     : Alex
--%>

<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="java.beans.Encoder"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>

<%@include file="../csrf.jsp" %>

<div class="well">
    <h1>Host Monitoring</h1>
    <p><a class="btn btn-primary btn-large">Learn more &raquo;</a></p>
</div>
<div class="row-fluid">
    <div class="span12">

<% 
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);

%>
<script type="text/javascript" language="javascript">
    function redir()
    {
        var x = document.getElementById("currentOffset");
        var xval = x.value;
        if (xval <= 0)
            xval = 30;

        var originalUrl=window.location;
        var newurl;
        var urlwithoutParameters;
        var parameters;
        var para;
        if (originalUrl.toString().indexOf("?") > -1)
        {
            urlwithoutParameters = originalUrl.toString().substr(0, originalUrl.toString().indexOf("?"));
            parameters = originalUrl.toString().split("?")[1];
            para = parameters.toString().split("&");
            newurl = urlwithoutParameters + "?";
            var i =0;
            var first = true;
            while (i < para.length)
            {
                if (para[i].indexOf("days=") ==-1)
                {
                    if (first ==true){
                        newurl += para[i];
                    }
                    else
                    {
                        newurl += "&" + para[i];
                    }
                }
                else {
                }
                i++;
                first=false;
            }
            if (para.length==1 && (para[0].indexOf("days=") >=0))
                newurl += "days=" + x.value;
            else
                newurl += "&days=" + x.value;
        }
        else
            newurl = originalUrl + "?days=" + x.value;
        window.location = newurl;
    }
</script>
Showing data for the past <%
    int date3 = 30;
    try {
        date3 = Integer.parseInt(request.getParameter("days"));
    } catch (Exception ex) {
    }
    if (date3 <= 0) {
        date3 = 30;
    }
    out.write("<input id=currentOffset length=4 type=text value=\"" + date3 + "\" onchange=\"javascript:redir()\">");

%> days<br><br>

<%
    out.write("<h3>" + Utility.encodeHTML(request.getParameter("Host")) + "</h3>");
    try {
        DataAccessService dasport = pl.GetDAS(application, request, response);
        if (!Utility.stringIsNullOrEmpty(request.getParameter("Host"))) {
            GetPerformanceAverageHostStatsRequestMsg req = new GetPerformanceAverageHostStatsRequestMsg();
            req.setClassification(c);
            req.setHostname(request.getParameter("Host"));
            TimeRange now = new TimeRange();
            long currentime = System.currentTimeMillis();
            Calendar gc2 = GregorianCalendar.getInstance();
            if (Utility.stringIsNullOrEmpty(request.getParameter("days"))) {
                gc2 = GregorianCalendar.getInstance();
                gc2.setTimeInMillis(currentime);
                gc2.add(Calendar.MONTH, -1);
                
                // out.write("hello");
            } else {
                int days = 30;
                try {
                    days = Integer.parseInt(request.getParameter("days"));
                    //    out.write("ok");
                } catch (Exception ex) {
                    out.write(ex.getLocalizedMessage());
                }
                //out.write("days = " + days);
                if (days <= 0) {
                    days = 30;
                }
                
                gc2.setTimeInMillis(currentime);
                gc2.add(Calendar.DATE, (days * (-1)));
                
            }

            // out.write("<br>time " + currentime);
            Calendar gc = GregorianCalendar.getInstance();
            gc.setTimeInMillis(currentime);

            

            now.setStart(gc2);
            now.setEnd(gc);
            req.setRange(now);



            GetPerformanceAverageStatsResponseMsg res = dasport.getPerformanceAverageHostingStats(req);

            if (res != null) {
                out.write("<table  class=\"table\"><tr><th>Service URL</th><th>Successful Invocations</th><th>Failed Invocations</th><th>Average Response Time</th><th>SLA Violations</th></tr>");

                out.write("<tr><td>"
                        + Utility.encodeHTML(request.getParameter("Host"))
                        + "</td><td>" + res.getSuccessfulInvocations()
                        + "</td><td>" + res.getFailingInvocations()
                        + "</td><td>" + res.getAverageResponseTime()
                        + "</td><td>"
                        + res.getServiceLevelAgreementViolations()
                        + "</td>"
                        + "</tr>");

                out.write("</table>");


            }
        } else {
            response.sendRedirect("index.jsp");
        }
    } catch (Exception ex) {
        out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
        LogHelper.getLog().log(Level.WARN, "Error caught rendering host performance data", ex);
    }
%>
    </div>
</div>