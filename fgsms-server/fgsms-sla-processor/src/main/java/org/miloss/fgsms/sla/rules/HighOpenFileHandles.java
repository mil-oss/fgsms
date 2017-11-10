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
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachinePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ProcessPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;

/**
 *
 * @author AO
 */
public class HighOpenFileHandles implements SLARuleInterface {
    
    @Override
    public boolean CheckTransactionalRule(SetStatusRequestMsg req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        return false;
    }
    
    @Override
    public boolean CheckTransactionalRule(ProcessPerformanceData req, List<NameValuePair> params, AtomicReference<String> nullableFaultMsg) {
        if (nullableFaultMsg != null) {
            nullableFaultMsg = new AtomicReference<String>();
        }
        NameValuePair value = Utility.getNameValuePairByName(params, "value");
        int x = 0;
        if (value.isEncrypted()) {
            x = Integer.parseInt(Utility.DE(value.getValue()));
        } else {
            x = Integer.parseInt(value.getValue());
        }
        if (x > req.getOpenFileHandles()) {
            nullableFaultMsg.set("The number of open file handles for a process, " + req.getOpenFileHandles() + " is higher that the threshold value of " + x + ", " + req.getOpenFileHandles());
            return true;
        }
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
        return false;
    }
    
    @Override
    public String GetDisplayName() {
        return "High Open File Handles Count";
    }
    
    @Override
    public String GetHtmlFormattedHelp() {
        return "This rule will trigger when a process has a high number of open file handles. This applies to process service policies only.<br><br>"
                + "Required Parameters:"
                + "<ul>"
                + "<li>value - some positive integer value representing the threshold such as if the open file count is greater than the value, the rule is triggered.</li>"
                + "</ul>";
        
    }
    
    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> items = new ArrayList<NameValuePair>();
        items.add(Utility.newNameValuePair("value", null, false, false));
        return items;
    }
    
    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return new ArrayList<NameValuePair>();
    }
    
    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError,ServicePolicy policy) {
        if (outError == null) {
            outError = new AtomicReference<String>();
        }
        if (params == null || params.isEmpty()) {
            outError.set("The parameter 'value'  is required. " + outError.get());
        }
             if (!(policy instanceof ProcessPolicy)) {
            outError.set("This rule only applies to Process Policies. " + outError.get());
        }
        boolean foundSubject = false;
        
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getName().equals("value")) {
                foundSubject = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'value'. " + outError.get());
                }
                
                try {
                    int x = -1;
                    if (params.get(i).isEncrypted()) {
                        x = Integer.parseInt(Utility.DE(params.get(i).getValue()));
                    } else {
                        x = Integer.parseInt((params.get(i).getValue()));
                    }
                    if (x < 0) {
                        outError.set("The parameter 'value'  must be greater than zero. " + outError.get());
                    }
                } catch (Exception ex) {
                    outError.set("Could not parse the value of  'value'  to an integer. Error: " + ex.getMessage() + ". " + outError.get());
                }
            }
            
        }
        if (!foundSubject) {
            outError.set("The parameter 'value'  is required. " + outError.get());
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
        
      
        return Utility.encodeHTML(GetDisplayName() + " " + item);
    }
       
         @Override
    public List<PolicyType> GetAppliesTo() {
         List<PolicyType> x = new ArrayList<PolicyType>();
         x.add(PolicyType.MACHINE);
         x.add(PolicyType.PROCESS);
         return x;
    }
}
