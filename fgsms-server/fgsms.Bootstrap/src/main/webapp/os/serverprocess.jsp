<%-- 
    Document   : serverprocess
    Created on : Jan 28, 2012, 1:11:20 PM
    Author     : Administrator
--%>

<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.PolicyType"%>
<%@page import="java.util.Enumeration"%>
<%@page import="javax.xml.datatype.Duration"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.Collections"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
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
<%

    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    PCS pcsport = pl.GetPCS(application, request, response);
    DatatypeFactory fac = DatatypeFactory.newInstance();

    if (Utility.stringIsNullOrEmpty(request.getParameter("server"))) {
%>
<script type="text/javascript">
    loadpage('os/domains.jsp','mainpane');
</script>
<%} else {


    try {



        GetProcessesListByMachineRequestMsg req = new GetProcessesListByMachineRequestMsg();
        req.setHostname(request.getParameter("server"));
        req.setClassification(c);
        GetProcessesListByMachineResponseMsg res = pcsport.getProcessesListByMachine(req);


        if (res != null && !res.getProcessName().isEmpty()) {
            Collections.sort(res.getProcessName());
            if (request.getMethod().equalsIgnoreCase("post")) {
                int i2 = -1;
                Enumeration enumer = request.getParameterNames();
                while (enumer.hasMoreElements()) {
                    String namedParam = (String) enumer.nextElement();
                    if (namedParam.contains("monitor")) {
                        String ns = namedParam.replace("monitor", "");
                        i2 = Integer.parseInt(ns);

                    }
                }
                if (i2 >= 0) {
                    String postedname = request.getParameter("process" + i2);
                    boolean ok = false;
                    if (!Utility.stringIsNullOrEmpty(postedname)) {
                        //valid that it's actually still on the list
                        for (int i = 0; i < res.getProcessName().size(); i++) {
                            if (res.getProcessName().get(i).equalsIgnoreCase(postedname)) {
                                ok = true;
                            }
                        }
                        if (ok) {
                            //create a process policy for that item, then redirect to the manage page
                            SetServicePolicyRequestMsg preq = new SetServicePolicyRequestMsg();

                            preq.setClassification(c);


                            preq.setURL("urn:" + res.getMachineInformation().getHostname().toLowerCase() + ":" + postedname);


                            ProcessPolicy pp = new ProcessPolicy();
                            pp.setMachineName(res.getMachineInformation().getHostname().toLowerCase());
                            pp.setDomainName(res.getMachineInformation().getDomain());
                            pp.setDisplayName(res.getProcessName().get(i2));
                            pp.setPolicyType(PolicyType.PROCESS);
                            pp.setAgentsEnabled(true);
                            pp.setRecordCPUusage(true);
                            pp.setRecordMemoryUsage(true);
                            pp.setRecordOpenFileHandles(true);
                            pp.setURL("urn:" + res.getMachineInformation().getHostname().toLowerCase() + ":" + res.getProcessName().get(i2));
                            pp.setParentObject(res.getMachineInformation().getUri());
                            pp.setDataTTL(DatatypeFactory.newInstance().newDuration(30L * 24L * 60L * 60L * 1000L));
                            Utility.addStatusChangeSLA(pp);
                            preq.setPolicy(pp);

                            pcsport.setServicePolicy(preq);
                            out.write("Saved. Redirecting");
%>
<script type="text/javascript">
    setTimeout(function()
    {
        loadpage('profile.jsp?url=<%=URLEncoder.encode("urn:" + res.getMachineInformation().getHostname().toLowerCase() + ":" + res.getProcessName().get(i2))%>','mainpane');
    }, 1000);
    
</script>
<%
                //response.sendRedirect("manage.jsp?url=" + URLEncoder.encode("urn:" + res.getMachineInformation().getHostname().toLowerCase() + ":" + res.getProcessName().get(i2)));
            } else {
                out.write("There was a problem processing your request. Please try again. Error #1<br>");
            }
        } else {
            out.write("There was a problem processing your request. Please try again. Error #2<br>");
        }
    }
} else {
%>
<div class="well">
    <h1>Machine Information</h1>
</div>
<div class="row-fluid">
    <div class="span12">

        This displays all of the last known installed or running components on a specific machine that has an Operating System FGSMS Agent installed and running on it.<br>
        <%

                        out.write("<h2>" + Utility.encodeHTML(res.getMachineInformation().getHostname()) + "</h2>");
                        out.write("<a class=\"btn btn-primary\" href=\"manage.jsp?url=" + URLEncoder.encode("urn:" + res.getMachineInformation().getHostname().toLowerCase() + ":system") + "\">View Policy</a> | ");
                        out.write("<a class=\"btn btn-primary\" href=\"javascript:loadpage('reporting/machinePerformance.jsp?uri=" + URLEncoder.encode("urn:" + res.getMachineInformation().getHostname().toLowerCase() + ":system") + "&server=" + URLEncoder.encode(res.getMachineInformation().getHostname().toLowerCase()) + "','mainpane');\">View Performance</a> | ");

                        if (res.getLastupdateat() != null) {
                            Duration d = fac.newDuration(System.currentTimeMillis() - res.getLastupdateat().getTimeInMillis());
                            out.write("Last Updated At: " + Utility.formatDateTime(res.getLastupdateat()) + " or about " + Utility.durationLargestUnitToString(d) + " ago.<br>");
                        }
                        out.write("<div id=\"tabs\"><ul>");


                        out.write("<li><a href=\"#tabs-1\">General</a></li>");
                        out.write("<li><a href=\"#tabs-2\">Running Processes</a></li>");
                        out.write("<li><a href=\"#tabs-3\">Network Adapters</a></li>");

                        out.write("<li><a href=\"#tabs-4\">Drives</a></li>"
                                + "<li><a href=\"#tabs-5\">Other</a></li>"
                                + "</ul>");

                        out.write("<div id=tabs-1>");

                        out.write("Domain Membership: " + Utility.encodeHTML(res.getMachineInformation().getDomain()) + "<br>");
                        out.write("Operating System: " + Utility.encodeHTML(res.getMachineInformation().getOperatingsystem()) + "<br>");
                        out.write("CPU Cores: " + (res.getMachineInformation().getCpucorecount()) + "<br>");
                        out.write("RAM:  " + (res.getMachineInformation().getMemoryinstalled()) + "</div>");


                        out.write("<div id=tabs-2>");
                        out.write("Running Processes/Installed Services<br>");

                        out.write("<table id=\"table1\" name=\"table1\"  border=1><th>Process Name</th><th>Action</th>");
                        for (int i = 0; i < res.getProcessName().size(); i++) {
                            if (!Utility.stringIsNullOrEmpty(res.getProcessName().get(i))) {

                                out.write("<tr><td><span title=\"" + Utility.encodeHTML(res.getProcessName().get(i)) + "\">" + Utility.encodeHTML((res.getProcessName().get(i))) + "</span></td><td>"
                                        + "<a class=\"btn btn-primary\"  href=\"javascript:postBackReRender('monitor" + i + "','os/serverprocess.jsp?server=" + URLEncoder.encode(request.getParameter("server")) + "','mainpane');\">Monitor this Item</a>" //+ "<input type=button name=\"monitor" + i + "\" value=\"Monitor this item\" onclick=\"javascript:postBackReRender('monitor" + i + "','os/serverprocess.jsp?server=" + URLEncoder.encode(request.getParameter("server")) + "','mainpane');\">"
                                        );
                                out.write("<input type=hidden name=\"process" + i + "\" value=\"" + Utility.encodeHTML(res.getProcessName().get(i)) + "\">");
                                out.write("</td></tr>");
                            }
                        }
                        out.write("</table></div>");


                        out.write("<div id=tabs-3>");
                        out.write("The following is network information about this computer that was detected by the agent monitoring it.<br>"
                                + "<table border=1><th>Network Interface</th><th>Information</th>");
                        for (int i = 0; i < res.getMachineInformation().getAddresses().size(); i++) {
                            out.write("<tr><td>" + Utility.encodeHTML(res.getMachineInformation().getAddresses().get(i).getAdapterName()) + "</td><td>"
                                    + Utility.encodeHTML("Description: " + Utility.encodeHTML(res.getMachineInformation().getAddresses().get(i).getAdapterDescription())) + "<br>"
                                    + Utility.encodeHTML("Default Gateway: " + Utility.encodeHTML(res.getMachineInformation().getAddresses().get(i).getDefaultGateway())) + "<br>"
                                    + Utility.encodeHTML("MAC: " + Utility.encodeHTML(res.getMachineInformation().getAddresses().get(i).getMac())) + "<br>"
                                    + Utility.encodeHTML("Subnet Mask: " + Utility.encodeHTML(res.getMachineInformation().getAddresses().get(i).getSubnetMask())) + "<br>"
                                    + Utility.encodeHTML("MTU: " + res.getMachineInformation().getAddresses().get(i).getMtu()) + "<br>"
                                    + Utility.encodeHTML("DNS: " + Utility.encodeHTML(Utility.listStringtoString(res.getMachineInformation().getAddresses().get(i).getDns()))) + "<br>"
                                    + Utility.encodeHTML("IP: " + Utility.encodeHTML(Utility.listStringtoString(res.getMachineInformation().getAddresses().get(i).getIp())))
                                    + "</td></tr>");
                        }
                        out.write("</table></div>");


                        out.write("<div id=tabs-4>");
                        out.write("The following is drive and diskinformation about this computer that was detected by the agent monitoring it.<br>");
                        out.write("<table  border=1><tr><th>Type</th><th>System ID</th><th>Total Space (MB)</th><th>Operational</th></tr>");
                        for (int i = 0; i < res.getMachineInformation().getDriveInformation().size(); i++) {
                            out.write("<tr>"
                                    + "<td>" + Utility.encodeHTML(res.getMachineInformation().getDriveInformation().get(i).getType()) + "</td><td>"
                                    + Utility.encodeHTML(res.getMachineInformation().getDriveInformation().get(i).getSystemid())
                                    + "</td><td>" + res.getMachineInformation().getDriveInformation().get(i).getTotalspace()
                                    + "</td><td>" + res.getMachineInformation().getDriveInformation().get(i).isOperational()
                                    + "</td></tr>");
                        }
                        out.write("</table></div>");

                        out.write("<div id=tabs-5>");
                        out.write("The following is additional information about this computer that was detected by the agent monitoring it.<br>"
                                + "<table  id=\"table2\"  name=\"table2\" border=1><th>Name</th><th>Value</th>");
                        for (int i = 0; i < res.getMachineInformation().getPropertyPair().size(); i++) {
                            out.write("<tr><td>" + Utility.encodeHTML(res.getMachineInformation().getPropertyPair().get(i).getPropertyname()) + "</td><td>"
                                    + Utility.encodeHTML(res.getMachineInformation().getPropertyPair().get(i).getPropertyvalue())
                                    + "</td></tr>");
                        }
                        out.write("</table></div>"
                                + "</div>");




                    }
                } else {
                    out.write("No information in currently avaiable. This can happen when an agent is first deployed. Try again in a few minutes.");
                }
            } catch (Exception ex) {
                out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font>");
                LogHelper.getLog().log(Level.ERROR, "Error from serviceprocess.jsp error1", ex);
            }

        %>

        <script>
            $(function() {
                $( "input:button").button();
                $( "#tabs" ).tabs();
            });
        </script>
    </div>
</div>
<%
    }
%>
