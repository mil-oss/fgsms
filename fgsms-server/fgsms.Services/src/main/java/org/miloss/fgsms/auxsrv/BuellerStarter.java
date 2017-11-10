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
 * Starts up Bueller when the servlet container starts
 *
 * @author AO
 */
public class BuellerStarter implements ServletContextListener {

    public static final String JOB_NAME = "Status Bueller";
    public static final String TRIGGER_NAME = "Status BuellerTrigger";
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
            if (!AuxConstants.QuartzJobExists(JOB_NAME, sc)) {
                KeyNameValueEnc interval = DBSettingsLoader.GetPropertiesFromDB(true, "Bueller", "Interval");
                long intinterval = 30000;
                if (interval != null) {
                    try {
                        intinterval = Long.parseLong(interval.getKeyNameValue().getPropertyValue());
                        if (intinterval < 1000) {
                            intinterval = 30000;
                        }
                    } catch (Exception ex) {
                    }
                }

                job = new JobDetail(JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME, BuellerScheduler.class);
                triger = TriggerUtils.makeSecondlyTrigger((int) intinterval / 1000);
                triger.setStartTime(new Date());
                triger.setName(TRIGGER_NAME);
                triger.setGroup(AuxConstants.QUARTZ_TRIGGER_GROUP_NAME);
                job.setRequestsRecovery(true);
                sc.scheduleJob(job, triger);
                log.log(Level.INFO, "Status Bueller job scheduled at " + intinterval + " ms");
                if (sc.isShutdown()) {
                    log.log(Level.WARN, "starting quartz");
                    sc.start();
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "error scheduling bueller", ex);
        }
    }

    protected static void Unschedule() {
        try {
            Scheduler sc = new StdSchedulerFactory().getScheduler(AuxConstants.QUARTZ_SCHEDULER_NAME);
            sc.deleteJob(BuellerStarter.JOB_NAME, AuxConstants.QUARTZ_GROUP_NAME);
        } catch (Exception ex) {
            log.log(Level.DEBUG, "Unscheduling " + JOB_NAME + " failed.", ex);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
