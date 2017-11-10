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

package org.miloss.fgsms.auxsrv;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.bueller.Bueller;
import org.quartz.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 **Quartz Job that kicks off the Status Bueller
 *
 * @author AO
 */
public class BuellerScheduler implements StatefulJob {

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            org.miloss.fgsms.bueller.Bueller m = new Bueller();
            m.Fire(true);
            AuxHelper.TryUpdateStatus(true, "urn:fgsms:Bueller:" + SLACommon.GetHostName(), "OK", true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        } catch (Exception ex) {
            Logger.getLogger(BuellerScheduler.class.getName()).log(Level.SEVERE, null, ex);
            AuxHelper.TryUpdateStatus(false, "urn:fgsms:Bueller:" + SLACommon.GetHostName(), ex.getMessage(), true, PolicyType.STATUS, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
        }

    }
}
