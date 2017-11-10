/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.das.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import static org.miloss.fgsms.services.das.impl.DAS4jBean.log;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.ArrayOfServiceType;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.ServiceType;
import org.miloss.fgsms.services.interfaces.dataaccessservice.ServiceUnavailableException;
import org.miloss.fgsms.services.interfaces.faults.ServiceUnavailableFaultCodes;

/**
 *
 * @author AO
 */
public class DASHelper {

    protected static GetPerformanceAverageStatsResponseMsg getPerformanceAvgStatsFromDB(WebServiceContext ctx, String uri, String displayname, SecurityWrapper classification) {
        Utility.validateClassification(classification);
        /*
             * 12-10-2011 if (timeRange == null) { throw new
             * IllegalArgumentException("time range is null"); }
         */
        if (Utility.stringIsNullOrEmpty(uri)) {
            throw new IllegalArgumentException("uri is null");
        }
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(DAS4jBean.class.getCanonicalName(), "GetPerformanceAvgStatsFromDB", currentUser, uri, classification, ctx.getMessageContext());
        GetPerformanceAverageStatsResponseMsg res = new GetPerformanceAverageStatsResponseMsg();
        res.setClassification(classification);
        res.setAverageResponseTime(-1);
        res.setFailingInvocations(-1);
        res.setSuccessfulInvocations(-1);
        res.setURL(uri);

        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement comm = null;
        ResultSet r = null;
        try {

            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            //revised 6-13-2012 to pull from quick stats agg2 table for adjustable
            //5, 15, 60 and 24 are mandatory
            //revised dec-10-2011 to pull from quickstats "agg" table
            boolean ok = false;
            comm = con.prepareStatement(" select avgres, success, failure, sla, mtbf from agg2 where URI=?  and soapaction=? and timerange=?;");
            //12-10-2011 PreparedStatement comm = con.prepareStatement(" select success, responsetimems,slafault from RawData where URI=? and Success=true and (UTCdatetime > ?) and (UTCdatetime < ?);");
            comm.setString(1, uri);
            comm.setString(2, "All-Methods");
            comm.setLong(3, 86400000);
            //12-10-2011 comm.setLong(2, timeRange.getStart().getTimeInMillis());
            //12-10-2011 comm.setLong(3, timeRange.getEnd().getTimeInMillis());

            r = comm.executeQuery();
            if (r.next()) {
                res.setAverageResponseTime(r.getLong("avgres"));
                res.setFailingInvocations(r.getLong("failure"));

                res.setMTBF(DAS4jBean.df.newDuration(r.getLong("mtbf")));
                res.setServiceLevelAgreementViolations(r.getLong("sla"));
                res.setSuccessfulInvocations(r.getLong("success"));
                ok = true;
            }

            if (!ok) {
                log.log(Level.WARN, "statistics not found for service " + uri);
                return null;
            }
            if (!Utility.stringIsNullOrEmpty(displayname)) {
                res.setDisplayName(displayname);
            }
            return res;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error getting average stats from database", ex);
        } finally {
            DBUtils.safeClose(r);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        return null;
    }

    /**
     * this function filters results by the current request context username
     *
     * @param classification
     * @return
     * @throws ServiceUnavailableException
     */
    protected static ArrayOfServiceType getServiceListfromPolicyDB(WebServiceContext ctx, SecurityWrapper classification) throws ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(DAS4jBean.class.getCanonicalName(), "GetServiceListfromPolicyDB", currentUser, "", classification, ctx.getMessageContext());
        //here user access is controlled by the database stored proceedure which filters
        //access by user permissions
        ArrayOfServiceType list = new ArrayOfServiceType();
        ServiceType temp = null;
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {

            if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "GetServiceListfromPolicyDB", (classification), ctx)) {
                comm = con.prepareStatement("select URI,  displayname, policytype, hostname, domaincol, parenturi from servicepolicies order by uri asc;");
            } else {
                //changed RC5
                comm = con.prepareStatement("select ObjectURI as URI, displayname, policytype,hostname, domaincol, parenturi from UserPermissions, servicepolicies where servicepolicies.uri=objecturi and username=? and (ReadObject=true or WriteObject=true or AdministerObject=true or AuditObject=true)  order by URI asc;");
                comm.setString(1, currentUser);

            }
            results = comm.executeQuery();
            int count = 0;
            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            while (results.next()) {
                temp = new ServiceType();
                temp.setURL(results.getString("URI"));
                temp.setDisplayName(results.getString("displayname"));
                temp.setPolicyType(PolicyType.values()[results.getInt("policytype")]);
                temp.setDomainname(results.getString("domaincol"));
                temp.setHostname(results.getString("hostname"));
                temp.setParentobject(results.getString("parenturi"));

                list.getServiceType().add(temp);
                count++;
            }
            log.log(Level.INFO, "GetServiceListfromPolicyDB records returned " + count + " for " + currentUser);

        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
            ServiceUnavailableException code = new ServiceUnavailableException("", null);
            code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
            throw code;
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        return list;
        // return null;
    }

    /**
     * Returns a list of services, filtered by the current user and policy type
     *
     * @param classification
     * @param p
     * @return
     * @throws ServiceUnavailableException
     */
    protected static ArrayOfServiceType getServiceListfromPolicyDB(WebServiceContext ctx, SecurityWrapper classification, PolicyType p) throws ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(DAS4jBean.class.getCanonicalName(), "GetServiceListfromPolicyDB", currentUser, "", classification, ctx.getMessageContext());

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            //here user access is controlled by the database stored proceedure which filters
            //access by user permissions
            ArrayOfServiceType list = new ArrayOfServiceType();
            ServiceType temp = null;

            if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "GetServiceListfromPolicyDB", (classification), ctx)) {
                comm = con.prepareStatement("select URI,  displayname, policytype, hostname, domaincol, parenturi from servicepolicies where policytype = ? order by uri asc;");
                comm.setInt(1, p.ordinal());

            } else {
                //changed RC5
                comm = con.prepareStatement("select ObjectURI as URI, displayname, policytype, hostname, domaincol, parenturi from UserPermissions, servicepolicies where servicepolicies.uri=objecturi and username=? and (ReadObject=true or WriteObject=true or AdministerObject=true or AuditObject=true)  and policytype = ? order by URI asc;");
                comm.setString(1, currentUser);
                comm.setInt(2, p.ordinal());

            }
            results = comm.executeQuery();
            int count = 0;
            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            while (results.next()) {
                temp = new ServiceType();
                temp.setURL(results.getString("URI"));
                temp.setDisplayName(results.getString("displayname"));
                temp.setPolicyType(PolicyType.values()[results.getInt("policytype")]);
                temp.setDomainname(results.getString("domaincol"));
                temp.setHostname(results.getString("hostname"));
                temp.setParentobject(results.getString("parenturi"));
                list.getServiceType().add(temp);
                count++;
            }
            log.log(Level.INFO, "GetServiceListfromPolicyDB records by policy type returned " + count + " for " + currentUser);
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            if (list.getServiceType().isEmpty()) {
                return null;
            }
            return list;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
        // return null;
    }

    /**
     * loads all actions for a given service from the actionlist in perf db
     * All-Actions is NOT explicitly added
     *
     * @param actions - out
     * @param uRL
     */
    protected static void addServiceActionsFromDB(List<String> actions, String uRL) {
        if (Utility.stringIsNullOrEmpty(uRL)) {
            throw new IllegalArgumentException("URL is null");
        }
        Connection con = Utility.getPerformanceDBConnection();
        ResultSet results = null;
        PreparedStatement comm = null;
        try {

            //dec 10 2011  PreparedStatement comm = con.prepareStatement("Select action from RawData where URI=? group by action;");
            comm = con.prepareStatement("Select soapaction from actionlist where URI=?;");
            comm.setString(1, uRL);
            results = comm.executeQuery();
            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            while (results.next()) {
                actions.add(results.getString("soapaction"));
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "error getting action list for service " + uRL, ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
    }

    /**
     * Returns a list of services, filtered by the current user and policy type
     * and hostname
     *
     * @param classification
     * @param p
     * @return
     * @throws ServiceUnavailableException
     */
    protected static ArrayOfServiceType getServiceListfromPolicyDB(WebServiceContext ctx, SecurityWrapper classification, PolicyType p, String hostname) throws ServiceUnavailableException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
            AuditLogger.logItem(DAS4jBean.class.getCanonicalName(), "GetServiceListfromPolicyDB", currentUser, "", classification, ctx.getMessageContext());
            //here user access is controlled by the database stored proceedure which filters
            //access by user permissions
            ArrayOfServiceType list = new ArrayOfServiceType();
            ServiceType temp = null;

            if (UserIdentityUtil.hasGlobalAdministratorRole(currentUser, "GetServiceListfromPolicyDB", (classification), ctx)) {
                comm = con.prepareStatement("select URI,  displayname, policytype from servicepolicies where policytype = ? and hostname=? order by uri asc;");
                comm.setInt(1, p.ordinal());
                comm.setString(2, hostname);

            } else {
                //changed RC5
                comm = con.prepareStatement("select ObjectURI as URI, displayname, policytype from UserPermissions, servicepolicies where servicepolicies.uri=objecturi and username=? and (ReadObject=true or WriteObject=true or AdministerObject=true or AuditObject=true)  and policytype = ? and hostname=? order by URI asc;");
                comm.setString(1, currentUser);
                comm.setInt(2, p.ordinal());
                comm.setString(3, hostname);
            }
            results = comm.executeQuery();
            int count = 0;
            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            while (results.next()) {
                temp = new ServiceType();
                temp.setURL(results.getString("URI"));
                temp.setDisplayName(results.getString("displayname"));
                temp.setPolicyType(PolicyType.values()[results.getInt("policytype")]);
                list.getServiceType().add(temp);
                count++;
            }
            log.log(Level.INFO, "GetServiceListfromPolicyDB records by hostname and policy returned " + count + " for " + currentUser);
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            if (list.getServiceType().isEmpty()) {

                return null;
            }

            return list;
        } catch (SQLException ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;

    }

    /**
     * Returns a list of services, filtered by the current user
     *
     * @param uRL
     * @param classification
     * @return
     * @throws ServiceUnavailableException
     */
    protected static ArrayOfServiceType getServiceListfromPolicyDB(WebServiceContext ctx, String uRL, SecurityWrapper classification) throws ServiceUnavailableException {
        String currentUser = UserIdentityUtil.getFirstIdentityToString(ctx);
        AuditLogger.logItem(DAS4jBean.class.getCanonicalName(), "GetServiceListfromPolicyDB", currentUser, "", classification, ctx.getMessageContext());

        UserIdentityUtil.assertReadAccess(uRL, currentUser, "GetServiceListfromPolicyDB", classification, ctx);
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {

            ArrayOfServiceType list = new ArrayOfServiceType();
            ServiceType temp = null;

            comm = con.prepareStatement("Select URI from ServicePolicies where URI=?");
            comm.setString(1, uRL);

            results = comm.executeQuery();
            /////////////////////////////////////////////
            //query
            /////////////////////////////////////////////
            while (results.next()) {
                temp = new ServiceType();
                temp.setURL(results.getString("URI"));
                DASHelper.addServiceActionsFromDB(temp.getActions(), uRL);
                //  log.log(Level.ERROR, "Hello world!" + temp.getActions().size());
                //temp.setActions(GetServiceActionListfromDB(uRL.getValue()));
                list.getServiceType().add(temp);
            }
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
            return list;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);

        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        ServiceUnavailableException code = new ServiceUnavailableException("", null);
        code.getFaultInfo().setCode(ServiceUnavailableFaultCodes.DATA_BASE_UNAVAILABLE);
        throw code;
        // return null;
    }

    protected static String getSLAFaultMsg(Connection con, String slaID) {
        if (con == null) {
            //log it
            return null;
        }
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            com = con.prepareStatement("select msg from slaviolations where incidentid=?;");
            com.setString(1, slaID);
            rs = com.executeQuery();
            if (rs.next()) {
                byte[] msg = rs.getBytes("msg");
                rs.close();
                com.close();
                //can't close it here con.close();
                return new String(msg, Constants.CHARSET);
            }

            return "";
        } catch (Exception ex) {
            log.log(Level.ERROR, "unable to get sla fault text from perf db.", ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
        }
        return null;
    }

    protected static Duration getUpTime(String uri) {
        Connection con = Utility.getPerformanceDBConnection();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            com = con.prepareStatement("select * from availability where uri=? order by utcdatetime desc limit 1");
            com.setString(1, uri);
            Duration d = null;
            rs = com.executeQuery();
            if (rs.next()) {
                long changeat = rs.getLong("utcdatetime");
                long now = System.currentTimeMillis();

                d = DAS4jBean.df.newDuration(now - changeat);
            }
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
            return d;
        } catch (Exception ex) {
            log.log(Level.ERROR, "unable to get uptime for uri " + uri, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }
        return null;
    }

}
