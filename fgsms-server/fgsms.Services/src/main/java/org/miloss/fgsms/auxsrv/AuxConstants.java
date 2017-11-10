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

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 *
 * @author AO
 */
public class AuxConstants {

    public static final String VERSION = "7.0 BUILD";
    public static final String QUARTZ_SCHEDULER_NAME = "fgsmsAuxServicesQuartzScheduler";
    public static final String QUARTZ_GROUP_NAME = "fgsms Aux Services";
    public static final String QUARTZ_TRIGGER_GROUP_NAME = "fgsmsAuxServicesTriggers";

    public static boolean QuartzJobExists(String JobName, Scheduler sc) throws SchedulerException {
        if (sc == null) {   //added in the case of the scheduler being null, which is probably only when the jdbc datasource is not configured. in this case we have bigger issues
            return false;
        }
        String[] jobNames = sc.getJobNames(QUARTZ_GROUP_NAME);
        for (int i = 0; i < jobNames.length; i++) {
            if (jobNames[i].equals(JobName)) {
                return true;
            }
        }
        return false;
    }

    public static JobDetail GetQuartzJobReference(String JobName, Scheduler sc) throws SchedulerException {
        if (sc == null) { //added in the case of the scheduler being null, which is probably only when the jdbc datasource is not configured. in this case we have bigger issues
            return null;
        }
        String[] jobNames = sc.getJobNames(QUARTZ_GROUP_NAME);
        for (int i = 0; i < jobNames.length; i++) {
            if (jobNames[i].equals(JobName)) {
                return sc.getJobDetail(JobName, QUARTZ_GROUP_NAME);
            }
        }
        return null;
    }

    public static Trigger GetQuartzTriggerReference(String JobName, Scheduler sc) throws SchedulerException {
        if (sc == null) { //added in the case of the scheduler being null, which is probably only when the jdbc datasource is not configured. in this case we have bigger issues
            return null;
        }
        String[] jobNames = sc.getJobNames(QUARTZ_GROUP_NAME);
        for (int i = 0; i < jobNames.length; i++) {
            if (jobNames[i].equals(JobName)) {
                return sc.getTrigger(JobName, QUARTZ_GROUP_NAME);
            }
        }
        return null;
    }
}
