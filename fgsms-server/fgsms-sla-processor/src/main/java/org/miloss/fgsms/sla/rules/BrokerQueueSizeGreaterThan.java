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
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.MachinePerformanceData;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.StatisticalServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;

/**
 *
 * @author AO
 */
public class BrokerQueueSizeGreaterThan implements SLARuleInterface {

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
        if (nullableFaultMsg == null) {
            nullableFaultMsg = new AtomicReference<String>();
        }
        NameValuePair val = Utility.getNameValuePairByName(params, "value");
        long value = 0;
        if (val.isEncrypted()) {
            value = Long.parseLong(Utility.DE(val.getValue()));
        } else {
            value = Long.parseLong(val.getValue());
        }
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getDepth() > value) {

                msg.append("The queue named ").append(data.get(i).getQueueOrTopicName()).append(" has a queue size of ").
                        append(data.get(i).getDepth()).append(" which is greater than the SLA parameter of ").append(value).append(". ");
            }
        }

        String s = msg.toString();
        if (Utility.stringIsNullOrEmpty(s)) {
            return false;
        } else {
            nullableFaultMsg.set(s + "," + nullableFaultMsg.get());
            return true;
        }
    }

    @Override
    public boolean CheckNonTransactionalRule(ServicePolicy pol, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg, boolean pooled) {
        return false;
    }

    @Override
    public String GetDisplayName() {
        return "Message Broker Queue Size is greater than ";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This rule will trigger if a message broker's queue size is larger than the specified value. "
                + "Applies to statistical message broker policies only.<br><br>"
                + "Required parameters:"
                + "<ul>"
                + "<li>value - some positive integer</li>"
                + "</ul>"
                + "Optional parameters:"
                + "<ul>"
                + "<li>name - Optional, if defined, this value must equal that of a specific queue or topic</li>"
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
        ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(Utility.newNameValuePair("name", null, false, false));
        return arrayList;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError, ServicePolicy policy) {
        if (outError == null) {
            outError = new AtomicReference<String>();
        }
        if (params == null || params.isEmpty()) {
            outError.set("The parameter 'value' is required. " + outError.get());
        }
        if (!(policy instanceof StatisticalServicePolicy)) {
            outError.set("This rule only applies to Statistical Service Policies. " + outError.get());
        }
        boolean foundLogger = false;
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getName().equals("value")) {
                foundLogger = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'value'. " + outError.get());
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
            if (params.get(i).getName().equals("name")) {
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'name'. " + outError.get());
                }
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
    public org.miloss.fgsms.plugins.sla.AlertType GetType() {
       return org.miloss.fgsms.plugins.sla.AlertType.Performance;
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
        
        NameValuePair mc2 = Utility.getNameValuePairByName(params, "name");
        String item2 = UNDEFINED_VALUE;
        if (mc2 != null) {
            item2 = mc2.getValue();
            if (mc2.isEncrypted() || mc2.isEncryptOnSave()) {
                item2 = ENCRYPTED_MASK;
            }
        }
        return Utility.encodeHTML(GetDisplayName() + " " + item + " on " + item2);
    }
     
       @Override
    public List<PolicyType> GetAppliesTo() {
         List<PolicyType> x = new ArrayList<PolicyType>();
         x.add(PolicyType.STATISTICAL);
         return x;
    }
}
