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

package org.miloss.fgsms.sla.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;
import org.miloss.fgsms.sla.SLACommon;

/**
 *
 * @author AO
 */
public class SLAActionLog implements SLAActionInterface {

    private static Logger log = Logger.getLogger("fgsms.SLAProcessor");

//    @Override
    public void ProcessAction(AlertContainer alert) {
        SendLogger(alert.getFaultMsg(), alert.getSlaActionBaseType(), alert.getModifiedurl(), alert.getIncidentid());

    }

    private static void SendLogger(String msg, SLAAction logAction, String url, String incident) {
        if (logAction != null) {
            NameValuePair GetNameValuePairByName = Utility.getNameValuePairByName(logAction.getParameterNameValue(), "Logger");
            if (GetNameValuePairByName != null) {
                Logger l = null;
                if (GetNameValuePairByName.isEncrypted()) {
                    l = Logger.getLogger(Utility.DE(GetNameValuePairByName.getValue()));
                } else {
                    l = Logger.getLogger((GetNameValuePairByName.getValue()));
                }
                l.log(Level.WARN, SLACommon.getBundleString("SLALoggerPrefix")
                        + url + " message: " + msg.toString() + " SLAFaultID: " + incident);
            }
        } else {
            log.warn("log action is null, defaulting");
            log.log(Level.WARN, SLACommon.getBundleString("SLALoggerPrefix")
                    + url + " message: " + msg.toString() + " SLAFaultID: " + incident);
        }

    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        r.add(Utility.newNameValuePair("Logger", null, false, false));
        return r;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError) {
        if (outError == null) {
            outError = new AtomicReference<String>();
        }
        if (params == null || params.isEmpty()) {
            outError.set("The parameter 'Logger' is required for action " + this.GetDisplayName() +". " + outError.get());
        }
        boolean foundLogger=false;
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getName().equals("Logger")) {
                foundLogger=true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'Logger'. " + outError.get());
                }
            }
        }
        if (!foundLogger)
            outError.set("The parameter 'Logger' is required. " + outError.get());
        if (Utility.stringIsNullOrEmpty(outError.get())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Record an event to log<Br>fgsms has a number of defined Loggers which can be used to configure"
                + "the Apache Log4j logging system to output logs to a wide variety of things, such as a File, Windows Event Log, Syslog and many more. "
                + "<Br><br>To use this plugin, you have define the following parameter:"
                + "<ul><li>Logger - Some built in loggers are: "
                + "<ul>"
                + "<li>fgsms.SLAProcessor.SysLog</li>"
                + "<li>fgsms.SLAProcessor.EventLog</li>"
                + "<li>fgsms.SLAProcessor.UdpLog</li>"
                + "<li>fgsms.SLAProcessor.FileLog</li></ul>"
                + "</li></ul>";
    }

    @Override
    public String GetDisplayName() {
        return "Record a Log message";
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
            SendLogger(alert.getFaultMsg(), alert.getSlaActionBaseType(), alert.getModifiedurl(), alert.getIncidentid());
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
       return Utility.getAllPolicyTypes();
    }
}
