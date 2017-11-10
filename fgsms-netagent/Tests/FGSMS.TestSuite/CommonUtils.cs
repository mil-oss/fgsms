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
using System.Linq;
using System.Text;
using System.ServiceModel;
using org.miloss.fgsms.agent;
using NUnit.Framework;
using System.Runtime.Serialization;
using System.Net.Security;
using System.Threading;
using System.ServiceModel.Channels;
using System.Net;
using Campari.Software;
using System.Configuration;
using System.ServiceProcess;
using System.Reflection;

namespace FGSMS.NETTestSuite
{
    internal static class CommonUtils
    {

        public static Configuration GetMappedConfig()
        {
            ExeConfigurationFileMap f = new ExeConfigurationFileMap(getCurrentConfigPath());
            Configuration c = ConfigurationManager.OpenMappedExeConfiguration(f, ConfigurationUserLevel.None);
            return c;
        }

        private static string getCurrentConfigPath()
        {
            return Assembly.GetExecutingAssembly().Location + ".config";
        }


        /**
         * Failed	TC00136_ASPNETMonitoredThickClienttoASPNETserviceMonitored	FGSMS.NETTestSuite	Assert.Fail failed. 
         * VerifyLastMessagePayloadsTwoAgentsOneTransaction, more total transactions returned that expected 1 vs 2
         * VerifyLastMessagePayloadsTwoAgentsOneTransaction, more actual transactions returned that expected 1 vs 2	
         */
        public static string VerifyLastMessagePayloadsTwoAgentsOneTransaction(string url_internal)
        {
            dataAccessService das = GetDASProxyAdmin();
            GetRecentMessageLogsRequestMsg req = new GetRecentMessageLogsRequestMsg();
            
            req.classification = new SecurityWrapper();
            req.offset = 0;
            req.offsetSpecified = true;
            req.records = 2;
            req.URL = url_internal;
            GetMessageLogsResponseMsg res = das.GetRecentMessageLogs(req);

            string ret = "";
            if (res == null || res == null || res.logs == null || res.logs.Length == 0)
                ret += ("VerifyLastMessagePayloadsTwoAgentsOneTransaction, no results found");
            //  if (res.GetMessageLogsResult.TotalRecords != 2)
            //      ret += "VerifyLastMessagePayloadsTwoAgentsOneTransaction, more/less total transactions returned that expected " + res.GetMessageLogsResult.TotalRecords + " vs " + 2;
            if (res.logs.Length > 2)
                ret += "VerifyLastMessagePayloadsTwoAgentsOneTransaction, more transactional log verification results were returned than expected" + res.logs.Length;
            if (res.logs.Length < 2)
            {
                ret += "VerifyLastMessagePayloadsTwoAgentsOneTransaction, less actual transactions returned that expected " + res.logs.Length + " vs " + 2;
            }


            //get the details
            GetMessageTransactionLogDetailsResponseMsg t1 = new GetMessageTransactionLogDetailsResponseMsg();
            GetMessageTransactionLogDetailsResponseMsg t2 = new GetMessageTransactionLogDetailsResponseMsg();
            GetMessageTransactionLogDetailsMsg r = new GetMessageTransactionLogDetailsMsg();
            r.classification = new SecurityWrapper();
            if (res.logs.Length > 0)
            {
                r.transactionID = res.logs[0].transactionId;

                t1 = das.GetMessageTransactionLogDetails(r);
            }
            if (res.logs.Length > 1)
            {
                r.transactionID = res.logs[1].transactionId;
                t2=das.GetMessageTransactionLogDetails(r);
            }

            das.Dispose();

            string agentsdata = "";
            // if (!String.IsNullOrEmpty(ret))
            if (t1 != null)
                agentsdata = t1.agentType + ":" + t1.transactionId;
            if (t2 != null)
                agentsdata = t2.agentType + ":" + t2.transactionId;
            if (!String.IsNullOrEmpty(ret))
                return ret + agentsdata;
            //verify transaction thread id matches

            if (String.IsNullOrEmpty(t1.relatedTransactionID))
                ret = "the transaction " + t1.transactionId + " did not have a related transaction id, this means that an http header was not propagated,  either by either recording agent, agent " + t1.agentType + " " + t1.agentMemo;

            if (String.IsNullOrEmpty(t2.relatedTransactionID))
                ret = "the transaction " + t2.transactionId + " did not have a related transaction id, this means that an http header was not propagated  either by either recording agent, agent " + t2.agentType + " " + t2.agentMemo;
            if (!String.IsNullOrEmpty(ret))
            {
                return ret;//prevent npe
            }
            if (!t1.transactionthreadId.Equals(t2.transactionthreadId, StringComparison.CurrentCultureIgnoreCase))
                ret += "VerifyLastMessagePayloadsTwoAgentsOneTransaction, the transaction thread id's do not match" + t1.transactionthreadId + " vs " + t2.transactionthreadId;
            if (!t1.relatedTransactionID.Equals(t2.transactionId, StringComparison.CurrentCultureIgnoreCase))
                ret += "VerifyLastMessagePayloadsTwoAgentsOneTransaction, the transaction related message id's does not match. this probably means an http wasn't propagated t1 " + t1.relatedTransactionID + " vs t2 " + t2.transactionId;
            if (!t2.relatedTransactionID.Equals(t1.transactionId, StringComparison.CurrentCultureIgnoreCase))
                ret += "VerifyLastMessagePayloadsTwoAgentsOneTransaction, the transaction related message id's does not match. this probably means an http wasn't propagated t1 " + t2.relatedTransactionID + " vs t2 " + t1.transactionId;

            return ret;

        }


        /// <summary>
        /// return empty string if nothing went wrong
        /// </summary>
        /// <param name="url"></param>
        /// <param name="request"></param>
        /// <param name="response"></param>
        /// <returns></returns>
        public static string VerifyLastMessagePayloads(string url, bool request, bool response, int expectedcount)
        {
            dataAccessService das = GetDASProxyAdmin();
            GetRecentMessageLogsRequestMsg req = new GetRecentMessageLogsRequestMsg();
            
            req.classification = new SecurityWrapper();
            req.offset = 0;
            req.offsetSpecified = true;
            req.records = 1;
            req.URL = url;
            GetMessageLogsResponseMsg res = das.GetRecentMessageLogs(req);
            
            string ret = "";
            if (res == null || res == null || res.logs == null || res.logs.Length == 0)
                return ("VerifyLastMessagePayloads, no results found");
            if (res.TotalRecords != expectedcount)
                ret += "VerifyLastMessagePayloads, less/more total transactions returned that expected " + res.TotalRecords + " vs " + expectedcount;
            if (res.logs.Length > expectedcount)
                ret += "VerifyLastMessagePayloads, more transactional log verification results were returned than expected" + res.logs.Length;
            if (request && (res.logs[0] == null || res.logs[0].hasRequestMessage))
                ret += "VerifyLastMessagePayloads, transactional log verification: request was not recorded";
            if (response && (res.logs[0] == null || res.logs[0].hasResponseMessage))
                ret += "VerifyLastMessagePayloads, transactional log verification: response was not recorded";
            if (res.logs[0] == null || res.logs[0].timestamp == null)
                ret += "VerifyLastMessagePayloads, transactional log verification: timestamp was not recorded";
            TimeSpan ts = new TimeSpan(DateTime.Now.Ticks - res.logs[0].timestamp.Ticks);
            if (ts.TotalMinutes > 1)
                ret += "VerifyLastMessagePayloads, timestamp was more than a minute different";
            return ret;

        }

        public static void RemoveService(string internalurl)
        {
            ConfigLoader cf = new ConfigLoader();
            PCSBinding svc = GetPCSProxyAdmin(cf.PCSurls[0]);
            DeleteServicePolicyRequestMsg r = new DeleteServicePolicyRequestMsg();
            
            r.classification = new SecurityWrapper();
            r.deletePerformanceData = true;
            r.URL = internalurl;
            svc.DeleteServicePolicy(r);
        }

        public static void SetPolicy(string internalurl, bool recordrequest, bool requestresponse)
        {
            if (String.IsNullOrEmpty(internalurl))
                throw new ArgumentNullException("url");
            TransactionalWebServicePolicy tp = new TransactionalWebServicePolicy();
            tp.PolicyType = policyType.Transactional;
            tp.URL = internalurl;
            tp.RecordHeaders = true;
            tp.RecordRequestMessage = recordrequest;
            tp.RecordResponseMessage = requestresponse;
            tp.MachineName = Environment.MachineName.ToLower();
            tp.BuellerEnabled = false;
            ConfigLoader cf = new ConfigLoader();
            PCSBinding svc = GetPCSProxyAdmin(cf.PCSurls[0]);
            SetServicePolicyRequestMsg req = new SetServicePolicyRequestMsg();
            
            req.classification = new SecurityWrapper();
            req.policy = tp;
            req.URL = internalurl;
            svc.SetServicePolicy(req);

        }

        public static dataAccessService GetDASProxyAdmin()
        {
            ServicePointManager.Expect100Continue = false;
            dataAccessService r = new dataAccessService();
            r.Credentials = new NetworkCredential(ConfigurationManager.AppSettings["FGSMSadmin"], ConfigurationManager.AppSettings["FGSMSadminpass"]);
            r.Url = ConfigurationManager.AppSettings["dataaccessservice"];
            return r;
          /*  BasicHttpBinding b = null;
            b = new BasicHttpBinding(BasicHttpSecurityMode.TransportCredentialOnly);
            b.Security.Transport.Realm = "FGSMS Services";
            b.MaxBufferSize = Int32.MaxValue;
            b.MaxReceivedMessageSize = Int32.MaxValue;
            b.MaxBufferPoolSize = Int32.MaxValue;
            b.TransferMode = TransferMode.Buffered;
            b.Name = "AgentBinding";
            b.ReaderQuotas.MaxArrayLength = Int32.MaxValue;
            b.ReaderQuotas.MaxBytesPerRead = Int32.MaxValue;
            b.ReaderQuotas.MaxDepth = Int32.MaxValue;
            b.ReaderQuotas.MaxNameTableCharCount = Int32.MaxValue;
            b.ReaderQuotas.MaxStringContentLength = Int32.MaxValue;
            b.ReceiveTimeout = new TimeSpan(0, 5, 0);
            b.SendTimeout = new TimeSpan(0, 5, 0);
            b.UseDefaultWebProxy = true;
            b.TextEncoding = Encoding.UTF8;
            b.OpenTimeout = new TimeSpan(0, 0, 5);
            //b.Namespace = "urn:org.miloss.FGSMS.services.interfaces.policyConfiguration";
            b.MessageEncoding = WSMessageEncoding.Text;
            b.CloseTimeout = new TimeSpan(0, 0, 5);


            b.Security.Transport.ClientCredentialType = HttpClientCredentialType.Basic;


            ChannelFactory<dataAccessService> factory =
                new ChannelFactory<dataAccessService>(b, ConfigurationManager.AppSettings["dataaccessservice"]);
            HttpRequestMessageProperty p = new HttpRequestMessageProperty();
            p.Headers.Add(HttpRequestHeader.Authorization, "Basic " + Convert.ToBase64String(Encoding.ASCII.GetBytes(ConfigurationManager.AppSettings["FGSMSadmin"] + ":" + ConfigurationManager.AppSettings["FGSMSadminpass"])));

            // factory.Credentials.UserName = new System.ServiceModel.Security.UserNamePasswordClientCredential();
            factory.Credentials.UserName.UserName = ConfigurationManager.AppSettings["FGSMSadmin"];
            factory.Credentials.SupportInteractive = false;
            factory.Credentials.UserName.Password = ConfigurationManager.AppSettings["FGSMSadminpass"];


            dataAccessService polservice = factory.CreateChannel();
            OperationContextScope scope = new OperationContextScope((IClientChannel)polservice);
            OperationContext.Current.OutgoingMessageProperties[HttpRequestMessageProperty.Name] = p;
            return polservice;*/
        }

        public static PCSBinding GetPCSProxyAdmin(string url)
        {
            ServicePointManager.Expect100Continue = false;
            PCSBinding r = new PCSBinding();
            r.Url = url;
            r.Credentials = new NetworkCredential( ConfigurationManager.AppSettings["FGSMSadmin"], ConfigurationManager.AppSettings["FGSMSadminpass"]);
            return r;
            /*
            BasicHttpBinding b = null;
            b = new BasicHttpBinding(BasicHttpSecurityMode.TransportCredentialOnly);
            b.Security.Transport.Realm = "FGSMS Services";
            b.MaxBufferSize = Int32.MaxValue;
            b.MaxReceivedMessageSize = Int32.MaxValue;
            b.MaxBufferPoolSize = Int32.MaxValue;
            b.TransferMode = TransferMode.Buffered;
            b.Name = "AgentBinding";
            b.ReaderQuotas.MaxArrayLength = Int32.MaxValue;
            b.ReaderQuotas.MaxBytesPerRead = Int32.MaxValue;
            b.ReaderQuotas.MaxDepth = Int32.MaxValue;
            b.ReaderQuotas.MaxNameTableCharCount = Int32.MaxValue;
            b.ReaderQuotas.MaxStringContentLength = Int32.MaxValue;
            b.ReceiveTimeout = new TimeSpan(0, 5, 0);
            b.SendTimeout = new TimeSpan(0, 5, 0);
            b.UseDefaultWebProxy = true;
            b.TextEncoding = Encoding.UTF8;
            b.OpenTimeout = new TimeSpan(0, 0, 5);
            //b.Namespace = "urn:org.miloss.FGSMS.services.interfaces.policyConfiguration";
            b.MessageEncoding = WSMessageEncoding.Text;
            b.CloseTimeout = new TimeSpan(0, 0, 5);


            b.Security.Transport.ClientCredentialType = HttpClientCredentialType.Basic;


            ChannelFactory<PCS> factory =
                new ChannelFactory<PCS>(b, url);
            HttpRequestMessageProperty p = new HttpRequestMessageProperty();
            p.Headers.Add(HttpRequestHeader.Authorization, "Basic " + Convert.ToBase64String(Encoding.ASCII.GetBytes(ConfigurationManager.AppSettings["FGSMSadmin"] + ":" + ConfigurationManager.AppSettings["FGSMSadminpass"])));

            // factory.Credentials.UserName = new System.ServiceModel.Security.UserNamePasswordClientCredential();
            factory.Credentials.UserName.UserName = ConfigurationManager.AppSettings["FGSMSadmin"];
            factory.Credentials.SupportInteractive = false;
            factory.Credentials.UserName.Password = ConfigurationManager.AppSettings["FGSMSadminpass"];


            PCS polservice = factory.CreateChannel();
            OperationContextScope scope = new OperationContextScope((IClientChannel)polservice);
            OperationContext.Current.OutgoingMessageProperties[HttpRequestMessageProperty.Name] = p;
            return polservice;*/
        }

        internal static void BounceIIS()
        {
            ServiceController[] svc = ServiceController.GetServices();
            for (int i = 0; i < svc.Length; i++)
            {
                if (svc[i].ServiceName.Equals("w3svc", StringComparison.CurrentCultureIgnoreCase))
                {
                    svc[i].Stop();
                    svc[i].WaitForStatus(ServiceControllerStatus.Stopped, new TimeSpan(0, 5, 0));
                    svc[i].Start();
                    svc[i].WaitForStatus(ServiceControllerStatus.Running, new TimeSpan(0, 5, 0));
                }
            }
        }

        internal static string VerifyServiceChainingEvents(string urlFirstHop, string urlSecondHop, bool hasrequest, bool hasresponse)
        {
            //expect exactly 4 transactions
            //one from the client, to the first hop
            //one from the service on first hop
            //one from the first hop client to second hop
            //one from the second hop service
            dataAccessService das = GetDASProxyAdmin();
            GetRecentMessageLogsRequestMsg req = new GetRecentMessageLogsRequestMsg();
            
            req.classification = new SecurityWrapper();
            req.offset = 0;
            req.offsetSpecified = true;
            req.records = 2;
            req.URL = urlFirstHop;
            GetMessageLogsResponseMsg firsthoprecords = das.GetRecentMessageLogs(req);

            req = new GetRecentMessageLogsRequestMsg();
            req= new GetRecentMessageLogsRequestMsg();
            req.classification = new SecurityWrapper();
            req.offset = 0;
            req.offsetSpecified = true;
            req.records = 2;
            req.URL = urlSecondHop;
            GetMessageLogsResponseMsg secondhoprecords = das.GetRecentMessageLogs(req);
            string ret = "";
            if (firsthoprecords == null || firsthoprecords == null || firsthoprecords.logs == null)
                ret += "no records returned for first hop at " + urlFirstHop;
            if (secondhoprecords == null || secondhoprecords== null || secondhoprecords.logs == null)
                ret += "no records returned for second hop at " + urlSecondHop;
            if (!String.IsNullOrEmpty(ret))
            {
                return ret;
            }
            if (firsthoprecords.logs.Length != 2)
                ret += "expected 2 records for " + urlFirstHop + " but recieved " + firsthoprecords.logs.Length;
            if (secondhoprecords.logs.Length != 2)
                ret += "expected 2 records for " + urlSecondHop + " but recieved " + secondhoprecords.logs.Length;
            if (!String.IsNullOrEmpty(ret))
            {
              
                return ret;
            }
            //transaction thread id should be the same on all records
            GetMessageTransactionLogDetailsResponseMsg t1 = new GetMessageTransactionLogDetailsResponseMsg();
            GetMessageTransactionLogDetailsResponseMsg t2 = new GetMessageTransactionLogDetailsResponseMsg();
            GetMessageTransactionLogDetailsResponseMsg t3 = new GetMessageTransactionLogDetailsResponseMsg();
            GetMessageTransactionLogDetailsResponseMsg t4 = new GetMessageTransactionLogDetailsResponseMsg();
            GetMessageTransactionLogDetailsMsg r = new GetMessageTransactionLogDetailsMsg();
            r.classification = new SecurityWrapper();
            r.transactionID = firsthoprecords.logs[0].transactionId;
            t1=das.GetMessageTransactionLogDetails(r);

            r.transactionID = firsthoprecords.logs[1].transactionId;
            t2=das.GetMessageTransactionLogDetails(r);

            r.transactionID = secondhoprecords.logs[0].transactionId;
            t3=das.GetMessageTransactionLogDetails(r);
            r.transactionID = secondhoprecords.logs[1].transactionId;
            t4=das.GetMessageTransactionLogDetails(r);

            if (!t1.transactionthreadId.Equals(t2.transactionthreadId) ||
                !t1.transactionthreadId.Equals(t3.transactionthreadId) ||
                !t1.transactionthreadId.Equals(t4.transactionthreadId))
                ret += "at least one of the thread id's didn't match up, meaning the chaining wasn't recorded correctly.";

            if (!t1.relatedTransactionID.Equals(t2.transactionId) || !t2.relatedTransactionID.Equals(t1.transactionId))
            {
                ret += " the first hops related/transaction ids didn't match up";
            }

            if (!t3.relatedTransactionID.Equals(t4.transactionId) || !t3.relatedTransactionID.Equals(t4.transactionId))
            {
                ret += " the second hops related/transaction ids didn't match up";
            }
            //message and related message ids should pair up


            //check request/response recording
            



          //  Assert.Inconclusive("i haven't finished this code yet");
            return ret;
        }
    }
}
