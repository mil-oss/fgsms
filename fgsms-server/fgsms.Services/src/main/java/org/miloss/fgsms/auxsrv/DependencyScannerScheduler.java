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
import org.apache.log4j.*;
import org.miloss.fgsms.dependency.DependencyScanner;
import org.quartz.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 **Quartz Job that kicks off the DependencyScanner
 *
 * @author AO
 */
public class DependencyScannerScheduler implements StatefulJob {

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            /*
             * KeyNameValueEnc interval =
             * DBSettingsLoader.GetPropertiesFromDB(true, "DependencyScanner",
             * "Interval"); long intinterval = 60000; if (interval != null) {
             * try { intinterval =
             * Long.parseLong(interval.getKeyNameValue().getPropertyValue()); if
             * (intinterval < 60000) { intinterval = 60000; } } catch (Exception
             * ex) { } }
             */
            DependencyScanner ds = new DependencyScanner();
            Object get = jec.getJobDetail().getJobDataMap().get("LastRanAt");
            if (get == null) {
                ds.go(true, 0);
            } else {
                ds.go(true, (Long) get);
            }
            jec.getJobDetail().getJobDataMap().put("LastRanAt", Long.valueOf(System.currentTimeMillis()));

            AuxHelper.TryUpdateStatus(true, "urn:fgsms:DependencyScanner:" + SLACommon.GetHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        } catch (Exception ex) {
            Logger.getLogger(DependencyScannerScheduler.class.getName()).log(Level.ERROR, null, ex);
            AuxHelper.TryUpdateStatus(false, "urn:fgsms:DependencyScanner:" + SLACommon.GetHostName(), ex.getMessage(), true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        }
        
        //no need to reschedule this
    }
}
