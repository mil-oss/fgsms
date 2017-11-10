/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.
 *
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.services.rs.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.*;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToHTMLResponseMsg;
import org.miloss.fgsms.sla.SLACommon;

/**
 * Automated Report Generator worker. This class is responsible for:<ol>
 * <li>Determine if any report jobs need to fire off right now</li> <li>Generate
 * all reports</li> <li>Process alerting function</li> </ol> This is a Quartz
 * job wrapper
 *
 * @author AO
 */
public class FgsmsReportGenerator {

    static final Logger log = Logger.getLogger("fgsms.AutoReportGenerator");
    private static final String subject = "An fgsms Scheduled Report is available";
    static DatatypeFactory f = null;

    private static void sendMailAlert(String htmlencodedText, String subject, InternetAddress[] subscribers, boolean pooled) {
        Properties props = null;
        if (pooled) {
            props = SLACommon.LoadSLAPropertiesPooled();
        } else {
            props = SLACommon.LoadSLAPropertiesNotPooled();
        }

        if (subscribers != null && subscribers.length > 0) {
            try {
                //get all subscribers

                Session mailSession = Session.getDefaultInstance(props);
                Message simpleMessage = new MimeMessage(mailSession);
                InternetAddress from;
                from = new InternetAddress(props.getProperty("defaultReplyAddress"));
                for (int i2 = 0; i2 < subscribers.length; i2++) {
                    try {
                        simpleMessage.setFrom(from);
                        simpleMessage.setRecipient(Message.RecipientType.TO, subscribers[i2]);
                        simpleMessage.setSubject(subject);
                        simpleMessage.setContent(htmlencodedText + "<br><br>"
                                + generateGuiLink(props.getProperty("fgsms.GUI.URL")), "text/html; charset=ISO-8859-1");
                        Transport.send(simpleMessage);
                    } catch (Exception ex) {
                        log.log(Level.ERROR, "Error sending ARS alert email! " + ex.getLocalizedMessage());
                    }
                }
            } catch (Exception ex) {
                log.log(Level.ERROR, "Error sending ARS alert email! " + ex.getLocalizedMessage());
            }
        }
    }

    private static String generateGuiLink(String property) {
        return "You can access all defined reports at the following link <a href=\"" + property + "/scheduledReports.jsp\">Click here</a> to manage your scheduled reports.";
    }

    private static InternetAddress[] getEmailAddresses(String user, boolean pooled) {
        Connection con = null;
        if (pooled) {
            con = Utility.getConfigurationDBConnection();
        } else {
            con = Utility.getConfigurationDB_NONPOOLED_Connection();
        }
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            List<InternetAddress> emails = new ArrayList<InternetAddress>();

            prepareStatement = con.prepareStatement("select * from users where username=?;");
            prepareStatement.setString(1, user);
            rs = prepareStatement.executeQuery();
            while (rs.next()) {
                InternetAddress a;
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email"))) {
                        a = new InternetAddress(rs.getString("email"));
                        emails.add(a);
                    }
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email1"))) {
                        a = new InternetAddress(rs.getString("email1"));
                        emails.add(a);
                    }
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email2"))) {
                        a = new InternetAddress(rs.getString("email2"));
                        emails.add(a);
                    }
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email3"))) {
                        a = new InternetAddress(rs.getString("email3"));
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, "Error created object to send email", ex);
                }
            }
            InternetAddress[] d = new InternetAddress[emails.size()];
            return emails.toArray(d);
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(prepareStatement);
            DBUtils.safeClose(con);
        }

        return null;
    }

    private static TimeRange convertDiffRangeToRange(TimeRangeDiff diff) throws DatatypeConfigurationException {
        if (f == null) {
            f = DatatypeFactory.newInstance();
        }
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        gcal.add(Calendar.MILLISECOND, (int) ((-1) * Utility.durationToTimeInMS(diff.getStart())));

        TimeRange r = new TimeRange();
        r.setStart((gcal));
        gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        gcal.add(Calendar.MILLISECOND, (int) ((-1) * Utility.durationToTimeInMS(diff.getEnd())));
        r.setEnd((gcal));
        return r;
    }

    public void FgsmsReportGenerator() throws DatatypeConfigurationException {
        f = DatatypeFactory.newInstance();
    }

    /**
     * this is used for testing purposes, it will generate a report based on job
     * id
     *
     * @param pooled
     * @param job
     * @return the generated report id
     */
    public String Fire(boolean pooled, String job) throws Exception {
        List<ReportDefinition> loadJobsFromDatabase = loadJobsFromDatabase(pooled, job);
        if (loadJobsFromDatabase == null || loadJobsFromDatabase.isEmpty()) {
            throw new Exception("report def not found");
        }
        return GenerateReport(loadJobsFromDatabase.get(0), pooled);
    }

    /**
     * Fire - this is the primary entry point. It will scan through the database
     * and search for jobs that need to be processed.
     *
     * @param pooled
     * @param waitforfinish if true, this function will block until all reports
     * have been generated, false uses a queuing mechanism and will exit after
     * all jobs have been enqueued
     */
    public void Fire(boolean pooled, boolean waitforfinish) {
        try {
            //get jobs
            List<ReportDefinition> items = loadJobsFromDatabase(pooled);
            log.log(Level.INFO, items.size() + " jobs defined. Processing....");
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).isEnabled() && CalendarCalculator.isTimeToRun(items.get(i), Calendar.getInstance())) {
                    log.log(Level.INFO, "Enqueing report generation for " + items.get(i).getJobId());
                    if (waitforfinish) {
                        String id = GenerateReport(items.get(i), pooled);
                        log.log(Level.INFO, "Report generated for " + items.get(i) + " with the id of " + id);
                        ProcessAlerts(items.get(i), id, pooled);
                    } else {
                        enqueue(items.get(i), pooled);
                    }
                } else {
                    log.log(Level.DEBUG, "Skipping report generation for " + items.get(i).getJobId() + " it's not time to fire");
                }
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
    }

    /**
     * generates the report, inserts it into the database and updates the table
     * to reflect the last updated timestamp
     *
     * @param geturns the report id
     */
    protected String GenerateReport(ReportDefinition get, boolean pooled) throws Exception {
        log.log(Level.INFO,"excuting ARS job " + get.getJobId() + " for " + get.getOwner());
        Reporting r = new Reporting();
        if (get.getExportCSVDataRequestMsg() != null) {
            org.miloss.fgsms.services.interfaces.reportingservice.ExportCSVDataRequestMsg x = new org.miloss.fgsms.services.interfaces.reportingservice.ExportCSVDataRequestMsg();
            x.setClassification(get.getExportCSVDataRequestMsg().getClassification());
            x.setAllServices(false);
            x.setExportType(get.getExportCSVDataRequestMsg().getExportType());
            x.getURLs().addAll(get.getExportCSVDataRequestMsg().getURLs());
            x.setRange(convertDiffRangeToRange(get.getExportCSVDataRequestMsg().getRange()));

            String path = "";
            String currentFolder = "";
            File targetfolder = null;
            UUID id = UUID.randomUUID();
            try {
                currentFolder = System.getProperty("jboss.server.temp.dir");
                if (currentFolder == null || currentFolder.equals("") || currentFolder.equalsIgnoreCase("null")) {
                    currentFolder = System.getProperty("java.io.tmpdir");
                }
//this worksd 
                path = currentFolder + Reporting.getFilePathDelimitor() + id.toString();
                targetfolder = new File(path);
                boolean mkdir = targetfolder.mkdir();
                if (!mkdir) {
                    log.log(Level.ERROR, "exportDataToHTML, unable to create temporary directory");
                    return null;
                }
                List<String> files = new ArrayList<String>();

                CsvExporter.generateCSVReport(x, path, files, get.getOwner(), null, r.getClassLevelFromDB());
                updateLastRanAt(get);
                return storeReport(Reporting.generateZipFileandDelete(files, path), get.getJobId(), pooled);
            } catch (Exception e) {
                log.log(Level.ERROR, "error caught generating report", e);

            }
            return null;

        } else if (get.getExportDataRequestMsg() != null) {
            try {
                TimeRange ConvertDiffRangeToRange = convertDiffRangeToRange(get.getExportDataRequestMsg().getRange());
                org.miloss.fgsms.services.interfaces.reportingservice.ExportDataRequestMsg request = new org.miloss.fgsms.services.interfaces.reportingservice.ExportDataRequestMsg();
                request.getURLs().addAll(get.getExportDataRequestMsg().getURLs());
                request.setRange(ConvertDiffRangeToRange);
                request.setReportTypes(get.getExportDataRequestMsg().getReportTypes());
                request.setClassification(get.getExportDataRequestMsg().getClassification());

                ExportDataToHTMLResponseMsg exportDataToHTML = r.generateHtmlReport(get.getOwner(), request);
                updateLastRanAt(get);
                byte[] bits = exportDataToHTML.getZipFile();
                return storeReport(bits, get.getJobId(), pooled);
            } catch (Exception ex) {
                log.log(Level.ERROR, "error caught generating report ", ex);
            }
        }
        return null;
    }

    protected void ProcessAlerts(ReportDefinition get, String id, boolean pooled) {
        String link = "";
        boolean guess = false;
        try {
            KeyNameValueEnc GetPropertiesFromDB = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "ServicesWar");
            if (GetPropertiesFromDB != null && GetPropertiesFromDB.getKeyNameValue() != null) {
                if (Utility.stringIsNullOrEmpty(GetPropertiesFromDB.getKeyNameValue().getPropertyValue())) {
                    //make an educated case
                    guess = true;

                } else {
                    link = "<a href=\"" + GetPropertiesFromDB.getKeyNameValue().getPropertyValue() + "/ReportFetch?reportid=" + URLEncoder.encode(id) + "\">" + Utility.encodeHTML(id) + "</a>";
                }
            }
        } catch (Exception ex) {
            guess = true;
            log.log(Level.WARN, "Unable to obtain the load balanced URL of the fgsmsServices war. Use the GUI to set the General Setting, key='defaults' name='ServicesWar' to clear this message. In the mean time, I'm just going to guess ", ex);
        }
        if (guess) {
            //somehow the service's endpoint wasn't defined anywhere in the general settings
            //table, this is a hard coded default value, hopefully it will never be needed
            //but it's there just in case
            link = "<a href=\"http://" + Utility.getHostName() + ":8888/fgsmsServices/ReportFetch?reportid=" + URLEncoder.encode(id) + "\">" + Utility.encodeHTML(id) + "</a>";
        }

        sendMailAlert(get.getOwner() + ",<br>A report has been generated and is now available for download. Additional information is as follows:<br>"
                + "<table><tr><td>Report Name</td><td>" + Utility.encodeHTML(get.getFriendlyName()) + "</td></tr>"
                + "<tr><td>Generated Report ID</td><td>" + link + "</td></tr></table><br>"
                + (guess ? "The download urls above may not work because the system administrator has not defined the public url of FGSMS in general settings." : "")
                + "You are the owner of this report and can delete the generated report or the reporting job itself.", subject, getEmailAddresses(get.getOwner(), pooled), pooled);

        for (int i = 0; i < get.getAdditionalReaders().size(); i++) {
            sendMailAlert(get.getAdditionalReaders().get(i) + ",<br>A report has been generated and is now available for download. Additional information is as follows:<br>"
                    + "<table><tr><td>Report Name</td><td>" + Utility.encodeHTML(get.getFriendlyName()) + "</td></tr>"
                    + "<tr><td>Generated Report ID</td><td>" + link + "</td></tr></table><br>"
                    + (guess ? "The download urls above may not work because the system administrator has not defined the public url of FGSMS in general settings." : "")
                    + "You have been granted read access to this generated report by the user " + get.getOwner(), subject, getEmailAddresses(get.getOwner(), pooled), pooled);
        }
        /* no longer supported
        if (!get.getNotifications().isEmpty()) {
            SLACommon.ProcessAlerts("Report is done", "Report is done", "", "", System.currentTimeMillis(), "", pooled, false, get.getNotifications(), "", null, AlertType.Status);
        }*/
    }

    private String storeReport(byte[] bits, String jobId, boolean pooled) throws Exception {
        String id = UUID.randomUUID().toString();
        Connection con = null;
        PreparedStatement cmd = null;
        if (pooled) {
            con = Utility.getPerformanceDBConnection();
        } else {
            con = Utility.getPerformanceDB_NONPOOLED_Connection();
        }
        try {
            cmd = con.prepareStatement("INSERT INTO arsreports(reportid, jobid, datetime,reportcontents)    VALUES (?, ?, ?,?);");
            cmd.setString(1, id);
            cmd.setString(2, jobId);
            cmd.setLong(3, System.currentTimeMillis());
            cmd.setBytes(4, bits);
            cmd.execute();
        } catch (Exception ex) {
            log.log(Level.ERROR, "error storing report", ex);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
            throw ex;
        } finally {
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        return id;
    }

    private List<ReportDefinition> loadJobsFromDatabase(boolean pooled) throws Exception {
        JAXBContext GetARSSerializationContext = Utility.getARSSerializationContext();
        Unmarshaller u = GetARSSerializationContext.createUnmarshaller();
        List<ReportDefinition> ret = new ArrayList<ReportDefinition>();
        Connection con;
        if (pooled) {
            con = Utility.getPerformanceDBConnection();
        } else {
            con = Utility.getPerformanceDB_NONPOOLED_Connection();
        }
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            prepareStatement = con.prepareStatement("select * from arsjobs where enabled=true;");
            rs = prepareStatement.executeQuery();
            ReportDefinition rd = null;
            while (rs.next()) {

                byte[] s = rs.getBytes("reportdef");
                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                JAXBElement<ReportDefinition> foo = (JAXBElement<ReportDefinition>) u.unmarshal(r, ReportDefinition.class);
                if (foo != null && foo.getValue() != null) {
                    rd = foo.getValue();
                    rd.setLastRanAt(ConvertLong(rs.getLong("lastranat")));
                    ret.add(rd);
                }
                bss.close();
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(prepareStatement);
            DBUtils.safeClose(con);
        }

        return ret;
    }

    private List<ReportDefinition> loadJobsFromDatabase(boolean pooled, String jobid) throws Exception {
        JAXBContext GetARSSerializationContext = Utility.getARSSerializationContext();
        Unmarshaller u = GetARSSerializationContext.createUnmarshaller();
        List<ReportDefinition> ret = new ArrayList<ReportDefinition>();
        Connection con;
        if (pooled) {
            con = Utility.getPerformanceDBConnection();
        } else {
            con = Utility.getPerformanceDB_NONPOOLED_Connection();
        }
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            prepareStatement = con.prepareStatement("select * from arsjobs where  jobid=?;");
            prepareStatement.setString(1, jobid);
            rs = prepareStatement.executeQuery();
            ReportDefinition rd = null;
            if (rs.next()) {

                byte[] s = rs.getBytes("reportdef");
                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                JAXBElement<ReportDefinition> foo = (JAXBElement<ReportDefinition>) u.unmarshal(r, ReportDefinition.class);
                if (foo != null && foo.getValue() != null) {
                    rd = foo.getValue();
                    rd.setLastRanAt(ConvertLong(rs.getLong("lastranat")));
                    ret.add(rd);
                }
                bss.close();
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(prepareStatement);
            DBUtils.safeClose(con);
        }
        return ret;
    }

    private Calendar ConvertLong(Long aLong) throws DatatypeConfigurationException {
        if (aLong == null) {
            return null;
        }
        if (aLong == 0) {
            return null;
        }
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(aLong);
        return (gcal);
    }

    private void enqueue(ReportDefinition rdef, boolean pooled) {

        ReportDefinitionExtension r = new ReportDefinitionExtension();
        r.def = rdef;
        r.pooled = pooled;

        //running outside of jboss
        RSProcessorSingleton.getInstance().Enqueue(r);

    }

    private void updateLastRanAt(ReportDefinition get) {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = null;
        try {
            cmd = con.prepareStatement("update arsjobs set lastranat=? where jobid=?");
            cmd.setLong(1, System.currentTimeMillis());
            cmd.setString(2, get.getJobId());
            cmd.executeUpdate();
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
    }
}
