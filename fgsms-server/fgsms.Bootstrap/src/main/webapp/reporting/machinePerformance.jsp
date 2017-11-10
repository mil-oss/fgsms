<%-- 
Document   : machinePerformance
Created on : Feb 5, 2012, 11:29:32 AM
Author     : Administrator
--%>

<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Machine Performance Information</h1>
</div>
<div class="row-fluid">
    <div class="span2"> 

        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);
            try {
                DataAccessService dasport = pl.GetDAS(application, request, response);
                if (Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
                    response.sendRedirect("index.jsp");
                }
                GetMostRecentMachineDataRequestMsg req = new GetMostRecentMachineDataRequestMsg();
                req.setUri(request.getParameter("uri"));
                req.setClassification(c);
                GetMostRecentMachineDataResponseMsg res = dasport.getMostRecentMachineData(req);
                if (res != null && res.getMachineData() != null) {
                    PCS pcsport = pl.GetPCS(application, request, response);
                    GetProcessesListByMachineRequestMsg req2 = new GetProcessesListByMachineRequestMsg();
                    req2.setHostname(res.getMachineData().getHostname());
                    req2.setClassification(c);
                    GetProcessesListByMachineResponseMsg res2 = pcsport.getProcessesListByMachine(req2);
                    DecimalFormat nf = new DecimalFormat("###.##");
                    out.write("<h1>" + Utility.encodeHTML(res.getMachineData().getMachinePerformanceData().getUri()) + "</h1>");
                    /*            out.write("<a href=\"manage.jsp?url=" + URLEncoder.encode(res.getMachineData().getMachinePerformanceData().getUri()) + "\">Manage</a>"
                     + " | <a href=\"availability.jsp?url=" + URLEncoder.encode(res.getMachineData().getMachinePerformanceData().getUri()) + "\">Availability</a>"
                     + " | <a href=\"serverprocess.jsp?server=" + URLEncoder.encode(res.getMachineData().getHostname()) + "\">Machine Information</a>"
                     + "<br>");*/
                    out.write("<table class=\"table\"><tr><td>Machine Hostname</td><td>" + Utility.encodeHTML(res.getMachineData().getHostname()) + "</td></tr>");
                    out.write("<tr><td>Domain</td><td>" + Utility.encodeHTML(res.getMachineData().getDomainName()) + "</td></tr>");
                    //  out.write("<tr><td>Policy URI</td><td>"+ "</td></tr>");
                    out.write("<tr><td>Operational</td><td>" + res.getMachineData().getMachinePerformanceData().isOperationalstatus() + "</td></tr>");
                    out.write("<tr><td>Status Message</td><td>" + Utility.encodeHTML(res.getMachineData().getMachinePerformanceData().getStatusmessage()) + "</td></tr>");
                    out.write("<tr><td>Memory Installed</td><td>" + res2.getMachineInformation().getMemoryinstalled() + "</td></tr>");
                    if (res.getMachineData().getMachinePerformanceData().getBytesusedMemory() != null) {
                        out.write("<tr><td>Memory in use (bytes)</td><td>" + res.getMachineData().getMachinePerformanceData().getBytesusedMemory() + "</td></tr>");
                        out.write("<tr><td>Free Memory (bytes)</td><td>" + (res2.getMachineInformation().getMemoryinstalled() - res.getMachineData().getMachinePerformanceData().getBytesusedMemory()) + "</td</tr>");
                        double total = res2.getMachineInformation().getMemoryinstalled();
                        double free = res.getMachineData().getMachinePerformanceData().getBytesusedMemory();
                        out.write("<tr><td>Free Memory (%)</td><td>" + nf.format((double) ((total - free) / total) * 100.0) + "</td</tr>");
                    }


                    out.write("<tr><td>Installed CPU Cores</td><td>" + res2.getMachineInformation().getCpucorecount() + "</td</tr>");


                    for (int k = 0; k < res.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().size(); k++) {
                        out.write("<tr><td>Network Adapter "
                                + Utility.encodeHTML(res.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().get(k).getAdapterName())
                                + "</td><td>");
                        if (res.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().get(k).getKilobytespersecondNetworkReceive() != null) {
                            out.write("RX " + res.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().get(k).getKilobytespersecondNetworkReceive() + " KB/s<br>");
                        }
                        if (res.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().get(k).getKilobytespersecondNetworkTransmit() != null) {
                            out.write("TX " + res.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().get(k).getKilobytespersecondNetworkTransmit() + " KB/s<br>");
                        }
                        out.write("</td></tr>");
                    }

                    if (res.getMachineData().getMachinePerformanceData().getNumberofActiveThreads() != null) {
                        out.write("<tr><td>Active Threads </td><td>" + res.getMachineData().getMachinePerformanceData().getNumberofActiveThreads() + "</td></tr>");
                    }
                    if (res.getMachineData().getMachinePerformanceData().getPercentusedCPU() != null) {
                        out.write("<tr><td>CPU</td><td>" + res.getMachineData().getMachinePerformanceData().getPercentusedCPU() + "</td></tr>");
                    }
                    if (res.getMachineData().getMachinePerformanceData().getTimestamp() != null) {
                        long diff = System.currentTimeMillis() - res.getMachineData().getMachinePerformanceData().getTimestamp().getTimeInMillis();
                        DatatypeFactory df = DatatypeFactory.newInstance();

                        out.write("<tr><td>Last Updated At</td><td>" + Utility.formatDateTime(res.getMachineData().getMachinePerformanceData().getTimestamp()) + " "
                                + Utility.durationToString(df.newDuration(diff))
                                + "</td></tr>");
                    }
                    for (int i = 0; i < res.getMachineData().getDriveInformation().size(); i++) {
                        out.write("<tr><td>Free Space on " + Utility.encodeHTML(res.getMachineData().getDriveInformation().get(i).getPartition()) + "</td>"
                                + "<td>" + res.getMachineData().getDriveInformation().get(i).getFreespace() + " MB</td></tr>");
                        if (res.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskRead() != null) {
                            out.write("<tr><td>I/O Read on " + Utility.encodeHTML(res.getMachineData().getDriveInformation().get(i).getPartition()) + "</td>"
                                    + "<td>" + res.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskRead() + " KB/s</td></tr>");
                        }
                        if (res.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskWrite() != null) {
                            out.write("<tr><td>I/O Write on " + Utility.encodeHTML(res.getMachineData().getDriveInformation().get(i).getPartition()) + "</td>"
                                    + "<td>" + res.getMachineData().getDriveInformation().get(i).getKilobytespersecondDiskWrite() + " KB/s</td></tr>");
                        }
                        out.write("<tr><td>Partition " + Utility.encodeHTML(res.getMachineData().getDriveInformation().get(i).getPartition()) + "</td>"
                                + "<td>");
                        if (res.getMachineData().getDriveInformation().get(i).isOperational()) {
                            out.write("online");
                        } else {
                            out.write("offline");
                        }
                        out.write("</td></tr>");
                    }
                    for (int i = 0; i < res.getMachineData().getProcessPerformanceData().size(); i++) {
                        out.write("<tr><td>Process" + Utility.encodeHTML(res.getMachineData().getProcessPerformanceData().get(i).getUri()) + "</td>"
                                + "<td>");

                        if (res.getMachineData().getProcessPerformanceData().get(i).getBytesusedMemory() != null) {
                            out.write("Memory (bytes): " + res.getMachineData().getProcessPerformanceData().get(i).getBytesusedMemory() + "<br>");
                        }
                        if (res.getMachineData().getProcessPerformanceData().get(i).getPercentusedCPU() != null) {
                            out.write("CPU : " + res.getMachineData().getProcessPerformanceData().get(i).getPercentusedCPU() + " %<br>");
                        }
                        if (res.getMachineData().getProcessPerformanceData().get(i).getNumberofActiveThreads() != null) {
                            out.write("Threads : " + res.getMachineData().getProcessPerformanceData().get(i).getNumberofActiveThreads() + "<br>");
                        }
                        out.write("Operational : <b>" + res.getMachineData().getProcessPerformanceData().get(i).isOperationalstatus() + "</b><br>");
                        out.write("Operational Status: " + Utility.encodeHTML(res.getMachineData().getProcessPerformanceData().get(i).getStatusmessage()) + "<br>");
                        out.write("</td></tr>");
                    }
                    out.write("</table>");

                }
            } catch (Exception ex) {
                out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());

            }


        %>

    </div>
    <div class="span10" style="min-width: 600px">
        <div id="percentscale" style="width:600px; height:300px">

        </div>   
        <div id="threadsfilecharts"  style="width:600px; height:300px">

        </div>
        <div id="networkdiskchart" style="width:600px; height:300px"></div>
    </div>




</div>
<script type="text/javascript">
    var since = Date.now() - (60 *60 * 1000);
    createCharts('<%=StringEscapeUtils.escapeEcmaScript(request.getParameter("uri"))%>','<%=StringEscapeUtils.escapeEcmaScript(request.getParameter("server"))%>','percentscale','threadsfilecharts','networkdiskchart',since);
</script>

