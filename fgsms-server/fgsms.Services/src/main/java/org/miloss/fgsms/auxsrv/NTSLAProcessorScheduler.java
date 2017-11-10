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
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.NonTransactionalSLAProcessor;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.common.Logger;;
import org.quartz.*;

/**
 **Quartz Job that kicks off the NT SLA Processor
 *
 * @author AO
 */
public class NTSLAProcessorScheduler implements StatefulJob {

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            org.miloss.fgsms.sla.NonTransactionalSLAProcessor n = new NonTransactionalSLAProcessor();

            n.run(true);
            AuxHelper.TryUpdateStatus(true, "urn:fgsms:NTSLAProcessor:" + SLACommon.GetHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        } catch (Exception ex) {
            AuxHelper.TryUpdateStatus(false, "urn:fgsms:NTSLAProcessor:" + SLACommon.GetHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        }


        KeyNameValueEnc interval = DBSettingsLoader.GetPropertiesFromDB(true, "NTSLAProcessor", "Interval");
        long intinterval = 10000;
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
            if (sc.deleteJob(NTSLAStarter.JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME)) {
                JobDetail job = new JobDetail(NTSLAStarter.JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME, this.getClass());
                Trigger triger = TriggerUtils.makeSecondlyTrigger((int) (intinterval / 1000));
                triger.setStartTime(new Date());
                triger.setName(NTSLAStarter.TRIGGER_NAME);
                triger.setGroup(AuxConstants.QUARTZ_TRIGGER_GROUP_NAME);
                job.setRequestsRecovery(true);
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis() + intinterval);
                triger.setStartTime(gcal.getTime());
                Date scheduleJob = sc.scheduleJob(job, triger);
            }
        } catch (Exception ex) {
            Logger.getLogger("NTSLA").log(org.apache.log4j.Level.WARN, "Error adjusting schedule for Qpid JMX Agent ", ex);
        }*/

    }
}
