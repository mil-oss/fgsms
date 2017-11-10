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
 * A MBean interface class for accessing the singleton message processor class
 * from JMX
 *
 * @author AO
 */
public interface MessageProcessorAdapterMBean {

    public String GetLastErrorMessage();

    public Long GetProcessedMessageCount();

    public void RemoveDeadMessage();

    public void Abort();

    public int GetPolicyCache();

    public int OutboundQueueSize();

    public int InternalMessageMapSize();

    public void PurgePolicyCache();

    public void PurgeOutboundQueue();

    public void PurgeMessageMap();

    public void ForceNewDataPusherThread();

    public boolean IsDependencyInjectionEnabled();

    public int ThreadMapSize();

    public void PurgeThreadMap();
    
    public int URLMappingCacheSize();
    
    public void PurgeURLMap();
}
