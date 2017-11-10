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
package org.miloss.fgsms.services.das.impl;

import org.miloss.fgsms.services.das.impl.DAS4jBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.*;
import org.miloss.fgsms.test.WebServiceBaseTests;
import static org.junit.Assert.*;
import org.junit.*;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import static org.miloss.fgsms.test.WebServiceBaseTests.RemoveServicePolicy;
import static org.miloss.fgsms.test.WebServiceBaseTests.bobusername;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author *
 */
public class DAS4jBeanTest2 extends WebServiceBaseTests {
     
     public DAS4jBeanTest2() throws Exception {
          super();
          url = "http://localhost:8180/jUnitTestCaseDAS";
          Init();
     }

     String agent = "junit";
     /**
      * Test of getAlerts method, of class DAS4jBean.
      */
     @Test
     public void testGetAlertsNullMsg() throws Exception {
          System.out.println("testGetAlertsNullMsg");
          GetAlertsRequestMsg getAlertsRequest = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getAlerts(getAlertsRequest);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
     }
     
     @Test
     public void testGetAlertsNullClass() throws Exception {
          System.out.println("testGetAlertsNullClass");
          GetAlertsRequestMsg getAlertsRequest = new GetAlertsRequestMsg();
          getAlertsRequest.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getAlerts(getAlertsRequest);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
     }
     
     @Test
     public void testGetAlertsValid() throws Exception {
          System.out.println("testGetAlertsValid");
          GetAlertsRequestMsg getAlertsRequest = new GetAlertsRequestMsg();
          getAlertsRequest.setClassification(new SecurityWrapper());
          getAlertsRequest.setRecordcount(40);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List<GetAlertsResponseMsg> result = instance.getAlerts(getAlertsRequest);
               assertNotNull(result);
               for (int i = 0; i < result.size(); i++) {
                    assertNotNull(result.get(i).getClassification());
                    assertNotNull(result.get(i).getTimestamp());
                    assertNotNull(result.get(i).getMessage());
                    assertNotNull(result.get(i).getType());
                    assertNotNull(result.get(i).getUrl());
               }
          } catch (Exception ex) {
               ex.printStackTrace();
               fail("unexpected failure");
          }
          
     }
     
     @Test
     public void testGetAlertsNoCount() throws Exception {
          System.out.println("testGetAlertsNoCount");
          GetAlertsRequestMsg getAlertsRequest = new GetAlertsRequestMsg();
          getAlertsRequest.setClassification(new SecurityWrapper());
          //getAlertsRequest.setRecordcount(40);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getAlerts(getAlertsRequest);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
     }
     
     @Test
     public void testGetAlertsInvalidCount() throws Exception {
          System.out.println("testGetAlertsInvalidCount");
          GetAlertsRequestMsg getAlertsRequest = new GetAlertsRequestMsg();
          getAlertsRequest.setClassification(new SecurityWrapper());
          getAlertsRequest.setRecordcount(-40);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getAlerts(getAlertsRequest);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
     }
     
     @Test
     public void testGetAlertsInvalidCount2() throws Exception {
          System.out.println("testGetAlertsInvalidCount2");
          GetAlertsRequestMsg getAlertsRequest = new GetAlertsRequestMsg();
          getAlertsRequest.setClassification(new SecurityWrapper());
          getAlertsRequest.setRecordcount(20000);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getAlerts(getAlertsRequest);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
     }

     /**
      * Test of getPerformanceAverageStatsAll method, of class DAS4jBean.
      */
     @Test
     public void testGetPerformanceAverageStatsAllNull() throws Exception {
          System.out.println("testGetPerformanceAverageStatsAllNull");
          SecurityWrapper classification = null;
          TimeRange range = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getPerformanceAverageStatsAll(classification, range);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsAllNullClass() throws Exception {
          System.out.println("testGetPerformanceAverageStatsAllNullClass");
          SecurityWrapper classification = null;
          TimeRange range = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getPerformanceAverageStatsAll(classification, range);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsAllNullRange() throws Exception {
          System.out.println("testGetPerformanceAverageStatsAllNullRange");
          SecurityWrapper classification = new SecurityWrapper();
          TimeRange range = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getPerformanceAverageStatsAll(classification, range);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsAllEmptyRange() throws Exception {
          System.out.println("testGetPerformanceAverageStatsAllEmptyRange");
          SecurityWrapper classification = new SecurityWrapper();
          TimeRange range = new TimeRange();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getPerformanceAverageStatsAll(classification, range);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsAllInvalidRange() throws Exception {
          System.out.println("testGetPerformanceAverageStatsAllInvalidRange");
          SecurityWrapper classification = new SecurityWrapper();
          TimeRange range = new TimeRange();
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          range.setEnd((gcal));
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getPerformanceAverageStatsAll(classification, range);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsAllInvalidRange2() throws Exception {
          System.out.println("testGetPerformanceAverageStatsAllInvalidRange2");
          SecurityWrapper classification = new SecurityWrapper();
          TimeRange range = new TimeRange();
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          range.setStart((gcal));
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getPerformanceAverageStatsAll(classification, range);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsAllInvalidRange3() throws Exception {
          System.out.println("testGetPerformanceAverageStatsAllInvalidRange3");
          SecurityWrapper classification = new SecurityWrapper();
          TimeRange range = new TimeRange();
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          range.setStart((gcal));
          gcal = new GregorianCalendar();
          gcal.add(Calendar.DATE, 1);
          gcal.setTimeInMillis(System.currentTimeMillis());
          range.setStart((gcal));
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getPerformanceAverageStatsAll(classification, range);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsAllValid() throws Exception {
          System.out.println("testGetPerformanceAverageStatsAllValid");
          RemoveServicePolicy(url);
          CreatePolicy(url);
          DAS4jBeanTest.InsertAgg(url);
          SecurityWrapper classification = new SecurityWrapper();
          TimeRange range = new TimeRange();
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          range.setStart((gcal));
          gcal = new GregorianCalendar();
          gcal.add(Calendar.DATE, -1);
          gcal.setTimeInMillis(System.currentTimeMillis());
          range.setEnd((gcal));
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List<GetPerformanceAverageStatsResponseMsg> result = instance.getPerformanceAverageStatsAll(classification, range);
               DAS4jBeanTest.RemoveAgg(url);
               RemoveServicePolicy(url);
               assertNotNull(result);
               for (int i = 0; i < result.size(); i++) {
                    assertNotNull(result.get(i).getClassification());
                    assertNotNull(result.get(i).getURL());
               }
          } catch (Exception ex) {
               ex.printStackTrace();
               DAS4jBeanTest.RemoveAgg(url);
               fail("unexpected failure");
          }
     }

     /**
      * Test of getMessageTransactionLogDetails method, of class DAS4jBean.
      */
     @Test
     public void testGetMessageTransactionLogDetailsNull() throws Exception {
          System.out.println("getMessageTransactionLogDetailsNull");
          GetMessageTransactionLogDetailsMsg req = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogDetailsResponseMsg result = instance.getMessageTransactionLogDetails(req);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetMessageTransactionLogDetailsNullClass() throws Exception {
          System.out.println("getMessageTransactionLogDetailsNullClass");
          GetMessageTransactionLogDetailsMsg req = new GetMessageTransactionLogDetailsMsg();
          
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogDetailsResponseMsg result = instance.getMessageTransactionLogDetails(req);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetMessageTransactionLogDetailsNullId() throws Exception {
          System.out.println("getMessageTransactionLogDetailsNullId");
          GetMessageTransactionLogDetailsMsg req = new GetMessageTransactionLogDetailsMsg();
          req.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogDetailsResponseMsg result = instance.getMessageTransactionLogDetails(req);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetMessageTransactionLogDetailsValidAsAdmin() throws Exception {
          System.out.println("testGetMessageTransactionLogDetailsValidAsAdmin");
          String id = Insert_WS_Transaction(url, agent);
          
          GetMessageTransactionLogDetailsMsg req = new GetMessageTransactionLogDetailsMsg();
          req.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               
               req.setTransactionID(id);
               GetMessageTransactionLogDetailsResponseMsg result = instance.getMessageTransactionLogDetails(req);
               RemoveTransaction(id);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertFalse(Utility.stringIsNullOrEmpty(result.getAction()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getAgentMemo()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getAgentType()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getCorrectedURL()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getMonitorHostname()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getOriginalRequestURL()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getRelatedTransactionID()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getServiceHostname()));
               assertTrue(Utility.stringIsNullOrEmpty(result.getSlaFaultMsg()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getTransactionId()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getTransactionthreadId()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getXmlRequestMessage()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getXmlResponseMessage()));
               assertNotNull(result.getHeadersRequest());
               assertNotNull(result.getHeadersResponse());
               assertNotNull(result.getIdentity());
               assertNotNull(result.getTimestamp());
               
          } catch (Exception ex) {
               ex.printStackTrace();
               RemoveTransaction(id);
               fail("unexpected failure");
          }
     }
     
     @Test
     public void testGetMessageTransactionLogDetailsValidAsBobWithAccess() throws Exception {
          System.out.println("testGetMessageTransactionLogDetailsValidAsBobWithAccess");
          String id = Insert_WS_Transaction(url, agent);
          GrantUserAuditAccess(bobusername, url);
          GetMessageTransactionLogDetailsMsg req = new GetMessageTransactionLogDetailsMsg();
          req.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(userbobctx);
          try {
               
               req.setTransactionID(id);
               GetMessageTransactionLogDetailsResponseMsg result = instance.getMessageTransactionLogDetails(req);
               RemoveTransaction(id);
               RemoveAuditAccess(bobusername, url);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertFalse(Utility.stringIsNullOrEmpty(result.getAction()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getAgentMemo()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getAgentType()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getCorrectedURL()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getMonitorHostname()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getOriginalRequestURL()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getRelatedTransactionID()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getServiceHostname()));
               assertTrue(Utility.stringIsNullOrEmpty(result.getSlaFaultMsg()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getTransactionId()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getTransactionthreadId()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getXmlRequestMessage()));
               assertFalse(Utility.stringIsNullOrEmpty(result.getXmlResponseMessage()));
               assertNotNull(result.getHeadersRequest());
               assertNotNull(result.getHeadersResponse());
               assertNotNull(result.getIdentity());
               assertNotNull(result.getTimestamp());
               
          } catch (Exception ex) {
               ex.printStackTrace();
               RemoveTransaction(id);
               RemoveAuditAccess(bobusername, url);
               fail("unexpected failure");
          }
     }
     
     @Test
     public void testGetMessageTransactionLogDetailsValidAsBobWithoutAccess() throws Exception {
          System.out.println("testGetMessageTransactionLogDetailsValidAsBobWithoutAccess");
          String id = Insert_WS_Transaction(url, agent);
          
          GetMessageTransactionLogDetailsMsg req = new GetMessageTransactionLogDetailsMsg();
          req.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(userbobctx);
          try {
               req.setTransactionID(id);
               GetMessageTransactionLogDetailsResponseMsg result = instance.getMessageTransactionLogDetails(req);
               RemoveTransaction(id);
               fail("unexpected success");
          } catch (Exception ex) {
               RemoveTransaction(id);
               
          }
     }

     /**
      * Test of getPerformanceAverageStats method, of class DAS4jBean.
      */
     @Test
     public void testGetPerformanceAverageStatsNullMsg() throws Exception {
          System.out.println("testGetPerformanceAverageStatsNullMsg");
          GetPerformanceAverageStatsRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsNullClassMsg() throws Exception {
          System.out.println("testGetPerformanceAverageStatsNullClassMsg");
          GetPerformanceAverageStatsRequestMsg request = new GetPerformanceAverageStatsRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsNullRangeMsg() throws Exception {
          System.out.println("testGetPerformanceAverageStatsNullRangeMsg");
          GetPerformanceAverageStatsRequestMsg request = new GetPerformanceAverageStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsNullRangeMsg2() throws Exception {
          System.out.println("testGetPerformanceAverageStatsNullRangeMsg2");
          GetPerformanceAverageStatsRequestMsg request = new GetPerformanceAverageStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          /*
           * TimeRange range = new TimeRange(); DatatypeFactory f =
           * DatatypeFactory.newInstance(); GregorianCalendar gcal = new
           * GregorianCalendar();
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setStart((gcal));
           * request.setRange(range);
           */
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsNullRangeMsg3() throws Exception {
          System.out.println("testGetPerformanceAverageStatsNullRangeMsg3");
          GetPerformanceAverageStatsRequestMsg request = new GetPerformanceAverageStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          /*
           * TimeRange range = new TimeRange(); DatatypeFactory f =
           * DatatypeFactory.newInstance(); GregorianCalendar gcal = new
           * GregorianCalendar();
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setEnd((gcal));
           * request.setRange(range);
           */
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsInvalidRange() throws Exception {
          System.out.println("testGetPerformanceAverageStatsInvalidRange");
          GetPerformanceAverageStatsRequestMsg request = new GetPerformanceAverageStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          /*
           * TimeRange range = new TimeRange(); DatatypeFactory f =
           * DatatypeFactory.newInstance();
           *
           * GregorianCalendar gcal = new GregorianCalendar();
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setStart((gcal)); gcal = new
           * GregorianCalendar(); gcal.add(Calendar.DATE, 1);
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setEnd((gcal));
           * request.setRange(range);
           */
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsNullURLMsg() throws Exception {
          System.out.println("testGetPerformanceAverageStatsNullURLMsg");
          GetPerformanceAverageStatsRequestMsg request = new GetPerformanceAverageStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          /*
           * TimeRange range = new TimeRange(); DatatypeFactory f =
           * DatatypeFactory.newInstance();
           *
           * GregorianCalendar gcal = new GregorianCalendar();
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setStart((gcal)); gcal = new
           * GregorianCalendar(); gcal.add(Calendar.DATE, -1);
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setEnd((gcal));
           * request.setRange(range);
           */
          
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsServiceExists() throws Exception {
          System.out.println("testGetPerformanceAverageStatsServiceExists");
          DAS4jBeanTest.InsertAgg(url);
          DAS4jBeanTest.InsertAgg(url, 86400000L);
          GetPerformanceAverageStatsRequestMsg request = new GetPerformanceAverageStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          /*
           * TimeRange range = new TimeRange(); DatatypeFactory f =
           * DatatypeFactory.newInstance();
           *
           * GregorianCalendar gcal = new GregorianCalendar();
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setStart((gcal)); gcal = new
           * GregorianCalendar(); gcal.add(Calendar.DATE, -1);
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setEnd((gcal));
           * request.setRange(range);
           */
          request.setURL(url);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               DAS4jBeanTest.RemoveAgg(url);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertNotNull(result.getURL());
               
          } catch (Exception ex) {
               ex.printStackTrace();
               DAS4jBeanTest.RemoveAgg(url);
               fail("unexpected fault");
          }
     }
     
     @Test
     public void testGetPerformanceAverageStatsServiceNotExists() throws Exception {
          System.out.println("testGetPerformanceAverageStatsServiceNotExists");
          GetPerformanceAverageStatsRequestMsg request = new GetPerformanceAverageStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
        //range is no longer used
        /*
           * TimeRange range = new TimeRange(); DatatypeFactory f =
           * DatatypeFactory.newInstance();
           *
           * GregorianCalendar gcal = new GregorianCalendar();
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setStart((gcal)); gcal = new
           * GregorianCalendar(); gcal.add(Calendar.DATE, -1);
           * gcal.setTimeInMillis(System.currentTimeMillis());
           * range.setEnd((gcal));
           * request.setRange(range);
           */
          request.setURL(url);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageStats(request);
               //fail("unexpected success");
          } catch (Exception ex) {
          }
     }

     /**
      * Test of getPerformanceAverageHostingStats method, of class DAS4jBean.
      */
     @Test
     public void testGetPerformanceAverageHostingStatsNull() throws Exception {
          System.out.println("getPerformanceAverageHostingStatsNull");
          GetPerformanceAverageHostStatsRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageHostingStatsNullRangeAndHost() throws Exception {
          System.out.println("testGetPerformanceAverageHostingStatsNullRangeAndHost");
          GetPerformanceAverageHostStatsRequestMsg request = new GetPerformanceAverageHostStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageHostingStatsNullRange() throws Exception {
          System.out.println("testGetPerformanceAverageHostingStatsNullRange");
          GetPerformanceAverageHostStatsRequestMsg request = new GetPerformanceAverageHostStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setHostname(Utility.getHostName());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageHostingStatsNullHost() throws Exception {
          System.out.println("testGetPerformanceAverageHostingStatsNullHost");
          GetPerformanceAverageHostStatsRequestMsg request = new GetPerformanceAverageHostStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setRange(new TimeRange());
          DAS4jBean instance = new DAS4jBean(adminctx);
          
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          request.getRange().setStart((gcal));
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
          gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          gcal.add(Calendar.DATE, -1);
          request.getRange().setEnd((gcal));
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetPerformanceAverageHostingStats() throws Exception {
          System.out.println("testGetPerformanceAverageHostingStats");
          GetPerformanceAverageHostStatsRequestMsg request = new GetPerformanceAverageHostStatsRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setRange(new TimeRange());
          request.setHostname(Utility.getHostName());
          DAS4jBean instance = new DAS4jBean(adminctx);
          
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          request.getRange().setStart((gcal));
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
          gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          gcal.add(Calendar.DATE, -1);
          request.getRange().setEnd((gcal));
          try {
               GetPerformanceAverageStatsResponseMsg result = instance.getPerformanceAverageHostingStats(request);
               assertNotNull(result);
               assertNotNull(result.getClassification());
          } catch (Exception ex) {
          }
     }

     /**
      * Test of getMessageLogsByRange method, of class DAS4jBean.
      */
     @Test
     public void testgetMessageLogsByRangeNullMessage() throws Exception {
          System.out.println("testgetMessageLogsByRangeNullMessage");
          GetMessageLogsRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageLogsResponseMsg result = instance.getMessageLogsByRange(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testgetMessageLogsByRangeNullclass() throws Exception {
          System.out.println("testgetMessageLogsByRangeNullclass");
          GetMessageLogsRequestMsg request = new GetMessageLogsRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageLogsResponseMsg result = instance.getMessageLogsByRange(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }

     /**
      * Test of getMessageTransactionLog method, of class DAS4jBean.
      */
     @Test
     public void testGetMessageTransactionLogNullMsg() throws Exception {
          System.out.println("testGetMessageTransactionLogNullMsg");
          GetMessageTransactionLogRequestMsg request = null; //new GetMessageTransactionLogRequestMsg();
          //request.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogResponseMsg result = instance.getMessageTransactionLog(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetMessageTransactionLogNoClass() throws Exception {
          System.out.println("testGetMessageTransactionLogNoClass");
          GetMessageTransactionLogRequestMsg request = new GetMessageTransactionLogRequestMsg();
          //  request.setClassification(new SecurityWrapper());

          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogResponseMsg result = instance.getMessageTransactionLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetMessageTransactionLogNullIdNullURL() throws Exception {
          System.out.println("testGetMessageTransactionLogNullIdNullURL");
          GetMessageTransactionLogRequestMsg request = new GetMessageTransactionLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogResponseMsg result = instance.getMessageTransactionLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetMessageTransactionLogNullId() throws Exception {
          System.out.println("testGetMessageTransactionLogNullId");
          
          GetMessageTransactionLogRequestMsg request = new GetMessageTransactionLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          
          request.setURL(url);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogResponseMsg result = instance.getMessageTransactionLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetMessageTransactionLogNullURL() throws Exception {
          System.out.println("testGetMessageTransactionLogNullURL");
          GetMessageTransactionLogRequestMsg request = new GetMessageTransactionLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          String id = Insert_WS_Transaction(url, agent);
          request.setTransactionId(id);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogResponseMsg result = instance.getMessageTransactionLog(request);
               RemoveTransaction(id);
               fail("unexpected success");
          } catch (Exception ex) {
               RemoveTransaction(id);
          }
     }
     
     @Test
     public void testGetMessageTransactionLog() throws Exception {
          System.out.println("testGetMessageTransactionLog");
          GetMessageTransactionLogRequestMsg request = new GetMessageTransactionLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          String id = Insert_WS_Transaction(url, agent);
          request.setTransactionId(id);
          request.setURL(url);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMessageTransactionLogResponseMsg result = instance.getMessageTransactionLog(request);
               RemoveTransaction(id);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertNotNull(result.getRequestMessage());
               assertNotNull(result.getRequestMessage());
          } catch (Exception ex) {
               RemoveTransaction(id);
          }
     }

     /**
      * Test of getOperationalStatusLog method, of class DAS4jBean.
      */
     @Test
     public void testGetOperationalStatusLogNullMessage() throws Exception {
          System.out.println("testGetOperationalStatusLogNullMessage");
          GetOperationalStatusLogRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetOperationalStatusLogNullClass() throws Exception {
          System.out.println("testGetOperationalStatusLogNullClass");
          GetOperationalStatusLogRequestMsg request = new GetOperationalStatusLogRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetOperationalStatusLogNullUrlNullRange() throws Exception {
          System.out.println("testGetOperationalStatusLogNullUrlNullRange");
          GetOperationalStatusLogRequestMsg request = new GetOperationalStatusLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetOperationalStatusLogNullUrl() throws Exception {
          System.out.println("testGetOperationalStatusLogNullUrlNullRange");
          GetOperationalStatusLogRequestMsg request = new GetOperationalStatusLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setRange(new TimeRange());
          // request
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          request.getRange().setEnd((gcal));
          
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
          gcal = new GregorianCalendar();
          gcal.add(Calendar.DATE, -1);
          gcal.setTimeInMillis(System.currentTimeMillis());
          request.getRange().setStart((gcal));
          
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetOperationalStatusLogNullRange() throws Exception {
          System.out.println("testGetOperationalStatusLogNullUrlNullRange");
          GetOperationalStatusLogRequestMsg request = new GetOperationalStatusLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setURL(url);
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetOperationalStatusValidExisting() throws Exception {
          //insert data to make this work
          int count = DAS4jBeanTest.InsertOperationalRecords(url);
          System.out.println("testGetOperationalStatusValidExisting");
          GetOperationalStatusLogRequestMsg request = new GetOperationalStatusLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setRange(new TimeRange());
          request.setURL(url);
          // request
          DAS4jBean instance = new DAS4jBean(adminctx);
          
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          request.getRange().setEnd((gcal));
          gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          gcal.add(Calendar.DATE, -1);
          
          request.getRange().setStart((gcal));
          
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               DAS4jBeanTest.RemoveOperationalRecords(url);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertNotNull(result.getURL());
               assertNotNull(result.getOperationalRecord());
               /*
                * assertFalse(result.getOperationalRecord().isEmpty()); if
                * (result.getOperationalRecord().size() > 2) { fail("too many
                * records returned"); }
                */
               //bassertEquals(1, result.getOperationalRecord().size());

          } catch (Exception ex) {
               ex.printStackTrace();
               DAS4jBeanTest.RemoveOperationalRecords(url);
               fail("unexpected fail");
          }
     }
     
     @Test
     public void testGetOperationalStatusValidNonExisting() throws Exception {
          
          System.out.println("testGetOperationalStatusValidNonExisting");
          GetOperationalStatusLogRequestMsg request = new GetOperationalStatusLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setRange(new TimeRange());
          request.setURL(url);
          // request
          DAS4jBean instance = new DAS4jBean(adminctx);
          
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          request.getRange().setEnd((gcal));
          gcal = new GregorianCalendar();
          gcal.add(Calendar.DATE, -1);
          gcal.setTimeInMillis(System.currentTimeMillis());
          request.getRange().setStart((gcal));
          
          try {
               GetOperationalStatusLogResponseMsg result = instance.getOperationalStatusLog(request);
               DAS4jBeanTest.RemoveOperationalRecords(url);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertNotNull(result.getURL());
               assertNotNull(result.getOperationalRecord());
               assertTrue(result.getOperationalRecord().isEmpty());
               
          } catch (Exception ex) {
               ex.printStackTrace();
               fail("unexpected fail");
          }
     }

     /**
      * Test of getMonitoredServiceList method, of class DAS4jBean.
      */
     @Test
     public void testGetMonitoredServiceListNullRequest() throws Exception {
          System.out.println("testGetMonitoredServiceListNullRequest");
          GetMonitoredServiceListRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMonitoredServiceListResponseMsg result = instance.getMonitoredServiceList(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
     }
     
     @Test
     public void testGetMonitoredServiceListNullClass() throws Exception {
          System.out.println("testGetMonitoredServiceListNullClass");
          GetMonitoredServiceListRequestMsg request = new GetMonitoredServiceListRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMonitoredServiceListResponseMsg result = instance.getMonitoredServiceList(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          
     }
     
     @Test
     public void testGetMonitoredServiceListValidNoUrl() throws Exception {
          System.out.println("testGetMonitoredServiceListValidNoUrl");
          DAS4jBeanTest.InsertServicePolicyAndActionList(url);
          GetMonitoredServiceListRequestMsg request = new GetMonitoredServiceListRequestMsg();
          request.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetMonitoredServiceListResponseMsg result = instance.getMonitoredServiceList(request);
               DAS4jBeanTest.RemoveServicePolicyAndActionList(url);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertNotNull(result.getServiceList());
               assertNotNull(result.getServiceList());
               assertNotNull(result.getServiceList().getServiceType());
               assertTrue(result.getServiceList().getServiceType().size() >= 1);
          } catch (Exception ex) {
               ex.printStackTrace();
               DAS4jBeanTest.RemoveServicePolicyAndActionList(url);
               fail("unexpected failure");
          }
          
     }
     
     @Test
     public void testGetMonitoredServiceListValidUrlSpecified() throws Exception {
          System.out.println("testGetMonitoredServiceListValidUrlSpecified");
          DAS4jBeanTest.InsertServicePolicyAndActionList(url);
          GetMonitoredServiceListRequestMsg request = new GetMonitoredServiceListRequestMsg();
          request.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory f = new org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory();
               
               request.setURL(url);
               GetMonitoredServiceListResponseMsg result = instance.getMonitoredServiceList(request);
               DAS4jBeanTest.RemoveServicePolicyAndActionList(url);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertNotNull(result.getServiceList());
               assertNotNull(result.getServiceList());
               assertNotNull(result.getServiceList().getServiceType());
               assertTrue(result.getServiceList().getServiceType().size() >= 1);
          } catch (Exception ex) {
               ex.printStackTrace();
               DAS4jBeanTest.RemoveServicePolicyAndActionList(url);
               fail("unexpected failure");
               
          }
          
     }

     /**
      * Test of getSLAFaultRecords method, of class DAS4jBean.
      */
     @Test
     public void testGetSLAFaultRecordsRecordsNullMsg() throws Exception {
          System.out.println("getSLAFaultRecordsNullMsg");
          GetSLAFaultRecordsRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getSLAFaultRecords(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetSLAFaultRecordsNullClass() throws Exception {
          System.out.println("testGetSLAFaultRecordsNullClass");
          GetSLAFaultRecordsRequestMsg request = new GetSLAFaultRecordsRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getSLAFaultRecords(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }

     /**
      * Test of getAllOperationalStatusLog method, of class DAS4jBean.
      */
     @Test
     public void testGetAllOperationalStatusLogNullRange() throws Exception {
          System.out.println("testGetAllOperationalStatusLogNullRange");
          GetAllOperationalStatusLogRequestMsg request = new GetAllOperationalStatusLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getAllOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetAllOperationalStatusLogWithRange() throws Exception {
          System.out.println("testGetAllOperationalStatusLogWithRange");
          GetAllOperationalStatusLogRequestMsg request = new GetAllOperationalStatusLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setRange(new TimeRange());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getAllOperationalStatusLog(request);
               
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetAllOperationalStatusLogWithRange2() throws Exception {
          System.out.println("testGetAllOperationalStatusLogWithRange no limit defined");
          GetAllOperationalStatusLogRequestMsg request = new GetAllOperationalStatusLogRequestMsg();
          request.setClassification(new SecurityWrapper());
          request.setRange(new TimeRange());
          DatatypeFactory f = DatatypeFactory.newInstance();
          GregorianCalendar gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          request.getRange().setStart((gcal));
          
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               List result = instance.getAllOperationalStatusLog(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
          gcal = new GregorianCalendar();
          gcal.setTimeInMillis(System.currentTimeMillis());
          gcal.add(Calendar.DATE, -1);
          request.getRange().setEnd((gcal));
          
          try {
               List<GetAllOperationalStatusLogResponseMsg> result = instance.getAllOperationalStatusLog(request);
               assertNotNull(result);
               for (int i = 0; i < result.size(); i++) {
                    assertNotNull(result.get(i));
                    assertNotNull(result.get(i).getClassification());
                    assertNotNull(result.get(i).getURL());
                    assertNotNull(result.get(i).getOperationalRecord());
                    for (int k = 0; k < result.get(i).getOperationalRecord().size(); k++) {
                         assertNotNull(result.get(i).getOperationalRecord().get(k).getID());
                         assertNotNull(result.get(i).getOperationalRecord().get(k).getID());
                         assertNotNull(result.get(i).getOperationalRecord().get(k).getTimestamp());
                         assertNotNull(result.get(i).getOperationalRecord().get(k).isOperational());
                    }
               }
          } catch (Exception ex) {
               ex.printStackTrace();
               fail("unexpected failure");
          }
     }

     /**
      * Test of getServiceHostList method, of class DAS4jBean.
      */
     @Test
     public void testGetServiceHostListNullMsg() throws Exception {
          System.out.println("testGetServiceHostListNullMsg");
          GetServiceHostListRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetServiceHostListResponseMsg result = instance.getServiceHostList(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetServiceHostListNullClassMsg() throws Exception {
          System.out.println("testGetServiceHostListNullClassMsg");
          GetServiceHostListRequestMsg request = new GetServiceHostListRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetServiceHostListResponseMsg result = instance.getServiceHostList(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetServiceHostList() throws Exception {
          System.out.println("testGetServiceHostList");
          GetServiceHostListRequestMsg request = new GetServiceHostListRequestMsg();
          request.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetServiceHostListResponseMsg result = instance.getServiceHostList(request);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertNotNull(result.getHosts());
               assertNotNull(result.getHosts());
               assertNotNull(result.getHosts().getHostInstanceStats());
               for (int i = 0; i < result.getHosts().getHostInstanceStats().size(); i++) {
                    assertNotNull(result.getHosts().getHostInstanceStats().get(i));
                    assertNotNull(result.getHosts().getHostInstanceStats().get(i).getHost());
                    assertNotNull(result.getHosts().getHostInstanceStats().get(i).getRecordedTransactionCount());
               }
               
          } catch (Exception ex) {
               ex.printStackTrace();
               fail("unexpected failure");
          }
     }

     /**
      * Test of getDataCollectorList method, of class DAS4jBean.
      */
     @Test
     public void testGetDataCollectorListNullMsg() throws Exception {
          System.out.println("testGetDataCollectorListNullMsg");
          GetDataCollectorListRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetDataCollectorListResponseMsg result = instance.getDataCollectorList(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetDataCollectorListClassMsg() throws Exception {
          System.out.println("testGetDataCollectorListClassMsg");
          GetDataCollectorListRequestMsg request = new GetDataCollectorListRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetDataCollectorListResponseMsg result = instance.getDataCollectorList(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetDataCollectorListMsg() throws Exception {
          System.out.println("testGetDataCollectorListMsg");
          GetDataCollectorListRequestMsg request = new GetDataCollectorListRequestMsg();
          request.setClassification(new SecurityWrapper());
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetDataCollectorListResponseMsg result = instance.getDataCollectorList(request);
               assertNotNull(result);
               assertNotNull(result.getClassification());
               assertNotNull(result.getHosts());
               assertNotNull(result.getHosts());
               assertNotNull(result.getHosts().getHostInstanceStats());
               for (int i = 0; i < result.getHosts().getHostInstanceStats().size(); i++) {
                    assertNotNull(result.getHosts().getHostInstanceStats().get(i));
                    assertNotNull(result.getHosts().getHostInstanceStats().get(i).getHost());
                    assertNotNull(result.getHosts().getHostInstanceStats().get(i).getRecordedTransactionCount());
               }
          } catch (Exception ex) {
               ex.printStackTrace();
               fail("unexpected failure");
          }
     }

     /**
      * Test of purgePerformanceData method, of class DAS4jBean.
      */
     @Test
     public void testPurgePerformanceData() throws Exception {
          System.out.println("purgePerformanceData");
          PurgePerformanceDataRequestMsg request = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               PurgePerformanceDataResponseMsg result = instance.purgePerformanceData(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testPurgePerformanceDataNotClass() throws Exception {
          System.out.println("purgePerformanceDataNotClass");
          PurgePerformanceDataRequestMsg request = new PurgePerformanceDataRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               PurgePerformanceDataResponseMsg result = instance.purgePerformanceData(request);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testPurgePerformanceDataNullRange() throws Exception {
          System.out.println("purgePerformanceDataNullRange");
          PurgePerformanceDataRequestMsg request = new PurgePerformanceDataRequestMsg();
          request.setClassification(new SecurityWrapper());
          
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               PurgePerformanceDataResponseMsg result = instance.purgePerformanceData(request);
               assertNotNull(result);
               assertNotNull(result.getClassification());
          } catch (Exception ex) {
               fail("unexpected failure");
          }
     }
     
     @Test
     public void testPurgePerformanceDataRange() throws Exception {
          System.out.println("testPurgePerformanceDataRange");
          PurgePerformanceDataRequestMsg request = new PurgePerformanceDataRequestMsg();
          request.setClassification(new SecurityWrapper());
          org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory fac = new org.miloss.fgsms.services.interfaces.dataaccessservice.ObjectFactory();
          /*
           * String InsertTransaction = InsertTransaction(); TimeRange r = new
           * TimeRange();
           * request.setRange(fac.createPurgePerformanceDataRequestMsgRange(new
           * TimeRange()));
           *
           *
           * DAS4jBean instance = new DAS4jBean(adminctx); try {
           * PurgePerformanceDataResponseMsg result =
           * instance.purgePerformanceData(request); fail("unexpected success"); }
           * catch (Exception ex) { }
           */
     }

     /**
      * Test of getAuditLog method, of class DAS4jBean.
      */
     @Test
     public void testGetAuditLogNullMsg() throws Exception {
          System.out.println("testGetAuditLogNullMsg");
          GetAuditLogRequestMsg getAuditLog = null;
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetAuditLogResponseMsg result = instance.getAuditLog(getAuditLog);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     @Test
     public void testGetAuditLogNullClassMsg() throws Exception {
          System.out.println("testGetAuditLogNullClassMsg");
          GetAuditLogRequestMsg getAuditLog = new GetAuditLogRequestMsg();
          DAS4jBean instance = new DAS4jBean(adminctx);
          try {
               GetAuditLogResponseMsg result = instance.getAuditLog(getAuditLog);
               fail("unexpected success");
          } catch (Exception ex) {
          }
     }
     
     
}
