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

package org.miloss.fgsms.plugins.federation;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.miloss.fgsms.plugins.PluginCommon;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.dataaccessservice.QuickStatWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;

/**
 * Implementors provide the ability to publish summarized data from fgsms to
 * an external system on a periodic basis. Non periodic federation publishers
 * (such as a web service MUST also implement this interface. In this case, the
 * Publish method does nothing. Constructor functions should be non blocking.
 *
 * @author AO
 */
public interface FederationInterface extends PluginCommon {

    /**
     * This is called by both the user interface as well as whenever someone
     * attempts to add or change a service policy containing this federation
     * target.
     *
     * @param fp the proposed federation policy
     * @param outMessage returns the error message, if any. Null or empty string
     * can be returned if there is no error. The message should be a human
     * readable reason for why the policy is invalid or misconfigured.
     * @return True if the configuration is valid, false otherwise
     */
    public boolean ValidateConfiguration(FederationPolicy fp, AtomicReference<String> outMessage);

    /**
     * Called by a Quartz timer job
     *
     * @param pooled indicates where or not we are using pooled database
     * connections
     * @param data represents the last known data set for a web service,
     * availability info for all policy types
     * @param sp the complete service policy of the thing we are publishing
     * @param fp the federation policy, which should allow users to dictate
     * implementation specific parameters and settings
     * @see org.miloss.fgsms.common.Utility.GetConfigurationDBConnection
     */
    public void Publish(boolean pooled, QuickStatWrapper data, ServicePolicy sp, FederationPolicy fp);

    
}
