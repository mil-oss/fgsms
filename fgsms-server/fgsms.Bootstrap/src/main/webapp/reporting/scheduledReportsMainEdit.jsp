<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.RunAtLocation"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Date"%>
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
</div>

<script  type="text/javascript" src="js/scheduledReports/scheduledReportsGeneral.js"></script>


<%    String jobId = request.getParameter("jobId");

    //initializing things
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

    GetMyScheduledReportsRequestMsg rs = new GetMyScheduledReportsRequestMsg();
    rs.setClassification(c);
    rs.setOffset(0);
    rs.setRecordlimit(100);
    GetMyScheduledReportsResponseMsg rr = arsport.getMyScheduledReports(rs);

    ReportDefinition rd = rr.findJobWithIdOf(jobId);
    if (rd == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    boolean html = false;
    List<String> urlList;
    ExportRecordsEnum exportType = null;
    ArrayOfReportTypeContainer reportTypes = null;

    String start = null;
    String end = null;
    List<String> additionalReaders = rd.getAdditionalReaders();
    String friendlyName = rd.getFriendlyName();
    boolean enabled = rd.isEnabled();

    String reoccurance = "1";
    List<SLAAction> notifications = rd.getNotifications();
    SLAAction notificationInstance = null;
    String destinationOverride = null; //amqp, jms, wsn
    boolean isTopic = false; //amqp, jms
    String logTo = null; //log
    String implementingClassName = null;
    String notificationType = null;
    String command = null;
    String runFromPath = null;
    // RunAtLocation runAtLocation = null; 

    String additionalReadersList = "";
    for (int addIt = 0; addIt < additionalReaders.size(); addIt++) {
        additionalReadersList = additionalReadersList.concat(additionalReaders.get(addIt));
        if (addIt < (additionalReaders.size() - 1)) {
            additionalReadersList = additionalReadersList.concat("; ");
        }
    }

    ScheduleDefinition sd = rd.getSchedule();
    List<AbstractSchedule> triggersList = sd.getTriggers();
    GregorianCalendar startingOn = new GregorianCalendar();
    GregorianCalendar startingAt = new GregorianCalendar();
    String simpleName = null;

    boolean weekly = false;
    boolean daily = false;
    boolean monthly = false;
    boolean once = false;
    boolean immediate = false;
    List<Daynames> daysOfTheWeek = null;
    List<Monthnames> monthNames = null;
    List<Integer> daysOfTheMonth = null;
    List<ReportTypeContainer> reportTypeList = null;
    //  List<String> daysOfTheWeekString;
    for (int listIt = 0; listIt < triggersList.size(); listIt++) {
        AbstractSchedule trigger = triggersList.get(listIt);
        startingAt.setTime(trigger.getStartingAt().getTime());

        simpleName = trigger.getClass().getSimpleName();
        //     out.write("simpleName is: "+simpleName); 
        if (simpleName.equals(WeeklySchedule.class.getSimpleName())) {
            weekly = true;
            daysOfTheWeek = ((WeeklySchedule) trigger).getDayOfTheWeekIs();
            reoccurance = ((WeeklySchedule) trigger).getReoccurs().toString();
        } else if (simpleName.equals(DailySchedule.class.getSimpleName())) {
            reoccurance = ((DailySchedule) trigger).getReoccurs().toString();
            daily = true;
        } else if (simpleName.equals(MonthlySchedule.class.getSimpleName())) {
            monthly = true;
            monthNames = ((MonthlySchedule) trigger).getMonthNameIs();
            daysOfTheMonth = ((MonthlySchedule) trigger).getDayOfTheMonthIs();
        } else if (simpleName.equals(ImmediateSchedule.class.getSimpleName())) {
            immediate=true;

        } else if (simpleName.equals(OneTimeSchedule.class.getSimpleName())) {
            once = true;

        } else {
            //should probably log this, it's a crazy whacky error (unhandled case)
        }

        //FIXME once and immediate
    }
    String startingOnString = startingOn.toString();
    startingOnString = startingOnString.substring(0, startingOnString.length() - 1);
    String startingAtString = startingAt.toString();
    int startingAtHour = startingAt.get(Calendar.HOUR);
    int startingAtMinutes = startingAt.get(Calendar.MINUTE);

    if (null == rd.getExportCSVDataRequestMsg()) {
        html = true;
        urlList = rd.getExportDataRequestMsg().getURLs();
        reportTypes = rd.getExportDataRequestMsg().getReportTypes();
        reportTypeList = reportTypes.getReportTypeContainer();

        start = Helper.DurationToString(rd.getExportDataRequestMsg().getRange().getStart());
        end = Helper.DurationToString(rd.getExportDataRequestMsg().getRange().getEnd());
    } else {
        exportType = rd.getExportCSVDataRequestMsg().getExportType();
        urlList = rd.getExportCSVDataRequestMsg().getURLs();
        start = Helper.DurationToString(rd.getExportCSVDataRequestMsg().getRange().getStart());
        end = Helper.DurationToString(rd.getExportCSVDataRequestMsg().getRange().getEnd());
    }
    /**
     * out.write("urldebugging"); for(int it=0; it<urlList.size(); it++){
     * out.write("url: "+urlList.get(it)); } out.write("urldebuggingend"); *
     */

//    out.write("start: "+start); 
//    out.write("end: "+end); 
    if (end == "") {
        end = "0";
    }


%>

<p>You are currently viewing the details for the <b>jobId: <span id="jobId"><% out.write(StringEscapeUtils.escapeHtml4(jobId));  %></span></b></p>
<p>The options have been auto-filled with your current selections. In order to edit it, update the details and click Submit</p>


<div  id="accordion">
    <h3><a name="1">Step 1 - HTML Report or CSV Export?</a></h3>
    <div>
        The report generator can create either HTML Reports which includes aggregated data and rendered charts OR a Comma Separated Value data export which will let you process the data in a spreadsheet program.
        <div id="exporttype">
            <input type=radio name=exporttype id="HTML" value="HTML" onclick="javascript:toggleVisiblityLocal(false);" <% if (html) {
                    out.write("checked");
            } %>><label for="HTML" id="labelHTML" <% if (html) {
                out.write(" aria-pressed=\"true\" ");
            }
                %>>HTML Report with charts</label>
            <input type=radio name=exporttype id="CSV" value="CSV"   onclick="javascript:toggleVisiblityLocal(true);" <% if (!html) {
                    out.write("checked");
                } %>><label for="CSV" id="labelCSV"<% if (!html) {
                out.write(" aria-pressed=\"true\" ");
            }
                %>>CSV Data Export</label>
        </div>
    </div>

    <h3><a name="2">Step 2 - Data/Report Type</a></h3>
    <div>
        <div id="csv" <% if (html) {
                out.write("style='display: none'");
            } %>>Type of data to export:<br>
            <div id="recordstype">
                <%

                    for (int i = 0;
                            i < ExportRecordsEnum.values().length; i++) {
                        out.write("<input type=radio name=recordstype value=\"" + ExportRecordsEnum.values()[i].value() + "\"  id=\"" + ExportRecordsEnum.values()[i].value() + "\"");
                        if (exportType != null) {
                            if (ExportRecordsEnum.values()[i].value() == exportType.value()) {
                                out.write("checked=checked ");
                            }
                        } else if (i == 0) {
                            out.write("checked=checked ");
                        }
                        out.write("><label for=\"" + ExportRecordsEnum.values()[i].value() + "\" id=\"label" + ExportRecordsEnum.values()[i].value() + "\">" + ExportRecordsEnum.values()[i].value() + "</label>");
                    }

                %>
            </div>
        </div>
        <div id="html" <% if (!html) {
                out.write("style='display: none'");
            } %>>HTML Report



            <table border="1"><tr><th></th><th>Report Type</th></tr>
                        <%
                        
                        List<Plugin> plugins = Helper.GetPluginList(pl.GetPCS(application, request, response), c, "REPORTING");
                        for (int i = 0; i < plugins.size(); i++) {
                            out.write("<tr><td><input type=\"checkbox\" name=\"REPORTTYPE_" + Utility.encodeHTML(plugins.get(i).getClassname()) + "\" ");

                            if (reportTypes != null) {
                                for (int j = 0; j < reportTypeList.size(); j++) {
                                    if (reportTypeList.get(j).getType().equals(plugins.get(i).getClassname())) {
                                        out.write(" checked");
                                    }
                                }
                            }
                            out.write("></td><td>" + Utility.encodeHTML(plugins.get(i).getDisplayname()));
                            out.write("</td></tr>");
                        }

                        %>
            </table>
        </div>

    </div>
    <h3><a name="3">Step 3 - Services</a></h3>
    <div>
        <div id="services" >
            <%            //if csv export, filter these results on previous selection
                if (slist != null) {
                    out.write("<table border=1><tr><th><a href=\"javascript:checkAll();\">All</a>");
                    out.write(" <a href=\"javascript:uncheckAll();\">None</a>");
                    out.write("</th><th>Service URL</th><th>Type</th></tr>");
                    //replace with a buttom to select all via javascript 
                    //out.write("<tr><td><input type=\"checkbox\" name=\"selectAllServices\" /></td><td>All Services</td></tr>");
                    for (int i = 0; i < slist.size(); i++) {
                        String url = slist.get(i).getURL().trim();
                        String encodedUrl = Utility.encodeHTML(url);
                        out.write("<tr><td><input type=\"checkbox\" name=\"URL" + encodedUrl + "\"");
                        if (!urlList.isEmpty()) {
                            if (urlList.contains(url)) {
                                out.write(" checked ");
                            }
                        }
                        out.write("></td>");
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
        Scheduled reports use relative time ranges to calculate the which records are included and which are not. For example, run the report on the last days worth of data, week or month.
        You'll need to select a start and end period of time, a <b>duration</b> <a href="javascript:ShowHelp('timeformats');">help</a>). Here's some examples:<br><Br>
        Start 2d, End 1d = Run the report starting from 2 days ago until 1 day ago.<Br>
        Start 2d, End 0 = Run the report on from 2 days ago up until now.<br>
        <table>
            <tr>
                <td>From</td>
                <td><input type="text" name="fromtime" id="fromtime" style="width:150px; height: 24px" value="<% out.write(Utility.encodeHTML(start)); %>"></td>
                <td><div id="fromtimeerror"></div></td>
            </tr>
            <tr>
                <td>Until</td>
                <td><input type="text" name="untiltime" id="untiltime" style="width:150px; height: 24px" value="<% out.write(Utility.encodeHTML(end)); %>"></td>
                <td><div id="untiltimeerror"></div></td>
            </tr>
        </table>
        Dates and times are always calculated when the report job starts.

    </div>

    <h3><a name="5">Step 5 - Access Control</a></h3>
    <div>
        You can optionally grant read access to the generated reports to other users, however you will be the only one who can make changes to or delete this scheduled report.
        Enter names delimited by a semicolon (;).<Br>
        <input type="text" name="additionalusers" id="additionalusers"  style="width:150px; height: 24px" value="<% out.write(additionalReadersList); %>">
    </div>

    <h3><a name="6">Step 6 - Scheduling</a></h3>
    <div>
        The next step is to define the schedule for this report. You run the right away or to schedule the report to be generated at some point in the future on a reoccurring basis. <Br><Br>
        Starting on and after (click for calendar)<input type="text" name="fromdatepicker"  id="fromdatepicker"/><br>
        <input type="hidden" id="hiddenField" value="<% out.write(startingOnString); %>" />
        Start the report generation at  <input type="text" name="startathour" maxlength="2"  size="2"   id="startathour"  value="<% out.write(Integer.toString(startingAtHour)); %>" /> : 
        <input type="text" name="startatminute" maxlength="2"  size="2"   id="startatminute"  value="<% out.write(Integer.toString(startingAtMinutes)); %>" /><br>

        <div id="ScheduleType">
            <input type="hidden" id="simpleName" value="<% out.write(simpleName); %>" />
            <input type="radio" name="ScheduleType" value="daily"  id="daily" onclick="javascript:toggleSchedule(true, false, false);" <% if (daily) {
                    out.write("checked");
                } %> /><label for="daily" id="labeldaily">Daily</label>
            <input type="radio" name="ScheduleType" value="weekly" id="weekly" onclick="javascript:toggleSchedule(false, true, false);" <% if (weekly) {
                    out.write("checked");
                }  %> /><label for="weekly" id="labelweekly">Weekly</label>
            <input type="radio" name="ScheduleType" value="monthly" id="monthly"  onclick="javascript:toggleSchedule(false, false, true);" <% if (monthly) {
                    out.write("checked");
                } %> /><label for="monthly" id="labelmonthly">Monthly</label>
            
             <input type="radio" name="ScheduleType" value="once" id="once"  onclick="javascript:toggleSchedule(false, false, false, true, false);" <% if (once) {
                    out.write("checked");
                } %>
                    /><label for="once" id="labelonce">Once</label>
            <input type="radio" name="ScheduleType" value="immediate" id="immediate"  onclick="javascript:toggleSchedule(false, false, false, false, true);" <% if (immediate) {
                    out.write("checked");
                } %>
                   /><label for="immediate" id="labelimmediate">Immediate</label>
        </div>


        <script type="text/javascript" language="javascript">

            $(document).ready(function () {
                var simpleName = document.getElementById("simpleName").value;
                console.log(simpleName);
                if (simpleName === "DailySchedule")
                    toggleSchedule(true, false, false,false,false);
                else if (simpleName === "WeeklySchedule") {
                    toggleSchedule(false, true, false,false,false);
                    weekly();
                } else if (simpleName === "MonthlySchedule") {
                    toggleSchedule(false, false, true,false,false);
                    monthly();
                } else {
                    toggleSchedule(false, false, false,false,false);
                    //once and done, immediate
                }


                function monthly() {
                    console.log('monthly');
            <%
                if (monthNames != null) {
                    for (int i = 0; i < monthNames.size(); i++) {
                        String instance = monthNames.get(i).value();
                        out.write("$(\"#monthselection option[value='" + instance + "']\").attr('selected', 'selected');");
                    }
                } else {
                    out.write("alert(\"an error had occured\"); ");
                }

                if (daysOfTheMonth != null) {
                    for (int i = 0; i < daysOfTheMonth.size(); i++) {
                        String instance = daysOfTheMonth.get(i).toString();
                        out.write("$(\"#dayofmonthselection option[value='" + instance + "']\").attr('selected', 'selected');");
                    }
                } else {
                    out.write("alert(\"an error had occured\"); ");
                }
            %>
                }

                function weekly() {
                    console.log("weekly");
            <%
                if (daysOfTheWeek != null) {
                    for (int i = 0; i < daysOfTheWeek.size(); i++) {
                        String instance = daysOfTheWeek.get(i).value();
                        out.write("$(\"#dayoftheweekselection option[value='" + instance + "']\").attr('selected', 'selected');");
                    }
                } else {
                    out.write("alert(\"an error had occured\"); ");
                }
            %>
                }

            });



            //  var startString = '2012-09-22'; 
            var another = document.getElementById("hiddenField").value;
            //     console.log(another); 
            var dateParts = another.match(/(\d+)/g);
            var realDate = new Date(dateParts[0], dateParts[1] - 1, dateParts[2]);
            $('#fromdatepicker').datepicker({dateformat: 'yyyy-mm-dd'});
            $('#fromdatepicker').datepicker("setDate", realDate);



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
            Reoccur every <input type="text" name="dailyreoccur" maxlength="3"    id="dailyreoccur"  value="<%=reoccurance%>" /> days.<br>    
        </div>
        <div id="weeklydiv" style="display: none">
            Reoccur every <input type="text" name="weeklyreoccur" maxlength="3"    id="weeklyreoccur"  value="<%=reoccurance%>" /> weeks.<br>    
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
                <td><input type="text" name="friendlyname" value="<% out.write(Utility.encodeHTML(friendlyName)); %>" /> </td>
            </tr>
            <tr>
                <td>Enabled - This will allow you to enable or disable report generation.</td>
                <td><input type="checkbox" name="enabled" <% if (enabled) {
                        out.write("checked");
                    } %> /> </td>
            </tr>
        </table>


    </div>




    <h3><a name="9">Step 8 - Validate and Submit</a></h3>
    <div>

        <input type="button" name="submitbutton" value="Submit" onclick="javascript:validate();" />
        <div id="errors"></div>
        <input type="submit" name="realsubmit" value="submit2" style="display:none"/>
    </div>

</div>

<script language="javascript" type="text/javascript">
    /* 
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    function  toggleVisiblityLocal(obj)
    {
        if (obj == false)
        {
            var x = document.getElementById('csv');
            x.style.display = "none";
            x = document.getElementById('html');
            x.style.display = "";
        } else
        {
            var x = document.getElementById('csv');
            x.style.display = "";
            x = document.getElementById('html');
            x.style.display = "none";
        }
    }
    function checkAll()
    {
        var e = document.getElementsByTagName("input");
        if (e != null)
        {
            for (var i = 0; i < e.length; i++)
            {

                if (e[i].getAttribute("type") == "checkbox")
                {
                    var x = null;
                    x = e[i].getAttribute("name");
                    if (x != undefined)
                    {
                        x = x.toString();

                        if (StringStartsWith(x, "URL"))
                        {
                            e[i].removeAttribute("checked");
                            check(e[i]);

                        }
                    }
                }

            }
        }
    }

    function isAuditExportSelected()
    {
        var x = document.getElementById("labelAuditLogs");
        if (x.getAttribute("aria-pressed") == "true")
            return true;
        return false;
    }

    //false if no items are checked
    function isAtLeastOneUrlSelected()
    {
        var e = document.getElementsByTagName("input");
        if (e != null)
        {
            var found = false;
            for (var i = 0; i < e.length; i++)
            {

                if (e[i].getAttribute("type") == "checkbox")
                {
                    var x = null;
                    x = e[i].getAttribute("name");
                    if (x != undefined)
                    {
                        x = x.toString();

                        if (StringStartsWith(x, "URL"))
                        {
                            var attrib = e[i].getAttribute("checked");
                            if (attrib != null)
                            {
                                //alert (x + " is checked");
                                return true;
                            }
                            try {
                                if (e[i].checked)
                                    return true;
                            } catch (e1) {
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    function check(obj)
    {
        try {
            obj.checked = 'checked';
        } catch (e)
        {
        }
        try {
            obj.setAttribute('checked', 'checked');
        } catch (e) {
        }
        try {
            obj.attributes['checked'] = 'checked';
        } catch (e) {
        }
    }

    function StringStartsWith(data, input)
    {
        return (data.substring(0, input.length) === input);
    }
    function uncheckAll()
    {
        var e = document.getElementsByTagName("input");
        if (e != null)
        {
            for (var i = 0; i < e.length; i++)
            {

                if (e[i].getAttribute("type") == "checkbox")
                {
                    var x = null;
                    x = e[i].getAttribute("name");
                    if (x != undefined)
                    {
                        x = x.toString();

                        if (StringStartsWith(x, "URL"))
                        {
                            try {
                                e[i].checked = false;
                            } catch (s) {
                            }
                            try {
                                e[i].removeAttribute("checked");
                            } catch (s) {
                            }

                            //check(e[i]);
                        }
                    }
                }

            }
        }
    }

    function isHtmlExport()
    {
        var x = document.getElementById("labelHTML");
        if (x.getAttribute("aria-pressed") == "true")
            return true;
        return false;

    }

    //return true if there is an error
    function checkTime()
    {
        var startAtHour = $('input[name="startathour"]').val();
        var startAtMinute = $('input[name="startatminute"]').val();
        if (startAtHour > 24 || startAtHour < 0)
            return true;
        if (startAtMinute > 59 || startAtMinute < 0)
            return true;
        return false;

    }

    //return true if there is an error
    function validateSchedule()
    {
        //if immediate is not checked && count of selected items is 0 return true
        if (!isImmediateChecked())
        {
            var x = 0;
            if (weekly())
            {
                x += CountSelectedItems("dayoftheweekselection");
                if (x == 0)
                    return true;
            }
            if (monthly())
            {
                x += CountSelectedItems("dayofmonthselection");
                x += CountSelectedItems("monthselection");
                if (x < 2)
                    return true;
            }
            return checkTime();
        }
        return true;
        //check start on date
        //check start at time for validity


    }
    function weekly()
    {
        var x = document.getElementById("labelweekly");
        if (x.getAttribute("aria-pressed") == "true")
            return true;
        return false;
    }
    function monthly()
    {
        var x = document.getElementById("labelmonthly");
        if (x.getAttribute("aria-pressed") == "true")
            return true;
        return false;
    }
    function CountSelectedItems(elementname)
    {
        var element = document.getElementById(elementname);
        if (element == undefined)
            return 0;
        if (element == null)
            return 0;
        var count = 0;
        if (element.options == null)
            return 0;
        if (element.options == undefined)
            return 0;
        if (element.options.length == null)
            return 0;
        if (element.options.length == undefined)
            return 0;
        for (i = 0; i < element.options.length; i++) {
            if (element.options[i].selected) {
                //selectedArray[count] = element.options[i].value;
                count++;
            }
        }
        return count;
    }
    function isImmediateChecked()
    {
        return false;
    }

    function isExportTypeSelected()
    {
        var ok = false;
        var x = document.getElementById("labelHTML");

        if (x.getAttribute("aria-pressed") == "true")
            ok = true;
        x = document.getElementById("labelCSV");
        if (x.getAttribute("aria-pressed") == "true")
            ok = true;

        return ok;
    }
    function CVXExportOptionSelected()
    {
        //label" + ExportRecordsEnum.values()[i].value()
        var ok = false;
        var x = null;
    <%        for (int i = 0; i < ExportRecordsEnum.values().length; i++) {
            out.write("        x=document.getElementById(\"label" + ExportRecordsEnum.values()[i].value() + "\");");
            out.write("      if (x.getAttribute(\"aria-pressed\") == \"true\")");
            out.write("          ok = true;");
        }

    %>

        return ok;
    }
    function isAtLeastOneHtmlReportSelected()
    {
        var e = document.getElementsByTagName("input");
        if (e != null)
        {
            for (var i = 0; i < e.length; i++)
            {

                if (e[i].getAttribute("type") == "checkbox")
                {
                    var x = null;
                    x = e[i].getAttribute("name");
                    if (x != undefined)
                    {
                        x = x.toString();

                        if (!StringStartsWith(x, "URL") && x != "runimmediate")
                        {
                            var attrib = e[i].getAttribute("checked");
                            if (attrib != null)
                            {
                                //  alert (x + " is checked");
                                return true;
                            }
                            try {
                                if (e[i].checked)
                                    return true;
                            } catch (e1) {
                            }

                        }
                    }
                }

            }
        }
        return false;
    }
    function validate()
    {

        var ok = true;
        var err = "";
        //confirm that one type of report is selected
        //date range start is greater than end
        //confirm that at least one service is selected
        var isexportselected = isExportTypeSelected();
        var exporthtml = isHtmlExport();
        var oneselect = isAtLeastOneUrlSelected();
        var htmlreportsselect = isAtLeastOneHtmlReportSelected();
        var auditexport = isAuditExportSelected();

        if (!isexportselected)
        {
            ok = false;
            err += "You must select a choice of CSV export or HTML report<br>";
        } else
        {

            if (exporthtml)
            {
                if (!oneselect)
                {
                    ok = false;
                    err += "When creating an HTML report, at least one service URL must be selected<br>";
                }
                if (!htmlreportsselect)
                {
                    ok = false;
                    err += "When creating an HTML report, at least one report type must be selected<br>";
                }
            } else
            {
                //export CSV
                if (!auditexport && !oneselect)
                {
                    ok = false;
                    err += "When creating a CSV export, at least one service URL must be selected<br>";
                } else
                {
                    //export csv audit logs
                    //no special requirements
                }
                if (!CVXExportOptionSelected())
                {
                    ok = false;
                    err += "When creating a CSV export, you  must select an export type.<br>";
                }

            }
        }



        //confirm that a time range is selected and is valid
        if (validateDurations())
        {
            ok = false;
            err += "At least one of the time periods (durations) are invalid, return to that section for details<br>";
        }



        //confirm schedule is set and valid
        if (validateSchedule())
        {
            ok = false;
            err += "The schedule you defined is invalid, see that section for details<br>";
        }

        if (ok)
        {
            var jobId = $('span#jobId').contents().text();
            //alert("jobId: "+ jobId); 
            document.getElementById("errors").innerHTML = "Everythings OK, hang on....";
            //  document.forms["form1"].submit(); 
            postBackReRender('editJob', 'reporting/scheduledEditPost.jsp?jobId=' + jobId, 'mainpane', 'data.jsp&reporting/scheduledReportsMain.jsp');
            //        $.post("scheduledReportsPost.jsp", {jobId : jobId}, function(response){
            //     alert(response); 
            //     }); 

        } else
        {
            document.getElementById("errors").innerHTML = err;
        }
    }

    //return true if there is an error
    function validateDurations()
    {

        var x = sendValidationRequest(document.getElementById('fromtime').value, 'fromtimeerror');
        var y = sendValidationRequest(document.getElementById('untiltime').value, 'untiltimeerror');
        return (x == false || y == false);

    }


    //sends a duration validation requests
    //val = the users input
    //elementToUpdate is an element on the page that will have the inner html text updated with the response
    //returns true is its OK, otherwise false
    //will block until a response comes back
    function sendValidationRequest(val, elementToUpdate) {
        var xmlHttpReq = false;
        var XMLHttpRequestObject = false;
        // Mozilla/Safari
        if (window.XMLHttpRequest) {
            XMLHttpRequestObject = new XMLHttpRequest();
        }
        // IE
        else if (window.ActiveXObject) {
            //  self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
            try {
                XMLHttpRequestObject = new ActiveXObject("Msxml2.XMLHTTP.6.0");
            } catch (e) {
            }
            try {
                XMLHttpRequestObject = new ActiveXObject("Msxml2.XMLHTTP.3.0");
            } catch (e) {
            }
            try {
                XMLHttpRequestObject = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {
            }
            // XMLHttpRequestObject = new ActiveXObject("Microsoft.XMLHTTP");
        }
        XMLHttpRequestObject.open('GET', 'help/datevalidator.jsp?val=' + val, false);
        //XMLHttpRequestObject.timeout = 10000;
        XMLHttpRequestObject.send(null);
        //XMLHttpRequestObject.onreadystatechange = function() {
        //            if (XMLHttpRequestObject.readyState == 4) {
        if (XMLHttpRequestObject.status == 200)
        {
            document.getElementById(elementToUpdate).innerHTML = XMLHttpRequestObject.responseText;
            if (XMLHttpRequestObject.responseText == "Valid!")
                return true;
            else
                return false;
        } else {
            document.getElementById(elementToUpdate).innerHTML = "Unable to validate: " + XMLHttpRequestObject.responseText;
            return false;
        }
        // else window.location = "index.jsp";

        //        }
        //}

    }
    function addDayOfWeek()
    {
        // alert("1");
        var current;
        var dayoftheweekselection = document.getElementById("dayoftheweekselection");
        if (dayoftheweekselection != null)
        {
            current = dayoftheweekselection.options[dayoftheweekselection.selectedIndex].value;
        }
        var listbox = document.getElementById("dayoftheweek");

        //     var x=document.getElementById("mySelect");
        var option = document.createElement("option");
        option.text = current;
        var exists = false;
        $('#dayoftheweek option').each(function ()
        {
            if (this.value == current)
            {
                exists = true;
            }
        });
        if (!exists)
        {
            try
            {
                // for IE earlier than version 8
                listbox.add(option, x.options[null]);
            } catch (e)
            {
                listbox.add(option, null);
            }
        }
    }
    function removeDayOfWeek()
    {
        //    alert("2");
        var current;
        //var dayoftheweekselection=document.getElementById("dayoftheweekselection");
        var listbox = document.getElementById("dayoftheweek");
        if (dayoftheweekselection != null)
        {
            current = listbox.options[listbox.selectedIndex];
            if (current != null)
            {
                listbox.remove(listbox.selectedIndex);
            }
        }
    }
    function addHourOfDay()
    {
        //   alert("3");
        var current;
        var dayoftheweekselection = document.getElementById("hourofdayselection");
        if (dayoftheweekselection != null)
        {
            current = dayoftheweekselection.options[dayoftheweekselection.selectedIndex].value;
        }
        var listbox = document.getElementById("hourofday");

        var option = document.createElement("option");
        option.text = current;
        var exists = false;
        $('#hourofday option').each(function ()
        {
            if (this.value == current)
            {
                exists = true;
            }
        });
        if (!exists)
        {
            try
            {
                // for IE earlier than version 8
                listbox.add(option, x.options[null]);
            } catch (e)
            {
                listbox.add(option, null);
            }
        }
    }
    function removeHourOfDay()
    {
        //     alert("4");
        var current;
        //var dayoftheweekselection=document.getElementById("dayoftheweekselection");
        var listbox = document.getElementById("hourofday");
        if (dayoftheweekselection != null)
        {
            current = listbox.options[listbox.selectedIndex];
            if (current != null)
            {
                listbox.remove(listbox.selectedIndex);
            }
        }
    }
    function addDayOfMonth()
    {
        //    alert("5");
        var current;
        var dayoftheweekselection = document.getElementById("dayofmonthselection");
        if (dayoftheweekselection != null)
        {
            current = dayoftheweekselection.options[dayoftheweekselection.selectedIndex].value;
        }
        var listbox = document.getElementById("dayofmonth");

        var option = document.createElement("option");
        option.text = current;
        var exists = false;
        $('#dayofmonth option').each(function ()
        {
            if (this.value == current)
            {
                exists = true;
            }
        });
        if (!exists)
        {
            try
            {
                // for IE earlier than version 8
                listbox.add(option, x.options[null]);
            } catch (e)
            {
                listbox.add(option, null);
            }
        }
    }
    function removeDayOfMonth()
    {
        //     alert("6");
        var current;
        //var dayoftheweekselection=document.getElementById("dayoftheweekselection");
        var listbox = document.getElementById("dayofmonth");
        if (dayoftheweekselection != null)
        {
            current = listbox.options[listbox.selectedIndex];
            if (current != null)
            {
                listbox.remove(listbox.selectedIndex);
            }
        }
    }


</script>


