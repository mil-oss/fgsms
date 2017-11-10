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
package org.miloss.fgsms.agentcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Constants;
import org.miloss.fgsms.common.Constants.AuthMode;
import org.miloss.fgsms.common.Logger;
import org.miloss.fgsms.common.PropertyLoader;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService_Service;
import org.miloss.fgsms.services.interfaces.common.SecurityWrapper;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService_Service;
import org.miloss.fgsms.services.interfaces.datacollector.DCS;
import org.miloss.fgsms.services.interfaces.datacollector.DataCollectorService;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PolicyConfigurationService;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportingService;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportingService_Service;
import org.miloss.fgsms.services.interfaces.status.StatusService;
import org.miloss.fgsms.services.interfaces.status.StatusServiceService;
import us.gov.ic.ism.v2.ClassificationType;

/**
 * The fgsms Configuration Loader for Agents This class gives you a centralized
 * utility class for accessing all of the configuration data stored within the
 * fgsms-agent.properties file.<br> It also provides some basic error checking
 * and will initialize client proxy objects to the PCS, DCS, and SS services.
 *
 * Keep in mind, after creating an instance of ConfigLoader, the execution
 * endpoints of the web service proxies are NOT set. You'll have to do that by
 * accessing the collections of URLs for the desired service AND/OR trigger a
 * discovery mechanism for them.
 *
 * @author AO
 */
public class ConfigLoader {

    public static final String PROP_MESSAGE_PROCESSOR_IMPL = "message.processor.impl";
    static Logger log = Logger.getLogger(org.miloss.fgsms.common.Constants.LoggerName);
    protected SecurityWrapper classlevel = new SecurityWrapper(ClassificationType.U, "None");
    protected StatusService ssport = null;
    protected StatusServiceService ss = null;
    protected Algorithm SSalgo = Algorithm.FAILOVER;
    protected int SSRetryCount = 2;
    protected List<String> SS_URLS = new ArrayList<String>();
    protected PolicyConfigurationService pcsservice = null;
    protected PCS pcsport = null;
    protected List<String> PCS_URLS = new ArrayList<String>();
    protected Algorithm PCSalgo = Algorithm.FAILOVER;
    protected int PCSRetryCount = 2;
    protected DataCollectorService dcsservice = null;
    protected DCS dcsport = null;
    protected Algorithm DCSalgo = Algorithm.FAILOVER;
    protected List<String> DCS_URLS = new ArrayList<String>();
    protected int DCSRetryCount = 2;
    protected Algorithm ACSalgo = Algorithm.FAILOVER;
    protected int ACSRetryCount = 2;
    protected List<String> ACS_URLS = new ArrayList<String>();
    protected Algorithm ACSAalgo = Algorithm.FAILOVER;
    protected int ACSARetryCount = 2;
    protected List<String> ACSA_URLS = new ArrayList<String>();
    protected ReportingService rsport = null;
    protected ReportingService_Service rs = null;
    protected DataAccessService dasport = null;
    protected DataAccessService_Service das = null;

    protected Algorithm RSAalgo = Algorithm.FAILOVER;
    protected int RSRetryCount = 2;
    protected List<String> RS_URLS = new ArrayList<String>();
    protected AutomatedReportingService arsport = null;
    protected AutomatedReportingService_Service ars = null;
    protected Algorithm ARSAalgo = Algorithm.FAILOVER;
    protected int ARSRetryCount = 2;
    protected List<String> ARS_URLS = new ArrayList<String>();
    protected Properties prop = null;
    protected String username = "";
    protected String password = "";
    protected String certinfo = "";
    protected org.miloss.fgsms.common.Constants.AuthMode mode_ = org.miloss.fgsms.common.Constants.AuthMode.None;
    protected long discoveryInterval = 600000;
    protected String offlinestorage = "";
    protected UnavailableBehavior behavior = UnavailableBehavior.PURGE;
    protected String javaxtruststore = null;
    protected String javaxtruststorepass = null;
    protected String javaxkeystore = null;
    protected String javaxkeystorepass = null;
    List<String> discover_providers = new ArrayList<String>();
    /**
     * Allows for setting up the config via a provided properties instance
     *
     * @param p
     */
    public ConfigLoader(Properties p) throws ConfigurationException {
        prop = p;
        load();
    }
    /**
     * create an empty config loader object and initializes the client proxy
     * objects to the initial state, no configuration settings will be
     * available.
     *
     * @param p ignored
     * @throws ConfigurationException
     */
    public ConfigLoader(boolean p) throws ConfigurationException {
        
        createClientProxies();
        
    }
    
    /**
     * this is a faster mechanism to initialize the config. it's "Faster" by only
     * loading the properties file. It does not initialize a configloader object
     * nor does it initialize service proxies
     * @return
     * @throws ConfigurationException 
     */
    public static Properties loadProperties() throws ConfigurationException{
    //first look for system properties
    Properties prop=null;
        try {
            //this needs to be inside of the try/catch for security manager issues
            if (System.getProperties().containsKey("org.miloss.fgsms.agentConfigFileOverride")) {
                InputStream is = null;
                try {
                    is = new FileInputStream(System.getProperty("org.miloss.fgsms.agentConfigFileOverride"));
                    prop = new Properties();
                    
                    prop.load(is);
                    log.log(Level.INFO, "Agent configuration loaded from System Property Override at " + System.getProperty("org.miloss.fgsms.agentConfigFileOverride") + Constants.Version);
                    
                } catch (Exception ex) {
                    log.log(Level.DEBUG, "", ex);
                    prop = null;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Exception ex) {
                            log.log(Level.DEBUG, "", ex);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            prop = null;
        }
        
        if (prop == null) {
            try {
                prop = new Properties();
                URL file = (ClassLoader.getSystemClassLoader().getResource("fgsms-agent.properties"));
                if (file != null) {
                    InputStream is = null;
                    try {
                        is = file.openStream();
                        prop.load(is);
                        log.log(Level.INFO, "Agent configuration loaded from System classloader at " + file.toString() + Constants.Version);
                        is.close();
                    } catch (Exception ex) {
                        log.log(Level.DEBUG, "", ex);
                    } finally {
                        if (is!=null)
                            try {
                                is.close();
                            } catch (Exception e2) {
                                log.log(Level.DEBUG, "", e2);
                            }
                    }
                } else {
                    prop = null;
                }
            } catch (Exception ex) {
                log.log(Level.DEBUG, "", ex);
                prop = null;
            }
        }
        
        if (prop == null) {
            try {
                prop = PropertyLoader.loadProperties("org/miloss/fgsms/agentcore/fgsms-agent");
                log.log(Level.INFO, "Agent configuration loaded from embedded config file within fgsms-agentCore.jar. Use -Dorg.miloss.fgsms.agentConfigFileOverride=path/to/config.properties to override or include the file fgsms-agent.properties in the classpath." + Constants.Version);
            } catch (Exception ex) {
                log.log(Level.FATAL, "Could not load the fgsms-agent.properties files (embedded)" + Constants.Version, ex);
//                LastErrorMessage = "coult not load fgsms-agent.properties file." + ex.getMessage();
            }
        }
        if (prop == null) {
            throw new ConfigurationException("Cannot locate the fgsms-agent.properties file");
        }
        return prop;
    }
    /**
     * loads the config from the properties file. Uses first the System property
     * org.miloss.fgsms.agentConfigFileOverride, then performs a class loader
     * lookup for fgsms-agent.properties and then finally loads
     * org/miloss/fgsms/agentcore/fgsms-agent from within this jar. This allows
     * for flexiblity in specifying where the properties file is located.
     *
     * @throws ConfigurationException
     */
    public ConfigLoader() throws ConfigurationException {
        this.prop = loadProperties();
        load();
        
    }
    public Algorithm getRSAalgo() {
        return RSAalgo;
    }
    public int getRSRetryCount() {
        return RSRetryCount;
    }
    public List<String> getRS_URLS() {
        return RS_URLS;
    }

    public int getACSARetryCount() {
        return ACSARetryCount;
    }

    public List<String> getACSA_URLS() {
        return ACSA_URLS;
    }

    public Algorithm getACSAalgo() {
        return ACSAalgo;
    }

    public int getACSRetryCount() {
        return ACSRetryCount;
    }

    public List<String> getACS_URLS() {
        return ACS_URLS;
    }

    public Algorithm getACSalgo() {
        return ACSalgo;
    }

    public Algorithm getARSAalgo() {
        return ARSAalgo;
    }

    public int getARSRetryCount() {
        return ARSRetryCount;
    }

    public List<String> getARS_URLS() {
        return ARS_URLS;
    }

    public AutomatedReportingService getArsport() {
        return arsport;
    }

    public ReportingService getRsport() {
        return rsport;
    }

    public String getJavaxkeystore() {
        return javaxkeystore;
    }

    /**
     * encrypted password to the key store
     *
     * @return
     */
    public String getJavaxkeystorepass() {
        return javaxkeystorepass;
    }

    public String getJavaxtruststore() {
        return javaxtruststore;
    }

    /**
     * encrypted password to the trust store
     *
     * @return
     */
    public String getJavaxtruststorepass() {
        return javaxtruststorepass;
    }

    public int getDCSRetryCount() {
        return DCSRetryCount;
    }

    public List<String> getDCS_URLS() {
        return DCS_URLS;
    }

    public Algorithm getDCSalgo() {
        return DCSalgo;
    }

    public int getPCSRetryCount() {
        return PCSRetryCount;
    }

    public List<String> getPCS_URLS() {
        return PCS_URLS;
    }

    public Algorithm getPCSalgo() {
        return PCSalgo;
    }

    public int getSSRetryCount() {
        return SSRetryCount;
    }

    public List<String> getSS_URLS() {
        return SS_URLS;
    }

    public Algorithm getSSalgo() {
        return SSalgo;
    }

    public UnavailableBehavior getBehavior() {
        return behavior;
    }

    public String getCertinfo() {
        return certinfo;
    }

    public SecurityWrapper getClasslevel() {
        return classlevel;
    }

    public DCS getDcsport() {
        return dcsport;
    }

    public DataCollectorService getDcsservice() {
        return dcsservice;
    }

    public long getDiscoveryInterval() {
        return discoveryInterval;
    }

    public AuthMode getMode_() {
        return mode_;
    }

    public String getOfflinestorage() {
        return offlinestorage;
    }

    /**
     * returns the encrypted password that the agent will use to connect to the
     * fgsms server as
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * an instance of the policy config service client
     *
     * @return
     */
    public PCS getPcsport() {
        return pcsport;
    }

    /**
     * an instance of the policy config service client
     *
     * @return
     */
    public PolicyConfigurationService getPcsservice() {
        return pcsservice;
    }

    /**
     * gets a references to the properties file for settings not covered in the
     * config loads, such as UDDI or FDS
     *
     * @return
     */
    public Properties getProp() {
        return prop;
    }

    /**
     * an instance of the status service client
     *
     * @return
     */
    public StatusServiceService getSs() {
        return ss;
    }

    /**
     * an instance of the status service client
     *
     * @return
     */
    public StatusService getSsport() {
        return ssport;
    }

    /**
     * the username used for this agent to connect to the fgsms server as
     *
     * @return
     */
    public String getUsername() {
        return username;
    }


    private void createClientProxies() throws ConfigurationException {
        String LastErrorMessage = "";
        /*

         if (mode_ == org.miloss.fgsms.common.Constants.AuthMode.UsernamePassword && (Utility.stringIsNullOrEmpty(username) || Utility.stringIsNullOrEmpty(password))) {
         LastErrorMessage = "Username/password auth mode set but the username/password is not specified";
         fatalerror = true;
         }
         if (mode_ == org.miloss.fgsms.common.Constants.AuthMode.PKI && (Utility.stringIsNullOrEmpty(javaxkeystorepass))) {
         LastErrorMessage = "PKI auth set but no keystore or password was specified";
         fatalerror = true;
         }
         */
        boolean fatalerror = false;
        try {
            pcsservice = new PolicyConfigurationService();
            pcsport = pcsservice.getPCSPort();
        } catch (Exception ex) {
            fatalerror = true;
            LastErrorMessage = "fgsms config loader, error caught initializing PCS client proxies. Unable to send data. Check the fgsms Agent properties file and ensure that the PCS WSDL location URL is defined and that the JAX-WS runtime is installed and configured within this container." + ex.getMessage();
            log.log(Level.WARN, null, ex);
        }
        try {
            dcsservice = new DataCollectorService();
            dcsport = dcsservice.getDCSPort();
        } catch (Exception ex) {
            fatalerror = true;
            LastErrorMessage = "fgsms config loader, error caught initializing DCS client proxies. Unable to send data. Check the fgsms Agent properties file and ensure that the DCS WSDL location URL is defined and that the JAX-WS runtime is installed and configured within this container." + ex.getMessage();
            log.log(Level.WARN, null, ex);

        }
        try {
            ss = new StatusServiceService();
            ssport = ss.getStatusServicePort();
        } catch (Exception ex) {
            LastErrorMessage = "fgsms config loader, error caught initializing SS client proxies. Unable to send data. Check the fgsms Agent properties file and ensure that the DCS WSDL location URL is defined and that the JAX-WS runtime is installed and configured within this container." + ex.getMessage();
            log.log(Level.WARN, null, ex);
        }

        try {
            rs = new ReportingService_Service();
            rsport = rs.getReportingServicePort();
        } catch (Exception ex) {
            LastErrorMessage = "fgsms config loader, error caught initializing RS client proxies. Unable to send data. Check the fgsms Agent properties file and ensure that the JAX-WS runtime is installed and configured within this container." + ex.getMessage();
            log.log(Level.WARN, null, ex);
        }

        try {
            ars = new AutomatedReportingService_Service();
            arsport = ars.getAutomatedReportingServicePort();
        } catch (Exception ex) {
            LastErrorMessage = "fgsms config loader, error caught initializing ARS client proxies. Unable to send data. Check the fgsms Agent properties file and ensure that the JAX-WS runtime is installed and configured within this container." + ex.getMessage();
            log.log(Level.WARN, null, ex);
        }

        try {
            das = new DataAccessService_Service();
            dasport = das.getDASPort();
        } catch (Exception ex) {
            LastErrorMessage = "fgsms config loader, error caught initializing DAS client proxies. Unable to send data. Check the fgsms Agent properties file and ensure that the JAX-WS runtime is installed and configured within this container." + ex.getMessage();
            log.log(Level.WARN, null, ex);
        }

        if (!Utility.stringIsNullOrEmpty(LastErrorMessage)) {
            log.log(Level.WARN, LastErrorMessage);
        }
        if (fatalerror) {
            throw new ConfigurationException(LastErrorMessage);
        }
    }

    private void load() throws ConfigurationException {

        if (prop == null) {
            throw new ConfigurationException("Cannot locate the fgsms-agent.properties file");
        }
        createClientProxies();

        try {
            behavior = UnavailableBehavior.valueOf(prop.getProperty("agent.unavailablebehavior").trim());
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
            behavior = UnavailableBehavior.PURGE;
        }
        try {
            username = prop.getProperty("fgsms.AuthMode.Username").trim();
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }
        try {
            password = prop.getProperty("fgsms.AuthMode.Password").trim();
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }
        try {
            certinfo = prop.getProperty("fgsms.AuthMode.PKICert");
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }

        try {
            mode_ = org.miloss.fgsms.common.Constants.AuthMode.valueOf(prop.getProperty("fgsms.AuthMode").trim());
        } catch (Exception ex) {
            mode_ = AuthMode.UsernamePassword;
            log.log(Level.WARN, null, ex);
        }

        try {
            offlinestorage = prop.getProperty("agent.offlinestorage").trim();
            if (behavior == UnavailableBehavior.HOLDPERSIST) {
                EnsureFolderExists(offlinestorage);
            }
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }
        try {
            PCSalgo = Algorithm.valueOf(prop.getProperty("policyconfigurationservice.algorithm").trim());
        } catch (Exception ex) {
            PCSalgo = Algorithm.FAILOVER;
            log.log(Level.WARN, null, ex);
        }
        try {
            DCSalgo = Algorithm.valueOf(prop.getProperty("datacollectorservice.algorithm").trim());
        } catch (Exception ex) {
            DCSalgo = Algorithm.FAILOVER;
            log.log(Level.WARN, null, ex);
        }
        try {
            SSalgo = Algorithm.valueOf(prop.getProperty("statusservice.algorithm").trim());
        } catch (Exception ex) {
            SSalgo = Algorithm.FAILOVER;
            log.log(Level.WARN, null, ex);
        }

        try {
            DCSRetryCount = Integer.parseInt(prop.getProperty("datacollectorservice.retry").trim());
        } catch (Exception ex) {
            DCSRetryCount = 2;
            log.log(Level.WARN, null, ex);
        }
        try {
            SSRetryCount = Integer.parseInt(prop.getProperty("statusservice.retry").trim());
        } catch (Exception ex) {
            SSRetryCount = 2;
            log.log(Level.WARN, null, ex);
        }

        try {
            PCSRetryCount = Integer.parseInt(prop.getProperty("policyconfigurationservice.retry").trim());
        } catch (Exception ex) {
            PCSRetryCount = 2;
            log.log(Level.WARN, null, ex);
        }
        try {
            discoveryInterval = Integer.parseInt(prop.getProperty("discovery.interval").trim());
        } catch (Exception ex) {
            log.log(Level.WARN, null, ex);
        }
        String t = prop.getProperty("policyconfigurationservice.URL");
        if (t.contains("|")) {
            String[] ts = t.split("\\|");
            for (int i = 0; i < ts.length; i++) {
                PCS_URLS.add(ts[i]);
            }
        } else {
            PCS_URLS.add(t);
        }

        t = prop.getProperty("datacollectorservice.URL");
        if (t.contains("|")) {
            String[] ts = t.split("\\|");
            for (int i = 0; i < ts.length; i++) {
                DCS_URLS.add(ts[i]);
            }
        } else {
            DCS_URLS.add(t);
        }
        t = prop.getProperty("statusservice.URL");
        if (t.contains("|")) {
            String[] ts = t.split("\\|");
            for (int i = 0; i < ts.length; i++) {
                SS_URLS.add(ts[i]);
            }
        } else {
            SS_URLS.add(t);
        }
        t = prop.getProperty("fgsms.TrustStore.Password");
        if (!Utility.stringIsNullOrEmpty(t)) {
            this.javaxtruststorepass = t;
            URL url = null;
            try {
                url = Thread.currentThread().getContextClassLoader().getResource("/META-INF/truststore.jks");
            } catch (Exception ex) {
            }
            if (url == null) {
                try {
                    url = Thread.currentThread().getContextClassLoader().getResource("META-INF/truststore.jks");
                } catch (Exception ex) {
                }
            }
            if (url != null) {
                this.javaxtruststore = url.toString();
                log.log(Level.INFO, "Loading truststore from " + javaxtruststore);
            }
        }
        discover_providers = new ArrayList<String>();
        t = prop.getProperty("discovery.impl");
        if (!Utility.stringIsNullOrEmpty(t)) {
            String[] s = t.split(",");
            discover_providers.addAll(Arrays.asList(s));
        }
        t = prop.getProperty("fgsms.KeyStore.Password");
        if (!Utility.stringIsNullOrEmpty(t)) {
            this.javaxkeystorepass = t;
            URL url = null;
            try {
                url = Thread.currentThread().getContextClassLoader().getResource("/META-INF/key.jks");
            } catch (Exception ex) {
            }
            if (url == null) {
                try {
                    url = Thread.currentThread().getContextClassLoader().getResource("META-INF/key.jks");

                } catch (Exception ex) {
                }
            }
            if (url != null) {
                this.javaxkeystore = url.toString();
                log.log(Level.INFO, "Loading keystore from " + javaxkeystore);
            }
        }

    }

    /**
     * Returns a list of strings representing the class names of discovery
     * providers
     *
     * @return
     */
    public List<String> getDiscovery_providers() {
        return discover_providers;
    }

    public long getDeadMessageQueueDuration() {
        String s = prop.getProperty("message.processor.dead.message.queue.duration");
        long ret = 10000;
        if (!Utility.stringIsNullOrEmpty(s)) {
            try {
                ret = Long.parseLong(s);
            } catch (Exception ex) {
                log.warn("error parsing message.processor.dead.message.queue.duration");
            }
        }

        if (ret < 10000) {
            ret = 10000;
        }
        return ret;
    }

    public boolean isDependencyInjectionEnabled() {
        try {
            return Boolean.parseBoolean(prop.getProperty("agent.dependencyinjection.enabled"));
        } catch (Exception ex) {
            log.warn("error parsing agent.dependencyinjection.enabled");
        }
        return false;
    }

    HashMap getWSAgentIgnoreList() {
        HashMap IgnoreList = new HashMap();
        String t = prop.getProperty("message.processor.ignoreList");
        if (!Utility.stringIsNullOrEmpty(t)) {
            if (t.contains("|")) {
                String[] ts = t.split("\\|");
                for (int i = 0; i < ts.length; i++) {
                    IgnoreList.put(ts[i].toLowerCase(), false);
                }
            } else {
                IgnoreList.put(t.toLowerCase(), false);
            }
        }
        return IgnoreList;
    }
    private void EnsureFolderExists(String folder) throws ConfigurationException {
        File f = null;
        try {
            f = new File(folder);
            if (f.exists()) {
                return;
            }
        } catch (Exception ex) {
        }
        try {
            new File(folder).mkdirs();
        } catch (Exception ex) {
            throw new ConfigurationException("Cannot ensure that the folder " + folder + " exists");
        }
    }

    public enum Algorithm {

        FAILOVER, ROUNDROBIN
    }

    public enum UnavailableBehavior {

        /**
         * PURGE, if I can't send it, throw it out
         */
        PURGE,
        /**
         * HOLD, if I can't send it, keep it in memory until I can or until I
         * get destroyed by the garbage collector
         */
        HOLD,
        /**
         * HOLDPERSIST, if I can't send it, store it to disk, then send when
         * it's available
         */
        HOLDPERSIST
    }

}
