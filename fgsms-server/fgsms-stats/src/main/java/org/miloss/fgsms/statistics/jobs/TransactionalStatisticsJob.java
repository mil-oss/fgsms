/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.statistics.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.log4j.Level;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Utility;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.SERVICE_NAME;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.allitems;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.log;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.myUrl;
import org.miloss.fgsms.statistics.StatisticsContainer;

/**
 *
 * @author AO
 */
public class TransactionalStatisticsJob extends BaseJob {

    List<Long> periods;
    String uri;

    public TransactionalStatisticsJob(List<Long> periods, String string) {
        this.periods = periods;
        this.uri = string;
    }

    @Override
    public void run() {
        UUID random = UUID.randomUUID();
        MessageProcessor.getSingletonObject().processMessageInput(SERVICE_NAME + " for " + uri, 0, myUrl, "transactional", "system", random.toString(), new HashMap(), "", this.getClass().getCanonicalName(), "", "");
        Connection ConfigCon = Utility.getConfigurationDBConnection();
        Connection PerfCon = Utility.getPerformanceDBConnection();
        try {
            doWorkTransactional(ConfigCon, PerfCon, periods, uri);
            MessageProcessor.getSingletonObject().processMessageOutput(random.toString(), "success", 0, false, System.currentTimeMillis(), new HashMap());
        } catch (Exception ex) {
            MessageProcessor.getSingletonObject().processMessageOutput(random.toString(), "error " + ex.getMessage(), 0, true, System.currentTimeMillis(), new HashMap());
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(PerfCon);
            DBUtils.safeClose(ConfigCon);
        }
    }

    private void createAllStats(List<StatisticsContainer> stats, List<Long> periods, final String url, final String action) {
        for (int k = 0; k < periods.size(); k++) {
            boolean found = false;
            for (int i = 0; i < stats.size(); i++) {
                if (stats.get(i).action.equals(action) && stats.get(i).uri.equals(url) && stats.get(i).timeperiod == periods.get(k).longValue()) {
                    found = true;
                }
            }
            if (!found) {
                StatisticsContainer s = new StatisticsContainer();
                s.uri = url;
                s.action = action;
                s.timeperiod = periods.get(k);
                stats.add(s);
            }

        }
    }

    private static long largestPeriod(List<Long> periods) {
        long l = -1;
        for (int i = 0; i < periods.size(); i++) {
            if (periods.get(i).longValue() > l) {
                l = periods.get(i).longValue();
            }
        }
        return l;
    }

    private static long largestPeriod_PriceIsRightRules(List<Long> periods, final long timestamp) {
        long l = -1;
        for (int i = 0; i < periods.size(); i++) {

            if (periods.get(i).longValue() >= (System.currentTimeMillis() - timestamp) && periods.get(i).longValue() > l) {
                l = periods.get(i).longValue();
            }
        }
        if (l == -1) {
            return largestPeriod(periods);
        }
        return l;
    }

    private StatisticsContainer getStatsForTimeStamp(List<StatisticsContainer> stats, final Long timestamp, final String uri, final String action, final List<Long> periods) {
        if (stats == null) {
            stats = new ArrayList<StatisticsContainer>();
        }
        // long currentperiod = LargestPeriod_PriceIsRightRules(periods, timestamp);
        //ok we have the current period
        long eventHappenedNmsAgo = System.currentTimeMillis() - timestamp;

        //we want the smallest StatisticsContainer container without going over x
        StatisticsContainer s = null;
        // StatisticsContainer smallest = null;
        for (int i = 0; i < stats.size(); i++) {
            //for each StatisticsContainer
            if (stats.get(i).uri.equals(uri)
                    && stats.get(i).action.equals(action)) {
                //match url and actions first

                if (s == null && eventHappenedNmsAgo < stats.get(i).timeperiod) {
                    s = stats.get(i);
                } else if (s != null && stats.get(i).timeperiod < s.timeperiod && stats.get(i).timeperiod > eventHappenedNmsAgo) {
                    s = stats.get(i);
                }

            }
        }
        if (s == null) {
            log.log(Level.DEBUG, "unexpected situation during statistics calculation");
            s = new StatisticsContainer();
            s.uri = uri;
            s.action = action;
            s.timeperiod = largestPeriod(periods);
            stats.add(s);
        }
        return s;
    }

    private List<Long> addIfMissingMandatoryTimePeriods(List<Long> periods) {
        periods.add(Long.valueOf(5 * 60 * 1000));
        periods.add(Long.valueOf(15 * 60 * 1000));
        periods.add(Long.valueOf(60 * 60 * 1000));
        periods.add(Long.valueOf(24 * 60 * 60 * 1000));
        Set<Long> minified = new HashSet<Long>(periods);
        return new ArrayList<Long>(minified);
    }

    private void print(StatisticsContainer s) {
        log.log(Level.DEBUG, s.timeperiod + " " + s.uri + " " + s.action + " " + s.success + " " + s.faults + " "
                + s.averageresponsetime + " " + s.max_request_size + " " + s.max_response_size + " "
                + s.max_responsetime + " " + s.mtbf + " " + s.availibity);

    }

    private void printPeriods(List<Long> periods) {
        String s = "";
        for (int i = 0; i < periods.size(); i++) {
            s += periods.get(i) + " ";
        }
        log.log(Level.DEBUG, "calculating status using the following time periods " + s.trim());
    }

    private StatisticsContainer getStatsForTimePeriod(List<StatisticsContainer> stats, Long timeperiod, final String uri, final String action, List<Long> periods) {
        if (stats == null) {
            stats = new ArrayList<StatisticsContainer>();
        }
        // long currentperiod = LargestPeriod_PriceIsRightRules(periods, timestamp);
        //ok we have the current period
        StatisticsContainer s = null;
        for (int i = 0; i < stats.size(); i++) {
            if (stats.get(i).uri.equals(uri)
                    && stats.get(i).action.equals(action)
                    && stats.get(i).timeperiod == timeperiod) {
                s = stats.get(i);
            }
        }
        if (s == null) {
            log.log(Level.WARN, "unexpected situation during statistics calculation, StatisticsContainer is null, i'll just create a new one and move on");
            s = new StatisticsContainer();
            s.uri = uri;
            s.action = action;
            s.timeperiod = largestPeriod(periods);
            stats.add(s);
        }
        return s;
    }

    private void doWorkTransactional(Connection ConfigCon, Connection PerfCon, List<Long> periods, String uri) throws Exception {
        PreparedStatement com = null;
        long now = System.currentTimeMillis();

        List<StatisticsContainer> stats = new ArrayList<StatisticsContainer>();
        try {

            log.log(Level.DEBUG, "loading actions for " + uri);
            List<String> actions = new ArrayList<String>();
            PreparedStatement comm = null;
            ResultSet results = null;
            try {
                comm = PerfCon.prepareStatement("Select soapaction from actionlist where URI=? order by soapaction  desc;");
                comm.setString(1, uri);
                results = comm.executeQuery();
                while (results.next()) {
                    String temp = results.getString("soapaction");

                    log.log(Level.DEBUG, uri + " " + temp);
                    if (!Utility.stringIsNullOrEmpty(temp)) {
                        temp = temp.trim();
                    }
                    if (!Utility.stringIsNullOrEmpty(temp)) {
                        actions.add(temp);
                    }
                }
            } catch (Exception ex) {
                log.error("error fetching actions for service", ex);
            } finally {
                DBUtils.safeClose(results);
                DBUtils.safeClose(comm);
            }
            log.log(Level.INFO, "calculating statistics for " + uri);
            actions.add(allitems);

            for (int k = 0; k < actions.size(); k++) {

                createAllStats(stats, periods, uri, actions.get(k));
                //ensure all the required rows are present using table locking
                for (int i = 0; i < periods.size(); i++) {
                    insertRow(PerfCon, uri, actions.get(k), periods.get(i));
                }
            }

            //get all transactions for a specific service
            //loop
            //  calculate tallies
            //calculate availability
            //update rows
            PreparedStatement cmd = null;
            ResultSet records = null;
            try {
                cmd = PerfCon.prepareStatement("select utcdatetime, success, uri,soapaction, responsetimems, slafault, requestsize, responsesize from rawdata where uri=? and utcdatetime > ?;");
                cmd.setString(1, uri);
                long limit = (long) ((long) now - largestPeriod(periods));
                cmd.setLong(2, limit);
                records = cmd.executeQuery();

                while (records.next()) {
                    StatisticsContainer rollup = getStatsForTimeStamp(stats, records.getLong("utcdatetime"), records.getString("uri"), allitems, periods);
                    StatisticsContainer s = getStatsForTimeStamp(stats, records.getLong("utcdatetime"), records.getString("uri"), records.getString("soapaction"), periods);
                    if (records.getBoolean("success")) {
                        s.success++;
                        rollup.success++;
                    } else {
                        s.faults++;
                        rollup.faults++;
                    }
                    if (!Utility.stringIsNullOrEmpty(records.getString("slafault"))) {
                        s.sla++;
                        //      rollup.sla++;
                    }
                    s.totalprocessingtime += records.getInt("responsetimems");
                    rollup.totalprocessingtime += records.getInt("responsetimems");
                    if (records.getInt("requestsize") > s.max_request_size) {
                        s.max_request_size = records.getInt("requestsize");
                    }
                    if (records.getInt("responsesize") > s.max_response_size) {
                        s.max_response_size = records.getInt("responsesize");
                    }
                    if (records.getInt("responsetimems") > s.max_responsetime) {
                        s.max_responsetime = records.getInt("responsetimems");
                    }

                    if (records.getInt("requestsize") > rollup.max_request_size) {
                        rollup.max_request_size = records.getInt("requestsize");
                    }
                    if (records.getInt("responsesize") > rollup.max_response_size) {
                        rollup.max_response_size = records.getInt("responsesize");
                    }
                    if (records.getInt("responsetimems") > rollup.max_responsetime) {
                        rollup.max_responsetime = records.getInt("responsetimems");
                    }
                    //all transactions for a given service and action that occured after our threshold data of longest period/time range
                }
            } catch (Exception ex) {
                log.error("error fetching transactions", ex);
            } finally {
                DBUtils.safeClose(records);
                DBUtils.safeClose(cmd);
            }

            //do availability and mtbf
            for (int i = 0; i < periods.size(); i++) {
                StatisticsContainer rollup = getStatsForTimePeriod(stats, periods.get(i), uri, allitems, periods);
                long slafaults = getSLACount(uri, periods.get(i), PerfCon);
                rollup.sla = slafaults;
                rollup.availibity = getAvailability(now, periods.get(i), uri, allitems, PerfCon, ConfigCon);
                rollup.mtbf = getMTBF(System.currentTimeMillis(), rollup.timeperiod, rollup.uri, rollup.action, PerfCon);
                for (int k = 0; k < actions.size(); k++) {
                    StatisticsContainer s = getStatsForTimePeriod(stats, periods.get(i), uri, actions.get(k), periods);
                    s.availibity = rollup.availibity;
                    s.mtbf = rollup.mtbf = getMTBF(System.currentTimeMillis(), rollup.timeperiod, rollup.uri, rollup.action, PerfCon);
                }
            }

            for (int i = 0; i < stats.size(); i++) //update the database
            {
                print(stats.get(i));
                insertRow(PerfCon, stats.get(i).uri, stats.get(i).action, stats.get(i).timeperiod);
                PreparedStatement up = null;
                try {
                    up = PerfCon.prepareStatement("UPDATE agg2 set "
                            + "success=?, failure=?, avgres=?, avail=?, sla=?, mtbf=?, maxreq=?,  maxres=?, maxresponsetime=?, "
                            + "  timestampepoch=?  "
                            + "WHERE uri=? and soapaction=? and timerange=?;");
                    up.setLong(1, stats.get(i).success);
                    up.setLong(2, stats.get(i).faults);
                    if (stats.get(i).success + stats.get(i).faults == 0) {
                        up.setLong(3, 0);
                    } else {
                        up.setLong(3, (long) (stats.get(i).totalprocessingtime / (long) ((long) stats.get(i).success + (long) stats.get(i).faults)));
                    }
                    if (stats.get(i).availibity < 0) {
                        up.setDouble(4, 0.0);
                    } else {
                        up.setDouble(4, stats.get(i).availibity);
                    }
                    up.setLong(5, stats.get(i).sla);
                    up.setLong(6, stats.get(i).mtbf);
                    up.setLong(7, stats.get(i).max_request_size);
                    up.setLong(8, stats.get(i).max_response_size);
                    up.setLong(9, stats.get(i).max_responsetime);
                    up.setLong(10, System.currentTimeMillis());
                    up.setString(11, stats.get(i).uri);
                    up.setString(12, stats.get(i).action);
                    up.setLong(13, stats.get(i).timeperiod);
                    up.executeUpdate();
                } catch (Exception ex) {
                    log.error("error updating statistcs row", ex);
                } finally {
                    DBUtils.safeClose(up);
                }

            }
            now = System.currentTimeMillis()-now;

            log.log(Level.INFO, "Statistics calculations took " + uri + " " + now + "ms");
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error caught during statistics calculation, please report", ex);
            throw ex;
        } finally {

            DBUtils.safeClose(com);     //for each service
        }
    }

    private long getMTBF(final long now, final long limit, final String url, final String action, Connection PerfCon) {
        PreparedStatement prepareStatement = null;
        ResultSet rs = null;
        try {
            List<Long> faults = new ArrayList<Long>();

            if (action.equals(allitems)) {
                prepareStatement = PerfCon.prepareStatement("select utcdatetime from rawdata where success=false and uri=? and utcdatetime > ?;");
                prepareStatement.setString(1, url);
                prepareStatement.setLong(2, now - limit);
            } else {
                prepareStatement = PerfCon.prepareStatement("select utcdatetime from rawdata where success=false and action=? and uri=? and utcdatetime > ?;");
                prepareStatement.setString(1, action);
                prepareStatement.setString(2, url);
                prepareStatement.setLong(3, now - limit);
            }
            rs = prepareStatement.executeQuery();
            while (rs.next()) {
                faults.add(rs.getLong(1));
            }
            rs.close();

            long diff = 0;
            Collections.sort(faults);
            if (faults.size() < 2) {
                return -1;
            }
            for (int i = 0; i < faults.size() - 1; i++) {
                diff += faults.get(i + 1) - faults.get(i);
            }
            return diff / (faults.size() - 1);
        } catch (SQLException ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(prepareStatement);
        }
        return 0;

    }

}
