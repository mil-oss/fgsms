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
    GetMachinePerformanceLogsByRangeResponseMsg res = null;

    GetOperationalStatusLogResponseMsg opres = null;
    GetStatusResponseMsg opres2 = null;
    Boolean currenstatus = null;
    Long currenttimestamp = null;

    if (!Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
        SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
        ProxyLoader pl = ProxyLoader.getInstance(application);

        try {

            DataAccessService dasport = pl.GetDAS(application, request, response);

            GetProcessesListByMachineRequestMsg req2 = new GetProcessesListByMachineRequestMsg();
            req2.setHostname(request.getParameter("server"));
            req2.setClassification(c);
            PCS pcsport = pl.GetPCS(application, request, response);
            GetProcessesListByMachineResponseMsg res2 = pcsport.getProcessesListByMachine(req2);


            GetMachinePerformanceLogsByRangeRequestMsg req = new GetMachinePerformanceLogsByRangeRequestMsg();

            req.setOffset(0);
            req.setRecordcount(100);
            req.setRange(new TimeRange());
            DatatypeFactory f = DatatypeFactory.newInstance();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());

            req.getRange().setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.DATE, -1);
            Gson gson = new Gson();
            
            req.setUri(request.getParameter("uri"));
            req.getRange().setStart((gcal));
            req.setClassification(c);
            res = dasport.getMachinePerformanceLogsByRange(req);
            if (res != null && !res.getMachinePerformanceData().isEmpty()) {

                out.write("{" +gson.toJson(request.getParameter("uri")) + ":[{");
                for (int i = 0; i < res.getMachinePerformanceData().size(); i++) {
                    double memused = 0;
                    double cpuused = 0;
                    long threads = 0;
                    //long files=0;

                    if (res.getMachinePerformanceData().get(i).getBytesusedMemory() != null) {
                        memused = (res.getMachinePerformanceData().get(i).getBytesusedMemory().doubleValue() / (double) res2.getMachineInformation().getMemoryinstalled()) * 100;
                    }
                    // org.codehaus.jettison.json.
                    cpuused = res.getMachinePerformanceData().get(i).getPercentusedCPU();
                    threads = res.getMachinePerformanceData().get(i).getNumberofActiveThreads();
                    out.write("\"" + res.getMachinePerformanceData().get(i).getTimestamp().getTimeInMillis() + "\": {");
                    out.write("\"mem\":" + memused + ",");
                    out.write("\"memraw\":" + res.getMachinePerformanceData().get(i).getBytesusedMemory() + ",");
                    out.write("\"cpu\":" + cpuused + ",");
                    out.write("\"threads\":" + threads + ",");
                    out.write("\"net\":[{");

                    for (int k = 0; k < res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().size(); k++) {
                        out.write(gson.toJson(res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getAdapterName()) + ":[{");
                        out.write("\"TX\":" + res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getKilobytespersecondNetworkTransmit() + ","
                                + "\"RX\":" + res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getKilobytespersecondNetworkReceive());
                        out.write("}]");
                        if (k != (res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().size() - 1)) {
                            out.write(",");
                        }
                    }
                    out.write("}],");

                    out.write("\"disk\": [{");
                    for (int k = 0; k < res.getMachinePerformanceData().get(i).getDriveInformation().size(); k++) {

                        double freespacepercent = 0;
                        long totalspace = 0;
                        long freespace = (res.getMachinePerformanceData().get(i).getDriveInformation().get(k).getFreespace());  //in MB
                        for (int a = 0; a < res2.getMachineInformation().getDriveInformation().size(); a++) {
                            if (res.getMachinePerformanceData().get(i).getDriveInformation().get(k).getPartition().equalsIgnoreCase(res2.getMachineInformation().getDriveInformation().get(a).getPartition())) {
                                totalspace = res2.getMachineInformation().getDriveInformation().get(a).getTotalspace();
                            }
                        }

                        out.write( gson.toJson(res.getMachinePerformanceData().get(i).getDriveInformation().get(k).getPartition()) + ":[{");
                        out.write("\"FreeSpace\":" + freespace + ",");
                        if (totalspace > 0) {
                            freespacepercent = ((double) ((double) (freespace) / (double) totalspace)) * 100;
                            out.write("\"FreeSpacePercent\":" + freespacepercent + ",");
                        }

                        if (res.getMachinePerformanceData().get(i).getDriveInformation().get(k).getKilobytespersecondDiskRead() != null) {
                            out.write("\"R\":" + res.getMachinePerformanceData().get(i).getDriveInformation().get(k).getKilobytespersecondDiskRead() + ",");
                        }
                        if (res.getMachinePerformanceData().get(i).getDriveInformation().get(k).getKilobytespersecondDiskWrite() != null) {
                            out.write("\"W\":" + res.getMachinePerformanceData().get(i).getDriveInformation().get(k).getKilobytespersecondDiskWrite());
                        }
                        out.write("}]");
                        if (k != (res.getMachinePerformanceData().get(i).getDriveInformation().size() - 1)) {
                            out.write(",");
                        }
                    }
                    out.write("}]");
                    out.write("}");
                    if (i != (res.getMachinePerformanceData().size() - 1)) {
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

