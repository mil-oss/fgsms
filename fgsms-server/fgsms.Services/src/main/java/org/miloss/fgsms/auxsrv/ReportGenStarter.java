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
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Starts up Report Generator when the servlet container
 * starts
 *
 * @author AO
 */
public class ReportGenStarter implements ServletContextListener {

    public static final String JOB_NAME = "Report Generator";
    public static final String TRIGGER_NAME = "Report Generator Trigger";
    private static Scheduler sc;
    static Logger log = Logger.getLogger("fgsms.Aux");

    public void contextInitialized(ServletContextEvent sce) {
        StartupCheck();

    }

    protected static void StartupCheck() {
        JobDetail job = null;
        Trigger triger = null;
        try {
            sc = new StdSchedulerFactory().getScheduler(AuxConstants.QUARTZ_SCHEDULER_NAME);
            if (sc == null) {
                log.fatal("Unable to reference the Quartz instance of " + AuxConstants.QUARTZ_SCHEDULER_NAME + " ensure that it exists and is started. Unable to schedule job for " + JOB_NAME);
            }

            if (!AuxConstants.QuartzJobExists(JOB_NAME, sc)) {
                job = new JobDetail(JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME, org.miloss.fgsms.auxsrv.ReportGenScheduler.class);
                job.setRequestsRecovery(true);
                triger = TriggerUtils.makeMinutelyTrigger();
                triger.setStartTime(new Date());
                triger.setName(TRIGGER_NAME);
                triger.setGroup(AuxConstants.QUARTZ_TRIGGER_GROUP_NAME);
                sc.scheduleJob(job, triger);
                log.log(Level.INFO, "Report Generator added");
                if (sc.isShutdown()) {
                    log.log(Level.WARN, "starting quartz");
                }
                sc.start();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "error scheduling Report Generator", ex);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        /*
         * try { sc = new StdSchedulerFactory().getScheduler();
         *
         * sc.unscheduleJob("Report Generator", "fgsms Aux Services ");
         * sc.deleteJob("Report Generator", "fgsms Aux Services ");
         *
         *
         * log.log(Level.WARN, "Undeploying fgsms Aux Services Succeeded. Jobs
         * removed from Quartz scheduler."); } catch (SchedulerException ex) {
         * log.log(Level.WARN, "error unscheduling bueller", ex);
        }
         */
    }
}
