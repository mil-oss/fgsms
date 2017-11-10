
<%@page import="org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.PolicyType"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.status.*"%>

<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="javax.xml.datatype.Duration"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormatSymbols"%>
<%@page import="java.util.GregorianCalendar"%> 
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>

<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="csrf.jsp" %>
<div class="well">
    <h1>Status</h1>
    <p>This is the most up to date status information available for all of the services from which you have access.</p>
</div>

<div class="row-fluid">
    <div class="span12">


        <a id="reloaderButton" class="btn btn-primary btn-large" id="autoreloads" onclick="javascript:toggleStatusReloader();">Automatic Reloads Disabled</a>







        <%             Cookie[] cookies = request.getCookies();
            String filter = "AllItems";
            if (cookies == null || cookies.length == 0) {
                //       out.write("No settings are currently configured.");
            } else {

                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equalsIgnoreCase("homefilter")) {
                        int x = 40;
                        try {
                            filter = (cookies[i].getValue());
                            if (filter.equalsIgnoreCase("OfflineOnly")) {
                                filter = "OfflineOnly";
                            } else {
                                filter = "AllItems";
                            }

                        } catch (Exception ex) {
                            filter = "AllItems";
                        }
                    }
                }
            }

        %>

        Filter <input type="radio" id="AllItems" name="homefilter" onchange="javascript:setCookie('homefilter', 'AllItems', 365); loadpage('status.jsp', 'mainpane');"  value="AllItems" <%             if (filter.equalsIgnoreCase("AllItems")) {
                out.write("checked=\"true\" ");
            }
                      %>>All Items | 
        <input type="radio" id="OfflineOnly" name="homefilter" onchange="javascript:setCookie('homefilter', 'OfflineOnly', 365); loadpage('status.jsp', 'mainpane');"  value="OfflineOnly" 
               <%
                   if (filter.equalsIgnoreCase("OfflineOnly")) {
                       out.write("checked=\"true\" ");
                   }
               %>
               >Offline Only
        <%
            response.setHeader("Cache-Control", "no-cache");
            if (request.getUserPrincipal() != null) {
                LogHelper.getLog().log(Level.INFO, "Current User: " + request.getUserPrincipal().getName() + " by " + request.getAuthType() + " ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());

            } else {
                LogHelper.getLog().log(Level.INFO, "Current User: anonymous ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());
            }

            int textlength = 100;

            // out.write("<br><Br>Current settings<Br><Br>");
            //Cookie[] cookies = request.getCookies();
            if (cookies == null || cookies.length == 0) {
                //out.write("No settings are currently configured.");
            } else {

                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equalsIgnoreCase("truncation")) {
                        int x = 100;
                        try {
                            textlength = Integer.parseInt(cookies[i].getValue());
                            if (x < 30 || x > 1000) {
                                textlength = 100;
                            }
                        } catch (Exception ex) {
                            textlength = 100;
                        }
                    }
                }
            }


        %>
        <br>
        Missing a service from this list? Click here to <a title="Missing a service from this list?" href="javascript:loadpage('requestAccess.jsp','mainpane','status.jsp');">Request Access</a>
        <script lang="javascript"  type="text/javascript">
            $('#accordion2').collapse({
                toggle: true
            });
            $(".collapse").collapse()
        </script>

        <script>

        </script>
        <%             try {
                SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
                if (c == null) {
                    LogHelper.getLog().log(Level.INFO, "redirecting to the index page, security wrapper is null");
                    response.sendRedirect("index.jsp");
                    return;
                }
                ProxyLoader pl = ProxyLoader.getInstance(application);

                DataAccessService dasport = pl.GetDAS(application, request, response);
                StatusService statusport = pl.GetSS(application, request, response);

                GetStatusRequestMsg req = new GetStatusRequestMsg();
                req.setClassification(c);
                List<GetStatusResponseMsg> statuslist = statusport.getAllStatus(req);

                GetMonitoredServiceListRequestMsg Listrequest = new GetMonitoredServiceListRequestMsg();

                Listrequest.setClassification(c);
                GetMonitoredServiceListResponseMsg list = dasport.getMonitoredServiceList(Listrequest);
                if (list == null || list.getServiceList() == null || list.getServiceList() == null
                        || list.getServiceList().getServiceType() == null
                        || list.getServiceList().getServiceType().size() == 0) {
                    out.write("<br>There are currently no services being monitored that you have access to.");
                } else {


        %>


        <%             //style=\"float: left; margin-right: .3em;\"
            String warnicon = "<i class=\"icon-warning-sign\"></i>&nbsp;&nbsp;&nbsp;";//<span class=\"ui-icon ui-icon-info\" ></span>&nbsp;&nbsp;&nbsp;";
            String erroricon = "<i class=\"icon-fire\"></i>&nbsp;&nbsp;&nbsp;";//<span class=\"ui-icon ui-icon-alert\" ></span> &nbsp;&nbsp;&nbsp;";
            String okicon = "<i class=\"icon-ok\"></i>&nbsp;&nbsp;&nbsp;";//"<span class=\"ui-icon ui-icon-check\" ></span>&nbsp;&nbsp;&nbsp;";
            String unknownicon = "<i class=\"icon-question-sign\"></i>&nbsp;&nbsp;&nbsp;";//"<span class=\"ui-icon ui-icon-help\" ></span>&nbsp;&nbsp;&nbsp;";
            //testing zone
            //        GetAllMostRecentMachineAndProcessDataRequestMsg r2 = new GetAllMostRecentMachineAndProcessDataRequestMsg();
            //         r2.setClassification(c);
            //         GetAllMostRecentMachineAndProcessDataResponseMsg r4 = dasport.getAllMostRecentMachineAndProcessData(r2);

            GetQuickStatsAllRequestMsg quickres = new GetQuickStatsAllRequestMsg();
            quickres.setAllMethodsOnly(true);
            quickres.setClassification(c);
            GetQuickStatsAllResponseMsg stats = dasport.getQuickStatsAll(quickres);
            DatatypeFactory fac = DatatypeFactory.newInstance();
        %>

        <div id="accordion2" class="accordion" >
            <%
                for (int i = 0; i < list.getServiceList().getServiceType().size(); i++) {
                    GetStatusResponseMsg result = Helper.Findrecord(list.getServiceList().getServiceType().get(i).getURL(), statuslist);
                    if ((filter.equalsIgnoreCase("OfflineOnly") && (result == null || !result.isOperational())) || filter.equalsIgnoreCase("AllItems")) {
                        //style="padding: 10px; "
            %>
            <div class="accordion-group" >

                <div class="accordion-heading alert <%
                    if (result == null) {
                        out.write("alert-info");
                    } else {
                        GregorianCalendar time = (GregorianCalendar) result.getTimeStamp();

                        long diff = System.currentTimeMillis() - time.getTimeInMillis();
                        if (result.isOperational()) {
                            //if diff is over an hour, flag as red
                            if (diff > (60 * 60 * 1000)) {
                                out.write("alert-error");  //was alert
                            } else if (diff > (10 * 60 * 1000)) {
                                //if diff is over 10 minutes, flag as orange
                                out.write("alert-warn");
                            } else {
                                out.write("alert-success");  //we are ok
                            }

                        } else {
                            out.write("alert-error");  //was alert
                        }
                    }
                     %>" style="margin-bottom:0px">
                    <%
                        if (result == null) {
                            out.write(unknownicon);
                        } else {
                            GregorianCalendar time = (GregorianCalendar) result.getTimeStamp();

                            long diff = System.currentTimeMillis() - time.getTimeInMillis();
                            if (result.isOperational()) {
                                //if diff is over an hour, flag as red
                                if (diff > (60 * 60 * 1000)) {
                                    out.write(erroricon);  //was alert
                                } else if (diff > (10 * 60 * 1000)) {
                                    //if diff is over 10 minutes, flag as orange
                                    out.write(warnicon);
                                } else {
                                    out.write(okicon);  //we are ok
                                }

                            } else {
                                out.write(erroricon);  //was alert
                            }
                        }
                        out.write("<a data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapse" + i + "\"  ");
                        /*
                         if (result == null) {
                              out.write("class=\"text-warning\""); //was warn
                         } else {
                              GregorianCalendar time = (GregorianCalendar) result.getTimeStamp();

                              long diff = System.currentTimeMillis() - time.getTimeInMillis();
                              if (result.isOperational()) {
                                   //if diff is over an hour, flag as red
                                   if (diff > (60 * 60 * 1000)) {
                                        out.write("class=\"text-error\"");  //was alert
                                   } else if (diff > (10 * 60 * 1000)) {
                                        //if diff is over 10 minutes, flag as orange
                                        out.write("class=\"text-warning\" ");
                                   } else {
                                        /// we're a-ok
                                   }

                              } else {
                                   out.write("class=\"text-error\""); //was error
                              }
                         }*/
                        out.write(" title=\"" + Utility.encodeHTML(list.getServiceList().getServiceType().get(i).getURL()) + "\">");

                        if (Utility.stringIsNullOrEmpty(list.getServiceList().getServiceType().get(i).getDisplayName())) {
                            out.write(Utility.truncate(Utility.encodeHTML(list.getServiceList().getServiceType().get(i).getURL()), textlength));
                        } else {
                            out.write(Utility.truncate(Utility.encodeHTML(list.getServiceList().getServiceType().get(i).getDisplayName()), textlength));
                        }

                        if (result == null) {
                            out.write(" - Status Unknown");
                        } else {
                            GregorianCalendar time = (GregorianCalendar) result.getTimeStamp();

                            long diff = System.currentTimeMillis() - time.getTimeInMillis();
                            if (result.isOperational()) {
                                out.write(" - GO");
                                //if diff is over an hour, flag as red
                                if (diff > (60 * 60 * 1000)) {
                                    out.write(" - Very Stale");
                                } else if (diff > (10 * 60 * 1000)) {
                                    //if diff is over 10 minutes, flag as orange
                                    out.write("- Stale");
                                }
                            } else {
                                out.write(" - NO GO");
                                if (diff > (60 * 60 * 1000)) {
                                    out.write(" - Very Stale");
                                } else if (diff > (10 * 60 * 1000)) {
                                    //if diff is over 10 minutes, flag as orange
                                    out.write("- Stale");
                                }
                            }
                        }

                        out.write("</a>");

                    %>

                </div>

                <div id="collapse<%=i%>" class="accordion-body collapse in">
                    <div class="accordion-inner">
                        <%
                                out.write("<a class=\"btn btn-primary\" href=\"javascript:loadpage('profile.jsp?url=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL(), "UTF-8")
                                        + "','mainpane');" + "\">Manage</a> | "
                                );
                                //  out.write(".jsp?url=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL(), "UTF-8")

                                //if (list.getServiceList().getServiceType().get(i).getPolicyType() == org.miloss.fgsms.services.interfaces.common.PolicyType.TRANSACTIONAL) {
                                //                                out.write(" <a  class=\"btn btn-primary\" href=\"javascript:loadpage_params('profile/getPolicy.jsp?url=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL(), "UTF-8") + "');\">Profile</a> | ");
                                //                        }
                                out.write("<a class=\"btn btn-primary\" href=\"javascript:loadpage('reporting/availability.jsp?url=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL()) + "','mainpane');\">Availability</a> | ");

                                //                    if (list.getServiceList().getServiceType().get(i).getPolicyType() == org.miloss.fgsms.services.interfaces.common.PolicyType.TRANSACTIONAL) {
                                //                                out.write("<a class=\"btn btn-primary\" href=\"TransactionLogViewer.jsp?url=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL(), "UTF-8") + "\">Transactions</a> | ");
                                //                        }
                                if (list.getServiceList().getServiceType().get(i).getPolicyType() == org.miloss.fgsms.services.interfaces.common.PolicyType.STATISTICAL) {
                                    out.write("<a class=\"btn btn-primary\" href=\"messageBrokerDetail.jsp?url=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL(), "UTF-8") + "\">Statistics</a> | ");
                                }

                                out.write("<font color=");

                                if (result == null) {
                                    out.write("#EE0000>??");

                                } else if (result.isOperational()) {
                                    out.write("#00DD00 title=\"" + Utility.encodeHTML(result.getMessage()) + "\">GO - ");
                                    out.write(Utility.encodeHTML(result.getMessage()));
                                } else {
                                    out.write("#EE0000 title=\"" + Utility.encodeHTML(result.getMessage()) + "\">NO GO - ");
                                    out.write(Utility.encodeHTML(result.getMessage()));
                                }
                                out.write("</font> | ");
                                if (result == null) {
                                    out.write(" ?? ");
                                } else {
                                    GregorianCalendar time = (GregorianCalendar) result.getTimeStamp();

                                    long diff = System.currentTimeMillis() - time.getTimeInMillis();

                                    //if diff is over an hour, flag as red
                                    if (diff > (60 * 60 * 1000)) {
                                        out.write("<font color=#FF0000 title=\"" + Utility.durationToString(fac.newDuration(diff)) + "\">");
                                    } else if (diff > (10 * 60 * 1000)) {
                                        //if diff is over 10 minutes, flag as orange
                                        out.write("<font color=#FF7D40 title=\"" + Utility.durationToString(fac.newDuration(diff)) + "\">");
                                    } else {
                                        out.write("<font>");
                                    }
                                    //        String[] ampm = new String[]{"AM", "PM"};
                                    int hour = time.get(Calendar.HOUR);
                                    if (hour == 0) {
                                        hour = 12;
                                    }
                                    String timestamp = Utility.durationLargestUnitToString(fac.newDuration(diff));

                                    out.write("Last Check-in: " + timestamp + "</font>");
                                }
                                if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.TRANSACTIONAL) {
                                    out.write(" | <a class=\"btn btn-primary\" href=\"javascript:loadpage('reporting/stattab.jsp?uri=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL()) + "','mainpane');\">Stats</a> ");
                                } else {
                                    out.write(" ");
                                }

                                out.write(" | " + list.getServiceList().getServiceType().get(i).getPolicyType().value());
                                if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.MACHINE) {
                                    //javascript:loadpage('reporting/machinePerformance.jsp?uri=urn%3Afgsmsdev1%3Asystem&server=fgsmsdev1','mainpane');
                                    out.write(" | <a class=\"btn btn-primary\" href=\"javascript:loadpage('reporting/machinePerformance.jsp?uri=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL()) + "&server=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getHostname()) + "','mainpane');\">Performance</a>");
                                    out.write(" | <a class=\"btn btn-primary\" href=\"javascript:loadpage('os/serverprocess.jsp?server=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getHostname()) + "','mainpane');\">Machine Information</a>");
                                }
                                if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.PROCESS) {
                                    out.write(" | <a class=\"btn btn-primary\" href=\"javascript:loadpage('reporting/processPerformance.jsp?uri=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL()) + "','mainpane');\">Performance</a>");
                                }
                                //this is used for non transactional services

                                try {
                                    QuickStatURIWrapper res2 = Helper.FindRecord(stats, list.getServiceList().getServiceType().get(i).getURL());
                                    boolean showall = false;
                                    if (res2 != null && res2.getQuickStatWrapper() != null && !res2.getQuickStatWrapper().isEmpty()) {

                                        for (int i2 = 0; i2 < res2.getQuickStatWrapper().size(); i2++) {

                                            if (showall || (!showall && res2.getQuickStatWrapper().get(i2).getAction().equalsIgnoreCase("All-Methods"))) {
                                                //    out.write(res2.getQuickStatWrapper().get(i2).getAction() + "<br>");
                                                if (result != null && res2.getQuickStatWrapper().get(i2).getUptime() != null) {
                                                    if (result.isOperational()) {
                                                        out.write(" | Current Up Time: " + Helper.DurationToString(res2.getQuickStatWrapper().get(i2).getUptime()));
                                                    } else {
                                                        out.write(" | Current Down Time: " + Helper.DurationToString(res2.getQuickStatWrapper().get(i2).getUptime()));
                                                    }
                                                }
                                                out.write("<table ><tr><td>");

                                                if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.TRANSACTIONAL) {
                                                    out.write("<table  class=\"table table-striped table-bordered\" ><tr>"
                                                            + "<th>Range(min)</th>"
                                                            + "<th>Availability</th>"
                                                            + "<th>Avg Res(ms)</th>"
                                                            + "<th>Success</th>"
                                                            + "<th>Faults</th>"
                                                            + "<th>SLA Fault</th>"
                                                            //    + "<th>SLA Compliance</th>"
                                                            + "<th>MTBF</th>"
                                                            + "<th>Max Req Size</th>"
                                                            + "<th>Max Res Size</th>"
                                                            + "<th>Max Res (ms)</th>"
                                                            + "<th>Updated at </th></tr>");
                                                } else if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.STATISTICAL) {
                                                    out.write("<table  class=\"table table-striped table-bordered\" ><tr>"
                                                            + "<th>Range(min)</th>"
                                                            + "<th>Availability</th>"
                                                            + "<th>SLA Fault</th>"
                                                            + "<th>Max Depth</th>"
                                                            + "<th>In</th>"
                                                            + "<th>Out</th>"
                                                            + "<th>Dropped</th>"
                                                            + "<th>Updated at </th></tr>");
                                                } else if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.MACHINE) {
                                                    out.write("<table  class=\"table table-striped table-bordered\" ><tr>"
                                                            + "<th>Range(min)</th>"
                                                            + "<th>Availability</th>"
                                                            + "<th>SLA Fault</th>"
                                                            + "<th>Avg CPU</th>"
                                                            + "<th>Avg Mem</th>"
                                                            + "<th>Avg Thread</th>"
                                                            + "<th>Updated at </th></tr>");
                                                } else if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.PROCESS) {
                                                    out.write("<table class=\"table table-striped table-bordered\" ><tr>"
                                                            + "<th>Range(min)</th>"
                                                            + "<th>Availability</th>"
                                                            + "<th>SLA Fault</th>"
                                                            + "<th>Avg CPU</th>"
                                                            + "<th>Avg Mem</th>"
                                                            + "<th>Avg Thread</th>"
                                                            + "<th>Avg File Handles</th>"
                                                            + "<th>Updated at </th></tr>");
                                                } else {
                                                    out.write("<table class=\"table table-striped table-bordered\" ><tr>"
                                                            + "<th>Range(min)</th>"
                                                            + "<th>Availability</th>"
                                                            + "<th>SLA Fault</th>"
                                                            + "<th>Updated at </th></tr>"
                                                            + "</tr>");
                                                }
                                                DecimalFormat myFormatter = new DecimalFormat("###.##");
                                                for (int k = 0; k < res2.getQuickStatWrapper().get(i2).getQuickStatData().size(); k++) {
                                                    out.write("<tr>");
                                                    Duration d = fac.newDuration(res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getTimeInMinutes().longValue() * 60 * 1000);
                                                    out.write("<td>" + Helper.DurationToString(d));
                                                    //out.write("<td>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getTimeInMinutes().toString());
                                                    out.write("</td><td>");
                                                    out.write(myFormatter.format(res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAvailabilityPercentage()));
                                                    out.write("%</td>");
                                                    if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.TRANSACTIONAL) {
                                                        out.write("<td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageResponseTime());
                                                        out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSuccessCount());
                                                        out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getFailureCount());
                                                        out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSLAViolationCount());
                                                        //     if (res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSLAViolationCount() == 0) {
                                                        //        out.write("<td>100%</td>");
                                                        //    } else {
                                                        //        long total = res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSuccessCount()
                                                        //                + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getFailureCount();
                                                        //        double slaf = (double) (total - res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSLAViolationCount()) / (double) total;
                                                        //        slaf = slaf * 100;
                                                        //       out.write("<td>" + myFormatter.format(slaf) + "%</td>");

                                                        //     }
                                                        out.write("</td><td align=center>" + Helper.DurationToString(res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getMTBF()));
                                                        out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getMaximumRequestSize());
                                                        out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getMaximumResponseSize());
                                                        out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getMaximumResponseTime());
                                                        out.write("</td>");

                                                    } else {
                                                        out.write("<td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getSLAViolationCount() + "</td>");
                                                        if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.MACHINE) {
                                                            out.write("<td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageCPUUsage());
                                                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageMemoryUsage());
                                                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageThreadCount() + "</td>");

                                                        }
                                                        if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.PROCESS) {
                                                            out.write("<td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageCPUUsage());
                                                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageMemoryUsage());
                                                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageThreadCount() + "</td>");
                                                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageOpenFileCount() + "</td>");
                                                        }
                                                        if (list.getServiceList().getServiceType().get(i).getPolicyType() == PolicyType.STATISTICAL) {
                                                            out.write("<td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getLargestQueueDepth());
                                                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageMessagesIn());
                                                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageMessagesOut());
                                                            out.write("</td><td align=center>" + res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getAverageMessagesDropped() + "</td>");
                                                        }

                                                    }
                                                    out.write("<td ");
                                                    long diff = System.currentTimeMillis() - res2.getQuickStatWrapper().get(i2).getQuickStatData().get(k).getUpdatedAt().getTimeInMillis();;
                                                    if (diff > (60 * 60 * 1000)) {
                                                        out.write(" class=alert");
                                                    } else if (diff > (10 * 60 * 1000)) {
                                                        out.write(" class=warn");
                                                    }
                                                    out.write(">" + Helper.DurationToString(fac.newDuration(diff)));
                                                    out.write("</td>");
                                                    out.write("</tr>");
                                                }
                                                out.write("</table>");
                                            }

                                        }
                                    } else if (list.getServiceList().getServiceType().get(i).getPolicyType().equals(PolicyType.TRANSACTIONAL)) {
                                        out.write(" | No data is currently available.");
                                    }

                                } catch (Exception ex) {
                                    out.write("<font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font>");
                                    LogHelper.getLog().log(Level.ERROR, "Error from quickstats.jsp", ex);
                                }
                                out.write("</td></tr></table>"); //end of table used for formatting non transactional data (process/machine)

                            }
                        %>
                    </div>
                </div>
            </div>
            <%
                }

            %>















            <%                         //whatever in the list remains is stuff that is updated directly via the status service or other status agents
                for (int i = 0; i < statuslist.size(); i++) {

                    GregorianCalendar time = (GregorianCalendar) statuslist.get(i).getTimeStamp();
                    long diff = System.currentTimeMillis() - time.getTimeInMillis();

                    if ((filter.equalsIgnoreCase("OfflineOnly") && (!statuslist.get(i).isOperational())) || filter.equalsIgnoreCase("AllItems") || diff > (60 * 60 * 1000)) {
            %>
            <div class="accordion-group">
                <div class="accordion-heading">
                    <%
                        if (statuslist.get(i).isOperational()) {
                            //if diff is over an hour, flag as red
                            if (diff > (60 * 60 * 1000)) {
                                out.write(erroricon);  //was alert
                            } else if (diff > (10 * 60 * 1000)) {
                                //if diff is over 10 minutes, flag as orange
                                out.write(warnicon);
                            } else {
                                out.write(okicon);  //we are ok
                            }

                        } else {
                            out.write(erroricon);  //was alert
                        }

                        out.write("<a data-toggle=\"collapse\" data-parent=\"#accordion2\" href=\"#collapses" + i + "\"  ");

                        if (statuslist.get(i).isOperational()) {
                            //if diff is over an hour, flag as red
                            if (diff > (60 * 60 * 1000)) {
                                out.write("class=\"text-error\"");        //was alert
                            } else if (diff > (10 * 60 * 1000)) {
                                //if diff is over 10 minutes, flag as orange
                                out.write("class=\"text-warn\" "); //was warn
                            } else {
                                /// we're a-ok
                            }
                        } else {
                            out.write("class=\"text-warn\" ");   //was alert
                        }
                        out.write(" title=\"" + Utility.encodeHTML(statuslist.get(i).getURI()) + "\">");

                        out.write(Utility.encodeHTML(Utility.truncate(statuslist.get(i).getURI(), textlength)));
                        if (statuslist.get(i).isOperational()) {
                            out.write(" - GO");
                        } else {
                            out.write(" - NO GO");
                        }
                        //if diff is over an hour, flag as red
                        if (diff > (60 * 60 * 1000)) {
                            out.write("- Very Stale ");
                        } else if (diff > (10 * 60 * 1000)) {
                            //if diff is over 10 minutes, flag as orange
                            out.write(" - Stale");
                        }
                        out.write("</a>");
                    %>

                </div>
                <div id="collapses<%=i%>" class="accordion-body collapse in">
                    <div class="accordion-inner">
                        <%

                            //content of the slider tab
                            out.write("<a class=\"btn btn-primary\" href=\"javascript:loadpage('profile.jsp?url=" + URLEncoder.encode(list.getServiceList().getServiceType().get(i).getURL(), "UTF-8")
                                    + "','mainpane');" + "\">Manage</a> | ");
                            out.write("<a href=\"availability.jsp?url="
                                    + URLEncoder.encode(statuslist.get(i).getURI()) + "\">Availability</a> | ");

                            /*
                         * out.write(statuslist.get(i).getPolicyType().value() + " |
                         * "); if (statuslist.get(i).getPolicyType() ==
                         * PolicyType.MACHINE) { out.write("<a
                         * href=\"machinePerformance.jsp?uri=" +
                         * URLEncoder.encode(statuslist.get(i).getURI() +
                         * "\">Performance</a> | ")); } if
                         * (statuslist.get(i).getPolicyType() == PolicyType.PROCESS)
                         * { out.write("<a href=\"processPerformance.jsp?uri=" +
                         * URLEncoder.encode(statuslist.get(i).getURI() +
                         * "\">Performance</a> | ")); }
                             */
                            out.write("<font color=");
                            if (statuslist.get(i).isOperational()) {
                                out.write("#00DD00 title=\"" + Utility.encodeHTML(statuslist.get(i).getMessage()) + "\">GO - ");
                                out.write(Utility.encodeHTML(statuslist.get(i).getMessage()));
                            } else {
                                out.write("#EE0000 title=\"" + Utility.encodeHTML(statuslist.get(i).getMessage()) + "\">NO GO - ");
                                out.write(Utility.encodeHTML(statuslist.get(i).getMessage()));
                            }
                            out.write("</font> | ");

                            //if diff is over an hour, flag as red
                            if (diff > (60 * 60 * 1000)) {
                                out.write("<font color=#FF0000 title=\"" + Utility.durationToString(fac.newDuration(diff)) + "\">");

                            } else if (diff > (10 * 60 * 1000)) {
                                //if diff is over 10 minutes, flag as orange
                                out.write("<font color=#FF7D40 title=\"" + Utility.durationToString(fac.newDuration(diff)) + "\">");
                            } else {
                                out.write("<font>");
                            }
                            String timestamp = Utility.durationLargestUnitToString(fac.newDuration(diff));
                            out.write("Last Check In: " + timestamp + "</font> |  ");

                            if (statuslist.get(i).getLastStatusChangeTimeStamp() != null) {
                                long asd = System.currentTimeMillis() - statuslist.get(i).getLastStatusChangeTimeStamp().getTimeInMillis();
                                Duration updown = fac.newDuration(asd);
                                if (statuslist.get(i).isOperational()) {
                                    out.write(" Up Time " + Helper.DurationToString(updown));
                                } else {
                                    out.write(" Down Time " + Helper.DurationToString(updown));
                                }
                            }
                            out.write(" This service has never checked in.");

                        %>
                    </div>
                </div>
            </div>
            <%                       }
                }
            %>

            <%
                    }
                } catch (SecurityException ex) {
                    out.write("Access was denied to the requested resource.");
                    LogHelper.getLog().log(Level.WARN, "index2", ex);
                } catch (Exception ex) {
                    out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
                    LogHelper.getLog().log(Level.WARN, "index2", ex);
                }
            %>




        </div>










    </div>

</div>

<script type="text/javascript">
    $('#accordion2').css('height', '');
</script>