/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.miloss.fgsms.services.pcs.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.xml.datatype.DatatypeFactory;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfSLA;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfSLAActionBaseType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfUserIdentity;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfUserInfo;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ArrayOfUserPermissionType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ClearCredentialsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ClearCredentialsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.DeleteServicePolicyResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ElevateSecurityLevelRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ElevateSecurityLevelResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicyCollection;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetAdministratorsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetAdministratorsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetAgentPrinicplesRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetAgentPrinicplesResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetAlertRegistrationsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetAlertRegistrationsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetAvailableAlertRegistrationsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetAvailableAlertRegistrationsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGeneralSettingsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetGlobalPolicyResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetMailSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetMailSettingsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetMyEmailAddressRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetMyEmailAddressResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetServicePermissionsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetServicePermissionsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetUDDIDataPublishServicePoliciesRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetUDDIDataPublishServicePoliciesResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GlobalPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValue;
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachinePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ObjectFactory;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ProcessPolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RemoveGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RemoveGeneralSettingsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.RightEnum;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLA;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAAction;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLARuleGeneric;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SLAregistration;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetAdministratorRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetAdministratorResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetAlertRegistrationsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetAlertRegistrationsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetCredentialsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetCredentialsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetGeneralSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetGlobalPolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetGlobalPolicyResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetMailSettingsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetMailSettingsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetMyEmailAddressRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetMyEmailAddressResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePermissionsRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePermissionsResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.StatusServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.UserIdentity;
import org.miloss.fgsms.services.interfaces.policyconfiguration.UserInfo;
import org.miloss.fgsms.services.interfaces.policyconfiguration.UserPermissionType;
import org.miloss.fgsms.sla.actions.SLAActionRestart;
import org.miloss.fgsms.sla.rules.AllFaults;
import org.miloss.fgsms.sla.rules.BrokerQueueSizeGreaterThan;
import org.miloss.fgsms.sla.rules.HighCPUUsage;
import static org.miloss.fgsms.test.WebServiceBaseTests.CreatePolicy;
import static org.miloss.fgsms.test.WebServiceBaseTests.RemoveServicePolicy;
import static org.miloss.fgsms.test.WebServiceBaseTests.bobusername;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author la.alex.oree
 */
public class LeakFinder  extends org.miloss.fgsms.test.WebServiceBaseTests {

    public LeakFinder() throws Exception {

        super();
        url = "http://localhost:8180/jUnitTestCasePCSLeakFinderTest";
        Init();


    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {

    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @After
    @Override
    public void tearDown() {
        super.tearDown();
        RemoveServicePolicy(url);
    }
    
    final static int RUNS=1000;
    final PCS4jBean instance = new PCS4jBean(adminctx);
      
      
       /**
     * Test of getServicePolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetServicePolicyAdmin() throws Exception {
	 for (int i=0; i < RUNS; i++){
        RemoveServicePolicy(url);
        System.out.println("testGetServicePolicyAdmin");

        ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURI(url);
        request.setPolicytype(PolicyType.TRANSACTIONAL);
        request.setDomain(Utility.getHostName());

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

	 }

        //     fail("should not be able to create a policy as administrator");
    }

    
    public static void RemoveServicePolicyold(String u) {
        try {
            //remove service policy
            Connection con = Utility.getConfigurationDBConnection();
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
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            //remove service policy
            Connection con = Utility.getPerformanceDBConnection();
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
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void testGetServicePolicyAgent() throws Exception {
        
	 for (int i=0; i < RUNS; i++){
        System.out.println("testGetServicePolicyAgent");
        RemoveServicePolicy(url);
        ServicePolicyRequestMsg request = new ServicePolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURI(url);
        ServicePolicyResponseMsg response = null;


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

    }

    /**
     * Test of setServicePolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetServicePolicyValidRequest() throws Exception {
	 for (int i=0; i < RUNS; i++){
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



        ServicePolicy pol = instance.getServicePolicy(get).getPolicy();
        assertNotNull(pol);

        FederationPolicyCollection fc = new FederationPolicyCollection();

        FederationPolicy fpol = new FederationPolicy();
        fpol.setImplementingClassName("org.miloss.fgsms.uddipub.UddiPublisher");
        fpol.getParameterNameValue().add(Utility.newNameValuePair("Binding", "someuddikey2", false, false));


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
    }

    public static ObjectFactory fac = new ObjectFactory();

    /**
     * Test of getUDDIDataPublishServicePolicies method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetUDDIDataPublishServicePoliciesValidMessage() throws Exception {
for (int interations=0; interations < RUNS; interations++){
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
    }

    /**
     * Test of setGlobalPolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetGlobalPolicyValid() throws Exception {
        for (int i=0; i < RUNS; i++){
	 System.out.println("testSetGlobalPolicyValid");

        
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
    }


    /**
     * Test of getGlobalPolicy method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetGlobalPolicyNullClassMsg() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("testGetGlobalPolicyNullMsg");
        GetGlobalPolicyRequestMsg request = new GetGlobalPolicyRequestMsg();

        
        try {
            GetGlobalPolicyResponseMsg result = instance.getGlobalPolicy(request);
        } catch (Exception ex) {
            fail("null msg should be allowed but failed " + ex.getMessage());
        }
	 }
    }

    @org.junit.Test
    public void testGetGlobalPolicy() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("getGlobalPolicy");
        GetGlobalPolicyRequestMsg request = new GetGlobalPolicyRequestMsg();
        request.setClassification(new SecurityWrapper());
        

        GetGlobalPolicyResponseMsg result = instance.getGlobalPolicy(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertNotNull(result.getClassification().getClassification());
        assertNotNull(result.getPolicy());
	 }

    }

    @org.junit.Test
    public void testDeleteServiceAsAdminValidRequest() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("testDeleteServiceAsAdminValidRequest");
        CreatePolicy(url);
        DeleteServicePolicyRequestMsg request = null;

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
    }

    @org.junit.Test
    public void testSetServicePermissionsValidRequestNoPermissions() throws Exception {
	 for (int i=0; i < RUNS; i++){
        RemoveServicePolicy(url);
        CreatePolicy(url);
        System.out.println("testSetServicePermissionsValidRequestNoPermissions");
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        
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

    }

    @org.junit.Test
    public void testSetServicePermissionsValidRequestSomePermissions() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("testSetServicePermissionsValidRequestSomePermissions");
        RemoveServicePolicy(url);
        CreatePolicy(url);
        SetServicePermissionsRequestMsg request = new SetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setURL(url);
        
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
    }


    @org.junit.Test
    public void testGetServicePermissionsNotExistingUser() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("testGetServicePermissionsNotExistingUser");
        RemoveServicePolicy(url);
        GetServicePermissionsRequestMsg request = new GetServicePermissionsRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.setUser(("bob"));
        
        try {
            GetServicePermissionsResponseMsg result = instance.getServicePermissions(request);
            assertNotNull(result);
            assertNotNull(result.getClassification());
            assertNotNull(result.getRights());
        } catch (Exception ex) {
            fail("error");
        }
	 }
    }

    @org.junit.Test
    public void testGetAdministratorsValidMessageAsAdmin() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("testGetAdministratorsValidMessage");
        GetAdministratorsRequestMsg request = new GetAdministratorsRequestMsg();
        request.setClassification(new SecurityWrapper());
        
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

    @org.junit.Test
    public void testSetAdministratorValid() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("setAdministrator");
        removeuser(bobusername);
        //get the list
        GetAdministratorsRequestMsg request = new GetAdministratorsRequestMsg();
        request.setClassification(new SecurityWrapper());
        GetAdministratorsResponseMsg result = null;
        
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
        //   
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
            for (int k = 0; k < result.getUserList().getUserInfo().size(); k++) {
                if (result.getUserList().getUserInfo().get(k).getUsername().equals(bobusername)) {
                    result.getUserList().getUserInfo().remove(k);
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
            for (int k = 0; k < result.getUserList().getUserInfo().size(); k++) {
                if (result.getUserList().getUserInfo().get(k).getUsername().equals(bobusername)) {
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
    }

    @org.junit.Test
    public void testElevateSecurityLevelValidAsAdmin() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("testElevateSecurityLevelValidAsUser");
        ElevateSecurityLevelRequestMsg request = new ElevateSecurityLevelRequestMsg();
        request.setClassification(new SecurityWrapper(ClassificationType.S, "TEST"));
        
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
    }

    @org.junit.Test
    public void testGetMyEmailAddressUserNotDefinedValid() throws Exception {
	 for (int i=0; i < RUNS; i++){
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
    }

    public void removeuser(String u) throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement ps = con.prepareStatement("delete from userpermissions where username=?; delete from slasub where username=?; delete from users where username=?;");
        ps.setString(1, bobusername);
        ps.setString(2, bobusername);
        ps.setString(3, bobusername);
        ps.execute();
    }

    @org.junit.Test
    public void testGetMyEmailAddressUserIsDefinedValid() throws Exception {
	 for (int i=0; i < RUNS; i++){
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
    }


    @org.junit.Test
    public void testSetMyEmailAddressValid() throws Exception {
	 for (int i=0; i < RUNS; i++){
        System.out.println("testSetMyEmailAddressValid");
        SetMyEmailAddressRequestMsg request = new SetMyEmailAddressRequestMsg();
        request.setClassification(new SecurityWrapper());
        request.getEmail().add("bob@bob.com");
        PCS4jBean instance = new PCS4jBean(userbobctx);

        SetMyEmailAddressResponseMsg result = instance.setMyEmailAddress(request);
        assertNotNull(result.getClassification());
	 }
    }


    /**
     * Test of getAlertRegistrations method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetAlertRegistrationsValidMsg() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        System.out.println("testGetAlertRegistrationsValidMsg");
        GetAlertRegistrationsRequestMsg request = new GetAlertRegistrationsRequestMsg();
        request.setClassification(new SecurityWrapper());
        

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
    }


    /**
     * Test of setAgentPrinicples method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetAgentPrinicplesAsAdmin() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        System.out.println("testGetAgentPrinicplesAsAgent");
        GetAgentPrinicplesRequestMsg request = new GetAgentPrinicplesRequestMsg();
        request.setClassification(new SecurityWrapper());
        
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
        }}
    }

    /**
     * Test of getMailSettings method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testGetMailSettingsValidRequestAsAdmin() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        System.out.println("testGetMailSettingsValidRequestAsAdmin");
        GetMailSettingsRequestMsg request = new GetMailSettingsRequestMsg();
        request.setClassification(new SecurityWrapper());
        
        GetMailSettingsResponseMsg result = instance.getMailSettings(request);
        assertNotNull(result);
        assertNotNull(result.getClassification());
        assertNotNull(result.getPropertiesList());
        assertFalse(result.getPropertiesList().isEmpty());
	 }

    }
    /**
     * Test of setMailSettings method, of class PCS4jBean.
     */
    @org.junit.Test
    public void testSetMailSettings() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        System.out.println("setMailSettings");
        GetMailSettingsRequestMsg r = new GetMailSettingsRequestMsg();
        r.setClassification(new SecurityWrapper());

        
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
    }


    @org.junit.Test
    public void testSetPolicyWithHttpheaderid() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
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
    }
    
    @org.junit.Test
    public void testGetGeneralSettingsvalid() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        RemoveGeneralSettings(TestKey, TestName);
        InsertGeneralSettings(TestKey, TestName, TestValue);
        System.out.println("testGetGeneralSettingsnc");
        
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
    }

    @org.junit.Test
    public void testGetGeneralSettingsvalidAgent() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
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
    }


    @org.junit.Test
    public void testGetGeneralSettingsvalid2() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        RemoveGeneralSettings(TestKey, TestName);
        InsertGeneralSettings(TestKey, TestName, TestValue);
        System.out.println("testGetGeneralSettings2");
        
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
        }}
    }


    @org.junit.Test
    public void testRemoveGeneralSettingsValid() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        InsertGeneralSettings(TestKey, TestName, TestValue);
        System.out.println("testRemoveGeneralSettingsValid");
        
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
        }}
    }
    @org.junit.Test
    public void testSetCredentialnClear() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        System.out.println("testSetCredentialnClear");
        
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
        }}
    }

    @org.junit.Test
    public void testSetCredentialsEncrypted() throws Exception {
	 for (int interations=0; interations < RUNS; interations++){
        System.out.println("testSetCredentialsEncrypted");
        
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
            RemoveCredentials();
        } catch (Exception ex) {
            RemoveCredentials();
            ex.printStackTrace();
            fail("unexpected failure");
        }}
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
	 for (int interations=0; interations < RUNS; interations++){
        System.out.println("testClearCredentials");
        
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
            System.out.println(ex.getMessage());
            fail("unexpected failure");
        }

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
    static String TestKey = "junit";
    static String TestName = "parameter";
    static String TestValue = "somevalue";

}
