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
import de.laures.cewolf.links.CategoryItemLinkGenerator;
import de.laures.cewolf.tooltips.CategoryToolTipGenerator;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.presentation.PerformanceViewerCache;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsResponseMsg;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author AO
 */
public class PerformanceViewerInvocations implements DatasetProducer, CategoryToolTipGenerator, CategoryItemLinkGenerator, Serializable {

    //private URL dasu;
    GetPerformanceAverageStatsResponseMsg[] mlist = null;

    public Object produceDataset(Map params) throws DatasetProduceException {
        //LogHelper.getLog().log(Level.ERROR, "fgsmsWeb produceDataset");
        try {
            if (mlist == null) {
                //mlist2 =  mlist.toArray(new GetPerformanceAverageStatsResponseMsg[0]);
                PerformanceViewerCache c = (PerformanceViewerCache) params.get("fgsms.mlist");
                //Object[] t = (Object[]) params.get("fgsms.mlist");
                // mlist = (GetPerformanceAverageStatsResponseMsg[]) t;
                mlist = c.data;
                //  mlist = (GetPerformanceAverageStatsResponseMsg[]) params.get("fgsms.mlist");
            }
            DefaultPieDataset dataset = new DefaultPieDataset();


            if (mlist == null || mlist.length == 0) {
                return dataset;
            }
            // LogHelper.getLog().log(Level.ERROR, "fgsmsWeb produceDataset, cache size " + mlist.length);
            int count = 0;
            for (int i = 0; i < mlist.length; i++) {
                if ((mlist[i].getSuccessfulInvocations() + mlist[i].getFailingInvocations()) > 0) {
                    if (!Utility.stringIsNullOrEmpty(mlist[i].getDisplayName())) {
                        dataset.insertValue(count, mlist[i].getDisplayName() + " Success: " + mlist[i].getSuccessfulInvocations() + " Fault: " + mlist[i].getFailingInvocations(), mlist[i].getSuccessfulInvocations() + mlist[i].getFailingInvocations());
                    } else {
                        dataset.insertValue(count, mlist[i].getURL() + " Success: " + mlist[i].getSuccessfulInvocations() + " Fault: " + mlist[i].getFailingInvocations(), mlist[i].getSuccessfulInvocations() + mlist[i].getFailingInvocations());
                    }
                    count++;
                }
            }
            return dataset;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, null, ex);
            throw new DatasetProduceException("error", ex);
        }
        //return null;
    }

    public boolean hasExpired(Map params, Date since) {
        return (System.currentTimeMillis() - since.getTime()) > 5000;
    }

    public String getProducerId() {
        return "fgsms.PerformanceViewerInvocations";
    }
    // public List<PerformanceViewerCache> cache;

    public String generateLink(Object dataset, int series, Object category) {
        try {
            LogHelper.getLog().log(Level.ERROR, "fgsmsWeb generateLinkSet");
            String s = (String) category;
            return "TransactionLogViewer.jsp?url=" + java.net.URLEncoder.encode(s, "UTF-8");
        } /* public String generateLink(Object dataset, int series, Object category) {
        throw new UnsupportedOperationException("Not supported yet.");
        }*/ catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PerformanceViewerAvgResTime.class.getName()).log(Level.ERROR, null, ex);
        }
        return "";

    }

    public String generateToolTip(CategoryDataset data, int series, int item) {
        LogHelper.getLog().log(Level.ERROR, "fgsmsWeb generateToolTip " + series + " " + item);
        return "TransactionLogViewer.jsp?url=" + data.getValue(series, item);

    }
    /*
    private void getData(int days) {
    try {
    LogHelper.getLog().log(Level.ERROR, "fgsmsWeb performance viewer invocations, getting monitored service list");
    DataAccessService_Service das = new DataAccessService_Service(dasu);
    DataAccessService dasport = das.getBasicHttpBindingDataAccessService();
    GetMonitoredServiceListResponseMsg mlist = dasport.getMonitoredServiceList(new GetMonitoredServiceListRequestMsg());
    if (mlist != null && mlist.getServiceList() != null && mlist.getServiceList().getValue() != null && mlist.getServiceList().getValue().getServiceType() != null && mlist.getServiceList().getValue().getServiceType().size() > 0) {
    cache = new ArrayList<PerformanceViewerCache>();
    for (int i = 0; i < mlist.getServiceList().getValue().getServiceType().size(); i++) {
    if (mlist.getServiceList().getValue().getServiceType().get(i) != null && !Utility.stringIsNullOrEmpty(mlist.getServiceList().getValue().getServiceType().get(i).getURL())) {
    GetPerformanceAverageStatsRequestMsg req = new GetPerformanceAverageStatsRequestMsg();
    TimeRange now = new TimeRange();
    long currentime = System.currentTimeMillis();
    
    Calendar gc = GregorianCalendar.getInstance();
    gc.setTimeInMillis(currentime);
    Calendar nowtime = DatatypeFactory.newInstance().newCalendar((GregorianCalendar) gc);
    
    Calendar gc2 = GregorianCalendar.getInstance();
    gc2.setTimeInMillis(currentime);
    gc2.add(Calendar.DATE, (days * -1));
    Calendar endtime = DatatypeFactory.newInstance().newCalendar((GregorianCalendar) gc2);
    
    
    
    
    now.setStart(endtime);
    now.setEnd(nowtime);
    req.setRange(now);
    req.setURL(mlist.getServiceList().getValue().getServiceType().get(i).getURL());
    GetPerformanceAverageStatsResponseMsg res = dasport.getPerformanceAverageStats(req);
    if (res != null) {
    PerformanceViewerCache temp = new PerformanceViewerCache();
    temp.url = mlist.getServiceList().getValue().getServiceType().get(i).getURL();
    temp.successes = res.getSuccessfulInvocations();
    temp.failures = res.getFailingInvocations();
    temp.averageResponseTime = res.getAverageResponseTime();
    cache.add(temp);
    }
    
    }
    }
    }
    } catch (Exception ex) {
    LogHelper.getLog().log(Level.ERROR, null, ex);
    }
    }*/
}
