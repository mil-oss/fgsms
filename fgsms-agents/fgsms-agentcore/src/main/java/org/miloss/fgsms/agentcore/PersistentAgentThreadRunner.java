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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AO
 */
public class PersistentAgentThreadRunner implements Runnable {

    private PersistentStorage m = null;

    PersistentAgentThreadRunner(PersistentStorage instance) {
        m = instance;
    }

    @Override
    public void run() {
        try {
            if (m == null) {
                m = new PersistentStorage();
            }
            m.init(null);
        } catch (Exception ex) {
            Logger.getLogger(PersistentAgentThreadRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
