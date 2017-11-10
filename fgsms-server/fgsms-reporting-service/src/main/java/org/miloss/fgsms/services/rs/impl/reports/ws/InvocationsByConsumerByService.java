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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
public class InvocationsByConsumerByService extends BaseWebServiceReport {

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetDisplayName() {
        return "Invocations By Consumer By Service";
    }

      /**
     * {@inheritDoc}
     */
    @Override
    public String GetHtmlFormattedHelp() {
        return super.GetHtmlFormattedHelp() + " This represents the number of invocations (transactions) per consumer for each service. Useful for identifying customers or consumers that are producing more traffic than others";
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
            data.append("<table class=\"table table-hover\"><tr><th>Consumer</th><th>URL</th><th>Invocations</th></tr>");
            Set<String> consumers = new HashSet<String>();

            //   for (int i = 0; i < (consumers.size()-1); i++) {
            for (int k = 0; k < urls.size(); k++) {
                if (!isPolicyTypeOf(urls.get(k), PolicyType.TRANSACTIONAL)) {
                    continue;
                }
                //https://github.com/mil-oss/fgsms/issues/112
                if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(k), classification, ctx)) {
                    continue;
                }
                String url = Utility.encodeHTML(BaseReportGenerator.getPolicyDisplayName(urls.get(k)));
                consumers.clear();
                try {
                    try {
                        cmd = con.prepareStatement("select consumeridentity from rawdata where uri=? and (UTCdatetime > ?) and (UTCdatetime < ?) group by consumeridentity;");
                        cmd.setString(1, urls.get(k));
                        cmd.setLong(2, range.getStart().getTimeInMillis());
                        cmd.setLong(3, range.getEnd().getTimeInMillis());
                        rs = cmd.executeQuery();
                        while (rs.next()) {
                            String s = rs.getString(1);
                            if (!Utility.stringIsNullOrEmpty(s)) {
                                String ids[] = s.split(";");
                                for (int i = 0; i < ids.length; i++) {
                                    if (!Utility.stringIsNullOrEmpty(ids[i])) {

                                        consumers.add(ids[i]);

                                    }
                                }
                            }

                        }
                    } catch (Exception ex) {
                        log.log(Level.WARN, " error querying database forINVOCATIONS_BY_CONSUMER_BY_SERVICE" + urls.get(k), ex);
                    } finally {
                        DBUtils.safeClose(rs);
                        DBUtils.safeClose(cmd);
                    }
                    consumers.add("unspecified");

                    //ok for this service, count the times each consumer as hit it
                    Iterator<String> iterator = consumers.iterator();
                    while (iterator.hasNext()) {
                        String user = iterator.next();
                        long count = 0;
                        try {
                            cmd = con.prepareStatement(
                                    "select count(*) from RawData where URI=? and "
                                    + "(UTCdatetime > ?) and (UTCdatetime < ?) and consumeridentity ~* ?;");
                            cmd.setString(1, urls.get(k));
                            cmd.setLong(2, range.getStart().getTimeInMillis());
                            cmd.setLong(3, range.getEnd().getTimeInMillis());
                            cmd.setString(4, user);

                            rs = cmd.executeQuery();

                            try {
                                if (rs.next()) {
                                    count = rs.getLong(1);
                                }
                            } catch (Exception ex) {
                                log.log(Level.WARN, " error querying database forINVOCATIONS_BY_CONSUMER_BY_SERVICE" + urls.get(k), ex);
                            }
                        } catch (Exception ex) {
                            log.log(Level.WARN, null, ex);
                        } finally {
                            DBUtils.safeClose(rs);
                            DBUtils.safeClose(cmd);
                        }

                        if (count > 0) {    //cut down on the chatter
                            data.append("<tr><td>").
                                    append(url).
                                    append("</td><td>").
                                    append(Utility.encodeHTML(user)).
                                    append("</td><td>");
                            data.append(count + "");
                            data.append("</td></tr>");

                            set.addValue(count, user, urls.get(k));

                        }
                    }

                    //this is for anonymous users or for when an identity was not
                    //recorded by the agent
                    long count = 0;
                    try {
                        cmd = con.prepareStatement(
                                "select count(*) from RawData where URI=? and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?) and consumeridentity is null;");
                        cmd.setString(1, urls.get(k));
                        cmd.setLong(2, range.getStart().getTimeInMillis());
                        cmd.setLong(3, range.getEnd().getTimeInMillis());

                        rs = cmd.executeQuery();

                        try {
                            if (rs.next()) {
                                count = rs.getLong(1);
                            }
                        } catch (Exception ex) {
                            log.log(Level.WARN, " error querying database forINVOCATIONS_BY_CONSUMER_BY_SERVICE" + urls.get(k), ex);
                        }
                    } catch (Exception ex) {
                        log.log(Level.WARN, null, ex);
                    } finally {
                        DBUtils.safeClose(rs);
                        DBUtils.safeClose(cmd);
                    }

                    if (count > 0) { //cut down on the chatter
                        data.append("<tr><td>").append(url).append("</td><td>(not recorded)</td><td>");
                        data.append(count + "");
                        data.append("</td></tr>");

                        set.addValue(count, "unspecified", url);
                    }

                } catch (Exception ex) {
                    data.append("0 bytes</td></tr>");
                    log.log(Level.ERROR, "Error opening or querying the database.", ex);
                }
            }
            //  insert query for unspecified chart = org.jfree.chart.ChartFactory.createBarChart(toFriendlyName(get.getType()), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);
            data.append("</table>");
            chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);
            try {
                int height = pixelHeightCalc(set.getColumnCount());
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, height);
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
