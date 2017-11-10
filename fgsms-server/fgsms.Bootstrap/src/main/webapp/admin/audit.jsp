<%-- 
    Document   : audit
    Created on : May 3, 2011, 2:58:48 PM
    Author     : Alex
--%>


<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
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
<div class="well">
    <h1>Audit Logs</h1>
    <p><a class="btn btn-primary btn-large" >Learn more &raquo;</a></p>
</div>
<div class="row-fluid">
    <div class="span12">
 

        FGSMS includes an audit logging system that records actions to and from the FGSMS web services.<br><br>
        <a class="btn btn-primary" href="javascript:loadpage('reporting/dataExport.jsp',',mainpane');">Export Audit Logs</a><br>
        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);
            try {
                int offset = 0;
                int records = 100;
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


                org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory dasfac = new org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory();


                DataAccessService dasport = pl.GetDAS(application, request, response);

                GetAuditLogRequestMsg req = new GetAuditLogRequestMsg();

                req.setClassification(c);
                req.setRecordcount(records);
                req.setOffset(offset);
                GetAuditLogResponseMsg res = dasport.getAuditLog(req);
                if (offset != 0 && (offset - 100) > 0) {
                    out.write("<a href=\"javascript:loadpage('admin/audit.jsp?records=" + records + "&offset=" + (offset - 100) + "','mainpane');\">Previous</a> ");
                } else if (offset != 0 && (offset - 100) <= 0) {
                    out.write("<a href=\"javascript:loadpage('admin/audit.jsp?records=" + records + "\','mainpane');\">Previous</a> ");
                } else {
                    out.write("Previous");
                } 
        %>

        <a href="javascript:loadpage('admin/audit.jsp?offset=<%=offset + 100%>&records=<%=records%>','mainpane');"> Next</a>
        <br>
        <%


                if (res != null && res.getAuditLog() != null && !res.getAuditLog().isEmpty()) {
                    out.write("Showing the last " + res.getAuditLog().size() + " records.<br>");
                    out.write("<table class=\"table\"><tr><th>Timestamp</th><th>Username</th><th>IP</th><th>Class</th><th>Method</th><th>Memo</th></tr>");
                    for (int i = 0; i < res.getAuditLog().size(); i++) {
                        out.write("<tr><td>" + Utility.formatDateTime(res.getAuditLog().get(i).getTimestamp()) + "</td><td>");
                        out.write(res.getAuditLog().get(i).getUsername() + "</td><td>");
                        out.write(res.getAuditLog().get(i).getIpaddress() + "</td><td>");
                        out.write(res.getAuditLog().get(i).getClazz() + "</td><td>");
                        out.write(res.getAuditLog().get(i).getMethod() + "</td><td>");
                        out.write(res.getAuditLog().get(i).getMemo() + "</td></tr>");
                    }

                } else {
                    out.write("No records were returned.");
                }


            } catch (Exception ex) {
                out.write("There was an error processing your request. " + ex.getLocalizedMessage());
            }
        %>
    </div></div>
