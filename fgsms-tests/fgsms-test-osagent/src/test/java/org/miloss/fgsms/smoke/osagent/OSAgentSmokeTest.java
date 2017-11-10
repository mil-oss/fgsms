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
package org.miloss.fgsms.smoke.osagent;

import java.io.FileInputStream;
import java.util.Properties;
import javax.xml.bind.JAXB;
import javax.xml.ws.BindingProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.miloss.fgsms.agentcore.ConfigLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.osagent.OSAgent;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService_Service;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetAgentTypesRequestMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetAgentTypesResponseMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentMachineDataRequestMsg;
import org.miloss.fgsms.services.interfaces.dataaccessservice.GetMostRecentMachineDataResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.GetProcessesListByMachineResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachinePolicy;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.ServicePolicyResponseMsg;
import org.miloss.fgsms.services.interfaces.policyconfiguration.SetServicePolicyRequestMsg;
import org.miloss.fgsms.services.interfaces.status.StatusService;

/**
 *
 * @author AO
 */
public class OSAgentSmokeTest {

    static Properties props = null;
    static ConfigLoader cfg;
    static StatusService ssport;
    static PCS pcsport;
    static DataAccessService dasPort;

    public OSAgentSmokeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        props = new Properties();
        props.load(new FileInputStream("../test.properties"));
        cfg = new ConfigLoader();
        pcsport = cfg.getPcsport();
        ((BindingProvider) pcsport).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getPCS_URLS().get(0));
        ((BindingProvider) pcsport).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, props.getProperty("fgsmsadminuser"));
        ((BindingProvider) pcsport).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, props.getProperty("fgsmsadminpass"));
        ssport = cfg.getSsport();
        ((BindingProvider) ssport).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, cfg.getSS_URLS().get(0));
        ((BindingProvider) ssport).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, props.getProperty("fgsmsadminuser"));
        ((BindingProvider) ssport).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, props.getProperty("fgsmsadminpass"));
        dasPort = new DataAccessService_Service().getDASPort();
        ((BindingProvider) dasPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, props.getProperty("dasurl"));
        ((BindingProvider) dasPort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, props.getProperty("fgsmsadminuser"));
        ((BindingProvider) dasPort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, props.getProperty("fgsmsadminpass"));
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class SmoketestBeueller.
     */
    @Test
    public void testMain() throws Exception {
        //connect to FGSMS as admin
        String GetHostName = Utility.getHostName();
        String expectedUrl = "urn:" + GetHostName + ":system";

        //confirm the OS agent is listed
        GetAgentTypesRequestMsg agentTypesRequestMsg = new GetAgentTypesRequestMsg();
        agentTypesRequestMsg.setClassification(new SecurityWrapper());
        GetAgentTypesResponseMsg agentTypes = dasPort.getAgentTypes(agentTypesRequestMsg);
        Assert.assertTrue("agent not registered", agentTypes.getAgentType().contains(OSAgent.class.getCanonicalName()));

        //fetch the policy, confirm it valid and of the right type
        ServicePolicyRequestMsg servicePolicyRequestMsg = new ServicePolicyRequestMsg();
        servicePolicyRequestMsg.setClassification(new SecurityWrapper());
        servicePolicyRequestMsg.setURI(expectedUrl);
        ServicePolicyResponseMsg servicePolicy = pcsport.getServicePolicy(servicePolicyRequestMsg);
        Assert.assertNotNull("null policy", servicePolicy.getPolicy());
        Assert.assertTrue("policy should be of type MachinePolicy but is of type " + servicePolicy.getPolicy().getClass().getCanonicalName(), servicePolicy.getPolicy() instanceof MachinePolicy);

        //get the current config
        GetProcessesListByMachineRequestMsg req = new GetProcessesListByMachineRequestMsg();
        req.setHostname(GetHostName);
        req.setClassification(new SecurityWrapper());
        GetProcessesListByMachineResponseMsg res = pcsport.getProcessesListByMachine(req);
        JAXB.marshal(res, System.out);

        //tell it to monitor everything
        MachinePolicy policy = (MachinePolicy) servicePolicy.getPolicy();
        policy.setRecordCPUusage(true);
        policy.setRecordMemoryUsage(true);
        for (int i = 0; i < res.getMachineInformation().getDriveInformation().size(); i++) {
            policy.getRecordDiskSpace().add(res.getMachineInformation().getDriveInformation().get(i).getPartition());
            policy.getRecordDiskUsagePartitionNames().add(res.getMachineInformation().getDriveInformation().get(i).getPartition());
        }

        for (int i = 0; i < res.getMachineInformation().getAddresses().size(); i++) {
            policy.getRecordNetworkUsage().add(res.getMachineInformation().getAddresses().get(i).getAdapterName());
        }

        SetServicePolicyRequestMsg setpol = new SetServicePolicyRequestMsg();
        setpol.setClassification(new SecurityWrapper());
        setpol.setURL(policy.getURL());
        setpol.setPolicy(policy);
        pcsport.setServicePolicy(setpol);

        System.out.println("sleeping for 120 seconds to wait for the os agent to check in, get the new config, then report back with the data");
        Thread.sleep(120000);
        //query to retrieve the data, as long as we get back a response we should 
        //be good to go
        GetMostRecentMachineDataRequestMsg mostRecentMachineDataRequestMsg = new GetMostRecentMachineDataRequestMsg();
        mostRecentMachineDataRequestMsg.setClassification(new SecurityWrapper());
        mostRecentMachineDataRequestMsg.setUri(expectedUrl);

        GetMostRecentMachineDataResponseMsg mostRecentMachineData = dasPort.getMostRecentMachineData(mostRecentMachineDataRequestMsg);
        Assert.assertNotNull(mostRecentMachineData.getMachineData());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getDomainName());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData().getBytesusedMemory());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData().getNumberofActiveThreads());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData().getTimestamp());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData().getStatusmessage());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData().getUri());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData().getId());
        Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData().getPercentusedCPU());
        //Assert.assertNotNull(mostRecentMachineData.getMachineData().getMachinePerformanceData().getStartedAt());
        Assert.assertFalse(mostRecentMachineData.getMachineData().getMachinePerformanceData().getNetworkAdapterPerformanceData().isEmpty());
        Assert.assertFalse(mostRecentMachineData.getMachineData().getMachinePerformanceData().getDriveInformation().isEmpty());
    }

}
