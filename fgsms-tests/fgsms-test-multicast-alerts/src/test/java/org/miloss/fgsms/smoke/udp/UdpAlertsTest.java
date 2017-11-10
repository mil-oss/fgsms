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
package org.miloss.fgsms.smoke.udp;

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
import org.junit.Assert;

/**
 *
 * @author AO
 */
public class UdpAlertsTest {

  
     @BeforeClass
     public static void setUpQpid() throws Exception {
          Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
               @Override
               public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
               }
          });
          System.setProperty("qpid.work_dir", new File(".").getAbsolutePath() + File.separator + "target/work");
        
     }

     @AfterClass
     public static void shutdownQpid() {
          
     }

     static final String JMSAlerts = "MulticastAlerting";
     static Properties p = null;

     static {
          try {
               p = new Properties();
               p.load(new FileInputStream(new File("../test.properties")));
          } catch (Exception ex) {
               Logger.getLogger(UdpAlertsTest.class.getName()).log(Level.SEVERE, null, ex);
          }
     }
     static boolean valuesinsert = false;
     static PCS pcsPort;

     static MulticastAlertSmokeTest obj;

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
               System.out.println("Multicast alert settings already configured");
          } else {

               System.out.println("setting up Multicast alert settings from properties file");

               SetGeneralSettingsRequestMsg request = new SetGeneralSettingsRequestMsg();
               request.setClassification(new SecurityWrapper());
             
                    request.getKeyNameValueEnc().add(
                         Utility.newKeyNameValueEnc(
                              Utility.newKeyNameValue(JMSAlerts, "ConnectionURL",
                                   (String) p.getProperty("Multicast.ConnectionURL")), false));
             
               SetGeneralSettingsResponseMsg setGeneralSettings = pcsPort.setGeneralSettings(request);
          }

          obj = new MulticastAlertSmokeTest();
         

     }

     public static void destroy() throws Exception {
          if (valuesinsert) {
               RemoveGeneralSettingsRequestMsg request = new RemoveGeneralSettingsRequestMsg();
               request.setClassification(new SecurityWrapper());
               request.getKeyNameValue().add(Utility.newKeyNameValue(JMSAlerts, "ConnectionURL", null));
              
               pcsPort.removeGeneralSettings(request);
          }
     }

    

     @org.junit.Test
     public void testMultiucast() throws Exception {
          Assume.assumeTrue(p.getProperty("Multicast.ConnectionURL") != null && p.getProperty("Multicast.ConnectionURL").length() >= 0);
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
