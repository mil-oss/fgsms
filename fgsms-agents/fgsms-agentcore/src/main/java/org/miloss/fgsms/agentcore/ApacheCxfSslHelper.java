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

import org.miloss.fgsms.common.Constants;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;

/**
 * Detects the presence of an Apache CXF stack, if present, adds SSL
 * context information
 *
 * @author AO
 */
public class ApacheCxfSslHelper {

    private static final Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    public static void doCXF(Object webserviceclient, ConfigLoader cfg) {

        try {
            //first, check to see if the cxf stack is present
            Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass("org.apache.cxf.transport.http.HTTPConduit");
            if (loadClass == null) {
                return;
            }
            Class<?> loadClass2 = Thread.currentThread().getContextClassLoader().loadClass("org.apache.cxf.common.i18n.UncheckedException");
            if (loadClass2 == null) {
                return;
            }
            log.log(Level.INFO, "CXF stack detected, adding SSL information");
            //ok it's available
            ApacheCxfSSlHelperGo.doCXF(webserviceclient, cfg);
        } catch (ClassNotFoundException ex) {
            log.log(Level.DEBUG, "CXF stack not found, most likely this is ignorable", ex);
        } catch (NoClassDefFoundError ex) {
            log.log(Level.DEBUG, "CXF stack not found, most likely this is ignorable", ex);

        }
    }
}
