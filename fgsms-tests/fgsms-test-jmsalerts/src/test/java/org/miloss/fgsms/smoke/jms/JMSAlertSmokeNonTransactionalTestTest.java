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
package org.miloss.fgsms.smoke.jms;

import org.miloss.fgsms.smoke.jms.JMSAlertSmokeTestNT;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.BindingProvider;
import org.junit.AfterClass;
import org.miloss.fgsms.common.Utility;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PolicyConfigurationService;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RemoveGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettingsResponseMsg;

/**
 *
 * @author AO
 */
public class JMSAlertSmokeNonTransactionalTestTest {

     static final String JMSAlerts = "JMSAlerts";
     static Properties p = null;
     static {
          try {
               p = new Properties();
               p.load(new FileInputStream(new File("../test.properties")));
          } catch (Exception ex) {
               Logger.getLogger(JMSAlertSmokeNonTransactionalTestTest.class.getName()).log(Level.SEVERE, null, ex);
          }

     }
     static boolean valuesinsert = false;
     static PCS pcsPort;
     
     
    @BeforeClass
    public static void before() throws Exception{
          System.out.println("Starting HornetQ server ...");
        
           HornetBroker.start();
    }

     @AfterClass
    public static void after() throws Exception{
         HornetBroker.stop();
        
            // Stopping the listener
            System.out.println("Stopping HornetQ server ...");
            
            System.out.println("Done.");
    }

     public static void configure() throws Exception {
        
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
          //if (rowcount > 0) {
          //     System.out.println("JMSAlerts alert settings already configured");
          //} else 
          {

               System.out.println("setting up JMSAlerts alert settings from properties file");

               SetGeneralSettingsRequestMsg request = new SetGeneralSettingsRequestMsg();
               request.setClassification(new SecurityWrapper());
               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "ContextProviderUrl",
                              (String) p.getProperty("jms.ContextProviderUrl")), false));
             

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "Destination",
                              (String) p.getProperty("jms.Destination")), false));

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "DestinationType",
                              (String) p.getProperty("jms.DestinationType")), false));

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "ConnectionFactoryLookup",
                              (String) p.getProperty("jms.ConnectionFactoryLookup")), false));

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "INITIAL_CONTEXT_FACTORY",
                              (String) p.getProperty("jms.INITIAL_CONTEXT_FACTORY")), false));

               request.getKeyNameValueEnc().add(
                    Utility.newKeyNameValueEnc(
                         Utility.newKeyNameValue(JMSAlerts, "URL_PKG_PREFIXES",
                              (String) p.getProperty("jms.URL_PKG_PREFIXES")), false));

               SetGeneralSettingsResponseMsg setGeneralSettings = pcsPort.setGeneralSettings(request);
               valuesinsert = true;
          }

     }

     public static void destroy() throws Exception {
          if (valuesinsert) {
               RemoveGeneralSettingsRequestMsg request = new RemoveGeneralSettingsRequestMsg();
               request.setClassification(new SecurityWrapper());
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "ContextProviderUrl", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "Destination", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "DestinationType", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "ConnectionFactoryLookup", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "INITIAL_CONTEXT_FACTORY", null));
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "URL_PKG_PREFIXES", null));
               pcsPort.removeGeneralSettings(request);
          }
     }

     /**
      * Test of main method, of class JMSAlertSmokeTestNT.
      */
     @org.junit.Test
     public void testMain() throws Exception {
          System.out.println("JMS Alert Non Transactional");
          configure();
          String[] args = null;
          boolean runtest = JMSAlertSmokeTestNT.runtest();
          destroy();
          assertTrue(runtest);

     }
}
