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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.status.SetStatusRequestMsg;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *  provides a simple to use interface for setting/updating the status
 * of any service All functions use the fgsms-agent.properties file within
 * this JAR for configuration purposes All functions support discovery
 * mechanisms, retry counts, failover/roundrobin
 *
 * @author AO
 */
public class StatusHelper extends  HelperBase{

    static ConfigLoader cfg = null;
    static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    private static void init() throws ConfigurationException {
        if (cfg == null) {
            cfg = new ConfigLoader();
        }

    }
    
    

    /**
     * returns false if the status could not be sent
     *
     * @param isrunning
     * @param URI must be in lowercase and follow the format
     * urn:hostname:agentorcomponentname
     * @param message status message if any
     * @param ispooled not used
     * @param policyType
     * @param DomainName
     * @param Hostname
     * @return
     */
    public static boolean tryUpdateStatus(boolean isrunning, String URI, String message, boolean ispooled, PolicyType policyType, String domainname, String hostname) throws ConfigurationException {


        if (cfg == null) {
            init();
        }
       discoverEndpoints();

        SetStatusRequestMsg req = new SetStatusRequestMsg();
        req.setClassification(new SecurityWrapper(ClassificationType.U, ""));
        req.setDomainName(domainname);
        req.setHostname(hostname);
        req.setMessage(message);
        req.setOperational(isrunning);
        req.setURI(URI);
        req.setPolicyType(policyType);

        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        try {
         
             
            req.setTimeStamp((gcal));
            return send(req);
        } catch (Exception ex) {
            log.log(Level.FATAL, "unable to send status alert", ex);
            return false;
        }
    }

    private static boolean send(SetStatusRequestMsg req) {
        int retrycount = 0;
        int urlcount = 0;
        boolean ok = false;

        switch (cfg.SSalgo) {
            case FAILOVER:
                retrycount = 0;
                urlcount = 0;
                ok = false;
                while (!ok && (retrycount < cfg.SSRetryCount)) {

                    urlcount = 0;
                    while (!ok && urlcount < cfg.SS_URLS.size()) {
                        try { // Call Web Service Operation

                            BindingProvider bp = (BindingProvider) cfg.ssport;
                            Map<String, Object> context = bp.getRequestContext();
//                            context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.SS_URLS.get(urlcount));
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
                            cfg.ssport.setStatus(req);

                            ok = true;
                            return true;

                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to set status SS at " + cfg.SS_URLS.get(urlcount) + " will retry " + (cfg.SSRetryCount - retrycount) + " times." + ex.getMessage(), ex);
                        }
                        urlcount++;
                    }
                    retrycount++;
                }
                if (!ok) {
                    log.log(Level.FATAL, "fgsms unable to set status from any of the SS[" + cfg.SS_URLS.size() + "] URLs, retry count exceeded.");
                    return false;
                }


                break;
            case ROUNDROBIN:
                retrycount = 0;
                urlcount = 0;
                ok = false;
                while (!ok && (retrycount < cfg.SSRetryCount)) {

                    urlcount = 0;
                    while (!ok && urlcount < cfg.SS_URLS.size()) {
                        try { // Call Web Service Operation

                            BindingProvider bp = (BindingProvider) cfg.ssport;
                            Map<String, Object> context = bp.getRequestContext();
                            //                        context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                            context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.SS_URLS.get(urlcount));
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
                            cfg.ssport.setStatus(req);
                            ok = true;
                            return true;

                        } catch (Exception ex) {
                            log.log(Level.WARN, "fgsms unable to set status  from SS at " + cfg.SS_URLS.get(urlcount) + " will retry " + (cfg.SSRetryCount - retrycount) + " times." + ex.getMessage(), ex);
                        }
                        urlcount++;
                    }
                    retrycount++;
                }
                if (!ok) {
                    log.log(Level.FATAL, "fgsms unable tto set status from any of the SS[" + cfg.SS_URLS.size() + "] URLs, retry count exceeded.");
                    return false;
                }

                break;
        }
        return false;
    }
}
