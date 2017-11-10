<%-- 
    Document   : brokerProfile
    Created on : May 10, 2011, 12:57:39 PM
    Author     : Alex 
--%>


<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>

<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
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

Broker Profile
 

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
    GetCurrentBrokerDetailsResponseMsg res = null;

    try {
        GetCurrentBrokerDetailsRequestMsg req = new GetCurrentBrokerDetailsRequestMsg();
        req.setClassification(c);
        req.setUrl(request.getParameter("uri"));
        res = das.getCurrentBrokerDetails(req);
    } catch (Exception ex) {
        out.write("There was an error processing your request: " + ex.getLocalizedMessage());
        LogHelper.getLog().log(Level.WARN, "error caught", ex);
    }



    if (res != null && !res.getQueueORtopicDetails().isEmpty()) {
%>

<script  type="text/javascript" >
   
    var data = new Array();

    <%
            //   out.write("<h3>" + Utility.encodeHTML(res.getUri()) + "</h3>");
            //<th>Agent Type</th>
            //  out.write("<table border=1><tr><th>Name</th><th>type</th><th>Full Name</th><th>Active Consumers</th><th>Total Consumers</th>"
            //         + "<th>Total Messages</th><th>Recieved Messages</th><th>Queue Depth</th><th>Recorded At</th></tr>");
            for (int i = 0; i < res.getQueueORtopicDetails().size(); i++) {
                out.write("data[" + i + "] = \"<table><tr><td>Name</td><td>" + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getName()) + "</td></tr><tr><td>");
                out.write("Type</td><td>" + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getItemtype()) + "</td></tr><tr><td>");
                out.write("Canonical Name</td><td>" + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getCanonicalname()).replace(",", ",<Br>") + "</td></tr><tr><td>");
                out.write("Agent Type</td><td>" + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getAgenttype()) + "</td></tr><tr><td>");
                out.write("Active Consumers</td><td>" + res.getQueueORtopicDetails().get(i).getActiveconsumercount() + "</td></tr><tr><td>");
                out.write("Consumer Count</td><td>" + res.getQueueORtopicDetails().get(i).getConsumercount() + "</td></tr><tr><td>");
                out.write("Messages Out Count</td><td>" + res.getQueueORtopicDetails().get(i).getMessagecount() + "</td></tr><tr><td>");
                out.write("Messages In Count</td><td>" + res.getQueueORtopicDetails().get(i).getRecievedmessagecount() + "</td></tr><tr><td>");
                out.write("Messages Dropped</td><td>" + res.getQueueORtopicDetails().get(i).getMessagesdropped() + "</td></tr><tr><td>");
                out.write("Queue Depth</td><td>" + res.getQueueORtopicDetails().get(i).getQueueDepth() + "</td></tr><tr><td>");
                out.write("Bytes Dropped</td><td>" + res.getQueueORtopicDetails().get(i).getBytesdropped() + "</td></tr><tr><td>");
                out.write("Bytes In</td><td>" + res.getQueueORtopicDetails().get(i).getBytesin() + "</td></tr><tr><td>");
                out.write("Bytes Out</td><td>" + res.getQueueORtopicDetails().get(i).getBytesout() + "</td></tr><tr><td>");


                out.write("Record at</td><td>" + Utility.formatDateTime(res.getQueueORtopicDetails().get(i).getTimestamp()) + "</td></tr><tr><td>\";");
                out.write("\n");


            }
            // throw new Exception ("No results were returned");
        }



    %>
</script>

<script language="javascript" type="text/javascript" >
    function showDataItem(index)
    {

        $("#detailsdiv").html(data[index]);
        //document.getElementById("detailsdiv").innerHTML = data[index];
    }


</script>


<%//startAjaxCalls();
%>
<table width="40%" border="1">
    <tr>
        <td valign="top" ><h2>URL: <%=Utility.encodeHTML(request.getParameter("uri"))%></h2>
            Queues
            <%
                if (res != null || !res.getQueueORtopicDetails().isEmpty()) {
                    out.write("<Table class=\"table\"><tr><th>Name</th><th>Depth</th></tr>");
                    for (int i = 0; i < res.getQueueORtopicDetails().size(); i++) {
                        if (res.getQueueORtopicDetails().get(i).getItemtype().equalsIgnoreCase("queue")) {
                            out.write("<td><a title=\"" + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getCanonicalname()) + "\""
                                    + " onmouseover='javascript:showDataItem(" + i + ")'>"
                                    + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getCanonicalname()) + "</td><td>"
                                    + res.getQueueORtopicDetails().get(i).getQueueDepth() + "</td></tr>");
                        }
                    }
                    out.write("</table>");
                }
                //Click to show details of queue over time, new page<Br>
%>






        </td>

    </tr>
    <tr>
        <td valign="top" width="40%">Topics<br>
            <%
                if (res != null || !res.getQueueORtopicDetails().isEmpty()) {
                    out.write("<table><tr><th>Name</th><th>Message Count</th></tr>");
                    for (int i = 0; i < res.getQueueORtopicDetails().size(); i++) {
                        if (!res.getQueueORtopicDetails().get(i).getItemtype().equalsIgnoreCase("queue")) {
                            out.write("<td><a title=\"" + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getCanonicalname()) + "\""
                                    + "   onmouseover='javascript:showDataItem(" + i + ")' >"
                                    + Utility.encodeHTML(res.getQueueORtopicDetails().get(i).getCanonicalname()) + "</td><td>"
                                    + res.getQueueORtopicDetails().get(i).getMessagecount() + "</td></tr>");
                        }
                    }
                    out.write("</table><Br><Br>");
                }
            %>

        </td>

    </tr>


</table>

<div id="detailsdiv" style="width: 600; text-align: right; right:0px; top:150px; position: fixed; z-index: 1000; overflow: auto" name="detailsdiv">Shows details of the currently select queue</div>
<div style="right:0px; bottom:0px; position: fixed; z-index: 1000">

</div>

