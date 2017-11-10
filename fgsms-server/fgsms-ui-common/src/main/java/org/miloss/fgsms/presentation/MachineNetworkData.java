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
 * This is a simple container for Machine and Network Data, used for charting purposes
 * @author AO
 */
public class MachineNetworkData {
  public List<RateStruct> stuff;
    public MachineNetworkData()
    {
        stuff = new ArrayList<RateStruct>();
    }
    public boolean Contains(String item)
    {
        for (int i=0; i< stuff.size(); i++)
        {
            if (stuff.get(i).item.equalsIgnoreCase(item))
                return true;
        }
        return false;
    }

    public void add(RateStruct add)
    {
        stuff.add(add);

    }

    public List<TransactionLogTimeStampStruct> get(String action)
    {
        for (int i=0; i< stuff.size(); i++)
        {
            if (stuff.get(i).item.equalsIgnoreCase(action))
                return stuff.get(i).data;
        }
        return null;
    }
    public int Size()
    {
        return stuff.size();
    }

}
