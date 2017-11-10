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
package org.miloss.fgsms.services.rs.impl;

import java.util.Queue;
import org.apache.log4j.Level;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.ReportDefinition;
import org.miloss.fgsms.common.Logger;;

/**
 * Performs alerting functions when not running in Jboss (i.e. thread
 * pooling)
 *
 * @author AO
 */
public class RSRunner implements Runnable {

    private Queue<ReportDefinitionExtension> queue = null;

    RSRunner(Queue<ReportDefinitionExtension> q) {
        queue = q;
    }
    static final Logger log = Logger.getLogger("fgsms.RSProcessor");

    @Override
    public void run() {

        ReportDefinitionExtension poll = queue.poll();
        while (poll != null) {
            try {
                FgsmsReportGenerator r = new FgsmsReportGenerator();
                String id = r.GenerateReport(poll.def, poll.pooled);
                r.ProcessAlerts(poll.def, id, poll.pooled);
            } catch (Exception e) {
                log.log(Level.ERROR, "Error trapped running the reporting job", e);
            }
            poll = queue.poll();

        }

        RSProcessorSingleton.running = false;
        log.log(org.apache.log4j.Level.INFO, "fgsms RSProcessor Alerting thread is terminating, queue is empty.");
    }
}
