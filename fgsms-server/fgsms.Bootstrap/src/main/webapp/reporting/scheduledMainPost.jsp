<%-- 
    Document   : scheduledMainPost
    Created on : Sep 20, 2012, 2:48:57 PM
    Author     : Administrator
--%>

<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.ImmediateSchedule"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.OneTimeSchedule"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.MonthlySchedule"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ReportTypeContainer"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.Monthnames"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.Daynames"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.WeeklySchedule"%>
<%@page import="java.math.BigInteger"%>
<%@page import="org.apache.log4j.Level"%> 
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.DailySchedule"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.ScheduleDefinition"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ExportRecordsEnum"%>
<%@page import="java.util.List"%>
<%@page import="org.miloss.fgsms.services.interfaces.reportingservice.ArrayOfReportTypeContainer"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.TimeRangeDiff"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.ReportDefinition"%>
<%@page import="org.miloss.fgsms.services.interfaces.automatedreportingservice.AddOrUpdateScheduledReportRequestMsg"%>
<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Scheduled Reports</h1>
    <p>FGSMS can generate reports for you automatically on a schedule. Once a report is generated, you will be notified when its ready for pickup. 
        Reports will stay available until you delete them. You can also give read only access to other users.</p>
</div>

<%
    String addingJob = request.getParameter("addingJob");

    if (addingJob != null && addingJob.equals("true")) {

        //initializing things
        SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
        if (c == null) {
            c = new SecurityWrapper(ClassificationType.U, "");
        }
        ProxyLoader pl = ProxyLoader.getInstance(application);
        AutomatedReportingService arsport = pl.GetARS(application, request, response);

        //instantiate things and populate the variable, users and additionalReaders in the ReportDefinition
        AddOrUpdateScheduledReportRequestMsg req = new AddOrUpdateScheduledReportRequestMsg();
        req.setClassification(c);
        ReportDefinition rd = new ReportDefinition();
        boolean ok1 = true;
        boolean isHTML = false;
        String exporttype = request.getParameter("exporttype");
        TimeRangeDiff range = null;

        //populate the additionalReaders of the request Definition
        if (!Utility.stringIsNullOrEmpty(request.getParameter("additionalusers"))) {
            String st = request.getParameter("additionalusers");
            st = st.trim();
            String[] users = st.split(";");
            if (users != null) {
                for (int i = 0; i < users.length; i++) {
                    if (!Utility.stringIsNullOrEmpty(users[i])) {
                        rd.getAdditionalReaders().add(users[i].trim());
                    }
                }
            }
        }

        //update the exporttype
        if (exporttype != null) {
            if (exporttype.equalsIgnoreCase("HTML")) {
                isHTML = true;
            }
        } else {
            ok1 = false;
            out.write("Error: Export type was not selected<br>");
        }

        //set the range
        try {
            range = new TimeRangeDiff();
            range.setEnd(Helper.StringToDuration(request.getParameter("untiltime")));
            range.setStart(Helper.StringToDuration(request.getParameter("fromtime")));
        } catch (Exception ex) {
            ok1 = false;
            out.write("Error building time range " + ex.getMessage());
        }
        if (range == null || range.getEnd() == null || range.getStart() == null) {
            ok1 = false;
            out.write("Your time ranges were invalid");
        }

        if (ok1 && isHTML) {
            //HTML report
            rd.setExportDataRequestMsg(new org.miloss.fgsms.services.interfaces.automatedreportingservice.ExportDataRequestMsg());
            rd.getExportDataRequestMsg().setClassification(c);
            try {
                ArrayOfReportTypeContainer arc = new ArrayOfReportTypeContainer();

                Enumeration it2 = request.getParameterNames();
                List<ReportTypeContainer> list = arc.getReportTypeContainer();
                while (it2.hasMoreElements()) {
                    String s = (String) it2.nextElement();
                    

                    if (s.startsWith("REPORTTYPE_"))
                    try {
                        ReportTypeContainer con = new ReportTypeContainer();
                        con.setType(s.replace("REPORTTYPE_", ""));
                        list.add(con);
                    } catch (Exception x) {
                    }
                    try {
                        if (s.startsWith("URL")) {
                            s = s.replaceFirst("URL", "");
                            s = StringEscapeUtils.unescapeHtml4(s);
                            rd.getExportDataRequestMsg().getURLs().add(s.trim());
                            //        out.write("urladded: "+s); 
                        }
                    } catch (Exception x) {
                    }
                }
                rd.getExportDataRequestMsg().setReportTypes(arc);
                rd.getExportDataRequestMsg().setRange(range);
            } catch (Exception ex) {
                ok1 = false;
                out.write("Error building Export data message: " + ex.getMessage());
            }

        }
        if (ok1 && !isHTML) {
            //CSV export
            rd.setExportCSVDataRequestMsg(new org.miloss.fgsms.services.interfaces.automatedreportingservice.ExportCSVDataRequestMsg());
            rd.getExportCSVDataRequestMsg().setClassification(c);
            rd.getExportCSVDataRequestMsg().setRange(range);
            rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.fromValue(request.getParameter("recordstype")));
            if (!rd.getExportCSVDataRequestMsg().getExportType().equals(ExportRecordsEnum.AUDIT_LOGS)) {
                //get all urls for this report
                Enumeration it2 = request.getParameterNames();
                while (it2.hasMoreElements()) {
                    String s = (String) it2.nextElement();
                    try {
                        if (s.startsWith("URL")) {
                            s = s.replaceFirst("URL", "");
                            rd.getExportCSVDataRequestMsg().getURLs().add(s.trim());
                        }
                    } catch (Exception x) {
                    }
                }
            }
        }
        if (!Utility.stringIsNullOrEmpty(request.getParameter("enabled")) && request.getParameter("enabled").equalsIgnoreCase("on")) {
            rd.setEnabled(true);
        } else {
            rd.setEnabled(false);
        }
        if (!Utility.stringIsNullOrEmpty(request.getParameter("friendlyname"))) {
            rd.setFriendlyName(Utility.truncate(request.getParameter("friendlyname").trim(), 128));
        }
        // do not set the report id here, the report service will generate the id for you
        //        rd.setJobId(UUID.randomUUID().toString());
        DatatypeFactory df = DatatypeFactory.newInstance();
        rd.setSchedule(new ScheduleDefinition());
        XMLGregorianCalendar startat = null;      //time!
        XMLGregorianCalendar starton = null;      //date!
        try {
            startat = df.newXMLGregorianCalendarTime(Integer.parseInt(request.getParameter("startathour")),
                    Integer.parseInt(request.getParameter("startatminute")),
                    0,
                    0);
        } catch (Exception ex) {
            out.write("Error: Unparsable start at time " + ex.getMessage() + "<br>");
            LogHelper.getLog().log(Level.WARN, "error caught", ex);
            ok1 = false;
        }
        try {
            String s = request.getParameter("fromdatepicker");
            String[] mmddyyyy = s.split("/");
            starton = df.newXMLGregorianCalendarDate(
                    Integer.parseInt(mmddyyyy[2]),
                    Integer.parseInt(mmddyyyy[0]),
                    Integer.parseInt(mmddyyyy[1]),
                    0);
        } catch (Exception ex) {
            out.write("Error: Unparsable start on date " + ex.getMessage() + "<br>");
            ok1 = false;
           LogHelper.getLog().log(Level.WARN, "error caught", ex);
        }
        if (!Utility.stringIsNullOrEmpty(request.getParameter("ScheduleType"))) {
            String s = request.getParameter("ScheduleType");
            if (s.equalsIgnoreCase("daily")) {
                DailySchedule ds = new DailySchedule();
                ds.setStartingAt(startat.toGregorianCalendar());
                ds.setReoccurs(BigInteger.ONE);
                try {
                    ds.setReoccurs(BigInteger.valueOf(Integer.parseInt(request.getParameter("dailyreoccur"))));
                } catch (Exception e) {
                }
                rd.getSchedule().getTriggers().add(ds);
            } else if (s.equalsIgnoreCase("weekly")) {
                WeeklySchedule ds = new WeeklySchedule();
                ds.setStartingAt(startat.toGregorianCalendar());
                ds.setReoccurs(BigInteger.ONE);
                try {
                    ds.setReoccurs(BigInteger.valueOf(Integer.parseInt(request.getParameter("weeklyreoccur"))));
                } catch (Exception e) {
                }
                String[] days = request.getParameterValues("dayoftheweekselection");
                //  if (days == null || days.length == 1) {
                if (days == null) {
                    ok1 = false;
                    out.write("Error: No days of the week were selected<br>");
                } else {
                    for (int i = 0; i < days.length; i++) {
                        try {
                            ds.getDayOfTheWeekIs().add(Daynames.fromValue(days[i]));
                        } catch (Exception ex) {
                        }
                    }
                }
                rd.getSchedule().getTriggers().add(ds);
            } else if (s.equalsIgnoreCase("monthly")) {

                MonthlySchedule ds = new MonthlySchedule();
                ds.setStartingAt(startat.toGregorianCalendar());
                String[] days = request.getParameterValues("monthselection");
                if (days == null) {
                    ok1 = false;
                    out.write("Error: No months were selected<br>");
                } else {
                    for (int i = 0; i < days.length; i++) {
                        try {
                            ds.getMonthNameIs().add(Monthnames.fromValue(days[i]));
                        } catch (Exception ex) {
                        }
                    }
                }
                days = request.getParameterValues("dayofmonthselection");
                if (days == null) {
                    ok1 = false;
                    out.write("Error: No days of the month were selected<br>");
                } else {
                    for (int i = 0; i < days.length; i++) {
                        try {
                            ds.getDayOfTheMonthIs().add(Integer.valueOf(days[i]));
                        } catch (Exception ex) {
                        }
                    }
                }
                rd.getSchedule().getTriggers().add(ds);
            } else if (s.equalsIgnoreCase("once")) {
                OneTimeSchedule ds = new OneTimeSchedule();
                ds.setStartingAt(startat.toGregorianCalendar());
                rd.getSchedule().getTriggers().add(ds);
            } else if (s.equalsIgnoreCase("immediate")) {
                ImmediateSchedule ds = new ImmediateSchedule();
                ds.setStartingAt(startat.toGregorianCalendar());
                rd.getSchedule().getTriggers().add(ds);
            } else {
                out.write("Error: An unrecognized schedule type was selected<br>");
                ok1 = false;
            }
        } else {
            out.write("Error: A schedule type was not selected<br>");
            ok1 = false;
        }
        //req.getJobs().add(rd);

        //fromdatepicker
        //startathour
        //startatminute
        //ScheduleType   =daily, weekly or monthly
        //dailyreoccur
        //weeklyreoccur
        //dayoftheweekselection
        //dayofmonthselection
        //monthselection
        req.getJobs().add(rd);
        req.setClassification(c);

        if (ok1) {
            try {
                arsport.addOrUpdateScheduledReport(req);
                out.write("<h2>Job Saved Successfully</h2>");
            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getMessage());
                LogHelper.getLog().log(Level.WARN, "error caught", ex);
            }
        } else {
            out.write("There was a problem with your submission, please try again");
        }

        out.write("Click<a href=\"javascript:loadpage('reporting/scheduledReportsMain.jsp','mainpane', 'data.jsp');\" > here</a> to see the complete listings. ");

    }

%>