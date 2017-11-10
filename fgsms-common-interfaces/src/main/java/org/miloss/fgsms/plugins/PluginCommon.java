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
 
package org.miloss.fgsms.plugins;

import java.util.List;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;

/**
 *
 * @author AO
 */
public interface PluginCommon {
  /**
     * end user facing display name
     *
     * @since 6.3
     * @return
     */
    public String GetDisplayName();

    /**
     * returns html formatted help to guide users towards proper usage.
     * Always include the following fields <ul>
     * <li>Applies to - what policies types it applies to</li>
     * <li>Required and optional parameters</li>
     * <li>Description</li>
     * <li>State if this is processed as data comes in, or periodically (some aggregated data)
     * </ul>
     * 
     * Html is allowed, javascript is not
     *
     * @since 6.3
     * @return
     */
    public String GetHtmlFormattedHelp();

    /**
     * returns a list of required parameters
     *
     * @since 6.3
     * @return
     */
    public List<NameValuePair> GetRequiredParameters();

    /**
     * returns a list of optional parameters
     *
     * @since 6.3
     * @return
     */
    public List<NameValuePair> GetOptionalParameters();

    /**
     * Returns a list of service policy types that this plugin can be applied to
     * use Utility.getAllPolicyTypes() if it applies to everything. Also Utility.IsContains()
     * @return 
     */
   public List<PolicyType> GetAppliesTo();

}
