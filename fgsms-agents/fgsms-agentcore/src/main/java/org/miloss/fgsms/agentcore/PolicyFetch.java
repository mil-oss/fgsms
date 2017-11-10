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

import java.io.*;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.common.Utility;

import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GlobalPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.StatisticalServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Constants;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * Provides some useful functions for building agents.. All functions
 * use the fgsms-agent.properties file within this JAR for configuration
 * purposes All functions support discovery mechanisms, retry counts,
 * failover/roundrobin
 *
 * @author AO
 */
public class PolicyFetch extends HelperBase{

    static ConfigLoader cfg = null;

    private static void Init() throws ConfigurationException {
        if (cfg == null) {
            cfg = new ConfigLoader();
        }

    }

    static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);

    /**
     * * Attempts to retrieve the current global policy from the fgsms server
     * A successful return typically indicates that the server is up and
     * running.
     *
     * @since 6.3
     * @return A non-null global policy if successful. Null if not successful
     * @throws ConfigurationException
     */
    public static GlobalPolicy TryFetchGlobalPolicy() throws ConfigurationException {
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
                            //    context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
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
                            //send it
                            GetGlobalPolicyRequestMsg req = new GetGlobalPolicyRequestMsg();
                            req.setClassification(new SecurityWrapper(ClassificationType.U, "none"));
                            GetGlobalPolicyResponseMsg servicePolicy = cfg.pcsport.getGlobalPolicy(req);

                            return servicePolicy.getPolicy();


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
                            //send it
                            GetGlobalPolicyRequestMsg req = new GetGlobalPolicyRequestMsg();
                            req.setClassification(new SecurityWrapper(ClassificationType.U, "none"));
                            GetGlobalPolicyResponseMsg servicePolicy = cfg.pcsport.getGlobalPolicy(req);

                            return servicePolicy.getPolicy();

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

    /**
     * Gets a service policy from the fgsms server under the context of the an
     * agent meaning that if the policy does not exist, it will be created. Will
     * use discovery and retry logic if configured for it.
     *
     * @param URI
     * @param policyType
     * @param domainname
     * @param hostname
     * @return
     * @throws ConfigurationException
     */
    public static ServicePolicy TryFetchPolicy(String URI, PolicyType policyType, String domainname, String hostname) throws ConfigurationException {
        if (cfg == null) {
            Init();
        }
        discoverEndpoints();

        ServicePolicyRequestMsg req = new ServicePolicyRequestMsg();
        req.setClassification(cfg.classlevel);
        req.setDomain(domainname);
        req.setURI(URI);
        req.setPolicytype(policyType);

        if (Utility.stringIsNullOrEmpty(hostname)) {
            req.setMachine(Utility.getHostName());
        } else {
            req.setMachine(hostname);
        }

        try {

            return Send(req);
        } catch (Exception ex) {
            log.log(Level.ERROR, "could not retrieve a policy for the specified url", ex);
            return null;
        }
    }

    private static ServicePolicy Send(ServicePolicyRequestMsg req) throws ConfigurationException {
        if (cfg == null) {
            Init();
        }
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
                            //    context.remove(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
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
                            //send it
                            ServicePolicyResponseMsg servicePolicy = cfg.pcsport.getServicePolicy(req);
                            cfg.classlevel = servicePolicy.getClassification();
                            return servicePolicy.getPolicy();


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
                            //send it
                            ServicePolicyResponseMsg servicePolicy = cfg.pcsport.getServicePolicy(req);
                            cfg.classlevel = servicePolicy.getClassification();
                            return servicePolicy.getPolicy();
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

    protected static String readAllText(InputStream in) {
        try {
            InputStreamReader sr = new InputStreamReader(in,Constants.CHARSET);
            StringBuilder fileData = new StringBuilder(1000);
            BufferedReader reader = new BufferedReader(sr);

            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
            sr.close();
            return fileData.toString();
        } catch (Exception ex) {
        }
        return "";
    }

    /**
     * Loads the default policy. does not register this policy with the fgsms
     * server
     *
     * @param url
     * @return
     */
    public static TransactionalWebServicePolicy loadTranasctionalDefaultPolicy(String url) {

        try {
            InputStream in = null;
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }

            // Returns null on lookup failures:
            in = loader.getResourceAsStream("org/miloss/fgsms/agentcore/defaultpolicy.xml");
            String pol = readAllText(in);
            in.close();

            JAXBContext jc = Utility.getSerializationContext();
            Unmarshaller u = jc.createUnmarshaller();
            ByteArrayInputStream bss = new ByteArrayInputStream(pol.getBytes(Constants.CHARSET));
            XMLInputFactory xf = XMLInputFactory.newInstance();
            XMLStreamReader r = xf.createXMLStreamReader(bss);
            JAXBElement<TransactionalWebServicePolicy> foo = (JAXBElement<TransactionalWebServicePolicy>) u.unmarshal(r, TransactionalWebServicePolicy.class);
            if (foo == null || foo.getValue() == null) {
                log.log(Level.WARN, "ServicePolicy is unexpectedly null or empty");
                return null;
            }
            ServicePolicyResponseMsg ret = new ServicePolicyResponseMsg();
            ret.setPolicy(foo.getValue());
            ret.setClassification(cfg.classlevel);
            ret.getPolicy().setURL(url);
            return (TransactionalWebServicePolicy) ret.getPolicy();
        } catch (Exception ex) {
            log.log(Level.ERROR, "error loading default policy from disk", ex);
        }
        return null;
    }

    /**
     * Loads the default policy for statistic services, does not register this
     * policy with the fgsms server
     *
     * @param url
     * @param machine
     * @param domain
     * @return
     */
    public static StatisticalServicePolicy loadStatisticalDefaultPolicy(String url, String machine, String domain) {
        StatisticalServicePolicy pol = new StatisticalServicePolicy();
        pol.setAgentsEnabled(true);
        pol.setURL(url);
        pol.setMachineName(machine);
        pol.setDomainName(domain);

        return pol;
    }
}
