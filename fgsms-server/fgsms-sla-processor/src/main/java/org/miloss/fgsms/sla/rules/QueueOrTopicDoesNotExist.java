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
import org.miloss.fgsms.services.interfaces.policyconfiguration.StatisticalServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;

/**
 *
 * @author AO
 */
public class QueueOrTopicDoesNotExist implements SLARuleInterface {

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
        String value = null;
        if (val.isEncrypted()) {
            value = (Utility.DE(val.getValue()));
        } else {
            value = (val.getValue());
        }
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getQueueOrTopicCanonicalName().equalsIgnoreCase(value))
            {
                return false;
            }
        }
         msg.append("The queue or topic named ").append(value).append(" does not exist!");

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
        return "Message Broker Queue Size";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This rule will trigger if the specified queue or topic canonical name size does not exist in the policies statical message broker. "
                + "Applies to statistical message broker policies only.<br><br>"
                + "Required parameters:"
                + "<ul>"
                + "<li>value - the exact name, ignoring case of the queue or topic canonical name</li>"
                + "</ul>";
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(Utility.newNameValuePair("value", null, false, false));
        return arrayList;
    }


    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError,ServicePolicy policy) {
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
    public List<NameValuePair> GetOptionalParameters() {
       return new ArrayList<NameValuePair>();
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

        return Utility.encodeHTML(GetDisplayName() + " " + item );
    }
       
              @Override
    public List<PolicyType> GetAppliesTo() {
         List<PolicyType> x = new ArrayList<PolicyType>();
         x.add(PolicyType.STATISTICAL);
        
         
         return x;
    }
}

