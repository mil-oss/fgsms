<%@page import="java.lang.String"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="java.math.BigInteger"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.UUID"%>
<%@page import="java.util.Enumeration"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.*"%>
<%@page import="java.util.List"%>
<%@page import="java.net.URLEncoder"%>
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

<%-- 
    Document   : newjsp2
    Created on : Jul 30, 2012, 3:27:24 PM
    Author     : 
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%




    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    if (c == null) {
        c = new SecurityWrapper(ClassificationType.U, "");
    }
    ProxyLoader pl = ProxyLoader.getInstance(application);
    AutomatedReportingService arsport = pl.GetARS(application, request, response);


    String jobId = request.getParameter("jobId");
    String action = request.getParameter("action");

//String name = request.getParameter("name"); 
    if (action==null ) {
        response.sendRedirect("index.jsp");
        return;
    }
    if (action.equals("calendarToggle")) {
    } else if (action.equals("validateAlerts")) {
    } else {

        /**
         * delete report utilizes DeleteReportRequest Msg in order to process
*
         */
        if (action.equals("deleteReport")) {
            String reportId = request.getParameter("reportId");
            // out.write("Your reportId: "+reportId); 
            DeleteReportRequestMsg req = new DeleteReportRequestMsg();
            req.setClassification(c);
            req.setReportId(reportId);
            try {
                arsport.deleteReport(req);
                out.write("<p>Success</p>");
            } catch (Exception ex) {
                out.write("<p>There was an error processing your request: " + ex.getMessage() + "</p>");
            }
        } /**
         * delete job utilizes DeleteScheduledReportRequestMsg 
*
         */
        else if (action.equals("deleteJob")) {
            if (jobId==null ) {
                response.sendRedirect("index.jsp");
                return;
            }
            DeleteScheduledReportRequestMsg req = new DeleteScheduledReportRequestMsg();
            req.setClassification(c);
            req.setJobId(jobId);

            try {
                arsport.deleteScheduledReport(req);
                out.print("<p>Success</p>");
            } catch (Exception ex) {
                out.write("<p>There was an error processing your request: " + ex.getMessage() + "</p>");
            }
        }
    }


%>