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

import javax.resource.spi.work.Work;
import org.miloss.fgsms.plugins.sla.AlertContainer;

/**
 * Provides a wrapper that enables Jboss's WorkManager to handle fgsms's
 * AlertContainer class
 *
 * @author AO
 */
public class SLAWorker implements Work {

    public SLAWorker(AlertContainer alert) {
        a = alert;
    }
    AlertContainer a = null;

    @Override
    public void release() {
    }

    @Override
    public void run() {
        if (a == null) {
            return;
        }
        SLACommon slac = new SLACommon();
        slac.DoAlerts(a);
    }
}
