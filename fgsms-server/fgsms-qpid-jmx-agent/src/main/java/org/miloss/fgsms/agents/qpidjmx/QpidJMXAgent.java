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
package org.miloss.fgsms.agents.qpidjmx;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.PropertyLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.sla.SLACommon;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.apache.log4j.PropertyConfigurator;
import org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;

/**
 * fgsms monitor for Qpid/MRG AMQP message brokers via JMX only
 *
 * @author AO fgsms monitor for Qpid/MRG AMQP message brokers via JMX only. This
 * means this can only be used on java based implementations and runs on the
 * fgsms server
 */
public class QpidJMXAgent {

    static Logger log = Logger.getLogger("fgsms.QpidJMX");
    private boolean running = true;
    //~ Inner Classes -----------------------------------------------------------------------------
    private boolean done = false;
    private File file;
    private FileChannel channel;
    private FileLock lock;
    private String[] urls = null;
    static final String AGENT = "org.miloss.fgsms.agents.amqp.jmx";

     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new QpidJMXAgent().Main(args);

    }
    
    public class RunWhenShuttingDown extends Thread {

        public void run() {
            System.out.println("Control-C caught. Shutting down...");
            running = false;

            while (!done) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
            closeLock();
            deleteFile();
            if (urls != null) {
                for (int i = 0; i < urls.length; i++) {
                    AuxHelper.TryUpdateStatus(false, IpAddressUtility.modifyURL(urls[i], true), "Agent Stopped", false, PolicyType.STATISTICAL, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
                }
            }
        }
    }

    private void closeLock() {
        try {
            lock.release();
        } catch (Exception e) {
        }
        try {
            channel.close();
        } catch (Exception e) {
        }
    }

    private void deleteFile() {
        try {
            file.delete();
        } catch (Exception e) {
        }
    }

    private void Main(String[] args) {

        try {
            file = new File("pyjmx.lck");
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
        } catch (Exception e) {
            // already locked
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file pyjmx.lck needs to be deleted.");
            return;
        }
        if (lock == null) {
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file pyjmx.lck needs to be deleted.");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());
        PropertyConfigurator.configure("log4j.properties");
        long interval = 10000;
        if (args.length == 1) {
            try {
                interval = Long.parseLong(args[0]);
                if (interval < 10000) {
                    interval = 10000;
                }
            } catch (Exception ex) {
            }
        }
        log.log(Level.INFO, "fgsms Qpid JMX Agent startup...");
        Properties prop = PropertyLoader.loadProperties("fgsmsqpidjmx/connection");

        String temp = (String) prop.get("JMXServiceURL");
        if (temp.contains("|")) {
            urls = temp.split("\\|");
        } else {
            urls = new String[1];
            urls[0] = temp;
        }

        if ((urls == null || urls.length == 0)) {
            log.log(Level.FATAL, "no JMX url has been defined.");
            return;
        }

        long lastranat = 0;
        while (running) {
            if (lastranat < System.currentTimeMillis() - interval) {
                try {
                    lastranat = System.currentTimeMillis();

                    for (int aa = 0; aa < urls.length; aa++) {
                        String originalurl = urls[aa];
                        String url = IpAddressUtility.modifyURL(originalurl, true);
                        try {
                            Connection con = Utility.getConfigurationDB_NONPOOLED_Connection();
                            AuxHelper.CheckStatisticalPolicyAndCreate(url, con, false, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
                            con.close();
                        } catch (Exception ex) {
                        }
                        Fire(false, originalurl, url);
                    }
                } catch (Exception ex) {
                    log.log(Level.ERROR, null, ex);
                }
                log.log(Level.INFO, "Sleeping " + interval + "ms until next interation....");
            }
            if (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    log.log(Level.ERROR, null, ex);
                }
            }
            done = true;
        }
    }

    public static void Fire(final boolean pooled, final String url, final String modifiedurl) throws Exception {

        ServicePolicy p = null;
        if (pooled) {
            p = SLACommon.LoadPolicyPooled(modifiedurl);
        } else {
            p = SLACommon.LoadPolicyNotPooled(modifiedurl);
        }

        JMXServiceURL u = new JMXServiceURL(url);
        Map m = new HashMap();
        boolean ok = true;
        String[] data = DBSettingsLoader.GetCredentials(pooled, modifiedurl);
        if (data != null) {
            String[] cred = new String[]{data[0], Utility.DE(data[1])};
            m.put(JMXConnector.CREDENTIALS, cred);
        }
        String status = "OK";
        JMXConnector c = null;
        try {
            c = JMXConnectorFactory.connect(u, m);
            AuxHelper.TryUpdateStatus(true, modifiedurl, status, pooled, PolicyType.STATISTICAL, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        } catch (Exception ex) {
            AuxHelper.TryUpdateStatus(false, modifiedurl, ex.getLocalizedMessage(), pooled, PolicyType.STATISTICAL, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
            //server offline
            status = "Offline " + ex.getMessage();
            log.log(Level.WARN, "AMQP QPID Server offline " + url);
            ok = false;
        }

        if (ok && c!=null) {
            log.log(Level.INFO, "AMQP QPID Server Online, updating statistics" + url);
            MBeanServerConnection mBeanServerConnection = c.getMBeanServerConnection();
            // Integer i = mBeanServerConnection.getMBeanCount();
            // String[] d = mBeanServerConnection.getDomains();
            Set<ObjectName> queryNames = mBeanServerConnection.queryNames(ObjectName.WILDCARD, null);
            //"org.apache.qpid"
            Iterator<ObjectName> iterator = queryNames.iterator();
            String[] queueattribs = new String[]{"QueueDepth",
                "Name",
                "MessageCount",
                "ReceivedMessageCount",
                "ActiveConsumerCount",
                //   "AutoDelete",
                //     "Capacity",
                "ConsumerCount",
                //    "Durable",
                //   "Exclusive",
                //  "FlowOverfull",
                // "FlowResumeCapacity",
                // "MaximumMessageAge",
                //"MaximumMessageCount",
                //"MaximumMessageSize",
                //"MaximumQueueDepth",
                "ExchangeType"//,
            //"Description"
            };

            java.sql.Connection perfcon = null;

            try {
                if (pooled) {
                    perfcon = Utility.getPerformanceDBConnection();

                } else {
                    perfcon = Utility.getPerformanceDB_NONPOOLED_Connection();

                }

            } catch (Exception ex) {
                log.log(Level.ERROR, "Cannot connect to fgsms performance db, monitoring is not possible, exiting...");
                return;
            }

            PreparedStatement ps = perfcon.prepareStatement("delete from brokerrawdata where host=?");
            ps.setString(1, modifiedurl);
            ps.executeUpdate();
            ps.close();
            AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
            Map currenttopics = new HashMap();
            while (iterator.hasNext()) {

                ObjectName n = iterator.next();
                if (n != null && n.getCanonicalName() != null) {
                    if (n.getCanonicalName().startsWith("org.apache.qpid")) {
                        {
                            // log.log(Level.INFO, "###################################################");
                            AttributeList attributes = null;
                            try {
                                attributes = mBeanServerConnection.getAttributes(new ObjectName(n.getCanonicalName()), queueattribs);
                            } catch (Exception ex2) {
                                try {
                                    mBeanServerConnection = c.getMBeanServerConnection();
                                    attributes = mBeanServerConnection.getAttributes(new ObjectName(n.getCanonicalName()), queueattribs);
                                } catch (Exception ex3) {
                                }
                            }

                            log.log(Level.DEBUG, "Updating: " + n.getCanonicalName());

                            String bigname = n.getCanonicalName();
                            String ExchangeType = "queue";
                            long ConsumerCount = 0;
                            long ActiveConsumerCount = 0;
                            long RecievedMessageCount = 0;
                            long MessageCount = 0;
                            String name = "";
                            long QueueDepth = 0;
                            if (attributes != null) {
                                for (int i2 = 0; i2 < attributes.size(); i2++) {
                                    //log.log(Level.INFO, attributes.get(i2).toString());
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("activeconsumercount = ")) {
                                        ActiveConsumerCount = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("activeconsumercount = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("receivedmessagecount = ")) {
                                        RecievedMessageCount = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("receivedmessagecount = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("messagecount = ")) {
                                        MessageCount = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("messagecount = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("queuedepth = ")) {
                                        QueueDepth = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("queuedepth = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("consumercount = ")) {
                                        ConsumerCount = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("consumercount = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("name = ")) {
                                        name = attributes.get(i2).toString().split("=")[1].trim();
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("exchangetype = ")) {
                                        ExchangeType = attributes.get(i2).toString().split("=")[1].trim();
                                    }
                                }
                            }
                            //log.log(Level.INFO, "item name = " + name + " " + bigname);
                            if (!Utility.stringIsNullOrEmpty(name)) {
                                currenttopics.put(n.getCanonicalName(), QueueDepth);

                                req.getData().add(updateData(modifiedurl, name, bigname, MessageCount, RecievedMessageCount, ConsumerCount, ActiveConsumerCount,
                                        QueueDepth, ExchangeType, 0, 0, 0, 0, perfcon));
                                //  if (slarules) {
                                //       SLACommon.ProcessStatisticalSLARules(p, modifiedurl, name, QueueDepth, pooled);
                                //   }

                            }
                        }
                    }

                }

            }

            // SLACommon.ProcessStatisticalSLARules((modifiedurl, currenttopics, pooled);
            req.setAgentType(AGENT);
            req.setBrokerURI(modifiedurl);
            req.setBrokerHostname(p.getMachineName());
            req.setDomain(p.getDomainName());
            req.setClassification(SLACommon.GetClassLevel(pooled));
            req.setOperationalStatus(ok);
            req.setOperationalStatusMessage(status);
            SLACommon.ProcessStatisticalSLARules(req, pooled);
            c.close();
            perfcon.close();
        }

    }

    private static BrokerData updateData(final String url, final String name, final String bigname,
            final  long MessageCount,  final long RecievedMessageCount,  final long ConsumerCount,  final long ActiveConsumerCount,
            final  long QueueDepth,  final String ExchangeType,  final long bytesin, final  long bytesout, final  long bytesdrop,
            final  long MessageDropCount, final  Connection perf) {
        BrokerData bd = new BrokerData();
        bd.setActiveConsumers(QueueDepth);
        bd.setQueueOrTopicCanonicalName(bigname);
        bd.setQueueOrTopicName(name);
        bd.setMessagesIn(RecievedMessageCount);
        bd.setMessagesDropped(MessageDropCount);
        bd.setDepth(QueueDepth);
        bd.setActiveConsumers(ActiveConsumerCount);
        bd.setBytesDropped(bytesdrop);
        bd.setBytesIn(bytesin);
        bd.setBytesOut(bytesout);
        bd.setItemType(ExchangeType);
        bd.setMessagesOut(MessageCount);
        bd.setTotalConsumers(ConsumerCount);
        if (!Utility.stringIsNullOrEmpty(name)) {
            try {
                PreparedStatement comm = perf.prepareStatement("Delete from brokerrawdata where host=? and namecol=? and canonicalname=?; "
                        + "INSERT INTO brokerrawdata (host, utcdatetime, namecol, canonicalname, messagecount,recievedmessagecount, consumercount, "
                        + "activeconsumercount, queuedepth, typecol, agenttype, messagedropcount, bytesdropcount, bytesin, bytesout) "
                        + "VALUES (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                        + "INSERT INTO brokerhistory (host, utcdatetime, namecol,canonicalname, messagecount,recievedmessagecount, consumercount, "
                        + "activeconsumercount, queuedepth, typecol, agenttype, messagedropcount, bytesdropcount, bytesin, bytesout) "
                        + "VALUES (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                comm.setString(1, url);
                comm.setString(2, name);
                comm.setString(3, bigname);
                comm.setString(4, url);
                comm.setLong(5, System.currentTimeMillis());
                comm.setString(6, name);
                comm.setString(7, bigname);
                comm.setLong(8, MessageCount);
                comm.setLong(9, RecievedMessageCount);
                comm.setLong(10, ConsumerCount);
                comm.setLong(11, ActiveConsumerCount);
                comm.setLong(12, QueueDepth);
                comm.setString(13, ExchangeType);
                comm.setString(14, AGENT);
                comm.setLong(15, MessageDropCount);
                comm.setLong(16, bytesdrop);
                comm.setLong(17, bytesin);
                comm.setLong(18, bytesout);

                comm.setString(19, url);
                comm.setLong(20, System.currentTimeMillis());
                comm.setString(21, name);
                comm.setString(22, bigname);
                comm.setLong(23, MessageCount);
                comm.setLong(24, RecievedMessageCount);
                comm.setLong(25, ConsumerCount);
                comm.setLong(26, ActiveConsumerCount);
                comm.setLong(27, QueueDepth);
                comm.setString(28, ExchangeType);
                comm.setString(29, AGENT);
                comm.setLong(30, MessageDropCount);
                comm.setLong(31, bytesdrop);
                comm.setLong(32, bytesin);
                comm.setLong(33, bytesout);
                comm.execute();
                comm.close();
            } catch (SQLException ex) {
                log.log(Level.WARN, "error saving broker data", ex);
            }

        }

        return bd;
    }

   
}
