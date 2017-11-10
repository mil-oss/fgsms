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
import org.miloss.fgsms.services.interfaces.datacollector.AddMachineAndProcessDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.AddMachineAndProcessDataResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetProcessListByMachineRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetProcessListByMachineResponseMsg;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;

/**
 *  Provides some useful functions for building agents.. All
 * functions use the fgsms-agent.properties file within this JAR for
 * configuration purposes All functions support discovery mechanisms, retry
 * counts, failover/roundrobin
 *
 * @author AO
 */
public class OSAgentHelper extends HelperBase{

    static ConfigLoader cfg = null;

    private static void Init() throws ConfigurationException {
        if (cfg == null) {
            cfg = new ConfigLoader();
        }

    }
    static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);


    /**
     * Sends the current machine configuration information to the fgsms PCS
     * service If it cannot be reached, null is returned.
     *
     * @param req
     * @return
     * @throws ConfigurationException
     */
    public static SetProcessListByMachineResponseMsg SetMachineInfo(SetProcessListByMachineRequestMsg req) throws ConfigurationException {
        if (cfg == null) {
            Init();
        }
        discoverEndpoints();
        req.setClassification(cfg.classlevel);

        try {

            return Send(req);
        } catch (Exception ex) {
            log.log(Level.ERROR, "could not retrieve a policy for the specified url", ex);
            return null;
        }
    }

    private static SetProcessListByMachineResponseMsg Send(SetProcessListByMachineRequestMsg req) throws ConfigurationException {
        if (cfg == null) {
            Init();
        }
        discoverEndpoints();
        int retrycount = 0;
        int urlcount = 0;
        switch (cfg.PCSalgo) {
            case FAILOVER:
                retrycount = 0;
                urlcount = 0;
                while ((retrycount < cfg.PCSRetryCount)) {

                    urlcount = 0;
                    while (urlcount < cfg.PCS_URLS.size()) {
                        try { // Call Web Service Operation

                            BindingProvider bp = (BindingProvider) cfg.pcsport;
                            Map<String, Object> context = bp.getRequestContext();
                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.PCS_URLS.get(urlcount));
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
                            //  ApacheCxfSslHelper.DoCXF(cfg.pcsport, cfg);


                            //JAXB.marshal(req, System.out);
                            //send it
                            SetProcessListByMachineResponseMsg servicePolicy = cfg.pcsport.setProcessListByMachine(req);
                            cfg.classlevel = servicePolicy.getClassification();
                            return servicePolicy;


                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to fetch policy from PCS at " + cfg.PCS_URLS.get(urlcount) + " will retry " + (cfg.PCSRetryCount - retrycount) + " times." + ex.getMessage(), ex);
                        }
                        urlcount++;
                    }
                    retrycount++;
                }

                log.log(Level.FATAL, "fgsms unable to fetch policy from any of the PCS[" + cfg.PCS_URLS.size() + "] URLs, retry count exceeded. Falling back to default policy");
                return null;
            case ROUNDROBIN:
                retrycount = 0;
                urlcount = 0;
                while ((retrycount < cfg.PCSRetryCount)) {

                    urlcount = 0;
                    while (urlcount < cfg.PCS_URLS.size()) {
                        try { // Call Web Service Operation

                            BindingProvider bp = (BindingProvider) cfg.pcsport;
                            Map<String, Object> context = bp.getRequestContext();
                            //    context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.PCS_URLS.get(urlcount));
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
                            SetProcessListByMachineResponseMsg servicePolicy = cfg.pcsport.setProcessListByMachine(req);
                            cfg.classlevel = servicePolicy.getClassification();
                            return servicePolicy;
                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to fetch policy from PCS at " + cfg.PCS_URLS.get(urlcount) + " will retry " + (cfg.PCSRetryCount - retrycount) + " times." + ex.getMessage(), ex);
                        }
                        urlcount++;
                    }
                    retrycount++;
                }

                log.log(Level.FATAL, "fgsms unable to fetch policy from any of the PCS[" + cfg.PCS_URLS.size() + "] URLs, retry count exceeded.");
                return null;

        }
        return null;
    }

    public static AddMachineAndProcessDataResponseMsg AddMachineAndProcessDataRequestMsg(AddMachineAndProcessDataRequestMsg req) throws ConfigurationException {
        if (cfg == null) {
            Init();
        }
        discoverEndpoints();
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
                            //    context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.DCS_URLS.get(urlcount));
                            //TODO test to see if this works in other environments where the JbossWS stack isn't available.
                            //                        StubExt sec = (StubExt) pcsport;
                            if (cfg.mode_ == org.miloss.fgsms.common.Constants.AuthMode.UsernamePassword) {
                                //                        sec.setSecurityConfig("/META-INF/fgsms-username-config.xml");
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
                            AddMachineAndProcessDataResponseMsg res = cfg.dcsport.addMachineAndProcessData(req);
                            cfg.classlevel = res.getClassification();
                            return res;


                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to add data at the DCS at " + cfg.DCS_URLS.get(urlcount) + " will retry " + (cfg.DCSRetryCount - retrycount) + " times." + ex.getMessage(), ex);
                        }
                        urlcount++;
                    }
                    retrycount++;
                }

                log.log(Level.FATAL, "fgsms unable to add data at the from any of the DCS[" + cfg.DCS_URLS.size() + "] URLs, retry count exceeded.");
                return null;
            case ROUNDROBIN:
                retrycount = 0;
                urlcount = 0;
                while ((retrycount < cfg.DCSRetryCount)) {

                    urlcount = 0;
                    while (urlcount < cfg.DCS_URLS.size()) {
                        try { // Call Web Service Operation

                            BindingProvider bp = (BindingProvider) cfg.dcsport;
                            Map<String, Object> context = bp.getRequestContext();
                            //    context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.DCS_URLS.get(urlcount));
              //              context.put(BindingProviderProperties.CONNECT_TIMEOUT, 10000);
                            if (cfg.mode_ == org.miloss.fgsms.common.Constants.AuthMode.UsernamePassword) {
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
                            AddMachineAndProcessDataResponseMsg servicePolicy = cfg.dcsport.addMachineAndProcessData(req);
                            cfg.classlevel = servicePolicy.getClassification();
                            return servicePolicy;
                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to add data at the DCS at " + cfg.DCS_URLS.get(urlcount) + " will retry " + (cfg.DCSRetryCount - retrycount) + " times." + ex.getMessage(), ex);
                        }
                        urlcount++;
                    }
                    retrycount++;
                }

                log.log(Level.FATAL, "fgsms unable to add data from any of the DCS[" + cfg.DCS_URLS.size() + "] URLs, retry count exceeded.");
                return null;

        }
        return null;
    }
}
