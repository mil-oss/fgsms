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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Starts up the Non Transactional SLA Processor when the servlet
 * container starts
 *
 * @author AO
 */
public class NTSLAStarter implements ServletContextListener {

    public static final String JOB_NAME = "NTSLA";
    public static final String TRIGGER_NAME = "NTSLA Trigger";
    private static Scheduler sc;
    static Logger log = Logger.getLogger("fgsms.Aux");

    public void contextInitialized(ServletContextEvent sce) {
    }

    protected static void StartupCheck() {
        try {
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
            }

            sc = new StdSchedulerFactory().getScheduler(AuxConstants.QUARTZ_SCHEDULER_NAME);
            if (sc == null) {
                log.fatal("Unable to reference the Quartz instance of " + AuxConstants.QUARTZ_SCHEDULER_NAME + " ensure that it exists and is started. Unable to schedule job for " + JOB_NAME);
            }
            JobDetail job = null;
            Trigger triger = null;

            if (!AuxConstants.QuartzJobExists(JOB_NAME, sc)) {
                job = new JobDetail(JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME, org.miloss.fgsms.auxsrv.NTSLAProcessorScheduler.class);
                job.setRequestsRecovery(true);
                triger = TriggerUtils.makeSecondlyTrigger((int) intinterval / 1000);
                triger.setName(TRIGGER_NAME);
                triger.setGroup(AuxConstants.QUARTZ_TRIGGER_GROUP_NAME);
                sc.scheduleJob(job, triger);
                log.log(Level.INFO, "NTSLA job added at interval " + intinterval);
                if (sc.isShutdown()) {
                    log.log(Level.WARN, "starting quartz");
                }
                sc.start();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "error scheduling ntsla", ex);
        }
    }

    protected static void Unschedule() {
        try {

            Scheduler sc = new StdSchedulerFactory().getScheduler(AuxConstants.QUARTZ_SCHEDULER_NAME);
            sc.deleteJob(JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME);
        } catch (Exception ex) {
            log.log(Level.DEBUG, "Error removing job for " + JOB_NAME, ex);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        /*
         * try { sc = new StdSchedulerFactory().getScheduler();
         *
         *
         * sc.unscheduleJob("NTSLA", "fgsms Aux Services ");
         * sc.deleteJob("NTSLA", "fgsms Aux Services ");
         *
         *
         * log.log(Level.WARN, "Undeploying fgsms Aux Services Succeeded. Jobs
         * removed from Quartz scheduler."); } catch (SchedulerException ex) {
         * log.log(Level.WARN, "error unscheduling NTSLA from quartz", ex); }
         */
    }
}
