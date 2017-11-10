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

/**
 * Provides an implementation that exposes message processor functionality via
 * JMX
 *
 * @author AO
 */
public class MessageProcessorAdapter implements MessageProcessorAdapterMBean {

    @Override
    public void RemoveDeadMessage() {
        
        MessageProcessor.getSingletonObject().removeDeadMessage();
    }

    @Override
    public void Abort() {
        
        MessageProcessor.getSingletonObject().abort();
    }

    @Override
    public int GetPolicyCache() {
        
        return MessageProcessor.getSingletonObject().getPolicyCache();
    }

    @Override
    public int OutboundQueueSize() {
        
        return MessageProcessor.getSingletonObject().outboundQueueSize();
    }

    @Override
    public int InternalMessageMapSize() {
        
        return MessageProcessor.getSingletonObject().internalMessageMapSize();
    }

    @Override
    public void PurgePolicyCache() {
        
        MessageProcessor.getSingletonObject().purgePolicyCache();
    }

    @Override
    public void PurgeOutboundQueue() {
        
        MessageProcessor.getSingletonObject().purgeOutboundQueue();
    }

    @Override
    public void PurgeMessageMap() {
        
        MessageProcessor.getSingletonObject().purgeMessageMap();
    }

    @Override
    public void ForceNewDataPusherThread() {
        
        MessageProcessor.getSingletonObject().forceNewDataPusherThread();
    }

    @Override
    public boolean IsDependencyInjectionEnabled() {
        
        return MessageProcessor.getSingletonObject().isDependencyInjectionEnabled();
    }

    @Override
    public int ThreadMapSize() {
        
        return MessageProcessor.getSingletonObject().getThreadMapSize();
    }

    @Override
    public void PurgeThreadMap() {
        
        MessageProcessor.getSingletonObject().purgeThreadMap();
    }

    @Override
    public String GetLastErrorMessage() {
        
        return MessageProcessor.getSingletonObject().getLastErrorMessage();
    }

    @Override
    public Long GetProcessedMessageCount() {
        
        return MessageProcessor.getSingletonObject().getTotalmessagesprocessed();
    }

    @Override
    public int URLMappingCacheSize() {
        
        return MessageProcessor.getSingletonObject().getURLaddressMap().size();
    }

    @Override
    public void PurgeURLMap() {
        
        MessageProcessor.getSingletonObject().getURLaddressMap().clear();
    }
}
