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

package org.miloss.fgsms.presentation;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMachinePerformanceLogsByRangeResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetProcessPerformanceLogsByRangeResponseMsg;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author AO
 */
public class MachinePerfLogsOverTimeMem implements DatasetProducer, Serializable {

    public Object produceDataset(Map params) throws DatasetProduceException {
        TimeSeriesCollection col = new TimeSeriesCollection();

        try {
            //Logger.getAnonymousLogger().log(Level.INFO, "fgsmsWEB debug, size" +  params.size());
            GetMachinePerformanceLogsByRangeResponseMsg res = (GetMachinePerformanceLogsByRangeResponseMsg) params.get("fgsms.data");

            if (res != null && res.getMachinePerformanceData() != null && !res.getMachinePerformanceData().isEmpty()) {
                  TimeSeries s = new TimeSeries("Memory (bytes)", org.jfree.data.time.Millisecond.class);
                for (int i = 0; i < res.getMachinePerformanceData().size(); i++) {
                  
                    Millisecond m = new Millisecond(res.getMachinePerformanceData().get(i).getTimestamp().getTime());
                    //  TimeSeriesDataItem t = new TimeSeriesDataItem(m,
                    //          recordset.stuff.get(i).data.get(k).ResponseTime);
                    // s.add(t);
                    if (res.getMachinePerformanceData().get(i).getBytesusedMemory()!=null)
                    s.addOrUpdate(m, res.getMachinePerformanceData().get(i).getBytesusedMemory());

                }
                col.addSeries(s);
            }
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "ProcessPerfLogsOverTimeMem produce chart data " + ex.getLocalizedMessage());
        }
        return col;
    }

    public boolean hasExpired(Map params, Date since) {
        return (System.currentTimeMillis() - since.getTime()) > 5000;
    }

    public String getProducerId() {
        return "fgsms.ProcessPerfLogsOverTimeMem";
    }
}
