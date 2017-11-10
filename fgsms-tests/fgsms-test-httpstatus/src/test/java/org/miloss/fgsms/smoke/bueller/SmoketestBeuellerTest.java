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
package org.miloss.fgsms.smoke.bueller;

import org.miloss.fgsms.smoke.bueller.SmoketestBeueller;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransportAuthenticationStyle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;

/**
 *
 * @author AO
 */
public class SmoketestBeuellerTest {

    public SmoketestBeuellerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
          props = new Properties();
        props.load(new FileInputStream("../test.properties"));

    }

    @AfterClass
    public static void tearDownClass() {
    }
    static Properties props = null;

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
        System.out.println("Test NTLM v1");
	Assume.assumeTrue(props.getProperty("NTLMv1URL")!=null);
	Assume.assumeTrue(props.getProperty("NTLMv1URL").length()>6);
	Assume.assumeTrue(props.getProperty("NTLMv1Username")!=null);
	Assume.assumeTrue(props.getProperty("NTLMv1Password")!=null);
	Assume.assumeTrue(props.getProperty("NTLMv1Computername")!=null);
	
	
        String[] args = null;
        boolean status = SmoketestBeueller.run(props.getProperty("NTLMv1URL"),
                props.getProperty("NTLMv1Username"),
                props.getProperty("NTLMv1Password"),
                TransportAuthenticationStyle.HTTP_NTLM,
                props.getProperty("fgsmsUsername"),
                props.getProperty("fgsmsPassword"),
                props.getProperty("pcsurl"),
                props.getProperty("ssurl"));
        if (!status) {
            fail();
        }
    }

    @Test
    public void testMain2() throws Exception {
        System.out.println("Test NTLM v2");
	Assume.assumeTrue(props.getProperty("NTLMv2URL")!=null);
	Assume.assumeTrue(props.getProperty("NTLMv2URL").length()>6);
	Assume.assumeTrue(props.getProperty("NTLMv2Username")!=null);
	Assume.assumeTrue(props.getProperty("NTLMv2Password")!=null);
	Assume.assumeTrue(props.getProperty("NTLMv2Computername")!=null);
        String[] args = null;
        boolean status = SmoketestBeueller.run(props.getProperty("NTLMv2URL"),
                props.getProperty("NTLMv2Username"),
                props.getProperty("NTLMv2Password"),
                TransportAuthenticationStyle.HTTP_NTLM,
                props.getProperty("fgsmsUsername"),
                props.getProperty("fgsmsPassword"),
                props.getProperty("pcsurl"),
                props.getProperty("ssurl"));
        if (!status) {
            fail();
        }
    }
}

//key=value