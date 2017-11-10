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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.SLAUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.plugins.sla.AlertContainer;
import org.miloss.fgsms.plugins.sla.SLAActionInterface;
import org.miloss.fgsms.services.interfaces.common.NameValuePair;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;

/**
 * Sends a JMS Alert
 *
 * @author AO
 */
public class JMSAlerter implements SLAActionInterface {

    static final Logger log = Logger.getLogger("fgsms.JMSAlerting");
    private static boolean isconfigured = false;
    private static boolean Topic = true;    //if false, queue
    private static String ContextProviderUrl = "";
    private static String Destination = "";
    private static String ConnectionFactoryLookup = "";
    private static String INITIAL_CONTEXT_FACTORY = "";
    private static String URL_PKG_PREFIXES = "";
    private static String username = "";
    private static String encryptedpassword = "";
    private static long LastConfigRefresh = 0;

    public static boolean SendAlert(AlertContainer alert) {

        boolean pooled = alert.isPooled();
        String XmlMessage = SLAUtils.WSDMtoXmlString((SLAUtils.createWSDMEvent(alert)));
        if (!isconfigured) {
            Configure(pooled);
            LastConfigRefresh = System.currentTimeMillis();
        }
        if (!isconfigured) {
            log.log(Level.ERROR, "Unable to publish JMS Alerts due to a required parameter being missing. See the How-To Guide for JMS Alerting for details on how to configure this feature.");
            return false;
        }
        boolean ok = false;
        if ((System.currentTimeMillis() - 5 * 60 * 1000) > LastConfigRefresh) {
            log.log(Level.INFO, "Refreshing configuration...");
            Configure(pooled);
            LastConfigRefresh = System.currentTimeMillis();
        }

        String destinationOverride = null;
        boolean isTopicOverride = false;


        NameValuePair nvpdestinationOverride = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "destinationOverride");
        NameValuePair nvpisTopicOverride = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "isTopicOverride");
        NameValuePair nvpConnectionURL = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "ConnectionURL");
        NameValuePair nvpusername = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "username");
        NameValuePair nvppassword = Utility.getNameValuePairByName(alert.getSlaActionBaseType().getParameterNameValue(), "password");
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
        if (pwd == null) {
            pwd = Utility.DE(encryptedpassword);
        }
        if (uname == null) {
            uname = username;
        }

        if (url == null) {
            url = ContextProviderUrl;
        }


        Context ic = null;
        ConnectionFactory cf;
        Connection con = null;
        try {
            Properties p = new Properties();
            p.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            p.put(Context.URL_PKG_PREFIXES, URL_PKG_PREFIXES);
            p.put(Context.PROVIDER_URL, url);
            ic = new InitialContext(p);
            String destinstation = Destination;
            boolean istopic = Topic;
            if (!Utility.stringIsNullOrEmpty(destinationOverride)) {
                destinstation = destinationOverride;
                istopic = isTopicOverride;
            }
            cf = (ConnectionFactory) ic.lookup(ConnectionFactoryLookup);
            if (istopic) {
                Topic topic = (Topic) ic.lookup(destinstation);
                if (!Utility.stringIsNullOrEmpty(uname) && (!Utility.stringIsNullOrEmpty(pwd))) {
                    con = cf.createConnection(uname, pwd);
                } else {
                    con = cf.createConnection();
                }
                Session s = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer pub = s.createProducer(topic);
                javax.jms.TextMessage msg = s.createTextMessage(XmlMessage);
                pub.send(msg);
            } else {
                Queue topic = (Queue) ic.lookup(destinstation);
                if (!Utility.stringIsNullOrEmpty(uname) && (!Utility.stringIsNullOrEmpty(pwd))) {
                    con = cf.createConnection(uname, pwd);
                } else {
                    con = cf.createConnection();
                }
                Session s = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer pub = s.createProducer(topic);
                javax.jms.TextMessage msg = s.createTextMessage(XmlMessage);
                pub.send(msg);
            }
            log.log(Level.INFO, "JMS Alert successfully published to " + (istopic ? "topic " : "queue ") + " " + destinstation);
            ok = true;
        } catch (Exception ex) {
            log.log(Level.WARN, "Unable to deliver JMS Alert! ", ex);
        } finally {
            if (ic != null) {
                try {
                    ic.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (JMSException ex) {
                }
            }
        }
        return ok;
    }
    private static final String KEY = "JMSAlerts";

    private static void Configure(boolean pooled) {
        boolean ok = true;
        KeyNameValueEnc p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "ContextProviderUrl");
        if (p != null && p.getKeyNameValue() != null) {
            ContextProviderUrl = p.getKeyNameValue().getPropertyValue();
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
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "ConnectionFactoryLookup");
        if (p != null && p.getKeyNameValue() != null) {
            ConnectionFactoryLookup = p.getKeyNameValue().getPropertyValue();
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "INITIAL_CONTEXT_FACTORY");
        if (p != null && p.getKeyNameValue() != null) {
            INITIAL_CONTEXT_FACTORY = p.getKeyNameValue().getPropertyValue();
        }
        p = DBSettingsLoader.GetPropertiesFromDB(pooled, KEY, "URL_PKG_PREFIXES");
        if (p != null && p.getKeyNameValue() != null) {
            URL_PKG_PREFIXES = p.getKeyNameValue().getPropertyValue();
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

//validate config
        if (Utility.stringIsNullOrEmpty(URL_PKG_PREFIXES)) {
            ok = false;
        }
        if (Utility.stringIsNullOrEmpty(INITIAL_CONTEXT_FACTORY)) {
            ok = false;
        }
        if (Utility.stringIsNullOrEmpty(ConnectionFactoryLookup)) {
            ok = false;
        }
        if (Utility.stringIsNullOrEmpty(Destination)) {
            ok = false;
        }
        if (Utility.stringIsNullOrEmpty(ContextProviderUrl)) {
            ok = false;
        }

        if (ok) {
            isconfigured = true;
        } else {
            isconfigured = false;
        }
    }

//    @Override
    public void ProcessAction(AlertContainer alert) {
        SendAlert(alert);

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
            outError.set("The administrator hasn't configured the default settings yet using General Settings. JMS Alerts won't be available until then." + outError.get());
            return false;
        }

    }

    @Override
    public String GetHtmlFormattedHelp() {
        return "Sends a WSDM Alert via JMS<br>This will send a WSDM formatted XML message that corresponds to the type of rule that was triggered."
                + "Most settings are configured via the <b>General Settings</b> page, however they can be overridden using optional parameters."
                + "<ul>"
                + "<li>destinationOverride - delivers alerts to a different topic/queue other than the administrator defined one.</li>"
                + "<li>isTopicOverride - true/false, set to true when the destinationOverride is a topic, otherwise false. </li>"
                + "<li>ConnectionURL - delivers alerts to a different JMS server or URL other than the administrator defined one. Uses the same formatting rules as in <b>General Settings</b> (%s for credentials and key stores)</li>"
                + "<li>username - use a different username other than the administrator defined one</li>"
                + "<li>password - user a different password other than the administrator defined one. This should be encrypted</li>";
    }

    @Override
    public String GetDisplayName() {
        return "JMS Alert";
    }

    @Override
    public void ProcessAction(AlertContainer alert, List<NameValuePair> params) {
          SendAlert(alert);
    }

    @Override
    public List<PolicyType> GetAppliesTo() {
        return Utility.getAllPolicyTypes();
    }
}
