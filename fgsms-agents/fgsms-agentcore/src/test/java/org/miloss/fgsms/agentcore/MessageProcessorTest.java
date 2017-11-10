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

package org.miloss.fgsms.agentcore;

import java.util.HashMap;
import java.util.UUID;
import org.miloss.fgsms.common.Utility;

import static org.junit.Assert.*;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;

/**
 *
 * @author AO
 */
public class MessageProcessorTest {

    public MessageProcessorTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }
    private static String url = "http://localhost:8080/jUnitTest";

    @org.junit.After
    public void tearDown() throws Exception {
    }

    /**
     * Test of removeDeadMessage method, of class MessageProcessor.
     */
    @org.junit.Test
    public void testRemoveDeadMessage() {
    }

    /**
     * Test of getSingletonObject method, of class MessageProcessor.
     */
    @org.junit.Test
    public void testGetSingletonObject() {
        System.out.println("getSingletonObject");
        IMessageProcessor result = MessageProcessor.getSingletonObject();
        assertNotNull(result);

    }

    /**
     * Test of processMessageInput method, of class MessageProcessor.
     */
    @org.junit.Test
    public void testProcessMessageInput_9args() {
        System.out.println("testProcessMessageInput_9args");
        String XMLrequest = "";
        int requestSize = 0;
        String soapAction = "";
        String HttpUsername = "";
        String HashCode = "";
        HashMap headers = null;
        String ipaddress = "";
        String agentclassname = "";
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        MessageProcessor.getSingletonObject().processMessageInput(XMLrequest, requestSize, url, soapAction, HttpUsername, HashCode, headers, ipaddress, agentclassname, null,null);
        if (i + 1 != MessageProcessor.getSingletonObject().internalMessageMapSize()) {
            fail("Item was not added. map size was " + i + " and is now " + MessageProcessor.getSingletonObject().internalMessageMapSize());
        }
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();
    }

    @org.junit.Test
    public void testProcessMessageInput_9argsNullURL() {
        System.out.println("testProcessMessageInput_9argsNullURL");
        String XMLrequest = "";
        int requestSize = 0;
        String url2 = "";
        String soapAction = "";
        String HttpUsername = "";
        String HashCode = "";
        HashMap headers = null;
        String ipaddress = "";
        String agentclassname = "";
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        MessageProcessor.getSingletonObject().processMessageInput(XMLrequest, requestSize, url2, soapAction, HttpUsername, HashCode, headers, ipaddress, agentclassname, null,null);
        if (i + 1 == MessageProcessor.getSingletonObject().internalMessageMapSize()) {
            fail("Item was unexpectedly added. map size was " + i + " and is now " + MessageProcessor.getSingletonObject().internalMessageMapSize());
        }
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();
    }

    /**
     * Test of processMessageInput method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testProcessMessageInput_11argsNullUrl() {
        System.out.println("testProcessMessageInput_11argsNullUrl");
        String XMLrequest = "";
        int requestSize = 0;
        String url = "";
        String soapAction = "";
        String HttpUsername = "";
        String HashCode = "";
        HashMap headers = null;
        String ipaddress = "";
        String agentclassname = "";
        String relatedtransaction = "";
        String threadid = "";
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        MessageProcessor.getSingletonObject().processMessageInput(XMLrequest, requestSize, url, soapAction, HttpUsername, HashCode, headers, ipaddress, agentclassname, relatedtransaction, threadid);
        if (i + 1 == MessageProcessor.getSingletonObject().internalMessageMapSize()) {

            fail("Item was added. map size was " + i + " and is now " + MessageProcessor.getSingletonObject().internalMessageMapSize());
        }
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();
    }

    @org.junit.Test
    public void testProcessMessageInput_11args() {
        System.out.println("testProcessMessageInput_11args");
        String XMLrequest = "";
        int requestSize = 0;

        String soapAction = "";
        String HttpUsername = "";
        String HashCode = "";
        HashMap headers = null;
        String ipaddress = "";
        String agentclassname = "";
        String relatedtransaction = "";
        String threadid = "";
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        MessageProcessor.getSingletonObject().processMessageInput(XMLrequest, requestSize, url, soapAction, HttpUsername, HashCode, headers, ipaddress, agentclassname, relatedtransaction, threadid);
        if (i + 1 != MessageProcessor.getSingletonObject().internalMessageMapSize()) {

            fail("Item not added. map size was " + i + " and is now " + MessageProcessor.getSingletonObject().internalMessageMapSize());
        }
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();
    }

    /**
     * Test of processMessageOutput method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testProcessMessageOutput_5argsNoMatch() {
        System.out.println("testProcessMessageOutput_5argsNoMatch");
        String HashCode = "";
        String responseXML = "";
        int responseSize = 0;
        boolean isFault = false;
        Long dod = null;
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();
        MessageProcessor.getSingletonObject().processMessageOutput(HashCode, responseXML, responseSize, isFault, dod, null,null);
        assertTrue(MessageProcessor.getSingletonObject().internalMessageMapSize()==0);
    }

    @org.junit.Test
    public void testProcessMessageOutput_5args() {
        System.out.println("testProcessMessageOutput_5args");
        String XMLrequest = "";
        int requestSize = 0;
        String soapAction = "";
        String HttpUsername = "";
        String HashCode = "";
        HashMap headers = null;
        String ipaddress = "";
        String agentclassname = "";
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        MessageProcessor.getSingletonObject().processMessageInput(XMLrequest, requestSize, url, soapAction, HttpUsername, HashCode, headers, ipaddress, agentclassname, null,null);

        String responseXML = "";
        int responseSize = 0;
        boolean isFault = false;
        Long dod = null;
        MessageProcessor.getSingletonObject().processMessageOutput(HashCode, responseXML, responseSize, isFault, dod, null,null);
        assertTrue(MessageProcessor.getSingletonObject().outboundQueueSize() > 0);
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();

    }

    /**
     * Test of processMessageOutput method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testProcessMessageOutput_7args() {
        System.out.println("testProcessMessageOutput_7args");
        String HashCode = "";
        String responseXML = "";
        int responseSize = 0;
        boolean isFault = false;
        Long dod = null;
        HashMap headers = null;
        String relatedTransaction = "";
        MessageProcessor.getSingletonObject().processMessageOutput(HashCode, responseXML, responseSize, isFault, dod, headers, relatedTransaction);
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();

    }

    /**
     * Test of processMessageOutput method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testProcessMessageOutput_6argsNoMatch() {
        System.out.println("testProcessMessageOutput_6argsNoMatch");
        String HashCode = "";
        String responseXML = "";
        int responseSize = 0;
        boolean isFault = false;
        Long dod = null;
        HashMap headers = null;
        MessageProcessor.getSingletonObject().processMessageOutput(HashCode, responseXML, responseSize, isFault, dod, headers);


    }

    @org.junit.Test
    public void testProcessMessageOutput_6args() {
        System.out.println("testProcessMessageOutput_6args");
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();
        String XMLrequest = "";
        int requestSize = 0;
        String soapAction = "";
        String HttpUsername = "";
        String HashCode = "";
        HashMap headers = null;
        String ipaddress = "";
        String agentclassname = "";
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        MessageProcessor.getSingletonObject().processMessageInput(XMLrequest, requestSize, url, soapAction, HttpUsername, HashCode, headers, ipaddress, agentclassname, null,null);
        assertTrue(MessageProcessor.getSingletonObject().internalMessageMapSize() > 0);

        String responseXML = "";
        int responseSize = 0;
        boolean isFault = false;
        Long dod = null;

        MessageProcessor.getSingletonObject().processMessageOutput(HashCode, responseXML, responseSize, isFault, dod, headers);
        assertTrue(MessageProcessor.getSingletonObject().internalMessageMapSize() == 0);
        assertTrue(MessageProcessor.getSingletonObject().outboundQueueSize() > 0);
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();


    }

    /**
     * Test of abort method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testAbort() {
        System.out.println("abort");
        MessageProcessor.getSingletonObject().abort();
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of getPolicyCache method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testGetPolicyCache() {
        System.out.println("getPolicyCache");
        int expResult = 0;
        int result = MessageProcessor.getSingletonObject().getPolicyCache();
        if (result < 0) {
            fail("can't have a negative cache count");
        }
    }

    /**
     * Test of outboundQueueSize method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testOutboundQueueSize() {
        System.out.println("outboundQueueSize");
        int expResult = 0;
        int result = MessageProcessor.getSingletonObject().outboundQueueSize();
        if (result < 0) {
            fail("can't have a negative cache count");
        }
    }

    /**
     * Test of internalMessageMapSize method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testInternalMessageMapSize() {
        System.out.println("internalMessageMapSize");
        int expResult = 0;
        int result = MessageProcessor.getSingletonObject().internalMessageMapSize();
        if (result < 0) {
            fail("can't have a negative cache count");
        }
    }

    /**
     * Test of purgePolicyCache method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testPurgePolicyCache() {
        System.out.println("purgePolicyCache");

    }

    /**
     * Test of purgeOutboundQueue method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testPurgeOutboundQueue() {
        System.out.println("purgeOutboundQueue");
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        //add 1000 messages to the queue
        //purge the cache
        //check sent verse enqueued counts
        //MessageProcessor.totalmessagesprocessed;

    }

    /**
     * Test of purgeMessageMap method, of class MessageProcessor.
     */
    @org.junit.Test
    public void testPurgeMessageMap() {
        System.out.println("purgeMessageMap");
        String XMLrequest = "";
        int requestSize = 0;
        String url = "";
        String soapAction = "";
        String HttpUsername = "";
        String HashCode = "";
        HashMap headers = null;
        String ipaddress = "";
        String agentclassname = "";
        MessageProcessor.getSingletonObject().processMessageInput(XMLrequest, requestSize, url, soapAction, HttpUsername, HashCode, headers, ipaddress, agentclassname, null,null);
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        MessageProcessor.getSingletonObject().purgeMessageMap();;
        if (MessageProcessor.getSingletonObject().internalMessageMapSize() != 0) {
            fail("Message map is of size " + MessageProcessor.getSingletonObject().internalMessageMapSize());
        }
    }

    /**
     * Test of removeFromQueue method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testRemoveFromQueueUUID() {
        System.out.println("RemoveFromQueueUUID");
        
        UUID id = UUID.randomUUID();
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();
        
        MessageProcessor.getSingletonObject().processMessageInput(null, 0, "http://something", null, null, id.toString(), null, null, null, null, null);
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        assertTrue(i ==1);
        MessageProcessor.getSingletonObject().removeFromQueue(id);
        if (MessageProcessor.getSingletonObject().internalMessageMapSize() + 1 != i) {
            fail("message map was " + i + "but now is " + MessageProcessor.getSingletonObject().internalMessageMapSize());
        }

    }

    @org.junit.Test
    public void testRemoveFromQueueString() {
        System.out.println("testRemoveFromQueueString");
        UUID id = UUID.randomUUID();
        MessageProcessor.getSingletonObject().purgeMessageMap();
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
        MessageProcessor.getSingletonObject().purgePolicyCache();
        
        MessageProcessor.getSingletonObject().processMessageInput(null, 0, "http://something", null, null, id.toString(), null, null, null, null, null);
        assertTrue(MessageProcessor.getSingletonObject().internalMessageMapSize()==1);
        int i = MessageProcessor.getSingletonObject().internalMessageMapSize();
        MessageProcessor.getSingletonObject().removeFromQueue(id.toString());
        if (MessageProcessor.getSingletonObject().internalMessageMapSize() + 1 != i) {
            fail("message map was " + i + "but now is " + MessageProcessor.getSingletonObject().internalMessageMapSize());
        }

    }

    /**
     * Test of getUserIdentities method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testGetUserIdentities() {
        System.out.println("getUserIdentities");
        /*
         * ServicePolicy p = null; MessageCorrelator mc = null; ArrayList
         * expResult = null; ArrayList result =
         * MessageProcessor.getSingletonObject().getUserIdentities(p, mc); assertEquals(expResult,
         * result); // TODO review the generated test code and remove the
         * default call to fail. //fail("The test case is a prototype.");
         */
    }

    /**
     * Test of getUsersfromXpath method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testGetUsersfromXpath() {
        /*
         * System.out.println("getUsersfromXpath"); ArrayOfXPathExpressionType
         * xpaths = null; String message = ""; ArrayList expResult = null;
         * ArrayList result = MessageProcessor.getSingletonObject().getUsersfromXpath(xpaths,
         * message); assertEquals(expResult, result); // TODO review the
         * generated test code and remove the default call to fail. fail("The
         * test case is a prototype.");
         */
    }

    /**
     * Test of getHostName method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testGetHostName() {

        System.out.println("GetHostName");
        String expResult = Utility.getHostName();
        String result = MessageProcessor.getSingletonObject().getHostName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPolicyIfAvailable method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testGetPolicyIfAvailableWhenNotCached() {
        System.out.println("testGetPolicyIfAvailableWhenNotCached");
        ServicePolicy expResult = null;
        ServicePolicy result = MessageProcessor.getSingletonObject().getPolicyIfAvailable("http://localhost");
        assertEquals(expResult, result);
    }

    /**
     * Test of getTransactionThreadId method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testGetTransactionThreadId() {
        /*
         * System.out.println("getTransactionThreadId"); Long ThreadId = null;
         * String expResult = ""; String result =
         * MessageProcessor.getSingletonObject().getTransactionThreadId(ThreadId);
         * assertEquals(expResult, result); // TODO review the generated test
         * code and remove the default call to fail. fail("The test case is a
         * prototype.");
         */
    }

    /**
     * Test of setTransactionThreadId method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testSetTransactionThreadId() throws Exception {
        /*
         * System.out.println("setTransactionThreadId"); Long ThreadId = null;
         * String id = ""; MessageProcessor.getSingletonObject().setTransactionThreadId(ThreadId,
         * id); // TODO review the generated test code and remove the default
         * call to fail. fail("The test case is a prototype.");
         */
    }

    /**
     * Test of clearTransactionThreadId method, of class MessageProcessor.getSingletonObject().
     */
    @org.junit.Test
    public void testClearTransactionThreadId() {
        /*
         * System.out.println("clearTransactionThreadId"); long ThreadId = 0L;
         * MessageProcessor.clearTransactionThreadId(ThreadId); // TODO review
         * the generated test code and remove the default call to fail.
         * fail("The test case is a prototype.");
         */
    }
}
