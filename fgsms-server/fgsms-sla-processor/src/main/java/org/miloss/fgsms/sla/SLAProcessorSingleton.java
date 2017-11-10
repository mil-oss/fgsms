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

import org.miloss.fgsms.plugins.sla.AlertContainer;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 * SLA Processor Singleton Provides basic thread pool
 * management and is only used when jboss's thread pooling isn't available
 *
 * @author AO
 */
public class SLAProcessorSingleton {

    private static boolean running = true;

    static synchronized void EnqueueAlert(AlertContainer alertContainer) {
        queue.add(alertContainer);
        log.log(Level.DEBUG, "Enqueue alert " + alertContainer.getSLAID());
        run();
    }

    /**
     * @return the running
     */
    public static boolean isRunning() {
        return running;
    }

    /**
     * @param aRunning the running to set
     */
    public static void setRunning(boolean aRunning) {
        running = aRunning;
    }

    private SLAProcessorSingleton() {
    }

    public static SLAProcessorSingleton getInstance() {
        return SLAProcessorSingletonHolder.INSTANCE;
    }

    private static class SLAProcessorSingletonHolder {

        private static final SLAProcessorSingleton INSTANCE = new SLAProcessorSingleton();
    }
    private static Thread t = null;
    private static final Queue<AlertContainer> queue = new ConcurrentLinkedQueue<AlertContainer>();
    static final Logger log = Logger.getLogger("fgsms.SLAProcessor");

    protected static int GetQueueSize() {
        if (queue == null) {
            return -1;
        }
        return queue.size();
    }

    protected static void run() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            if (!isRunning() || t == null) {
                log.log(Level.INFO, " == fgsms Alerting== launched new thread send out alerts.");
                try {
                    t = new Thread(new AlertRunner(queue), "SLA Alerting Thread " + UUID.randomUUID().toString());
                    t.start();
                    if (!t.isAlive()) {
                        throw new NullPointerException("completely unexpected error starting the SLA alerting thread");
                    }
                } catch (Exception ex) {
		     ex.printStackTrace();
                    log.log(Level.FATAL, "******************************************************* fgsms could not start the SLA Processor Alerting Thread. This is most likely due to server overloading, memory limits or hitting the maxium thread pool for the container. Please consider revising. "
                            + "Purging " + queue.size() + " from the outbound queue to prevent container overload. *********************************************", ex);
                    try {
                        queue.clear();
                    } catch (Exception ex2) {
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
