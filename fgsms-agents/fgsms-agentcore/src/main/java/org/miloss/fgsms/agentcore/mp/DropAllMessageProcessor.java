/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.agentcore.mp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.agentcore.IMessageProcessor;
import org.miloss.fgsms.agentcore.MessageCorrelator;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfXPathExpressionType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;

/** 
 * This is a super simple implementation of message processor, which basically does 
 * nothing. It's primarily for test purposes
 * @author AO
 */
public class DropAllMessageProcessor implements IMessageProcessor {

    private final static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    private ConfigLoader cfg = null;

    public DropAllMessageProcessor() {
        try {
            cfg = new ConfigLoader();
        } catch (ConfigurationException ex) {
            log.fatal("Error loading config file!", ex);
        }
    }

    @Override
    public boolean isDependencyInjectionEnabled() {
        return false;
    }

    @Override
    public void abort() {

    }

    @Override
    public void clearTransactionThreadId(long ThreadId) {

    }

    @Override
    public void forceNewDataPusherThread() {

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
        return "";
    }

    @Override
    public int getPolicyCache() {
        return 0;
    }

    @Override
    public TransactionalWebServicePolicy getPolicyIfAvailable(String url) {
        return null;
    }

    @Override
    public long getTotalmessagesprocessed() {
        return 0;
    }

    @Override
    public String getTransactionThreadId(Long ThreadId) {
        return "";
    }

    @Override
    public ArrayList<String> getUserIdentities(TransactionalWebServicePolicy p, MessageCorrelator mc) {
        return new ArrayList<String>();
    }

    @Override
    public ArrayList<String> getUsersfromXpath(ArrayOfXPathExpressionType xpaths, String message) {
        return new ArrayList<String>();
    }

    @Override
    public int internalMessageMapSize() {
        return 0;
    }

    @Override
    public int outboundQueueSize() {
        return 0;
    }

    @Override
    public void processMessageInput(String XMLrequest, int requestSize, String url, String soapAction, String HttpUsername, String HashCode, HashMap headers, String ipaddress, String agentclassname, String relatedtransaction, String threadid) {

    }

    @Override
    public void processMessageOutput(String HashCode, String responseXML, int responseSize, boolean isFault, Long dod, HashMap headers, String relatedTransaction) {

    }

    @Override
    public void processMessageOutput(String HashCode, String responseXML, int responseSize, boolean isFault, Long dod, HashMap headers) {

    }

    @Override
    public void processPreppedMessage(AddDataRequestMsg request) {

    }

    @Override
    public void purgeMessageMap() {

    }

    @Override
    public void purgeOutboundQueue() {

    }

    @Override
    public void purgePolicyCache() {

    }

    @Override
    public void removeDeadMessage() {

    }

    @Override
    public void removeFromQueue(UUID id) {

    }

    @Override
    public void removeFromQueue(String id) {

    }

    @Override
    public void setRunning(boolean b) {

    }

    @Override
    public void setTransactionThreadId(Long ThreadId, String id) throws Exception {

    }

    @Override
    public boolean shouldAgentRecordRequestContent(String requesturl) {
        return false;
    }

    @Override
    public boolean shouldAgentRecordResponseContent(String requesturl) {
        return false;
    }

    @Override
    public void terminate() {

    }

    @Override
    public Set<String> getIgnoreList() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Map<String, String> getURLaddressMap() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public int getThreadMapSize() {
        return 0;
    }

    @Override
    public void purgeThreadMap() {

    }

    @Override
    public void setLastErrorMessage(String all_PCS_endpoints_are_either_unreachable_) {

    }

    @Override
    public void incMessagesProcessed(int size) {

    }

}
