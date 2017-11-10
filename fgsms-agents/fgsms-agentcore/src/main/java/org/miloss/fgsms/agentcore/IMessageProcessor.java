/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.agentcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfXPathExpressionType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;

/**
 *
 * @author AO
 */
public interface IMessageProcessor {

    boolean isDependencyInjectionEnabled();

    /**
     * purges all queues and maps
     */
    void abort();

    /*
     * * Dependency Detection Only clear on an outbound service response
     */
    void clearTransactionThreadId(long ThreadId);

    /**
     * Use with caution. this will fire up a new thread. this is provided
     * specifically for mbean management
     */
    void forceNewDataPusherThread();

    ConfigLoader getConfig();

    /**
     * returns the lowercase value of the current hostname
     *
     * @return
     */
    String getHostName();

    String getLastErrorMessage();

    /**
     * gets the size of the current policy cache
     *
     * @return
     */
    int getPolicyCache();

    /**
     * Attempts to find and return a service policy from the local cache. if it
     * is not available, null will be returned. should never throw an exception.
     * use this function as a memory footprint reducer.
     *
     * @param url
     * @return service policy, possibly null
     */
    TransactionalWebServicePolicy getPolicyIfAvailable(String url);

    /**
     * @return the totalmessagesprocessed
     */
    long getTotalmessagesprocessed();

    /**
     * Dependency Detection This should only be called in the context of a
     * client handler seeing outbound transactions
     */
    String getTransactionThreadId(Long ThreadId);

    /**
     * turns all user identities as defined by the service policy, plus ip
     * addresses
     *
     * @param p policy
     * @param mc, current mesage correlation
     * @return
     */
    ArrayList<String> getUserIdentities(TransactionalWebServicePolicy p, MessageCorrelator mc);

    /**
     * returns user identities via xpath
     *
     * @param xpaths
     * @param message
     * @return
     */
    ArrayList<String> getUsersfromXpath(ArrayOfXPathExpressionType xpaths, String message);

    /**
     * gets the size of the current message map
     *
     * @return
     */
    int internalMessageMapSize();

    /**
     * gets the size of the current queue
     *
     * @return
     */
    int outboundQueueSize();

    /**
     * caches stored data and adds it to the message map. see note on
     * agentclassname reference
     *
     *
     * @param XMLrequest - may be null, the request content
     * @param requestSize - size of the payload
     * @param url - the requested URL that the client asked for
     * http://localhost/path
     * @param soapAction - SOAPAction or HTTP Method
     * @param HttpUsername the username or DN of the requestor
     * @param HashCode - very important, a unique identifier for this message.
     * Use UUID.random
     * @param headers - Transport layer headers of the request
     * @param ipaddress - IP of the requestor
     * @param agentclassname - the class of the agent that recorded the message.
     * MUST CONTAIN "client" IF THIS IS A CLIENT or from a proxy TRANSACTION!!
     * this is necessary for proper URL resolution
     * @param relatedtransaction - if this transaction was also recorded
     * elsewhere, pull this data from the http headers
     * @param threadid - if this transaction was also recorded elsewhere, pull
     * this data from the http headers. or generate a new one if it was not
     * present using UUID.random
     */
    void processMessageInput(String XMLrequest, int requestSize, String url, String soapAction, String HttpUsername, String HashCode, HashMap headers, String ipaddress, String agentclassname, String relatedtransaction, String threadid);


    /**
     * wrapper to the main function This is normally only called when a CLIENT
     * receives a response from a service
     *
     * @param HashCode
     * @param responseXML
     * @param responseSize
     * @param isFault
     * @param dod
     * @param headers
     * @param relatedTransaction
     */
    void processMessageOutput(String HashCode, String responseXML, int responseSize, boolean isFault, Long dod, HashMap headers, String relatedTransaction);

    /**
     * pairs up a response with a request and adds it to the queue for
     * transmission
     *
     * @param HashCode the UUID of the message that links this response to the
     * request
     * @param responseXML response payload
     * @param responseSize
     * @param isFault
     * @param dod time of death, no longer used
     * @param headers
     */
    void processMessageOutput(String HashCode, String responseXML, int responseSize, boolean isFault, Long dod, HashMap headers);

    /**
     * this assumes that the request is already prepopulated with all required
     * information according the policy for this item typically used for the
     * offline persistent storage agent. As of RC6
     *
     * @param request
     */
    void processPreppedMessage(AddDataRequestMsg request);

    /**
     * purges the map
     */
    void purgeMessageMap();

    /**
     * purges the queue
     */
    void purgeOutboundQueue();

    /**
     * purges the policy cache
     */
    void purgePolicyCache();

    /**
     * Any message in the Message Map older than the dead message interval will
     * be removed from the Message Map, flagged as faulted and added to the
     * outbound queue
     */
    void removeDeadMessage();
    //private static boolean Shutdown;
    //private static java.lang.Thread runner;

    /**
     * Use when for some reason, a transaction cannot be monitored NOTE, removes
     * from the Message Map, not the outbound queue
     *
     * @param id
     */
    void removeFromQueue(UUID id);

    /**
     * Use when for some reason, a transaction cannot be monitored NOTE, removes
     * from the Message Map, not the outbound queue
     *
     * @param id
     */
    void removeFromQueue(String id);

    void setRunning(boolean b);

    /**
     * * Dependency Detection Only Set on an inbound service request
     */
    void setTransactionThreadId(Long ThreadId, String id) throws Exception;

    /**
     * performance optimzation to be used by agents that determines if the
     * request body should be it uses a lazy algorithm for resolving request
     * urls to modified urls and will always resort to being on the safe side
     *
     * @param requesturl
     * @return
     */
    boolean shouldAgentRecordRequestContent(String requesturl);

    /**
     * performance optimization to be used by agents
     *
     * @param requesturl
     * @return
     */
    boolean shouldAgentRecordResponseContent(String requesturl);

    void terminate();

    /**
     * list of soap actions that should be ignored by agents
     *
     * @return
     */
    Set<String> getIgnoreList();
    
    
    /**
     * @return the URLaddressMap
     */
    Map<String,String> getURLaddressMap();

    int getThreadMapSize();

    void purgeThreadMap();

    void setLastErrorMessage(String all_PCS_endpoints_are_either_unreachable_);
       void incMessagesProcessed(int size);
}
