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
/*  ----------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Command
 *  Command, Power and Intergration Directorate
 *  ----------------------------------------------------------------------------
 *  fgsms - Government Open Source Software
 *  ----------------------------------------------------------------------------
 *  Author:     AO
 *	Website: 	https://github.com/mil-oss/fgsms
 *  ----------------------------------------------------------------------------
 */
package org.miloss.fgsms.plugins.sla;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.miloss.fgsms.plugins.PluginCommon;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;

/**
 * Service Level Agreement Rule Plugin Interface - This will allow
 * developers to add custom Rules into fgsms. <p>All implementations
 * should be stateless</p> <p><h2>Deployment Instructions</h2> To deploy your
 * own implementation of an SLARuleInterface implementation, build your jar file
 * and any supporting jars and copy them to each fgsms server that contains
 * fgsms.AuxServices.war and fgsmsServices.war in the WEB-INF/lib folder
 * </p> <p><h2>To use your implementation</h2> Use the PCS web service to set
 * the service policy for a specific web service, adding an SLA with a
 * SLARuleGeneric as the rule and at least one action defined. Use
 * SLARuleGeneric to define the implementing class as your implementing class.
 * Optionally, specify any per service settings necessary for making a rule
 * determination. This can also be done from fgsmsWeb and fgsmsBootstrap web
 * user interfaces. </p>
 *
 * @author AO
 * @since 6.2
 * @see org.miloss.fgsms.services.interfaces.policyconfiguration.PCS
 * @see
 * org.miloss.fgsms.services.interfaces.policyconfiguration.SLARuleGeneric
 * @see
 * org.miloss.fgsms.services.interfaces.policyconfiguration.SLAActionGeneric
 * @see org.miloss.fgsms.sla.NonTransactionalSLAProcessor
 * @see
 * org.miloss.fgsms.services.interfaces.policyconfiguration.RuleBaseType
 * @see org.miloss.fgsms.common.Utility
 * @see org.miloss.fgsms.common.AuditLogger
 * @see org.miloss.fgsms.common.DBSettingsLoader
 * @see org.miloss.fgsms.sla.SLACommon
 * @see
 * org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyRequestMsg
 * @see
 * org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy
 * @see org.miloss.fgsms.services.interfaces.policyconfiguration.SLA
 */
public interface SLARuleInterface extends PluginCommon{

    /**
     * Returns True is the rule's conditions have been met (SLA Violation) This
     * represents when a Status message comes in to the status service, such as
     * from an OS agent. This function should never throw an exception
     *
     * @param req Set Status message
     * @param params per service policy, rule instance parameters this can be
     * null
     * @param nullableFaultMsg the human readable text explaining what happened
     * or why the rule was triggered
     * @return
     */
    public boolean CheckTransactionalRule(SetStatusRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg);

    /**
     * Returns True is the rule's conditions have been met (SLA Violation)
     * Process Performance data This function should never throw an exception
     *
     * @param req process performance data
     * @param params per service policy, rule instance parameters this can be
     * null
     * @param nullableFaultMsg the human readable text explaining what happened
     * or why the rule was triggered
     * @return
     */
    public boolean CheckTransactionalRule(ProcessPerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg);

    /**
     * Returns True is the rule's conditions have been met (SLA Violation)
     * Machine Performance data This function should never throw an exception
     *
     * @param req machine performance data
     * @param params per service policy, rule instance parameters this can be
     * null
     * @param nullableFaultMsg the human readable text explaining what happened
     * or why the rule was triggered
     * @return
     */
    public boolean CheckTransactionalRule(MachinePerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg);
   
    
    
    /**
     * Returns True is the rule's conditions have been met (SLA Violation) Web
     * Service Transaction performance data This function should never throw an
     * exception
     *
     * @param req web service transaction data
     * @param params per service policy, rule instance parameters this can be
     * null
     * @param nullableFaultMsg the human readable text explaining what happened
     * or why the rule was triggered
     * @return
     */
    public boolean CheckTransactionalRule(AddDataRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg);

    /**
     * Returns True is the rule's conditions have been met (SLA Violation)
     * Statistical Message Broker data This function should never throw an
     * exception
     *
     * @param url The url of the broker in question
     * @param data BrokerData, generally a list of queue, topics and their
     * associated metrics
     * @param params per service policy, rule instance parameters this can be
     * null
     * @param nullableFaultMsg the human readable text explaining what happened
     * or why the rule was triggered
     * @return
     */
    public boolean CheckTransactionalRule(String url, List<BrokerData> data, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg);

    /**
     * Returns True is the rule's conditions have been met (SLA
     * Violation).  <p>This is used for non transactional rules.
     * Implementors should access the database and perform whatever calculations
     * are required in order to determine if the rule's conditions are met or
     * not.</p> <p>This function should never throw an exception.</p> <p>The
     * calling function is typically the NonTransactionalSLAProcessor which
     * operates on a set schedule that can be adjusted by administrators. It
     * operates using the Quarts schedulers</p> <p>Since no data is provided, an
     * implementor is expected to access data on their own. To access global
     * settings</p> <p> <h2>Open the fgsms databases</h2>
     * <pre>
     * Connection con =null;
     * if (pooled)
     *  con= Utility.GetPerformanceDBConnection();
     * else
     *  con = Utility.GetPerformanceDB_NONPOOLED_Connection();
     * //or use Utility.ConfigurationDBConnection(); for the configuration database
     * try {
     * // do something
     * } catch (Exception ex) {
     * // log some message
     * } finally {
     * if (con != null) {
     * try {
     * con.close();
     * } catch (Exception e) {
     * }
     * }
     * }
     * </pre> </p>
     *
     * @param pol This is a reference the service policy that this rule is
     * attached to. Do not modify, unexpected results may occur
     * @param params per service policy, rule instance parameters this can be
     * null
     * @param nullableFaultMsg the human readable text explaining what happened
     * or why the rule was triggered
     * @param pooled Indicates the usage of pooled database connections or not
     * @return true if the rule indicates that a problem has occurred
     * @see org.miloss.fgsms.sla.NonTransactionalSLAProcessor
     * @see
     * org.miloss.fgsms.services.interfaces.policyconfiguration.RuleBaseType
     * @see org.miloss.fgsms.common.Utility
     * @see org.miloss.fgsms.common.AuditLogger
     * @see org.miloss.fgsms.common.DBSettingsLoader
     * @see org.miloss.fgsms.sla.SLACommon
     * @see
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyRequestMsg
     * @see
     * org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy
     * @see org.miloss.fgsms.services.interfaces.policyconfiguration.SLA
     */
    public boolean CheckNonTransactionalRule(ServicePolicy pol, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg, boolean pooled);

   /**
     * Validates the configuration settings. This is called by both the user
     * interface as well as whenever someone attempts to add or change a service
     * policy containing this federation target.
     *
     * @since 6.3
     * @param params parameters for this instance for a SLARule for validation
     * @param outError human readable error message representing whatever the problem areas are
     * @param policy a reference to the proposed service policy
     * @return True if the configuration is valid, false otherwise
     */
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError,ServicePolicy policy);
    
    /**
     * If the rule is triggered, this defines what type of rule this is. This is used to determine
     * which type of Alert Type is sent to subscribers
     * @return 
     */
    public AlertType GetType();
    
    /**
     * returns a html encoded string contain usually the rule name plus the configured values. Encrypted values
     * should use the field ENCRYPTED_MASK. Input params list may be from direct user input, therefore you should
     * validate the input before processing.
     * 
     * Html is allowed, javascript is not
     * 
     * example:
     * "Response time is greater than 50 ms"
     * "Consumer Identity contains (encrypted)
     * @param params
     * @return 
     * @see StringEscapeUtils, Utility
     */
    public String GetHtmlFormattedDisplay(List<NameValuePair> params);
    
    public final String ENCRYPTED_MASK = "(encrypted)";
    public final String UNDEFINED_VALUE = "(undefined)";
}
