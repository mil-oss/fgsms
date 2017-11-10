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

package org.miloss.fgsms.osagent.callbacks;

import org.miloss.fgsms.osagent.callbacks.RemoteAgentCallbackImpl;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusRequestMessage;
import org.miloss.fgsms.services.interfaces.common.GetOperatingStatusResponseMessage;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.osagent.OSAgent;
import static org.junit.Assert.*;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author AO
 */
public class RemoteAgentCallbackImplTest {

    public RemoteAgentCallbackImplTest() {
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }
    /*
     @org.junit.Test
     public void testExecuteTasks() throws Exception {
     System.out.println("executeTasks");
     String authorizationcode = "";
     String id = "";
     String workingDir = "";
     String command = "";
     boolean waitforexit = false;
     RemoteAgentCallbackImpl instance = null;
     boolean expResult = false;
     boolean result = instance.executeTasks(authorizationcode, id, workingDir, command, waitforexit);
     assertEquals(expResult, result);
     fail("The test case is a prototype.");
     }*/

    /*
     @org.junit.Test
     public void testRun() {
     System.out.println("run");
     RemoteAgentCallbackImpl instance = null;
     instance.run();
     fail("The test case is a prototype.");
     }*/
    @org.junit.Test
    public void testGetOperatingStatus() throws Exception {
        System.out.println("getOperatingStatus");
        GetOperatingStatusRequestMessage res = new GetOperatingStatusRequestMessage();
        res.setClassification(new SecurityWrapper(ClassificationType.U, ""));
        RemoteAgentCallbackImpl instance = new RemoteAgentCallbackImpl(new OSAgent());
        GetOperatingStatusResponseMessage operatingStatus = instance.getOperatingStatus(res);

        assertNotNull(operatingStatus.getClassification());
        assertNotNull(operatingStatus.getClassification().getClassification());
        assertTrue(operatingStatus.isStatus());
        assertNotNull(operatingStatus.getStartedAt());
        assertNotNull(operatingStatus.getDataSentSuccessfully());
        assertNotNull(operatingStatus.getDataNotSentSuccessfully());
        assertTrue(operatingStatus.getDataSentSuccessfully() >= 0);
        assertTrue(operatingStatus.getDataSentSuccessfully() >= 0);
        assertNotNull(operatingStatus.getVersionInfo());
        assertNotNull(operatingStatus.getVersionInfo().getVersionData());
        assertNotNull(operatingStatus.getVersionInfo().getVersionSource());
        System.out.println("Agent reports version " +operatingStatus.getVersionInfo().getVersionData() + " from source " +  operatingStatus.getVersionInfo().getVersionSource());


    }

    @org.junit.Test
    public void testGetOperatingStatusNullClass() throws Exception {
        System.out.println("testGetOperatingStatusNullClass");
        GetOperatingStatusRequestMessage res = new GetOperatingStatusRequestMessage();
        //res.setClassification(new SecurityWrapper(ClassificationType.U, ""));
        RemoteAgentCallbackImpl instance = new RemoteAgentCallbackImpl(new OSAgent());
        try {
            GetOperatingStatusResponseMessage operatingStatus = instance.getOperatingStatus(res);
            fail("unexpected success");
        } catch (Exception ex) {
        }

    }
}
