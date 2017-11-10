/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl.reports;

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
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import org.miloss.fgsms.services.rs.impl.Reporting;
import static org.miloss.fgsms.services.rs.impl.Reporting.getFilePathDelimitor;
import static org.miloss.fgsms.services.rs.impl.reports.BaseReportGenerator.isPolicyTypeOf;

/**
 *
 * @author AO
 */
public class ServiceLevelAgreementReport extends BaseReportGenerator {

    @Override
    public String GetDisplayName() {
        return "Service Level Agreement violations";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Applies to all service policy types. This report generates a table and bar chart outlining all SLA violations for a given service. An SLA is basically"
                + " a rule and action based on recorded data. An SLA Fault is not necessarily a performance and"
                + " are based on the rules configured for each service.";
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        ArrayList<PolicyType> ret = new ArrayList<PolicyType>();
        ret.add(PolicyType.TRANSACTIONAL);
        ret.add(PolicyType.MACHINE);
        ret.add(PolicyType.PROCESS);
        ret.add(PolicyType.STATISTICAL);
        ret.add(PolicyType.STATUS);
        return ret;
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
            data.append("<table class=\"table table-hover\"><tr><th>URI</th><th>SLA Violations</th></tr>");

            for (int i = 0; i < urls.size(); i++) {
                if (!isPolicyTypeOf(urls.get(i), PolicyType.TRANSACTIONAL)) {
                    continue;
                }
                //https://github.com/mil-oss/fgsms/issues/112
                if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(i), classification, ctx)) {
                    continue;
                }
                String url = Utility.encodeHTML(BaseReportGenerator.getPolicyDisplayName(urls.get(i)));
                data.append("<tr><td>").append(url).append("</td><td>");
                try {
                    cmd = con.prepareStatement(
                            "select count(*) from slaviolations where uri = ? and "
                            + "(utcdatetime > ?) and (utcdatetime < ?);");
                    cmd.setString(1, urls.get(i));
                    cmd.setLong(2, range.getStart().getTimeInMillis());
                    cmd.setLong(3, range.getEnd().getTimeInMillis());
                    rs = cmd.executeQuery();
                    long count = -1;
                    try {
                        if (rs.next()) {
                            count = rs.getLong(1);
                        }
                    } catch (Exception ex) {
                        log.log(Level.DEBUG, " error querying database url:" + urls.get(i), ex);
                    }

                    if (count >= 0) {
                        data.append(count + "");
                    } else {
                        data.append("N/A");
                    }

                    if (count > 0) {
                        set.addValue(count, url, url);
                    }
                } catch (Exception ex) {
                    log.log(Level.ERROR, "Error opening or querying the database." + GetDisplayName(), ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }
                data.append("</td></tr>");
            }

            chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);
            data.append("</table>");
            try {
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, Reporting.pixelHeightCalc(urls.size()));
            } catch (Exception ex) {
                log.log(Level.ERROR, "Error saving chart image for request", ex);
            }

            data.append("<img src=\"image_").append(this.getClass().getSimpleName()).append(".png\">");
            files.add(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName()+ ".png");

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(con);
        }
    }

}
