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
package org.miloss.fgsms.services.pcs.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.miloss.fgsms.agentcore.ConfigurationException;
import org.miloss.fgsms.sla.actions.SLAActionRestart;
import org.miloss.fgsms.sla.rules.AllFaults;
import org.miloss.fgsms.sla.rules.BrokerQueueSizeGreaterThan;
import org.miloss.fgsms.sla.rules.HighCPUUsage;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author AO
 */
public class PCS4jBeanTest extends org.miloss.fgsms.test.WebServiceBaseTests {

    static String TestKey = "junit";
    static String TestName = "parameter";
    static String TestValue = "somevalue";

    public PCS4jBeanTest() throws Exception {

        super();
        url = "http://localhost:8180/jUnitTestCasePCS";
        Init();

    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
//  /            
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @After
    @Override
    public void tearDown() {
        super.tearDown();
        RemoveServicePolicy(url);
        RemoveServicePolicy(url + "asdasd");
        //ds.close();

    }

    /**
     * Test of getServicePolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetServicePolicyAdmin() throws Exception {
        //HttpServletRequest session = ((HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST));
        RemoveServicePolicy(url);
        System.out.println("testGetServicePolicyAdmin");

        ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURI(url);
        request.setPolicytype(PolicyType.TRANSACTIONAL);
        request.setDomain(Utility.getHostName());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            ServicePolicyResponseMsg response = instance.getServicePolicy(request);
            RemoveServicePolicy(url);
            assertNotNull(response);
            assertNotNull(response.getClassification());
            assertNotNull(response.getPolicy());
            assertNotNull(response.getPolicy().getURL());
            System.out.println(response.getPolicy().getClass().getCanonicalName());
            assertEquals(response.getPolicy().getPolicyType(), PolicyType.TRANSACTIONAL);

        } catch (Exception ex) {

            ex.printStackTrace();
            RemoveServicePolicy(url);
            fail("unexpected failure");

        }

        //     fail("should not be able to create a policy as administrator");
    }

    /**
     * Test of getServicePolicy method, of class PCS4jBean.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetServicePolicyAdminExpectingNullResponse() throws Exception {
        RemoveServicePolicy(url);
        //HttpServletRequest session = ((HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST));
        System.out.println("testGetServicePolicyAdminExpectingNullResponse");
        ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURI(url);
        //request.setPolicytype(PolicyType.TRANSACTIONAL);
        //request.setDomain(Utility.getHostName());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            ServicePolicyResponseMsg response = instance.getServicePolicy(request);
            RemoveServicePolicy(url);

        } catch (Exception ex) {

            RemoveServicePolicy(url);
            throw ex;

        }

        //     fail("should not be able to create a policy as administrator");
    }

    public static void RemoveServicePolicyold(String u) {
        Connection con = Utility.getConfigurationDBConnection();
        try {
            //remove service policy

            PreparedStatement com = con.prepareStatement(
                    "delete from servicepolicies where uri=?; "
                    + "delete from userpermissions where objecturi=?;");

            com.setString(1, u);
            com.setString(2, u);
            /*
             * com.setString(2, u); com.setString(3, u); com.setString(4, u);
             * com.setString(5, u); com.setString(6, u);
             *
             * com.setString(7, u);
             */
            com.execute();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception ex) {
            }
        }

        con = Utility.getPerformanceDBConnection();
        try {
            //remove service policy

            PreparedStatement com = con.prepareStatement("delete from rawdata where uri=?; "
                    + "delete from agg2 where uri=?;"
                    + "delete from actionlist where uri=?;"
                    + "delete from alternateurls where uri=?;"
                    + "delete from rawdatatally where uri=?;");
            com.setString(1, u);
            com.setString(2, u);
            com.setString(3, u);
            com.setString(4, u);
            com.setString(5, u);

            com.execute();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception ex) {
            }
        }
    }

    public void testGetServicePolicyAgent() throws Exception {
        //HttpServletRequest session = ((HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST));
        System.out.println("testGetServicePolicyAgent");
        RemoveServicePolicy(url);
        ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURI(url);
        ServicePolicyResponseMsg response = null;

        PCS4jBean instance = new PCS4jBean(agentctx);
        try {
            response = instance.getServicePolicy(request);
            RemoveServicePolicy(url);
            assertNotNull(response);
            assertNotNull(response.getClassification());
            assertNotNull(response.getPolicy());
            assertNotNull(response.getPolicy().getURL());
            assertEquals(response.getPolicy().getPolicyType(), PolicyType.TRANSACTIONAL);
        } catch (Exception ex) {
            RemoveServicePolicy(url);
            fail("unexpected error" + ex.getMessage());
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetServicePolicyNullURL() throws Exception {
        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        System.out.println("testSetServicePolicyNullURL");
        RemoveServicePolicy(url);
        request.setClassification(new SecurityWrapper());
        //request.setURL(url);
        request.setPolicy(new MachinePolicy());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            instance.setServicePolicy(request);
            RemoveServicePolicy(url);

        } catch (Exception ex) {
            RemoveServicePolicy(url);
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetServicePolicyNullURL2() throws Exception {
        System.out.println("testSetServicePolicyNullURL2");
        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        //request.setURL(url);
        request.setPolicy(new ProcessPolicy());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            instance.setServicePolicy(request);
            RemoveServicePolicy(url);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url);
            throw ex;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetServicePolicyNullURL3() throws Exception {
        System.out.println("testSetServicePolicyNullURL3");
        RemoveServicePolicy(url);
        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        //request.setURL(url);
        request.setPolicy(new StatusServicePolicy());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            instance.setServicePolicy(request);
            RemoveServicePolicy(url);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url);
            throw ex;
        }
    }

    @org.junit.Test
    public void testSetServicePolicyNullClass() throws Exception {
        System.out.println("testSetServicePolicyNullClass");
        RemoveServicePolicy(url);
        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        //   request.setClassification(new SecurityWrapper());
        request.setURL(url);
        request.setPolicy(new TransactionalWebServicePolicy());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            instance.setServicePolicy(request);
            RemoveServicePolicy(url);
            fail("unexpected success");
        } catch (Exception ex) {
            RemoveServicePolicy(url);
        }
    }

    @org.junit.Test
    public void testSetServicePolicyNullPolicy() throws Exception {
        System.out.println("testSetServicePolicyNullPolicy");
        RemoveServicePolicy(url);
        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        //   request.setPolicy(new ServicePolicy());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            instance.setServicePolicy(request);
            RemoveServicePolicy(url);
            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url);
        }
    }

    /**
     * Test of setServicePolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetServicePolicyValidRequest() throws Exception {
        System.out.println("testSetServicePolicyValidRequest");
        RemoveServicePolicy(url);
        ServicePolicyRequestMsg get = new ServicePolicyRequestMsg();
        get.setClassification(new SecurityWrapper());
        get.setURI(url);
        get.setMachine(Utility.getHostName());
        get.setDomain(org.miloss.fgsms.common.Constants.DomainUnspecified);
        get.setPolicytype(PolicyType.TRANSACTIONAL);
        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);

        // request.setPolicy(null);
        PCS4jBean instance = new PCS4jBean(adminctx);

        ServicePolicy pol = instance.getServicePolicy(get).getPolicy();
        assertNotNull(pol);

        FederationPolicyCollection fc = new FederationPolicyCollection();

        FederationPolicy fpol = new FederationPolicy();
        fpol.setImplementingClassName("org.miloss.fgsms.uddipub.UddiPublisher");
        fpol.getParameterNameValue().add(Utility.newNameValuePair("Binding", "someuddikey2", false, false));
        fpol.getParameterNameValue().add(Utility.newNameValuePair("Range", "300000", false, false));

        fc.getFederationPolicy().add(fpol);
        //  pol.getFederationPolicies().add(fpol);
        pol.setFederationPolicyCollection(fc);
        pol.setPolicyType(PolicyType.TRANSACTIONAL);
        request.setPolicy(pol);
        SetServicePolicyResponseMsg result = instance.setServicePolicy(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());

        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("select * from servicepolicies where uri=?");
        com.setString(1, url);
        ResultSet rs = com.executeQuery();
        boolean ok = false;
        if (rs.next()) {
            boolean b = rs.getBoolean("hasFederation");
            if (b == true) {
                ok = true;
            }

        }
        rs.close();
        com.close();
        con.close();
        RemoveServicePolicy(url);
        assertTrue(ok);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetServicePolicyInValidRequestMismatchedUrls() throws Exception {
        System.out.println("testSetServicePolicyInValidRequestMismatchedUrls");
        RemoveServicePolicy(url);
        RemoveServicePolicy(url + "asdasd");
        ServicePolicyRequestMsg get = new ServicePolicyRequestMsg();
        get.setClassification(new SecurityWrapper());
        get.setURI(url + "asdasd");
        get.setPolicytype(PolicyType.TRANSACTIONAL);

        PCS4jBean instance = new PCS4jBean(adminctx);
        ServicePolicy pol = instance.getServicePolicy(get).getPolicy();
        assertNotNull(pol);

        boolean ok = false;
        pol.setPolicyType(PolicyType.TRANSACTIONAL);

        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);

        request.setPolicy(pol);

        SetServicePolicyResponseMsg result = instance.setServicePolicy(request);

    }
    public static ObjectFactory fac = new ObjectFactory();

    /**
     * Test of getUDDIDataPublishServicePolicies method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetUDDIDataPublishServicePoliciesValidMessage() throws Exception {
        RemoveServicePolicy(url);

        FederationPolicyCollection fpc = new FederationPolicyCollection();

        FederationPolicy fpol = new FederationPolicy();
        fpol.setImplementingClassName("org.miloss.fgsms.uddipub.UddiPublisher");
        fpol.getParameterNameValue().add(Utility.newNameValuePair("Binding", "someuddikey2", false, false));
        fpol.getParameterNameValue().add(Utility.newNameValuePair("Averages", "true", false, false));
        fpol.getParameterNameValue().add(Utility.newNameValuePair("Range", Integer.toString(5 * 60 * 1000), false, false));
        fpc.getFederationPolicy().add(fpol);
        CreatePolicy(url, fpc);
        System.out.println("testGetUDDIDataPublishServicePoliciesValidMessage");
        GetUDDIDataPublishServicePoliciesRequestMsg request = new GetUDDIDataPublishServicePoliciesRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(agentctx);
        GetUDDIDataPublishServicePoliciesResponseMsg result = instance.getUDDIDataPublishServicePolicies(request);
        RemoveServicePolicy(url);
        assertNotNull(result);
        assertNotNull(result.getPolicies());
        assertNotNull(result.getPolicies());
        assertFalse(result.getPolicies().getServicePolicy().isEmpty());
        boolean ok = false;
        for (int i = 0; i < result.getPolicies().getServicePolicy().size(); i++) {
            if (result.getPolicies().getServicePolicy().get(i).getURL().equals(url)) {
                ok = true;
            }
        }
        assertTrue(ok);
        //we should get some kind of response
        //set policy of a given service with uddi parameters set
        //try again, expecting the new service to be listed
        //delete policy
    }

    /**
     * Test of setGlobalPolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetGlobalPolicyValid() throws Exception {
        System.out.println("testSetGlobalPolicyValid");

        PCS4jBean instance = new PCS4jBean(adminctx);
        DatatypeFactory fac = DatatypeFactory.newInstance();

        //get current info
        GetGlobalPolicyRequestMsg greq = new GetGlobalPolicyRequestMsg();
        greq.setClassification(new SecurityWrapper());
        GetGlobalPolicyResponseMsg globalPolicy = instance.getGlobalPolicy(greq);

        //copy it
        GlobalPolicy policy = new GlobalPolicy();
        policy.setPolicyRefreshRate(globalPolicy.getPolicy().getPolicyRefreshRate());
        //     policy.setUDDIPublishRate(globalPolicy.getPolicy().getUDDIPublishRate());
        policy.setRecordedMessageCap(globalPolicy.getPolicy().getRecordedMessageCap());
        policy.setAgentsEnabled(globalPolicy.getPolicy().isAgentsEnabled());
        policy.setClassification(globalPolicy.getClassification());

        //valid it
        assertNotNull(policy);
        assertNotNull(policy.getPolicyRefreshRate());
//no longer necessary        assertNotNull(policy.getUDDIPublishRate());

        //change it
        policy.getPolicyRefreshRate().add(fac.newDuration(1000));
        //    policy.getUDDIPublishRate().add(fac.newDuration(1000));
        policy.setAgentsEnabled(!policy.isAgentsEnabled());
        policy.setRecordedMessageCap(policy.getRecordedMessageCap() - 100);

        //set it
        SetGlobalPolicyRequestMsg request = null;
        request = new SetGlobalPolicyRequestMsg();
        request.setClassification(globalPolicy.getClassification());
        request.setPolicy(policy);
        SetGlobalPolicyResponseMsg result = instance.setGlobalPolicy(request);
        assertNotNull(result.getClassification());

        GetGlobalPolicyRequestMsg greqnew = new GetGlobalPolicyRequestMsg();
        greq.setClassification(new SecurityWrapper());
        GetGlobalPolicyResponseMsg globalPolicynew = instance.getGlobalPolicy(greq);
        //get it again to valid that the change was made

        //set it back to the original values
        request = null;
        request = new SetGlobalPolicyRequestMsg();
        request.setClassification(globalPolicy.getClassification());
        request.setPolicy(globalPolicy.getPolicy());
        result = instance.setGlobalPolicy(request);

        //now valid that globalPolicynew and policy are equal
        assertEquals(globalPolicynew.getPolicy().getPolicyRefreshRate(), policy.getPolicyRefreshRate());
//        assertEquals(globalPolicynew.getPolicy().getUDDIPublishRate(), policy.getUDDIPublishRate());
        assertEquals(globalPolicynew.getPolicy().getRecordedMessageCap(), policy.getRecordedMessageCap());

        //validate that only one record exists in the database
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement prepareStatement = con.prepareStatement("select count(*) from globalpolicies");
        ResultSet rs = prepareStatement.executeQuery();
        long items = 0;
        if (rs.next()) {
            items = rs.getLong(1);
        }
        rs.close();
        prepareStatement.close();
        con.close();
        if (items > 1) {
            fail("multple global policies detected");
        }
    }

    /**
     * Test of getGlobalPolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetGlobalPolicyNullMsg() throws Exception {
        System.out.println("testGetGlobalPolicyNullMsg");
        GetGlobalPolicyRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetGlobalPolicyResponseMsg result = instance.getGlobalPolicy(request);
            fail("null msg not allowed");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Test of getGlobalPolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetGlobalPolicyNullClassMsg() throws Exception {
        System.out.println("testGetGlobalPolicyNullMsg");
        GetGlobalPolicyRequestMsg request = new GetGlobalPolicyRequestMsg();

        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetGlobalPolicyResponseMsg result = instance.getGlobalPolicy(request);
        } catch (Exception ex) {
            fail("null msg should be allowed but failed " + ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetGlobalPolicy() throws Exception {
        System.out.println("getGlobalPolicy");
        GetGlobalPolicyRequestMsg request = new GetGlobalPolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(adminctx);

        GetGlobalPolicyResponseMsg result = instance.getGlobalPolicy(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertNotNull(result.getClassification().getClassification());
        assertNotNull(result.getPolicy());

    }

    /**
     * Test of deleteServicePolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testDeleteServiceNullMsgPolicy() throws Exception {
        System.out.println("testDeleteServiceNullMsgPolicy");
        DeleteServicePolicyRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        DeleteServicePolicyResponseMsg expResult = null;
        try {
            DeleteServicePolicyResponseMsg result = instance.deleteServicePolicy(request);
            fail("null msg is not allowed");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    @org.junit.Test
    public void testDeleteServicePolicyNullURL() throws Exception {
        System.out.println("testDeleteServicePolicyNullURL");
        DeleteServicePolicyRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        DeleteServicePolicyResponseMsg expResult = null;
        request = new DeleteServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            DeleteServicePolicyResponseMsg result = instance.deleteServicePolicy(request);
            fail("null url is not allowed");
        } catch (Exception ex) {
        }
    }

    @org.junit.Test
    public void testDeleteServiceAsAdminValidRequest() throws Exception {
        System.out.println("testDeleteServiceAsAdminValidRequest");
        CreatePolicy(url);
        DeleteServicePolicyRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        DeleteServicePolicyResponseMsg expResult = null;
        request = new DeleteServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        try {
            DeleteServicePolicyResponseMsg result = instance.deleteServicePolicy(request);
            if (!result.isSuccess()) {
                fail("delete failed");
            }
        } catch (Exception ex) {
            fail("delete failed");
        }
        VerifyPolicyDeleted(url);
    }

    @org.junit.Test
    public void testDeleteServiceAsAdmin() throws Exception {
        System.out.println("testDeleteServiceAsAdmin");
        DeleteServicePolicyRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        DeleteServicePolicyResponseMsg expResult = null;
        request = new DeleteServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            DeleteServicePolicyResponseMsg result = instance.deleteServicePolicy(request);
            fail("null class is not allowed");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testDeleteServiceAsAgent() throws Exception {
        System.out.println("testDeleteServiceAsAgent");
        CreatePolicy(url);
        DeleteServicePolicyRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        DeleteServicePolicyResponseMsg expResult = null;
        request = new DeleteServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            DeleteServicePolicyResponseMsg result = instance.deleteServicePolicy(request);
            fail("shouldn't be able to delete a service as an agent role");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        RemoveServicePolicy(url);
    }

    /**
     * Test of setServicePermissions method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetServicePermissionsNullMessage() throws Exception {
        System.out.println("testSetServicePermissionsNullMessage");
        SetServicePermissionsRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetServicePermissionsResponseMsg expResult = null;
        try {
            SetServicePermissionsResponseMsg result = instance.setServicePermissions(request);
            RemoveServicePolicy(url);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url);
        }

    }

    /**
     * Test of setServicePermissions method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetServicePermissionsNullClassNullUrl() throws Exception {
        System.out.println("testSetServicePermissionsNullClassNullUrl");
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();

        PCS4jBean instance = new PCS4jBean(adminctx);
        SetServicePermissionsResponseMsg expResult = null;
        try {
            SetServicePermissionsResponseMsg result = instance.setServicePermissions(request);
            RemoveServicePolicy(url);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url);
        }

    }

    /**
     * Test of setServicePermissions method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetServicePermissionsNullClass() throws Exception {
        System.out.println("testSetServicePermissionsNullClass");
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();
        //request.setClassification(new SecurityWrapper());
        request.setURL(url);
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetServicePermissionsResponseMsg expResult = null;
        try {
            SetServicePermissionsResponseMsg result = instance.setServicePermissions(request);
            RemoveServicePolicy(url);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url);
        }

    }

    @org.junit.Test
    public void testSetServicePermissionsNullPermissions() throws Exception {

        System.out.println("testSetServicePermissionsNullPermissions");
        CreatePolicy(url);
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            SetServicePermissionsResponseMsg result = instance.setServicePermissions(request);
            RemoveServicePolicy(url);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        RemoveServicePolicy(url);
    }

    @org.junit.Test
    public void testSetServicePermissionsValidRequestNoPermissions() throws Exception {
        RemoveServicePolicy(url);
        CreatePolicy(url);
        System.out.println("testSetServicePermissionsValidRequestNoPermissions");
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        PCS4jBean instance = new PCS4jBean(adminctx);
        request.setRights(new ArrayOfUserPermissionType());

        try {
            SetServicePermissionsResponseMsg result = instance.setServicePermissions(request);
            //   RemoveServicePolicy(url);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            GetServicePermissionsRequestMsg p = new GetServicePermissionsRequestMsg();
            p.setClassification(new SecurityWrapper());
            p.setURL((url));
            GetServicePermissionsResponseMsg servicePermissions = instance.getServicePermissions(p);
            RemoveServicePolicy(url);
            assertNotNull(servicePermissions);
            assertNotNull(servicePermissions.getClassification());
            assertNotNull(servicePermissions.getRights());
            assertTrue(servicePermissions.getRights().getUserServicePermissionType().isEmpty());
        } catch (Exception ex) {
            RemoveServicePolicy(url);
            ex.printStackTrace();
            fail("error caught" + ex.getMessage());
        }

    }

    @org.junit.Test
    public void testSetServicePermissionsValidRequestNoPermissionsForNotExistingService() throws Exception {
        RemoveServicePolicy(url + "asdasdasd");
        System.out.println("testSetServicePermissionsValidRequestNoPermissionsForNotExistingService");
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url + "asdasdasd");
        PCS4jBean instance = new PCS4jBean(adminctx);
        request.setRights(new ArrayOfUserPermissionType());

        try {
            SetServicePermissionsResponseMsg result = instance.setServicePermissions(request);
            RemoveServicePolicy(url + "asdasdasd");
            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url + "asdasdasd");
        }

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetServicePermissionsUnregisteredService() throws Exception {
        System.out.println("testSetServicePermissionsUnregisteredService");
        RemoveServicePolicy(url);
        //  CreatePolicy(url);
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        PCS4jBean instance = new PCS4jBean(adminctx);
        request.setRights(new ArrayOfUserPermissionType());
        UserPermissionType p = new UserPermissionType();
        p.setUser("bob");
        p.setRight(RightEnum.AUDIT);
        request.getRights().getUserPermissionType().add(p);

        try {
            SetServicePermissionsResponseMsg result = instance.setServicePermissions(request);
            RemoveServicePolicy(url);

        } catch (Exception ex) {
            RemoveServicePolicy(url);

            throw ex;

        }
    }

    @org.junit.Test
    public void testSetServicePermissionsValidRequestSomePermissions() throws Exception {
        System.out.println("testSetServicePermissionsValidRequestSomePermissions");
        RemoveServicePolicy(url);
        CreatePolicy(url);
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        PCS4jBean instance = new PCS4jBean(adminctx);
        request.setRights(new ArrayOfUserPermissionType());
        UserPermissionType p = new UserPermissionType();
        p.setUser("bob");
        p.setRight(RightEnum.AUDIT);
        request.getRights().getUserPermissionType().add(p);

        try {
            SetServicePermissionsResponseMsg result = instance.setServicePermissions(request);
            RemoveServicePolicy(url);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url);
            ex.printStackTrace();
            fail("unexpected failure" + ex.getMessage());
        }
    }

    /**
     * Test of getServicePermissions method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetServicePermissionsNullMessage() throws Exception {
        System.out.println("testGetServicePermissionsNullMessage");
        GetServicePermissionsRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetServicePermissionsResponseMsg result = instance.getServicePermissions(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetServicePermissionsNullURL() throws Exception {
        System.out.println("testGetServicePermissionsNullURL");
        GetServicePermissionsRequestMsg request = new GetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetServicePermissionsResponseMsg result = instance.getServicePermissions(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetServicePermissionsNullClass() throws Exception {
        System.out.println("testGetServicePermissionsNullClass");
        RemoveServicePolicy(url);
        GetServicePermissionsRequestMsg request = new GetServicePermissionsRequestMsg();
        //   request.setClassification(new SecurityWrapper());
        //     request.setUser(fac.createGetServicePermissionsRequestMsgURL(url));
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetServicePermissionsResponseMsg result = instance.getServicePermissions(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetServicePermissionsNotExistingService() throws Exception {
        System.out.println("testGetServicePermissionsNotExistingService");
        RemoveServicePolicy(url);
        GetServicePermissionsRequestMsg request = new GetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL((url));
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetServicePermissionsResponseMsg result = instance.getServicePermissions(request);
            RemoveServicePolicy(url);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        RemoveServicePolicy(url);
    }

    @org.junit.Test
    public void testGetServicePermissionsNotExistingUser() throws Exception {
        System.out.println("testGetServicePermissionsNotExistingUser");
        RemoveServicePolicy(url);
        CreatePolicy(url);
        GetServicePermissionsRequestMsg request = new GetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setUser(("bob"));
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetServicePermissionsResponseMsg result = instance.getServicePermissions(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getRights());
        } catch (Exception ex) {
            fail("error");
        } finally {
            RemoveServicePolicy(url);
        }
    }

    /**
     * Test of getAdministrators method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetAdministratorsNullMsg() throws Exception {
        System.out.println("testGetAdministratorsNullMsg");
        GetAdministratorsRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetAdministratorsResponseMsg result = instance.getAdministrators(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetAdministratorsNullClass() throws Exception {
        System.out.println("testGetAdministratorsNullClass");
        GetAdministratorsRequestMsg request = new GetAdministratorsRequestMsg();
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetAdministratorsResponseMsg result = instance.getAdministrators(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetAdministratorsValidMessageAsAdmin() throws Exception {
        System.out.println("testGetAdministratorsValidMessage");
        GetAdministratorsRequestMsg request = new GetAdministratorsRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetAdministratorsResponseMsg result = instance.getAdministrators(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getUserList());
            assertNotNull(result.getUserList().getUserInfo());
            assertFalse(result.getUserList().getUserInfo().isEmpty());
            assertTrue(UserListContains(result.getUserList().getUserInfo(), "fgsmsadmin"));

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure.");
        }
    }

    static boolean UserListContains(List<UserInfo> list, String value) {
        if (list == null) {
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUsername() != null && list.get(i).getUsername().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test of setAdministrator method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetAdministratorNullMsg() throws Exception {
        System.out.println("setAdministrator");
        SetAdministratorRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            SetAdministratorResponseMsg result = instance.setAdministrator(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testSetAdministratorNullClass() throws Exception {
        System.out.println("setAdministrator");
        SetAdministratorRequestMsg request = new SetAdministratorRequestMsg();
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            SetAdministratorResponseMsg result = instance.setAdministrator(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testSetAdministratorInvalidTooManyEmailAddress() throws Exception {
        System.out.println("setAdministrator");
        removeuser(bobusername);
        SetAdministratorRequestMsg request = new SetAdministratorRequestMsg();
        request.setClassification(new SecurityWrapper());
        ArrayOfUserInfo users = new ArrayOfUserInfo();
        UserInfo e = new UserInfo();
        e.setDisplayName((("bob")));
        e.setUsername(bobusername);
        e.getEmail().add(bobusername + "0@localhost.localdomain");
        e.getEmail().add(bobusername + "1@localhost.localdomain");
        e.getEmail().add(bobusername + "2@localhost.localdomain");
        e.getEmail().add(bobusername + "3@localhost.localdomain");
        e.getEmail().add(bobusername + "4@localhost.localdomain");
        users.getUserInfo().add(e);
        request.setUserList((users));//createArrayOfUserInfo(users));
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            SetAdministratorResponseMsg result = instance.setAdministrator(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        removeuser(bobusername);
    }

    @org.junit.Test
    public void testSetAdministratorValidAsBoB() throws Exception {
        System.out.println("setAdministrator");
        removeuser(bobusername);
        SetAdministratorRequestMsg request = new SetAdministratorRequestMsg();
        request.setClassification(new SecurityWrapper());
        ArrayOfUserInfo users = new ArrayOfUserInfo();
        UserInfo e = new UserInfo();
        e.setDisplayName(("bob"));
        e.setUsername(bobusername);
        e.getEmail().add(bobusername + "@localhost.localdomain");
        users.getUserInfo().add(e);
        request.setUserList((users));//createArrayOfUserInfo(users));
        PCS4jBean instance = new PCS4jBean(userbobctx);
        try {
            SetAdministratorResponseMsg result = instance.setAdministrator(request);
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        removeuser(bobusername);
    }

    @org.junit.Test
    public void testSetAdministratorValid() throws Exception {
        System.out.println("setAdministrator");
        removeuser(bobusername);
        //get the list
        GetAdministratorsRequestMsg request = new GetAdministratorsRequestMsg();
        request.setClassification(new SecurityWrapper());
        GetAdministratorsResponseMsg result = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            result = instance.getAdministrators(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getUserList());
            assertNotNull(result.getUserList().getUserInfo());
            assertFalse(result.getUserList().getUserInfo().isEmpty());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure.");
        }

        //add bob
        //set the list
        SetAdministratorRequestMsg request2 = new SetAdministratorRequestMsg();
        request2.setClassification(new SecurityWrapper());
        ArrayOfUserInfo users = result.getUserList();
        UserInfo e = new UserInfo();
        e.setDisplayName(("bob"));
        e.setUsername(bobusername);
        e.getEmail().add(bobusername + "@localhost.localdomain");
        users.getUserInfo().add(e);
        request2.setUserList((users));//createArrayOfUserInfo(users));
        //   PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            SetAdministratorResponseMsg result2 = instance.setAdministrator(request2);
            assertNotNull(result2);
            assertNotNull(result2.getClassification());
//            fail("unexpected success.");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure.");
        }

        //verify that bob was added
        request = new GetAdministratorsRequestMsg();
        request.setClassification(new SecurityWrapper());
        result = null;

        try {
            result = instance.getAdministrators(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getUserList());
            assertNotNull(result.getUserList().getUserInfo());
            assertFalse(result.getUserList().getUserInfo().isEmpty());
            boolean ok = false;
            for (int i = 0; i < result.getUserList().getUserInfo().size(); i++) {
                if (result.getUserList().getUserInfo().get(i).getUsername().equals(bobusername)) {
                    result.getUserList().getUserInfo().remove(i);
                    ok = true;
                }
            }
            assertTrue(ok);
            //try to remove it
            request2 = new SetAdministratorRequestMsg();
            request2.setClassification(new SecurityWrapper());
            users = result.getUserList();
            request2.setUserList((users));//createArrayOfUserInfo(users));
            instance.setAdministrator(request2);

            //fetch the list again and validate that its gone
            result = instance.getAdministrators(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getUserList());
            assertNotNull(result.getUserList().getUserInfo());
            assertFalse(result.getUserList().getUserInfo().isEmpty());
            ok = true;
            for (int i = 0; i < result.getUserList().getUserInfo().size(); i++) {
                if (result.getUserList().getUserInfo().get(i).getUsername().equals(bobusername)) {
                    // result.getUserList().getValue().getUserInfo().remove(i);
                    ok = false;
                }
            }
            assertTrue(ok);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure.");
        }
        removeuser(bobusername);
    }

    /**
     * Test of elevateSecurityLevel method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testElevateSecurityLevelNullmsg() throws Exception {
        System.out.println("testElevateSecurityLevelNullmsg");
        ElevateSecurityLevelRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            ElevateSecurityLevelResponseMsg result = instance.elevateSecurityLevel(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        resetClassLevel();
    }

    @org.junit.Test
    public void testElevateSecurityLevelValidAsUser() throws Exception {
        System.out.println("testElevateSecurityLevelValidAsUser");
        ElevateSecurityLevelRequestMsg request = new ElevateSecurityLevelRequestMsg();
        request.setClassification(new SecurityWrapper(ClassificationType.S, "TEST"));
        PCS4jBean instance = new PCS4jBean(userbobctx);
        try {
            ElevateSecurityLevelResponseMsg result = instance.elevateSecurityLevel(request);
            resetClassLevel();
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            resetClassLevel();
        }
    }

    @org.junit.Test
    public void testElevateSecurityLevelValidAsAdmin() throws Exception {
        System.out.println("testElevateSecurityLevelValidAsUser");
        ElevateSecurityLevelRequestMsg request = new ElevateSecurityLevelRequestMsg();
        request.setClassification(new SecurityWrapper(ClassificationType.S, "TEST"));
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            ElevateSecurityLevelResponseMsg result = instance.elevateSecurityLevel(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertEquals(request.getClassification().getCaveats(), result.getClassification().getCaveats());
            assertEquals(request.getClassification().getClassification(), result.getClassification().getClassification());
            resetClassLevel();
        } catch (Exception ex) {
            ex.printStackTrace();
            resetClassLevel();
            fail("unexpected failure.");
        }
    }

    @org.junit.Test
    public void testElevateSecurityLevelHighToLowAsAdmin() throws Exception {
        System.out.println("testElevateSecurityLevelValidAsUser");
        ElevateSecurityLevelRequestMsg request = new ElevateSecurityLevelRequestMsg();
        request.setClassification(new SecurityWrapper(ClassificationType.S, "TEST"));
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            ElevateSecurityLevelResponseMsg result = instance.elevateSecurityLevel(request);
            request = new ElevateSecurityLevelRequestMsg();
            request.setClassification(new SecurityWrapper(ClassificationType.U, "TEST"));
            result = instance.elevateSecurityLevel(request);
            resetClassLevel();
            fail("unexpected success.");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            resetClassLevel();
        }
    }

    /**
     * Test of getMyEmailAddress method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetMyEmailAddressNullMsg() throws Exception {
        System.out.println("testGetMyEmailAddressNullMsg");
        GetMyEmailAddressRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(userbobctx);
        try {
            GetMyEmailAddressResponseMsg result = instance.getMyEmailAddress(request);

            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetMyEmailAddressNullClass() throws Exception {
        System.out.println("testGetMyEmailAddressNullClass");
        GetMyEmailAddressRequestMsg request = new GetMyEmailAddressRequestMsg();
        PCS4jBean instance = new PCS4jBean(userbobctx);
        try {
            GetMyEmailAddressResponseMsg result = instance.getMyEmailAddress(request);

            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetMyEmailAddressUserNotDefinedValid() throws Exception {
        System.out.println("testGetMyEmailAddressUserNotDefinedValid");
        removeuser(bobusername);

        GetMyEmailAddressRequestMsg request = new GetMyEmailAddressRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(userbobctx);
        try {
            GetMyEmailAddressResponseMsg result = instance.getMyEmailAddress(request);
            assertNotNull(result.getClassification());
            if ((result.getEmail().isEmpty())) {
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            fail("unexpected fault");
        }
    }

    public void removeuser(String u) throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement ps = con.prepareStatement("delete from userpermissions where username=?; delete from slasub where username=?; delete from users where username=?;");
        ps.setString(1, bobusername);
        ps.setString(2, bobusername);
        ps.setString(3, bobusername);
        ps.execute();
        ps.close();
        con.close();
    }

    @org.junit.Test
    public void testGetMyEmailAddressUserIsDefinedValid() throws Exception {
        System.out.println("testGetMyEmailAddressUserNotDefinedValid");
        removeuser(bobusername);
        adduser(bobusername);

        GetMyEmailAddressRequestMsg request = new GetMyEmailAddressRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(userbobctx);
        try {
            GetMyEmailAddressResponseMsg result = instance.getMyEmailAddress(request);
            assertNotNull(result.getClassification());
            //   if (Utility.stringIsNullOrEmpty(url))
            assertNotNull(result.getEmail());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    /**
     * Test of setMyEmailAddress method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetMyEmailAddressNullmessage() throws Exception {
        System.out.println("testSetMyEmailAddressNullmessage");
        SetMyEmailAddressRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(userbobctx);
        try {
            SetMyEmailAddressResponseMsg result = instance.setMyEmailAddress(request);
            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetMyEmailAddressNullClass() throws Exception {
        System.out.println("testSetMyEmailAddressNullClass");
        SetMyEmailAddressRequestMsg request = new SetMyEmailAddressRequestMsg();

        PCS4jBean instance = new PCS4jBean(userbobctx);

        SetMyEmailAddressResponseMsg result = instance.setMyEmailAddress(request);

    }

    @org.junit.Test
    public void testSetMyEmailAddressNullEmail() throws Exception {
        System.out.println("testSetMyEmailAddressNullEmail");
        SetMyEmailAddressRequestMsg request = new SetMyEmailAddressRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(userbobctx);
        try {
            SetMyEmailAddressResponseMsg result = instance.setMyEmailAddress(request);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testSetMyEmailAddressValid() throws Exception {
        System.out.println("testSetMyEmailAddressValid");
        SetMyEmailAddressRequestMsg request = new SetMyEmailAddressRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.getEmail().add("bob@bob.com");
        PCS4jBean instance = new PCS4jBean(userbobctx);

        SetMyEmailAddressResponseMsg result = instance.setMyEmailAddress(request);
        assertNotNull(result.getClassification());

    }

    /**
     * Test of setAlertRegistrations method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetAlertRegistrationsNullMsg() throws Exception {
        System.out.println("testSetAlertRegistrationsNullMsg");
        SetAlertRegistrationsRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetAlertRegistrationsResponseMsg expResult = null;
        try {
            SetAlertRegistrationsResponseMsg result = instance.setAlertRegistrations(request);
            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetAlertRegistrationsNullClassMsg() throws Exception {
        System.out.println("testSetAlertRegistrationsNullClassMsg");
        SetAlertRegistrationsRequestMsg request = new SetAlertRegistrationsRequestMsg();
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetAlertRegistrationsResponseMsg expResult = null;
        try {
            SetAlertRegistrationsResponseMsg result = instance.setAlertRegistrations(request);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    /**
     * Test of getAlertRegistrations method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetAlertRegistrationsValidMsg() throws Exception {
        System.out.println("testGetAlertRegistrationsValidMsg");
        GetAlertRegistrationsRequestMsg request = new GetAlertRegistrationsRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(adminctx);

        GetAlertRegistrationsResponseMsg result = instance.getAlertRegistrations(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertNotNull(result.getItems());
        String id = UUID.randomUUID().toString();
        //good, make a policyl set it, check it again, should be present.
        SetServicePolicyRequestMsg r = new SetServicePolicyRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setURL(url);
        r.setPolicy(new TransactionalWebServicePolicy());
        r.getPolicy().setURL(url);
        ArrayOfSLA asla = new ArrayOfSLA();
        SLA sla = new SLA();
        SLARuleGeneric rule = new SLARuleGeneric();
        rule.setClassName(AllFaults.class.getCanonicalName());

        sla.setRule(rule);
        ArrayOfSLAActionBaseType actions = new ArrayOfSLAActionBaseType();
        actions.getSLAAction().add(Utility.newEmailAction("fgsms@asdasd.com", "hello", "fgsms@asdasd.com"));

        sla.setAction(actions);
        asla.getSLA().add(sla);
        sla.setGuid(id);
        r.getPolicy().setPolicyType(PolicyType.TRANSACTIONAL);
        r.getPolicy().setServiceLevelAggrements(asla);
        SetServicePolicyResponseMsg setServicePolicy = instance.setServicePolicy(r);
        assertNotNull(setServicePolicy);
        assertNotNull(setServicePolicy.getClassification());

        GetAvailableAlertRegistrationsRequestMsg ra = new GetAvailableAlertRegistrationsRequestMsg();
        ra.setClassification(new SecurityWrapper());
        GetAvailableAlertRegistrationsResponseMsg availableAlertRegistrations = instance.getAvailableAlertRegistrations(ra);
        assertNotNull(availableAlertRegistrations);
        assertNotNull(availableAlertRegistrations.getClassification());
        assertNotNull(availableAlertRegistrations.getServicePolicy());
        assertFalse(availableAlertRegistrations.getServicePolicy().isEmpty());
        boolean ok = false;
        for (int i = 0; i < availableAlertRegistrations.getServicePolicy().size(); i++) {
            if (availableAlertRegistrations.getServicePolicy().get(i).getURL().equals(url)) {
                ok = true;
            }
        }
        assertTrue(ok);
        //succesfully available as a registerable alert

        //register for it
        SetAlertRegistrationsRequestMsg sa2 = null;
        try {
            instance.setAlertRegistrations(sa2);
            fail("unexpected success null msg");
        } catch (Exception ex) {
        }
        sa2 = new SetAlertRegistrationsRequestMsg();
        try {
            instance.setAlertRegistrations(sa2);
            fail("unexpected success null class");
        } catch (Exception ex) {
        }
        sa2.setClassification(new SecurityWrapper());
        SLAregistration slar = new SLAregistration();
        slar.setSLAID(id);
        slar.setServiceUri(url);
        sa2.getItems().add(slar);
        SetAlertRegistrationsResponseMsg setAlertRegistrations = instance.setAlertRegistrations(sa2);
        assertNotNull(setAlertRegistrations);
        assertNotNull(setAlertRegistrations.getClassification());

        //validate that it's done
        result = instance.getAlertRegistrations(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertNotNull(result.getItems());
        assertFalse(result.getItems().isEmpty());
        ok = false;
        for (int i = 0; i < result.getItems().size(); i++) {
            if (result.getItems().get(i).getServiceUri().equals(url)) {
                ok = true;
            }
        }
        assertTrue(ok);

        DeleteServicePolicyRequestMsg rr = new DeleteServicePolicyRequestMsg();
        rr.setClassification(new SecurityWrapper());
        rr.setDeletePerformanceData(true);
        rr.setURL(url);
        instance.deleteServicePolicy(rr);
        VerifyPolicyDeleted(url);
        //remove policy, check it again, should NOT be present
        result = instance.getAlertRegistrations(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertNotNull(result.getItems());
        ok = false;
        for (int i = 0; i < result.getItems().size(); i++) {
            if (result.getItems().get(i).getServiceUri().equals(url)) {
                ok = true;
            }
        }
        assertFalse(ok);
        RemoveServicePolicy(url);
    }

    @org.junit.Test
    public void testGetAlertRegistrationsNullMsg() throws Exception {
        System.out.println("testGetAlertRegistrationsNullMsg");
        GetAlertRegistrationsRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetAlertRegistrationsResponseMsg result = instance.getAlertRegistrations(request);
            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetAlertRegistrationsNullClass() throws Exception {
        System.out.println("testGetAlertRegistrationsNullClass");
        GetAlertRegistrationsRequestMsg request = new GetAlertRegistrationsRequestMsg();
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetAlertRegistrationsResponseMsg result = instance.getAlertRegistrations(request);
            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Test of getAvailableAlertRegistrations method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetAvailableAlertRegistrationsNullMsg() throws Exception {
        System.out.println("testGetAvailableAlertRegistrationsNullMsg");

        GetAvailableAlertRegistrationsRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            instance.getAvailableAlertRegistrations(request);
            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Test of getAgentPrinicples method, of class PCS4jBean.
     */
    @org.junit.Test(expected = SecurityException.class)
    public void testGetAgentPrinicplesAsAgent() throws Exception {
        System.out.println("testGetAgentPrinicplesAsAgent");
        GetAgentPrinicplesRequestMsg request = new GetAgentPrinicplesRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(agentctx);

        GetAgentPrinicplesResponseMsg result = instance.getAgentPrinicples(request);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testGetAgentPrinicplesNullClass() throws Exception {
        System.out.println("testGetAgentPrinicplesNullMsg");
        GetAgentPrinicplesRequestMsg request = new GetAgentPrinicplesRequestMsg();
        //request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(adminctx);

        GetAgentPrinicplesResponseMsg result = instance.getAgentPrinicples(request);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testGetAgentPrinicplesNullMsg() throws Exception {
        System.out.println("testGetAgentPrinicplesNullMsg");
        GetAgentPrinicplesRequestMsg request = null;// new GetAgentPrinicplesRequestMsg();
        //request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(adminctx);

        GetAgentPrinicplesResponseMsg result = instance.getAgentPrinicples(request);
    }

    /**
     * Test of setAgentPrinicples method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetAgentPrinicplesAsAdmin() throws Exception {
        System.out.println("testGetAgentPrinicplesAsAdmin");
        GetAgentPrinicplesRequestMsg request = new GetAgentPrinicplesRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            GetAgentPrinicplesResponseMsg result = instance.getAgentPrinicples(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getUserList());
            assertNotNull(result.getUserList().getUserInfo());
            assertFalse(result.getUserList().getUserInfo().isEmpty());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    /**
     * Test of getMailSettings method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetMailSettingsValidRequestAsAdmin() throws Exception {
        System.out.println("testGetMailSettingsValidRequestAsAdmin");
        GetMailSettingsRequestMsg request = new GetMailSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(adminctx);
        GetMailSettingsResponseMsg result = instance.getMailSettings(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertNotNull(result.getPropertiesList());
        assertFalse(result.getPropertiesList().isEmpty());

    }

    @org.junit.Test(expected = SecurityException.class)
    public void testGetMailSettingsValidRequestAsAgent() throws Exception {
        System.out.println("testGetMailSettingsValidRequestAsAgent");
        GetMailSettingsRequestMsg request = new GetMailSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        PCS4jBean instance = new PCS4jBean(agentctx);

        GetMailSettingsResponseMsg result = instance.getMailSettings(request);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testGetMailSettingsNullMessage() throws Exception {
        System.out.println("testGetMailSettingsNullMessage");
        GetMailSettingsRequestMsg request = null;
        PCS4jBean instance = new PCS4jBean(adminctx);

        GetMailSettingsResponseMsg result = instance.getMailSettings(request);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testGetMailSettingsNullClass() throws Exception {
        System.out.println("testGetMailSettingsNullClass");
        GetMailSettingsRequestMsg request = new GetMailSettingsRequestMsg();
        PCS4jBean instance = new PCS4jBean(adminctx);

        GetMailSettingsResponseMsg result = instance.getMailSettings(request);

    }

    /**
     * Test of setMailSettings method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetMailSettings() throws Exception {
        System.out.println("setMailSettings");
        GetMailSettingsRequestMsg r = new GetMailSettingsRequestMsg();
        r.setClassification(new SecurityWrapper());

        PCS4jBean instance = new PCS4jBean(adminctx);
        GetMailSettingsResponseMsg mailSettings = instance.getMailSettings(r);
        assertNotNull(mailSettings);

        SetMailSettingsRequestMsg request = new SetMailSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        //just transfer them all over

        request.getPropertiesList().addAll(mailSettings.getPropertiesList());

        SetMailSettingsResponseMsg result = instance.setMailSettings(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());

        r = new GetMailSettingsRequestMsg();
        r.setClassification(new SecurityWrapper());

        GetMailSettingsResponseMsg mailSettings2 = instance.getMailSettings(r);
        //    assertEquals(mailSettings, mailSettings2);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetMailSettingsNullMessage() throws Exception {
        System.out.println("testSetMailSettingsNullMessage");
        SetMailSettingsRequestMsg request = new SetMailSettingsRequestMsg();
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetMailSettingsResponseMsg expResult = null;

        SetMailSettingsResponseMsg result = instance.setMailSettings(request);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetMailSettingsNullClass() throws Exception {
        System.out.println("testSetMailSettingsNullClass");
        SetMailSettingsRequestMsg request = new SetMailSettingsRequestMsg();
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetMailSettingsResponseMsg expResult = null;

        SetMailSettingsResponseMsg result = instance.setMailSettings(request);

    }

    @org.junit.Test
    public void testSLARemovalAlerting() throws Exception {
        //create policy with slas
        //set it
        //add subscription
        //remove sla
        //test db to ensure that the sla was removed
        //test db to ensure that the subscription was removed
    }

    @org.junit.Test
    public void testSLARemoval() throws Exception {
        //create policy with slas
        //set it
        //remove sla
        //set policy
        //test db to ensure that the sla was removed
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetPolicyWithNullType() throws Exception {
        System.out.println("testSetPolicyWithNullType");
        SetServicePolicyRequestMsg r = new SetServicePolicyRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setURL(url);
        r.setPolicy(new TransactionalWebServicePolicy());
        r.getPolicy().setURL(url);
        PCS4jBean instance = new PCS4jBean(adminctx);
        //  r.getPolicy().setPolicyType(PolicyType.TRANSACTIONAL);
        instance.setServicePolicy(r);
        DeleteServicePolicyRequestMsg del = new DeleteServicePolicyRequestMsg();
        del.setClassification(new SecurityWrapper());
        del.setURL(url);
        instance.deleteServicePolicy(del);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetPolicyWithStatisticalInvalidSLAs() throws Exception {
        System.out.println("testSetPolicyWithStatisticalInvalidSLAs");
        RemoveServicePolicy(url);
        SetServicePolicyRequestMsg r = new SetServicePolicyRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setURL(url);
        r.setPolicy(new TransactionalWebServicePolicy());
        r.getPolicy().setURL(url);
        r.getPolicy().setPolicyType(PolicyType.STATISTICAL);
        //set statistical sla
        ArrayOfSLA asla = new ArrayOfSLA();
        SLA sla = new SLA();
        SLARuleGeneric rule = new SLARuleGeneric();
        rule.setClassName(AllFaults.class.getCanonicalName());

        sla.setRule(rule);
        ArrayOfSLAActionBaseType actions = new ArrayOfSLAActionBaseType();
        actions.getSLAAction().add(Utility.newEmailAction("fgsms@asdasd.com", "hello", "fgsms@asdasd.com"));

        sla.setAction(actions);
        asla.getSLA().add(sla);
        sla.setGuid(UUID.randomUUID().toString());
        //TODO is this valid?
        r.getPolicy().setServiceLevelAggrements((asla));//fac.createArrayOfSLAElement(asla));
        PCS4jBean instance = new PCS4jBean(adminctx);

        try {
            instance.setServicePolicy(r);
            RemoveServicePolicy(url);

        } catch (Exception ex) {
            RemoveServicePolicy(url);
            throw ex;
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetPolicyWithTransactionInvalidSLAs() throws Exception {
        System.out.println("testSetPolicyWithTransactionInvalidSLAs");
        RemoveServicePolicy(url);
        SetServicePolicyRequestMsg r = new SetServicePolicyRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setURL(url);
        r.setPolicy(new TransactionalWebServicePolicy());
        r.getPolicy().setURL(url);
        r.getPolicy().setPolicyType(PolicyType.TRANSACTIONAL);
        //set statistical sla
        ArrayOfSLA asla = new ArrayOfSLA();
        SLA sla = new SLA();
        SLARuleGeneric rule = new SLARuleGeneric();
        rule.setClassName(BrokerQueueSizeGreaterThan.class.getCanonicalName());
        sla.setRule(rule);
        ArrayOfSLAActionBaseType actions = new ArrayOfSLAActionBaseType();
        actions.getSLAAction().add(Utility.newEmailAction("fgsms@asdasd.com", "hello", "fgsms@asdasd.com"));
        sla.setAction(actions);
        asla.getSLA().add(sla);
        sla.setGuid(UUID.randomUUID().toString());
        //TODO validate
        r.getPolicy().setServiceLevelAggrements((asla));//createArrayOfSLAElement(asla));
        PCS4jBean instance = new PCS4jBean(adminctx);

        try {
            instance.setServicePolicy(r);
            RemoveServicePolicy(url);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            RemoveServicePolicy(url);
            throw ex;
        }
    }

    /*
     * create policy with userid http header check db remove policy check db
     */
    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetPolicyWithEmptyHttpheaderid() throws Exception {
        System.out.println("testSetPolicyWithHttpheaderid");
        RemoveServicePolicy(url);
        SetServicePolicyRequestMsg r = new SetServicePolicyRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setURL(url);

        TransactionalWebServicePolicy tp = new TransactionalWebServicePolicy();
        r.setPolicy(tp);
        r.getPolicy().setURL(url);
        r.getPolicy().setPolicyType(PolicyType.TRANSACTIONAL);
        ArrayOfUserIdentity uids = new ArrayOfUserIdentity();
        UserIdentity id = new UserIdentity();
        uids.getUserIdentity().add(id);
        id.setUseHttpHeader(Boolean.TRUE);
        tp.setUserIdentification((uids));//createArrayOfUserIdentity(uids));
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            SetServicePolicyResponseMsg setServicePolicy = instance.setServicePolicy(r);
            RemoveServicePolicy(url);
            assertNotNull(setServicePolicy);
        } catch (Exception ex) {
            RemoveServicePolicy(url);
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @org.junit.Test
    public void testSetPolicyWithHttpheaderid() throws Exception {
        System.out.println("testSetPolicyWithHttpheaderid");
        RemoveServicePolicy(url);
        SetServicePolicyRequestMsg r = new SetServicePolicyRequestMsg();
        r.setClassification(new SecurityWrapper());
        r.setURL(url);
        TransactionalWebServicePolicy tp = new TransactionalWebServicePolicy();
        r.setPolicy(tp);
        r.getPolicy().setURL(url);
        r.getPolicy().setPolicyType(PolicyType.TRANSACTIONAL);
        ArrayOfUserIdentity uids = new ArrayOfUserIdentity();
        UserIdentity id = new UserIdentity();
        id.setHttpHeaderName(("someheader"));
        id.setUseHttpHeader(Boolean.TRUE);
        uids.getUserIdentity().add(id);
        tp.setUserIdentification((uids));//createArrayOfUserIdentity(uids));
        PCS4jBean instance = new PCS4jBean(adminctx);
        try {
            SetServicePolicyResponseMsg setServicePolicy = instance.setServicePolicy(r);
            assertNotNull(setServicePolicy);

            ServicePolicyRequestMsg rr = new ServicePolicyRequestMsg();
            rr.setClassification(new SecurityWrapper());
            rr.setURI(url);
            rr.setPolicytype(PolicyType.TRANSACTIONAL);
            ServicePolicyResponseMsg servicePolicy = instance.getServicePolicy(rr);
            assertNotNull(servicePolicy);
            assertNotNull(servicePolicy.getClassification());
            assertNotNull(servicePolicy.getPolicy());
            assertNotNull(servicePolicy.getPolicy().getPolicyType());
            if (servicePolicy.getPolicy() instanceof TransactionalWebServicePolicy) {
                TransactionalWebServicePolicy t = (TransactionalWebServicePolicy) servicePolicy.getPolicy();
                assertNotNull(t.getUserIdentification());

                assertNotNull(t.getUserIdentification().getUserIdentity());
                assertFalse(t.getUserIdentification().getUserIdentity().isEmpty());
            } else {
                fail("returned an unexpected policy type " + servicePolicy.getPolicy().getClass());
            }

            DeleteServicePolicyRequestMsg dr = new DeleteServicePolicyRequestMsg();
            dr.setClassification(new SecurityWrapper());
            dr.setURL(url);
            DeleteServicePolicyResponseMsg deleteServicePolicy = instance.deleteServicePolicy(dr);
            assertNotNull(deleteServicePolicy);
            assertNotNull(deleteServicePolicy.getClassification());

            VerifyPolicyDeleted(url);
        } catch (Exception ex) {
            ex.printStackTrace();
            RemoveServicePolicy(url);
            fail("unexpected failure");
        }
    }

    /*
     * create policy with userid xpath check db remove policy check db
     */
    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetGeneralSettings() throws Exception {
        System.out.println("testSetGeneralSettings");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetGeneralSettingsRequestMsg request = null;

        instance.setGeneralSettings(request);
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetGeneralSettingsNuclass() throws Exception {
        System.out.println("testSetGeneralSettings");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetGeneralSettingsRequestMsg request = new SetGeneralSettingsRequestMsg();

        instance.setGeneralSettings(request);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testGetGeneralSettings() throws Exception {
        System.out.println("testGetGeneralSettings");
        PCS4jBean instance = new PCS4jBean(adminctx);
        GetGeneralSettingsRequestMsg request = null;

        instance.getGeneralSettings(request);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testGetGeneralSettingsnc() throws Exception {
        System.out.println("testGetGeneralSettingsnc");
        PCS4jBean instance = new PCS4jBean(adminctx);
        GetGeneralSettingsRequestMsg request = new GetGeneralSettingsRequestMsg();

        instance.getGeneralSettings(request);

    }

    @org.junit.Test
    public void testGetGeneralSettingsvalid() throws Exception {
        RemoveGeneralSettings(TestKey, TestName);
        InsertGeneralSettings(TestKey, TestName, TestValue);
        System.out.println("testGetGeneralSettingsnc");
        PCS4jBean instance = new PCS4jBean(adminctx);
        GetGeneralSettingsRequestMsg request = new GetGeneralSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            GetGeneralSettingsResponseMsg response = instance.getGeneralSettings(request);
            assertNotNull(response);
            assertNotNull(response.getClassification());
            assertNotNull(response.getKeyNameValue());
        } catch (Exception ex) {
            RemoveGeneralSettings(TestKey, TestName);
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testGetGeneralSettingsvalidAgent() throws Exception {
        RemoveGeneralSettings(TestKey, TestName);
        InsertGeneralSettings(TestKey, TestName, TestValue);
        System.out.println("testGetGeneralSettingsAgent");
        PCS4jBean instance = new PCS4jBean(agentctx);
        GetGeneralSettingsRequestMsg request = new GetGeneralSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            GetGeneralSettingsResponseMsg response = instance.getGeneralSettings(request);
            RemoveGeneralSettings(TestKey, TestName);
            assertNotNull(response);
            assertNotNull(response.getClassification());
            assertNotNull(response.getKeyNameValue());
        } catch (Exception ex) {
            RemoveGeneralSettings(TestKey, TestName);
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test
    public void testGetGeneralSettingsvalidBob() throws Exception {
        System.out.println("testGetGeneralSettingsvalidBob");
        PCS4jBean instance = new PCS4jBean(userbobctx);
        GetGeneralSettingsRequestMsg request = new GetGeneralSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            GetGeneralSettingsResponseMsg response = instance.getGeneralSettings(request);
            fail("unexpected success");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @org.junit.Test
    public void testGetGeneralSettingsvalid2() throws Exception {
        RemoveGeneralSettings(TestKey, TestName);
        InsertGeneralSettings(TestKey, TestName, TestValue);
        System.out.println("testGetGeneralSettings2");
        PCS4jBean instance = new PCS4jBean(adminctx);
        GetGeneralSettingsRequestMsg request = new GetGeneralSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            GetGeneralSettingsResponseMsg response = instance.getGeneralSettings(request);
            RemoveGeneralSettings(TestKey, TestName);
            assertNotNull(response);
            assertNotNull(response.getClassification());
            assertNotNull(response.getKeyNameValue());
            assertFalse(response.getKeyNameValue().isEmpty());
        } catch (Exception ex) {
            RemoveGeneralSettings(TestKey, TestName);
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testRemoveGeneralSettings() throws Exception {

        System.out.println("testRemoveGeneralSettings");
        PCS4jBean instance = new PCS4jBean(adminctx);
        RemoveGeneralSettingsRequestMsg request = null;

        try {
            RemoveGeneralSettingsResponseMsg response = instance.removeGeneralSettings(request);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testRemoveGeneralSettingsmc() throws Exception {

        System.out.println("testRemoveGeneralSettingsmc");
        PCS4jBean instance = new PCS4jBean(adminctx);
        RemoveGeneralSettingsRequestMsg request = new RemoveGeneralSettingsRequestMsg();

        try {
            RemoveGeneralSettingsResponseMsg response = instance.removeGeneralSettings(request);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @org.junit.Test
    public void testRemoveGeneralSettingsValid() throws Exception {
        InsertGeneralSettings(TestKey, TestName, TestValue);
        System.out.println("testRemoveGeneralSettingsValid");
        PCS4jBean instance = new PCS4jBean(adminctx);
        RemoveGeneralSettingsRequestMsg request = new RemoveGeneralSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            KeyNameValue pair = new KeyNameValue();
            pair.setPropertyKey("junit");
            pair.setPropertyName("parameter");
            request.getKeyNameValue().add(pair);
            RemoveGeneralSettingsResponseMsg response = instance.removeGeneralSettings(request);
            VerifyGeneralSettingRemoval(TestKey, TestName);
            assertNotNull(response);
            assertNotNull(response.getClassification());
        } catch (Exception ex) {
            RemoveGeneralSettings(TestKey, TestName);
            ex.printStackTrace();
            fail("unexpected failure");
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetCredentialnClearNull() throws Exception {
        System.out.println("testSetCredentialnClearNull");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetCredentialsRequestMsg request = null;
        //request.setClassification(new SecurityWrapper());
        try {

            SetCredentialsResponseMsg response = instance.setCredentials(request);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetCredentialnClearNullClass() throws Exception {
        System.out.println("testSetCredentialnClearNullClass");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();

        SetCredentialsResponseMsg response = instance.setCredentials(request);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetCredentialnClearNullBody() throws Exception {
        System.out.println("testSetCredentialnClearNullBody");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());

        SetCredentialsResponseMsg response = instance.setCredentials(request);

    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetCredentialnClearNullURL() throws Exception {
        System.out.println("testSetCredentialnClearNullURL");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            request.setUsername("bob");
            request.setPassword("password");
            SetCredentialsResponseMsg response = instance.setCredentials(request);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetCredentialnClearNullusername() throws Exception {
        System.out.println("testSetCredentialnClearNullusername");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            request.setUrl(url);
            //  request.setUsername("bob");
            request.setPassword("password");
            SetCredentialsResponseMsg response = instance.setCredentials(request);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testSetCredentialnClearNullpwd() throws Exception {
        System.out.println("testSetCredentialnClearNullpwd");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            request.setUrl(url);
            request.setUsername("bob");
            //  request.setPassword(pwdcol);
            SetCredentialsResponseMsg response = instance.setCredentials(request);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @org.junit.Test(expected = SecurityException.class)
    public void testSetCredentialnClearasAgent() throws Exception {
        System.out.println("testSetCredentialnClearasAgent");
        PCS4jBean instance = new PCS4jBean(agentctx);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            request.setUrl(url);
            request.setUsername("bob");
            request.setPassword("password");
            SetCredentialsResponseMsg response = instance.setCredentials(request);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    @org.junit.Test(expected = SecurityException.class)
    public void testSetCredentialnClearasBob() throws Exception {
        System.out.println("testSetCredentialnClearasBob");
        PCS4jBean instance = new PCS4jBean(userbobctx);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());

        request.setUrl(url);
        request.setUsername("bob");
        request.setPassword("password");
        SetCredentialsResponseMsg response = instance.setCredentials(request);

    }

    @org.junit.Test
    public void testSetCredentialnClear() throws Exception {
        System.out.println("testSetCredentialnClear");
        PCS4jBean instance = new PCS4jBean(adminctx);
        CreatePolicy(url);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            request.setPassword("password");
            request.setPasswordEncrypted(false);
            request.setUrl(url);
            request.setUsername("junit");
            SetCredentialsResponseMsg response = instance.setCredentials(request);
            assertNotNull(response);
            assertNotNull(response.getClassification());

            VerifyCredentialsAreEncrypted("password");
            RemoveCredentials();
        } catch (Exception ex) {
            RemoveCredentials();
            ex.printStackTrace();
            fail("unexpected failure");
        } finally {
            RemoveServicePolicy(url);
        }
    }

    @org.junit.Test
    public void testSetCredentialsEncrypted() throws Exception {
        System.out.println("testSetCredentialsEncrypted");
        PCS4jBean instance = new PCS4jBean(adminctx);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());
        CreatePolicy(url);
        try {
            request.setPassword(Utility.EN("password"));
            request.setPasswordEncrypted(true);
            request.setUrl(url);
            request.setUsername("junit");
            SetCredentialsResponseMsg response = instance.setCredentials(request);
            assertNotNull(response);
            assertNotNull(response.getClassification());

            VerifyCredentialsAreEncrypted("password");
            RemoveCredentials();
        } catch (Exception ex) {
            RemoveCredentials();
            ex.printStackTrace();
            fail("unexpected failure");
        } finally {
            RemoveServicePolicy(url);
        }
    }

    private void VerifyCredentialsAreEncrypted(String pwd) throws SQLException {
        boolean ok = true;
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("select * from bueller where uri=? ");
        com.setString(1, url);

        ResultSet rs = com.executeQuery();
        if (rs.next()) {
            if (!rs.getString("username").equalsIgnoreCase("junit")) {
                ok = false;
                System.out.println("username does not match");
            }
            String storepwd = new String(rs.getBytes("pwdcol"));
            if (storepwd.equals(pwd)) {
                ok = false;
                System.out.println("password was stored in clear text");
            }
            if (!Utility.DE(storepwd).equals(pwd)) {
                ok = false;
                System.out.println("password was not decryptable using the java keys");
            }

        }
        com.close();
        con.close();
        if (!ok) {
            fail("failed see output above");
        }

    }

    private void VerifyCredentialsAreRemoved() throws SQLException {
        boolean ok = true;
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("select * from bueller where uri=? ");
        com.setString(1, url);

        ResultSet rs = com.executeQuery();
        if (rs.next()) {
            ok = false;
        }
        com.close();
        con.close();
        if (!ok) {
            fail("pwd still present!");
        }

    }

    private void RemoveCredentials() throws SQLException {
        boolean ok = true;
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("delete from bueller where uri=? ");
        com.setString(1, url);

        com.executeUpdate();

        com.close();
        con.close();
    }

    @org.junit.Test
    public void testClearCredentials() throws Exception {
        System.out.println("testClearCredentials");
        PCS4jBean instance = new PCS4jBean(adminctx);
        CreatePolicy(url);
        SetCredentialsRequestMsg request = new SetCredentialsRequestMsg();
        request.setClassification(new SecurityWrapper());
        try {
            request.setPassword(Utility.EN("password"));
            request.setPasswordEncrypted(true);
            request.setUrl(url);
            request.setUsername("junit");
            SetCredentialsResponseMsg response = instance.setCredentials(request);
            assertNotNull(response);
            assertNotNull(response.getClassification());

            VerifyCredentialsAreEncrypted("password");
            ClearCredentialsRequestMsg rr = new ClearCredentialsRequestMsg();
            rr.setClassification(new SecurityWrapper());
            rr.setUrl(url);
            ClearCredentialsResponseMsg clearCredentials = instance.clearCredentials(rr);
            VerifyCredentialsAreRemoved();
            RemoveCredentials();
            assertNotNull(clearCredentials);
            assertNotNull(clearCredentials.getClassification());

        } catch (Exception ex) {
            RemoveCredentials();
            RemoveServicePolicy(url);
            System.out.println(ex.getMessage());
            fail("unexpected failure");
        }

    }

    private void InsertGeneralSettings(String key, String name, String value) throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("insert into settings (namecol, keycol, valuecol) values (?,?,?);");
        com.setString(2, key);
        com.setString(1, name);
        com.setBytes(3, value.getBytes());
        com.executeUpdate();
        com.close();
        con.close();
    }

    private void RemoveGeneralSettings(String key, String name) throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("delete from  settings where namecol=? and keycol=?;");
        com.setString(2, key);
        com.setString(1, name);
        com.executeUpdate();
        com.close();
        con.close();
    }

    private void VerifyGeneralSettingRemoval(String key, String name) throws SQLException {
        boolean ok = true;
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("select * from settings where namecol=? and keycol=?;");
        com.setString(2, key);
        com.setString(1, name);
        ResultSet rs = com.executeQuery();
        if (rs.next()) {
            ok = false;
        }
        com.close();
        con.close();
        RemoveGeneralSettings(TestKey, TestName);
        if (!ok) {
            fail("settings were not removed by the service");
        }
    }

    @org.junit.Test(expected = SecurityException.class)
    public void testSetServicePolicyAsBobWithEvelatedSLAAction() throws Exception {
        System.out.println("testSetServicePolicyAsBobWithEvelatedSLAAction");
        /**
         * set policy as admin grant write permissions to bob set policy as bob
         * with SLA restart action expect failure
         */
        RemoveServicePolicy(url);
        ServicePolicyRequestMsg get = new ServicePolicyRequestMsg();
        get.setClassification(new SecurityWrapper());
        get.setURI(url);
        get.setPolicytype(PolicyType.PROCESS);
        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        PCS4jBean instance = new PCS4jBean(adminctx);
        PCS4jBean instance2 = new PCS4jBean(userbobctx);

        ServicePolicy pol = instance.getServicePolicy(get).getPolicy();
        assertNotNull(pol);

        SetServicePermissionsRequestMsg p = new SetServicePermissionsRequestMsg();
        p.setURL(url);
        p.setClassification(new SecurityWrapper());
        ArrayOfUserPermissionType a = new ArrayOfUserPermissionType();
        UserPermissionType up = new UserPermissionType();
        up.setUser(bobusername);
        up.setRight(RightEnum.WRITE);
        a.getUserPermissionType().add(up);
        p.setRights(a);
        SetServicePermissionsResponseMsg setServicePermissions = instance.setServicePermissions(p);
        assertNotNull(setServicePermissions);
        assertNotNull(setServicePermissions.getClassification());

        request.setPolicy(pol);
        ArrayOfSLA createServicePolicyServiceLevelAggrements = (new ArrayOfSLA());
        SLA sla = new SLA();
        sla.setGuid(UUID.randomUUID().toString());
        ArrayOfSLAActionBaseType actions = new ArrayOfSLAActionBaseType();
        SLAAction aa = new SLAAction();
        aa.setImplementingClassName(SLAActionRestart.class.getCanonicalName());
        actions.getSLAAction().add(aa);
        sla.setAction(actions);

        SLARuleGeneric rule = new SLARuleGeneric();
        rule.setClassName(HighCPUUsage.class.getCanonicalName());
        createServicePolicyServiceLevelAggrements.getSLA().add(sla);

        request.getPolicy().setServiceLevelAggrements(createServicePolicyServiceLevelAggrements);
        try {
            SetServicePolicyResponseMsg result = instance2.setServicePolicy(request);   //as bob
            RemoveServicePolicy(url);

        } catch (Exception ex) {

            RemoveServicePolicy(url);
            throw ex;
        }
    }

    @Ignore
    @Test
    public void perfSetServicePolicy() throws Exception {
        PCS4jBean instance = new PCS4jBean(adminctx);
        String baseurl = "http://localhost:9998/pcs/prefSetServicePolicy";
        for (int i = 0; i < 1000; i++) {
            System.out.println("Creating " + baseurl + i);
            long now = System.currentTimeMillis();
            setPolicy(instance, baseurl + i);
            System.out.println("created " + baseurl + i + " in " + (System.currentTimeMillis() - now));
        }
        for (int i = 0; i < 1000; i++) {
            System.out.println("deleting " + baseurl + i);
            long now = System.currentTimeMillis();
            RemoveServicePolicy(baseurl + i);
            System.out.println("deleting " + baseurl + i + " in " + (System.currentTimeMillis() - now));
        }
    }

    private static void setPolicy(PCS pcs, String url) throws ConfigurationException, ServiceUnavailableException, AccessDeniedException, DatatypeConfigurationException {
        System.out.println("Setting up policy");
        TransactionalWebServicePolicy pol = new TransactionalWebServicePolicy();
        pol.setAgentsEnabled(true);
        pol.setBuellerEnabled(false);
        pol.setDataTTL(DatatypeFactory.newInstance().newDuration(600000));
        pol.setHealthStatusEnabled(false);
        pol.setMachineName(Utility.getHostName());
        pol.setPolicyType(PolicyType.TRANSACTIONAL);
        pol.setURL(url);
        SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
        req.setClassification(new SecurityWrapper());
        req.setURL(pol.getURL());
        req.setPolicy(pol);
        pcs.setServicePolicy(req);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void pluginRegistration() throws Exception {
        //registers a plugin that isn't registered
        System.out.println("pluginRegistration");

        PCS4jBean instance = new PCS4jBean(adminctx);

        RemoveServicePolicy(url);
        ServicePolicyRequestMsg get = new ServicePolicyRequestMsg();
        get.setClassification(new SecurityWrapper());
        get.setURI(url);
        get.setPolicytype(PolicyType.TRANSACTIONAL);

        ServicePolicy pol = instance.getServicePolicy(get).getPolicy();
        assertNotNull(pol);

        SetServicePolicyRequestMsg request = new SetServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        request.setPolicy(pol);

        ArrayOfSLA createServicePolicyServiceLevelAggrements = (new ArrayOfSLA());
        SLA sla = new SLA();
        sla.setGuid(UUID.randomUUID().toString());
        ArrayOfSLAActionBaseType actions = new ArrayOfSLAActionBaseType();
        SLAAction aa = new SLAAction();
        aa.setImplementingClassName(UUID.randomUUID().toString());
        actions.getSLAAction().add(aa);
        sla.setAction(actions);
        SLARuleGeneric rule = new SLARuleGeneric();
        rule.setClassName(AllFaults.class.getCanonicalName());

        sla.setRule(rule);
        createServicePolicyServiceLevelAggrements.getSLA().add(sla);

        request.getPolicy().setServiceLevelAggrements(createServicePolicyServiceLevelAggrements);

        try {
            SetServicePolicyResponseMsg result = instance.setServicePolicy(request);

        } catch (Exception ex) {

            throw ex;
        } finally {
            RemoveServicePolicy(url);
        }
    }
}
