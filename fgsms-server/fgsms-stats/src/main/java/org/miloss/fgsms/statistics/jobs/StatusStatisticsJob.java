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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Level;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.dataaccessservice.OperationalRecord;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.SERVICE_NAME;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.allitems;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.log;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.myUrl;

/**
 *
 * @author AO
 */
public class StatusStatisticsJob extends BaseJob implements Runnable {

    List<Long> periods;

    public StatusStatisticsJob(List<Long> periods) {
        this.periods = periods;
    }

    @Override
    public void run() {
        UUID random = UUID.randomUUID();
        MessageProcessor.getSingletonObject().processMessageInput(SERVICE_NAME, 0, myUrl, "status", "system", random.toString(), new HashMap(), "", this.getClass().getCanonicalName(), "", "");
        Connection ConfigCon = Utility.getConfigurationDBConnection();
        Connection PerfCon = Utility.getPerformanceDBConnection();
        try {
            doWorkForStatusItemsOnly(ConfigCon, PerfCon, periods);
            MessageProcessor.getSingletonObject().processMessageOutput(random.toString(), "success", 0, false, System.currentTimeMillis(), new HashMap());
        } catch (Exception ex) {
            MessageProcessor.getSingletonObject().processMessageOutput(random.toString(), "error " + ex.getMessage(), 0, true, System.currentTimeMillis(), new HashMap());
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(PerfCon);
            DBUtils.safeClose(ConfigCon);
        }

    }

    private void doWorkForStatusItemsOnly(Connection ConfigCon, Connection PerfCon, List<Long> periods) throws Exception {
        try {
            long now = System.currentTimeMillis();

            if (ConfigCon == null || PerfCon == null) {
                log.log(Level.ERROR, "doWorkForStatusItemsOnly database unavailable");
                return;
            }
            PreparedStatement com = ConfigCon.prepareStatement("select uri,policytype from servicepolicies where policytype=?;");
            com.setInt(1, PolicyType.STATUS.ordinal());
            ResultSet rs = com.executeQuery();
            while (rs.next()) {          //for each service

                log.log(Level.INFO, "calculating statistics for " + rs.getString("uri"));
                String t = allitems;
                //do rollup
                for (int i = 0; i < periods.size(); i++) {
                    insertRow(PerfCon, rs.getString("uri"), t, periods.get(i));
                    PreparedStatement up = PerfCon.prepareStatement("UPDATE agg2 set "
                            + " avail=?, "
                            + "  timestampepoch=?,  "
                            + "success=-1, failure=-1, avgres=-1, sla=?, mtbf=-1, maxreq=-1,  maxres=-1, maxresponsetime=-1 "
                            + "WHERE uri=? and soapaction=? and timerange=?;");

                    double avail = getAvailability(now, periods.get(i), rs.getString("uri"), t, PerfCon, ConfigCon);
                    long sla = getSLACount(rs.getString("uri"), periods.get(i), PerfCon);
                    up.setDouble(1, avail);
                    up.setLong(2, now);
                    up.setLong(3, sla);
                    up.setString(4, rs.getString("uri"));
                    up.setString(5, t);
                    up.setLong(6, periods.get(i));
                    up.executeUpdate();
                    log.log(Level.DEBUG, "Updated stats for service " + rs.getString("uri") + " action " + t);
                    up.close();
                }
            }
            rs.close();
            com.close();

            now = System.currentTimeMillis() - now;
            log.log(Level.INFO, "Statistics calculations took " + now + "ms");

        } catch (Exception ex) {
            //System.console().writer().println("caught error!" + ex.getLocalizedMessage());
            log.log(Level.ERROR, null, ex);
            throw ex;
        }
    }

}
