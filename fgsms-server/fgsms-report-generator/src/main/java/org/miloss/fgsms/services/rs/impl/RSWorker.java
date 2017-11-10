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

package org.miloss.fgsms.services.rs.impl;

import javax.resource.spi.work.Work;

/**
 *
 * @author AO
 */
public class RSWorker implements Work {

    public RSWorker(ReportDefinitionExtension alert) {
        a = alert;
    }
    ReportDefinitionExtension a=null;

    @Override
    public void release() {
        
    }

    @Override
    public void run() {
        if (a==null)
            return;
        FgsmsReportGenerator r = new FgsmsReportGenerator();
        try {
            String s=r.GenerateReport(a.def, a.pooled);
               r.ProcessAlerts(a.def, s,a.pooled);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     
    }
}
