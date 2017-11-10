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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;

/**
 *
 * @author AO
 */
public class SLAActionRestart implements SLAActionInterface {

    private static Logger log = Logger.getLogger("fgsms.SLAProcessor");
    public static final String RUNAT = "runAtLocation";

   

  
    @Override
    public List<NameValuePair> GetRequiredParameters() {
        return new ArrayList<NameValuePair>();
        
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
       return new ArrayList<NameValuePair>();
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This action will attempt to restart a service (Windows via OS Agent), a computer (via OS Agent), or some other capability as supported by the agent."
                + "Currently, the Apache ServiceMix agent can attempt to start stopped JBI Components. "
                + "<br>"
                + "Important: In order to use this SLA Action, you must have Global Administrator permissions in fgsms.<br>"
                + "Required parameters:"
                + "<ul>"
                + "<li>runFrom - the working path to start the command from</li>"
                + "<li>command - the command to execute</li>"
                + "<li>runAt - either fgsms_SERVER or fgsms_AGENT</li>"
                + "</ul>";
    }

    @Override
    public String GetDisplayName() {
        return "Restart a computer or service";
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
     //   throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        List<PolicyType> x = new ArrayList<PolicyType>();
        x.add(PolicyType.STATISTICAL);
        x.add(PolicyType.MACHINE);
        x.add(PolicyType.PROCESS);
        return x;
    }
}
