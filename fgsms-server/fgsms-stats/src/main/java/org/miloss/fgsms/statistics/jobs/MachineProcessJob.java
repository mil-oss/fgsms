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
public class MachineProcessJob extends BaseJob implements Runnable {

    List<Long> periods;

    public MachineProcessJob(List<Long> periods) {
        this.periods = periods;
    }

    @Override
    public void run() {

        UUID random = UUID.randomUUID();
        MessageProcessor.getSingletonObject().processMessageInput(SERVICE_NAME, 0, myUrl, "machine/process", "system", random.toString(), new HashMap(), "", this.getClass().getCanonicalName(), "", "");
        Connection ConfigCon = Utility.getConfigurationDBConnection();
        Connection PerfCon = Utility.getPerformanceDBConnection();
        try {
            doWorkMachinesProcesses(ConfigCon, PerfCon, periods);
            MessageProcessor.getSingletonObject().processMessageOutput(random.toString(), "success", 0, false, System.currentTimeMillis(), new HashMap());
        } catch (Exception ex) {
            MessageProcessor.getSingletonObject().processMessageOutput(random.toString(), "error " + ex.getMessage(), 0, true, System.currentTimeMillis(), new HashMap());

        } finally {
            DBUtils.safeClose(PerfCon);
            DBUtils.safeClose(ConfigCon);
        }
    }

    private void doWorkMachinesProcesses(Connection ConfigCon, Connection PerfCon, List<Long> periods) throws Exception {
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            long now = System.currentTimeMillis();

            if (ConfigCon == null || PerfCon == null) {
                log.log(Level.ERROR, "doWorkMachinesProcesses database unavailable");
                return;
            }
            com = ConfigCon.prepareStatement("select uri,policytype from servicepolicies where policytype=? or policytype=?;");
            com.setInt(1, PolicyType.MACHINE.ordinal());
            com.setInt(2, PolicyType.PROCESS.ordinal());
            rs = com.executeQuery();
            while (rs.next()) {          //for each service

                log.log(Level.INFO, "calculating statistics for " + rs.getString("uri"));
                String t = allitems;
                //do rollup
                for (int i = 0; i < periods.size(); i++) {
                    insertRow(PerfCon, rs.getString("uri"), t, periods.get(i));
                    //TODO check policy type and change queries
                    PreparedStatement up = PerfCon.prepareStatement("UPDATE agg2 set "
                            + " avail=?, "
                            + "  timestampepoch=?,  "
                            + " sla=?,"
                            + "avgcpu =?,  avgmem =? ,  avgthread =?,  avgfile =?"
                            + "WHERE uri=? and soapaction=? and timerange=?;");

                    double avail = getAvailability(now, periods.get(i), rs.getString("uri"), t, PerfCon, ConfigCon);
                    long sla = getSLACount(rs.getString("uri"), periods.get(i), PerfCon);
                    up.setDouble(1, avail);
                    up.setLong(2, now);
                    up.setLong(3, getSLACount(rs.getString("uri"), periods.get(i), PerfCon));
                    //avg cpu
                    up.setDouble(4, getAvgCPU(rs.getString("uri"), periods.get(i), PerfCon));
                    up.setLong(5, getAvgMem(rs.getString("uri"), periods.get(i), PerfCon));
                    up.setLong(6, getAvgThread(rs.getString("uri"), periods.get(i), PerfCon));
                    if (rs.getInt("policytype") == PolicyType.PROCESS.ordinal()) {
                        up.setLong(7, getAvgFile(rs.getString("uri"), periods.get(i), PerfCon));
                    } else {
                        up.setLong(7, -1);
                    }
                    up.setString(8, rs.getString("uri"));
                    up.setString(9, t);
                    up.setLong(10, periods.get(i));
                    up.executeUpdate();
                    log.log(Level.DEBUG, "Updated stats for service " + rs.getString("uri") + " action " + t);
                    up.close();
                }
            }
        
            now = System.currentTimeMillis() - now;
            log.log(Level.INFO, "Statistics calculations took " + now + "ms");

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            throw ex;
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);

        }
    }

    private double getAvgCPU(final String url, final Long ts, Connection con) {
        double r = 0;
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            cmd = con.prepareStatement("select avg(percentcpu) from rawdatamachineprocess where uri=? and utcdatetime > ?");
            cmd.setString(1, url);
            cmd.setLong(2, System.currentTimeMillis() - ts);
            rs = cmd.executeQuery();
            if (rs.next()) {
                r = rs.getLong(1);
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
        }
        return r;
    }

    private long getAvgMem(final String url, final Long ts, Connection con) {
        Double r = Double.valueOf(0);
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            cmd = con.prepareStatement("select avg(memoryused) from rawdatamachineprocess where uri=? and utcdatetime > ?");
            cmd.setString(1, url);
            cmd.setLong(2, System.currentTimeMillis() - ts);
            rs = cmd.executeQuery();
            if (rs.next()) {
                r = rs.getDouble(1);
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
        }
        return r.longValue();
    }

    private long getAvgThread(final String url, final Long ts, Connection con) {
        Double r = Double.valueOf(0);
        PreparedStatement cmd = null;
        ResultSet rs = null;

        try {
            cmd = con.prepareStatement("select avg(threads) from rawdatamachineprocess where uri=? and utcdatetime > ?");
            cmd.setString(1, url);
            cmd.setLong(2, System.currentTimeMillis() - ts);
            rs = cmd.executeQuery();
            if (rs.next()) {
                r = rs.getDouble(1);
            }
            rs.close();
            cmd.close();

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (Throwable ex) {
            }
            try {
                if (cmd != null && !cmd.isClosed()) {
                    cmd.close();
                }
            } catch (Throwable ex) {
            }
        }
        return r.longValue();
    }

    private long getAvgFile(final String url, final Long ts, Connection con) {
        Double r = Double.valueOf(0);
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            cmd = con.prepareStatement("select avg(openfiles) from rawdatamachineprocess where uri=? and utcdatetime > ?");
            cmd.setString(1, url);
            cmd.setLong(2, System.currentTimeMillis() - ts);
            rs = cmd.executeQuery();
            if (rs.next()) {
                r = rs.getDouble(1);
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
        }
        return r.longValue();
    }

}
