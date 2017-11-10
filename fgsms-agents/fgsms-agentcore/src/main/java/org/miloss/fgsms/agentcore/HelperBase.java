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

package org.miloss.fgsms.agentcore;

import java.util.ArrayList;
import java.util.List;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.plugins.agents.IEndpointDiscovery;

/**
 * Supports DCS, SS, and PCS discovery
 * @author AO
 */
public abstract class HelperBase {

    static ConfigLoader cfg = null;

    private static void Init() throws ConfigurationException {
        if (cfg == null) {
            cfg = new ConfigLoader();
        }

    }
    static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    static List<IEndpointDiscovery> endpointproviders=null;
    
    /**
     * calls all {@link IEndpointDiscovery} interfaces to discover the location
     * of FGSMS's server, if configured
     * @throws ConfigurationException 
     */
    public static void discoverEndpoints() throws ConfigurationException {
        Init();
        if (cfg == null || cfg.prop == null) {
            throw new NullPointerException("fgsms properties file is not available.");
        }
        if (endpointproviders == null) {
            endpointproviders = DataPusher.LoadEndpointProviders(cfg);
        }
        List<String> endpointspcs = new ArrayList<String>();
        List<String> endpointsdcs = new ArrayList<String>();
        List<String> endpointsss = new ArrayList<String>();
        boolean ran = false;
        for (int i = 0; i < endpointproviders.size(); i++) {
            if ((System.currentTimeMillis() - cfg.discoveryInterval) > endpointproviders.get(i).GetLastLookup()) {
                if (endpointproviders.get(i).IsEnabled()) {
                    endpointspcs.addAll(endpointproviders.get(i).GetPCSURLs());
                    endpointsdcs.addAll(endpointproviders.get(i).GetDCSURLs());
                    endpointsss.addAll(endpointproviders.get(i).GetSSURLs());
                    ran = true;
                    endpointproviders.get(i).SetLastLookup(System.currentTimeMillis());
                }
            }

        }
        if (ran) {
            for (int i = 0; i < endpointspcs.size(); i++) {
                if (!cfg.PCS_URLS.contains(endpointspcs.get(i))) {
                    cfg.PCS_URLS.add(endpointspcs.get(i));
                }
            }
            for (int i = 0; i < endpointspcs.size(); i++) {
                if (!cfg.DCS_URLS.contains(endpointsdcs.get(i))) {
                    cfg.DCS_URLS.add(endpointsdcs.get(i));
                }
            }
            for (int i = 0; i < endpointsss.size(); i++) {
                if (!cfg.SS_URLS.contains(endpointsss.get(i))) {
                    cfg.SS_URLS.add(endpointsss.get(i));
                }
            }
        }

    }
}
