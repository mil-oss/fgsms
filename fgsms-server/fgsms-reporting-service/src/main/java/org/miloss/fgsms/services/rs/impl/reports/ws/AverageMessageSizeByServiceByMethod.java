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
public class AverageMessageSizeByServiceByMethod extends BaseWebServiceReport {

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetDisplayName() {
        return "Average Message Size By Service By Method";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetHtmlFormattedHelp() {
        return super.GetHtmlFormattedHelp() + " This represents the average combined request and response message size of each "
                + "service grouped by method. A negative value generally indicates that the information was not able to be collected by the agent."
                + "For the chart, X axis is time, Y axis is response time";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(OutputStreamWriter data, List<String> urls, String path, List<String> files, TimeRange range, String currentuser, SecurityWrapper classification, WebServiceContext ctx) throws IOException {
        int itemcount = 0;
        Connection con = Utility.getPerformanceDBConnection();
        try {
            PreparedStatement cmd = null;
            ResultSet rs = null;
            DefaultCategoryDataset set = new DefaultCategoryDataset();
            JFreeChart chart = null;
            data.append("<hr /><h2>");
            data.append(GetDisplayName());
            data.append("</h2>");
            data.append(GetHtmlFormattedHelp() + "<br />");
            //add description
            data.append("<table class=\"table table-hover\"><tr><th>URL</th><th>Action</th><th>Average Message Size (bytes)</th></tr>");
            itemcount = 0;
            for (int i = 0; i < urls.size(); i++) {
                if (!isPolicyTypeOf(urls.get(i), PolicyType.TRANSACTIONAL)) {
                    continue;
                }
                //https://github.com/mil-oss/fgsms/issues/112
                if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(i), classification, ctx)) {
                    continue;
                }
                String url = Utility.encodeHTML(BaseReportGenerator.getPolicyDisplayName(urls.get(i)));
                try {
                    List<String> actions = getSoapActions(urls.get(i), con);

                    for (int k = 0; k < actions.size(); k++) {
                        itemcount++;
                        long count = -1;
                        try {
                            cmd = con.prepareStatement(
                                    "select AVG(responseSize + requestSize) as messagesSize from RawData where URI=? and "
                                    + "(UTCdatetime > ?) and (UTCdatetime < ?) and soapaction=?");
                            cmd.setString(1, urls.get(i));
                            cmd.setLong(2, range.getStart().getTimeInMillis());
                            cmd.setLong(3, range.getEnd().getTimeInMillis());
                            cmd.setString(4, actions.get(k));

                            rs = cmd.executeQuery();

                            try {
                                if (rs.next()) {
                                    count = rs.getLong(1);
                                }
                            } catch (Exception ex) {
                                log.log(Level.DEBUG, " error querying database for average message size url:" + urls.get(i), ex);
                            }
                        } catch (Exception ex) {
                            log.log(Level.ERROR, "Error opening or querying the database." + this.getClass().getSimpleName(), ex);
                        } finally {
                            DBUtils.safeClose(rs);
                            DBUtils.safeClose(cmd);
                        }

                        data.append("<tr><td>").append(url).append("</td><td>");
                        data.append(Utility.encodeHTML(actions.get(k)));
                        data.append("</td><td>");
                        if (count >= 0) {
                            data.append(count + " bytes");
                        } else {
                            data.append("N/A");
                        }
                        data.append("</td></tr>");
                        if (count >= 0) {
                            set.addValue(count, actions.get(k), url);
                        }

                    }
                } catch (Exception ex) {
                    data.append("0 bytes</td></tr>");
                    log.log(Level.ERROR, "Error opening or querying the database." + this.getClass().getSimpleName(), ex);
                }
            }
            chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);
            data.append("</table>");
            try {
                int height = pixelHeightCalc(itemcount);
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, height);
            } catch (Exception ex) {
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
