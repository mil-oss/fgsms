
<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>

<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%> 
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="java.util.List"%>
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
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@include file="../csrf.jsp" %>
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
 
    if (!Utility.stringIsNullOrEmpty(request.getParameter("url"))) {

        try {

            DataAccessService dasport = pl.GetDAS(application, request, response);

            GetSLAFaultRecordsRequestMsg slar = new GetSLAFaultRecordsRequestMsg();
            slar.setClassification(c);
            slar.setURL(request.getParameter("url"));
            TimeRange now = new TimeRange();
            long currentime = System.currentTimeMillis();

            Calendar gc2 = GregorianCalendar.getInstance();
            gc2.setTimeInMillis(currentime);
            gc2.add(Calendar.MONTH, -1);
            
            Calendar gc = GregorianCalendar.getInstance();
            gc.setTimeInMillis(currentime);

            XMLGregorianCalendar nowtime = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gc);

            now.setStart(gc);
            now.setEnd(gc2);

            slar.setRange(now);
            List<GetSLAFaultRecordsResponseMsg> faults = dasport.getSLAFaultRecords(slar);
            if (faults != null && !faults.isEmpty()) {
                out.write("Show SLA Violations for service " + Utility.encodeHTML(request.getParameter("url")) + "<br>");

%>

<table border="1" class="table"><tr><th>SLA Incident ID</th><th>Fault Message</th><th>Related Transaction ID</th><th>Timestamp</th></tr>


    <%
                    for (int i = 0; i < faults.size(); i++) {
                        out.write("<tr><td>" + Utility.encodeHTML(faults.get(i).getIncidentID()));
                        out.write("</td><td>" + (Utility.encodeHTML(faults.get(i).getMessage())));
                        out.write("</td><td>");
                        if (!Utility.stringIsNullOrEmpty(faults.get(i).getRelatedTransaction())) {
                            out.write("<a href=\"javascript:loadpage('reporting/SpecificTransactionLogViewer.jsp?url="
                                    + URLEncoder.encode(request.getParameter("url")) + "&ID="
                                    + URLEncoder.encode(faults.get(i).getRelatedTransaction()) + "','mainpane');\">"
                                    + Utility.encodeHTML(faults.get(i).getRelatedTransaction()) + "</a>");
                        }
                        out.write("</td>");
                        out.write("<td>" + Utility.formatDateTime(faults.get(i).getTimestamp()) + "</td></tr>");
                    }
                    out.write("</table>");
                } else
                    out.write("No records were returned.");
            } catch (Exception ex) {
                out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font>");
                LogHelper.getLog().log(Level.ERROR, "Error from slarecords.jsp", ex);
            }
        }
    %>


