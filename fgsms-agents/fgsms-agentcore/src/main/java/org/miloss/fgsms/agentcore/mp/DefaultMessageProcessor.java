/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.agentcore.mp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Level;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.agentcore.DataPusher;
import org.miloss.fgsms.agentcore.IMessageProcessor;
import org.miloss.fgsms.agentcore.MessageCorrelator;
import org.miloss.fgsms.agentcore.PolicyHelper;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.AndOrNot;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfUserIdentity;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfXPathExpressionType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RuleBaseType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLA;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLARuleGeneric;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.w3c.dom.Document;

/**
* The Message Processor handles and manages the
 * collection of Transactional Web Service records. Using data from the fgsms
 * PCS, the MP sends performance stats to the fgsms DCS.<br><br>
 * Features support for online/offline data storage, roundrobin/failover support
 * and discovery of FGSMS service endpoints
 *
 * @author AO
 * @see IMessageProcessor
 * @see ConfigLoader
 */
public class DefaultMessageProcessor implements IMessageProcessor {

    private final static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    private Thread[] pool = new Thread[Runtime.getRuntime().availableProcessors()];

    private final HashMap messageMap = new HashMap();
    private final HashMap ThreadIdMap = new HashMap();
    private final ConcurrentLinkedQueue outboundQueue = new ConcurrentLinkedQueue();
    private final HashMap policyCache = new HashMap();
    private long deadMessageInterval = 600000;
    private boolean isDependencyInjectionEnabled=true;

    private String lasterror = "";
    private final Map<String, String> URLaddressMap = new HashMap();
    private ConfigLoader cfg = null;
    private boolean running = true;
    private long totalmessagesprocessed = 0;
    /**
     * Always use toLower() when checking the ignore list
     */
    private final Set<String> ignoreList = new HashSet<String>();

    private String myHostname = null;

    public DefaultMessageProcessor() {
        //TODO consider putting a maximum cap on the thread pool, may be 4 at the most
        try {
            cfg = new ConfigLoader();
        } catch (ConfigurationException ex) {
            log.fatal("Error loading config file!", ex);
            lasterror = ex.getMessage();
            System.err.println("Error loading config file!");
            ex.printStackTrace();
            log.log(Level.FATAL, "Unable to load configuration, messages will not be transmitted", ex);
            deadMessageInterval = 10000;
            isDependencyInjectionEnabled = false;
        }
    }

    public int getThreadMapSize() {
        return ThreadIdMap.size();
    }

    public void purgeThreadMap() {
        ThreadIdMap.clear();
    }

    @Override
    public void abort() {
        outboundQueue.clear();
        messageMap.clear();
    }

    /**
     * IP to DNS mappings to speed up performance non blocking
     *
     * @param URL
     * @return
     */
    protected String ipWrapAndCacher(String URL) {
        if (URLaddressMap.containsKey(URL)) {
            return (String) URLaddressMap.get(URL);
        }
        return URL;
    }

    @Override
    public void clearTransactionThreadId(long ThreadId) {
        ThreadIdMap.remove(ThreadId);
    }

    @Override
    public void forceNewDataPusherThread() {
        try {
            log.log(Level.INFO, " == fgsms Message Processor== launched new thread to push out data to DCS.");
            Thread t = (new Thread(new DataPusher(policyCache, outboundQueue)));
            t.start();
            running = true;
        } catch (Exception ex) {
            log.log(Level.FATAL, "******************************************************* fgsms could not start the Data Pusher Thread. This is most likely due to server overloading, memory limits or hitting the maxium thread pool for the container. Please consider revising. "
                    + "Purging " + messageMap.size() + " records from MsgMap and " + outboundQueue.size() + " from the outbound queue to prevent container overload. *********************************************", ex);
            try {
                messageMap.clear();
                outboundQueue.clear();
            } catch (Exception ex2) {
            }
        }
    }

    @Override
    public ConfigLoader getConfig() {
        return cfg;
    }

    @Override
    public String getHostName() {
        return Utility.getHostName();
    }

    @Override
    public String getLastErrorMessage() {
        return lasterror;
    }

    @Override
    public int getPolicyCache() {
        if (policyCache == null) {
            return 0;
        }
        return policyCache.size();
    }

    @Override
    public TransactionalWebServicePolicy getPolicyIfAvailable(String url) {
        Object j = policyCache.get(url);
        if (j == null) {
            return null;
        }
        PolicyHelper pol = (PolicyHelper) j;
        if (pol == null || pol.policy == null) {
            return null;
        }
        return pol.policy;
    }

    @Override
    public long getTotalmessagesprocessed() {
        return totalmessagesprocessed;
    }

    @Override
    public String getTransactionThreadId(Long ThreadId) {
        return (String) ThreadIdMap.get(ThreadId);
    }

    @Override
    public ArrayList<String> getUserIdentities(TransactionalWebServicePolicy p, MessageCorrelator mc) {
        if (p == null) {
            throw new NullPointerException("policy is null");

        }
        if (mc == null) {
            throw new NullPointerException("MessageCorrelator is null");

        }
        ArrayList<String> users = new ArrayList<String>();
        if (!Utility.stringIsNullOrEmpty(mc.ipaddress)) {
            users.add(mc.ipaddress);
        }
        if (!Utility.stringIsNullOrEmpty(mc.HttpIdentity)) {
            users.add(mc.HttpIdentity);
        }
        if (p.getUserIdentification() == null
                || p.getUserIdentification().getUserIdentity() == null
                || p.getUserIdentification().getUserIdentity().isEmpty()) {
            return users;

        }
        ArrayOfUserIdentity id = (ArrayOfUserIdentity) p.getUserIdentification();
        for (int i = 0; i < id.getUserIdentity().size(); i++) {
            if (id.getUserIdentity().get(i).isUseHttpHeader() != null && id.getUserIdentity().get(i).isUseHttpHeader()) {
                try {
                    users.add((String) mc.Headers.get(id.getUserIdentity().get(i).getHttpHeaderName()));
                    // log.log(Level.INFO, "Requestor identity via http header name successful, " + (String) mc.Headers.get(id.getUserIdentity().get(i).getHttpHeaderName()));
                } catch (Exception ex) {
                    log.log(Level.WARN, "Error retrieving Requestor identity via http header name: " + ex.getLocalizedMessage());
                }
            }
            //if (id.getUserIdentity().get(i).isUseHttpCredential() != null && id.getUserIdentity().get(i).isUseHttpCredential()) {
//                users.add(mc.HttpIdentity);
            //log.log(Level.INFO, "User identification via Http credentials successful, " + mc.HttpIdentity);
            //        }
            if (//id.getUserIdentity().get(i).getNamespaces() != null
                    id.getUserIdentity().get(i).getXPaths() != null) {
                // ArrayOfXMLNamespacePrefixies xmlns = id.getUserIdentity().get(i).getNamespaces().getValue();
                ArrayOfXPathExpressionType xpaths = id.getUserIdentity().get(i).getXPaths();
                ArrayList<String> tlocal = getUsersfromXpath(xpaths, mc.RequestMessage);
                // log.log(Level.INFO, "User identification via XPath returned " + t.size() + " results.");
                for (int k = 0; k < tlocal.size(); k++) {
                    users.add(tlocal.get(k));
                }
            }
        }
        log.log(Level.INFO, "getUserIdentities returning " + users.size() + " requestor identities");
        return users;
    }

    @Override
    public ArrayList<String> getUsersfromXpath(ArrayOfXPathExpressionType xpaths, String message) {
        ArrayList<String> userlist = new ArrayList<String>();

        if (xpaths == null || xpaths.getXPathExpressionType() == null || xpaths.getXPathExpressionType().isEmpty()) {
            return new ArrayList<String>();
        }
        try {
            InputStream is = new ByteArrayInputStream(message.getBytes(Constants.CHARSET));
            Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            for (int i = 0; i < xpaths.getXPathExpressionType().size(); i++) {
                XPath xpath = XPathFactory.newInstance().newXPath();
                javax.xml.xpath.XPathExpression xp = xpath.compile(xpaths.getXPathExpressionType().get(i).getXPath());
                String t2 = (String) xp.evaluate(xmlDocument, XPathConstants.STRING);
                if (!Utility.stringIsNullOrEmpty(t2)) {
                    userlist.add(t2);
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "Error evaluating xpath expression for consumer identification", ex);
        }

        return userlist;
    }

    public void incMessagesProcessed(int size) {
        totalmessagesprocessed += size;
    }

    @Override
    public int internalMessageMapSize() {
        if (messageMap == null) {
            return 0;
        }
        //FIXME this sometimes returns negatives numbers
        return messageMap.size();
    }

    @Override
    public int outboundQueueSize() {
        if (outboundQueue == null) {
            return 0;
        }
        return outboundQueue.size();
    }

  

    @Override
    public void processMessageInput(String XMLrequest, int requestSize, String url, String soapAction, String HttpUsername, String HashCode, HashMap headers, String ipaddress, String agentclassname, String relatedtransaction, String threadid) {

        if (!running) {
            return;
        }
        if (XMLrequest == null) {
            XMLrequest = "";
        }
        long now = System.currentTimeMillis();
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageInput0 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        if (isOnIgnoreList(url) || Utility.stringIsNullOrEmpty(url)) {
            log.log(Level.INFO, "fgsms, message for " + url + " is on the ignore list or is null or empty. A valid URL must be specified.");
            return;
        }
        long start = System.currentTimeMillis();
        MessageCorrelator mc = new MessageCorrelator();
        mc.RecievedAt = System.currentTimeMillis();
        mc.Headers = headers;
        mc.RelatedMsgId = relatedtransaction;
        mc.TransactionThreadId = threadid;
        mc.reqsize = requestSize;
        mc.URL = mc.originalurl = url;
        //memory reduction optimization, we really don't know how long this data will hang around in the queue,
        //if the policy is available we can reduce memory usage by not hanging on to the message content
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageInput1 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        TransactionalWebServicePolicy tp = getPolicyIfAvailable(url);
        if (tp != null) {
            if (tp.isRecordRequestMessage() || tp.isRecordFaultsOnly() || containsSLAXpathOrUserIdentXpath(tp)) {
                mc.RequestMessage = XMLrequest;
            }
        } else {
            //policy is not available, save the content just in case
            mc.RequestMessage = XMLrequest;
        }

        log.log(Level.TRACE, "MessageProcessor.ProcessMessageInput2 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        mc.soapAction = soapAction;
        mc.MessageID = HashCode;
        mc.HttpIdentity = HttpUsername;
        mc.ipaddress = ipaddress;
        mc.agent_class_name = agentclassname;
        //mc.TransactionThreadId = TransactionThreadID;

        messageMap.put(HashCode, mc);
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageInput2 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        //log.log(Level.WARN, "fgsms, processMessageInput, Message ID:" + mc.MessageID);
        log.log(Level.DEBUG, "fgsms, processMessageInput, MsgMap:" + messageMap.size() + " Outbound Queue:" + outboundQueue.size());
        log.log(Level.DEBUG, "fgsms, Input message for " + url + " action " + soapAction);
        removeDeadMessage();
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageInput3 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
    }

  
    @Override
    public void processMessageOutput(String HashCode, String responseXML, int responseSize, boolean isFault, Long dod, HashMap headers, String relatedTransaction) {

        long now = System.currentTimeMillis();
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageOutput0 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        MessageCorrelator mc = (MessageCorrelator) messageMap.get(HashCode);
        if (mc == null) {
            log.log(Level.DEBUG, "fgsms ProcessOutboundMessage, MsgMap:" + messageMap.size() + " Outbound Queue:" + outboundQueue.size());
            log.log(Level.WARN, "fgsms on processMessageOutput, a corresponding request could not be paired to the "
                    + "response with id " + HashCode + ". It's possible that property message.processor.dead.message.queue.duration is set too low in the"
                    + " fgsms-agent.properties file. This shouldn't happen often, if it does, please contact the fgsms developers at https://github.com/mil-oss/fgsms");
            removeDeadMessage();
            run();
            return;
        }
        if (!Utility.stringIsNullOrEmpty(relatedTransaction)) {
            mc.RelatedMsgId = relatedTransaction;
        }
        processMessageOutput(HashCode, responseXML, responseSize, isFault, dod, headers);
    }

    @Override
    public void processMessageOutput(String HashCode, String responseXML, int responseSize, boolean isFault, Long dod, HashMap headers) {
        if (!running) {
            return;
        }
        if (responseXML == null) {
            responseXML = "";
        }
        long now = System.currentTimeMillis();
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageOutput0 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        MessageCorrelator mc = (MessageCorrelator) messageMap.get(HashCode);
        if (mc == null) {
            log.log(Level.DEBUG, "fgsms ProcessOutboundMessage, MsgMap:" + messageMap.size() + " Outbound Queue:" + outboundQueue.size());
            log.log(Level.WARN, "fgsms on processMessageOutput, a corresponding request could not be paired to the "
                    + "response with id " + HashCode + ". It's possible that property message.processor.dead.message.queue.duration is set too low in the"
                    + " fgsms-agent.properties file. This shouldn't happen often, if it does, please contact the fgsms developers at https://github.com/mil-oss/fgsms");
            removeDeadMessage();
            run();
            return;
        }
        mc.CompletedAt = System.currentTimeMillis();
        mc.currentMapsize = messageMap.size();
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageOutput1 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        //memory reduction optimization, we really don't know how long this data will hang around in the queue,
        //if the policy is available we can reduce memory usage by not hanging on to the message content
        TransactionalWebServicePolicy tp = getPolicyIfAvailable(mc.URL);
        if (tp != null) {
            if (tp.isRecordResponseMessage() || (tp.isRecordFaultsOnly() && isFault)) {
                mc.ResponseMessage = responseXML;
            }
            if (tp.isRecordFaultsOnly() && !isFault) {
                mc.RequestMessage = "";
                mc.ResponseMessage = "";
            }
        } else {
            //policy is not available, save the content just in case
            mc.ResponseMessage = responseXML;
        }
        if (mc.ResponseMessage == null) {
            mc.ResponseMessage = "";
        }

        log.log(Level.TRACE, "MessageProcessor.ProcessMessageOutput2 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        mc.IsFault = isFault;
        mc.ressize = responseSize;

        mc.Header_Response = headers;

        outboundQueue.add(mc);
        messageMap.remove(HashCode);
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageOutput3 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        run();
        log.log(Level.TRACE, "MessageProcessor.ProcessMessageOutput4 timer: " + (System.currentTimeMillis() - now) + " thread:" + Thread.currentThread().getId());
        //2386ms
        log.log(Level.DEBUG, "fgsms, ProcessOutboundMessage: Message for URL " + mc.URL + " and action " + mc.soapAction + " is now in the outbound queue to the DCS.");
        log.log(Level.DEBUG, "fgsms, PostProcessOutboundMessage, MsgMap:" + messageMap.size() + " Outbound Queue:" + outboundQueue.size());
    }

    @Override
    public void processPreppedMessage(AddDataRequestMsg request) {
        if (!running) {
            return;
        }
        if (request == null) {
            throw new IllegalArgumentException("request");
        }

        outboundQueue.add(request);
        run();
    }

    @Override
    public void purgeMessageMap() {
        messageMap.clear();
    }

    @Override
    public void purgeOutboundQueue() {
        outboundQueue.clear();
    }

    @Override
    public void purgePolicyCache() {
        policyCache.clear();
    }

    /**
     * fires up a new thread to get the queue's emptied
     */
    private synchronized void run() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            //TODO add check to prevent firing up threads unnecessarily
            //the magic number seems to be about 200 transactions pre thread on the outbound rate
            //therefore we should do no more than
            //queue size /200 threads

            for (int i = 0; i < pool.length; i++) {
                if (pool[i] == null) {
                    pool[i] = (new Thread(new DataPusher(policyCache, outboundQueue), "fgsms Agent Thread " + i));
                    pool[i].setName("FGSMS.MP." + i);
                    pool[i].start();

                } else {
                    if (!pool[i].isAlive()) {
                        pool[i] = (new Thread(new DataPusher(policyCache, outboundQueue), "fgsms Agent Thread " + i));
                        pool[i].setName("FGSMS.MP." + i);
                        pool[i].start();

                    }
                }
            }
        } catch (OutOfMemoryError ex) {
            log.log(Level.FATAL, "OOM! fgsms could not start the Data Pusher Thread. This is most likely due to server overloading, memory limits or hitting the maxium thread pool for the container. Please consider revising. "
                    + "Purging " + messageMap.size() + " records from MsgMap and " + outboundQueue.size() + " from the outbound queue to prevent container overload. *********************************************", ex);
            try {
                messageMap.clear();
                outboundQueue.clear();
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
            /*
                if (!running || t == null) {
                    log.log(Level.INFO, " == fgsms Message Processor== launched new thread to push out data to DCS.");
                    try {
                        t = (new Thread(new DataPusher(policyCache, outboundQueue), "fgsms Agent Thread"));
                        
                        t.start();
                        //  if (!t.isAlive()) {
                        //       throw new NullPointerException();
                        //  }
                        RunningThreads++;
                    } catch (Exception ex) {
                        log.log(Level.FATAL, "******************************************************* fgsms could not start the Data Pusher Thread. This is most likely due to server overloading, memory limits or hitting the maxium thread pool for the container. Please consider revising. "
                                + "Purging " + messageMap.size() + " records from MsgMap and " + outboundQueue.size() + " from the outbound queue to prevent container overload. *********************************************", ex);
                        try {
                            messageMap.clear();
                            outboundQueue.clear();
                        } catch (Exception ex2) {
                        }
                    }
                }*/
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void removeDeadMessage() {
        if (messageMap.isEmpty()) {
            return;
        }
        try {
            Iterator it = messageMap.keySet().iterator();
            List<String> ids = new ArrayList<String>();
            while (it.hasNext()) {
                String id = (String) it.next();
                if (!Utility.stringIsNullOrEmpty(id)) {
                    MessageCorrelator temp = (MessageCorrelator) messageMap.get(id);
                    if (temp != null) {
                        if ((System.currentTimeMillis() - deadMessageInterval > temp.RecievedAt)) {
                            ids.add(id);
                        }
                    }
                }
            }
            if (!ids.isEmpty()) {
                log.log(Level.WARN, "fgsms, purging " + ids.size() + " dead records from MsgMap. These message could have been faults that the agent didn't see, some kind of server fault, a ?WSDL request or perhaps something else.");
                for (int i = 0; i < ids.size(); i++) {
                    MessageCorrelator mc = (MessageCorrelator) messageMap.remove(ids.get(i));
                    mc.IsFault = true;
                    mc.CompletedAt = System.currentTimeMillis();
                    mc.ResponseMessage = "Message timed out, a response was not returned, or this was a WSDL request.";
                    outboundQueue.add(mc);
                }
            }

        } catch (Exception ex) {
        }

    }

    /**
     * tests to see if a url is on the ignore list
     *
     * @param url
     * @return
     */
    private boolean isOnIgnoreList(String url) {
        return (ignoreList.contains(url.toLowerCase()));

    }

    @Override
    public void removeFromQueue(UUID id) {
        messageMap.remove(id.toString());
        removeDeadMessage();
    }

    @Override
    public void removeFromQueue(String id) {
        messageMap.remove(id);
        removeDeadMessage();
    }

    @Override
    public void setRunning(boolean b) {
        running = b;
    }

    @Override
    public void setTransactionThreadId(Long ThreadId, String id) throws Exception {

        if (ThreadIdMap.containsKey(ThreadId)) {
            throw new Exception("attempting to set transaction thread id for thread " + id + " when an id has already been set");
        }
        ThreadIdMap.put(ThreadId, id);
    }

    @Override
    public boolean shouldAgentRecordRequestContent(String requesturl) {
        TransactionalWebServicePolicy GetPolicyIfAvailable = getPolicyIfAvailable(ipWrapAndCacher(requesturl));
        if (GetPolicyIfAvailable == null) {
            return true;
        }
        if (GetPolicyIfAvailable.isRecordRequestMessage() || GetPolicyIfAvailable.isRecordFaultsOnly()) {
            return true;
        }
        if (containsSLAXpathOrUserIdentXpath(GetPolicyIfAvailable)) {
            return true;
        }
        return false;
    }

    /**
     * used to determine if we need to record the request/response pair
     *
     * @param get
     * @return
     */
    protected boolean doesSLAContainXpath(SLA get) {
        if (get == null) {
            return false;
        }
        return doesSLARuleContainXpath(get.getRule());
    }

    /**
     * recursive used to determine if we need to record the request/response
     * pair
     *
     * @param rule
     * @return
     */
    protected boolean doesSLARuleContainXpath(RuleBaseType rule) {
        if (rule == null) {
            return false;
        }
        if (rule instanceof SLARuleGeneric) {
            SLARuleGeneric r = (SLARuleGeneric) rule;
            if (r.getClassName().equalsIgnoreCase("org.miloss.fgsms.sla.rules.XPathExpression")) {
                return true;
            }
        }
        if (rule instanceof AndOrNot) {
            AndOrNot t1 = (AndOrNot) rule;
            return doesSLARuleContainXpath(t1.getLHS()) || doesSLARuleContainXpath(t1.getRHS());
        }
        return false;
    }

    /**
     * used to determine if we need to save the request or response
     *
     * @param pol
     * @return
     */
    protected boolean containsSLAXpathOrUserIdentXpath(TransactionalWebServicePolicy pol) {
        boolean found = false;
        if (pol.getUserIdentification() != null && !pol.getUserIdentification().getUserIdentity().isEmpty()) {
            for (int i = 0; i < pol.getUserIdentification().getUserIdentity().size(); i++) {
                if (pol.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                        && pol.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                        && !pol.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().isEmpty()) {
                    found = true;
                }
            }
        }
        if (found) {
            return true;
        }
        if (pol.getServiceLevelAggrements() != null
                && !pol.getServiceLevelAggrements().getSLA().isEmpty()) {
            for (int i = 0; i < pol.getServiceLevelAggrements().getSLA().size(); i++) {
                found = found || doesSLAContainXpath(pol.getServiceLevelAggrements().getSLA().get(i));
            }
        }
        return found;
    }

    @Override
    public boolean shouldAgentRecordResponseContent(String requesturl) {
        TransactionalWebServicePolicy GetPolicyIfAvailable = getPolicyIfAvailable(ipWrapAndCacher(requesturl));
        if (GetPolicyIfAvailable == null) {
            return true;
        }
        if (GetPolicyIfAvailable.isRecordResponseMessage() || GetPolicyIfAvailable.isRecordFaultsOnly()) {
            return true;
        }
        if (containsSLAXpathOrUserIdentXpath(GetPolicyIfAvailable)) {
            return true;
        }
        return false;
    }

    @Override
    public void terminate() {
        for (int i = 0; i < pool.length; i++) {
            if (pool[i] != null) {
                try {
                    pool[i].interrupt();
                } catch (Throwable t) {
                }
                pool[i] = null;
            }
        }
        pool = null;
        outboundQueue.clear();
        messageMap.clear();
        policyCache.clear();
        log.log(Level.WARN, "Message Processor terminated");

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("org.miloss.fgsms.MessageProcessor:type=MessageProcessorAdapterMBean");
            mbs.unregisterMBean(name);
        } catch (Throwable ex) {
            log.log(Level.WARN, "unable to register MessageProcessor mbean", ex);
        }

    }

    @Override
    public Set<String> getIgnoreList() {
        return ignoreList;
    }

    @Override
    public Map<String, String> getURLaddressMap() {
        return URLaddressMap;
    }

    @Override
    public boolean isDependencyInjectionEnabled() {
        return isDependencyInjectionEnabled;
    }

    @Override
    public void setLastErrorMessage(String msg) {
        lasterror=msg;
    }

}
