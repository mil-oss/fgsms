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
package org.miloss.fgsms.auxsrv;

import java.util.Date;
import java.util.GregorianCalendar;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.quartz.*;

/**
 * Quartz Job that kicks off the Apache Qpid agent
 *
 * @author AO
 */
public class QpidJMXScheduler implements StatefulJob {

    private static Logger log = Logger.getLogger("fgsms.Agents");

    public void execute(JobExecutionContext jec) throws JobExecutionException {
         AuxHelper.TryUpdateStatus(true, "urn:fgsms:QpidJMXAgent:" + SLACommon.GetHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        try {
            KeyNameValueEnc item = DBSettingsLoader.GetPropertiesFromDB(true, "QpidJMXAgent", "URLs");
            String t = null;
            String[] urls = null;
            try {
                if (item != null) {
                    t = item.getKeyNameValue().getPropertyValue();
                    urls = t.split("\\|");
                }
            } catch (Exception ex) {
            }

            if (urls != null) {
                for (int i = 0; i < urls.length; i++) {
                    String connecturl = urls[i];
                    String modifiedurl = org.miloss.fgsms.common.IpAddressUtility.modifyURL(connecturl, true);

                    org.miloss.fgsms.agents.qpidjmx.QpidJMXAgent.Fire(true,
                            connecturl,
                            modifiedurl);
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "Could not start the JMX QPID Agent ", ex);
        }




        KeyNameValueEnc interval = DBSettingsLoader.GetPropertiesFromDB(true, "QpidJMXAgent", "Interval");
        long intinterval = 30000;
        if (interval != null) {
            try {
                intinterval = Long.parseLong(interval.getKeyNameValue().getPropertyValue());
                if (intinterval < 1000) {
                    intinterval = 1000;
                }
            } catch (Exception ex) {
            }
        }/*
        try {
            Scheduler sc = jec.getScheduler();
            if (sc.deleteJob(QpidJMXStarter.JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME)) {
                JobDetail job = new JobDetail(QpidJMXStarter.JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME, this.getClass());
                Trigger triger = TriggerUtils.makeSecondlyTrigger((int) (intinterval / 1000));
                triger.setStartTime(new Date());
                triger.setName(QpidJMXStarter.TRIGGER_NAME);
                triger.setGroup(AuxConstants.QUARTZ_TRIGGER_GROUP_NAME);
                job.setRequestsRecovery(true);
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis() + intinterval);
                triger.setStartTime(gcal.getTime());
                Date scheduleJob = sc.scheduleJob(job, triger);
            }
        } catch (Exception ex) {
            log.log(org.apache.log4j.Level.WARN, "Error adjusting schedule for Qpid JMX Agent ", ex);
        }*/


    }
}
