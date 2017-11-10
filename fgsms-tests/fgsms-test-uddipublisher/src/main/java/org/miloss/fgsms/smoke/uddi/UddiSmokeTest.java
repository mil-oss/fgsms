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
package org.miloss.fgsms.smoke.uddi;

import java.io.File;
import java.util.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import org.apache.juddi.v3.client.UDDIService;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.common.PublicationConstants;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.DCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.apache.log4j.*;
import org.uddi.api_v3.*;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author AO
 */
public class UddiSmokeTest {

    static Logger log = Logger.getLogger("hi");

    static final String UddiPublisher = "UddiPublisher";
    static final String url = "http://" + Utility.getHostName() + ":12345/UddiPublicationTest";
    static PCS pcsport = null;
    private static String names[] = new String[]{"Interval", "Enabled", "Username", "password", "AuthMode", "ClientCertRequired", "IgnoreSSLErrors", "InquiryURL", "PublishURL",
        "SecurityURL"};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new UddiSmokeTest().go();
    }

    private boolean isMsUDDI = false;
    private String bizkey = null;
    private String username = "fgsmsadmin";
    private String password = "";
    private UDDIPublicationPortType publication;
    private UDDISecurityPortType security;
    private UDDIInquiryPortType inquiry;
    private org.miloss.fgsms.agentcore.ConfigLoader cfg = null;

    private void go() throws Exception {

        UDDIService c = new UDDIService();

        publication = c.getUDDIPublicationPort();
        BindingProvider bp = (BindingProvider) publication;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/juddiv3/services/publish");

        security = c.getUDDISecurityPort();
        bp = (BindingProvider) security;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/juddiv3/services/security");
        boolean PublishTestPublisher = this.PublishTestPublisher();
        if (PublishTestPublisher) {
            System.exit(1);
        }

        Pair<String, String> s1 = this.PublishTestService();
        boolean PublishTestService = (s1 == null);
        if (PublishTestService) {
            System.exit(1);
        }
        System.exit(0);
    }

    private void Test_SetupProxies(Properties p) throws ConfigurationException {
        cfg = new ConfigLoader();
        username = p.getProperty("uddi.username");
        password = p.getProperty("uddi.password");

        UDDIService c = new UDDIService();

        publication = c.getUDDIPublicationPort();
        BindingProvider bp = (BindingProvider) publication;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, p.getProperty("uddi.publish"));

        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, p.getProperty("uddi.username"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, p.getProperty("uddi.password"));

        if (!Utility.stringIsNullOrEmpty(p.getProperty("uddi.security"))) {

            security = c.getUDDISecurityPort();
            bp = (BindingProvider) security;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, p.getProperty("uddi.security"));

        }

        inquiry = c.getUDDIInquiryPort();
        bp = (BindingProvider) inquiry;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, p.getProperty("uddi.inquiry"));

        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, p.getProperty("uddi.username"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, p.getProperty("uddi.password"));

        pcsport = cfg.getPcsport();
        bp = (BindingProvider) pcsport;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, p.getProperty("fgsmsadminuser"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, p.getProperty("fgsmsadminpass"));
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getPCS_URLS().get(0));

        if (Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStore")) || Utility.stringIsNullOrEmpty(System.getProperty("javax.net.ssl.trustStorePassword"))) {
            try {
                System.out.println("setting trust store settings");
                System.setProperty("javax.net.ssl.trustStorePassword", Utility.DE(cfg.getJavaxtruststorepass()));
                System.setProperty("javax.net.ssl.trustStore", (cfg.getJavaxtruststore()));
            } catch (Exception ex) {
                log.log(Level.WARN, "error caught when referencing (get or set) System.properties for SSL communication. Check to ensure that this is enabled in your JAAS managemer" + ex.getMessage());
            }
        }
        //fix for sun's ri ws stack
        File f = new File("truststore.jks");
        if (f.exists()) {
            System.out.println("switching keystores to local file");
            System.setProperty("javax.net.ssl.trustStore", f.getAbsolutePath());
        }
        System.out.println("using " + p.getProperty("username") + " " + p.getProperty("password"));

    }

    private void Test_Setfgsms_UDDI_Publish_Settings(Properties p) throws Exception {
        RemovePublisherSettings();
        System.out.println("Configuring publication settings");
        SetGeneralSettingsRequestMsg req1 = new SetGeneralSettingsRequestMsg();
        KeyNameValueEnc knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(false);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[0]);
        knv.getKeyNameValue().setPropertyValue("30000");
        req1.getKeyNameValueEnc().add(knv);
        knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(false);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[1]);
        knv.getKeyNameValue().setPropertyValue("true");
        req1.getKeyNameValueEnc().add(knv);
        knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(false);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[2]);
        knv.getKeyNameValue().setPropertyValue(p.getProperty("uddi.username"));
        req1.getKeyNameValueEnc().add(knv);

        knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(true);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[3]);
        knv.getKeyNameValue().setPropertyValue(p.getProperty("uddi.password"));
        req1.getKeyNameValueEnc().add(knv);

        knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(false);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[4]);
        if (isMsUDDI) {
            knv.getKeyNameValue().setPropertyValue(("Http"));
        } else {
            knv.getKeyNameValue().setPropertyValue(("Uddi"));
        }
        req1.getKeyNameValueEnc().add(knv);

        knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(false);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[5]);
        knv.getKeyNameValue().setPropertyValue(p.getProperty("uddi.clientcert"));
        req1.getKeyNameValueEnc().add(knv);

        knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(false);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[6]);
        knv.getKeyNameValue().setPropertyValue(("true"));
        req1.getKeyNameValueEnc().add(knv);

        knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(false);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[7]);
        knv.getKeyNameValue().setPropertyValue(p.getProperty("uddi.inquiry"));
        req1.getKeyNameValueEnc().add(knv);

        knv = new KeyNameValueEnc();
        knv.setShouldEncrypt(false);
        knv.setKeyNameValue(new KeyNameValue());
        knv.getKeyNameValue().setPropertyKey(UddiPublisher);
        knv.getKeyNameValue().setPropertyName(names[8]);
        knv.getKeyNameValue().setPropertyValue(p.getProperty("uddi.publish"));
        req1.getKeyNameValueEnc().add(knv);

        if (!isMsUDDI) {
            knv = new KeyNameValueEnc();
            knv.setShouldEncrypt(false);
            knv.setKeyNameValue(new KeyNameValue());
            knv.getKeyNameValue().setPropertyKey(UddiPublisher);
            knv.getKeyNameValue().setPropertyName(names[9]);
            knv.getKeyNameValue().setPropertyValue(p.getProperty("uddi.security"));
            req1.getKeyNameValueEnc().add(knv);

        }

        req1.setClassification(new SecurityWrapper());

        pcsport.setGeneralSettings(req1);
        System.out.println("Configuring publication settings completed");

    }

    private void RemovePublisherSettings() throws Exception {
        System.out.println("removing publication settings");
        RemoveGeneralSettingsRequestMsg req = new RemoveGeneralSettingsRequestMsg();
        req.setClassification(new SecurityWrapper());
        for (int i = 0; i < names.length; i++) {
            KeyNameValue knv = new KeyNameValue();
            knv.setPropertyKey(UddiPublisher);
            knv.setPropertyName(names[i]);
            req.getKeyNameValue().add(knv);
        }
        RemoveGeneralSettingsResponseMsg removeGeneralSettings = pcsport.removeGeneralSettings(req);

        GetGeneralSettingsRequestMsg req1 = new GetGeneralSettingsRequestMsg();
        req1.setClassification(new SecurityWrapper());
        req1.setKeyvalue(UddiPublisher);
        GetGeneralSettingsResponseMsg generalSettings = pcsport.getGeneralSettings(req1);
        if (generalSettings.getKeyNameValue().isEmpty()) {
            System.out.println("removing publication settings completed");
        } else {
            System.out.println("removing publication settings FAILED, expected an empty set, but " + generalSettings.getKeyNameValue().size() + " was returned");
            for (int i = 0; i < generalSettings.getKeyNameValue().size(); i++) {
                System.out.println(generalSettings.getKeyNameValue().get(i).getPropertyKey() + "=" + generalSettings.getKeyNameValue().get(i).getPropertyName());
            }
        }
    }

    private void SendData() throws Exception {
        List<AddDataRequestMsg> req = new ArrayList<AddDataRequestMsg>();
        for (int i = 0; i < 10; i++) {
            AddDataRequestMsg r = new AddDataRequestMsg();
            r.setSuccess(false);
            r.setAgentType("jUnit");
            r.setAction("test");
            r.setClassification(new SecurityWrapper());
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setRecordedat((gcal));
            r.setRequestSize(100);
            r.setRequestURI(url);
            r.setResponseSize(100);
            r.setResponseTime(100);
            r.setServiceHost(Utility.getHostName());
            r.setTransactionID(UUID.randomUUID().toString());
            r.setURI(url);
            req.add(r);
        }
        for (int i = 0; i < 10; i++) {
            AddDataRequestMsg r = new AddDataRequestMsg();
            r.setSuccess(true);
            r.setAgentType("jUnit");
            r.setAction("test");
            r.setClassification(new SecurityWrapper());
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            r.setRecordedat((gcal));
            r.setRequestSize(100);
            r.setRequestURI(url);
            r.setResponseSize(100);
            r.setResponseTime(100);
            r.setServiceHost(Utility.getHostName());
            r.setTransactionID(UUID.randomUUID().toString());
            r.setURI(url);
            req.add(r);
        }
        DCS dcs = cfg.getDcsport();
        BindingProvider bp = (BindingProvider) dcs;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getDCS_URLS().get(0));
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, cfg.getUsername());
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.getPassword()));
        System.out.println("Delivering " + 20 + " msgs to dcs");
        dcs.addMoreData(req);
    }

    String GetConfigValue(GetGeneralSettingsResponseMsg g, String name) {
        for (int i = 0; i < g.getKeyNameValue().size(); i++) {
            if (g.getKeyNameValue().get(i).getPropertyName().equalsIgnoreCase(name)) {
                return g.getKeyNameValue().get(i).getPropertyValue();
            }
        }
        return "";
    }

    private void RemovePolicy() throws Exception {
        System.out.println("Removing the policy");
        DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(url);
        req.setDeletePerformanceData(true);
        pcsport.deleteServicePolicy(req);
    }

    private void SetPolicy(String uddiBindingKey) throws ConfigurationException, ServiceUnavailableException, AccessDeniedException, DatatypeConfigurationException {
        System.out.println("Setting up policy");
        TransactionalWebServicePolicy pol = new TransactionalWebServicePolicy();
        pol.setAgentsEnabled(true);
        pol.setBuellerEnabled(false);
        pol.setDataTTL(DatatypeFactory.newInstance().newDuration(600000));
        pol.setHealthStatusEnabled(false);
        pol.setMachineName(Utility.getHostName());
        pol.setPolicyType(PolicyType.TRANSACTIONAL);
        pol.setURL(url);

        pol.setFederationPolicyCollection(new FederationPolicyCollection());
        FederationPolicy fp = new FederationPolicy();
        fp.setImplementingClassName("org.miloss.fgsms.uddipub.UddiPublisher");
        fp.getParameterNameValue().add(Utility.newNameValuePair(username, url, false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("Range", "300000,900000,3600000,1440000", false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("Averages", "true", false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("Success", "true", false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("Faults", "true", false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("Uptime", "true", false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("Maximums", "true", false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("SLA", "true", false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("Status", "true", false, false));
        fp.getParameterNameValue().add(Utility.newNameValuePair("Binding", uddiBindingKey, false, false));
        //fp.getParameterNameValue().add(Utility.newNameValuePair("Password", "true", false, false));
        //fp.getParameterNameValue().add(Utility.newNameValuePair("Username", "true", false, false));

        pol.getFederationPolicyCollection().getFederationPolicy().add(fp);

        SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(pol.getURL());
        req.setPolicy(pol);
        pcsport.setServicePolicy(req);
    }

    public Pair<String, Integer> DoUddiTest(Properties p, boolean IsMsUDDI) throws ConfigurationException, Exception {

        Pair<String, Integer> ret = new Pair<String, Integer>();
        //setup
        isMsUDDI = IsMsUDDI;
        Test_SetupProxies(p);

        dumpAllTestEntities();

        //publsh a test business and service to uddi
        PublishTestPublisher();     //=bizkey
        String bizkey2 = PublishTestBusiness();
        Pair<String, String> svcbtkeys = PublishTestService();
        if (svcbtkeys == null) {
            ret.one = "ERROR";
            ret.two = 1;
            return ret;
        }

        //clear all publish settings, 
        //set uddi publisher settings in fgsms
        Test_Setfgsms_UDDI_Publish_Settings(p);

        //set the service policy in fgsms with the federation policy
        SetPolicy(svcbtkeys.two);
        //add some data via dcs
        SendData();
        //wait for publish interval
        System.out.println("waiting for up to 6 minutes for the publisher to publish");
        int timeout = 3600000;
        boolean success=false;
        while (timeout > 0) {

            System.out.println("sleeping for 15 seconds, time remaining: " + timeout);
            Thread.sleep(15000);
            timeout -= 15000;
            //get results
            //perform a uddi inquiry, confirm that all tmodels are present that were specified in the policy
            ret = validateResults(svcbtkeys.two);
            if (ret.two >=32) {
                success=true;
                break;
            }
            //TODO revisit this hard coded value for validity
        }
        System.out.println("uddi publish tear down");
        //cleanup
        //unset uddi publisher settings
        RemovePublisherSettings();
        //remove service & business  from uddi
        try {
            removeTestService(svcbtkeys.one);
        } catch (Exception ex) {
            System.out.println("error deleteing temp entities");
            ex.printStackTrace();;
        }
        try {
            removeTestBusiness(bizkey2);
        } catch (Exception ex) {
            System.out.println("error deleteing temp entities");
            ex.printStackTrace();;
        }

        //remove service policy and data
        RemovePolicy();

        if (success)
            ret.two=0;  //return code for unit tests
        return ret;

    }

    /**
     * testing only
     *
     * @return
     */
    protected boolean PublishTestPublisher() {
        try {
            SaveBusiness sb1 = new SaveBusiness();
            BusinessEntity e = new BusinessEntity();
            Name n = new Name();
            n.setValue("Test Business Entity");
            n.setLang(PublicationConstants.lang);
            e.getName().add(n);
            // biz.add(e);
            sb1.getBusinessEntity().add(e);
            sb1.setAuthInfo(LoginWrapper());
            BusinessDetail saveBusiness = publication.saveBusiness(sb1);
            bizkey = saveBusiness.getBusinessEntity().get(0).getBusinessKey();
            return true;
        } catch (DispositionReportFaultMessage ex) {
            System.out.println("error publising test business entity");
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            System.out.println("error publising test business entity");
            ex.printStackTrace();
            return false;
        }
    }

    /// 
    /// testing purposes only
    /// 
    protected String PublishTestBusiness() {
        try {
            SaveBusiness sb1 = new SaveBusiness();
            //List<BusinessEntity> biz = new ArrayList<BusinessEntity>();
            BusinessEntity e = new BusinessEntity();
            e.setBusinessKey(bizkey);
            Name n = new Name();
            n.setValue("Test Business Entity");
            n.setLang(PublicationConstants.lang);
            e.getName().add(n);
            // biz.add(e);
            sb1.getBusinessEntity().add(e);
            sb1.setAuthInfo(LoginWrapper());
            BusinessDetail saveBusiness = publication.saveBusiness(sb1);

            return saveBusiness.getBusinessEntity().get(0).getBusinessKey();
        } catch (DispositionReportFaultMessage ex) {
            System.out.println("error publising test business entity");
            ex.printStackTrace();
            return "ERROR";
        } catch (Exception ex) {
            System.out.println("error publising test business entity");
            ex.printStackTrace();
            return "ERROR";
        }
    }
    /// 
    /// testing purposes only
    /// 

    protected Pair<String, String> PublishTestService() throws Exception {
        try {
            SaveService sb1 = new SaveService();
            BusinessService s = new BusinessService();
            s.setBusinessKey(bizkey);
            Name n = new Name();
            n.setLang(PublicationConstants.lang);
            n.setValue("Test Service fgsms UDDI Service");
            s.getName().add(n);
            BindingTemplates l = new BindingTemplates();
            BindingTemplate bt = new BindingTemplate();
            AccessPoint ap = new AccessPoint();
            ap.setValue(url);
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
            sb1.setAuthInfo(LoginWrapper());
            ServiceDetail saveService = publication.saveService(sb1);
            log.log(Level.INFO, "Service saved successfully");
            Pair<String, String> r = new Pair<String, String>();
            r.one = saveService.getBusinessService().get(0).getServiceKey();
            r.two = saveService.getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey();
            return r;
            //return true;
        } catch (DispositionReportFaultMessage ex) {
            System.out.println("Error saving test service");
            ex.printStackTrace();
            return null;
        }
    }

    private String LoginWrapper() {

        return Login(username, password);
    }

    private String Login(String username, String password) {
        if (Utility.stringIsNullOrEmpty(username) || Utility.stringIsNullOrEmpty(password)) {
            return "";
        }
        if (isMsUDDI) {
            return "";
        }

        //return "";/*
        GetAuthToken request = new GetAuthToken();
        request.setUserID(username);
        request.setCred((password));
        try {
            AuthToken response = security.getAuthToken(request);
            return response.getAuthInfo();
        } catch (Exception ex) {
            System.out.println("Error authenticating to the UDDI server: ");
            ex.printStackTrace();
            throw new IllegalArgumentException("can't authenticate to UDDI with the provided credentials");
            //return "";
        }
    }

    private Pair<String, Integer> validateResults(String key) throws Exception {
        System.out.println("Validating results for the binding " + key);
        Pair<String, Integer> r = new Pair<String, Integer>();
        r.one = "";
        r.two = 0;
        GetBindingDetail req = new GetBindingDetail();
        req.setAuthInfo(LoginWrapper());
        req.getBindingKey().add(key);
        BindingDetail bindingDetail = inquiry.getBindingDetail(req);
        if (bindingDetail == null || bindingDetail.getBindingTemplate().isEmpty() || bindingDetail.getBindingTemplate().get(0).getTModelInstanceDetails() == null
                || bindingDetail.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo() == null
                || bindingDetail.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().isEmpty()) {
            r.one += "No binding or category bag was found, settings were not published";
            r.two++;
            System.out.println("Validating FAILED! data no data published for binding " + key);
            return r;
        }
        for (int x = 0; x < bindingDetail.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().size(); x++) {
            TModelInstanceInfo j = bindingDetail.getBindingTemplate().get(0).getTModelInstanceDetails().getTModelInstanceInfo().get(x);
            if (PublicationConstants.isTmodelFromfgsms(j.getTModelKey())) {
                System.out.println("Tmodel Key="
                        + j.getTModelKey()
                        + " value="
                        + j.getInstanceDetails().getInstanceParms());
                r.two++;
            }
        }
        System.out.println("totals keys count " + r.two);
        
        if (r.two >= 32) {
            System.out.println("Validating successful! binding " + key + " keys published " + r.two);
        } else {
            if (r.two> 0)
            System.out.println("Validating FAILED! some data was present but not evertyhign required. binding " + key);
        }
        return r;
    }

    private void removeTestService(String key) throws Exception {
        DeleteService ds = new DeleteService();
        ds.setAuthInfo(LoginWrapper());
        ds.getServiceKey().add(key);
        publication.deleteService(ds);
    }

    private void removeTestBusiness(String key) throws Exception {
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(LoginWrapper());
        db.getBusinessKey().add(key);
        publication.deleteBusiness(db);

    }

    private void dumpAllTestEntities() throws Exception {
        FindBusiness fb = new FindBusiness();
        fb.setAuthInfo(LoginWrapper());
        Name n = new Name();
        n.setLang(PublicationConstants.lang);
        n.setValue(PublicationConstants.UDDI_WILDCARD);
        fb.setFindQualifiers(new FindQualifiers());
        fb.getFindQualifiers().getFindQualifier().add(PublicationConstants.UDDI_FIND_QUALIFIER_APPROXIMATE_MATCH);
        fb.getName().add(n);
        BusinessList findBusiness = inquiry.findBusiness(fb);
        DeleteBusiness db = new DeleteBusiness();
        db.setAuthInfo(LoginWrapper());
        if (findBusiness != null && findBusiness.getBusinessInfos() != null) {
            for (int i = 0; i < findBusiness.getBusinessInfos().getBusinessInfo().size(); i++) {
                if (findBusiness.getBusinessInfos().getBusinessInfo().get(i).getName().get(0).getValue().equalsIgnoreCase("Test Business Entity")) {
                    db.getBusinessKey().clear();
                    db.getBusinessKey().add(findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey());
                    try {
                        publication.deleteBusiness(db);
                    } catch (Exception ex) {
                        System.out.println("couldn't remove entity " + findBusiness.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey() + ex.getMessage());
                    }
                }
            }
            //System.out.println("deleting " + db.getBusinessKey().size() + " test entities");
            //if (!db.getBusinessKey().isEmpty())

        }
    }
}
