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
package org.miloss.fgsms.smoke.auth;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.Assert.*;
import org.miloss.fgsms.smoketest.common.Tools;

/**
 * assumes username/password auth
 *
 * @author AO
 */
public class SmokeTestAuthTest {
    Properties prop = new Properties();
    public SmokeTestAuthTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        
        prop.load(new FileInputStream(new File("../test.properties")));
        Tools.waitForServer(prop.getProperty("server"));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class SmokeTestAuth.
     */
    @Test
    public void testMain() throws Exception {
    
    }

    /**
     * Test of TestQuartz method, of class SmokeTestAuth.
     */
    @Test
    public void testTestServiceEndpoint() throws Exception {
        SmokeTestAuth instance = new SmokeTestAuth();
        int result = instance.testAuth(prop.getProperty("dasurl")+"?wsdl", null,null );
        assertNotEquals("should have failed", 200, result );
    }
    
    @Test
    public void testTestServiceEndpointWithAuth() throws Exception {
        SmokeTestAuth instance = new SmokeTestAuth();
        int result = instance.testAuth(prop.getProperty("dasurl")+"?wsdl", prop.getProperty("fgsmsadminuser"),prop.getProperty("fgsmsadminpass"));
        assertEquals("should have succeeded", 200, result );
    }
    
    @Test
    public void testTestServerEndpoint() throws Exception {
        SmokeTestAuth instance = new SmokeTestAuth();
        int result = instance.testAuth(prop.getProperty("server"), null,null );
        assertEquals("should have succeeded", 200, result );
    }
    
    
    
}
