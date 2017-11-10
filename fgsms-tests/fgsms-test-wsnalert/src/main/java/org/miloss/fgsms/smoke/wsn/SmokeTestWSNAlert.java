/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 * <p>
 * If it is not possible or desirable to put the notice in a particular file,
 * then You may include the notice in a location (such as a LICENSE file in a
 * relevant directory) where a recipient would be likely to look for such a
 * notice.
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
package org.miloss.fgsms.smoke.wsn;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.datacollector.AddDataRequestMsg;
import org.miloss.fgsms.services.interfaces.datacollector.DCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.miloss.fgsms.wsn.clientcallback.IWSNCallBack;
import org.miloss.fgsms.wsn.clientcallback.WSNotifyCallback;
import org.oasis_open.docs.wsn.b_2.*;
import org.oasis_open.docs.wsn.brw_2.NotificationBroker;
import org.oasis_open.docs.wsn.brw_2.PausableSubscriptionManager;
import org.oasis_open.docs.wsn.client.NotificationService;
import org.miloss.fgsms.wsn.WSNConstants;
import org.miloss.fgsms.wsn.WSNUtility;
import org.oasis_open.docs.wsn.client.PausableSubscriptionManagerService;

/**
 *Transactional service tests
 * @author AO
 */
public class SmokeTestWSNAlert implements IWSNCallBack {

    String submgrurl = "http://localhost:8888/WSN-Broker/SubscriptionManager";

    public SmokeTestWSNAlert() throws Exception {
        callbackurl = "http://localhost:7777/WSNCallback";
        p = new Properties();
        p.load(new FileInputStream(new File("../test.properties")));
    }

    static String callbackurl = "http://localhost:7777/WSNCallback";
    static final String callbackurlListen = "http://0.0.0.0:7777/WSNCallback";

    private static void Dump(GetGeneralSettingsResponseMsg g) {
        for (int i = 0; i < g.getKeyNameValue().size(); i++) {
            System.out.println(g.getKeyNameValue().get(i).getPropertyKey() + "\t" + g.getKeyNameValue().get(i).getPropertyName() + "\t" + g.getKeyNameValue().get(i).getPropertyValue());
        }
    }

    /**
     * @param args the command line arguments
     */
    private int count = 0;
    private int expectedcount = 5;
    int timeout = 60000;
    private Properties p;

    public static void main(String[] args) throws Exception {
        boolean success = false;
        if (args.length > 0) {
            success = new SmokeTestWSNAlert().run(true);
        } else {
            success = new SmokeTestWSNAlert().run(false);
        }
        if (!success) {
            System.exit(1);
        }
    }

    protected boolean run(boolean listenonly) throws Exception {
        //setup the callback
        org.oasis_open.docs.wsn.brw_2.NotificationBroker port = new WSNotifyCallback(this);
        Endpoint publish = Endpoint.publish(callbackurlListen, port);
        if (publish.isPublished()) {
            System.out.println("Callback listening on " + url);
        } else {
            System.err.println("Callback NOT listening on " + url);
            return false;
        }

        cfg = new ConfigLoader();

        pcsport = cfg.getPcsport();
        BindingProvider bp = (BindingProvider) pcsport;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, p.getProperty("fgsmsadminuser"));
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, p.getProperty("fgsmsadminpass"));
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getPCS_URLS().get(0));

        //make a ficitious policy with a wsn alert
        if (!listenonly) {
            SetPolicy();
        }

        //validate wsn broker config
        GetGeneralSettingsResponseMsg g = ValidWSNIsEnabled();
        Dump(g);
        //setup the subscription
        String subscriptionid = SetSubscription(g);
        if (!listenonly) {
            System.out.println("Sending data");
            //trigger the alert
            SendData();

            long start = System.currentTimeMillis();
            while ((start + timeout) > System.currentTimeMillis()) {
                if (expectedcount <= count) {
                    break;
                }
                Thread.sleep(1000);
            }
        } else {
            Scanner keyin = new Scanner(System.in);
            System.out.println("CTRL-C to exit");
            keyin.next();
        }

        //wait until all messages have been recieved.
        try {
            publish.stop();
        } catch (Exception ex) {
        }
        DestroySubscription(g, subscriptionid);
        //destroy subscription
        if (!listenonly) {
            RemovePolicy();
        }

        System.out.println("Callback endpoint stopped on " + url);
        if (count >= expectedcount) {
            System.out.println("The test passed");
            return true;
        }
        System.err.println("The test failed expected " + expectedcount + " but only got " + count);
        return false;
    }

    static final String url = "http://" + Utility.getHostName() + ":12345/WSNSmokeTest";

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
        SLAAction jms = new SLAAction();
        jms.setImplementingClassName("org.miloss.fgsms.sla.actions.WSNotificationAlerter");
        //SLAActionWSNotification jms = new SLAActionWSNotification();
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

    private GetGeneralSettingsResponseMsg ValidWSNIsEnabled() throws Exception {
        System.out.println("Checking WSN alerting policy");
        GetGeneralSettingsRequestMsg req = new GetGeneralSettingsRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setKeyvalue("WSNAlerts");
        GetGeneralSettingsResponseMsg generalSettings = pcsport.getGeneralSettings(req);

        if (generalSettings.getKeyNameValue().size() < 2) {
            throw new Exception("WSN Alerting is not fully configured on this instance");
        }
        return generalSettings;
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

    @Override
    public void OnMessage(Notify notify) {
        System.out.println("Inbound notify " + notify.getNotificationMessage().size() + " items");
        count++;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer ts = tf.newTransformer();
            StringWriter sw = new StringWriter();
            ts.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            for (int i = 0; i < notify.getNotificationMessage().size(); i++) {
                List<String> TopicExpressionToList = WSNUtility.topicExpressionToList(notify.getNotificationMessage().get(i).getTopic());
                System.out.println("Topics: " + Utility.listStringtoString(TopicExpressionToList));
                ts.transform(new DOMSource(notify.getNotificationMessage().get(i).getMessage().getAny()), new StreamResult(sw));
                System.out.println("Content: " + sw.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String SetSubscription(GetGeneralSettingsResponseMsg g) throws Exception {

        NotificationService svc = new NotificationService();
        NotificationBroker notificationPort = svc.getNotificationPort();
        BindingProvider bp = (BindingProvider) notificationPort;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, GetConfigValue(g, "BrokerURL"));
        Subscribe sub = new Subscribe();
        sub.setFilter(new FilterType());
        TopicExpressionType tet = new TopicExpressionType();
        tet.setDialect(WSNConstants.WST_TOPICEXPRESSION_SIMPLE);
        tet.getContent().add(GetConfigValue(g, "Destination"));
        sub.getFilter().getAny().add(new org.oasis_open.docs.wsn.b_2.ObjectFactory().createTopicExpression(tet));

        W3CEndpointReferenceBuilder b = new W3CEndpointReferenceBuilder();
        b.address(callbackurl);
        sub.setConsumerReference(b.build());
        SubscribeResponse subscribe = notificationPort.subscribe(sub);
        return WSNUtility.getWSAAddress(subscribe.getSubscriptionReference());
    }


    private void DestroySubscription(GetGeneralSettingsResponseMsg g, String subscriptionid) throws Exception {
        System.out.println("killing the subscription " + subscriptionid);

        PausableSubscriptionManagerService svc = new PausableSubscriptionManagerService();
        PausableSubscriptionManager notificationPort = svc.getPausableSubscriptionManagerPort();
        BindingProvider bp = (BindingProvider) notificationPort;

        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, submgrurl);

        Unsubscribe unsub = new Unsubscribe();
        W3CEndpointReferenceBuilder b = new W3CEndpointReferenceBuilder();

        b.address(subscriptionid);
        unsub.getAny().add(b.build());
        notificationPort.unsubscribe(unsub);
    }

    private void SendData() throws Exception {
        List<AddDataRequestMsg> req = new ArrayList<AddDataRequestMsg>();
        for (int i = 0; i < expectedcount; i++) {
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
        DCS dcs = cfg.getDcsport();
        BindingProvider bp = (BindingProvider) dcs;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getDCS_URLS().get(0));
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, cfg.getUsername());
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(cfg.getPassword()));
        System.out.println("Delivering " + expectedcount + " msgs to dcs");
        dcs.addMoreData(req);
    }

    boolean run(boolean b, String subscriptionmanagerurl) throws Exception {
        submgrurl = subscriptionmanagerurl;
        return run(b);
    }
}
