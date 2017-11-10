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
public class TotalMessageSizesByService extends BaseWebServiceReport {

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetDisplayName() {
        return "Total Message Size By Service";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetHtmlFormattedHelp() {
        return super.GetHtmlFormattedHelp() + "Displays the sum of all request and response messages sizes, assuming it was recorded. grouped by service."
                + " This data does not necessarily include transport headers, encryption, or any kind transport overhead or packet level"
                + " retransmissions and represents the payload sizes that was recorded by the agent."
                + " The accuracy of the data"
                + " is limited to the time range this report was generated from. For the chart, X axis is service, Y axis is bytes total for all messages";
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
            data.append("This represents the total transmitted message size, request + response, for each service over the given period of time.<br />");
            data.append("<table class=\"table table-hover\"><tr><th>URL</th><th>Total Message Size (bytes)</th></tr>");
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
                            "select sum(requestsize + responsesize) from rawdata where uri=? and "
                            + "(UTCdatetime > ?) and (UTCdatetime < ?) and (requestsize>=0) and (responsesize>=0);");
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

                
                count = 0;
                try {
                    cmd = con.prepareStatement(
                            "select avg(requestsize + responsesize) from rawdata where uri=? and "
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
                        // log.log(Level.WARN, " error querying database for average message size url:" + urls.get(i), ex);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }
                data.append("<tr><td>").append(url).append("</td><td>");
                if (count > 0) {
                    data.append(count + " bytes");
                    set.addValue(count, url, url);
                } else
                    data.append("N/A");
                data.append("</td></tr>");
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
