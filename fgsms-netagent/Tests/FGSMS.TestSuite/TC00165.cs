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
using org.miloss.fgsms.agent;
using System.Threading;

namespace FGSMS.NETTestSuite
{
    [TestFixture]
    public class TC00165
    {
        string urlFirstHop = "http://" + Environment.MachineName.ToLower() + ":12347/TC00165/svcBasic";
        [Test]
        public void TC00165_1_MultipleServices_SameBaseAddress()
        {
            CommonUtils.RemoveService(urlFirstHop);
            CommonUtils.SetPolicy(urlFirstHop, false, false);

            try
            {
                MessageProcessor mp = MessageProcessor.Instance;
                MessageProcessor.PurgePolicyCache();
                MessageProcessor.PurgeOutboundQueue();
                Assert.IsTrue(MessageProcessor.GetPolicyCacheSize() == 0);
                Assert.IsTrue(MessageProcessor.GetQueueSize() == 0);
                ServiceHost host = new ServiceHost(typeof(FGSMSNetServiceImpl3));
                host.Description.Behaviors.Remove(typeof(org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior));
                host.Description.Behaviors.Add(new org.miloss.fgsms.agent.wcf.AgentWCFServiceBehavior());
                host.Open();


                IFGSMSNetService svc = WCFtTests.getproxyWithMonitor(urlFirstHop);
                SomeComplexRequestObject req = new SomeComplexRequestObject();
                req.stdout = "hi";

                SomeComplexReturnObject ret = svc.getData(req);

                Thread.Sleep(10000);
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
                CommonUtils.RemoveService(urlFirstHop);
                Assert.Fail(_err);
            }

            Thread.Sleep(10000);
            string err = "";

            if (MessageProcessor.GetPolicyCacheSize() == 0)
                err += "policy cache is empty, it should have at least one item in it";
            if (!String.IsNullOrEmpty(MessageProcessor.LastErrorMessage))
                err += "agent error " + MessageProcessor.LastErrorMessage;

            TransactionalWebServicePolicy tp = MessageProcessor.GetPolicyIfAvailable(urlFirstHop, true);
            if (tp == null)
                err += "the policy for the service was not cached, which means something went wrong" + urlFirstHop;
            
           
            CommonUtils.RemoveService(urlFirstHop);
            
            if (!String.IsNullOrEmpty(err))
                Assert.Fail(err);
        }
    }
}
