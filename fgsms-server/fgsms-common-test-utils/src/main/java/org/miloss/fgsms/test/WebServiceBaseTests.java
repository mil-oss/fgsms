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
package org.miloss.fgsms.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.UUID;
import javax.naming.Context;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.handler.MessageContext;
import org.miloss.fgsms.common.IpAddressUtility;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.FederationPolicyCollection;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransactionalWebServicePolicy;
import org.junit.*;
import static org.junit.Assert.*;
import org.miloss.fgsms.common.Constants;
import org.postgresql.ds.PGPoolingDataSource;
import us.gov.ic.ism.v2.ClassificationType;

/**
 *
 * @author AO
 */
public abstract class WebServiceBaseTests {

    protected static String url = null;
    protected static String INVOCATION_URL = null;
    protected static MyWebServiceContext adminctx = null;
    protected static MyWebServiceContext agentctx = null;
    protected static MyWebServiceContext userbobctx = null;
    protected static MyWebServiceContext auditctx = null;
    protected static MyWebServiceContext usermaryctx = null;
    public static String adminusername = "fgsmsadmin";
    public static String auditusername = "fgsmsaudit";
    public static String agentusername = "fgsmsagent";
    public static String bobusername = "jUnitBob";
    public static String maryusername = "jUnitMary";
    public static DatatypeFactory df = null;
    protected static PGPoolingDataSource ds = null;
    private static boolean initialized = false;

    public WebServiceBaseTests() throws Exception {
    }

    public void Init() throws Exception {

        df = DatatypeFactory.newInstance();
        if (INVOCATION_URL == null) {
            INVOCATION_URL = url;
        }
        url = IpAddressUtility.modifyURL(url, false);
        INVOCATION_URL = IpAddressUtility.modifyURL(INVOCATION_URL, false);
        MyMessageContext mc = new MyMessageContext();

        HttpServletRequest req = new MyHttpServletRequest(adminusername);
        mc.put(MessageContext.SERVLET_REQUEST, req);
        adminctx = new MyWebServiceContext(mc, adminusername);

        mc = new MyMessageContext();
        req = new MyHttpServletRequest(agentusername);
        mc.put(MessageContext.SERVLET_REQUEST, req);
        mc.put("javax.xml.ws.service.endpoint.address", INVOCATION_URL);
        agentctx = new MyWebServiceContext(mc, agentusername);

        mc = new MyMessageContext();
        req = new MyHttpServletRequest(bobusername);
        mc.put(MessageContext.SERVLET_REQUEST, req);
        mc.put("javax.xml.ws.service.endpoint.address", INVOCATION_URL);
        userbobctx = new MyWebServiceContext(mc, bobusername);

        mc = new MyMessageContext();
        req = new MyHttpServletRequest(maryusername);
        mc.put(MessageContext.SERVLET_REQUEST, req);
        mc.put("javax.xml.ws.service.endpoint.address", INVOCATION_URL);
        usermaryctx = new MyWebServiceContext(mc, maryusername);

        mc = new MyMessageContext();
        req = new MyHttpServletRequest(auditusername);
        mc.put(MessageContext.SERVLET_REQUEST, req);
        mc.put("javax.xml.ws.service.endpoint.address", INVOCATION_URL);
        auditctx = new MyWebServiceContext(mc, auditusername);

        File f = new File("../resources/test-database.properties");
        Properties db = new Properties();
        if (f.exists()) {
            System.out.println("Loading test properties from " + f.getAbsolutePath());
            FileInputStream fis = new FileInputStream(f);
            db.load(fis);
            fis.close();
        } else {
            f = new File("../../resources/test-database.properties");
            if (f.exists()) {
                System.out.println("Loading test properties from " + f.getAbsolutePath());
                FileInputStream fis = new FileInputStream(f);
                db.load(fis);
                fis.close();
            } else {

                f = new File("../../../resources/test-database.properties");
                if (f.exists()) {
                    System.out.println("Loading test properties from " + f.getAbsolutePath());
                    FileInputStream fis = new FileInputStream(f);
                    db.load(fis);
                    fis.close();
                } else {

                    fail("cannot load test-database.properties");
                }
            }
        }

        if (initialized) {
            return;
        }
        initialized = true;

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

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @After
    public void tearDown() {
        RemoveServicePolicy(url);
        //RemoveServicePolicy(brokerurl);
        //ds.close();
    }

    public static void RemoveServicePolicy(String u) {
        Connection con = Utility.getConfigurationDBConnection();
        Connection perf = Utility.getPerformanceDBConnection();
        try {
            //remove service policy

            PreparedStatement com = con.prepareStatement(
                    "delete from servicepolicies where uri=?; "
                    + "delete from status where uri=?; "
                    + "delete from userpermissions where objecturi=?;"
                    + "delete from status where uri=?;"
                    + "delete from bueller where uri=?;"
                    + "delete from slasub where uri=?");
            com.setString(1, u);
            com.setString(2, u);
            com.setString(3, u);
            com.setString(4, u);
            com.setString(5, u);
            com.setString(6, u);

            com.execute();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                   // System.out.println("attempting config connection close");
                    con.close();
                  //  System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }

        try {
            //remove service policy

            PreparedStatement com = perf.prepareStatement(
                    "delete from rawdata where uri=?; "
                    + "delete from agg2 where uri=?;"
                    + "delete from availability where uri=?;"
                    + "delete from alternateurls where uri=?;"
                    + "delete from actionlist where uri=?;"
                    + "delete from brokerhistory where host=?;"
                    + "delete from brokerrawdata where host=?;"
                    + "delete from rawdata where uri=?;"
                    + "delete from rawdatadrives where uri=?;"
                    + "delete from rawdatamachineprocess  where uri=?;"
                    + "delete from rawdatanic  where uri=?;"
                    + "delete from slaviolations  where uri=?;"
                    + "delete from dependencies  where destinationurl=?;"
                    + "delete from dependencies  where sourceurl=?;"
                    + "delete from rawdatatally where uri=?;");
            com.setString(1, u);
            com.setString(2, u);
            com.setString(3, u);
            com.setString(4, u);
            com.setString(5, u);
            com.setString(6, u);
            com.setString(7, u);
            com.setString(8, u);
            com.setString(9, u);
            com.setString(10, u);
            com.setString(11, u);
            com.setString(12, u);
            com.setString(13, u);
            com.setString(14, u);
            com.setString(15, u);

            com.execute();
            //System.out.println("attempting perf connection close");
            perf.close();
            //System.out.println("perf connection closed");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (!perf.isClosed()) {
                    //System.out.println("attempting perf connection close");
                    perf.close();
                    //System.out.println("perf connection closed");
                }
            } catch (Exception e) {
            }
        }
    }

    public static void CreatePolicy(String uri) {
        CreatePolicy(uri, null);

    }

    public static void CreatePolicy(String uri, FederationPolicyCollection fedpolicies) {
        Connection con = Utility.getConfigurationDBConnection();
        Connection perf = Utility.getPerformanceDBConnection();
        try {

            PreparedStatement com = con.prepareStatement("delete from servicepolicies where uri=?;");
            com.setString(1, uri);
            com.execute();
            com = con.prepareStatement(
                    "INSERT INTO servicepolicies("
                    + "            uri, datattl, " //5
                    + "            buellerenabled, " //3
                    + "            policytype, xmlpolicy,hasfederation)" //1
                    + "    VALUES (?, ?, ?, ?, ?,?);");
            com.setString(1, uri);
            com.setLong(2, 500000);
            com.setBoolean(3, true);
            com.setInt(4, PolicyType.TRANSACTIONAL.ordinal());
            TransactionalWebServicePolicy twp = new TransactionalWebServicePolicy();
            twp.setURL(uri);
            twp.setFederationPolicyCollection(fedpolicies);
            twp.setDescription("jUnit Test Description");
            DatatypeFactory fac = DatatypeFactory.newInstance();
            twp.setDataTTL(fac.newDuration(10000000));
            Marshaller m = Utility.getSerializationContext().createMarshaller();
            StringWriter sw = new StringWriter();
            m.marshal(twp, sw);

            String s = sw.toString();
            com.setBytes(5, s.getBytes(Constants.CHARSET));

            com.setBoolean(6, fedpolicies != null && !fedpolicies.getFederationPolicy().isEmpty());

            com.execute();
            com.close();
            //System.out.println("attempting config connection close");
            con.close();
            //System.out.println("config connection closed");

            com = perf.prepareStatement("INSERT INTO rawdatatally(uri, success, faults, slafault)    VALUES (?, ?, ?, ?);");
            com.setString(1, uri);
            com.setLong(2, 0);
            com.setLong(3, 0);
            com.setLong(4, 0);
            com.execute();
            com.close();
            //System.out.println("attempting perf connection close");
            perf.close();
            //System.out.println("perf connection closed");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                   // System.out.println("attempting config connection close");
                    con.close();
                   // System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }

            try {
                if (!perf.isClosed()) {
                   // System.out.println("attempting perf connection close");
                    perf.close();
                    //System.out.println("perf connection closed");
                }
            } catch (Exception e) {
            }
        }

    }

    public void VerifyPolicyDeleted(String uri) {
        boolean ok = true;
        Connection con = Utility.getConfigurationDBConnection();
        try {

            PreparedStatement com
                    = com = con.prepareStatement("select * from servicepolicies where uri=?;");
            com.setString(1, uri);
            ResultSet rs = com.executeQuery();

            if (rs.next()) {
                ok = false;
            }
            rs.close();
            com.close();
            //System.out.println("attempting config connection close");
            con.close();
           // System.out.println("config connection closed");
        } catch (Exception ex) {
            ex.printStackTrace();
            ok = false;
        } finally {
            try {
                if (!con.isClosed()) {
                   // System.out.println("attempting config connection close");
                    con.close();
                  //  System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }
        if (!ok) {
            fail("policy not deleted");
        }
    }

    public void adduser(String bobusername) throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        try {
            PreparedStatement ps = con.prepareStatement("delete from userpermissions where username=?; delete from slasub where username=?; delete from users where username=?;"
                    + "INSERT INTO users(            username        , displayname        , email       )    VALUES( ?,  ?,  ?);       ");
            ps.setString(1, bobusername);
            ps.setString(2, bobusername);
            ps.setString(3, bobusername);
            ps.setString(4, bobusername);
            ps.setString(5, "junit");
            ps.setString(6, bobusername + "@localhost.localdomain");
            ps.execute();
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                   // System.out.println("attempting config connection close");
                    con.close();
                   // System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }
    }

    //testagenttallies
    public void resetClassLevel() throws SQLException {
        Connection con = Utility.getConfigurationDBConnection();
        try {

            PreparedStatement ps = con.prepareStatement("UPDATE globalpolicies   SET       classification=?, caveat=?");

            ps.setString(1, ClassificationType.U.value());
            ps.setString(2, "");
            ps.execute();
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                    //System.out.println("attempting config connection close");
                    con.close();
                   // System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }
    }

    public String Insert_WS_Transaction(String url, String agent) throws Exception {
        System.out.println("attempting to insert ws transaction for " + url);
        Connection con = Utility.getPerformanceDBConnection();
        try {
            PreparedStatement cmd = con.prepareStatement("delete from rawdata where uri=?");
            cmd.setString(1, url);
            cmd.execute();
            cmd.close();
            cmd = con.prepareStatement("INSERT INTO rawdata(        "
                    + "    uri, responsetimems, monitorsource, hostingsource, requestxml, " //1-5
                    + "            responsexml, consumeridentity, transactionid, soapaction, responsesize, " //6-10
                    + "            requestsize, utcdatetime, success, slafault, agenttype, originalurl, " //11-16
                    + "            message, relatedtransactionid, threadid, requestheaders, responseheaders)" //17-21
                    + "    VALUES (?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?);");
            cmd.setString(1, url);
            String id = UUID.randomUUID().toString();
            cmd.setInt(2, 1000);
            cmd.setString(3, Utility.getHostName());
            cmd.setString(4, Utility.getHostName());
            cmd.setObject(5, Utility.EN(new String("<body>helloworld</body>")).getBytes(Constants.CHARSET));
            cmd.setObject(6, Utility.EN(new String("<body>helloworld response</body>")).getBytes(Constants.CHARSET));
            cmd.setString(7, System.getenv("USERNAME"));
            cmd.setString(8, id);
            cmd.setString(9, "POST");
            cmd.setInt(10, 1024);
            cmd.setInt(11, 1024);
            cmd.setLong(12, System.currentTimeMillis());
            cmd.setBoolean(13, false);
            cmd.setString(14, "sla fault!");
            cmd.setString(15, agent);
            cmd.setString(16, url);
            cmd.setObject(17, "hello from junit".getBytes());
            cmd.setString(18, UUID.randomUUID().toString());
            cmd.setString(19, UUID.randomUUID().toString());
            cmd.setObject(20, Utility.EN(new String("SOAPAction: something")).getBytes(Constants.CHARSET));
            cmd.setObject(21, Utility.EN(new String("Response 200 OK")).getBytes(Constants.CHARSET));
            cmd.execute();
            //System.out.println("successful insert ws transaction for " + url);
            cmd.close();
           // System.out.println("attempting config connection close");
            con.close();
           // System.out.println("config connection closed");
            return id;
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                 //   System.out.println("attempting config connection close");
                    con.close();
                  //  System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }
        System.out.println("insert failed ws transaction for " + url);
        throw new Exception();
    }

    public void RemoveTransaction(String id) throws Exception {
        Connection con = Utility.getPerformanceDBConnection();
        try {
            PreparedStatement cmd = con.prepareStatement("delete from rawdata where transactionid=?");
            cmd.setString(1, id);
            cmd.execute();
            cmd.close();

          //  System.out.println("attempting config connection close");
            con.close();
           // System.out.println("config connection closed");

        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                 //   System.out.println("attempting config connection close");
                    con.close();
                   // System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }
    }

    public void GrantUserAuditAccess(String u, String url) throws Exception {
        Connection con = Utility.getConfigurationDBConnection();
        try {
            PreparedStatement cmd = con.prepareStatement("INSERT INTO userpermissions(   "
                    + "         username, objecturi, auditobject)"
                    + "    VALUES (?, ?, ?);");
            cmd.setString(1, u);;
            cmd.setString(2, url);;
            cmd.setBoolean(3, true);
            cmd.execute();
            cmd.close();

           // System.out.println("attempting config connection close");
            con.close();
           // System.out.println("config connection closed");

        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                //    System.out.println("attempting config connection close");
                    con.close();
                   // System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }
    }

    public void RemoveAuditAccess(String u, String url) throws Exception {
        Connection con = Utility.getConfigurationDBConnection();
        try {
            PreparedStatement cmd = con.prepareStatement("delete from userpermissions where username=? and objecturi=?; ");

            cmd.setString(1, u);
            cmd.setString(2, url);

            cmd.execute();
            cmd.close();

           // System.out.println("attempting config connection close");
            con.close();
           // System.out.println("config connection closed");

        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                 //   System.out.println("attempting config connection close");
                    con.close();
                   // System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * removes all WS transactions for a specific url
     *
     * @param url
     * @throws Exception
     */
    public void RemoveTransactions(String url) throws Exception {

        Connection con = Utility.getPerformanceDBConnection();
        try {
            PreparedStatement cmd = con.prepareStatement("delete from rawdata where uri=?");
            cmd.setString(1, url);
            cmd.execute();
            cmd.close();

           // System.out.println("attempting config connection close");
            con.close();
           // System.out.println("config connection closed");

        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (!con.isClosed()) {
                  //  System.out.println("attempting config connection close");
                    con.close();
                   // System.out.println("config connection closed");
                }
            } catch (Exception e) {
            }
        }
    }
}
