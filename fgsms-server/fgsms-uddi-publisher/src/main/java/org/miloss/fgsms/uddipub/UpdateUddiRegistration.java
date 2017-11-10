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
 * 
 */
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.uddipub;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.ws.BindingProvider;
import org.apache.juddi.v3.client.UDDIService;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.sla.SLACommon;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This SLA Plugin will register and unregister in UDDI when a web service when
 * its status changes
 *
 *
 * This is incomplete
 *
 * @author AO
 */
public class UpdateUddiRegistration implements org.miloss.fgsms.plugins.sla.SLAActionInterface {

    private static final Logger log = Logger.getLogger("fgsms.UDDIStatusUpdater");
    static boolean configuredForUddi = false;
    static long lastRefresh = 0;
    static boolean isPooled = false;
    private UDDIService uddi = null;
    private UDDIInquiryPortType inquiry = null;
    private UDDISecurityPortType security = null;
    private UDDIPublicationPortType publication = null;
    private boolean ClientCertRequired = false;
    private String inquiryurl = "";
    private String publishurl = "";
    private String securityurl = "";

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> ret = new ArrayList<NameValuePair>();
        ret.add(Utility.newNameValuePair(UddiPublisher.OPTION_BINDING_KEY, null, false, false));
        return ret;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        return new ArrayList<NameValuePair>();
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This plugin will either register or unregister a UDDI binding template registration based on the current status indicator. This is designed to work"
                + " in hand with the Rule: Change in Status<br><br>"
                + "<ul>"
                + "<li>" + UddiPublisher.OPTION_BINDING_KEY + " - Required. This is the key of the binding template in UDDI from which data will be attached</li>"
                + "</ul>";
    }

    @Override
    public String GetDisplayName() {
        return "Update UDDI Registration";
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
        AtomicReference<Boolean> status = new AtomicReference<Boolean>();
        SLACommon.GetCurrentStatus(alert.isPooled(), null, status, null, null);
        if (status != null && status.get() != null && status.get() == true) {
            Register(alert.isPooled(), alert.getServicePolicy());
        } else {
            Unregister(alert.isPooled(), alert.getServicePolicy());
        }
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        List<PolicyType> ret = new ArrayList<PolicyType>();
        ret.add(PolicyType.STATUS);
        ret.add(PolicyType.TRANSACTIONAL);
        return ret;
    }

    private enum AuthMode {

        Uddi, Http
    }
    private AuthMode uddiauth = AuthMode.Http;
    /// 
    /// Checks to see if the open esm tmodels have been registered with the uddi registry.
    /// if they haven't been, they are published
    /// 
    public boolean State = false;
    protected String username = "";
    protected String password = "";

    /*
     * protected URL pcsURL; private PCS client;
     */

    private String LoginWrapper() {

        return Login(username, password);
    }

    private String Login(String username, String password) {
        if (Utility.stringIsNullOrEmpty(username) || Utility.stringIsNullOrEmpty(password)) {
            return "";
        }
        if (uddiauth != AuthMode.Uddi) {
            return "";
        }

        //return "";/*
        GetAuthToken request = new GetAuthToken();
        request.setUserID(username);
        request.setCred(Utility.DE(password));
        try {
            AuthToken response = security.getAuthToken(request);
            return response.getAuthInfo();
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error authenticating to the UDDI server: ", ex);
            throw new IllegalArgumentException("can't authenticate to UDDI with the provided credentials");
            //return "";
        }
    }

    private void Register(boolean pooled, ServicePolicy servicePolicy) {
        Init(pooled);
        if (!State) {
            return;
        }
        FederationPolicyExt fp = new FederationPolicyExt(GetUddiFedPol(servicePolicy));
        if (fp == null || fp.getBindingKey() == null) {
            return;
        }
        //TODO
    }

    private void Unregister(boolean pooled, ServicePolicy servicePolicy) {
        Init(pooled);
        if (!State) {
            return;
        }
        FederationPolicyExt fp = new FederationPolicyExt(GetUddiFedPol(servicePolicy));
        if (fp == null || fp.getBindingKey() == null) {
            return;
        }
        //TODO
    }

    private FederationPolicy GetUddiFedPol(ServicePolicy p) {
        if (p == null) {
            return null;
        }
        if (p.getFederationPolicyCollection() == null) {
            return null;
        }
        for (int i = 0; i < p.getFederationPolicyCollection().getFederationPolicy().size(); i++) {
            if (p.getFederationPolicyCollection().getFederationPolicy().get(i).getImplementingClassName().equalsIgnoreCase("org.miloss.fgsms.uddipub.UddiPublisher")) {
                return p.getFederationPolicyCollection().getFederationPolicy().get(i);
            }
        }
        return null;
    }

    private void Init(boolean pooled) {
        if (System.currentTimeMillis() - 5 * 60 * 1000 > lastRefresh) {
            isPooled = pooled;
            try {
                String t = "";
                KeyNameValueEnc p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "Username");
                if (p != null && p.getKeyNameValue() != null) {
                    username = p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "password");
                if (p != null && p.getKeyNameValue() != null) {
                    password = p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "ClientCertRequired");
                if (p != null && p.getKeyNameValue() != null) {
                    try {
                        ClientCertRequired = Boolean.parseBoolean(p.getKeyNameValue().getPropertyValue());
                    } catch (Exception ex) {
                        ClientCertRequired = false;
                    }
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "AuthMode");
                if (p != null && p.getKeyNameValue() != null) {
                    t = p.getKeyNameValue().getPropertyValue();
                    try {
                        uddiauth = AuthMode.valueOf(t);
                    } catch (Exception ex) {
                        log.log(Level.WARN, "Unable to parse the value of UddiPublisher.AuthMode!", ex);
                        throw ex;
                    }
                }

                inquiryurl = "";
                publishurl = "";
                securityurl = "";

                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "InquiryURL");
                if (p != null && p.getKeyNameValue() != null) {
                    inquiryurl = p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "PublishURL");
                if (p != null && p.getKeyNameValue() != null) {
                    publishurl = p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "SecurityURL");
                if (p != null && p.getKeyNameValue() != null) {
                    securityurl = p.getKeyNameValue().getPropertyValue();
                }

                String jbossconfigpath = "";
                if (!Utility.stringIsNullOrEmpty(System.getProperty("jboss.server.config.url"))) {
                    jbossconfigpath = System.getProperty("jboss.server.config.url");
                }

                String truststore = null;
                String truststorepass = null;
                String keystore = null;
                String keystorepass = null;
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "defaults", "keystore");
                if (p != null && p.getKeyNameValue() != null) {
                    keystore = jbossconfigpath + p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "defaults", "keystorepass");
                if (p != null && p.getKeyNameValue() != null) {
                    keystorepass = p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "defaults", "truststore");
                if (p != null && p.getKeyNameValue() != null) {
                    truststore = jbossconfigpath + p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "defaults", "truststorepass");
                if (p != null && p.getKeyNameValue() != null) {
                    truststorepass = p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "keystore");
                if (p != null && p.getKeyNameValue() != null) {
                    keystore = jbossconfigpath + p.getKeyNameValue().getPropertyValue();
                }
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "keystorepass");
                if (p != null && p.getKeyNameValue() != null) {
                    keystorepass = p.getKeyNameValue().getPropertyValue();
                }

                //get SSL trust store
                boolean ignoresslerrors = false;
                p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "IgnoreSSLErrors");
                if (p != null && p.getKeyNameValue() != null) {
                    try {
                        ignoresslerrors = Boolean.parseBoolean(p.getKeyNameValue().getPropertyValue());
                    } catch (Exception ex) {
                        ignoresslerrors = false;
                    }
                }
                if (ignoresslerrors) {
                    HostnameVerifier ignoreHostName = new HostnameVerifier() {
                        public boolean verify(String string, SSLSession ssls) {
                            return true;
                        }
                    };
                    TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            public void checkClientTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }

                            public void checkServerTrusted(
                                    java.security.cert.X509Certificate[] certs, String authType) {
                            }
                        }
                    };

                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostName);
                    SSLContext.setDefault(sc);
                }

                if (Utility.stringIsNullOrEmpty(inquiryurl)) {
                    State = false;
                    log.log(Level.FATAL, "inquiry url is not set, see the how to guide for configuration Uddi Publication.");
                    return;
                }
                if (Utility.stringIsNullOrEmpty(publishurl)) {
                    State = false;
                    log.log(Level.FATAL, "publishurl url is not set, see the how to guide for configuration Uddi Publication.");
                    return;
                }
                if (Utility.stringIsNullOrEmpty(securityurl) && uddiauth == AuthMode.Uddi) {
                    State = false;
                    log.log(Level.FATAL, "auth mode is UDDI and the security url is not set, see the how to guide for configuration Uddi Publication.");
                    return;
                }

//inquiry endpoint
                uddi = new UDDIService();
                inquiry = uddi.getUDDIInquiryPort();
                BindingProvider bpPCS = (BindingProvider) inquiry;
                Map<String, Object> contextPCS = bpPCS.getRequestContext();
                contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, inquiryurl.trim());

                if (uddiauth == AuthMode.Http && !Utility.stringIsNullOrEmpty(password) && !Utility.stringIsNullOrEmpty(username)) {
                    contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(password));
                    contextPCS.put(BindingProvider.USERNAME_PROPERTY, username);
                }
                if (inquiryurl.toLowerCase().startsWith("https")) {
                    if (!Utility.stringIsNullOrEmpty(truststore) && !Utility.stringIsNullOrEmpty(truststorepass)) {
                        log.log(Level.INFO, "Inquiry Endpoint: Loading truststore from " + truststore);
                        contextPCS.put("javax.net.ssl.trustStorePassword", Utility.DE(truststorepass));
                        contextPCS.put("javax.net.ssl.trustStore", truststore);
                    }
                }
                if (ClientCertRequired && Utility.stringIsNullOrEmpty(keystore) && Utility.stringIsNullOrEmpty(keystorepass)) {
                    contextPCS.put("javax.net.ssl.keyStorePassword", Utility.DE(keystorepass));
                    contextPCS.put("javax.net.ssl.keyStore", keystore);
                    log.log(Level.INFO, "Inquiry Endpoint: Loading keystore from " + keystore);
                }

//publish endpoint
                publication = uddi.getUDDIPublicationPort();
                bpPCS = (BindingProvider) publication;

                contextPCS = bpPCS.getRequestContext();

                contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, publishurl.trim());
                if (uddiauth == AuthMode.Http && !Utility.stringIsNullOrEmpty(password) && !Utility.stringIsNullOrEmpty(username)) {
                    contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(password));
                    contextPCS.put(BindingProvider.USERNAME_PROPERTY, username);
                }
                if (publishurl.toLowerCase().startsWith("https")) {
                    if (!Utility.stringIsNullOrEmpty(truststore) && !Utility.stringIsNullOrEmpty(truststorepass)) {
                        contextPCS.put("javax.net.ssl.trustStorePassword", Utility.DE(truststorepass));
                        contextPCS.put("javax.net.ssl.trustStore", truststore);
                        log.log(Level.INFO, "Publish Endpoint: Loading truststore from " + truststore);
                    }
                }
                if (ClientCertRequired && Utility.stringIsNullOrEmpty(keystore) && Utility.stringIsNullOrEmpty(keystorepass)) {
                    contextPCS.put("javax.net.ssl.keyStorePassword", Utility.DE(keystorepass));
                    contextPCS.put("javax.net.ssl.keyStore", keystore);
                    log.log(Level.INFO, "Publish Endpoint: Loading keystore from " + truststore);
                }

                if (uddiauth == AuthMode.Uddi) {
                    //sercurity endpoint
                    security = uddi.getUDDISecurityPort();
                    bpPCS = (BindingProvider) security;
                    contextPCS = bpPCS.getRequestContext();
                    contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, securityurl.trim());
                    if (securityurl.toLowerCase().startsWith("https")) {
                        if (!Utility.stringIsNullOrEmpty(truststore) && !Utility.stringIsNullOrEmpty(truststorepass)) {
                            contextPCS.put("javax.net.ssl.trustStorePassword", Utility.DE(truststorepass));
                            contextPCS.put("javax.net.ssl.trustStore", truststore);
                            log.log(Level.INFO, "Security Endpoint: Loading truststore from " + truststore);
                        }
                    }
                    if (ClientCertRequired && Utility.stringIsNullOrEmpty(keystore) && Utility.stringIsNullOrEmpty(keystorepass)) {
                        contextPCS.put("javax.net.ssl.keyStorePassword", Utility.DE(keystorepass));
                        contextPCS.put("javax.net.ssl.keyStore", keystore);
                        log.log(Level.INFO, "Security Endpoint: Loading truststore from " + keystore);
                    }
                }

            } catch (Exception ex) {
                State = false;
                log.warn("check for configuration file, uddi.properties", ex);
                return;
            }
            State = true;
        }
    }

    /**
     * Alternate URLs, basically multiple hostnames, FQDN, host file entries,
     * etc can map to the same service. Sometimes a particular url is firewalled
     * or only listens on a specific hostname. By default bueller will first
     * attempt a connection using the modified url, i.e. the URL displayed in
     * fgsms which usually uses the hostname of the machine hosting the service
     * If another url was observed by an agent at some point in time, then this
     * will fetch all other urls for
     *
     * @param url
     * @param perf
     * @return
     */
    protected static Set<String> GetAlternateUrls(String url, java.sql.Connection perf) throws MalformedURLException {
        Set<String> alts = new HashSet<String>();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {

            //dec 10-2011 PreparedStatement com = perf.prepareStatement("select  originalurl from rawdata where uri=? group by originalurl;");
            com = perf.prepareStatement("select  alturi from alternateurls where uri=?;");
            com.setString(1, url);
            rs = com.executeQuery();
            while (rs.next()) {
                String t = rs.getString(1);
                if (!Utility.stringIsNullOrEmpty(t)) {
                    t = t.trim();
                    if (!Utility.stringIsNullOrEmpty(t)) {
                        if (!t.equals(url)) {
                            if (!url.equals(t)) {
                                try {
                                    URL turl = new URL(t);
                                    if (turl.getHost().toLowerCase().equalsIgnoreCase("localhost")) {
                                        alts.add(t);
                                    }
                                } catch (Exception ex) {
                                    log.log(Level.DEBUG, null, ex);
                                }
                            }
                        }
                    }
                }
                //TODO future optimization but not required, this might be a good spot to filter out localhost/127.0.0.1 records, but only if this machine is different that the machine hosting the service
            }
        } catch (Exception ex) {
            log.log(Level.DEBUG, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);

        }
        return alts;

    }

    /**
     * provides a wrapper for getting alternate urls without passing a db
     * connection
     *
     * @param url
     * @param pooled
     * @return
     */
    static Set<String> GetAlternateUrls(String url, boolean pooled) {

        java.sql.Connection perf = null;
        if (pooled) {
            perf = Utility.getPerformanceDBConnection();
        } else {
            perf = Utility.getPerformanceDB_NONPOOLED_Connection();
        }
        Set<String> ret = Collections.EMPTY_SET;
        try {
            ret = GetAlternateUrls(url, perf);
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        } finally {
            DBUtils.safeClose(perf);
        }
        return ret;
    }
}
