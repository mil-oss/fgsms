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
package org.miloss.fgsms.agents.qpidpy;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.apache.log4j.PropertyConfigurator;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.agentcore.StatusHelper;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;

/**
 * fgsms monitor for Qpid/MRG AMQP brokers based on C++. 
 *
 * @author AO fgsms monitor for Qpid/MRG AMQP brokers based on C++. This
 * command runs a customized version of qpid-stats, parses the output and then
 * records it in the fgsms database.
 */
public class Mainv2 {

    static Logger log = Logger.getLogger("fgsms.QpidPython");
    private boolean running = true;
    private boolean done = false;
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

    private void Run(String[] args) throws ConfigurationException {

        try {
            file = new File("amqp.lck");
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
        } catch (Exception e) {
            // already locked
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file amqp.lck needs to be deleted.");
            return;
        }
        if (lock == null) {
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file amqp.lck needs to be deleted.");
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


        String url = IpAddressUtility.modifyURL("amqp://localhost", false);
        qpidcmdws q = new qpidcmdws();
        while (running) {
            try {
                //create/fetch the policy
                //org.miloss.fgsms.agentcore.PolicyFetch.TryFetchPolicy(url, PolicyType.STATISTICAL, "unspecified", Utility.getHostName());

//                AuxHelper.CheckStatisticalPolicyAndCreate(IpAddressUtility.modifyURL("amqp://localhost", false), config, false, AuxHelper.UNSPECIFIED, SLACommon.getHostName());

                q.Fire(url);



                //SLACommon.ProcessStatisticalSLARules2(IpAddressUtility.modifyURL("amqp://localhost", false), currentitems, false);

                //    org.miloss.fgsms.agentcore.StatusHelperViaWebService.tryUpdateStatus(true, url, "OK", false, PolicyType.STATISTICAL, AuxHelper.UNSPECIFIED, Utility.getHostName());


            } catch (Exception ex) {
                log.log(Level.ERROR, ex);
                // org.miloss.fgsms.agentcore.StatusHelperViaWebService.tryUpdateStatus(true, url, "OK", false, PolicyType.STATISTICAL, AuxHelper.UNSPECIFIED, Utility.getHostName());
            }
            if (running) {
                try {
                    log.log(Level.INFO, "Sleeping " + interval + "ms until next interation....");
                    Thread.sleep(interval);
                } catch (InterruptedException ex) {
                    log.log(Level.ERROR, null, ex);
                }
            }

        }
        done = true;

    }

    //~ Inner Classes -----------------------------------------------------------------------------
    public class RunWhenShuttingDown extends Thread {

        public void run() {
            System.out.println("Control-C caught. Shutting down...");
            //keepOn = false;
            running = false;
            while (!done) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
            closeLock();
            deleteFile();
            try {
                boolean TryUpdateStatus = StatusHelper.tryUpdateStatus(false, IpAddressUtility.modifyURL("amqp://localhost", false), "Agent Stopped", false, PolicyType.STATISTICAL, null, Utility.getHostName());
                if (!TryUpdateStatus) {
                    System.out.println("unable to send the last statue update, see log output for details ");
                }
                //TryUpdateStatus(false, null, null);
            } catch (ConfigurationException ex) {
                System.out.println("unable to send the last statue update " + ex.getMessage());
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ConfigurationException {
        new Mainv2().Run(args);

    }
}
