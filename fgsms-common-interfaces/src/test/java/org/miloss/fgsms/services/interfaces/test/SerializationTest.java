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

package org.miloss.fgsms.services.interfaces.test;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.*;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ExportCSVDataRequestMsg;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ExportDataRequestMsg;
import org.miloss.fgsms.services.interfaces.common.*;
import org.miloss.fgsms.services.interfaces.dataaccessservice.*;
import org.miloss.fgsms.services.interfaces.datacollector.*;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.services.interfaces.reportingservice.*;
import org.miloss.fgsms.services.interfaces.status.StatusServiceService;

import org.junit.Test;
import static org.junit.Assert.*;
import org.oasis_open.docs.wsn.client.CreatePullPointService;
import org.oasis_open.docs.wsn.client.NotificationService;
import org.oasis_open.docs.wsn.client.PausableSubscriptionManagerService;
import org.oasis_open.docs.wsn.client.PublisherRegistrationManagerService;
import org.oasis_open.docs.wsn.client.PullPointService;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author AO
 */
public class SerializationTest {

    public SerializationTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void TestSerialization() throws ClassNotFoundException, MalformedURLException {
        ArrayOfTransactionLog t0 = new ArrayOfTransactionLog();
        if (!(t0 instanceof Serializable)) {
            fail("ArrayOfTransactionLog is not serializable");
        }
    }

    @Test
    public void TestSerialization2() throws ClassNotFoundException, MalformedURLException {
        GetCurrentBrokerDetailsResponseMsg t1 = new GetCurrentBrokerDetailsResponseMsg();
        if (!(t1 instanceof Serializable)) {
            fail("GetCurrentBrokerDetailsResponseMsg is not serializable");
        }
    }

    @Test
    public void TestSerialization3() throws ClassNotFoundException, MalformedURLException {
        GetCurrentBrokerDetailsResponse t3 = new GetCurrentBrokerDetailsResponse();
        if (!(t3 instanceof Serializable)) {
            fail("GetCurrentBrokerDetailsResponse is not serializable");
        }
    }

    @Test
    public void TestSerialization4() throws ClassNotFoundException, MalformedURLException {
        GetMessageLogsResponse t4 = new GetMessageLogsResponse();
        if (!(t4 instanceof Serializable)) {
            fail("GetMessageLogsResponse is not serializable");
        }
    }

    @Test
    public void TestSerialization5() throws ClassNotFoundException, MalformedURLException {
        GetMessageLogsResponseMsg t5 = new GetMessageLogsResponseMsg();
        if (!(t5 instanceof Serializable)) {
            fail("GetMessageLogsResponseMsg is not serializable");
        }
    }

    @Test
    public void TestSerialization6() throws ClassNotFoundException, MalformedURLException {
        GetMessageLogsByRangeResponse t6 = new GetMessageLogsByRangeResponse();
        if (!(t6 instanceof Serializable)) {
            fail("GetMessageLogsByRangeResponse is not serializable");
        }
    }

    @Test
    public void TestSerialization7() throws ClassNotFoundException, MalformedURLException {
        GetPerformanceAverageStatsResponseMsg t7 = new GetPerformanceAverageStatsResponseMsg();
        if (!(t7 instanceof Serializable)) {
            fail("GetPerformanceAverageStatsResponseMsg is not serializable");
        }
    }

    @Test
    public void TestSerialization8() throws ClassNotFoundException, MalformedURLException {
        QueueORtopicDetails t8 = new QueueORtopicDetails();
        if (!(t8 instanceof Serializable)) {
            fail("QueueORtopicDetails is not serializable");
        }
    }

    @Test
    public void TestSerialization9() throws ClassNotFoundException, MalformedURLException {
        SecurityWrapper t9 = new SecurityWrapper();
        if (!(t9 instanceof Serializable)) {
            fail("SecurityWrapper is not serializable");
        }
    }

    @Test
    public void TestSerialization10() throws ClassNotFoundException, MalformedURLException {
        TransactionLog t10 = new TransactionLog();
        if (!(t10 instanceof Serializable)) {
            fail("TransactionLog is not serializable");
        }
    }

    @Test
    public void TestSerialization11() throws ClassNotFoundException, MalformedURLException {
        GetOperationalStatusLogResponseMsg t11 = new GetOperationalStatusLogResponseMsg();
        if (!(t11 instanceof Serializable)) {
            fail("GetOperationalStatusLogResponseMsg is not serializable");
        }
    }

    @Test
    public void TestSerialization12() throws ClassNotFoundException, MalformedURLException {
        GetMachinePerformanceLogsByRangeResponseMsg t12 = new GetMachinePerformanceLogsByRangeResponseMsg();
        if (!(t12 instanceof Serializable)) {
            fail("GetMachinePerformanceLogsByRangeResponseMsg is not serializable");
        }
    }

    @Test
    public void TestSerialization13() throws ClassNotFoundException, MalformedURLException {
        GetMachinePerformanceLogsByRangeResponse t13 = new GetMachinePerformanceLogsByRangeResponse();
        if (!(t13 instanceof Serializable)) {
            fail("GetMachinePerformanceLogsByRangeResponse is not serializable");
        }
    }

    @Test
    public void TestSerialization14() throws ClassNotFoundException, MalformedURLException {
        MachinePerformanceData t14 = new MachinePerformanceData();
        if (!(t14 instanceof Serializable)) {
            fail("MachinePerformanceData is not serializable");
        }
    }

    @Test
    public void TestSerialization15() throws ClassNotFoundException, MalformedURLException {

        NetworkAdapterPerformanceData t15 = new NetworkAdapterPerformanceData();
        if (!(t15 instanceof Serializable)) {
            fail("NetworkAdapterPerformanceData is not serializable");
        }
    }

    @Test
    public void TestSerialization16() throws ClassNotFoundException, MalformedURLException {

        DriveInformation t16 = new DriveInformation();
        if (!(t16 instanceof Serializable)) {
            fail("DriveInformation is not serializable");
        }
    }

    @Test
    public void TestSerialization17() throws ClassNotFoundException, MalformedURLException {
        GetProcessPerformanceLogsByRangeResponseMsg t17 = new GetProcessPerformanceLogsByRangeResponseMsg();
        if (!(t17 instanceof Serializable)) {
            fail("GetProcessPerformanceLogsByRangeResponseMsg is not serializable");
        }
    }

    @Test
    public void TestSerialization18() throws ClassNotFoundException, MalformedURLException {

        ProcessPerformanceData t18 = new ProcessPerformanceData();
        if (!(t18 instanceof Serializable)) {
            fail("ProcessPerformanceData is not serializable");
        }
    }

    @Test
    public void TestSerialization19() throws ClassNotFoundException, MalformedURLException {

        OperationalRecord t19 = new OperationalRecord();
        if (!(t19 instanceof Serializable)) {
            fail("OperationalRecord is not serializable");
        }
    }

    @Test
    public void TestSerialization20() throws ClassNotFoundException, MalformedURLException {

        //  AddMoreData t11 = new AddMoreData();
        if (!Class.forName(AddMoreData.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        // if (!(t11 instanceof XmlRootElement)) {
        {
            fail("AddMoreData must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization21() throws ClassNotFoundException, MalformedURLException {
        //ServicePolicy t12 = new ServicePolicy();
        //FederationPolicy
        if (!Class.forName(FederationPolicyCollection.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("FederationPolicyCollection must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization22() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(ServicePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("ServicePolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization23() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(ProcessPolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("ProcessPolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization24() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(MachinePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("MachinePolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization25() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(AddDataRequestMsg.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("AddDataRequestMsg must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization28() throws ClassNotFoundException, MalformedURLException {


        if (!Class.forName(StatusServicePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("StatusServicePolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization29() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(StatisticalServicePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("StatisticalServicePolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization30() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(TransactionalWebServicePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("TransactionalWebServicePolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization31() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(SetProcessListByMachineResponseMsg.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("SetProcessListByMachineResponseMsg must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization32() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(MachineInformation.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("MachineInformation must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization33() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(SetProcessListByMachineRequestMsg.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("SetProcessListByMachineRequestMsg must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization34() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(AlertMessageDefinition.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("AlertMessageDefinition must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization39() throws ClassNotFoundException, MalformedURLException {


        //this is just a reminder to set the constructor on this item
        SecurityWrapper w = new SecurityWrapper();
        assertNotNull(w.getClassification());
        assertEquals(ClassificationType.U, w.getClassification());
    }

    @Test
    public void TestSerialization40() throws ClassNotFoundException, MalformedURLException {
        SecurityWrapper w = new SecurityWrapper(ClassificationType.U, "");
        assertNotNull(w.getClassification());
        assertEquals(ClassificationType.U, w.getClassification());
    }

    @Test
    public void TestSerialization41() throws ClassNotFoundException, MalformedURLException {
        org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory dasfac = new org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory();
    }

    @Test
    public void TestSerialization42() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(ReportDefinition.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("ReportDefinition must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization421() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(AddDataRequestMsg.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("AddDataRequestMsg must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization422() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(StatisticalServicePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("StatisticalServicePolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization423() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(StatusServicePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("StatusServicePolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization424() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(ProcessPolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("ProcessPolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization425() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(TransactionalWebServicePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("TransactionalWebServicePolicy must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization426() throws ClassNotFoundException, MalformedURLException {
        if (!Class.forName(MachinePolicy.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        {
            fail("MachinePolicy must have the @XmlRootElement");
        }




        /* org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory pcsfac = new org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory();
        
         QName q = pcsfac._ArrayOfSLAElement_QNAME;
         //q = pcsfac._ArrayOfServicePolicy_QNAME;
         q = pcsfac._ArrayOfUserIdentity_QNAME; // = _TransactionalWebServicePolicyUserIdentification_QNAME
         q = pcsfac._ArrayOfUserInfo_QNAME;
         q = pcsfac._ArrayOfXMLNamespacePrefixies_QNAME; //createArrayOfXMLNamespacePrefixies
         q = pcsfac._ArrayOfXPathExpressionType_QNAME;*/
        /*
         * public final static QName _ArrayOfServicePolicy_QNAME = new
         * QName("urn:org:miloss:fgsms:services:interfaces:policyConfiguration",
         * "policies"); public final static QName
         * _ArrayOfXMLNamespacePrefixies_QNAME = new
         * QName("urn:org:miloss:fgsms:services:interfaces:policyConfiguration",
         * "Namespaces"); public final static QName
         * _ArrayOfXPathExpressionType_QNAME = new
         * QName("urn:org:miloss:fgsms:services:interfaces:policyConfiguration",
         * "XPaths"); public final static QName _ArrayOfUserIdentity_QNAME = new
         * QName("urn:org:miloss:fgsms:services:interfaces:policyConfiguration",
         * "UserIdentification"); public final static QName
         * _ArrayOfSLAElement_QNAME = new
         * QName("urn:org:miloss:fgsms:services:interfaces:policyConfiguration",
         * "ServiceLevelAggrements"); public final static QName
         * _ArrayOfUserInfo_QNAME = new
         * QName("urn:org:miloss:fgsms:services:interfaces:policyConfiguration",
         * "UserList");
         */

    }

    @Test
    public void TestSerialization48() throws ClassNotFoundException, MalformedURLException {

        org.miloss.fgsms.services.interfaces.automatedreportingservice.ReportDefinition t15 = new ReportDefinition();
        if (!(t15 instanceof Serializable)) {
            fail("ARS ReportDefinition is not serializable");
        }
    }

    @Test
    public void TestSerialization49() throws ClassNotFoundException, MalformedURLException {

        org.miloss.fgsms.services.interfaces.automatedreportingservice.ExportDataRequestMsg t15 = new ExportDataRequestMsg();
        if (!(t15 instanceof Serializable)) {
            fail("ARS ExportDataRequestMsg is not serializable");
        }
    }

    @Test
    public void TestSerialization50() throws ClassNotFoundException, MalformedURLException {

        org.miloss.fgsms.services.interfaces.automatedreportingservice.ExportCSVDataRequestMsg t15 = new ExportCSVDataRequestMsg();
        if (!(t15 instanceof Serializable)) {
            fail("ARS ExportCSVDataRequestMsg is not serializable");
        }
    }

    @Test
    public void TestSerialization52() throws ClassNotFoundException, MalformedURLException {

        org.miloss.fgsms.services.interfaces.automatedreportingservice.ScheduleDefinition t15 = new ScheduleDefinition();
        if (!(t15 instanceof Serializable)) {
            fail("ARS ScheduleDefinition is not serializable");
        }
    }

    @Test
    public void TestSerialization53() throws ClassNotFoundException, MalformedURLException {

        org.miloss.fgsms.services.interfaces.automatedreportingservice.TimeRangeDiff t15 = new TimeRangeDiff();
        if (!(t15 instanceof Serializable)) {
            fail("ARS TimeRangeDiff is not serializable");
        }
    }

    @Test
    public void TestSerialization54() throws ClassNotFoundException, MalformedURLException {

        //  AddMoreData t11 = new AddMoreData();
        if (!Class.forName(ReportDefinition.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        // if (!(t11 instanceof XmlRootElement)) {
        {
            fail("ReportDefinition must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization55() throws ClassNotFoundException, MalformedURLException {

        //  AddMoreData t11 = new AddMoreData();
        if (!Class.forName(ReportDefinition.class.getCanonicalName()).isAnnotationPresent(XmlRootElement.class)) //if (!(t12 instanceof @XmlRootElement ))
        // if (!(t11 instanceof XmlRootElement)) {
        {
            fail("ReportDefinition must have the @XmlRootElement");
        }
    }

    @Test
    public void TestSerialization56() throws ClassNotFoundException, MalformedURLException {

        GetMyScheduledReportsResponseMsg r = new GetMyScheduledReportsResponseMsg();
        ReportDefinition findJobWithIdOf = r.findJobWithIdOf("xyz");
        /*
         *   public ReportDefinition findJobWithIdOf(String jobId) {
         ReportDefinition rd = null;
         for (int i = 0; i < this.completedJobs.size(); i++) {
         if (this.completedJobs.get(i).getJob().getJobId().equals(jobId)) {
         rd = this.completedJobs.get(i).getJob();
         return rd;
         }
         }
         return rd
         */
    }

    @Test
    public void TestSerialization57() throws Exception {
        if (!Class.forName(RuleBaseType.class.getCanonicalName()).isAnnotationPresent(XmlSeeAlso.class)) {
            fail("RuleBaseType must have the @XmlSeeAlso(\" for all known implementations \")");
        }
    }

    @Test
    public void TestSerialization59() throws ClassNotFoundException, MalformedURLException {

        ArrayOfReportTypeContainer t15 = new ArrayOfReportTypeContainer();
        if (!(t15 instanceof Serializable)) {
            fail("ARS ArrayOfReportTypeContainer is not serializable");
        }
    }

    @Test
    public void ProxyLoaderTests1() throws Exception {
        PolicyConfigurationService p = new PolicyConfigurationService();
        PCS pcsPort = p.getPCSPort();
    }

    @Test
    public void ProxyLoaderTests2() throws Exception {
        DataAccessService_Service p = new DataAccessService_Service();
        p.getDASPort();
    }

    @Test
    public void ProxyLoaderTests3() throws Exception {
        DataCollectorService p = new DataCollectorService();
        p.getDCSPort();
    }

    @Test
    public void ProxyLoaderTests4() throws Exception {
        ReportingService_Service p = new ReportingService_Service();
        p.getReportingServicePort();
    }

    @Test
    public void ProxyLoaderTests5() throws Exception {
        AutomatedReportingService_Service p = new AutomatedReportingService_Service();
        p.getAutomatedReportingServicePort();
    }

    @Test
    public void ProxyLoaderTests6() throws Exception {
        StatusServiceService p = new StatusServiceService();
        p.getStatusServicePort();
    }

    @Test
    public void ProxyLoaderTests7() throws Exception {
        CreatePullPointService p = new CreatePullPointService();
        p.getCreatePullPointBindingPort();
    }

    @Test
    public void ProxyLoaderTests8() throws Exception {
        NotificationService p = new NotificationService();
        p.getNotificationPort();
    }

    @Test
    public void ProxyLoaderTests9() throws Exception {
        PausableSubscriptionManagerService p = new PausableSubscriptionManagerService();
        p.getPausableSubscriptionManagerPort();
    }

    @Test
    public void ProxyLoaderTests10() throws Exception {
        PublisherRegistrationManagerService p = new PublisherRegistrationManagerService();
        p.getPublisherRegistrationManagerPort();
    }

    @Test
    public void ProxyLoaderTests11() throws Exception {
        PullPointService p = new PullPointService();
        p.getPullPointBindingPort();
    }
}
// @XmlRootElement