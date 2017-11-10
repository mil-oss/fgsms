<%-- 
    Document   : transactionFetcher
    Created on : May 18, 2011, 2:16:40 PM
    Author     : Alex 
--%>

<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>

<%@page import="javax.xml.datatype.DatatypeFactory"%> 
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Calendar"%>
<%@page import="javax.xml.datatype.Duration"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="javax.xml.bind.JAXBElement"%>
<%@page import="java.math.BigInteger"%>
<%@page import="java.util.UUID"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.net.URL"%>

<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<% 
   SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);
    DatatypeFactory df = DatatypeFactory.newInstance();

    try {
    
        DataAccessService dasport = pl.GetDAS(application, request, response);
        
        GetMessageLogsRequestMsg req = new GetMessageLogsRequestMsg();
        req.setClassification(c);

        if (!Utility.stringIsNullOrEmpty(request.getParameter("agent"))) {
            req.setAgentType(request.getParameter("agent"));
        }
        if (!Utility.stringIsNullOrEmpty(request.getParameter("monitorhostname"))) {
            req.setMonitorhostname(request.getParameter("monitorhostname"));
        }
        if (!Utility.stringIsNullOrEmpty(request.getParameter("servicehostname"))) {
            req.setServicehostname(request.getParameter("servicehostname"));
        }
        req.setOffset(0);
        //
        req.setRecords(100);
        try {
            if (!Utility.stringIsNullOrEmpty(request.getParameter("records"))) {
                req.setRecords(Integer.parseInt(request.getParameter("records")));
            }

        } catch (Exception ex) {
        }
        String todate = "";
        String fromdate = "";
        if (!Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
            req.setURL(request.getParameter("url"));
        }
        if (!Utility.stringIsNullOrEmpty(request.getParameter("todate"))) {
            todate = request.getParameter("todate");
        }
        if (!Utility.stringIsNullOrEmpty(request.getParameter("fromdate"))) {
            fromdate = request.getParameter("fromdate");
        }

        if (!Utility.stringIsNullOrEmpty(request.getParameter("filter"))) {
            String t = request.getParameter("filter").trim();
            if (t.equalsIgnoreCase("FaultedTransactions")) {
                req.setFaultsOnly(true);
            }
            if (t.equalsIgnoreCase("SLAViolations")) {
                req.setSlaViolationsOnly(true);
            }
        }
        if (Utility.stringIsNullOrEmpty(todate) || Utility.stringIsNullOrEmpty(fromdate)) {
            out.write("A date range must be specified.");

        } else {
            TimeRange r = new TimeRange();
            GregorianCalendar t1 = new GregorianCalendar(
                    Integer.parseInt(fromdate.split("/")[2]),
                    Integer.parseInt(fromdate.split("/")[0]) - 1,
                    Integer.parseInt(fromdate.split("/")[1]));
            //XMLGregorianCalendar t11 = df.newXMLGregorianCalendar(t1);
            r.setStart(t1);

            //r = new org.miloss.fgsms.services.interfaces.reportingservice.TimeRange();
            t1 = new GregorianCalendar(
                    Integer.parseInt(todate.split("/")[2]),
                    Integer.parseInt(todate.split("/")[0]) - 1,
                    Integer.parseInt(todate.split("/")[1]));
            //t11 =  df.newXMLGregorianCalendar(t1);
            r.setEnd(t1);
            req.setRange(r);



            //faults only
            //sla faults only
            //offset

            GetMessageLogsResponseMsg res = dasport.getMessageLogsByRange(req);
            if (res != null && !res.getLogs().getTransactionLog().isEmpty()) {
                out.write("Total Records available: " + res.getTotalRecords() + " between " + Utility.formatDateTime(req.getRange().getStart()) + " and " + Utility.formatDateTime(req.getRange().getEnd())
                        + ". Displaying the last " + res.getLogs().getTransactionLog().size() + " records.");
                out.write("<table class=\"table table-hover\"><tr>"
                        + "<th>URL</th>"
                        + "<th>Action</th>"
                        + "<th>Success/Fault</th>"
                        + "<th>Requestor Identity</th>"
                        + "<th>Response Time</th>"
                      
                        + "<th>Timestamp</th>"
                        + "<th>SLA Violation</th>"
                        + "<th>Details</th></tr>");

                for (int i = 0; i < res.getLogs().getTransactionLog().size(); i++) {
                    out.write("<tr><td>");
                    out.write(Utility.encodeHTML(
                            res.getLogs().getTransactionLog().get(i).getURL()) + "</td><td>");
                   
                    if (Utility.stringIsNullOrEmpty(res.getLogs().getTransactionLog().get(i).getAction())) {
                        out.write("NA</td><td>");
                    } else {
                        out.write(Utility.encodeHTML(res.getLogs().getTransactionLog().get(i).getAction()) + "</td><td>");
                    }
                    if (!res.getLogs().getTransactionLog().get(i).isIsFault()) {
                        out.write("<font color=#FF0000><b>Fault</b></font>");
                    } else {
                        out.write("Success");
                    }
                    out.write("</td><td>" + Utility.encodeHTML(TransactionLogViewerData.ParseIdentities(res.getLogs().getTransactionLog().get(i).getIdentity()))
                            + "</td><td>" + res.getLogs().getTransactionLog().get(i).getResponseTime()
                            + "ms</td>");
                    /*if (res.getLogs().getTransactionLog().get(i).isHasRequestMessage()) { 
                        out.write("REQ ");
                    }
                    if (res.getLogs().getTransactionLog().get(i).isHasResponseMessage()) {
                        out.write("RES");
                    }*/
                    //out.write("</td>");
                    /*  if (res.getLogs().getTransactionLog().get(i).isHasRequestMessage()) {
                    out += ("<a target=_blank href=\"MessageLogViewer.jsp?id=" + res.getLogs().getTransactionLog().get(i).getTransactionId()
                    + "&request=true&url=" + URLEncoder.encode(URL, "UTF-8") + "\">Request " + res.getLogs().getTransactionLog().get(i).getRequestSize()
                    + " bytes.</a>");
                    } else {
                    out += ("Request " + res.getLogs().getTransactionLog().get(i).getRequestSize() + " bytes.");
                    }
                    if (res.getLogs().getTransactionLog().get(i).isHasResponseMessage()) {
                    out += ("<a target=_blank href=\"MessageLogViewer.jsp?id=" + res.getLogs().getTransactionLog().get(i).getTransactionId()
                    + "&response=true&url=" + URLEncoder.encode(URL, "UTF-8") + "\"> Response "
                    + res.getLogs().getTransactionLog().get(i).getResponseSize() + " bytes.</a>");
                    } else {
                    out += ("Response " + res.getLogs().getTransactionLog().get(i).getResponseSize() + " bytes.");
                    }*/
                    out.write("<td>" + Utility.formatDateTime(res.getLogs().getTransactionLog().get(i).getTimestamp())
                            + "</td><td>");
                    if (res.getLogs().getTransactionLog().get(i).isIsSLAFault()) {
                        out.write("<font color=#FF0000><b>Fault</b></font>");
                    }
                    out.write("</td><td>"
                            + Utility.encodeHTML(res.getLogs().getTransactionLog().get(i).getSlaFaultMsg())
                            + "<a href=\"javascript:showModalMessageDetails('" + URLEncoder.encode(res.getLogs().getTransactionLog().get(i).getTransactionId(), "UTF-8") + "');\">Details</a>"
                            + "</td></tr></tr>");

                }
            } else {
                out.write("<br><Br>No records were returned.");
            }
        }
    } catch (Exception ex) {
        out.write("There was an error processing your request: " + ex.getLocalizedMessage());
        LogHelper.getLog().log(Level.WARN, "error caught transaction fetcher", ex);

    }
%>
