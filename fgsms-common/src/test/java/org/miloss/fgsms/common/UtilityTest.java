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
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.apache.commons.lang3.StringEscapeUtils;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.DailySchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.Daynames;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ExportCSVDataRequestMsg;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ExportDataRequestMsg;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.MonthlySchedule;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.Monthnames;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ReportDefinition;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ScheduleDefinition;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.TimeRangeDiff;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.WeeklySchedule;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.*;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.services.interfaces.reportingservice.ArrayOfReportTypeContainer;
import org.miloss.fgsms.services.interfaces.reportingservice.ExportRecordsEnum;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportTypeContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.postgresql.ds.PGPoolingDataSource;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author *
 */
public class UtilityTest {

    public UtilityTest() throws Exception {
        File f = new File("../resources/test-database.properties");
        Properties db = new Properties();
        if (f.exists()) {
            FileInputStream fis = new FileInputStream(f);
            db.load(fis);
            fis.close();
        } else {
            fail("cannot load test-database.properties");
        }
        try {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryForTest.class.getName());
            // Construct DataSource 
            //jdbc:postgresql://localhost:5432/fgsmsPerformance
            PGPoolingDataSource ds = new PGPoolingDataSource();
            ds.setMaxConnections(Integer.parseInt((String) db.get("perf_maxconnections")));
            ds.setServerName((String) db.get("perf_servername"));
            ds.setPortNumber(Integer.parseInt((String) db.get("perf_port")));
            ds.setDatabaseName((String) db.get("perf_db"));
            ds.setUser((String) db.get("perf_user"));
            ds.setPassword((String) db.get("perf_password"));
            ds.setSsl((Boolean) Boolean.parseBoolean((String) db.get("perf_ssl")));
            ds.setInitialConnections(Integer.parseInt((String) db.get("perf_initialconnections")));
            System.out.println("Binding to " + ds.getServerName() + ":" + ds.getPortNumber());
            InitialContextFactoryForTest.bind("java:fgsmsPerformance", ds);

            ds = new PGPoolingDataSource();
            ds.setMaxConnections(Integer.parseInt((String) db.get("config_maxconnections")));
            ds.setServerName((String) db.get("config_servername"));
            ds.setPortNumber(Integer.parseInt((String) db.get("config_port")));
            ds.setDatabaseName((String) db.get("config_db"));
            ds.setUser((String) db.get("config_user"));
            ds.setPassword((String) db.get("config_password"));
            ds.setSsl((Boolean) Boolean.parseBoolean((String) db.get("config_ssl")));
            ds.setInitialConnections(Integer.parseInt((String) db.get("config_initialconnections")));
            System.out.println("Binding to " + ds.getServerName() + ":" + ds.getPortNumber());
            InitialContextFactoryForTest.bind("java:fgsmsConfig", ds);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSerialization() throws JAXBException {
        TransactionalWebServicePolicy m = new TransactionalWebServicePolicy();
        JAXBContext ctx = Utility.getSerializationContext();
        Marshaller createMarshaller = ctx.createMarshaller();
        ObjectFactory pcsfac = new ObjectFactory();
        StringWriter sw = new StringWriter();
        createMarshaller.marshal(pcsfac.createServicePolicy(m), sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        createMarshaller.marshal(pcsfac.createFederationPolicy(new FederationPolicy()), sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        createMarshaller.marshal(pcsfac.createStatisticalServicePolicy(), sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        createMarshaller.marshal(pcsfac.createStatusServicePolicy(), sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        createMarshaller.marshal(pcsfac.createProcessPolicy(), sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        createMarshaller.marshal(pcsfac.createMachinePolicy(), sw);
        System.out.println(sw.toString());


        org.miloss.fgsms.services.interfaces.datacollector.ObjectFactory d = new org.miloss.fgsms.services.interfaces.datacollector.ObjectFactory();



        AddDataRequestMsg t = new AddDataRequestMsg();
        sw = new StringWriter();
        createMarshaller.marshal(t, sw);
        System.out.println(sw.toString());

        AddData a = new AddData();
        a.setReq((t));

        sw = new StringWriter();
        createMarshaller.marshal(a, sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        AddMoreData ab = new AddMoreData();
        ab.getReq().add(t);
        createMarshaller.marshal(ab, sw);
        System.out.println(sw.toString());



        System.out.println("Serialization complete");
    }

    @Test
    public void testSerialization2() throws JAXBException {
        MachinePolicy m = new MachinePolicy();
        JAXBContext ctx = Utility.getSerializationContext();
        Marshaller createMarshaller = ctx.createMarshaller();
        ObjectFactory pcsfac = new ObjectFactory();
        StringWriter sw = new StringWriter();
        createMarshaller.marshal(pcsfac.createServicePolicy(m), sw);
        System.out.println(sw.toString());

        org.miloss.fgsms.services.interfaces.datacollector.ObjectFactory d = new org.miloss.fgsms.services.interfaces.datacollector.ObjectFactory();

        AddDataRequestMsg t = new AddDataRequestMsg();
        sw = new StringWriter();
        createMarshaller.marshal(t, sw);
        System.out.println(sw.toString());

        AddData a = new AddData();
        a.setReq((t));

        sw = new StringWriter();
        createMarshaller.marshal(a, sw);
        System.out.println(sw.toString());

        sw = new StringWriter();
        AddMoreData ab = new AddMoreData();
        ab.getReq().add(t);
        createMarshaller.marshal(ab, sw);
        System.out.println(sw.toString());



        System.out.println("Serialization complete");
    }

    /**
     * Test of durationToString method, of class Utility.
     */
    @Test
    public void testDurationToString() {
        System.out.println("DurationToString");
        DatatypeFactory f;
        try {
            f = DatatypeFactory.newInstance();
            Duration d = f.newDuration(1000);

            String expResult = "1s";
            String result = Utility.durationToString(d);
            assertEquals(expResult, result);

        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(UtilityTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void testARSSerialization() throws Exception {
        JAXBContext ctx = Utility.getARSSerializationContext();
        DatatypeFactory df = DatatypeFactory.newInstance();
        //   org.miloss.fgsms.services.interfaces.automatedreportingservice.AddOrUpdateScheduledReportRequestMsg req = new AddOrUpdateScheduledReportRequestMsg();
        // req.setClassification(new SecurityWrapper());
        GregorianCalendar gcal = new GregorianCalendar();
        ReportDefinition rd = new ReportDefinition();
        rd.getAdditionalReaders().add("test");
        rd.setEnabled(true);
        rd.setFriendlyName("test");
        rd.setJobId("test");
        //rd.setLastRanAt(df.newCalendar(gcal));
        rd.setOwner("test");
        rd.setSchedule(new ScheduleDefinition());
        WeeklySchedule ws = new WeeklySchedule();
        ws.getDayOfTheWeekIs().add(Daynames.SUNDAY);
        ws.setReoccurs(BigInteger.ONE);
        rd.getSchedule().getTriggers().add(ws);
        DailySchedule ds = new DailySchedule();
        ds.setReoccurs(BigInteger.ONE);
        ds.setStartingAt((gcal));
        
        rd.getSchedule().getTriggers().add(ds);
        MonthlySchedule ms = new MonthlySchedule();
        ms.getDayOfTheMonthIs().add(1);
        ms.getMonthNameIs().add(Monthnames.MAY);
        ds.setStartingAt((gcal));
        
        rd.getSchedule().getTriggers().add(ms);

        rd.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        rd.getExportCSVDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportCSVDataRequestMsg().setExportType(ExportRecordsEnum.MACHINE);
        rd.getExportCSVDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportCSVDataRequestMsg().getURLs().add("test");
        rd.getExportCSVDataRequestMsg().getRange().setEnd(df.newDuration(1000));
        rd.getExportCSVDataRequestMsg().getRange().setStart(df.newDuration(1000));

        rd.setExportDataRequestMsg(new ExportDataRequestMsg());
        rd.getExportDataRequestMsg().setClassification(new SecurityWrapper());
        rd.getExportDataRequestMsg().getURLs().add("test");
        rd.getExportDataRequestMsg().setRange(new TimeRangeDiff());
        rd.getExportDataRequestMsg().getRange().setEnd(df.newDuration(1000));
        rd.getExportDataRequestMsg().getRange().setStart(df.newDuration(1000));
        rd.getExportDataRequestMsg().setReportTypes(new ArrayOfReportTypeContainer());
        ReportTypeContainer rtc = new ReportTypeContainer();
        rtc.setType("org.miloss.fgsms.services.rs.impl.reports.ws.MeanTimeBetweenFailureByService");
        rd.getExportDataRequestMsg().getReportTypes().getReportTypeContainer().add(rtc);
        rd.getNotifications().add(new SLAAction());
        //   req.getJobs().add(rd);
        Marshaller m = ctx.createMarshaller();
        StringWriter sw = new StringWriter();
        m.marshal(rd, sw);
        System.out.println(sw.toString());
        assertNotNull(sw.toString());
        Unmarshaller u = ctx.createUnmarshaller();
        Object unmarshal = u.unmarshal(new StringReader(sw.toString()));
        ReportDefinition rd2 = (ReportDefinition) unmarshal;
        assertNotNull(rd2);
        assertNotNull(rd2.getExportCSVDataRequestMsg());
        assertNotNull(rd2.getExportDataRequestMsg());
        assertFalse(rd2.getNotifications().isEmpty());
        assertNotNull(rd2.getSchedule());
        assertFalse(rd2.getSchedule().getTriggers().isEmpty());
    }

    /**
     * Test of durationLargestUnitToString method, of class Utility.
     */
    @Test
    public void testDurationLargestUnitToString() {
        try {
            System.out.println("DurationLargestUnitToString");
            DatatypeFactory f;
            f = DatatypeFactory.newInstance();
            Duration d = f.newDuration(100000);


            String expResult = "1min";
            String result = Utility.durationLargestUnitToString(d);
            assertEquals(expResult, result);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(UtilityTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Test of ICMClassificationToString method, of class Utility.
     */
    @Test
    public void testICMClassificationToString() {
        System.out.println("ICMClassificationToString");
        ClassificationType type = ClassificationType.U;
        String expResult = "UNCLASSIFIED";
        String result = Utility.ICMClassificationToString(type);
        assertEquals(expResult, result);

    }

    /**
     * Test of ICMClassificationToColorCodeString method, of class Utility.
     */
    @Test
    public void testICMClassificationToColorCodeString() {
        System.out.println("ICMClassificationToColorCodeString");
        ClassificationType type = ClassificationType.U;
        String expResult = "lime";
        String result = Utility.ICMClassificationToColorCodeString(type);
        assertEquals(expResult, result);

    }

    /**
     * Test of listStringtoString method, of class Utility.
     */
    @Test
    public void testListStringtoString() {
        System.out.println("ListStringtoString");
        List<String> data = new ArrayList<String>();
        data.add("bob");
        data.add(" ");
        data.add("smith");
        String expResult = "bob smith";
        String result = Utility.listStringtoString(data);
        assertEquals(expResult, result);


    }

    /**
     * Test of getActionNameFromXML method, of class Utility.
     */
    @Test
    public void testGetActionNameFromXML() {
        System.out.println("GetActionNameFromXML");
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:org:miloss:fgsms:services:interfaces:policyConfiguration\" xmlns:urn1=\"urn:org:miloss:fgsms:services:interfaces:common\">   <soapenv:Header/>   <soapenv:Body>      <urn:GetGlobalPolicy>         <urn:request>            <urn:classification>               <urn1:classification>U</urn1:classification>               <urn1:caveats></urn1:caveats>            </urn:classification>         </urn:request>      </urn:GetGlobalPolicy>   </soapenv:Body></soapenv:Envelope>";
        String expResult = "urn:GetGlobalPolicy";
        String result = Utility.getActionNameFromXML(xml);
        assertEquals(expResult, result);


    }

    /**
     * Test of encodeHTML method, of class Utility.
     */
    @Test
    @Ignore
    public void testEncodeHTML() {
        System.out.println("encodeHTML");
        String s = "this is normal text.<br>This is <b>encoded < text";
        String expResult = "this is normal text.&[lt;br&gt;This is &lt;b&gt;encoded &lt]; text";
        String result = Utility.encodeHTML(s);
        assertEquals(expResult, result);


    }

    /**
     * Test of stringIsNullOrEmpty method, of class Utility.
     */
    @Test
    public void testStringIsNullOrEmpty() {
        System.out.println("StringIsNullOrEmpty");
        String s = "";
        boolean expResult = true;
        boolean result = Utility.stringIsNullOrEmpty(s);
        assertEquals(expResult, result);
        s = null;
        result = Utility.stringIsNullOrEmpty(s);
        assertEquals(expResult, result);

        s = " ";
        expResult = false;
        result = Utility.stringIsNullOrEmpty(s);
        assertEquals(expResult, result);

        s = "asdasdasd";
        expResult = false;
        result = Utility.stringIsNullOrEmpty(s);
        assertEquals(expResult, result);

    }

    /**
     * Test of durationToTimeInMS method, of class Utility.
     */
    @Test
    public void testDurationToTimeInMS() {
        try {
            System.out.println("DurationToTimeInMS");
            DatatypeFactory f = DatatypeFactory.newInstance();

            Duration d = f.newDuration(1000);
            long expResult = 1000L;
            long result = Utility.durationToTimeInMS(d);
            assertEquals(expResult, result);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(UtilityTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("unexpected failure");
        }


    }
   

    /**
     * Test of truncate method, of class Utility.
     */
    @Test
    public void testTruncate() {
        System.out.println("Truncate");
        String x = "0123456789";
        int length = 4;
        String expResult = "0123";
        String result = Utility.truncate(x, length);
        assertEquals(expResult, result);

        x = "";
        length = 4;
        expResult = "";
        result = Utility.truncate(x, length);
        assertEquals(expResult, result);

        x = null;
        length = 4;
        expResult = "";
        result = Utility.truncate(x, length);
        assertEquals(expResult, result);

        x = "asdasd";
        length = 0;
        expResult = "";
        result = Utility.truncate(x, length);
        assertEquals(expResult, result);

        x = null;
        length = 0;
        expResult = "";
        result = Utility.truncate(x, length);
        assertEquals(expResult, result);


    }

    /**
     * Test of getPerformanceDB_NONPOOLED_Connection method, of class Utility.
     */
    @Ignore
    @Deprecated
    @Test
    public void testGetPerformanceDB_NONPOOLED_Connection() {
        System.out.println("GetPerformanceDB_NONPOOLED_Connection");
        Connection result = Utility.getPerformanceDB_NONPOOLED_Connection();
        assertNotNull(result);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(UtilityTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getConfigurationDB_NONPOOLED_Connection method, of class Utility.
     */
    @Ignore
    @Deprecated
    @Test
    public void testGetConfigurationDB_NONPOOLED_Connection() {
        System.out.println("GetConfigurationDB_NONPOOLED_Connection");

        Connection result = Utility.getConfigurationDB_NONPOOLED_Connection();
        assertNotNull(result);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(UtilityTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getPerformanceDBConnection method, of class Utility.
     */
    @Test
    public void testGetPerformanceDBConnection() {
        System.out.println("GetPerformanceDBConnection");
        Connection result = Utility.getConfigurationDBConnection();
        assertNotNull(result);
        try {
            result.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }


    }

    /**
     * Test of getConfigurationDBConnection method, of class Utility.
     */
    @Test
    public void testGetConfigurationDBConnection() {
        System.out.println("GetPerformanceDBConnection");
        Connection result = Utility.getConfigurationDBConnection();
        assertNotNull(result);
        try {
            result.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void TestSerialization55() throws ClassNotFoundException, MalformedURLException, JAXBException {

        JAXBContext ctx = Utility.getARSSerializationContext();
        ReportDefinition r = new ReportDefinition();

        r.getAdditionalReaders().add("bob");
        r.setExportDataRequestMsg(new ExportDataRequestMsg());
        r.setExportCSVDataRequestMsg(new ExportCSVDataRequestMsg());
        r.setSchedule(new ScheduleDefinition());
        r.getNotifications().add(new SLAAction());
         r.getNotifications().get(0).getParameterNameValue().add(new NameValuePair());
        Marshaller createMarshaller = ctx.createMarshaller();
        StringWriter sw = new StringWriter();
        createMarshaller.marshal(r, sw);
        System.out.println(sw.toString());
    }
    
    
    @Test
    public void testStringEscaping() throws Exception {
        String url = "http://something:99/path/something?wsdl";
        System.out.println(url);
        String encoded = StringEscapeUtils.escapeHtml4(url);
        System.out.println(encoded);
        String back=StringEscapeUtils.unescapeHtml4(encoded);
        System.out.println(back);
        assertEquals(url,back);
    }
}
