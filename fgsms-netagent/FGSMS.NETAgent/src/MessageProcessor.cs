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
using System.Collections;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics;
using System.IO;
using System.Security.Cryptography.X509Certificates;
using System.Threading;
using System.Xml;
using System.Xml.XPath;

using System.ServiceModel;
using System.Web;
using System.Collections.Specialized;
using System.Net;
using System.Net.NetworkInformation;
using System.Xml.Serialization;
using System.Runtime.InteropServices;


namespace org.miloss.fgsms.agent
{
    /// <summary>
    /// A singleton threadsafe class for management performance metrics for .net services for communicating to the Java core services of FGSMS
    /// </summary>
    /// 

    public class MessageProcessor : IDisposable
    {
        private static volatile MessageProcessor instance;
        private static object syncRoot = new Object();
        private static List<Thread> publishingThread;
        private static ConfigLoader config = null;
        // private static Thread dependencyThread;
        //private static bool Shutdown;
        const string name = "org.miloss.fgsms.agents.Processor";
        //private static TraceSource log;
        //private static Queue<DependencyContainer> dependency_queue;

        private static System.Collections.Queue the_queue;
        private static Hashtable policyCache;
        public static Hashtable threadmap;
        /// <summary>
        /// the maxium message capture size, default is 1 meg, 1,024,000 bytes
        /// </summary>
        public static Int32 MAXCAP = 1024000;
        private static DateTime policyLastUpdate = DateTime.MinValue;
        private static PCSBinding polservice = null;
        private static DCSBinding dcsservice = null;

        public static SecurityWrapper security_level
        {
            get
            {
                MessageProcessor mp = Instance;
                return currentlevel;
            }
        }
        private static SecurityWrapper currentlevel = null;
        private static bool Enabled = true;
        public static bool IsEnabled
        {
            get
            {
                MessageProcessor m = MessageProcessor.Instance;
                return Enabled;
            }
        }

        public static void PurgeOutboundQueue()
        {
            MessageProcessor m = MessageProcessor.Instance;
            the_queue.Clear();
        }

        public static ConfigLoader GetConfig
        {
            get
            {
                MessageProcessor m = MessageProcessor.Instance;
                Init();
                return config;
            }
        }
        private static bool configured = false;

        private static List<string> PCS_URLS = new List<string>();
        private static List<string> DCS_URLS = new List<string>();


        //     private Boolean errorState = false;
        // private static Hashtable dependencyCache;

        private MessageProcessor()
        {
            if (AppDomain.CurrentDomain != null)
                AppDomain.CurrentDomain.ProcessExit += new EventHandler(CurrentDomain_ProcessExit);
            //Shutdown = true;
            currentlevel = new SecurityWrapper();
            currentlevel.caveats = "";
            currentlevel.classification = ClassificationType.U;
            Hashtable t2 = new Hashtable();
            threadmap = Hashtable.Synchronized(t2);
            //log = new TraceSource(name);
            //  dependencyCache = new Hashtable();
            //   dependency_queue = new Queue<DependencyContainer>();
            Hashtable t = new Hashtable();
            policyCache = Hashtable.Synchronized(t);
            // publishingThread = new Thread(new ThreadStart(SendPerformanceData));
            //publishingThread.Start();
            publishingThread = new List<Thread>();

            //  dependencyThread = new Thread(new ThreadStart(SendDependencyData));
            //  dependencyThread.Start();
            Queue q = new Queue();
            the_queue = Queue.Synchronized(q);

            try
            {
                config = new ConfigLoader();
                // polservice = config.GetPCSProxy();
                // dcsservice = config.GetDCSProxy();

            }
            catch
            {
                ErrorState = true;
            }
        }

        /// <summary>
        /// this will hopefully catch when IIS bounces application pools with an agent running
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void CurrentDomain_ProcessExit(object sender, EventArgs e)
        {
            this.Dispose();
        } // end MessageProcessor


        private static string _lasterror;

        public static string LastErrorMessage
        {
            get
            {
                MessageProcessor m = MessageProcessor.Instance;
                return _lasterror;
            }
        }


        private static void Init()
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (!configured || config == null)
            {
                try
                {
                    config = new ConfigLoader();
                }
                catch (Exception ex)
                {
                    _lasterror = ex.Message;
                    ErrorState = true;

                    configured = false;
                    return;
                    //log this
                }
            }


            configured = true;
        }
        private static DateTime LastUddiPCSCallout = DateTime.MinValue;
        private static DateTime LastUddiDCSCallout = DateTime.MinValue;
     
        private static DateTime LastDNSDCSCallout = DateTime.MinValue;
        private static DateTime LastDNSPCSCallout = DateTime.MinValue;


        private static UDDIDiscovery uddi = null;
        
        private static void DiscoveryPCSUrl()
        {
            MessageProcessor m = MessageProcessor.Instance;
            List<String> endpoints = new List<String>();
            bool ran = false;
            if ((DateTime.Now - new TimeSpan(0, 0, 0, config.DiscoveryInterval) > LastUddiPCSCallout))
            {
                if (config.UddiEnabled)
                {
                    if (uddi == null)
                    {
                        uddi = new UDDIDiscovery(config);
                    }
                    endpoints.AddRange(uddi.GetPCSURLs());
                    uddi.Dispose();
                    ran = true;
                    LastUddiPCSCallout = DateTime.Now;
                }
            }
           
            if ((DateTime.Now - new TimeSpan(0, 0, 0, config.DiscoveryInterval) > LastDNSPCSCallout))
            {
                if (config.DNSEnabled)
                {
                    endpoints.AddRange(DNSDiscovery.DiscoverViaDNS(DNSDiscovery.DiscoverType.PCS));
                    ran = true;
                    LastDNSPCSCallout = DateTime.Now;
                }
            }
            if (ran)
            {
                for (int i = 0; i < endpoints.Count; i++)
                {
                    if (!PCS_URLS.Contains(endpoints[i]))
                    {
                        PCS_URLS.Add(endpoints[i]);
                    }
                }
            }
        }

        private static void DiscoveryDCSUrl()
        {
            MessageProcessor m = MessageProcessor.Instance;
            List<String> endpoints = new List<String>();
            bool ran = false;
            if ((DateTime.Now - new TimeSpan(0, 0, 0, config.DiscoveryInterval) > LastUddiDCSCallout))
            {
                if (config.UddiEnabled)
                {
                    if (uddi == null)
                    {
                        uddi = new UDDIDiscovery(config);
                    }
                    endpoints.AddRange(uddi.GetDCSURLs());
                    uddi.Dispose();
                    ran = true;
                    LastUddiDCSCallout = DateTime.Now;
                }
            }

            if ((DateTime.Now - new TimeSpan(0, 0, 0, config.DiscoveryInterval) > LastDNSDCSCallout))
            {
                if (config.DNSEnabled)
                {
                    endpoints.AddRange(DNSDiscovery.DiscoverViaDNS(DNSDiscovery.DiscoverType.DCS));
                    ran = true;
                    LastDNSDCSCallout = DateTime.Now;
                }
            }
            if (ran)
            {
                for (int i = 0; i < endpoints.Count; i++)
                {
                    if (!DCS_URLS.Contains(endpoints[i]))
                    {
                        DCS_URLS.Add(endpoints[i]);
                    }
                }
            }
        }




        public void Dispose()
        {
            MessageProcessor m = MessageProcessor.Instance;
            shutdown = true;
            if (config == null)
                return;
            if (config.ServiceUnavailableBehavior == ConfigLoader.UnavailableBehavior.HOLDPERSIST)
            {
                while (the_queue.Count != 0)
                {
                    Object j = the_queue.Dequeue();
                    MessageCorrelator message = j as MessageCorrelator;
                    if (message != null)
                    {
                        AddDataRequestMsg r = PrepMessage(message);
                        WriteToDisk(r);
                    }
                    else
                    {
                        AddDataRequestMsg rr = j as AddDataRequestMsg;
                        if (rr != null)
                        {
                            WriteToDisk(rr);
                        }
                    }
                }
            }

            the_queue.Clear();
            for (int i = 0; i < publishingThread.Count; i++)
            {
                if (publishingThread[i] != null && publishingThread[i].ThreadState == System.Threading.ThreadState.Running)
                    publishingThread[i].Join();
            }
            if (the_queue.Count != 0)//|| dependency_queue.Count != 0)
                Logger.error("Message Processor has been shut down, but there are still messages in the outbound queue. " +
                    "Data for these transactions will be list");


           
            GC.SuppressFinalize(this);
       }

        ~MessageProcessor()
        {

            this.Dispose();
        }
        

        // int static_countLock = 0;

        /// <summary>
        /// Accessor for singleton object
        /// </summary>
        public static MessageProcessor Instance
        {
            get
            {
                if (instance == null)
                {
                    lock (syncRoot)
                    {

                        if (instance == null)
                        {

                            instance = new MessageProcessor();

                        }
                    }
                }

                return instance;
            }
        } // end Instance

        public static bool IsThreadAlive()
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (publishingThread == null)
                return false;
            return publishingThread.Count > 0;
        }
        /// <summary>
        /// Typically, this is only true when there is a misconfiguration
        /// </summary>
        public static bool ErrorState = false;

        private static PolicyHelper FetchPolicy(string URL)
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (config.OperatingMode == ConfigLoader.OperationMode.OFFLINE)
                return GetDefaultPolicy(URL);
            PolicyHelper myPolicy = null;
            //first lets check our policy cache, if we don't have one cached or it's out of date, update it.
            lock (policyCache)
            {
                myPolicy = (PolicyHelper)policyCache[URL];
                if (myPolicy == null)
                {
                    switch (config.PCSalgo)
                    {
                        case ConfigLoader.Algorithm.FAILOVER:
                            for (int i = 0; i < config.pcsurl.Count; i++)
                            {
                                if (polservice != null)
                                {
                                    try
                                    {
                                        ((IClientChannel)polservice).Close();
                                        ((IClientChannel)polservice).Dispose();
                                    }
                                    catch (Exception ex)
                                    {
                                        //log.TraceEvent(TraceEventType.Warning, 0, "Uh oh" + ex.Message + ex.StackTrace);
                                    }
                                }
                                polservice = config.GetPCSProxy(config.pcsurl[i]);
                                for (int k = 0; k < config.PCSretrycount; k++)
                                {
                                    try
                                    {

                                        ServicePolicyRequestMsg requestr = new ServicePolicyRequestMsg();
                                        //requestr = new ServicePolicyRequestMsg();
                                        requestr.URI = URL;
                                        //remember, .NET/IIS is not case sensitive
                                        // ServicePolicyRequestMsg r = new ServicePolicyRequestMsg();


                                        requestr.classification = currentlevel;
                                        ServicePolicyResponseMsg responsep1 = polservice.GetServicePolicy(requestr);
                                        PolicyHelper temp = new PolicyHelper();
                                        temp.lastUpdate = DateTime.Now;
                                        if (responsep1.policy.GetType() != typeof(TransactionalWebServicePolicy))
                                        {
                                            Logger.info("FGSMS.MessageProcessor" + "Successfully retrieved a policy from the policy configuration service, however it was of an unexpected type " + URL + " type info " + responsep1.policy.GetType().FullName);
                                            throw new ArgumentOutOfRangeException();
                                        }
                                        temp.policy = (TransactionalWebServicePolicy)responsep1.policy;
                                        MAXCAP = temp.policy.RecordedMessageCap;
                                        Enabled = temp.policy.AgentsEnabled;
                                        try
                                        {
                                            policyCache.Add(URL, temp);
                                        }
                                        catch (Exception e)
                                        {
                                            Logger.debug(e, "error caught adding " + URL + " to the policy cache");
                                        }

                                        currentlevel = responsep1.classification;
                                        Logger.info("FGSMS.MessageProcessor" + "Successfully Refreshed policy from the policy configuration service for " + URL);

                                        myPolicy = policyCache[URL] as PolicyHelper;
                                        return myPolicy;
                                    }
                                    //{"There was no endpoint listening at http://localhost:8080/FGSMSServices/PCS that could accept the message. This is often caused by an incorrect address or SOAP action. See InnerException, if present, for more details."}
                                    catch (EndpointNotFoundException ex)
                                    {
                                        _lasterror = ex.GetType().FullName + " " + ex.Message;
                                        Logger.info(ex, "FGSMS.MessageProcessor" + "unable to transmit to " + config.pcsurl[i]);
                                        //this means that the current PCS url is not functioning, try moving to the next one
                                        //polservice = config.GetPCSProxy();
                                    }
                                    catch (Exception ex)
                                    {
                                        Logger.info(ex, "FGSMS.MessageProcessor" + "unable to transmit to " + config.pcsurl[i] + " Policy Updater - failed to policy from the policy configuration service for " + URL + " check that the URL and credentials are correct");
                                    }
                                }

                            }
                            break;
                        case ConfigLoader.Algorithm.ROUNDROBIN:
                            for (int k = 0; k < config.PCSretrycount; k++)
                            {
                                for (int i = 0; i < config.pcsurl.Count; i++)
                                {
                                    try
                                    {
                                        if (polservice != null)
                                        {
                                            try
                                            {
                                                ((IClientChannel)polservice).Close();
                                                ((IClientChannel)polservice).Dispose();
                                            }
                                            catch (Exception ex)
                                            {
                                                //       log.TraceEvent(TraceEventType.Warning, 0, "Uh oh" + ex.Message + ex.StackTrace);
                                            }
                                        }
                                        polservice = config.GetPCSProxy(config.pcsurl[i]);
                                        ServicePolicyRequestMsg requestr = new ServicePolicyRequestMsg();
                                        //requestr = new ServicePolicyRequestMsg();
                                        requestr.URI = URL;
                                        //remember, .NET/IIS is not case sensitive
                                        // GetServicePolicyRequest r = new GetServicePolicyRequest();
                                        // r.request = requestr;
                                        requestr.classification = currentlevel;
                                        ServicePolicyResponseMsg responsep1 = polservice.GetServicePolicy(requestr);
                                        PolicyHelper temp = new PolicyHelper();
                                        temp.lastUpdate = DateTime.Now;
                                        if (responsep1.policy.GetType() != typeof(TransactionalWebServicePolicy))
                                        {
                                            Logger.info("FGSMS.MessageProcessor" + "Successfully retrieved a policy from the policy configuration service, however it was of an unexpected type " + URL + " type info " + responsep1.policy.GetType().FullName);
                                            throw new ArgumentOutOfRangeException();
                                        }
                                        temp.policy = (TransactionalWebServicePolicy)responsep1.policy;
                                        MAXCAP = temp.policy.RecordedMessageCap;
                                        Enabled = temp.policy.AgentsEnabled;
                                        try
                                        {
                                            policyCache.Add(URL, temp);
                                        }
                                        catch (Exception e)
                                        {
                                            Logger.debug(e, "error caught adding " + URL + " to the policy cache");
                                            //its possible that there is another instance of this class running, even through there isn't supposed to be.
                                        }

                                        currentlevel = responsep1.classification;
                                        Logger.info("FGSMS.MessageProcessor" + "Successfully Refreshed policy from the policy configuration service for " + URL);

                                        myPolicy = policyCache[URL] as PolicyHelper;
                                        return myPolicy;
                                    }
                                    //{"There was no endpoint listening at http://localhost:8080/FGSMSServices/PCS that could accept the message. This is often caused by an incorrect address or SOAP action. See InnerException, if present, for more details."}
                                    catch (EndpointNotFoundException ex)
                                    {
                                        _lasterror = ex.GetType().FullName + " " + ex.Message;
                                        //this means that the current PCS url is not function, try moving to the next one
                                        //polservice = config.GetPCSProxy();
                                        Logger.info(ex, "FGSMS.MessageProcessor" + "unable to transmit to " + config.pcsurl[i]);
                                    }
                                    catch (Exception ex)
                                    {
                                        Logger.info(ex, "FGSMS.MessageProcessor" + "unable to transmit to " + config.pcsurl[i] + " Policy Updater - failed to policy from the policy configuration service for " + URL + " check that the URL and credentials are correct");
                                    }
                                }

                            }
                            break;

                    }
                    if (myPolicy == null)
                    {
                        Logger.error("FGSMS.MessageProcessor" + "Cannot contact the policy config service. Reverting to default policy. ");

                        return GetDefaultPolicy(URL);
                    }
                }
            }

            TimeSpan times = XmlConvert.ToTimeSpan(myPolicy.policy.PolicyRefreshRate);
            if ((DateTime.Now.Ticks - myPolicy.lastUpdate.Ticks) > times.Ticks)
            {
                //TODO load balance this
                //if the policy is out of date, update it.
                try
                {
                    //policyConfigurationServiceClient service = new policyConfigurationServiceClient();
                    ServicePolicyRequestMsg requestr = new ServicePolicyRequestMsg();

                    requestr.URI = URL;
                    //ServicePolicyRequest r = new GetServicePolicyRequest();
                    //r.request = requestr;
                    requestr.classification = currentlevel;
                    ServicePolicyResponseMsg responsep = polservice.GetServicePolicy(requestr);
                    policyCache.Remove(URL);
                    PolicyHelper temp = new PolicyHelper();
                    if (responsep.policy.GetType() != typeof(TransactionalWebServicePolicy))
                    {
                        Logger.info("FGSMS.MessageProcessor" + "Successfully retrieved a policy from the policy configuration service, however it was of an unexpected type " + URL + " type info " + responsep.policy.GetType().FullName);
                        throw new ArgumentOutOfRangeException();
                    }
                    temp.policy = (TransactionalWebServicePolicy)responsep.policy;
                    temp.lastUpdate = DateTime.Now;
                    currentlevel = responsep.classification;
                    try
                    {
                        policyCache.Add(URL, temp);
                    }
                    catch (Exception e)
                    {
                        Logger.debug(e, "error caught adding " + URL + " to the policy cache");
                    }
                    Logger.info("Policy Updater- Successfully Refreshed policy from the policy configuration service for " + URL.ToString());
                    return temp;
                }
                catch (Exception ex)
                {
                    Logger.warn(ex, "Policy update failure, unable to update policy for " + URL + ". Using old policy instead. Error:");

                }
            }
            return myPolicy;
        }

        private static PolicyHelper GetDefaultPolicy(String url)
        {
            PolicyHelper p = new PolicyHelper();
            p.lastUpdate = DateTime.MinValue;
            p.policy = new TransactionalWebServicePolicy();
            p.policy.AgentsEnabled = true;
            p.policy.BuellerEnabled = true;
            p.policy.PolicyType = policyType.Transactional;
            p.policy.RecordedMessageCap = MAXCAP;
            p.policy.RecordFaultsOnly = true;
            p.policy.RecordHeaders = true;
            p.policy.RecordRequestMessage = false;
            p.policy.RecordResponseMessage = false;
            p.policy.URL = url;
            return p;
        }
        private static AddDataRequestMsg PrepMessage(MessageCorrelator message)
        {
            Logger.debug(DateTime.Now.ToString("o") + " Prepmessage enter " + message.MessageID);


            MessageProcessor i = MessageProcessor.Instance;

            Logger.info("SendPerformanceData, Data acquistion successful, sending data to the data collector." + message.URL + " at " + message.RecievedAt + " recording agent " + message.agenttype);
            //if (FGSMSConstants.log) log.TraceEvent(TraceEventType.Information, 0, ());


            AddDataRequestMsg request = new AddDataRequestMsg();
            request = new AddDataRequestMsg();
            request.Action = message.soapAction;

            if (String.IsNullOrEmpty(message.URL))
            {
                Logger.warn("FGSMS.MessageProcessor Send Peformance Data, the url on this message is null, skipping");
                message = null;
                return null;
            }
            if (message.agenttype.ToLower().Contains(".client"))
                message.URL = ModifyURL(message.URL, true);
            else message.URL = ModifyURL(message.URL, false);
            Logger.debug(

                 DateTime.Now.ToString("o") + " Prepmessage after url mod " + message.MessageID);

            //This double check is required due to adjusted urls do not remove
            if (IsOnIgnoreList(message.URL))
            {
                message = null;
                try
                {
                    //if (FGSMSConstants.log) EventLog.WriteEntry("FGSMS.MessageProcessor", "Message ignored due to ignore list " + message.originalUrl + " translated " + message.URL, EventLogEntryType.Information);
                }
                catch { }
                return null;
            }

            PolicyHelper myPolicy = FetchPolicy(message.URL);
            Logger.debug(

                 DateTime.Now.ToString("o") + " Prepmessage after pol fetch" + message.MessageID);

            if (myPolicy == null)
                return null;
            //record the request
            if (!String.IsNullOrEmpty(message.RequestMessage))
                if (myPolicy.policy.RecordRequestMessage)
                {
                    request.XmlRequest =
                        message.RequestMessage;
                    //truncate the request
                    if (myPolicy.policy.RecordedMessageCap < request.XmlRequest.Length)
                        request.XmlRequest = request.XmlRequest.Substring(0, myPolicy.policy.RecordedMessageCap);
                }

            //record response
            if (!String.IsNullOrEmpty(message.ResponseMessage))
                if (myPolicy.policy.RecordResponseMessage)
                {
                    request.XmlResponse =
                        message.ResponseMessage;
                    //truncate the response
                    if (myPolicy.policy.RecordedMessageCap < request.XmlResponse.Length)
                        request.XmlResponse = request.XmlResponse.Substring(0, myPolicy.policy.RecordedMessageCap);
                }

            request.requestSize = (int)message.requestsize;
            request.responseSize = (int)message.responsesize;
            if (myPolicy.policy.RecordFaultsOnly && message.IsFault)
            {
                request.XmlRequest =
                 message.RequestMessage;
                request.XmlResponse =
                   message.ResponseMessage;
                //truncate the request
                if (myPolicy.policy.RecordedMessageCap < request.XmlRequest.Length)
                    request.XmlRequest = request.XmlRequest.Substring(0, myPolicy.policy.RecordedMessageCap);
                //truncate the response
                if (myPolicy.policy.RecordedMessageCap < request.XmlResponse.Length)
                    request.XmlResponse = request.XmlResponse.Substring(0, myPolicy.policy.RecordedMessageCap);

            }
            if (myPolicy.policy.RecordHeaders)
            {
                if (message.RequestHeaders != null && message.RequestHeaders.Count > 0)
                {
                    List<header> l = new List<header>();
                    for (int b = 0; b < message.RequestHeaders.AllKeys.Length; b++)
                    {
                        header h = new header();
                        h.name = message.RequestHeaders.AllKeys[b];
                        h.value = new string[] { message.RequestHeaders[message.RequestHeaders.AllKeys[b]] };
                        l.Add(h);
                    }
                    request.headersRequest = l.ToArray();
                    l = null;
                }
                if (message.ResponseHeaders != null && message.ResponseHeaders.Count > 0)
                {
                    List<header> l = new List<header>();
                    for (int b = 0; b < message.ResponseHeaders.Count; b++)
                    {
                        header h = new header();
                        h.name = message.ResponseHeaders.AllKeys[b];
                        h.value = new string[] { message.ResponseHeaders[message.ResponseHeaders.AllKeys[b]] };
                        l.Add(h);
                    }
                    request.headersResponse = l.ToArray();
                    l = null;
                }
            }
            request.Identity = GetUserIdentity(myPolicy.policy.UserIdentification, ref message);
            long ticks = message.CompletedAt.Ticks - message.RecievedAt.Ticks;
            request.recordedat = message.RecievedAt;
            TimeSpan ts = new TimeSpan(ticks);
            request.responseTime = (ts.Hours * 60 * 60 * 1000) + (ts.Minutes * 60 * 1000) + (ts.Seconds * 1000) + (ts.Milliseconds);
            request.ServiceHost = System.Environment.MachineName.ToLower();
            request.Success = !message.IsFault;
            request.URI = message.URL;
            request.TransactionID = message.MessageID;
            request.TransactionThreadID = message.threadid;
            request.RelatedTransactionID = message.relatedtransactionid;


            request.agentType = message.agenttype;
            request.RequestURI = message.originalUrl;

            request.Action = message.soapAction;
            request.Action = request.Action.Replace("\"", string.Empty);
            request.Action = request.Action.Replace("'", string.Empty);
            long memused = System.GC.GetTotalMemory(false);

            ulong installedMemory = 0;
            ulong freememroy = 0;

            MEMORYSTATUSEX memStatus = new MEMORYSTATUSEX();
            if (GlobalMemoryStatusEx(memStatus))
            {
                installedMemory = memStatus.ullTotalPhys;
                freememroy = memStatus.ullAvailPhys;
            }

            request.Message += "Agent Outbound queue: " + the_queue.Count + " Outbound Threads " + ThreadPoolSize() + " Thread " + Thread.CurrentThread.Name;
            request.Message += " mem stat " + freememroy + "/" + installedMemory;
            /*if (String.IsNullOrEmpty(message.RequestMessage))
                request.requestSize = 0;
            else request.requestSize = message.RequestMessage.Length;
            if (String.IsNullOrEmpty(message.ResponseMessage))
                request.responseSize = 0;
            else request.responseSize = message.ResponseMessage.Length;*/
            request.classification = currentlevel;
            Logger.debug(

                 DateTime.Now.ToString("o") + " Prepmessage exit " + message.MessageID);

            message = null;

            return request;

        }

        private static Hashtable ThreadIdMap = new Hashtable();
        /// <summary>
        /// This should only be called in the context of a client handler seeing outbound transactions
        /// </summary>
        /// <param name="ThreadId"></param>
        /// <returns></returns>
        public static String GetTransactionThreadId(string ThreadId)
        {
            MessageProcessor m = MessageProcessor.Instance;
            string ret = null;
            lock (ThreadIdMap)
            {
                ret = (String)ThreadIdMap[ThreadId];
            }
            return ret;
        }
        /// <summary>
        ///  Only Set on an inbound service request
        /// </summary>
        /// <param name="ThreadId"></param>
        /// <param name="id"></param>
        public static void SetTransactionThreadId(string ThreadId, String id)
        {
            MessageProcessor m = MessageProcessor.Instance;
            lock (ThreadIdMap)
            {
                if (ThreadIdMap.ContainsKey(ThreadId))
                {
                    //throw new Exception("attempting to set transaction thread id for thread " + id + " when an id has already been set");
                }
                ThreadIdMap.Add(ThreadId, id);
            }
        }
        /// <summary>
        /// Only clear on an outbound service response
        /// </summary>
        /// <param name="ThreadId"></param>
        public static void ClearTransactionThreadId(string ThreadId)
        {
            MessageProcessor m = MessageProcessor.Instance;
            lock (ThreadIdMap)
            {
                ThreadIdMap.Remove(ThreadId);
            }
        }

        private static bool shutdown = false;

        private static void SendBulkPerformanceData()
        {
            MessageProcessor m = MessageProcessor.Instance;
            Logger.debug(

                 DateTime.Now.ToString("o") + " Sendbulk");

            Init();
            Logger.debug(

                 DateTime.Now.ToString("o") + " Sendbulk after init");

            MessageProcessor i = MessageProcessor.Instance;
            if (ErrorState)
            {
                Logger.error("FGSMS.MessageProcessor Message processor shuting down, the configuration file isn't available or FGSMS is being uninstalled. " + the_queue.Count + " message transactions were orphaned");

                //ErrorState = true;
                publishingThread.Remove(Thread.CurrentThread);
                return;
            }
            if (!Enabled)
            {
                //persistence?
                the_queue.Clear();
            }
            if (!ErrorState)
                try
                {
                    //enabled check removed here because once we're disabled, theres no way to reenable things
                    while (!shutdown && (the_queue.Count != 0) && Enabled)
                    // || HasPersistenceFiles()))       //remove for architecture change
                    {
                        int count = 0;
                        AddDataRequestMsg[] req = null;
                        List<AddDataRequestMsg> data = new List<AddDataRequestMsg>();
                        int totalbody = 0;
                        //each enabled check is here because policies will be being updated during this routine, thus its necessary
                        //grab the first 40 transactions or up to a total of MAXCAP
                        while (count < 40 && the_queue.Count != 0 && Enabled && totalbody < 1024000)
                        {
                            Object j = the_queue.Dequeue();
                            MessageCorrelator message = j as MessageCorrelator;
                            if (message != null)
                            {
                                count++;
                                AddDataRequestMsg r = PrepMessage(message);
                                if (r != null)
                                {
                                    if (r.XmlResponse != null)
                                        totalbody += r.XmlResponse.Length;
                                    if (r.XmlRequest != null)
                                        totalbody += r.XmlRequest.Length;
                                    data.Add(r);
                                    message = null;
                                }
                                else the_queue.Enqueue(message);
                                // PCS is not available!
                            }
                            else
                            {
                                AddDataRequestMsg rr = j as AddDataRequestMsg;
                                if (rr != null)
                                {
                                    if (rr.XmlRequest != null)
                                        totalbody += rr.XmlRequest.Length;
                                    if (rr.XmlResponse != null)
                                        totalbody += rr.XmlResponse.Length;
                                    data.Add(rr);
                                }
                                rr = null;
                            }
                            j = null;
                        }

                        //get items stored to disk, meaning that we couldn't send it before, so lets try it again

                        //change 1-18-2012 from while to if, only grab the first item form disk
                        //while
                        /*        if (HasPersistenceFiles() && count < 40 && totalbody < 1024000)
                            {
                                AddDataRequestMsg rr = ReadItemsFromDisk();
                                if (rr != null)
                                {
                                    if (rr.XmlRequest != null)
                                        totalbody += rr.XmlRequest.Length;
                                    if (rr.XmlResponse != null)
                                        totalbody += rr.XmlResponse.Length;
                                    data.Add(rr);
                                }
                            }*/

                        req = data.ToArray();
                        Logger.debug(

                             DateTime.Now.ToString("o") + " Sendbulk items " + req.Length + " queue " + the_queue.Count);


                        /*    XmlSerializer xs = new XmlSerializer(typeof(AddMoreDataRequest));
                            StringWriter sw = new StringWriter();
                            xs.Serialize(sw, req);
                            String temp = sw.ToString();*/
                        data.Clear();
                        data = null;
                        if (req.Length == 0)
                            continue;
                        bool success = false;

                        if (config.OperatingMode == ConfigLoader.OperationMode.ONLINE)
                            //ok ready to send
                            switch (config.DCSalgo)
                            {
                                case ConfigLoader.Algorithm.ROUNDROBIN:
                                    //dcsservice = config.GetDCSProxy(config.dcsurl[0]);
                                    Logger.debug(

                                         DateTime.Now.ToString("o") + " build dcs proxy ");



                                    if (dcsservice != null)
                                        try
                                        {
                                            ((IClientChannel)dcsservice).Dispose();
                                            ((IClientChannel)dcsservice).Close();
                                        }
                                        catch { }
                                    dcsservice = config.GetDCSProxy(config.dcsurl[0]);
                                    Logger.debug(

                                         DateTime.Now.ToString("o") + " build dcs proxy done");


                                    for (int b = 0; b < config.dcsurl.Count; b++)
                                    {
                                        if (!success)
                                            for (int a = 0; a < config.DCSretrycount; a++)
                                            {
                                                try
                                                {
                                                    AddDataResponseMsg response = dcsservice.AddMoreData(req);
                                                    if ((response.Status == DataResponseStatus.Failure))
                                                    {
                                                        //this case usually only happens when the same transaction was attempted to be written more than once or there was a problemn on the other end


                                                        success = true;
                                                    }
                                                    else
                                                    {
                                                        success = true;
                                                        Logger.debug(

                                                             DateTime.Now.ToString("o") + " Sendbulk items " + req.Length + " data sent");

                                                        break;

                                                    }
                                                }

                                                catch (Exception ex)
                                                {
                                                    _lasterror = ex.GetType().FullName + " " + ex.Message;
                                                    Logger.warn("FGSMS.MessageProcessor" + "ProcessOutputMessage - Data acquistion successful, but there was an error sending the information to the data collector.  " + ex.ToString());

                                                }
                                                Logger.debug(

                                                     DateTime.Now.ToString("o") + " Sendbulk items " + req.Length + " data send fail" + _lasterror);

                                            }
                                        if (b + 1 < config.dcsurl.Count)
                                        {
                                            Logger.debug(

                                                 DateTime.Now.ToString("o") + " rebuild dcs proxy ");

                                            if (dcsservice != null)
                                            {
                                                try
                                                {
                                                    ((IClientChannel)dcsservice).Close();
                                                    ((IClientChannel)dcsservice).Dispose();
                                                }
                                                catch (Exception ex)
                                                {
                                                    //      log.TraceEvent(TraceEventType.Warning, 0, "Uh oh" + ex.Message + ex.StackTrace);
                                                }
                                            }
                                            dcsservice = config.GetDCSProxy(config.dcsurl[b + 1]);
                                            Logger.debug(

                                                 DateTime.Now.ToString("o") + " rebuild dcs proxy done");

                                        }
                                    }
                                    break;
                                case ConfigLoader.Algorithm.FAILOVER:
                                    for (int a = 0; a < config.DCSretrycount; a++)
                                    {

                                        Logger.debug

                                             (DateTime.Now.ToString("o") + " build dcs proxy ");

                                        if (dcsservice != null)
                                        {
                                            try
                                            {
                                                ((IClientChannel)dcsservice).Close();
                                                ((IClientChannel)dcsservice).Dispose();
                                            }
                                            catch (Exception ex)
                                            {
                                                //log.TraceEvent(TraceEventType.Warning, 0, "Uh oh" + ex.Message + ex.StackTrace);
                                            }
                                        }
                                        dcsservice = config.GetDCSProxy(config.dcsurl[0]);
                                        Logger.debug(

                                             DateTime.Now.ToString("o") + " build dcs proxy done");


                                        //dcsservice = config.GetDCSProxy(config.dcsurl[0]);

                                        if (!success)
                                            for (int b = 0; b < config.dcsurl.Count; b++)
                                            {

                                                try
                                                {
                                                    AddDataResponseMsg response = dcsservice.AddMoreData(req);
                                                    if ((response.Status == DataResponseStatus.Failure))
                                                    {
                                                        Logger.warn("FGSMS.MessageProcessor" + "ProcessOutputMessage - Data acquistion successful, but there was an error sending the information to the data collector. Check the logs at that service. ");

                                                    }
                                                    else
                                                    {
                                                        success = true;
                                                        Logger.debug(

                                                             DateTime.Now.ToString("o") + " Sendbulk items " + req.Length + " data sent");

                                                        break;
                                                        /*try
                                                        {
                                                            if (FGSMSConstants.log) EventLog.WriteEntry("FGSMS.MessageProcessor", "ProcessOutputMessage - Data acquistion successful, message sent to the data collector was successful for " + req.req.Length + " records", EventLogEntryType.Information);
                                                        }
                                                        catch { }
                                                        if (FGSMSConstants.log) log.TraceEvent(TraceEventType.Information, 0, ("ProcessOutputMessage - Data acquistion successful, message sent to the data collector was successful for " + req.req.Length + " records"));
                                                   */
                                                    }
                                                }

                                                catch (Exception ex)
                                                {
                                                    _lasterror = ex.GetType().FullName + " " + ex.Message;
                                                    Logger.warn(ex, "FGSMS.MessageProcessor" + "ProcessOutputMessage - Data acquistion successful, but there was an error sending the information to the data collector.  " + ex.ToString());
                                                }
                                                if (b + 1 < config.dcsurl.Count)
                                                {
                                                    Logger.debug(

                                                         DateTime.Now.ToString("o") + " rebuild dcs proxy ");

                                                    if (dcsservice != null)
                                                    {
                                                        try
                                                        {
                                                            ((IClientChannel)dcsservice).Close();
                                                            ((IClientChannel)dcsservice).Dispose();
                                                        }
                                                        catch (Exception ex)
                                                        {
                                                        }
                                                    }
                                                    dcsservice = config.GetDCSProxy(config.dcsurl[b + 1]);
                                                    Logger.debug(

                                                         DateTime.Now.ToString("o") + " rebuild dcs proxy done");

                                                }
                                            }
                                    }
                                    break;
                            }
                        if (!success && config.ServiceUnavailableBehavior == ConfigLoader.UnavailableBehavior.HOLD)
                        {
                            for (int k = 0; k < req.Length; k++)
                            {
                                the_queue.Enqueue(req[k]);
                            }
                        }
                        if (!success && config.ServiceUnavailableBehavior == ConfigLoader.UnavailableBehavior.PURGE)
                        {
                            req = null;
                        }
                        if (!success && config.ServiceUnavailableBehavior == ConfigLoader.UnavailableBehavior.HOLDPERSIST)
                        {
                            Logger.debug(

                                 DateTime.Now.ToString("o") + " Sendbulk items " + req.Length + " write to disk");

                            for (int k = 0; k < req.Length; k++)
                            {
                                WriteToDisk(req[k]);
                            }

                            Logger.debug(DateTime.Now.ToString("o") + " Sendbulk items " + req.Length + " write to disk done");

                        }

                    }
                    DoCallbacks_EmptyQueue();
                    if (!Enabled)
                        the_queue.Clear();
                    if (the_queue.Count == 0)
                    {
                        DoCallbacks_LastMessageSent();
                        publishingThread.Remove(Thread.CurrentThread);
                        return;
                    }


                    // list = null;
                }

                catch (Exception Exception)
                {
                    _lasterror = Exception.GetType().FullName + " " + Exception.Message;
                    Logger.error(Exception, "FGSMS.MessageProcessor" + " ProcessOutputMessage Data acquistion successful, but there was an error sending the information to the data collector.");
                }
            publishingThread.Remove(Thread.CurrentThread);
            Logger.debug(

                 DateTime.Now.ToString("o") + " Sendbulk Thread exit");

            if (dcsservice != null)
            {
                try
                {
                    ((IClientChannel)dcsservice).Close();
                    ((IClientChannel)dcsservice).Dispose();
                }
                catch (Exception ex)
                {
                    //    log.TraceEvent(TraceEventType.Warning, 0, "Uh oh" + ex.Message + ex.StackTrace);
                }
            }
            if (polservice != null)
            {
                try
                {
                    ((IClientChannel)polservice).Close();
                    ((IClientChannel)polservice).Dispose();
                }
                catch (Exception ex)
                {
                    //  log.TraceEvent(TraceEventType.Warning, 0, "Uh oh" + ex.Message + ex.StackTrace);
                }
            }

        }

        /* private static bool HasPersistenceFiles()
         {
             if (!String.IsNullOrEmpty(config.PersistLocation))
             {
                 if (Directory.Exists(config.PersistLocation))
                 {
                     String[] s = Directory.GetFiles(config.PersistLocation);
                     if (s != null && s.Length > 0)
                         return true;
                 }
             }
             return false;
             //throw new NotImplementedException();
         }*/


        /// <summary>
        /// returns 1 item that was stored to disk, if read successfully, the file will be deleted from disk
        /// </summary>
        /// <returns></returns>
        public static AddDataRequestMsg ReadItemsFromDisk()
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (!String.IsNullOrEmpty(config.PersistLocation))
            {
                if (!Directory.Exists(config.PersistLocation))
                {
                    return null;
                }
                if (Directory.Exists(config.PersistLocation))
                {
                    try
                    {
                        XmlSerializer xs = new XmlSerializer(typeof(AddDataRequestMsg));
                        // List<AddDataRequestMsg> list = new List<AddDataRequestMsg>();

                        string[] files = Directory.GetFiles(config.PersistLocation);
                        int count = 0;
                        //while (count < files.Length && count <= maxitems)
                        //{
                        string t = File.ReadAllText(files[count], System.Text.Encoding.UTF8);

                        t = new Util().DE(t);
                        StringReader sr = new StringReader(t);
                        AddDataRequestMsg a = (AddDataRequestMsg)xs.Deserialize(sr);
                        //only delete if deserial was successful
                        File.Delete(files[count]);
                        //list.Add(a);
                        //    count++;
                        return a;
                        //}
                        //return list.ToArray();
                    }
                    catch (Exception ex)
                    {
                        Logger.error(ex, "FGSMS.MessageProcessor" + " ReadItemsFromDisk (HOLDANDPERSIST) Error caught reading from disk.");
                        //_lasterror = "Couldn't read stored message from disk" + ex.Message;
                    }
                }
            }
            return null;
        }

        /// <summary>
        /// uses configuration object to store data to disk, if it's not possible, it's thrown out
        /// </summary>
        /// <param name="addDataRequestMsg"></param>
        public static void WriteToDisk(AddDataRequestMsg addDataRequestMsg)
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (config.ServiceUnavailableBehavior == ConfigLoader.UnavailableBehavior.HOLDPERSIST)
            {
                if (!String.IsNullOrEmpty(config.PersistLocation))
                {
                    if (!Directory.Exists(config.PersistLocation))
                    {
                        try
                        {
                            Directory.CreateDirectory(config.PersistLocation);
                        }
                        catch { }
                    }
                    if (Directory.Exists(config.PersistLocation))
                    {
                        try
                        {
                            StringWriter sw = new StringWriter();
                            XmlSerializer xs = new XmlSerializer(typeof(AddDataRequestMsg));
                            xs.Serialize(sw, addDataRequestMsg);
                            File.WriteAllText(config.PersistLocation + "\\" + addDataRequestMsg.TransactionID + ".msg",
                                new Util().EN(sw.ToString()), System.Text.Encoding.UTF8);
                        }
                        catch (Exception ex)
                        {
                            Logger.error(ex, "FGSMS.MessageProcessor" + " WriteItemsFromDisk (HOLDANDPERSIST) Error caught writing to disk." + config.PersistLocation);
                        }
                    }
                }

            }
        }



        /// <summary>
        /// uses the appSettings to config to determine if this message is ignorable
        /// </summary>
        /// <param name="url"></param>
        /// <param name="config"></param>
        /// <returns></returns>
        private static bool IsOnIgnoreList(string url)
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (!configured)
                Init();
            for (int i = 0; i < config.IgnoreList.Count; i++)
            {
                if (url.Equals(config.IgnoreList[i], StringComparison.CurrentCultureIgnoreCase))
                    return true;
            }
            return false;
        }

        /// <summary>
        /// purges the outbound queue and aborts all outbound threads.
        /// </summary>
        public static void Abort()
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (the_queue != null)
                the_queue.Clear();
        }

        //Servers only, whatever the inbound url is, replace the host portion of the url with my hostname
        //http://something1
        private static System.Text.RegularExpressions.Regex r1 = new System.Text.RegularExpressions.Regex("\\://[a-zA-Z0-9\\-\\.]+\\/", System.Text.RegularExpressions.RegexOptions.None);
        //http://something.something:8080/something
        private static System.Text.RegularExpressions.Regex r2 = new System.Text.RegularExpressions.Regex("\\://[a-zA-Z0-9\\-\\.]+:", System.Text.RegularExpressions.RegexOptions.None);
        //http://something.something/something
        private static System.Text.RegularExpressions.Regex r3 = new System.Text.RegularExpressions.Regex("\\://[a-zA-Z0-9\\-\\.]+?$", System.Text.RegularExpressions.RegexOptions.None);
        //special case, jms://something:80/something
        private static System.Text.RegularExpressions.Regex r4 = new System.Text.RegularExpressions.Regex("\\:[a-zA-Z0-9\\-\\.]+:", System.Text.RegularExpressions.RegexOptions.None);


        private static Hashtable urlmap = new Hashtable();

        /// <summary>
        /// Modifies URLs to absolute/unique URLs
        ///if isClient
        ///If an IPv4 address is present and it's one of my IPs, use the hostname
        ///else it will attempt to resolve it
        ///
        /// if !isClient replace the server part with my hostname
        ///in both cases, explicitly state the port number
        /// </summary>
        /// <param name="url">url to be modified</param>
        /// <param name="isClient">see above</param>
        /// <returns>http(s)://hostname:port/path</returns>
        public static string ModifyURL(string url, bool isClient)
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (String.IsNullOrEmpty(url))
            {

                return url;
            }
            if (urlmap.ContainsKey(url))
            {
                return (string)urlmap[url];
            }



            string str = url;
            String host = "localhost";
            host = System.Net.Dns.GetHostName().ToLower();

            List<string> ips = new List<string>();
            // localips.Add("127.0.0.1");
            IPHostEntry ip = Dns.GetHostEntry(host);
            IPAddress[] addr = ip.AddressList;
            ips.Add(IPAddress.IPv6Loopback.ToString());
            ips.Add(IPAddress.Loopback.ToString());
            for (int i = 0; i < addr.Length; i++)
            {
                ips.Add(addr[i].ToString());
            }

            //Clients only, if the url contains localhost or one of my IP addresses, replace with my hostname, 
            //else attempt to resolve remote hostname, 
            //if it resolves, replace
            //if not, leave it as is
            if (isClient)
            {
                bool containsLocalhostOrLocalIP = false;
                if (url.Contains("://localhost/"))
                    containsLocalhostOrLocalIP = true;
                if (url.Contains("://localhost:"))
                    containsLocalhostOrLocalIP = true;
                for (int i = 0; i < ips.Count; i++)
                {
                    if (url.Contains("://" + ips[i]))
                    {
                        containsLocalhostOrLocalIP = true;
                        break;
                    }
                }
                if (containsLocalhostOrLocalIP)
                {
                    for (int i = 0; i < ips.Count; i++)
                    {
                        if (str.StartsWith("https:", StringComparison.CurrentCultureIgnoreCase))
                        {
                            str = r1.Replace(str, "://" + host + ":443/", 1);
                            str = r2.Replace(str, "://" + host + ":", 1);
                            str = r3.Replace(str, "://" + host + ":443/", 1);
                        }
                        else if (str.StartsWith("http:", StringComparison.CurrentCultureIgnoreCase))
                        {
                            str = r1.Replace(str, "://" + host + ":80/", 1);
                            str = r2.Replace(str, "://" + host + ":", 1);
                            str = r3.Replace(str, "://" + host + ":80/", 1);
                        }
                        else
                        {
                            str = r1.Replace(str, "://" + host + "/", 1);
                            str = r2.Replace(str, "://" + host + ":", 1);
                            str = r3.Replace(str, "://" + host + "/", 1);
                        }
                    }
                    if (str.StartsWith("jms:"))
                        str = r4.Replace(str, ":" + host + ":", 1);
                    try { urlmap.Add(url, str); }
                    catch { }
                    return str;
                }

                //ok this transaction was captured from a client agent and the service is not co-located
                //if the request url contains an ip address
                string ipurl = ContainsIPAddress(str);
                if (!String.IsNullOrEmpty(ipurl))
                {
                    //attempt to reverse dns the ip address

                    try
                    {
                        string ipaddress = r1.Match(str).Value;
                        IPHostEntry h = System.Net.Dns.GetHostEntry(IPAddress.Parse(ipurl));
                        host = h.HostName;
                        for (int i = 0; i < ips.Count; i++)
                        {
                            str = r1.Replace(str, "://" + host + "/", 1);
                            str = r2.Replace(str, "://" + host + ":", 1);
                            str = r3.Replace(str, "://" + host + "/", 1);
                        }
                        if (str.StartsWith("jms:"))
                            str = r4.Replace(str, ":" + host + ":", 1);
                    }
                    catch (Exception ex)
                    {
                        Logger.warn(ex, "FGSMS.MessageProcessor Client side url modifier, error caught trying to rdns the remote entry of " + url + ex.Message);
                    }
                    try { urlmap.Add(url, str); }
                    catch { }
                    return str;
                }
                else
                {
                    //url contains some kind of host name, resolve to IP
                    string hostname = "";
                    // hostname = r2.Match(url).Value;
                    string hostnameregex = @"://([^/:]+)";
                    System.Text.RegularExpressions.Regex re = new System.Text.RegularExpressions.Regex(hostnameregex);
                    hostname = re.Match(url).Value;
                    hostname = hostname.Replace("://", "");
                    bool found = false;
                    try
                    {
                        IPHostEntry entry = System.Net.Dns.GetHostEntry(hostname);
                        for (int i = 0; i < ips.Count; i++)
                        {
                            if (ips[i] == entry.AddressList[0].ToString())
                            {
                                str = str.Replace(hostname, host);
                                try { urlmap.Add(url, str); }
                                catch { }
                                found = true;
                                //return str;
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                    }



                    if (found)
                    {
                        //this url is colocated, replace with hostname using regex
                        if (str.StartsWith("https:", StringComparison.CurrentCultureIgnoreCase))
                        {
                            str = r1.Replace(str, "://" + host + ":443/", 1);
                            str = r2.Replace(str, "://" + host + ":", 1);
                            str = r3.Replace(str, "://" + host + ":443/", 1);
                        }
                        else if (str.StartsWith("http:", StringComparison.CurrentCultureIgnoreCase))
                        {
                            str = r1.Replace(str, "://" + host + ":80/", 1);
                            str = r2.Replace(str, "://" + host + ":", 1);
                            str = r3.Replace(str, "://" + host + ":80/", 1);
                        }
                        else
                        {
                            str = r1.Replace(str, "://" + host + "/", 1);
                            str = r2.Replace(str, "://" + host + ":", 1);
                            str = r3.Replace(str, "://" + host + "/", 1);
                        }
                    }
                    else
                    {
                        //this url is NOT colocated, process to force port numbers to be listed
                        if (str.StartsWith("https:", StringComparison.CurrentCultureIgnoreCase))
                        {
                            str = r1.Replace(str, "://" + hostname + ":443/", 1);
                            str = r2.Replace(str, "://" + hostname + ":", 1);
                            str = r3.Replace(str, "://" + hostname + ":443/", 1);
                        }
                        else if (str.StartsWith("http:", StringComparison.CurrentCultureIgnoreCase))
                        {
                            str = r1.Replace(str, "://" + hostname + ":80/", 1);
                            str = r2.Replace(str, "://" + hostname + ":", 1);
                            str = r3.Replace(str, "://" + hostname + ":80/", 1);
                        }
                        else
                        {
                            str = r1.Replace(str, "://" + hostname + "/", 1);
                            str = r2.Replace(str, "://" + hostname + ":", 1);
                            str = r3.Replace(str, "://" + hostname + "/", 1);
                        }
                    }
                    try { urlmap.Add(url, str); }
                    catch { }
                    return str;


                }
                //catch all, leave the url as is
                // return str;

            }
            else
            { //servers only



                //for (int i = 0; i < ips.Count; i++)
                {
                    if (str.StartsWith("https:", StringComparison.CurrentCultureIgnoreCase))
                    {
                        str = r1.Replace(str, "://" + host + ":443/", 1);
                        str = r2.Replace(str, "://" + host + ":", 1);
                        str = r3.Replace(str, "://" + host + ":443/", 1);
                    }
                    else if (str.StartsWith("http:", StringComparison.CurrentCultureIgnoreCase))
                    {
                        str = r1.Replace(str, "://" + host + ":80/", 1);
                        str = r2.Replace(str, "://" + host + ":", 1);
                        str = r3.Replace(str, "://" + host + ":80/", 1);
                    }
                    else
                    {
                        str = r1.Replace(str, "://" + host + "/", 1);
                        str = r2.Replace(str, "://" + host + ":", 1);
                        str = r3.Replace(str, "://" + host + "/", 1);
                    }
                }
                if (str.StartsWith("jms:"))
                    str = r4.Replace(str, ":" + host + ":", 1);
                try { urlmap.Add(url, str); }
                catch { }
                return str;
            }

        }
        private static System.Text.RegularExpressions.Regex regexIpAddress = new System.Text.RegularExpressions.Regex(@"\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\b", System.Text.RegularExpressions.RegexOptions.None);
        private static string ContainsIPAddress(string url)
        {

            //string reg = @"\b\d{1,3}\.\d{1,3\.\d{1,3}\.\d{1,3}\b";

            // System.Text.RegularExpressions.Regex r1 = new System.Text.RegularExpressions.Regex("\\://[a-zA-Z0-9\\-\\.]+\\/", System.Text.RegularExpressions.RegexOptions.None);
            try
            {
                return regexIpAddress.Match(url).Value;
            }
            catch (Exception ex)
            {
            }
            return "";
        }
        public static int ThreadPoolSize()
        {
            MessageProcessor m = MessageProcessor.Instance;
            if (publishingThread == null)
                return 0;
            return publishingThread.Count;
        }
        /// <summary>
        /// gets the current queue size, should never throw
        /// </summary>
        /// <returns></returns>
        public static int GetQueueSize()
        {
            MessageProcessor m = MessageProcessor.Instance;
            return the_queue.Count;
        }
        /// <summary>
        /// gets the current policy cache size, should never throw
        /// </summary>
        /// <returns></returns>
        public static int GetPolicyCacheSize()
        {
            MessageProcessor m = MessageProcessor.Instance;
            return policyCache.Count;
        }

        /// <summary>
        /// GetUserIdentity
        /// </summary>
        /// <param name="userIdentity">policy items related to identifying a user</param>
        /// <param name="msg">reference to the current correlation object</param>
        /// <returns>A string array representing all user identities found using the given service policy</returns>
        private static string[] GetUserIdentity(UserIdentity[] userIdentity, ref MessageCorrelator msg)
        {
            if (msg == null)
            {
                return null;
            }

            if (userIdentity != null && userIdentity.Length > 0)
            {
                StringReader sr = new StringReader(msg.RequestMessage);
                XPathDocument doc = new XPathDocument(sr);
                sr.Dispose();
                foreach (UserIdentity ui in userIdentity)
                {
                    if (ui.UseHttpCredential)
                    {
                        if (msg.identity == null)
                        {
                            //log warning
                        }
                        else
                        {
                            if (msg.ClientCertificate != null)
                                msg.identity.Add(msg.ClientCertificate.Subject);
                        }

                    }
                    if (ui.UseHttpHeader && !String.IsNullOrEmpty(ui.HttpHeaderName))
                    {
                        if (!String.IsNullOrEmpty(msg.RequestHeaders[ui.HttpHeaderName]))
                            msg.identity.Add(msg.RequestHeaders[ui.HttpHeaderName]);
                    }
                    XPathNavigator navi = doc.CreateNavigator();
                    XmlNamespaceManager mgr = new XmlNamespaceManager(navi.NameTable);

                    foreach (XMLNamespacePrefixies ns in ui.Namespaces)
                    {
                        mgr.AddNamespace(ns.Prefix, System.Web.HttpUtility.HtmlDecode(ns.Namespace));
                    }
                    foreach (XPathExpressionType xpath in ui.XPaths)
                    {

                        System.Xml.XPath.XPathExpression x = System.Xml.XPath.XPathExpression.Compile(xpath.XPath, mgr);
                        XPathNodeIterator valueIT = navi.Select(x);
                        if (valueIT.Count > 0)
                        {
                            while (valueIT.MoveNext())
                            {
                                try
                                {
                                    if (xpath.IsCertificate)
                                    {
                                        byte[] certb = System.Text.Encoding.UTF8.GetBytes(valueIT.Current.Value);
                                        X509Certificate2 xcert = new X509Certificate2(certb);
                                        if (xcert != null)
                                            msg.identity.Add(System.Web.HttpUtility.HtmlEncode(xcert.Subject));
                                    }
                                    else
                                        msg.identity.Add(System.Web.HttpUtility.HtmlEncode(valueIT.Current.Value));
                                }
                                catch (Exception ex)
                                {
                                    Logger.error(ex, "FGSMS.MessageProcessor" + @"GetUserIdentity, error obtaining user identity. This means that the
                                xpath query worked but something went haywire when parsing the results. XPATH=" + xpath.XPath + "error ");

                                }
                            }
                        }
                    }
                }
            }
            if (msg.identity.Count == 0)
                return null;
            List<string> myusers = new List<string>();
            for (int i = 0; i < msg.identity.Count; i++)
            {
                if (!String.IsNullOrEmpty(msg.identity[i]))
                    myusers.Add(msg.identity[i]);
            }
            return myusers.ToArray();
        } // end GetUserIdentity



        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Auto)]
        public class MEMORYSTATUSEX
        {
            public uint dwLength;
            public uint dwMemoryLoad;
            public ulong ullTotalPhys;
            public ulong ullAvailPhys;
            public ulong ullTotalPageFile;
            public ulong ullAvailPageFile;
            public ulong ullTotalVirtual;
            public ulong ullAvailVirtual;
            public ulong ullAvailExtendedVirtual;
            public MEMORYSTATUSEX()
            {
                this.dwLength = (uint)Marshal.SizeOf(this);
                //this.dwLength = (uint)Marshal.SizeOf(typeof(NativeMethods.MEMORYSTATUSEX));
            }
        }

        [return: MarshalAs(UnmanagedType.Bool)]
        [DllImport("kernel32.dll", CharSet = CharSet.Auto, SetLastError = true)]
        public static extern bool GlobalMemoryStatusEx([In, Out] MEMORYSTATUSEX lpBuffer);


        /// <summary>
        /// this assumes that the message corrleator object is populated as required.
        /// minimum settings, url, classification and a unique message id
        /// </summary>
        /// <param name="mc"></param>
        public static void ProcessMessage(MessageCorrelator mc)
        {
            MessageProcessor mp = MessageProcessor.Instance;
            if (mc == null)
                return;
            
            if (mc.soapAction.Equals("urn:org:miloss:FGSMS:services:interfaces:dataCollector/dataCollectorService/AddData", StringComparison.CurrentCultureIgnoreCase))
                return;     //prevent recursive loops
            if (mc.soapAction.Equals("AddData", StringComparison.CurrentCultureIgnoreCase))
                return;     //prevent recursive loops
            if (mc.soapAction.Equals("AddMoreData", StringComparison.CurrentCultureIgnoreCase))
                return;     //prevent recursive loops
            if (mc.soapAction.Equals("urn:org:miloss:FGSMS:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy", StringComparison.CurrentCultureIgnoreCase))
                return;     //prevent recursive loops
            if (mc.soapAction.Equals("urn:org:miloss:FGSMS:services:interfaces:dataCollector/dataCollectorService/AddMoreData", StringComparison.CurrentCultureIgnoreCase))
                return;
            if (mc.soapAction.Equals(FGSMSConstants.DCSaction, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (mc.soapAction.Equals(FGSMSConstants.DCSaction2, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (mc.soapAction.Equals(FGSMSConstants.DCSaction3, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (mc.soapAction.Equals(FGSMSConstants.DCSaction4, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (mc.soapAction.Equals(FGSMSConstants.PCSaction, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (mc.soapAction.Equals(FGSMSConstants.PCSaction2, StringComparison.CurrentCultureIgnoreCase))
                return;
            Logger.debug(DateTime.Now.ToString("o") + " Process Message " + mc.MessageID);
          
            the_queue.Enqueue(mc);
            lock (publishingThread)
            {
                if (publishingThread == null || publishingThread.Count == 0)
                {
                    Thread t = new Thread(new ThreadStart(SendBulkPerformanceData));
                    t.Name = "FGSMS DataPusher from thread " + Thread.CurrentThread.ManagedThreadId;
                    t.Start();
                    publishingThread.Add(t);

                }
                if (the_queue.Count > 1000)// && !Event.agenttype.EndsWith("client", StringComparison.CurrentCultureIgnoreCase))
                {
                    lock (publishingThread)
                    {
                        if (publishingThread.Count < 2)
                        {
                            //Shutdown = false;
                            // ThreadStart ts = new ThreadStart(SendPerformanceData);

                            Thread t = new Thread(new ThreadStart(SendBulkPerformanceData));
                            t.Name = "FGSMS Data Pusher from thread " + Thread.CurrentThread.Name;
                            t.IsBackground = true;
                            t.Start();
                            publishingThread.Add(t);
                        }
                        else
                        {
                            for (int k = 0; k < publishingThread.Count; k++)
                            {
                                if (publishingThread[k] == null || !publishingThread[k].IsAlive)
                                {
                                    publishingThread[k] = new Thread(new ThreadStart(SendBulkPerformanceData));
                                    publishingThread[k].Name = "FGSMS Data Pusher from thread " + Thread.CurrentThread.Name;
                                    publishingThread[k].IsBackground = true;
                                    publishingThread[k].Start();
                                }
                            }

                        }
                    }
                }
            }
            Logger.debug(DateTime.Now.ToString("o") + " Process Message Exit " + mc.MessageID);
        }

        /// <summary>
        /// Enqueues a previously prepped message, typically used by the persistent storage agent
        /// this assumes that the message corrleator object is populated as required.
        /// minimum settings, url, classification and a unique message id
        /// </summary>
        /// <param name="mc"></param>
        public static void ProcessMessage(AddDataRequestMsg req)
        {
            MessageProcessor mp = MessageProcessor.Instance;
            if (req == null)
                return;
            the_queue.Enqueue(req);
            lock (publishingThread)
            {
                if (publishingThread == null || publishingThread.Count == 0)
                {
                    Thread t = new Thread(new ThreadStart(SendBulkPerformanceData));
                    t.Name = "FGSMS DataPusher from thread " + Thread.CurrentThread.ManagedThreadId;
                    t.Start();
                    publishingThread.Add(t);

                }
                if (the_queue.Count > 1000)// && !Event.agenttype.EndsWith("client", StringComparison.CurrentCultureIgnoreCase))
                {
                    lock (publishingThread)
                    {
                        if (publishingThread.Count < 2)
                        {
                            //Shutdown = false;
                            // ThreadStart ts = new ThreadStart(SendPerformanceData);

                            Thread t = new Thread(new ThreadStart(SendBulkPerformanceData));
                            t.Name = "FGSMS Data Pusher from thread " + Thread.CurrentThread.Name;
                            t.IsBackground = true;
                            t.Start();
                            publishingThread.Add(t);
                        }
                        else
                        {
                            for (int k = 0; k < publishingThread.Count; k++)
                            {
                                if (publishingThread[k] == null || !publishingThread[k].IsAlive)
                                {
                                    publishingThread[k] = new Thread(new ThreadStart(SendBulkPerformanceData));
                                    publishingThread[k].Name = "FGSMS Data Pusher from thread " + Thread.CurrentThread.Name;
                                    publishingThread[k].IsBackground = true;
                                    publishingThread[k].Start();
                                }
                            }

                        }
                    }
                }
            }
        }


        /// <summary>
        /// Attempts to send a single message sychronously
        /// this assumes that the prepedMessage object is populated as required.
        /// minimum settings, url, classification and a unique message id
        /// </summary>
        /// <param name="mc"></param>
        public static bool ProcessSingleMessage(AddDataRequestMsg prepedMessage)
        {
            MessageProcessor mp = MessageProcessor.Instance;
            if (prepedMessage == null)
                throw new ArgumentNullException();

            if (dcsservice != null)
                try
                {
                    ((IClientChannel)dcsservice).Dispose();
                    ((IClientChannel)dcsservice).Close();
                }
                catch { }
            dcsservice = config.GetDCSProxy(config.dcsurl[0]);
            AddDataRequestMsg[] req = new AddDataRequestMsg[1]; ;

            req[0] = prepedMessage;
            bool success = false;
            for (int b = 0; b < config.dcsurl.Count; b++)
            {
                if (!success)
                    for (int a = 0; a < config.DCSretrycount; a++)
                    {
                        try
                        {
                            AddDataResponseMsg response = dcsservice.AddMoreData(req);
                            if ((response.Status == DataResponseStatus.Failure))
                            {
                                //this case usually only happens when the same transaction was attempted to be written more than once or there was a problemn on the other end
                                success = true;
                            }
                            else
                            {
                                success = true;
                                break;
                            }
                        }

                        catch (Exception ex)
                        {
                            _lasterror = ex.GetType().FullName + " " + ex.Message;
                            Logger.error(ex, "FGSMS.MessageProcessor" + " ProcessSingleMessage - Data acquistion successful, but there was an error sending the information to the data collector.  (incorrect password or URL?) ");
                        }

                    }
                if (b + 1 < config.dcsurl.Count)
                {

                    if (dcsservice != null)
                    {
                        try
                        {
                            ((IClientChannel)dcsservice).Close();
                            ((IClientChannel)dcsservice).Dispose();
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                    dcsservice = config.GetDCSProxy(config.dcsurl[b + 1]);

                }
            }
            try
            {
                ((IClientChannel)dcsservice).Close();
                ((IClientChannel)dcsservice).Dispose();
            }
            catch { }
            return success;

        }




        /// <summary>
        /// An older version of the matrix, uses the alternate version which now supports recording headers.
        /// </summary>
        /// <param name="requestUrl"></param>
        /// <param name="start"></param>
        /// <param name="end"></param>
        /// <param name="soapAction"></param>
        /// <param name="requestMessage"></param>
        /// <param name="responseMessage"></param>
        /// <param name="messageID"></param>
        /// <param name="IsFault"></param>
        /// <param name="context"></param>
        /// <param name="ipaddress"></param>
        /// <param name="classname"></param>
        /// <param name="user"></param>
        [Obsolete]
        public static void ProcessMessage(string requestUrl,
           DateTime start,
           DateTime end,
           string soapAction,
           string requestMessage,
           string responseMessage,
           string messageID,
           bool IsFault,
           HttpContext context,
           string ipaddress,
           string classname,
           string user)
        {
            ProcessMessage(requestUrl, start, end, soapAction, requestMessage, responseMessage, messageID, IsFault, context, ipaddress, classname, user, String.Empty, string.Empty, string.Empty, null, null);
        }

        /// <summary>
        /// Adds a message to the processor queue
        /// </summary>
        /// <param name="requestUrl"></param>
        /// <param name="start"></param>
        /// <param name="end"></param>
        /// <param name="soapAction"></param>
        /// <param name="requestMessage"></param>
        /// <param name="responseMessage"></param>
        /// <param name="messageID"></param>
        /// <param name="IsFault">true if faulting message</param>
        /// <param name="context">HttpContext for things running inside IIS, nullable</param>
        /// <param name="ipaddress">IP of the requestor</param>
        /// <param name="classname">classname of the monitoring agent</param>
        /// <param name="user">Username of the person requesting the service</param>
        /// <param name="memo"></param>
        /// <param name="threadid"></param>
        /// <param name="relatedtransactionid"></param>
        /// <param name="requestheaders"></param>
        /// <param name="responseheaders"></param>
        public static void ProcessMessage(string requestUrl,
            DateTime start,
            DateTime end,
            string soapAction,
            string requestMessage,
            string responseMessage,
            string messageID,
            bool IsFault,
            HttpContext context,
            string ipaddress,
            string classname,
            string user, string memo,
            string threadid,
            string relatedtransactionid,
            NameValueCollection requestheaders,
            NameValueCollection responseheaders)
        {
            MessageProcessor i = MessageProcessor.Instance;
            Logger.debug(DateTime.Now.ToString("o") + " Process Message " + messageID + requestUrl);

            if (Trace.CorrelationManager.ActivityId == Guid.Empty)
            {
                Guid newGuid = Guid.NewGuid();
                Trace.CorrelationManager.ActivityId = newGuid;
            }
            soapAction = soapAction.Replace("\"", "");
            soapAction = soapAction.Replace("\'", "");
            soapAction = soapAction.Replace("{", "");
            soapAction = soapAction.Replace("}", "");
            //special case

            if (soapAction.Equals("urn:org:miloss:FGSMS:services:interfaces:dataCollector/dataCollectorService/AddData", StringComparison.CurrentCultureIgnoreCase))
                return;     //prevent recursive loops
            if (soapAction.Equals("AddData", StringComparison.CurrentCultureIgnoreCase))
                return;     //prevent recursive loops
            if (soapAction.Equals("AddMoreData", StringComparison.CurrentCultureIgnoreCase))
                return;     //prevent recursive loops
            if (soapAction.Equals("urn:org:miloss:FGSMS:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy", StringComparison.CurrentCultureIgnoreCase))
                return;     //prevent recursive loops
            if (soapAction.Equals("urn:org:miloss:FGSMS:services:interfaces:dataCollector/dataCollectorService/AddMoreData", StringComparison.CurrentCultureIgnoreCase))
                return;
            if (soapAction.Equals(FGSMSConstants.DCSaction, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (soapAction.Equals(FGSMSConstants.DCSaction2, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (soapAction.Equals(FGSMSConstants.DCSaction3, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (soapAction.Equals(FGSMSConstants.DCSaction4, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (soapAction.Equals(FGSMSConstants.PCSaction, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (soapAction.Equals(FGSMSConstants.PCSaction2, StringComparison.CurrentCultureIgnoreCase))
                return;
            if (ErrorState) return;
            MessageCorrelator Event = new MessageCorrelator();
            if (requestUrl.ToString().Equals("urn:undeterminable"))
            {
                //TODO maybe we should log this? The only time i've seen this happen is during a strange concurrency issue.
                Logger.warn(DateTime.Now.ToString("o") + " Could not process a message identified as " + requestUrl + soapAction + " because the url is marked as undeterminable");
                Event = null;
                return;
            }
            if (IsOnIgnoreList(requestUrl))
            {
                Logger.debug(DateTime.Now.ToString("o") + requestUrl + " is on the ignore list");
                return;
            }
            Logger.debug(DateTime.Now.ToString("o") + " process ignore list exit");
            Event.originalUrl = Event.URL = requestUrl.ToString();
            Event.agenttype = classname;
            Event.ipaddress = ipaddress;
            Event.RecievedAt = start;
            if (requestMessage == null)
                Event.requestsize = 0;
            else
                Event.requestsize = requestMessage.Length;
            if (responseMessage == null)
                Event.responsesize = 0;
            else
                Event.responsesize = responseMessage.Length;
            //memory reduction optimization, we really don't know how long this data will hang around in the queue,
            //if the policy is available we can reduce memory usage by not hanging on to the message content
            TransactionalWebServicePolicy tp = MessageProcessor.GetPolicyIfAvailable(Event.originalUrl, classname.ToLower().EndsWith("client"));
            if (tp != null)
            {
                if (tp.RecordRequestMessage || (tp.RecordFaultsOnly && IsFault) || ContainsSLAXpathOrUserIdentXpath(tp))
                {
                    if (MessageProcessor.MAXCAP < requestMessage.Length)
                        Event.RequestMessage = requestMessage.Substring(0, MessageProcessor.MAXCAP);
                    else Event.RequestMessage = requestMessage;
                }
                if (tp.RecordResponseMessage || (tp.RecordFaultsOnly && IsFault))
                {
                    if (MessageProcessor.MAXCAP < requestMessage.Length)
                        Event.ResponseMessage = responseMessage.Substring(0, MessageProcessor.MAXCAP);
                    else Event.ResponseMessage = responseMessage;
                }
            }
            else
            {
                if (requestMessage != null)
                {
                    //policy is not available, save the content just in case
                    if (MessageProcessor.MAXCAP < requestMessage.Length)
                        Event.RequestMessage = requestMessage.Substring(0, MessageProcessor.MAXCAP);
                    else Event.RequestMessage = requestMessage;
                }
                if (responseMessage != null)
                {
                    if (MessageProcessor.MAXCAP < responseMessage.Length)
                        Event.ResponseMessage = responseMessage.Substring(0, MessageProcessor.MAXCAP);
                    else Event.ResponseMessage = responseMessage;
                }
            }
            //Event.RequestMessage = requestMessage;
            Event.soapAction = soapAction;
            Event.MessageID = messageID;
            Event.CompletedAt = end;
            //Event.ResponseMessage = responseMessage;
            Event.identity = new List<string>();
            Event.relatedtransactionid = relatedtransactionid;
            Event.memo = memo;
            Event.threadid = threadid;
            Event.RequestHeaders = requestheaders;
            Event.ResponseHeaders = responseheaders;
            if (!String.IsNullOrEmpty(user))
                Event.identity.Add(user);
            if (!String.IsNullOrEmpty(ipaddress))
                Event.identity.Add(ipaddress);
            if (context != null)
            {
                if (context.Request.ClientCertificate != null)
                    Event.ClientCertificate = new X509Certificate2(context.Request.ClientCertificate.Certificate);
                //Event.RequestHeaders = context.Request.Headers;
                Event.userp = context.User;
                if (!Event.identity.Contains(context.User.Identity.Name))
                    Event.identity.Add(context.User.Identity.Name);
            }
            Logger.debug(DateTime.Now.ToString("o") + " Process Message enq " + messageID);

            the_queue.Enqueue(Event);
            lock (publishingThread)
            {
                if (publishingThread == null || publishingThread.Count == 0)
                {
                    Thread t = new Thread(new ThreadStart(SendBulkPerformanceData));
                    t.Name = "FGSMS DataPusher from thread " + Thread.CurrentThread.ManagedThreadId;
                    t.Start();
                    publishingThread.Add(t);
                    Logger.debug(DateTime.Now.ToString("o") + " Process Message thread start " + Thread.CurrentThread.ManagedThreadId + " " + messageID);
                }
                lock (publishingThread)
                {
                    //        int a = the_queue.Count / Environment.ProcessorCount;   //logical processors != cores;
                    if (the_queue.Count > 1000)// && !Event.agenttype.EndsWith("client", StringComparison.CurrentCultureIgnoreCase))
                    {


                        if (publishingThread.Count < 2)
                        {
                            //Shutdown = false;
                            // ThreadStart ts = new ThreadStart(SendPerformanceData);

                            Thread t = new Thread(new ThreadStart(SendBulkPerformanceData));
                            t.Name = "FGSMS Data Pusher from thread " + Thread.CurrentThread.Name;
                            t.IsBackground = true;
                            t.Start();
                            publishingThread.Add(t);
                            Logger.debug(DateTime.Now.ToString("o") + " Process Message thread start " + Thread.CurrentThread.ManagedThreadId + " " + messageID);
                        }
                        else
                        {
                            for (int k = 0; k < publishingThread.Count; k++)
                            {
                                if (publishingThread[k] == null || !publishingThread[k].IsAlive)
                                {
                                    publishingThread[k] = new Thread(new ThreadStart(SendBulkPerformanceData));
                                    publishingThread[k].Name = "FGSMS Data Pusher from thread " + Thread.CurrentThread.Name;
                                    publishingThread[k].IsBackground = true;
                                    publishingThread[k].Start();
                                    Logger.debug(DateTime.Now.ToString("o") + " Process Message thread start " + Thread.CurrentThread.ManagedThreadId + " " + messageID);
                                }
                            }

                        }

                    }
                }
            }

            Logger.debug(DateTime.Now.ToString("o") + " Process Message exit " + " " + messageID);
        }

        private static bool ContainsSLAXpathOrUserIdentXpath(TransactionalWebServicePolicy pol)
        {
            bool found = false;
            if (pol.UserIdentification != null && pol.UserIdentification.Length > 0)
            {
                for (int i = 0; i < pol.UserIdentification.Length; i++)
                {
                    if (pol.UserIdentification[i].XPaths != null
                            && pol.UserIdentification[i].XPaths.Length > 0)
                    {
                        found = true;
                    }
                }
            }
            if (found)
            {
                return true;
            }
            if (pol.ServiceLevelAggrements != null && pol.ServiceLevelAggrements.Length > 0)
            {
                for (int i = 0; i < pol.ServiceLevelAggrements.Length; i++)
                {
                    found = found || DoesSLAContainXpath(pol.ServiceLevelAggrements[i]);
                }
            }
            return found;
        }

        private static bool DoesSLAContainXpath(SLA get)
        {
            if (get == null)
            {
                return false;
            }
            return DoesSLARuleContainXpath(get.Rule);
        }

        private static bool DoesSLARuleContainXpath(RuleBaseType rule)
        {
            if (rule == null)
                return false;
            if (rule is XPathExpression)
            {
                return true;
            }
            if (rule is AndOrNot)
            {
                AndOrNot t1 = (AndOrNot)rule;
                return DoesSLARuleContainXpath(t1.LHS) || DoesSLARuleContainXpath(t1.RHS);
            }
            return false;
        }

        /// <summary>
        /// Returns a TransactionalWebServicePolicy, if and only if it has been fetched from the FGSMS server, otherwise null is returned
        /// </summary>
        /// <param name="url">The request or modified url</param>
        /// <param name="isclient">IsClient is a flag that will search for alternate request URLs and attempt to match the provided url with an modified one</param>
        /// <returns></returns>
        public static TransactionalWebServicePolicy GetPolicyIfAvailable(string url, bool isclient)
        {
            if (String.IsNullOrEmpty(url))
                return null;
            MessageProcessor mp = MessageProcessor.Instance;
            PolicyHelper p;
            if (policyCache.ContainsKey(url))
            {
                return ((PolicyHelper)policyCache[url]).policy;
            }
            if (policyCache.ContainsKey(url.Trim()))
            {
                return ((PolicyHelper)policyCache[url]).policy;
            }
            p = policyCache[url] as PolicyHelper;
            if (p != null)
                return p.policy;
            p = policyCache[ModifyURL(url, isclient)] as PolicyHelper;
            if (p != null)
                return p.policy;
            lock (policyCache)
            {
                IEnumerator it = policyCache.GetEnumerator();
                while (it.MoveNext())
                {
                    //  string k = it.Current.GetType().ToString();
                    DictionaryEntry s = (DictionaryEntry)it.Current;
                    if (s.Key.ToString().Trim().Equals(url.Trim(), StringComparison.CurrentCultureIgnoreCase))
                    {
                        p = s.Value as PolicyHelper;
                        if (p != null)
                            return p.policy;
                    }

                }
            };

            return null;
        }



        /// <summary>
        /// Purges the internal policy cache AND the URL mappings from request urls to the modified urls that FGSMS uses
        /// </summary>
        public static void PurgePolicyCache()
        {
            MessageProcessor mp = MessageProcessor.Instance;
            policyCache.Clear();
            urlmap.Clear();
        }

        private static LinkedList<IFGSMS_Callbacks> callbacks = new LinkedList<IFGSMS_Callbacks>();

        /// <summary>
        /// As of RC6.1, Enables client/service code to register for callbacks for when certain events occur
        /// </summary>
        /// <param name="obj"></param>
        public static void RegisterCallbacks(IFGSMS_Callbacks obj)
        {
            MessageProcessor mp = MessageProcessor.Instance;
            if (!callbacks.Contains(obj))
                callbacks.AddLast(obj);
        }
        /// <summary>
        ///  As of RC6.1, unregisters a callback
        /// </summary>
        /// <param name="obj"></param>
        public static void UnRegisterCallbacks(IFGSMS_Callbacks obj)
        {
            MessageProcessor mp = MessageProcessor.Instance;
            if (callbacks.Contains(obj))
                callbacks.Remove(obj);
        }

        private static void DoCallbacks_EmptyQueue()
        {
            List<IFGSMS_Callbacks> removitems = new List<IFGSMS_Callbacks>();
            lock (callbacks)
            {
                IEnumerator<IFGSMS_Callbacks> it = callbacks.GetEnumerator();
                while (it.MoveNext())
                {
                    try
                    {
                        it.Current.NotifyEmptyQueue();
                    }
                    catch
                    {
                        removitems.Add(it.Current);
                    }
                }
                for (int i = 0; i < removitems.Count; i++)
                {
                    callbacks.Remove(removitems[i]);
                }
            };
        }

        private static void DoCallbacks_LastMessageSent()
        {
            List<IFGSMS_Callbacks> removitems = new List<IFGSMS_Callbacks>();
            lock (callbacks)
            {
                IEnumerator<IFGSMS_Callbacks> it = callbacks.GetEnumerator();
                while (it.MoveNext())
                {
                    try
                    {
                        it.Current.NotifyEmptyQueueAllMessagesHaveBeenSent();
                    }
                    catch
                    {
                        removitems.Add(it.Current);
                    }
                }
                for (int i = 0; i < removitems.Count; i++)
                {
                    callbacks.Remove(removitems[i]);
                }
            };
        }

        /// <summary>
        /// this is a helper function to help agents determine if they need to record playloads
        /// contains logic for std policy settings and for xpath/user
        /// </summary>
        /// <param name="url"></param>
        /// <param name="isclient"></param>
        /// <returns></returns>
        public static bool ShouldAgentRecordRequestPayload(string url, bool isclient)
        {
            TransactionalWebServicePolicy tp = GetPolicyIfAvailable(url, isclient);

            if (tp != null)
            {
                if (tp.RecordRequestMessage || tp.RecordFaultsOnly)
                    return true;
                if (ContainsSLAXpathOrUserIdentXpath(tp))
                    return true;
            }

            return true;
        }
        /// <summary>
        /// this is a helper function to help agents determine if they need to record playloads
        /// contains logic for std policy settings and for xpath/user
        /// </summary>
        /// <param name="url"></param>
        /// <param name="isclient"></param>
        /// <returns></returns>
        public static bool ShouldAgentRecordResponsePayload(string url, bool isclient)
        {
            TransactionalWebServicePolicy tp = GetPolicyIfAvailable(url, isclient);

            if (tp != null)
            {
                if (tp.RecordResponseMessage || tp.RecordFaultsOnly)
                    return true;
                if (ContainsSLAXpathOrUserIdentXpath(tp))
                    return true;
            }

            return true;
        }




    } // end class MessageProcessor


    /// <summary>
    /// provides a temporary container for storing message information
    /// </summary>
    public class MessageCorrelator
    {
        public int requestsize = 0;
        public int responsesize = 0;
        public string soapAction;
        public string URL;
        public string MessageID;
        public string originalUrl;
        public string ipaddress;
        public string agenttype;
        public string RequestMessage;
        public string ResponseMessage;
        public string memo;
        public string relatedtransactionid;
        public string threadid;
        public DateTime CompletedAt;
        public DateTime RecievedAt;
        public bool IsFault;
        public NameValueCollection RequestHeaders;
        public NameValueCollection ResponseHeaders;
        public System.Security.Principal.IPrincipal userp;
        public List<string> identity;
        public X509Certificate2 ClientCertificate;
    } // end class MessageCorrelator

    /// <summary>
    /// provides a storage class for service policies. this is used in a hashtable to cache message policies
    ///
    /// </summary>
    public class PolicyHelper
    {
        public TransactionalWebServicePolicy policy;
        public DateTime lastUpdate;
    } // end class PolicyHelper




} // end namespace org.miloss.fgsms.agents
