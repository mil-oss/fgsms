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
import java.util.List;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.services.interfaces.dataaccessservice.OperationalRecord;
import static org.miloss.fgsms.statistics.FgsmsStatsv2.log;

/**
 *
 * @author AO
 */
public abstract class BaseJob implements Runnable{

    protected long getSLACount(final String url, final Long ts, Connection con) {
        long r = 0;
        PreparedStatement cmd = null;
        ResultSet rs = null;
        try {
            cmd = con.prepareStatement("select count(*) from slaviolations where uri=? and utcdatetime > ?");
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
    protected void insertRow(Connection perf, final String url, final String action, final long period) {
        PreparedStatement prepareStatement = null;
        try {
            prepareStatement = perf.prepareStatement("BEGIN WORK;"
                    + ";LOCK TABLE agg2 IN ACCESS EXCLUSIVE MODE;"
                    + "INSERT INTO agg2(uri, soapaction, timestampepoch, timerange)    SELECT  ?,  ?, ?, ? WHERE NOT EXISTS"
                    + "(Select uri, soapaction, timerange from agg2 where uri=? and soapaction=? and timerange=?);"
                    + "COMMIT WORK;");

            prepareStatement.setString(1, url);
            prepareStatement.setString(2, action);
            prepareStatement.setLong(3, System.currentTimeMillis());
            prepareStatement.setLong(4, period);
            prepareStatement.setString(5, url);
            prepareStatement.setString(6, action);
            prepareStatement.setLong(7, period);
            prepareStatement.executeUpdate();
            prepareStatement.close();
        } catch (SQLException ex) {
            log.log(Level.FATAL, "unable to insert row, attempting rollback", ex);
            PreparedStatement rollback = null;
            try {
                rollback = perf.prepareStatement("ROLLBACK WORK;");
                rollback.execute();
            } catch (Throwable x) {
                log.log(Level.WARN, "unable to roll back transaction", ex);
            } finally {
                DBUtils.safeClose(rollback);
            }
        } finally {
            DBUtils.safeClose(prepareStatement);
        }
    }

    protected double getAvailability(final long now, final long limit, final String uri, final String action, Connection PerfCon, Connection config) {
        try {
            List<OperationalRecord> res = new ArrayList<OperationalRecord>();
            //within our time limit, get the previous item
            PreparedStatement com = PerfCon.prepareStatement("SELECT uri, utcdatetime, status, id, message "
                    + "FROM availability where utcdatetime <= ?  and uri=? order by utcdatetime desc limit 1");
            com.setString(2, uri);
            com.setLong(1, now - limit);
            ResultSet rs = com.executeQuery();
            while (rs.next()) {
                OperationalRecord cur = new OperationalRecord();
                cur.setID(rs.getString("id"));
                cur.setOperational(rs.getBoolean("status"));
                cur.setMessage(rs.getString("message"));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(now - limit);
                cur.setTimestamp((cal));
                res.add(cur);
            }
            rs.close();
            com.close();

            //get all items from the limit until now
            com = PerfCon.prepareStatement("SELECT uri, utcdatetime, status, id, message "
                    + "FROM availability where utcdatetime > ?  and uri=? order by utcdatetime asc");
            com.setString(2, uri);
            com.setLong(1, now - limit);
            rs = com.executeQuery();
            while (rs.next()) {
                OperationalRecord cur = new OperationalRecord();
                cur.setID(rs.getString("id"));
                cur.setOperational(rs.getBoolean("status"));
                cur.setMessage(rs.getString("message"));
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTimeInMillis(rs.getLong("utcdatetime"));
                cur.setTimestamp((cal));
                res.add(cur);
            }
            rs.close();
            com.close();

            if (res.isEmpty()) {
                //special case
                log.log(Level.DEBUG, "special case for " + uri + " no status data is available, return 0");
                return 0;
            }

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(System.currentTimeMillis());
            OperationalRecord cur = new OperationalRecord();
            cur.setOperational(res.get(res.size() - 1).isOperational());
            cur.setTimestamp((cal));
            res.add(cur);

            long totaluptime = 0;
            long totaldowntime = 0;

            //start with the second item and compare to the first
            for (int i = 1; i < res.size(); i++) {
                Calendar tempcal = res.get(i).getTimestamp();

                long current = (tempcal.getTimeInMillis());
                long previous = res.get(i - 1).getTimestamp().getTimeInMillis();

                if (res.get(i - 1).isOperational()) {
                    totaluptime += current - previous;
                } else {
                    totaldowntime += current - previous;
                }

            }

            //last time compared to now
            if (res.get(res.size() - 1).isOperational()) {
                totaluptime += System.currentTimeMillis() - (res.get(res.size() - 1).getTimestamp().getTimeInMillis());
            } else {

                totaldowntime += System.currentTimeMillis() - (res.get(res.size() - 1).getTimestamp().getTimeInMillis());
            }

            double d = (double) ((double) totaluptime / (double) (totaldowntime + totaluptime)) * 100.0;
            /*
             * System.out.println(uri + " " + d); System.out.println("up " +
             * totaluptime + " down " + totaldowntime); System.out.println(d);
             * System.out.println("now " + now); System.out.println("limit" +
             * limit); System.out.println("now-limit ago " + (now - limit));
             * System.out.println((now - limit)); long t = now - limit;
             * System.out.println((t));
             */

            return d;

        } catch (Exception ex) {
            log.log(Level.WARN, "error calculating availability", ex);
        }
        return 0;
    }

}
