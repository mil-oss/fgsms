<%--  
    Document   : quartz
    Created on : Aug 13, 2012, 3:52:39 PM
    Author     : Administrator
--%>

<%@page import="org.quartz.Trigger"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.quartz.JobDetail"%>
<%@page import="org.quartz.Scheduler"%>
<%@page import="org.quartz.impl.StdSchedulerFactory"%>
<%@page import="org.quartz.SchedulerFactory"%>
<%@page import="org.miloss.fgsms.auxsrv.AuxConstants"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>FGSMS Aux Services Admin Page</title>
    </head> 
    <body>
        <h1>FGSMS Quartz Status</h1>
        <a href="quartz-xml.jsp">View this page as XML</a> <a href="quartz-status.xsd">Schema</a><br><br>
        <%
            try {
                Scheduler sc = new StdSchedulerFactory().getScheduler(AuxConstants.QUARTZ_SCHEDULER_NAME);
                out.write("Quartz Scheduler Name (const) " + AuxConstants.QUARTZ_SCHEDULER_NAME + "<br>");
                out.write("Quartz Scheduler Name (real) " + sc.getSchedulerName() + "<br>");
                out.write("Quartz Instance ID " + sc.getSchedulerInstanceId() + "<br>");

                out.write("Running Quartz Group Names:<Br>");
                String[] groups = sc.getJobGroupNames();
                for (int i = 0; i < groups.length; i++) {
                    out.write(groups[i] + "<br>");

                }

                out.write("Running Quartz jobs:<Br><table border=1>");

                String[] jobNames = sc.getJobNames(AuxConstants.QUARTZ_GROUP_NAME);
                for (int i = 0; i < jobNames.length; i++) {
                    out.write("<tr><td>");
                    out.write(jobNames[i] + "</td<td>");
                    JobDetail jd = sc.getJobDetail(jobNames[i], AuxConstants.QUARTZ_GROUP_NAME);
                    if (jd == null) {
                        out.write("Unable to get reference");
                    } else {
                        out.write("Full Name " + jd.getFullName() + "<br>Name " + jd.getName() + "<br>Group " + jd.getGroup() + "<br>Settings ");
                        if (jd.isDurable()) {
                            out.write(" DURABLE ");
                        } else {
                            out.write(" Not durable ");
                        }
                        if (jd.isStateful()) {
                            out.write(" Stateful ");
                        } else {
                            out.write(" Not stateful ");
                        }
                          if (jd.isVolatile()) {
                            out.write(" Volatile ");
                        } else {
                            out.write(" Not Volatile ");
                        }
                    }
                    out.write("</td></tr>");
                }
                out.write("</table>");
                SimpleDateFormat sdf = new SimpleDateFormat();
                out.write("Quartz version " + sc.getMetaData().getVersion() + "<br>");
                out.write("Jobs Executed: " + sc.getMetaData().getNumberOfJobsExecuted() + "<br>");
                out.write("Running Since: " + sdf.format(sc.getMetaData().getRunningSince()) + "<br>");

                out.write("Trigger Group Names:<Br>");
                groups = sc.getTriggerGroupNames();
                for (int i = 0; i < groups.length; i++) {
                    out.write(groups[i] + ": ");
                    String[] names = sc.getTriggerNames(groups[i]);
                    for (int k = 0; k < names.length; k++) {
                        out.write("========================================================<br>");
                        out.write(names[k] + " ");
                        org.quartz.Trigger t=sc.getTrigger(names[k],groups[i]);
                        out.write("<Br>May fire again: " + t.mayFireAgain());
                        out.write("<Br>End time: " + t.getEndTime());
                        
                        out.write("<Br>Fire instance id: " + t.getFireInstanceId());
                        out.write("<Br>Final fire time: " + t.getFinalFireTime());
                        out.write("<Br>Misfire instruction: " + t.getMisfireInstruction());
                        out.write("<Br>Last started at: " + t.getPreviousFireTime());
                        out.write("<Br>Next start at: " + t.getNextFireTime());
                        out.write("<Br>Priority: " + t.getPriority());
                        out.write("<br>");
                        
                    }
                    
                    out.write("<br>");
                }



            } catch (Exception ex) {
                response.setStatus(500);
                out.write("Quartz may not be running! " + ex.getMessage());
            }
        %>
    </body>
</html>
