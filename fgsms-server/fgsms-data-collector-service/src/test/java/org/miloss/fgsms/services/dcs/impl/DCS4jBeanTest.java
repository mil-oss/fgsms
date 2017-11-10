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
package org.miloss.fgsms.services.dcs.impl;

import org.miloss.fgsms.services.dcs.impl.DCS4jBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.Header;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.NameValuePairSet;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.*;
import org.miloss.fgsms.test.WebServiceBaseTests;
import static org.junit.Assert.*;
import org.junit.Test;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author AO
 */
public class DCS4jBeanTest extends WebServiceBaseTests {

    public DCS4jBeanTest() throws Exception {
        super();
        url = "http://localhost:8180/jUnitTestCaseDCS";
        brokerurl = IpAddressUtility.modifyURL(brokerurl, false);
        Init();


        url = IpAddressUtility.modifyURL(url, false);

    }
    static String brokerurl = "amqp://localhost/junitBrokerDCS";

    /**
     * Test of addData method, of class DCS4jBean.
     */
    @org.junit.Test
    public void testAddDataAsAdmin() throws Exception {
        System.out.println("testAddDataAsAdmin");
        AddDataRequestMsg req = new AddDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURI(url);

        DCS4jBean instance = new DCS4jBean(adminctx);
        try {
            AddDataResponseMsg result = instance.addData(req);
            RemoveTransactions(url);
            fail("unexpected success");
        } catch (Exception ex) {
        }

    }

    @org.junit.Test
    public void testAddDataAsBob() throws Exception {
        System.out.println("testAddDataNullClass");
        AddDataRequestMsg req = new AddDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURI(url);

        DCS4jBean instance = new DCS4jBean(userbobctx);
        try {
            AddDataResponseMsg result = instance.addData(req);
            RemoveTransactions(url);
            fail("unexpected success");
        } catch (Exception ex) {
        }

    }

    @org.junit.Test
    public void testAddDataNullClass() throws Exception {
        System.out.println("testAddDataNullClass");
        AddDataRequestMsg req = new AddDataRequestMsg();
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddDataResponseMsg result = instance.addData(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }

    }

    @org.junit.Test
    public void testAddDataNullMsg() throws Exception {
        System.out.println("testAddDataNullClass");
        AddDataRequestMsg req = new AddDataRequestMsg();
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddDataResponseMsg result = instance.addData(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testAddDataInValidMessageNullURL() throws Exception {
        System.out.println("testAddDataInValidMessage");
        AddDataRequestMsg req = new AddDataRequestMsg();
        req.setClassification(new SecurityWrapper());

        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            //req.setURI(url);
            req.setAction("POST");
            AddDataResponseMsg result = instance.addData(req);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getStatus());
            assertEquals(result.getStatus(), DataResponseStatus.SUCCESS);
            fail("unexpected succes");

        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testAddDataInValidPartialMessage() throws Exception {
        System.out.println("testAddDataInValidPartialMessage");
        AddDataRequestMsg req = new AddDataRequestMsg();
        req.setClassification(new SecurityWrapper());

        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            req.setURI(url);
            req.setAction("POST");
            AddDataResponseMsg result = instance.addData(req);
            RemoveTransactions(url);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getStatus());
            assertEquals(result.getStatus(), DataResponseStatus.SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure"); 

        }
    }

    @org.junit.Test
    public void testAddDataInValidPartialMessageNullAction() throws Exception {
        System.out.println("testAddDataInValidPartialMessage");
        AddDataRequestMsg req = new AddDataRequestMsg();
        req.setClassification(new SecurityWrapper());

        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            req.setURI(url);
            req.setAction("POST");
            AddDataResponseMsg result = instance.addData(req);
            RemoveTransactions(url);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getStatus());
            assertEquals(result.getStatus(), DataResponseStatus.SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");

        }
    }

    @org.junit.Test
    public void testAddDataValidPartialMessage() throws Exception {
        System.out.println("testAddDataInvalidMessage");
        AddDataRequestMsg req = new AddDataRequestMsg();
        req.setClassification(new SecurityWrapper());

        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            req.setURI(url);
            AddDataResponseMsg result = instance.addData(req);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getStatus());
            assertEquals(result.getStatus(), DataResponseStatus.SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");

        }
    }

    @org.junit.Test
    public void testAddDataValidPartialMessagePasswordMasking() throws Exception {
        System.out.println("testAddDataInvalidMessage");
        AddDataRequestMsg req = new AddDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        String id = UUID.randomUUID().toString();
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            req.setURI(url);
            req.setTransactionID(id);
            System.out.println("generating new message id " + id);
            Header h = new Header();
            h.setName("Authorization");
            h.getValue().add("password");
            req.getHeadersRequest().add(h);

            AddDataResponseMsg result = instance.addData(req);
            boolean ok = TestTransactionForPasswordRequestHeader(id);
            //RemoveTransaction(id);
            assertTrue(ok);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getStatus());
            assertEquals(result.getStatus(), DataResponseStatus.SUCCESS);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");

        }
    }

    /**
     * return true if successful
     *
     * @param id
     * @return
     * @throws Exception
     */
    private boolean TestTransactionForPasswordRequestHeader(String id) throws Exception {
        boolean ok = true;
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement cmd = con.prepareStatement("select * from rawdata where transactionid=?");
        cmd.setString(1, id);
        ResultSet rs = cmd.executeQuery();
        if (rs.next()) {
            byte[] by = rs.getBytes("requestheaders");
            String s = new String(by, "UTF-8");
            s = Utility.DE(s);
            String[] headers = s.split("\\|");
            for (int a = 0; a < headers.length; a++) {
                String[] s1 = headers[a].split("=");
                Header h = new Header();
                h.setName(s1[0]);
                if (s1.length == 2) {
                    String[] vs = s1[1].split(";");
                    if (vs != null && vs.length > 0) {
                        for (int b = 0; b < vs.length; b++) {
                            h.getValue().add(vs[b]);
                        }
                    }
                }
                if (h.getName().equalsIgnoreCase("Authorization") && h.getValue().get(0).equalsIgnoreCase("password")) {
                    ok = false;
                    System.out.println("Password stored.");
                }
            }
        } else {
            ok = false;
            System.out.println("Unable to find transaction");
        }
        cmd.close();
        con.close();
        return ok;
    }

    /**
     * Test of addMoreData method, of class DCS4jBean.
     */
    @org.junit.Test
    public void testAddMoreDataNullMsg() throws Exception {
        System.out.println("testAddMoreDataNullMsg");
        List<AddDataRequestMsg> req = null;
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddDataResponseMsg result = instance.addMoreData(req);

            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testAddMoreDataInValidMsgNullClass() throws Exception {
        System.out.println("testAddMoreDataInValidMsgNullClass");
        List<AddDataRequestMsg> req = new ArrayList<AddDataRequestMsg>();
        DCS4jBean instance = new DCS4jBean(agentctx);

        try {
            AddDataRequestMsg i = new AddDataRequestMsg();
            i.setURI(url);
            i.setAction("POST");
            req.add(i);
            AddDataResponseMsg result = instance.addMoreData(req);
            RemoveTransactions(url);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getStatus());
            assertEquals(result.getStatus(), DataResponseStatus.SUCCESS);
            fail("unexpected success");
        } catch (Exception ex) {
            RemoveTransactions(url);
        }
    }

    @org.junit.Test
    public void testAddMoreDataInValidMsg() throws Exception {
        System.out.println("testAddMoreDataInValidMsg");
        List<AddDataRequestMsg> req = new ArrayList<AddDataRequestMsg>();
        DCS4jBean instance = new DCS4jBean(agentctx);

        try {
            AddDataRequestMsg i = new AddDataRequestMsg();
            i.setURI(url);
            i.setAction("POST");
            i.setClassification(new SecurityWrapper());
            req.add(i);
            AddDataResponseMsg result = instance.addMoreData(req);
            RemoveTransactions(url);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getStatus());
            assertEquals(result.getStatus(), DataResponseStatus.SUCCESS);
        } catch (Exception ex) {
            ex.printStackTrace();
            RemoveTransactions(url);
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testAddMoreDataInValidMsgAsAdmin() throws Exception {
        System.out.println("testAddMoreDataInValidMsg");
        List<AddDataRequestMsg> req = new ArrayList<AddDataRequestMsg>();
        DCS4jBean instance = new DCS4jBean(adminctx);

        try {
            AddDataResponseMsg result = instance.addMoreData(req);
            RemoveTransactions(url);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testAddMoreDataInValidMsgAsBob() throws Exception {
        System.out.println("testAddMoreDataInValidMsg");
        List<AddDataRequestMsg> req = new ArrayList<AddDataRequestMsg>();
        DCS4jBean instance = new DCS4jBean(userbobctx);

        try {
            AddDataResponseMsg result = instance.addMoreData(req);
            RemoveTransactions(url);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    /**
     * Test of addStatisticalData method, of class DCS4jBean.
     */
    @org.junit.Test
    public void testAddStatisticalDataNullMsg() throws Exception {
        System.out.println("testAddStatisticalDataNullMsg");
        AddStatisticalDataRequestMsg req = null;
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddStatisticalDataResponseMsg result = instance.addStatisticalData(req);
            RemoveTransactions(url);
            fail("unexpected success");
        } catch (Exception ex) {
        }

    }

    @org.junit.Test
    public void testAddStatisticalDataNullClassMsg() throws Exception {
        System.out.println("testAddStatisticalDataNullClassMsg");
        AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddStatisticalDataResponseMsg result = instance.addStatisticalData(req);
            RemoveTransactions(url);
            fail("unexpected success");
        } catch (Exception ex) {
        }

    }

    @org.junit.Test
    public void testAddStatisticalDataInValidMsg() throws Exception {
        System.out.println("testAddStatisticalDataInValidMsg");
        AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddStatisticalDataResponseMsg result = instance.addStatisticalData(req);
            RemoveTransactions(url);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testAddStatisticalDataNullAgentType() throws Exception {
        System.out.println("testAddStatisticalDataInValidMsg");
        AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setBrokerURI(url);
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddStatisticalDataResponseMsg result = instance.addStatisticalData(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testAddStatisticalDataValidMessagesSingleItemAsUser() throws Exception {
        System.out.println("testAddStatisticalDataValidMessagesSingleItemAsUser");
        RemoveServicePolicy(brokerurl);
        AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setBrokerURI(brokerurl);
        req.setAgentType("junitAgent");
        BrokerData bd = new BrokerData();
        bd.setActiveConsumers(1);
        bd.setTotalConsumers(2);
        bd.setBytesDropped(0);
        bd.setBytesIn(500);
        bd.setBytesOut(500);
        bd.setDepth(0);
        bd.setItemType("queue");
        bd.setQueueOrTopicCanonicalName("junit.agent.test.queue.name");
        bd.setQueueOrTopicName("queue");

        req.getData().add(bd);
        DCS4jBean instance = new DCS4jBean(userbobctx);
        try {
            AddStatisticalDataResponseMsg result = instance.addStatisticalData(req);
            RemoveServicePolicy(brokerurl);
            fail("unexpected success");
        } catch (Exception ex) {
        }
        RemoveServicePolicy(brokerurl);

    }

    @org.junit.Test
    public void testAddStatisticalDataValidMessagesSingleItemAsAdmin() throws Exception {
        System.out.println("testAddStatisticalDataValidMessagesSingleItemAsAdmin");
        RemoveServicePolicy(brokerurl);
        AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setBrokerURI(brokerurl);
        req.setAgentType("junitAgent");
        BrokerData bd = new BrokerData();
        bd.setActiveConsumers(1);
        bd.setTotalConsumers(2);
        bd.setBytesDropped(0);
        bd.setBytesIn(500);
        bd.setBytesOut(500);
        bd.setDepth(0);
        bd.setItemType("queue");
        bd.setQueueOrTopicCanonicalName("junit.agent.test.queue.name");
        bd.setQueueOrTopicName("queue");

        req.getData().add(bd);
        DCS4jBean instance = new DCS4jBean(adminctx);
        try {
            AddStatisticalDataResponseMsg result = instance.addStatisticalData(req);
            RemoveServicePolicy(brokerurl);
            fail("unexpected success");
        } catch (Exception ex) {
        }
        RemoveServicePolicy(brokerurl);
    }

    @org.junit.Test
    public void testAddStatisticalDataValidMessagesSingleItem() throws Exception {
        System.out.println("testAddStatisticalDataValidMessagesSingleItem");
        RemoveServicePolicy(brokerurl);
        AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setBrokerURI(brokerurl);
        req.setAgentType("junitAgent");
        BrokerData bd = new BrokerData();
        bd.setActiveConsumers(1);
        bd.setTotalConsumers(2);
        bd.setBytesDropped(0);
        bd.setBytesIn(500);
        bd.setBytesOut(500);
        bd.setDepth(0);
        bd.setItemType("queue");
        bd.setQueueOrTopicCanonicalName("junit.agent.test.queue.name");
        bd.setQueueOrTopicName("queue");

        req.getData().add(bd);
        DCS4jBean instance = new DCS4jBean(agentctx);
        req.setBrokerHostname(Utility.getHostName());
        AddStatisticalDataResponseMsg result = instance.addStatisticalData(req);

        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertTrue(result.isSuccess());

        //check database for records in both tables
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement prepareStatement = con.prepareStatement("select count(*) from brokerhistory where host=?");
        prepareStatement.setString(1, brokerurl);
        ResultSet executeQuery = prepareStatement.executeQuery();
        executeQuery.next();
        int i = executeQuery.getInt(1);
        assertTrue(i == 1);
        executeQuery.close();
        prepareStatement.close();

        prepareStatement = con.prepareStatement("select count(*) from brokerrawdata where host=?");
        prepareStatement.setString(1, brokerurl);
        executeQuery = prepareStatement.executeQuery();
        executeQuery.next();
        i = executeQuery.getInt(1);
        assertTrue(i == 1);
        executeQuery.close();
        prepareStatement.close();



        //add a new set


        req = new AddStatisticalDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setBrokerURI(brokerurl);
        req.setAgentType("junitAgent");
        bd = new BrokerData();
        bd.setActiveConsumers(2);
        bd.setTotalConsumers(3);
        bd.setBytesDropped(1);
        bd.setBytesIn(501);
        bd.setBytesOut(500);
        bd.setDepth(1);
        bd.setItemType("queue");
        bd.setQueueOrTopicCanonicalName("junit.agent.test.queue.name");
        bd.setQueueOrTopicName("queue");

        req.getData().add(bd);
        req.setBrokerHostname(Utility.getHostName());
        instance.addStatisticalData(req);



        //check database again


        prepareStatement = con.prepareStatement("select count(*) from brokerhistory where host=?");
        prepareStatement.setString(1, brokerurl);
        executeQuery = prepareStatement.executeQuery();
        executeQuery.next();
        i = executeQuery.getInt(1);
        assertTrue(i == 2);
        executeQuery.close();
        prepareStatement.close();

        prepareStatement = con.prepareStatement("select count(*) from brokerrawdata where host=?");
        prepareStatement.setString(1, brokerurl);
        executeQuery = prepareStatement.executeQuery();
        executeQuery.next();
        i = executeQuery.getInt(1);
        assertTrue(i == 1);
        executeQuery.close();
        prepareStatement.close();




        con.close();
        RemoveServicePolicy(brokerurl);
    }

    @org.junit.Test
    public void testAddStatisticalDataValidMessagesTwoItemsDownToOneItem() throws Exception {
        System.out.println("testAddStatisticalDataValidMessagesTwoItemsDownToOneItem");
        RemoveServicePolicy(brokerurl);
        AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setBrokerURI(brokerurl);
        req.setAgentType("junitAgent");
        BrokerData bd = new BrokerData();
        bd.setActiveConsumers(1);
        bd.setTotalConsumers(2);
        bd.setBytesDropped(0);
        bd.setBytesIn(500);
        bd.setBytesOut(500);
        bd.setDepth(0);
        bd.setItemType("queue");
        bd.setQueueOrTopicCanonicalName("junit.agent.test.queue.name");
        bd.setQueueOrTopicName("queue");

        req.getData().add(bd);

        bd = new BrokerData();
        bd.setActiveConsumers(4);
        bd.setTotalConsumers(0);
        bd.setBytesDropped(0);
        bd.setBytesIn(400);
        bd.setBytesOut(400);
        bd.setDepth(0);
        bd.setItemType("topic");
        bd.setQueueOrTopicCanonicalName("junit.agent.test.topic.name");
        bd.setQueueOrTopicName("topic");
        req.getData().add(bd);
        DCS4jBean instance = new DCS4jBean(agentctx);
        req.setBrokerHostname(Utility.getHostName());
        AddStatisticalDataResponseMsg result = instance.addStatisticalData(req);

        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertTrue(result.isSuccess());

        //check database for records in both tables
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement prepareStatement = con.prepareStatement("select count(*) from brokerhistory where host=?");
        prepareStatement.setString(1, brokerurl);
        ResultSet executeQuery = prepareStatement.executeQuery();
        executeQuery.next();
        int i = executeQuery.getInt(1);
        assertTrue(i == 2);
        executeQuery.close();
        prepareStatement.close();

        prepareStatement = con.prepareStatement("select count(*) from brokerrawdata where host=?");
        prepareStatement.setString(1, brokerurl);
        executeQuery = prepareStatement.executeQuery();
        executeQuery.next();
        i = executeQuery.getInt(1);
        assertTrue(i == 2);
        executeQuery.close();
        prepareStatement.close();



        //add a new set


        req = new AddStatisticalDataRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setBrokerURI(brokerurl);
        req.setAgentType("junitAgent");
        bd = new BrokerData();
        bd.setActiveConsumers(2);
        bd.setTotalConsumers(3);
        bd.setBytesDropped(1);
        bd.setBytesIn(501);
        bd.setBytesOut(500);
        bd.setDepth(1);
        bd.setItemType("queue");
        bd.setQueueOrTopicCanonicalName("junit.agent.test.queue.name");
        bd.setQueueOrTopicName("queue");

        req.getData().add(bd);
        req.setBrokerHostname(Utility.getHostName());
        instance.addStatisticalData(req);



        //check database again


        prepareStatement = con.prepareStatement("select count(*) from brokerhistory where host=?");
        prepareStatement.setString(1, brokerurl);
        executeQuery = prepareStatement.executeQuery();
        executeQuery.next();
        i = executeQuery.getInt(1);
        assertTrue(i == 3);
        executeQuery.close();
        prepareStatement.close();

        prepareStatement = con.prepareStatement("select count(*) from brokerrawdata where host=?");
        prepareStatement.setString(1, brokerurl);
        executeQuery = prepareStatement.executeQuery();
        executeQuery.next();
        i = executeQuery.getInt(1);
        assertTrue(i == 1);
        executeQuery.close();
        prepareStatement.close();




        con.close();
        RemoveServicePolicy(brokerurl);
    }

    @org.junit.Test
    public void addMachineAndProcessDataTestNullMsg() throws Exception {
        System.out.println("addMachineAndProcessDataTestNullMsg");
        List<AddDataRequestMsg> req = null;
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddMachineAndProcessDataResponseMsg result = instance.addMachineAndProcessData(null);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void addMachineAndProcessDataTestEmptyMsg() throws Exception {
        System.out.println("addMachineAndProcessDataTestEmptyMsg");
        List<AddDataRequestMsg> req = null;
        DCS4jBean instance = new DCS4jBean(agentctx);
        try {
            AddMachineAndProcessDataResponseMsg result = instance.addMachineAndProcessData(new AddMachineAndProcessDataRequestMsg());
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void addMachineAndProcessDataTestNullClass() throws Exception {
        System.out.println("addMachineAndProcessDataTestNullClass");
        DCS4jBean instance = new DCS4jBean(agentctx);
        AddMachineAndProcessDataRequestMsg req = new AddMachineAndProcessDataRequestMsg();
        req.setAgentType("agent");
        req.setClassification(null);
        req.setDomainname("UNSPECIFIED");
        req.setHostname(Utility.getHostName());
        req.setMachineData(new MachinePerformanceData());
        req.getMachineData().setUri(url);
        req.setSensorData(null);
        req.getMachineData().setBytesusedMemory(null);
        req.getMachineData().setId(null);
        req.getMachineData().setNumberofActiveThreads(null);
        req.getMachineData().setOperationalstatus(true);
        req.getMachineData().setPercentusedCPU(null);
        req.getMachineData().setStartedAt(null);
        req.getMachineData().setTimestamp(null);
        try {
            AddMachineAndProcessDataResponseMsg result = instance.addMachineAndProcessData(req);
            RemoveServicePolicy(url);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void addMachineAndProcessDataTestNullURL() throws Exception {
        System.out.println("addMachineAndProcessDataTestNullURL");
        DCS4jBean instance = new DCS4jBean(agentctx);
        AddMachineAndProcessDataRequestMsg req = new AddMachineAndProcessDataRequestMsg();
        req.setAgentType("agent");
        req.setClassification(new SecurityWrapper(ClassificationType.U, ""));
        req.setDomainname("UNSPECIFIED");
        req.setHostname(Utility.getHostName());
        req.setMachineData(new MachinePerformanceData());
        req.getMachineData().setUri(null);
        req.setSensorData(null);
        req.getMachineData().setBytesusedMemory(null);
        req.getMachineData().setId(null);
        req.getMachineData().setNumberofActiveThreads(null);
        req.getMachineData().setOperationalstatus(true);
        req.getMachineData().setPercentusedCPU(null);
        req.getMachineData().setStartedAt(null);
        req.getMachineData().setTimestamp(null);
        try {
            AddMachineAndProcessDataResponseMsg result = instance.addMachineAndProcessData(req);
            fail("unexpected success");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void addMachineAndProcessDataTestValid() throws Exception {
        System.out.println("addMachineAndProcessDataTestNullURL");
        String localurl = "urn:localnost:service-" + UUID.randomUUID().toString();
        DCS4jBean instance = new DCS4jBean(agentctx);
        AddMachineAndProcessDataRequestMsg req = new AddMachineAndProcessDataRequestMsg();
        req.setAgentType("agent");
        req.setClassification(new SecurityWrapper(ClassificationType.U, ""));
        req.setDomainname("UNSPECIFIED");
        req.setHostname(Utility.getHostName());
        req.setMachineData(new MachinePerformanceData());
        req.getMachineData().setUri(localurl);
        req.setSensorData(null);
        req.getMachineData().setBytesusedMemory(null);
        req.getMachineData().setId(null);
        req.getMachineData().setNumberofActiveThreads(null);
        req.getMachineData().setOperationalstatus(true);
        req.getMachineData().setPercentusedCPU(null);
        req.getMachineData().setStartedAt(null);
        req.getMachineData().setTimestamp(null);
        try {
            AddMachineAndProcessDataResponseMsg result = instance.addMachineAndProcessData(req);
            RemoveServicePolicy(localurl);
        } catch (Exception ex) {
            RemoveServicePolicy(localurl);
            fail("unexpected failure");
            ex.printStackTrace();
        }
    }

    @org.junit.Test
    public void addMachineAndProcessDataTestValidSensorData() throws Exception {
        System.out.println("addMachineAndProcessDataTestValidSensorData");
        String localurl = "urn:localnost:service-" + UUID.randomUUID().toString();
        DCS4jBean instance = new DCS4jBean(agentctx);
        AddMachineAndProcessDataRequestMsg req = new AddMachineAndProcessDataRequestMsg();
        req.setAgentType("agent");
        req.setClassification(new SecurityWrapper(ClassificationType.U, ""));
        req.setDomainname("UNSPECIFIED");
        req.setHostname(Utility.getHostName());
        req.setMachineData(new MachinePerformanceData());
        req.getMachineData().setUri(localurl);
        req.setSensorData(new NameValuePairSet());
        req.getSensorData().getItems().add(Utility.newNameValuePair("name", "value", false, false));
        req.getMachineData().setBytesusedMemory(null);
        req.getMachineData().setId(null);
        req.getMachineData().setNumberofActiveThreads(null);
        req.getMachineData().setOperationalstatus(true);
        req.getMachineData().setPercentusedCPU(null);
        req.getMachineData().setStartedAt(null);
        req.getMachineData().setTimestamp(null);
        try {
            AddMachineAndProcessDataResponseMsg result = instance.addMachineAndProcessData(req);
            RemoveServicePolicy(localurl);
        } catch (Exception ex) {
            RemoveServicePolicy(localurl);
            fail("unexpected failure");
            ex.printStackTrace();
        }
    }
}