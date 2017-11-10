<%-- 
    Document   : reportGenerator
    Created on : Jan 6, 2011, 8:47:08 PM
    Author     : Alex
--%>

<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.Plugin"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
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
<%@page import="java.util.List"%>
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
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<div class="well">
    <h1>Reports</h1>
    <p>Create stylized web based reports</p>
    <a href="javascript:loadpage('help/scheduledreports.jsp','mainpane','scheduledReportsMain.jsp');" class="btn btn-primary">Learn More</a>
</div>
<div class="row-fluid">
    <div class="span12">

        <table width="100%" border="0">
            <tr><td colspan="2">
                    This page will help you generate an web based report for a give set of services, report types and time range. 
                    Please provide the following:<Br>
                    Date Range (click for calendar). <Br>
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
                    <a class="btn btn-large btn-primary" href="javascript:goReportGen();">Generate Report</a>
                </td>
            </tr>
            
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
                                out.write("<table border=1><tr><th></th><th>Service URL</th></tr>");
                                out.write("<tr><td><input type=\"checkbox\" name=\"selectAllServices\" /></td><td>All Services</td></tr>");
                                for (int i = 0; i < list.getServiceList().getServiceType().size(); i++) {
                                    out.write("<tr><td><input type=\"checkbox\" name=\"URL" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL(), Constants.CHARSET)
                                            + "\"></td>");
                                    out.write("<td>"
                                            + Utility.encodeHTML(list.getServiceList().getServiceType().get(i).getURL()) + "</td></tr>");
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
                <td valign="top"><h3>Report Types</h3>
                    Generate the following report types. Place a check mark next to each report type.<br>
                    <table border="1"><tr><th></th><th>Report Type</th></tr>
                        <tr><td><input type="checkbox" name="AllReportTypes"/></td><td>All Report Types</td></tr>
                                <%
                                    
                                    List<Plugin> plugins = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "REPORTING");
                                    for (int i = 0; i < plugins.size(); i++) {
                                        out.write("<tr><td><input type=\"checkbox\" name=\"REPORTTYPE_" + Utility.encodeHTML(plugins.get(i).getClassname()) + "\"></td><td>"
                                                +Utility.encodeHTML(plugins.get(i).getDisplayname()));
                                        out.write("</td></tr>");
                                    }

                                %>
                    </table>

                </td>
            </tr>
        </table>






    </div>
</div>