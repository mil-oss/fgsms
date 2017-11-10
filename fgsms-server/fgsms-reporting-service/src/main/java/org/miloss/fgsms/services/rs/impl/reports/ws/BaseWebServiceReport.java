/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.rs.impl.reports.ws;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.rs.impl.reports.BaseReportGenerator;
import org.miloss.fgsms.services.rs.impl.Reporting;

/**
 *
 * @author AO
 */
public abstract class BaseWebServiceReport extends BaseReportGenerator{

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PolicyType> GetAppliesTo() {
        ArrayList<PolicyType> ret = new ArrayList<PolicyType>();
        ret.add(PolicyType.TRANSACTIONAL);
        return ret;
    }
    
     /**
     * {@inheritDoc}
     */
    @Override
    public String GetHtmlFormattedHelp() {
        return "Applies to web services and transactional services only and services that generally have a request/response or a start/stop messaging pattern. ";
    }
  
    
    
    protected List<String> getSoapActions(final String url, Connection PerfCon) {
        List<String> list = new ArrayList<String>();
        PreparedStatement comm = null;
        ResultSet rs = null;
        try {

            comm = PerfCon.prepareStatement("Select soapaction from actionlist where URI=? order by soapaction  desc;");
            comm.setString(1, url);
            rs = comm.executeQuery();
            while (rs.next()) {
                String s = rs.getString(1);
                s = s.trim();
                if (!Utility.stringIsNullOrEmpty(s)) {
                    if (!s.equalsIgnoreCase(Reporting.allitems)) {
                        list.add(s);
                    }
                }
            }

        } catch (Exception ex) {
            log.log(Level.WARN, "error loading soap action list", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(comm);
        }
        return list;
    }
}
