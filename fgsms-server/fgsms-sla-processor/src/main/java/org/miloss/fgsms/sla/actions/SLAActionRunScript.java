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
package org.miloss.fgsms.sla.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RunAtLocation;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;
import org.miloss.fgsms.sla.SLACommon;

/**
 *
 * @author AO
 */


public class SLAActionRunScript implements SLAActionInterface {

    private static Logger log = Logger.getLogger("fgsms.SLAProcessor");
    public static final String RUNAT = "runAt";

    //   @Override
    public void ProcessAction(AlertContainer alert) {

    }

    private static void runScript(String faultMsg, SLAAction slaActionRunScript, String modifiedurl, String incidentid) {
        String path = null;
        String command = null;
        String runat = null;
        NameValuePair nvcommand = Utility.getNameValuePairByName(slaActionRunScript.getParameterNameValue(), "From");
        if (nvcommand != null) {
            if (nvcommand.isEncrypted()) {
                command = Utility.DE(nvcommand.getValue());
            } else {
                command = nvcommand.getValue();
            }
        }
        NameValuePair nvrunat = Utility.getNameValuePairByName(slaActionRunScript.getParameterNameValue(), "From");
        if (nvrunat != null) {
            if (nvrunat.isEncrypted()) {
                runat = Utility.DE(nvrunat.getValue());
            } else {
                runat = nvrunat.getValue();
            }
        }
        NameValuePair nvpath = Utility.getNameValuePairByName(slaActionRunScript.getParameterNameValue(), "From");
        if (nvpath != null) {
            if (nvpath.isEncrypted()) {
                path = Utility.DE(nvpath.getValue());
            } else {
                path = nvpath.getValue();
            }
        }

        Runtime run = Runtime.getRuntime();
        if (runat != null && runat.equalsIgnoreCase(RunAtLocation.FGSMS_SERVER.toString())) {
            Process pr = null;
            try {
                String[] env = new String[]{"SLA_MESSAGE=" + faultMsg, "SLA_URL=" + modifiedurl, "SLA_ID=" + incidentid};
                if (!Utility.stringIsNullOrEmpty(path)) {
                    File f = new File(path);

                    if (f.exists()) {
                        pr = run.exec(command, env, f);
                    } else {
                        pr = run.exec(command, env);
                    }
                } else {
                    pr = run.exec(command, env);
                }
            } catch (Exception ex) {
                log.log(Level.WARN, SLACommon.getBundleString("ErrorUnableToRunSLAScript"), ex);
            } finally {
            }
        }

    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        r.add(Utility.newNameValuePair("command", null, false, false));
        r.add(Utility.newNameValuePair("runFromPath", null, false, false));
        r.add(Utility.newNameValuePair(RUNAT, null, false, false));
        return r;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        return r;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError) {
        if (outError == null) {
            outError = new AtomicReference<String>();
        }
        if (params == null || params.isEmpty()) {
            outError.set("The parameter 'Subject' and 'Body' is required. " + outError.get());
        }
        boolean foundcommand = false;
        boolean foundrunat = false;
        boolean foundpath = false;
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).getName().equals("command")) {
                foundcommand = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'command'. " + outError.get());
                }
            }
            if (params.get(i).getName().equals(RUNAT)) {
                foundrunat = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'runAtLocation'. " + outError.get());
                } else {
                    if (!params.get(i).getValue().equalsIgnoreCase("FGSMS_SERVER")
                            && !params.get(i).getValue().equalsIgnoreCase("FGSMSF_AGENT")) {
                        outError.set("The value must be specified for the parameter 'runAtLocation' must either be FGSMS_SERVER or FGSMS_AGENT. " + outError.get());
                    }
                }
            }
            if (params.get(i).getName().equals("runFromPath")) {
                foundrunat = true;
                if (Utility.stringIsNullOrEmpty(params.get(i).getValue())) {
                    outError.set("A value must be specified for the parameter 'runFromPath'. " + outError.get());
                }
            }

        }
        if (!foundrunat || !foundcommand) {
            outError.set("The parameter 'Subject' and 'Body' is required. " + outError.get());
        }
        if (Utility.stringIsNullOrEmpty(outError.get())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This plugin will enable you to run a program, script or command when the rule set is triggered. This can be used to reboot computers, services, processes, "
                + "or to call a 3rd party cool without writing code. Note: because of security restrictions, only global administrators can add this"
                + " plugin. Once its added, only administrators can then alter the service policy, except during removal of this action. "
                + "Required settings:"
                + "<ul>"
                + "<li>command - this is what's passed to the operating system to shell or command prompt</li>"
                + "<li>runFromPath - this is working directory for when the command is executed</li>"
                + "<li>runAt - This must be one of two values, FGSMS_SERVER or FGSMS_AGENT. This tells the SLA Processor where the script or command is supposed to run from."
                + "FGSMS_SERVER - The script runs at one of the fgsms servers. This could be from a server hosting fgsms's Web services or fgsms's Aux services."
                + "FGSMS_AGENT - The script runs at the agent level, such as Operating System Agent, or the Qpid C++ Agent. Note: if the SLA Rule is triggered at a location"
                + "other than the runAtLocation, the script or command will not be executed. </li>"
                + "</ul>"
                + "The following environment variables are set that can be referenced in your script or command"
                + "<ul>"
                + "<li>SLA_MESSAGE - the fault message as defined by the rule set</li>"
                + "<li>SLA_URL - the fgsms Service Policy URL</li>"
                + "<li>SLA_ID - the recorded SLA Incident ID. This is not available when running at the Agent level</li>"
                + "</ul>";
    }

    @Override
    public String GetDisplayName() {
        return "Run a program or script";
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
        runScript(alert.getFaultMsg(), alert.getSlaActionBaseType(), alert.getModifiedurl(), alert.getIncidentid());
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        return Utility.getAllPolicyTypes();
    }
}
