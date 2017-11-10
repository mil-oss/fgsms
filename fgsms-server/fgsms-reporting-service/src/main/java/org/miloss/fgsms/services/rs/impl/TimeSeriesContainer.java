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

import java.util.ArrayList;
import java.util.List;
import org.jfree.data.time.TimeSeries;

/**
 *Used by the reporting service for generating reports
 * @author AO
 */
public class TimeSeriesContainer {

    public TimeSeriesContainer() {
        data = new ArrayList<TimeSeries>();
    }
    public List<TimeSeries> data;

    public TimeSeries Get(String name, Class t) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getKey().compareTo(name) == 0) {
                return data.get(i);
            }
        }
        TimeSeries ts = new TimeSeries(name, t);
        data.add(ts);
        return ts;
    }
}
