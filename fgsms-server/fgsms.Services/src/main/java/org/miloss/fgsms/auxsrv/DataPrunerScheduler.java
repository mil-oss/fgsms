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

import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.datapruner.DataPruner;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/**
 *Quartz Job that kicks off the Data Pruner
 * This job as well as the UDDI publisher is handled differently from the other fgsms quartz jobs. It runs every 2 minutes however does no work
 * unless we have gone past the scheduled interval. This is done so that status information is correctly displayed.
 * @author AO
 */
public class DataPrunerScheduler implements StatefulJob {

    public void DataPrunerScheduler() {
    }
    private static Logger log = Logger.getLogger("fgsms.DataPruner");

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        log.log(Level.INFO, "========================================================== Data Pruner Job executing...");
        try {
            AuxHelper.TryUpdateStatus(true, "urn:fgsms:DataPruner:" + SLACommon.GetHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
            Long lastranat = null;
            try {
                lastranat = (Long) jec.getJobDetail().getJobDataMap().get("lastranat");
            } catch (Exception ex) {
            }
            if (lastranat == null) {
                lastranat = (long) 0;

            }
            KeyNameValueEnc interval = DBSettingsLoader.GetPropertiesFromDB(true, "DataPruner", "Interval");
            long intinterval = 86400000;
            if (interval != null) {
                try {
                    intinterval = Long.parseLong(interval.getKeyNameValue().getPropertyValue());
                    if (intinterval < 30000) {
                        intinterval = 30000;
                    }
                } catch (Exception ex) {
                }
            }
            if (lastranat + intinterval < System.currentTimeMillis()) {
                org.miloss.fgsms.datapruner.DataPruner m = new DataPruner();
                m.Purge(true);
                jec.getJobDetail().getJobDataMap().put("lastranat", System.currentTimeMillis());
            }
        } catch (Exception ex) {
            AuxHelper.TryUpdateStatus(false, "urn:fgsms:DataPruner:" + SLACommon.GetHostName(), ex.getMessage(), true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
            log.log(Level.WARN, "error caught", ex);
        }

    }
}
