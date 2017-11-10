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
package org.miloss.fgsms.services.ars.impl;

import java.io.File;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeFactory;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.*;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportRecordsEnum;
import org.miloss.fgsms.test.WebServiceBaseTests;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.miloss.fgsms.services.rs.impl.FgsmsReportGenerator;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author AO
 */
public class AutomatedReportingServiceImplTest extends WebServiceBaseTests {

    public AutomatedReportingServiceImplTest() throws Exception {
        super();
        url = "http://localhost:8180/jUnitTestCaseARS";
        Init();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
        System.setProperty("jboss.server.temp.dir", temp);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @After
    public void tearDown() {
        RemoveServicePolicy(url);
    }

    /*
     * Test cases nulls update a report that i don't own update a report with an
     * // * invalid combination add or update a report with both report types //
     * * defined, invalid
     *
     * delete a report that doesn't belong to me delete a job that doesn't
     * belong to me get a report that doesn't belong to me and i don't have
     * permissions get a report that doesn't belong to me but I DO have read
     *
     * permissions smoke test, create a policy, add data, create a scheduled *
     * report, wait for it to trigger, then fetch it, destroy the report and
     * thejob
     *
     */
    @Test(expected = org.miloss.fgsms.services.interfaces.automatedreportingservice.AccessDeniedException.class)
    public void DeleteAJobThatDoesntBelongtoMe() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("DeleteAJobThatDoesntBelongtoMe");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);
        GrantUserAuditAccess(maryusername, url);

        AutomatedReportingServiceImpl bob = new AutomatedReportingServiceImpl(userbobctx);
        AutomatedReportingServiceImpl mary = new AutomatedReportingServiceImpl(usermaryctx);
        String job = createjob(mary, maryusername);
        DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
        rq.setClassification(new SecurityWrapper());
        rq.setJobId(job);
        try {

            bob.deleteScheduledReport(rq);

        } catch (Exception ex) {
            throw ex;
        } finally {
            mary.deleteScheduledReport(rq);
        }
    }

    @Test(expected = AccessDeniedException.class)
    public void ReadAReportJobThatDoesntBelongtoMe() throws Exception {
        RemoveServicePolicy(url);

        System.out.println("ReadAReportJobThatDoesntBelongtoMe");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);

        AutomatedReportingServiceImpl bob = new AutomatedReportingServiceImpl(userbobctx);
        RemoveAllReportsAndJobs(bob);
        AutomatedReportingServiceImpl mary = new AutomatedReportingServiceImpl(usermaryctx);

        String job = createjob(bob, bobusername);
        System.out.println(job);
        org.miloss.fgsms.services.rs.impl.FgsmsReportGenerator gen = new FgsmsReportGenerator();
        System.out.println("Triggering report generation");
        String reportid = gen.Fire(true, job);
        System.out.println("report generated at " + reportid);
        //there should be a report now
        GetMyScheduledReportsRequestMsg r = new GetMyScheduledReportsRequestMsg();
        r.setClassification(new SecurityWrapper());
        GetMyScheduledReportsResponseMsg myScheduledReports = bob.getMyScheduledReports(r);
        //confirm report was generated
        assertNotNull(myScheduledReports);
        assertNotNull(myScheduledReports.getClassification());
        assertFalse(myScheduledReports.getCompletedJobs().isEmpty());
        assertNotNull(myScheduledReports.getCompletedJobs().get(0).getJob());
        assertFalse(myScheduledReports.getCompletedJobs().get(0).getReports().isEmpty());
        //get the report
        GetReportRequestMsg re = new GetReportRequestMsg();
        re.setClassification(new SecurityWrapper());
        re.setReportId(myScheduledReports.getCompletedJobs().get(0).getReports().get(0).getReportId());
        try {
            GetReportResponseMsg report = mary.getReport(re);
            DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
            rq.setClassification(new SecurityWrapper());
            rq.setJobId(job);
            bob.deleteScheduledReport(rq);
        } catch (Exception ex) {
            throw ex;
        } finally {
            DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
            rq.setClassification(new SecurityWrapper());
            rq.setJobId(job);
            DeleteScheduledReportResponseMsg deleteScheduledReport = bob.deleteScheduledReport(rq);
            assertNotNull(deleteScheduledReport);
            assertNotNull(deleteScheduledReport.getClassification());
        }

    }

    @Test
    public void ReadAReportJobThatDoesBelongtoMe() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("ReadAReportJobThatDoesBelongtoMe");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);
        AutomatedReportingServiceImpl bob = new AutomatedReportingServiceImpl(userbobctx);
        RemoveAllReportsAndJobs(bob);
        String job = createjob(bob, bobusername);
        System.out.println(job);
        org.miloss.fgsms.services.rs.impl.FgsmsReportGenerator gen = new FgsmsReportGenerator();
        System.out.println("Triggering report generation");
        gen.Fire(true, true);
        //there should be a report now
        GetMyScheduledReportsRequestMsg r = new GetMyScheduledReportsRequestMsg();
        r.setClassification(new SecurityWrapper());
        GetMyScheduledReportsResponseMsg myScheduledReports = bob.getMyScheduledReports(r);
        assertNotNull(myScheduledReports);
        assertNotNull(myScheduledReports.getClassification());
        assertFalse(myScheduledReports.getCompletedJobs().isEmpty());
        assertNotNull(myScheduledReports.getCompletedJobs().get(0).getJob());
        assertFalse(myScheduledReports.getCompletedJobs().get(0).getReports().isEmpty());
        GetReportRequestMsg re = new GetReportRequestMsg();
        re.setClassification(new SecurityWrapper());
        re.setReportId(myScheduledReports.getCompletedJobs().get(0).getReports().get(0).getReportId());
        GetReportResponseMsg report = bob.getReport(re);
        assertNotNull(report);
        assertNotNull(report.getClassification());
        assertNotNull(report.getReportId());
        assertNotNull(report.getZippedReport());
        assertTrue(report.getZippedReport().length > 0);

        DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
        rq.setClassification(new SecurityWrapper());
        rq.setJobId(job);
        DeleteScheduledReportResponseMsg deleteScheduledReport = bob.deleteScheduledReport(rq);
        assertNotNull(deleteScheduledReport);
        assertNotNull(deleteScheduledReport.getClassification());

    }

    @Test
    public void ReadAReportJobThatDoesNotBelongtoMeButIHaveAccess() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("ReadAReportJobThatDoesNotBelongtoMeButIHaveAccess");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);
        AutomatedReportingServiceImpl bob = new AutomatedReportingServiceImpl(userbobctx);
        RemoveAllReportsAndJobs(bob);
        AutomatedReportingServiceImpl mary = new AutomatedReportingServiceImpl(usermaryctx);

        ReportDefinition rdef = createjob3(bob, maryusername);
        String job = rdef.getJobId();
        System.out.println(job);
        org.miloss.fgsms.services.rs.impl.FgsmsReportGenerator gen = new FgsmsReportGenerator();
        System.out.println("Triggering report generation");
        gen.Fire(true, true);
        //there should be a report now
        GetMyScheduledReportsRequestMsg r = new GetMyScheduledReportsRequestMsg();
        r.setClassification(new SecurityWrapper());
        GetMyScheduledReportsResponseMsg myScheduledReports = bob.getMyScheduledReports(r);
        assertNotNull(myScheduledReports);
        assertNotNull(myScheduledReports.getClassification());
        assertFalse(myScheduledReports.getCompletedJobs().isEmpty());
        assertNotNull(myScheduledReports.getCompletedJobs().get(0).getJob());
        assertFalse(myScheduledReports.getCompletedJobs().get(0).getReports().isEmpty());
        GetReportRequestMsg re = new GetReportRequestMsg();
        re.setClassification(new SecurityWrapper());
        re.setReportId(myScheduledReports.getCompletedJobs().get(0).getReports().get(0).getReportId());
        GetReportResponseMsg report = mary.getReport(re);
        assertNotNull(report);
        assertNotNull(report.getClassification());
        assertNotNull(report.getReportId());
        assertNotNull(report.getZippedReport());
        assertTrue(report.getZippedReport().length > 0);

        DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
        rq.setClassification(new SecurityWrapper());
        rq.setJobId(job);
        DeleteScheduledReportResponseMsg deleteScheduledReport = bob.deleteScheduledReport(rq);
        assertNotNull(deleteScheduledReport);
        assertNotNull(deleteScheduledReport.getClassification());

    }

    @Test(expected = AccessDeniedException.class)
    public void UpdateAJobThatDoesntBelongtoMe() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("UpdateAJobThatDoesntBelongtoMe");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);
        GrantUserAuditAccess(maryusername, url);

        AutomatedReportingServiceImpl bob = new AutomatedReportingServiceImpl(userbobctx);
        AutomatedReportingServiceImpl mary = new AutomatedReportingServiceImpl(usermaryctx);
        ReportDefinition job = createjob2(mary, maryusername);
        AddOrUpdateScheduledReportRequestMsg rq = new AddOrUpdateScheduledReportRequestMsg();

        rq.setClassification(new SecurityWrapper());
        rq.getJobs().add(job);
        try {

            bob.addOrUpdateScheduledReport(rq);

        } catch (Exception ex) {
            throw ex;
        } finally {
            DeleteScheduledReportRequestMsg r = new DeleteScheduledReportRequestMsg();
            r.setJobId(job.getJobId());
            r.setClassification(new SecurityWrapper());
            mary.deleteScheduledReport(r);
        }
    }

    @Test
    public void UpdateAJobThatDoesBelongtoMe() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("UpdateAJobThatDoesBelongtoMe");
        CreatePolicy(url);
        //GrantUserAuditAccess(bobusername, url);
        GrantUserAuditAccess(maryusername, url);

        //AutomatedReportingServiceImpl bob = new AutomatedReportingServiceImpl(userbobctx);
        AutomatedReportingServiceImpl mary = new AutomatedReportingServiceImpl(usermaryctx);
        ReportDefinition job = createjob2(mary, maryusername);
        AddOrUpdateScheduledReportRequestMsg rq = new AddOrUpdateScheduledReportRequestMsg();
        boolean ok = true;
        rq.setClassification(new SecurityWrapper());
        rq.getJobs().add(job);
        try {
            job.setFriendlyName("stuff");
            AddOrUpdateScheduledReportResponseMsg addOrUpdateScheduledReport = mary.addOrUpdateScheduledReport(rq);
            assertNotNull(addOrUpdateScheduledReport);
            assertNotNull(addOrUpdateScheduledReport.getClassification());
            assertNotNull(addOrUpdateScheduledReport.getJobs().get(0));
            assertNotNull(addOrUpdateScheduledReport.getJobs().get(0).getFriendlyName());
            assertTrue(addOrUpdateScheduledReport.getJobs().size() == 1);
            assertEquals("msg", "stuff", addOrUpdateScheduledReport.getJobs().get(0).getFriendlyName());

        } catch (Exception ex) {
            ex.printStackTrace();
            ok = false;
        }
        DeleteScheduledReportRequestMsg r = new DeleteScheduledReportRequestMsg();
        r.setJobId(job.getJobId());
        r.setClassification(new SecurityWrapper());
        mary.deleteScheduledReport(r);
        if (!ok) {
            fail("unexpected failure");
        }
    }

    ReportDefinition createjob2(AutomatedReportingServiceImpl instance, String owner) throws Exception {
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().getURLs().add(url);
        rd.setOwner(owner);
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.TRANSACTIONS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());

        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);

        try {
            AddOrUpdateScheduledReportResponseMsg result = instance.addOrUpdateScheduledReport(request);
            return result.getJobs().get(0);

        } catch (Exception ex) {
            RemoveServicePolicy(url);
            ex.printStackTrace();
            fail("unexpected fault.");
        }
        return null;
    }

    String createjob(AutomatedReportingServiceImpl instance, String owner) throws Exception {
        ReportDefinition r = createjob2(instance, owner);
        if (r != null) {
            return r.getJobId();
        }
        return null;
    }

    /**
     * Test of getMyScheduledReports method, of class
     * AutomatedReportingServiceImpl.
     */
    @Test
    public void testGetMyScheduledReportsNullMsg() throws Exception {
        System.out.println("getMyScheduledReports");
        GetMyScheduledReportsRequestMsg request = null;
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            GetMyScheduledReportsResponseMsg result = instance.getMyScheduledReports(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetMyScheduledReportsNullClass() throws Exception {
        System.out.println("getMyScheduledReportsNullMsg");
        GetMyScheduledReportsRequestMsg request = new GetMyScheduledReportsRequestMsg();
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            GetMyScheduledReportsResponseMsg result = instance.getMyScheduledReports(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetMyScheduledReportsNullValues() throws Exception {
        System.out.println("testGetMyScheduledReportsNullValues");
        GetMyScheduledReportsRequestMsg request = new GetMyScheduledReportsRequestMsg();
        request.setClassification(new SecurityWrapper());
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            GetMyScheduledReportsResponseMsg result = instance.getMyScheduledReports(request);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @Test
    public void testGetMyScheduledReports() throws Exception {
        System.out.println("testGetMyScheduledReports");
        GetMyScheduledReportsRequestMsg request = new GetMyScheduledReportsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setOffset(0);
        request.setRecordlimit(100);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            GetMyScheduledReportsResponseMsg result = instance.getMyScheduledReports(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    /**
     * Test of addOrUpdateScheduledReport method, of class
     * AutomatedReportingServiceImpl.
     */
    @Test
    public void testAddOrUpdateScheduledReportNullMsg() throws Exception {
        System.out.println("testAddOrUpdateScheduledReportNullMsg");
        AddOrUpdateScheduledReportRequestMsg request = null;
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testAddOrUpdateScheduledReportNullClass() throws Exception {
        System.out.println("testAddOrUpdateScheduledReportNullClass");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testAddOrUpdateScheduledReportNullValues() throws Exception {
        System.out.println("testAddOrUpdateScheduledReportNullValues");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testAddOrUpdateScheduledReport1() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport1");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testAddOrUpdateScheduledReport2() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport2");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        /*
         * rd.getSchedule().setImmediate(true); rd.getSchedule().setTrigger(new
         * ReportingTrigger());
         * rd.getSchedule().getTrigger().setJoiner(Joiner.OR);
         */
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");
        } catch (Exception ex) {
        }
    }

    @Test
    //trigger not defined
    public void testAddOrUpdateScheduledReport3() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport3");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.AUDIT_LOGS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        /*
         * rd.getSchedule().setImmediate(true); rd.getSchedule().setTrigger(new
         * ReportingTrigger());
         * rd.getSchedule().getTrigger().setJoiner(Joiner.OR);
         */
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");
        } catch (Exception ex) {
        }
    }

    @Test
    //invalid due to user permissions
    public void testAddOrUpdateScheduledReport4() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport4");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.AUDIT_LOGS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));

        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);

        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    @Test
    //valid due to user permissions
    public void testAddOrUpdateScheduledReport41() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport41");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.AUDIT_LOGS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));
        rd.setSchedule(new ScheduleDefinition());
        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(adminctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getJobs());
            assertFalse(result.getJobs().isEmpty());
            for (int i = 0; i < result.getJobs().size(); i++) {
                assertNotNull(result.getJobs().get(i));
                assertNotNull(result.getJobs().get(i).getExportCSVDataRequestMsg());
                assertNotNull(result.getJobs().get(i).getJobId());
                assertNotNull(result.getJobs().get(i).getOwner());
                assertNotNull(result.getJobs().get(i).getSchedule());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
            rq.setClassification(new SecurityWrapper());
            rq.setJobId(result.getJobs().get(0).getJobId());
            DeleteScheduledReportResponseMsg deleteScheduledReport = instance.deleteScheduledReport(rq);
            assertNotNull(deleteScheduledReport);
            assertNotNull(deleteScheduledReport.getClassification());
            fail("unexpected failure.");
        }
        DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
        rq.setClassification(new SecurityWrapper());
        rq.setJobId(result.getJobs().get(0).getJobId());
        DeleteScheduledReportResponseMsg deleteScheduledReport = instance.deleteScheduledReport(rq);
        assertNotNull(deleteScheduledReport);
        assertNotNull(deleteScheduledReport.getClassification());
    }

    @Test
    //invalid due to missing urls
    public void testAddOrUpdateScheduledReport42() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport42");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.TRANSACTIONS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.setSchedule(new ScheduleDefinition());
        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(adminctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");

        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    @Test
    //valid due transactions export
    public void testAddOrUpdateScheduledReport43() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("testAddOrUpdateScheduledReport43");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().getURLs().add(url);
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.TRANSACTIONS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));
        rd.setSchedule(new ScheduleDefinition());

        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            RemoveServicePolicy(url);
            RemoveAuditAccess(bobusername, url);

        } catch (Exception ex) {
            RemoveServicePolicy(url);
            ex.printStackTrace();
            DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
            rq.setClassification(new SecurityWrapper());
            rq.setJobId(result.getJobs().get(0).getJobId());
            DeleteScheduledReportResponseMsg deleteScheduledReport = instance.deleteScheduledReport(rq);
            assertNotNull(deleteScheduledReport);
            assertNotNull(deleteScheduledReport.getClassification());

            fail("unexpected fault.");
        }
        DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
        rq.setClassification(new SecurityWrapper());
        rq.setJobId(result.getJobs().get(0).getJobId());
        DeleteScheduledReportResponseMsg deleteScheduledReport = instance.deleteScheduledReport(rq);
        assertNotNull(deleteScheduledReport);
        assertNotNull(deleteScheduledReport.getClassification());
    }

    @Test(expected = SecurityException.class)
    //invalid due transactions export permissions
    public void testAddOrUpdateScheduledReport44() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("testAddOrUpdateScheduledReport44");
        CreatePolicy(url);
        RemoveAuditAccess(bobusername, url);
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().getURLs().add(url);
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.TRANSACTIONS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));
        rd.setSchedule(new ScheduleDefinition());
        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
        } catch (Exception ex) {
            throw ex;
        } finally {
            RemoveServicePolicy(url);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    //invalid due schedule 
    public void testAddOrUpdateScheduledReport45() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("testAddOrUpdateScheduledReport45");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().getURLs().add(url);
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.TRANSACTIONS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());

        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        MonthlySchedule ds = new MonthlySchedule();

        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            RemoveServicePolicy(url);
            RemoveAuditAccess(bobusername, url);

        } catch (Exception ex) {
            throw ex;
        } finally {
            RemoveServicePolicy(url);

        }
    }

    @Test(expected = IllegalArgumentException.class)
    //invalid due schedule 
    public void testAddOrUpdateScheduledReport46() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("testAddOrUpdateScheduledReport46");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().getURLs().add(url);
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.TRANSACTIONS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());

        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());

        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(-1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            RemoveServicePolicy(url);
            RemoveAuditAccess(bobusername, url);

        } catch (Exception ex) {
            throw ex;
        } finally {
            RemoveServicePolicy(url);
        }
    }

    @Test
    //valid due schedule 
    public void testAddOrUpdateScheduledReport47() throws Exception {
        RemoveServicePolicy(url);
        System.out.println("testAddOrUpdateScheduledReport47");
        CreatePolicy(url);
        GrantUserAuditAccess(bobusername, url);
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().getURLs().add(url);
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.TRANSACTIONS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());

        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());

        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;
        try {
            result = instance.addOrUpdateScheduledReport(request);
            RemoveServicePolicy(url);
            RemoveAuditAccess(bobusername, url);

        } catch (Exception ex) {
            RemoveServicePolicy(url);
            ex.printStackTrace();
            DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
            rq.setClassification(new SecurityWrapper());
            rq.setJobId(result.getJobs().get(0).getJobId());
            DeleteScheduledReportResponseMsg deleteScheduledReport = instance.deleteScheduledReport(rq);
            assertNotNull(deleteScheduledReport);
            assertNotNull(deleteScheduledReport.getClassification());
            fail("unexpected fault.");
        }
        DeleteScheduledReportRequestMsg rq = new DeleteScheduledReportRequestMsg();
        rq.setClassification(new SecurityWrapper());
        rq.setJobId(result.getJobs().get(0).getJobId());
        DeleteScheduledReportResponseMsg deleteScheduledReport = instance.deleteScheduledReport(rq);
        assertNotNull(deleteScheduledReport);
        assertNotNull(deleteScheduledReport.getClassification());
    }

    @Test(expected = IllegalArgumentException.class)
    //time range is invalid
    public void testAddOrUpdateScheduledReport5() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport5");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.AUDIT_LOGS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(100000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(400000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = null;

        result = instance.addOrUpdateScheduledReport(request);

    }

    @Test
    //tigger is invalid
    public void testAddOrUpdateScheduledReport6() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport6");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.AUDIT_LOGS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        //ds.setStartingat(());
        //ds.setStartingon(());
        rd.setSchedule(new ScheduleDefinition());
        rd.getSchedule().getTriggers().add(ds);        //rd.getSchedule().getTrigger().setJoiner(Joiner.OR);

        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            AddOrUpdateScheduledReportResponseMsg result = instance.addOrUpdateScheduledReport(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    //tigger is invalid
    public void testAddOrUpdateScheduledReport8() throws Exception {
        System.out.println("testAddOrUpdateScheduledReport8");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.AUDIT_LOGS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        WeeklySchedule ds = new WeeklySchedule();
        ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));
        rd.setSchedule(new ScheduleDefinition());
        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        AddOrUpdateScheduledReportResponseMsg result = instance.addOrUpdateScheduledReport(request);

    }

    @Test(expected = IllegalArgumentException.class)
    //tigger is invalid
    public void testAddOrUpdateScheduledReportHTML() throws Exception {
        System.out.println("testAddOrUpdateScheduledReportHTML");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.getExportDataRequestMsg().setClassification(new SecurityWrapper());

        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.valueOf(-1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);

        AddOrUpdateScheduledReportResponseMsg result = instance.addOrUpdateScheduledReport(request);

    }

    @Test(expected = IllegalArgumentException.class)
    //tigger is invalid
    public void testAddOrUpdateScheduledReportHTML1() throws Exception {
        System.out.println("testAddOrUpdateScheduledReportHTML1");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        //     rd.getExportDataRequestMsg().setClassification(new SecurityWrapper());

        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        MonthlySchedule ds = new MonthlySchedule();
        //ds.setReoccurs(BigInteger.valueOf(1));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);

        AddOrUpdateScheduledReportResponseMsg result = instance.addOrUpdateScheduledReport(request);

    }

    @Test(expected = IllegalArgumentException.class)
    //tigger is invalid
    public void testAddOrUpdateScheduledReportHTML2() throws Exception {
        System.out.println("testAddOrUpdateScheduledReportHTML2");
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        request.getJobs().add(rd);
        rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        //     rd.getExportDataRequestMsg().setClassification(new SecurityWrapper());

        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        MonthlySchedule ds = new MonthlySchedule();
        //ds.setReoccurs(BigInteger.valueOf(1));
        ds.getDayOfTheMonthIs().add(Integer.valueOf(0));
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);

        AddOrUpdateScheduledReportResponseMsg result = instance.addOrUpdateScheduledReport(request);

    }

    /*
     * delete report test cases delete as global admin, should always work
     * delete as the creator of the job delete as the someone other than the
     * creator delete as the someone other than the creator that has read access
     * to it
     */
    /**
     * Test of getReport method, of class AutomatedReportingServiceImpl.
     */
    @Test
    public void testGetReport() throws Exception {
        System.out.println("getReport");
        GetReportRequestMsg request = null;
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            GetReportResponseMsg result = instance.getReport(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetReport2() throws Exception {
        System.out.println("getReport2");
        GetReportRequestMsg request = new GetReportRequestMsg();
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            GetReportResponseMsg result = instance.getReport(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetReport3() throws Exception {
        System.out.println("getReport3");
        GetReportRequestMsg request = new GetReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            GetReportResponseMsg result = instance.getReport(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetReport4() throws Exception {
        System.out.println("getReport4");
        GetReportRequestMsg request = new GetReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setReportId(UUID.randomUUID().toString());
        AutomatedReportingServiceImpl instance = new AutomatedReportingServiceImpl(userbobctx);
        try {
            GetReportResponseMsg result = instance.getReport(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }
    static String agent = "ARS-Test-Agent";

    private ReportDefinition createjob3(AutomatedReportingServiceImpl instance, String maryusername) throws Exception {
        AddOrUpdateScheduledReportRequestMsg request = new AddOrUpdateScheduledReportRequestMsg();
        request.setClassification(new SecurityWrapper());
        ReportDefinition rd = new ReportDefinition();
        rd.getAdditionalReaders().add(maryusername);
        request.getJobs().add(rd);
        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().getURLs().add(url);
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.TRANSACTIONS);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getRange().setStart(DatatypeFactory.newInstance().newDuration(500000));
        rd.getExportCSVDataRequestMsg().getRange().setEnd(DatatypeFactory.newInstance().newDuration(300000));
        //   rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.setEnabled(true);
        rd.setSchedule(new ScheduleDefinition());
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.ONE);
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        ds.setStartingAt((gcal));

        rd.getSchedule().getTriggers().add(ds);

        try {
            AddOrUpdateScheduledReportResponseMsg result = instance.addOrUpdateScheduledReport(request);
            return result.getJobs().get(0);

        } catch (Exception ex) {
            RemoveServicePolicy(url);
            ex.printStackTrace();
            fail("unexpected fault.");
        }
        return null;
    }
    //TODO test cases for invalid sla alerts

    private void RemoveAllReportsAndJobs(AutomatedReportingServiceImpl bob) {
        System.out.println("purging reports");
        GetMyScheduledReportsRequestMsg req = new GetMyScheduledReportsRequestMsg();
        req.setClassification(new SecurityWrapper(ClassificationType.U, ""));
        req.setOffset(0);
        req.setRecordlimit(1000);
        GetMyScheduledReportsResponseMsg myScheduledReports = null;
        try {
            myScheduledReports = bob.getMyScheduledReports(req);
        } catch (AccessDeniedException ex) {
            Logger.getLogger(AutomatedReportingServiceImplTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceUnavailableException ex) {
            Logger.getLogger(AutomatedReportingServiceImplTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (myScheduledReports != null) {
            for (int i = 0; i < myScheduledReports.getCompletedJobs().size(); i++) {
                DeleteScheduledReportRequestMsg r2 = new DeleteScheduledReportRequestMsg();
                r2.setClassification(new SecurityWrapper(ClassificationType.U, ""));
                r2.setJobId(myScheduledReports.getCompletedJobs().get(i).getJob().getJobId());
                try {
                    bob.deleteScheduledReport(r2);
                } catch (AccessDeniedException ex) {
                    Logger.getLogger(AutomatedReportingServiceImplTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ServiceUnavailableException ex) {
                    Logger.getLogger(AutomatedReportingServiceImplTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
