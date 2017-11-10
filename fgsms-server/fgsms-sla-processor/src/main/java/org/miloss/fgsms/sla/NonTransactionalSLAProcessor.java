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
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.sla;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.datatype.Duration;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertType;
import org.miloss.fgsms.plugins.sla.SLARuleInterface;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;

/**
 * NonTransactionalSLAProcessor - handles most non transactional SLA
 * rules for web services, processes, operating systems, status What
 * is a Non Transactional SLA? Something that occurs over a range of time or
 * grouping of records
 *
 * @author AO
 */
public class NonTransactionalSLAProcessor {

    public NonTransactionalSLAProcessor()//Hashtable policyCache, ConcurrentLinkedQueue<AddDataRequestMsg> outboundQueue)
    {
    }
    // in ms
    //   private long timeRange;
    // private Hashtable policyCache;
    private static Logger log = Logger.getLogger("fgsms.SLAProcessor");

    private List<ServicePolicy> Load(boolean pooled) {
        List<ServicePolicy> list = null;
        
            list = SLACommon.LoadServicePoliciesPooled();
        
        return list;

    }

    /**
     * processes time range based SLAs for all service policy types
     *
     * @param ispooled
     */
    public void run(boolean ispooled) {
        log.log(Level.INFO, "Loading service policies...");
        List<ServicePolicy> servicePolicyQueue = Load(ispooled);

        //     UpdateStatus();
        log.log(Level.INFO, servicePolicyQueue.size() + " policies loaded...");

        if (servicePolicyQueue.isEmpty()) {
            log.log(Level.INFO, "fgsms SLA NT Processor nothing to process, exiting...");
            return;
        }
        log.log(Level.INFO, "fgsms SLA Pusher entering loop " + servicePolicyQueue.size() + " policies to process. ");
        for (int k = 0; k < servicePolicyQueue.size(); k++) {

            if (servicePolicyQueue.get(k) == null) {
                continue;
            }
            if (servicePolicyQueue.get(k).getServiceLevelAggrements() == null) {
                continue;
            }
            
            if (servicePolicyQueue.get(k).getServiceLevelAggrements().getSLA().isEmpty()) {
                continue;
            }


            for (int i = 0; i < servicePolicyQueue.get(k).getServiceLevelAggrements().getSLA().size(); i++) {
                boolean flag = false;
                StringBuffer faultMsg = new StringBuffer();


                if (servicePolicyQueue.get(k).getServiceLevelAggrements().getSLA() == null) {
                    continue;
                }
                if (servicePolicyQueue.get(k).getServiceLevelAggrements().getSLA().get(i) == null) {
                    continue;
                }
                if (servicePolicyQueue.get(k).getServiceLevelAggrements().getSLA().get(i).getRule() == null) {
                    continue;
                }

                //identify if any non transaction rules are present, if so collect results and add them to a HashMap
//                flag = ProcessNonTransactionalRules(pol.url, faultMsg, pol.slas.getValue().getSLA().get(i).getRule(), lastprocessedat);
                flag = ProcessNonTransactionalRules(
                        faultMsg, servicePolicyQueue.get(k).getServiceLevelAggrements().getSLA().get(i).getRule(),
                        servicePolicyQueue.get(k).getPolicyType(), ispooled, servicePolicyQueue.get(k));

                if (flag) {
                    String incident = UUID.randomUUID().toString();
                    log.log(Level.WARN, "SLA Fault tripped for " + servicePolicyQueue.get(k).getURL() + " message: " + faultMsg);


                    SLACommon.RecordSLAFault(new AtomicReference<String>(faultMsg.toString()), servicePolicyQueue.get(k).getURL(), null, System.currentTimeMillis(), incident, ispooled);
                    SLACommon.ProcessAlerts(faultMsg.toString(), "<h2>" + Utility.encodeHTML(faultMsg.toString()) + "</h2>", servicePolicyQueue.get(k).getURL(), null, System.currentTimeMillis(),
                            incident, ispooled, false, servicePolicyQueue.get(k).getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction(),
                            servicePolicyQueue.get(k).getServiceLevelAggrements().getSLA().get(i).getGuid(), servicePolicyQueue.get(k), AlertType.Performance);
                }
            }
        }
    }

    public static String GenerateLink(String relativeUrl, ServicePolicy pol) {
        if (pol.getPolicyType() == null || pol.getPolicyType() == PolicyType.TRANSACTIONAL) {
            return "<a href=\"" + relativeUrl + "/TransactionLogViewer.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">View recent transactions</a><br>"
                    + "<a href=\"" + relativeUrl + "/availability.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">View availability data</a>";
        }

        if (pol.getPolicyType() == PolicyType.STATISTICAL) {
            return "<a href=\"" + relativeUrl + "/messageBrokerDetail.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">View statistics</a><br>"
                    + "<a href=\"" + relativeUrl + "/availability.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">View availability data</a>";
        }

        if (pol.getPolicyType() == PolicyType.STATUS) {
            return "<a href=\"" + relativeUrl + "/availability.jsp?url=" + URLEncoder.encode(pol.getURL()) + "\">View availability data</a>";
        }
        return "<a href=\"" + relativeUrl + "\">fgsms</a>";

    }

    public static String GenerateLink(String relativeUrl, String pol) {

        return "<a href=\"" + relativeUrl + "/TransactionLogViewer.jsp?url=" + URLEncoder.encode(pol) + "\">View recent transactions</a><br>"
                + "<a href=\"" + relativeUrl + "/availability.jsp?url=" + URLEncoder.encode(pol) + "\">View availability data</a>";
    }

    private boolean ProcessNonTransactionalRules(StringBuffer faultMsg, RuleBaseType rule, PolicyType pol, boolean pooled, ServicePolicy p) {

        if (rule instanceof AndOrNot) {
            AndOrNot aon = (AndOrNot) rule;
            switch (aon.getFlag()) {
                case AND:
                     return ProcessNonTransactionalRules(faultMsg, aon.getLHS(), pol, pooled, p) && ProcessNonTransactionalRules(faultMsg, aon.getRHS(), pol, pooled, p);
                case OR:
                    return ProcessNonTransactionalRules(faultMsg, aon.getLHS(), pol, pooled, p) || ProcessNonTransactionalRules(faultMsg, aon.getRHS(), pol, pooled, p);
                case NOT:
                    return !ProcessNonTransactionalRules(faultMsg, aon.getLHS(), pol, pooled, p);
            }
        } else if (rule instanceof SLARuleGeneric) {
            SLARuleGeneric x = (SLARuleGeneric) rule;
            if (x.getProcessAt() == RunAtLocation.FGSMS_SERVER) {
                Class c = null;
                try {
                    c = Thread.currentThread().getContextClassLoader().loadClass(x.getClassName());
                } catch (ClassNotFoundException ex) {
                    log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                    return false;
                }
                Object obj = null;
                if (c != null) {
                    try {
                        obj = c.newInstance();
                    } catch (InstantiationException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                        return false;
                    } catch (IllegalAccessException ex) {
                        log.log(Level.ERROR, SLACommon.getBundleString("ErrorSLAPluginRuleNCDF") + x.getClassName(), ex);
                        return false;
                    }
                    SLARuleInterface cast = null;
                    try {
                        cast = (SLARuleInterface) obj;
                    } catch (ClassCastException ex) {
                        log.log(Level.ERROR, String.format(SLACommon.getBundleString("ErrorSLAPluginRuleTypeCast"), x.getClassName(), SLARuleInterface.class.getCanonicalName()), ex);
                        return false;
                    }
                    try {
                        AtomicReference<String> msg = new AtomicReference<String>();
                        boolean CheckNonTransactionalRule = cast.CheckNonTransactionalRule(p, x.getParameterNameValue(), msg, pooled);
                        if (CheckNonTransactionalRule) {
                            faultMsg.append(msg.get());
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

    boolean sendTest(String email) {

        log.log(Level.INFO, "fgsms SLA Pusher test message");
        try {
            Properties props = SLACommon.LoadSLAPropertiesPooled();

            Session mailSession = Session.getDefaultInstance(props);
            Message simpleMessage = new MimeMessage(mailSession);
            InternetAddress from;
            if (Utility.stringIsNullOrEmpty(email)) {
                from = new InternetAddress(props.getProperty("defaultReplyAddress"));
            } else {
                from = new InternetAddress(email);
            }

            InternetAddress to = new InternetAddress(email);
            simpleMessage.setFrom(from);
            simpleMessage.setRecipient(Message.RecipientType.TO, to);
            simpleMessage.setSubject("fgsms SLA Processor Test Message");

            simpleMessage.setContent("This is a test indicating that your fgsms SLA Processor is configured correctly." + "<br>"
                    + "<a href=\"" + props.getProperty("fgsms.GUI.URL") + "\">fgsms</a>", "text/html; charset=ISO-8859-1");
            Transport.send(simpleMessage);
            return true;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error sending SLA alert email! " + ex.getLocalizedMessage());
        }
        return false;


    }
    private static String allitems = "All-Methods";

    public static long GrabMTBF(long x, String URL) {
        Connection con = null;
        try {
            con = Utility.getPerformanceDB_NONPOOLED_Connection();
            PreparedStatement comm = con.prepareStatement("select mtbf from agg2 where uri=? and soapaction=? and timerange=?");
            // comm.setString(1, ;
            comm.setString(1, URL);
            comm.setString(2, allitems);
            comm.setLong(3, x);
            ResultSet rs = comm.executeQuery();
            long ret = 0;
            if (rs.next()) {
                ret = rs.getLong(1);
            }
            rs.close();
            comm.close();
            con.close();
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
        try {
            con.close();
        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("ErrorClosingDB"), ex);
        }
        return 0;
    }

    public static long GrabMTBF(Duration x, String URL) {
        return GrabMTBF(RangeColumnName(x), URL);
    }

    public static long GrabFaultRate(long timeInMs, String URL) {
        Connection con = null;
        try {
            con = Utility.getPerformanceDBConnection();
            PreparedStatement comm = con.prepareStatement("select failure from agg2 where uri=? and soapaction=? and timerange=?");
            //      comm.setString(1,);
            comm.setString(1, URL);
            comm.setString(2, allitems);
            comm.setLong(3, timeInMs);
            ResultSet rs = comm.executeQuery();
            long ret = 0;
            if (rs.next()) {
                ret = rs.getLong(1);
            }
            rs.close();
            comm.close();
            con.close();
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("ErrorClosingDB"), ex);
        }

        return 0;
    }

    public static long GrabFaultRate(Duration x, String URL) {
        return GrabFaultRate(RangeColumnName(x), URL);
    }

    public static long GrabInvocationRate(long x, String URL) {
        Connection con = null;
        try {
            con = Utility.getPerformanceDBConnection();
            PreparedStatement comm = con.prepareStatement("select success from agg2 where URI=? and soapaction=? and timerange=?");

            comm.setString(1, URL);
            comm.setString(2, allitems);
            comm.setLong(3, x);
            ResultSet rs = comm.executeQuery();
            long ret = 0;
            if (rs.next()) {
                ret = rs.getLong(1);
            }
            rs.close();
            comm.close();
            con.close();
            return ret;
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("ErrorClosingDB"), ex);
        }
        return 0;
    }

    public static long GrabInvocationRate(Duration x, String URL) {
        return GrabInvocationRate(RangeColumnName(x), URL);
    }

    public static long RangeColumnName(Duration time) {
        return (Utility.durationToTimeInMS(time));
    }

    public static long GetDiskUsageOverTime(String pol, long timerange, boolean pooled) {
        Connection con = null;
        long ret = -1;
        try {
            
                con = Utility.getPerformanceDBConnection();
            
            long limit = System.currentTimeMillis() - timerange;
            PreparedStatement com = con.prepareStatement("select avg(diskKBs) from rawdatamachineprocess  where uri=? and utcdatetime > ?; ");
            com.setString(1, pol);
            com.setLong(2, limit);
            ResultSet rs = com.executeQuery();
            if (rs.next()) {
                ret = (long) rs.getDouble(1);
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.WARN, "trouble calculating avg disk used for " + pol, ex);
        }
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("ErrorClosingDB"), ex);
        }
        return ret;
    }

    public static long GetDiskUsageOverTime(String pol, Duration timerange, boolean pooled) {
        return GetDiskUsageOverTime(pol, Utility.durationToTimeInMS(timerange), pooled);
    }

    public static int GetCPUUsageOverTime(String pol, long timerange, boolean pooled) {
        Connection con = null;
        int ret = -1;
        try {
            
                con = Utility.getPerformanceDBConnection();
            
            long limit = System.currentTimeMillis() - timerange;
            PreparedStatement com = con.prepareStatement("select avg(percentcpu) from rawdatamachineprocess  where uri=? and utcdatetime > ?; ");
            com.setString(1, pol);
            com.setLong(2, limit);
            ResultSet rs = com.executeQuery();
            if (rs.next()) {
                ret = (int) rs.getDouble(1);
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.WARN, "trouble calculating avg cpu used for " + pol, ex);
        }
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("ErrorClosingDB"), ex);
        }
        return ret;
    }

    public static int GetCPUUsageOverTime(String pol, Duration timerange, boolean pooled) {
        return GetCPUUsageOverTime(pol, Utility.durationToTimeInMS(timerange), pooled);
    }

    public static long GetMemoryUsageOverTime(String pol, long timerange, boolean pooled) {
        Connection con = null;
        long ret = -1;
        try {
            
                con = Utility.getPerformanceDBConnection();
            
            long limit = System.currentTimeMillis() - timerange;
            PreparedStatement com = con.prepareStatement("select avg(memoryused) from rawdatamachineprocess  where uri=? and utcdatetime > ?; ");
            com.setString(1, pol);
            com.setLong(2, limit);
            ResultSet rs = com.executeQuery();
            if (rs.next()) {
                ret = (long) rs.getDouble(1);
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.WARN, "trouble calculating avg mmem used for " + pol, ex);
        }
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("ErrorClosingDB"), ex);
        }
        return ret;
    }

    public static long GetMemoryUsageOverTime(String pol, Duration timerange, boolean pooled) {
        return GetMemoryUsageOverTime(pol, Utility.durationToTimeInMS(timerange), pooled);
    }

    public static long GetNetworkUsageOverTime(String pol, Duration timerange, boolean pooled, String hostname, String domain) {
        Connection con = null;
        long ret = -1;
        try {
           
                con = Utility.getPerformanceDBConnection();
           
            long limit = System.currentTimeMillis() - Utility.durationToTimeInMS(timerange);
            PreparedStatement com = con.prepareStatement("select avg(sendkbs) from rawdatanic  "
                    + "where hostname=? and domainname=? and utcdatetime > ?; ");


            com.setString(1, hostname);
            com.setString(2, domain);
            com.setLong(3, limit);
            ResultSet rs = com.executeQuery();
            if (rs.next()) {
                ret = (long) rs.getDouble(1);
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.WARN, "trouble calculating avg network used for " + pol, ex);
        }
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("ErrorClosingDB"), ex);
        }
        return ret;
    }

    public static long GetLastKnownStatus(String URL, boolean pooled, AtomicReference<Boolean> success) {
        Connection con = null;
        
            con = Utility.getConfigurationDBConnection();
        
        try {
            PreparedStatement cmd = con.prepareStatement("select utcdatetime from status where uri=?");
            cmd.setString(1, URL);
            long ts = -1;
            ResultSet rs = cmd.executeQuery();
            if (rs.next()) {
                ts = rs.getLong(1);
            }
            con.close();
            if (ts > -1) {
                success.set(true);
            }
            rs.close();
            cmd.close();
            con.close();
            return ts;
        } catch (Exception ex) {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (Exception x) {
                log.log(Level.WARN, SLACommon.getBundleString("ErrorClosingDB"), x);
            }
            success.set(false);
            return -1;
        }
    }
}
