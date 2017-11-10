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

import java.util.ArrayList;
import java.util.List;

/**
 *Provides a simple class to use for temporary storage of transactional web service performance data for the purposes of chart rendering
 * @author AO
 */
public class TransactionLogData {
    public List<TransactionLogStruct> stuff;
    public TransactionLogData()
    {
        stuff = new ArrayList<TransactionLogStruct>();
    }
    public boolean Contains(String action)
    {
        for (int i=0; i< stuff.size(); i++)
        {
            if (stuff.get(i).action.equalsIgnoreCase(action))
                return true;
        }
        return false;
    }

    public void add(TransactionLogStruct add)
    {
        stuff.add(add);

    }

    public List<TransactionLogTimeStampStruct> get(String action)
    {
        for (int i=0; i< stuff.size(); i++)
        {
            if (stuff.get(i).action.equalsIgnoreCase(action))
                return stuff.get(i).data;
        }
        return null;
    }
    public int Size()
    {
        return stuff.size();
    }

}
