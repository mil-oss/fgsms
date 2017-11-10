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
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */
package org.miloss.fgsms.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXB;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.miloss.fgsms.plugins.federation.FederationInterface;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLARuleGeneric;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.miloss.fgsms.test.WebServiceBaseTests;

/**
 *
 * @author alex
 */
public class HelperTest extends WebServiceBaseTests {

     public HelperTest() throws Exception {
          url = "http://localhost:8080/uihelper";
          Init();
     }

     @BeforeClass
     public static void setUpClass() {
     }

     @AfterClass
     public static void tearDownClass() {
     }

     @Before
     public void setUp() {
     }

     @After
     public void tearDown() {
     }

     @Test
     public void testBuildFromHttpPostFedCol() throws Exception {
          System.out.println("testBuildFromHttpPostFedCol");
          Map<String, String> params = new HashMap<String, String>();
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_SLA", "");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_Range", "300000");
          params.put("sla_fdd73a15-b3e8-4c1d-9164-101e74f23fee_RULE_class", "org.miloss.fgsms.sla.rules.StaleData");
          params.put("combo_SLA_ACTION", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("save_policy", "true");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_Success", "");
          params.put("plugin_PUBLISH_Maximums", "");
          params.put("plugin_PUBLISH_Averages", "");
          params.put("plugin_PUBLISH_SLA", "");
          params.put("username", "username");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_Uptime", "");
          params.put("plugin_ACTION_destinationOverride", "");
          params.put("plugin_PUBLISH_Faults", "");
          params.put("sla_ca4bbb3b-e2fb-40a1-b313-1b799dfeb089_RULE_class", "org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus");
          params.put("plugin_PUBLISH_Binding", "uddi:something");
          params.put("plugin_RULE_value", "");
          params.put("sla_ca4bbb3b-e2fb-40a1-b313-1b799dfeb089_ACTION_6a8a1392-b582-4dd5-91d3-0a92692fd2a2_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_SLA", "on");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_Status", "");
          params.put("plugin_PUBLISH_Status", "");
          params.put("plugin_PUBLISH_Range", "300000");
          params.put("sla_fdd73a15-b3e8-4c1d-9164-101e74f23fee_ACTION_d287acb3-0cff-4e49-bf85-a0e8ab38edb0_Body_", "Stale Data Alert for http://config-virtualbox:8080/fgsmsServices/services/PCS. This is the default alert that indicates that either a server or agent is offline. This alert can be changed by changing the policy for this service.");
          params.put("sla_ca4bbb3b-e2fb-40a1-b313-1b799dfeb089_ACTION_6a8a1392-b582-4dd5-91d3-0a92692fd2a2_enc_Subject_", "false");
          params.put("url", "http://config-virtualbox:8080/fgsmsServices/services/PCS");
          params.put("sla_fdd73a15-b3e8-4c1d-9164-101e74f23fee_ACTION_d287acb3-0cff-4e49-bf85-a0e8ab38edb0_enc_Subject_", "false");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_Maximums", "on");
          params.put("nonce", "6355bc3d-c5c9-4eb2-a0ee-4f865d5b6b00");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_Range", "on");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_class", "org.miloss.fgsms.uddipub.UddiPublisher");
          params.put("sla_fdd73a15-b3e8-4c1d-9164-101e74f23fee_ACTION_d287acb3-0cff-4e49-bf85-a0e8ab38edb0_Subject_", "Stale Data Alert for http://config-virtualbox:8080/fgsmsServices/services/PCS");
          params.put("plugin_ACTION_ConnectionURL", "");
          params.put("sla_fdd73a15-b3e8-4c1d-9164-101e74f23fee_ACTION_d287acb3-0cff-4e49-bf85-a0e8ab38edb0_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("sla_fdd73a15-b3e8-4c1d-9164-101e74f23fee_ACTION_d287acb3-0cff-4e49-bf85-a0e8ab38edb0_enc_Body_", "false");
          params.put("plugin_ACTION_username", "");
          params.put("sla_ca4bbb3b-e2fb-40a1-b313-1b799dfeb089_ACTION_6a8a1392-b582-4dd5-91d3-0a92692fd2a2_enc_Body_", "false");
          params.put("sla_ca4bbb3b-e2fb-40a1-b313-1b799dfeb089_ACTION_6a8a1392-b582-4dd5-91d3-0a92692fd2a2_Body_", "Change of status for http://config-virtualbox:8080/fgsmsServices/services/PCS. This is the default status alert. This can be changed by changing the policy for this service.");
          params.put("plugin_PUBLISH_Uptime", "");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_Binding", "uddi:something");
          params.put("plugin_ACTION_enc_password", "on");
          params.put("plugin_PUBLISH_Success", "");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_Faults", "");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_Binding", "on");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_Averages", "on");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_Uptime", "on");
          params.put("sla_ca4bbb3b-e2fb-40a1-b313-1b799dfeb089_ACTION_6a8a1392-b582-4dd5-91d3-0a92692fd2a2_Subject_", "Change of status for http://config-virtualbox:8080/fgsmsServices/services/PCS");
          params.put("adduserperm", "read");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_Averages", "");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_Success", "on");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_Maximums", "");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_Status", "on");
          params.put("plugin_ACTION_password", "");
          params.put("plugin_ACTION_isTopicOverride", "");
          params.put("fed_340b24b4-75a8-43c9-9021-0fc2d087c6d6_enc_Faults", "on");

          HttpServletRequest request = new HttpServletRequestImpl(params);
          ServicePolicy pol = new TransactionalWebServicePolicy();

          ServicePolicy result = Helper.buildFromHttpPost(request, pol);
          JAXB.marshal(result, System.out);
//          assertNotNull(result.getURL());
          assertNotNull(result.getFederationPolicyCollection());
          assertNotNull(result.getFederationPolicyCollection().getFederationPolicy());
          assertEquals(1, result.getFederationPolicyCollection().getFederationPolicy().size());
          for (int i = 0; i < result.getFederationPolicyCollection().getFederationPolicy().size(); i++) {
               assertNotNull(result.getFederationPolicyCollection().getFederationPolicy().get(i).getImplementingClassName());

               Class<?> forName = Class.forName(result.getFederationPolicyCollection().getFederationPolicy().get(i).getImplementingClassName());
               FederationInterface plugin = (FederationInterface) forName.newInstance();
               AtomicReference<String> outmsg = new AtomicReference<String>();
               outmsg.set("");
               plugin.ValidateConfiguration(result.getFederationPolicyCollection().getFederationPolicy().get(i), outmsg);
               assertTrue("vaidation failed" + outmsg.get(), outmsg.get().length() == 0);
          }

     }

     @Test
     public void testBuildFromHttpPostWithUID() throws Exception {
          System.out.println("buildFromHttpPost");
          Map<String, String> params = new HashMap<String, String>();
          params.put("uid_b95e7953-a68b-4992-b146-fa9a6ff70d1d_xpathprefixes", "");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_RULE_class", "org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus");
          params.put("uid_d9320570-9c2c-48a3-b2f6-9b005b869b87_x509certxpath", "on");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_81a1c08d-72c6-43d3-8ea1-240197fe3d30_enc_Body_", "true");
          params.put("combo_SLA_ACTION", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("uid_d9320570-9c2c-48a3-b2f6-9b005b869b87_httpheadername", "header1");
          params.put("save_policy", "true");
          params.put("plugin_PUBLISH_Maximums", "");
          params.put("uid_d9320570-9c2c-48a3-b2f6-9b005b869b87_xpathprefixes", "");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_dcd310cf-cb43-49e3-b77f-5e79f2ef8532_Subject_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS");
          params.put("plugin_PUBLISH_Averages", "");
          params.put("newIdent_xpathexpression", "/header/body/element1");
          params.put("uid_d9320570-9c2c-48a3-b2f6-9b005b869b87_xpathexpression", "");
          params.put("plugin_PUBLISH_SLA", "");
          params.put("newIdent_xpathprefixes", "notdefined");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_81a1c08d-72c6-43d3-8ea1-240197fe3d30_enc_Subject_", "true");
          params.put("username", "username");
          params.put("plugin_ACTION_destinationOverride", "");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_RULE_class", "org.miloss.fgsms.sla.rules.StaleData");
          params.put("plugin_PUBLISH_Faults", "");
          params.put("description", "");
          params.put("plugin_PUBLISH_Binding", "");
          params.put("plugin_RULE_value", "");
          params.put("poc", "");
          params.put("uid_b5ba6af8-def2-49f7-9ac9-9e7561ea83e3_xpathprefixes", "notdefined");
          params.put("externalurl", "");
          params.put("plugin_PUBLISH_Status", "");
          params.put("plugin_PUBLISH_Range", "");
          params.put("uid_b5ba6af8-def2-49f7-9ac9-9e7561ea83e3_method", "xpath");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_81a1c08d-72c6-43d3-8ea1-240197fe3d30_Subject_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS");
          params.put("uid_b95e7953-a68b-4992-b146-fa9a6ff70d1d_httpheadername", "");
          params.put("url", "http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS");
          params.put("messagecap", "1024000");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_81a1c08d-72c6-43d3-8ea1-240197fe3d30_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("newIdent_useridtype", "xpath");
          params.put("uid_b95e7953-a68b-4992-b146-fa9a6ff70d1d_x509certxpath", "on");
          params.put("nonce", "46988d40-5ccb-42cc-8c66-60261b4acf63");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_dcd310cf-cb43-49e3-b77f-5e79f2ef8532_Body_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS. This is the default alert that indicates that either a server or agent is offline. This alert can be changed by changing the policy for this service.");
          params.put("plugin_ACTION_ConnectionURL", "");
          params.put("refreshrate", "3min ");
          params.put("locationlong", "");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_dcd310cf-cb43-49e3-b77f-5e79f2ef8532_enc_Subject_", "true");
          params.put("plugin_ACTION_username", "");
          params.put("plugin_PUBLISH_Uptime", "");
          params.put("bucket", "");
          params.put("plugin_ACTION_enc_password", "on");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_81a1c08d-72c6-43d3-8ea1-240197fe3d30_Body_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS. This is the default status alert. This can be changed by changing the policy for this service.");
          params.put("newIdent_httpheadername", "header1");
          params.put("uid_b5ba6af8-def2-49f7-9ac9-9e7561ea83e3_httpheadername", "header1");
          params.put("plugin_PUBLISH_Success", "");
          params.put("domainname", "");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_dcd310cf-cb43-49e3-b77f-5e79f2ef8532_enc_Body_", "false");
          params.put("parentobject", "");
          params.put("recordfault", "recordfault");
          params.put("adduserperm", "read");
          params.put("uid_b5ba6af8-def2-49f7-9ac9-9e7561ea83e3_x509certxpath", "on");
          params.put("locationlat", "");
          params.put("buellerenabled", "on");
          params.put("uid_b95e7953-a68b-4992-b146-fa9a6ff70d1d_xpathexpression", "");
          params.put("servername", "alexs-mbp-2.home");
          params.put("newIdent_x509certxpath", "on");
          params.put("uid_b95e7953-a68b-4992-b146-fa9a6ff70d1d_method", "httpcred");
          params.put("uid_b5ba6af8-def2-49f7-9ac9-9e7561ea83e3_xpathexpression", "/header/body/element1");
          params.put("datattl", "30d ");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_dcd310cf-cb43-49e3-b77f-5e79f2ef8532_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("plugin_ACTION_password", "");
          params.put("plugin_ACTION_isTopicOverride", "");
          params.put("displayName", "");
          params.put("uid_d9320570-9c2c-48a3-b2f6-9b005b869b87_method", "httpheader");

          HttpServletRequest request = new HttpServletRequestImpl(params);
          ServicePolicy pol = new TransactionalWebServicePolicy();

          TransactionalWebServicePolicy result = (TransactionalWebServicePolicy) Helper.buildFromHttpPost(request, pol);
          JAXB.marshal(result, System.out);
//          assertNotNull(result.getURL());
          assertNotNull(result.getUserIdentification());
          assertNotNull(result.getUserIdentification().getUserIdentity());
          assertFalse(result.getUserIdentification().getUserIdentity().isEmpty());
          boolean foundHttp = false;
          boolean foundHeader = false;
          boolean foundXpath = false;
          for (int i = 0; i < result.getUserIdentification().getUserIdentity().size(); i++) {
               if (result.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential() != null
                    && result.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential()) {
                    foundHttp = true;
               }
               if (result.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader() != null
                    && result.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader()) {
                    foundHeader = true;
                    assertEquals("header1", result.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName());
               }
               if (result.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                    && result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType() != null
                    && !result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().isEmpty()) {

                    foundXpath = true;
                    assertEquals(1, result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().size());
                    assertEquals("/header/body/element1", result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(0).getXPath());
                    assertEquals(true, result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(0).isIsCertificate());
                    //TODO
               }

          }
          assertTrue(foundHeader);
          assertTrue(foundHttp);
          assertTrue(foundXpath);

     }

     @Test
     public void testUserIdentwithXpath() throws Exception {
          System.out.println("buildFromHttpPost");
          Map<String, String> params = new HashMap<String, String>();
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_RULE_class", "org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus");
          params.put("uid_ba1de898-4831-4c8f-9244-0a6cb516fc3e_xpathprefixes", "");
          params.put("uid_ba1de898-4831-4c8f-9244-0a6cb516fc3e_httpheadername", "");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_1eaefe37-50c6-410e-99c6-954ae78985f7_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("combo_SLA_ACTION", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("uid_1cb7375d-c2d6-424d-bb17-a867d230598e_httpheadername", "header1");
          params.put("save_policy", "true");
          params.put("plugin_PUBLISH_Maximums", "");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_531f0aa8-f2cf-495c-8881-6551504fb1d4_enc_Subject_", "true");
          params.put("plugin_PUBLISH_Averages", "");
          params.put("newIdent_xpathexpression", "/header/body/element1");
          params.put("plugin_PUBLISH_SLA", "");
          params.put("newIdent_xpathprefixes", "notused");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_531f0aa8-f2cf-495c-8881-6551504fb1d4_Body_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS. This is the default alert that indicates that either a server or agent is offline. This alert can be changed by changing the policy for this service.");
          params.put("username", "username");
          params.put("plugin_ACTION_destinationOverride", "");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_1eaefe37-50c6-410e-99c6-954ae78985f7_Subject_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_RULE_class", "org.miloss.fgsms.sla.rules.StaleData");
          params.put("plugin_PUBLISH_Faults", "");
          params.put("description", "");
          params.put("plugin_PUBLISH_Binding", "");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_1eaefe37-50c6-410e-99c6-954ae78985f7_enc_Body_", "true");
          params.put("plugin_RULE_value", "");
          params.put("uid_ab8ff66d-9175-4562-ac96-8c836d8de0f9_method", "xpath");
          params.put("poc", "");
          params.put("externalurl", "");
          params.put("plugin_PUBLISH_Status", "");
          params.put("plugin_PUBLISH_Range", "");
          params.put("url", "http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS");
          params.put("messagecap", "1024000");
          params.put("uid_1cb7375d-c2d6-424d-bb17-a867d230598e_x509certxpath", "on");
          params.put("newIdent_useridtype", "xpath");
          params.put("nonce", "68832b95-0041-4111-a4a7-1b39eaa9bdaa");
          params.put("plugin_ACTION_ConnectionURL", "");
          params.put("refreshrate", "3min ");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_531f0aa8-f2cf-495c-8881-6551504fb1d4_enc_Body_", "false");
          params.put("locationlong", "");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_1eaefe37-50c6-410e-99c6-954ae78985f7_Body_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS. This is the default status alert. This can be changed by changing the policy for this service.");
          params.put("plugin_ACTION_username", "");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_531f0aa8-f2cf-495c-8881-6551504fb1d4_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("plugin_PUBLISH_Uptime", "");
          params.put("sla_b9017f23-5f2c-4cdb-a8b2-6f50ee3748eb_ACTION_531f0aa8-f2cf-495c-8881-6551504fb1d4_Subject_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/PCS");
          params.put("bucket", "");
          params.put("plugin_ACTION_enc_password", "on");
          params.put("newIdent_httpheadername", "header1");
          params.put("plugin_PUBLISH_Success", "");
          params.put("uid_ba1de898-4831-4c8f-9244-0a6cb516fc3e_x509certxpath", "on");
          params.put("domainname", "");
          params.put("sla_07585aca-5e1d-46d8-9f4c-2ab87cf241eb_ACTION_1eaefe37-50c6-410e-99c6-954ae78985f7_enc_Subject_", "true");
          params.put("parentobject", "");
          params.put("uid_ab8ff66d-9175-4562-ac96-8c836d8de0f9_xpathprefixes", "soap##http://www.w3.org/soap|security##http://www.secure-madeup.org");
          params.put("uid_ba1de898-4831-4c8f-9244-0a6cb516fc3e_method", "httpcred");
          params.put("uid_1cb7375d-c2d6-424d-bb17-a867d230598e_method", "httpheader");
          params.put("uid_ab8ff66d-9175-4562-ac96-8c836d8de0f9_httpheadername", "header1");
          params.put("recordfault", "recordfault");
          params.put("adduserperm", "read");
          params.put("locationlat", "");
          params.put("buellerenabled", "on");
          params.put("servername", "alexs-mbp-2.home");
          params.put("uid_ba1de898-4831-4c8f-9244-0a6cb516fc3e_xpathexpression", "");
          params.put("uid_ab8ff66d-9175-4562-ac96-8c836d8de0f9_x509certxpath", "on");
          params.put("uid_1cb7375d-c2d6-424d-bb17-a867d230598e_xpathexpression", "");
          params.put("newIdent_x509certxpath", "on");
          params.put("datattl", "30d ");
          params.put("plugin_ACTION_password", "");
          params.put("uid_ab8ff66d-9175-4562-ac96-8c836d8de0f9_xpathexpression", "/header/body/element1");
          params.put("plugin_ACTION_isTopicOverride", "");
          params.put("displayName", "");
          params.put("uid_1cb7375d-c2d6-424d-bb17-a867d230598e_xpathprefixes", "");

          HttpServletRequest request = new HttpServletRequestImpl(params);
          ServicePolicy pol = new TransactionalWebServicePolicy();

          TransactionalWebServicePolicy result = (TransactionalWebServicePolicy) Helper.buildFromHttpPost(request, pol);
          JAXB.marshal(result, System.out);
//          assertNotNull(result.getURL());
          assertNotNull(result.getUserIdentification());
          assertNotNull(result.getUserIdentification().getUserIdentity());
          assertFalse(result.getUserIdentification().getUserIdentity().isEmpty());
          boolean foundHttp = false;
          boolean foundHeader = false;
          boolean foundXpath = false;
          for (int i = 0; i < result.getUserIdentification().getUserIdentity().size(); i++) {
               if (result.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential() != null
                    && result.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential()) {
                    foundHttp = true;
               }
               if (result.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader() != null
                    && result.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader()) {
                    foundHeader = true;
                    assertEquals("header1", result.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName());
               }
               if (result.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                    && result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType() != null
                    && !result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().isEmpty()) {

                    foundXpath = true;
                    assertEquals(1, result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().size());
                    assertEquals("/header/body/element1", result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(0).getXPath());
                    assertEquals(true, result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(0).isIsCertificate());
                    //TODO
               }

          }
          assertTrue(foundHeader);
          assertTrue(foundHttp);
          assertTrue(foundXpath);

     }

     @Test

     public void testBuildFromHttPost3UserIdents() throws Exception {
          System.out.println("testBuildFromHttPost3UserIdents");
          Map<String, String> params = new HashMap<String, String>();
          params.put("plugin_PUBLISH_Range", "");
          params.put("adduserperm", "read");
          params.put("parentobject", "");
          params.put("combo_SLA_ACTION", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("plugin_ACTION_isTopicOverride", "");
          params.put("url", "http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_d9f4bcd6-08e2-4e90-9bbf-c3d188ee373a_enc_Body_", "true");
          params.put("plugin_PUBLISH_Success", "");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_d9f4bcd6-08e2-4e90-9bbf-c3d188ee373a_Subject_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS");
          params.put("domainname", "");
          params.put("newIdent_httpheadername", "");
          params.put("plugin_RULE_value", "");
          params.put("description", "");
          params.put("externalurl", "");
          params.put("servername", "alexs-mbp-2.home");
          params.put("nonce", "1f211816-523c-480b-9822-38538fb2342a");
          params.put("plugin_PUBLISH_SLA", "");
          params.put("plugin_ACTION_enc_password", "on");
          params.put("recordfault", "recordfault");
          params.put("uid_7abc32a5-e6c1-4e9b-9068-8e3f42e98859_method", "httpcred");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_a80dbe80-260e-45de-b460-067c53565afa_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("newIdent_xpathprefixes", "");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_d9f4bcd6-08e2-4e90-9bbf-c3d188ee373a_enc_Subject_", "false");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_d9f4bcd6-08e2-4e90-9bbf-c3d188ee373a_Body_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS. This is the default status alert. This can be changed by changing the policy for this service.");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_RULE_class", "org.miloss.fgsms.sla.rules.StaleData");
          params.put("uid_533fcd01-ce28-4bd7-9c4a-7c6755b6fe77_httpheadername", "/body/element1");
          params.put("uid_c075c384-57d6-4059-a1f9-5cf9d5700248_method", "httpheader");
          params.put("locationlong", "");
          params.put("uid_c075c384-57d6-4059-a1f9-5cf9d5700248_httpheadername", "asdasd");
          params.put("displayName", "");
          params.put("plugin_PUBLISH_Binding", "");
          params.put("bucket", "");
          params.put("plugin_PUBLISH_Maximums", "");
          params.put("newIdent_x509certxpath", "on");
          params.put("buellerenabled", "on");
          params.put("username", "username");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_RULE_class", "org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus");
          params.put("newIdent_xpathexpression", "/body/element1");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_a80dbe80-260e-45de-b460-067c53565afa_Subject_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS");
          params.put("plugin_ACTION_password", "");
          params.put("plugin_ACTION_ConnectionURL", "");
          params.put("plugin_ACTION_username", "");
          params.put("plugin_PUBLISH_Averages", "");
          params.put("poc", "");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_a80dbe80-260e-45de-b460-067c53565afa_enc_Body_", "false");
          params.put("save_policy", "true");
          params.put("plugin_ACTION_destinationOverride", "");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_a80dbe80-260e-45de-b460-067c53565afa_enc_Subject_", "true");
          params.put("uid_533fcd01-ce28-4bd7-9c4a-7c6755b6fe77_method", "xpath");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_d9f4bcd6-08e2-4e90-9bbf-c3d188ee373a_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("locationlat", "");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_a80dbe80-260e-45de-b460-067c53565afa_Body_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS. This is the default alert that indicates that either a server or agent is offline. This alert can be changed by changing the policy for this service.");
          params.put("newIdent_useridtype", "xpath");
          params.put("refreshrate", "3min ");
          params.put("plugin_PUBLISH_Uptime", "");
          params.put("plugin_PUBLISH_Faults", "");
          params.put("datattl", "30d ");
          params.put("plugin_PUBLISH_Status", "");
          params.put("messagecap", "1024000");

          HttpServletRequest request = new HttpServletRequestImpl(params);
          ServicePolicy pol = new TransactionalWebServicePolicy();

          TransactionalWebServicePolicy result = (TransactionalWebServicePolicy) Helper.buildFromHttpPost(request, pol);
          JAXB.marshal(result, System.out);
//          assertNotNull(result.getURL());
          assertNotNull(result.getUserIdentification());
         

     }

     //saving a new xpath
     @Test
     public void testBuildFromHttpPostUserIdentXPath() throws Exception {

          System.out.println("testBuildFromHttpPostUserIdentXPath");
          Map<String, String> params = new HashMap<String, String>();

          params.put("plugin_PUBLISH_Range", "");
          params.put("uid_2a4b1ad3-8291-4ffc-8254-4def0e873cca_method", "xpath");
          params.put("uid_2a4b1ad3-8291-4ffc-8254-4def0e873cca_xpathprefixes", "ns1##google.com");
          params.put("uid_2a4b1ad3-8291-4ffc-8254-4def0e873cca_xpathexpression", "/env/body/el1");
          params.put("uid_2a4b1ad3-8291-4ffc-8254-4def0e873cca_httpheadername", "");
          params.put("uid_2a4b1ad3-8291-4ffc-8254-4def0e873cca_x509certxpath", "on");

          params.put("adduserperm", "read");
          params.put("parentobject", "");
          params.put("combo_SLA_ACTION", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_aaaa83bf-bb51-40a4-a059-ee57306e4406_Body_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS. This is the default alert that indicates that either a server or agent is offline. This alert can be changed by changing the policy for this service.");
          params.put("plugin_ACTION_isTopicOverride", "");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_9e8e20fa-2397-42b4-ba2c-2c39be41abe7_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("url", "http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS");
          params.put("plugin_PUBLISH_Success", "");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_9e8e20fa-2397-42b4-ba2c-2c39be41abe7_enc_Subject_", "true");
          params.put("domainname", "");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_9e8e20fa-2397-42b4-ba2c-2c39be41abe7_Subject_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS");
          params.put("newIdent_httpheadername", "");
          params.put("plugin_RULE_value", "");
          params.put("description", "");
          params.put("externalurl", "");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_aaaa83bf-bb51-40a4-a059-ee57306e4406_Subject_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS");
          params.put("nonce", "283351f8-7fc8-4b68-a0e7-7676c93c4dee");
          params.put("servername", "alexs-mbp-2.home");
          params.put("plugin_PUBLISH_SLA", "");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_9e8e20fa-2397-42b4-ba2c-2c39be41abe7_enc_Body_", "true");
          params.put("plugin_ACTION_enc_password", "on");
          params.put("recordfault", "recordfault");
          params.put("newIdent_xpathprefixes", "ns1##google.com");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_aaaa83bf-bb51-40a4-a059-ee57306e4406_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_ACTION_9e8e20fa-2397-42b4-ba2c-2c39be41abe7_Body_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/DAS. This is the default status alert. This can be changed by changing the policy for this service.");
          params.put("locationlong", "");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_RULE_class", "org.miloss.fgsms.sla.rules.StaleData");
          params.put("displayName", "");
          params.put("plugin_PUBLISH_Binding", "");
          params.put("bucket", "");
          params.put("plugin_PUBLISH_Maximums", "");
          params.put("newIdent_x509certxpath", "on");
          params.put("buellerenabled", "on");
          params.put("username", "username");
          params.put("sla_a31460df-e5e0-4e3f-9af3-881235e931c3_RULE_class", "org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus");
          params.put("newIdent_xpathexpression", "/env/body/el1");
          params.put("plugin_ACTION_password", "");
          params.put("plugin_ACTION_ConnectionURL", "");
          params.put("plugin_ACTION_username", "");
          params.put("plugin_PUBLISH_Averages", "");
          params.put("poc", "");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_aaaa83bf-bb51-40a4-a059-ee57306e4406_enc_Subject_", "false");
          params.put("sla_cbc9c74f-93fc-4533-adea-d054a91cb28c_ACTION_aaaa83bf-bb51-40a4-a059-ee57306e4406_enc_Body_", "true");
          params.put("save_policy", "true");
          params.put("plugin_ACTION_destinationOverride", "");
          params.put("locationlat", "");
          params.put("newIdent_useridtype", "xpath");
          params.put("refreshrate", "3min ");
          params.put("plugin_PUBLISH_Uptime", "");
          params.put("plugin_PUBLISH_Faults", "");
          params.put("datattl", "30d ");
          params.put("plugin_PUBLISH_Status", "");
          params.put("messagecap", "1024000");
          HttpServletRequest request = new HttpServletRequestImpl(params);
          ServicePolicy pol = new TransactionalWebServicePolicy();

          TransactionalWebServicePolicy result = (TransactionalWebServicePolicy) Helper.buildFromHttpPost(request, pol);
          JAXB.marshal(result, System.out);
//          assertNotNull(result.getURL());
          assertNotNull(result.getUserIdentification());
          for (int i = 0; i < result.getUserIdentification().getUserIdentity().size(); i++) {
               assertNotNull(result.getUserIdentification().getUserIdentity().get(i).getXPaths());
               assertTrue(result.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().size() == 1);
               assertTrue(result.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().size() == 1);
          }
     }

     /**
      * Test of buildFromHttpPost method, of class Helper.
      */
     @Test
     public void testBuildFromHttpPost() throws Exception {
          System.out.println("buildFromHttpPost");
          Map<String, String> params = new HashMap<String, String>();

          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_password", "");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_enc_destinationOverride", "on");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_isTopicOverride", "");
          params.put("sla_95cf1d13-104a-4d5c-9761-8a4fa4471098_RULE_class", "org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus");
          params.put("combo_SLA_ACTION", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("plugin_ACTION_enc_password", "on");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_RULE_value", "asdasd");
          params.put("save_policy", "true");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_enc_isTopicOverride", "on");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_destinationOverride", "asdasd");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_enc_ConnectionURL", "on");
          params.put("username", "username");
          params.put("plugin_ACTION_destinationOverride", "asdasd");
          params.put("sla_95cf1d13-104a-4d5c-9761-8a4fa4471098_ACTION_75a2d9ba-6b86-40d7-b283-8ca9f3beac21_Body_", "Change of status for http://config-virtualbox:8180/jUnitTestCasePCSasdasd. This is the default status alert. This can be changed by changing the policy for this service.");
          params.put("sla_753647dc-50a6-4a8d-9192-4698358ab9da_RULE_class", "org.miloss.fgsms.sla.rules.StaleData");
          params.put("plugin_RULE_value", "asdasd");
          params.put("sla_753647dc-50a6-4a8d-9192-4698358ab9da_ACTION_7e0221e7-b1b7-4918-9d8c-bb276c15ed81_enc_Subject_", "false");
          params.put("sla_753647dc-50a6-4a8d-9192-4698358ab9da_ACTION_7e0221e7-b1b7-4918-9d8c-bb276c15ed81_Body_", "Stale Data Alert for http://config-virtualbox:8180/jUnitTestCasePCSasdasd. This is the default alert that indicates that either a server or agent is offline. This alert can be changed by changing the policy for this service.");
          params.put("sla_95cf1d13-104a-4d5c-9761-8a4fa4471098_ACTION_75a2d9ba-6b86-40d7-b283-8ca9f3beac21_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("adduserperm", "read");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_RULE_enc_value", "on");
          params.put("sla_95cf1d13-104a-4d5c-9761-8a4fa4471098_ACTION_75a2d9ba-6b86-40d7-b283-8ca9f3beac21_enc_Body_", "false");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_ConnectionURL", "");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_RULE_class", "org.miloss.fgsms.sla.rules.ActionContainsIgnoreCase");
          params.put("sla_753647dc-50a6-4a8d-9192-4698358ab9da_ACTION_7e0221e7-b1b7-4918-9d8c-bb276c15ed81_enc_Body_", "false");
          params.put("url", "http://config-virtualbox:8180/jUnitTestCasePCSasdasd");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_username", "");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_classname", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("sla_753647dc-50a6-4a8d-9192-4698358ab9da_ACTION_7e0221e7-b1b7-4918-9d8c-bb276c15ed81_Subject_", "Stale Data Alert for http://config-virtualbox:8180/jUnitTestCasePCSasdasd");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_enc_password", "on");
          params.put("nonce", "7df104de-12a3-47cc-8c0f-31bb2b884929");
          params.put("sla_95cf1d13-104a-4d5c-9761-8a4fa4471098_ACTION_75a2d9ba-6b86-40d7-b283-8ca9f3beac21_Subject_", "Change of status for http://config-virtualbox:8180/jUnitTestCasePCSasdasd");
          params.put("sla_f594f4bd-20f4-4345-88ab-be45114f68c7_ACTION_e825a85a-791b-4fc2-bf07-c8b289df6a6a_enc_username", "on");
          params.put("sla_95cf1d13-104a-4d5c-9761-8a4fa4471098_ACTION_75a2d9ba-6b86-40d7-b283-8ca9f3beac21_enc_Subject_", "false");
          params.put("plugin_ACTION_ConnectionURL", "");
          params.put("plugin_ACTION_password", "");
          params.put("sla_action_e825a85a-791b-4fc2-bf07-c8b289df6a6a_classname", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("plugin_ACTION_isTopicOverride", "");
          params.put("sla_753647dc-50a6-4a8d-9192-4698358ab9da_ACTION_7e0221e7-b1b7-4918-9d8c-bb276c15ed81_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");
          params.put("plugin_ACTION_username", "");

          HttpServletRequest request = new HttpServletRequestImpl(params);
          ServicePolicy pol = new TransactionalWebServicePolicy();

          ServicePolicy result = Helper.buildFromHttpPost(request, pol);
          JAXB.marshal(result, System.out);
//          assertNotNull(result.getURL());
          assertNotNull(result.getServiceLevelAggrements());
          assertNotNull(result.getServiceLevelAggrements().getSLA());
          assertEquals(3, result.getServiceLevelAggrements().getSLA().size());
          for (int i = 0; i < result.getServiceLevelAggrements().getSLA().size(); i++) {
               assertNotNull(result.getServiceLevelAggrements().getSLA().get(i).getGuid());
               assertNotNull(result.getServiceLevelAggrements().getSLA().get(i).getAction());
               assertTrue(result.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().size() > 0);
               assertNotNull(result.getServiceLevelAggrements().getSLA().get(i).getRule());
               SLARuleGeneric rule = (SLARuleGeneric) result.getServiceLevelAggrements().getSLA().get(i).getRule();
               assertNotNull(rule.getClassName());
               Class.forName(rule.getClassName());
               assertNotEquals(rule.getClassName(), "true");
               assertNotEquals(rule.getClassName(), "false");
               for (int k = 0; k < rule.getParameterNameValue().size(); k++) {
                    assertNotNull(rule.getParameterNameValue().get(k).getName());
                    //assertNotNull(rule.getParameterNameValue().get(k).getValue());

               }
               for (int k = 0; k < result.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().size(); k++) {
                    SLAAction sla = result.getServiceLevelAggrements().getSLA().get(i).getAction().getSLAAction().get(k);
                    assertNotNull(sla.getImplementingClassName());
                    assertNotEquals(sla.getImplementingClassName(), "true");
                    assertNotEquals(sla.getImplementingClassName(), "false");
                    Class.forName(sla.getImplementingClassName());
                    for (int j = 0; j < sla.getParameterNameValue().size(); j++) {
                         assertNotNull(sla.getParameterNameValue().get(k).getName());
                         //assertNotNull(rule.getParameterNameValue().get(k).getValue());

                    }

               }

          }
     }

     @Test
     public void anotheretsst() throws Exception {
          System.out.println("buildFromHttpPost");
          Map<String, String> params = new HashMap<String, String>();

          params.put("uid_7c116a7f-8fa4-48f4-9533-125e1373e99f_method", "xpath");
          params.put("uid_7c116a7f-8fa4-48f4-9533-125e1373e99f_xpath_55a2006e-a058-4de7-b923-fbeb89754651_prefix", "ns1");
          params.put("uid_7c116a7f-8fa4-48f4-9533-125e1373e99f_xpath_55a2006e-a058-4de7-b923-fbeb89754651_namespace", "www.google.com");
          
          params.put("uid_7c116a7f-8fa4-48f4-9533-125e1373e99f_xpathexpression", "/body/element12");

          params.put("uid_7c116a7f-8fa4-48f4-9533-125e1373e99f_x509certxpath", "on");
          
          params.put("uid_a42ea7bc-438c-4abf-b7f9-ea0df55b8a06_method", "httpcred");
          params.put("uid_77767250-02bf-4aea-91ab-9c87fb514797_method", "httpheader");
          params.put("uid_77767250-02bf-4aea-91ab-9c87fb514797_httpheadername", "header1");
          

          params.put("plugin_PUBLISH_Range", "");
          params.put("plugin_PUBLISH_Range", "");
          params.put("adduserperm", "read");
          params.put("parentobject", "");
          params.put("combo_SLA_ACTION", "org.miloss.fgsms.sla.actions.AMQPAlerter");
          params.put("plugin_ACTION_isTopicOverride", "");
          params.put("sla_ca93e47c-84fe-4d05-90a9-6aa446473a9a_ACTION_962b838e-1d3a-4052-98ec-5c94adcae838_enc_Subject_", "true");
          params.put("url", "http://alexs-mbp-2.home:8080/fgsmsServices/services/DCS");
          params.put("plugin_PUBLISH_Success", "");
          params.put("domainname", "unspecified");
          params.put("newIdent_httpheadername", "header1");
          params.put("sla_c91e6eb5-564b-40ba-9d1c-86f1b7ff83f3_ACTION_1631cc91-b6fe-4544-8e39-3c4244e156b2_Subject_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/DCS");
          params.put("plugin_RULE_value", "");
          params.put("sla_c91e6eb5-564b-40ba-9d1c-86f1b7ff83f3_ACTION_1631cc91-b6fe-4544-8e39-3c4244e156b2_enc_Subject_", "true");
          params.put("description", "");
          params.put("sla_c91e6eb5-564b-40ba-9d1c-86f1b7ff83f3_ACTION_1631cc91-b6fe-4544-8e39-3c4244e156b2_Body_", "Stale Data Alert for http://alexs-mbp-2.home:8080/fgsmsServices/services/DCS. This is the default alert that indicates that either a server or agent is offline. This alert can be changed by changing the policy for this service.");
          params.put("sla_ca93e47c-84fe-4d05-90a9-6aa446473a9a_RULE_class", "org.miloss.fgsms.sla.rules.ChangeInAvailabilityStatus");
          params.put("externalurl", "");
          params.put("sla_ca93e47c-84fe-4d05-90a9-6aa446473a9a_ACTION_962b838e-1d3a-4052-98ec-5c94adcae838_Subject_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/DCS");

          params.put("sla_ca93e47c-84fe-4d05-90a9-6aa446473a9a_ACTION_962b838e-1d3a-4052-98ec-5c94adcae838_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");

          params.put("servername", "alexs-mbp-2.home");
          params.put("nonce", "c09d8352-693e-4c41-a161-23ec90af5a6e");
          params.put("plugin_PUBLISH_SLA", "");
          params.put("plugin_ACTION_enc_password", "on");
          params.put("newIdent_xpathprefixes", "ns1##www.google.com");
          params.put("sla_c91e6eb5-564b-40ba-9d1c-86f1b7ff83f3_ACTION_1631cc91-b6fe-4544-8e39-3c4244e156b2_classname", "org.miloss.fgsms.sla.actions.EmailAlerter");

          params.put("locationlong", "");
          params.put("displayName", "");
          params.put("plugin_PUBLISH_Binding", "");
          params.put("bucket", "");
          params.put("plugin_PUBLISH_Maximums", "");
          params.put("newIdent_x509certxpath", "on");
          params.put("buellerenabled", "on");
          params.put("username", "username");
          params.put("sla_ca93e47c-84fe-4d05-90a9-6aa446473a9a_ACTION_962b838e-1d3a-4052-98ec-5c94adcae838_enc_Body_", "false");
          params.put("newIdent_xpathexpression", "/body/element12");
          params.put("sla_ca93e47c-84fe-4d05-90a9-6aa446473a9a_ACTION_962b838e-1d3a-4052-98ec-5c94adcae838_Body_", "Change of status for http://alexs-mbp-2.home:8080/fgsmsServices/services/DCS. This is the default status alert. This can be changed by changing the policy for this service.");
          params.put("plugin_ACTION_password", "");
          params.put("plugin_ACTION_ConnectionURL", "");
          params.put("sla_c91e6eb5-564b-40ba-9d1c-86f1b7ff83f3_RULE_class", "org.miloss.fgsms.sla.rules.StaleData");
          params.put("plugin_ACTION_username", "");
          params.put("plugin_PUBLISH_Averages", "");
          params.put("poc", "");

          params.put("save_policy", "true");
          params.put("plugin_ACTION_destinationOverride", "");
          params.put("sla_c91e6eb5-564b-40ba-9d1c-86f1b7ff83f3_ACTION_1631cc91-b6fe-4544-8e39-3c4244e156b2_enc_Body_", "true");
          params.put("locationlat", "");
          params.put("newIdent_useridtype", "httpheader");
          params.put("refreshrate", "3min ");
          params.put("plugin_PUBLISH_Uptime", "");
          params.put("plugin_PUBLISH_Faults", "");
          params.put("datattl", "30d ");
          params.put("plugin_PUBLISH_Status", "");
          params.put("messagecap", "1024");

          HttpServletRequest request = new HttpServletRequestImpl(params);
          ServicePolicy pol = new TransactionalWebServicePolicy();

          ServicePolicy result = Helper.buildFromHttpPost(request, pol);
          JAXB.marshal(result, System.out);
//          assertNotNull(result.getURL());
          TransactionalWebServicePolicy tp = (TransactionalWebServicePolicy) pol;
          Assert.assertNotNull(tp.getUserIdentification());
          Assert.assertNotNull(tp.getUserIdentification().getUserIdentity());
          Assert.assertTrue(tp.getUserIdentification().getUserIdentity().size() == 3);

          boolean http = false;
          boolean header = false;
          boolean xpath = false;
          for (int i = 0; i < tp.getUserIdentification().getUserIdentity().size(); i++) {
               if (tp.getUserIdentification().getUserIdentity().get(i).isSetHttpHeaderName()
                    && tp.getUserIdentification().getUserIdentity().get(i).isUseHttpHeader()) {
                    header = true;
                    assertNotNull(tp.getUserIdentification().getUserIdentity().get(i).getHttpHeaderName());

               }
               if (tp.getUserIdentification().getUserIdentity().get(i).isSetUseHttpCredential()
                    && tp.getUserIdentification().getUserIdentity().get(i).isUseHttpCredential()) {
                    http = true;
               }
               if (tp.getUserIdentification().getUserIdentity().get(i).isSetXPaths()
                    && tp.getUserIdentification().getUserIdentity().get(i).getXPaths() != null
                    && !tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().isEmpty()) {
                    xpath = true;
                    assertNotNull(tp.getUserIdentification().getUserIdentity().get(i).getXPaths().getXPathExpressionType().get(0).getXPath());

                    assertNotNull(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces());
                    assertNotNull(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies());
                    assertFalse(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().isEmpty());
                    for (int k = 0; k < tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().size(); k++) {
                         assertNotNull(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getNamespace());
                         assertNotNull(tp.getUserIdentification().getUserIdentity().get(i).getNamespaces().getXMLNamespacePrefixies().get(k).getPrefix());
                    }

               }

          }
          assertTrue(http);
          assertTrue(header);
          assertTrue(xpath);

     }

}
