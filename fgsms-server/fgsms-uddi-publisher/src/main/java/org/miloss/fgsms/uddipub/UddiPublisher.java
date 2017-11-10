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
package org.miloss.fgsms.uddipub;

import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.util.Calendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.agents.JAXWSGenericAgent;
import org.miloss.fgsms.agents.JAXWSGenericClientAgent;
import org.miloss.fgsms.common.AuditLogger;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.PublicationConstants;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.dataaccessservice.QuickStatData;
import org.miloss.fgsms.services.interfaces.dataaccessservice.QuickStatWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * UDDI Publisher. This class is used to provide selected statistics to a UDDI
 * server by updating the binding key information for a specific, preregistered
 * endpoint
 *
 * @author AO
 */
public class UddiPublisher implements org.miloss.fgsms.plugins.federation.FederationInterface {

    protected static final Logger log = Logger.getLogger("fgsms.UDDIPublisher");
    //juddi needs a urn: prefix
    /// 
    /// success
    /// 
    //"uddi:juddi.apache.org:something  
    private UDDIService uddi = new UDDIService();
    private UDDIInquiryPortType inquiry = null;
    private UDDISecurityPortType security = null;
    private UDDIPublicationPortType publication = null;
    private boolean isPooled = false;
    private boolean ClientCertRequired = false;
    private String inquiryurl = "";
    private String publishurl = "";
    private String securityurl = "";
    public static final String OPTION_PUBLISH_TIME_RANGE = "Range";
    public static final String OPTION_PUBLISH_STATUS = "Status";
    public static final String OPTION_PUBLISH_SLA = "SLA";
    public static final String OPTION_PUBLISH_MAX = "Maximums";
    public static final String OPTION_PUBLISH_UPTIME = "Uptime";
    public static final String OPTION_PUBLISH_FAULTS = "Faults";
    public static final String OPTION_PUBLISH_SUCCESS = "Success";
    public static final String OPTION_PUBLISH_AVG_RES_TIME = "Averages";
    public static final String OPTION_BINDING_KEY = "Binding";
    public static final String OPTION_PUBLISH_USERNAME = "Username";
    public static final String OPTION_PUBLISH_PASSWORD = "Password";

    public UddiPublisher() {
        //required for UI functions
        this(true);
    }

    public UddiPublisher(boolean pooled) {
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
            


//inquiry endpoint
            inquiry = uddi.getUDDIInquiryPort();
            
            BindingProvider bpPCS = (BindingProvider) inquiry;
            addMontior(bpPCS);
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
addMontior(bpPCS);
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
                addMontior(bpPCS);
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

    private boolean TmodelKeyIsfgsms(String tModelKey) {
        String[] items = PublicationConstants.getAllTmodelKeys();
        for (int i = 0; i < items.length; i++) {
            if (tModelKey.equalsIgnoreCase(items[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns null if no records are found (meaning no aggregated stats are
     * available yet)
     *
     * @param data
     * @param timeInMs
     * @return
     */
    private QuickStatData getRecord(QuickStatWrapper data, long timeInMs) {
        if (data == null) {
            return null;
        }
        for (int i = 0; i < data.getQuickStatData().size(); i++) {
            if ((data.getQuickStatData().get(i).getTimeInMinutes().longValue() * 60 * 1000) == timeInMs) {
                return data.getQuickStatData().get(i);
            }
        }
        return null;
    }

    private String getLastChangeTimeStamp(String uri, boolean pooled) {
        try {
            Calendar cal = null;
            Connection con = null;
            if (pooled) {
                con = Utility.getPerformanceDBConnection();
            } else {
                con = Utility.getPerformanceDB_NONPOOLED_Connection();
            }
            PreparedStatement com = con.prepareStatement("select * from availability where uri=? order by utcdatetime desc limit 1");
            com.setString(1, uri);
            //         Duration d=null;
            ResultSet rs = com.executeQuery();
            if (rs.next()) {
                long changeat = rs.getLong("utcdatetime");
                DatatypeFactory f = DatatypeFactory.newInstance();
                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTimeInMillis(changeat);
                cal = (gcal);
            }
            rs.close();
            com.close();
            con.close();
            if (cal != null) {
                return cal.toString();
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        }
        return "";
    }

    @Override
    public boolean ValidateConfiguration(FederationPolicy fp, AtomicReference<String> outMessage) {

        boolean ok = true;
        if (outMessage == null) {
            outMessage = new AtomicReference<String>("");
        }
        FederationPolicyExt e = new FederationPolicyExt(fp);
        if (e.getPublishTimeRange().isEmpty()) {
            ok = false;
            outMessage.set("Time Range is required to have at least one value.");
        }
        if (Utility.stringIsNullOrEmpty(e.getBindingKey())) {
            ok = false;
            outMessage.set("A binding key is required for publication.");
        }
        return ok;
    }

    /**
     * Publish fgsms data to the UDDI server uses a direct database connection
     * to the performance database uses a connect to the fgsms PCS
     */
    @Override
    public void Publish(boolean pooled, QuickStatWrapper data, ServicePolicy sp, FederationPolicy fp) {
         log.log(Level.INFO, "Starting UDDI publication for " + sp.getURL());
        this.isPooled = pooled;
        if (!checkTmodelPublication()) {
            log.log(Level.ERROR, "Unable to publish tModel definitions to the UDDI registry, registry publication is not possible: URLs (i/p/s) " + inquiryurl + " " + publishurl + " " + securityurl);
            State = false;
            return;
        }
        FederationPolicyExt policy = new FederationPolicyExt(fp);

        String auth = loginWrapper();
        try {
            
            if (Utility.stringIsNullOrEmpty(policy.getBindingKey())) {
                log.log(Level.WARN, "unable to publish UDDI data for service " + sp.getURL() + " because the binding key is null or empty");
                return;
            }
            GetBindingDetail ir = new GetBindingDetail();
            ir.setAuthInfo(auth);
            ir.getBindingKey().add(policy.getBindingKey());
            BindingDetail irr = null;
            try {
                irr = inquiry.getBindingDetail(ir);
            } catch (DispositionReportFaultMessage ex) {
                log.log(Level.ERROR, "Could not publish data for service " + sp.getURL() + " to UDDI binding key " + policy.getBindingKey() + dispositionReportFaultMessageToString(ex), ex);
                return;
            } catch (Exception ex) {
                log.log(Level.ERROR, "Could not publish data for service " + sp.getURL() + " to UDDI binding key " + policy.getBindingKey(), ex);
                return;
            }
            if (irr == null || irr.getBindingTemplate() == null || irr.getBindingTemplate().isEmpty()) {
                log.log(Level.ERROR, "Could not publish data for service " + sp.getURL() + " to UDDI binding key " + policy.getBindingKey() + ". The UDDI server didn't return any binding templates matching the provided key");
                return;
            }
            if (irr.getBindingTemplate().size() > 1) {
                log.log(Level.WARN, "The UDDI returned more than one binding template for the  service " + sp.getURL() + " to UDDI binding key " + policy.getBindingKey() + ". Only the first binding will be modified");
                return;
            }

            if (data == null) {
                log.log(Level.ERROR, "No quick stat data is available for " + sp.getURL() + ", unable to publish");
                return;
            }
            //get all publishable fgsms data by the policy's settings
            List<TModelInstanceInfo> kr = new ArrayList<TModelInstanceInfo>();

            for (int aa = 0; aa < policy.getPublishTimeRange().size(); aa++) {

                //5 min 300000
                //15  900000
                // 1hr 3600000
                //24hr 86400000
                long time = Utility.durationToTimeInMS(policy.getPublishTimeRange().get(aa));

                QuickStatData localdata = getRecord(data, time);
                if (localdata == null) {
                    log.log(Level.WARN, "No quick stat data for the time period " + time + " for the service " + sp.getURL() + " is available, skipping.");
                    continue;
                }
                if (time == 300000) {

                    if (policy.isPublishSuccessCount()) {
                        String val = (new Long(localdata.getSuccessCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = (NaN);
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelSuccessCount5min, val, PublicationConstants.tmodelSuccessText));
                    }
                    if (policy.isPublishFailureCount()) {
                        String val = (new Long(localdata.getFailureCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = (NaN);
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelFailureCount5min, val, PublicationConstants.tmodelFailureCountText));

                        val = new String();

                        if (localdata.getMTBF() != null) {
                            val = (Utility.durationToString(localdata.getMTBF()));
                        } else {
                            val = (NaN);
                        }
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = (NaN);
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMTBF5min, val, PublicationConstants.tmodelMTBFText));

                    }
                    if (policy.isPublishSLAFaults()) {
                        String val = (new Long(localdata.getSLAViolationCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = (NaN);
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelSLAViolations5min, val, PublicationConstants.tmodelSLAViolationsText));

                    }

                    if (policy.isPublishAverageResponseTime()) {
                        String val = (new Long(localdata.getAverageResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelAverageResponseTime5min, val,PublicationConstants.tmodelAverageResponseTimeText ));
                    }

                    if (policy.isPublishUpTimePercent()) {

                        String val = (new Long(localdata.getAverageResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelUpDownTimePercentage5min, val, PublicationConstants.tmodelUpDownTimePercentageText));
                    }

                    if (policy.isPublishMaximums()) {

                        String val = (new Long(localdata.getMaximumRequestSize()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxRequestSize5min, val, PublicationConstants.tmodelMaxRequestSizeText));

                        val = (new Long(localdata.getMaximumResponseSize()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxResponseSize5min, val, PublicationConstants.tmodelMaxResponseSizeText));

                        val = (new Long(localdata.getMaximumResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxresponseTime5min, val, PublicationConstants.tmodelMaxresponseTimeText));

                    }
                } else if (time == 900000) {

                    if (policy.isPublishSuccessCount()) {

                        String val = (new Long(localdata.getSuccessCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelSuccessCount15min, val, PublicationConstants.tmodelSuccessText));
                    }
                    if (policy.isPublishFailureCount()) {

                        String val = (new Long(localdata.getFailureCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelFailureCount15min, val, PublicationConstants.tmodelFailureCountText));

                        if (localdata.getMTBF() != null) {
                            val = (Utility.durationToString(localdata.getMTBF()));
                        } else {
                            val = NaN;
                        }
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMTBF15min, val, PublicationConstants.tmodelMTBFText));

                    }
                    if (policy.isPublishSLAFaults()) {

                        String val = (new Long(localdata.getSLAViolationCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelSLAViolations15min, val, PublicationConstants.tmodelSLAViolationsText));

                    }

                    if (policy.isPublishAverageResponseTime()) {

                        String val = (new Long(localdata.getAverageResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelAverageResponseTime15min, val,PublicationConstants.tmodelAverageResponseTimeText ));
                    }

                    if (policy.isPublishUpTimePercent()) {

                        String val = (new Long(localdata.getAverageResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelUpDownTimePercentage15min, val, PublicationConstants.tmodelUpDownTimePercentageText));
                    }

                    if (policy.isPublishMaximums()) {

                        String val = (new Long(localdata.getMaximumRequestSize()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxRequestSize15min, val, PublicationConstants.tmodelMaxRequestSizeText));

                        val = (new Long(localdata.getMaximumResponseSize()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxResponseSize15min, val, PublicationConstants.tmodelMaxResponseSizeText));

                        val = (new Long(localdata.getMaximumResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxresponseTime15min, val, PublicationConstants.tmodelMaxresponseTimeText));

                    }
                } else if (time == 3600000) {

                    if (policy.isPublishSuccessCount()) {
                        String val = (new Long(localdata.getSuccessCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelSuccessCount60min, val, PublicationConstants.tmodelSuccessText));
                    }
                    if (policy.isPublishFailureCount()) {

                        String val = (new Long(localdata.getFailureCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelFailureCount60min, val, PublicationConstants.tmodelFailureCountText));

                        if (localdata.getMTBF() != null) {
                            val = (Utility.durationToString(localdata.getMTBF()));
                        } else {
                            val = NaN;
                        }
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMTBF60min, val, PublicationConstants.tmodelMTBFText));

                    }
                    if (policy.isPublishSLAFaults()) {

                        String val = (new Long(localdata.getSLAViolationCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelSLAViolations60min, val, PublicationConstants.tmodelSLAViolationsText));

                    }

                    if (policy.isPublishAverageResponseTime()) {

                        String val = (new Long(localdata.getAverageResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelAverageResponseTime60min, val,PublicationConstants.tmodelAverageResponseTimeText ));
                    }

                    if (policy.isPublishUpTimePercent()) {

                        String val = (new Long(localdata.getAverageResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelUpDownTimePercentage60min, val, PublicationConstants.tmodelUpDownTimePercentageText));
                    }

                    if (policy.isPublishMaximums()) {

                        String val = (new Long(localdata.getMaximumRequestSize()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxRequestSize60min, val, PublicationConstants.tmodelMaxRequestSizeText));

                        val = (new Long(localdata.getMaximumResponseSize()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxResponseSize60min, val, PublicationConstants.tmodelMaxResponseSizeText));

                        val = (new Long(localdata.getMaximumResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxresponseTime60min, val, PublicationConstants.tmodelMaxresponseTimeText));

                    }
                } else if (time == 86400000) {

                    if (policy.isPublishSuccessCount()) {

                        String val = (new Long(localdata.getSuccessCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelSuccessCount24hr, val, PublicationConstants.tmodelSuccessText));
                    }
                    if (policy.isPublishFailureCount()) {

                        String val = (new Long(localdata.getFailureCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelFailureCount24hr, val, PublicationConstants.tmodelFailureCountText));

                        if (localdata.getMTBF() != null) {
                            val = (Utility.durationToString(localdata.getMTBF()));
                        } else {
                            val = NaN;
                        }
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMTBF24hr, val, PublicationConstants.tmodelMTBFText));
                        // kr.add(CreateTII(PublicationConstants.tmodelAverageResponseTime5min, val));

                    }
                    if (policy.isPublishSLAFaults()) {

                        String val = (new Long(localdata.getSLAViolationCount()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelSLAViolations24hr, val, PublicationConstants.tmodelSLAViolationsText));

                    }

                    if (policy.isPublishAverageResponseTime()) {

                        String val = (new Long(localdata.getAverageResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelAverageResponseTime24hr, val,PublicationConstants.tmodelAverageResponseTimeText ));
                    }

                    if (policy.isPublishUpTimePercent()) {

                        String val = (new Long(localdata.getAverageResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelUpDownTimePercentage24hr, val, PublicationConstants.tmodelUpDownTimePercentageText));
                    }

                    if (policy.isPublishMaximums()) {

                        String val = (new Long(localdata.getMaximumRequestSize()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxRequestSize24hr, val, PublicationConstants.tmodelMaxRequestSizeText));

                        val = (new Long(localdata.getMaximumResponseSize()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxResponseSize24hr, val, PublicationConstants.tmodelMaxResponseSizeText));

                        val = (new Long(localdata.getMaximumResponseTime()).toString());
                        if (Utility.stringIsNullOrEmpty(val)) {
                            val = NaN;
                        }
                        kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelMaxresponseTime24hr, val, PublicationConstants.tmodelMaxresponseTimeText));

                    }
                }
            }

            /*
             * 
             * item.setTModelKey(PublicationConstants.tmodelTimeRange);
             * item.setKeyName(PublicationConstants.tmodelTimeRange);
             * item.setKeyValue(response.getPolicies().getValue().getServicePolicy().get(i).getPublishToUDDITimeRange().getDays()
             * + " days " +
             * response.getPolicies().getValue().getServicePolicy().get(i).getPublishToUDDITimeRange().getHours()
             * + " hrs " +
             * response.getPolicies().getValue().getServicePolicy().get(i).getPublishToUDDITimeRange().getMinutes()
             * + " mins"); kr.add(CreateTII(PublicationConstants.tmodelAverageResponseTime5min, val));
             */
            String val = (GetMonitoredStatusFromDB(sp.getURL(), isPooled));
            if (Utility.stringIsNullOrEmpty(val)) {
                val = NaN;
            }
            kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelOperationalMonitoredBy, val, PublicationConstants.tmodelOperationalMonitoredByText));

            
            Calendar xcal = ((GregorianCalendar) GregorianCalendar.getInstance());
            if (Utility.stringIsNullOrEmpty(val)) {
                val = NaN;
            } else {
                val = Utility.formatDateTime(xcal.getTime());
            }
            kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelPublishTimeStamp, val, PublicationConstants.tmodelPublishTimeStampText));

            if (policy.isPublishLastKnownStatus()) {

                val = (getOperationalStatusFromDB(sp.getURL(), isPooled));
                if (Utility.stringIsNullOrEmpty(val)) {
                    val = NaN;
                }
                kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelOperationalStatus, val, PublicationConstants.tmodelOperationalStatusText));

                val = (getOperationalStatusTimeStamp(sp.getURL(), isPooled));
                if (Utility.stringIsNullOrEmpty(val)) {
                    val = NaN;
                }
                kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelOperationalStatusTimeStamp, val, PublicationConstants.tmodelOperationalStatusTimeStampText));
            }
            if (policy.isPublishTimeSinceLastStatusChange()) {

                val = (getLastChangeTimeStamp(sp.getURL(), isPooled));
                if (Utility.stringIsNullOrEmpty(val)) {
                    val = NaN;
                }
                kr.add(createTmodelInstnaceInfo(PublicationConstants.tmodelOperationalStatuschange, val,PublicationConstants.tmodelOperationalStatuschangeText));
            }

            //situation #1, this service has never had fgsms performance data added
            if (irr.getBindingTemplate().get(0).getTModelInstanceDetails() == null || irr.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo() == null
                    || irr.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().isEmpty()) {
                //nothing special to do here
                irr.getBindingTemplate().get(0).setTModelInstanceDetails(new TModelInstanceDetails());
            } else {
                //situation #2, this service already had open esm performmance data or has some other data in the tModel instance info
                //ignore old fgsms data, preserve existing entries
                for (int k4 = 0; k4 < irr.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().size(); k4++) {
                    String j = irr.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().get(k4).getTModelKey();
                    if (!TmodelKeyIsfgsms(j)) {
                        kr.add(irr.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().get(k4));
                    }

                }
            }
            irr.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().addAll(kr);

            SaveBinding sb = new SaveBinding();
            sb.setAuthInfo(auth);
            sb.getBindingTemplate().add(irr.getBindingTemplate().get(0));
            publication.saveBinding(sb);
            log.log(Level.INFO, "fgsms performance data for " + sp.getURL() + " has been successfully published to UDDI.");
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "PublishUddiData", "UddiPublisher", sp.getURL() + " publishing successful", AuditLogger.unspecified, null);
            }
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, "error saving publication data " + dispositionReportFaultMessageToString(ex), ex);
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "PublishUddiData", "UddiPublisher", sp.getURL() + " publishing failed", AuditLogger.unspecified, null);
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "error saving publication data", ex);
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "PublishUddiData", "UddiPublisher", sp.getURL() + " publishing failed", AuditLogger.unspecified, null);
            }
        }

    }

    @Override
    public String GetDisplayName() {
        return "UDDI Metrics Publisher";
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "This federation plugin will expose data for the given service via a web service request using the UDDI v3 specification using WSDM QoS parameters. A UDDI v3"
                + "compliant server is required that supports at least the Inquiry and Publicaion endpoints. This plugin attaches tModel Instance Info data elements to the specified "
                + "binding template key. This information is published periodically (administratively controlled).<br><br>"
                + "<ul>"
                + "<li>" + OPTION_PUBLISH_TIME_RANGE + " - Required, a comma delimted time range in millseconds. It must be one or more of the following"
                + "<ul><li>5 min = 300000</li>" + "                <li>15min = 900000</li>" + "                <li>1hr = 3600000</li>" + "                <li>24hr = 86400000</li>" + "</ul></li>"
                + "<li>" + OPTION_BINDING_KEY + " - Required. This is the key of the binding template in UDDI from which data will be attached</li>"
                + "<li>" + OPTION_PUBLISH_AVG_RES_TIME + " - Optional, valid values =true/false. Publishes average response time. Default = false</li>"
                + "<li>" + OPTION_PUBLISH_FAULTS + " - Optional, valid values =true/false. Publishes fault counts and MTBF. Default = false</li>"
                + "<li>" + OPTION_PUBLISH_MAX + " - Optional, valid values =true/false. Publishes the maximum response times and message sizes. Default = false</li>"
                + "<li>" + OPTION_PUBLISH_SLA + " - Optional, valid values =true/false. Publishes SLA violations count. Default = false</li>"
                + "<li>" + OPTION_PUBLISH_STATUS + " - Optional, valid values =true/false. Publishes last known status and the time stamp. Default = false</li>"
                + "<li>" + OPTION_PUBLISH_SUCCESS + " - Optional, valid values =true/false. Publishes successful message counts. Default = false</li>"
                + "<li>" + OPTION_PUBLISH_UPTIME + " - Optional, valid values =true/false. Publishes Up/Down time information. Default = false</li>"
                + "</ul>";
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> ret = new ArrayList<NameValuePair>();
        ret.add(Utility.newNameValuePair(OPTION_PUBLISH_TIME_RANGE, null, false, false));
        ret.add(Utility.newNameValuePair(OPTION_BINDING_KEY, null, false, false));
        return ret;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        ArrayList<NameValuePair> ret = new ArrayList<NameValuePair>();
        ret.add(Utility.newNameValuePair(OPTION_PUBLISH_AVG_RES_TIME, null, false, false));
        ret.add(Utility.newNameValuePair(OPTION_PUBLISH_FAULTS, null, false, false));
        ret.add(Utility.newNameValuePair(OPTION_PUBLISH_MAX, null, false, false));
        ret.add(Utility.newNameValuePair(OPTION_PUBLISH_SLA, null, false, false));
        ret.add(Utility.newNameValuePair(OPTION_PUBLISH_STATUS, null, false, false));
        ret.add(Utility.newNameValuePair(OPTION_PUBLISH_SUCCESS, null, false, false));
        ret.add(Utility.newNameValuePair(OPTION_PUBLISH_UPTIME, null, false, false));
        //ret.add(Utility.newNameValuePair(OPTION_PUBLISH_USERNAME, null, false, true));
        //ret.add(Utility.newNameValuePair(OPTION_PUBLISH_USERNAME, null, false, true));
        return ret;
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        List<PolicyType> ret = new ArrayList<PolicyType>();
        ret.add(PolicyType.STATUS);
        ret.add(PolicyType.TRANSACTIONAL);
        return ret;
    }

    private TModelInstanceInfo createTmodelInstnaceInfo(String key, String value, String description) {
        TModelInstanceInfo r = new TModelInstanceInfo();
        r.setTModelKey(key);
        r.getDescription().add(new Description(description, PublicationConstants.lang));
        r.setInstanceDetails(new InstanceDetails());
        r.getInstanceDetails().setInstanceParms(value);
        return r;
    }

    /**
     * adds an fgsms agent to the request/response handler chains
     * @param bpPCS 
     */
     public static void addMontior(BindingProvider bpPCS) {
          if (bpPCS==null)return;
         List<Handler> handlerChain = bpPCS.getBinding().getHandlerChain();
         if (handlerChain==null)
              handlerChain = new ArrayList<Handler>();
         for (int i=0; i < handlerChain.size(); i++){
              if (handlerChain.get(i) instanceof JAXWSGenericClientAgent)
                   return;
              if (handlerChain.get(i) instanceof JAXWSGenericAgent)
                   return;
         }
         handlerChain.add(new JAXWSGenericClientAgent());
          bpPCS.getBinding().setHandlerChain(handlerChain);
          
         
          
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

    private String loginWrapper() {

        return login(username, password);
    }

    /*
     * default: once per day
     */
    public long getUddiPublishInterval() {
        try {
            KeyNameValueEnc p = DBSettingsLoader.GetPropertiesFromDB(isPooled, "UddiPublisher", "Interval");
            if (p != null) {
                String t = p.getKeyNameValue().getPropertyValue();
                long l = Long.parseLong(t);
                return l;
            }
        } catch (Exception ex) {
        }
        return 24 * 60 * 60 * 1000;
    }

    private String login(String username, String password) {
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
    /// 
    /// Checks to see if the open esm tmodels have been registered with the uddi registry.
    /// if they haven't been, they are published
    /// 

    private boolean checkTmodelPublication() {
        GetTModelDetail req = new GetTModelDetail();
        req.setAuthInfo(loginWrapper());
        String[] items = PublicationConstants.getAllTmodelKeys();
        for (int i = 0; i < items.length; i++) {
            req.getTModelKey().add(items[i]);
        }

        TModelDetail response = null;
        try {
            response = inquiry.getTModelDetail(req);
        } catch (DispositionReportFaultMessage f) {
            log.log(Level.WARN, "error caught probing for tmodels " + dispositionReportFaultMessageToString(f), f);
        } catch (Exception ex) {
        }
        if (response == null || response.getTModel() == null || response.getTModel().size() < items.length) {
            log.log(Level.WARN, "Some or all of the fgsms tModels are not present, publishing....");
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "CheckTmodelPublication", "UddiPublisher", "Publishing tModel definitions", AuditLogger.unspecified, null);
            }
            return publishTmodels();
        } else {
            log.log(Level.INFO, "All fgsms tModels are present");
        }
        return true;

    }
    
   

    private boolean publishTmodels() {

        //what happens if one or more of the tmodels already exists and we attempt to republish them?
        //openuddi doesn't seem to care, nor does systinet
        org.uddi.api_v3.ObjectFactory fac = new org.uddi.api_v3.ObjectFactory();
        TModel t;
        Name n;
        Description d;
        CategoryBag c;

        KeyedReference keygen = new KeyedReference();
        keygen.setTModelKey("uddi:uddi.org:categorization:types");
        keygen.setKeyName("keyGenerator");
        keygen.setKeyValue("keyGenerator");

        SaveTModel request = new SaveTModel();
        request.setAuthInfo(loginWrapper());
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelKeyGen);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelKeyGenText);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelKeyGenText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((keygen));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        try {
            publication.saveTModel(request);
            log.log(Level.INFO, "successfullly created the fgsms UDDI Key Geneator");
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "PublishTmodels", "UddiPublisher", "fgsms Key Generator Published", AuditLogger.unspecified, null);
            }
        } catch (DispositionReportFaultMessage f) {

            log.log(Level.ERROR, "unable to set keygenerator tmodel" + dispositionReportFaultMessageToString(f), f);
        } catch (Exception ex) {
            log.log(Level.ERROR, "unable to set keygenerator tmodel", ex);
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "PublishTmodels", "UddiPublisher", "fgsms Key Generator Failed", AuditLogger.unspecified, null);
            }
            return false;

        }

        KeyedReference shared = new KeyedReference();
        shared.setTModelKey("uddi:uddi.org:categorization:types");
        shared.setKeyName("uddi-org:types:categorization");
        shared.setKeyValue("categorization");

        request = new SaveTModel();
        request.setAuthInfo(loginWrapper());

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelSLAViolations5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelSLAViolationsDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelSLAViolationsText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelSLAViolations15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelSLAViolationsDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelSLAViolationsText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelSLAViolations60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelSLAViolationsDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelSLAViolationsText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelSLAViolations24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelSLAViolationsDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelSLAViolationsText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelSuccessCount5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelSuccessCountDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelSuccessText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        //////////////////////////////////////

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelSuccessCount15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelSuccessCountDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelSuccessText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        //////////////////////////////////////

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelSuccessCount60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelSuccessCountDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelSuccessText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        //////////////////////////////////////

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelSuccessCount24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelSuccessCountDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelSuccessText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        //////////////////////////////////////

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelFailureCount5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelFailureCountDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelFailureCountText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelFailureCount15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelFailureCountDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelFailureCountText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelFailureCount60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelFailureCountDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelFailureCountText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelFailureCount24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelFailureCountDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelFailureCountText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMTBF5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMTBFDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMTBFText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMTBF15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMTBFDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMTBFText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMTBF60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMTBFDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMTBFText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMTBF24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMTBFDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMTBFText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
//availability5min
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelUpDownTimePercentage5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelUpDownTimePercentageDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelUpDownTimePercentage5min);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelUpDownTimePercentage15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelUpDownTimePercentageDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelUpDownTimePercentage15min);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelUpDownTimePercentage60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelUpDownTimePercentageDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelUpDownTimePercentage60min);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelUpDownTimePercentage24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelUpDownTimePercentageDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelUpDownTimePercentage24hr);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxResponseSize5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxResponseSizeDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxResponseSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxResponseSize15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxResponseSizeDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxResponseSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxResponseSize60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxResponseSizeDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxResponseSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxResponseSize24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxResponseSizeDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxResponseSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxresponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxRequestSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxRequestSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxRequestSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxresponseTime5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxresponseTimeDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxresponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxresponseTime15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxresponseTimeDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxresponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxresponseTime60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxresponseTimeDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxresponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxresponseTime24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxresponseTimeDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxresponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxRequestSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        //////////////////////////////////////

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxRequestSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        //////////////////////////////////////

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxRequestSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        //////////////////////////////////////

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxRequestSizeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);
        //////////////////////////////////////

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelAverageResponseTime5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelAverageResponseTimeDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelAverageResponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelAverageResponseTime15min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelAverageResponseTimeDescription15min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelAverageResponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelAverageResponseTime60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelAverageResponseTimeDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelAverageResponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxresponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelMaxRequestSize5min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelMaxRequestSizeDescription5min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelMaxresponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelAverageResponseTime60min);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelAverageResponseTimeDescription60min);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelAverageResponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelAverageResponseTime24hr);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelAverageResponseTimeDescription24hr);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelAverageResponseTimeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelTimeRange);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelTimeRangeDescription);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelTimeRangeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

//////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelOperationalMonitoredBy);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelOperationalMonitoredByDescription);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelOperationalMonitoredByDescription);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelOperationalStatusTimeStamp);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelOperationalStatusTimeStampDescription);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelOperationalStatusTimeStampText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelOperationalStatus);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelOperationalStatusDescription);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelOperationalStatusText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelOperationalStatuschange);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelOperationalStatuschangeDescription);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelOperationalStatuschangeText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        //////////////////////////////////////
        t = new TModel();
        t.setTModelKey(PublicationConstants.tmodelPublishTimeStamp);
        n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.tmodelPublishTimeStampDescription);
        t.setName(n);
        d = new Description();
        d.setLang(PublicationConstants.lang);
        d.setValue(PublicationConstants.tmodelPublishTimeStampText);
        t.getDescription().add(d);
        c = new CategoryBag();
        c.getKeyedReference().add((shared));
        t.setCategoryBag(c);
        request.getTModel().add(t);

        try {
            TModelDetail response = publication.saveTModel(request);
            log.log(Level.INFO, "successfullly created the fgsms UDDI tModels");
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "PublishTmodels", "UddiPublisher", "fgsms tModels Published", AuditLogger.unspecified, null);
            }
            return true;
        } catch (DispositionReportFaultMessage f) {
            log.log(Level.ERROR, "Error publishing tmodel definitions: " + dispositionReportFaultMessageToString(f), f);
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "PublishTmodels", "UddiPublisher", "fgsms tModel Publishing failed", AuditLogger.unspecified, null);
            }
            return false;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error publishing tmodel definitions: ", ex);
            if (isPooled) {
                AuditLogger.logItem(this.getClass().getCanonicalName(), "PublishTmodels", "UddiPublisher", "fgsms tModel Publishing failed", AuditLogger.unspecified, null);
            }
            return false;
        }

    }
    private static final String NaN = "NaN";
    public static final String FederationTarget_UDDI_Publisher = "UDDI";


    /// 
    /// testing purposes only
    /// 
    protected boolean publishTestPublisher() {
        try {
            SaveBusiness sb1 = new SaveBusiness();
            //List<BusinessEntity> biz = new ArrayList<BusinessEntity>();
            BusinessEntity e = new BusinessEntity();
            e.setBusinessKey("uddi:org.miloss.fgsms:test");
            Name n = new Name();
            n.setValue("Test Business Entity");
            n.setLang(PublicationConstants.lang);
            e.getName().add(n);
            // biz.add(e);
            sb1.getBusinessEntity().add(e);
            sb1.setAuthInfo(loginWrapper());
            publication.saveBusiness(sb1);
            return true;
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, "error publising test business entity", ex);
            log.log(Level.ERROR, "error publising test business entity" + dispositionReportFaultMessageToString(ex), ex);
            return false;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error publising test business entity", ex);
            return false;
        }
    }

    /// 
    /// testing purposes only
    /// 
    protected boolean publishTestBusiness() {
        try {
            SaveBusiness sb1 = new SaveBusiness();
            //List<BusinessEntity> biz = new ArrayList<BusinessEntity>();
            BusinessEntity e = new BusinessEntity();
            e.setBusinessKey("uddi:org:miloss:fgsms:test");
            Name n = new Name();
            n.setValue("Test Business Entity");
            n.setLang(PublicationConstants.lang);
            e.getName().add(n);
            // biz.add(e);
            sb1.getBusinessEntity().add(e);
            sb1.setAuthInfo(loginWrapper());
            publication.saveBusiness(sb1);
            return true;
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, "error publising test business entity", ex);
            log.log(Level.ERROR, "error publising test business entity" + dispositionReportFaultMessageToString(ex), ex);
            return false;
        } catch (Exception ex) {
            log.log(Level.ERROR, "error publising test business entity", ex);
            return false;
        }
    }
    /// 
    /// testing purposes only
    /// 

    protected boolean publishTestService() {
        try {
            SaveService sb1 = new SaveService();
            BusinessService s = new BusinessService();
            s.setBusinessKey("uddi:org:miloss:fgsms:test");
            Name n = new Name();
            n.setLang(PublicationConstants.lang);
            n.setValue("Test Service fgsms DAS");
            s.getName().add(n);
            s.setServiceKey("uddi:org:miloss:fgsms:test:service1");
            BindingTemplates l = new BindingTemplates();
            BindingTemplate bt = new BindingTemplate();
            bt.setBindingKey("uddi:fgsms.DAS");
            // bt.
            AccessPoint ap = new AccessPoint();
            ap.setValue("http://localhost:8080/DAS/DAS4jBean");
            bt.setAccessPoint(ap);
            l.getBindingTemplate().add(bt);
            s.setBindingTemplates(l);
            sb1.getBusinessService().add(s);
            /*
             * sb1.businessService[0].categoryBag = new categoryBag2();
             * keyedReference2[] cat = new keyedReference2[4]; cat[0] = new
             * keyedReference2(); cat[0].keyName =
             * tmodelSuccessCountDescription; cat[0].tModelKey =
             * tmodelSuccessCount; cat[0].keyValue = "42"; cat[1] = new
             * keyedReference2(); cat[1].keyName =
             * tmodelFailureCountDescription; cat[1].tModelKey =
             * tmodelFailureCount; cat[1].keyValue = "1"; cat[2] = new
             * keyedReference2(); cat[2].keyName = tmodelTimeRangeDescription;
             * cat[2].tModelKey = tmodelTimeRange; cat[2].keyValue = "30 days";
             * cat[3] = new keyedReference2(); cat[3].keyName =
             * tmodelAverageResponseTimeDescription; cat[3].tModelKey =
             * tmodelAverageResponseTime; cat[3].keyValue = "69 ms";
             * sb1.businessService[0].categoryBag.Items = cat; //oil
             */
            sb1.setAuthInfo(loginWrapper());
            publication.saveService(sb1);
            log.log(Level.INFO, "Service saved successfully");
            return true;
        } catch (DispositionReportFaultMessage ex) {
            log.log(Level.ERROR, "Error saving test service", ex);
            log.log(Level.ERROR, "Error saving test service" + dispositionReportFaultMessageToString(ex), ex);
            return false;
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error saving test service", ex);
            
            return false;
        }
    }

    private String getOperationalStatusFromDB(String uRL, boolean pooled) {
        try {

              Connection configCon=Utility.getConfigurationDBConnection();
            PreparedStatement com = configCon.prepareStatement("select status from status where uri=?");
            com.setString(1, uRL);
            ResultSet rs = com.executeQuery();
            if (rs.next()) {
                Boolean s = rs.getBoolean("status");
                rs.close();
                com.close();
                //     con.close();
                if (s) {
                    return "Operational";
                }
                //  return s;
                return "Unavailable";
            } else {
                rs.close();
                com.close();
                //   con.close();
                return "Status Unavailable";
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error getting operational status from db", ex);
            return "Status Unavailable";
        }
    }

    private String GetMonitoredStatusFromDB(String uRL, boolean pooled) {
        try {
             Connection con = Utility.getConfigurationDBConnection();
            PreparedStatement com = con.prepareStatement("select monitored from status where uri=?");
            com.setString(1, uRL);
            ResultSet rs = com.executeQuery();
            if (rs.next()) {
                boolean s = rs.getBoolean("monitored");
                rs.close();
                com.close();
                  con.close();
                if (s) {
                    return "true";
                } else {
                    return "false";
                }
            } else {
                rs.close();
                com.close();
                   con.close();
                return "false";
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "Error getting monitored status from db", ex);
            return "false";
        }
    }

    private String getOperationalStatusTimeStamp(String uRL, boolean pooled) {
        try {
                 Connection con = Utility.getConfigurationDBConnection();
            PreparedStatement com = con.prepareStatement("select utcdatetime from status where uri=?");
            com.setString(1, uRL);
            ResultSet rs = com.executeQuery();
            if (rs.next()) {
                Long timeinms = rs.getLong("utcdatetime");
                GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
                cal.setTimeInMillis(timeinms);
                
                Calendar xcal = (cal);

                rs.close();
                com.close();
                 con.close();
                if (xcal != null) {
                    return xcal.toString();
                }
            } else {
                rs.close();
                com.close();
                con.close();
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "error getting operational status timestamp from db", ex);

        }
        return "Operational Status Timestamp Unavailable";
    }

 
    protected void inquiry(String servicekey) {
        try {
            GetServiceDetail r = new GetServiceDetail();
            r.getServiceKey().add(servicekey);
            ServiceDetail serviceDetail = inquiry.getServiceDetail(r);
            if (serviceDetail != null && serviceDetail.getBusinessService() != null
                    && !serviceDetail.getBusinessService().isEmpty()) {
                JAXBContext jc = JAXBContext.newInstance(ServiceDetail.class);
                //JAXB.marshal(jc, null);
                // Unmarshaller u = jc.createUnmarshaller();
                //  Object element = u.unmarshal(new File("foo.xml"));

                //note, this doesn't work
                Marshaller m = jc.createMarshaller();
                StringWriter sw = new StringWriter();
                m.marshal(serviceDetail, sw);
                //Marshalling to a File:
                log.log(Level.INFO, "Results" + sw.toString());

            } else {
                log.log(Level.WARN, "no results return ");
                //return "";
            }

        } catch (Exception ex) {
            Logger.getLogger(UddiPublisher.class.getName()).log(Level.ERROR, null, ex);
        }
        //  return "";
    }

    
   
    private static String dispositionReportFaultMessageToString(DispositionReportFaultMessage f) {
        StringBuilder err = new StringBuilder();
        if (f != null && f.getFaultInfo() != null) {
            for (int i = 0; i < f.getFaultInfo().getResult().size(); i++) {
                err.append("Error number" + f.getFaultInfo().getResult().get(i).getErrno());
                if (f.getFaultInfo().getResult().get(i).getKeyType() != null) {
                    err.append(" keytype " + f.getFaultInfo().getResult().get(i).getKeyType().value());
                }
                if (f.getFaultInfo().getResult().get(i).getErrInfo() != null) {
                    err.append(
                            " errocode " + f.getFaultInfo().getResult().get(i).getErrInfo().getErrCode()
                            + " msg " + f.getFaultInfo().getResult().get(i).getErrInfo().getValue());
                }
            }
        }
        return err.toString();
    }
}
