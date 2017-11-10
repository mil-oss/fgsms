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
package org.miloss.fgsms.bueller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;
;
import org.apache.log4j.PropertyConfigurator;
import org.miloss.fgsms.common.DBSettingsLoader;
import org.miloss.fgsms.common.DBUtils;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.common.PolicyType;
import org.miloss.fgsms.services.interfaces.policyconfiguration.KeyNameValueEnc;
import org.miloss.fgsms.services.interfaces.policyconfiguration.TransportAuthenticationStyle;
import org.miloss.fgsms.sla.AuxHelper;
import org.miloss.fgsms.sla.SLACommon;

/**
 * This tool for fgsms will fire off an HTTP GET request message in an attempt
 * to retrieve the wsdl of a web service that is currently being monitored by
 * fgsms. This will only function on services with an fgsms policy defined
 * within the realm of the local instance of fgsms. It then set's the status of
 * the service as known to fgsms. This information is available via the fgsms
 * Status Service and is visually depicted within the fgsms GUI.
 * Bueller.....Bueller.............Bueller..........................
 *
 * @author AO
 */


public class Bueller {

    protected static final Logger log = Logger.getLogger("fgsms.StatusBueller");
    public static boolean running = true;
    public static boolean noloop = false;
    private static String truststore = "";
    private static String truststorepass = "";
    private static String keystore = "";
    private static String keystorepass = "";
    private static long LastConfiguredAt = 0;
    private static boolean Configured = false;
    private static boolean ignoreSSL = false;
    private static org.apache.http.conn.ssl.SSLSocketFactory sf = null;
    private static org.apache.http.conn.ssl.SSLSocketFactory sfpki = null;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        /*
        * if (args.length == 1) { if (args[0].equalsIgnoreCase("start")) { // t
        * = (new Thread(new DataPusher(policyCache, outboundQueue))); localref
        * = new Bueller(); new Thread(localref).start();
        *
        * }
        *
        * if (args[0].equalsIgnoreCase("stop")) { running = false; } }
        */
        new Bueller().Main(args);
        
    }
   
    /**
     * Alternate URLs, basically multiple hostnames, FQDN, host file entries, ip
     * addresses, etc can map to the same service. Sometimes a particular url is
     * firewalled or only listens on a specific hostname. By default bueller
     * will first attempt a connection using the modified url, i.e. the URL
     * displayed in fgsms which usually uses the hostname of the machine hosting
     * the service If another url was observed by an agent at some point in
     * time, then this will fetch all other urls for
     *
     * @param url
     * @param perf
     * @return
     */
    protected static List<String> GetAlternateUrls(String url, java.sql.Connection perf) {
        List<String> alts = new ArrayList<String>();
        PreparedStatement com = null;
        ResultSet rs = null;
        try {
            
            //dec 10-2011 PreparedStatement com = perf.prepareStatement("select  originalurl from rawdata where uri=? group by originalurl;");
            com = perf.prepareStatement("select  alturi from alternateurls where uri=?;");
            com.setString(1, url);
            rs = com.executeQuery();
            while (rs.next()) {
                String t = rs.getString(1);
                if (!Utility.stringIsNullOrEmpty(t)) {
                    t = t.trim();
                    if (!Utility.stringIsNullOrEmpty(t)) {
                        if (!t.equals(url)) {
                            if (!url.equals(t)) {
                                alts.add(t);
                            }
                        }
                    }
                }
                //TODO future optimization but not required, this might be a good spot to filter out localhost/127.0.0.1 records, but only if this machine is different that the machine hosting the service
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
        }
        return alts;
        
    }
    /**
     * provides a wrapper for getting alternate urls without passing a db
     * connection
     *
     * @param url
     * @param pooled
     * @return
     */
    protected static List<String> GetAlternateUrls(String url, boolean pooled) {
        
        java.sql.Connection perf = null;
        if (pooled) {
            perf = Utility.getPerformanceDBConnection();
        } else {
            perf = Utility.getPerformanceDB_NONPOOLED_Connection();
        }
        List<String> ret = Collections.EMPTY_LIST;
        try {
            ret = GetAlternateUrls(url, perf);
        } catch (Exception ex) {
            log.log(Level.ERROR, null, ex);
        } finally {
            DBUtils.safeClose(perf);
        }
        return ret;
    }
    private boolean done = false;
    //~ Inner Classes -----------------------------------------------------------------------------
    private File file;
    private FileChannel channel;
    private FileLock lock;

    private void closeLock() {
        try {
            lock.release();
        } catch (Exception e) {
        }
        try {
            channel.close();
        } catch (Exception e) {
        }
    }

    private void deleteFile() {
        try {
            file.delete();
        } catch (Exception e) {
        }
    }

    private void cleanUpOldStuff(List<String> MonitoredURLS, java.sql.Connection con) {
        long timer = System.currentTimeMillis();
        PreparedStatement cmd;
        int x = 0;
        try {
            cmd = con.prepareStatement("delete from status where uri=(select status.uri from status  left outer join  servicepolicies  on (status.uri = servicepolicies.uri) where xmlpolicy is null)");
            x = cmd.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.WARN, null, ex);
        }

        if (x > 0) {
            log.log(Level.INFO, "Purging " + x + " from status table");
        }
        log.log(Level.INFO, "Bueller cleanup performed in " + (System.currentTimeMillis() - timer) + "ms");
        /*
           * try { log.log(Level.INFO, "Performing cleanup operations");
           * PreparedStatement com = con.prepareStatement("delete FROM status
           * WHERE NOT EXISTS (SELECT * FROM servicepolicies WHERE " + "
           * servicepolicies.policytype=? and servicepolicies.uri = status.uri and
           * " + " servicepolicies.buellerenabled=true )"); com.setInt(1,
           * PolicyType.TRANSACTIONAL.ordinal()); int i = com.executeUpdate(); if
           * (i > 0) { log.log(Level.INFO, i + " records removed. This happens
           * when I start running and I grab a list of services to ping and while
           * executing, a service policy is deleted and we end up with an orphaned
           * record."); } log.log(Level.INFO, "done.");
           *
           * } catch (SQLException ex) { log.log(Level.WARN, null, ex); }
         */

    }

    public void run() {
        this.Main(null);
    }

    private String doJmsURL(boolean pooled, String endpoint) {
        try {

            boolean ok = false;
            String server = endpoint.split("#")[0];
            server = server.replace("jms:", "jnp://");
            String name = endpoint.split("#")[1];
            String msg = "";
            String[] info = DBSettingsLoader.GetCredentials(pooled, endpoint);
            String username = null;
            String password = null;
            if (info != null) {
                username = info[0];
                password = info[1];
            } else {
                info = DBSettingsLoader.GetDefaultBuellerCredentials(pooled);
                if (info != null) {
                    username = info[0];
                    password = info[1];
                }
            }

            if (name.startsWith("topic")) {
                try {
                    Properties properties1 = new Properties();
                    properties1.put(Context.INITIAL_CONTEXT_FACTORY,
                            "org.jnp.interfaces.NamingContextFactory");
                    properties1.put(Context.URL_PKG_PREFIXES,
                            "org.jboss.naming:org.jnp.interfaces");
                    //properties1.put(Context.PROVIDER_URL, "jnp://127.0.0.1:1099");
                    properties1.put(Context.PROVIDER_URL, server);

                    InitialContext iniCtx = new InitialContext(properties1);

                    TopicConnectionFactory tcf = (TopicConnectionFactory) iniCtx.lookup("TopicConnectionFactory");
                    TopicConnection createTopicConnection = null;
                    if (info != null) {
                        createTopicConnection = tcf.createTopicConnection(username, Utility.DE(password)); //Topic topic = (Topic) iniCtx.lookup("/topic/quickstart_jmstopic_topic");
                    } else {
                        createTopicConnection = tcf.createTopicConnection(); //Topic topic = (Topic) iniCtx.lookup("/topic/quickstart_jmstopic_topic");
                    }
                    createTopicConnection.start();
                    createTopicConnection.stop();
                    createTopicConnection.close();
                    //Topic topic = (Topic) iniCtx.lookup("//" + name);
                    ok = true;

                    //topic = null;
                    iniCtx.close();

                } catch (Exception ex) {
                    System.out.println(ex);
                    msg = ex.getLocalizedMessage();
                    //return ex.getLocalizedMessage();
                }
            } else if (name.startsWith("queue")) {
                try {

                    Properties properties1 = new Properties();
                    properties1.put(Context.INITIAL_CONTEXT_FACTORY,
                            "org.jnp.interfaces.NamingContextFactory");
                    properties1.put(Context.URL_PKG_PREFIXES,
                            "org.jboss.naming:org.jnp.interfaces");
                    properties1.put(Context.PROVIDER_URL, server);
                    InitialContext iniCtx = new InitialContext(properties1);
                    QueueConnection conn;
                    QueueSession session;
                    Queue que;

                    Object tmp = iniCtx.lookup("ConnectionFactory");
                    QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
                    if (info != null) {
                        conn = qcf.createQueueConnection(username, Utility.DE(password));
                    } else {
                        conn = qcf.createQueueConnection();
                    }

                    que = (Queue) iniCtx.lookup(name);
                    session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
                    conn.start();

                    //System.out.println("Connection Started");
                    ok = true;

                    conn.stop();
                    session.close();
                    iniCtx.close();

                } catch (Exception ex) {
                    log.log(Level.WARN, "Could not bind to jms queue", ex);
                    msg = ex.getLocalizedMessage();
                }
                if (ok) {
                    return "OK";
                }
                return "Unable to bind to JMS queue: " + msg;
            } else {
                return "Unsupported Protocol";
            }
        } catch (Exception ex) {
            log.log(Level.WARN, "service " + endpoint + " is offline or an error occured", ex);
            return "Offline " + ex.getLocalizedMessage();
        }
        return "undeterminable";
    }


    private void Main(String args[]) {

        try {
            file = new File("bueller.lck");
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
        } catch (Exception e) {
            // already locked
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file bueller.lck needs to be deleted.");
            return;
        }
        if (lock == null) {
            closeLock();
            System.out.println("Could not obtain the lock, this means that either this program is already running or something went wrong and the file bueller.lck needs to be deleted.");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());

        PropertyConfigurator.configure("log4j.properties");
//        log.log(Level.INFO, "###########################################################");
        log.log(Level.INFO, "fgsms.Bueller (service ping machine) startup.....");
        //      log.log(Level.INFO, "###########################################################");
        long lastRanAt = 0;
        int interval = 10000;
        if (args.length == 1 && args[0].equalsIgnoreCase("noloop")) {
            noloop = true;
        }

        while (running) {
            if (System.currentTimeMillis() - lastRanAt > interval) {
                lastRanAt = System.currentTimeMillis();
                //run
                log.log(Level.INFO, "Bueller.....");
                try {
                    Fire(false);

                    AuxHelper.TryUpdateStatus(true, "urn:fgsms:Bueller:" + Utility.getHostName(), "OK, took " + (System.currentTimeMillis() - lastRanAt) + "ms to process.", false, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());

                } catch (Exception ex) {
                    //return;
                    AuxHelper.TryUpdateStatus(false, "urn:fgsms:Bueller:" + Utility.getHostName(), "Running but possible database error" + (System.currentTimeMillis() - lastRanAt) + "ms to process.", false, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
                    //commented out to that Bueller will continue to run, even if the database isn't available (the data will still be lost though)
                }
                log.log(Level.INFO, "Pausing until the next iteration in " + interval + "ms.....");

                //          log.log(Level.INFO, "###########################################################");
                //         log.log(Level.INFO, "###########################################################");
            }
            if (noloop) {
                running = false;
            }
            if (running) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    log.log(Level.FATAL, null, ex);
                }
            }

        }
        done = true;
    }


    protected void Init(boolean pooled) throws Exception {
        if ((System.currentTimeMillis() - 30000) < LastConfiguredAt && Configured) {
            log.log(Level.DEBUG, "already configured, using last known config");
            return;
        }
        log.log(Level.INFO, "Refreshing configuration");
        LastConfiguredAt = System.currentTimeMillis();
        Configured = true;

        String tmp = System.getProperty("jboss.server.config.url");

        if (Utility.stringIsNullOrEmpty(tmp)) {
            //FIX for Jboss 7
            try {
                tmp = System.getProperty("jboss.server.config.dir");
                if (tmp != null && !tmp.equalsIgnoreCase("null")) {
                    File f = new File(tmp);
                    tmp = f.toURI().toURL().toString();
                    tmp += File.separator;
                }
            } catch (Exception e) {
            }
        }
        //fix for tomcat
        if (Utility.stringIsNullOrEmpty(tmp)) {
            tmp = System.getProperty("catalina.home");
            if (tmp != null) {
                tmp = tmp + File.separator + "conf" + File.separator;
            }
        }
        //fix for OpenJDK/linux issues
        if (tmp != null) {
            String os = System.getProperty("os.name");
            if (os.toLowerCase().indexOf("win") == -1) {
                tmp = File.separator + tmp;
            }

        }
        X509HostnameVerifier hostnameVerifier = new org.apache.http.conn.ssl.StrictHostnameVerifier();
        KeyNameValueEnc d = DBSettingsLoader.GetPropertiesFromDB(pooled, "Bueller", "IgnoreSSLErrors");
        if (d != null && d.getKeyNameValue() != null) {
            try {
                ignoreSSL = Boolean.parseBoolean(d.getKeyNameValue().getPropertyValue());
            } catch (Exception ex) {
                ignoreSSL = false;
            }
        }
        if (ignoreSSL) {
            log.log(Level.WARN, "SSL Hostname verification turned off");
            hostnameVerifier = new AllowAllHostnameVerifier();
        }

        if (!Utility.stringIsNullOrEmpty(tmp)) {

            d = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "truststore");
            if (d != null && d.getKeyNameValue() != null) {
                truststore = tmp + d.getKeyNameValue().getPropertyValue();
            }
            d = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "truststorepass");
            if (d != null && d.getKeyNameValue() != null) {
                truststorepass = d.getKeyNameValue().getPropertyValue();
            }
            d = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "keystore");
            if (d != null && d.getKeyNameValue() != null) {
                keystore = tmp + d.getKeyNameValue().getPropertyValue();
            }
            d = DBSettingsLoader.GetPropertiesFromDB(pooled, "defaults", "keystorepass");
            if (d != null && d.getKeyNameValue() != null) {
                keystorepass = d.getKeyNameValue().getPropertyValue();
            }

            if (!(Utility.stringIsNullOrEmpty(truststore) && !Utility.stringIsNullOrEmpty(truststorepass)) && !Utility.stringIsNullOrEmpty(keystore) && !Utility.stringIsNullOrEmpty(keystorepass)) {

                KeyStore trustStore = null;
                FileInputStream instream = null;
                try {
                    log.log(Level.INFO, "loading trust store from " + truststore);
                    instream = new FileInputStream(new File(truststore.replace("file:/", "")));
                    trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(instream, Utility.DE(truststorepass).toCharArray());
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                    trustStore = null;
                } finally {
                    if (instream != null) {
                        try {
                            instream.close();
                        } catch (Exception ex) {
                            log.log(Level.DEBUG, null, ex);
                        }
                    }
                }
                KeyStore keyStore = null;

                try {
                    log.log(Level.INFO, "loading key store from " + keystore);
                    instream = new FileInputStream(new File(keystore.replace("file:/", "")));
                    keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    keyStore.load(instream, Utility.DE(keystorepass).toCharArray());
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                    keyStore = null;
                } finally {
                    if (instream != null) {
                        try {
                            instream.close();
                        } catch (Exception ex) {
                            log.log(Level.DEBUG, null, ex);
                        }
                    }
                }
                if (trustStore != null && keyStore != null) {
                    sfpki = new org.apache.http.conn.ssl.SSLSocketFactory(org.apache.http.conn.ssl.SSLSocketFactory.TLS, keyStore, Utility.DE(keystorepass), trustStore, new SecureRandom(),
                            hostnameVerifier);
                    //sf = new org.apache.http.conn.ssl.SSLSocketFactory(asdasdtrustStore, Utility.DE(truststorepass));
                } else if (trustStore != null) {
                    sf = new org.apache.http.conn.ssl.SSLSocketFactory(trustStore, Utility.DE(truststorepass));
                }
            } else if (!(Utility.stringIsNullOrEmpty(truststore) && !Utility.stringIsNullOrEmpty(truststorepass))) {
                KeyStore trustStore = null;
                FileInputStream instream = new FileInputStream(new File(truststore.replace("file:/", "")));
                try {
                    log.log(Level.INFO, "loading trust store from " + truststore);
                    trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(instream, Utility.DE(truststorepass).toCharArray());
                } catch (Exception ex) {
                    log.log(Level.WARN, null, ex);
                    trustStore = null;
                } finally {
                    if (instream != null) {
                        try {
                            instream.close();
                        } catch (Exception ex) {
                            log.log(Level.DEBUG, null, ex);
                        }
                    }
                }
                if (trustStore != null) {
                    sf = new org.apache.http.conn.ssl.SSLSocketFactory(trustStore, Utility.DE(truststorepass));
                    sf.setHostnameVerifier(hostnameVerifier);
                }
            }

            //check system.properties for javax.net.ssl
            //load trust store and keystore from jboss?
            //setup ssl 
        } else {
            log.log(Level.WARN, "unable to determine the location to the key/trust stores because the environment variable jboss.server.config.url, jboss.server.config.dir and catalina.home are not defined. At least one must be set for SSL to function");
        }
    }

    /**
     * Performs the work of status bueller by sending an http get message to
     * attempt to pull the wsdl Loads all service urls that are of PolicyType
     * transactional
     *
     * @param pooled
     * @throws SQLException
     * @throws Exception
     */
    public void Fire(boolean pooled) throws SQLException, Exception {
        Init(pooled);
        java.sql.Connection con = null;
        java.sql.Connection perf = null;
        if (pooled) {
            con = Utility.getConfigurationDBConnection();
            perf = Utility.getPerformanceDBConnection();
        } else {
            con = Utility.getConfigurationDB_NONPOOLED_Connection();
            perf = Utility.getPerformanceDB_NONPOOLED_Connection();
        }
        PreparedStatement com = null;
        ResultSet rs = null;
        List<String> urls = new ArrayList<String>();
        try {
            boolean run = true;
            KeyNameValueEnc GetPropertiesFromDB = DBSettingsLoader.GetPropertiesFromDB(pooled, "Bueller", "Enabled");
            if (GetPropertiesFromDB != null && GetPropertiesFromDB.getKeyNameValue() != null) {
                try {
                    run = Boolean.parseBoolean(GetPropertiesFromDB.getKeyNameValue().getPropertyValue());
                } catch (Exception ex) {
                }
            }
            if (!run) {
                log.log(Level.INFO, "Bueller is disabled by General Settings, key=Bueller, name=Enabled, exiting...");
                con.close();
                perf.close();
                return;
            }

            //snip, there used to be code here to tie into a jboss workmanager was it was removed due to licensing concerns.
            com = con.prepareStatement("select uri from servicepolicies where buellerenabled=true and policytype=?; ");
            com.setInt(1, PolicyType.TRANSACTIONAL.ordinal());
            rs = com.executeQuery();

            while (rs.next()) {
                urls.add(rs.getString(1));
            }
            rs.close();
            com.close();
        } catch (Exception ex) {
            log.log(Level.ERROR, "unexpected error running bueller ", ex);

        } finally {
            DBUtils.safeClose(rs);
            DBUtils.safeClose(com);
        }

        try {
            log.log(Level.INFO, urls.size() + " urls to process");

            {
                for (int i = 0; i < urls.size(); i++) {
                    if (!running) {
                        log.log(Level.INFO, "Interupt detected on url " + (i + 1) + " of " + urls.size());
                        break;
                    }
                    try {
                        String s = sendGetRequest(pooled, urls.get(i), 0);
                        Boolean currenstatus = s.equalsIgnoreCase("ok");
                        log.log(Level.INFO, (i + 1) + "/" + urls.size() + " " + urls.get(i) + " status is " + s);
                        if (!currenstatus) {
                            List<String> alts = GetAlternateUrls(urls.get(i), perf);
                            for (int k = 0; k < alts.size(); k++) {
                                if (currenstatus) {
                                    break;
                                }
                                s = sendGetRequest(pooled, alts.get(k), 0);
                                currenstatus = s.equalsIgnoreCase("ok");
                                log.log(Level.INFO, urls.get(i) + " via alternate URL " + alts.get(k) + " status is " + s);
                            }
                        }

                        AuxHelper.TryUpdateStatus(currenstatus, urls.get(i), s, pooled, AuxHelper.UNSPECIFIED, SLACommon.GetHostName(), AuxHelper.FLAGS.NO_AUTO_CREATE);

                    } catch (Exception ex) {
                        log.log(Level.ERROR, "error setting status in config db for uri " + urls.get(i), ex);
                    }
                }

                cleanUpOldStuff(urls, con);
            }
        } catch (Exception ex) {
            log.log(Level.ERROR, "unexpected error running bueller ", ex);

        } finally {
            DBUtils.safeClose(con);
            DBUtils.safeClose(perf);
        }
    }

    protected Credentials transformCredentials(String[] info) {
        if (info.length == 2) {
            return new UsernamePasswordCredentials(info[0], Utility.DE(info[1]));
        }
        if (info.length == 3) {
            TransportAuthenticationStyle tas = TransportAuthenticationStyle.valueOf(info[2]);
            switch (tas) {
                case HTTP_NTLM:
                    String data = info[0];
                    String[] t = data.split("\\\\");
                    String username = t[1];
                    String domain = t[0];
                    return new NTCredentials(username, Utility.DE(info[1]), SLACommon.GetHostName(), domain);
                case HTTP_DIGEST:
                case HTTP_BASIC:
                default:
                    return new UsernamePasswordCredentials(info[0], Utility.DE(info[1]));
            }
        }
        return null;
    }

    /**
     * attempts an http get request with authentication
     *
     * @param pooled
     * @param endpoint
     * @param policyURL
     * @return
     */
    protected String sendGetRequestAuth(boolean pooled, String endpoint, String policyURL, int depth) {//, AuthMode mode) {
        if (depth > 10) {
            //abort, possible redirect loop
            return "Aborting due to redirect loop";
        }
        String[] info = DBSettingsLoader.GetCredentials(pooled, policyURL);

        if (info == null) {
            info = DBSettingsLoader.GetDefaultBuellerCredentials(pooled);
            if (info == null) {
                return "Unauthorized, no credentials are available";
            }
        }

        if (endpoint.startsWith("http://")) {
            // Send a GET request to the servlet

            DefaultHttpClient c = new DefaultHttpClient();
            try {
                c.getCredentialsProvider().setCredentials(AuthScope.ANY, transformCredentials(info));
                if (!c.getCredentialsProvider().getCredentials(AuthScope.ANY).getClass().getCanonicalName().equalsIgnoreCase(NTCredentials.class.getCanonicalName())) {
                    log.log(Level.WARN, "Usage of non-NTLM authentication over a non SSL channel.");
                }
                HttpGet m = new HttpGet(endpoint);
                HttpResponse res = c.execute(m);
                int status = res.getStatusLine().getStatusCode();
                try {
                    InputStream content = res.getEntity().getContent();
                    byte[] buffer = new byte[1024];
                    while (content.read(buffer) >= 0) {
                    }
                } catch (Exception f) {
                }
                c.getConnectionManager().shutdown();
                if (status < 300 || status == HttpStatus.SC_NOT_MODIFIED) {
                    return "OK";
                } else if (status == HttpStatus.SC_MOVED_PERMANENTLY
                        || status == HttpStatus.SC_TEMPORARY_REDIRECT
                        || status == HttpStatus.SC_MOVED_TEMPORARILY) {
                    String newUrl = res.getHeaders("Location")[0].getValue();
                    return sendGetRequestAuth(pooled, newUrl, policyURL, depth + 1);
                }

                return String.valueOf(status);
            } catch (Exception ex) {
                c.getConnectionManager().shutdown();
                log.log(Level.INFO, "code " + ex.getLocalizedMessage());
                return "offline: " + ex.getMessage();
            }
        } else if (endpoint.startsWith("https://")) {

            //first try with the username/password over ssl
            if (sf == null && sfpki == null) {
                return "Unauthorized, no trust store available for SSL communication";
            }
            DefaultHttpClient c = new DefaultHttpClient();
            try {
                URL url = new URL(endpoint);
                String scheme = "https";
                int port = url.getPort();

                if (port == -1 && endpoint.toLowerCase().startsWith("https:")) {
                    port = 443;
                }

                Scheme sch = null;

                if (sfpki == null) {
                    sch = new Scheme("https", port, sf);
                } else {
                    sch = new Scheme("https", port, sfpki);
                }
                c.getConnectionManager().getSchemeRegistry().register(sch);

                c.getCredentialsProvider().setCredentials(AuthScope.ANY, transformCredentials(info));

                HttpGet m = new HttpGet(endpoint);
                HttpResponse res = c.execute(m);
                int status = res.getStatusLine().getStatusCode();
                try {
                    InputStream content = res.getEntity().getContent();
                    byte[] buffer = new byte[1024];
                    while (content.read(buffer) >= 0) {
                    }
                } catch (Exception f) {
                }
                c.getConnectionManager().shutdown();
                if (status < 300 || status == HttpStatus.SC_NOT_MODIFIED) {
                    return "OK";
                } else if (status == HttpStatus.SC_MOVED_PERMANENTLY
                        || status == HttpStatus.SC_TEMPORARY_REDIRECT
                        || status == HttpStatus.SC_MOVED_TEMPORARILY) {
                    String newUrl = res.getHeaders("Location")[0].getValue();
                    return sendGetRequestAuth(pooled, newUrl, policyURL, depth + 1);
                }

                return String.valueOf(status);
            } catch (Exception ex) {
                c.getConnectionManager().shutdown();
                log.log(Level.INFO, "code " + ex.getLocalizedMessage());
                return "offline: " + ex.getMessage();
            }

        } else {
            return "undeterminable";
        }

    }


    /**
     * Sends an HTTP GET request to a url
     *
     * @param endpoint - The URL of the server. (Example: "
     * http://www.yahoo.com/search") Note: This method will add the question
     * mark (?wsdl) to the request
     * @return - OK for 200 messages, all others, the actually response code or
     * error message
     */
    protected String sendGetRequest(boolean pooled, String endpoint, int depth) {
        if (depth > 10) {
            //abort, possible redirect loop
            return "Aborting due to redirect loop";
        }
        String result = null;
        String policyUrl = new String(endpoint);
        if (endpoint.startsWith("http://")) {
            // Send a GET request to the servlet
            HttpURLConnection conn = null;
            try {
                String originalendpoint = endpoint;
                if (!endpoint.endsWith("?wsdl")) {
                    endpoint = endpoint + "?wsdl";
                }
                conn = (HttpURLConnection) new URL(endpoint).openConnection();

                if (conn.getResponseCode() == 401) {
                    //basic example WWW-Authenticate: Basic realm="fgsms Services"
                    //digest example WWW-Authenticate: Digest realm="fgsms Services", qop="auth", nonce="2569aa2af54f6d47e8472f47f2e3da01", opaque="a39d25cce80574f8255b97052d8f1544"
                    //WWW-Authenticate: Negotiate
                    //WWW-Authenticate: NTLM
                    //        String authtype = conn.getHeaderField("WWW-Authenticate");
                    //    if (authtype.toLowerCase().startsWith("digest")) {
                    return sendGetRequestAuth(pooled, endpoint, policyUrl, depth + 1);
                } else if (conn.getResponseCode() == 404) {
                    //fix for sonatype nexus and non wsdl urls
                    conn = (HttpURLConnection) new URL(originalendpoint).openConnection();
                    return "Not found";
                } else if (conn.getResponseCode() == HttpStatus.SC_MOVED_PERMANENTLY
                        || conn.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY
                        || conn.getResponseCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {
                    //follow the redirect
                    String newUrl = conn.getHeaderField("Location");
                    return sendGetRequest(pooled, newUrl, depth + 1);
                    //System.out.println("Moved to " + newUrl); //should be the new destination url
                    //return "Moved " + conn.getResponseMessage();
                } else if (conn.getResponseCode() == HttpStatus.SC_NOT_MODIFIED) {
                    return "OK";
                }
                InputStream inputStream = null;
                try {
                    inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024];
                    while (inputStream.read(buffer) >= 0) {
                    }
                    inputStream.close();
                } catch (Exception f) {
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception ex) {
                        }
                    }
                }

                String msg = conn.getResponseMessage();
                conn.disconnect();
                return msg;
            } catch (java.net.UnknownHostException ex) {
                return "Host unknown";
            } catch (Exception ex) {
                return ex.getMessage();
            } finally {
                if (conn != null) {
                    try {
                        conn.disconnect();
                    } catch (Exception ex) {
                    }
                }
            }
        } else if (endpoint.startsWith("https://")) {
            if (!endpoint.endsWith("?wsdl")) {
                endpoint = endpoint + "?wsdl";
            }
            DefaultHttpClient c = new DefaultHttpClient();
            try {
                URL url = new URL(endpoint);
                int port = url.getPort();
                if (port == -1 && endpoint.toLowerCase().startsWith("http:")) {
                    port = 80;
                }
                if (port == -1 && endpoint.toLowerCase().startsWith("https:")) {
                    port = 443;
                }
                Scheme sch = null;
                if (sfpki == null) {
                    sch = new Scheme("https", port, sf);
                } else {
                    sch = new Scheme("https", port, sfpki);
                }
                if (endpoint.toLowerCase().startsWith("https:")) {
                    c.getConnectionManager().getSchemeRegistry().register(sch);
                }

                HttpGet m = new HttpGet(endpoint);
                HttpResponse res = c.execute(m);
                int status = res.getStatusLine().getStatusCode();
                try {
                    InputStream content = res.getEntity().getContent();
                    byte[] buffer = new byte[1024];
                    while (content.read(buffer) >= 0) {
                    }
                } catch (Exception f) {
                }
                c.getConnectionManager().shutdown();
                if (status == 401) {
                    return sendGetRequestAuth(pooled, endpoint, policyUrl, depth + 1);
                } else if (status == HttpStatus.SC_MOVED_PERMANENTLY
                        || status == HttpStatus.SC_MOVED_TEMPORARILY
                        || status == HttpStatus.SC_TEMPORARY_REDIRECT) {
                    String newUrl = res.getHeaders("Location")[0].getValue();
                    return sendGetRequest(pooled, newUrl, depth + 1);
                } else if (status == HttpStatus.SC_NOT_MODIFIED) {
                    return "OK";
                } else {
                    return (status < 300) ? "OK" : "offline";
                }
            } catch (Exception ex) {
                c.getConnectionManager().shutdown();
                log.log(Level.WARN, "error caught connecting to " + endpoint, ex);
                return ex.getMessage();
            }

        } else if (endpoint.startsWith("jms:")) {
            return doJmsURL(pooled, endpoint);
        }
        return "Unknown protocol";
    }

    public class RunWhenShuttingDown extends Thread {
        
        public void run() {
            System.out.println("Control-C caught. Shutting down...");
            running = false;
            while (!done) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
            closeLock();
            deleteFile();
            if (!noloop) {
                AuxHelper.TryUpdateStatus(false, "urn:fgsms:Bueller:" + Utility.getHostName(), "Stopped", false, AuxHelper.UNSPECIFIED, SLACommon.GetHostName());
            }
        }
    }
}
