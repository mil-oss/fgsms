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
package org.miloss.fgsms.discovery.dns;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.miloss.fgsms.plugins.agents.IEndpointDiscovery;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.xbill.DNS.*;

/**
 *
 * @author AO
 */
public class DNSDiscovery implements IEndpointDiscovery {

    static final Logger log = Logger.getLogger("fgsms.DNSDiscovery");

    private static List<String> DnsProbeTXT(String value) {
        log.log(Level.INFO, "Discovering endpoints for " + value + " via DNS TXT record search");
        List<String> ret = new ArrayList<String>();
        try {
            Lookup up = null;
            Record[] run = null;
            up = new Lookup(value, Type.TXT);
            run = up.run();
            if (run != null) {
                for (int i = 0; i < run.length; i++) {

                    TXTRecord tr = (TXTRecord) run[i];
                    String v = tr.rdataToString();
                    try {

                        if (tr.rdataToString().startsWith("\"") && tr.rdataToString().endsWith("\"")) {
                            v = v.substring(1, v.length() - 1);
                        }
                        if (v.toLowerCase().startsWith("http://")
                                || v.toLowerCase().startsWith("https://")) {
                            ret.add(v);
                        } else {
                            log.log(Level.WARN, "throwing out value " + v + " as it is not parsable as a URL");
                        }
                    } catch (Exception e) {
                        log.log(Level.WARN, "throwing out value " + v + " as it is not parsable as a URL");
                    }
                }
            }
            if (up.getResult() != 0) {
                log.log(Level.ERROR, "Unable to discovery endpoints for " + value + " via DNS error: " + up.getErrorString());
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "Unable to discovery endpoints for " + value + " via DNS error", ex);
        }
        return ret;

    }
    boolean DNSenabled = true;

    @Override
    public void LoadConfig(Properties p) {
        try {
            DNSenabled = Boolean.parseBoolean(p.getProperty("discovery.dns.enabled").trim());
        } catch (Exception ex) {
            DNSenabled = false;
            log.log(Level.WARN, null, ex);
        }
    }

    @Override
    public List<String> GetDASURLs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
long lastlookup=0;
    @Override
    public void SetLastLookup(long timeinms) {
        lastlookup = timeinms;
    }

    @Override
    public long GetLastLookup() {
        return lastlookup;
    }

    @Override
    public boolean IsEnabled() {
        return DNSenabled;
    }

    enum DiscoverType {

        DAS, PCS, RS, ARS, SS, DCS
    }

    private static List<String> DiscoverViaDNS(DiscoverType type) {
        return DnsProbeTXT("fgsms-" + type.toString().toLowerCase());
    }

    @Override
    public List<String> GetPCSURLs() {
        return DiscoverViaDNS(DiscoverType.PCS);
    }

    @Override
    public List<String> GetDCSURLs() {
        return DiscoverViaDNS(DiscoverType.DCS);
    }

    @Override
    public List<String> GetSSURLs() {
        return DiscoverViaDNS(DiscoverType.SS);
    }

  
    @Override
    public List<String> GetRSURLs() {
        return DiscoverViaDNS(DiscoverType.RS);
    }

    @Override
    public List<String> GetARSURLs() {
        return DiscoverViaDNS(DiscoverType.ARS);
    }

    @Override
    public List<String> GetURLs(String lookupkey) {
        return DnsProbeTXT(lookupkey);
    }
}
