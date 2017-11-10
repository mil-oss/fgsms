/**e
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
using System.Configuration;

namespace FGSMS.NETTestSuite
{
    [TestFixture]
    public class ASPNetAgentTest
    {
        [Test]
        public void TC00135_SanityCheck()
        {

        }

        #region TC00136 monitored ASP.NET thick client (this) to monitored ASP.NET web service in IIS
        [Test]
        public void TC00136_ASPNETMonitoredThickClienttoASPNETserviceMonitoredReqRes()
        {
            TC00136_dowork(true, true);
        }

        [Test]
        public void TC00136_ASPNETMonitoredThickClienttoASPNETserviceMonitoredRes()
        {
            TC00136_dowork(false, true);
        }
        [Test]
        public void TC00136_ASPNETMonitoredThickClienttoASPNETserviceMonitored()
        {
            TC00136_dowork(false, false);
        }

        [Test]
        public void TC00136_ASPNETMonitoredThickClienttoASPNETserviceMonitoredReq()
        {
            TC00136_dowork(true, false);
        }




        void TC00136_dowork(bool request, bool response)
        {
            //Configuration c = CommonUtils.GetMappedConfig();
            Thread.Sleep(10000);
            string urlmod = MessageProcessor.ModifyURL(ConfigurationManager.AppSettings["HelloWorldASPNET2"], false);
                //c.AppSettings.Settings["HelloWorldASPNET2"].Value, true);
            try
            {
                CommonUtils.BounceIIS();
                CommonUtils.RemoveService(urlmod);
                Thread.Sleep(5000);
                CommonUtils.SetPolicy(urlmod, request, response);
                HelloWorldASPNET2.Service2 svc = new HelloWorldASPNET2.Service2();
                MessageProcessor mp = MessageProcessor.Instance;
                MessageProcessor.PurgeOutboundQueue();
                MessageProcessor.PurgePolicyCache();

                svc.Url = ConfigurationManager.AppSettings["HelloWorldASPNET2"];
                    //c.AppSettings.Settings["HelloWorldASPNET2"].Value;
                string s = svc.HelloWorld("test");
                DateTime timeout = DateTime.Now.AddMinutes(2);

                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (MessageProcessor.GetQueueSize() > 0 && DateTime.Now < timeout)
                    Thread.Sleep(1000);
                Thread.Sleep(15000);
                svc.Dispose();
            }
            catch (Exception ex)
            {
                string _err = "";
                while (ex != null)
                {
                    _err += ex.Message + " " + ex.StackTrace;
                    ex = ex.InnerException;
                }
                CommonUtils.RemoveService(urlmod);
                Assert.Fail(_err);
            }
            TransactionalWebServicePolicy tp = MessageProcessor.GetPolicyIfAvailable(urlmod, true);
            Assert.IsNotNull(tp);
            Assert.IsTrue(String.IsNullOrEmpty(MessageProcessor.LastErrorMessage));
            string s2 = CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(urlmod);
            CommonUtils.RemoveService(urlmod);
            if (!String.IsNullOrEmpty(s2))
                Assert.Fail(s2);
        }
        #endregion

        //TC00137 is in TestSuite2 due to context issues with ASP.NET agents

        #region TC00138 asp.net thick to  IIS hosted wcf service

        [Test]
        public void TC00138_1_ASPNETThickClienttoIISWCFserviceMonitored()
        {
            TC00138_dowork(false, false);
        }

        [Test]
        public void TC00138_2_ASPNETThickClienttoIISWCFserviceMonitoredReq()
        {
            TC00138_dowork(true, false);
        }


        [Test]
        public void TC00138_3_ASPNETThickClienttoIISWCFserviceMonitoredReqRes()
        {
            TC00138_dowork(true, true);
        }

        [Test]
        public void TC00138_4_ASPNETThickClienttoIISWCFserviceMonitoredRes()
        {
            TC00138_dowork(false, true);
        }

        void TC00138_dowork(bool request, bool response)
        {
            Thread.Sleep(10000);
            MessageProcessor mp = MessageProcessor.Instance;
            //Configuration config = CommonUtils.GetMappedConfig();
            string urlmod = MessageProcessor.ModifyURL(ConfigurationManager.AppSettings["HelloWorldESMWCFviaASPNET"],
            //   config.AppSettings.Settings["HelloWorldESMWCFviaASPNET"].Value, 
            true);
            try
            {
                //CommonUtils.BounceIIS();
                CommonUtils.RemoveService(urlmod);
                Thread.Sleep(5000);
                CommonUtils.SetPolicy(urlmod, request, response);
                HelloWorldESMWCFviaASPNET.Service1 svc = new HelloWorldESMWCFviaASPNET.Service1();

              
                MessageProcessor.PurgeOutboundQueue();
                MessageProcessor.PurgePolicyCache();
                callback c = new callback();
                MessageProcessor.RegisterCallbacks(c);

                svc.Url = ConfigurationManager.AppSettings["HelloWorldESMWCFviaASPNET"];
                //config.AppSettings.Settings["HelloWorldESMWCFviaASPNET"].Value;
                string s = svc.WorkingGetData(5, true);
                DateTime timeout = DateTime.Now.AddMinutes(2);

                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (DateTime.Now < timeout && !c.done)

                    Thread.Sleep(1000);
                Thread.Sleep(4000);
                svc.Dispose();
                
                MessageProcessor.UnRegisterCallbacks(c);
            }
            catch (Exception ex)
            {
                string _err = "";
                while (ex != null)
                {
                    _err += ex.Message + " " + ex.StackTrace;
                    ex = ex.InnerException;
                }
                CommonUtils.RemoveService(urlmod);
                Assert.Fail(_err);
            }
            TransactionalWebServicePolicy tp = MessageProcessor.GetPolicyIfAvailable(urlmod, true);
            Assert.IsNotNull(tp);
            Assert.IsTrue(String.IsNullOrEmpty(MessageProcessor.LastErrorMessage));
            string s2 = CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(urlmod);
            CommonUtils.RemoveService(urlmod);
            if (!String.IsNullOrEmpty(s2))
                Assert.Fail(s2);
        }
        #endregion


        #region TC00140 monitored ASP.NET thick to monitored self hosted WCF

        [Test]
        public void TC00140_1_ASPNETThickClienttoWCFserviceMonitored()
        {
            TC00140_dowork(false, false);
        }

        [Test]
        public void TC00140_2_ASPNETThickClienttoWCFserviceMonitoredReq()
        {
            TC00140_dowork(true, false);
        }


        [Test]
        public void TC00140_3_ASPNETThickClienttoWCFserviceMonitoredReqRes()
        {
            TC00140_dowork(true, true);
        }

        [Test]
        public void TC00140_4_ASPNETThickClienttoWCFserviceMonitoredRes()
        {
            TC00140_dowork(false, true);
        }

        void TC00140_dowork(bool request, bool response)
        {
            Thread.Sleep(10000);
            MessageProcessor mp = MessageProcessor.Instance;
            string urlmod = WCFtTests.url;
            try
            {
                CommonUtils.RemoveService(urlmod);
                Thread.Sleep(5000);
                CommonUtils.SetPolicy(urlmod, request, response);
                aspnet.client.towcf.FGSMSNetServiceImpl svc = new aspnet.client.towcf.FGSMSNetServiceImpl();
                svc.Url = urlmod;


                MessageProcessor.PurgeOutboundQueue();
                MessageProcessor.PurgePolicyCache();

                ServiceHost host = new ServiceHost(typeof(FGSMSNetServiceImpl));
                host.Description.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior));
                host.Description.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior());
                host.Open();
              

                svc.Url = urlmod;
                aspnet.client.towcf.SomeComplexRequestObject x = new aspnet.client.towcf.SomeComplexRequestObject();
                x.stderr = "hi";
                x.stdout = "hi";
                svc.getData(x);
                DateTime timeout = DateTime.Now.AddMinutes(2);

                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (DateTime.Now < timeout && MessageProcessor.GetQueueSize() != 0)
                    Thread.Sleep(1000);
                host.Close();
                svc.Dispose();
                Thread.Sleep(1000);
            }
            catch (Exception ex)
            {
                string _err = "";
                while (ex != null)
                {
                    _err += ex.Message + " " + ex.StackTrace;
                    ex = ex.InnerException;
                }
                CommonUtils.RemoveService(urlmod);
                Assert.Fail(_err);
            }
            TransactionalWebServicePolicy tp = MessageProcessor.GetPolicyIfAvailable(urlmod, true);
            Assert.IsNotNull(tp);
            Assert.IsTrue(String.IsNullOrEmpty(MessageProcessor.LastErrorMessage));
            string s2 = CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(urlmod);
            CommonUtils.RemoveService(urlmod);
            if (!String.IsNullOrEmpty(s2))
                Assert.Fail(s2);
        }
        #endregion
    }

    public class callback : IFGSMS_Callbacks
    {
        public bool done=false;
        public void NotifyEmptyQueue()
        {
            
        }

        public void NotifyEmptyQueueAllMessagesHaveBeenSent()
        {
            done = true;
        }
    }
}
