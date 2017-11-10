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
import org.apache.log4j.Level;

import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;

/**
 *
 * @author AO
 */
public class ManageHelper {

    GetProcessesListByMachineResponseMsg res = null;

    public String GetPartitionNamesAsHtmlListboxForThroughput(String url, String domain, String machine, PCS pcsport, SecurityWrapper c, List<String> existingItems) {
        GetProcessesListByMachineRequestMsg req = new GetProcessesListByMachineRequestMsg();
        req.setClassification(c);
        req.setHostname(machine);
        StringBuilder s = new StringBuilder();
        if (res == null) {
            try {
                res = pcsport.getProcessesListByMachine(req);
            } catch (AccessDeniedException ex) {
               LogHelper.getLog().log(Level.WARN, null, ex);
            } catch (ServiceUnavailableException ex) {
               LogHelper.getLog().log(Level.WARN, null, ex);
            }
        }
        if (res != null && res.getMachineInformation() != null) {

            s = s.append("<select name=\"recorddiskusage\" id=\"recorddiskusage\" multiple=\"multiple\" >");
            for (int i = 0; i < res.getMachineInformation().getDriveInformation().size(); i++) {
                s = s.append("  <option value=\"").append(Utility.encodeHTML(res.getMachineInformation().getDriveInformation().get(i).getSystemid())).append("\"");
                if (isIn(res.getMachineInformation().getDriveInformation().get(i).getSystemid(), existingItems)) {
                    s = s.append(" selected ");
                }
                s = s.append(" >").
                        append(Utility.encodeHTML(res.getMachineInformation().getDriveInformation().get(i).getSystemid())).append("</option>");
            }
            s = s.append("</select>");
        } else {
            s = s.append("<input type=text name=\"recorddiskusage\"  value=\"").append(Utility.encodeHTML(Utility.listStringtoString(existingItems))).append("\" >");
        }
        return s.toString();
    }

    public String GetNICNamesAsHtmlListbox(String url, String domain, String machine, PCS pcsport, SecurityWrapper c, List<String> existingItems) {
        GetProcessesListByMachineRequestMsg req = new GetProcessesListByMachineRequestMsg();
        req.setClassification(c);
        req.setHostname(machine);
        StringBuilder s = new StringBuilder();
        if (res == null) {
            try {
                res = pcsport.getProcessesListByMachine(req);
            } catch (AccessDeniedException ex) {
               LogHelper.getLog().log(Level.WARN, null, ex);
            } catch (ServiceUnavailableException ex) {
               LogHelper.getLog().log(Level.WARN, null, ex);
            }
        }
        if (res != null && res.getMachineInformation() != null) {

            s = s.append("<select name=\"recordnetworkusage\" id=\"recordnetworkusage\" multiple=\"multiple\" >");
            for (int i = 0; i < res.getMachineInformation().getAddresses().size(); i++) {
                s = s.append("  <option value=\"").append(Utility.encodeHTML(res.getMachineInformation().getAddresses().get(i).getAdapterName())).append("\"");
                if (isIn(res.getMachineInformation().getAddresses().get(i).getAdapterName(), existingItems)) {
                    s = s.append(" selected ");
                }
                s = s.append(" >").
                        append(Utility.encodeHTML(res.getMachineInformation().getAddresses().get(i).getAdapterName())).append(" on ").
                        append(Utility.encodeHTML(Utility.listStringtoString(res.getMachineInformation().getAddresses().get(i).getIp()))).append("</option>");
            }
            s = s.append("</select>");
        } else {
            s = s.append("<input type=text name=\"recordnetworkusage\"  value=\"").append(Utility.encodeHTML(Utility.listStringtoString(existingItems))).append("\" >");
        }
        return s.toString();
    }
    
     public String GetPartitionNamesAsHtmlListboxForFreeSpace(String url, String domain, String machine, PCS pcsport, SecurityWrapper c, List<String> existingItems) {
        GetProcessesListByMachineRequestMsg req = new GetProcessesListByMachineRequestMsg();
        req.setClassification(c);
        req.setHostname(machine);
        StringBuilder s = new StringBuilder();
        if (res == null) {
            try {
                res = pcsport.getProcessesListByMachine(req);
            } catch (AccessDeniedException ex) {
               LogHelper.getLog().log(Level.WARN, null, ex);
            } catch (ServiceUnavailableException ex) {
               LogHelper.getLog().log(Level.WARN, null, ex);
            }
        }
        if (res != null && res.getMachineInformation() != null) {

            s = s.append("<select name=\"recorddrivespace\" id=\"recorddrivespace\" multiple=\"multiple\" >");
            for (int i = 0; i < res.getMachineInformation().getDriveInformation().size(); i++) {
                s = s.append("  <option value=\"").append(Utility.encodeHTML(res.getMachineInformation().getDriveInformation().get(i).getSystemid())).append("\"");
                if (isIn(res.getMachineInformation().getDriveInformation().get(i).getSystemid(), existingItems)) {
                    s = s.append(" selected ");
                }
                s = s.append(" >").
                        append(Utility.encodeHTML(res.getMachineInformation().getDriveInformation().get(i).getSystemid())).append("</option>");
            }
            s = s.append("</select>");
        } else {
            s = s.append("<input type=text name=\"recorddrivespace\"  value=\"").append(Utility.encodeHTML(Utility.listStringtoString(existingItems))).append("\" >");
        }
        return s.toString();
    }
     
     

    private boolean isIn(String systemid, List<String> existingItems) {
        for (int i = 0; i < existingItems.size(); i++) {
            if (systemid.equalsIgnoreCase(existingItems.get(i))) {
                return true;
            }
        }
        return false;
    }
}
