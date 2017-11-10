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

package org.miloss.fgsms.sla.rules;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Calendar;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertType;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;
import org.miloss.fgsms.sla.NonTransactionalSLAProcessor;
import org.miloss.fgsms.sla.SLACommon;

/**
 * This rule is triggered when data becomes stale. I.e. an agent stopped
 * responding or functioning, a server went offline. This behaves different
 * depending on what the policy type of the item is Transactional Web
 * Service:with bueller or health check enabled, triggered when bueller or
 * health check fails and stops running Statistical Message Broker, triggered
 * when the machine goes offine (Qpid Py only), all others, when the agent is no
 * longer configured to access the component or if the agent stops
 * Process/Machine, triggered when the machine goes offline or the agent is
 * stopped
 *
 * @author AO
 */
public class StaleData implements SLARuleInterface {

    @Override
    public boolean CheckTransactionalRule(SetStatusRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(ProcessPerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(MachinePerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(AddDataRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckTransactionalRule(String url, List<BrokerData> data, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }

    @Override
    public boolean CheckNonTransactionalRule(ServicePolicy pol, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg, boolean pooled) {

        if (nullableFaultMsg == null) {
            nullableFaultMsg = new AtomicReference<String>();
        }
        StringBuilder faultMsg = new StringBuilder();
        NameValuePair val = Utility.getNameValuePairByName(params, "value");
        long threshold = 0;
        if (val != null) {
            if (val.isEncrypted()) {
                threshold = Long.parseLong(Utility.DE(val.getValue()));
            } else {
                threshold = Long.parseLong(val.getValue());
            }
        } else {
            KeyNameValueEnc GetPropertiesFromDB = DBSettingsLoader.GetPropertiesFromDB(pooled, "NTSLAProcessor", "StaleDataThreshold");
            if (GetPropertiesFromDB != null && GetPropertiesFromDB.getKeyNameValue() != null && GetPropertiesFromDB.getKeyNameValue().getPropertyValue() != null) {
                try {
                    if (GetPropertiesFromDB.isShouldEncrypt()) {
                        threshold = Long.parseLong(Utility.DE(GetPropertiesFromDB.getKeyNameValue().getPropertyValue()));
                    } else {
                        threshold = Long.parseLong(GetPropertiesFromDB.getKeyNameValue().getPropertyValue());
                    }
                } catch (Exception x) {
                }
            }
        }
        if (threshold < 1) {
            threshold = 300000; //default of 5 minutes
        }

        long NTSLAInterval = 300000;
        KeyNameValueEnc GetPropertiesFromDB = DBSettingsLoader.GetPropertiesFromDB(pooled, "NTSLAProcessor", "Interval");
        if (GetPropertiesFromDB != null && GetPropertiesFromDB.getKeyNameValue() != null && GetPropertiesFromDB.getKeyNameValue().getPropertyValue() != null) {
            try {
                NTSLAInterval = Long.parseLong(GetPropertiesFromDB.getKeyNameValue().getPropertyValue());
            } catch (Exception x) {
            }
        }
        //find the oldest status record
        AtomicReference<Boolean> status = new AtomicReference<Boolean>(false);
        long laststatusat = NonTransactionalSLAProcessor.GetLastKnownStatus(pol.getURL(), pooled, status);
        if (System.currentTimeMillis() - laststatusat > threshold) {

            if ((System.currentTimeMillis() - NTSLAInterval) > threshold) {
                //TODO this is wrong
                //no need to process alerts if the delta is greater than the threshold + NTSLA runner (assuming NTSLA runner is 100% uptime)
                //this means that the alert was previously processed, skip it to prevent mail bombs
                return false;
            }
            DatatypeFactory df;
            try {
                df = DatatypeFactory.newInstance();
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(laststatusat);
                Calendar xcal = gcal;
                faultMsg.append(SLACommon.getBundleString("ConsideredStale")).append(" ").append(xcal.toString()).append(" which was ").append(Utility.durationToString(df.newDuration(System.currentTimeMillis() - laststatusat))).append(" ago.");
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(NonTransactionalSLAProcessor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                faultMsg.append(SLACommon.getBundleString("ConsideredStale")).append(" ").append(laststatusat).append("ms (Epoch) ago.");
            }

            nullableFaultMsg.set(faultMsg.toString() + ", " + nullableFaultMsg.get());
            return true;
        }
        return false;
    }

    @Override
    public String GetDisplayName() {
        return "Stale Data";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "The Stale Data rule is used to help alert people that there is either a problem with an agent, or a problem with the computer hosting the agent."
                + "This typically triggers when a time threshold is reached when no data has been received by the agent. The threshold is administrator"
                + "controlled via General Settings. Applies to all policy types. If the administator has not defined the default value, 300000 (5 minutes) is used.<br><br>"
                + "Optional Parameter:"
                + "<ul>"
                + "<li>value - overrides the administrator defined default (time in ms)</li>"
                + "</ul>";
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        List<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(Utility.newNameValuePair("value", null, false, false));
        return arrayList;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError, ServicePolicy sp) {
        return true;
    }

    @Override
    public AlertType GetType() {
        return AlertType.Status;
    }

    @Override
    public String GetHtmlFormattedDisplay(List<NameValuePair> params) {
        NameValuePair mc = Utility.getNameValuePairByName(params, "value");
        String item = UNDEFINED_VALUE;
        if (mc != null) {
            item = mc.getValue();
            if (mc.isEncrypted() || mc.isEncryptOnSave()) {
                item = ENCRYPTED_MASK;
            }
        }
        if (item == null) {
            KeyNameValueEnc GetPropertiesFromDB = DBSettingsLoader.GetPropertiesFromDB(true, "NTSLAProcessor", "Interval");
            if (GetPropertiesFromDB != null) {
                if (GetPropertiesFromDB.isShouldEncrypt()) {
                    item = ENCRYPTED_MASK;
                } else {
                    item = GetPropertiesFromDB.getKeyNameValue().getPropertyValue();
                }
            }
        }
        if (Utility.stringIsNullOrEmpty(item)) {
            item = "300000";
        }
        return GetDisplayName() + " older than " + Utility.encodeHTML(item) + " ms";
    }
    
           @Override
    public List<PolicyType> GetAppliesTo() {
        return Utility.getAllPolicyTypes();
    }
}
