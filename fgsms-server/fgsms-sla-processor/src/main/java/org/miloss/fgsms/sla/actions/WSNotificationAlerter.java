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
package org.miloss.fgsms.sla.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.ws.BindingProvider;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.SLAUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.wsn.WSNConstants;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType;
import org.oasis_open.docs.wsn.b_2.NotificationMessageHolderType.Message;
import org.oasis_open.docs.wsn.b_2.Notify;
import org.oasis_open.docs.wsn.b_2.TopicExpressionType;
import org.oasis_open.docs.wsn.br_2.RegisterPublisher;
import org.oasis_open.docs.wsn.br_2.RegisterPublisherResponse;
import org.oasis_open.docs.wsn.brw_2.NotificationBroker;
import org.oasis_open.docs.wsn.client.NotificationService;

import org.w3c.dom.Element;

/**
 * Sends alerts via WS-Notification
 *
 * @author AO
 * @since 6.0
 */
public class WSNotificationAlerter implements SLAActionInterface {

    private static final String KEY = "WSNAlerts";
    static final Logger log = Logger.getLogger("fgsms.WSNAlerting");
    private static boolean isconfigured = false;
    private static String configuredWSN_DIALECT = null;
    private static String topic = null;
    private static String username = "";
    private static String encryptedpassword = "";
    private static String brokerurl = "";
    private static String keystore = "";
    private static String keystorepass = "";
    private static boolean isMainTopicRegistered = true;
    private static String truststore = "";
    private static String truststorepass = "";
    private static NotificationBroker notificationPort = null;
    //private static org.oasis_open.docs.wsn.brw_2.NotificationService ns = null;
    private static long LastConfigRefresh = 0;

    public static void SendAlert(AlertContainer alert, Element xmlalert) {
        boolean pooled = alert.isPooled();

        if (!isconfigured) {
            Configure(pooled);
            LastConfigRefresh = System.currentTimeMillis();
        }
        if (!isconfigured) {
            log.log(org.apache.log4j.Level.ERROR, SLACommon.getBundleString("ErrorWSNNotConfigured"));
            return;
        }
        if ((System.currentTimeMillis() - 5 * 60 * 1000) > LastConfigRefresh) {
            log.log(Level.INFO, SLACommon.getBundleString("ErrorWSNRefresh"));
            Configure(pooled);
            LastConfigRefresh = System.currentTimeMillis();
        }

        boolean RegisterPublisher = false;
        String dialect = null;
        String Destination = null;
        String uname = null;
        String pwd = null;
        String BrokerURL = null;

        NameValuePair nvpRegisterPublisher = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "RegisterPublisher");
        NameValuePair nvpBrokerURL = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "BrokerURL");
        NameValuePair nvpdialect = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "dialect");
        NameValuePair nvpDestination = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "Destination");
        NameValuePair nvpusername = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "username");
        NameValuePair nvppassword = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "password");
        if (nvpRegisterPublisher != null) {
            if (nvpRegisterPublisher.isEncrypted()) {
                RegisterPublisher = Boolean.parseBoolean(Utility.DE(nvpRegisterPublisher.getValue()));
            } else {
                RegisterPublisher = Boolean.parseBoolean(nvpRegisterPublisher.getValue());
            }
        }

        if (nvpusername != null) {
            if (nvpusername.isEncrypted()) {
                uname = Utility.DE(nvpusername.getValue());
            } else {
                uname = (nvpusername.getValue());
            }
        }
        if (nvppassword != null) {
            if (nvppassword.isEncrypted()) {
                pwd = Utility.DE(nvppassword.getValue());
            } else {
                pwd = (nvppassword.getValue());
            }
        }


        if (nvpBrokerURL != null) {
            if (nvpBrokerURL.isEncrypted()) {
                BrokerURL = Utility.DE(nvpBrokerURL.getValue());
            } else {
                BrokerURL = (nvpBrokerURL.getValue());
            }
        }
        if (nvpdialect != null) {
            if (nvpdialect.isEncrypted()) {
                dialect = Utility.DE(nvpdialect.getValue());
            } else {
                dialect = (nvpdialect.getValue());
            }
        }
        if (nvpDestination != null) {
            if (nvpDestination.isEncrypted()) {
                Destination = Utility.DE(nvpDestination.getValue());
            } else {
                Destination = (nvpDestination.getValue());
            }
        }

        BindingProvider bp = (BindingProvider) notificationPort;
        if (BrokerURL != null) {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, BrokerURL);
        } else {
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, brokerurl);
        }


        if (!Utility.stringIsNullOrEmpty(uname) && !Utility.stringIsNullOrEmpty(pwd)) {
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, (uname));
        } else {
            if (!Utility.stringIsNullOrEmpty(username) && !Utility.stringIsNullOrEmpty(encryptedpassword)) {
                bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, (Utility.DE(encryptedpassword)));
                bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, (username));
            }
        }



        Notify notify = new Notify();
        NotificationMessageHolderType msg = new NotificationMessageHolderType();

        TopicExpressionType tet = new TopicExpressionType();
        if (Utility.stringIsNullOrEmpty(configuredWSN_DIALECT) && Utility.stringIsNullOrEmpty(dialect)) {
            tet.setDialect(WSNConstants.WST_TOPICEXPRESSION_SIMPLE);
        } else {
            if (!Utility.stringIsNullOrEmpty(dialect)) {
                tet.setDialect(dialect);
            } else {
                tet.setDialect(configuredWSN_DIALECT);
            }
        }
        if (Utility.stringIsNullOrEmpty(Destination) && Utility.stringIsNullOrEmpty(topic)) {
            tet.getContent().add("fgsmsAlerts");
        } else if (!Utility.stringIsNullOrEmpty(Destination)) {
            tet.getContent().add(Destination);
        } else {
            tet.getContent().add(topic);
        }
        if (RegisterPublisher) {
            RegisterProducer((String) tet.getContent().get(0));
        }
        msg.setTopic((tet));
        Message m = new Message();

        m.setAny(xmlalert);

        msg.setMessage(m);
        notify.getNotificationMessage().add(msg);
        try {

            notificationPort.notify(notify);
	    log.log(Level.INFO, "Alert delivered");
        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("ErrorWSNDelivery"), ex);
        }
    }

    private static void Configure(boolean pooled) {
        boolean ok = true;
        KeyNameValueEnc p = null;

        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "BrokerURL");
        if (p != null && p.getKeyNameValue() != null) {
            brokerurl = p.getKeyNameValue().getPropertyValue();
        }

        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "username");
        if (p != null && p.getKeyNameValue() != null) {
            username =
                    p.getKeyNameValue().getPropertyValue();
        } else {
            username = null;
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "password");
        if (p != null && p.getKeyNameValue() != null) {
            encryptedpassword =
                    p.getKeyNameValue().getPropertyValue();
        } else {
            encryptedpassword =
                    null;
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "dialect");
        if (p != null && p.getKeyNameValue() != null) {
            configuredWSN_DIALECT =
                    p.getKeyNameValue().getPropertyValue();
        } else {
            configuredWSN_DIALECT = null;
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "Destination");
        if (p != null && p.getKeyNameValue() != null) {
            topic = p.getKeyNameValue().getPropertyValue();
        } else {
            topic = null;
        }


        p = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "truststore");
        if (p != null && p.getKeyNameValue() != null) {
            try {
                truststore = p.getKeyNameValue().getPropertyValue();
            } catch (Exception ex) {
            }
        } else {
            truststore = null;
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "truststorepass");
        if (p != null && p.getKeyNameValue() != null) {
            try {
                truststorepass = p.getKeyNameValue().getPropertyValue();
            } catch (Exception ex) {
            }
        } else {
            truststorepass = null;
        }
        boolean RegisterPublisher = false;
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "RegisterPublisher");
        if (p != null && p.getKeyNameValue() != null) {
            try {
                RegisterPublisher = Boolean.parseBoolean(p.getKeyNameValue().getPropertyValue());
            } catch (Exception ex) {
                RegisterPublisher = false;
            }
        } else {
            RegisterPublisher = false;
        }
//validate config

        if (Utility.stringIsNullOrEmpty(brokerurl)) {
            ok = false;
        }
        if (Utility.stringIsNullOrEmpty(topic)) {
            ok = false;
        }

        if (ok) {
            String tmp = System.getProperty("jboss.server.config.url");
            if (Utility.stringIsNullOrEmpty(tmp)) {
                //FIX for Jboss 7
                try {
                    tmp = System.getProperty("jboss.server.config.dir");
                    File f = new File(tmp);
                    tmp = f.toURI().toURL().toString();
                    tmp += File.separator;
                } catch (Exception e) {
                }
            }
            //fix for OpenJDK/linux issues
            tmp = File.separator + tmp;
            NotificationService svc = new NotificationService();
            notificationPort = svc.getNotificationPort();
            BindingProvider bp = (BindingProvider) notificationPort;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, brokerurl);
            if (!Utility.stringIsNullOrEmpty(username) && !Utility.stringIsNullOrEmpty(encryptedpassword)) {
                bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, (Utility.DE(encryptedpassword)));
                bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, (username));
            }
            if (brokerurl.toLowerCase().startsWith(("https://"))) {
                if (!Utility.stringIsNullOrEmpty(truststorepass) && !Utility.stringIsNullOrEmpty(truststore)) {
                    bp.getRequestContext().put("javax.net.ssl.trustStorePassword", Utility.DE(truststorepass));
                    bp.getRequestContext().put("javax.net.ssl.trustStore", tmp + truststore);
                }
                if (!Utility.stringIsNullOrEmpty(keystorepass) && !Utility.stringIsNullOrEmpty(keystore)) {
                    bp.getRequestContext().put("javax.net.ssl.keyStorePassword", Utility.DE(keystorepass));
                    bp.getRequestContext().put("javax.net.ssl.keyStore", tmp + keystore);
                }
            }
            isconfigured = true;
        } else {
            isconfigured = false;
        }


        if (RegisterPublisher && !isMainTopicRegistered) {
            RegisterProducer(topic);
        }

    }

    private static void RegisterProducer(String t) {
        try {
            RegisterPublisher req = new RegisterPublisher();
            TopicExpressionType localtopic = new TopicExpressionType();
            if (Utility.stringIsNullOrEmpty(configuredWSN_DIALECT)) {
                localtopic.setDialect(WSNConstants.WST_TOPICEXPRESSION_SIMPLE);
            } else {
                localtopic.setDialect(configuredWSN_DIALECT);
            }
            if (Utility.stringIsNullOrEmpty(t)) {
                localtopic.getContent().add("fgsmsAlerts");
            } else {
                localtopic.getContent().add(t);
            }
            req.getTopic().add(localtopic);

            RegisterPublisherResponse registerPublisher =
                    notificationPort.registerPublisher(req);
            log.log(Level.INFO, String.format(SLACommon.getBundleString("WSNTopicRegisteredSuccess"), t));
            isMainTopicRegistered = true;
        } catch (Exception ex) {
            log.log(Level.ERROR, String.format(SLACommon.getBundleString("WSNTopicRegisteredFailed"), t), ex);

        }
    }

    /**
     * Strips off the xml declaration
     *
     * @param xmlalert
     * @return
     */
    public static String StripXmlHeader(String xmlalert) {
        if (xmlalert.startsWith("<?xml")) {
            int trimat = xmlalert.indexOf("?>", 0);
            xmlalert = xmlalert.substring(trimat + 2);
            log.log(Level.DEBUG, "XML header trimmed " + xmlalert);
        }
        return xmlalert;
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();

        return r;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        r.add(Utility.newNameValuePair("RegisterPublisher", null, false, false));
        r.add(Utility.newNameValuePair("dialect", null, false, false));
        r.add(Utility.newNameValuePair("Destination", null, false, false));
        r.add(Utility.newNameValuePair("username", null, false, false));
        r.add(Utility.newNameValuePair("password", null, false, true));
        r.add(Utility.newNameValuePair("BrokerURL", null, false, false));
        return r;
    }

    @Override
    public boolean ValidateConfiguration(List<NameValuePair> params, AtomicReference<String> outError) {
        if (outError == null) {
            outError = new AtomicReference<String>();
        }
        Configure(true);
        if (isconfigured) {
            return true;
        } else {
            outError.set("The administrator hasn't configured the default settings yet using General Settings. WSN Alerts won't be available until then." + outError.get());
            return false;
        }
        //TODO validate true/false arguments
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Sends a WSDM formatted XML message to a WS-Notification message broker. Optional parameters:"
                + "<ul>"
                + "<li>RegisterPublisher - this will cause fgsms to register itself as a message publisher within the broker instance. Not all brokers support it. Valid values are true or false. Default is false.</li>"
                + "<li>dialect - Some brokers need a custom dialect. Use this to override to the default, which is " + WSNConstants.WST_TOPICEXPRESSION_SIMPLE + "</li>"
                + "<li>Destination - This is the name of the queue or topic that the messages are delivered to and that clients subscribe to</li>"
                + "<li>username -  for this instance only, overriding the administrator defined setting.</li>"
                + "<li>password -  for this instance only, overriding the administrator defined setting. This should be encrypted</li>"
                + "<li>BrokerURL - use this to change the URL of the broker for this instance only, overriding the administrator defined setting.</li>"
                + "</ul>";
    }

    @Override
    public String GetDisplayName() {
        return "WS-Notification Alert";
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
        WSNotificationAlerter.SendAlert(alert, SLAUtils.WSDMAlertToDomElement(SLAUtils.createWSDMEvent(alert)));
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        return Utility.getAllPolicyTypes();
    }
}
