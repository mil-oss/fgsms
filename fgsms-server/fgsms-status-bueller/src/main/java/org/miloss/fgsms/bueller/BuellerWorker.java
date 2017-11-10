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
package org.miloss.fgsms.bueller;

import java.util.List;
import javax.resource.spi.work.Work;
import org.apache.log4j.Level;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;

/**
 *
 * @author AO
 */
public class BuellerWorker implements Work {

    private String url = "";
    private boolean pooled = false;
    private Bueller reference=null;
    public BuellerWorker(String connecturl, boolean pooled, Bueller ref) {
        this.pooled = pooled;
        url = connecturl;
        reference = ref;
    }

    @Override
    public void release() {
    }

    @Override
    public void run() {
        try {
            String s = reference.sendGetRequest(pooled, url,0);
            Boolean currenstatus = s.equalsIgnoreCase("ok");
            //log.log(Level.INFO, (i + 1) + "/" + urls.size() + " " + urls.get(i) + " status is " + s);
            if (!currenstatus) {
                List<String> alts = reference.GetAlternateUrls(url, pooled);
                for (int k = 0; k < alts.size(); k++) {
                    if (currenstatus) {
                        break;
                    }
                    s = reference.sendGetRequest(pooled, alts.get(k),0);
                    currenstatus = s.equalsIgnoreCase("ok");
                    reference.log.log(Level.INFO, url + " via alternate URL " + alts.get(k) + " status is " + s);
                }
            }
            reference.log.log(Level.INFO, url + " status is " + s);
            AuxHelper.TryUpdateStatus(currenstatus, url, s, pooled, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());

        } catch (Exception ex) {
            reference.log.log(Level.ERROR, "error setting status in config db for uri " + url + ex.getLocalizedMessage(), ex);
        }
    }
}
