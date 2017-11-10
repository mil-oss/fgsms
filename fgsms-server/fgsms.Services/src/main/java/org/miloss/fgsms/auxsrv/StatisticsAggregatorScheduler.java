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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.statistics.FgsmsStatsv2;
import org.quartz.*;

/**
 * Quartz Job that kicks off the Statistics Aggregator
 *
 * @author AO
 */
public class StatisticsAggregatorScheduler implements StatefulJob {

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            org.miloss.fgsms.statistics.FgsmsStatsv2 s = new FgsmsStatsv2();
            s.doWork(true);
            AuxHelper.TryUpdateStatus(true, "urn:fgsms:StatisticsAggregator:" + SLACommon.GetHostName(), "OK", true, PolicyType.TRANSACTIONAL, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        } catch (Exception ex) {
            AuxHelper.TryUpdateStatus(false, "urn:fgsms:StatisticsAggregator:" + SLACommon.GetHostName(), ex.getMessage(), true, PolicyType.TRANSACTIONAL, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        }

        try {
            KeyNameValueEnc interval = DBSettingsLoader.GetPropertiesFromDB(true, "StatisticsAggregator", "Interval");
            //defautl is 5 minutes
            long intinterval = 5 * 60 * 1000;
            if (interval != null) {
                try {
                    intinterval = Long.parseLong(interval.getKeyNameValue().getPropertyValue());
                    if (intinterval < 1000) {
                        intinterval = 5 * 60 * 1000;
                    }
                } catch (Exception ex) {
                }
            }
/*
            Scheduler sc = jec.getScheduler();
            if (sc.deleteJob(StatisticsStarter.JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME)) {
                JobDetail job = new JobDetail(StatisticsStarter.JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME, this.getClass());
                Trigger triger = TriggerUtils.makeMinutelyTrigger((int) (intinterval / 60000));
                triger.setStartTime(new Date());
                triger.setName(StatisticsStarter.TRIGGER_NAME);
                triger.setGroup(AuxConstants.QUARTZ_TRIGGER_GROUP_NAME);
                job.setRequestsRecovery(true);
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(System.currentTimeMillis() + intinterval);
                triger.setStartTime(gcal.getTime());
                Date scheduleJob = sc.scheduleJob(job, triger);
            }*/
        } catch (Exception ex) {
            Logger.getLogger(StatisticsAggregatorScheduler.class.getName()).log(Level.WARNING, "Error adjusting schedule", ex);
        }

    }
}
