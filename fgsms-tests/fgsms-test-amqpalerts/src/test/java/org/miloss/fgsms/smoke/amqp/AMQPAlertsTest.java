/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular file,
 * then You may include the notice in a location (such as a LICENSE file in a
 * relevant directory) where a recipient would be likely to look for such a
 * notice.
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
package org.miloss.fgsms.smoke.amqp;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.BindingProvider;
import org.junit.AfterClass;
import org.miloss.fgsms.common.Utility;
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

import java.io.File;
import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;
import org.junit.Assert;

/**
 *
 * @author AO
 */
public class AMQPAlertsTest {

     static org.apache.qpid.server.Broker broker;

     @BeforeClass
     public static void setUpQpid() throws Exception {
          Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
               @Override
               public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
               }
          });
          System.setProperty("qpid.work_dir", new File(".").getAbsolutePath() + File.separator + "target/work");
          //System.setProperty("qpid.http_port", "9090");
          org.apache.qpid.server.Broker broker2 = new Broker();

          BrokerOptions options = new BrokerOptions();
          options.setOverwriteConfigurationStore(true);
          //options.setManagementModeHttpPortOverride(9090);
          //options.setManagementModeJmxPortOverride(9099);
          //options.setManagementMode(false);
          options.setStartupLoggedToSystemOut(true);
          String file=new File(".").getAbsolutePath() + File.separator + "config.json";
          System.out.println(file);
          options.setConfigurationStoreLocation(file);
          broker2.startup(options);

          AMQPAlertsTest.broker = broker2;

         // Thread.sleep(50000);

     }

     @AfterClass
     public static void shutdownQpid() {
          broker.shutdown();
     }

     static final String JMSAlerts = "AMQPAlerts";
     static Properties p = null;

     static {
          try {
               p = new Properties();
               p.load(new FileInputStream(new File("../test.properties")));
          } catch (Exception ex) {
               Logger.getLogger(AMQPAlertsTest.class.getName()).log(Level.SEVERE, null, ex);
          }
     }
     static boolean valuesinsert = false;
     static PCS pcsPort;

     static AMQPAlertSmokeTest obj;

     public static void configure(boolean ssl, boolean forceSettings) throws Exception {
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
          if (!forceSettings && rowcount > 0) {
               System.out.println("AMQP alert settings already configured");
          } else {

               System.out.println("setting up AMQP alert settings from properties file");

               SetGeneralSettingsRequestMsg request = new SetGeneralSettingsRequestMsg();
               request.setClassification(new SecurityWrapper());
               if (!ssl) {
                    request.getKeyNameValueEnc().add(
                         Utility.newKeyNameValueEnc(
                              Utility.newKeyNameValue(JMSAlerts, "ConnectionURL",
                                   (String) p.getProperty("amqp.ConnectionURL")), false));
               } else {
                    request.getKeyNameValueEnc().add(
                         Utility.newKeyNameValueEnc(
                              Utility.newKeyNameValue(JMSAlerts, "ConnectionURLSSL",
                                   (String) p.getProperty("amqp.ConnectionURLSSL")), false));
               }

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "Destination",
                              (String) p.getProperty("amqp.Destination")), false));

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "DestinationType",
                              (String) p.getProperty("amqp.DestinationType")), false));

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "username",
                              (String) p.getProperty("amqp.username")), false));

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "password",
                             Utility.EN((String) p.getProperty("amqp.password"))), false));

               SetGeneralSettingsResponseMsg setGeneralSettings = pcsPort.setGeneralSettings(request);
          }

          obj = new AMQPAlertSmokeTest();
          if (ssl) {
               System.setProperty("truststore", p.getProperty("truststore"));
               System.setProperty("truststorepass", p.getProperty("truststorepass"));
          }

     }

     public static void destroy() throws Exception {
          if (valuesinsert) {
               RemoveGeneralSettingsRequestMsg request = new RemoveGeneralSettingsRequestMsg();
               request.setClassification(new SecurityWrapper());
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "ConnectionURL", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "ConnectionURLSSL", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "Destination", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "DestinationType", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "ConnectionFactoryLookup", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "username", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "password", null));
               pcsPort.removeGeneralSettings(request);
          }
     }

     /**
      * Test of main method, of class AMQPAlertSmokeTest.
      */
     @org.junit.Test
     public void testSecure() throws Exception {
          Assume.assumeTrue(p.getProperty("amqp.ConnectionURLSSL") != null && p.getProperty("amqp.ConnectionURLSSL").length() >= 0);
          configure(true,true);
          try {
               if (obj.run(p)) {
                    destroy();

                    Assert.fail("unexpected failure");
               }
          } catch (Exception ex) {
               ex.printStackTrace();
               destroy();
               Assert.fail("unexpected failure");
          }
     }

     @org.junit.Test
     public void testUnsecure() throws Exception {
          Assume.assumeTrue(p.getProperty("amqp.ConnectionURL") != null && p.getProperty("amqp.ConnectionURL").length() >= 0);
          configure(false,true);
          try {
               if (obj.run(p)) {
                    destroy();
                    Assert.fail("unexpected failure");
               }
          } catch (Exception ex) {
               ex.printStackTrace();
               destroy();
               Assert.fail("unexpected failure");
          }
     }

}
