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

import org.miloss.fgsms.services.interfaces.dataaccessservice.*;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author AO
 */
public class MessageBrokerQueueDepthChart implements DatasetProducer, Serializable {

    public Object produceDataset(Map params) throws DatasetProduceException {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        try{
        GetCurrentBrokerDetailsResponseMsg res = (GetCurrentBrokerDetailsResponseMsg) params.get("fgsms.data");

        for (int i = 0; i < res.getQueueORtopicDetails().size(); i++) {
            if (res.getQueueORtopicDetails().get(i).getItemtype().equalsIgnoreCase("queue")) {
                //data.addValue(set.stuff.get(i).success, set.stuff.get(i).action + " Success", set.stuff.get(i).action);
                long k = res.getQueueORtopicDetails().get(i).getQueueDepth();
               // k = (long) (Math.random() * 100);
                data.addValue( k,res.getQueueORtopicDetails().get(i).getCanonicalname(),res.getQueueORtopicDetails().get(i).getCanonicalname());
            }
        }
        }
        catch (Exception ex)
        {
            LogHelper.getLog().log(Level.WARN, "error building chart for message broker queue depth", ex);
        }
        return data;
    }

    public boolean hasExpired(Map params, Date since) {
        return (System.currentTimeMillis() - since.getTime()) > 5000;
    }

    public String getProducerId() {
       return "fgsms.MessageBrokerQueueDepthChart";
    }
}
