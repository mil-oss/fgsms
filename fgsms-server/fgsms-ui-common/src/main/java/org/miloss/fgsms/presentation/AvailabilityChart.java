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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetOperationalStatusLogResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.OperationalRecord;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 *
 * @author AO
 */
public class AvailabilityChart implements DatasetProducer, Serializable {

    /**
     * used from service profile requires the following items in the parameter
     * map fgsms.availabledata typeof GetOperationalStatusLogResponseMsg
     * fgsms.laststatus type Boolean fgsms.laststatustimestamp type Long
     *
     * @param params
     * @return
     * @throws DatasetProduceException
     */
    public Object produceDataset(Map params) throws DatasetProduceException {
        LogHelper.getLog().log(Level.DEBUG, "fgsmsWeb produceDataset");
        TimeSeriesCollection col = new TimeSeriesCollection();
        try {
            /*
             * if (dasu == null) { dasu = new URL((String)
             * params.get("dasurl")); } int days = 365; SecurityWrapper c =
             * (SecurityWrapper) params.get("currentclassification"); try { days
             * = (Integer) params.get("fgsms.days"); if (days <= 0) { days =
             * 365; } } catch (Exception ex) {
             * //Logger.getLogger(PerformanceViewerInvocations.class.getName()).log(Level.SEVERE,
             * "fgsmsWeb produceDataset", ex); }
             */
            // String username = "";
            // String password = "";
           /*
             * AuthMode mode_ = AuthMode.None; try { mode_ = (AuthMode)
             * params.get("fgsms.authmode"); if (mode_ ==
             * AuthMode.UsernamePassword) { username = (String)
             * params.get("fgsms.username"); password = (String)
             * params.get("fgsms.password"); } } catch (Exception ex) {
             * Logger.getLogger(PerformanceViewerInvocations.class.getName()).log(Level.ERROR,
             * "fgsmsWeb produceDataset", ex); return null; }
             *
             * try { // serviceurl = (String) params.get("fgsms.url"); } catch
             * (Exception ex) {
             * Logger.getLogger(PerformanceViewerInvocations.class.getName()).log(Level.ERROR,
             * "fgsmsWeb produceDataset", ex); return null; }
             */
            GetOperationalStatusLogResponseMsg res = null;
            try {
                res = (GetOperationalStatusLogResponseMsg) params.get("fgsms.availabledata");
            } catch (Exception ex) {
                LogHelper.getLog().log(Level.ERROR, "fgsmsWeb produceDataset, can't get the oplog", ex);
                return col;
            }
            if (res == null) {
                LogHelper.getLog().log(Level.ERROR, "fgsmsWeb produceDataset, oplog is null");
                return col;
            }
            Boolean laststatus = true;
            Long laststatusat = System.currentTimeMillis();
            try {
                laststatus = (Boolean) params.get("fgsms.laststatus");
                laststatusat = (Long) params.get("fgsms.laststatustimestamp");
            } catch (Exception ex) {
            }
            //add last known status item
            GregorianCalendar cal = new GregorianCalendar();
            if (laststatusat != null) {
                cal.setTimeInMillis(laststatusat.longValue());
            }
            
            OperationalRecord cur = new OperationalRecord();
            cur.setOperational(laststatus);
            cur.setTimestamp((cal));
            res.getOperationalRecord().add(cur);

            if (res != null && res.getOperationalRecord() != null && res.getOperationalRecord().size() > 1) {

                TimeSeries s1 = new TimeSeries("Operational", org.jfree.data.time.Millisecond.class);
                for (int i = 1; i < res.getOperationalRecord().size(); i++) {
                    Calendar tempcal = res.getOperationalRecord().get(i).getTimestamp();
                    tempcal.add(Calendar.SECOND, -1);
                    Millisecond current = new Millisecond(tempcal.getTime());
                    Millisecond previous = new Millisecond(res.getOperationalRecord().get(i - 1).getTimestamp().getTime());
                    TimeSeriesDataItem t = null;
                    if (res.getOperationalRecord().get(i - 1).isOperational()) {
                        try {
                            t = new TimeSeriesDataItem(previous, 1);
                            s1.add(t);
                        } catch (Exception ex) {
                        }
                        try {
                            t = new TimeSeriesDataItem(current, 1);
                            s1.add(t);
                        } catch (Exception ex) {
                        }
                    } else {
                        try {
                            t = new TimeSeriesDataItem(previous, 0);
                            s1.add(t);
                        } catch (Exception ex) {
                        }
                        try {
                            t = new TimeSeriesDataItem(current, 0);
                            s1.add(t);
                        } catch (Exception ex) {
                        }

                    }

                }
                if (res.getOperationalRecord().get(res.getOperationalRecord().size() - 1).isOperational()) {

                    TimeSeriesDataItem t = null;
                    t = new TimeSeriesDataItem(new Millisecond(res.getOperationalRecord().get(res.getOperationalRecord().size() - 1).getTimestamp().getTime()), 1);
                    s1.add(t);
                    t = new TimeSeriesDataItem(new Millisecond(GregorianCalendar.getInstance().getTime()), 1);
                    s1.add(t);
                } else {

                    TimeSeriesDataItem t = null;
                    t = new TimeSeriesDataItem(new Millisecond(res.getOperationalRecord().get(res.getOperationalRecord().size() - 1).getTimestamp().getTime()), 0);
                    s1.add(t);
                    t = new TimeSeriesDataItem(new Millisecond(GregorianCalendar.getInstance().getTime()), 0);
                    s1.add(t);
                }

                col.addSeries(s1);

            }

            return col;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "fgsmsWeb produceDataset, error caught", ex);
            //throw new DatasetProduceException("error", ex);
        }
        return col;
    }

    public boolean hasExpired(Map params, Date since) {
        return (System.currentTimeMillis() - since.getTime()) > 5000;
    }

    public String getProducerId() {
        return "fgsms.AvailabiltiyChart";
    }
}
