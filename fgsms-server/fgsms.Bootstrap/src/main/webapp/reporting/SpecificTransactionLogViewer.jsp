<%--
    Document   : SpeficiTransactionLogViewer 
    Created on : Dec 9, 2010, 9:51:32 AM
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
    <h1>Transaction Viewer</h1>
</div>  
<div class="row-fluid">
    <div class="span12">

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


            GetMessageTransactionLogDetailsResponseMsg res = null;
            try {
                DataAccessService dasport = pl.GetDAS(application, request, response);

                GetMessageTransactionLogDetailsMsg req = new GetMessageTransactionLogDetailsMsg();
                req.setClassification(c);
                req.setTransactionID(tid);


                res = dasport.getMessageTransactionLogDetails(req);
            } catch (Exception ex) {
                out.write("There was an error processing your request. " + ex.getLocalizedMessage());
            }

            boolean ok = true;
            if (res == null) {
                ok = false;
            }

            if (ok) {

        %>
        <div>
            <table width="100%" border="1"><tr width="100%"><td width="50%">
                        <table class="table table-hover"  valign="top">
                            <tr><td>Action</td><td><%=Utility.encodeHTML(res.getAction())%></td></tr>
                            <tr><td>Request size</td><td><%=res.getRequestSize()%> bytes</td></tr>
                            <tr><td>Response size</td><td><%=res.getResponseSize()%> bytes</td></tr>
                            <tr><td>Response Time</td><td><%=res.getResponseTime()%> ms</td></tr>
                            <tr><td>Timestamp</td><td><%=Utility.formatDateTime(res.getTimestamp())%></td></tr>
                            <tr><td>Consumer</td><td><%
                                if (res.getIdentity() != null && !res.getIdentity().isEmpty()) {
                                    for (int i = 0; i < res.getIdentity().size(); i++) {
                                        out.write(res.getIdentity().get(i) + " ");
                                    }
                                }
                                    %></td></tr>
                            <tr><td>Fault</td><td><%=res.isIsFault()%></td></tr>
                            <tr><td>Agent Memo</td><td><%=Utility.encodeHTML(res.getAgentMemo())%></td></tr>
                            <tr><td>Request Headers</td><td><%
                                if (!res.getHeadersRequest().isEmpty()) {
                                    for (int i = 0; i < res.getHeadersRequest().size(); i++) {
                                        out.write(Utility.encodeHTML(res.getHeadersRequest().get(i).getName()) + "=");
                                        for (int a = 0; a < res.getHeadersRequest().get(i).getValue().size(); a++) {
                                            out.write(Utility.encodeHTML(res.getHeadersRequest().get(i).getValue().get(a)) + ";");
                                        }
                                        out.write("<br>");
                                    }

                                }

                                    %></td></tr>

                        </table>
                    </td><td width="50%">

                        <table valign="top" class="table table-hover">
                            <tr><td>Transaction ID</td><td><%=Utility.encodeHTML(res.getTransactionId())%></td></tr>
                            <tr><td>Thread ID</td><td><%
                                if (!Utility.stringIsNullOrEmpty(res.getTransactionthreadId())) {
                                    out.write("<a href=\"javascript:loadpage('reporting/ThreadViewer.jsp?ID=" + URLEncoder.encode(res.getTransactionthreadId()) + "','mainpane');\">" + Utility.encodeHTML(res.getTransactionthreadId()) + "</a>");
                                }


                                    %></td></tr>
                            <tr><td>Related Transaction ID</td><td>
                                    <%
                                        if (!Utility.stringIsNullOrEmpty(res.getRelatedTransactionID())) {
                                            out.write("<a href=\"javascript:loadpage('reporting/SpecificTransactionLogViewer.jsp?ID=" + URLEncoder.encode(res.getRelatedTransactionID()) + "','mainpane');\">" + Utility.encodeHTML(res.getRelatedTransactionID()) + "</a>");
                                        }


                                    %>
                                </td></tr>
                            <tr><td>SLA Fault</td><td><%=res.isIsSLAFault()%></td></tr>
                            <tr><td>SLA Fault Message</td><td><%=Utility.encodeHTML(res.getSlaFaultMsg())%></td></tr>
                            <tr><td>Original Request URL</td><td><%=Utility.encodeHTML(res.getOriginalRequestURL())%></td></tr>
                            <tr><td>Monitor</td><td><%=Utility.encodeHTML(res.getMonitorHostname())%></td></tr>
                            <tr><td>Host</td><td><%=Utility.encodeHTML(res.getServiceHostname())%></td></tr>
                            <tr><td>Agent</td><td><%=Utility.encodeHTML(res.getAgentType())%></td></tr>


                            <tr><td>Response Headers</td><td><%
                                if (!res.getHeadersResponse().isEmpty()) {
                                    for (int i = 0; i < res.getHeadersResponse().size(); i++) {
                                        out.write(Utility.encodeHTML(res.getHeadersResponse().get(i).getName()) + "=");
                                        for (int a = 0; a < res.getHeadersResponse().get(i).getValue().size(); a++) {
                                            out.write(Utility.encodeHTML(res.getHeadersResponse().get(i).getValue().get(a)) + ";");
                                        }
                                        out.write("<br>");
                                    }

                                }

                                    %></td></tr>
                        </table>
                    </td>
                </tr>

            </table>
        </div>
        <div style="overflow: auto; width:50%; position: relative; bottom: 0px; left: 0px; float: left;" >
            <h3>Request</h3>
            <%
                if (Utility.stringIsNullOrEmpty(res.getXmlRequestMessage())) {
                    out.write("Not available.");
                } else {
                    out.write("<pre>" + Helper.PrettyPrintXMLToHtml(res.getXmlRequestMessage()) + "</pre>");
                }

            %>
        </div>


        <div style="overflow: auto; width:50%; position: relative; bottom: 0px; right: 0px; float: right;">
            <h3>Response</h3>
            <%
                if (Utility.stringIsNullOrEmpty(res.getXmlResponseMessage())) {
                    out.write("Not available");
                } else {
                    out.write("<pre>" + Helper.PrettyPrintXMLToHtml(res.getXmlResponseMessage()) + "</pre>");
                }

            %>

        </div>



        <%

            } else
                out.write("No results were returned.");

        %>


    </div>
</div>