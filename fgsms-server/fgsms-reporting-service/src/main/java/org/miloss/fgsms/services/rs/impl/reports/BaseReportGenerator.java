/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.miloss.fgsms.services.rs.impl.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.plugins.reporting.ReportGeneratorPlugin;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.sla.SLACommon;

/**
 *
 * @author AO
 */
public abstract class BaseReportGenerator implements ReportGeneratorPlugin{

    protected static final String name = "fgsms.ReportingService";
    public final static Logger log = Logger.getLogger(name);
    public static String allitems = "All-Methods";
    public final 
            DecimalFormat format = new DecimalFormat("###,###.##");
      
    public static String getPolicyDisplayName(String uri){
        ServicePolicy policy = SLACommon.LoadPolicyPooled(uri);
        if (policy==null){
            //very unexpected
            return uri;
        }
        if (policy.getDisplayName()!=null && policy.getDisplayName().length()>0)
            return policy.getDisplayName();
        return uri;
    }
    protected static boolean isPolicyTypeOf(String uri, PolicyType p) {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select policytype from servicepolicies where uri=?");
            com.setString(1, uri);
            rs = com.executeQuery();
            if (rs.next()) {
                int x = rs.getInt(1);
                if (PolicyType.values()[x].equals(p)) {
                    return true;
                }
            }

        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return false;

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
