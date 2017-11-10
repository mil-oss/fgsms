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
import java.util.ArrayList;
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
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import static org.miloss.fgsms.services.rs.impl.Reporting.getFilePathDelimitor;
import static org.miloss.fgsms.services.rs.impl.Reporting.pixelHeightCalc;

/**
 *
 * @author AO
 */
public class SuccessFailureCountByHostingServer extends BaseWebServiceReport {

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetDisplayName() {
        return "Success/Failure Count by Hosting Server";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(OutputStreamWriter data, List<String> urls, String path, List<String> files, TimeRange range, String currentuser, SecurityWrapper classification, WebServiceContext ctx) throws IOException {
        if (!UserIdentityUtil.hasGlobalAdministratorRole(currentuser, "SUCCESS_FAILURE_COUNT_BY_HOSTING_SERVER", classification, ctx)) {
            data.append("<h2>Access for " + GetDisplayName() + " was denied for non-global admin users</h2>");
        }

        Connection con = Utility.getPerformanceDBConnection();
        try {
            PreparedStatement cmd = null;
            ResultSet rs = null;
            DefaultCategoryDataset set = new DefaultCategoryDataset();
            JFreeChart chart = null;
            data.append("<hr /><h2>").append(GetDisplayName()).append("</h2>");
            data.append("This represents Web Application Server utilization (host) by success and failure rates.<br />");
            data.append("<table class=\"table table-hover\"><tr><th>Host</th><th>Successes</th><th>Failures</th></tr>");
            List<String> dcs = new ArrayList<String>();
            try {

                cmd = con.prepareStatement("select hostingsource from RawData group by hostingsource;");
                rs = cmd.executeQuery();
                while (rs.next()) {
                    dcs.add(rs.getString(1));
                }
            } catch (Exception ex) {
                log.log(Level.WARN, null, ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(cmd);
            }
            try {
                //          itemcount = dcs.size();
                for (int i = 0; i < dcs.size(); i++) {
                    data.append("<tr><td>").append(Utility.encodeHTML(dcs.get(i))).append("</td>");
                    int success = 0;
                    int failures = 0;
                    try {
                        cmd = con.prepareStatement("select count(*) from RawData where hostingsource=? and success=false and UTCdatetime > ? and UTCdatetime < ?;");
                        cmd.setString(1, dcs.get(i));
                        cmd.setLong(2, range.getStart().getTimeInMillis());
                        cmd.setLong(3, range.getEnd().getTimeInMillis());
                        rs = cmd.executeQuery();
                        try {
                            if (rs.next()) {
                                failures = rs.getInt(1);
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

                    try {
                        cmd = con.prepareStatement("select count(*) from RawData where hostingsource=? and success=true and UTCdatetime > ? and UTCdatetime < ?;");
                        cmd.setString(1, dcs.get(i));
                        cmd.setLong(2, range.getStart().getTimeInMillis());
                        cmd.setLong(3, range.getEnd().getTimeInMillis());
                        rs = cmd.executeQuery();
                        try {
                            if (rs.next()) {
                                success = rs.getInt(1);
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
                    data.append("<td>").append(success + "").append("</td>");
                    data.append("<td>").append(failures + "").append("</td></tr>");
                    if (success > 0) {
                        set.addValue(success, dcs.get(i) + " Success", dcs.get(i) + " Success");
                    }
                    if (failures > 0) {
                        set.addValue(failures, dcs.get(i) + " Failures", dcs.get(i) + " Failures");
                    }

                }

            } catch (Exception ex) {
                log.log(Level.ERROR, "Error generating chart information.", ex);
            }
            chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Hosting Servers", "", set, PlotOrientation.HORIZONTAL, true, false, false);
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
