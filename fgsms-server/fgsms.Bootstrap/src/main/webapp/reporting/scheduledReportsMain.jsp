<%-- 
    Document   : scheduledReports
    Created on : May 29, 2012, 1:46:15 PM
    Author     : Administrator
--%>



<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.Plugin"%>
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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Scheduled Reports</h1>
    <p>FGSMS can generate reports for you automatically on a schedule. Once a report is generated, you will be notified when its ready for pickup. 
        Reports will stay available until you delete them. You can also give read only access to other users.</p>
    <a href="javascript:loadpage('help/scheduledreports.jsp','mainpane','scheduledReportsMain.jsp');" class="btn btn-primary">Learn More</a>
</div>



<script  type="text/javascript" src="js/scheduledReports/scheduledReportsGeneral.js"></script>
<script  type="text/javascript" src="js/scheduledReports/scheduledReportsScript.js"></script>
<script  type="text/javascript" src="js/scheduledReports/main.js"></script>


<div id="result"></div>



<%    //initializing things
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    if (c == null) {
        c = new SecurityWrapper(ClassificationType.U, "");
    }
    ProxyLoader pl = ProxyLoader.getInstance(application);
    AutomatedReportingService arsport = pl.GetARS(application, request, response);
    List<ServiceType> slist = null;

    DataAccessService dasport = pl.GetDAS(application, request, response);

    try {
        GetMonitoredServiceListRequestMsg Listrequest = new GetMonitoredServiceListRequestMsg();
        Listrequest.setClassification(c);
        GetMonitoredServiceListResponseMsg list = dasport.getMonitoredServiceList(Listrequest);
        if (list == null || list.getServiceList() == null || list.getServiceList() == null
                || list.getServiceList().getServiceType() == null
                || list.getServiceList().getServiceType().size() == 0) {
            //   out.write("<br>There are currently no services being monitored that you have access to.");
        } else {
            slist = list.getServiceList().getServiceType();

        }
    } catch (Exception ex) {
    }

    //debugging purposes
    if (request.getMethod().equalsIgnoreCase("POST")) {

    } //end of if it was called through POST request 
    //if its the first time being called.
    else {
        GetMyScheduledReportsRequestMsg rs = new GetMyScheduledReportsRequestMsg();
        rs.setClassification(c);
        rs.setOffset(0);
        rs.setRecordlimit(100);

        //draw the initial table with the information on produced reports
        GetMyScheduledReportsResponseMsg rr = arsport.getMyScheduledReports(rs);
        if (rr != null && !rr.getCompletedJobs().isEmpty()) {
            String rrSize = Integer.toString(rr.getCompletedJobs().size());

%>


<div id="response"></div>

<p><b>Click on the Job Id to edit. </b></p>
<br/>
<p>Scheduled Reporting Tasks (<span id="rrSize"><%=rr.getCompletedJobs().size()%></span>)</p><br />

<div id="result"></div>
<table id="existingRecords" class="table table-condensed"  >
    <thead>
        <tr>
            <th>Job Id</th>
            <th>Friendly Name</th>
            <th>Available Reports</th>
            <th>Downloads</th>
            <th>Owner</th>
            <th>Additional Readers</th>
        </tr>
    </thead>
    <tbody>
        <% for (int i = 0; i < rr.getCompletedJobs().size(); i++) {
                ReportDefinition rd = rr.getCompletedJobs().get(i).getJob();
                List<CompletedJobs> rl = rr.getCompletedJobs().get(i).getReports();
                List<String> additionalReadersList = rd.getAdditionalReaders();
                boolean enabled = rd.isEnabled();
                String owner = rd.getOwner();
                String friendlyName = rd.getFriendlyName();
                String jobId = rd.getJobId();

        %>
        <tr>
            <td class='jobId'> 
                <button type="button" class="deleteJob">x</button><a href="javascript:loadpage('reporting/scheduledReportsMainEdit.jsp?jobId=<% out.write(URLEncoder.encode(jobId));  %>','mainpane', 'data.jsp&reporting/scheduledReportsMain.jsp');"><% out.write(Utility.encodeHTML(jobId)); %></a></td>
            <td class='editable'> <% out.write(Utility.encodeHTML(friendlyName)); %> </td>
            <td><span class='numberOfReports'><% out.write(Utility.encodeHTML(Integer.toString(rl.size()))); %></span></td>
            <td id="reportsList">
                <%
                    for (int k = 0; k < rl.size(); k++) {
                        if (rl.get(k).getTimestamp() != null) {
                            String pickupUrl = (String) pl.getRawConfiguration().get("reportpickuplocation");
                            out.write("<button type='button' class='delReport'>x</button><a title='dwl' href=\"" + pickupUrl + "?reportid=" + URLEncoder.encode(rl.get(k).getReportId()) + "\">" + Utility.formatDateTime(rl.get(k).getTimestamp()) + "</a><input type='hidden' value='" + URLEncoder.encode(rl.get(k).getReportId()) + "'/><br />");
                        }

                    } %>
            </td>
            <td class='editable'><% out.write(Utility.encodeHTML(owner)); %></td>
            <td>
                <span id="readersList">
                    <%
                        if (additionalReadersList.size() == 0) {
                            out.write("N/A");
                        } else {
                            for (int k = 0; k < additionalReadersList.size(); k++) // {out.write("<input type='button' class='delReaders' value='"+URLEncoder.encode(additionalReadersList.get(k))+"' />");}} 
                            {
                                out.write("<p>" + URLEncoder.encode(additionalReadersList.get(k)) + "</p>");
                            }
                        }
                    %>
                </span>
            </td>

            <%
                } %>


    </tbody>
</table>       













<%

} else {
%>
There are currently no schedule reporting jobs defined. 

<%
        }

    }
%>
<br />
<br />

<p><a href="javascript:toggleNewReport();" class="btn btn-large btn-primary">Schedule a New Reporting Job</a></p>
<div id="accordion" style="hidden">
    <h3><a name="1">Step 1 - HTML Report or CSV Export?</a></h3>
    <div>
        The report generator can create either HTML Reports which includes aggregated data and rendered charts OR a Comma Separated Value data export which will let you process the data in a spreadsheet program.
        <div id="exporttype">
            <input type=radio name=exporttype id="HTML" value="HTML" onclick="javascript:toggleVisiblityLocal(false);"><label for="HTML" id="labelHTML">HTML Report with charts</label>
            <input type=radio name=exporttype id="CSV" value="CSV"   onclick="javascript:toggleVisiblityLocal(true);"><label for="CSV" id="labelCSV">CSV Data Export</label>
        </div>
    </div>

    <h3><a name="2">Step 2 - Data/Report Type</a></h3>
    <div>
        <div id="csv" style="display: none">Type of data to export:<br>
            <div id="recordstype">
                <%
                    for (int i = 0;
                            i < ExportRecordsEnum.values().length; i++) {
                        out.write("<input type=radio name=recordstype value=\"" + ExportRecordsEnum.values()[i].value() + "\"  id=\"" + ExportRecordsEnum.values()[i].value() + "\"");
                        if (i == 0) {
                            out.write("checked=checked ");
                        }
                        out.write("><label for=\"" + ExportRecordsEnum.values()[i].value() + "\" id=\"label" + ExportRecordsEnum.values()[i].value() + "\">" + ExportRecordsEnum.values()[i].value() + "</label>");
                    }

                %>
            </div>
        </div>
        <div id="html" style="display: none">HTML Report
            <table border="1"><tr><th></th><th>Report Type</th></tr>
                        <%
                    List<Plugin> plugins = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "REPORTING");
                    for (int i = 0; i < plugins.size(); i++) {
                        out.write("<tr><td><input type=\"checkbox\" name=\"REPORTTYPE_" + Utility.encodeHTML(plugins.get(i).getClassname()) + "\"></td><td>"
                                +Utility.encodeHTML(plugins.get(i).getDisplayname()));
                        out.write("</td></tr>");
                    }


                        

                        %>
            </table>
        </div>

    </div>
    <h3><a name="3">Step 3 - Services</a></h3>
    <div>
        <div id="services"  >
            <%            //if csv export, filter these results on previous selection
                if (slist != null) {
                    out.write("<table border=1><tr><th><a href=\"javascript:checkAll();\">All</a>");
                    out.write(" <a href=\"javascript:uncheckAll();\">None</a>");
                    out.write("</th><th>Service URL</th><th>Type</th></tr>");
                    //replace with a buttom to select all via javascript 
                    //out.write("<tr><td><input type=\"checkbox\" name=\"selectAllServices\" /></td><td>All Services</td></tr>");
                    for (int i = 0; i < slist.size(); i++) {
                        out.write("<tr><td><input type=\"checkbox\" name=\"URL" + Utility.encodeHTML(slist.get(i).getURL()) + "\"></td>");
                        out.write("<td>"
                                + Utility.encodeHTML(slist.get(i).getURL()) + "</td><td>" + slist.get(i).getPolicyType().value() + "</td></tr>");
                    }
                    out.write("</table>");
                }
            %>
        </div>
        <div id="auditselect" style="display: none">
            <p>For these selection types, this module option is unavailable.</p>
        </div>

    </div>
    <h3><a name="4">Step 4 - Time Range</a></h3>
    <div>
        The scheduled report generator uses relative time ranges to calculate the which records are included and which are not. For example, run the report on the last days worth of data, week or month.
        You'll need to select a start and end period of time, a <b>duration</b> <a href="javascript:ShowHelp('timeformats');">help</a>). Here's some examples:<br><Br>
        Start 2d, End 1d = Run the report starting from 2 days ago until 1 day ago.<Br>
        Start 2d, End 0 = Run the report on from 2 days ago up until now (the time the report is generated).<br>
        <table>
            <tr>
                <td>From</td>
                <td><input type="text" name="fromtime" id="fromtime" style="width:150px; height: 24px"></td>
                <td><div id="fromtimeerror"></div></td>
            </tr>
            <tr>
                <td>Until</td>
                <td><input type="text" name="untiltime" id="untiltime" style="width:150px; height: 24px" value="0"></td>
                <td><div id="untiltimeerror"></div></td>
            </tr>
        </table>
        Dates and times are always calculated when the reporting job starts.

    </div>

    <h3><a name="5">Step 5 - Access Control</a></h3>
    <div>
        You can optionally grant read access to the generated reports to other users, however you will be the only one who can make changes to or delete this scheduled report.
        Enter names delimited by a semicolon (;).<Br>
        <input type="text" name="additionalusers" id="additionalusers"  style="width:150px; height: 24px">
    </div>

    <h3><a name="6">Step 6 - Scheduling</a></h3>
    <div>
        The next step is to define the schedule for this report. You run the right away or to schedule the report to be generated at some point in the future on a reoccurring basis. If manually inputting the data, the date must be of the format mm/dd/yyyy. <Br><Br>
        Starting on and after (click for calendar)<input type="text" name="fromdatepicker"  id="fromdatepicker"/><br>
        Start the report generation at  <input type="text" name="startathour" maxlength="2"  size="2"   id="startathour"  value="<%=Calendar.getInstance().get(Calendar.HOUR_OF_DAY)%>" /> : 
        <input type="text" name="startatminute" maxlength="2"  size="2"   id="startatminute"  value="<%=Calendar.getInstance().get(Calendar.MINUTE)%>" /><br>

        <div id="ScheduleType">
            <input type="radio" name="ScheduleType" value="daily" id="daily" onclick="javascript:toggleSchedule(true, false, false, false, false);"/><label for="daily" id="labeldaily">Daily</label>
            <input type="radio" name="ScheduleType" value="weekly" id="weekly" onclick="javascript:toggleSchedule(false, true, false, false, false);"/><label for="weekly" id="labelweekly">Weekly</label>
            <input type="radio" name="ScheduleType" value="monthly" id="monthly"  onclick="javascript:toggleSchedule(false, false, true, false, false);"/><label for="monthly" id="labelmonthly">Monthly</label>
            <input type="radio" name="ScheduleType" value="once" id="once"  onclick="javascript:toggleSchedule(false, false, false, true, false);"/><label for="once" id="labelonce">Once</label>
            <input type="radio" name="ScheduleType" value="immediate" id="immediate"  onclick="javascript:toggleSchedule(false, false, false, false, true);"/><label for="immediate" id="labelimmediate">Immediate</label>
        </div>
        <script type="text/javascript" language="javascript">


            function toggleSchedule(daily, weekly, monthly, once, immediate)
            {
                var x = document.getElementById('dailydiv');
                x.style.display = "none";
                x = document.getElementById('weeklydiv');
                x.style.display = "none";
                x = document.getElementById('monthlydiv');
                x.style.display = "none";
                if (daily)
                {
                    x = document.getElementById('dailydiv');
                    x.style.display = "";

                }
                if (weekly)
                {
                    x = document.getElementById('weeklydiv');
                    x.style.display = "";
                }
                if (monthly)
                {
                    x = document.getElementById('monthlydiv');
                    x.style.display = "";
                }
            }

        </script>
        <div id="dailydiv" style="display: none">
            Reoccur every <input type="text" name="dailyreoccur" maxlength="3"    id="dailyreoccur"  value="1" /> days.<br>    
        </div>
        <div id="weeklydiv" style="display: none">
            Reoccur every <input type="text" name="weeklyreoccur" maxlength="3"    id="weeklyreoccur"  value="1" /> weeks.<br>    
            On the days: 
            <select name="dayoftheweekselection" id="dayoftheweekselection" multiple="multiple" >
                <option value="SUNDAY" >Sunday</option>
                <option value="MONDAY">Monday</option>
                <option value="TUESDAY">Tuesday</option>
                <option value="WEDNESDAY">Wednesday</option>
                <option value="THURSDAY">Thursday</option>
                <option value="FRIDAY">Friday</option>
                <option value="SATURDAY">Saturday</option>
            </select>
        </div>
        <div id="monthlydiv" style="display: none">

            Run on when the date is         <select name="dayofmonthselection" id="dayofmonthselection" multiple="multiple" >
                <option value="1" >1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>    
                <option value="6">6</option>
                <option value="7">7</option>
                <option value="8">8</option>    
                <option value="9">9</option>

                <option value="10">10</option>
                <option value="11">11</option>
                <option value="12">12</option>
                <option value="13">13</option>
                <option value="14">14</option>
                <option value="15">15</option>    
                <option value="16">16</option>
                <option value="17">17</option>
                <option value="18">18</option>    
                <option value="19">19</option>
                <option value="20">20</option>
                <option value="21">21</option>
                <option value="22">22</option>
                <option value="23">23</option>

                <option value="24">24</option>
                <option value="25">25</option>    
                <option value="26">26</option>
                <option value="27">27</option>
                <option value="28">28</option>    
                <option value="29">29*</option>
                <option value="30">30*</option>
                <option value="31">31*</option>


            </select>
            <br>
            In the month(s): 
            <select name="monthselection" id="monthselection" multiple="multiple" >
                <option value="JANUARY" >January</option>
                <option value="FEBRUARY">February</option>
                <option value="MARCH">March</option>
                <option value="APRIL">April</option>
                <option value="MAY">May</option>
                <option value="JUNE">June</option>    
                <option value="JULY">July</option>
                <option value="AUGUST">August</option>
                <option value="SEPTEMBER">September</option>    
                <option value="OCTOBER">October</option>
                <option value="NOVEMBER">November</option>
                <option value="DECEMBER">December</option>

            </select>
        </div>



    </div>

    <h3><a name="7">Step 7 - Options</a></h3>
    <div>

        There are a few other options you can set when defining reports.<br>
        <table border="1">
            <tr>
                <td>Friendly Name - This is used as a description to make it easier for you to identify</td>
                <td><input type="text" name="friendlyname" value="My Report" /> </td>
            </tr>
            <tr>
                <td>Enabled - This will allow you to enable or disable report generation.</td>
                <td><input type="checkbox" name="enabled" checked /> </td>
            </tr>
        </table>


    </div> 


    <h3><a name="8">Step 8 - Validate and Submit</a></h3>
    <div>

        <input type="button" name="submitbutton" value="Submit" onclick="javascript:validate();" />
        <div id="errors"></div>
        <input type="submit" name="realsubmit" value="submit2" style="display:none"/>
    </div>

</div>

<script language="javascript" type="text/javascript">


    function CVXExportOptionSelected()
    {
//label" + ExportRecordsEnum.values()[i].value()
        var ok = false;
        var x = null;
    <% for (int i = 0;
                    i < ExportRecordsEnum.values().length; i++) {
                out.write("        x=document.getElementById(\"label" + ExportRecordsEnum.values()[i].value() + "\");");
                out.write("      if (x.getAttribute(\"aria-pressed\") == \"true\")");
                out.write("          ok = true;");
            }

    %>
        return ok;
    }


</script>





