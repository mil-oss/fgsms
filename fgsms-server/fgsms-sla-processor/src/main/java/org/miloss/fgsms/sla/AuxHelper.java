/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.
 *
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.sla;

import java.io.StringWriter;
import java.sql.*;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Logger;

;

/**
 * Provides helper functions for getting/setting the status of items that run
 * outside of a web container Such as Qpid Py, Aux.Services Quartz jobs, etc
 *
 * @author AO most functions support pooled (JNDI) or non pooled connections via
 * fgsms.Common
 */
public class AuxHelper {

    private static Logger log = Logger.getLogger("fgsms.SLAProcessor");

    public enum FLAGS {

        AUTO_CREATE,
        NO_AUTO_CREATE
    }

    /**
     * Sets the status of an item wrapper, sets policy type to status, machine
     * name to the current machine name, and domain to unspecified, parent
     * component to null, policies are auto created
     *
     * @param currenstatus
     * @param name
     * @param s
     * @param pooled
     * @deprecatedwrap
     */
    @Deprecated
    public static void TryUpdateStatus(boolean currenstatus, String name, String s, boolean pooled) {
        TryUpdateStatus(currenstatus, name, s, pooled, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName(), null, FLAGS.AUTO_CREATE);
    }

    /**
     * * Sets the status of an item wrapper, sets machine name to the current
     * machine name, and domain to unspecified, parent component to null,
     * policies are auto created
     *
     * @param currenstatus
     * @param name
     * @param s
     * @param pooled
     * @param p
     * @deprecated
     */
    @Deprecated
    public static void TryUpdateStatus(boolean currenstatus, String name, String s, boolean pooled, PolicyType p) {
        TryUpdateStatus(currenstatus, name, s, pooled, p, AuxHelper.UNSPECIFIED, SLACommon.GetHostName(), null, FLAGS.AUTO_CREATE);
    }

    /**
     *
     * @param currenstatus
     * @param name/uri of the service
     * @param statusMessage
     * @param pooled
     * @param policyType
     * @param domain
     * @param server
     * @param parentcomponent
     * @param flag
     */
    public static void TryUpdateStatus(final boolean currenstatus, final String name, final String statusMessage, boolean pooled, final PolicyType policyType, final String domain, final String server, final String parentcomponent, final FLAGS flag) {
        java.sql.Connection con = null;
        java.sql.Connection perf = null;
        PreparedStatement com = null;
        ResultSet availset = null;
        // end get current status
        String oldstatusmsg = "";
        boolean oldstatus = true;
        boolean firstseenstatus = true;
        try {
            if (!pooled) {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();
                perf = Utility.getPerformanceDB_NONPOOLED_Connection();
            } else {
                con = Utility.getConfigurationDBConnection();
                perf = Utility.getPerformanceDBConnection();
            }
            if (flag == FLAGS.AUTO_CREATE) {
                CheckPolicyAndCreate(name, con, policyType, pooled, domain, server, parentcomponent);
            }

            com = perf.prepareStatement("select * from availability where uri=? order by utcdatetime desc limit 1;");
            com.setString(1, name);
            availset = com.executeQuery();
            if (availset.next()) {
                oldstatus = (availset.getBoolean("status"));
                oldstatusmsg = availset.getString("message");
                if (Utility.stringIsNullOrEmpty(oldstatusmsg)) {
                    oldstatusmsg = "";
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(availset);
            DBUtils.safeClose(com);
        }
        try {

            com = con.prepareStatement("select * from status  where uri=?;");
            com.setString(1, name);
            availset = com.executeQuery();
            if (availset.next()) {
                firstseenstatus = false;
            }
            availset.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(availset);
            DBUtils.safeClose(com);
        }

        if (firstseenstatus) {
            PreparedStatement state = null;
            try {
                //insert in status
                state = con.prepareStatement("INSERT INTO status(uri, utcdatetime, message, status, monitored) VALUES (?, ?, ?, ?, ?);");
                state.setString(1, name);
                state.setLong(2, System.currentTimeMillis());
                state.setString(3, statusMessage);
                state.setBoolean(4, currenstatus);
                state.setBoolean(5, true);
                state.executeUpdate();
            } catch (Exception ex) {
                log.log(Level.WARN, null, ex);
            } finally {
                DBUtils.safeClose(state);

            }
            try {
                //insert current into perf availity
                state = perf.prepareStatement("INSERT INTO availability(uri, utcdatetime, id, message, status)   VALUES (?, ?, ?, ?, ?);");
                state.setString(1, name);
                state.setLong(2, System.currentTimeMillis());
                state.setString(3, UUID.randomUUID().toString());
                state.setString(4, statusMessage);
                state.setBoolean(5, currenstatus);
                state.executeUpdate();

            } catch (Exception ex) {
                log.log(Level.WARN, null, ex);
            } finally {
                DBUtils.safeClose(state);
            }

        } else if (currenstatus != oldstatus || !oldstatusmsg.equalsIgnoreCase(statusMessage)) {
            //something happened, let's record it

            //update current in config status table
            PreparedStatement state = null;
            try {
                state = con.prepareStatement("UPDATE status set utcdatetime=?, message=?, status=? where uri=?;");
                state.setLong(1, System.currentTimeMillis());
                state.setString(2, statusMessage);
                state.setBoolean(3, currenstatus);
                state.setString(4, name);
                state.executeUpdate();
            } catch (Exception ex) {
                log.log(Level.WARN, null, ex);
            } finally {
                DBUtils.safeClose(state);

            }
            try {

                //insert current into perf availity
                state = perf.prepareStatement("INSERT INTO availability(uri, utcdatetime, id, message, status)   VALUES (?, ?, ?, ?, ?);");
                state.setString(1, name);
                state.setLong(2, System.currentTimeMillis());
                state.setString(3, UUID.randomUUID().toString());
                state.setString(4, statusMessage);
                state.setBoolean(5, currenstatus);
                state.executeUpdate();
            } catch (Exception ex) {
                log.log(Level.WARN, null, ex);
            } finally {
                DBUtils.safeClose(state);

            }

            if (currenstatus != oldstatus) {

                SLACommon.TriggerStatusChange(name, oldstatus, oldstatusmsg, currenstatus, statusMessage, pooled);
            }
        } else {
            //no change in status, just update the now timestamp to keep the ui happy
            //update current in config status table
            PreparedStatement state = null;
            try {
                state = con.prepareStatement("UPDATE status   SET utcdatetime=? WHERE uri=?;");
                state.setString(2, name);
                state.setLong(1, System.currentTimeMillis());
                state.executeUpdate();
            } catch (Exception ex) {
                log.log(Level.WARN, null, ex);
            } finally {
                DBUtils.safeClose(state);

            }

        }

        DBUtils.safeClose(con);
        DBUtils.safeClose(perf);
    }

    /**
     * Sets the status of an item, if it has changed, SLA alerts are processed
     * data is recorded in two databases, one with the current status, and one
     * with all previous status messages name = the URI of the item s = current
     * status message wrapper, policies are auto created
     *
     * @param currenstatus
     * @param name
     * @param s
     * @param pooled
     * @param p
     * @param domain
     * @param server
     * @param parentcomponent
     */
    public static void TryUpdateStatus(boolean currenstatus, String name, String s, boolean pooled, PolicyType p, String domain, String server, String parentcomponent) {
        TryUpdateStatus(currenstatus, name, s, pooled, p, domain, server, parentcomponent, FLAGS.AUTO_CREATE);
    }

    /**
     * Sets the status of an item, if it has changed, SLA alerts are processed
     * data is recorded in two databases, one with the current status, and one
     * with all previous status messages name = the URI of the item s = current
     * status message
     *
     * @param currenstatus
     * @param name/uri of the thing we are setting the status on
     * @param statusMessage message
     * @param pooled, determines how the database connection will be loaded,
     * pooled = JNDI lookup
     * @param polity type
     * @param domain, if not known, use AuxHelper.UNSPECIFIED
     * @param server , if not known, use AuxHelper.UNSPECIFIED
     */
    public static void TryUpdateStatus(boolean currenstatus, String name, String statusMessage, boolean pooled, PolicyType policyType, String domain, String server) {
        TryUpdateStatus(currenstatus, name, statusMessage, pooled, policyType, domain, server, null, FLAGS.AUTO_CREATE);
    }//PolicyType.STATISTICAL

    /**
     * wrapper that sets the policy type to Status
     *
     * @param currenstatus
     * @param name unique name of the thing we are checking
     * @param s current status message
     */
    public static void TryUpdateStatus(boolean currenstatus, String name, String s, boolean pooled, String domain, String server, FLAGS flag) {
        TryUpdateStatus(currenstatus, name, s, pooled, PolicyType.STATUS, domain, server, null, flag);
    }

    /**
     * wrapper that sets the policy type to Status, auto create policy, parent
     * component is null
     *
     * @param currenstatus
     * @param name unique name of the thing we are checking
     * @param s current status message
     */
    public static void TryUpdateStatus(boolean currenstatus, String name, String s, boolean pooled, String domain, String server) {
        TryUpdateStatus(currenstatus, name, s, pooled, PolicyType.STATUS, domain, server, null, FLAGS.AUTO_CREATE);
    }

    /**
     * Attempts to creates a policy of the given type. Since the database has
     * unique keys, an error messages are just swallowed up connection must be
     * to the configuration database and be open, remains open after exiting
     *
     * @param uRI
     * @param con
     * @param pooled is not used by this function, but is passed along
     */
    @Deprecated
    public static void CheckPolicyAndCreate(String URL, Connection con, PolicyType p, boolean pooled, String domainname, String hostname) {
        CheckPolicyAndCreate(URL, con, p, pooled, domainname, hostname, null);
    }

    /**
     *
     * @param URL
     * @param con
     * @param p
     * @param pooled
     * @param domainname
     * @param hostname
     * @param parentcomponent
     */
    public static void CheckPolicyAndCreate(final String URL, Connection con, final PolicyType p, boolean pooled, String domainname, String hostname, String parentcomponent) {
        try {
            Connection perf = null;
            PreparedStatement c2 = null;
            try {

                if (pooled) {
                    perf = Utility.getPerformanceDBConnection();
                } else {
                    perf = Utility.getPerformanceDB_NONPOOLED_Connection();
                }
                c2 = perf.prepareStatement("INSERT INTO rawdatatally (URI)"
                        + "SELECT ? WHERE NOT EXISTS"
                        + "(SELECT uri FROM rawdatatally WHERE uri=?);");
                c2.setString(1, URL);
                c2.setString(2, URL);
                c2.execute();
                c2.close();
                perf.close();
            } catch (Exception ex) {
                log.log(Level.WARN, SLACommon.getBundleString("ErrorInsertingRawDataTallyRecord"), ex);
            } finally {
                DBUtils.safeClose(c2);
                DBUtils.safeClose(perf);
            }
            ServicePolicy policy = null;

            KeyNameValueEnc d = null;
            long retention = Long.valueOf("31536000000");
            switch (p) {
                case STATISTICAL:
                    policy = new StatisticalServicePolicy();
                    d = DBSettingsLoader.GetPropertiesFromDB(pooled, "Policy.Statistics", "defaultRetention");
                    break;
                case STATUS:
                    policy = new StatusServicePolicy();
                    d = DBSettingsLoader.GetPropertiesFromDB(pooled, "Policy.Status", "defaultRetention");
                    break;
                case TRANSACTIONAL:
                    policy = new TransactionalWebServicePolicy();
                    if (URL.toLowerCase().startsWith("http://") || URL.toLowerCase().startsWith("https://")) {
                        ((TransactionalWebServicePolicy) policy).setBuellerEnabled(true);
                    } else {
                        ((TransactionalWebServicePolicy) policy).setBuellerEnabled(false);
                    }
                    d = DBSettingsLoader.GetPropertiesFromDB(pooled, "Policy.Transactional", "defaultRetention");
                    break;
                case MACHINE:
                    policy = new MachinePolicy();
                    d = DBSettingsLoader.GetPropertiesFromDB(pooled, "Policy.Machine", "defaultRetention");
                    break;
                case PROCESS:
                    policy = new ProcessPolicy();
                    d = DBSettingsLoader.GetPropertiesFromDB(pooled, "Policy.Process", "defaultRetention");
                    break;
            }
            if (policy == null) {
                log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorCreatingServicePolicy"), URL) + "policy type is unhandled!");
                throw new NullPointerException();
            }
            if (d != null && d.getKeyNameValue() != null) {
                try {
                    retention = Long.parseLong(d.getKeyNameValue().getPropertyValue());
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(SLACommon.getBundleString("ErrorUnableToParse"), "defaultRetention" + p.value(), "Long"), ex);
                }
            }
            DatatypeFactory df = DatatypeFactory.newInstance();
            policy.setDataTTL(df.newDuration(retention));
            policy.setDomainName(domainname);
            policy.setMachineName(hostname);
            policy.setPolicyType(p);
            policy.setURL(URL);
            policy.setParentObject(parentcomponent);

            //   UUID id = UUID.randomUUID();
            JAXBContext jc = Utility.getSerializationContext();

            Marshaller m = jc.createMarshaller();
            Utility.addStatusChangeSLA(policy);
            policy.setAgentsEnabled(true);

            WritePolicytoDB(policy, URL, "system", pooled);

            SLACommon.AlertGlobalAdminsNewPolicyCreated("system", URL, pooled, p);
        } catch (PolicyExistsException e) {
            //in this case, the policy exists already, therefor we won't alert anyone that it was created.
            e = e; //this is ignorable
        } catch (Exception ex) {
            log.log(Level.DEBUG, String.format(SLACommon.getBundleString("ErrorCreatingServicePolicy"), URL), ex);
        }
    }
    public static final String UNSPECIFIED = "unspecified";

    /**
     * Writes a service policy to the database, uses a locking mechanism to
     * prevent duplicates First removes any existing policy then writes the
     * policy to the db
     *
     * @param policy
     * @param URL
     * @param currentuser
     * @param pooled
     * @throws Exception
     */
    private static void WritePolicytoDB(final ServicePolicy policy, final String URL, final String currentuser, boolean pooled) throws Exception {
        Connection con = null;
        PreparedStatement comm = null;
        try {
            if (policy == null) {
                throw new IllegalArgumentException("policy");
            }
            if (policy.getPolicyType() == null) {
                throw new IllegalArgumentException("policy");
            }
            if (Utility.stringIsNullOrEmpty(URL)) {
                throw new IllegalArgumentException("URL");
            }

            if (pooled) {
                con = Utility.getConfigurationDBConnection();
            } else {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();
            }

            PreparedStatement c2 = null;
            Connection p = null;
            try {

                if (pooled) {
                    p = Utility.getPerformanceDBConnection();
                } else {
                    p = Utility.getPerformanceDB_NONPOOLED_Connection();
                }
                c2 = p.prepareStatement("INSERT INTO rawdatatally (URI)"
                        + "SELECT ? WHERE NOT EXISTS"
                        + "(SELECT uri FROM rawdatatally WHERE uri=?);");
                c2.setString(1, URL);
                c2.setString(2, URL);
                c2.execute();

                p.close();
            } catch (Exception ex) {
                log.log(Level.WARN, SLACommon.getBundleString("ErrorInsertingRawDataTallyRecord"), ex);
            } finally {
                DBUtils.safeClose(c2);
                DBUtils.safeClose(p);
            }

            comm = con.prepareStatement("BEGIN WORK;LOCK TABLE servicepolicies IN SHARE ROW EXCLUSIVE MODE;"
                    + "INSERT INTO servicepolicies (URI, DataTTL, " //2
                    + " latitude, longitude, displayname, policytype, buellerenabled, hostname, domaincol, xmlpolicy, hassla, hasfederation, bucket, lastchanged, lastchangedby, "
                    + "healthstatusenabled, parenturi )" //13
                    + " SELECT ?,?,?,?,?,  ?,?,?,?,?, ?,?,?, ?,?,?,? WHERE NOT EXISTS"
                    + " (SELECT uri FROM servicepolicies WHERE uri=?);" //1
                    + ""
                    + "COMMIT WORK;");

            comm.setString(1, URL);
            if (policy.getDataTTL() == null) {
                comm.setLong(2, 30 * 24 * 60 * 60);
            } else {
                comm.setLong(2, Utility.durationToTimeInMS(policy.getDataTTL()));
            }
            if (policy.getLocation() != null) {
                comm.setDouble(3, policy.getLocation().getLatitude());
                comm.setDouble(4, policy.getLocation().getLongitude());
            } else {
                comm.setNull(3, Types.DOUBLE);
                comm.setNull(4, Types.DOUBLE);
            }
            if (Utility.stringIsNullOrEmpty(policy.getDisplayName())) {
                comm.setNull(5, java.sql.Types.VARCHAR);
            } else {
                comm.setString(5, policy.getDisplayName());
            }

            comm.setInt(6, policy.getPolicyType().ordinal());
            if (policy instanceof TransactionalWebServicePolicy) {
                TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) policy;
                comm.setBoolean(7, tp.isBuellerEnabled());
            } else {
                comm.setBoolean(7, false);
            }

            if (Utility.stringIsNullOrEmpty(policy.getMachineName())) {
                comm.setString(8, UNSPECIFIED);
            } else {
                comm.setString(8, policy.getMachineName());
            }

            if (Utility.stringIsNullOrEmpty(policy.getDomainName())) {
                comm.setString(9, UNSPECIFIED);
            } else {
                comm.setString(9, policy.getDomainName());
            }
            //xml
            Marshaller m = Utility.getSerializationContext().createMarshaller();
            StringWriter sw = new StringWriter();
            m.marshal(policy, sw);

            String s = sw.toString();
            comm.setBytes(10, s.getBytes(Constants.CHARSET));

            if (policy.getServiceLevelAggrements() != null
                    && policy.getServiceLevelAggrements().getSLA() != null && !policy.getServiceLevelAggrements().getSLA().isEmpty()) {
                comm.setBoolean(11, true);
            } else {
                comm.setBoolean(11, false);
            }
            if (policy.getFederationPolicyCollection() != null && !policy.getFederationPolicyCollection().getFederationPolicy().isEmpty()) {
                comm.setBoolean(12, true);
            } else {
                comm.setBoolean(12, false);
            }

            if (Utility.stringIsNullOrEmpty(policy.getBucketCategory())) {
                comm.setString(13, UNSPECIFIED);
            } else {
                comm.setString(13, policy.getBucketCategory());
            }
            comm.setLong(14, System.currentTimeMillis());
            comm.setString(15, currentuser);
            if (policy instanceof TransactionalWebServicePolicy) {
                TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) policy;
                comm.setBoolean(16, tp.isHealthStatusEnabled());
            } else {
                comm.setBoolean(16, false);
            }
            if (Utility.stringIsNullOrEmpty(policy.getParentObject())) {
                comm.setNull(17, java.sql.Types.VARCHAR);
            } else {
                comm.setString(17, policy.getParentObject());
            }
            comm.setString(18, URL);
            int rows = comm.executeUpdate();
            comm.close();
            con.close();
            if (rows == 0) {
                throw new PolicyExistsException();
            }
            //policy inserted successfully
            return;
        } catch (SQLException ex) {
            log.log(Level.ERROR, SLACommon.getBundleString("ErrorExceptionPolicyCreation") + URL, ex);
        } catch (NullPointerException ex) {
            log.log(Level.ERROR, SLACommon.getBundleString("ErrorExceptionPolicyCreation") + URL, ex);
        }
        try {
            if (con != null && !con.isClosed()) {
                PreparedStatement com = con.prepareStatement("ROLLBACK WORK;");
                com.execute();
                con.close();
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, SLACommon.getBundleString("ErrorClosingDB"), ex);
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (Exception ex) {
                log.log(Level.ERROR, SLACommon.getBundleString("ErrorClosingDB"), ex);
            }
        }
        throw new PolicyExistsException();
    }

    /**
     * wrapper for PolicyType = Status connection must be to the configuration
     * database and be open, remains open after exiting
     *
     * @param uRI
     * @param con
     * @param pooled is not used by this function, but is passed along
     */
    @Deprecated
    public static void CheckPolicyAndCreate(String uRI, Connection con, boolean pooled, String domain, String server) {
        CheckPolicyAndCreate(uRI, con, PolicyType.STATUS, pooled, domain, server, null);
    }

    /**
     *
     * connection must be to the configuration database and be open, remains
     * open after exiting
     *
     * @param uRI
     * @param con
     * @param pooled is not used by this function, but is passed along
     */
    public static void CheckStatisticalPolicyAndCreate(String uRI, Connection con, boolean pooled, String domain, String server) {
        CheckPolicyAndCreate(uRI, con, PolicyType.STATISTICAL, pooled, domain, server, null);
    }
}
