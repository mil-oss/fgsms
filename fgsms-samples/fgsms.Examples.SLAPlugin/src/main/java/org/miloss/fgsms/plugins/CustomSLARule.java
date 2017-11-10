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
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.plugins;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertType;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;

/**
 * This is a sample plugin for SLA Rules. In this case, all web service
 * transactions are triggered as SLA faults.
 *
 * @author AO
 */
public class CustomSLARule implements SLARuleInterface {

    @Override
    public boolean CheckTransactionalRule(SetStatusRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(AddDataRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        /*String x;
        String val = null;
        KeyNameValueEnc setting = DBSettingsLoader.GetPropertiesFromDB(true, "MyPlugin", "MyName");
        if (setting != null && setting.getKeyNameValue() != null) {
            val = setting.getKeyNameValue().getPropertyValue();
        }
        AuditLogger.LogItem(this.getClass().getCanonicalName(), "CheckRule", "system", "my message", new SecurityWrapper(), null);
        Connection con = Utility.getPerformanceDBConnection();
        try {
            //do something
        } catch (Exception ex) {
            //log some message
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
        nullableFaultMsg.set("Hello World from " + this.getClass().getCanonicalName());
        */
        return true;
    }

    @Override
    public boolean CheckTransactionalRule(String url, List<BrokerData> data, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckNonTransactionalRule(ServicePolicy pol, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg, boolean pooled) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(ProcessPerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public String GetDisplayName() {
        return "Hello World Custom Rule";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Help! for Hello World Custom Rule";
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public boolean CheckTransactionalRule(MachinePerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError, ServicePolicy policy) {
        return true;
    }

    @Override
    public AlertType GetType() {
        return AlertType.Performance;
    }

    @Override
    public String GetHtmlFormattedDisplay(List<NameValuePair> params) {
        return GetDisplayName() + " some logical value to compare against";
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        return Utility.getAllPolicyTypes();
    }

}
