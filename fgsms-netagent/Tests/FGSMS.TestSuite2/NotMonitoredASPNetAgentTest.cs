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

using System.Configuration;
namespace FGSMS.NETTestSuite
{
    /// <summary>
    /// this covers ASP.NET Clients that are NOT monitored by an agent
    /// </summary>
    [TestFixture]
    public class ASPNetAgentTest
    {
        void TC00137_dowork(bool request, bool response)
        {
             string urlmod = MessageProcessor.ModifyURL(ConfigurationManager.AppSettings["HelloWorldASPNET2"], true);

            try
            {
                CommonUtils.RemoveService(urlmod);
                CommonUtils.SetPolicy(urlmod, request, response);
                HelloWorldASPNET2.Service2 svc = new HelloWorldASPNET2.Service2();
                MessageProcessor mp = MessageProcessor.Instance;
                MessageProcessor.PurgeOutboundQueue();
                MessageProcessor.PurgePolicyCache();

                svc.Url = ConfigurationManager.AppSettings["HelloWorldASPNET2"];
                string s = svc.HelloWorld("test");
                //just confirm that we are NOT monitored for this context
                Assert.IsTrue(MessageProcessor.GetPolicyCacheSize() == 0);
                Assert.IsTrue(MessageProcessor.GetQueueSize() == 0);
                Thread.Sleep(10000); //give the remote agent a few seconds to record and send back the data
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
            Thread.Sleep(15000); //give the remote agent a few seconds to record and send back the data
            string s2=CommonUtils.VerifyLastMessagePayloads(urlmod, request, response, 1);
            CommonUtils.RemoveService(urlmod);
            if (!String.IsNullOrEmpty(s2))
                Assert.Fail(s2);
        }

        [Test]
        public void TC00137_1_ASPNETThickClienttoASPNETserviceMonitoredReqRes()
        {
            TC00137_dowork(true, true);
        }

        [Test]
        public void TC00137_ASPNETThickClienttoASPNETserviceMonitoredReq()
        {
            TC00137_dowork(true, false);
        }

        [Test]
        public void TC00137_ASPNETThickClienttoASPNETserviceMonitoredRes()
        {
            TC00137_dowork(false, true);
        }


        [Test]
        public void TC00137_ASPNETThickClienttoASPNETserviceMonitored()
        {
            TC00137_dowork(false, false);
        }

        
    }
}
