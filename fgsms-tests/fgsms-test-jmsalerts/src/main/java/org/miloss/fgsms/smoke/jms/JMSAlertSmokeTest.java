/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * <p>
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.
 * <p>
 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.smoke.jms;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;

import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.smoketest.common.FilterProperties;

/**
 *
 * @author AO
 */
public class JMSAlertSmokeTest {

    //returns true if successful
    public static boolean runtest() throws Exception {
        return run(false);
    }

    private static boolean run(boolean listenonly) throws Exception {

        Properties p = new Properties();
        p.load(new FileInputStream(new File("../test.properties")));

        FilterProperties.remove("amqp", p);
        FilterProperties.removeKeyPrefix("jms", p);

        cfg = new ConfigLoader();
        url = IpAddressUtility.modifyURL(url, false);
        pcsport = cfg.getPcsport();
        BindingProvider bp = (BindingProvider) pcsport;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, p.getProperty("fgsmsadminuser"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, p.getProperty("fgsmsadminpass"));
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getPCS_URLS().get(0));
        System.out.println("using " + cfg.getPCS_URLS().get(0) + " for pcs url");
        Dump(p);
        //create fictitous policy with SLA for JMS Alert
        if (!listenonly) {
            SetPolicy();
        }

        GetGeneralSettingsResponseMsg g = ValidJmsIsEnabled();
        System.out.println("passed");
        Dump(g);
        //set JMS settings if not present
        //fire up a concurrent thread to 
        //add some data that will trigger the alert
        TopicExample te = new TopicExample(cfg, url);
        te.count = 0;
        te.expectedcount = 5;
        te.timeout = 120000;
        te.example(g, (listenonly), p);   //this will block until the timeout or until all messages are recieved.
        if (!listenonly) {
            RemovePolicy();
        }
        if (te.count == te.expectedcount) {
            System.out.println("The test passed");
            return true;
        }
        System.err.println("The test failed expected " + te.expectedcount + " but only got " + te.count);
        return false;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        boolean listenonly = false;
        if (args.length > 0) {
            listenonly = true;
        }
        if (!run(listenonly)) {
            System.exit(1);
        }


    }

    static String url = "http://localhost:12345/JmsSmokeTest";

    private static void SetPolicy() throws ConfigurationException, ServiceUnavailableException, AccessDeniedException, DatatypeConfigurationException {
        System.out.println("Setting up policy");
        TransactionalWebServicePolicy pol = new TransactionalWebServicePolicy();
        pol.setAgentsEnabled(true);
        pol.setBuellerEnabled(false);
        pol.setDataTTL(DatatypeFactory.newInstance().newDuration(600000));
        pol.setHealthStatusEnabled(false);
        pol.setMachineName(Utility.getHostName());
        pol.setPolicyType(PolicyType.TRANSACTIONAL);
        pol.setURL(url);
        ArrayOfSLA slas = new ArrayOfSLA();
        SLA sla = new SLA();
        sla.setGuid(UUID.randomUUID().toString());
        SLARuleGeneric r = new SLARuleGeneric();
        r.setClassName("org.miloss.fgsms.sla.rules.AllFaults");
        r.setProcessAt(RunAtLocation.FGSMS_SERVER);
        sla.setRule(r);

        sla.setAction(new ArrayOfSLAActionBaseType());
        SLAAction jms = new SLAAction();
        jms.setImplementingClassName("org.miloss.fgsms.sla.actions.JMSAlerter");


        //  jms.setIsTopic(true);
        //  jms.setDestinationOverride(null);
        sla.getAction().getSLAAction().add(jms);
        slas.getSLA().add(sla);

        pol.setServiceLevelAggrements((slas));

        SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(pol.getURL());
        req.setPolicy(pol);
        pcsport.setServicePolicy(req);
    }

    static ConfigLoader cfg = null;
    static PCS pcsport = null;

    private static GetGeneralSettingsResponseMsg ValidJmsIsEnabled() throws Exception {
        System.out.println("Checking JMS alerting policy");
        GetGeneralSettingsRequestMsg req = new GetGeneralSettingsRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setKeyvalue("JMSAlerts");
        GetGeneralSettingsResponseMsg generalSettings = pcsport.getGeneralSettings(req);

        if (generalSettings.getKeyNameValue().size() < 6) {
            throw new Exception("JMS Alerting is not fully configured on this instance");
        }
        return generalSettings;
    }

    private static void RemovePolicy() throws Exception {
        System.out.println("Removing the policy");
        DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(url);
        req.setDeletePerformanceData(true);
        pcsport.deleteServicePolicy(req);
    }

    private static void Dump(GetGeneralSettingsResponseMsg g) {
        System.out.println("Dumping FGSMS General settings");
        for (int i = 0; i < g.getKeyNameValue().size(); i++) {
            System.out.println(g.getKeyNameValue().get(i).getPropertyKey() + "\t" + g.getKeyNameValue().get(i).getPropertyName() + "\t" + g.getKeyNameValue().get(i).getPropertyValue());
        }
        System.out.println("Dumping FGSMS General settings END");
    }

    private static void Dump(Properties p) {
        System.out.println("Dumping properties");
        Iterator<Entry<Object, Object>> iterator = p.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Object, Object> next = iterator.next();
            System.out.println(next.getKey() + " = " + next.getValue());
        }
        System.out.println("Dumping properties END");
    }
}
