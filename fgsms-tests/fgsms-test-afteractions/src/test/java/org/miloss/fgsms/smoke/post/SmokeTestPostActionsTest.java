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
package org.miloss.fgsms.smoke.post;

import org.miloss.fgsms.smoke.post.SmokeTestPostActions;
import java.io.FileInputStream;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.miloss.fgsms.test.WebServiceBaseTests;

/**
 * This test suite looks for records in the database that shouldn't be there at
 * this stage in the testing process, such as DCS "AddData" records
 *
 * @author AO
 */
public class SmokeTestPostActionsTest extends WebServiceBaseTests {

     public SmokeTestPostActionsTest() throws Exception {
	  super();
	  url = "http://localhost:9999/doesnt-matter";
	  Init();
     }

     static Properties props;

     @BeforeClass
     public static void setUpClass() throws Exception {
	  props = new Properties();
	  props.load(new FileInputStream("../test.properties"));

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
     public void testCheckForDCSAddDataRecords() throws Exception {
	  System.out.println("testCheckForDCSAddDataRecords");
	  boolean expResult = true;
	  boolean result = SmokeTestPostActions.CheckForDCSAddDataRecords();
	  assertEquals(expResult, result);

     }

     @Test
     public void testCheckForDCSAddMoreDataRecords() throws Exception {
	  System.out.println("testCheckForDCSAddMoreDataRecords");
	  boolean expResult = true;
	  boolean result = SmokeTestPostActions.CheckForDCSAddMoreDataRecords();
	  assertEquals(expResult, result);

     }

     @Test
     public void testCheckForDCSRegistration() throws Exception {
	  System.out.println("testCheckForDCSRegistration");
	  boolean expResult = true;
	  boolean result = SmokeTestPostActions.CheckForDCSRegistration();
	  assertEquals(expResult, result);

     }

     @Test
     @Ignore    //FIXME disabled until i can figure out how to get opstat working again
     public void testCheckAPIDocDeployment() throws Exception {
	  System.out.println("testCheckAPIDocDeployment");
	  boolean expResult = true;
	  boolean result = SmokeTestPostActions.testCheckAPIDocDeployment();
	  assertEquals(expResult, result);

     }

     @Test
     @Ignore    //FIXME disabled until i can figure out how to get opstat working again
     public void testOpStatusEndpointARS() throws Exception {
	  System.out.println("testOpStatusEndpointARS");
	  boolean expResult = true;

	  boolean result = new SmokeTestPostActions().testOpStat(props.getProperty("arsurl_opstat"));
	  System.out.println("result was " + result);
	  assertEquals(expResult, result);

     }

     @Test
     @Ignore    //FIXME disabled until i can figure out how to get opstat working again
     public void testOpStatusEndpointRS() throws Exception {
	  System.out.println("testOpStatusEndpointRS");
	  boolean expResult = true;
	  boolean result = new SmokeTestPostActions().testOpStat(props.getProperty("rsurl_opstat"));
	  System.out.println("result was " + result);
	  assertEquals(expResult, result);
     }

     @Test
     @Ignore    //FIXME disabled until i can figure out how to get opstat working again
     public void testOpStatusEndpointPCS() throws Exception {
	  System.out.println("testOpStatusEndpointPCS");
	  boolean expResult = true;
	  boolean result = new SmokeTestPostActions().testOpStat(props.getProperty("pcsurl_opstat"));
	  System.out.println("result was " + result);
	  assertEquals(expResult, result);
     }

     @Test
     @Ignore    //FIXME disabled until i can figure out how to get opstat working again
     public void testOpStatusEndpointDCS() throws Exception {
	  System.out.println("testOpStatusEndpointDCS");
	  boolean expResult = true;
	  boolean result = new SmokeTestPostActions().testOpStat(props.getProperty("dcsurl_opstat"));
	  System.out.println("result was " + result);
	  assertEquals(expResult, result);
     }

     @Test
     @Ignore    //FIXME disabled until i can figure out how to get opstat working again
     public void testOpStatusEndpointSS() throws Exception {
	  System.out.println("testOpStatusEndpointSS");
	  boolean expResult = true;
	  boolean result = new SmokeTestPostActions().testOpStat(props.getProperty("ssurl_opstat"));
	  System.out.println("result was " + result);
	  assertEquals(expResult, result);
     }

     @Test
     @Ignore    //FIXME disabled until i can figure out how to get opstat working again
     public void testOpStatusEndpointDAS() throws Exception {
	  System.out.println("testOpStatusEndpointDAS");
	  boolean expResult = true;
	  boolean result = new SmokeTestPostActions().testOpStat(props.getProperty("dasurl_opstat"));
	  System.out.println("result was " + result);
	  assertEquals(expResult, result);
     }
}
