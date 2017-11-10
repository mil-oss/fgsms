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
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.services.rs.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.reportingservice.ArrayOfReportTypeContainer;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportCSVDataRequestMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataRequestMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToCSVResponseMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportDataToHTMLResponseMsg;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportRecordsEnum;
import org.miloss.fgsms.services.interfaces.reportingservice.ObjectFactory;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportTypeContainer;
import org.miloss.fgsms.test.WebServiceBaseTests;
import static org.junit.Assert.*;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.TimeRange;

/**
 *
 * @author AO
 */
public class ReportingTest extends WebServiceBaseTests {

    public ReportingTest() throws Exception {
        super();
        url = "http://localhost/jUnitTestRS";
        Init();
        String temp = System.getenv("TMP");
        if (temp == null) {
            temp = System.getenv("TEMP");
        }
        if (temp == null) {
            temp = System.getenv("TMPDIR");   //for macos
        }
        if (temp == null) { //ubuntu
            File t = new File("/tmp/");
            if (t.exists()) {
                temp = "/tmp/";
            } else {
                t = new File("/usr/tmp/");
                if (t.exists()) {
                    temp = "/usr/tmp/";
                }
            }
        }
        System.setProperty("jboss.server.temp.dir", "./target/");
    }

    static ObjectFactory f = new ObjectFactory();

    /**
     * Test of exportDataToHTML method, of class Reporting.
     */
    @org.junit.Test
    public void testExportDataToHTMLNullMsg() throws Exception {
        System.out.println("testExportDataToHTMLNullMsg");
        ExportDataRequestMsg request = null;
        Reporting instance = new Reporting(adminctx);
        try {
            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLNullclassl() throws Exception {
        System.out.println("testExportDataToHTMLNullclassl");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        Reporting instance = new Reporting(adminctx);
        try {
            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLNoRangeOrReportTypes() throws Exception {
        System.out.println("testExportDataToHTMLNoRangeOrReportTypes");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {

            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLPartialRangeNoReportTypes() throws Exception {
        System.out.println("testExportDataToHTMLPartialRangeNoReportTypes");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));

            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLPartialRangeNoReportTypes2() throws Exception {
        System.out.println("testExportDataToHTMLPartialRangeNoReportTypes2");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            //  r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));

            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLAllServicesNoReports() throws Exception {
        System.out.println("testExportDataToHTMLAllServicesNoReports");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setAllServices(true);
            ArrayOfReportTypeContainer rrs = new ArrayOfReportTypeContainer();
            request.setReportTypes(rrs);
            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLAllServicesAllReportsNoUrls() throws Exception {
        System.out.println("testExportDataToHTMLAllServicesAllReportsNoUrls");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setAllServices(true);
            ArrayOfReportTypeContainer rrs = new ArrayOfReportTypeContainer();

            rrs.getReportTypeContainer().addAll(getAllReportTypes());

            request.setReportTypes(rrs);
            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLAllServicesAllReportsunknownurl() throws Exception {
        System.out.println("testExportDataToHTMLAllServicesAllReportsunknownurl");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            ArrayOfReportTypeContainer rrs = new ArrayOfReportTypeContainer();
            rrs.getReportTypeContainer().addAll(getAllReportTypes());
            request.setReportTypes(rrs);
            request.getURLs().add(url);
            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLOneServiceAllReports() throws Exception {
        System.out.println("testExportDataToHTMLOneServiceAllReports");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);

            ArrayOfReportTypeContainer rrs = new ArrayOfReportTypeContainer();
            rrs.getReportTypeContainer().addAll(getAllReportTypes());
            request.setReportTypes(rrs);
            request.setAllServices(false);
            request.getURLs().add(url);
            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getZipFile());
            writeZipToTarget("testExportDataToHTMLOneServiceAllReports", result.getZipFile());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testExportDataToHTMLAllServicesAllReportsAllurl() throws Exception {
        System.out.println("testExportDataToHTMLAllServicesAllReportsAllurl");
        ExportDataRequestMsg request = new ExportDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);

            ArrayOfReportTypeContainer rrs = new ArrayOfReportTypeContainer();
            rrs.getReportTypeContainer().addAll(getAllReportTypes());
            request.setReportTypes(rrs);
            request.setAllServices(true);
            ExportDataToHTMLResponseMsg result = instance.exportDataToHTML(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getZipFile());
            writeZipToTarget("testExportDataToHTMLAllServicesAllReportsAllurl", result.getZipFile());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    /**
     * Test of exportDataToCSV method, of class Reporting.
     */
    @org.junit.Test
    public void testExportDataToCSVValidAsAdminValidAudit() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminValidAudit");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.AUDIT_LOGS);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getZipFile());
            writeZipToTarget("testExportDataToCSVValidAsAdminValidAudit", result.getZipFile());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminValidAvailability() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminValidAvailability");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.AVAILABILITY);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            request.setAllServices(true);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            // assertNotNull(result.getZipFile());
            writeZipToTarget("testExportDataToCSVValidAsAdminValidAvailability", result.getZipFile());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminNulltype() throws Exception {
        System.out.println("testExportDataToCSVValitestExportDataToCSVValidAsAdminNulltypedAsAdminValidTransactions");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            // request.setExportType(ExportRecordsEnum.TRANSACTIONS);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            fail("unexpected success");

        } catch (Exception ex) {
            //     ex.printStackTrace();
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminValidTransactions() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminValidTransactions");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.TRANSACTIONS);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            request.setAllServices(true);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            //assertNotNull(result.getZipFile());
            writeZipToTarget("testExportDataToCSVValidAsAdminValidTransactions", result.getZipFile());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminValidStats() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminValidStats");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.STATISTICS);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            request.setAllServices(true);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            //    assertNotNull(result.getZipFile());
            if (result.getZipFile() == null) {
                System.out.println("WARN zip is null");
            }
            writeZipToTarget("testExportDataToCSVValidAsAdminValidStats", result.getZipFile());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminValidAudiPartialRanget2() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminValidAudiPartialRanget2");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.AUDIT_LOGS);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminValidAudiPartialRanget() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminValidAudiPartialRanget");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.AUDIT_LOGS);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));

            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminNullRange() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminNullRange");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.AUDIT_LOGS);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminNoReportsSpecified() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminNoReportsSpecified");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToCSVNullClass() throws Exception {
        System.out.println("testExportDataToCSVNullClass");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        Reporting instance = new Reporting(adminctx);
        try {
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testExportDataToCSVNullMsg() throws Exception {
        System.out.println("testExportDataToCSVNullMsg");
        ExportCSVDataRequestMsg request = null;
        Reporting instance = new Reporting(adminctx);
        try {
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of getFilePathDelimitor method, of class Reporting.
     */
    @org.junit.Test
    public void testGetFilePathDelimitor() throws Exception {
        System.out.println("GetFilePathDelimitor");

        String result = Reporting.getFilePathDelimitor();
        if (!result.equals("/") && !result.equals("\\")) {
            fail("unexpected file delimintor");
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminAvailability() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminAvailability");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.AVAILABILITY);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            request.setAllServices(true);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            //    assertNotNull(result.getZipFile());
            if (result.getZipFile() == null) {
                System.out.println("WARN zip is null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminProcess() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminProcess");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.PROCESS);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            request.setAllServices(true);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            //      assertNotNull(result.getZipFile());
            if (result.getZipFile() == null) {
                System.out.println("WARN zip is null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminMachine() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminMachine");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.MACHINE);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            request.setAllServices(true);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            //       assertNotNull(result.getZipFile());
            if (result.getZipFile() == null) {
                System.out.println("WARN zip is null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testExportDataToCSVValidAsAdminWS() throws Exception {
        System.out.println("testExportDataToCSVValidAsAdminWS");
        ExportCSVDataRequestMsg request = new ExportCSVDataRequestMsg();
        request.setClassification(new SecurityWrapper());
        Reporting instance = new Reporting(adminctx);
        try {
            request.setExportType(ExportRecordsEnum.TRANSACTIONS);
            TimeRange r = new TimeRange();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setEnd((gcal));
            gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            gcal.add(Calendar.HOUR, -1);
            r.setStart((gcal));
            request.setRange(r);
            request.setAllServices(true);
            ExportDataToCSVResponseMsg result = instance.exportDataToCSV(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            if (result.getZipFile() == null) {
                System.out.println("WARN zip is null");
            }
            //        assertNotNull(result.getZipFile());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testGetFooter() throws Exception {
        System.out.println("testGetFooter");
        Reporting instance = new Reporting(adminctx);
        assertNotNull(instance.getFooter());
    }

    @org.junit.Test
    public void testGetHeader() throws Exception {
        System.out.println("testGetHeader");
        Reporting instance = new Reporting(adminctx);
        assertNotNull(instance.getHeader());
    }

    private void writeZipToTarget(String testName, byte[] zipFile) throws Exception {
        if (zipFile == null) {
            return;
        }
        FileOutputStream fos = new FileOutputStream("./target/" + testName + ".zip");
        fos.write(zipFile);
        fos.close();
    }

    private List<ReportTypeContainer> getAllReportTypes() throws Exception {
        List<ReportTypeContainer> array = new ArrayList<ReportTypeContainer>();
        Connection configurationDBConnection = Utility.getConfigurationDBConnection();
        PreparedStatement prepareStatement = configurationDBConnection.prepareStatement("select classname from plugins where appliesto='REPORTING'");
        ResultSet executeQuery = prepareStatement.executeQuery();
        while (executeQuery.next()) {
            ReportTypeContainer x= new ReportTypeContainer();
            x.setType(executeQuery.getString(1));
            array.add(x);
        }
        executeQuery.close();
        prepareStatement.close();
        configurationDBConnection.close();
        return array;
    }
}
