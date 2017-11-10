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

import java.io.File;
import java.io.FileInputStream;
import org.miloss.fgsms.sla.SLACommon;
import org.miloss.fgsms.sla.NonTransactionalSLAProcessor;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.naming.Context;
import static org.junit.Assert.*;
import org.postgresql.ds.PGPoolingDataSource;

/**
 *
 * @author AO
 */
public class NonTransactionalSLAProcessorTest  {
    public NonTransactionalSLAProcessorTest() throws Exception {
         
    }
    
     protected static PGPoolingDataSource ds =null;

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
         
      
        File f = new File("../resources/test-database.properties");
        Properties db = new Properties();
        if (f.exists()) {
            FileInputStream fis = new FileInputStream(f);
            db.load(fis);
            fis.close();
        } else {
            f = new File("../../resources/test-database.properties");
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                db.load(fis);
                fis.close();
            } else {
                fail("cannot load test-database.properties");
            }
        }
        
        
        
       // db.store(System.out, "none");
        try {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryForTest.class.getName());
            // Construct DataSource 
            //jdbc:postgresql://localhost:5432/fgsmsPerformance
            ds = new PGPoolingDataSource();
            ds.setMaxConnections(Integer.parseInt((String) db.get("perf_maxconnections")));
            ds.setServerName((String) db.get("perf_servername"));
            ds.setPortNumber(Integer.parseInt((String) db.get("perf_port")));
            ds.setDatabaseName((String) db.get("perf_db"));
            ds.setUser((String) db.get("perf_user"));
            ds.setPassword((String) db.get("perf_password"));
            ds.setSsl((Boolean) Boolean.parseBoolean((String) db.get("perf_ssl")));
            ds.setInitialConnections(Integer.parseInt((String) db.get("perf_initialconnections")));
            System.out.println("Binding to " + ds.getServerName() + ":" + ds.getPortNumber());
            InitialContextFactoryForTest.bind("java:fgsmsPerformance", ds);

            ds = new PGPoolingDataSource();
            ds.setMaxConnections(Integer.parseInt((String) db.get("config_maxconnections")));
            ds.setServerName((String) db.get("config_servername"));
            ds.setPortNumber(Integer.parseInt((String) db.get("config_port")));
            ds.setDatabaseName((String) db.get("config_db"));
            ds.setUser((String) db.get("config_user"));
            ds.setPassword((String) db.get("config_password"));
            ds.setSsl((Boolean) Boolean.parseBoolean((String) db.get("config_ssl")));
            ds.setInitialConnections(Integer.parseInt((String) db.get("config_initialconnections")));
            System.out.println("Binding to " + ds.getServerName() + ":" + ds.getPortNumber());
            InitialContextFactoryForTest.bind("java:fgsmsConfig", ds);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }
    

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
        if (ds != null) {
            try {
                ds.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Test of run method, of class NonTransactionalSLAProcessor.
     */
    @org.junit.Test
    public void testRun() {
        System.out.println("run");
        long lastprocessedat = System.currentTimeMillis() - 1000000;
        NonTransactionalSLAProcessor instance = new NonTransactionalSLAProcessor();
        instance.run(true);
//TODO come up with some interesting test cases
    }

    /**
     * Test of GetHostName method, of class NonTransactionalSLAProcessor.
     */
    @org.junit.Test
    public void testGetHostName() {
        try {
            System.out.println("GetHostName");
            String expResult = Inet4Address.getLocalHost().getHostName().toLowerCase();
            String result = SLACommon.GetHostName();
            assertEquals(expResult, result);
        } catch (UnknownHostException ex) {
           System.out.println("exception caught determining hostname"  + ex.getMessage());
        }
    }

    /**
     * Test of sendTest method, of class NonTransactionalSLAProcessor.
     */
    @org.junit.Test
    public void testSendTest() {
        System.out.println("sendTest");
        String email = "fgsms@localhost.localdomain";
        NonTransactionalSLAProcessor instance = new NonTransactionalSLAProcessor();
        boolean expResult = true;
        boolean result = instance.sendTest(email);
        if (!result)
            System.out.println("ERROR, could not send email alert. It's probably because a mail server isn't running on the machine running the test.");
      //  assertEquals(expResult, result);
    }
}
