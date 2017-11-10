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
import java.io.UnsupportedEncodingException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetPerformanceAverageStatsResponseMsg;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author AO
 */
public class PerformanceViewerAvgResTime implements DatasetProducer, CategoryItemLinkGenerator, Serializable {

    public PerformanceViewerAvgResTime() {
    }
    GetPerformanceAverageStatsResponseMsg[] mlist = null;

    public Object produceDataset(Map params) throws DatasetProduceException {
        //LogHelper.getLog().log(Level.ERROR, "fgsmsWeb produceDataset");
        try {
            if (mlist == null) {
                //mlist2 =  mlist.toArray(new GetPerformanceAverageStatsResponseMsg[0]);
                //Object[] t = (Object[]) params.get("fgsms.mlist");
                // mlist = (GetPerformanceAverageStatsResponseMsg[]) t;
                //  mlist = (GetPerformanceAverageStatsResponseMsg[]) params.get("fgsms.mlist");
                PerformanceViewerCache c = (PerformanceViewerCache) params.get("fgsms.mlist");
                mlist = c.data;
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
                        dataset.insertValue(count, mlist[i].getDisplayName() + " " + mlist[i].getAverageResponseTime() + "ms", mlist[i].getAverageResponseTime());
                    } else {
                        dataset.insertValue(count, mlist[i].getURL() + " " + mlist[i].getAverageResponseTime() + "ms", mlist[i].getAverageResponseTime());
                    }
                    count++;
                }
            }
            return dataset;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, null, ex);
            throw new DatasetProduceException("error");
        }
        //return null;
    }

    public boolean hasExpired(Map params, Date since) {
        return (System.currentTimeMillis() - since.getTime()) > 5000;
    }

    public String getProducerId() {
        return "fgsms.PerformanceViewerAvgResTime";
    }

    public String generateLink(Object dataset, int series, Object category) {
        try {
            LogHelper.getLog().log(Level.ERROR, "fgsmsWeb generateLinkSet");
            String s = (String) category;
            return "TransactionLogViewer.jsp?url=" + java.net.URLEncoder.encode(s, "UTF-8");
        } /* public String generateLink(Object dataset, int series, Object category) {
        throw new UnsupportedOperationException("Not supported yet.");
        }*/ catch (UnsupportedEncodingException ex) {
            LogHelper.getLog().log(Level.ERROR, null, ex);
        }
        return "";

    }
}
