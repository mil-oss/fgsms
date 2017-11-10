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

import org.miloss.fgsms.common.Utility;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 *Starts 6 of the fgsms server processes, deprecated since RC6, use {component}Scheduler.java
 * @author AO
 */
@Deprecated
public class JobStarter implements ServletContextListener {

    private Scheduler sc;
    Logger log = Logger.getLogger("fgsms.Aux");

    public void contextInitialized(ServletContextEvent sce) {
        try {
            sc = new StdSchedulerFactory().getScheduler();
            String jmxurl = sce.getServletContext().getInitParameter("fgsms.qpid.jmx.connectionurl"); //this pulls from context-param
            String intervalInSeconds = sce.getServletContext().getInitParameter("fgsms.qpid.jmx.interval");
            String jmxusername = sce.getServletContext().getInitParameter("fgsms.qpid.jmx.username");
            String jmxpassword = sce.getServletContext().getInitParameter("fgsms.qpid.jmx.encryptedpassword");
            try {

                sc.deleteJob("Uddi Publisher", "fgsms Aux Services ");
                sc.deleteJob("Status Bueller", "fgsms Aux Services ");
                sc.deleteJob("Data Pruner", "fgsms Aux Services ");
                sc.deleteJob("NTSLA", "fgsms Aux Services ");
                sc.deleteJob("Statistic Aggregator", "fgsms Aux Services ");
                sc.deleteJob("JMX QPID", "fgsms Aux Services ");
            } catch (Exception ex) {
            }
            JobDetail job = new JobDetail("Federation Publisher", "fgsms Aux Services ", org.miloss.fgsms.auxsrv.FederationScheduler.class);
            Trigger triger = TriggerUtils.makeMinutelyTrigger(5);
            triger.setName("Federation Publisher Trigger");
            sc.scheduleJob(job, triger);
            log.log(Level.INFO, "Federation Publisher job added");

            job = new JobDetail("Data Pruner", "fgsms Aux Services ", org.miloss.fgsms.auxsrv.DataPrunerScheduler.class);
            triger = TriggerUtils.makeMinutelyTrigger(5);
            triger.setName("Data Pruner Trigger");
            sc.scheduleJob(job, triger);
            log.log(Level.INFO, "Data Pruner job added");

            job = new JobDetail("Status Bueller", "fgsms Aux Services ", org.miloss.fgsms.auxsrv.BuellerScheduler.class);
            triger = TriggerUtils.makeSecondlyTrigger(10);
            triger.setName("Status BuellerTrigger");
            sc.scheduleJob(job, triger);
            log.log(Level.INFO, "Status Buellerjob added");

            job = new JobDetail("NTSLA", "fgsms Aux Services ", org.miloss.fgsms.auxsrv.NTSLAProcessorScheduler.class);
            triger = TriggerUtils.makeMinutelyTrigger(5);
            triger.setName("NTSLA Trigger");
            sc.scheduleJob(job, triger);
            log.log(Level.INFO, "NTSLA job added");

            job = new JobDetail("Statistic Aggregator", "fgsms Aux Services ", org.miloss.fgsms.auxsrv.StatisticsAggregatorScheduler.class);
            triger = TriggerUtils.makeMinutelyTrigger(5);
            triger.setName("Stat Agg Trigger");
            sc.scheduleJob(job, triger);
            log.log(Level.INFO, "Stats Agg job added");

            if (!Utility.stringIsNullOrEmpty(jmxurl)) {
                job = new JobDetail("JMX QPID", "fgsms Aux Services ", org.miloss.fgsms.auxsrv.QpidJMXScheduler.class);
               job.getJobDataMap().put("connecturl", jmxurl);
               job.getJobDataMap().put("username", jmxusername);
               job.getJobDataMap().put("encryptedpassword", jmxpassword);
               try{
                triger = TriggerUtils.makeSecondlyTrigger(Integer.parseInt(intervalInSeconds));
               }
               catch (Exception ex)
               {
                   log.log(Level.WARN, "the configuration parameter for fgsms.qpid.jmx.interval is " + intervalInSeconds + " and could not be parsed as an integer. Defaulting to 30 second pings");
                   triger = TriggerUtils.makeSecondlyTrigger(30);
               }
                triger.setName("JMX QPID Trigger");
                
                sc.scheduleJob(job, triger);
                log.log(Level.INFO, "JMX QPID added");
            }

            if (sc.isShutdown()) {
                log.log(Level.WARN, "starting quartz");
            }
            sc.start();
        } catch (Exception ex) {
            log.log(Level.WARN, "error scheduling UDDI Publisher", ex);
        }

    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            sc = new StdSchedulerFactory().getScheduler();
            sc.unscheduleJob("Statistic Aggregator", "fgsms Aux Services ");
            sc.deleteJob("Statistic Aggregator", "fgsms Aux Services ");
            sc.unscheduleJob("NTSLA", "fgsms Aux Services ");
            sc.deleteJob("NTSLA", "fgsms Aux Services ");
            sc.unscheduleJob("Status Bueller", "fgsms Aux Services ");
            sc.deleteJob("Status Bueller", "fgsms Aux Services ");

            sc.unscheduleJob("JMX QPID", "fgsms Aux Services ");
            sc.deleteJob("JMX QPID", "fgsms Aux Services ");


            sc.unscheduleJob("Data Pruner", "fgsms Aux Services ");
            sc.deleteJob("Data Pruner", "fgsms Aux Services ");
            sc.unscheduleJob("Uddi Publisher", "fgsms Aux Services ");
            sc.deleteJob("Uddi Publisher", "fgsms Aux Services ");
            log.log(Level.WARN, "Undeploying fgsms Aux Services Succeeded. Jobs removed from Quartz scheduler.");
        } catch (SchedulerException ex) {
            log.log(Level.WARN, "error unscheduling UDDI Publisher", ex);
        }
    }
}
