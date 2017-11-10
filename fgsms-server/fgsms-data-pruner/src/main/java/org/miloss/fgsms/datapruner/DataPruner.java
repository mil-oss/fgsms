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
package org.miloss.fgsms.datapruner;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.GregorianCalendar;
import org.miloss.fgsms.common.Utility;

import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.apache.log4j.PropertyConfigurator;
import org.miloss.fgsms.services.interfaces.common.PolicyType;

/**
 * Removes old records per the service policy's data retention setting
 *
 * @author AO
 */
public class DataPruner {

    static Logger log = Logger.getLogger("fgsms.DataPruner");
    private boolean running = true;
    private File file;
    private FileChannel channel;
    private FileLock lock;
    private boolean done = false;
    public static boolean noloop = false;

    /**
     * Data Pruner, this utility process for fgsms will remove stale data from
     * the performance database based on service policy
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        new DataPruner().Main(args);

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
            if (!noloop) {
                AuxHelper.TryUpdateStatus(false, "urn:fgsms:DataPruner:" + Utility.getHostName(), "Stopped", false, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
            }
        }
    }

    private void Main(String[] args) throws InterruptedException {
        boolean pooled = false;
        try {
            file = new File("dp.lck");
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
        } catch (Exception e) {
            // already locked
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file dp.lck needs to be deleted.");
            return;
        }
        if (lock == null) {
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file dp.lck needs to be deleted.");
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("noloop")) {
            noloop = true;
        }

        Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());
        PropertyConfigurator.configure("log4j.properties");
        long lastRanAt = 0;
        long interval = 24 * 60 * 60 * 1000;        //24 hours
        long lastUpdateAt = 0;
        long updatestatusinterval = 5 * 60 * 1000;  //5 minues

        log.log(Level.INFO, "fgsms Data Pruner startup");
        while (running) {

            {
                if (System.currentTimeMillis() - lastUpdateAt > updatestatusinterval) {
                    AuxHelper.TryUpdateStatus(true, "urn:fgsms:DataPruner:" + Utility.getHostName(), "OK", pooled, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
                    lastUpdateAt = System.currentTimeMillis();

                }
                //check last time purged
                if (System.currentTimeMillis() - lastRanAt > interval) {
                    AuxHelper.TryUpdateStatus(true, "urn:fgsms:DataPruner:" + Utility.getHostName(), "OK", pooled, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
                    log.log(Level.INFO, "fgsms Data Pruner is purging old records...");
                    Purge(false);
                    log.log(Level.INFO, "fgsms Data Pruner sleeping for " + interval + "ms");
                    lastRanAt = System.currentTimeMillis();
                }
            }
            if (noloop) {
                running = false;
            }
            if (running) {
                Thread.sleep(5000);
            }

        }
        done = true;
    }

    /**
     * this is called from the quartz job or from void main
     *
     * @param pooled
     */
    public void Purge(boolean pooled) {
        try {
            GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
            long now = cal.getTimeInMillis();
            Connection ConfigCon = null;
            Connection PerfCon = null;
            if (pooled) {
                ConfigCon = Utility.getConfigurationDBConnection();
                PerfCon = Utility.getPerformanceDBConnection();
            } else {
                ConfigCon = Utility.getConfigurationDB_NONPOOLED_Connection();
                PerfCon = Utility.getPerformanceDB_NONPOOLED_Connection();
            }
            PreparedStatement com = ConfigCon.prepareStatement("select uri, datattl, hostname from servicepolicies;");
            ResultSet rs = com.executeQuery();
            while (rs.next() && running) {
                long ttl = now - rs.getLong("datattl");
                PreparedStatement del = PerfCon.prepareStatement(
                        "delete from rawdata where"
                        + "(utcdatetime < ?) and (uri = ?);"
                        + "delete from brokerhistory where"
                        + "(utcdatetime < ?) and (host = ?);"
                        + "delete from availability where"
                        + "(utcdatetime < ?) and (uri = ?);"
                        + "delete from slaviolations where"
                        + "(utcdatetime < ?) and (uri = ?);"
                        + "delete from rawdatadrives where"
                        + "(utcdatetime < ?) and (hostname = ?);"
                        + "delete from rawdatamachineprocess where"
                        + "(utcdatetime < ?) and (uri = ?);"
                        + "delete from rawdatanic where"
                        + "(utcdatetime < ?) and (uri = ?);"
                        + "delete from rawdatamachinesensor where"
                        + "(utcdatetime < ?) and (uri = ?);");
                del.setLong(1, ttl);
                del.setString(2, rs.getString("uri"));
                del.setLong(3, ttl);
                del.setString(4, rs.getString("uri"));
                del.setLong(5, ttl);
                del.setString(6, rs.getString("uri"));
                del.setLong(7, ttl);
                del.setString(8, rs.getString("uri"));

                del.setLong(9, ttl);
                //unspecified hostnames shouldn't be an issue
                del.setString(10, rs.getString("hostname"));
                del.setLong(11, ttl);
                del.setString(12, rs.getString("uri"));

                del.setLong(13, ttl);
                del.setString(14, rs.getString("uri"));
                del.setLong(15, ttl);
                del.setString(16, rs.getString("uri"));

                int i = del.executeUpdate();
                if (i == 0) {
                    log.log(Level.DEBUG, i + " records removed for service " + rs.getString("uri"));
                } else {
                    log.log(Level.INFO, i + " records removed for service " + rs.getString("uri"));
                }
                del.close();
            }
            rs.close();
            com.close();
            PerfCon.close();

            ConfigCon.close();
            //run once a day

        } catch (Exception ex) {
            System.console().writer().println("caught error!" + ex.getLocalizedMessage());
            log.log(Level.ERROR, null, ex);

            AuxHelper.TryUpdateStatus(false, "urn:fgsms:DataPruner:" + Utility.getHostName(), "ERROR", pooled, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        }
    }
}
