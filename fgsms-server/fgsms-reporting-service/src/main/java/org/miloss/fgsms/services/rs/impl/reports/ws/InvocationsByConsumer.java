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
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.common.TimeRange;
import static org.miloss.fgsms.services.rs.impl.Reporting.getFilePathDelimitor;
import static org.miloss.fgsms.services.rs.impl.Reporting.pixelHeightCalc;

/**
 *
 * @author AO
 */
public class InvocationsByConsumer extends BaseWebServiceReport {

    /**
     * {@inheritDoc}
     */
    @Override
    public String GetDisplayName() {
        return "Invocations By Consumer";
    }
    
     /**
     * {@inheritDoc}
     */
    @Override
    public String GetHtmlFormattedHelp(){
        return super.GetHtmlFormattedHelp() + "Aggregated against all web service traffic, by client username or ip address";
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(OutputStreamWriter data, List<String> urls, String path, List<String> files, TimeRange range, String currentuser, SecurityWrapper classification, WebServiceContext ctx) throws IOException {
        Connection con = Utility.getPerformanceDBConnection();
        try{
        PreparedStatement cmd = null;
        PreparedStatement userQuery = null;
        ResultSet rs = null;
        DefaultCategoryDataset set = new DefaultCategoryDataset();
        JFreeChart chart = null;
        data.append("<hr /><h2>").append(GetDisplayName()).append("</h2>");
        data.append("This represents service usage by consumer for the provided services.<br />");
        //add description
        data.append("<table class=\"table table-hover\"><tr><th>User</th><th>Invocations</th></tr>");

        ResultSet actionRS = null;
        Set<String> users = new HashSet<String>();
        try {

            userQuery = con.prepareStatement("select consumeridentity from rawdata group by consumeridentity");
            actionRS = userQuery.executeQuery();
            users.add("unspecified");
            while (actionRS.next()) {
                try {
                    String s = actionRS.getString(1);
                    s = s.trim();
                    if (!Utility.stringIsNullOrEmpty(s)) {
                        String[] items = s.split(";");
                        for (int x = 0; x < items.length; x++) {
                            if (!Utility.stringIsNullOrEmpty(items[x])) {
                                users.add(items[x].trim());
                            }
                        }
                    }

                } catch (Exception ex) {
                    log.log(Level.WARN, " error querying database", ex);
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, " error querying database", ex);
        } finally {
            DBUtils.safeClose(actionRS);
            DBUtils.safeClose(userQuery);
        }

        try {
            Iterator<String> iterator = users.iterator();
            while (iterator.hasNext()) {
                String u = iterator.next();
                try {
                    if (u.equalsIgnoreCase("unspecified")) {
                        cmd = con.prepareStatement(
                                "select count(*)  from RawData where consumeridentity is null and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?)");

                        cmd.setLong(1, range.getStart().getTimeInMillis());
                        cmd.setLong(2, range.getEnd().getTimeInMillis());
                    } else {
                        cmd = con.prepareStatement(
                                "select count(*)  from RawData where consumeridentity=? and "
                                + "(UTCdatetime > ?) and (UTCdatetime < ?)");
                        cmd.setString(1, u);
                        cmd.setLong(2, range.getStart().getTimeInMillis());
                        cmd.setLong(3, range.getEnd().getTimeInMillis());
                    }
                    rs = cmd.executeQuery();
                    long count = 0;
                    try {
                        if (rs.next()) {
                            count = rs.getLong(1);
                        }
                    } catch (Exception ex) {
                        log.log(Level.DEBUG, " error querying database", ex);
                    }

                    data.append("<tr><td>").append(Utility.encodeHTML(u)).append("</td><td>");
                    data.append(count + "").append("</td></tr>");
                    if (count > 0) {
                        set.addValue(count, u, u);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, " error querying database", ex);
                } finally {
                    DBUtils.safeClose(rs);
                    DBUtils.safeClose(cmd);
                }
            }
        } catch (Exception ex) {
            data.append("0</td></tr>");
            log.log(Level.ERROR, "Error opening or querying the database.", ex);
        } finally {
            DBUtils.safeClose(cmd);
            
        }

        chart = org.jfree.chart.ChartFactory.createBarChart(GetDisplayName(), "Consumers", "", set, PlotOrientation.HORIZONTAL, true, false, false);
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
