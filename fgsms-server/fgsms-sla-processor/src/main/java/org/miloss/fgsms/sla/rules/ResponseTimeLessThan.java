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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertType;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;

/**
 *
 * @author AO
 */
public class ResponseTimeLessThan implements SLARuleInterface {

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
        if (nullableFaultMsg == null) {
            nullableFaultMsg = new AtomicReference<String>();
        }
        NameValuePair GetNameValuePairByName = Utility.getNameValuePairByName(params, "value");
        long val = 0;
        String item = GetNameValuePairByName.getValue();
        if (GetNameValuePairByName.isEncrypted()) {
            item = Utility.DE(GetNameValuePairByName.getValue());
        }
        val = Long.parseLong(item);
        if (req.getResponseTime() < val) {
            nullableFaultMsg.set("The response time size, " + req.getResponseTime() + ", is less that the value " + val + ", " + nullableFaultMsg.get());
        }
        return false;
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
    public String GetDisplayName() {
        return "Response time is less than value";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "If a transaction's response time is less than the specified value, then this rule will trigger.<Br>"
                + "Applies to transactional service policies only.<br><br>"
                + "Required parameters:"
                + "<ul>"
                + "<li>value - a positive integer to compare against (ms)</li>"
                + "</ul>";
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(Utility.newNameValuePair("value", null, false, false));
        return arrayList;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError, ServicePolicy policy) {
        if (outError == null) {
            outError = new AtomicReference<String>();
        }
        if (params == null || params.isEmpty()) {
            outError.set("The parameter 'value' is required. " + outError.get());
        }
        if (!(policy instanceof TransactionalWebServicePolicy)) {
            outError.set("This rule only applies to Transactional Service Policies. " + outError.get());
        }
        boolean foundLogger = false;
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getName().equals("value")) {
                foundLogger = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'value'. " + outError.get());
                }
            }
            try {
                long x = Long.parseLong(params.get(i).getValue());
                if (x <= 0) {
                    outError.set("The parameter 'value' must be greater than zero. " + outError.get());
                }
            } catch (Exception ex) {
                outError.set("Bad value for parameter 'value'. It must be a integer or long. Message:" + ex.getMessage() + ". " + outError.get());
            }
        }
        if (!foundLogger) {
            outError.set("The parameter 'value' is required. " + outError.get());
        }
        if (Utility.stringIsNullOrEmpty(outError.get())) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public AlertType GetType() {
        return AlertType.Performance;
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
        return Utility.encodeHTML(GetDisplayName() + " " + item+"ms");
    }
    
    @Override
    public List<PolicyType> GetAppliesTo() {
        List<PolicyType> ret = new ArrayList<PolicyType>();
        ret.add(PolicyType.TRANSACTIONAL);
        return ret;
    }
}
