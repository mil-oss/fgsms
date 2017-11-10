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
package org.miloss.fgsms.common;

import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.UserIdentityUtil;
import org.miloss.fgsms.common.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.Context;
import javax.xml.ws.handler.MessageContext;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.postgresql.ds.PGPoolingDataSource;

/**
 *
 * @author *
 */
public class UserIdentityUtilTest {

    static String testuser = "testuser";

    public UserIdentityUtilTest() throws Exception {
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
    MyWebServiceContext globaladmin = new MyWebServiceContext(new MyMessageContext(), testuser);
    MyWebServiceContext globalagent = new MyWebServiceContext(new MyMessageContext(), testuser);
    MyWebServiceContext regularuser = new MyWebServiceContext(new MyMessageContext(), testuser);
    MyWebServiceContext globalread = new MyWebServiceContext(new MyMessageContext(), testuser);
    MyWebServiceContext globalwrite = new MyWebServiceContext(new MyMessageContext(), testuser);
    MyWebServiceContext globalaudit = new MyWebServiceContext(new MyMessageContext(), testuser);

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

        globaladmin.roles.add(Constants.ROLES_GLOBAL_ADMINISTRATOR);
        globaladmin.getMessageContext().put(MessageContext.SERVLET_REQUEST, new MyHttpServletRequest(testuser));
        globalread.roles.add(Constants.ROLES_GLOBAL_READ);
        globalread.getMessageContext().put(MessageContext.SERVLET_REQUEST, new MyHttpServletRequest(testuser));
        globalwrite.roles.add(Constants.ROLES_GLOBAL_WRITE);
        globalwrite.getMessageContext().put(MessageContext.SERVLET_REQUEST, new MyHttpServletRequest(testuser));
        globalaudit.roles.add(Constants.ROLES_GLOBAL_AUDITOR);
        globalaudit.getMessageContext().put(MessageContext.SERVLET_REQUEST, new MyHttpServletRequest(testuser));
        globalagent.roles.add(Constants.ROLES_GLOBAL_AGENT);
        globalagent.getMessageContext().put(MessageContext.SERVLET_REQUEST, new MyHttpServletRequest(testuser));
        regularuser.roles.clear();
        regularuser.getMessageContext().put(MessageContext.SERVLET_REQUEST, new MyHttpServletRequest(testuser));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of assertReadAccess method, of class UserIdentityUtil.
     */
    @Test
    public void testAssertReadAccessViaServletContext() throws Exception {
        System.out.println("testAssertReadAccessViaServletContext");

        UserIdentityUtil.assertReadAccess(null, testuser, "testAssertReadAccessViaServletContext", new SecurityWrapper(), globaladmin);

    }

    @Test
    public void testAssertReadAccessViaServletContext2() throws Exception {
        System.out.println("testAssertReadAccessViaServletContext2");

        UserIdentityUtil.assertReadAccess(null, testuser, "testAssertReadAccessViaServletContext", new SecurityWrapper(), globalaudit);

    }

    @Test
    public void testAssertReadAccessViaServletContext3() throws Exception {
        System.out.println("testAssertReadAccessViaServletContext3");

        UserIdentityUtil.assertReadAccess(null, testuser, "testAssertReadAccessViaServletContext", new SecurityWrapper(), globalread);
    }

    @Test
    public void testAssertReadAccessViaServletContext4() throws Exception {
        System.out.println("testAssertReadAccessViaServletContext4");

        UserIdentityUtil.assertReadAccess(null, testuser, "testAssertReadAccessViaServletContext", new SecurityWrapper(), globalwrite);

    }

    @Test(expected = SecurityException.class)
    public void testAssertReadAccessViaServletContext5() throws Exception {
        System.out.println("testAssertReadAccessViaServletContext5");

        UserIdentityUtil.assertReadAccess(null, testuser, "testAssertReadAccessViaServletContext", new SecurityWrapper(), regularuser);

    }

    @Test(expected = SecurityException.class)
    public void testAssertReadAccessViaServletContext6() throws Exception {
        System.out.println("testAssertReadAccessViaServletContext6");

        UserIdentityUtil.assertReadAccess(null, testuser, "testAssertReadAccessViaServletContext6", new SecurityWrapper(), globalagent);

    }

    /**
     * Test of assertWriteAccess method, of class UserIdentityUtil.
     */
    @Test
    public void testAssertWriteAccessViaServletContext() throws Exception {
        System.out.println("testAssertWriteAccessViaServletContext");

        UserIdentityUtil.assertWriteAccess(null, testuser, "testAssertWriteAccessViaServletContext", new SecurityWrapper(), globalaudit);

    }

    @Test
    public void testAssertWriteAccessViaServletContext2() throws Exception {
        System.out.println("testAssertWriteAccessViaServletContext2");

        UserIdentityUtil.assertWriteAccess(null, testuser, "testAssertWriteAccessViaServletContext2", new SecurityWrapper(), globaladmin);

    }

    @Test(expected = SecurityException.class)
    public void testAssertWriteAccessViaServletContext3() throws Exception {
        System.out.println("testAssertWriteAccessViaServletContext3");

        UserIdentityUtil.assertWriteAccess(null, testuser, "testAssertWriteAccessViaServletContext3", new SecurityWrapper(), globalread);
        fail("unexpected success");

    }

    @Test
    public void testAssertWriteAccessViaServletContext4() throws Exception {
        System.out.println("testAssertWriteAccessViaServletContext4");

        UserIdentityUtil.assertWriteAccess(null, testuser, "testAssertWriteAccessViaServletContext4", new SecurityWrapper(), globalwrite);

    }

    @Test(expected = SecurityException.class)
    public void testAssertWriteAccessViaServletContext5() throws Exception {
        System.out.println("testAssertWriteAccessViaServletContext5");

        UserIdentityUtil.assertWriteAccess(null, testuser, "testAssertWriteAccessViaServletContext5", new SecurityWrapper(), globalagent);

    }

    @Test(expected = SecurityException.class)
    public void testAssertWriteAccessViaServletContext6() throws Exception {
        System.out.println("testAssertWriteAccessViaServletContext6");

        UserIdentityUtil.assertWriteAccess(null, testuser, "testAssertWriteAccessViaServletContext6", new SecurityWrapper(), regularuser);
        fail("unexpected success");

    }

    /**
     * Test of assertAuditAccess method, of class UserIdentityUtil.
     */
    @Test(expected = SecurityException.class)
    public void testAssertAuditAccessViaServletContext() throws Exception {
        System.out.println("testAssertAuditAccessViaServletContext");

        UserIdentityUtil.assertAuditAccess(null, testuser, "testAssertAuditAccessViaServletContext", new SecurityWrapper(), regularuser);

    }

    @Test(expected = SecurityException.class)
    public void testAssertAuditAccessViaServletContext2() throws Exception {
        System.out.println("testAssertAuditAccessViaServletContext2");

        UserIdentityUtil.assertAuditAccess(null, testuser, "testAssertAuditAccessViaServletContext2", new SecurityWrapper(), globalread);

    }

    @Test(expected = SecurityException.class)
    public void testAssertAuditAccessViaServletContext3() throws Exception {
        System.out.println("testAssertAuditAccessViaServletContext3");

        UserIdentityUtil.assertAuditAccess(null, testuser, "testAssertAuditAccessViaServletContext2", new SecurityWrapper(), globalwrite);

    }

    @Test(expected = SecurityException.class)
    public void testAssertAuditAccessViaServletContext4() throws Exception {
        System.out.println("testAssertAuditAccessViaServletContext4");

        UserIdentityUtil.assertAuditAccess(null, testuser, "testAssertAuditAccessViaServletContext4", new SecurityWrapper(), globalagent);

    }

    @Test
    public void testAssertAuditAccessViaServletContext5() throws Exception {
        System.out.println("testAssertAuditAccessViaServletContext5");

        UserIdentityUtil.assertAuditAccess(null, testuser, "testAssertAuditAccessViaServletContext5", new SecurityWrapper(), globaladmin);

    }

    @Test
    public void testAssertAuditAccessViaServletContext6() throws Exception {
        System.out.println("testAssertAuditAccessViaServletContext6");

        UserIdentityUtil.assertAuditAccess(null, testuser, "testAssertAuditAccessViaServletContext6", new SecurityWrapper(), globalaudit);

    }

    /**
     * Test of assertAdministerAccess method, of class UserIdentityUtil.
     */
    @Test(expected = SecurityException.class)
    public void testAssertAdministerAccessViaServletContext() throws Exception {
        System.out.println("testAssertAdministerAccessViaServletContext");

        UserIdentityUtil.assertAdministerAccess(null, testuser, "testAssertAdministerAccessViaServletContext", new SecurityWrapper(), globalaudit);

    }

    @Test(expected = SecurityException.class)
    public void testAssertAdministerAccessViaServletContext2() throws Exception {
        System.out.println("testAssertAdministerAccessViaServletContext2");

        UserIdentityUtil.assertAdministerAccess(null, testuser, "testAssertAdministerAccessViaServletContext2", new SecurityWrapper(), globalagent);

    }

    @Test(expected = SecurityException.class)
    public void testAssertAdministerAccessViaServletContext3() throws Exception {
        System.out.println("testAssertAdministerAccessViaServletContext3");

        UserIdentityUtil.assertAdministerAccess(null, testuser, "testAssertAdministerAccessViaServletContext3", new SecurityWrapper(), globalread);

    }

    @Test(expected = SecurityException.class)
    public void testAssertAdministerAccessViaServletContext4() throws Exception {
        System.out.println("testAssertAdministerAccessViaServletContext4");

        UserIdentityUtil.assertAdministerAccess(null, testuser, "testAssertAdministerAccessViaServletContext4", new SecurityWrapper(), globalwrite);

    }

    @Test(expected = SecurityException.class)
    public void testAssertAdministerAccessViaServletContext5() throws Exception {
        System.out.println("testAssertAdministerAccessViaServletContext5");
        UserIdentityUtil.assertAdministerAccess(null, testuser, "testAssertAdministerAccessViaServletContext5", new SecurityWrapper(), regularuser);

    }

    @Test
    public void testAssertAdministerAccessViaServletContext6() throws Exception {
        System.out.println("testAssertAdministerAccessViaServletContext6");

        UserIdentityUtil.assertAdministerAccess(null, testuser, "testAssertAdministerAccessViaServletContext6", new SecurityWrapper(), globaladmin);

    }

    /**
     * Test of assertGlobalAdministratorRole method, of class UserIdentityUtil.
     */
    @Test(expected = SecurityException.class)
    public void testAssertGlobalAdministratorRole() throws Exception {
        System.out.println("testAssertGlobalAdministratorRole");

        UserIdentityUtil.assertGlobalAdministratorRole(null, "testAssertGlobalAdministratorRole", new SecurityWrapper(), null);

    }

    @Test
    public void testAssertGlobalAdministratorRole2() throws Exception {
        System.out.println("testAssertGlobalAdministratorRole2");

        UserIdentityUtil.assertGlobalAdministratorRole("fgsmsadmin", "testAssertGlobalAdministratorRole", new SecurityWrapper(), null);

    }

    @Test(expected = SecurityException.class)
    public void testAssertGlobalAdministratorRoleViaServletContext() throws Exception {
        System.out.println("testAssertGlobalAdministratorRoleViaServletContext");

        UserIdentityUtil.assertGlobalAdministratorRole(testuser, "testAssertGlobalAdministratorRoleViaServletContext", new SecurityWrapper(), regularuser);

    }

    @Test
    public void testAssertGlobalAdministratorRoleViaServletContext2() throws Exception {
        System.out.println("testAssertGlobalAdministratorRoleViaServletContext2");

        UserIdentityUtil.assertGlobalAdministratorRole(testuser, "testAssertGlobalAdministratorRoleViaServletContext2", null, globaladmin);

    }

    @Test(expected = SecurityException.class)
    public void testAssertGlobalAdministratorRoleViaServletContext3() throws Exception {
        System.out.println("testAssertGlobalAdministratorRoleViaServletContext3");

        UserIdentityUtil.assertGlobalAdministratorRole(testuser, "testAssertGlobalAdministratorRoleViaServletContext3", null, globalagent);

    }

    @Test(expected = SecurityException.class)
    public void testAssertGlobalAdministratorRoleViaServletContext4() throws Exception {
        System.out.println("testAssertGlobalAdministratorRoleViaServletContext4");

        UserIdentityUtil.assertGlobalAdministratorRole(testuser, "testAssertGlobalAdministratorRoleViaServletContext4", null, globalread);

    }

    @Test(expected = SecurityException.class)
    public void testAssertGlobalAdministratorRoleViaServletContext5() throws Exception {
        System.out.println("testAssertGlobalAdministratorRoleViaServletContext5");

        UserIdentityUtil.assertGlobalAdministratorRole(testuser, "testAssertGlobalAdministratorRoleViaServletContext5", null, globalwrite);

    }

    @Test(expected = SecurityException.class)
    public void testAssertGlobalAdministratorRoleViaServletContext6() throws Exception {
        System.out.println("testAssertGlobalAdministratorRoleViaServletContext6");

        UserIdentityUtil.assertGlobalAdministratorRole(testuser, "testAssertGlobalAdministratorRoleViaServletContext6", null, globalaudit);

    }

    @Test(expected = SecurityException.class)
    public void testAssertGlobalAdministratorRole3() throws Exception {
        System.out.println("testAssertGlobalAdministratorRole3");

        UserIdentityUtil.assertGlobalAdministratorRole(null, null, null, null);

    }

    void InsertAdminRecord() throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("INSERT INTO users(            username, displayname, email, rolecol)    VALUES (?, ?, ?, ?);");
        com.setString(1, testuser);
        com.setString(2, testuser);
        com.setString(3, testuser);
        com.setString(4, "admin");
        com.execute();
        com.close();
        con.close();

    }

    @Test
    public void testAssertGlobalAdministratorRole4() throws Exception {
        System.out.println("testAssertGlobalAdministratorRole4");
        try {
            InsertAdminRecord();
            UserIdentityUtil.assertGlobalAdministratorRole(testuser, null, null, null);
            RemoveRoleRecord(testuser);

        } catch (Exception ex) {
            ex.printStackTrace();

            RemoveRoleRecord(testuser);
            fail("unexpected failure");
        }
    }

    /**
     * Test of hasGlobalAdministratorRole method, of class UserIdentityUtil.
     */
    @Test
    public void testHasGlobalAdministratorRole() {
    }

    /**
     * Test of getFirstIdentityToString method, of class UserIdentityUtil.
     */
    @Test
    public void testGetFirstIdentityToString() {
        System.out.println("testGetFirstIdentityToString");
        UserIdentityUtil.getFirstIdentityToString(null);
    }

    @Test
    public void testGetFirstIdentityToString2() {
        System.out.println("testGetFirstIdentityToString2");
        UserIdentityUtil.getFirstIdentityToString(new MyWebServiceContext());
    }

    @Test
    public void testGetFirstIdentityToString3() {
        System.out.println("testGetFirstIdentityToString3");
        MyWebServiceContext my = new MyWebServiceContext();
        // MyWebServiceContext wsv = new MyWebServiceContext();
        UserIdentityUtil.getFirstIdentityToString(my);
    }

    @Test
    public void testGetFirstIdentityToString4() {
        System.out.println("testGetFirstIdentityToString4");
        MyWebServiceContext my = new MyWebServiceContext(new MyMessageContext(), testuser);

        my.getMessageContext().put(MessageContext.SERVLET_REQUEST, new MyHttpServletRequest(testuser));
        // MyWebServiceContext wsv = new MyWebServiceContext();
        String user = UserIdentityUtil.getFirstIdentityToString(my);
        assertEquals(user, testuser);
    }

    @Test
    public void AssertAdminOrAgentRole() {
        UserIdentityUtil.assertAdminOrAgentRole("fgsmsadmin", "unittest", null, null);
    }

    @Test
    public void AssertAdminOrAgentRole2() throws Exception {
        try {
            UserIdentityUtil.assertAdminOrAgentRole("bob", "unittest", null, null);
            fail("unexpeced success");
        } catch (Exception ex) {
        }
    }

    private void RemoveRoleRecord(String testuser) throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        PreparedStatement com = con.prepareStatement("delete from users where username=?");
        com.setString(1, testuser);
        com.execute();
        com.close();
        con.close();

    }
}
