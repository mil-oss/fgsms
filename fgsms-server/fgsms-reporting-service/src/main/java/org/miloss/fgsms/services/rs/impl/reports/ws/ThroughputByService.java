/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl.reports.ws;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Level;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import static org.miloss.fgsms.services.rs.impl.Reporting.getFilePathDelimitor;
import static org.miloss.fgsms.services.rs.impl.Reporting.pixelHeightCalc;
import org.miloss.fgsms.services.rs.impl.reports.BaseReportGenerator;

/**
 *
 * @author AO
 */
public class ThroughputByService extends BaseWebServiceReport {

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetDisplayName() {
        return "Throughput By Service";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetHtmlFormattedHelp() {
        return super.GetHtmlFormattedHelp() + "This represents the average invocation rate over the given period of time. The accuracy of the data"
                + " is limited to the time range this report was generated from. For the chart, X axis is service, Y axis is throughput (bytes total of all messages/time). "
                + "This data does not necessarily represent bytes on the wire and may or may not include transport overhead, headers, or packetization.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(OutputStreamWriter data, List<String> urls, String path, List<String> files, TimeRange range, String currentuser, SecurityWrapper classification, WebServiceContext ctx) throws IOException {

        Connection con = Utility.getPerformanceDBConnection();
        try {
            PreparedStatement cmd = null;
            ResultSet rs = null;
            DefaultCategoryDataset set = new DefaultCategoryDataset();
            JFreeChart chart = null;
            data.append("<hr /><h2>").append(GetDisplayName()).append("</h2>");
            data.append(GetHtmlFormattedHelp() + "<br />");
            data.append("<table  class=\"table table-hover\"><tr><th>URL</th><th>Invocations</th><th>Msg/Day</th><th>Msg/Hour</th><th>Msg/Min</th><th>Msg/Sec</th></tr>");
            for (int i = 0; i < urls.size(); i++) {
                if (!isPolicyTypeOf(urls.get(i), PolicyType.TRANSACTIONAL)) {
                    continue;
                }
                //https://github.com/mil-oss/fgsms/issues/112
                if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(i), classification, ctx)) {
                    continue;
                }
                String url = Utility.encodeHTML(BaseReportGenerator.getPolicyDisplayName(urls.get(i)));
                long count = 0;
                try {
                    cmd = con.prepareStatement(
                            "select count(*) from RawData where URI=? and "
                            + "(UTCdatetime > ?) and (UTCdatetime < ?) ;");
                    cmd.setString(1, urls.get(i));
                    cmd.setLong(2, range.getStart().getTimeInMillis());
                    cmd.setLong(3, range.getEnd().getTimeInMillis());
                    rs = cmd.executeQuery();

                    try {
                        if (rs.next()) {
                            count = rs.getLong(1);
                        }
                    } catch (Exception ex) {
                        log.log(Level.DEBUG, null, ex);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }

                double d = range.getEnd().getTimeInMillis() - range.getStart().getTimeInMillis();
                double day = d / 86400000;
                double hours = d / 3600000;
                double min = d / 60000;
                double sec = d / 1000;

                data.append("<tr><td>").append(url).append("</td><td>");
                data.append(count+"");
                data.append("</td><td>").append(format.format((double) ((double) count / day))).
                        append("</td><td>").append(format.format((double) ((double) count / hours))).
                        append("</td><td>").append(format.format((double) ((double) count / min))).
                        append("</td><td>").append(format.format((double) ((double) count / sec))).
                        append("</td></tr>");
                if (count > 0) {
                    set.addValue((double) ((double) count / day), url, url);
                }

            }
            chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);
            data.append("</table>");
            try {
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, pixelHeightCalc(set.getRowCount()));
            } catch (IOException ex) {
                log.log(Level.ERROR, "Error saving chart image for request", ex);
            }

            data.append("<img src=\"image_").append(this.getClass().getSimpleName()).append(".png\">");
            files.add(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png");
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(con);
        }
    }

}
