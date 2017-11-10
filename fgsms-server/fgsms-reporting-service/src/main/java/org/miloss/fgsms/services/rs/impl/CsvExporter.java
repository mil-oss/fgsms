/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportCSVDataRequestMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToCSVResponseMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportRecordsEnum;
import org.miloss.fgsms.services.interfaces.reportingservice.ServiceUnavailableException;
import static org.miloss.fgsms.services.rs.impl.Reporting.delimiter;
import static org.miloss.fgsms.services.rs.impl.Reporting.getFilePathDelimitor;
import static org.miloss.fgsms.services.rs.impl.Reporting.isPolicyTypeOf;
import static org.miloss.fgsms.services.rs.impl.Reporting.log;
import static org.miloss.fgsms.services.rs.impl.Reporting.name;
import static org.miloss.fgsms.services.rs.impl.Reporting.toSafeFileName;

/**
 *
 * @author AO
 */
public class CsvExporter {

    static ExportDataToCSVResponseMsg exportDataToCSV(WebServiceContext ctx, String currentUser, SecurityWrapper classlevel, ExportCSVDataRequestMsg request) throws ServiceUnavailableException {
        ExportDataToCSVResponseMsg res = new ExportDataToCSVResponseMsg();
        res.setClassification(classlevel);

        String path = "";

        String currentFolder = "";
        File targetfolder = null;
        UUID id = UUID.randomUUID();
        try {
            currentFolder = System.getProperty("jboss.server.temp.dir");
            if (currentFolder == null || currentFolder.equals("") || currentFolder.equalsIgnoreCase("null")) {
                currentFolder = System.getProperty("java.io.tmpdir");
            }

            path = currentFolder + getFilePathDelimitor() + id.toString();
            targetfolder = new File(path);
            boolean mkdir = targetfolder.mkdir();
            if (!mkdir) {
                HttpServletRequest session = ((HttpServletRequest) ctx.getMessageContext().get(MessageContext.SERVLET_REQUEST));

                targetfolder = (File) session.getAttribute("javax.servlet.context.tmpdir");

            }

            if (!targetfolder.exists()) {
                log.log(Level.INFO, name + "exportDataToHTML, unable to create temporary directory " + currentFolder + targetfolder.getAbsolutePath());
                ServiceUnavailableException f = new ServiceUnavailableException("", null);
                f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.MISCONFIGURATION);
                throw f;
            }

        } catch (Exception e) {
            log.log(Level.INFO, name + "exportDataToHTML, load props" + e);
            ServiceUnavailableException f = new ServiceUnavailableException("", null);
            f.getFaultInfo().setCode(ServiceUnavailableFaultCodes.MISCONFIGURATION);
            throw f;
        }
        List<String> files = new ArrayList<String>();

        generateCSVReport(request, path, files, currentUser, ctx, classlevel);

        res.setZipFile(Reporting.generateZipFileandDelete(files, path));
        return res;
    }

    /**
     * if all services is true and not a global admin user returns a list of all
     * services that the current user has at least AUDIT access if all services
     * is true and is a global admin user, all service urls are returned
     *
     * else, only the list of services from which the user has AUDIT access is
     * returned
     *
     * @param allServices
     * @param requestedurls
     * @param currentUser
     * @param sec
     * @return
     */
    private static List<String> urlListAudit(boolean allServices, List<String> requestedurls, String currentUser, SecurityWrapper sec, WebServiceContext ctx) {
        List<String> urls = new ArrayList<String>();
        if (allServices) {
            Connection con = Utility.getConfigurationDBConnection();
            PreparedStatement cmd = null;
            ResultSet rs = null;
            try {
                if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "GenerateReports", (sec), ctx)) {
                    cmd = con.prepareStatement("select URI from servicepolicies;");
                } else if (UserIdentityUtil.hasGlobalAuditRole(currentUser, "GenerateReports", (sec), ctx)) {
                    cmd = con.prepareStatement("select URI from servicepolicies;");
                } else {
                    cmd = con.prepareStatement("select ObjectURI as URI from UserPermissions "
                            + "where username=? and "
                            + "AdministerObject=1 or AuditObject=1;");
                    cmd.setString(1, currentUser);
                }
                rs = cmd.executeQuery();
                while (rs.next()) {
                    urls.add(rs.getString("URI"));
                }

            } catch (Exception ex) {
                log.log(Level.INFO, name + "GenerateReports", ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(cmd);
                DBUtils.safeClose(con);
            }
        } else {

            if (requestedurls != null
                    && requestedurls.size() > 0) {
                for (int i = 0; i < requestedurls.size(); i++) {
                    urls.add(requestedurls.get(i));
                    UserIdentityUtil.assertReadAccess(urls.get(i), currentUser, "urlListAudit", sec, ctx);
                }
            }
        }
        return urls;
    }

    protected static void generateCSVReport(ExportCSVDataRequestMsg request, String filenameandpath, List<String> filelist, String currentuser, WebServiceContext ctx, SecurityWrapper security) {

        if (request == null) {
            throw new IllegalArgumentException("request");
        }
        if (request.getExportType() == null) {
            throw new IllegalArgumentException("report type");
        }
        validateCSVRequest(request);

        if (request.getExportType().value().equalsIgnoreCase(ExportRecordsEnum.AUDIT_LOGS.value())) {
            UserIdentityUtil.assertGlobalAuditRole(currentuser, "GenerateCSVReport", request.getClassification(), ctx);
            generateAuditCSVReport(request, filenameandpath, filelist, currentuser, security);
            return;
        }
        if (request.getExportType().value().equalsIgnoreCase(ExportRecordsEnum.ALL.value())
                && UserIdentityUtil.hasGlobalAuditRole(currentuser, "GenerateCSVReport", security, ctx)) {
            generateAuditCSVReport(request, filenameandpath, filelist, currentuser, security);
        }

        List<String> urlstoprocess = urlListAudit(request.isAllServices(), request.getURLs(), currentuser, request.getClassification(), ctx);

        for (int i = 0; i < urlstoprocess.size(); i++) {

            switch (request.getExportType()) {
                case ALL:
                    generateAvailabilityCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    generateStatisticsCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    generateTransactionsCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    generateMachineCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    generateProcessCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    break;
                case AVAILABILITY:
                    generateAvailabilityCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    break;
                case STATISTICS:
                    generateStatisticsCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    break;
                case TRANSACTIONS:
                    generateTransactionsCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    break;
                case MACHINE:
                    generateMachineCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    break;
                case PROCESS:
                    generateProcessCSVReport(request, filenameandpath, filelist, currentuser, urlstoprocess.get(i), security);
                    break;
                default:
                    throw new IllegalArgumentException();
            }

        }

    }

    private static void generateTransactionsCSVReport(ExportCSVDataRequestMsg request, String filenameandpath, List<String> filelist, String currentuser, String url, SecurityWrapper security) {
        if (!Reporting.isPolicyTypeOf(url, PolicyType.TRANSACTIONAL)) {
            return;
        }
        StringBuilder data = new StringBuilder();
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(
                    new FileOutputStream(filenameandpath + getFilePathDelimitor() + "TransactionExport-" + toSafeFileName(url) + ".csv"),
                    Constants.CHARSET
            );

            filelist.add(filenameandpath + getFilePathDelimitor() + "TransactionExport-" + Reporting.toSafeFileName(url) + ".csv");
            data = data.append("%CLASSIFICATION% Generated at %DATETIME_NOW% for %DATETIME_FROM% until %DATETIME_TO% ").append(System.getProperty("line.separator"));
            data = data.append(url).append(System.getProperty("line.separator"));
            data = data.append("URI").append(delimiter).append("Response Time (ms)").append(delimiter).append("Recorded at (DCS Host)").append(delimiter).append("Service Hostname").append(delimiter).append("Consumer").append(delimiter).append("Transaction Id").append(delimiter).append("Action").append(delimiter).append("Response size (bytes)").append(delimiter).append("Request size (bytes)").append(delimiter).append("Timestamp (ms since epoch)").append(delimiter).append("Success").append(delimiter).append("SLA Fault").append(delimiter).append("Agent type").append(delimiter).append("Original Request Url").append(delimiter).append("memo/notes from the agent").append(delimiter).append("Related Transaction ID").append(delimiter).append("Thread Id").append(delimiter).append("Request Headers").append(delimiter).append("Response Headers").append(delimiter).append("Request").append(delimiter).append("Response").append(System.getProperty("line.separator"));

            Calendar time = Calendar.getInstance();
            String t = data.toString();
            String[] ampm = new String[]{"AM", "PM"};
            t = t.replaceAll("%DATETIME_NOW%",
                    new Date().toString());

            time = request.getRange().getStart();
            t = t.replaceAll("%DATETIME_FROM%",
                    time.getTime().toString());
            time = request.getRange().getEnd();
            t = t.replaceAll("%DATETIME_TO%",
                    time.getTime().toString());

            if (request.getClassification() == null) {
                request.setClassification(security);
            }
            t = t.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " Caveat: " + request.getClassification().getCaveats()));

            out.write(t, 0, t.length());

            com = con.prepareStatement(
                    "select * from rawdata  "
                    + "where uri=? and utcdatetime > ? and utcdatetime < ? order by utcdatetime desc ;");
            com.setString(1, url);
            com.setLong(2, request.getRange().getStart().getTimeInMillis());
            com.setLong(3, request.getRange().getEnd().getTimeInMillis());

            rs = com.executeQuery();
            while (rs.next()) {
                data = new StringBuilder();
                data = data.append(rs.getString("uri")).append(delimiter).append(rs.getLong("responsetimems")).append(delimiter).append(rs.getString("monitorsource")).append(delimiter).append(rs.getString("hostingsource")).append(delimiter).append(rs.getString("consumeridentity")).append(delimiter).append(rs.getString("transactionid")).append(delimiter).append(rs.getString("soapaction")).append(delimiter).append(rs.getLong("responsesize")).append(delimiter).append(rs.getLong("requestsize")).append(delimiter).append(rs.getLong("utcdatetime")).append(delimiter);
                data = data.append(rs.getBoolean("success")).append(delimiter);
                String sla = rs.getString("slafault");
                if (Utility.stringIsNullOrEmpty(sla)) {
                    data = data.append("false").append(delimiter);
                } else {
                    data = data.append("true").append(delimiter);
                }
                data = data.append(rs.getString("agenttype")).append(delimiter).append(rs.getString("originalurl")).append(delimiter);

                byte[] msg = rs.getBytes("message");
                if (msg != null && msg.length > 0) {
                    data = data.append(new String(msg, Constants.CHARSET)).append(delimiter);
                } else {
                    data = data.append(delimiter);
                }
                data = data.append(rs.getString("relatedtransactionid")).append(delimiter).append(rs.getString("threadid")).append(delimiter);

                msg = rs.getBytes("requestheaders");
                if (msg != null && msg.length > 0) {
                    data = data.append(Utility.DE(new String(msg, Constants.CHARSET)).replace('|', ' ')).append(delimiter);
                } else {
                    data = data.append(delimiter);
                }
                msg = rs.getBytes("responseheaders");
                if (msg != null && msg.length > 0) {
                    data = data.append(Utility.DE(new String(msg, Constants.CHARSET)).replace('|', ' ')).append(delimiter);
                } else {
                    data = data.append(delimiter);
                }
                msg = rs.getBytes("requestxml");
                if (msg != null && msg.length > 0) {
                    data = data.append(Utility.DE(new String(msg, Constants.CHARSET))).append(delimiter);
                } else {
                    data = data.append(delimiter);
                }

                msg = rs.getBytes("responsexml");
                if (msg != null && msg.length > 0) {
                    data = data.append(Utility.DE(new String(msg, Constants.CHARSET))).append(delimiter);
                } else {
                    data = data.append(delimiter);
                }
                data = data.append(System.getProperty("line.separator"));
                t = data.toString();
                out.write(t, 0, t.length());

            }

        } catch (Exception ex) {
            log.log(Level.ERROR, "", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }

    }

    private static void generateStatisticsCSVReport(ExportCSVDataRequestMsg request, String filenameandpath, List<String> filelist, String currentuser, String url, SecurityWrapper security) {
        if (!Reporting.isPolicyTypeOf(url, PolicyType.STATISTICAL)) {
            return;
        }
        StringBuilder data = new StringBuilder();
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        OutputStreamWriter out = null;
        ResultSet rs = null;
        try {

            filelist.add(filenameandpath + getFilePathDelimitor() + "StatisticsExport-" + Reporting.toSafeFileName(url) + ".csv");

            out = new OutputStreamWriter(
                    new FileOutputStream(filenameandpath + getFilePathDelimitor() + "StatisticsExport-" + toSafeFileName(url) + ".csv"),
                    Constants.CHARSET
            );

            data = data.append("%CLASSIFICATION% Generated at %DATETIME_NOW% for %DATETIME_FROM% until %DATETIME_TO% ").append(System.getProperty("line.separator"));
            data = data.append(url).append(System.getProperty("line.separator"));
            data = data.append("Host").append(delimiter).append("Timestamp (ms since epoch)").append(delimiter).append("Name").append(delimiter).append("Consumer Count").append(delimiter).append("Active Consumer Count").append(delimiter).append("Type").append(delimiter).append("Agent Type").append(delimiter).append("Messages Sent").append(delimiter).append("Messages Recieved").append(delimiter).append("Messages Dropped").append(delimiter).append("Bytes Sent").append(delimiter).append("Bytes Recieved").append(delimiter).append("Bytes Dropped").append(delimiter).append("Queue Depth").append(delimiter).append("Canonical Name").append(delimiter).append(System.getProperty("line.separator"));

            Calendar time = Calendar.getInstance();

            String[] ampm = new String[]{"AM", "PM"};
            String t = data.toString();
            t = t.replaceAll("%DATETIME_NOW%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " at "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getStart();
            t = t.replaceAll("%DATETIME_FROM%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getEnd();
            t = t.replaceAll("%DATETIME_TO%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);

            if (request.getClassification() == null) {
                request.setClassification(security);
            }
            t = t.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " Caveat: " + request.getClassification().getCaveats()));

            data = new StringBuilder(t);

            data = data.append(System.getProperty("line.separator"));
            t = data.toString();
            out.write(t, 0, t.length());

            com = con.prepareStatement(
                    "select * from brokerhistory "
                    + "where host=? and utcdatetime > ? and utcdatetime < ? order by utcdatetime desc ;");
            com.setString(1, url);
            com.setLong(2, request.getRange().getStart().getTimeInMillis());
            com.setLong(3, request.getRange().getEnd().getTimeInMillis());

            rs = com.executeQuery();
            while (rs.next()) {
                data = new StringBuilder();
                data.append(rs.getString("host"));
                data.append(delimiter);
                data.append(rs.getLong("utcdatetime"));
                data.append(delimiter);
                data.append(rs.getString("namecol"));
                data.append(delimiter);
                data.append(rs.getLong("consumercount"));
                data.append(delimiter);
                data.append(rs.getLong("activeconsumercount"));
                data.append(delimiter);
                data.append(rs.getString("typecol"));
                data.append(delimiter);
                data.append(rs.getString("agenttype"));
                data.append(delimiter);
                data.append(rs.getLong("messagecount"));
                data.append(delimiter);
                data.append(rs.getLong("recievedmessagecount"));
                data.append(delimiter);
                data.append(rs.getLong("messagedropcount"));
                data.append(delimiter);
                data.append(rs.getLong("bytesout"));
                data.append(delimiter);
                data.append(rs.getLong("bytesin"));
                data.append(delimiter);
                data.append(rs.getLong("bytesdropcount"));
                data.append(delimiter);
                data.append(rs.getLong("queuedepth"));
                data.append(delimiter);
                data.append(rs.getString("canonicalname"));
                data.append(delimiter);
                data.append(System.getProperty("line.separator"));
                t = data.toString();
                out.write(t, 0, t.length());

            }

        } catch (Exception ex) {
            log.log(Level.ERROR, "", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }

    }

    private static void generateAvailabilityCSVReport(ExportCSVDataRequestMsg request, String filenameandpath,
            List<String> filelist, String currentuser, String url, SecurityWrapper security) {
        StringBuilder data = new StringBuilder();
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        OutputStreamWriter out = null;
        ResultSet rs = null;
        try {
            filelist.add(filenameandpath + getFilePathDelimitor() + "AvailabilityExport-" + Reporting.toSafeFileName(url) + ".csv");
            out = new OutputStreamWriter(
                    new FileOutputStream(filenameandpath + getFilePathDelimitor() + "AvailabilityExport-" + toSafeFileName(url) + ".csv"),
                    Constants.CHARSET
            );

            /*
             * uri text, "utcdatetime" bigint, id text, message text, status
             * boolean
             */
            data = data.append("%CLASSIFICATION% Generated at %DATETIME_NOW% for %DATETIME_FROM% until %DATETIME_TO% ").append(System.getProperty("line.separator"));
            data = data.append(url).append(System.getProperty("line.separator"));
            data = data.append("URI").append(delimiter).append("Timestamp (ms since epoch)").append(delimiter).append("Record Id").append(delimiter).append("Message").append(delimiter).append("Operational").append(System.getProperty("line.separator"));

            Calendar time = Calendar.getInstance();
            String t = data.toString();
            String[] ampm = new String[]{"AM", "PM"};
            t = t.replaceAll("%DATETIME_NOW%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " at "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getStart();
            t = t.replaceAll("%DATETIME_FROM%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getEnd();
            t = t.replaceAll("%DATETIME_TO%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);

            if (request.getClassification() == null) {
                request.setClassification(security);
            }
            t = t.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " Caveat: " + request.getClassification().getCaveats()));

            t += System.getProperty("line.separator");
            out.write(t, 0, t.length());

            com = con.prepareStatement(
                    "select * from availability "
                    + "where uri=? and utcdatetime  > ? and utcdatetime < ? order by utcdatetime desc ;");
            com.setString(1, url);
            com.setLong(2, request.getRange().getStart().getTimeInMillis());
            com.setLong(3, request.getRange().getEnd().getTimeInMillis());

            /*
             * uri text, "utcdatetime" bigint, id text, message text, status
             * boolean
             */
            rs = com.executeQuery();
            while (rs.next()) {
                data = new StringBuilder();
                data.append(rs.getString("uri"));
                data.append(delimiter);
                data.append(rs.getLong("utcdatetime"));
                data.append(delimiter);
                data.append(rs.getString("id"));
                data.append(delimiter);
                data.append(Reporting.stripCommas(rs.getString("message")));
                data.append(delimiter);
                data.append(rs.getBoolean("status"));
                data.append(System.getProperty("line.separator"));
                t = data.toString();
                out.write(t, 0, t.length());

            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }

    }

    private static void generateAuditCSVReport(ExportCSVDataRequestMsg request,
            String filenameandpath, List<String> filelist, String currentuser,
            SecurityWrapper security) {
        StringBuilder data = new StringBuilder();
        Connection con = Utility.getConfigurationDBConnection();
        OutputStreamWriter out = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            // File f = new File(filenameandpath + getFilePathDelimitor() + "availability.csv");

            filelist.add(filenameandpath + getFilePathDelimitor() + "AuditExport.csv");
            out = new OutputStreamWriter(
                    new FileOutputStream(filenameandpath + getFilePathDelimitor() + "AuditExport.csv"),
                    Constants.CHARSET
            );

            /*
             * "utcdatetime" bigint, username text, classname text, method text,
             * memo bytea, classification text, ipaddress text
             */
            data = data.append("%CLASSIFICATION% Generated at %DATETIME_NOW% for %DATETIME_FROM% until %DATETIME_TO% ").append(System.getProperty("line.separator"));
            data = data.append("fgsms Audit Logs").append(System.getProperty("line.separator"));
            data = data.append("Timestamp (ms since epoch)").append(delimiter).append("Username").append(delimiter).append("Class").append(delimiter).append("Method").append(delimiter).append("Memo").append(delimiter).append("Classification").append(delimiter).append("IP Address").append(System.getProperty("line.separator"));

            Calendar time = Calendar.getInstance();
            String t = data.toString();
            String[] ampm = new String[]{"AM", "PM"};
            t = t.replaceAll("%DATETIME_NOW%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " at "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getStart();
            t = t.replaceAll("%DATETIME_FROM%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getEnd();
            t = t.replaceAll("%DATETIME_TO%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);

            if (request.getClassification() == null) {
                request.setClassification(security);
            }
            t = t.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " Caveat: " + request.getClassification().getCaveats()));

            t += System.getProperty("line.separator");
            out.write(t, 0, t.length());

            com = con.prepareStatement(
                    "select * from auditlog "
                    + "where utcdatetime  > ? and utcdatetime < ? order by utcdatetime desc ;");

            com.setLong(1, request.getRange().getStart().getTimeInMillis());
            com.setLong(2, request.getRange().getEnd().getTimeInMillis());

            /*
             * "utcdatetime" bigint, username text, classname text, method text,
             * memo bytea, classification text, ipaddress text
             */
            rs = com.executeQuery();
            while (rs.next()) {
                data = new StringBuilder();
                data = data.append(rs.getString("utcdatetime")).append(delimiter).
                        append(rs.getString("username")).
                        append(delimiter).append(rs.getString("classname")).
                        append(delimiter).
                        append(rs.getString("method")).
                        append(delimiter).
                        append(new String(rs.getBytes("memo"), Constants.CHARSET)).
                        append(delimiter).append(rs.getString("classification")).
                        append(delimiter).append(rs.getString("ipaddress"));
                data = data.append(System.getProperty("line.separator"));
                t = data.toString();
                out.write(t, 0, t.length());

            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }

    }

    private static void generateHardDriveCSVReport(ExportCSVDataRequestMsg request,
            String filenameandpath, List<String> filelist,
            String currentuser, String url, SecurityWrapper security) {
        if (!Reporting.isPolicyTypeOf(url, PolicyType.MACHINE)) {
            return;
        }
        StringBuilder data = new StringBuilder();
        Connection con = Utility.getConfigurationDBConnection();
        OutputStreamWriter out = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            // File f = new File(filenameandpath + getFilePathDelimitor() + "availability.csv");

            filelist.add(filenameandpath + getFilePathDelimitor() + "DiskExport-" + Reporting.toSafeFileName(url) + ".csv");
            out = new OutputStreamWriter(
                    new FileOutputStream(filenameandpath + getFilePathDelimitor() + "DiskExport-" + toSafeFileName(url) + ".csv"),
                    Constants.CHARSET
            );

            data = data.append("%CLASSIFICATION% Generated at %DATETIME_NOW% for %DATETIME_FROM% until %DATETIME_TO% ").append(System.getProperty("line.separator"));
            data = data.append(url).append(System.getProperty("line.separator"));
            data = data.append("URI").
                    append(delimiter).append("Timestamp (ms since epoch)").
                    append(delimiter).append("Record Id").
                    append(delimiter).append("Free space").
                    append(delimiter).append("Write KB/sec").
                    append(delimiter).append("Read KB/sec").
                    append(delimiter).append("Dev ID").
                    append(delimiter).append("Drive ID").
                    append(delimiter).append("Operational").
                    append(delimiter).append("Status Message").
                    append(System.getProperty("line.separator"));

            Calendar time = Calendar.getInstance();
            String t = data.toString();
            String[] ampm = new String[]{"AM", "PM"};
            t = t.replaceAll("%DATETIME_NOW%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " at "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getStart();
            t = t.replaceAll("%DATETIME_FROM%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getEnd();
            t = t.replaceAll("%DATETIME_TO%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);

            if (request.getClassification() == null) {
                request.setClassification(security);
            }
            t = t.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " Caveat: " + request.getClassification().getCaveats()));

            t += System.getProperty("line.separator");
            out.write(t, 0, t.length());

            com = con.prepareStatement(
                    "select * from rawdatadrives "
                    + "where uri=? and utcdatetime  > ? and utcdatetime < ? order by utcdatetime desc ;");
            com.setString(1, url);
            com.setLong(2, request.getRange().getStart().getTimeInMillis());
            com.setLong(3, request.getRange().getEnd().getTimeInMillis());

            /*
             * uri text, "utcdatetime" bigint, id text, message text, status
             * boolean
             */
            rs = com.executeQuery();
            while (rs.next()) {
                data = new StringBuilder();
                data = data.append(rs.getString("uri")).
                        append(delimiter).append(rs.getLong("utcdatetime")).
                        append(delimiter).append(rs.getString("id")).
                        append(delimiter).append(rs.getLong("freespace")).
                        append(delimiter).append(rs.getLong("writeKBs")).
                        append(delimiter).append(rs.getLong("readKBs")).
                        append(delimiter).append(rs.getString("deviceidentifier")).
                        append(delimiter).append(rs.getString("driveidentifier")).
                        append(delimiter).append(rs.getBoolean("status")).
                        append(delimiter).append(rs.getString("statusmsg"));

                data = data.append(System.getProperty("line.separator"));
                t = data.toString();
                out.write(t, 0, t.length());
            }

        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    private static void generateNetworkCSVReport(ExportCSVDataRequestMsg request,
            String filenameandpath, List<String> filelist,
            String currentuser, String url, SecurityWrapper security) {
        if (!Reporting.isPolicyTypeOf(url, PolicyType.MACHINE)) {
            return;
        }
        StringBuilder data = new StringBuilder();
        Connection con = Utility.getConfigurationDBConnection();
        OutputStreamWriter out = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            // File f = new File(filenameandpath + getFilePathDelimitor() + "availability.csv");

            filelist.add(filenameandpath + getFilePathDelimitor() + "NetworkExport-" + Reporting.toSafeFileName(url) + ".csv");
            out = new OutputStreamWriter(
                    new FileOutputStream(filenameandpath + getFilePathDelimitor() + "NetworkExport-" + toSafeFileName(url) + ".csv"),
                    Constants.CHARSET
            );

            data = data.append("%CLASSIFICATION% Generated at %DATETIME_NOW% for %DATETIME_FROM% until %DATETIME_TO% ").append(System.getProperty("line.separator"));
            data = data.append(url).append(System.getProperty("line.separator"));
            data = data.append("URI").
                    append(delimiter).append("Timestamp (ms since epoch)").
                    append(delimiter).append("Record Id").
                    append(delimiter).append("NIC id").
                    append(delimiter).append("RX KB/sec").
                    append(delimiter).append("TX KB/sec").
                    append(System.getProperty("line.separator"));

            Calendar time = GregorianCalendar.getInstance();
            String t = data.toString();
            String[] ampm = new String[]{"AM", "PM"};
            t = t.replaceAll("%DATETIME_NOW%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " at "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getStart();
            t = t.replaceAll("%DATETIME_FROM%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getEnd();
            t = t.replaceAll("%DATETIME_TO%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);

            if (request.getClassification() == null) {
                request.setClassification(security);
            }
            t = t.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " Caveat: " + request.getClassification().getCaveats()));

            t += System.getProperty("line.separator");
            out.write(t, 0, t.length());

            com = con.prepareStatement(
                    "select * from rawdatanic "
                    + "where uri=? and utcdatetime  > ? and utcdatetime < ? order by utcdatetime desc ;");
            com.setString(1, url);
            com.setLong(2, request.getRange().getStart().getTimeInMillis());
            com.setLong(3, request.getRange().getEnd().getTimeInMillis());

            /*
             * uri text, "utcdatetime" bigint, id text, message text, status
             * boolean
             */
            rs = com.executeQuery();
            while (rs.next()) {
                data = new StringBuilder();
                data = data.append(rs.getString("uri")).
                        append(delimiter).append(rs.getLong("utcdatetime")).
                        append(delimiter).append(rs.getString("id")).
                        append(delimiter).append(rs.getString("nicid")).
                        append(delimiter).append(rs.getLong("receiveKBs")).
                        append(delimiter).append(rs.getLong("sendKBs"));

                data = data.append(System.getProperty("line.separator"));
                t = data.toString();
                out.write(t, 0, t.length());
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, "", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    @SuppressWarnings("fallthrough")
    private static void validateCSVRequest(ExportCSVDataRequestMsg request) {
        if (request.getExportType() == null) {
            throw new IllegalArgumentException("specify an export type");
        }
        switch (request.getExportType()) {
            case AUDIT_LOGS:
                break;
            case AVAILABILITY:
                if (request.isAllServices() == null && request.getURLs().isEmpty()) {
                    throw new IllegalArgumentException("either all services needs to be set, or specify a list of urls from which to export data.");
                }
                break;
            case STATISTICS:
                if (request.isAllServices() == null && request.getURLs().isEmpty()) {
                    throw new IllegalArgumentException("either all services needs to be set, or specify a list of urls from which to export data.");
                }

                break;
            case TRANSACTIONS:
                if (request.isAllServices() == null && request.getURLs().isEmpty()) {
                    throw new IllegalArgumentException("either all services needs to be set, or specify a list of urls from which to export data.");
                }

            case MACHINE:
            case PROCESS:
                if (request.isAllServices() == null && request.getURLs().isEmpty()) {
                    throw new IllegalArgumentException("either all services needs to be set, or specify a list of urls from which to export data.");
                }
                break;
        }

    }

    private static void generateMachineCSVReport(ExportCSVDataRequestMsg request,
            String filenameandpath, List<String> filelist, String currentuser,
            String url, SecurityWrapper security) {
        if (!isPolicyTypeOf(url, PolicyType.MACHINE)) {
            return;
        }
        StringBuilder data = new StringBuilder();
        Connection con = Utility.getConfigurationDBConnection();
        OutputStreamWriter out = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            // File f = new File(filenameandpath + getFilePathDelimitor() + "availability.csv");

            filelist.add(filenameandpath + getFilePathDelimitor() + "MachineExport-" + toSafeFileName(url) + ".csv");
            out = new OutputStreamWriter(
                    new FileOutputStream(filenameandpath + getFilePathDelimitor() + "MachineExport-" + toSafeFileName(url) + ".csv"),
                    Constants.CHARSET
            );

            /*
             * uri text, "utcdatetime" bigint, id text, message text, status
             * boolean
             */
            data = data.append("%CLASSIFICATION% Generated at %DATETIME_NOW% for %DATETIME_FROM% until %DATETIME_TO% ").append(System.getProperty("line.separator"));
            data = data.append(url).append(System.getProperty("line.separator"));
            data = data.append("URI").
                    append(delimiter).append("Timestamp (ms since epoch)").
                    append(delimiter).append("Record Id").
                    append(delimiter).append("Percent CPU").
                    append(delimiter).append("Memory").
                    append(delimiter).append("Threads").
                    append(delimiter).append("Open Files").
                    append(delimiter).append("Started At").
                    append(System.getProperty("line.separator"));

            Calendar time = Calendar.getInstance();
            String t = data.toString();
            String[] ampm = new String[]{"AM", "PM"};
            t = t.replaceAll("%DATETIME_NOW%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " at "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getStart();
            t = t.replaceAll("%DATETIME_FROM%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getEnd();
            t = t.replaceAll("%DATETIME_TO%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);

            if (request.getClassification() == null) {
                request.setClassification(security);
            }
            t = t.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " Caveat: " + request.getClassification().getCaveats()));

            t += System.getProperty("line.separator");
            out.write(t, 0, t.length());

            com = con.prepareStatement(
                    "select * from rawdatamachineprocess "
                    + "where uri=? and utcdatetime  > ? and utcdatetime < ? order by utcdatetime desc ;");
            com.setString(1, url);
            com.setLong(2, request.getRange().getStart().getTimeInMillis());
            com.setLong(3, request.getRange().getEnd().getTimeInMillis());

            /*
             * uri text, "utcdatetime" bigint, id text, message text, status
             * boolean
             */
            rs = com.executeQuery();
            while (rs.next()) {
                data = new StringBuilder();
                data = data.append(rs.getString("uri")).
                        append(delimiter).append(rs.getLong("utcdatetime")).
                        append(delimiter).append(rs.getString("id")).
                        append(delimiter).append(rs.getInt("percentCPU"));
                Long l = rs.getLong("memoryused");
                if (l != null) {
                    data = data.append(delimiter).append(l.toString());
                }
                l = rs.getLong("threads");
                if (l != null) {
                    data = data.append(delimiter).append(l.toString());
                }
                l = rs.getLong("openfiles");
                if (l != null) {
                    data = data.append(delimiter).append(l.toString());
                }
                l = rs.getLong("startedat");
                if (l != null) {
                    data = data.append(delimiter).append(l.toString());
                }
                data = data.append(System.getProperty("line.separator"));
                t = data.toString();
                out.write(t, 0, t.length());

            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }

        generateHardDriveCSVReport(request, filenameandpath, filelist, currentuser, url, security);
        generateNetworkCSVReport(request, filenameandpath, filelist, currentuser, url, security);
    }

    private static void generateProcessCSVReport(ExportCSVDataRequestMsg request,
            String filenameandpath, List<String> filelist, String currentuser,
            String url, SecurityWrapper security) {
        if (!isPolicyTypeOf(url, PolicyType.PROCESS)) {
            return;
        }
        StringBuilder data = new StringBuilder();
        Connection con = Utility.getConfigurationDBConnection();
        OutputStreamWriter out = null;
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            // File f = new File(filenameandpath + getFilePathDelimitor() + "availability.csv");

            filelist.add(filenameandpath + getFilePathDelimitor() + "ProcessExport-" + toSafeFileName(url) + ".csv");
            out = new OutputStreamWriter(
                    new FileOutputStream(filenameandpath + getFilePathDelimitor() + "ProcessExport-" + toSafeFileName(url) + ".csv"),
                    Constants.CHARSET
            );

            //BufferedWriter out = new BufferedWriter(new FileWriter(filenameandpath + getFilePathDelimitor() + "ProcessExport-" + toSafeFileName(url) + ".csv"));
            data = data.append("%CLASSIFICATION% Generated at %DATETIME_NOW% for %DATETIME_FROM% until %DATETIME_TO% ").append(System.getProperty("line.separator"));
            data = data.append(url).append(System.getProperty("line.separator"));
            data = data.append("URI").
                    append(delimiter).append("Timestamp (ms since epoch)").
                    append(delimiter).append("Record Id").
                    append(delimiter).append("Percent CPU").
                    append(delimiter).append("Memory").
                    append(delimiter).append("Threads").
                    append(delimiter).append("Open Files").
                    append(delimiter).append("Started At").
                    append(System.getProperty("line.separator"));

            Calendar time = Calendar.getInstance();
            String t = data.toString();
            String[] ampm = new String[]{"AM", "PM"};
            t = t.replaceAll("%DATETIME_NOW%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " at "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getStart();
            t = t.replaceAll("%DATETIME_FROM%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);
            time = request.getRange().getEnd();
            t = t.replaceAll("%DATETIME_TO%",
                    time.get(Calendar.YEAR) + "/"
                    + (time.get(Calendar.MONTH) + 1) + "/"
                    + time.get(Calendar.DATE) + " "
                    + time.get(Calendar.HOUR) + ":"
                    + time.get(Calendar.MINUTE) + ":"
                    + time.get(Calendar.SECOND) + " "
                    + ampm[time.get(Calendar.AM_PM)]);

            if (request.getClassification() == null) {
                request.setClassification(security);
            }
            t = t.replaceAll("%CLASSIFICATION%", (Utility.ICMClassificationToString(request.getClassification().getClassification()) + " Caveat: " + request.getClassification().getCaveats()));

            t += System.getProperty("line.separator");
            out.write(t, 0, t.length());

            com = con.prepareStatement(
                    "select * from rawdatamachineprocess "
                    + "where uri=? and utcdatetime  > ? and utcdatetime < ? order by utcdatetime desc ;");
            com.setString(1, url);
            com.setLong(2, request.getRange().getStart().getTimeInMillis());
            com.setLong(3, request.getRange().getEnd().getTimeInMillis());

            /*
             * uri text, "utcdatetime" bigint, id text, message text, status
             * boolean
             */
            rs = com.executeQuery();
            while (rs.next()) {
                data = new StringBuilder();
                data = data.append(rs.getString("uri")).
                        append(delimiter).append(rs.getLong("utcdatetime")).
                        append(delimiter).append(rs.getString("id")).
                        append(delimiter).append(rs.getInt("percentCPU"));
                Long l = rs.getLong("memoryused");
                if (l != null) {
                    data = data.append(delimiter).append(l.toString());
                }
                l = rs.getLong("threads");
                if (l != null) {
                    data = data.append(delimiter).append(l.toString());
                }
                l = rs.getLong("openfiles");
                if (l != null) {
                    data = data.append(delimiter).append(l.toString());
                }
                l = rs.getLong("startedat");
                if (l != null) {
                    data = data.append(delimiter).append(l.toString());
                }
                data = data.append(System.getProperty("line.separator"));
                t = data.toString();
                out.write(t, 0, t.length());

            }

        } catch (Exception ex) {
            log.log(Level.ERROR, "", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }
    }

}
