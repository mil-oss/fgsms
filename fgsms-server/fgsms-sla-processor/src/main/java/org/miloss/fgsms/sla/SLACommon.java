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

import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Logger;
;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.AlertType;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.DriveInformation;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.ProcessPerformanceData;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddMachineAndProcessDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalDataRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * Provides a number of key functions that are related to SLA management,
 * alerting, and status setting This does most of the SLA work
 *
 * @author AO
 */


public class SLACommon {

    /**
     * Thie following items are for SLA Logger actions
     */
    private static final Logger log = Logger.getLogger("fgsms.SLAProcessor");
    private static ResourceBundle bundle = null;

    /**
     * loads a resource from the properties file
     *
     * @param key
     * @return
     */
    public static String getBundleString(String key) {
        SetupBundle();
        return bundle.getString(key);
    }

    private static synchronized void SetupBundle() {
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle("org.miloss.fgsms.sla/SLAResources", Locale.getDefault());
            } catch (Exception ex) {
                log.log(Level.FATAL, "unable to load the resource bundle for " + "org.miloss.fgsms.sla/SLAResources" + Locale.getDefault().toString(), ex);
            }
        }
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle("org.miloss.fgsms.sla/SLAResources");
            } catch (Exception ex) {
                log.log(Level.FATAL, "unable to load the resource bundle for " + "org.miloss.fgsms.sla/SLAResources", ex);
            }
        }
        if (bundle == null) {
            bundle = new ResourceBundle() {
                @Override
                protected Object handleGetObject(String key) {
                    return "Bundle not available!";
                }

                @Override
                public Enumeration<String> getKeys() {
                    return null;
                }
            };
        }
    }

    /**
     * Returns the current machine's hostname using
     * InetAddress.getLocalHost().getHostName().toLowerCase(); The hostname will
     * always be lower case. This function should never throw an exception. If
     * the current machine does not have a valid hostname or it is not dns
     * resolvable, then "ADDRESS_UNKNOWN" is returned
     *
     * @return
     */
    public static String GetHostName() {
        return Utility.getHostName();
    }

    /**
     * used by NT SLA processor for web services loads service policies with SLA
     * policies defined Connection must be to the config database, remains open
     * after executing
     *
     * @param con an open database connection to the config database, remains
     * open after returning
     * @return
     */
    private static List<ServicePolicy> loadServicePolicies(Connection con) {
        SetupBundle();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {
            List<ServicePolicy> l = new ArrayList<ServicePolicy>();

            comm = con.prepareStatement("Select * from ServicePolicies where hassla=true;");

            //ret.PublishToUDDITimeRange = null;
            /////////////////////////////////////////////
            //get the policy for this service
            /////////////////////////////////////////////
            Unmarshaller um = Utility.getSerializationContext().createUnmarshaller();
            results = comm.executeQuery();

            while (results.next()) {
                ServicePolicy ret = null;

                PolicyType pt = PolicyType.values()[results.getInt("policytype")];

                byte[] s = results.getBytes("xmlpolicy");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                switch (pt) {
                    case MACHINE:
                        JAXBElement<MachinePolicy> foo = (JAXBElement<MachinePolicy>) um.unmarshal(r, MachinePolicy.class);
                        if (foo == null || foo.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("MachinePolicyNull"));
                        } else {
                            ret = foo.getValue();
                        }
                        break;
                    case PROCESS:
                        JAXBElement<ProcessPolicy> foo3 = (JAXBElement<ProcessPolicy>) um.unmarshal(r, ProcessPolicy.class);
                        if (foo3 == null || foo3.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("ProcessPolicyNull"));
                        } else {
                            ret = foo3.getValue();
                        }
                        break;
                    case STATISTICAL:
                        JAXBElement<StatisticalServicePolicy> foo1 = (JAXBElement<StatisticalServicePolicy>) um.unmarshal(r, StatisticalServicePolicy.class);
                        if (foo1 == null || foo1.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("BrokerPolicyNull"));
                        } else {
                            ret = foo1.getValue();
                        }
                        break;
                    case STATUS:
                        JAXBElement<StatusServicePolicy> foo2 = (JAXBElement<StatusServicePolicy>) um.unmarshal(r, StatusServicePolicy.class);
                        if (foo2 == null || foo2.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("StatusPolicyNull"));
                        } else {
                            ret = foo2.getValue();
                        }
                        break;
                    case TRANSACTIONAL:
                        JAXBElement<TransactionalWebServicePolicy> foo4 = (JAXBElement<TransactionalWebServicePolicy>) um.unmarshal(r, TransactionalWebServicePolicy.class);
                        if (foo4 == null || foo4.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("WSPolicyNull"));
                        } else {
                            ret = foo4.getValue();
                        }
                        break;
                }

                r.close();
                bss.close();

                l.add(ret);

            }
            return l;
        } catch (Exception ex) {
            log.log(Level.WARN, bundle.getString("ErrorLoadingPolicyForSLA"), ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
        }
        return new ArrayList<ServicePolicy>();
    }

    /**
     * used by NT SLA for web services
     *
     * @return either an empty list, or a list containing service policies that
     * contain SLA's
     */
    public static List<ServicePolicy> LoadServicePoliciesPooled() {
        SetupBundle();
        Connection con = Utility.getConfigurationDBConnection();
        List<ServicePolicy> r = null;
        try {
            r = loadServicePolicies(con);
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorLoadingPolicy"), ex);
        } finally {
            DBUtils.safeClose(con);
        }
        if (r == null) {
            r = new ArrayList<ServicePolicy>();
        }
        return r;
    }

    /**
     * used by NT SLA for web services
     *
     * @return
     */
    public static List<ServicePolicy> LoadServicePoliciesNotPooled() {
        Connection con = Utility.getConfigurationDB_NONPOOLED_Connection();
        SetupBundle();
        List<ServicePolicy> r = null;
        try {
            r = loadServicePolicies(con);
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorLoadingPolicy"), ex);
        } finally {
            DBUtils.safeClose(con);
        }
        if (r == null) {
            r = new ArrayList<ServicePolicy>();
        }
        return r;
    }

    /**
     * Loads a specific policy, connection stays open after running
     *
     * @param con
     * @param uRI
     * @return
     */
    private static ServicePolicy loadPolicy(final Connection con, final String uRI) {
        if (Utility.stringIsNullOrEmpty(uRI)) {
            throw new IllegalArgumentException("requestedURI");
        }
        SetupBundle();
        PreparedStatement comm = null;
        ResultSet results = null;
        try {

            ServicePolicy ret = null;

            comm = con.prepareStatement("Select * from ServicePolicies where URI=?;");
            comm.setString(1, uRI);

            results = comm.executeQuery();

            if (results.next()) {
                PolicyType pt = PolicyType.values()[results.getInt("policytype")];
                Unmarshaller um = Utility.getSerializationContext().createUnmarshaller();
                byte[] s = results.getBytes("xmlpolicy");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                //1 = reader
                //2 = writer
                //                XMLStreamReaderImpl r = new XMLStreamReaderImpl(bss, new PropertyManager(1));
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                switch (pt) {
                    case MACHINE:
                        JAXBElement<MachinePolicy> foo = (JAXBElement<MachinePolicy>) um.unmarshal(r, MachinePolicy.class);
                        if (foo == null || foo.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo.getValue();
                        }
                        break;
                    case PROCESS:
                        JAXBElement<ProcessPolicy> foo3 = (JAXBElement<ProcessPolicy>) um.unmarshal(r, ProcessPolicy.class);
                        if (foo3 == null || foo3.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo3.getValue();
                        }
                        break;
                    case STATISTICAL:
                        JAXBElement<StatisticalServicePolicy> foo1 = (JAXBElement<StatisticalServicePolicy>) um.unmarshal(r, StatisticalServicePolicy.class);
                        if (foo1 == null || foo1.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo1.getValue();
                        }
                        break;
                    case STATUS:
                        JAXBElement<StatusServicePolicy> foo2 = (JAXBElement<StatusServicePolicy>) um.unmarshal(r, StatusServicePolicy.class);
                        if (foo2 == null || foo2.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo2.getValue();
                        }
                        break;
                    case TRANSACTIONAL:
                        JAXBElement<TransactionalWebServicePolicy> foo4 = (JAXBElement<TransactionalWebServicePolicy>) um.unmarshal(r, TransactionalWebServicePolicy.class);
                        if (foo4 == null || foo4.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo4.getValue();
                        }
                        break;
                }
                r.close();
                bss.close();

                return ret;
            }

        }catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException"), ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
        }
        return null;

    }

    /**
     * Loads a policy using a caching mechanism, used by DCS and DAS if the
     * policy does not exist, it will NOT be created
     *
     * @param uRI
     * @return
     */
    public static ServicePolicy LoadPolicyPooled(String uRI) {
        SetupBundle();
        Connection con = Utility.getConfigurationDBConnection();
        ServicePolicy sp = loadPolicy(con, uRI);
        try {
            con.close();
        } catch (SQLException ex) {
            log.log(Level.WARN, bundle.getString("ErrorClosingDB"), ex);
        }
        return sp;
    }

    /**
     * used by qpid agents
     *
     * @param uRI
     * @return
     */
    @Deprecated
    public static ServicePolicy LoadPolicyNotPooled(String uRI) {
        SetupBundle();
        Connection con = Utility.getConfigurationDB_NONPOOLED_Connection();
        ServicePolicy sp = loadPolicy(con, uRI);
        DBUtils.safeClose(con);
        return sp;
    }

    public static String GenerateSubscriptionLink(String property) {
        SetupBundle();
        return String.format(bundle.getString("AlertingSettings"), property);
        //return "You are currently subscribed to receive email alerts for this service. <a href=\"" + property + "/alertingSettings.jsp\">Click here</a> to manage your subscriptions.";
    }

    /**
     *
     * @param con stays open after executing
     * @param guid
     * @return may be null
     */
    private static InternetAddress[] getSubscribers(Connection con, String guid) {
        SetupBundle();
        PreparedStatement prepareStatement=null;
        ResultSet rs= null;
        try {
            List<InternetAddress> emails = new ArrayList<InternetAddress>();

            prepareStatement = con.prepareStatement("select users.username,email,email1,email2,email3 from slasub, users where slaid=? and slasub.username=users.username;");
            prepareStatement.setString(1, guid);
            rs = prepareStatement.executeQuery();
            //String emails = "";
            while (rs.next()) {
                InternetAddress a;
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email"))) {
                        a = new InternetAddress(rs.getString("email").trim());
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(bundle.getString("ErrorCreateInternetAddress"), rs.getString("users.username")) + rs.getString("email"), ex);
                    //"Error creating InternetAddress object which is used to send email to %s, perhaps its an invalid email address " + rs.getString("users.username"), ex);
                }
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email1"))) {
                        a = new InternetAddress(rs.getString("email1").trim());
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(bundle.getString("ErrorCreateInternetAddress"), rs.getString("users.username")) + rs.getString("email1"), ex);
                }
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email2"))) {
                        a = new InternetAddress(rs.getString("email2").trim());
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(bundle.getString("ErrorCreateInternetAddress"), rs.getString("users.username")) + rs.getString("email2"), ex);
                }
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email3"))) {
                        a = new InternetAddress(rs.getString("email3").trim());
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(bundle.getString("ErrorCreateInternetAddress"), rs.getString("users.username")) + rs.getString("email3"), ex);
                }

            }
            
            Set<InternetAddress> nodup = new HashSet<InternetAddress>(emails);
            InternetAddress[] d = new InternetAddress[nodup.size()];
            return emails.toArray(d);
        } catch (SQLException ex) {
            log.log(Level.ERROR, bundle.getString("ErrorSQLException"), ex);
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException"), ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(prepareStatement);
        }
        return null;
    }

    /**
     * Returns a list of user's email addresses that are subscribed to a
     * specific SLA with email actions
     *
     * @param guid
     * @return
     */
    @Deprecated
    public static InternetAddress[] GetSubscribersNotPooled(String guid) {
        SetupBundle();
        Connection con = Utility.getConfigurationDB_NONPOOLED_Connection();
        InternetAddress[] sp = getSubscribers(con, guid);
         DBUtils.safeClose(con);
        return sp;
    }

    /**
     * Returns a list of user's email addresses that are subscribed to a
     * specific SLA with email actions
     *
     * @param guid
     * @return
     */
    public static InternetAddress[] GetSubscribersPooled(String guid) {
        SetupBundle();
        Connection con = Utility.getConfigurationDBConnection();
        InternetAddress[] sp = getSubscribers(con, guid);
          DBUtils.safeClose(con);
        return sp;

    }

    /**
     * gets a service policy from the database, will not create one if one does not exist.
     * @param requestedURI
     * @param pooled
     * @return 
     */
    private static org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy getPolicy(final String requestedURI, final boolean pooled) {
        Connection con = null;
        SetupBundle();
         PreparedStatement comm = null;
         ResultSet results =null;
        try {
            if (Utility.stringIsNullOrEmpty(requestedURI)) {
                throw new IllegalArgumentException("requestedURI");
            }
            if (pooled) {
                con = Utility.getConfigurationDBConnection();
            } else {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();
            }

            ServicePolicy ret = null;
           

            comm = con.prepareStatement("Select * from ServicePolicies where URI=?;");
            comm.setString(1, requestedURI);

            results = comm.executeQuery();

            if (results.next()) {
                PolicyType pt = PolicyType.values()[results.getInt("policytype")];
                Unmarshaller um = Utility.getSerializationContext().createUnmarshaller();
                byte[] s = results.getBytes("xmlpolicy");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                //1 = reader
                //2 = writer
                //                XMLStreamReaderImpl r = new XMLStreamReaderImpl(bss, new PropertyManager(1));
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                switch (pt) {
                    case MACHINE:
                        JAXBElement<MachinePolicy> foo = (JAXBElement<MachinePolicy>) um.unmarshal(r, MachinePolicy.class);
                        if (foo == null || foo.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("MachinePolicyNull"));
                        } else {
                            ret = foo.getValue();
                        }
                        break;
                    case PROCESS:
                        JAXBElement<ProcessPolicy> foo3 = (JAXBElement<ProcessPolicy>) um.unmarshal(r, ProcessPolicy.class);
                        if (foo3 == null || foo3.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("ProcessPolicyNull"));
                        } else {
                            ret = foo3.getValue();
                        }
                        break;
                    case STATISTICAL:
                        JAXBElement<StatisticalServicePolicy> foo1 = (JAXBElement<StatisticalServicePolicy>) um.unmarshal(r, StatisticalServicePolicy.class);
                        if (foo1 == null || foo1.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("BrokerPolicyNull"));
                        } else {
                            ret = foo1.getValue();
                        }
                        break;
                    case STATUS:
                        JAXBElement<StatusServicePolicy> foo2 = (JAXBElement<StatusServicePolicy>) um.unmarshal(r, StatusServicePolicy.class);
                        if (foo2 == null || foo2.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("StatusPolicyNull"));
                        } else {
                            ret = foo2.getValue();
                        }
                        break;
                    case TRANSACTIONAL:
                        JAXBElement<TransactionalWebServicePolicy> foo4 = (JAXBElement<TransactionalWebServicePolicy>) um.unmarshal(r, TransactionalWebServicePolicy.class);
                        if (foo4 == null || foo4.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("WSPolicyNull"));
                        } else {
                            ret = foo4.getValue();
                        }
                        break;
                }
                r.close();
                bss.close();
                return ret;
            }
        
        } catch (JAXBException ex) {
            log.log(Level.ERROR, bundle.getString("ErrorMarshallingPolicy"), ex);
        } catch (SQLException ex) {
            log.log(Level.ERROR, bundle.getString("ErrorSQLException"), ex);
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException"), ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        
        return null;

    }

    private static boolean containsChangeInStatus(RuleBaseType b) {
        if (b == null) {
            return false;
        }
        if (b instanceof SLARuleGeneric) {
            {
                SLARuleGeneric asd = (SLARuleGeneric) b;
                if (asd.getClassName().equalsIgnoreCase(ChangeInAvailabilityStatus.class.getCanonicalName())) {
                    return true;
                }
            }

        }
        if (b instanceof AndOrNot) {
            AndOrNot x = (AndOrNot) b;
            if (x.getFlag() == JoiningType.AND) {
                return containsChangeInStatus(x.getLHS()) && containsChangeInStatus(x.getRHS());
            }
            if (x.getFlag() == JoiningType.OR) {
                return containsChangeInStatus(x.getLHS()) || containsChangeInStatus(x.getRHS());
            }
        }
        return false;
    }

    /**
     * Call this function when a monitored item changes status. This function
     * will handle all actions related to the change, such as sending emails
     *
     * @param uri
     * @param oldstatus
     * @param oldmsg
     * @param currenstatus
     * @param s
     * @param pooled
     */
    public static void TriggerStatusChange(String uri, Boolean oldstatus, String oldmsg, Boolean currenstatus, String s, boolean pooled) {
        SetupBundle();
        ServicePolicy pol = SLACommon.getPolicy(uri, pooled);
        if (pol == null || pol.getServiceLevelAggrements() == null
                || pol.getServiceLevelAggrements().getSLA().isEmpty()) {
            return;
        }

        for (int i = 0; i < pol.getServiceLevelAggrements().getSLA().size(); i++) {
            if (containsChangeInStatus(pol.getServiceLevelAggrements().getSLA().get(i).getRule())) {
                String msgplain = String.format(bundle.getString("StatusChangeEmailAlertPlain"), uri, s);
                String msghtml = String.format(bundle.getString("StatusChangeEmailAlertHtml"), Utility.encodeHTML(uri), oldmsg, s, (currenstatus ? "OK" : "NG"));
                //"Change In Status Alert for " + pol.getURL() + " current status is " + s, "<br>The service identified by " + Utility.encodeHTML(uri) + " current availablity status has changed. It was previously " + oldmsg + " and is now " + s + "<br>"
                //                + "<h2>Current Status: " + (currenstatus ? "OK" : "NG") + "</h2>"
                ProcessAlerts(msgplain, msghtml, uri, null,
                        System.currentTimeMillis(), null, pooled,
                        false, pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction(),
                        pol.getServiceLevelAggrements().getSLA().get(i).getGuid(), pol, AlertType.Status);
            }

        }
    }

    public static void RecordSLAFault(AtomicReference<String> faultMsg, final String uRL, final String relatedMsgID, long timeInMillis, String incidentid, final boolean pooled) {
        SetupBundle();
        Connection con = null;
         PreparedStatement com=null;
        try {
            if (pooled) {
                con = Utility.getPerformanceDBConnection();
            } else {
                con = Utility.getPerformanceDB_NONPOOLED_Connection();
            }
           
            if (Utility.stringIsNullOrEmpty(relatedMsgID)) {
                /*
                 * Non transactional
                 */
                com = con.prepareStatement("INSERT INTO slaviolations (utcdatetime, msg, uri, incidentid)"
                        + " VALUES (?, ?, ?, ?); ");
                com.setLong(1, timeInMillis);
                com.setBytes(2, faultMsg.get().getBytes(Constants.CHARSET));
                com.setString(3, uRL);
                com.setString(4, incidentid);
            } else {
                com = con.prepareStatement("INSERT INTO slaviolations (utcdatetime, msg, uri, relatedtransaction, incidentid)"
                        + "VALUES (?, ?, ?, ?, ?); "
                        + "UPDATE rawdata SET  slafault=? WHERE transactionid=?;");
                com.setLong(1, timeInMillis);
                if (faultMsg == null || faultMsg.get() == null) {
                    com.setBytes(2, "No fault message provided.".getBytes(Constants.CHARSET));
                } else {
                    com.setBytes(2, faultMsg.get().getBytes(Constants.CHARSET));
                }
                com.setString(3, uRL);
                com.setString(4, relatedMsgID);
                com.setString(5, incidentid);
                //com.setLong(6, timeInMillis);
                com.setString(6, incidentid);
                com.setString(7, relatedMsgID);
            }

            com.execute();
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorSavingSLAViolation"), ex);
        } finally {
            DBUtils.safeClose(com);
            DBUtils.safeClose(con);
        }

    }

    /**
     * returns all of the mail settings, use from pooled connections with jndi
     *
     * @return
     */
    public static Properties LoadSLAPropertiesPooled() {
        Connection con = Utility.getConfigurationDBConnection();
        Properties p = loadSLAProperties(con);
        DBUtils.safeClose(con);
        return p;
    }

    /**
     * returns all of the mail settings, use from nonpooled connections,
     * connections loaded from properties file in common
     *
     * @return
     */
    @Deprecated
    public static Properties LoadSLAPropertiesNotPooled() {
        Connection con = Utility.getConfigurationDB_NONPOOLED_Connection();
        Properties p = loadSLAProperties(con);
        DBUtils.safeClose(con);
        return p;
    }

    /**
     * This function loads all of the Email properties from the database
     *
     * @param config
     * @return
     */
    private static Properties loadSLAProperties(Connection config) {
        SetupBundle();
        Properties p = new Properties();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            if (config == null) {
                return p;
            }
            com = config.prepareStatement("Select * from mail;");
            rs = com.executeQuery();
            while (rs.next()) {
                p.put(rs.getString("property"), rs.getString("valuecol"));
            }
        } catch (SQLException ex) {
            log.log(Level.WARN, bundle.getString("ErrorLoadingSendMailSettings"), ex);
        } catch (Exception ex) {
            log.log(Level.WARN, bundle.getString("ErrorUncaughtException"), ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
        }
        return p;
    }

    /**
     * sends and informative alert to everyone in the database that has the
     * admin role (site admins)
     *
     * @param agentUsername
     * @param url
     * @param pooled
     * @param p
     */
    public static void AlertGlobalAdminsNewPolicyCreated(String agentUsername, String url, boolean pooled, PolicyType p) {
        SetupBundle();
        try {
            //get all subscribers

            Properties props = null;
            if (pooled) {
                props = LoadSLAPropertiesPooled();
            } else {
                props = LoadSLAPropertiesNotPooled();
            }

            Session mailSession = Session.getDefaultInstance(props);
            Message simpleMessage = new MimeMessage(mailSession);
            InternetAddress from;
            InternetAddress[] to = null;
            from = new InternetAddress(props.getProperty("defaultReplyAddress"));
            if (pooled) {
                to = GetAllfgsmsAdminsEmailAddressPooled();
            } else {
                to = GetAllfgsmsAdminsEmailAddressNotPooled();
            }
            simpleMessage.setFrom(from);
            log.log(Level.INFO, "Sending New Policy Alert to Administrators via Email to " + to.length + " addresses");
            for (int i = 0; i < to.length; i++) {

                try {
                    simpleMessage.setRecipient(Message.RecipientType.TO, to[i]);
                    simpleMessage.setSubject(bundle.getString("AlertGlobalAdminsNewPolicyCreatedSubject"));
//"A new policy has been created by:<br><br>" + Utility.encodeHTML(agentUsername) + "<br><br>URL: " + Utility.encodeHTML(url)
                    //                        + "<br><br>The Policy Type for this service is  " + p.value() + "<br><br>This email is just an informative alert that a new service has been detected and that users may wish to gain access to it.<Br>"
                    //                    + "<a href=\"" + props.getProperty("fgsms.GUI.URL") + "\">fgsms</a>" + "<br>" + GenerateManageLink(url, props.getProperty("fgsms.GUI.URL")) + "<br>"
                    simpleMessage.setContent(
                            String.format(bundle.getString("AlertGlobalAdminsNewPolicyCreated"),
                                    Utility.encodeHTML(agentUsername),
                                    Utility.encodeHTML(url),
                                    p.value(),
                                    props.getProperty("fgsms.GUI.URL")) + GenerateManageLink(url, props.getProperty("fgsms.GUI.URL")),
                            bundle.getString("EmailEncodingType"));
                    Transport.send(simpleMessage);
                } catch (Exception ex) {
                    log.log(Level.ERROR, bundle.getString("ErrorSendingEmail") + to[i].getAddress() + ex);
                }
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException") + ex.toString());
        }
    }

    public static String GenerateManageLink(String uri, String baseurl) {
        SetupBundle();
        return String.format(bundle.getString("ManageLink"), baseurl, URLEncoder.encode(uri));
        //"<a href=\"" + baseurl + "/manage.jsp?url=" + URLEncoder.encode(uri) + "\">Manage this policy</a>";
    }

    public static void AlertUserSLASubscribed(String subscribersUsername, List<String> subscribersEmail, SetAlertRegistrationsRequestMsg request, boolean pooled) {
        SetupBundle();
        try {
            //get all subscribers

            Properties props = null;
            if (pooled) {
                props = LoadSLAPropertiesPooled();
            } else {
                props = LoadSLAPropertiesNotPooled();
            }

            Session mailSession = Session.getDefaultInstance(props);
            Message simpleMessage = new MimeMessage(mailSession);
            InternetAddress from;

            from = new InternetAddress(props.getProperty("defaultReplyAddress"));
            for (int k = 0; k < subscribersEmail.size(); k++) {
                try {
                    InternetAddress to = new InternetAddress(subscribersEmail.get(k).trim());
                    simpleMessage.setFrom(from);
                    simpleMessage.setRecipient(Message.RecipientType.TO, to);
                    simpleMessage.setSubject(bundle.getString("AlertSubscriptionSubject"));
                    StringBuilder sb = new StringBuilder();
                    sb = sb.append(subscribersUsername).append(bundle.getString("AlertSubscriptionBody1"));
                    for (int i = 0; i < request.getItems().size(); i++) {
                        sb = sb.append("<li>").append(Utility.encodeHTML(request.getItems().get(i).getServiceUri())).append(" - ").append(Utility.encodeHTML(request.getItems().get(i).getSLAID())).append("</li>");
                    }
                    sb = sb.append(bundle.getString("AlertSubscriptionBody2")).append(SLACommon.GenerateSubscriptionLink(props.getProperty("fgsms.GUI.URL")));
                    simpleMessage.setContent(sb.toString(), bundle.getString("EmailEncodingType"));
                    Transport.send(simpleMessage);
                } catch (Exception e) {
                    //log.log(Level.ERROR, "AlertUserSLASubscribed Error sending SLA alert email! " + subscribersEmail.get(k) + " " + e.getMessage());
                    log.log(Level.ERROR, bundle.getString("ErrorSendingEmail") + subscribersEmail.get(k) + " " + e.getMessage());
                    log.log(Level.DEBUG, bundle.getString("ErrorSendingEmail") + subscribersEmail.get(k), e);
                }
            }
        } catch (Exception ex) {
            //log.log(Level.ERROR, "AlertUserSLASubscribed Error sending SLA alert email! " + ex.getMessage());
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException"), ex);
        }
    }

    /**
     * sends an email to email subscribed users for alerts, letting them know
     * that the SLA was deleted, along with their subscription
     *
     * @param currentUser
     * @param subscribersUsername
     * @param subscribersEmail
     * @param uRL
     * @param id
     * @param pooled
     */
    public static void AlertUserSLADeleted(String currentUser, String subscribersUsername, List<String> subscribersEmail, String uRL, String id, boolean pooled) {
        SetupBundle();
        try {
            //get all subscribers

            Properties props = null;
            if (pooled) {
                props = LoadSLAPropertiesPooled();
            } else {
                props = LoadSLAPropertiesNotPooled();
            }

            Session mailSession = Session.getDefaultInstance(props);
            Message simpleMessage = new MimeMessage(mailSession);
            InternetAddress from;

            from = new InternetAddress(props.getProperty("defaultReplyAddress"));
            for (int k = 0; k < subscribersEmail.size(); k++) {
                try {
                    InternetAddress to = new InternetAddress(subscribersEmail.get(k).trim());
                    simpleMessage.setFrom(from);
                    simpleMessage.setRecipient(Message.RecipientType.TO, to);
                    simpleMessage.setSubject(bundle.getString("AlertSubscriptionDeletedSubject"));

                    simpleMessage.setContent(
                            String.format(bundle.getString("AlertSubscriptionDeletedBody"), subscribersUsername, Utility.encodeHTML(uRL),
                                    Utility.encodeHTML(id), Utility.encodeHTML(currentUser), props.getProperty("fgsms.GUI.URL"))
                            //subscribersUsername + "<br><br>The SLA subscription for the service located at<br><br>" + uRL
                            //+ "<br><br>with the id " + id + " has been removed because either the SLA parameters were removed or the service is no longer monitored and was removed. This action was performed by  " + currentUser + ".<br>"
                            //+ "<a href=\"" + props.getProperty("fgsms.GUI.URL") + "\">fgsms</a>" + "<br><br>"
                            + SLACommon.GenerateSubscriptionLink(props.getProperty("fgsms.GUI.URL")), bundle.getString("EmailEncodingType"));
                    Transport.send(simpleMessage);
                } catch (Exception e) {
                    log.log(Level.ERROR, bundle.getString("ErrorSendingEmail") + subscribersEmail.get(k), e);
                    //log.log(Level.ERROR, "AlertUserSLADeleted Error sending SLA alert email! " + subscribersEmail.get(k) + " " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            //log.log(Level.ERROR, "AlertUserSLADeleted Error sending SLA alert email! " + ex.getMessage());
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException"), ex);
        }
    }

    @Deprecated
    public static InternetAddress[] GetAllfgsmsAdminsEmailAddressNotPooled() {
        SetupBundle();
        Connection con = Utility.getConfigurationDB_NONPOOLED_Connection();
        InternetAddress[] sp = getAllfgsmsAdminsEmailAddress(con);
        DBUtils.safeClose(con);
        return sp;
    }

    public static InternetAddress[] GetAllfgsmsAdminsEmailAddressPooled() {
        SetupBundle();
        Connection con = Utility.getConfigurationDBConnection();
        InternetAddress[] sp = getAllfgsmsAdminsEmailAddress(con);
       DBUtils.safeClose(con);
        return sp;

    }

    private static InternetAddress[] getAllfgsmsAdminsEmailAddress(Connection con) {
        SetupBundle();
        PreparedStatement prepareStatement=null;
        ResultSet rs = null;
        try {
            List<InternetAddress> emails = new ArrayList<InternetAddress>();

            prepareStatement = con.prepareStatement("select username,email,email1,email2,email3 from users where rolecol='admin';");
            rs = prepareStatement.executeQuery();
            //String emails = "";
            while (rs.next()) {
                InternetAddress a;
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email"))) {
                        a = new InternetAddress(rs.getString("email").trim());
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(bundle.getString("ErrorCreateInternetAddress"), rs.getString("username")) + rs.getString("email"), ex);
                }
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email1"))) {
                        a = new InternetAddress(rs.getString("email1").trim());
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(bundle.getString("ErrorCreateInternetAddress"), rs.getString("username")) + rs.getString("email1"), ex);
                }
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email2"))) {
                        a = new InternetAddress(rs.getString("email2").trim());
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(bundle.getString("ErrorCreateInternetAddress"), rs.getString("username")) + rs.getString("email2"), ex);
                }
                try {
                    if (!Utility.stringIsNullOrEmpty(rs.getString("email3"))) {
                        a = new InternetAddress(rs.getString("email3").trim());
                        emails.add(a);
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, String.format(bundle.getString("ErrorCreateInternetAddress"), rs.getString("username")) + rs.getString("email3"), ex);
                }
            }
            
            Set<InternetAddress> s = new HashSet<InternetAddress>(emails);
            InternetAddress[] d = new InternetAddress[s.size()];
            return s.toArray(d);
        } catch (SQLException ex) {
            log.log(Level.ERROR, bundle.getString("ErrorSQLException"), ex);
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException"), ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(prepareStatement);
        }
        return null;

    }

    public static boolean ListContainsStatisticalRules(ArrayOfSLA serviceLevelAggrements) {
        if (serviceLevelAggrements == null) {
            return false;
        }

        if (serviceLevelAggrements.getSLA().isEmpty()) {
            return false;
        }
        for (int i = 0; i < serviceLevelAggrements.getSLA().size(); i++) {
            if (containsStatisticalRules(serviceLevelAggrements.getSLA().get(i).getRule())) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsStatisticalRules(RuleBaseType rule) {
        if (rule == null) {
            return false;
        }
        //FIXME
        return true;
    }

    /*
     public static void ProcessStatisticalSLARules2(String modifiedurl, Map currenttopics, boolean pooled) {
     ServicePolicy pol = null;
     SetupBundle();
     if (pooled) {
     pol = LoadPolicyPooled(modifiedurl);
     } else {
     pol = LoadPolicyNotPooled(modifiedurl);
     }
     if (pol == null) {
     return;
     }
     if (pol.getServiceLevelAggrements() == null) {
     return;
     }
     if (pol.getServiceLevelAggrements().getValue() == null) {
     return;
     }
     if (pol.getServiceLevelAggrements().getValue().getSLA() == null) {
     return;
     }
     if (pol.getServiceLevelAggrements().getValue().getSLA().isEmpty()) {
     return;
     }
     for (int i = 0; i < pol.getServiceLevelAggrements().getValue().getSLA().size(); i++) {
           
            
     if (ProcessStatisticalSLARulesRecursive(req, pooled, null, null, null))
     if (pol.getServiceLevelAggrements().getValue().getSLA().get(i).getRule() instanceof QueueOrTopicDoesNotExist) {
     QueueOrTopicDoesNotExist rr = (QueueOrTopicDoesNotExist) pol.getServiceLevelAggrements().getValue().getSLA().get(i).getRule();
     boolean found = false;
     if (currenttopics.containsKey(rr.getParameter())) {
     found = true;
     }

     if (!found) {
     String msg = String.format(bundle.getString("SLAQueueNotExists"), rr.getParameter());
     String id = UUID.randomUUID().toString();
     RecordSLAFault(new AtomicReference<String>(msg), modifiedurl, null, System.currentTimeMillis(), id, pooled);
     ProcessAlerts(msg, "<h2>" + Utility.encodeHTML(msg) + "</h2>", modifiedurl, null, System.currentTimeMillis(), id, pooled, false, pol.getServiceLevelAggrements().getValue().getSLA().get(i).getAction().getSLAActionBaseType(), pol.getServiceLevelAggrements().getValue().getSLA().get(i).getGuid(), pol, AlertType.Status);
     }
     }
     if (pol.getServiceLevelAggrements().getValue().getSLA().get(i).getRule() instanceof BrokerQueueSizeGreaterThan) {
     BrokerQueueSizeGreaterThan rr = (BrokerQueueSizeGreaterThan) pol.getServiceLevelAggrements().getValue().getSLA().get(i).getRule();
     Iterator it = currenttopics.keySet().iterator();
     StringBuilder msg = new StringBuilder();
     while (it.hasNext()) {
     String str = (String) it.next();
     long get = (Long) currenttopics.get(str);
     if (get > rr.getParameter()) {
     msg.append("The queue named ").append(str).append(" has a queue size of ").append(get).append(" which is greater than the SLA parameter of ").append(rr.getParameter()).append(". ");
     }
     }
     String s = msg.toString();
     if (!Utility.stringIsNullOrEmpty(s)) {
     String id = UUID.randomUUID().toString();
     RecordSLAFault(new AtomicReference<String>(s), modifiedurl, null, System.currentTimeMillis(), id, pooled);
     ProcessAlerts(s, "<h2>" + Utility.encodeHTML(s) + "</h2>", modifiedurl, null, System.currentTimeMillis(), id, pooled, false, pol.getServiceLevelAggrements().getValue().getSLA().get(i).getAction().getSLAActionBaseType(), pol.getServiceLevelAggrements().getValue().getSLA().get(i).getGuid(), pol, AlertType.Status);
     }
     }
     }
     }*/
    /**
     * will never return null, but returned value may be an empty list if no
     * policies are defined
     *
     * @param hostname
     * @return
     */
    public static List<ProcessPolicy> LoadProcessPoliciesPooledByHostname(String hostname) {
        if (Utility.stringIsNullOrEmpty(hostname)) {
            throw new IllegalArgumentException("hostname");
        }
        SetupBundle();
        Connection con = Utility.getConfigurationDBConnection();
        List<ProcessPolicy> l = new ArrayList<ProcessPolicy>();
        PreparedStatement comm = null;
         ResultSet results=null;
        try {

            ProcessPolicy ret = null;
            

            comm = con.prepareStatement("Select * from ServicePolicies where policytype=? and hostname=?;");
            comm.setInt(1, PolicyType.PROCESS.ordinal());
            comm.setString(2, hostname);

            results = comm.executeQuery();

            while (results.next()) {
                PolicyType pt = PolicyType.values()[results.getInt("policytype")];
                Unmarshaller um = Utility.getSerializationContext().createUnmarshaller();
                byte[] s = results.getBytes("xmlpolicy");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                //1 = reader
                //2 = writer
                //                XMLStreamReaderImpl r = new XMLStreamReaderImpl(bss, new PropertyManager(1));
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                switch (pt) {
                    case PROCESS:
                        JAXBElement<ProcessPolicy> foo3 = (JAXBElement<ProcessPolicy>) um.unmarshal(r, ProcessPolicy.class);
                        if (foo3 == null || foo3.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("ProcessPolicyNull"));
                        } else {
                            ret = foo3.getValue();
                        }
                        break;
                }
                r.close();
                bss.close();

                l.add(ret);
            }
        } catch (JAXBException ex) {
            log.log(Level.ERROR, bundle.getString("ErrorMarshallingPolicy"), ex);
        } catch (SQLException ex) {
            log.log(Level.ERROR, bundle.getString("ErrorSQLException"), ex);
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException"), ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        
        return l;

    }

    /**
     * may return null if no policy exists
     *
     * @param hostname
     * @return
     */
    private static MachinePolicy loadMachinePolicyPooled(String hostname) {
        if (Utility.stringIsNullOrEmpty(hostname)) {
            throw new IllegalArgumentException("hostname");
        }
        SetupBundle();
        Connection con = Utility.getConfigurationDBConnection();
        MachinePolicy ret = null;
        PreparedStatement comm = null;
        ResultSet results = null;
        try {

            comm = con.prepareStatement("Select * from ServicePolicies where policytype=? and hostname=?;");
            comm.setInt(1, PolicyType.MACHINE.ordinal());
            comm.setString(2, hostname);

            results = comm.executeQuery();

            while (results.next()) {
                PolicyType pt = PolicyType.values()[results.getInt("policytype")];
                Unmarshaller um = Utility.getSerializationContext().createUnmarshaller();
                byte[] s = results.getBytes("xmlpolicy");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                //1 = reader
                //2 = writer
                //                XMLStreamReaderImpl r = new XMLStreamReaderImpl(bss, new PropertyManager(1));
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                switch (pt) {
                    case MACHINE:
                        JAXBElement<MachinePolicy> foo3 = (JAXBElement<MachinePolicy>) um.unmarshal(r, MachinePolicy.class);
                        if (foo3 == null || foo3.getValue() == null) {
                            log.log(Level.WARN, "policy is unexpectedly null or empty");
                        } else {
                            ret = foo3.getValue();
                        }
                        break;
                }
                r.close();
                bss.close();
            }
           
        } catch (JAXBException ex) {
            log.log(Level.ERROR, bundle.getString("ErrorMarshallingPolicy"), ex);
        } catch (SQLException ex) {
            log.log(Level.ERROR, bundle.getString("ErrorSQLException"), ex);
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorUncaughtException"), ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
            DBUtils.safeClose(con);
        }
        GlobalPolicy gp = DBSettingsLoader.GetGlobalPolicy(true);
        if (ret != null) {
            ret.setPolicyRefreshRate(gp.getPolicyRefreshRate());
        }
        return ret;
    }

    public static MachinePolicy ProcessMachineLevelSLAs(AddMachineAndProcessDataRequestMsg req, boolean pooled) {
        MachinePolicy mp = loadMachinePolicyPooled(req.getHostname());
        AuxHelper.TryUpdateStatus(req.getMachineData().isOperationalstatus(),
                req.getMachineData().getUri(),
                req.getMachineData().getStatusmessage(), pooled,
                PolicyType.MACHINE,
                req.getDomainname(),
                req.getHostname());
        processMachineSLAs(req, mp, pooled);

        return mp;
    }

    public static List<ProcessPolicy> ProcessProcessLevelSLAs(AddMachineAndProcessDataRequestMsg req, boolean pooled) {
        List<ProcessPolicy> ret = LoadProcessPoliciesPooledByHostname(req.getHostname());
        for (int i = 0; i < req.getProcessData().size(); i++) {
            //Bug fix, if a process policy was deleted at the agent doesn't know about it, this will actually recreate the process policy, which is not desired.
            if (PolicyExists(req.getProcessData().get(i).getUri(), pooled)) {
                AuxHelper.TryUpdateStatus(req.getProcessData().get(i).isOperationalstatus(),
                        req.getProcessData().get(i).getUri(),
                        req.getProcessData().get(i).getStatusmessage(), pooled,
                        PolicyType.PROCESS,
                        req.getDomainname(),
                        req.getHostname());
                processProcessSLAs(req.getProcessData().get(i), ret, pooled);
            }
        }

        return ret;
    }

    private static boolean processMachineSLAsRecursive(AddMachineAndProcessDataRequestMsg req, RuleBaseType rule, MachinePolicy pol, boolean pooled, AtomicReference<String> outFaultmsg) {
        if (outFaultmsg == null) {
            outFaultmsg = new AtomicReference<String>();
        }
        if (pol == null) {
            log.log(Level.INFO, bundle.getString("ErrorProcessMachineSLANullPolicy") + req.getDomainname() + " " + req.getHostname());
            return false;
        }
        SetupBundle();
        StringBuilder sb = new StringBuilder();

        if (pol.getServiceLevelAggrements() == null) {
            return false;
        }

        if (rule instanceof AndOrNot) {
            AndOrNot aon = (AndOrNot) rule;
            switch (aon.getFlag()) {
                case AND:
                    return processMachineSLAsRecursive(req, rule, pol, pooled, outFaultmsg) && processMachineSLAsRecursive(req, rule, pol, pooled, outFaultmsg);
                case OR:
                    return processMachineSLAsRecursive(req, rule, pol, pooled, outFaultmsg) || processMachineSLAsRecursive(req, rule, pol, pooled, outFaultmsg);
                case NOT:
                    return !processMachineSLAsRecursive(req, rule, pol, pooled, outFaultmsg);
            }
        }

        if (rule instanceof SLARuleGeneric) {
            SLARuleGeneric x = (SLARuleGeneric) rule;
            if (x.getProcessAt() == RunAtLocation.FGSMS_SERVER) {
                Class c = null;
                try {
                    c = Thread.currentThread().getContextClassLoader().loadClass(x.getClassName());
                } catch (ClassNotFoundException ex) {
                    log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                }
                Object obj = null;
                if (c != null) {
                    try {
                        obj = c.newInstance();
                    } catch (InstantiationException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);

                    } catch (IllegalAccessException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                    }
                    SLARuleInterface cast = null;
                    try {
                        cast = (SLARuleInterface) obj;
                    } catch (ClassCastException ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleTypeCast"), x.getClassName(), SLARuleInterface.class.getCanonicalName()), ex);
                    }
                    try {
                        AtomicReference<String> f = new AtomicReference<String>();
                        if (cast != null && cast.CheckTransactionalRule(req.getMachineData(), x.getParameterNameValue(), f)) {
                            sb = sb.append(f.get());
                            outFaultmsg.set(sb.toString() + " " + outFaultmsg.get());
                            return true;
                        }
                    } catch (Exception ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleUnexpectedError"), x.getClassName()), ex);
                    }
                }
            }
        }
        return false;
    }

    /**
     * does process specific SLA processing, scope: just right now
     *
     * @param processData
     * @param pooled
     */
    private static void processMachineSLAs(AddMachineAndProcessDataRequestMsg req, MachinePolicy pol, boolean pooled) {

        if (pol == null || pol.getServiceLevelAggrements() == null) {
            return;
        }
        for (int i = 0; i < pol.getServiceLevelAggrements().getSLA().size(); i++) {
            RuleBaseType rule = pol.getServiceLevelAggrements().getSLA().get(i).getRule();

            AtomicReference<String> msg = new AtomicReference<String>();
            if (processMachineSLAsRecursive(req, rule, pol, pooled, msg)) {
                String id = UUID.randomUUID().toString();
                RecordSLAFault(new AtomicReference<String>(msg.get()), pol.getURL(), req.getMachineData().getId(), System.currentTimeMillis(), id, pooled);
                ProcessAlerts(msg.get(), "<h2" + Utility.encodeHTML(msg.get()) + "</h2>", pol.getURL(), req.getMachineData().getId(), System.currentTimeMillis(), id, pooled,
                        false, pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction(), pol.getServiceLevelAggrements().getSLA().get(i).getGuid(), pol, AlertType.Performance);
            }
        }
    }

    private static boolean processProcessSLAsRecursive(ProcessPerformanceData machineData, List<ProcessPolicy> pol, boolean pooled, RuleBaseType rule, AtomicReference<String> outFaultMsg) {

        if (rule instanceof AndOrNot) {
            AndOrNot aon = (AndOrNot) rule;
            switch (aon.getFlag()) {
                case AND:
                    return processProcessSLAsRecursive(machineData, pol, pooled, rule, outFaultMsg) && processProcessSLAsRecursive(machineData, pol, pooled, rule, outFaultMsg);
                case OR:
                    return processProcessSLAsRecursive(machineData, pol, pooled, rule, outFaultMsg) || processProcessSLAsRecursive(machineData, pol, pooled, rule, outFaultMsg);
                case NOT:
                    return !processProcessSLAsRecursive(machineData, pol, pooled, rule, outFaultMsg);
            }
        }
        if (rule instanceof SLARuleGeneric) {
            SLARuleGeneric x = (SLARuleGeneric) rule;
            if (x.getProcessAt() == RunAtLocation.FGSMS_SERVER) {
                Class c = null;
                try {
                    c = Thread.currentThread().getContextClassLoader().loadClass(x.getClassName());
                } catch (ClassNotFoundException ex) {
                    log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                }
                Object obj = null;
                if (c != null) {
                    try {
                        obj = c.newInstance();
                    } catch (InstantiationException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);

                    } catch (IllegalAccessException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                    }
                    SLARuleInterface cast = null;
                    try {
                        cast = (SLARuleInterface) obj;
                    } catch (ClassCastException ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleTypeCast"), x.getClassName(), SLARuleInterface.class.getCanonicalName()), ex);
                    }
                    try {
                        AtomicReference<String> f = new AtomicReference<String>();
                        if (cast != null && cast.CheckTransactionalRule(machineData, x.getParameterNameValue(), f)) {
                            outFaultMsg.set(f.get() + " " + outFaultMsg.get());
                            return true;
                        }
                    } catch (Exception ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleUnexpectedError"), x.getClassName()), ex);
                    }
                }
            }
        }
        return false;
    }

    /**
     * process SLAs for specific processes
     *
     * @param machineData
     * @param pol
     * @param pooled
     */
    private static void processProcessSLAs(ProcessPerformanceData machineData, List<ProcessPolicy> pol, boolean pooled) {
        if (pol == null) {
            log.log(Level.WARN, bundle.getString("ErrorProcessSLANullPolicy") + machineData.getUri());
            return;
        }
        SetupBundle();
        ProcessPolicy pp = locateRecord(pol, machineData.getUri());
        if (pp == null) {
            return;
        }
        if (pp.getServiceLevelAggrements() == null) {
            return;
        }
        for (int i = 0; i < pp.getServiceLevelAggrements().getSLA().size(); i++) {
            RuleBaseType rule = pp.getServiceLevelAggrements().getSLA().get(i).getRule();

            AtomicReference<String> fault = new AtomicReference<String>();

            if (processProcessSLAsRecursive(machineData, pol, pooled, rule, fault)) {
                String id = UUID.randomUUID().toString();
                RecordSLAFault(new AtomicReference<String>(fault.get()), pp.getURL(), machineData.getId(), System.currentTimeMillis(), id, pooled);
                ProcessAlerts((fault.get()), "<h2>" + Utility.encodeHTML(fault.get()) + "</h2>", pp.getURL(), machineData.getId(), System.currentTimeMillis(),
                        id, pooled, false, pp.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction(),
                        pp.getServiceLevelAggrements().getSLA().get(i).getGuid(), pp, AlertType.Performance);
            }
        }
    }

    private static ProcessPolicy locateRecord(List<ProcessPolicy> pol, String uri) {
        if (pol == null || pol.isEmpty()) {
            return null;
        }
        for (int i = 0; i < pol.size(); i++) {
            if (pol.get(i).getURL().equalsIgnoreCase(uri)) {
                return pol.get(i);
            }
        }
        return null;
    }

    private static boolean processStatisticalSLARulesRecursive(AddStatisticalDataRequestMsg req, boolean pooled, RuleBaseType rule, AtomicReference<String> outFaultMsg, AtomicReference<AlertType> alertType) {
        if (rule instanceof AndOrNot) {
            AndOrNot aon = (AndOrNot) rule;
            switch (aon.getFlag()) {
                case AND:
                    return processStatisticalSLARulesRecursive(req, pooled, rule, outFaultMsg, alertType) && processStatisticalSLARulesRecursive(req, pooled, rule, outFaultMsg, alertType);
                case OR:
                    return processStatisticalSLARulesRecursive(req, pooled, rule, outFaultMsg, alertType) || processStatisticalSLARulesRecursive(req, pooled, rule, outFaultMsg, alertType);
                case NOT:
                    return !processStatisticalSLARulesRecursive(req, pooled, rule, outFaultMsg, alertType);
            }
        }
        if (rule instanceof SLARuleGeneric) {
            SLARuleGeneric x = (SLARuleGeneric) rule;
            if (x.getProcessAt() == RunAtLocation.FGSMS_SERVER) {
                Class c = null;
                try {
                    c = Thread.currentThread().getContextClassLoader().loadClass(x.getClassName());
                } catch (ClassNotFoundException ex) {
                    log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                }
                Object obj = null;
                if (c != null) {
                    try {
                        obj = c.newInstance();
                    } catch (InstantiationException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);

                    } catch (IllegalAccessException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                    }
                    SLARuleInterface cast = null;
                    try {
                        cast = (SLARuleInterface) obj;
                    } catch (ClassCastException ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleTypeCast"), x.getClassName(), SLARuleInterface.class.getCanonicalName()), ex);
                    }
                    try {
                        AtomicReference<String> f = new AtomicReference<String>();
                        if (cast != null && cast.CheckTransactionalRule(req.getBrokerURI(), req.getData(), x.getParameterNameValue(), f)) {
                            alertType.set(cast.GetType());
                            outFaultMsg.set(f.get() + " " + outFaultMsg.get());
                            return true;
                        }
                    } catch (Exception ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleUnexpectedError"), x.getClassName()), ex);
                    }
                }
            }
        }
        return false;
    }

    /**
     * processes broker rules for when a queue or topic doesn't exist and
     * QueueOrTopicDoesNotExist, BrokerQueueSizeGreaterThan Map contains the key
     * (topic name), value, size of the queue
     *
     * @param req
     * @param pooled
     */
    public static void ProcessStatisticalSLARules(AddStatisticalDataRequestMsg req, boolean pooled) {
        ServicePolicy pol = null;
        SetupBundle();
        if (pooled) {
            pol = LoadPolicyPooled(req.getBrokerURI());
        } else {
            pol = LoadPolicyNotPooled(req.getBrokerURI());
        }
        if (pol == null) {
            return;
        }
        if (pol.getServiceLevelAggrements() == null) {
            return;
        }

        if (pol.getServiceLevelAggrements().getSLA() == null) {
            return;
        }
        if (pol.getServiceLevelAggrements().getSLA().isEmpty()) {
            return;
        }
        for (int i = 0; i < pol.getServiceLevelAggrements().getSLA().size(); i++) {
            AtomicReference<String> ref = new AtomicReference<String>();
            AtomicReference<AlertType> alertType = new AtomicReference<AlertType>();
            if (processStatisticalSLARulesRecursive(req, pooled, pol.getServiceLevelAggrements().getSLA().get(i).getRule(), ref, alertType)) {
                String id = UUID.randomUUID().toString();
                RecordSLAFault(new AtomicReference<String>(ref.get()), req.getBrokerURI(), null, System.currentTimeMillis(), id, pooled);
                ProcessAlerts(ref.get(), Utility.encodeHTML(ref.get()), req.getBrokerURI(), null, System.currentTimeMillis(), id, pooled, false,
                        pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction(), pol.getServiceLevelAggrements().getSLA().get(i).getGuid(), pol,
                        alertType.get());
            }
        }

    }

    /**
     * removes the trailing comma
     *
     * @param msg
     * @return
     */
    private static String trimFaultMsg(String msg) {
        if (Utility.stringIsNullOrEmpty(msg)) {
            return "";
        }
        String s = msg.trim();
        if (s.endsWith(",")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * Handles sending of alert messages. Items are added a queue and processed
     * either via Jboss's worker pool or via a singleton SLA processor thread.
     *
     * @param faultMsg human readable fault message
     * @param htmlEncodedFaultMessage html formatted alert message
     * @param modifiedurl the policy url
     * @param relatedMessageId a related record that caused this action to be
     * trigger, may be null
     * @param currentTimeMillis the time it was trigger
     * @param incidentid a unique identifier for the SLA, it will be recorded
     * using this ID
     * @param pooled are we in jboss and can we use jndi for database lookups?
     * @param sendtoAdminsOnly
     * @param slaActionBaseType the action to trigger
     * @param SLAID the UUID of the SLA Rule/Action set
     * @param t service policy
     * @param at alert type
     */
    public static void ProcessAlerts(String faultMsg, String htmlEncodedFaultMessage, String modifiedurl, String relatedMessageId, long currentTimeMillis,
            String incidentid, boolean pooled, boolean sendtoAdminsOnly, List<SLAAction> slaActionBaseType,
            String SLAID, ServicePolicy t, AlertType at) {
        SetupBundle();
        if (slaActionBaseType == null || slaActionBaseType.isEmpty()) {
            log.log(Level.WARN, bundle.getString("ErrorNullSLAAlert"));
            return;
        }

        SLAProcessorSingleton instance = SLAProcessorSingleton.getInstance();
        org.oasis_open.docs.wsdm.muws2_2.ObjectFactory f = new org.oasis_open.docs.wsdm.muws2_2.ObjectFactory();
        for (int i = 0; i < slaActionBaseType.size(); i++) {
            AlertContainer alert = null;
            switch (at) {
                case Performance:
                    alert = new AlertContainer(trimFaultMsg(faultMsg), htmlEncodedFaultMessage, modifiedurl, relatedMessageId, currentTimeMillis, incidentid, pooled, sendtoAdminsOnly, slaActionBaseType.get(i), SLAID, t, f.createPerformanceReport());
                    break;
                case Status:
                    alert = new AlertContainer(trimFaultMsg(faultMsg), htmlEncodedFaultMessage, modifiedurl, relatedMessageId, currentTimeMillis, incidentid, pooled, sendtoAdminsOnly, slaActionBaseType.get(i), SLAID, t, f.createAvailabilitySituation());
            }

            SLAProcessorSingleton.EnqueueAlert(alert);
        }

    }

    /**
     * Checks for the existence of a policy
     *
     * @param uri
     * @param pooled
     * @return
     */
    public static boolean PolicyExists(String uri, boolean pooled) {
        boolean found = false;
        Connection con = null;
        if (pooled) {
            con = Utility.getConfigurationDBConnection();
        } else {
            con = Utility.getConfigurationDB_NONPOOLED_Connection();
        }
        PreparedStatement cmd=null;
        ResultSet rs=null;
        try {
            cmd = con.prepareStatement("select count(*) from servicepolicies where uri=?;");
            cmd.setString(1, uri);
            rs = cmd.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 1) {
                    found = true;
                }
            }
            
        } catch (Exception ex) {
            log.log(Level.WARN, bundle.getString("ErrorCheckPolicyExists"), ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        return found;
    }

    /**
     * Returns true if the proposed SLA configuration is valid
     *
     * @param outmsg
     * @param pol
     * @return
     */
    public static boolean ValidateSLAs(AtomicReference<String> outmsg, ServicePolicy pol) {
        if (outmsg == null) {
            outmsg = new AtomicReference<String>();
        }
        if (pol.getServiceLevelAggrements() == null
                || pol.getServiceLevelAggrements().getSLA().isEmpty()) {
            return true;
        }
        boolean valid = true;
        for (int i = 0; i < pol.getServiceLevelAggrements().getSLA().size(); i++) {
            if (Utility.stringIsNullOrEmpty(pol.getServiceLevelAggrements().getSLA().get(i).getGuid())) {
                outmsg.set("Each SLA must have a unique identifier, try UUID.randomUUID().toString() or Guid.newGuid().toString(). " + outmsg.get());
                valid = false;
            }
            if (pol.getServiceLevelAggrements().getSLA().get(i).getRule() == null) {
                outmsg.set("Each SLA must have a Rule. " + outmsg.get());
                valid = false;
            } else {
                //validate rules
                //AtomicReference<String> ref = new AtomicReference<String>();
                valid = valid && (validateRulesRecursive(pol, outmsg, pol.getServiceLevelAggrements().getSLA().get(i).getRule()));
            }
            if (pol.getServiceLevelAggrements().getSLA().get(i).getAction() == null
                    || pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().isEmpty()) {
                outmsg.set("Each SLA must have at least one Action. " + outmsg.get());
                valid = false;
            } else {
                valid = valid && validateActions(pol, outmsg, pol.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction());
                //validate Actions
            }
        }
        return valid;
    }

    /**
     * Returns true if the proposed SLA configuration is valid
     */
    private static boolean validateRulesRecursive(ServicePolicy pol, AtomicReference<String> ref, RuleBaseType rule) {
        if (rule instanceof SLARuleGeneric) {

            SLARuleGeneric r = (SLARuleGeneric) rule;
            if (Utility.stringIsNullOrEmpty(r.getClassName())) {
                ref.set(ref.get() + " Rule class name is not defined");
                return false;
            }
            try {
                Class<SLARuleInterface> forName = (Class<SLARuleInterface>) Class.forName(r.getClassName());
                SLARuleInterface newInstance = forName.newInstance();
                if (!newInstance.GetAppliesTo().contains(pol.getPolicyType())) {
                    ref.set(ref.get() + " The specified Rule, class name=" + r.getClassName() + " does not apply to the policy type of " + pol.getPolicyType().value());
                    return false;
                }
                return newInstance.ValidateConfiguration(r.getParameterNameValue(), ref, pol);
            } catch (Exception ex) {
                ref.set(ref.get() + " The specified Rule, class name=" + r.getClassName() + " could not be loaded, " + ex.getMessage());
                return false;
            }

        } else if (rule instanceof AndOrNot) {
            AndOrNot aon = (AndOrNot) rule;
            switch (aon.getFlag()) {
                case AND:
                    return validateRulesRecursive(pol, ref, aon.getLHS()) && validateRulesRecursive(pol, ref, aon.getRHS());
                case NOT:
                    return !validateRulesRecursive(pol, ref, aon.getLHS());
                case OR:
                    return validateRulesRecursive(pol, ref, aon.getLHS()) || validateRulesRecursive(pol, ref, aon.getRHS());
            }
        } else {
            ref.set(ref.get() + " Unknown rule type");
        }
        return false;
    }

    private static List<ServicePolicy> loadServicePoliciesWithFederation(Connection con) {
        SetupBundle();
         PreparedStatement comm = null;
         ResultSet results = null;
        try {
            List<ServicePolicy> l = new ArrayList<ServicePolicy>();
           
            comm = con.prepareStatement("Select * from ServicePolicies where hasfederation=true;");
            /////////////////////////////////////////////
            //get the policy for this service
            /////////////////////////////////////////////

            Unmarshaller um = Utility.getSerializationContext().createUnmarshaller();
            results = comm.executeQuery();

            while (results.next()) {
                ServicePolicy ret = null;

                PolicyType pt = PolicyType.values()[results.getInt("policytype")];

                byte[] s = results.getBytes("xmlpolicy");

                ByteArrayInputStream bss = new ByteArrayInputStream(s);
                XMLInputFactory xf = XMLInputFactory.newInstance();
                XMLStreamReader r = xf.createXMLStreamReader(bss);
                switch (pt) {
                    case MACHINE:
                        JAXBElement<MachinePolicy> foo = (JAXBElement<MachinePolicy>) um.unmarshal(r, MachinePolicy.class);
                        if (foo == null || foo.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("MachinePolicyNull"));
                        } else {
                            ret = foo.getValue();
                        }
                        break;
                    case PROCESS:
                        JAXBElement<ProcessPolicy> foo3 = (JAXBElement<ProcessPolicy>) um.unmarshal(r, ProcessPolicy.class);
                        if (foo3 == null || foo3.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("ProcessPolicyNull"));
                        } else {
                            ret = foo3.getValue();
                        }
                        break;
                    case STATISTICAL:
                        JAXBElement<StatisticalServicePolicy> foo1 = (JAXBElement<StatisticalServicePolicy>) um.unmarshal(r, StatisticalServicePolicy.class);
                        if (foo1 == null || foo1.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("BrokerPolicyNull"));
                        } else {
                            ret = foo1.getValue();
                        }
                        break;
                    case STATUS:
                        JAXBElement<StatusServicePolicy> foo2 = (JAXBElement<StatusServicePolicy>) um.unmarshal(r, StatusServicePolicy.class);
                        if (foo2 == null || foo2.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("StatusPolicyNull"));
                        } else {
                            ret = foo2.getValue();
                        }
                        break;
                    case TRANSACTIONAL:
                        JAXBElement<TransactionalWebServicePolicy> foo4 = (JAXBElement<TransactionalWebServicePolicy>) um.unmarshal(r, TransactionalWebServicePolicy.class);
                        if (foo4 == null || foo4.getValue() == null) {
                            log.log(Level.WARN, bundle.getString("WSPolicyNull"));
                        } else {
                            ret = foo4.getValue();
                        }
                        break;
                }

                r.close();
                bss.close();

                l.add(ret);

            }
           
            return l;
        } catch (Exception ex) {
            log.log(Level.WARN, bundle.getString("ErrorLoadingPolicyForSLA"), ex);
        } finally {
            DBUtils.safeClose(results);
            DBUtils.safeClose(comm);
        }
        return new ArrayList<ServicePolicy>();
    }

    /**
     * used by Federation Scheduler for publication
     *
     * @return either an empty list, or a list containing service policies that
     * contain SLA's
     */
    public static List<ServicePolicy> LoadFederationServicePoliciesPooled() {

        SetupBundle();
        Connection con = Utility.getConfigurationDBConnection();
        List<ServicePolicy> r = null;
        try {
            r = loadServicePoliciesWithFederation(con);
        } catch (Exception ex) {
            log.log(Level.ERROR, bundle.getString("ErrorLoadingPolicy"), ex);
        } finally {
            DBUtils.safeClose(con);
        }
        if (r == null) {
            r = new ArrayList<ServicePolicy>();
        }
        return r;

    }

    /**
     * return true if valid
     *
     * @param pol
     * @param outmsg
     * @param action
     * @return
     */
    private static boolean validateActions(ServicePolicy pol, AtomicReference<String> ref, List<SLAAction> action) {
        for (int i = 0; i < action.size(); i++) {
            if (Utility.stringIsNullOrEmpty(action.get(i).getImplementingClassName())) {
                ref.set(ref.get() + " action class name is not defined");
                return false;
            }
            
            Connection con = Utility.getConfigurationDBConnection();
            PreparedStatement cmd=null;
            ResultSet rs=null;
            try{
                //validate that the plugin is registered
                cmd=con.prepareStatement("select * from plugins where classname = ? AND appliesto = ?");
                cmd.setString(1, action.get(i).getImplementingClassName());
                cmd.setString(2, "SLA_ACTION");
                rs = cmd.executeQuery();
                if (rs.next()){
                    //we are ok
                } else {
                    ref.set(ref.get() + " The specified action, class name=" + action.get(i).getImplementingClassName() + " is not registered");
                    return false;
                }
            }catch (Exception ex){
                log.log(Level.WARN, null, ex);
                
            } finally{
                DBUtils.safeClose(rs);
                DBUtils.safeClose(cmd);
                DBUtils.safeClose(con);
            }
            try {
                Class<SLAActionInterface> forName = (Class<SLAActionInterface>) Class.forName(action.get(i).getImplementingClassName());
                SLAActionInterface newInstance = forName.newInstance();
                if (!newInstance.GetAppliesTo().contains(pol.getPolicyType())) {
                    ref.set(ref.get() + " The specified action, class name=" + action.get(i).getImplementingClassName() + " does not apply to the policy type of " + pol.getPolicyType().value());
                    return false;
                }
                return newInstance.ValidateConfiguration(action.get(i).getParameterNameValue(), ref);
            } catch (Exception ex) {
                ref.set(ref.get() + " The specified action, class name=" + action.get(i).getImplementingClassName() + " could not be loaded, " + ex.getMessage());
                return false;
            }

        }
        return true;
    }

    /**
     * Handles each alert and calls the corresponding function to process the
     * alert
     *
     * @param alert
     */
    protected void DoAlerts(AlertContainer alert) {
        log.debug("Processing alert for " + alert.getModifiedurl() + " msg " + alert.getFaultMsg() + " class " + alert.getSlaActionBaseType().getImplementingClassName());
        Class c = null;
        try {
            c = Thread.currentThread().getContextClassLoader().loadClass(alert.getSlaActionBaseType().getImplementingClassName());
        } catch (ClassNotFoundException ex) {
            //ignore 
        }
        if (c == null) {
            try {
                c = this.getClass().getClassLoader().loadClass(alert.getSlaActionBaseType().getImplementingClassName());
            } catch (ClassNotFoundException ex) {
                //ignore 
            }
        }
        if (c == null) {
            try {
                c = Class.forName(alert.getSlaActionBaseType().getImplementingClassName());
            } catch (ClassNotFoundException ex) {
                //ignore 
            }
        }
        if (c == null) {
            log.error("Error to Load class for SLA Alert!!!" + alert.getSlaActionBaseType().getImplementingClassName());
        } else {
            try {
                Object j = c.newInstance();
                SLAActionInterface item = (SLAActionInterface) j;
                item.ProcessAction(alert, alert.getSlaActionBaseType().getParameterNameValue());
            } catch (Exception ex) {
                log.error("Error unable to process SLA Alert!!!", ex);
            }
        }

    }

    private static DriveInformation locateDriveRecord(List<DriveInformation> driveInformation, String partition) {
        if (driveInformation == null) {
            return null;
        }
        for (int i = 0; i < driveInformation.size(); i++) {
            if (partition.equalsIgnoreCase(driveInformation.get(i).getPartition())) {
                return driveInformation.get(i);
            }
        }
        return null;
    }

    public static SecurityWrapper GetClassLevel(boolean pooled) {
        Connection con = null;
        SecurityWrapper c = new SecurityWrapper();
        if (pooled) {
            con = Utility.getConfigurationDBConnection();
        } else {
            con = Utility.getConfigurationDB_NONPOOLED_Connection();
        }
        PreparedStatement cmd =null;
        ResultSet rs = null;
        try {
             cmd = con.prepareStatement("select classification,caveat from globalpolicies;");
             rs = cmd.executeQuery();
            if (rs.next()) {
                c.setCaveats(rs.getString("caveat"));
                c.setClassification(ClassificationType.fromValue(rs.getString("classification")));
            }
            
        } catch (Exception ex) {
            log.log(Level.WARN,null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
        return c;
    }

    /**
     * Gets the current status of an item.
     *
     * @param pooled
     * @param policyurl
     * @param out_status NULL if status is unknown, true = runnning
     * @param out_timestamp NULL if status is unknown, else time since epoch
     * @param out_message
     * @since 6.2
     */
    public static void GetCurrentStatus(boolean pooled, String policyurl, AtomicReference< Boolean> out_status, AtomicReference< Long> out_timestamp, AtomicReference< String> out_message) {
        java.sql.Connection con = null;
        SetupBundle();
         PreparedStatement cmd =null;
        ResultSet rs = null;
        try {
            if (!pooled) {
                con = Utility.getConfigurationDB_NONPOOLED_Connection();

            } else {
                con = Utility.getConfigurationDBConnection();
            }

            cmd = con.prepareStatement("select * from status  where uri=?;");
            cmd.setString(1, policyurl);
            rs = cmd.executeQuery();
            if (rs.next()) {
                out_timestamp.set(rs.getLong("utcdatetime"));
                out_message.set(rs.getString("message"));
                out_status.set(rs.getBoolean("status"));
            } else {
                out_timestamp.set(null);
                out_message.set(null);
                out_status.set(null);
            }
           
        } catch (Exception ex) {
            log.log(Level.WARN, String.format(bundle.getString("ErrorUnableToUpdateStatus"), policyurl), ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(cmd);
            DBUtils.safeClose(con);
        }
    }
}
