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
 * Starts up the Apache Qpid via JMX Agent when the servlet container
 * starts
 *
 * @author AO
 */
public class QpidJMXStarter implements ServletContextListener {

    public static final String JOB_NAME = "JMX QPID";
    public static final String TRIGGER_NAME = "JMX QPID Trigger";
    private static Scheduler sc;
    static Logger log = Logger.getLogger("fgsms.Aux");

    public void contextInitialized(ServletContextEvent sce) {

        StartupCheck();
    }

    protected static void StartupCheck() {
        try {
            JobDetail job = null;
            Trigger triger = null;
            sc = new StdSchedulerFactory().getScheduler(AuxConstants.QUARTZ_SCHEDULER_NAME);
            if (sc == null) {
                log.fatal("Unable to reference the Quartz instance of " + AuxConstants.QUARTZ_SCHEDULER_NAME + " ensure that it exists and is started. Unable to schedule job for " + JOB_NAME);
            }
            //String jmxurl = sce.getServletContext().getInitParameter("fgsms.qpid.jmx.connectionurl"); //this pulls from context-param

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
            }


            if (!AuxConstants.QuartzJobExists(JOB_NAME, sc)) {
                job = new JobDetail(JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME, org.miloss.fgsms.auxsrv.QpidJMXScheduler.class);

                try {
                    triger = TriggerUtils.makeSecondlyTrigger((int) (intinterval / 1000));

                    triger.setStartTime(new Date());
                } catch (Exception ex) {
                    log.log(Level.WARN, "the configuration parameter for fgsms.qpid.jmx.interval is " + intinterval + " and could not be parsed as an integer. Defaulting to 30 second pings");
                    triger = TriggerUtils.makeSecondlyTrigger(30);
                    intinterval = 30 * 1000;
                }
                triger.setName(TRIGGER_NAME);
                triger.setGroup(AuxConstants.QUARTZ_TRIGGER_GROUP_NAME);
                sc.scheduleJob(job, triger);
                log.log(Level.INFO, "JMX QPID added at internval " + intinterval + "ms");

                if (sc.isShutdown()) {
                    log.log(Level.WARN, "starting quartz");
                }
                sc.start();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "error scheduling qpid agent", ex);
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
         * sc.unscheduleJob("JMX QPID", "fgsms Aux Services ");
         * sc.deleteJob("JMX QPID", "fgsms Aux Services ");
         *
         *
         * log.log(Level.WARN, "Undeploying fgsms Aux Services Succeeded. Jobs
         * removed from Quartz scheduler."); } catch (SchedulerException ex) {
         * log.log(Level.WARN, "error unscheduling qpid agent", ex); }
         */
    }
}
