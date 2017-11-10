/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl.reports.os;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Level;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import static org.miloss.fgsms.services.rs.impl.Reporting.getFilePathDelimitor;
import org.miloss.fgsms.services.rs.impl.TimeSeriesContainer;
import org.miloss.fgsms.services.rs.impl.reports.BaseReportGenerator;

/**
 *
 * @author AO
 */
public class FreeDiskSpace extends BaseReportGenerator {

    @Override
    public String GetDisplayName() {
        return "Free Disk Space";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This represents the free disk space (MB) on a logical partition on a given machine over time. Low disk space can cause system "
                + "failure or prevent the system from saving audit logs. For the chart, X axis is time, Y axis free disk space host/partition";
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        List<PolicyType> r = new ArrayList<PolicyType>();
        r.add(PolicyType.MACHINE);
        return r;
    }

    @Override
    public void generateReport(OutputStreamWriter data, List<String> urls, String path, List<String> files, TimeRange range, String currentuser, SecurityWrapper classification, WebServiceContext ctx) throws IOException {

        Connection con = Utility.getPerformanceDBConnection();
        try {
            PreparedStatement cmd = null;
            ResultSet rs = null;
            JFreeChart chart = null;
            data.append("<hr /><h2>").append(GetDisplayName()).append("</h2>");
            data.append(GetHtmlFormattedHelp() + "<br />");

            data.append("<table class=\"table table-hover\"><tr><th>URI</th><th>Average Send Rate</th><th>Average Free Disk Space (all paritions)</th><th>Average Write KB/s</th><th>Average Read KB/s</th></tr>");

            TimeSeriesCollection col = new TimeSeriesCollection();
            for (int i = 0; i < urls.size(); i++) {
                if (!isPolicyTypeOf(urls.get(i), PolicyType.MACHINE)) {
                    continue;
                }
                //https://github.com/mil-oss/fgsms/issues/112
                if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(i), classification, ctx)) {
                    continue;
                }
                String url = Utility.encodeHTML(BaseReportGenerator.getPolicyDisplayName(urls.get(i)));
                double average = 0;
                data.append("<tr><td>").append(url).append("</td>");
                try {

                    cmd = con.prepareStatement("select avg(freespace) from rawdatadrives where uri=? and utcdatetime > ? and utcdatetime < ?;");
                    cmd.setString(1, urls.get(i));
                    cmd.setLong(2, range.getStart().getTimeInMillis());
                    cmd.setLong(3, range.getEnd().getTimeInMillis());
                    rs = cmd.executeQuery();
                    if (rs.next()) {
                        average = rs.getDouble(1);

                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }

                data.append("<td>").append(average + "").append("</td>");
                try {
                    cmd = con.prepareStatement("select avg(writekbs) from rawdatadrives where uri=? and utcdatetime > ? and utcdatetime < ?;");
                    cmd.setString(1, urls.get(i));
                    cmd.setLong(2, range.getStart().getTimeInMillis());
                    cmd.setLong(3, range.getEnd().getTimeInMillis());
                    rs = cmd.executeQuery();
                    average = 0;
                    if (rs.next()) {
                        average = rs.getDouble(1);

                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }

                data.append("<td>").append(average + "").append("</td>");
                try {
                    cmd = con.prepareStatement("select avg(readkbs) from rawdatadrives where uri=? and utcdatetime > ? and utcdatetime < ?;");
                    cmd.setString(1, urls.get(i));
                    cmd.setLong(2, range.getStart().getTimeInMillis());
                    cmd.setLong(3, range.getEnd().getTimeInMillis());
                    rs = cmd.executeQuery();
                    average = 0;
                    if (rs.next()) {
                        average = rs.getDouble(1);

                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }
                data.append("<td>").append(average + "").append("</td></tr>");

                //ok now get the raw data....
                TimeSeriesContainer tsc = new TimeSeriesContainer();
                try {
                    cmd = con.prepareStatement("select readkbs, writekbs,freespace, utcdatetime, driveidentifier from rawdatadrives where uri=? and utcdatetime > ? and utcdatetime < ?;");
                    cmd.setString(1, urls.get(i));
                    cmd.setLong(2, range.getStart().getTimeInMillis());
                    cmd.setLong(3, range.getEnd().getTimeInMillis());
                    rs = cmd.executeQuery();

                    while (rs.next()) {
                        TimeSeries ts2 = tsc.Get(url + " " + rs.getString("driveidentifier"), Millisecond.class);
                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(rs.getLong(4));
                        Millisecond m = new Millisecond(gcal.getTime());
                        ts2.addOrUpdate(m, rs.getLong("freespace"));
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }
                for (int ik = 0; ik < tsc.data.size(); ik++) {
                    col.addSeries(tsc.data.get(ik));
                }

            }

            data.append("</table>");
            chart = org.jfree.chart.ChartFactory.createTimeSeriesChart(GetDisplayName(), "Timestamp", "MBytes", col, true, false, false);

            try {
                // if (set.getRowCount() != 0) {
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, 400);
                data.append("<img src=\"image_").append(this.getClass().getSimpleName()).append(".png\">");
                files.add(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png");
                // }
            } catch (IOException ex) {
                log.log(Level.ERROR, "Error saving chart image for request", ex);
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(con);
        }
    }

}
