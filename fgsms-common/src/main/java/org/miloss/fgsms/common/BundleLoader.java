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

package org.miloss.fgsms.common;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.log4j.Level;


/**
 *
 * @author AO
 */
public class BundleLoader {

    private static ResourceBundle bundle = null;

    /**
     * loads a resource from the properties file
     *
     * @param key
     * @return
     */
    public static String getBundleString(String key) {
        SetupBundle();
        return bundle.getString(key);
    }
   private static Logger log = Logger.getLogger("org.miloss.fgsms.common/SLAResources");
    private static synchronized void SetupBundle() {
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle("org.miloss.fgsms.common/SLAResources", Locale.getDefault());
            } catch (Exception ex) {
                log.log(Level.FATAL, "unable to load the resource bundle for " + "org.miloss.fgsms.common/SLAResources" + Locale.getDefault().toString(), ex);
            }
        }
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle("org.miloss.fgsms.common/SLAResources");
            } catch (Exception ex) {
                log.log(Level.FATAL, "unable to load the resource bundle for " + "org.miloss.fgsms.common/SLAResources", ex);
            }
        }
    }
}
