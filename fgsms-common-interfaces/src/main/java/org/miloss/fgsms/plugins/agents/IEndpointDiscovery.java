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
package org.miloss.fgsms.plugins.agents;

import java.util.List;
import java.util.Properties;

/**
 * Implementors of the class are typically used for fgsms Agents to discover where home is
 * @author AO
 */
public interface IEndpointDiscovery {

    public boolean IsEnabled();
    
    public void LoadConfig(Properties p);

    public List<String> GetARSURLs();

    public List<String> GetRSURLs();

    public List<String> GetPCSURLs();

    public List<String> GetDASURLs();

    public List<String> GetDCSURLs();

    public List<String> GetSSURLs();

    public List<String> GetURLs(String lookupkey);

    public void SetLastLookup(long timeinms);

    public long GetLastLookup();
}
