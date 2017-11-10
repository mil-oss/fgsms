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
import java.util.Collections;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
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
public class MeanTimeBetweenFailureByService extends BaseWebServiceReport {

    DatatypeFactory df;

    public MeanTimeBetweenFailureByService() {
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetDisplayName() {
        return "Mean time between faults (MTBF)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetHtmlFormattedHelp() {
        return super.GetHtmlFormattedHelp() + "This is the measure of average time between failed service invocations. Failed invocations for a service can be "
                + "for a number of reasons and may not necessarily be due to a problem with the service (could be network related, faulty clients, etc)."
                + "This report does not necessarily represent service up time.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(OutputStreamWriter data, List<String> urls, String path, List<String> files, TimeRange range, String currentuser, SecurityWrapper classification, WebServiceContext ctx) throws IOException {

        DefaultCategoryDataset set = new DefaultCategoryDataset();
        JFreeChart chart = null;
        data.append("<hr /><h2>").append(GetDisplayName()).append("</h2>");
        data.append(GetHtmlFormattedHelp() + "<br />");
        data.append("<table class=\"table table-hover\"><tr><th>URL</th><th>MTBF (ms)</th><th>Timespan</th><th>XML Duration Value</th></tr>");

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
                long mtbf = meanTimeBetweenFailures(urls.get(i), range);
                Duration newDuration = df.newDuration(mtbf);
                data.append("<tr><td>").append(url).append("</td><td>");
                if (mtbf == -1 || mtbf == 0) {
                    data.append("Never</td><td>0</td><td>0</d></tr>");
                } else {
                    data.append(mtbf + "").append("ms</td><td>").append(Utility.durationToString(newDuration)).append("</td><td>").append(newDuration.toString()).append("</td></tr>");
                }
                if (mtbf > 0) {
                    set.addValue(mtbf, url, url);
                }

            } catch (Exception ex) {
                data.append("0 bytes</td></tr>");
                log.log(Level.ERROR, "Error opening or querying the database.", ex);
            }
        }
        chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Service URL", "", set, PlotOrientation.HORIZONTAL, true, false, false);
        data.append("</table>");
        try {
            if (set.getRowCount() != 0) {
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, pixelHeightCalc(set.getRowCount()));
                data.append("<img src=\"image_").append(this.getClass().getSimpleName()).append(".png\">");
                files.add(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png");
            }
        } catch (IOException ex) {
            log.log(Level.ERROR, "Error saving chart image for request", ex);
        }

    }

    private long meanTimeBetweenFailures(String url, TimeRange timeRange) {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            List<Long> faults = new ArrayList<Long>();

            com = con.prepareStatement("select utcdatetime from rawdata where uri=? and success=false and (utcdatetime > ?) and (utcdatetime < ?);");
            com.setString(1, url);
            com.setLong(2, timeRange.getStart().getTimeInMillis());
            com.setLong(3, timeRange.getEnd().getTimeInMillis());
            rs = com.executeQuery();
            while (rs.next()) {
                faults.add(rs.getLong(1));

            }

            if (faults.isEmpty()) {
                return 0;
            }
            Collections.sort(faults);
            long diff = 0;
            for (int i = 1; i < faults.size(); i++) {
                diff += (faults.get(i) - faults.get(i - 1));
            }
            return diff / (faults.size() - 1);

        } catch (Exception ex) {
            log.log(Level.DEBUG, null, ex);
            //log.log(Level.WARN, "error calculating mtbf", ex);
            //not really important
            //expecting divid by 0 is there's no faults, etc
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return -1;
    }
}
