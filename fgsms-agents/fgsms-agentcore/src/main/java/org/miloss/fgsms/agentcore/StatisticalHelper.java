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

import java.util.Map;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.AddStatisticalDataResponseMsg;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 * Provides some useful functions for building agents.. All functions
 * use the fgsms-agent.properties file within this JAR for configuration
 * purposes All functions support discovery mechanisms, retry counts,
 * failover/roundrobin
 *
 * @since RC6
 * @author AO
 */
public class StatisticalHelper extends HelperBase{

    protected static ConfigLoader cfg = null;
    static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    private static void Init() throws ConfigurationException {
        if (cfg == null) {
            cfg = new ConfigLoader();
        }

    }
    
    

    /**
     * Attempts to send statistical data to the fgsms DCS service
     *
     * @param req
     * @return true if successful
     * @throws ConfigurationException if the current agent configuration is not
     * available or is invalid
     */
    public static boolean send(AddStatisticalDataRequestMsg req) throws ConfigurationException {
        if (cfg == null) {
            Init();
        }
    discoverEndpoints();
        req.setClassification(cfg.classlevel);
        int retrycount = 0;
        int urlcount = 0;
        switch (cfg.DCSalgo) {
            case FAILOVER:
                retrycount = 0;
                urlcount = 0;
                while ((retrycount < cfg.DCSRetryCount)) {

                    urlcount = 0;
                    while (urlcount < cfg.DCS_URLS.size()) {
                        try { // Call Web Service Operation

                            BindingProvider bp = (BindingProvider) cfg.dcsport;
                            Map<String, Object> context = bp.getRequestContext();
//                            context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.DCS_URLS.get(urlcount));
                            if (cfg.mode_ == org.miloss.fgsms.common.Constants.AuthMode.UsernamePassword) {
                                context.put(BindingProvider.USERNAME_PROPERTY, cfg.username);
                                context.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.password));
                            }
                            if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStore")) && !Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStorePassword"))) {
                                context.put("javax.net.ssl.trustStorePassword", Utility.DE(cfg.getJavaxtruststorepass()));
                                context.put("javax.net.ssl.trustStore", Utility.DE(cfg.getJavaxtruststore()));
                            }
                            try {
                                if (cfg.mode_ == org.miloss.fgsms.common.Constants.AuthMode.PKI) {
                                    if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStore")) && !Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStorePassword"))) {
                                        context.put("javax.net.ssl.keyStorePassword", Utility.DE(cfg.getJavaxkeystorepass()));
                                        context.put("javax.net.ssl.keyStore", Utility.DE(cfg.getJavaxkeystore()));
                                    }
                                }

                            } catch (Exception ex) {
                                log.log(Level.FATAL, "error caught when referencing (get or set) System.properties for SSL communication. Check to ensure that this is enabled in your JAAS managemer", ex);
                            }
                            //send it
                            AddStatisticalDataResponseMsg addStatisticalData = cfg.dcsport.addStatisticalData(req);
                            cfg.classlevel = addStatisticalData.getClassification();

                            return true;


                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to transmit to CS at " + cfg.DCS_URLS.get(urlcount) + " will retry " + (cfg.DCSRetryCount - retrycount) + " times." + ex.getMessage(), ex);
                        }
                        urlcount++;
                    }
                    retrycount++;
                }

                log.log(Level.FATAL, "fgsms unable to transmit to any of the DCS[" + cfg.DCS_URLS.size() + "] URLs, retry count exceeded. Falling back to default policy");
                return false;
            case ROUNDROBIN:
                retrycount = 0;
                urlcount = 0;
                while ((retrycount < cfg.DCSRetryCount)) {

                    urlcount = 0;
                    while (urlcount < cfg.DCS_URLS.size()) {
                        try { // Call Web Service Operation

                            BindingProvider bp = (BindingProvider) cfg.dcsport;
                            Map<String, Object> context = bp.getRequestContext();
                            //                        context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.DCS_URLS.get(urlcount));
//                            StubExt sec = (StubExt) pcsport;
                            if (cfg.mode_ == org.miloss.fgsms.common.Constants.AuthMode.UsernamePassword) {
//                                sec.setSecurityConfig("/META-INF/fgsms-username-config.xml");
                                context.put(BindingProvider.USERNAME_PROPERTY, cfg.username);
                                context.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.password));
                            }
                            if (cfg.mode_ == org.miloss.fgsms.common.Constants.AuthMode.PKI) {
                                if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStore")) && !Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.keyStorePassword"))) {
                                    System.setProperty("javax.net.ssl.keyStorePassword", Utility.DE(cfg.getJavaxkeystorepass()));
                                    System.setProperty("javax.net.ssl.keyStore", Utility.DE(cfg.getJavaxkeystore()));
                                }
                            }
                            if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStore")) && !Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStorePassword"))) {
                                System.setProperty("javax.net.ssl.trustStorePassword", Utility.DE(cfg.getJavaxtruststorepass()));
                                System.setProperty("javax.net.ssl.trustStore", Utility.DE(cfg.getJavaxtruststore()));
                            }
                            //send it
                            AddStatisticalDataResponseMsg addStatisticalData = cfg.dcsport.addStatisticalData(req);
                            cfg.classlevel = addStatisticalData.getClassification();
                            return true;
                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to transmit to DCS at " + cfg.DCS_URLS.get(urlcount) + " will retry " + (cfg.DCSRetryCount - retrycount) + " times." + ex.getMessage(), ex);
                        }
                        urlcount++;
                    }
                    retrycount++;
                }
                log.log(Level.FATAL, "fgsms unable totransmit to any of the DCS[" + cfg.DCS_URLS.size() + "] URLs, retry count exceeded.");
        }
        return false;
    }
}
