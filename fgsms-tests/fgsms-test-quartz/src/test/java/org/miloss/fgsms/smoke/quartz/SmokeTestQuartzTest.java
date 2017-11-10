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
package org.miloss.fgsms.smoke.quartz;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.miloss.fgsms.smoketest.common.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 *
 * @author AO
 */
public class SmokeTestQuartzTest {
    
    public SmokeTestQuartzTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        Properties prop = new Properties();
        prop.load(new FileInputStream(new File("../test.properties")));
        Tools.waitForServer(prop.getProperty("server"));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class SmokeTestQuartz.
     */
    @Test
    public void testMain() throws Exception {
    
    }

    /**
     * Test of TestQuartz method, of class SmokeTestQuartz.
     */
    @Test
    public void testTestQuartz() throws Exception {
        System.out.println("Test if Quartz is running and configured correctly");
        String url = "/admin/quartz-xml.jsp";

        Properties p = new Properties();
        p.load(new FileInputStream("../test.properties"));

        SmokeTestQuartz instance = new SmokeTestQuartz();

        String result = instance.TestQuartz(p.getProperty("server") +url, p.getProperty("fgsmsUsername"),p.getProperty("fgsmsPassword") );
        if (result.length() > 0) {
            System.out.println("initial attempt failed " + result);
            Thread.sleep(10000);
            result = instance.TestQuartz(p.getProperty("server") +url, p.getProperty("fgsmsUsername"),p.getProperty("fgsmsPassword") );
            System.out.println("second attempt " + result);
        }
        assertTrue(result, result.length()==0);
        
    }
    
    
    
}
