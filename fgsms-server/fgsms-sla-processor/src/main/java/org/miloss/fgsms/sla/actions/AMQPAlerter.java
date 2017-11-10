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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.*;
import javax.naming.Context;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.apache.qpid.client.AMQConnection;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.SLAUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.sla.SLACommon;

/**
 * Uses the Qpid AMQP client library to publish alerts
 *
 * @author AO
 * @since 5.0, major refactor since 6.3
 */
public class AMQPAlerter implements SLAActionInterface {

    private static final String KEY = "AMQPAlerts";
    static final Logger log = Logger.getLogger("fgsms.AMQPAlerting");
    private static boolean isconfigured = false;
    private static boolean Topic = true;    //if false, queue
    private static String ConnectionURL = "";
    private static String Destination = "";
    private static String INITIAL_CONTEXT_FACTORY = "";
    private static String username = "";
    private static String encryptedpassword = "";
    private static long LastConfigRefresh = 0;
    private static String keystore = "";
    private static String keystorepass = "";
    private static String truststore = "";
    private static String truststorepass = "";

    /**
     * sends an alert using the AMQP APIs (not JMS) Calls
     * ProcessActionRet(alert)
     */
   // @Override
    public void ProcessAction(AlertContainer alert) {
        ProcessActionRet(alert);
    }

    /**
     * Returns true if the message was delivered successfully
     *
     * @param alert
     * @return
     */
    public boolean ProcessActionRet(AlertContainer alert) {

        boolean pooled = alert.isPooled();
        String XmlMessage = SLAUtils.WSDMtoXmlString((SLAUtils.createWSDMEvent(alert)));
        NameValuePair nvpdestinationOverride = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "destinationOverride");
        NameValuePair nvpisTopicOverride = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "isTopicOverride");
        NameValuePair nvpConnectionURL = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "ConnectionURL");
        NameValuePair nvpusername = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "username");
        NameValuePair nvppassword = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "password");
        String destinationOverride = null;
        boolean isTopicOverride = false;
        String url = null;
        String uname = null;
        String pwd = null;
        if (nvpdestinationOverride != null) {
            if (nvpdestinationOverride.isEncrypted()) {
                destinationOverride = Utility.DE(nvpdestinationOverride.getValue());
            } else {
                destinationOverride = (nvpdestinationOverride.getValue());
            }
        }
        if (nvpisTopicOverride != null) {
            if (nvpisTopicOverride.isEncrypted()) {
                isTopicOverride = Boolean.parseBoolean(Utility.DE(nvpdestinationOverride.getValue()));
            } else {
                isTopicOverride = Boolean.parseBoolean(nvpdestinationOverride.getValue());
            }
        }
        if (nvpConnectionURL != null) {
            if (nvpConnectionURL.isEncrypted()) {
                url = (Utility.DE(nvpConnectionURL.getValue()));
            } else {
                url = (nvpConnectionURL.getValue());
            }
        }
        if (nvpusername != null) {
            if (nvpusername.isEncrypted()) {
                uname = (Utility.DE(nvpusername.getValue()));
            } else {
                uname = (nvpusername.getValue());
            }
        }
        if (nvppassword != null) {
            if (nvppassword.isEncrypted()) {
                pwd = (Utility.DE(nvppassword.getValue()));
            } else {
                pwd = (nvppassword.getValue());
            }
        }





        if (!isconfigured) {
            Configure(pooled);
            LastConfigRefresh = System.currentTimeMillis();
        }
        if (!isconfigured) {
            log.log(Level.ERROR, SLACommon.getBundleString("ErrorAMQPConfig"));
            return false;
        }
        if ((System.currentTimeMillis() - 5 * 60 * 1000) > LastConfigRefresh) {
            log.log(Level.INFO, SLACommon.getBundleString("ErrorAMQPRefresh"));
            Configure(pooled);
            LastConfigRefresh = System.currentTimeMillis();
        }
        Context ic = null;

        if (url == null) {
            url = ConnectionURL;
        }
        int parametercount = GetParameterCount(url);

        String tmp = System.getProperty("jboss.server.config.url");
        if (Utility.stringIsNullOrEmpty(tmp)) {
            //FIX for Jboss 7
            try {
                tmp = System.getProperty("jboss.server.config.dir");
                File f = new File(tmp);
                tmp = f.toURI().toURL().toString();
                tmp += File.separator;
            } catch (Exception e) {
                log.log(Level.DEBUG, null, e);
            }
        }
        //fix for OpenJDK/linux issues
        tmp = File.separator + tmp;

        if (uname == null) {
            uname = username;
        }
        if (pwd == null) {
            pwd = Utility.DE(encryptedpassword);
        }
        //username/password only
        if (parametercount == 2) {
            url = String.format(url, uname, URLEncoder.encode(pwd));
        }
        //username/password with SSL
        if (parametercount == 4) {
            url = String.format(url, uname, URLEncoder.encode(pwd), tmp + truststore, URLEncoder.encode(Utility.DE(truststorepass)));
        }
        //username/password with SSL
        if (parametercount == 6) {
            url = String.format(url, uname, URLEncoder.encode(pwd), tmp + truststore, URLEncoder.encode(Utility.DE(truststorepass)), tmp + keystore, URLEncoder.encode(Utility.DE(keystorepass)));
        }
        boolean ok = false;
        AMQConnection con = null;
        try {
            boolean topic_ = Topic;
            String use = Destination;
            if (!Utility.stringIsNullOrEmpty(destinationOverride)) {
                use = destinationOverride;
                topic_ = isTopicOverride;
            }
            //amqp://guest:guest@clientid/test?brokerlist='tcp://localhost:5672'

            con = new AMQConnection(url);
            Session s = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

            if (topic_) {
                Topic topic = s.createTopic(use);
                MessageProducer pub = s.createProducer(topic);
                javax.jms.TextMessage msg = s.createTextMessage(XmlMessage);
                pub.send(msg);
                log.log(Level.INFO, String.format(SLACommon.getBundleString("AMQPSuccessTopic"), topic.getTopicName(), use));
            } else {
                Queue topic = s.createQueue(use);
                MessageProducer pub = s.createProducer(topic);
                javax.jms.TextMessage msg = s.createTextMessage(XmlMessage);
                pub.send(msg);
                log.log(Level.INFO, String.format(SLACommon.getBundleString("AMQPSuccessQueue"), topic.getQueueName(), use));
            }
            ok = true;

        } catch (Exception ex) {
            log.log(Level.WARN, SLACommon.getBundleString("AMQPFailure"), ex);
        } finally {
            if (ic != null) {
                try {
                    ic.close();
                } catch (Exception e) {
                    log.log(Level.DEBUG, SLACommon.getBundleString("ErrorClosingResource"), e);
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (JMSException ex) {
                    log.log(Level.DEBUG, SLACommon.getBundleString("ErrorClosingResource"), ex);
                }
            }
        }
        return ok;
    }

    private static void Configure(boolean pooled) {
        boolean ok = true;
        KeyNameValueEnc p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "ConnectionURL");
        if (p != null && p.getKeyNameValue() != null) {
            ConnectionURL = p.getKeyNameValue().getPropertyValue();
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "DestinationType");
        if (p != null && p.getKeyNameValue() != null) {
            String t = p.getKeyNameValue().getPropertyValue();
            if (!Utility.stringIsNullOrEmpty(t)) {
                Topic = t.equalsIgnoreCase("topic");
            }
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "Destination");
        if (p != null && p.getKeyNameValue() != null) {
            Destination = p.getKeyNameValue().getPropertyValue();
        }


        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "INITIAL_CONTEXT_FACTORY");
        if (p != null && p.getKeyNameValue() != null) {
            INITIAL_CONTEXT_FACTORY = p.getKeyNameValue().getPropertyValue();
        }


        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "username");
        if (p != null && p.getKeyNameValue() != null) {
            username = p.getKeyNameValue().getPropertyValue();
        } else {
            username = null;
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "password");
        if (p != null && p.getKeyNameValue() != null) {
            encryptedpassword = p.getKeyNameValue().getPropertyValue();
        } else {
            encryptedpassword = null;
        }


        p = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "truststorepass");
        if (p != null && p.getKeyNameValue() != null) {
            truststorepass = p.getKeyNameValue().getPropertyValue();
        } else {
            truststorepass = null;
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "truststore");
        if (p != null && p.getKeyNameValue() != null) {
            truststore = p.getKeyNameValue().getPropertyValue();
        } else {
            truststore = null;
        }

        p = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "keystore");
        if (p != null && p.getKeyNameValue() != null) {
            keystore = p.getKeyNameValue().getPropertyValue();
        } else {
            keystore = null;
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "keystorepass");
        if (p != null && p.getKeyNameValue() != null) {
            keystorepass = p.getKeyNameValue().getPropertyValue();
        } else {
            keystorepass = null;
        }

//validate config

        if (Utility.stringIsNullOrEmpty(Destination)) {
            ok = false;
        }
        if (Utility.stringIsNullOrEmpty(ConnectionURL)) {
            ok = false;
        }
        if (ok) {
            isconfigured = true;
        } else {
            isconfigured = false;
        }
    }

    private static int GetParameterCount(String ConnectionURL) {
        int count = 0;
        int idx = 0;
        while (idx < ConnectionURL.length()) {
            int indexOf = ConnectionURL.indexOf("%s", idx);
            if (indexOf == -1) {
                return count;
            }
            count++;
            idx = indexOf + 1;
        }
        return count;
    }

    @Override
    public List<NameValuePair> GetRequiredParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        return r;
    }

    @Override
    public List<NameValuePair> GetOptionalParameters() {
        List<NameValuePair> r = new ArrayList<NameValuePair>();
        r.add(Utility.newNameValuePair("destinationOverride", null, false, false));
        r.add(Utility.newNameValuePair("isTopicOverride", null, false, false));
        r.add(Utility.newNameValuePair("ConnectionURL", null, false, false));
        //r.add(Utility.newNameValuePair("DestinationType", null, false, false));
        //r.add(Utility.newNameValuePair("Destination", null, false, false));
        //r.add(Utility.newNameValuePair("INITIAL_CONTEXT_FACTORY", null, false, false));
        r.add(Utility.newNameValuePair("username", null, false, false));
        r.add(Utility.newNameValuePair("password", null, false, true));
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
            outError.set("The administrator hasn't configured the default settings yet using General Settings. AMQP Alerts won't be available until then." + outError.get());
            return false;
        }
        //TODO this could use some more validation
    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Sends a WSDM Alert via AMQP<br>This will send a WSDM formatted XML message that corresponds to the type of rule that was triggered."
                + "Most settings are configured via the <b>General Settings</b> page, however they can be overridden using optional parameters."
                + "<ul>"
                + "<li>destinationOverride - delivers alerts to a different topic/queue other than the administrator defined one.</li>"
                + "<li>isTopicOverride - true/false, set to true when the destinationOverride is a topic, otherwise false. </li>"
                + "<li>ConnectionURL - delivers alerts to a different AMQP server or URL other than the administrator defined one. Uses the same formatting rules as in <b>General Settings</b> (%s for credentials and key stores)</li>"
                + "<li>username - use a different username other than the administrator defined one</li>"
                + "<li>password - user a different password other than the administrator defined one. This should be encrypted</li>";
    }

    @Override
    public String GetDisplayName() {
        return "Apache Qpid/Redhat MRG Alert";
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
           ProcessActionRet(alert);
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        return Utility.getAllPolicyTypes();
    }
}
