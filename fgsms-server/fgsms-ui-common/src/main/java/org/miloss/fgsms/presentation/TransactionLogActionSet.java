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
 *
 * @author AO
 */
public class TransactionLogActionSet {

    public List<TransactionLogActionData> stuff;
    public TransactionLogActionSet()
    {
        stuff = new ArrayList<TransactionLogActionData>();
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

    public void add(TransactionLogActionData add)
    {
        stuff.add(add);

    }

    public TransactionLogActionData get(String action)
    {
        for (int i=0; i< stuff.size(); i++)
        {
            if (stuff.get(i).action.equalsIgnoreCase(action))
                return stuff.get(i);
        }
        return null;
    }
    public int Size()
    {
        return stuff.size();
    }

}
