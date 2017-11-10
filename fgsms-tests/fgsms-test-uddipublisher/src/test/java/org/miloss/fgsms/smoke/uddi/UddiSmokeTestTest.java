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
package org.miloss.fgsms.smoke.uddi;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;

/**
 *
 * @author AO
 */
public class UddiSmokeTestTest {

    public UddiSmokeTestTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    static Properties p = null;

    static {
        try {
            p = new Properties();
            p.load(new FileInputStream(new File("../test.properties")));
        } catch (Exception ex) {
            Logger.getLogger(UddiSmokeTestTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @org.junit.Test
    public void testUddiPublisherWebService_MSUDDI() throws Exception {
        System.out.println("testUddiPublisherWebService_MSUDDI");

        Properties p = new Properties();
        FileInputStream fs = new FileInputStream(new File("../test.properties"));
        p.load(fs);
        fs.close();

        if (p.containsKey("uddi.1.inquiry")) {
            p.remove("uddi.inquiry");
            p.remove("uddi.publish");
            p.remove("uddi.security");
            p.remove("uddi.clientcert");
            p.remove("uddi.username");
            p.remove("uddi.password");
            p.remove("uddi.authMode");

            p.setProperty("uddi.inquiry", p.getProperty("uddi.1.inquiry"));
            p.setProperty("uddi.publish", p.getProperty("uddi.1.publish"));
            p.setProperty("uddi.security", p.getProperty("uddi.1.security"));
            p.setProperty("uddi.clientcert", p.getProperty("uddi.1.clientcert"));
            p.setProperty("uddi.username", p.getProperty("uddi.1.username"));
            p.setProperty("uddi.password", p.getProperty("uddi.1.password"));
            p.setProperty("uddi.authMode", p.getProperty("uddi.1.authMode"));
        } else {
            Assume.assumeTrue("alter ms uddi urls are no defined", false);
        }

        org.miloss.fgsms.smoketest.common.Tools.waitForServer(p.getProperty("uddi.inquiry") + "?wsdl");
        UddiSmokeTest instance = new UddiSmokeTest();

        Pair<String, Integer> result = instance.DoUddiTest(p, true);
        System.out.println("testUddiPublisherWebService_MSUDDI complete, sleeping 5 minutes to clear settings");
        //Thread.sleep(300000);
        if (result.two != 0) {
            fail("unexpected failure " + result.one + " code " + result.two);
        }
    }

    @org.junit.Test
    public void testUddiPublisherWebService_JUDDI() throws Exception {
        System.out.println("testUddiPublisherWebService_JUDDI");
        UddiSmokeTest instance = new UddiSmokeTest();

        Properties p = new Properties();
        FileInputStream fs = new FileInputStream(new File("../test.properties"));
        p.load(fs);
        fs.close();
        
        org.miloss.fgsms.smoketest.common.Tools.waitForServer(p.getProperty("uddi.inquiry") + "?wsdl");
        Pair<String, Integer> result = instance.DoUddiTest(p, false);
        System.out.println("testUddiPublisherWebService_JUDDI complete, sleeping 5 minutes to clear settings");
        //Thread.sleep(300000);

        if (result.two != 0) {
            fail("unexpected failure " + result.one + " code " + result.two);
        }

    }

}
