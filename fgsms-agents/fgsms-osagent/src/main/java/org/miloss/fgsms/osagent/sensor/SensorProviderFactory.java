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

package org.miloss.fgsms.osagent.sensor;

import org.apache.log4j.*;
import org.miloss.fgsms.osagent.OSAgent;

/**
 *
 * @author AO
 */
public class SensorProviderFactory {

    public static ISensorProvider getInstance(String classname) {
        try {
            return (ISensorProvider) Class.forName(classname).newInstance();
        } catch (Exception ex) {
            OSAgent.log.log(Level.FATAL, "can't load class", ex);
        }
        return null;
    }
}
;