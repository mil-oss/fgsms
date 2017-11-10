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
import org.miloss.fgsms.services.interfaces.common.NameValuePair;

/**
 * Service Level Agreement Action Plugin Interface - This will allow
 * developers to add custom actions into fgsms. *<p>All
 * implementations should be stateless</p> <p><h2>Deployment Instructions</h2>
 * To deploy your own implementation of an SLAActionInterface implementation,
 * build your jar file and any supporting jars and copy them to each fgsms
 * server that contains fgsms.AuxServices.war and fgsmsServices.war in the
 * WEB-INF/lib folder. For actions that apply to agents, also copy your files to
 * the lib folder of each supported agent and then update the classpath (if
 * necessary). </p> <p><h2>To use your implementation</h2> Use the PCS web
 * service to set the service policy for a specific web service, adding an SLA
 * with at least one rule and an action as SLAAction. Use SLAAction to define
 * the implementing class as your implementing class. Optional, provide any
 * context variables. This can also be done from fgsmsWeb and fgsmsBootstrap
 * web user interfaces. </p> <p> Note: SLA Actions can be processed at the
 * fgsms server or at the Agent. </p>
 *
 * @author AO
 * @since 6.2, heavily modified at 6.3
 * @see org.miloss.fgsms.services.interfaces.policyconfiguration.PCS
 * @see
 * org.miloss.fgsms.services.interfaces.policyconfiguration.SLARuleGeneric
 * @see org.miloss.fgsms.plugins.sla.SLARuleInterface
 * @see org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction
 */
public interface SLAActionInterface extends PluginCommon{

    /**
     *
     * <p>Process an action. This could do anything you want it to do.
     * Such as call a web service, send an email, an SMS or a JMS
     * message.</p>
     *
     * <p>This function should never throw an unhandled exception. Please do
     * your best.</p>
     *
     * <p>Here's a few useful examples for getting configuration settings from
     * fgsms that you'll probably need that aren't provided with the
     * parameters to this interface.</p> <p> <h2>Get Configurating Settings,
     * also (General Settings from the GUI)</h2> <p>As a developer, you have two
     * choices for getting access to properties that can be used to accomplish
     * your tasks. <ol> <li>Access the General Settings table from the fgsms
     * config database. The General Settings capability of fgsms (as of 6.0)
     * provides access to global settings that can be used for any purpose. Each
     * setting is easily configurable via web service or via the web user
     * interfaces fgsmsWeb and fgsmsBootstrap. These settings are per
     * instance of fgsms, which translates to per configuration database. See
     * the example below for a how-to.</li> <li>Access the Per SLA Plugin Action
     * settings - These settings are configured Per Service, Per SLA, Per SLA
     * Plugin Action. These can be configured via web service
     * SetServicePolicyRequestMsg. You can access this through the parameter
     * parameterNameValue. </li> </ol> </p> <h3>Example: Obtaining General
     * Settings (per fgsms instance)</h3>
     * <pre>
     * String val=null;
     * KeyNameValueEnc setting = DBSettingsLoader.GetPropertiesFromDB(true, "MyPlugin", "MyName");
     * if (setting!=null && setting.getKeyNameValue()!=null)
     * {
     *   val=setting.getKeyNameValue().getPropertyValue();
     * }
     * //use val for something, log if it's null
     * </pre>Note: The value could be encrypted. </p>
     *
     * <p> <h2>Encrypting/Decrypting a setting</h2> When obtaining general
     * settings, or perhaps in per SLA Action settings, passwords and other
     * sensitive data may need to be stored. If it is encrypted using fgsms's
     * encryption module (AES256), it can be decrypted using the following
     * example. If the key is not encrypted, the original value is returned. See
     * the documentation from Utility. Use care when decrypting sensitive data,
     * this data should not remain in memory for extended periods of time in an
     * unencrypted state
     * <pre>
     * String val=Utility.DE(val);
     * </pre> </p> <p> <h2>Audit Logging</h2>
     * <pre>AuditLogger.LogItem(this.getClass().getCanonicalName(), "CheckRule", "system", "my message", new SecurityWrapper(), null);
     * </pre> </p>
     *
     * <p> <h2>Getting the status of something</h2> Sometimes alerts are
     * triggered on a status change, but it's may not be obvious as to whether
     * the current status is OK or not. Use the following code snippet to make
     * that determination. Null values may be returned depending on whether or
     * not status information is available.
     * <pre>
     *   SLACommon.GetCurrentStatus((boolean pooled, String policyurl, Boolean out_status, Long out_timestamp, String out_message);
     * </pre> </p> <p> <h2>Open the fgsms databases</h2>
     * <pre>
     * Connection con = Utility.GetPerformanceDBConnection();
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
     *
     * @see AlertContainer
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
     * @see
     * org.miloss.fgsms.services.interfaces.policyconfiguration.SLAActionGeneric
     * @see org.miloss.fgsms.services.interfaces.policyconfiguration.SLA
     * @param alert This alert container has a reference to the policy. Do not
     * make modifications to it, unpredictable results may occur. Also provided
     * is a human readable fault message, the actual Rule(s)
     *
     *
     * that caused the trigger to occur, (note: this may be another plugin) and
     * various other parameters. The AlertContainer should never be null.
     * @param params  
     *
     *
     */
    public void ProcessAction(AlertContainer alert,List<NameValuePair> params);

   
    

    /**
     * validates the configuration before saving it. This is called by both the
     * user interface as well as whenever someone attempts to add or change a
     * service policy containing this federation target.
     *
     * @since 6.3
     * @param params
     * @param outError
     * @return True if the configuration is valid, false otherwise
     */
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError);

    
}
