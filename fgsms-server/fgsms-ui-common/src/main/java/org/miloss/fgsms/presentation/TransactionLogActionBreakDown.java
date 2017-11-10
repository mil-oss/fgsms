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
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMessageLogsResponseMsg;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author AO
 */
public class TransactionLogActionBreakDown implements DatasetProducer, Serializable {

    public Object produceDataset(Map params) throws DatasetProduceException {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        try {
            //Logger.getAnonymousLogger().log(Level.INFO, "fgsmsWEB debug, size" +  params.size());
            GetMessageLogsResponseMsg res = (GetMessageLogsResponseMsg) params.get("fgsms.data");
            TransactionLogActionSet set = new TransactionLogActionSet();

            if (res!=null && res.getLogs() != null && res.getLogs() != null && res.getLogs().getTransactionLog() != null && res.getLogs().getTransactionLog().size() > 0) {
                for (int i = 0; i < res.getLogs().getTransactionLog().size(); i++) {
                    String action = res.getLogs().getTransactionLog().get(i).getAction();
                    int clip = 0;
                    if (res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("/") > clip) {
                        clip = res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("/");
                    }
                    if (res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("}") > clip) {
                        clip = res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("}");
                    }
                    if (res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf(":") > clip) {
                        clip = res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf(":");
                    }
                    if (res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("#") > clip) {
                        clip = res.getLogs().getTransactionLog().get(i).getAction().lastIndexOf("#");
                    }

                    if (clip > 0) {
                        action = (res.getLogs().getTransactionLog().get(i).getAction().substring(clip + 1));
                    }
                    if (set.Contains(action)) {
                        if (res.getLogs().getTransactionLog().get(i).isIsFault()) {
                            set.get(action).success++;
                        } else {
                            set.get(action).failures++;
                        }
                    } else {
                        TransactionLogActionData t = new TransactionLogActionData();
                        t.action = action;
                        t.success = 0;
                        t.failures = 0;
                        if (res.getLogs().getTransactionLog().get(i).isIsFault()) {
                            t.success++;
                        } else {
                            t.failures++;
                        }
                        set.add(t);
                    }

                }
                for (int i = 0; i < set.stuff.size(); i++) {
                   // LogHelper.getLog().log(Level.WARNING,
                   //         set.stuff.get(i).action + set.stuff.get(i).success + set.stuff.get(i).failures );
                    if (set.stuff.get(i).success > 0) {
                        data.addValue(set.stuff.get(i).success, set.stuff.get(i).action + " Success", set.stuff.get(i).action);
                    }
                    if (set.stuff.get(i).failures > 0) {
                        data.addValue(set.stuff.get(i).failures, set.stuff.get(i).action + " Failures", set.stuff.get(i).action);
                    }
                }
            }
            
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "TransactionLogActionBreakDown produce chart data " + ex.getLocalizedMessage());
        }
        return data;
    }

    public boolean hasExpired(Map params, Date since) {
        return (System.currentTimeMillis() - since.getTime()) > 5000;
    }

    public String getProducerId() {
        return "fgsms.TransactionLogActionBreakDown";
    }
}
