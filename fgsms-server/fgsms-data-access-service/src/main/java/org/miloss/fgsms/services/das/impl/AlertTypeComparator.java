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

package org.miloss.fgsms.services.das.impl;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author AO
 */
public class AlertTypeComparator implements Comparator, Serializable {

    public int compare(Object o1, Object o2) {
        AlertHelper x = (AlertHelper) o1;
        AlertHelper y = (AlertHelper) o2;
        if (x.timestamp > y.timestamp) {
            return -1;
        }
        if (x.timestamp == y.timestamp) {
            return 0;
        }
        return 1;
    }
}
