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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * This quartz job will periodically check to ensure that all necessary jobs are
 * functioning and that the schedule intervals are synchronized with the desired
 * settings of each job.
 *
 * @author AO
 */
public class JobController implements StatefulJob, ServletContextListener {

    public static final String JOB_NAME = "JobController";
    public static final String TRIGGER_NAME = "JobController";
    private static Scheduler sc;
    static Logger log = Logger.getLogger("fgsms.Aux");

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        /*
         * unschedule all jobs schedule all jobs based on new intervals confirm
         * startup
         */
        TriggerStartups();

        try {
            if (sc == null) {
                sc = new StdSchedulerFactory().getScheduler(AuxConstants.QUARTZ_SCHEDULER_NAME);
            }
            String[] jobNames = sc.getJobNames(AuxConstants.QUARTZ_GROUP_NAME);
            int count = 0;
            for (int i = 0; i < jobNames.length; i++) {
                if (jobNames[i].equalsIgnoreCase(JOB_NAME)
                        
                        || jobNames[i].equalsIgnoreCase(BuellerStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(QpidJMXStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(NTSLAStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(StatisticsStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(SMXJMXStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(HornetQStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(FederationStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(DataPrunerStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(ReportGenStarter.JOB_NAME)
                        || jobNames[i].equalsIgnoreCase(DependencyScannerStarter.JOB_NAME)) {
                    count++;
                }
            }
            if (count < 12) {
                TriggerStartups();
            }
        } catch (Exception ex) {
            log.log(Level.FATAL, null, ex);
        }
    }

    void TriggerStartups() {
        //agents
        BuellerStarter.Unschedule();
        BuellerStarter.StartupCheck();

        HornetQStarter.Unschedule();
        HornetQStarter.StartupCheck();

        QpidJMXStarter.Unschedule();
        QpidJMXStarter.StartupCheck();

        SMXJMXStarter.Unschedule();
        SMXJMXStarter.StartupCheck();

        //core services
        NTSLAStarter.Unschedule();
        NTSLAStarter.StartupCheck();

        StatisticsStarter.Unschedule();
        StatisticsStarter.StartupCheck();

        //items that aren't adjustable via quartz (that need to report their operating status)
        DataPrunerStarter.StartupCheck();
        DependencyScannerStarter.StartupCheck();
        FederationStarter.StartupCheck();
        ReportGenStarter.StartupCheck();

        //check it again to ensure started

        BuellerStarter.StartupCheck();
        HornetQStarter.StartupCheck();
        SMXJMXStarter.StartupCheck();
        StatisticsStarter.StartupCheck();
        NTSLAStarter.StartupCheck();
        QpidJMXStarter.StartupCheck();
    }

    @Override
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
                job = new JobDetail(JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME, JobController.class);
                job.setRequestsRecovery(true);
                job.setDurability(true);
                triger = TriggerUtils.makeMinutelyTrigger(15);
                triger.setGroup(AuxConstants.QUARTZ_TRIGGER_GROUP_NAME);
                triger.setName(TRIGGER_NAME);
                sc.scheduleJob(job, triger);
                log.log(Level.INFO, JOB_NAME + " job added, running every 15");
                if (sc.isShutdown()) {
                    log.log(Level.WARN, "starting quartz");
                    sc.start();
                }
            } else {
                log.log(Level.DEBUG, JOB_NAME + " already scheduled");
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "error scheduling data pruner", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
