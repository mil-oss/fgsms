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
import org.miloss.fgsms.services.rs.impl.reports.BaseReportGenerator;

/**
 *
 * @author AO
 */
public class OpenFilesByProcess extends BaseReportGenerator {

    @Override
    public String GetDisplayName() {
        return "Open Files by Process";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This represents the number of open file handles that are owned by a specific process. A high or increasing"
                + " number handles can indicate a resource leak. For the chart, X axis is time, Y axis open file handle count";
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        List<PolicyType> r = new ArrayList<PolicyType>();
        r.add(PolicyType.PROCESS);
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
            data.append("<table class=\"table table-hover\"><tr><th>URI</th><th>Average Open File Handles Count</th></tr>");
            TimeSeriesCollection col = new TimeSeriesCollection();
            for (int i = 0; i < urls.size(); i++) {
                if (!isPolicyTypeOf(urls.get(i), PolicyType.PROCESS)) {
                    continue;
                }
                //https://github.com/mil-oss/fgsms/issues/112
                if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(i), classification, ctx)) {
                    continue;
                }
String url = Utility.encodeHTML(BaseReportGenerator.getPolicyDisplayName(urls.get(i)));
                data.append("<tr><td>").append(url).append("</td>");
                double average = 0;
                try {
                    cmd = con.prepareStatement("select avg(openfiles) from rawdatamachineprocess where uri=? and utcdatetime > ? and utcdatetime < ?;");
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

                data.append("<td>").append(average + "").append("</td></tr>");
                TimeSeries ts = new TimeSeries(url, Millisecond.class);
                try {
                    //ok now get the raw data....
                    cmd = con.prepareStatement("select utcdatetime, openfiles from rawdatamachineprocess where uri=? and utcdatetime > ? and utcdatetime < ?;");
                    cmd.setString(1, urls.get(i));
                    cmd.setLong(2, range.getStart().getTimeInMillis());
                    cmd.setLong(3, range.getEnd().getTimeInMillis());

                    rs = cmd.executeQuery();

                    while (rs.next()) {

                        GregorianCalendar gcal = new GregorianCalendar();
                        gcal.setTimeInMillis(rs.getLong(1));
                        Millisecond m = new Millisecond(gcal.getTime());
                        ts.addOrUpdate(m, rs.getLong(2));

                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }

                col.addSeries(ts);

            }

            chart = org.jfree.chart.ChartFactory.createTimeSeriesChart(GetDisplayName(), "Timestamp", "Count", col, true, false, false);

            data.append("</table>");
            try {
                // if (set.getRowCount() != 0) {
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, 400);
                data.append("<img src=\"image_").append(this.getClass().getSimpleName()).append(".png\">");
                files.add(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png");
                //}
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
