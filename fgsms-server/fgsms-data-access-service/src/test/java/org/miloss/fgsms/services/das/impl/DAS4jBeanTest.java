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
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author *
 */
public class DAS4jBeanTest extends WebServiceBaseTests {

    public DAS4jBeanTest() throws Exception {
        super();
        url = "http://localhost:8180/jUnitTestCaseDAS";
        Init();
    }

    @Test
    public void testGetAuditLog() throws Exception {
        System.out.println("testGetAuditLog");
        GetAuditLogRequestMsg getAuditLog = new GetAuditLogRequestMsg();
        getAuditLog.setClassification(new SecurityWrapper());
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAuditLogResponseMsg result = instance.getAuditLog(getAuditLog);
            //
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @Test
    public void testGetAuditLog2() throws Exception {
        System.out.println("testGetAuditLog2");
        GetAuditLogRequestMsg getAuditLog = new GetAuditLogRequestMsg();
        getAuditLog.setClassification(new SecurityWrapper());
        getAuditLog.setRecordcount(40);

        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAuditLogResponseMsg result = instance.getAuditLog(getAuditLog);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getAuditLog());
            for (int i = 0; i < result.getAuditLog().size(); i++) {
                assertNotNull(result.getAuditLog().get(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    /**
     * Test of getBrokerList method, of class DAS4jBean.
     */
    @Test
    public void testGetBrokerListNull() throws Exception {
        System.out.println("getBrokerList");
        GetBrokerListRequestMsg getBrokerListRequest = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetBrokerListResponseMsg result = instance.getBrokerList(getBrokerListRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }

    }

    @Test
    public void testGetBrokerListNullClass() throws Exception {
        System.out.println("testGetBrokerListNullClass");
        GetBrokerListRequestMsg getBrokerListRequest = new GetBrokerListRequestMsg();

        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetBrokerListResponseMsg result = instance.getBrokerList(getBrokerListRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }

    }

    @Test
    public void testGetBrokerListClass() throws Exception {
        System.out.println("testGetBrokerListClass");
        GetBrokerListRequestMsg getBrokerListRequest = new GetBrokerListRequestMsg();
        getBrokerListRequest.setClassification(new SecurityWrapper());
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetBrokerListResponseMsg result = instance.getBrokerList(getBrokerListRequest);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getBrokerList());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }

    }

    @Test
    public void testGetBrokerListClassAsBob() throws Exception {
        System.out.println("testGetBrokerListClass");
        GetBrokerListRequestMsg getBrokerListRequest = new GetBrokerListRequestMsg();
        getBrokerListRequest.setClassification(new SecurityWrapper());
        DAS4jBean instance = new DAS4jBean(userbobctx);
        try {
            GetBrokerListResponseMsg result = instance.getBrokerList(getBrokerListRequest);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getBrokerList());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }

    }

    /**
     * Test of getCurrentBrokerDetails method, of class DAS4jBean.
     */
    @Test
    public void testGetCurrentBrokerDetails() throws Exception {
        System.out.println("getCurrentBrokerDetails");
        GetCurrentBrokerDetailsRequestMsg getCurrentBrokerDetailsRequest = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetCurrentBrokerDetailsResponseMsg result = instance.getCurrentBrokerDetails(getCurrentBrokerDetailsRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of getHistoricalBrokerDetails method, of class DAS4jBean.
     */
    @Test
    public void testGetHistoricalBrokerDetails() throws Exception {
        System.out.println("getHistoricalBrokerDetails");
        GetHistoricalBrokerDetailsRequestMsg getHistoricalBrokerDetailsRequest = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetHistoricalBrokerDetailsResponseMsg result = instance.getHistoricalBrokerDetails(getHistoricalBrokerDetailsRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
        //TODO more test cases
    }

    /**
     * Test of getHistoricalTopicQueueDetails method, of class DAS4jBean.
     */
    @Test
    public void testGetHistoricalTopicQueueDetails() throws Exception {
        System.out.println("getHistoricalTopicQueueDetails");
        //TODO insert test records then try again
        GetHistoricalTopicQueueDetailsRequestMsg getHistoricalTopicQueueDetailsRequest = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetHistoricalTopicQueueDetailsResponseMsg result = instance.getHistoricalTopicQueueDetails(getHistoricalTopicQueueDetailsRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
        //TODO more test cases
    }

    /**
     * Test of getAgentTypes method, of class DAS4jBean.
     */
    @Test
    public void testGetAgentTypes() throws Exception {
        System.out.println("getAgentTypes");
        GetAgentTypesRequestMsg getAgentTypesRequest = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAgentTypesResponseMsg result = instance.getAgentTypes(getAgentTypesRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetAgentTypesNullClass() throws Exception {
        System.out.println("testGetAgentTypesNullClass");
        GetAgentTypesRequestMsg getAgentTypesRequest = new GetAgentTypesRequestMsg();
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAgentTypesResponseMsg result = instance.getAgentTypes(getAgentTypesRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetAgentTypesClass() throws Exception {
        System.out.println("testGetAgentTypesClass");
        GetAgentTypesRequestMsg getAgentTypesRequest = new GetAgentTypesRequestMsg();
        getAgentTypesRequest.setClassification(new SecurityWrapper());
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAgentTypesResponseMsg result = instance.getAgentTypes(getAgentTypesRequest);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getAgentType());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected fault");
        }
    }

    /**
     * Test of getThreadTransactions method, of class DAS4jBean.
     */
    @Test
    public void testGetThreadTransactions() throws Exception {
        System.out.println("getThreadTransactions");
        GetThreadTransactionsRequestMsg getThreadTransactionsRequest = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetThreadTransactionsResponseMsg result = instance.getThreadTransactions(getThreadTransactionsRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
        //TODO more test cases
    }

    /**
     * Test of getRecentMessageLogs method, of class DAS4jBean.
     */
    @Test
    public void testGetRecentMessageLog() throws Exception {
        System.out.println("getRecentMessageLogs");
        GetRecentMessageLogsRequestMsg request = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetMessageLogsResponseMsg result = instance.getRecentMessageLogs(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetRecentMessageLogClass() throws Exception {
        System.out.println("testGetRecentMessageLogClass");
        String id = Insert_WS_Transaction(url, agent);
        GetRecentMessageLogsRequestMsg request = new GetRecentMessageLogsRequestMsg();

        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetMessageLogsResponseMsg result = instance.getRecentMessageLogs(request);
            fail("unexpected success");
        } catch (Exception ex) {
            RemoveTransaction(id);
        }
    }
    //junit
    String agent = "junit";

    @Test
    public void testGgetRecentMessageLogs() throws Exception {
        String id = Insert_WS_Transaction(url, agent);
        List<String> failingcombos = new ArrayList<String>();
        boolean ok = true;
        for (int i = 0; i < 256; i++) {
            String b = Integer.toBinaryString(i);
            //pad it
            while (b.length() < 8) {
                b = "0" + b;
            }
            System.out.print("Test " + b);

            boolean success
                    = getRecentMessageLogs(
                            b.charAt(0) == '1' ? true : false,
                            b.charAt(1) == '1' ? true : false,
                            b.charAt(2) == '1' ? true : false,
                            b.charAt(3) == '1' ? true : false,
                            b.charAt(4) == '1' ? true : false,
                            b.charAt(5) == '1' ? true : false,
                            b.charAt(6) == '1' ? true : false,
                            b.charAt(7) == '1' ? true : false);
            //expected failures
            if (b.charAt(6) == '1' && b.charAt(7) == '1') {
                System.out.println(" " + success + " expected false");
                if (success) {
                    failingcombos.add(b);
                }
                ok = ok && !success;
            } else if (b.endsWith("11")
                    || b.equals("00110010")
                    || //
                    b.equals("00110000")
                    || b.equals("00110001")
                    || b.equals("00100010")
                    || b.equals("00100001")
                    || b.equals("00000000")
                    || //nothing defined
                    b.equals("00100000")
                    || //only an offset
                    b.equals("00000010")
                    || //
                    b.equals("00000001")
                    || //only an offset
                    b.equals("00010000")
                    || //limit
                    b.equals("00010001")
                    || //limit and slafaults
                    b.equals("00010010") //limit and faults
                    //00010001
                    ) {
                System.out.println(" " + success + " expected false");
                if (success) {
                    failingcombos.add(b);
                }
                ok = ok && !success;
            } else {
                System.out.println(" " + success + " expected true");
                if (!success) {
                    failingcombos.add(b);
                }
                ok = ok && success;
            }
        }

        RemoveTransaction(id);
        assertTrue(FriendListToString(failingcombos), ok);
    }

    public static String FriendListToString(List<String> failingcombos) {
        if (failingcombos == null || failingcombos.isEmpty()) {
            return "";
        }
        String s = "";
        for (int i = 0; i < failingcombos.size(); i++) {
            s += failingcombos.get(i) + ", ";

        }
        return s;
    }

    //11111000 agent url monitor
    //1                             2                           3                       4                           5                           6                           7                               8
    public boolean getRecentMessageLogs(boolean agent, boolean urld, boolean offset, boolean limit, boolean monitor, boolean hostname, boolean faults, boolean slafaults) throws DatatypeConfigurationException {
        boolean success = true;

        GetRecentMessageLogsRequestMsg request = new GetRecentMessageLogsRequestMsg();
        request.setClassification(new SecurityWrapper());
        if (agent) {
            request.setAgentType("junit");
        }
        if (faults) {
            request.setFaultsOnly(true);
        }
        if (slafaults) {
            request.setSlaViolationsOnly(true);
        }
        if (monitor) {
            request.setMonitorhostname(Utility.getHostName());
        }
        if (hostname) {
            request.setServicehostname(Utility.getHostName());
        }
        if (urld) {
            request.setURL(url);
        }
        if (offset) {
            request.setOffset(0);
        }
        if (limit) {
            request.setRecords(5);
        }
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetMessageLogsResponseMsg result = instance.getRecentMessageLogs(request);
            if (result == null) {
                success = false;
            }
            if (result.getClassification() == null) {
                success = false;
            }
            if (result.getLogs() == null) {
                success = false;
            }
            if (result.getLogs() == null) {
                success = false;
            }

        } catch (Exception ex) {
            success = false;
            //ex.printStackTrace();
            //    fail("unexpected failure");
        }
        return success;
    }

    /**
     * Test of getMessageLogsByRange method, of class DAS4jBean.
     */
    @Test
    public void testGetMessageLogsByRangeNullMsg() throws Exception {
        System.out.println("testGetMessageLogsByRangeNullMsg");
        GetMessageLogsRequestMsg request = null;
        DAS4jBean instance = new DAS4jBean(adminctx);

        try {
            GetMessageLogsResponseMsg result = instance.getMessageLogsByRange(request);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of getQuickStats method, of class DAS4jBean.
     */
    @Test
    public void testGetQuickStats() throws Exception {
        System.out.println("getQuickStats");
        GetQuickStatsRequestMsg getQuickStatsRequest = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetQuickStatsResponseMsg result = instance.getQuickStats(getQuickStatsRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetQuickStatsNoClass() throws Exception {
        System.out.println("getQuickStats");
        GetQuickStatsRequestMsg getQuickStatsRequest = new GetQuickStatsRequestMsg();
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetQuickStatsResponseMsg result = instance.getQuickStats(getQuickStatsRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testgetMessageLogsByRange() throws Exception {
        System.out.println("testgetMessageLogsByRange");
        String id = Insert_WS_Transaction(url, agent);
        List<String> failingcombos = new ArrayList<String>();
        boolean ok = true;
        TimeRange r = new TimeRange();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        DatatypeFactory f = DatatypeFactory.newInstance();
        r.setEnd((gcal));
        gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        gcal.add(Calendar.DATE, -1);
        r.setStart((gcal));

        for (int i = 0; i < 256; i++) {
            String b = Integer.toBinaryString(i);
            //pad it
            while (b.length() < 8) {
                b = "0" + b;
            }
            System.out.print("Test " + b);

            boolean success
                    = getMessageLogsByRange(
                            b.charAt(0) == '1' ? true : false,
                            b.charAt(1) == '1' ? true : false,
                            b.charAt(2) == '1' ? true : false,
                            b.charAt(3) == '1' ? true : false,
                            b.charAt(4) == '1' ? true : false,
                            b.charAt(5) == '1' ? true : false,
                            b.charAt(6) == '1' ? true : false,
                            b.charAt(7) == '1' ? true : false, r);
            //expected failures
            if (b.charAt(6) == '1' && b.charAt(7) == '1') {
                System.out.println(" " + success + " expected false");
                if (success) {
                    failingcombos.add(b);
                }
                ok = ok && !success;
            } else if (b.endsWith("11")
                    || b.equals("00110010")
                    || //
                    b.equals("00110000")
                    || b.equals("00110001")
                    || b.equals("00100010")
                    || b.equals("00100001")
                    || b.equals("00000000")
                    || //nothing defined
                    b.equals("00100000")
                    || //only an offset
                    b.equals("00000010")
                    || //
                    b.equals("00000001")
                    || //only an offset
                    b.equals("00010000")
                    || //limit
                    b.equals("00010001")
                    || //limit and slafaults
                    b.equals("00010010") //limit and faults
                    //00010001
                    ) {
                System.out.println(" " + success + " expected false");
                if (success) {
                    failingcombos.add(b);
                }
                ok = ok && !success;
            } else {
                System.out.println(" " + success + " expected true");
                if (!success) {
                    failingcombos.add(b);
                }
                ok = ok && success;
            }
        }

        RemoveTransaction(id);
        assertTrue(FriendListToString(failingcombos), ok);
    }
    //00000100

    public boolean getMessageLogsByRange(boolean agent, boolean urld, boolean offset, boolean limit, boolean monitor, boolean hostname, boolean faults, boolean slafaults, TimeRange r) throws DatatypeConfigurationException {
        boolean success = true;

        GetMessageLogsRequestMsg request = new GetMessageLogsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setRange(r);
        if (agent) {
            request.setAgentType("junit");
        }
        if (faults) {
            request.setFaultsOnly(true);
        }
        if (slafaults) {
            request.setSlaViolationsOnly(true);
        }
        if (monitor) {
            request.setMonitorhostname(Utility.getHostName());
        }
        if (hostname) {
            request.setServicehostname(Utility.getHostName());
        }
        if (urld) {
            request.setURL(url);
        }
        if (offset) {
            request.setOffset(0);
        }
        if (limit) {
            request.setRecords(5);
        }
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetMessageLogsResponseMsg result = instance.getMessageLogsByRange(request);
            if (result == null) {
                success = false;
            }
            if (result.getClassification() == null) {
                success = false;
            }
            if (result.getLogs() == null) {
                success = false;
            }
            if (result.getLogs() == null) {
                success = false;
            }

        } catch (Exception ex) {
            success = false;
            //ex.printStackTrace();
            //    fail("unexpected failure");
        }
        return success;
    }

    @Test
    public void testGetQuickStatValid() throws Exception {
        System.out.println("testGetQuickStatValid");
        GetQuickStatsRequestMsg getQuickStatsRequest = new GetQuickStatsRequestMsg();
        getQuickStatsRequest.setClassification(new SecurityWrapper());
        getQuickStatsRequest.setUri(url);
        CreatePolicy(url);
        InsertAgg(url);
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetQuickStatsResponseMsg result = instance.getQuickStats(getQuickStatsRequest);
            RemoveAgg(url);
            RemoveServicePolicy(url);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getUri());
            assertNotNull(result.getQuickStatWrapper());
            for (int i = 0; i < result.getQuickStatWrapper().size(); i++) {
                assertNotNull(result.getQuickStatWrapper().get(i).getAction());
                //assertNotNull(result.getQuickStatWrapper().get(i).getUptime());
                assertFalse(result.getQuickStatWrapper().get(i).getQuickStatData().isEmpty());
                for (int k = 0; k < result.getQuickStatWrapper().get(i).getQuickStatData().size(); k++) {
                    assertNotNull(result.getQuickStatWrapper().get(i).getQuickStatData().get(k));
                    assertNotNull(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getUpdatedAt());
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getUpdatedAt().getTimeInMillis() <= System.currentTimeMillis());
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getAvailabilityPercentage() >= 0);
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getAverageResponseTime() >= 0);
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getFailureCount() >= 0);
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getMaximumRequestSize() >= 0);
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getMaximumResponseSize() >= 0);
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getMaximumResponseTime() >= 0);
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getSLAViolationCount() >= 0);
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getSuccessCount() >= 0);
                    assertTrue(result.getQuickStatWrapper().get(i).getQuickStatData().get(k).getTimeInMinutes().intValue() >= 0);
                }
            }

        } catch (Exception ex) {
            RemoveAgg(url);
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    /**
     * Test of getQuickStatsAll method, of class DAS4jBean.
     */
    @Test
    public void testGetQuickStatsAll() throws Exception {
        System.out.println("getQuickStatsAll");
        GetQuickStatsAllRequestMsg getQuickStatsAllRequest = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetQuickStatsAllResponseMsg result = instance.getQuickStatsAll(getQuickStatsAllRequest);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testgetAuditLogsByTimeRange() throws Exception {
        System.out.println("testgetAuditLogsByTimeRange");
        GetAuditLogsByTimeRangeRequestMsg r = null;
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAuditLogsByTimeRangeResponseMsg result = instance.getAuditLogsByTimeRange(r);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testgetAuditLogsByTimeRangeC() throws Exception {
        System.out.println("testgetAuditLogsByTimeRangeC");
        GetAuditLogsByTimeRangeRequestMsg r = new GetAuditLogsByTimeRangeRequestMsg();
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAuditLogsByTimeRangeResponseMsg result = instance.getAuditLogsByTimeRange(r);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetAuditLogsByTimeRangeInValid() throws Exception {
        System.out.println("testgetAuditLogsByTimeRangeInValid");
        GetAuditLogsByTimeRangeRequestMsg r = new GetAuditLogsByTimeRangeRequestMsg();
        r.setClassification(new SecurityWrapper());
        DAS4jBean instance = new DAS4jBean(adminctx);

        GetAuditLogsByTimeRangeResponseMsg result = instance.getAuditLogsByTimeRange(r);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetAuditLogsByTimeRangeValid2() throws Exception {
        System.out.println("testgetAuditLogsByTimeRangeValid2");
        GetAuditLogsByTimeRangeRequestMsg r = new GetAuditLogsByTimeRangeRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setRange(new TimeRange());
        DAS4jBean instance = new DAS4jBean(adminctx);

        GetAuditLogsByTimeRangeResponseMsg result = instance.getAuditLogsByTimeRange(r);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetAuditLogsByTimeRangeInValid3() throws Exception {
        System.out.println("testgetAuditLogsByTimeRangeInValid3");
        GetAuditLogsByTimeRangeRequestMsg r = new GetAuditLogsByTimeRangeRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setRange(new TimeRange());
        r.setOffset(0);
        r.setRecordcount(1);
        DAS4jBean instance = new DAS4jBean(adminctx);

        GetAuditLogsByTimeRangeResponseMsg result = instance.getAuditLogsByTimeRange(r);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testgetAuditLogsByTimeRangeInValid4() throws Exception {
        System.out.println("testgetAuditLogsByTimeRangeInValid4");
        GetAuditLogsByTimeRangeRequestMsg r = new GetAuditLogsByTimeRangeRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setRange(new TimeRange());
        r.setOffset(0);
        r.setRecordcount(1);
        DAS4jBean instance = new DAS4jBean(adminctx);
        GetAuditLogsByTimeRangeResponseMsg result = instance.getAuditLogsByTimeRange(r);

    }

    @Test
    public void testgetAuditLogsByTimeRangeValid5() throws Exception {
        System.out.println("testgetAuditLogsByTimeRangeValid5");
        GetAuditLogsByTimeRangeRequestMsg r = new GetAuditLogsByTimeRangeRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setRange(new TimeRange());
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        DatatypeFactory f = DatatypeFactory.newInstance();

        r.getRange().setEnd((gcal));

        gcal = new GregorianCalendar();
        gcal.add(Calendar.DATE, -1);
        r.getRange().setStart((gcal));

        r.setOffset(0);
        r.setRecordcount(1);
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAuditLogsByTimeRangeResponseMsg result = instance.getAuditLogsByTimeRange(r);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertFalse(result.getAuditLog().isEmpty());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @Test
    public void testInsertTwoTransactionsAskForOne() throws Exception {
        System.out.println("testInsertTwoTransactionsAskForOne");
        RemoveServicePolicy(url);
        CreatePolicy(url);
        Thread.sleep(1000);
        String s = InsertTransactionWithoutRemovingAllPreviousRecords();
        String s2 = InsertTransactionWithoutRemovingAllPreviousRecords();
        DAS4jBean instance = new DAS4jBean(adminctx);
        GetRecentMessageLogsRequestMsg req = new GetRecentMessageLogsRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(url);
        req.setOffset(0);
        req.setRecords(1);
        try {
            GetMessageLogsResponseMsg recentMessageLogs = instance.getRecentMessageLogs(req);
            RemoveTransaction(s);
            RemoveTransaction(s2);
            RemoveServicePolicy(url);

            assertNotNull(recentMessageLogs);
            assertNotNull(recentMessageLogs.getClassification());
            assertEquals(2, recentMessageLogs.getTotalRecords());
            assertNotNull(recentMessageLogs.getLogs());
            assertNotNull(recentMessageLogs.getLogs());
            assertNotNull(recentMessageLogs.getLogs().getTransactionLog());
            assertEquals(recentMessageLogs.getLogs().getTransactionLog().size(), 1);
            boolean found1 = false;
            boolean found2 = false;
            for (int i = 0; i < recentMessageLogs.getLogs().getTransactionLog().size(); i++) {
                if (s.equalsIgnoreCase(recentMessageLogs.getLogs().getTransactionLog().get(i).getTransactionId())) {
                    found1 = true;
                }
                if (s2.equalsIgnoreCase(recentMessageLogs.getLogs().getTransactionLog().get(i).getTransactionId())) {
                    found2 = true;
                }
            }
            assertTrue(found1 || found2);

        } catch (Exception ex) {
            RemoveTransaction(s);
            RemoveTransaction(s2);
            RemoveServicePolicy(url);
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @Test
    public void testGetQuickStatsAllNullclass() throws Exception {
        System.out.println("testGetQuickStatsAllNullclass");
        GetQuickStatsAllRequestMsg getQuickStatsAllRequest = new GetQuickStatsAllRequestMsg();
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetQuickStatsAllResponseMsg result = instance.getQuickStatsAll(getQuickStatsAllRequest);
            fail("unexpected success");

        } catch (Exception ex) {
        }
    }

    @Test
    public void deleteServiceDependencyNull() throws Exception {
        System.out.println("deleteServiceDependencyNull");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            instance.deleteServiceDependency(null);
            fail("unexpected success");

        } catch (Exception ex) {
        }
    }

    @Test
    public void deleteServiceDependencyNullClass() throws Exception {
        System.out.println("deleteServiceDependencyNullClass");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            instance.deleteServiceDependency(new DeleteServiceDependencyRequestMsg());
            fail("unexpected success");

        } catch (Exception ex) {
        }
    }

    @Test
    public void deleteServiceDependencyValid() throws Exception {
        System.out.println("deleteServiceDependencyValid");
        DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
        RemoveServicePolicy(url);
        RemoveServicePolicy(url + "Dependent");

        CreatePolicy(url);
        CreatePolicy(url + "Dependent");
        InsertDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            DeleteServiceDependencyRequestMsg r = new DeleteServiceDependencyRequestMsg();
            r.setClassification(new SecurityWrapper());
            r.setUri(url);
            r.setDependenturi((url + "Dependent"));
            r.setAction("SRCACTION");
            r.setDependentaction("DESTACTION");
            instance.deleteServiceDependency(r);
            boolean ok = ConfirmDependentDeleted(url, "SRCACTION", url + "Dependent", "DESTACTION");
            if (!ok) {
                DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
                RemoveServicePolicy(url);
                RemoveServicePolicy(url + "Dependent");
                fail("unexpected failure");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
            RemoveServicePolicy(url);
            RemoveServicePolicy(url + "Dependent");
            fail("unexpected failure");
        }

    }

    @Test
    public void deleteServiceDependencyInValidURL() throws Exception {
        System.out.println("deleteServiceDependencyInValidURL");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            DeleteServiceDependencyRequestMsg r = new DeleteServiceDependencyRequestMsg();
            r.setClassification(new SecurityWrapper());
            r.setUri(url);
            //item to delete not defined
            instance.deleteServiceDependency(r);
            fail("unexpected success");

        } catch (Exception ex) {
        }
    }

    @Test
    public void deleteServiceDependencyAccessDenied() throws Exception {
        System.out.println("deleteServiceDependencyAccessDenied");
        RemoveServicePolicy(url);
        RemoveServicePolicy(url + "Dependent");
        CreatePolicy(url);
        CreatePolicy(url + "Dependent");
        InsertDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");

        DAS4jBean instance = new DAS4jBean(userbobctx);
        try {
            DeleteServiceDependencyRequestMsg r = new DeleteServiceDependencyRequestMsg();
            r.setClassification(new SecurityWrapper());
            r.setUri(url);
            r.setDependenturi(url + "Dependent");
            r.setAction("SRCACTION");
            r.setDependentaction("DESTACTION");
            instance.deleteServiceDependency(r);
            RemoveServicePolicy(url);
            RemoveServicePolicy(url + "Dependent");

            fail("unexpected success");

        } catch (Exception ex) {
            DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
            RemoveServicePolicy(url);
            RemoveServicePolicy(url + "Dependent");
        }

    }

    @Test
    public void getServiceDependenciesNull() throws Exception {
        System.out.println("getServiceDependenciesNull");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            instance.getServiceDependencies(null);
            fail("unexpected success");

        } catch (Exception ex) {
        }
    }

    @Test
    public void getServiceDependenciesNullClass() throws Exception {
        System.out.println("getServiceDependenciesNullClass");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            instance.getServiceDependencies(new GetServiceDependenciesRequestMsg());
            fail("unexpected success");

        } catch (Exception ex) {
        }
    }

    @Test
    public void getServiceDependenciesValid() throws Exception {
        System.out.println("getServiceDependenciesValid");
        CreatePolicy(url);
        CreatePolicy(url + "Dependent");
        InsertDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");

        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetServiceDependenciesRequestMsg r = new GetServiceDependenciesRequestMsg();
            r.setClassification(new SecurityWrapper());
            r.setUri(url);
            GetServiceDependenciesResponseMsg serviceDependencies = instance.getServiceDependencies(r);
            DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
            RemoveServicePolicy(url);
            RemoveServicePolicy(url + "Dependent");

            assertTrue(serviceDependencies.getDependonme().isEmpty());
            assertFalse(serviceDependencies.getIdependon().isEmpty());
            assertNotNull(serviceDependencies.getClassification());

            CreatePolicy(url);
            CreatePolicy(url + "Dependent");
            InsertDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");

            r = new GetServiceDependenciesRequestMsg();
            r.setClassification(new SecurityWrapper());
            r.setUri(url + "Dependent");
            serviceDependencies = instance.getServiceDependencies(r);
            DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
            RemoveServicePolicy(url);
            RemoveServicePolicy(url + "Dependent");
            assertFalse(serviceDependencies.getDependonme().isEmpty());
            assertTrue(serviceDependencies.getIdependon().isEmpty());
            assertNotNull(serviceDependencies.getClassification());

        } catch (Exception ex) {
            ex.printStackTrace();
            DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
            RemoveServicePolicy(url);
            RemoveServicePolicy(url + "Dependent");
            fail("unexpected failure");
        }
    }

    @Test
    public void getServiceDependenciesInValidURLServiceDoesNotExist() throws Exception {
        System.out.println("getServiceDependenciesInValidURL");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetServiceDependenciesRequestMsg r = new GetServiceDependenciesRequestMsg();
            r.setClassification(new SecurityWrapper());
            r.setUri("http://somemadeupurl/service");
            GetServiceDependenciesResponseMsg serviceDependencies = instance.getServiceDependencies(r);
            assertTrue(serviceDependencies.getDependonme().isEmpty());
            assertTrue(serviceDependencies.getIdependon().isEmpty());
        } catch (Exception ex) {
            fail("unexpected failure");
        }
    }

    @Test
    public void getServiceDependenciesAccessDenied() throws Exception {
        System.out.println("getServiceDependenciesAccessDenied");

        RemoveServicePolicy(url);
        RemoveServicePolicy(url + "Dependent");
        CreatePolicy(url);
        CreatePolicy(url + "Dependent");
        InsertDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");

        DAS4jBean instance = new DAS4jBean(userbobctx);
        try {
            GetServiceDependenciesRequestMsg r = new GetServiceDependenciesRequestMsg();
            r.setClassification(new SecurityWrapper());
            r.setUri(url);
            GetServiceDependenciesResponseMsg serviceDependencies = instance.getServiceDependencies(r);
            RemoveServicePolicy(url);
            RemoveServicePolicy(url + "Dependent");
            DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
            fail("unexpected success");

        } catch (Exception ex) {

            RemoveServicePolicy(url);
            RemoveServicePolicy(url + "Dependent");
            DeleteDependent(url, "SRCACTION", url + "Dependent", "DESTACTION");
        }

    }

    @Test
    public void getAllServiceDependenciesNull() throws Exception {
        System.out.println("getAllServiceDependenciesNull");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            instance.getAllServiceDependencies(null);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void getAllServiceDependenciesNullClass() throws Exception {
        System.out.println("getAllServiceDependenciesNullClass");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            instance.getAllServiceDependencies(new GetAllServiceDependenciesRequestMsg());
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @Test
    public void getAllServiceDependenciesValid() throws Exception {
        System.out.println("getAllServiceDependenciesValid");
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetAllServiceDependenciesRequestMsg req = new GetAllServiceDependenciesRequestMsg();
            req.setClassification(new SecurityWrapper());
            GetAllServiceDependenciesResponseMsg allServiceDependencies = instance.getAllServiceDependencies(req);
            assertNotNull(allServiceDependencies.getClassification());
            assertNotNull(allServiceDependencies.getClassification());
            assertNotNull(allServiceDependencies.getDependencies());
            for (int i = 0; i < allServiceDependencies.getDependencies().size(); i++) {
                assertNotNull(allServiceDependencies.getDependencies().get(i).getDestinationaction());
                assertNotNull(allServiceDependencies.getDependencies().get(i).getDestinationuri());
                assertNotNull(allServiceDependencies.getDependencies().get(i).getSourceaction());
                assertNotNull(allServiceDependencies.getDependencies().get(i).getSourceuri());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAllServiceDependenciesAccessDenied() throws Exception {
        System.out.println("getAllServiceDependenciesAccessDenied");

        DAS4jBean instance = new DAS4jBean(userbobctx);

        GetAllServiceDependenciesRequestMsg req = new GetAllServiceDependenciesRequestMsg();
        req.setClassification(new SecurityWrapper());
        GetAllServiceDependenciesResponseMsg allServiceDependencies = instance.getAllServiceDependencies(new GetAllServiceDependenciesRequestMsg());

    }

    @Test
    public void testGetQuickStatsAllvalid() throws Exception {
        System.out.println("getQuickStatsAll");
        GetQuickStatsAllRequestMsg getQuickStatsAllRequest = new GetQuickStatsAllRequestMsg();
        getQuickStatsAllRequest.setClassification(new SecurityWrapper());
        CreatePolicy(url);
        InsertAgg(url);
        DAS4jBean instance = new DAS4jBean(adminctx);
        try {
            GetQuickStatsAllResponseMsg result = instance.getQuickStatsAll(getQuickStatsAllRequest);
            RemoveAgg(url);
            RemoveServicePolicy(url);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getQuickStatURIWrapper());
            for (int i = 0; i < result.getQuickStatURIWrapper().size(); i++) {
                assertNotNull(result.getQuickStatURIWrapper().get(i).getUri());
                for (int k = 0; k < result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().size(); k++) {
                    assertNotNull(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k));
                    assertNotNull(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getAction());
                    assertNotNull(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData());
                    for (int j = 0; j < result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().size(); j++) {
                        assertNotNull(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j));
                        assertNotNull(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getUpdatedAt());
                        assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getUpdatedAt().getTimeInMillis() <= System.currentTimeMillis());
                        assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getAvailabilityPercentage() >= 0);

                        /*
                               * System.out.println(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getAverageResponseTime());
                               * assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getAverageResponseTime()
                               * >= 0);
                               * assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getFailureCount()
                               * >= 0);
                               *
                               * //assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(i).getMTBF()
                               * >= 0);
                               * assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getMaximumRequestSize()
                               * >= 0);
                               * assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getMaximumResponseSize()
                               * >= 0);
                               * assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getSLAViolationCount()
                               * >= 0);
                               * assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getMaximumResponseTime()
                               * >= 0);
                               * assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getSucessCount()
                               * >= 0);
                         */
                        assertTrue(result.getQuickStatURIWrapper().get(i).getQuickStatWrapper().get(k).getQuickStatData().get(j).getTimeInMinutes().intValue() >= 0);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");

        }
    }

    private String InsertTransactionWithoutRemovingAllPreviousRecords() throws Exception {
        Connection con = Utility.getPerformanceDBConnection();

        PreparedStatement cmd = con.prepareStatement("INSERT INTO rawdata(        "
                + "    uri, responsetimems, monitorsource, hostingsource, requestxml, " //1-5
                + "            responsexml, consumeridentity, transactionid, soapaction, responsesize, " //6-10
                + "            requestsize, utcdatetime, success, slafault, agenttype, originalurl, " //11-16
                + "            message, relatedtransactionid, threadid, requestheaders, responseheaders)" //17-21
                + "    VALUES (?, ?, ?, ?, ?, "
                + "            ?, ?, ?, ?, ?, "
                + "            ?, ?, ?, ?, ?, ?, "
                + "            ?, ?, ?, ?, ?);");
        cmd.setString(1, url);
        String id = UUID.randomUUID().toString();
        cmd.setInt(2, 1000);
        cmd.setString(3, Utility.getHostName());
        cmd.setString(4, Utility.getHostName());
        cmd.setObject(5, Utility.EN(new String("<body>helloworld</body>")).getBytes());
        cmd.setObject(6, Utility.EN(new String("<body>helloworld response</body>")).getBytes());
        cmd.setString(7, System.getenv("USERNAME"));
        cmd.setString(8, id);
        cmd.setString(9, "POST");
        cmd.setInt(10, 1024);
        cmd.setInt(11, 1024);
        cmd.setLong(12, System.currentTimeMillis());
        cmd.setBoolean(13, false);
        cmd.setString(14, "sla fault!");
        cmd.setString(15, agent);
        cmd.setString(16, url);
        cmd.setObject(17, "hello from junit".getBytes());
        cmd.setString(18, UUID.randomUUID().toString());
        cmd.setString(19, UUID.randomUUID().toString());
        cmd.setObject(20, Utility.EN(new String("SOAPAction: something")).getBytes());
        cmd.setObject(21, Utility.EN(new String("Response 200 OK")).getBytes());
        cmd.execute();
        cmd.close();
        con.close();
        return id;
    }

    public static void InsertAgg(String url) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        try {

            PreparedStatement cmd = con.prepareStatement("INSERT INTO agg2("
                    + "            success, failure, avgres, "
                    + "            mtbf, maxreq, maxres, "
                    + "            maxresponsetime,  " + "uri, soapaction, timerange, timestampepoch, avail)"
                    + "    VALUES (?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, "
                    + "?,?"
                    + "            );");
            cmd.setLong(1, 100);
            cmd.setLong(2, 100);
            cmd.setLong(3, 100);
            cmd.setLong(4, 100);
            cmd.setLong(5, 100);

            cmd.setLong(6, 100);
            cmd.setLong(7, 100);
            cmd.setString(8, url);
            cmd.setString(9, "All-Methods");
            cmd.setLong(10, 5 * 60 * 1000);
            cmd.setLong(11, System.currentTimeMillis());
            cmd.setDouble(12, 100);
            cmd.execute();
            cmd.close();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            con.close();
        } catch (Exception e) {
        }
    }

    public static void InsertAgg(String url, long period) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        try {

            PreparedStatement cmd = con.prepareStatement("INSERT INTO agg2("
                    + "            success, failure, avgres, "
                    + "            mtbf, maxreq, maxres,"
                    + "            maxresponsetime,  " + "uri, soapaction, timerange, timestampepoch, avail)"
                    + "    VALUES (?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?,?"
                    + "            );");
            cmd.setLong(1, 100);
            cmd.setLong(2, 100);
            cmd.setLong(3, 100);
            cmd.setLong(4, 100);
            cmd.setLong(5, 100);

            cmd.setLong(6, 100);
            cmd.setLong(7, 100);
            cmd.setString(8, url);
            cmd.setString(9, "All-Methods");
            cmd.setLong(10, period);
            cmd.setLong(11, System.currentTimeMillis());
            cmd.setDouble(12, 100);

            cmd.execute();
            cmd.close();
            con.close();
        } catch (Exception ex) {
        }
        try {
            con.close();
        } catch (Exception e) {
        }
    }

    public static void RemoveAgg(String url) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = con.prepareStatement("delete from agg2 where uri=?; ");

        cmd.setString(1, url);

        cmd.execute();
        cmd.close();
        con.close();
    }

    /**
     * inserts one simulated record per day
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static int InsertOperationalRecords(String url) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = null;

        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        for (int i = 0; i < 10; i++) {
            cmd = con.prepareStatement("INSERT INTO availability("
                    + "            uri, utcdatetime, id, message, status)"
                    + "    VALUES (?, ?, ?, ?, ?);");

            cmd.setString(1, url);
            cmd.setLong(2, gcal.getTimeInMillis());
            cmd.setString(3, UUID.randomUUID().toString());
            cmd.setString(4, "junit");
            cmd.setBoolean(5, false);
            cmd.execute();
            cmd.close();
            gcal.add(Calendar.DATE, -1);

        }
        con.close();
        return 10;
    }

    public static void RemoveOperationalRecords(String url) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = null;

        cmd = con.prepareStatement("Delete from availability where uri=?");
        cmd.setString(1, url);
        cmd.execute();
        cmd.close();
        con.close();

    }

    public static void InsertServicePolicyAndActionList(String url) throws SQLException {
        CreatePolicy(url);
        InsertActions(url);
    }

    public static void RemoveServicePolicyAndActionList(String url) throws SQLException {
        RemoveServicePolicy(url);
        RemoveActionList(url);
    }

    public static void InsertActions(String url) throws SQLException {
        RemoveActionList(url);
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = com = con.prepareStatement("   INSERT INTO actionlist(           uri, soapaction)    VALUES (?, ?);");
        com.setString(1, url);
        com.setString(2, "POST");
        com.execute();
        com.close();
        con.close();

    }

    public static void RemoveActionList(String url) throws SQLException {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = con.prepareStatement("delete from actionlist where uri=?;");
        com.setString(1, url);
        com.execute();
        com.close();
        con.close();
    }

    private void InsertDependent(String url, String action1, String url2, String action2) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = con.prepareStatement("INSERT INTO dependencies(sourceurl,sourcesoapaction, destinationurl, destinationsoapaction )    VALUES (?, ?, ?, ?);");
        com.setString(1, url);
        com.setString(2, action1);
        com.setString(3, url2);
        com.setString(4, action2);
        com.execute();
        com.close();
        con.close();

    }

    private boolean ConfirmDependentDeleted(String url, String action1, String url2, String action2) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = con.prepareStatement("select * from dependencies where sourceurl=? and sourcesoapaction=? and destinationurl=? and destinationsoapaction=?    ;");
        com.setString(1, url);
        com.setString(2, action1);
        com.setString(3, url2);
        com.setString(4, action2);
        ResultSet rs = com.executeQuery();
        boolean ok = true;
        if (rs.next()) {
            ok = false;
        }
        rs.close();
        com.close();
        con.close();
        return ok;
    }

    private void DeleteDependent(String url, String action1, String url2, String action2) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = con.prepareStatement("delete from dependencies where sourceurl=? and sourcesoapaction=? and destinationurl=? and destinationsoapaction=?  ;");
        com.setString(1, url);
        com.setString(2, action1);
        com.setString(3, url2);
        com.setString(4, action2);
        com.execute();
        com.close();
        con.close();

    }
}
