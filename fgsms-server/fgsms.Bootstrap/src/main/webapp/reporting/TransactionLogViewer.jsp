<%--
    Document   : index
    Created on : Dec 9, 2010, 9:51:32 AM
    Author     : Alex
--%>

<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.Properties"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>

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
Transaction Log

<% 
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);
%>
<script type="text/javascript" language="javascript">
    function redir()
    {
        //params, url offset records and filter
        var x;
        var xval;
        var faultsonly = false;
        var slaonly = false;
        x = document.forms[0].elements["filter"];
        var size = x.length;
        
        for (var i=0; i< size; i++)
        {
            if (x[i].checked)
            {
                //   alert(x[i].value);
                if (x[i].value ==    "FaultedTransactions")
                    faultsonly = true;
                else if (x[i].value == "SLAViolations")
                    slaonly = true;
            }
        }

       

        


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
                //retain all original parameters
                if (para[i].indexOf("filter=") ==-1)
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
                    //ignore the one we are changing
                }
                i++;
                first=false;
            }
            //now add the one that we are changing
            if (slaonly)
                newurl += "&filter=SLAViolations";
            else if ( faultsonly)
                newurl += "&filter=FaultedTransactions";
        }
        else
        {
            if (slaonly)

            newurl = originalUrl + "?filter=SLAViolations";
        else if ( faultsonly)
            newurl = originalUrl + "?filter=FaultedTransactions";
    }
    window.location = newurl;
}
</script>

<%

    int offset = 0;
    int records = 100;
    boolean slaonly = false;
    boolean faultsonly = false;
    String url = "";
    if (Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
        response.sendRedirect("index.jsp");
        return;
    } else {
        url = URLDecoder.decode(request.getParameter("url"), Constants.CHARSET);
    }
    if (!Utility.stringIsNullOrEmpty(request.getParameter("records"))) {
        try {
            records = Integer.parseInt(request.getParameter("records"));
            if (records < 1 || records > 2000) {
                records = 100;
            }
        } catch (Exception ex) {
        }
    }

    out.write("Service URL: <a target=_blank href=\"" + URLEncoder.encode(url, Constants.CHARSET) + "\">" + Utility.encodeHTML(url) + "</a><br>");
    out.write("<a href=\"performanceViewer.jsp?url=" + URLEncoder.encode(url, Constants.CHARSET) + "\">View Performance</a> | "
            + " <a href=\"serviceprofile.jsp?url=" + URLEncoder.encode(url, Constants.CHARSET) + "\">View Profile</a> | "
            + " <a href=\"manage.jsp?url=" + URLEncoder.encode(url, Constants.CHARSET) + "\">Manage Policy</a> | "
            + "<a href=\"availability.jsp?url=" + URLEncoder.encode(url, Constants.CHARSET) + "\">Availability Log</a>"
            + "<Br>");
%>

<%
    if (!Utility.stringIsNullOrEmpty(request.getParameter("offset"))) {
        offset = Integer.parseInt(request.getParameter("offset"));
        if (offset < 0) {
            offset = 0;
        }
    }


    if (!Utility.stringIsNullOrEmpty(request.getParameter("records"))) {
        records = Integer.parseInt(request.getParameter("records"));
        if (records < 0) {
            records = 100;
        }
    }
%>
<%


    if (request.getParameter("filter") != null && !Utility.stringIsNullOrEmpty(request.getParameter("filter"))) {
        String t2 = request.getParameter("filter");



        if (t2.equalsIgnoreCase("FaultedTransactions")) {
            faultsonly = true;
%>
<input type="radio" id="AllTransactions" name="filter" onchange="javascript:redir();"  value="AllTransactions">All Transactions
<input type="radio" id="FaultedTransactions" name="filter"  onchange="javascript:redir();" checked="true" value="FaultedTransactions">Show only Faulted Transaction
<input type="radio" id="SLAViolations" name="filter"  onchange="javascript:redir();" value="SLAViolations">Show only SLA Violations
<%
} else if (t2.equalsIgnoreCase("SLAViolations")) {
    slaonly = true;
%>
<input type="radio" id="AllTransactions" name="filter"  onchange="javascript:redir();" value="AllTransactions">All Transactions
<input type="radio" id="FaultedTransactions" name="filter" onchange="javascript:redir();" value="FaultedTransactions">Show only Faulted Transaction
<input type="radio" id="SLAViolations" name="filter"  onchange="javascript:redir();" checked="true" value="SLAViolations">Show only SLA Violations
<%
} else {
%>
<input type="radio" id="AllTransactions" name="filter" onchange="javascript:redir();" checked="true" value="AllTransactions">All Transactions
<input type="radio" id="FaultedTransactions" name="filter" onchange="javascript:redir();" value="FaultedTransactions">Show only Faulted Transaction
<input type="radio" id="SLAViolations" name="filter" onchange="javascript:redir();" value="SLAViolations">Show only SLA Violations
<%     }

} else {
%>
<input type="radio" id="AllTransactions" name="filter" onchange="javascript:redir();" checked="true" value="AllTransactions">All Transactions
<input type="radio" id="FaultedTransactions" name="filter" onchange="javascript:redir();" value="FaultedTransactions">Show only Faulted Transaction
<input type="radio" id="SLAViolations" name="filter" onchange="javascript:redir();" value="SLAViolations">Show only SLA Violations
<%            }


    GetMessageLogsResponseMsg res = null;
    try {
 
        
        DataAccessService dasport = pl.GetDAS(application, request, response);

        GetRecentMessageLogsRequestMsg req1 = new GetRecentMessageLogsRequestMsg();
        req1.setClassification(c);
        req1.setURL(url);
        req1.setOffset(offset);
        req1.setRecords(records);
        TimeRange now = new TimeRange();
        long currentime = System.currentTimeMillis();
        req1.setSlaViolationsOnly(slaonly);
        req1.setFaultsOnly(faultsonly);
        res = dasport.getRecentMessageLogs(req1);
    } catch (Exception ex) {
        out.write("There was an error processing your request. " + ex.getLocalizedMessage());
        res = null;
    }

    int minwidth = 400;
    Cookie[] cs = request.getCookies();
    if (cs != null) {
        for (int i = 0; i < cs.length; i++) {
            if (cs[i].getName().equalsIgnoreCase("screenwidth")) {
                try {
                    int k = Integer.parseInt(cs[i].getValue());
                    if (k > 700) {
                        minwidth = (k - 50) / 2;  //this covers the page margins
                    }
                } catch (Exception ex) {
                }
            }

        }
    }
%>

<Br>


<div align="right">
    <%
        if (offset != 0 && (offset - 100) > 0) {
            if (slaonly) {
                out.write("<a href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(url) + "&records=" + records + "&offset=" + (offset - 100) + "&filter=SLAViolations\">Previous</a> ");
            } else if (faultsonly) {
                out.write("<a href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(url) + "&records=" + records + "&offset=" + (offset - 100) + "&filter=FaultedTransactions\">Previous</a> ");
            }
            out.write("<a href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(url) + "&records=" + records + "&offset=" + (offset - 100) + "\">Previous</a> ");
        } else if (offset != 0 && (offset - 100) <= 0) {
            if (slaonly) {
                out.write("<a href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(url) + "&records=" + records + "&filter=SLAViolations\">Previous</a> ");
            } else if (faultsonly) {
                out.write("<a href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(url) + "&records=" + records + "&filter=FaultedTransactions\">Previous</a> ");
            } else {
                out.write("<a href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(url) + "&records=" + records + "\">Previous</a> ");
            }
        } else {
            out.write("Previous");
        }
    %>

    <a href="TransactionLogViewer.jsp?url=<%=URLEncoder.encode(url)%>&offset=<%=offset + 100%>&records=<%=records%>"> Next</a></div>
    <%




        if (res != null
                && res.getLogs() != null
                
                && res.getLogs().getTransactionLog() != null
                && res.getLogs().getTransactionLog().size() > 0) {
            out.write("Total Records available: about " + res.getTotalRecords() + ".");
            out.write("<table border=1><tr>"
                    + "<th>Action</th>"
                    + "<th>Success/Fault</th>"
                    + "<th>Requestor Identity</th>"
                    + "<th>Response Time</th>"
                    + "<th>REQ/RES</th>"
                    + "<th>Timestamp</th>"
                    + "<th>SLA Violation</th>"
                    + "<th>Details</th></tr>");
            //data = res.getLogs().getValue().getTransactionLog();
            //session.setAttribute("fgsms.transactionlog" + URL, data);
            boolean colorflag = false;
            for (int i = 0; i < res.getLogs().getTransactionLog().size(); i++) {
                out.write("<tr ");
                colorflag = !colorflag;
                if (colorflag) {
                    out.write(" bgcolor=#EEFFEE><td>");
                } else {
                    out.write(" bgcolor=#DDFFDD><td>");
                }
                int clip = 0;
                /*if (res.getLogs().getValue().getTransactionLog().get(i).getAction().lastIndexOf("/") > clip) {
                clip = res.getLogs().getValue().getTransactionLog().get(i).getAction().lastIndexOf("/");
                }
                if (res.getLogs().getValue().getTransactionLog().get(i).getAction().lastIndexOf(":") > clip) {
                clip = res.getLogs().getValue().getTransactionLog().get(i).getAction().lastIndexOf(":");
                }
                if (res.getLogs().getValue().getTransactionLog().get(i).getAction().lastIndexOf("#") > clip) {
                clip = res.getLogs().getValue().getTransactionLog().get(i).getAction().lastIndexOf("#");
                }
                if (clip > 0) {
                out += (res.getLogs().getValue().getTransactionLog().get(i).getAction().substring(clip + 1));
                }*/
                if (Utility.stringIsNullOrEmpty(res.getLogs().getTransactionLog().get(i).getAction())) {
                    out.write("NA</td><td>");
                } else {
                    out.write(res.getLogs().getTransactionLog().get(i).getAction() + "</td><td>");
                }
                if (!res.getLogs().getTransactionLog().get(i).isIsFault()) {
                    out.write("<font color=#FF0000><b>Fault</b></font>");
                } else {
                    out.write("Success");
                }
                out.write("</td><td>" + Utility.encodeHTML(TransactionLogViewerData.ParseIdentities(res.getLogs().getTransactionLog().get(i).getIdentity()))
                        + "</td><td>" + res.getLogs().getTransactionLog().get(i).getResponseTime()
                        + "ms</td><td>");
                if (res.getLogs().getTransactionLog().get(i).isHasRequestMessage()) {
                    out.write("REQ ");
                }
                if (res.getLogs().getTransactionLog().get(i).isHasResponseMessage()) {
                    out.write("RES");
                }
                out.write("</td>");
                /*  if (res.getLogs().getValue().getTransactionLog().get(i).isHasRequestMessage()) {
                out += ("<a target=_blank href=\"MessageLogViewer.jsp?id=" + res.getLogs().getValue().getTransactionLog().get(i).getTransactionId()
                + "&request=true&url=" + URLEncoder.encode(URL, "UTF-8") + "\">Request " + res.getLogs().getValue().getTransactionLog().get(i).getRequestSize()
                + " bytes.</a>");
                } else {
                out += ("Request " + res.getLogs().getValue().getTransactionLog().get(i).getRequestSize() + " bytes.");
                }
                if (res.getLogs().getValue().getTransactionLog().get(i).isHasResponseMessage()) {
                out += ("<a target=_blank href=\"MessageLogViewer.jsp?id=" + res.getLogs().getValue().getTransactionLog().get(i).getTransactionId()
                + "&response=true&url=" + URLEncoder.encode(URL, "UTF-8") + "\"> Response "
                + res.getLogs().getValue().getTransactionLog().get(i).getResponseSize() + " bytes.</a>");
                } else {
                out += ("Response " + res.getLogs().getValue().getTransactionLog().get(i).getResponseSize() + " bytes.");
                }*/
                out.write("<td>" + Utility.formatDateTime(res.getLogs().getTransactionLog().get(i).getTimestamp())
                        + "</td><td>");
                if (res.getLogs().getTransactionLog().get(i).isIsSLAFault()) {
                    out.write("<font color=#FF0000><b>Fault</b></font>");
                }
                out.write("</td><td>"
                        + Utility.encodeHTML(res.getLogs().getTransactionLog().get(i).getSlaFaultMsg())
                        + "<a href=\"SpecificTransactionLogViewer.jsp?ID=" + res.getLogs().getTransactionLog().get(i).getTransactionId() + "\">Details</a>"
                        + "</td></tr></tr>");

            }


        } else {
            out.write("<br><Br>No records were returned.");
        }
    %>

