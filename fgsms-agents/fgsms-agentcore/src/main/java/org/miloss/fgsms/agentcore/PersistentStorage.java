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

package org.miloss.fgsms.agentcore;

import org.miloss.fgsms.common.Utility;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.services.interfaces.common.*;
import org.miloss.fgsms.services.interfaces.datacollector.*;

import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.apache.log4j.PropertyConfigurator;
import org.miloss.fgsms.common.Constants;

/**
 *  The persistent storage agent Used for transactional web service
 * logs when they cannot be transmitted and are stored on disk This will
 * periodically ready from disk and attempt to send back a single transaction
 * log, if successful, all remaining items are enqueued.
 *
 * @author AO
 */
public class PersistentStorage {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Usage: java -jar fgsms.AgentCore.jar");
        System.out.println("This will monitor the configured or provided directory for encrypted files written by a Java based fgsms agent that couldn't be delievered.\n"
                + "These messages are read and then attempted to be delivered periodically. If delivered, they are removed from disk.This only applies to \n"
                + "transactions agents which cover web services. Configuration settings are within this jar file.");
        new PersistentStorage().init(args);
    }
    static PersistentStorage instance = null;

    public static void start(String[] args) {
        instance = new PersistentStorage(args);
        t = new Thread(new PersistentAgentThreadRunner(instance));
        t.start();
    }
    private static Thread t = null;

    public static void stop(String[] args) {
        if (t != null && t.isAlive() && instance != null) {
            instance.running = false;
            try {
                t.join();
            } catch (InterruptedException ex) {
                log.log(Level.ERROR, null, ex);
            }
        }
    }
    static Logger log = Logger.getLogger("fgsms.PersistentStorageAgent");
    private boolean running = true;
    private File file;
    private FileChannel channel;
    private FileLock lock;
    private boolean sendstatus = true;

    public PersistentStorage(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("nostatus")) {
            sendstatus = false;
        }
    }
    
    public PersistentStorage() {
    }

    public void init(String[] args) throws InterruptedException {

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
            running = false;
            return;
        }

        if (args != null && args.length == 1 && args[0].equalsIgnoreCase("noloop")) {
            noloop = true;
        }



        Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());
        PropertyConfigurator.configure("log4j.properties");
        long lastRanAt = 0;
        long interval = 5 * 60 * 1000;        //5 min
        long lastUpdateAt = 0;
        long updatestatusinterval = 5 * 60 * 1000;  //5 minues

        log.log(Level.INFO, "fgsms PersistentStorageAgent startup");
        while (running) {
            {

                //check last time purged
                if (System.currentTimeMillis() - lastRanAt > interval) {


                    try {
                        //  log.log(Level.INFO, "fgsms Data Pruner is purging old records...");
                        boolean workdone = doWork(false);
                    } catch (ConfigurationException ex) {
                        log.log(Level.WARN, null, ex);
                        running = false;
                        done = true;
                        //unrecoverable error
                        return; 

                    } catch (Exception ex) {
                        try {
                            if (sendstatus) {
                                StatusHelper.tryUpdateStatus(false, "urn:" + Utility.getHostName() + ":PersistentStorageAgent" + Utility.getHostName(), ex.getMessage(), pooled, PolicyType.STATUS, Constants.UNSPECIFIED, Utility.getHostName());
                            }
                        } catch (Exception x) {
                            log.log(Level.WARN, "couldn't send status update", x);
                        }
                    }
                    log.log(Level.INFO, "fgsms PersistentStorageAgent sleeping for " + interval + "ms");
                    lastRanAt = System.currentTimeMillis();
                }
                //moved below so that the status is only updated if we are configured for persistent storage, thus preventing the stale data warning
                if (System.currentTimeMillis() - lastUpdateAt > updatestatusinterval) {
                    try {
                        if (sendstatus) {
                            StatusHelper.tryUpdateStatus(true, "urn:" + Utility.getHostName() + ":PersistentStorageAgent", "OK", pooled, PolicyType.STATUS, Constants.UNSPECIFIED, Utility.getHostName());
                        }
                    } catch (Exception ex) {
                        log.log(Level.WARN, "couldn't send status update", ex);
                    }
                    lastUpdateAt = System.currentTimeMillis();

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
     * return true if successful, false means all endpoints are down, lets delay
     * for a bit then try again
     *
     * @param pooled
     * @return
     */
    public static boolean doWork(boolean pooled) throws ConfigurationException {

        ConfigLoader cfg = new ConfigLoader();

        String v = cfg.getOfflinestorage();

        if (cfg.behavior == null || cfg.behavior != ConfigLoader.UnavailableBehavior.HOLDPERSIST) {

            log.log(Level.ERROR, "the current config (fgsms-agent.properties) is not configured for offline storage");
            throw new ConfigurationException("not configured for persistant storage");
        }
        DataPusher.EnsureFolderExists(v);
        File f = new File(v);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException ex) {
            }
            if (!f.exists()) {
                log.log(Level.FATAL, "the storage directory does not exist and I can't create it. Check the OS file permissions. " + v);
            }
            throw new ConfigurationException("directory does not exist");
        }
        //load one record
        AddMoreData CheckPersistStore = DataPusher.CheckPersistStore();
        if (CheckPersistStore == null) {
            return false;
        }
        /*URL dcsurl = Thread.currentThread().getContextClassLoader().getResource("META-INF/" + org.miloss.fgsms.common.Constants.DCS_META);

        if (dcsurl == null) {
            dcsurl = Thread.currentThread().getContextClassLoader().getResource("/META-INF/" + org.miloss.fgsms.common.Constants.PCS_META);
        }*/
        DataPusher dp = new DataPusher();
        
        DataPusher.Init();
        DataCollectorService dcsservice = new DataCollectorService();
        DCS dcsport = dcsservice.getDCSPort();
        BindingProvider bp = (BindingProvider) dcsport;
        Map<String, Object> context = bp.getRequestContext();
        if (cfg.mode_ == org.miloss.fgsms.common.Constants.AuthMode.UsernamePassword) {
            context.put(BindingProvider.USERNAME_PROPERTY, cfg.username);
            context.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.password));
        }
        if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStore")) && !Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStorePassword"))) {
            context.put("javax.net.ssl.trustStorePassword", Utility.DE(cfg.getJavaxtruststorepass()));
            context.put("javax.net.ssl.trustStore", Utility.DE(cfg.getJavaxtruststore()));
        }
        try {
            if (cfg.mode_ == org.miloss.fgsms.common.Constants.AuthMode.PKI) {
                if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStore")) && !Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStorePassword"))) {
                    context.put("javax.net.ssl.keyStorePassword", Utility.DE(cfg.getJavaxkeystorepass()));
                    context.put("javax.net.ssl.keyStore", Utility.DE(cfg.getJavaxkeystore()));
                }
            }

        } catch (Exception ex) {
            log.log(Level.FATAL, "error caught when referencing (get or set) System.properties for SSL communication. Check to ensure that this is enabled in your JAAS managemer", ex);
        }
        //ApacheCxfSslHelper.DoCXF(cfg.pcsport, cfg);

        boolean success = false;
        for (int i = 0; i < cfg.DCS_URLS.size(); i++) {
            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.DCS_URLS.get(i));//
            try {
                dcsport.addMoreData(CheckPersistStore.getReq());
                success = true;
                break;
            } catch (Exception ex) {
                log.log(Level.WARN, "couldn't transmit the DCS message to " + cfg.DCS_URLS.get(i));
            }
        }
        if (!success) {
            DataPusher.StorePersist(CheckPersistStore);
            return false;
        } else {
            CheckPersistStore = DataPusher.CheckPersistStore();
            while (CheckPersistStore != null) {
                for (int i = 0; i < CheckPersistStore.getReq().size(); i++) {
                    MessageProcessor.getSingletonObject().processPreppedMessage(CheckPersistStore.getReq().get(i));
                }
                CheckPersistStore = DataPusher.CheckPersistStore();
            }
            return true;
        }
        //try to send here
        //if successful, delete that file from disk
        //, read everything and add to the queue
        //else wait and try again
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
                try {
                    if (sendstatus) {
                        StatusHelper.tryUpdateStatus(false, "urn:" + Utility.getHostName() + ":PersistentStorageAgent", "Stopped", false, PolicyType.STATUS, Constants.UNSPECIFIED, Utility.getHostName());
                    }
                } catch (Exception ex) {
                    log.log(Level.WARN, "couldn't send status update", ex);
                }

            }
        }
    }
    private boolean done = false;
    private static boolean noloop = false;
}
