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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Level;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import org.miloss.fgsms.plugins.reporting.ReportGeneratorPlugin;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import static org.miloss.fgsms.services.rs.impl.Reporting.getFilePathDelimitor;
import static org.miloss.fgsms.services.rs.impl.Reporting.log;
import org.miloss.fgsms.services.rs.impl.StatusRecordsExt;

/**
 *
 * @author AO
 */
public class AvailabilityByService implements ReportGeneratorPlugin {

    @Override
    public String GetDisplayName() {
        return "Availability By Service";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This represents the availability over time. A value of 1 "
                + "represents operational and 0 "
                + "represents non-operational status. " + 
                "For web services, this is limited by the ability for the Bueller component to a) connect to the service and b) authenticate to it.";
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

            JFreeChart chart = null;

            data.append("<hr /><h2>").append(GetDisplayName()).append("</h2>");
            data.append(GetHtmlFormattedHelp() + "<br />");
            data.append("<table class=\"table table-hover\"><tr><th>URI</th><th>Number of status changes</th><th>Percent Uptime</th><th>Percent Downtime</th></tr>");
            DecimalFormat percentFormat = new DecimalFormat("###.#####");
            TimeSeriesCollection col = new TimeSeriesCollection();
            for (int i = 0; i < urls.size(); i++) {
                //https://github.com/mil-oss/fgsms/issues/112
                if (!UserIdentityUtil.hasReadAccess(currentuser, "getReport", urls.get(i), classification, ctx)) {
                    continue;
                }
                String url = Utility.encodeHTML(BaseReportGenerator.getPolicyDisplayName(urls.get(i)));
                TimeSeries s1 = new TimeSeries(url, org.jfree.data.time.Millisecond.class);
                try {
                    data.append("<tr><td>").append(url).append("</td>");
                    List<StatusRecordsExt> records = getStatusRecords(urls.get(i), range, con);
                    //special case, no status records available
                    if (records == null || records.isEmpty()) {
                        data.append("<td>-</td><td>-</td></tr>");
                        continue;
                    }
                    double uptime = getUptimePercentage(records, range);
                    data.append("<td>").append(records.size() + "").append("</td><td>").append(percentFormat.format(uptime)).
                            append("</td><td>").append(percentFormat.format(100 - uptime)).append("</td></tr>");
                    TimeSeriesDataItem t = null;
                    for (int k = 0; k < records.size(); k++) {
                        if (records.get(k).status) {
                            try {
                                t = new TimeSeriesDataItem(new Millisecond(records.get(k).gcal.getTime()), 1);
                                s1.addOrUpdate(t);
                            } catch (Exception ex) {
                                log.log(Level.WARN, null, ex);
                            }
                        } else {
                            try {
                                t = new TimeSeriesDataItem(new Millisecond(records.get(k).gcal.getTime()), 0);
                                s1.addOrUpdate(t);
                            } catch (Exception ex) {
                                log.log(Level.WARN, null, ex);
                            }
                        }
                        col.addSeries(s1);
                    }

                } catch (Exception ex) {
                    log.log(Level.ERROR, "Error opening or querying the database.", ex);
                }

            }
            chart = org.jfree.chart.ChartFactory.createTimeSeriesChart(GetDisplayName(), "Timestamp", "Status", col, true, false, false);

            data.append("</table>");
            try {
                ChartUtilities.saveChartAsPNG(new File(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png"), chart, 1500, 800);
                data.append("<img src=\"image_").append(this.getClass().getSimpleName()).append(".png\">");
                files.add(path + getFilePathDelimitor() + "image_" + this.getClass().getSimpleName() + ".png");

            } catch (IOException ex) {
                log.log(Level.ERROR, "Error saving chart image for request", ex);
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(con);
        }
    }

    private List<StatusRecordsExt> getStatusRecords(String url, TimeRange range, Connection con) {
        List<StatusRecordsExt> ret = new ArrayList<StatusRecordsExt>();
        //first get the record just before the start of the range, if it exists
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select status from availability where uri=? and  utcdatetime < ? order by utcdatetime desc limit 1;");
            com.setString(1, url);
            com.setLong(2, range.getStart().getTimeInMillis());
            rs = com.executeQuery();
            if (rs.next()) {
                //add a record for the exact start time of the range
                StatusRecordsExt x = new StatusRecordsExt();
                x.gcal = new GregorianCalendar();
                x.gcal.setTimeInMillis(range.getStart().getTimeInMillis());
                x.status = rs.getBoolean("status");
                ret.add(x);
            }
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
        }
        try {
            //get everything within the range
            com = con.prepareStatement("select * from availability where uri=? and  utcdatetime >= ?  and utcdatetime <=? order by utcdatetime asc;");
            com.setString(1, url);
            com.setLong(2, range.getStart().getTimeInMillis());
            com.setLong(3, range.getEnd().getTimeInMillis());
            rs = com.executeQuery();
            boolean lastStatus = true;
            while (rs.next()) {
                //in order to produce a square wave style chart, inject an additional point for this time-1 for the same status
                //as the previous item
                if (!ret.isEmpty()) {
                    StatusRecordsExt get = ret.get(ret.size()-1);
                    StatusRecordsExt x = new StatusRecordsExt();
                    x.gcal = new GregorianCalendar();
                    x.gcal.setTimeInMillis(rs.getLong("utcdatetime")-1);
                    x.status = get.status;
                    ret.add(x);
                }
                StatusRecordsExt x = new StatusRecordsExt();
                x.gcal = new GregorianCalendar();
                x.gcal.setTimeInMillis(rs.getLong("utcdatetime"));
                x.status = rs.getBoolean("status");
                lastStatus = x.status;
                ret.add(x);
            }

            StatusRecordsExt x = new StatusRecordsExt();
            x.gcal = new GregorianCalendar();
            x.gcal.setTimeInMillis(range.getEnd().getTimeInMillis());
            x.status = lastStatus;
            ret.add(x);

            //getting the next record isn't important in this case as we are only interested in data within the range.
            return ret;
        } catch (Exception ex) {

            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
        }
        return null;
    }

    /**
     *
     * @param records a non empty list
     * @param range
     * @return
     */
    private double getUptimePercentage(List<StatusRecordsExt> records, TimeRange range) {
        long totaluptime = 0;
        long totaltime = range.getEnd().getTimeInMillis() - range.getStart().getTimeInMillis();
        if (records.size() == 1) {
            if (records.get(0).status) {
                return 100;
            } else {
                return 0;
            }
        }
        for (int i = 0; i < records.size() - 1; i++) {
            if (records.get(i).status) {
                totaluptime += records.get(i + 1).gcal.getTimeInMillis() - records.get(i).gcal.getTimeInMillis();
            }
        }
        if (records.get(records.size() - 1).status) {
            totaluptime += range.getEnd().getTimeInMillis() - records.get(records.size() - 1).gcal.getTimeInMillis();
        }
        if (totaltime > 0) {
            return (double) ((double) totaluptime / (double) totaltime * 100D);
        }
        return -1;
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return Collections.EMPTY_LIST;
    }

}
