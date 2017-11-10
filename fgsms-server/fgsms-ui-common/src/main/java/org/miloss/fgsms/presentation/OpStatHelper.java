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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.status.OpStatusService;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Utility;

/**
 *
 * @author AO
 */
public class OpStatHelper {

    static Logger log = Logger.getLogger("fgsms.OpStat");

    public static List<OpStatWrapper> GetStatusAll(IProxyLoader pl, ServletContext context, HttpServletRequest req, HttpServletResponse res) {
        List<OpStatWrapper> list = new ArrayList<OpStatWrapper>();
        try {
            OpStatusService GetOpStat = pl.GetOpStat(context, req, res);
            SecurityWrapper w = (SecurityWrapper) req.getSession().getAttribute("currentclassification");
            List<String> urls = new ArrayList<String>();
            Properties rawConfiguration = pl.getRawConfiguration();
            String s = rawConfiguration.getProperty(IProxyLoader.ARS);
            String[] ss = null;
            if (s.contains("|")) {
                ss = s.split("|");
                TransferToList(urls, ss);
            } else {
                urls.add(s);
            }

            s = rawConfiguration.getProperty(IProxyLoader.DATAACCESS);
            if (s.contains("|")) {
                ss = s.split("|");

                TransferToList(urls, ss);
            } else {
                urls.add(s);
            }
            s = rawConfiguration.getProperty(IProxyLoader.DCS);
            if (s.contains("|")) {
                ss = s.split("|");
                TransferToList(urls, ss);
            } else {
                urls.add(s);
            }
            s = rawConfiguration.getProperty(IProxyLoader.POLICYCONFIG);
            if (s.contains("|")) {
                ss = s.split("|");
                TransferToList(urls, ss);
            } else {
                urls.add(s);
            }
            s = rawConfiguration.getProperty(IProxyLoader.REPORTING);
            if (s.contains("|")) {
                ss = s.split("|");

                TransferToList(urls, ss);
            } else {
                urls.add(s);
            }
            s = rawConfiguration.getProperty(IProxyLoader.STATUS);
            if (s.contains("|")) {
                ss = s.split("|");
                TransferToList(urls, ss);
            } else {
                urls.add(s);
            }
            GetOperatingStatusRequestMessage reqmsg = new GetOperatingStatusRequestMessage();
            reqmsg.setClassification(w);

            for (int i = 0; i < urls.size(); i++) {
                log.log(Level.INFO, (i + 1) + "/" + urls.size() + " requesting opstatus for " + urls.get(i)+ "-opstat");
                BindingProvider bp = (BindingProvider) GetOpStat;
                bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urls.get(i) + "-opstat");
                try {
                    GetOperatingStatusResponseMessage operatingStatus = GetOpStat.getOperatingStatus(reqmsg);
                    OpStatWrapper x = new OpStatWrapper();
                    x.uri = urls.get(i);
                    x.msg = operatingStatus;
                    list.add(x);
                } catch (Exception ex) {
                    GetOperatingStatusResponseMessage resr = new GetOperatingStatusResponseMessage();
                    resr.setStatus(false);
                    resr.setStatusMessage("Unable to connect " + ex.getMessage());
                    OpStatWrapper x = new OpStatWrapper();
                    x.uri = urls.get(i);
                    x.msg = resr;
                    list.add(x);
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }
        return list;
    }

    private static void TransferToList(List<String> destination, String[] source) {
        if (source == null) {
            return;
        }
        for (int i = 0; i < source.length; i++) {
            destination.add(source[i]);
        }
    }

    public static String toHtmlFormatedString(GetOperatingStatusResponseMessage item, String url) {
        StringBuilder sb = new StringBuilder();

        sb.append("<div style=\"border-style: dotted; border-width: 1px\">");
        sb.append("URL: ");
        sb.append(url);
        sb.append("<br>Is Running?:  ");
        sb.append(Boolean.toString(item.isStatus()));
        sb.append("<br>Status Message: ");
        sb.append(item.getStatusMessage());
        sb.append("<br>Started at: ");
        if (item.getStartedAt() != null) {
            sb.append(Utility.formatDateTime(item.getStartedAt()));
        }
        if (item.getVersionInfo()!=null){
          sb.append("<br> Version: ");
          sb.append(item.getVersionInfo().getVersionData());
          sb.append("<br>Version Source: ");
          sb.append(item.getVersionInfo().getVersionSource());
        }

        sb.append("<br>: Data Sent Success: ");
        sb.append(Long.toString(item.getDataSentSuccessfully()));
        sb.append("<br>: Data Sent Failure: ");
        sb.append(Long.toString(item.getDataNotSentSuccessfully()));


        sb.append("</div>");
        return sb.toString();
    }
}
