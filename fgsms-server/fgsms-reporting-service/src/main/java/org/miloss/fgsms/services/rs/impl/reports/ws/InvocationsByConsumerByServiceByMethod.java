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
public class InvocationsByConsumerByServiceByMethod extends BaseWebServiceReport {

    @Override
    public String GetDisplayName() {
        return "Invocations by Consumer by Service by Method";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetHtmlFormattedHelp() {
        return super.GetHtmlFormattedHelp() + " This represents the number of invocations (transactions) per consumer for each service grouped by method. Useful for identifying customers or consumers that are producing more traffic than others";
    }

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
            data.append("<table class=\"table table-hover\"><tr><th>URL</th><th>Consumer</th><th>Method</th><th>Invocations</th></tr>");
            Set<String> consumers2 = new HashSet<String>();
            List<String> actions = new ArrayList<String>();
            for (int k = 0; k < urls.size(); k++) {
                if (!isPolicyTypeOf(urls.get(k), PolicyType.TRANSACTIONAL)) {
                    continue;
                }
                //https://github.com/mil-oss/fgsms/issues/112
                if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(k), classification, ctx)) {
                    continue;
                }
                String url = Utility.encodeHTML(BaseReportGenerator.getPolicyDisplayName(urls.get(k)));
                consumers2.clear();
                actions.clear();

                try {
                    actions = getSoapActions(urls.get(k), con);

                    rs = null;
                    for (int k2 = 0; k2 < actions.size(); k2++) {
                        try {
                            consumers2.clear();

                            cmd = con.prepareStatement("select  consumeridentity from rawdata where uri=?  and (UTCdatetime > ?) and (UTCdatetime < ?) and soapaction=? group by consumeridentity;");
                            cmd.setString(1, urls.get(k));
                            cmd.setLong(2, range.getStart().getTimeInMillis());
                            cmd.setLong(3, range.getEnd().getTimeInMillis());
                            cmd.setString(4, actions.get(k2));
                            rs = cmd.executeQuery();
                            while (rs.next()) {
                                String s = rs.getString(1);

                                //   log.log(Level.WARN, " debug INVOCATIONS_BY_CONSUMER_BY_SERVICE url:" + urls.get(k) + " user: " + s);
                                if (!Utility.stringIsNullOrEmpty(s)) {
                                    String ids[] = s.split(";");
                                    for (int i = 0; i < ids.length; i++) {
                                        if (!Utility.stringIsNullOrEmpty(ids[i])) {
                                            //  log.log(Level.WARN, " debug2 INVOCATIONS_BY_CONSUMER_BY_SERVICE url:" + urls.get(k) + " user: " + ids[i]);
                                            if (!consumers2.contains(ids[i])) {
                                                consumers2.add(ids[i]);
                                            }
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
                        consumers2.add("unspecified");

                        //ok for this service, count the times each consumer as hit it
                        Iterator<String> iterator = consumers2.iterator();
                        while (iterator.hasNext()) {

                            String u = iterator.next();

                            long count = 0;
                            try {
                                cmd = con.prepareStatement(
                                        "select count(*) from RawData where URI=? and soapaction=? and "
                                        + "(UTCdatetime > ?) and (UTCdatetime < ?) and consumeridentity ~* ?;");
                                cmd.setString(1, urls.get(k));
                                cmd.setString(2, actions.get(k2));
                                cmd.setLong(3, range.getStart().getTimeInMillis());
                                cmd.setLong(4, range.getEnd().getTimeInMillis());
                                cmd.setString(5, u);

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

                            if (count > 0) {  //cut down on the noise
                                data.append("<tr><td>").append(url).append("</td><td>").
                                        append(Utility.encodeHTML(u)).
                                        append("</td><td>").
                                        append(Utility.encodeHTML(actions.get(k2))).
                                        append("</td><td>");
                                data.append(count + "");
                                data.append("</td></tr>");

                                set.addValue(count, u, urls.get(k) + " " + actions.get(k2));
                            }
                        }

                        long count = 0;
                        try {
                            cmd = con.prepareStatement(
                                    "select count(*) from RawData where URI=? and soapaction=? and "
                                    + "(UTCdatetime > ?) and (UTCdatetime < ?) and consumeridentity is null;");
                            cmd.setString(1, urls.get(k));
                            cmd.setString(2, actions.get(k2));
                            cmd.setLong(3, range.getStart().getTimeInMillis());
                            cmd.setLong(4, range.getEnd().getTimeInMillis());

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

                        if (count > 0) {    //cut down on the noise
                            data.append("<tr><td>").
                                    append(url).
                                    append("</td><td>unspecified</td><td>").
                                    append(Utility.encodeHTML(actions.get(k2))).
                                    append("</td><td>");
                            data.append(count + "");
                            data.append("</td></tr>");

                            set.addValue(count, "unspecified", url + " " + actions.get(k2));

                        }
                    }

                } catch (Exception ex) {
                    log.log(Level.ERROR, "Error opening or querying the database.", ex);
                }
            }
            //  insert query for unspecified chart = org.jfree.chart.ChartFactory.createBarChart(toFriendlyName(get.getType()), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);
            data.append("</table>");
            chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);
            try {
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, pixelHeightCalc(set.getColumnCount()));
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
