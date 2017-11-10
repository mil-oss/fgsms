/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl.reports.broker;

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
import static org.miloss.fgsms.services.rs.impl.Reporting.getFilePathDelimitor;
import static org.miloss.fgsms.services.rs.impl.Reporting.pixelHeightCalc;
import org.miloss.fgsms.services.rs.impl.reports.BaseReportGenerator;

/**
 *
 * @author AO
 */
public class QueueTopicCountByBroker extends BaseReportGenerator {

    @Override
    public String GetDisplayName() {
        return "Queue or Topic Count on a Message Broker";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This represents the count of channels/topics/queues for a given message broker. For the chart, X axis is time, Y is queue/topic count";
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        ArrayList<PolicyType> ret = new ArrayList<PolicyType>();
        ret.add(PolicyType.STATISTICAL);
        return ret;
    }

    @Override
    public void generateReport(OutputStreamWriter data, List<String> urls, String path, List<String> files, TimeRange range, String currentuser, SecurityWrapper classification, WebServiceContext ctx) throws IOException {

        Connection con = Utility.getPerformanceDBConnection();
        try{
        PreparedStatement cmd = null;
        ResultSet rs = null;
        DefaultCategoryDataset set = new DefaultCategoryDataset();
        JFreeChart chart = null;

        data.append("<hr /><h2>").append(GetDisplayName()).append("</h2>");
        data.append(GetHtmlFormattedHelp() + "<br />");
        data.append("<table class=\"table table-hover\"><tr><th>URI</th><th>Channel Count</th></tr>");

        for (int i = 0; i < urls.size(); i++) {
            if (!isPolicyTypeOf(urls.get(i), PolicyType.STATISTICAL)) {
                continue;
            }
            //https://github.com/mil-oss/fgsms/issues/112
            if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(i), classification, ctx)) {
                continue;
            }
             String url = Utility.encodeHTML(getPolicyDisplayName(urls.get(i)));
            data.append("<tr><td>").append(url).append("</td>");
            long count = 0;
            try {

                //FIXME could this be simplified?
                cmd = con.prepareStatement("select canonicalname from brokerhistory where host=?  group by canonicalname;");
                cmd.setString(1, urls.get(i));
                rs = cmd.executeQuery();
                if (rs.next()) {
                    count++;
                }

            } catch (Exception ex) {
                log.log(Level.ERROR, "Error opening or querying the database.", ex);
            } finally {
                DBUtils.safeClose(rs);
                DBUtils.safeClose(cmd);
            }
            data.append("<td>").append(count + "").append("</td></tr>");
            set.addValue(count, urls.get(i), urls.get(i));

        }
        data.append("</table>");
       
        chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);

        try {
            //  if (set.getRowCount() != 0) {
            ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, pixelHeightCalc(set.getRowCount()));
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
