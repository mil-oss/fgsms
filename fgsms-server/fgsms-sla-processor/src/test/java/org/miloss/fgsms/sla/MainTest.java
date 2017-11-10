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

package org.miloss.fgsms.sla;

import org.miloss.fgsms.sla.SLACommon;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author AO
 */
public class MainTest {
    
    public MainTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of main method, of class Main.
     */
    @org.junit.Test
    public void testMain() throws Exception {
     /*   System.out.println("main");
        String[] args = new String[2];
        args[0] ="SENDTEST";
        args[1]="fgsms@localhost.localdomain";
        Main m = new Main();
       int result = m.run(args);
        
        if (result != 0)
            fail("SLA processor send mail test failed");
       */
    }
    
    @org.junit.Test
    public void testResourceLoading() throws Exception {
        String bundleString = SLACommon.getBundleString("MachinePolicyNull");
        System.out.append(bundleString);
        assertNotNull(bundleString);
    }
}
