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

package org.miloss.fgsms.osagent;

import java.util.List;
import org.miloss.fgsms.osagent.OSAgent;
import javax.xml.datatype.DatatypeConfigurationException;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.miloss.fgsms.services.interfaces.common.DriveInformation;
import org.miloss.fgsms.services.interfaces.policyconfiguration.MachineInformation;

/**
 *
 * @author AO
 */
public class OSAgentTest {

    public OSAgentTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testStartup() throws DatatypeConfigurationException {
        System.out.println("testStartup");
        OSAgent instance = new OSAgent();
    }

    @Test
    public void testRuntime() throws Exception {
        System.out.println("GatherInformation");

        MachineInformation GatherInformation = OSAgent.gatherInformation();
        assertNotNull(GatherInformation);
        assertNotNull(GatherInformation.getHostname());
        assertNotNull(GatherInformation.getDomain());
        assertNotNull(GatherInformation.getOperatingsystem());
        assertNotNull(GatherInformation.getPropertyPair());
        assertFalse(GatherInformation.getPropertyPair().isEmpty());
        assertFalse(GatherInformation.getDriveInformation().isEmpty());
    }

    @Test
    public void testGetDriveInfo() throws Exception {
        System.out.println("GetDriveInfo");

        //    OSAgent instance = new OSAgent();
        List<DriveInformation> GetDriveInfo = OSAgent.getDriveInfo();
        assertNotNull(GetDriveInfo);
        for (int i = 0; i < GetDriveInfo.size(); i++) {
            System.out.println(GetDriveInfo.get(i).getPartition());
            System.out.println(GetDriveInfo.get(i).getSystemid());
            System.out.println(GetDriveInfo.get(i).getId());
            System.out.println(GetDriveInfo.get(i).getTimestamp().toString());
            System.out.println(GetDriveInfo.get(i).getFreespace());
            assertNotNull(GetDriveInfo.get(i).getPartition());
        }
    }

    @Test
    public void testGetClassLevelAsCopy() throws DatatypeConfigurationException {
        System.out.println("getClassLevelAsCopy");
        OSAgent instance = new OSAgent();
        SecurityWrapper classlevel = instance.getClasslevel();
        assertNotNull(classlevel);
        assertNotNull(classlevel.getCaveats());
        assertNotNull(classlevel.getClassification());
        
        SecurityWrapper result = instance.getClassLevelAsCopy();
        assertNotNull(result);//Equals(expResult, result);
        
        result = instance.getClassLevelAsCopy();
        assertEquals(result.getCaveats(), classlevel.getCaveats());
        assertEquals(result.getClassification(), classlevel.getClassification());

    }
}
