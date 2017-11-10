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

package org.miloss.fgsms.common;

import org.miloss.fgsms.common.AuditLogger;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.naming.Context;
import javax.xml.ws.handler.MessageContext;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import us.gov.ic.ism.v2.ClassificationType;
import static org.junit.Assert.*;
import org.postgresql.ds.PGPoolingDataSource;

/**
 *
 * @author *
 */
public class AuditLoggerTest {

    public AuditLoggerTest() throws Exception {
        File f = new File("../resources/test-database.properties");
        Properties db = new Properties();
        if (f.exists()) {
            FileInputStream fis = new FileInputStream(f);
            db.load(fis);
            fis.close();
        } else {
            fail("cannot load test-database.properties");
        }
        try {
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryForTest.class.getName());
            // Construct DataSource 
            //jdbc:postgresql://localhost:5432/fgsmsPerformance
            PGPoolingDataSource ds = new PGPoolingDataSource();
            ds.setMaxConnections(Integer.parseInt((String) db.get("perf_maxconnections")));
            ds.setServerName((String) db.get("perf_servername"));
            ds.setPortNumber(Integer.parseInt((String) db.get("perf_port")));
            ds.setDatabaseName((String) db.get("perf_db"));
            ds.setUser((String) db.get("perf_user"));
            ds.setPassword((String) db.get("perf_password"));
            ds.setSsl((Boolean) Boolean.parseBoolean((String) db.get("perf_ssl")));
            ds.setInitialConnections(Integer.parseInt((String) db.get("perf_initialconnections")));
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
            InitialContextFactoryForTest.bind("java:fgsmsConfig", ds);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of logItem method, of class AuditLogger.
     */
    @Test
    public void testLogItem_6args_1() {
        //System.out.println("Audit Logger cannot be tested without Jboss");
        String classname = "something.class";
        String method = "Junit Test";
        String username = "Bob";
        String memo = "jUnit tets";
        SecurityWrapper classification = new SecurityWrapper(ClassificationType.U, "none");
        MessageContext messageContext = null;
        AuditLogger.logItem(classname, method, username, memo, classification, messageContext);
    }
    
    @Test
    public void testLogItem_6args_2() {
        //System.out.println("Audit Logger cannot be tested without Jboss");
        String classname = "something.class";
        String method = "Junit Test";
        String username = "Bob";
        String memo = "jUnit tets";
        SecurityWrapper classification = new SecurityWrapper(ClassificationType.U, "none");
        MessageContext messageContext = null;
        AuditLogger.logItem(null, method, username, memo, classification, messageContext);
    }
    
    @Test
    public void testLogItem_6args_3() {
        //System.out.println("Audit Logger cannot be tested without Jboss");
        String classname = "something.class";
        String method = "Junit Test";
        String username = "Bob";
        String memo = "jUnit tets";
        SecurityWrapper classification = new SecurityWrapper(ClassificationType.U, "none");
        MessageContext messageContext = null;
        AuditLogger.logItem(null, null, username, memo, classification, messageContext);
    }
    
    
    @Test
    public void testLogItem_6args_4() {
        //System.out.println("Audit Logger cannot be tested without Jboss");
        String classname = "something.class";
        String method = "Junit Test";
        String username = "Bob";
        String memo = "jUnit tets";
        SecurityWrapper classification = new SecurityWrapper(ClassificationType.U, "none");
        MessageContext messageContext = null;
        AuditLogger.logItem(null, null, username, memo, classification, null);
    }
    
    @Test
    public void testLogItem_6args_5() {
        //System.out.println("Audit Logger cannot be tested without Jboss");
        String classname = "something.class";
        String method = "Junit Test";
        String username = "Bob";
        String memo = "jUnit tets";
        SecurityWrapper classification = new SecurityWrapper(ClassificationType.U, "none");
        MessageContext messageContext = null;
        AuditLogger.logItem(null, null, username, memo, classification,  new MyMessageContext());
    }
}
