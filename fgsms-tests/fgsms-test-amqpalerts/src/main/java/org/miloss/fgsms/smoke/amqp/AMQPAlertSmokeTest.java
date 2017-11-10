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
package org.miloss.fgsms.smoke.amqp;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.BindingProvider;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.agentcore.MessageProcessor;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.DCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.smoketest.common.FilterProperties;

/**
 *
 * @author AO
 */
public class AMQPAlertSmokeTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
	 Properties p = new Properties();
	 p.load(new FileInputStream("../test.properies"));
	 FilterProperties.remove("jms", p);
	    FilterProperties.removeKeyPrefix("amqp", p);
        if (new AMQPAlertSmokeTest().run(p)) {
            System.exit(1);
        }
    }
    
    Properties props;
    //returns false if successful
    public boolean run(Properties p) throws Exception {

	 props=p;
        //create fictitous policy with SLA for JMS Alert

        SetPolicy();
        
        GetGeneralSettingsResponseMsg g = ValidAMQPIsEnabled();
        //set JMS settings if not present
        //fire up a concurrent thread to 
        //add some data that will trigger the alert
        TopicExampleAmqp te = new TopicExampleAmqp(cfg, url);
        te.count = 0;
        te.expectedcount = 5;
        te.timeout = 120000;
        /*new Thread(new Runnable() {
           @Override
           public void run() {
                //send in 5 faulty reports
                MessageProcessor.getSingletonObject();
                String hash = null;
                hash=UUID.randomUUID().toString();
                for (int i=0; i < 5; i++){
                     hash=UUID.randomUUID().toString();
                    MessageProcessor.processMessageInput("", 100, url, "uri:action", "N/A", hash, new HashMap(), "127.0.0.1", "UnitTestAgent", null, null);
                    MessageProcessor.processMessageOutput(hash, "", 100, true, 0L, new HashMap());
                }
                
           }
          }).start();*/
        te.example(g);   //this will block until the timeout or until all messages are recieved.

        RemovePolicy();
        if (te.count == te.expectedcount) {
            System.out.println("The test passed");
            return false;
        }
        System.err.println("The test failed expected " + te.expectedcount + " but only got " + te.count);
        return true;
    }
    static String url = "http://" + Utility.getHostName() + ":12345/amqpSmokeTest";
    
    private void SetPolicy() throws ConfigurationException, ServiceUnavailableException, AccessDeniedException, DatatypeConfigurationException {
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
        SLARuleGeneric rule = new SLARuleGeneric();
        rule.setClassName("org.miloss.fgsms.sla.rules.AllFaults");
        rule.setProcessAt(RunAtLocation.FGSMS_SERVER);
        sla.setRule(rule);
        sla.setAction(new ArrayOfSLAActionBaseType());
        SLAAction amqp = new SLAAction();
        amqp.setImplementingClassName("org.miloss.fgsms.sla.actions.AMQPAlerter");
     //  jms.setIsTopic(true);
        //  jms.setDestinationOverride(null);
        sla.getAction().getSLAAction().add(amqp);
        slas.getSLA().add(sla);
        ObjectFactory pcsfac = new ObjectFactory();
        pol.setServiceLevelAggrements((slas));
        cfg = new ConfigLoader();
        
        pcsport = cfg.getPcsport();
        BindingProvider bp = (BindingProvider) pcsport;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,  props.getProperty("fgsmsadminuser"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,  props.getProperty("fgsmsadminpass"));
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getPCS_URLS().get(0));
        SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(pol.getURL());
        req.setPolicy(pol);
        pcsport.setServicePolicy(req);
    }
    static ConfigLoader cfg = null;
    static PCS pcsport = null;
    
    private GetGeneralSettingsResponseMsg ValidAMQPIsEnabled() throws Exception {
        System.out.println("Checking AMQP alerting policy");
        GetGeneralSettingsRequestMsg req = new GetGeneralSettingsRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setKeyvalue("AMQPAlerts");
        GetGeneralSettingsResponseMsg generalSettings = pcsport.getGeneralSettings(req);
        
        if (generalSettings.getKeyNameValue().size() < 4) {
            throw new Exception("AMQP Alerting is not fully configured on this instance");
        }
        return generalSettings;
    }
    
    private void RemovePolicy() throws Exception {
        System.out.println("Removing the policy");
        DeleteServicePolicyRequestMsg req = new DeleteServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(url);
        req.setDeletePerformanceData(true);
        pcsport.deleteServicePolicy(req);
    }
}
