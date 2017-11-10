/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular file,
 * then You may include the notice in a location (such as a LICENSE file in a
 * relevant directory) where a recipient would be likely to look for such a
 * notice.
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

import java.util.Queue;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.common.Logger;;

/**
 * Performs alerting functions when not running in Jboss (i.e. thread
 * pooling)
 *
 * @author AO
 */
public class AlertRunner implements Runnable {

     private Queue<AlertContainer> queue = null;

     AlertRunner(Queue<AlertContainer> q) {
	  queue = q;
     }
     static final Logger log = Logger.getLogger("fgsms.SLAProcessor");

     @Override
     public void run() {
	  try {
	       AlertContainer poll = queue.poll();
	       if (poll != null) {
		    SLACommon slac = new SLACommon();
		    do {
			 slac.DoAlerts(poll);
			 poll = queue.poll();
		    } while (poll != null);
	       }
	       SLAProcessorSingleton.setRunning(false);
	       log.log(org.apache.log4j.Level.INFO, SLACommon.getBundleString("InternalAlertingSingletonStop"));
	  } catch (Throwable t) {
	       log.log(org.apache.log4j.Level.FATAL, SLACommon.getBundleString("InternalAlertingSingletonStop"), t);
	       t.printStackTrace();
	  }
     }
}
