<%-- 
    Document   : machineDataJson
    Created on : Sep 24, 2012, 4:05:48 PM
    Author     : Administrator
--%>

<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.TimeRange"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.io.StringWriter"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineResponseMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.PCS"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineRequestMsg"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.services.interfaces.status.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>

<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.common.Utility"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    /*
     url:
     { 
     timestamp:
     {        //zero or more
     cpu, 
     mem, 
     *      thread
     *      files
     *        disk
     {
     c:                100 %
     , d:                5 %
     }
     *      nic: { nic1: { tx: tx, rx: rx } }
     }
     }
     */
    GetProcessPerformanceLogsByRangeResponseMsg res = null;

    GetOperationalStatusLogResponseMsg opres = null;
    GetStatusResponseMsg opres2 = null;
    Boolean currenstatus = null;
    Long currenttimestamp = null;

    if (!Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
        SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
        ProxyLoader pl = ProxyLoader.getInstance(application);

        try {

            DataAccessService dasport = pl.GetDAS(application, request, response);

            //  GetProcessesListByMachineRequestMsg req2 = new GetProcessesListByMachineRequestMsg();

            // req2.setHostname(request.getParameter("server"));
            //req2.setClassification(c);
            //PCS pcsport = pl.GetPCS(application, request, response);
            // GetProcessesListByMachineResponseMsg res2 = pcsport.getProcessesListByMachine(req2);


            GetProcessPerformanceLogsByRangeRequestMsg req = new GetProcessPerformanceLogsByRangeRequestMsg();
            long since = System.currentTimeMillis() - (6 * 60 * 60 * 1000);
            if (!Utility.stringIsNullOrEmpty(request.getParameter("since"))) {
                try {
                    long s = Long.parseLong(request.getParameter("since"));
                    if (s > 0) {
                        since = s;
                    }
                } catch (Exception x) {
                }
            }
            req.setOffset(0);
            req.setRecordcount(1000);
            req.setRange(new TimeRange());
            DatatypeFactory f = DatatypeFactory.newInstance();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());

            req.getRange().setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(since);
            //gcal.add(Calendar.DATE, -1);
            req.getRange().setStart((gcal));

            req.setUri(request.getParameter("uri"));
            req.setClassification(c);
            Gson gson  = new Gson();
            res = dasport.getProcessPerformanceLogsByRange(req);
            if (res != null && !res.getProcessData().isEmpty()) {

                out.write("{" + gson.toJson(request.getParameter("uri")) + ":[{");
                for (int i = 0; i < res.getProcessData().size(); i++) {
                    double memused = 0;
                    double cpuused = 0;
                    long threads = 0;
                    //long files=0;
                    if (res.getProcessData().get(i).getBytesusedMemory() != null && res.getInstalledMemory() != -1) {
                        memused = (res.getProcessData().get(i).getBytesusedMemory().doubleValue() / (double) res.getInstalledMemory()) * 100;
                    }

                    if (res.getProcessData().get(i).getPercentusedCPU() != null) {
                        cpuused = res.getProcessData().get(i).getPercentusedCPU();
                    }
                    if (res.getProcessData().get(i).getNumberofActiveThreads() != null) {
                        threads = res.getProcessData().get(i).getNumberofActiveThreads();
                    }
                    out.write("\"" + res.getProcessData().get(i).getTimestamp().getTimeInMillis() + "\": {");

                    out.write("\"mem\":" + memused + ",");
                    if (res.getProcessData().get(i).getBytesusedMemory() != null) {
                        out.write("\"memraw\":" + res.getProcessData().get(i).getBytesusedMemory() + ",");
                    }
                    out.write("\"cpu\":" + cpuused + ",");
                    out.write("\"threads\":" + threads + ",");
                    out.write("\"openfiles\":" + res.getProcessData().get(i).getOpenFileHandles());

                    out.write("}");
                    if (i != (res.getProcessData().size() - 1)) {
                        out.write(",");
                    }
                }

                out.write("}]}");

            } else {
                //out.write("No records returned");
            }
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught generating process charts over time", ex);
        }

    }

%>

