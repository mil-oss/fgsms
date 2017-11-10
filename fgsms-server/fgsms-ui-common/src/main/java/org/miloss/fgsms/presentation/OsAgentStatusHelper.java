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

package org.miloss.fgsms.presentation;

import java.util.List;
import javax.xml.ws.BindingProvider;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.services.interfaces.agentcallbackservice.RemoteAgentCallbackPort;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PropertyPair;
import org.miloss.fgsms.services.interfaces.status.OpStatusService;

/**
 * This class provides an html render status indicator for an OS agent, given
 * its hostname
 *
 * @author AO
 */
public class OsAgentStatusHelper {

    public static String GetStatus(OpStatusService svc, String callbackUrl, SecurityWrapper c) {
        GetOperatingStatusRequestMessage req = new GetOperatingStatusRequestMessage();
        req.setClassification(c);
        BindingProvider bp = (BindingProvider) svc;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, callbackUrl);
        GetOperatingStatusResponseMessage operatingStatus;
        try {
            operatingStatus = svc.getOperatingStatus(req);
            return OpStatHelper.toHtmlFormatedString(operatingStatus, callbackUrl);
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.WARN, null, ex);
            return ex.getMessage();
        }
    }

    /**
     * returns true if the OS agent has reported its callback address
     * @param list
     * @return 
     */
    public static boolean ContainsOSAgentCallbackURL(List<PropertyPair> list) {
        if (list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPropertyname().equalsIgnoreCase(org.miloss.fgsms.common.Constants.PROPERTYPAIR_OS_AGENT_CALLBACK_URL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the url of the OS agent callback endpoint
     *
     * @param list
     * @return null if not found, otherwise the value of the
     */
    public static String GetOSAgentCallBackURL(List<PropertyPair> list) {
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPropertyname().equalsIgnoreCase(org.miloss.fgsms.common.Constants.PROPERTYPAIR_OS_AGENT_CALLBACK_URL)) {
                return list.get(i).getPropertyvalue();
            }
        }
        return null;
    }
}
