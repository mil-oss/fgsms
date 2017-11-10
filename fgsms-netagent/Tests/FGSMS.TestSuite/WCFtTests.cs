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
using System.Text;
using System.Collections.Generic;
using NUnit.Framework;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Net.Security;
using org.miloss.fgsms.agent;
using System.Threading;
using System.ServiceModel.Channels;
using System.Net;
using Campari.Software;

namespace FGSMS.NETTestSuite
{
    [TestFixture]
    public class WCFtTests
    {
        
        string url2mod = "http://" + Environment.MachineName.ToLower() + ":80/HelloWorldESMWCF2/HelloWorldESMWCF2.svc";


        #region TC00035 monitor a service from a thin client running in iis
        
        string url3 = "http://localhost/HelloWorldESMTester/HelloWorldESMWCF.svc";
        string url3mod = "http://" + Environment.MachineName.ToLower() + ":80/HelloWorldESMTester/HelloWorldESMWCF.svc";
        [Test]
        public void TC00035_1_SanityCheck()
        {
            //assuming that it's being monitored on the service side per default config

            //remove policy and records
            //hit the no monitor endpoint
            //confirm no policy was created and no records exist

            //TC00035_doWork("http://localhost/HelloWorldESMTester/callNoMonitor.aspx", false, false);
        }

        [Test]
        public void TC00035_2_ThinClientNoPayloadsNoClientMonitorServiceMonitor()
        {
            TC00035_doWork("http://localhost/HelloWorldESMTester/callNoMonitor.aspx", false, false, 1);
            //there should be exactly one transaction recorded, since this service is monitored via web.config settings
        }


        [Test]
        public void TC00035_3_ThinClientNoPayloadsWithClientMonitorAndServiceMonitor()
        {
            TC00035_doWork("http://localhost/HelloWorldESMTester/callWithMonitor.aspx", false, false, 2);
            //there should be exactly two transactions recorded, since this service is monitored via web.config settings
        }

        [Test]
        public void TC00035_4_ThinClientWithPayloadsWithClientMonitorAndServiceMonitor()
        {
            TC00035_doWork("http://localhost/HelloWorldESMTester/callWithMonitor.aspx", true, true, 2);
            //there should be exactly two transactions recorded, since this service is monitored via web.config settings
        }

      /*  [Test]
        public void TC00134_1_ChainingThinClientToWCFServiceToWCFService()
        {
            TC00035_doWork("http://localhost/HelloWorldESMTester/callWithMonitor.aspx", true, true, 2);
            //there should be exactly two transactions recorded, since this service is monitored via web.config settings
        }*/
        //Failed	TC00035_3_ThinClientNoPayloadsWithClientMonitorAndServiceMonitor	FGSMS.NETTestSuite	
      /*  Assert.Fail failed. the transaction 05604057-bff9-4907-8527-f9c6b68fc4dc did not have a related transaction id, 
            this means that an http header was not propagated agent org.miloss.fgsms.agent.wcf.AgentWCFClientMessageInspector.client 
        Agent Outbound queue: 0 Outbound Threads 1 Thread FGSMS DataPusher from thread 13 mem stat 324771840/4294434816	
*/
        void TC00035_doWork(string url, bool recordrequest, bool recordresponse, int expectedcount)
        {
            Thread.Sleep(10000);
            string st = "";
            try
            {
               CommonUtils.RemoveService(url3mod);
               CommonUtils.SetPolicy(url3mod, recordrequest, recordresponse);

                WebClient cw = new WebClient();
                st = cw.DownloadString(url);
                Thread.Sleep(15000);
                DateTime timeout = DateTime.Now.AddMinutes(1);
                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
            }
            catch (Exception ex)
            {
                CommonUtils.RemoveService(url3mod);
                string s = ""; while (ex != null)
                {
                    s += ex.Message + ex.StackTrace;
                    ex = ex.InnerException;
                }
                Assert.Fail(s);
            }
            string err = CommonUtils.VerifyLastMessagePayloads(url3mod, recordrequest, recordresponse, expectedcount);
            if (expectedcount == 2)
                err += err = CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(url3mod);
            if (st.Contains("ERROR"))
                err += st;
            CommonUtils.RemoveService(url3mod);
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);
        }

        
        #endregion



        #region TC00033 monitor an IIS hosted service, service side
        [Test]
        public void TC00033_1_MonitorIISHostedServiceDefault()
        {
            TC00033_doWorkRemote2(false, false);
        }

        [Test]
        public void TC00033_2_MonitorIISHostedServiceRecordRequestDefault()
        {
            TC00033_doWorkRemote2(true, false);
        }
        [Test]
        public void TC00033_3_MonitorIISHostedServiceRecordResponseDefault()
        {
            TC00033_doWorkRemote2(false, true);
        }
        [Test]
        public void TC00033_4_MonitorIISHostedServiceRecordRequestAndResponseDefault()
        {
            TC00033_doWorkRemote2(true, true);
        }



        void TC00033_doWorkRemote2(bool recordrequest, bool recordresponse)
        {
            Thread.Sleep(10000);
            try
            {
                CommonUtils.RemoveService(url2mod);
                CommonUtils.SetPolicy(url2mod, recordrequest, recordresponse);

                HelloWorldESMWCF2.Service1Client c = new HelloWorldESMWCF2.Service1Client();
                string s = c.GetData(5);
                c.Close();
                Thread.Sleep(15000);
                DateTime timeout = DateTime.Now.AddMinutes(1);
                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
            }
            catch (Exception ex)
            {
                string _err = "";
                while (ex != null)
                {
                    _err += ex.Message + " " + ex.StackTrace;
                    ex = ex.InnerException;
                }
                CommonUtils.RemoveService(url_mod);
                Assert.Fail(_err);
            }
            string err = CommonUtils.VerifyLastMessagePayloads(url2mod, recordrequest, recordresponse, 1);
            CommonUtils.RemoveService(url2mod);
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);
        }
        #endregion

        #region TC00032 monitor a self hosted service
        [Test]
        public void TC00032_1_MonitorSelftHostedServiceRecordRequestAndResponse()
        {
            TC00032_doWork(true, true);
        }

        [Test]
        public void TC00032_2_MonitorSelftHostedServiceRecordNotRequestAndResponse()
        {
            TC00032_doWork(false, false);
        }


        [Test]
        public void TC00032_3_MonitorSelftHostedServiceRecordRequestAndNotResponse()
        {
            TC00032_doWork(true, false);
        }

        [Test]
        public void TC00032_4_MonitorSelftHostedServiceNotRecordRequestAndRecordResponse()
        {
            TC00032_doWork(false, true);
        }


        /// <summary>
        /// self hosted service, service handler added programmatically
        /// </summary>
        /// <param name="recordrequest"></param>
        /// <param name="recordresponse"></param>
        void TC00032_doWork(bool recordrequest, bool recordresponse)
        {
            Thread.Sleep(10000);
            CommonUtils.RemoveService(url_mod);
            CommonUtils.SetPolicy(url_mod, recordrequest, recordresponse);

            try
            {
                MessageProcessor mp = MessageProcessor.Instance;
                MessageProcessor.PurgePolicyCache();
                MessageProcessor.PurgeOutboundQueue();
                Assert.IsTrue(MessageProcessor.GetPolicyCacheSize() == 0);
                Assert.IsTrue(MessageProcessor.GetQueueSize() == 0);
                ServiceHost host = new ServiceHost(typeof(FGSMSNetServiceImpl));
                host.Description.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior));
                host.Description.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior());

                host.Open();

                IFGSMSNetService svc = getproxy(url);
                SomeComplexRequestObject req = new SomeComplexRequestObject();
                req.stdout = "hi";

                svc.getData(req);

                Thread.Sleep(5000);
                DateTime timeout = DateTime.Now.AddMinutes(1);
                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (MessageProcessor.GetQueueSize() > 0 && DateTime.Now < timeout)
                    Thread.Sleep(1000);
                ((IClientChannel)svc).Close();
                ((IClientChannel)svc).Dispose();

                host.Close();
            }
            catch (Exception ex)
            {
                string _err = "";
                while (ex != null)
                {
                    _err += ex.Message + " " + ex.StackTrace;
                    ex = ex.InnerException;
                }
                CommonUtils.RemoveService(url_mod);
                Assert.Fail(_err);
            }

            Thread.Sleep(10000);
            string err = "";

            if (MessageProcessor.GetPolicyCacheSize() == 0)
                err += "policy cache is empty, it should have at least one item in it";
            if (!String.IsNullOrEmpty(MessageProcessor.LastErrorMessage))
                err += "agent error " + MessageProcessor.LastErrorMessage;

            TransactionalWebServicePolicy tp = MessageProcessor.GetPolicyIfAvailable(url_mod, false);
            if (tp == null)
                err += "the policy for the service was not cached, which means something went wrong";
            err += CommonUtils.VerifyLastMessagePayloads(url_mod, recordrequest, recordresponse, 1);
            CommonUtils.RemoveService(url_mod);
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);
        }
        #endregion

        #region TC00034 Monitor from the client side to a self hosted wcf service
        [Test]
        public void TC00034_1_MonitorSelftHostedClientRecordRequestAndResponse()
        {
            TC00034_doWork(true, true);
        }

        [Test]
        public void TC00034_2_MonitorSelftHostedClientRecordNotRequestAndResponse()
        {
            TC00034_doWork(false, false);
        }


        [Test]
        public void TC00034_3_MonitorSelftHostedClientRecordRequestAndNotResponse()
        {
            TC00034_doWork(true, false);
        }

        [Test]
        public void TC00034_4_MonitorSelftHostedClientNotRecordRequestAndRecordResponse()
        {
            TC00034_doWork(false, true);
        }


        /// <summary>
        /// self hosted service, client handler added programmatically
        /// </summary>
        /// <param name="recordrequest"></param>
        /// <param name="recordresponse"></param>
        void TC00034_doWork(bool recordrequest, bool recordresponse)
        {
            Thread.Sleep(10000);
            CommonUtils.RemoveService(url_mod);
            CommonUtils.SetPolicy(url_mod, recordrequest, recordresponse);

            try
            {
                MessageProcessor mp = MessageProcessor.Instance;
                MessageProcessor.PurgePolicyCache();
                MessageProcessor.PurgeOutboundQueue();
                Assert.IsTrue(MessageProcessor.GetPolicyCacheSize() == 0);
                Assert.IsTrue(MessageProcessor.GetQueueSize() == 0);
                ServiceHost host = new ServiceHost(typeof(FGSMSNetServiceImpl));
                host.Description.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior));
                //host.Description.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior());
                host.Open();
                IFGSMSNetService svc = getproxyWithMonitor(url_mod);
                SomeComplexRequestObject req = new SomeComplexRequestObject();
                req.stdout = "hi";

                svc.getData(req);

                Thread.Sleep(5000);
                DateTime timeout = DateTime.Now.AddMinutes(2);
                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (MessageProcessor.GetQueueSize() > 0 && DateTime.Now < timeout)
                    Thread.Sleep(1000);
                ((IClientChannel)svc).Close();
                ((IClientChannel)svc).Dispose();
                host.Close();
            }
            catch (Exception ex)
            {
                string _err = "";
                while (ex != null)
                {
                    _err += ex.Message + " " + ex.StackTrace;
                    ex = ex.InnerException;
                }
                CommonUtils.RemoveService(url_mod);
                Assert.Fail(_err);
            }

            Thread.Sleep(10000);
            string err = "";

            if (MessageProcessor.GetPolicyCacheSize() == 0)
                err += "policy cache is empty, it should have at least one item in it";
            if (!String.IsNullOrEmpty(MessageProcessor.LastErrorMessage))
                err += "agent error " + MessageProcessor.LastErrorMessage;

            TransactionalWebServicePolicy tp = MessageProcessor.GetPolicyIfAvailable(url_mod, true);
            if (tp == null)
                err += "the policy for the service was not cached, which means something went wrong";
            err += CommonUtils.VerifyLastMessagePayloads(url_mod, recordrequest, recordresponse, 1);
          //  err += CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(url_mod);
            CommonUtils.RemoveService(url_mod);
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);
        }
        #endregion

        

        public static IFGSMSNetService getproxy(string _url)
        {
            BasicHttpBinding b = null;

            b = new BasicHttpBinding(BasicHttpSecurityMode.None);

            b.MaxBufferSize = Int32.MaxValue;
            b.MaxReceivedMessageSize = Int32.MaxValue;
            b.MaxBufferPoolSize = Int32.MaxValue;
            b.TransferMode = TransferMode.Buffered;
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

            ChannelFactory<IFGSMSNetService> factory =
                new ChannelFactory<IFGSMSNetService>(b, _url);


            b.Security.Transport.ClientCredentialType = HttpClientCredentialType.Basic;

            IFGSMSNetService polservice = factory.CreateChannel();



            return polservice;
        }

        public static string url = "http://" + Environment.MachineName.ToLower() + ":12345/JUnitTestService";
        public static string url_mod = "http://" + Environment.MachineName.ToLower() + ":12345/JUnitTestService";
        public static IFGSMSNetService getproxyWithMonitor(string _url_internal)
        {
            BasicHttpBinding b = null;

            b = new BasicHttpBinding(BasicHttpSecurityMode.None);

            b.MaxBufferSize = Int32.MaxValue;
            b.MaxReceivedMessageSize = Int32.MaxValue;
            b.MaxBufferPoolSize = Int32.MaxValue;
            b.TransferMode = TransferMode.Buffered;
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
            ChannelFactory<IFGSMSNetService> factory =
                new ChannelFactory<IFGSMSNetService>(b, _url_internal);
            b.Security.Transport.ClientCredentialType = HttpClientCredentialType.Basic;
            factory.Endpoint.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFClientEndpointBehavior));
            factory.Endpoint.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFClientEndpointBehavior());
            IFGSMSNetService polservice = factory.CreateChannel();
            return polservice;
        }



        #region TC00139 Monitor from both the client side to a self hosted wcf service
        [Test]
        public void TC00139_1_MonitorSelftHostedClientRecordRequestAndResponse()
        {
            TC00139_doWork(true, true);
        }

        [Test]
        public void TC00139_2_MonitorSelftHostedClientRecordNotRequestAndResponse()
        {
            TC00139_doWork(false, false);
        }


        [Test]
        public void TC00139_3_MonitorSelftHostedClientRecordRequestAndNotResponse()
        {
            TC00139_doWork(true, false);
        }

        [Test]
        public void TC00139_4_MonitorSelftHostedClientNotRecordRequestAndRecordResponse()
        {
            TC00139_doWork(false, true);
        }


        /// <summary>
        /// monitored self hosted service and client handler added programmatically
        /// </summary>
        /// <param name="recordrequest"></param>
        /// <param name="recordresponse"></param>
        void TC00139_doWork(bool recordrequest, bool recordresponse)
        {
            Thread.Sleep(10000);
            CommonUtils.RemoveService(url_mod);
            CommonUtils.SetPolicy(url_mod, recordrequest, recordresponse);

            try
            {
                MessageProcessor mp = MessageProcessor.Instance;
                MessageProcessor.PurgePolicyCache();
                MessageProcessor.PurgeOutboundQueue();
                Assert.IsTrue(MessageProcessor.GetPolicyCacheSize() == 0);
                Assert.IsTrue(MessageProcessor.GetQueueSize() == 0);
                ServiceHost host = new ServiceHost(typeof(FGSMSNetServiceImpl));
                host.Description.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior));
                host.Description.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior());
                host.Open();
                IFGSMSNetService svc = getproxyWithMonitor(url_mod);
                SomeComplexRequestObject req = new SomeComplexRequestObject();
                req.stdout = "hi";

                svc.getData(req);

                Thread.Sleep(5000);
                DateTime timeout = DateTime.Now.AddMinutes(2);
                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (MessageProcessor.GetQueueSize() > 0 && DateTime.Now < timeout)
                    Thread.Sleep(1000);
                ((IClientChannel)svc).Close();
                ((IClientChannel)svc).Dispose();
                host.Close();
            }
            catch (Exception ex)
            {
                string _err = "";
                while (ex != null)
                {
                    _err += ex.Message + " " + ex.StackTrace;
                    ex = ex.InnerException;
                }
                CommonUtils.RemoveService(url_mod);
                Assert.Fail(_err);
            }

            Thread.Sleep(10000);
            string err = "";

            if (MessageProcessor.GetPolicyCacheSize() == 0)
                err += "policy cache is empty, it should have at least one item in it";
            if (!String.IsNullOrEmpty(MessageProcessor.LastErrorMessage))
                err += "agent error " + MessageProcessor.LastErrorMessage;

            TransactionalWebServicePolicy tp = MessageProcessor.GetPolicyIfAvailable(url_mod, true);
            if (tp == null)
                err += "the policy for the service was not cached, which means something went wrong";
            err += CommonUtils.VerifyLastMessagePayloads(url_mod, recordrequest, recordresponse, 2);
              err += CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(url_mod);
            CommonUtils.RemoveService(url_mod);
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);
        }
        #endregion

        


    }


    public static class constants
    {
        public const string ns = "urn:mil:army:cerdec:cpi:build";
    }

    [ServiceContract(Namespace = constants.ns, ProtectionLevel = ProtectionLevel.None)]
    public interface IFGSMSNetService
    {
        [OperationContract]
        SomeComplexReturnObject getData(SomeComplexRequestObject request);

        [OperationContract(Action="MyCustomAction")]
        string getDataSimple(int request);

        [OperationContract]
        void oneway(int request);


        [OperationContract]
        SomeComplexReturnObject getDataViaChain(SomeComplexRequestObject request);

    }
    public class FGSMSNetServiceImpl2 : IFGSMSNetService
    {

        public SomeComplexReturnObject getData(SomeComplexRequestObject request)
        {
            SomeComplexReturnObject r = new SomeComplexReturnObject();
            r.duration = request.duration;
            r.responsecode = request.responsecode;
            r.stderr = "hi via NOT chaining " + request.stderr;
            r.stdout = request.stdout;
            return r;
        }

        public SomeComplexReturnObject getDataViaChain(SomeComplexRequestObject request)
        {
            throw new NotImplementedException();
        }


        public string getDataSimple(int request)
        {
            throw new NotImplementedException();
        }

        public void oneway(int request)
        {
            throw new NotImplementedException();
        }
    }

    public class FGSMSNetServiceImpl3 : IFGSMSNetService
    {

        public SomeComplexReturnObject getData(SomeComplexRequestObject request)
        {
            SomeComplexReturnObject r = new SomeComplexReturnObject();
            r.duration = request.duration;
            r.responsecode = request.responsecode;
            r.stderr = "hi via NOT chaining " + request.stderr;
            r.stdout = request.stdout;
            return r;
        }

        public SomeComplexReturnObject getDataViaChain(SomeComplexRequestObject request)
        {
            throw new NotImplementedException();
        }


        public string getDataSimple(int request)
        {
            throw new NotImplementedException();
        }

        public void oneway(int request)
        {
            throw new NotImplementedException();
        }
    }
    public class FGSMSNetServiceImpl : IFGSMSNetService
    {
        public SomeComplexReturnObject getData(SomeComplexRequestObject request)
        {
            SomeComplexReturnObject r = new SomeComplexReturnObject();
            r.duration = request.duration;
            r.responsecode = request.responsecode;
            r.stderr = "hi via NOT chaining " + request.stderr;
            r.stdout = request.stdout;
            return r;
        }
        public SomeComplexReturnObject getDataViaChain(SomeComplexRequestObject request)
        {
           IFGSMSNetService anotherendpoint= WCFtTests.getproxyWithMonitor(TC00134.urlSecondHop);
           SomeComplexReturnObject r = anotherendpoint.getData(request);
           ((IClientChannel)anotherendpoint).Close();
           ((IClientChannel)anotherendpoint).Dispose();
           r.stderr += " hi via chaining!!";
            return r;
        }


        public string getDataSimple(int request)
        {
            return "hi" + request.ToString();
        }

        public void oneway(int request)
        {
            
        }
    }

  
}
