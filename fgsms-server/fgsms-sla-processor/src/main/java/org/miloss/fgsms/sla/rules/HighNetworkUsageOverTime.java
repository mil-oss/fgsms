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
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachinePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;
import org.miloss.fgsms.sla.NonTransactionalSLAProcessor;

/**
 *
 * @author AO
 */
public class HighNetworkUsageOverTime implements SLARuleInterface {

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
        NameValuePair GetNameValuePairByName = Utility.getNameValuePairByName(params, "value");
        long rate = Long.parseLong(GetNameValuePairByName.getValue());
        GetNameValuePairByName = Utility.getNameValuePairByName(params, "duration");
        long duration = Long.parseLong(GetNameValuePairByName.getValue());
        long faultrate = NonTransactionalSLAProcessor.GetDiskUsageOverTime(pol.getURL(), duration, pooled);
        if (faultrate > rate) {
            nullableFaultMsg.set("The measured Disk I/O rate, " + faultrate + " is greater than " + rate + ", " + nullableFaultMsg.get());
            return true;
        }
        return false;
    }

    @Override
    public String GetDisplayName() {
        return "High Disk I/O Rates";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This rule will trigger when the Network I/O utilization rate is greater than the specified value for the specified duration."
                + "This rule is processed periodically as part of the Non-Transactional SLA Processor, who execution cycle is controled by the administrator. "
                + "This rule applies to Machine policies.<br><br>"
                + "Required parameters:"
                + "<ul>"
                + "<li>value - must be a positive integer. This is the threshold in KB</li>"
                + "<li>duration - must be a positive integer or long. This represents time in milliseconds for the duration. It must be one of the values from which"
                + "statistics are aggregated. By default, the following durations are calculated 5 minutes (300000ms), 15 minutes (900000ms), 1 hour (3600000ms),"
                + "and 24 hours (1440000ms). Administrators can add additional time periods for aggregation via General Settings."
                + "</il>"
                + "</ul>";
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
        arrayList.add(Utility.newNameValuePair("value", null, false, false));
        arrayList.add(Utility.newNameValuePair("duration", null, false, false));

        return arrayList;
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
            outError.set("The parameter 'value' is required. " + outError.get());
        }
             if (!(policy instanceof MachinePolicy)) {
            outError.set("This rule only applies to Machine Policies. " + outError.get());
        }
        boolean foundLogger = false;
        boolean foundduration = false;
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
                    outError.set("Bad value for parameter 'value'. It must be an integer or long. Message:" + ex.getMessage() + ". " + outError.get());
                }
            }
            if (params.get(i).getName().equals("duration")) {
                foundduration = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'duration'. " + outError.get());
                }
                try {
                    long x = Long.parseLong(params.get(i).getValue());
                    if (x <= 0) {
                        outError.set("The parameter 'duration' must be greater than zero. " + outError.get());
                    }
                } catch (Exception ex) {
                    outError.set("Bad value for parameter 'duration'. It must be an integer or long. Message:" + ex.getMessage() + ". " + outError.get());
                }
            }
        }
        if (!foundLogger) {
            outError.set("The parameter 'value' is required. " + outError.get());
        }
        if (!foundduration) {
            outError.set("The parameter 'duration' is required. " + outError.get());
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
        
        NameValuePair mc2 = Utility.getNameValuePairByName(params, "duration");
        String item2 = UNDEFINED_VALUE;
        if (mc2 != null) {
            item2 = mc2.getValue();
            if (mc2.isEncrypted() || mc2.isEncryptOnSave()) {
                item2 = ENCRYPTED_MASK;
            }
        }
        return Utility.encodeHTML(GetDisplayName() + " " + item + "/" + item2+"ms");
    }
   
     @Override
    public List<PolicyType> GetAppliesTo() {
         List<PolicyType> x = new ArrayList<PolicyType>();
         x.add(PolicyType.MACHINE);
      
         
         return x;
    }
}
