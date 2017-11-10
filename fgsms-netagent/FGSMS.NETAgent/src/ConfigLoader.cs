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
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Configuration;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.Net;
using System.Collections.ObjectModel;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using System.Diagnostics;
using System.Web.Configuration;
using System.Collections.Specialized;

namespace org.miloss.fgsms.agent
{
    /// <summary>
    /// ConfigLoader loads configuration information for .NET based services useing FGSMS .NET agents from the current .net framework machine.config or from the FGSMS ACS service
    /// </summary>
    public class ConfigLoader
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ConfigLoader"/> class from the local machine config
        /// Note, there may be four machine config files per machine depending on the version of dotnet install and processor architecture
        /// throws an exception when authmode is specified and credentials are not
        /// throws an exception when no urls are specified for dcs and pcs and we are not operating in offline mode
        /// </summary>
        public ConfigLoader()
        {
            try
            {
                Configuration machineconfig = ConfigurationManager.OpenMachineConfiguration();
                LoadFrom(SimpleConfig.LoadFrom(machineconfig));
            }catch (Exception ex)
            {
                Console.Out.WriteLine(ex);
            }
            try
            {
                Configuration localApp = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
                LoadFrom(SimpleConfig.LoadFrom(localApp));
            }
            catch (Exception ex) {
                //Logger.error(ex, "Error caught loading the config");
                Console.Out.WriteLine(ex);
            }
            try {
                System.Configuration.Configuration configuration = null;
                if (System.Web.HttpContext.Current != null) //this is null in wcf services running in iis express and probably self hosted services
                {
                    configuration =
                        System.Web.Configuration.WebConfigurationManager.OpenWebConfiguration("~");
                    LoadFrom(SimpleConfig.LoadFrom(configuration));
                }
            }
            catch (Exception ex){
                //Logger.error(ex, "Error caught loading the config");
                Console.Out.WriteLine(ex);

            }
            if (WebConfigurationManager.AppSettings != null)
            {
                LoadFrom(SimpleConfig.LoadFrom(WebConfigurationManager.AppSettings));
            }
           
               
          
          
            ValidateConfig();
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="ConfigLoader"/> class from a <see cref="SimpleConfig"/> option
        /// Note, there may be four machine config files per machine depending on the version of dotnet install and processor architecture
        /// throws an exception when authmode is specified and credentials are not
        /// throws an exception when no urls are specified for dcs and pcs and we are not operating in offline mode
        /// </summary>
        public ConfigLoader(SimpleConfig cfg)
        {
            LoadFrom(cfg);
            ValidateConfig();
        }

        private void ValidateConfig()
        {
            if (ServiceUnavailableBehavior == UnavailableBehavior.HOLDPERSIST && String.IsNullOrEmpty(PersistLocation))
            {
                Logger.error("Invalid configuration, The config is set to hold and persist data on disk, yet the persist locaiton is empty. Persistence storage will not be available. Default to PURGE mode");

                ServiceUnavailableBehavior = UnavailableBehavior.PURGE;

            }
            if (ServiceUnavailableBehavior == UnavailableBehavior.HOLDPERSIST && !String.IsNullOrEmpty(PersistLocation))
            {
                EnsureFolderExists(PersistLocation);
            }

            if ((dcsurl.Count == 0 || pcsurl.Count == 0) && operatingmode == OperationMode.ONLINE && (!UddiEnabled && !DNSEnabled))
                throw new ConfigurationErrorsException("FGSMS PCS or DCS urls are either not defined or malformed in the machine config AND all discovery mechanisms are disabled AND we are in ONLINE operating mode. Monitoring is not possible");
            if (authMode == AuthMode.usernamePassword && (String.IsNullOrEmpty(username) || String.IsNullOrEmpty(password)))
                throw new ConfigurationErrorsException("Username and password authentication is specified but the username or password is blank");
            if (authMode == AuthMode.PKI && String.IsNullOrEmpty(pkiinfo))
                throw new ConfigurationErrorsException("PKI authentication is specified but the PKI info is blank");

        }

        
        private void LoadFrom(SimpleConfig config)
        {
            try
            {
                configfilepath = config.config_loc;

                try
                {

                    string x = config.appSettings[IgnoreListKey];
                    if (!String.IsNullOrEmpty(x))
                        ignoreList.AddRange(x.Trim().Split('|'));
                }
                catch { }

                try
                {
                    _logLevel = (LogLevel)Enum.Parse(typeof(LogLevel), config.appSettings[LogLevelKey]);
                }
                catch {
                    _logLevel = LogLevel.WARN;
                }

                try
                {
                    string x = config.appSettings[AuthModePasswordKey];

                    if (!String.IsNullOrEmpty(x))
                        password = x;
                }
                catch { }
                try
                {
                    string x = config.appSettings[AuthModeUsernameKey];

                    if (!String.IsNullOrEmpty(x))
                        username = x;
                }
                catch { }
                try
                {
                    string x = config.appSettings[AuthModePKIInfoKey];

                    if (!String.IsNullOrEmpty(x))
                        pkiinfo = x;
                }
                catch { }



                try
                {
                    string x = config.appSettings[DCSUrlKey];

                    if (!String.IsNullOrEmpty(x))
                        dcsurl.AddRange(x.Trim().Split('|'));
                }
                catch { }
                try
                {
                    string x = config.appSettings[SSUrlKey];

                    if (!String.IsNullOrEmpty(x))
                        ssurl.AddRange(x.Trim().Split('|'));
                }
                catch { }
                try
                {
                    string x = config.appSettings[PCSUrlKey];
                    if (!String.IsNullOrEmpty(x))
                        pcsurl.AddRange(x.Trim().Split('|'));
                }
                catch { }

                try
                {
                    DCSalgo = (Algorithm)Enum.Parse(typeof(Algorithm), config.appSettings[UddiFindTypekey]);
                }
                catch { }
                try
                {
                    DCSalgo = (Algorithm)Enum.Parse(typeof(Algorithm), config.appSettings[DCSlagokey]);
                }
                catch { }
                try
                {
                    PCSalgo = (Algorithm)Enum.Parse(typeof(Algorithm), config.appSettings[PCSlagokey]);
                }
                catch { }
                try
                {
                    authMode = (AuthMode)Enum.Parse(typeof(AuthMode), config.appSettings[AuthModeKey]);
                }
                catch { }
                try
                {
                    DeadMessageDuration = (long)long.Parse(config.appSettings[DeadMsgkey]);
                }
                catch { }
                try
                {
                    DependencyInjectionEnabled = (bool)bool.Parse(config.appSettings[DepInjkey]);
                }
                catch { }
                try
                {
                    UddiEnabled = (bool)bool.Parse(config.appSettings[UddiEnabledKey]);
                }
                catch { }

                try
                {
                    DNSEnabled = (bool)bool.Parse(config.appSettings[DNSEnabledKey]);
                }
                catch { }


                try
                {
                    ServiceUnavailableBehavior = (UnavailableBehavior)Enum.Parse(typeof(UnavailableBehavior), config.appSettings[AgentBevKey]);
                }
                catch { }
                try
                {
                    DCSretrycount = Int32.Parse(config.appSettings[DCSlretrykey]);
                }
                catch { }
                try
                {
                    PCSretrycount = Int32.Parse(config.appSettings[PCSlretrykey]);
                }
                catch { }
                try
                {
                    DiscoveryInterval = Int32.Parse(config.appSettings[discoveryInterval]);
                }
                catch { }
                try
                {
                    UddiInquiryUrl = (config.appSettings[UddiURLKey]);
                }
                catch { }
                try
                {
                    UddiSecurityUrl = (config.appSettings[UddiSecUrlKey]);
                }
                catch { }
                try
                {
                    UddiAuthRequired = bool.Parse(config.appSettings[UddiAuthKey]);
                }
                catch { }

                try
                {
                    UddiUsername = (config.appSettings[UddiUsernameKey]);
                }
                catch { }
                try
                {
                    UddiEncryptedPassword = (config.appSettings[UddipwdKey]);
                }
                catch { }
                try
                {
                    UddiDCSLookup = (config.appSettings[UddiDCSkey]);
                }
                catch { }
                try
                {
                    UddiPCSLookup = (config.appSettings[UddiPCSkey]);
                }
                catch { }
                try
                {
                    _uddifindType = (UddiFindType)Enum.Parse(typeof(UddiFindType), (config.appSettings[UddiFindTypekey]));
                }
                catch { }

                try
                {
                    operatingmode = (OperationMode)Enum.Parse(typeof(OperationMode), (config.appSettings[operatingModeKey]));
                }
                catch { }


                try
                {
                    PersistLocation = (config.appSettings[PersistKey]);
                }
                catch { }

            }
            catch (Exception)
            {

            }

        }

        private void EnsureFolderExists(string PersistLocation)
        {
            try
            {
                if (!Directory.Exists(PersistLocation))
                    Directory.CreateDirectory(PersistLocation);
            }
            catch (Exception ex)
            {
                Logger.warn(ex, "FGSMS.MessageProcessor, Invalid configuration, the folder " + PersistLocation + " does not exist and I can't create it. Persistence storage will not be available. Defaulting to PURGE mode");

                ServiceUnavailableBehavior = UnavailableBehavior.PURGE;
            }
        }


        /// <summary>
        /// Initializes a new instance of the <see cref="ConfigLoader"/> class using a mapped machine config
        /// throws an exception when authmode is specified and credentials are not
        /// throws an exception when no urls are specified for dcs and pcs and we are not operating in offline mode
        /// </summary>
        public ConfigLoader(Configuration machineconfig)
        {
            LoadFrom(SimpleConfig.LoadFrom(machineconfig));
            ValidateConfig();
        }
        /*

        /// <summary>
        /// PROTOTYPE A convience wrapper that uses DNS to discovery the endpoint of the FGSMS Agent Config Service (ACS)
        /// then fetches, decrypts, parses and finally loads a ConfigLoader object using those settings.
        /// May return null if DNS discovery fails or if the ACS fails to return a configuration
        /// </summary>
        /// <returns></returns>
        public static ConfigLoader LoadFromACSviaDNSLookup()
        {
            ConfigLoader cfg = null;
            List<string> urls = DNSDiscovery.DiscoverViaDNS(DNSDiscovery.DiscoverType.ACS);
            for (int i = 0; i < urls.Count; i++)
            {
                ACSBinding acs = null;
                try
                {
                    acs = ConfigLoader.GetACSProxy(urls[i]);
                    GetCurrentConfigurationRequestMsg r = new GetCurrentConfigurationRequestMsg();
                    r.classification = new SecurityWrapper();
                    r.agentType = "dotnet";
                    GetCurrentConfigurationResponseMsg res = acs.GetCurrentConfiguration(r);
                    if (res != null && res.configuration != null && res.configuration.Length > 0)
                    {
                        Util u = new Util();
                        String conf = u.DE(Encoding.UTF8.GetString(res.configuration));
                        SimpleConfig sc = SimpleConfig.LoadFrom(conf);
                        cfg = new ConfigLoader(sc);
                        cfg.configfilepath = urls[i];
                    }
                }
                catch (Exception ex)
                {
                    Logger.warn(ex, "Error loading ACS config from " + urls[i]);
                }
                finally
                {
                    try
                    {
                        ((IClientChannel)acs).Close();
                        ((IClientChannel)acs).Dispose();
                    }
                    catch { }
                }
                if (cfg != null)
                    break;
            }

            return null;
        }*/
        /// <summary>
        /// creates an empty config object, used for creating a fresh config and savinging to disk
        /// </summary>
        /// <param name="load"></param>
        public ConfigLoader(bool load)
        {

        }
        public bool UddiAuthRequired = false;
        public string UddiInquiryUrl = "";
        public string UddiSecurityUrl = "";
        public string UddiUsername = "";
        public string UddiEncryptedPassword = "";
        public string UddiDCSLookup = "";
        public string UddiPCSLookup = "";
        public string PersistLocation = "";
        
        private Util util = new Util();
        private string configfilepath = "";

        /// <summary>
        /// path of the current machine.config file
        /// </summary>
        public string ConfigPath
        {
            get { return configfilepath; }
        }
        public enum Algorithm
        {
            FAILOVER, ROUNDROBIN
        }
        public enum LogLevel
        {
            DEBUG=0, INFO=1, WARN=2, ERROR=3
        }
        public enum UnavailableBehavior
        {
            HOLD, PURGE, HOLDPERSIST
        }

        public enum OperationMode
        {
            ONLINE, OFFLINE
        }

        public enum UddiFindType
        {
            EndpointBindingKey, EndpointKeyWord, ServiceKey
        }
        private UddiFindType _uddifindType;
        public UddiFindType uddiFindType
        {
            get { return _uddifindType; }
        }

        internal List<string> dcsurl = new List<string>();
        internal List<string> pcsurl = new List<string>();
        internal List<string> ssurl = new List<string>();

        public ReadOnlyCollection<string> DCSurls
        {
            get { return dcsurl.AsReadOnly(); }
        }

        public ReadOnlyCollection<string> PCSurls
        {
            get { return pcsurl.AsReadOnly(); }
        }

        public ReadOnlyCollection<string> SSurls
        {
            get { return ssurl.AsReadOnly(); }
        }

        private List<string> ignoreList = new List<string>();
        public bool UddiEnabled = false;
        public bool DNSEnabled = false;
        public Algorithm DCSalgo = Algorithm.FAILOVER;
        public Algorithm PCSalgo = Algorithm.FAILOVER;
        public int DCSretrycount = 2;
        public int PCSretrycount = 2;

        private string username;
        private string password;
        private string pkiinfo;
        private OperationMode operatingmode = OperationMode.ONLINE;
        public OperationMode OperatingMode
        {
            get { return operatingmode; }
        }

        /// <summary>
        /// This is the lookup key for finding the certificate that this agent will use to authenticate back to FGSMS
        /// </summary>
        public string CertificateInfo
        {
            get { return pkiinfo; }
        }

        /// <summary>
        /// get's the current log level
        /// </summary>
        public LogLevel GetLogLevel
        {
            get { return _logLevel; }
        }

        /// <summary>
        /// The username that this agent will use to connect to the FGSMS services
        /// </summary>
        public string AgentUsername
        {
            get
            {
                return username;
            }
        }


        /// <summary>
        /// The encrypted password that this agent will use to connect to the FGSMS services
        /// </summary>
        public string AgentPassword
        {
            get
            {
                return password;
            }
        }
        private AuthMode authMode = AuthMode.None;
        public AuthMode AuthenticationMode
        {
            get
            {
                return authMode;
            }
        }
        /// <summary>
        /// Auth mode from this agent to FGSMS DCS and PCS services
        /// None, Transport, and TransportCredentialOnly only supported
        /// 
        /// TransportCredentialOnly == HTTP BASIC == FGSMS UsernamePassword auth
        /// </summary>
        // public BasicHttpSecurityMode AuthMode = BasicHttpSecurityMode.None;

        /// <summary>
        /// # Discovery Failover - if enabled, a list of URLs will be periodically loaded and dynamically ADDED to the list from the config file for each service
        /// default is 600000ms
        /// </summary>
        public int DiscoveryInterval = 600000;

        /// <summary>
        /// #################### Performance Tuning #########################
        /// #time in ms in which a message is declared dead. This occurs when an request comes in, but a response is not returned
        /// # sometimes is this a really slow service, in which may what to think about call backs, @OneWay transactions, the 
        /// # message is dropped for some reason, or WSDL requests
        /// # if the server is particularlly busy, decrease this value to prevent memory issues, default is 600000 = 10 minutes
        /// # minimum value = 10000 = 10 seconds, anything less and 10 seconds will be used. If it's not specified, 10 minutes is used
        /// </summary>
        /// 
        public long DeadMessageDuration = 600000;


        /// <summary>
        /// ################### Dependency Injection #########################
        /// #default value = true, only supported on .NET and JbossWS 3.3.0 or higher
        /// </summary>
        /// 
        public bool DependencyInjectionEnabled = true;

        /// <summary>
        /// if FGSMS's PCS and/or DCS is not available, what should the agent do?
        /// values: purge = purge the queues and drop all messages until PCS/DCS is avaiable, then resume normal operations
        /// values: hold = hang on to the data until PCS/DCS is available again, if this container goes down, data still in the queue will be lost
        /// values: (future use only, do not use) holdpersist = hang on to the data until PCS/DCS is available again, if this container goes down, data will be stored in a temp file
        /// </summary>
        public UnavailableBehavior ServiceUnavailableBehavior = UnavailableBehavior.PURGE;

        public enum AuthMode
        {
            None, usernamePassword, PKI
        }

        public readonly static string operatingModeKey = "org.miloss.fgsms.agent.operatingmode";
        public readonly static string PersistKey = "org.miloss.fgsms.agent.persistlocation";
        public readonly static string DeadMsgkey = "org.miloss.fgsms.message.processor.dead.message.queue.duration";
        public readonly static string DCSUrlKey = "org.miloss.fgsms.DCS.URL";
        public readonly static string SSUrlKey = "org.miloss.fgsms.SS.URL";
        public readonly static string PCSUrlKey = "org.miloss.fgsms.PCS.URL";
        public readonly static string AuthModeKey = "org.miloss.fgsms.agent.AuthMode";
        public readonly static string AuthModePasswordKey = "org.miloss.fgsms.agent.AuthModePassword";
        public readonly static string AuthModeUsernameKey = "org.miloss.fgsms.agent.AuthModeUsername";
        public readonly static string AuthModePKIInfoKey = "org.miloss.fgsms.agent.AuthModePKIInfo";

        public readonly static string IgnoreListKey = "org.miloss.fgsms.IgnoreList";
        public readonly static string DepInjkey = "org.miloss.fgsms.agent.dependencyinjection.enabled";

        public readonly static string AgentBevKey = "org.miloss.fgsms.agent.unavailablebehavior";
        public readonly static string PCSlagokey = "org.miloss.fgsms.agent.policyconfigurationservice.algorithm";
        public readonly static string PCSlretrykey = "org.miloss.fgsms.agent.policyconfigurationservice.retry";
        public readonly static string DCSlagokey = "org.miloss.fgsms.agent.datacollectorservice.algorithm";
        public readonly static string DCSlretrykey = "org.miloss.fgsms.agent.datacollectorservice.retry";


        /// <summary>
        /// 6.2 changed to support multiple levels
        /// </summary>
        public readonly static string LogLevelKey= "org.miloss.org.miloss.fgsms.loglevel";


        public readonly static string DNSEnabledKey = "org.miloss.fgsms.discovery.dns.enabled";


        public readonly static string UddiEnabledKey = "org.miloss.fgsms.discovery.uddi.enabled";
        public readonly static string UddiURLKey = "org.miloss.fgsms.discovery.uddi.inquiry.url";
        public readonly static string UddiAuthKey = "org.miloss.fgsms.discovery.uddi.inquiry.authrequired";
        public readonly static string UddiSecUrlKey = "org.miloss.fgsms.discovery.uddi.security.url";
        public readonly static string UddiUsernameKey = "org.miloss.fgsms.discovery.uddi.security.username";
        public readonly static string UddipwdKey = "org.miloss.fgsms.discovery.uddi.security.password";
        public readonly static string UddiFindTypekey = "org.miloss.fgsms.discovery.uddi.lookup.findType";
        public readonly static string UddiDCSkey = "org.miloss.fgsms.discovery.uddi.lookup.dcs";
        public readonly static string UddiPCSkey = "org.miloss.fgsms.discovery.uddi.lookup.pcs";

        public readonly static string discoveryInterval = "org.miloss.fgsms.discovery.interval";
        /// <summary>
        /// ################### Agent Ignore List ############################
        /// sometimes there are services that we wish to NOT any data on, perhaps it's a very chatty service or something
        /// that we just aren't interested in recording data on. Pipe "|" delimited.
        /// message.processor.ignoreList=http://url/service1|http://url/service2
        /// </summary>
        ///   

        public ReadOnlyCollection<string> IgnoreList
        {
            get
            {
                return ignoreList.AsReadOnly();
            }
        }
        private List<string> IgnoreListInternal
        {
            get
            {
                return ignoreList;
            }
        }
        
        
        /// <summary>
        /// This function will return a proxy to the DCS service using the provided username and password
        /// OR if FGSMS is configured for PKI auth, the certinfo field will be used to search for a certificate
        /// </summary>
        /// <param name="url"></param>
        /// <param name="Myusername"></param>
        /// <param name="Mypassword"></param>
        /// <param name="certinfo"></param>
        /// <returns></returns>
        public DCSBinding GetDCSProxy(string url, string Myusername, string Mypassword, string certinfo)
        {
            ServicePointManager.Expect100Continue = false;
            DCSBinding r = new DCSBinding();
            r.Url = url;
            switch (authMode)
            {
                case AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(Myusername, Mypassword);
                    break;
                case AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(certinfo));
                    break;
            }
            return r;
        }

        public DCSBinding GetDCSProxy(string url)
        {
            ServicePointManager.Expect100Continue = false;

            DCSBinding r = new DCSBinding();
            r.Url = url;
            switch (authMode)
            {
                case AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(username, util.DE(password));
                    break;
                case AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(pkiinfo));
                        break;
            }
            return r;
        }

        /// <summary>
        /// This function will return a proxy to the PCS service using the provided username and password
        /// OR if FGSMS is configured for PKI auth, the certinfo field will be used to search for a certificate
        /// </summary>
        /// <param name="url"></param>
        /// <param name="Myusername"></param>
        /// <param name="Mypassword"></param>
        /// <param name="certinfo"></param>
        /// <returns></returns>
        public PCSBinding GetPCSProxy(string url, string Myusername, string Mypassword, string certinfo)
        {
            ServicePointManager.Expect100Continue = false;
            PCSBinding r = new PCSBinding();
            r.Url = url;
            switch (authMode)
            {
                case AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(Myusername, Mypassword);
                    break;
                case AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(certinfo));
                    break;
            }
            return r;
        }


        public PCSBinding GetPCSProxy(string url)
        {
            ServicePointManager.Expect100Continue = false;
            PCSBinding r = new PCSBinding();
            r.Url = url;
            switch (authMode)
            {
                case AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(username, util.DE(password));
                    break;
                case AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(pkiinfo));
                    break;
            }
            return r;
        
        }


        /// <summary>
        /// This function will return a proxy to the Status service using the provided username and password
        /// OR if FGSMS is configured for PKI auth, the certinfo field will be used to search for a certificate
        /// </summary>
        /// <param name="url"></param>
        /// <param name="Myusername"></param>
        /// <param name="Mypassword"></param>
        /// <param name="certinfo"></param>
        /// <returns></returns>
        public statusServiceBinding GetSSProxy(string url, string Myusername, string Mypassword, string certinfo)
        {
            ServicePointManager.Expect100Continue = false;
            statusServiceBinding r = new statusServiceBinding();
            r.Url = url;
            switch (authMode)
            {
                case AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(Myusername, Mypassword);
                    break;
                case AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(certinfo));
                    break;
            }
            return r;
        }



      

        private System.Security.Cryptography.X509Certificates.X509Certificate2 FindCert(string pkiinfo)
        {
            System.Security.Cryptography.X509Certificates.X509Store store = new System.Security.Cryptography.X509Certificates.X509Store(System.Security.Cryptography.X509Certificates.StoreName.My, System.Security.Cryptography.X509Certificates.StoreLocation.LocalMachine);
            store.Open(System.Security.Cryptography.X509Certificates.OpenFlags.ReadOnly);
            X509Certificate2Collection col = store.Certificates.Find(System.Security.Cryptography.X509Certificates.X509FindType.FindBySerialNumber, pkiinfo, true);
            store.Close();
            if (col != null && col.Count > 0)
            {
                //if (col.Count > 1)

                return col[0];
            }
            throw new ConfigurationErrorsException("no certificates were found matching that serial number");
        }



        LogLevel _logLevel = LogLevel.WARN;
        /// <summary>
        /// if true, additional debugging information is written to the log
        /// </summary>
        public bool DEBUG
        {
            get { return _logLevel== LogLevel.DEBUG; }
        }


    }

    /// <summary>
    /// simple config is an adapter that allows for loading appsetings from either the machine config section, the local app settings collection or from a remote
    /// app settings from the FGSMS ACS service.
    /// </summary>
    public class SimpleConfig
    {
        public string config_loc = "";
        public Dictionary<string, string> appSettings = new Dictionary<string, string>();
        public static SimpleConfig LoadFrom(Configuration c)
        {

            SimpleConfig co = new SimpleConfig();
            co.config_loc = c.FilePath;
            string[] keys = c.AppSettings.Settings.AllKeys;
            for (int i = 0; i < keys.Length; i++)
            {
                co.appSettings.Add(keys[i], c.AppSettings.Settings[keys[i]].Value);
            }
            return co;
        }
        public static SimpleConfig LoadFrom(String delimitedSettings)
        {
            SimpleConfig co = new SimpleConfig();
            string[] lines = delimitedSettings.Split(new string[] { LineDelimitor }, StringSplitOptions.RemoveEmptyEntries);


            for (int i = 0; i < lines.Length; i++)
            {
                string[] items = lines[i].Split(new string[] { NameValueDelimitor }, StringSplitOptions.RemoveEmptyEntries);
                if (items.Length == 2)
                {
                    co.appSettings.Add(items[0], items[1]);
                }
                else
                {
                    //parsing error/invalid config
                    Logger.warn("invalid config recieved from ACS service");
                }
            }
            return co;
        }

        public static SimpleConfig LoadFrom(NameValueCollection appSettings)
        {

            SimpleConfig co = new SimpleConfig();
            co.config_loc = "composite";
            string[] keys = appSettings.AllKeys;
            for (int i = 0; i < keys.Length; i++)
            {
                co.appSettings.Add(keys[i], appSettings[keys[i]]);
            }
            return co;
        }

        public static readonly String LineDelimitor = Environment.NewLine;
        public static readonly String NameValueDelimitor = "|||";


    }
}
