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

package org.miloss.fgsms.sla;

import java.net.URLEncoder;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.plugins.sla.AlertType;

/**
 * Provides SLA processing for web service transactions
 *
 * @author AO
 */
public class TransactionalSLAProcessor {

    private static Logger log = Logger.getLogger("fgsms.SLAProcessor");
    private static Logger syslog = Logger.getLogger("fgsms.SLAProcessor.Syslog");
    private static Logger eventlog = Logger.getLogger("fgsms.SLAProcessor.EventLog");
    private static Logger udplog = Logger.getLogger("fgsms.SLAProcessor.UdpLog");
    private static Logger filelog = Logger.getLogger("fgsms.SLAProcessor.FileLog");

    /*
     * Use this for transactional updates
     */
    public void ProcessNewTransaction(AddDataRequestMsg req, String transactionid) {
        ServicePolicy pol = SLACommon.LoadPolicyPooled(req.getURI());
        Properties props = SLACommon.LoadSLAPropertiesPooled();


        if (pol == null) {
            return;
        }
        if (pol.getServiceLevelAggrements() == null
                || pol.getServiceLevelAggrements().getSLA() == null
                || pol.getServiceLevelAggrements().getSLA().isEmpty()) {
            return;
        }
        for (int i = 0; i < pol.getServiceLevelAggrements().getSLA().size(); i++) {
            boolean flag = false;

            String faultMsg = "";
            AtomicReference<String> ref = new AtomicReference<String>(faultMsg);
            if (pol.getServiceLevelAggrements().getSLA().get(i) == null) {
                continue;
            }




            //transactional checks
            // log.log(Level.INFO, "Checking for Transactional SLA violations for the service at " + pol.getURL() + ". " + msglog.getTransactionLog().size() + " records to sift through");


            flag = ProcessRules(req, ref, pol.getServiceLevelAggrements().getSLA().get(i).getRule());
            long time = System.currentTimeMillis();
            try {
                time = req.getRecordedat().getTimeInMillis();
            } catch (Exception ex) {
            }
            if (flag) //record SLA fault
            {
                String incident = UUID.randomUUID().toString();
                log.log(Level.INFO, "SLA violation for the service at " + pol.getURL() + " Transaction ID: " + transactionid + " " + ref.get());
                SLACommon.RecordSLAFault(ref, pol.getURL(), transactionid, time, incident, true);
                SLACommon.ProcessAlerts(ref.get(), ref.get() + "<br>" + GenerateLink(props.getProperty("fgsms.GUI.URL"), pol.getURL(),
                        transactionid), pol.getURL(), transactionid, time, incident, true, false, pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction(),
                        pol.getServiceLevelAggrements().getSLA().get(i).getGuid(), pol, AlertType.Performance);


            }
        }
    }

    private String GenerateLink(String relativeUrl, String ServiceURL, String transactionId) {
        return "<a href=\"" + relativeUrl + "/SpecificTransactionLogViewer.jsp?ID=" + URLEncoder.encode(transactionId) + "\">View this transaction</a>";
    }


    /*
     * Use this for timed jobs and for status changes
     *
     * public void ProcessNonTransactionalSLA() { List<ServicePolicy> pols =
     * SLACommon.LoadServicePoliciesPooled();
     *
     * }
     */
    private boolean ProcessRules(AddDataRequestMsg get, AtomicReference<String> faultMsg, RuleBaseType rule) {
        String s = faultMsg.get();
        if (rule instanceof AndOrNot) {
            AndOrNot x = (AndOrNot) rule;
            if (x.getFlag() == JoiningType.AND) {
                return ProcessRules(get, faultMsg, x.getLHS()) && ProcessRules(get, faultMsg, x.getRHS());
            }
            if (x.getFlag() == JoiningType.OR) {
                return ProcessRules(get, faultMsg, x.getLHS()) || ProcessRules(get, faultMsg, x.getRHS());
            }
            if (x.getFlag() == JoiningType.NOT) {
                return !ProcessRules(get, faultMsg, x.getLHS());
            }
        }

        if (rule instanceof SLARuleGeneric) {
            SLARuleGeneric x = (SLARuleGeneric) rule;
            if (x.getProcessAt()==null || x.getProcessAt() == RunAtLocation.FGSMS_SERVER) {
                Class c = null;
                try {
                    c = Thread.currentThread().getContextClassLoader().loadClass(x.getClassName());
                } catch (ClassNotFoundException ex) {
                    log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                    return false;
                }
                Object obj = null;
                if (c != null) {
                    try {
                        obj = c.newInstance();
                    } catch (InstantiationException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                        return false;
                    } catch (IllegalAccessException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                        return false;
                    }
                    SLARuleInterface cast = null;
                    try {
                        cast = (SLARuleInterface) obj;
                    } catch (ClassCastException ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleTypeCast"), x.getClassName(), SLARuleInterface.class.getCanonicalName()), ex);
                        return false;
                    }
                    try {
                        AtomicReference<String> smsg = new AtomicReference<String>();
                        boolean CheckRule = cast.CheckTransactionalRule(get, x.getParameterNameValue(), smsg);
                        if (CheckRule) {
                            faultMsg.set(smsg.get());
                        }
                        return CheckRule;
                    } catch (Exception ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleUnexpectedError"), x.getClassName()), ex);
                    }
                }
            }
        }
        return false;
    }
}
