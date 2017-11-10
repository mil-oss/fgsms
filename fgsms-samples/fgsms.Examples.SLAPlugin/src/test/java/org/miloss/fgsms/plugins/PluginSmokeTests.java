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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import org.miloss.fgsms.plugins.sla.alertservice.AlertRecieverTestImpl;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.DCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfSLA;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfSLAActionBaseType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValue;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachinePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PolicyConfigurationService;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ProcessPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RemoveGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RunAtLocation;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLA;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLARuleGeneric;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.StatisticalServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.StatusServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import static org.junit.Assert.*;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;

/**
 *
 * @author AO
 */
public class PluginSmokeTests {

    private Properties prop;
    private ConfigLoader cfg;
    private PCS pcsPort;

    public PluginSmokeTests() throws ConfigurationException {
        System.out.println("PluginSmokeTests");
        cfg = new ConfigLoader();
    }

    @org.junit.Before
    public void setUp() throws Exception {
        System.out.println("Setup");
        prop = new Properties();
        File f=new File("test.properties");
        System.out.println("Loading test properties from " + f.getCanonicalPath());
        prop.load(new FileInputStream(f));
    }

    @org.junit.After
    public void tearDown() throws Exception {
        System.out.println("tearDown");
    }

    /**
     * Tests a web service transactional sla
     *
     * @throws Exception
     */
    @org.junit.Test
    public void PluginTest1() throws Exception {
        System.out.println("PluginTest1");
        String url = "http://" + Utility.getHostName() + ":9999/CustomSLAReciever";
        AlertRecieverTestImpl svc = new AlertRecieverTestImpl();
        SetupPCSEndpoint();
        Endpoint ep = Endpoint.publish(url, svc);
        ConfigureCallbackPlugin(url);
        SetServicePolicy(url + "Policy", PolicyType.TRANSACTIONAL);
        SendData(url + "Policy");
        Thread.sleep(30000);
        ep.stop();
        UnconfigureCallbackPlugin();
        KillPolicy(url + "Policy");
        if (svc.count == 0) {
            fail("no alerts recieved");
        }
        if (svc.count > 1) {
            fail("more alerts recieved than expected");
        }
        System.out.println("passed");
    }

    private void SetupPCSEndpoint() {
      
        PolicyConfigurationService c = new PolicyConfigurationService();
        pcsPort = c.getPCSPort();
        BindingProvider bp = (BindingProvider) pcsPort;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (String) prop.get("PCS"));
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, (String) prop.get("fgsmsadminuser"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, (String) prop.get("fgsmsadminpass"));
    }

    private void SetServicePolicy(String firsthop_url, PolicyType pt) throws Exception {


        SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(firsthop_url);

        ServicePolicy sp = null;
        switch (pt) {
            case TRANSACTIONAL:
                TransactionalWebServicePolicy tp = new TransactionalWebServicePolicy();
                tp.setRecordFaultsOnly(false);
                tp.setRecordRequestMessage(false);
                tp.setRecordResponseMessage(false);
                tp.setBuellerEnabled(false);
                tp.setHealthStatusEnabled(false);
                tp.setURL(firsthop_url);
                tp.setDomainName("unspecified");
                tp.setMachineName(Utility.getHostName());
                sp = tp;
                break;
            case MACHINE:
                MachinePolicy mp = new MachinePolicy();
                sp = mp;
                break;
            case PROCESS:
                ProcessPolicy pp = new ProcessPolicy();
                sp = pp;
                break;
            case STATISTICAL:
                StatisticalServicePolicy bps = new StatisticalServicePolicy();
                sp = bps;
                break;
            case STATUS:
                StatusServicePolicy ssp = new StatusServicePolicy();
                sp = ssp;
        }
        sp.setPolicyType(pt);
        sp.setServiceLevelAggrements(addSLAPlugin());
        req.setPolicy(sp);
        req.setURL(firsthop_url);
        pcsPort.setServicePolicy(req);
    }

    private ArrayOfSLA addSLAPlugin() {
        
        ArrayOfSLA slas = new ArrayOfSLA();
        SLA sla = new SLA();
        sla.setAction(new ArrayOfSLAActionBaseType());
        SLAAction sa = new SLAAction();
        sa.setImplementingClassName(CustomSLAAction.class.getCanonicalName());

        sla.getAction().getSLAAction().add(sa);
        sla.setGuid(UUID.randomUUID().toString());
        SLARuleGeneric rule = new SLARuleGeneric();
        rule.setClassName(CustomSLARule.class.getCanonicalName());
        rule.setProcessAt(RunAtLocation.FGSMS_SERVER);

        sla.setRule(rule);

        slas.getSLA().add(sla);
        return (slas);
    }

    private void SendData(String url) throws Exception {
        List<AddDataRequestMsg> req = new ArrayList<AddDataRequestMsg>();
        //for (int i = 0; i < expectedcount; i++) {
        AddDataRequestMsg r = new AddDataRequestMsg();
        r.setSuccess(false);
        r.setAgentType("jUnit");
        r.setAction("test");
        r.setClassification(new SecurityWrapper());
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        r.setRecordedat(gcal);
        r.setRequestSize(100);
        r.setRequestURI(url);
        r.setResponseSize(100);
        r.setResponseTime(100);
        r.setServiceHost(Utility.getHostName());
        r.setTransactionID(UUID.randomUUID().toString());
        r.setURI(url);
        req.add(r);
        //}
        DCS dcs = cfg.getDcsport();
        BindingProvider bp = (BindingProvider) dcs;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getDCS_URLS().get(0));
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, cfg.getUsername());
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.getPassword()));
        // System.out.println("Delivering " + expectedcount + " msgs to dcs");
        dcs.addMoreData(req);
    }

    private void ConfigureCallbackPlugin(String url) throws Exception {
        SetGeneralSettingsRequestMsg req = new SetGeneralSettingsRequestMsg();
        req.setClassification(new SecurityWrapper());
        KeyNameValueEnc keyNameValueEnc = new KeyNameValueEnc();
        keyNameValueEnc.setShouldEncrypt(false);
        KeyNameValue knv = new KeyNameValue();
        knv.setPropertyKey("SDK.Samples.AlertRecieverService");
        knv.setPropertyName("URL");
        knv.setPropertyValue(url);
        keyNameValueEnc.setKeyNameValue(knv);
        req.getKeyNameValueEnc().add(keyNameValueEnc);
        pcsPort.setGeneralSettings(req);
    }

    private void UnconfigureCallbackPlugin() throws Exception {
        RemoveGeneralSettingsRequestMsg req = new RemoveGeneralSettingsRequestMsg();

        KeyNameValue knv = new KeyNameValue();
        knv.setPropertyKey("SDK.Samples.AlertRecieverService");
        knv.setPropertyName("URL");
        req.setClassification(new SecurityWrapper());
        req.getKeyNameValue().add(knv);
        pcsPort.removeGeneralSettings(req);


    }

    private void KillPolicy(String url) throws Exception {
        DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
        req.setClassification(new  SecurityWrapper());
        req.setURL(url);
        req.setDeletePerformanceData(true);
        pcsPort.deleteServicePolicy(req);
    }
}
