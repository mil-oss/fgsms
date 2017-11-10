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
using System.Linq;
using NUnit.Framework;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.ServiceModel.Web;
using org.miloss.fgsms.agent;
using System.Threading;
using System.Net;

namespace FGSMS.NETTestSuite
{
    [TestFixture]
    public class TC00156
    {
        string url_mod = "http://" + Environment.MachineName.ToLower() + ":8000/";

        [Test]
        public void TC00156_WCF_REST()
        {
            dowork_156(false, false);
        }
        [Test]
        public void TC00156_WCF_REST_res()
        {
            dowork_156(false, true);
        }

        [Test]
        public void TC00156_WCF_REST_req_res()
        {
            dowork_156(true, true);
        }
        public void TC00156_WCF_REST_req()
        {
            dowork_156(true, false);
        }


        public void dowork_156(bool recordrequest, bool recordresponse)
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
                WebServiceHost host = new WebServiceHost(typeof(Service), new Uri(url_mod));
                host.Description.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior));
                host.Description.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior());
                ServiceEndpoint ep = host.AddServiceEndpoint(typeof(IService), new WebHttpBinding(), "");
                host.Open();
                //do work
                WebClient c = new WebClient();
                c.DownloadString(url_mod+ "EchoWithGet?s=hello");
                host.Close();
                Thread.Sleep(5000);
                DateTime timeout = DateTime.Now.AddMinutes(2);
                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (MessageProcessor.GetQueueSize() > 0 && DateTime.Now < timeout)
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
            //err += CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(url_mod);
            CommonUtils.RemoveService(url_mod);
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);


        }












        [Test]
        public void TC00157_WCF_REST_JSON()
        {
            dowork_157(false, false);
        }
        [Test]
        public void TC00157_WCF_REST_res_JSON()
        {
            dowork_157(false, true);
        }

        [Test]
        public void TC00157_WCF_REST_req_res_JSON()
        {
            dowork_157(true, true);
        }
        public void TC00157_WCF_REST_req_JSON()
        {
            dowork_157(true, false);
        }


        public void dowork_157(bool recordrequest, bool recordresponse)
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
                WebServiceHost host = new WebServiceHost(typeof(Service), new Uri(url_mod));
                host.Description.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior));
                host.Description.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior());
                ServiceEndpoint ep = host.AddServiceEndpoint(typeof(IService), new WebHttpBinding(), "");
                host.Open();
                //do work
                WebClient c = new WebClient();
                c.DownloadString(url_mod + "EchoWithGeJson?s=hello");
                host.Close();
                Thread.Sleep(5000);
                DateTime timeout = DateTime.Now.AddMinutes(2);
                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (MessageProcessor.GetQueueSize() > 0 && DateTime.Now < timeout)
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
            //err += CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(url_mod);
         //   CommonUtils.RemoveService(url_mod);
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);


        }








        [Test]
        public void TC00158_WCF_REST_JSON()
        {
            dowork_158(false, false);
        }
        [Test]
        public void TC00158_WCF_REST_res_JSON()
        {
            dowork_158(false, true);
        }

        [Test]
        public void TC00158_WCF_REST_req_res_JSON()
        {
            dowork_158(true, true);
        }
        public void TC00158_WCF_REST_req_JSON()
        {
            dowork_158(true, false);
        }


        public void dowork_158(bool recordrequest, bool recordresponse)
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
                WebServiceHost host = new WebServiceHost(typeof(Service), new Uri(url_mod));
                host.Description.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior));
                host.Description.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior());
                ServiceEndpoint ep = host.AddServiceEndpoint(typeof(IService), new WebHttpBinding(), "");
                host.Open();
                //do work
                WebClient c = new WebClient();
                c.DownloadString(url_mod + "EchoWithGeJson?s=hello");
                host.Close();
                Thread.Sleep(5000);
                DateTime timeout = DateTime.Now.AddMinutes(2);
                Console.Out.WriteLine("message sent queue size" + MessageProcessor.GetQueueSize());
                while (MessageProcessor.GetQueueSize() > 0 && DateTime.Now < timeout)
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
            //err += CommonUtils.VerifyLastMessagePayloadsTwoAgentsOneTransaction(url_mod);
            CommonUtils.RemoveService(url_mod);
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);


        }

    }

    [ServiceContract]
    public interface IService
    {
        [OperationContract]
        [WebGet]
        string EchoWithGet(string s);

        [OperationContract]
        [WebGet(ResponseFormat=WebMessageFormat.Json)]
        string EchoWithGeJson(string s);

        [OperationContract]
        [WebGet(ResponseFormat = WebMessageFormat.Xml)]
        string EchoWithGetXml(string s);

        [OperationContract]
        [WebInvoke]
        string EchoWithPost(string s);
    }

    public class Service : IService
    {
        public string EchoWithGet(string s)
        {
            return "You said " + s;
        }

        public string EchoWithPost(string s)
        {
            return "You said " + s;
        }


        public string EchoWithGeJson(string s)
        {
            return "You said " + s;
        }

        public string EchoWithGetXml(string s)
        {
            return "You said " + s;
        }
    }

}
