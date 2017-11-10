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
package org.miloss.fgsms.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.dataaccessservice.OperationalRecord;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.apache.log4j.Level;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.statistics.jobs.BrokerStatisticsJob;
import org.miloss.fgsms.statistics.jobs.MachineProcessJob;
import org.miloss.fgsms.statistics.jobs.StatusStatisticsJob;
import org.miloss.fgsms.statistics.jobs.TransactionalStatisticsJob;

/**
 * Statistics Calculator for fgsms. It rips through the various performance
 * database tables and calculates aggregated statistics for all services.
 * Several other capabilities depend on this functioning, including the non
 * transactional SLA processor (for rate information, MBTF,etc), Federation
 * publishers (UDDI) and the user interface is heavily driven off this data.
 *
 * runs every 30 seconds by default, overrideable via general settings Version
 * 2. Stats Agg can now calculate stats on any interval of time that is user
 * definable
 *
 * @author AO
 */
public class FgsmsStatsv2 {

    public static final String myUrl = "urn:fgsms:StatisticsAggregator:" + Utility.getHostName();
    public static final String SERVICE_NAME = "Statistics Aggregation";
    public static Logger log = Logger.getLogger("fgsms.Stats");
    public static String allitems = "All-Methods";
    ThreadPoolExecutor threadPool;

    public FgsmsStatsv2() {
        threadPool = new ThreadPoolExecutor(0, 100, 25, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }


    private List<Long> addIfMissingMandatoryTimePeriods(List<Long> periods) {
        periods.add(Long.valueOf(5 * 60 * 1000));
        periods.add(Long.valueOf(15 * 60 * 1000));
        periods.add(Long.valueOf(60 * 60 * 1000));
        periods.add(Long.valueOf(24 * 60 * 60 * 1000));
        Set<Long> minified = new HashSet<Long>(periods);
        return new ArrayList<Long>(minified);
    }

    private void printPeriods(List<Long> periods) {
        String s = "";
        for (int i = 0; i < periods.size(); i++) {
            s += periods.get(i) + " ";
        }
        log.log(Level.DEBUG, "calculating status using the following time periods " + s.trim());
    }

    
    /**
     * removes all entries that are not on the current list of time
     * ranges/periods
     *
     * @param pooled
     * @param periods
     */
    private void cleanup(Connection perfCon, List<Long> periods) {
        if (perfCon == null) {
            log.log(Level.ERROR, "cleanup database unavailable");
            return;
        }
        String s = "";
        for (int i = 0; i < periods.size(); i++) {
            s += "timerange != " + periods.get(i) + "  ";
            if (i + 1 < periods.size()) {
                s += " AND ";
            }
        }
        PreparedStatement cmd = null;
        try {
            cmd = perfCon.prepareStatement("delete from agg2 where " + s);
            cmd.executeUpdate();
        } catch (Exception e) {
            log.log(Level.WARN, null, e);
        } finally {
            DBUtils.safeClose(cmd);
        }
    }


    /**
     * Performance statistics aggregation for all service types. calculation
     * happens on the calling thread. This method is thread safe
     *
     * @param pooled
     */
    public void doWork(boolean pooled) throws Exception {
        List<Long> periods = new ArrayList<Long>();
        try {
            KeyNameValueEnc GetPropertiesFromDB = DBSettingsLoader.GetPropertiesFromDB(pooled, "StatisticsAggregator", "Periods");
            String[] items = GetPropertiesFromDB.getKeyNameValue().getPropertyValue().split(",");
            for (int i = 0; i < items.length; i++) {
                long l = Long.parseLong(items[i]);
                if (l > 0) {
                    periods.add(l);
                }
            }
            periods = addIfMissingMandatoryTimePeriods(periods);
        } catch (Exception e) {
            log.log(Level.WARN, "settings from the database for time periods was unparsable, reverting to the defaults.", e);
            periods.add(Long.valueOf(5 * 60 * 1000));
            periods.add(Long.valueOf(15 * 60 * 1000));
            periods.add(Long.valueOf(60 * 60 * 1000));
            periods.add(Long.valueOf(24 * 60 * 60 * 1000));
        }

        printPeriods(periods);
        Connection ConfigCon = null;
        Connection PerfCon = null;

        PreparedStatement com = null;

        ResultSet rs = null;

        try {

            if (pooled) {
                ConfigCon = Utility.getConfigurationDBConnection();
                PerfCon = Utility.getPerformanceDBConnection();
            } else {
                ConfigCon = Utility.getConfigurationDB_NONPOOLED_Connection();
                PerfCon = Utility.getPerformanceDB_NONPOOLED_Connection();
            }
            if (ConfigCon == null || PerfCon == null) {
                log.log(Level.FATAL, "statistics job on start, database unavailable");
                DBUtils.safeClose(PerfCon);
                DBUtils.safeClose(ConfigCon);
                throw new Exception("database unavailable");
            }

            threadPool.execute(new StatusStatisticsJob(periods));
            threadPool.execute(new MachineProcessJob(periods));
            threadPool.execute(new BrokerStatisticsJob(periods));

            com = ConfigCon.prepareStatement("select uri from servicepolicies where policytype=?;");
            com.setInt(1, PolicyType.TRANSACTIONAL.ordinal());
            rs = com.executeQuery();
            while (rs.next()) {
                threadPool.execute(new TransactionalStatisticsJob(periods, rs.getString("uri")));
            }
            rs.close();
            com.close();

        } catch (Exception ex) {
            //overall failure
            log.log(Level.ERROR, "Error caught during statistics calculation, please report", ex);
            throw ex;
        } finally {
            cleanup(PerfCon, periods);
            DBUtils.safeClose(com);
            DBUtils.safeClose(rs);
            DBUtils.safeClose(PerfCon);
            DBUtils.safeClose(ConfigCon);
        }

    }



}
