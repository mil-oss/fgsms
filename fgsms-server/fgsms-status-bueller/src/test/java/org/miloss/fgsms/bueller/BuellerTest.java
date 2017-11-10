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
package org.miloss.fgsms.bueller;

import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Ignore;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.test.WebServiceBaseTests;

/**
 * This tests some functionality for Bueller, the remainder is tested via
 * tests/SmokeTestBueller. Many of these tests are dependent on local
 * configuration AND the fgsms server has to be running on port 8180
 *
 * @author AO
 */
public class BuellerTest extends WebServiceBaseTests {

     public BuellerTest() throws Exception {
          super();
          url = "http://localhost:4444/bueller";
          Init();
             instance = new Bueller();
          instance.Init(true);
     }

     

     static Bueller instance;

     /**
      * Test of Fire method, of class Bueller.
      */
     @org.junit.Test
     public void testNTLMv2_Localhost() throws Exception {
          System.out.println("testNTLMv2_Localhost");
          String url = IpAddressUtility.modifyURL("http://localhost/ntlm", true);
          String[] GetCredentials = DBSettingsLoader.GetCredentials(true, url);
          if (GetCredentials == null) {
               Assume.assumeTrue(false);
          }
          Assume.assumeNotNull(GetCredentials[0]);

          //Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("win"));
          String s = instance.sendGetRequestAuth(true, "http://localhost/ntlm", url,0);
          if (s.equalsIgnoreCase("OK")) {
          } else {
               fail(s);
          }

     }

     @Ignore
     @org.junit.Test
     public void testBasic_Localhost() throws Exception {
          System.out.println("testBasic_Localhost");
          String url = IpAddressUtility.modifyURL("http://localhost:8180/fgsmsServices/PCS", true);

          String s = instance.sendGetRequestAuth(true, "http://localhost:8180/fgsmsServices/PCS?wsdl", url,0);
          if (s.equalsIgnoreCase("OK")) {
          } else {
               fail(s);
          }
     }

     @org.junit.Test
     public void testDigest_Localhost() throws Exception {
          System.out.println("testDigest_Localhost");
          String url = IpAddressUtility.modifyURL("http://localhost:8180/DigestWeb", true);
          String[] GetCredentials = DBSettingsLoader.GetCredentials(true, url);
          if (GetCredentials == null) {
               Assume.assumeTrue(false);
          }
          Assume.assumeNotNull(GetCredentials[0]);
          String s = instance.sendGetRequestAuth(true, "http://localhost:8180/DigestWeb", url,0);
          if (s.equalsIgnoreCase("OK")) {
          } else {
               if (s.equalsIgnoreCase("404")) {
                    System.out.println("testDigest_Localhost " + "http://localhost:8180/DigestWeb test web app probably isn't installed");
                    Assume.assumeTrue(false);
               }
               fail(s);
          }
     }
}
