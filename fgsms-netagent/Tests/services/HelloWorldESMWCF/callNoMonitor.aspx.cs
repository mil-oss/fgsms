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
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.ServiceModel;
using System.Text;
using System.Web.Configuration;
using org.miloss.fgsms.agent.wcf;

namespace HelloWorldESMTester
{
    public partial class call : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {

        }

        public IService1 getproxyWithMonitor()
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
            
            b.MessageEncoding = WSMessageEncoding.Text;
            b.CloseTimeout = new TimeSpan(0, 0, 5);
            b.Security.Transport.ClientCredentialType = HttpClientCredentialType.Basic;
            ChannelFactory<IService1> factory =
                new ChannelFactory<IService1>(b, WebConfigurationManager.AppSettings["executionurl"]);
            b.Security.Transport.ClientCredentialType = HttpClientCredentialType.Basic;
            factory.Endpoint.Behaviors.Add(new AgentWCFClientEndpointBehavior());
            IService1 polservice = factory.CreateChannel();
            return polservice;
        }

    }
}