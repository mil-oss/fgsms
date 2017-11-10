<%-- 
    Document   : reportGenerator
    Created on : Jan 6, 2011, 8:47:08 PM
    Author     : Alex
--%> 

<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>

<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Properties"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="javax.xml.bind.JAXBElement"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="java.util.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.net.URL"%>
<%@page import="java.net.URL"%>
<%@include file="../csrf.jsp" %>
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    String error = "";
 

    if (request.getMethod().equalsIgnoreCase("post")) {
        try {
          
            Enumeration<String> listP = request.getParameterNames();
            List<String> urls = new ArrayList<String>();
            
            boolean allservices = false;
            
            while (listP.hasMoreElements()) {
                String name = listP.nextElement();
                if (name.equalsIgnoreCase("selectAllServices")) {
                    allservices = true;
                } else if (name.startsWith("URL")) {
                    urls.add(URLDecoder.decode(name.substring(3), Constants.CHARSET));
                } 
            }
            //check form inputs
            //request.getp
            boolean ok = true;
            String fromdate = request.getParameter("fromdatepicker");
            String todate = request.getParameter("todatepicker");
            if (Utility.stringIsNullOrEmpty(fromdate) || Utility.stringIsNullOrEmpty(todate)) {
                error += "There was a problem parsing your date selections, please try again.";

                ok = false;
            }
            ExportRecordsEnum t = ExportRecordsEnum.TRANSACTIONS;
            if (!Utility.stringIsNullOrEmpty(request.getParameter("recordstype"))) {
                try {
                    String s = request.getParameter("recordstype");
                    t = ExportRecordsEnum.fromValue(s);
                 
                } catch (Exception ex) {
                }
            } else {
                ok = false;
            }
            //if ok
            //build report request
            //output response

            org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory fac = new org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory();

            //todo

            ReportingService rsport = pl.GetRS(application, request, response);

            ExportCSVDataRequestMsg req = new ExportCSVDataRequestMsg();

            req.setClassification(c);

            req.setExportType(t);
            req.setAllServices(allservices);
            if (!allservices) {
                List<String> au = new ArrayList<String>();
                for (int i = 0; i < urls.size(); i++) {
                    req.getURLs().add(urls.get(i));
                }

            }

            TimeRange r = new TimeRange();

            if (fromdate.equalsIgnoreCase(todate)) {
                ok = false;
                error += "Please select two dates that differ from each other.";

            }
            try {
               Calendar t11 = Utility.parseDateTime(fromdate);
                r.setStart(t11);
                t11 = Utility.parseDateTime(todate);
                r.setEnd(t11);
                req.setRange(r);
            } catch (Exception ex) {
                ok = false;
                error += "There was a problem parsing your date selections, please try again. ";
            }

            if (ok) {
                ExportDataToCSVResponseMsg res = rsport.exportDataToCSV(req);
                if (res != null && res.getZipFile() != null) {
                    response.reset();
                    response.setHeader("contentType", "application/zip");
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Content-disposition", "attachment; filename=\"export"
                            + GregorianCalendar.getInstance().get(Calendar.YEAR)
                            + (GregorianCalendar.getInstance().get(Calendar.MONTH) + 1)
                            + GregorianCalendar.getInstance().get(Calendar.DATE) + " " + Utility.ICMClassificationToString(c.getClassification()) + ".zip\"");
                    response.setStatus(200);
                    response.setCharacterEncoding("UTF-8");

                    try {
                        ServletOutputStream output = response.getOutputStream();
                        output.write(res.getZipFile());
                        output.close();
                    } catch (Exception e) {
                    }
                } else {
                    ok = false;
                    error += "No results were returned.";
                }
            }
        } catch (Exception ex) {
            error += "There was a problem processing your request. Message:" + ex.getLocalizedMessage();
            LogHelper.getLog().log(Level.ERROR, "Error from dataExport.jsp", ex);
        }
    } else {

%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<div class="well">
    <h1>Export Data</h1>
    <p>Export performance data into a comma separated value file for easy importing into a spreadsheet program.</p>
    <a href="javascript:loadpage('help/scheduledreports.jsp','mainpane','scheduledReportsMain.jsp');" class="btn btn-primary">Learn More</a>
</div>

<div class="row-fluid">
    <div class="span12">


        <%
            if (!Utility.stringIsNullOrEmpty(error)) {
                out.write("<script type=\"text/javascript\">alert(\"" + error + "\");</script>");
            }
        %>
        <table  class=\"table\" >
            <tr><td>
                    This page will help you export data in a comma-separated values file that uses the pipe symbol "|" as a delimiter. This file an be easily imported into most spreadsheet programs.
                    Available options include the service list and the time range. All data will be exported, except for service request/response messages.<br>
                    Please provide the following:<Br>
                    Date Range (click for calendar) <Br>
               Note: you can also use the following formats:
            <ul> 
                <li>mm/dd/yyyy</li> 
                <li>mm/dd/yyyy hh:mm:ss</li>
                <li>EEE MMM dd HH:mm:ss zzz yyyy - this is the standard output from the unix date command</li> 
                <li>EEE mm/dd/yyyy HH:mm:ss.ms - this is the standard output from the windows command echo %date% %time%</li>
                <li>yyyy-MM-ddThh:mm:ss.zzzzzzz</li>
                <li>Epoch time (ms)</li>
                <li>PnYnMnDTnHnMnS - XML lexical representation</li>
            </ul><Br>
                    From:<input type="text" name="fromdatepicker"  id="fromdatepicker"/>

                    To:<input type="text" name="todatepicker" id="todatepicker"/> 
                    <a class="btn btn-large btn-primary" href="javascript:goDataExport();">Generate Report</a>
                </td>
            </tr>

            <tr  valign="top"><td>Select what kind of data you wish to export<br>
                    <%
                        for (int i = 0; i < ExportRecordsEnum.values().length; i++) {
                            out.write("<input type=radio name=recordstype value=\"" + ExportRecordsEnum.values()[i].value() + "\">" + ExportRecordsEnum.values()[i].value() + "<Br>");
                        }

                    %>
                </td></tr>
            <tr  valign="top">

                <td  valign="top"><h3>Services</h3>
                    Select the services from which to fetch data.<br>
                    <%
                        try {


                            DataAccessService dasport = pl.GetDAS(application, request, response);


                            GetMonitoredServiceListRequestMsg Listrequest = new GetMonitoredServiceListRequestMsg();
                            Listrequest.setClassification(c);
                            GetMonitoredServiceListResponseMsg list = dasport.getMonitoredServiceList(Listrequest);
                            if (list == null || list.getServiceList() == null || list.getServiceList() == null
                                    || list.getServiceList().getServiceType() == null
                                    || list.getServiceList().getServiceType().size() == 0) {
                                out.write("<br>There are currently no services being monitored that you have access to.");
                            } else {
                                out.write("<table class=\"table\" border=1><tr><th></th><th>Service URL</th><th>Display Name</th><th>Type</th></tr>");
                                out.write("<tr><td><input type=\"checkbox\" name=\"selectAllServices\" /></td><td>All Services</td></tr>");
                                for (int i = 0; i < list.getServiceList().getServiceType().size(); i++) {
                                    out.write("<tr><td><input type=\"checkbox\" name=\"URL" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL(), "UTF-8")
                                            + "\"></td>");
                                    out.write("<td>"
                                            + Utility.encodeHTML(list.getServiceList().getServiceType().get(i).getURL()) + "</td>"
                                            + "<td>" + Utility.encodeHTML(list.getServiceList().getServiceType().get(i).getDisplayName())
                                            + "</td>"
                                            + "<td>" + list.getServiceList().getServiceType().get(i).getPolicyType().toString()
                                            + "</td></tr>");
                                }
                                out.write("</table>");
                            }
                        } catch (SecurityException ex) {
                            out.write("Access was denied to the requested resource.");
                        } catch (Exception ex) {
                            out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
                        }

                    %>

                </td>

            </tr>
        </table>



    </div>
</div>
<%
    }
%>