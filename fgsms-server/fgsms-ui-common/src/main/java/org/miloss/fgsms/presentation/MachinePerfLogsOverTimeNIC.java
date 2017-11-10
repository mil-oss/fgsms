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
import java.util.ArrayList;
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
public class MachinePerfLogsOverTimeNIC implements DatasetProducer, Serializable {

    public Object produceDataset(Map params) throws DatasetProduceException {
        TimeSeriesCollection col = new TimeSeriesCollection();
        try {
            GetMachinePerformanceLogsByRangeResponseMsg res = (GetMachinePerformanceLogsByRangeResponseMsg) params.get("fgsms.data");
            MachineNetworkData dataRX = new MachineNetworkData();
            MachineNetworkData dataTX = new MachineNetworkData();
            if (res != null && res.getMachinePerformanceData() != null && !res.getMachinePerformanceData().isEmpty()) {
                for (int i = 0; i < res.getMachinePerformanceData().size(); i++) {

                    for (int k = 0; k < res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().size(); k++) {
                        TransactionLogTimeStampStruct t2 = new TransactionLogTimeStampStruct();
                        t2.ResponseTime = res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getKilobytespersecondNetworkReceive();
                         t2.cal = res.getMachinePerformanceData().get(i).getTimestamp();
                        if (dataRX.Contains(res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getAdapterName())) {
                            dataRX.get(res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getAdapterName()).add(t2);
                        } else {
                            RateStruct t3 = new RateStruct();
                            t3.item = res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getAdapterName();
                            t3.data = new ArrayList<TransactionLogTimeStampStruct>();
                            t3.data.add(t2);
                            dataRX.add(t3);
                        }

                        t2 = new TransactionLogTimeStampStruct();
                        t2.ResponseTime = res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getKilobytespersecondNetworkTransmit();
                         t2.cal = res.getMachinePerformanceData().get(i).getTimestamp();
                        if (dataTX.Contains(res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getAdapterName())) {
                            dataTX.get(res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getAdapterName()).add(t2);
                        } else {
                            RateStruct t3 = new RateStruct();
                            t3.item = res.getMachinePerformanceData().get(i).getNetworkAdapterPerformanceData().get(k).getAdapterName();
                            t3.data = new ArrayList<TransactionLogTimeStampStruct>();
                            t3.data.add(t2);
                            dataTX.add(t3);
                        }
                    }


                }

                for (int x = 0; x < dataRX.stuff.size(); x++) {
                    TimeSeries rx = new TimeSeries("RX on " + dataRX.stuff.get(x).item, org.jfree.data.time.Millisecond.class);
                    for (int y = 0; y < dataRX.stuff.get(x).data.size(); y++) {
                        Millisecond m = new Millisecond(dataRX.stuff.get(x).data.get(y).cal.getTime());
                        rx.addOrUpdate(m, dataRX.stuff.get(x).data.get(y).ResponseTime);
                    }
                    col.addSeries(rx);
                }

                for (int x = 0; x < dataTX.stuff.size(); x++) {
                    TimeSeries rx = new TimeSeries("TX on " + dataTX.stuff.get(x).item, org.jfree.data.time.Millisecond.class);
                    for (int y = 0; y < dataTX.stuff.get(x).data.size(); y++) {
                        Millisecond m = new Millisecond(dataTX.stuff.get(x).data.get(y).cal.getTime());
                        rx.addOrUpdate(m, dataTX.stuff.get(x).data.get(y).ResponseTime);
                    }
                    col.addSeries(rx);
                }
            }
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "MachinePerfLogsOverTimeNIC produce chart data " + ex.getLocalizedMessage());
        }
        return col;
    }

    public boolean hasExpired(Map params, Date since) {
        return (System.currentTimeMillis() - since.getTime()) > 5000;
    }

    public String getProducerId() {
        return "fgsms.MachinePerfLogsOverTimeNIC";
    }
}
