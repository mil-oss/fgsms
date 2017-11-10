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
package org.miloss.fgsms.smoke.wsn;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import org.junit.AfterClass;
import org.miloss.fgsms.common.Utility;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PolicyConfigurationService;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RemoveGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettingsResponseMsg;
import org.miloss.fgsms.wsn.broker.WSNotificationBroker;

/**
 *
 * @author AO
 */
public class SmokeTestWSNAlertTest {

     static final String JMSAlerts = "WSNAlerts";
     static Properties p = null;

     static {
          try {
               p = new Properties();
               p.load(new FileInputStream(new File("../test.properties")));
          } catch (Exception ex) {
               ex.printStackTrace();
          }
     }
     static boolean valuesinsert = false;
     static PCS pcsPort;

     static Endpoint ep, ep2;

     public static void configure(boolean ssl) throws Exception {
          p = new Properties();
          p.load(new FileInputStream(new File("../test.properties")));

          PolicyConfigurationService client = new PolicyConfigurationService();
          pcsPort = client.getPCSPort();
          ((BindingProvider) pcsPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, p.getProperty("fgsmsadminuser"));
          ((BindingProvider) pcsPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, p.getProperty("fgsmsadminpass"));
          ((BindingProvider) pcsPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, p.getProperty("pcsurl"));

          GetGeneralSettingsRequestMsg req = new GetGeneralSettingsRequestMsg();
          req.setClassification(new SecurityWrapper());
          GetGeneralSettingsResponseMsg generalSettings = pcsPort.getGeneralSettings(req);
          int rowcount = 0;
          for (int i = 0; i < generalSettings.getKeyNameValue().size(); i++) {
               if (generalSettings.getKeyNameValue().get(i).getPropertyKey().equalsIgnoreCase(JMSAlerts)) {
                    rowcount++;
               }
          }
          if (rowcount > 0) {
               System.out.println("WSN alert settings already configured");
          } else {
               callbackurl = "http://localhost:9999/WSNBroker";
               ep = Endpoint.publish(callbackurl, new WSNotificationBroker());
               p.setProperty("wsn.BrokerURL", callbackurl);
               System.out.println("setting up WSN alert settings from properties file");
               SetGeneralSettingsRequestMsg request = new SetGeneralSettingsRequestMsg();
               request.setClassification(new SecurityWrapper());

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "BrokerURL",
                              (String) p.getProperty("wsn.BrokerURL")), false));

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "Destination",
                              (String) p.getProperty("wsn.Destination")), false));

               SetGeneralSettingsResponseMsg setGeneralSettings = pcsPort.setGeneralSettings(request);
               valuesinsert = true;
          }

     }

     public static void destroy() throws Exception {
          if (valuesinsert) {
               RemoveGeneralSettingsRequestMsg request = new RemoveGeneralSettingsRequestMsg();
               request.setClassification(new SecurityWrapper());
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "BrokerURL", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "Destination", null));
               pcsPort.removeGeneralSettings(request);
          }

          if (ep != null) {
               ep.stop();
          }
          ep = null;
     }
     static String callbackurl = "";
     int count = 0;
     int expectedcount = 5;
     // int timeout = 60000;

     @BeforeClass
     public static void before() throws Exception {
          configure(false);

     }

     @AfterClass
     public static void after() throws Exception {
          destroy();
     }

     /**
      * Testing transactional based alerts from fgsms.Services.war
      */
     @org.junit.Test
     public void testDoSmokeTransactionalTest() throws Exception {
          Assume.assumeTrue(((String) p.getProperty("wsn.BrokerURL") != null) && (((String) p.getProperty("wsn.BrokerURL")).length() > 0));
          try {
               if (!new SmokeTestWSNAlert().run(false, (String) p.getProperty("wsn.submgrurl"))) {
                    fail("unexpected failure");
               }
          } catch (Exception ex) {
               ex.printStackTrace();
               fail("unexpected failure");
          }
     }

     /**
      * Testing transactional based alerts from fgsms.Services.war
      */
     @org.junit.Test
     public void testDoSmokeNonTransactionalTest() throws Exception {
          Assume.assumeTrue(((String) p.getProperty("wsn.BrokerURL") != null) && (((String) p.getProperty("wsn.BrokerURL")).length() > 0));
          // Assume.assumeTrue(((String)p.getProperty("wsn.submgrurl")!=null) && (((String)p.getProperty("wsn.submgrurl")).length()>0));
          try {
               if (!new SmokeTestWSNAlertNT().run(false, (String) p.getProperty("wsn.submgrurl"))) {
                    fail("unexpected failure");
               }
          } catch (Exception ex) {
               ex.printStackTrace();
               fail("unexpected failure");
          }
     }
}
