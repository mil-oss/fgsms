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
package org.miloss.fgsms.agents.smxviajmxagent;

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
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.apache.log4j.PropertyConfigurator;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.PropertyLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.BrokerData;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.sla.actions.SLAActionRestart;

/**
 * fgsms monitor for Apache ServiceMix message brokers via JMX
 * only
 *
 * @author AO
 *
 */
public class FgsmsSMXviaJMXAgent {

    static Logger log = Logger.getLogger("fgsms.SMXJMX");
    private boolean running = true;
    //~ Inner Classes -----------------------------------------------------------------------------
    private boolean done = false;

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
    private File file;
    private FileChannel channel;
    private FileLock lock;

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
    public static String[] urls = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new FgsmsSMXviaJMXAgent().Main(args);

    }

    private void Main(String[] args) {

        try {
            file = new File("smx.lck");
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
        } catch (Exception e) {
            // already locked
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file smx.lck needs to be deleted.");
            return;
        }
        if (lock == null) {
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file smx.lck needs to be deleted.");
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
        log.log(Level.INFO, "fgsms SMX JMX Agent startup...");
        Properties prop = PropertyLoader.loadProperties("fgsms.smxviajmxagent/connection");

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

    @SuppressWarnings("unchecked")
    public static void Fire(boolean pooled, final String url, final String modifiedurl) throws Exception {

        ServicePolicy p = null;
        if (pooled) {
            p = SLACommon.LoadPolicyPooled(modifiedurl);
        } else {
            p = SLACommon.LoadPolicyNotPooled(modifiedurl);
        }



        JMXServiceURL u = new JMXServiceURL(url);
        Map m = new HashMap();

        boolean hascred = false;
        boolean ok = true;
        String[] data = DBSettingsLoader.GetCredentials(pooled, modifiedurl);
        if (data != null && data.length >= 2) {
            String[] cred = new String[]{data[0], Utility.DE(data[1])};
            m.put(JMXConnector.CREDENTIALS, cred);
            hascred = true;
        }
        String status = "OK";
        JMXConnector c = null;
        try {
            if (hascred) {
                c = JMXConnectorFactory.connect(u, m);
            } else {
                c = JMXConnectorFactory.connect(u);
            }
            AuxHelper.TryUpdateStatus(true, modifiedurl, status, pooled, PolicyType.STATISTICAL, AuxHelper.UNSPECIFIED, SLACommon.GetHostName(), null);
        } catch (Exception ex) {
            status = ex.getMessage();
            if (status.equalsIgnoreCase("Expected String[2], got null")) {
                status = "No credentials were provided";
            }
            AuxHelper.TryUpdateStatus(false, modifiedurl, status, pooled, PolicyType.STATISTICAL, AuxHelper.UNSPECIFIED, SLACommon.GetHostName(), null);
            //server offline
            log.log(Level.WARN, "SMX Server offline " + url + " " + status);
            ok = false;
        }

        if (ok && c!=null) {
            log.log(Level.INFO, "SMX Server Online, updating ActiveMQ statistics" + url);
            MBeanServerConnection mBeanServerConnection = c.getMBeanServerConnection();
            // Integer i = mBeanServerConnection.getMBeanCount();
            // String[] d = mBeanServerConnection.getDomains();
            Set<ObjectName> queryNames = mBeanServerConnection.queryNames(ObjectName.WILDCARD, null);
            //"org.apache.qpid"
            Iterator<ObjectName> iterator = queryNames.iterator();
            String[] queueattribs = new String[]{
                "Name",
                "ConsumerCount",
                "DequeueCount",
                "DispatchCount",
                "EnqueueCount",
                "ExpiredCount",
                "ProducerCount",
                "QueueSize"
            };

            java.sql.Connection perfcon = null;
            PreparedStatement ps=null;
            
            try {
                if (pooled) {
                    perfcon = Utility.getPerformanceDBConnection();

                } else {
                    perfcon = Utility.getPerformanceDB_NONPOOLED_Connection();

                }

            

            ps = perfcon.prepareStatement("delete from brokerrawdata where host=?");
            ps.setString(1, modifiedurl);
            ps.executeUpdate();
            Map currenttopics = new HashMap();
            //<editor-fold defaultstate="collapsed" desc="do Active MQ queue/topic statistics">
            while (iterator.hasNext()) {
                ObjectName n = iterator.next();
                if (n != null && n.getCanonicalName() != null) {
                    if (n.getCanonicalName().startsWith("org.apache.activemq:BrokerName")) {
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
                            String ExchangeType = (bigname.contains("Type=Topic")) ? "Topic" : "Queue";


                            //long DequeueCount = 0;
                            // long DispatchCount = 0;
                            //       long EnqueueCount = 0;
                            long ExpiredCount = 0;
                            //   long ProducerCount = 0;





                            long ConsumerCount = 0;
                            long RecievedMessageCount = 0;
                            long MessageCount = 0;
                            String name = "";
                            long QueueDepth = 0;
                            if (attributes != null) {
                                for (int i2 = 0; i2 < attributes.size(); i2++) {
                                    //log.log(Level.INFO, attributes.get(i2).toString());
                                    /*
                                     *
                                     * attrib Name =
                                     * ActiveMQ.Advisory.Connection attrib
                                     * ConsumerCount = 0 attrib DequeueCount = 0
                                     * attrib DispatchCount = 0 messages sent
                                     * attrib EnqueueCount = 3 attrib
                                     * ExpiredCount = 0 attrib ProducerCount = 0
                                     * attrib QueueSize = 0
                                     */

                                    if (attributes.get(i2).toString().toLowerCase().startsWith("enqueuecount = ")) {
                                        RecievedMessageCount = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("enqueuecount = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("dispatchcount = ")) {
                                        MessageCount = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("dispatchcount = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("queuesize = ")) {
                                        QueueDepth = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("queuesize = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("consumercount = ")) {
                                        ConsumerCount = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("consumercount = ", "").trim());
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("name = ")) {
                                        name = attributes.get(i2).toString().split("=")[1].trim();
                                    }
                                    if (attributes.get(i2).toString().toLowerCase().startsWith("expiredcount = ")) {
                                        ExpiredCount = Long.parseLong(attributes.get(i2).toString().toLowerCase().replace("expiredcount = ", "").trim());
                                    }
                                }
                            }
                            //log.log(Level.INFO, "item name = " + name + " " + bigname);
                            if (!Utility.stringIsNullOrEmpty(name)) {
                                currenttopics.put(n.getCanonicalName(), QueueDepth);

                                UpdateData(modifiedurl, name, bigname, MessageCount, RecievedMessageCount, ConsumerCount, ConsumerCount,
                                        QueueDepth, ExchangeType, 0, 0, 0, ExpiredCount, perfcon);
                            }
                        }
                    }
                }
            }
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="do built in JBI components">
            int components = 0;
            String[] componentattribs = new String[]{"CurrentState", "Name", "MainType"};
            queryNames = mBeanServerConnection.queryNames(ObjectName.WILDCARD, null);
            iterator = queryNames.iterator();
            while (iterator.hasNext()) {

                ObjectName n = iterator.next();
                if (n != null && n.getCanonicalName() != null) {
                    {
                        AttributeList attributes = null;
                        try {
                            mBeanServerConnection = c.getMBeanServerConnection();
                            attributes = mBeanServerConnection.getAttributes(new ObjectName(n.getCanonicalName()), componentattribs);
                        } catch (Exception ex2) {
                            try {
                                mBeanServerConnection = c.getMBeanServerConnection();
                                attributes = mBeanServerConnection.getAttributes(new ObjectName(n.getCanonicalName()), componentattribs);
                            } catch (Exception ex3) {
                            }
                        }
                        if (attributes != null) {
                            String name = "";
                            String currentstate = "";
                            for (int i2 = 0; i2 < attributes.size(); i2++) {
                                String[] temp = attributes.get(i2).toString().split("=");
                                if (temp.length == 2) {
                                    if (temp[0].trim().equalsIgnoreCase("name")) {
                                        name = temp[1].trim();
                                    }
                                    if (temp[0].trim().equalsIgnoreCase("CurrentState")) {
                                        currentstate = temp[1].trim();
                                    }
                                }
                            }
                            if (!Utility.stringIsNullOrEmpty(name) && !Utility.stringIsNullOrEmpty(currentstate)) {
                                boolean running = currentstate.toLowerCase().contains("start") ? true : false;
                                AuxHelper.TryUpdateStatus(running, modifiedurl + "/" + name, currentstate, pooled, PolicyType.STATUS, AuxHelper.UNSPECIFIED, AuxHelper.UNSPECIFIED, modifiedurl);
                                if (!running) {
                                    StatusServicePolicy spol = null;
                                    if (pooled) {
                                        spol = (StatusServicePolicy) SLACommon.LoadPolicyPooled(modifiedurl + "/" + name);
                                    } else {
                                        spol = (StatusServicePolicy) SLACommon.LoadPolicyNotPooled(modifiedurl + "/" + name);
                                    }
                                    if (containsRestartSLA(spol.getServiceLevelAggrements())) {
                                        //TryRestart n;
                                        try {
                                            Object invoke = mBeanServerConnection.invoke(n, "Start", null, null);
                                        } catch (Exception ex) {
                                            log.log(Level.ERROR, "the attemp to restart the service identified by " + modifiedurl + "/" + name, ex);
                                        }
                                    }
                                }
                                components++;
                            }
                        }
                    }
                }
            }
            //</editor-fold>


            //<editor-fold defaultstate="collapsed" desc="do Apache Camel Route statistics">

            queryNames = mBeanServerConnection.queryNames(ObjectName.WILDCARD, null);
            iterator = queryNames.iterator();
            AddStatisticalDataRequestMsg req = new AddStatisticalDataRequestMsg();
            while (iterator.hasNext()) {
                ObjectName n = iterator.next();
                if (n != null && n.getCanonicalName() != null) {
                    if ((n.getDomain().equalsIgnoreCase("org.apache.camel")) && (n.getCanonicalName().contains("type=routes"))) {
                        {
                            log.log(Level.DEBUG, "Updating: " + n.getCanonicalName());

                            String bigname = n.getCanonicalName();
                            String ExchangeType = "Route";



                            //   long RecievedMessageCount = 0;
                            long MessageCount = 0;
                            long MessageDropCount = 0;
                            long ExchangesTotal = 0;
                            String name = "";
                            long QueueDepth = 0;
                            //     EndpointUri ExchangesFailed ExchangesTotal State
                            //    Description MaxProcessingTime MinProcessingTime MeanProcessingTime
                            //    TotalProcessingTime ExchangesCompleted
                            try {
                                name = (String) mBeanServerConnection.getAttribute(n, "EndpointUri");
                            } catch (Exception ex) {
                            }
                            try {
                                MessageDropCount = (Long) mBeanServerConnection.getAttribute(n, "ExchangesFailed");
                            } catch (Exception ex) {
                            }
                            try {
                                MessageCount = (Long) mBeanServerConnection.getAttribute(n, "ExchangesCompleted");
                            } catch (Exception ex) {
                            }
                            try {
                                ExchangesTotal = (Long) mBeanServerConnection.getAttribute(n, "ExchangesTotal");
                            } catch (Exception ex) {
                            }

                            if (!Utility.stringIsNullOrEmpty(name)) {
                                currenttopics.put(n.getCanonicalName(), QueueDepth);
                                req.getData().add(UpdateData(modifiedurl, name, bigname, MessageCount, ExchangesTotal, 0, 0,
                                        QueueDepth, ExchangeType, 0, 0, 0, MessageDropCount, perfcon));
                            }

                        }
                    }
                }
            }
            //</editor-fold>
            log.log(Level.INFO, "SMX Server Online, updating status for " + components + " SMX components" + url);
            
            req.setAgentType(AGENT);
            req.setBrokerURI(modifiedurl);
            req.setBrokerHostname(p.getMachineName());
            req.setDomain(p.getDomainName());
            req.setClassification(SLACommon.GetClassLevel(pooled));
            req.setOperationalStatus(ok);
            req.setOperationalStatusMessage(status);
            SLACommon.ProcessStatisticalSLARules(req, pooled);
            } catch (Exception ex) {
                log.log(Level.ERROR, "Cannot connect to fgsms performance db, monitoring is not possible, exiting...");
                return;
            } finally {
                DBUtils.safeClose(ps);
                DBUtils.safeClose(perfcon);
            }
        } else {
           // AuxHelper.TryUpdateStatus(false, modifiedurl, status, pooled, PolicyType.STATISTICAL, AuxHelper.UNSPECIFIED, SLACommon.GetHostName(), null);
        }


    }
    final static String AGENT = "org.miloss.fgsms.agents.amqp.jmx";

    private static BrokerData UpdateData(String url, String name, String bigname,
            long MessageCount, long RecievedMessageCount, long ConsumerCount, long ActiveConsumerCount,
            long QueueDepth, String ExchangeType, long bytesin, long bytesout, long bytesdrop,
            long MessageDropCount, Connection perf) {

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
            PreparedStatement comm=null;
            try {
                comm = perf.prepareStatement("Delete from brokerrawdata where host=? and namecol=? and canonicalname=?; "
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
                
            } catch (SQLException ex) {
                log.log(Level.WARN, "error saving broker data", ex);
            } finally {
                DBUtils.safeClose(comm);
            }

        }
        return bd;

    }

    private static boolean containsRestartSLA(ArrayOfSLA serviceLevelAggrements) {
        if (serviceLevelAggrements == null) {
            return false;
        }
        
        if (serviceLevelAggrements.getSLA().isEmpty()) {
            return false;
        }
        for (int i = 0; i < serviceLevelAggrements.getSLA().size(); i++) {
            if (serviceLevelAggrements.getSLA().get(i).getAction() == null) {
                continue;
            }
            for (int k = 0; k < serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().size(); k++) {
                if (serviceLevelAggrements.getSLA().get(i).getAction().getSLAAction().get(k).getImplementingClassName().equalsIgnoreCase(
                        SLAActionRestart.class.getCanonicalName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
