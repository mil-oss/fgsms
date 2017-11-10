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
using System.Diagnostics;
using System.IO;
using System.Text;
using System.Web;
using System.Web.Services.Protocols;
using System.Net;
using System.Threading;
using System.Security.Cryptography.X509Certificates;
using System.Collections;
using System.Collections.Specialized;
using System.Xml;


namespace org.miloss.fgsms.agent
{

    /// <summary>
    /// AgentSoapExtension
    /// 
    /// Provides monitoring support for legacy ASP.NET web services (System.Web.Services and WSE2/3)
    /// this can be applied globally to a system by changing the following file
    /// C:/Windows/Microsoft.NET/Framework{64}/v{version}/CONFIG/machine.config
    /// <system.web>
    ///     <webServices>
    ///      <soapExtensionTypes>
    ///        <add type="type, assembly" priority="1" group="HIGH" />
    ///      </soapExtensionTypes>
    ///     </webServices>
    /// </system.web>
    /// </summary>
    public class AgentSoapExtension : System.Web.Services.Protocols.SoapExtension
    {
        private TraceSource log;
        private const string name = "org.miloss.fgsms.agents.AgentSoapExtension";
        //private Stack<Uri> depends;
        public AgentSoapExtension()
        {
            //depends = new Stack<Uri>();
            log = new TraceSource(name);
        } // end AgentWSE2Input

        static AgentMessageTable ctx2 = AgentMessageTable.Instance;
        static MessageProcessor ctx = MessageProcessor.Instance;

        /* Stream oldStream;
         Stream newStream;
         public override Stream ChainStream(Stream stream)
         {
             oldStream = stream;
             newStream = new MemoryStream();
             return newStream;
         }*/
        public override object GetInitializer(Type serviceType)
        {
            return null;
        }

        public override object GetInitializer(System.Web.Services.Protocols.LogicalMethodInfo methodInfo,
            System.Web.Services.Protocols.SoapExtensionAttribute attribute)
        {
            return null;
        }

        public override void Initialize(object initializer)
        {
            return;
        }

        public override void ProcessMessage(System.Web.Services.Protocols.SoapMessage message)
        {

            AgentMessageTable ab = AgentMessageTable.Instance;
            //  if (AgentMessageTable.GetSize() >= 100)
            //      ;
            AgentMessageTable a = AgentMessageTable.Instance;
            System.Console.WriteLine("Tread " + Thread.CurrentThread.ManagedThreadId + " Message Table:" + AgentMessageTable.GetSize());
            try
            {
                if (HttpContext.Current == null)
                {

                }
                else
                {
                    //depends.Push(HttpContext.Current.Request.Url);
                }
                if (message == null)
                {
                    throw new ArgumentNullException("message");
                }


                if (message.GetType() == typeof(SoapClientMessage))
                {
                    switch (message.Stage)
                    {
                        case SoapMessageStage.BeforeSerialize:
                            //outbound message client
                            this.ProcessOutboundRequest(message);
                            //this.InsertHeadersOutbound(message, true);
                            return;

                        case SoapMessageStage.AfterSerialize:
                            //outbound message client
                            
                            return;

                        case SoapMessageStage.BeforeDeserialize:
                            this.GrabClientResponseMessageBeforeDeserialization(message);
                            //inbound message client
                            //4-16-2012 moved call to after deserialization to obtain soap headers for dependency detection
                            return;

                        case SoapMessageStage.AfterDeserialize:
                            this.ProcessInboundResponse(message);
                            return;
                    }
                }
                else if (message.GetType() == typeof(SoapServerMessage))
                {
                    switch (message.Stage)
                    {
                        case SoapMessageStage.BeforeSerialize:
                            //outbound server
                            this.InsertHeadersOutbound(message, false);
                            return;

                        case SoapMessageStage.AfterSerialize:
                            //outbound server
                            this.ProcessOutboundResponse(message);
                            return;

                        case SoapMessageStage.BeforeDeserialize:
                            //this.ProcessInboundRequest(message);  //this yields no access to soap headers
                            return;

                        case SoapMessageStage.AfterDeserialize:
                            this.ProcessInboundRequest(message); 
                            //just moved this
                            return;
                    }

                }
                else
                {
                    try
                    {
                        
                       Logger.warn(this.GetType().FullName + "unknown message type was not handled: " + message.GetType().FullName);
                    }
                    catch { }
                }

            }
            catch (Exception ex)
            {
                Logger.error(ex, this.GetType().FullName +  "error caught when processing a message" );
             /*   String s = "";
                while (ex != null)
                {
                    s += ex.Message + System.Environment.NewLine + ex.StackTrace + System.Environment.NewLine + ex.InnerException + System.Environment.NewLine + System.Environment.NewLine;
                    ex = ex.InnerException;
                }
                try
                {
                    if (FGSMSConstants.log) EventLog.WriteEntry(this.GetType().FullName, "error caught " + message.GetType().FullName + s, EventLogEntryType.Error);
                }
                catch { }*/
            }
        }

        private void InsertHeadersOutbound(SoapMessage message, bool isclient)
        {
            MessageProcessor mp = MessageProcessor.Instance;
            if (MessageProcessor.GetConfig.DependencyInjectionEnabled)
            {
                string thread = "";
                string thismessageid = "";
                if (localhashcode != -1)
                {
                    MessageCorrelator mc = AgentMessageTable.GetRequestbyHash(localhashcode);
                    if (mc != null)
                    {
                        mc.threadid = MessageProcessor.GetTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name);
                        if (String.IsNullOrEmpty(mc.threadid))
                        {
                            mc.threadid = Guid.NewGuid().ToString();
                        }
                        thread = mc.threadid;
                        thismessageid = mc.MessageID;
                        message.Headers.Add(new FGSMSSoapHeaderRelatedMessageASPNET(thismessageid));
                        message.Headers.Add(new FGSMSSoapHeaderTransactionThreadIdASPNET(thread));
                    }
                }
            }
        }

        

        private void ProcessInboundResponse(SoapMessage message)
        {
            ProcessResponse(message, true);
        }

        private void ProcessOutboundRequest(SoapMessage message)
        {
            ProcessRequest(message, true);
        }

        private void ProcessInboundRequest(SoapMessage message)
        {
            ProcessRequest(message, false);
        }

        private void ProcessOutboundResponse(SoapMessage message)
        {
            ProcessResponse(message, false);
        }

        private string StreamtoString(Stream messageStream)
        {

            List<byte> msg = new List<byte>();
            Boolean good = false;
            try
            {
                if (messageStream.CanSeek)
                    messageStream.Seek(0, SeekOrigin.Begin);

                for (int i = 0; i < messageStream.Length; i++)
                {
                    msg.Add((byte)messageStream.ReadByte());
                }
                if (messageStream.CanSeek)
                    messageStream.Seek(0, SeekOrigin.Begin);
                //messageStream.Position = 0;
                good = true;
            }
            catch { }
            if (!good)
            {
                /*try
                {
                    int count = 0;
                    int b = (int)messageStream.ReadByte();
                    while (b != -1 && count < 1020000)
                    {
                        msg.Add((byte)b);
                        b = (int)messageStream.ReadByte();
                        count++;
                    }
                    good = true;
                }
                catch { }*/
            }
            /*try
            {
                messageStream.Seek(0, SeekOrigin.Begin);

            }
            catch (Exception ex)
            {

            }*/
            if (!good)
            {
                //try
                // {
                //    if (FGSMSConstants.log) EventLog.WriteEntry(this.GetType().FullName, "Unable to obtain message body, auditing will not be available.", EventLogEntryType.Information);
                // }
                // catch { }
                return "Request/Response messages are not available from ASP.NET Client running outside of the context of IIS";
            }
            byte[] msg2 = msg.ToArray();
            string s = Encoding.UTF8.GetString(msg2);
            return s;
        }
        protected int localhashcode = -1;


        private void ProcessRequest(SoapMessage message, Boolean isclient)
        {
            if (Trace.CorrelationManager.ActivityId == Guid.Empty)
            {
                Guid newGuid = Guid.NewGuid();
                Trace.CorrelationManager.ActivityId = newGuid;
            }
            MessageProcessor ctx = MessageProcessor.Instance;
            MessageCorrelator mc = new MessageCorrelator();

            mc.MessageID = Guid.NewGuid().ToString();
            if (isclient)
            {
                mc.threadid = MessageProcessor.GetTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name);
                if (String.IsNullOrEmpty(mc.threadid))
                {
                    mc.threadid = Guid.NewGuid().ToString();
                }
                if (MessageProcessor.GetConfig.DependencyInjectionEnabled)
                {
                    message.Headers.Add(new FGSMSSoapHeaderRelatedMessageASPNET(mc.MessageID));
                    message.Headers.Add(new FGSMSSoapHeaderTransactionThreadIdASPNET(mc.threadid));
                }
            }
            else //service processing a request
            {
                IEnumerator it = message.Headers.GetEnumerator();
                while (it.MoveNext())
                {
                    SoapUnknownHeader e = it.Current as SoapUnknownHeader;
                    if (e != null)
                    {

                        //if (e.Element.Name.Equals(FGSMSSoapHeaderTransactionThreadIdWCF.Name2) && e.Element.NamespaceURI.Equals(FGSMSSoapHeaderTransactionThreadIdWCF.Namespace2))
                        //{
                        //    mc.threadid = e.Element.InnerText;
                        //}
                        if (e.Element.Name.Equals(FGSMSSoapHeaderRelatedMessageIdWCF.Name2) && e.Element.NamespaceURI.Equals(FGSMSSoapHeaderRelatedMessageIdWCF.Namespace2))
                        {
                            mc.relatedtransactionid = e.Element.InnerText;
                        }
                        if (e.Element.Name.Equals(FGSMSSoapHeaderTransactionThreadIdWCF.Name2) && e.Element.NamespaceURI.Equals(FGSMSSoapHeaderTransactionThreadIdWCF.Namespace2))
                        {
                            mc.threadid = e.Element.InnerText;
                        }
                    }

                  
                }

             
            }
            if (String.IsNullOrEmpty(mc.threadid))
                mc.threadid = Guid.NewGuid().ToString();
            MessageProcessor.SetTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name, mc.threadid);



            try
            {
                Uri url = new Uri("urn:undeterminable");
                if (!isclient && System.Web.HttpContext.Current != null)
                    url = System.Web.HttpContext.Current.Request.Url;
                else url = new Uri(message.Url);
                AgentMessageTable hashtable = AgentMessageTable.Instance;
                if (isclient)
                    localhashcode = message.GetHashCode();
                else if (HttpContext.Current != null)
                {
                    localhashcode = HttpContext.Current.Request.GetHashCode();
                }
                else
                {
                    //uh oh, http context is null and this is a server side request, unexpected.
                }
                if (HttpContext.Current == null)
                {
                    mc.RecievedAt = DateTime.Now;
                    mc.RequestMessage = StreamtoString(message.Stream);
                    mc.requestsize = mc.RequestMessage.Length;
                    mc.originalUrl = mc.URL = url.ToString();
                    mc.RequestHeaders = new System.Collections.Specialized.NameValueCollection();
                    //   mc.RequestHeaders.Add("Content-Type", message.ContentType);
                    //  mc.RequestHeaders.Add("Content-Encoding", message.ContentEncoding);
                    mc.RequestHeaders.Add("SOAPAction", message.Action);
                    mc.soapAction = message.Action;
                    AgentMessageTable.AddRequest(mc, localhashcode);
                    /* AgentMessageTable.AddRequest(localhashcode,
                         DateTime.Now,
                         StreamtoString(message.Stream),
                         //InputMessageToString(message),
                         url.ToString()
                         // message.ToString()
                     );*/
                }
                else
                {
                    mc.RecievedAt = DateTime.Now;

                    mc.RequestMessage = StreamtoString(message.Stream);
                    mc.requestsize = mc.RequestMessage.Length;
                    mc.originalUrl = mc.URL = url.ToString();
                    mc.RequestHeaders = new System.Collections.Specialized.NameValueCollection();
                    mc.RequestHeaders = HttpContext.Current.Request.Headers;
                    mc.userp = HttpContext.Current.User;
                    try
                    {
                        mc.soapAction = message.Action;
                    }
                    catch { }
                    if (String.IsNullOrEmpty(mc.soapAction))
                    {
                        mc.soapAction = mc.RequestHeaders["SOAPAction"];
                    }
                    if (HttpContext.Current.Request.ClientCertificate != null)
                        mc.ClientCertificate = new X509Certificate2(HttpContext.Current.Request.ClientCertificate.Certificate);

                    AgentMessageTable.AddRequest(mc, localhashcode);
                    /*
                    AgentMessageTable.AddRequest(localhashcode,
                   DateTime.Now,
                   StreamtoString(message.Stream),
                        //OutputMessageToString(message),
                   url.ToString());
                    // message.ToString()*/
                }

            }
            catch (Exception ex)
            {
                Logger.error(ex, this.GetType().FullName + " error caught processing a request ");
                
            }
            // PurgeOldMessages();
        }

        private void PurgeOldMessages()
        {
            AgentMessageTable x = AgentMessageTable.Instance;
            //Timeout threshold
            DateTime cutoff = DateTime.Now.Subtract(new TimeSpan(0, 0, 0, 0, (int)MessageProcessor.GetConfig.DeadMessageDuration));
            List<int> l = AgentMessageTable.GetOldMessages(cutoff);
            if (l.Count > 0)
                
                    Logger.warn("ASP.NET Agent, Purging " + l.Count + " stale records from the message table. These were most likely time outs and will be logged as faults.");
                    
                
            for (int i = 0; i < l.Count; i++)
            {
                MessageCorrelator mc = AgentMessageTable.RemoveMessage(localhashcode);
                mc.CompletedAt = DateTime.Now;
                if (String.IsNullOrEmpty(mc.soapAction))
                    mc.soapAction = "urn:undeterminable";
                mc.ResponseMessage = "Request Timeout";
                mc.IsFault = true;
                mc.agenttype = "org.miloss.fgsms.agents.AgentSoapExtension.DeadMessageQueue";
                MessageProcessor.ProcessMessage(mc);
                /*
                MessageProcessor.ProcessMessage((AgentMessageTable.GetRequestURL(l[i])),
                                AgentMessageTable.GetRequestHash(l[i]),
                                DateTime.Now,
                                "urn:undeterminable",
                                AgentMessageTable.GetRequestMessage(l[i]),
                                "Request Timeout",
                                localhashcode.ToString(), true,
                                HttpContext.Current,
                                "", "org.miloss.fgsms.agents.AgentSoapExtension.DeadMessageQueue",
                                ""
                                );*/


            }

        }

        private void GrabClientResponseMessageBeforeDeserialization(SoapMessage message)
        {
            AgentMessageTable ctx2 = AgentMessageTable.Instance;
            AgentMessageTable hashtable = AgentMessageTable.Instance;
            if (localhashcode == -1)
                return;
            MessageCorrelator mc = AgentMessageTable.GetRequestbyHash(localhashcode);
            if (mc != null)
            
            
            {
                try
                {
                    mc.ResponseMessage = StreamtoString(message.Stream);
                    if (mc.ResponseHeaders == null)
                        mc.ResponseHeaders = new NameValueCollection();
                    mc.ResponseHeaders.Add("Content-Encoding", message.ContentEncoding);
                    mc.ResponseHeaders.Add("Content-Type", message.ContentType);

                }
                catch (Exception ex)
                {
                    Logger.debug(ex);
                }

            }
        }

        private void ProcessResponse(SoapMessage message, Boolean isclient)
        {
            MessageProcessor ctx = MessageProcessor.Instance;
            //  if (!MessageProcessor.Enabled)
            //      return;
            if (Trace.CorrelationManager.ActivityId == Guid.Empty)
            {
                Guid newGuid = Guid.NewGuid();
                Trace.CorrelationManager.ActivityId = newGuid;
            }
           
            try
            {
                AgentMessageTable ctx2 = AgentMessageTable.Instance;
                AgentMessageTable hashtable = AgentMessageTable.Instance;
                if (localhashcode == -1)
                {
                    Logger.warn(this.GetType().FullName + " ProcessResponse unable to local hash reference coresponding request message");
                  
                    return;
                }

             



                if (isclient && HttpContext.Current == null)
                //if this is a standalone client processing a response, this is common
                {
                    MessageCorrelator mc = AgentMessageTable.RemoveMessage(localhashcode);
                    if (mc == null)
                    
                    {
                        Logger.warn(
                        this.GetType().FullName+" ProcessResponse unable to reference coresponding request message");
                        
                        return;
                    }
                    if (isclient && MessageProcessor.GetConfig.DependencyInjectionEnabled)
                    {
                        IEnumerator it= message.Headers.GetEnumerator();
                        while (it.MoveNext())
                        {
                            SoapUnknownHeader e = it.Current as SoapUnknownHeader;
                            if (e != null)
                            {

                                //if (e.Element.Name.Equals(FGSMSSoapHeaderTransactionThreadIdWCF.Name2) && e.Element.NamespaceURI.Equals(FGSMSSoapHeaderTransactionThreadIdWCF.Namespace2))
                                //{
                                //    mc.threadid = e.Element.InnerText;
                                //}
                                if (e.Element.Name.Equals(FGSMSSoapHeaderRelatedMessageIdWCF.Name2) && e.Element.NamespaceURI.Equals(FGSMSSoapHeaderRelatedMessageIdWCF.Namespace2))
                                {
                                    mc.relatedtransactionid = e.Element.InnerText;
                                }
                            }
                        }
                    }
                    if (String.IsNullOrEmpty(mc.threadid))
                    {
                        mc.threadid = MessageProcessor.GetTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name);
                        if (String.IsNullOrEmpty(mc.threadid))
                        {
                            mc.threadid = Guid.NewGuid().ToString();
                        }
                    }

                    if (!isclient && MessageProcessor.GetConfig.DependencyInjectionEnabled)
                    {
                        message.Headers.Add(new FGSMSSoapHeaderRelatedMessageASPNET(mc.MessageID));
                        message.Headers.Add(new FGSMSSoapHeaderTransactionThreadIdASPNET(mc.threadid));
                    }


                    try
                    {
                        if (String.IsNullOrEmpty(mc.soapAction))
                        {
                            string action = "urn:undeterminable";
                            if (action == "urn:undeterminable")
                            {
                                action = message.Action;
                                action = action.Replace("\"", "");
                                action = action.Replace("'", "");
                                if (String.IsNullOrEmpty(action))
                                    action = "urn:undeterminable";
                            }
                            if (action == "urn:undeterminable")
                            {
                                action = message.MethodInfo.Name; ;
                                action = action.Replace("\"", "");
                                action = action.Replace("'", "");
                                if (String.IsNullOrEmpty(action))
                                    action = "urn:undeterminable";
                            }
                        }
                        string ip = "";
                        try
                        {
                            string myHost = System.Net.Dns.GetHostName();
                            System.Net.IPAddress[] list = System.Net.Dns.GetHostEntry(myHost).AddressList;
                            for (int i = 0; i < list.Length; i++)
                            {
                                if (!IPAddress.IsLoopback(System.Net.Dns.GetHostEntry(myHost).AddressList[i]))
                                {
                                    ip = System.Net.Dns.GetHostEntry(myHost).AddressList[i].ToString();
                                    break;
                                }
                            }
                        }
                        catch { }

                        string user = System.Environment.UserName;
                        mc.identity = new List<string>();
                        mc.identity.Add(user);
                        // mc.identity.Add(ip);
                        mc.ipaddress = ip;
                        mc.CompletedAt = DateTime.Now;
                        mc.IsFault = AgentSoapExtension.IsFault(message);
                        mc.agenttype = this.GetType().FullName + ".client";
                        mc.memo = "MsgMap=" + AgentMessageTable.GetSize();
                        if (mc.ResponseHeaders == null)
                            mc.ResponseHeaders = new NameValueCollection();
                        if (HttpContext.Current != null && HttpContext.Current.Response != null && HttpContext.Current.Response.Headers != null)
                        {
                            mc.ResponseHeaders.Add(HttpContext.Current.Response.Headers);
                        }
                        else
                        
                        mc.ResponseHeaders.Add("SOAPAction", message.Action);
                        



                        if (message.OneWay)
                        {
                            mc.ResponseMessage = "";
                            mc.responsesize = 0;

                            MessageProcessor.ProcessMessage(mc);

                            /*
                            mc.ipaddress = ip;
                            MessageProcessor.ProcessMessage((
                                url),
                                start,
                                DateTime.Now,
                                action,
                                req,
                                "",
                                localhashcode.ToString(),
                                AgentSoapExtension.IsFault(message),
                                null,
                                ip, this.GetType().FullName + ".client",
                                user
                                );*/
                        }
                        else
                        {
                    
                            mc.responsesize = mc.ResponseMessage.Length;
                            MessageProcessor.ProcessMessage(mc);
                            /*
                            MessageProcessor.ProcessMessage((url),
                                start,
                                DateTime.Now,
                                action,
                                req,
                                //InputMessageToString(message),
                                StreamtoString(message.Stream),
                                localhashcode.ToString(),
                                AgentSoapExtension.IsFault(message),
                                null,
                                ip, this.GetType().FullName + ".client",
                                user
                                );*/
                        }


                    }
                    catch (Exception ex)
                    {
                        try
                        {
                            AgentMessageTable.RemoveMessage(localhashcode);
                        }
                        catch { }
                        Logger.error(ex,this.GetType().FullName+ " Error caught process response for clients");
                       

                    }
                }
                else
                {
                    //this is a response from a service, either directly from the service, or a service chaining event and the caller is processing the response

                    try
                    {
                        if (HttpContext.Current == null)
                        {Logger.warn(this.GetType().FullName+ " SniiferFilter failure, http context is null, this shouldn't happen please report if you see this message");
                        }
                        else
                            // HttpContext.Current.Response.Filter = new SniiferFilter(HttpContext.Current.Response.Filter);
                            HttpContext.Current.Response.Filter = new SniiferFilter(HttpContext.Current.Response.Filter);
                        // if (FGSMSConstants.log) EventLog.WriteEntry(this.GetType().FullName, "SniiferFilter success to " + System.Web.HttpContext.Current.Request.Url.ToString(), EventLogEntryType.Information);
                    }
                    catch { }
                }
                //this may be a good hook for dependency checking
                //HttpContext.Current.Items.Add(
                //need to make a container, add it to the context somehow
                //on each outbound message, grab the context container, add dependency if detected.

                //on the first inbound connection, setup the context
                //on an outbound connection, check if it's a request or response
                //if response, we are done, send dependency tree to the message processor
                //if request, get the context, add the dependency
                try
                {
                    //if (FGSMSConstants.log) EventLog.WriteEntry(this.GetType().FullName, "ProcessOutbound success", EventLogEntryType.Information);
                }
                catch { }

            }
            catch (Exception ex)
            {
                Logger.error(ex, "Process outbound");
            }
            if (!isclient)
                try
                {
                    MessageProcessor.ClearTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name);
                }
                catch { }
            else
            {
                try
                {
                    MessageProcessor.ClearTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name);
                }
                catch { }
            }
            //PurgeOldMessages();
        }

        /* private string InputMessageToString(SoapMessage message)
         {
             newStream.Position = 0;
             TextReader reader = new StreamReader(newStream);
             MemoryStream ms = new MemoryStream();
             StreamWriter sw = new StreamWriter(ms);
             Copy(newStream, ms);
             newStream.Position = 0;
             Copy(newStream, oldStream);

             byte[] bits = ms.ToArray();
             ms.Position=0;
             char[] l = new char[bits.Length];
             for (int i=0; i< bits.Length; i++)
             {
                 l[i] = new char();
                 l[i] = (char)bits[i];
             }
             string x = new string(l);
             return x;
         }

         private string OutputMessageToString(SoapMessage message)
         {
             Copy(oldStream, newStream);
             newStream.Position = 0;
             TextReader reader = new StreamReader(newStream);
             MemoryStream ms = new MemoryStream();
             StreamWriter sw = new StreamWriter(ms);
             //Copy(newStream, ms);
             newStream.Position = 0;
             Copy(newStream, ms);
             newStream.Position = 0;
             byte[] bits = ms.ToArray();
             ms.Position = 0;
             char[] l = new char[bits.Length];
             for (int i = 0; i < bits.Length; i++)
             {
                 l[i] = new char();
                 l[i] = (char)bits[i];
             }
             string x = new string(l);
             return x;
         }
         void Copy(Stream from, Stream to)
         {
             TextReader reader = new StreamReader(from);
             TextWriter writer = new StreamWriter(to);

             writer.Write(reader.ReadToEnd());
             writer.Flush();
         }*/

        private static bool IsFault(SoapMessage message)
        {
            if (message == null)
                return false;
            if (message.Exception != null)
                return true;
            if (HttpContext.Current != null && HttpContext.Current.Response != null && HttpContext.Current.Response.StatusCode != 200)
                return true;
            return false;
        }
        public static bool IsFault(string message)
        {
            //TOOD this can use some more logic
            if (HttpContext.Current != null && HttpContext.Current.Response.StatusCode != 200)
                return true;
            if (message.Contains("fault"))
                return true;
            return false;
        }


    }

    /// <summary>
    /// due to the constraints of the asp.net soap extension, we use this "filter"
    /// to read the response message from a stream.
    /// This code is parically borrowed and customized from
    /// http://aspnetresources.com/articles/HttpFilters.aspx
    /// </summary>
    public class SniiferFilter : Stream
    {
        Stream responseStream;
        long position;
        StringBuilder responseHtml;

        static AgentMessageTable ctx2 = AgentMessageTable.Instance;
        static MessageProcessor ctx = MessageProcessor.Instance;

        public SniiferFilter(Stream inputStream)
        {
            responseStream = inputStream;
            responseHtml = new StringBuilder();
        }

        #region Filter overrides
        public override bool CanRead
        {
            get { return responseStream.CanRead; }
        }

        public override bool CanSeek
        {
            get { return responseStream.CanSeek; }
        }

        public override bool CanWrite
        {
            get { return responseStream.CanWrite; }
        }

        public override void Close()
        {
            responseStream.Close();
        }

        public override void Flush()
        {
            responseStream.Flush();
        }

        public override long Length
        {
            get { return responseStream.Length; }
        }

        public override long Position
        {
            get { return position; }
            set { position = value; }
        }

        public override long Seek(long offset, SeekOrigin origin)
        {
            return responseStream.Seek(offset, origin);
        }

        public override void SetLength(long length)
        {
            responseStream.SetLength(length);
        }

        public override int Read(byte[] buffer, int offset, int count)
        {
            return responseStream.Read(buffer, offset, count);
        }
        #endregion

        #region Dirty work
        public override void Write(byte[] buffer, int offset, int count)
        {
            if (HttpContext.Current == null)
            {
                Logger.warn(this.GetType().FullName+ " SniiferFilter, http context is null, calling assembly must be outside of IIS");
            }
            else
            {
                string strBuffer = System.Text.UTF8Encoding.UTF8.GetString(buffer, offset, count);
                responseHtml.Append(strBuffer);
                string response = responseHtml.ToString();



                try
                {
                    int k = System.Web.HttpContext.Current.Request.GetHashCode();
                    MessageCorrelator mc = AgentMessageTable.GetRequestbyHash(k);
                    //DateTime start = AgentMessageTable.GetRequestHash(k);
                    //if (start == null || start == DateTime.MinValue)
                    if (mc == null)
                    {
                        Logger.warn(this.GetType().FullName + " SniiferFilter unable to find corresponding request message to this response, it will be ignored.");
                        
                    }
                    else
                    {
                        if (String.IsNullOrEmpty(mc.soapAction))
                        {
                            string action = "urn:undeterminable";

                            action = System.Web.HttpContext.Current.Request.Headers["SOAPAction"];
                            if (!String.IsNullOrEmpty(action))
                            {
                                action = action.Replace("\"", "");
                                action = action.Replace("'", "");
                                if (String.IsNullOrEmpty(action))
                                    action = "urn:undeterminable";
                            }
                            if (action == "urn:undeterminable")
                            {
                                action = System.Web.HttpContext.Current.Request.HttpMethod;
                                action = action.Replace("\"", "");
                                action = action.Replace("'", "");
                                if (String.IsNullOrEmpty(action))
                                    action = "urn:undeterminable";
                            }
                        }

                        //string user = null;
                        if (mc.identity == null)
                            mc.identity = new List<string>();
                        if (System.Web.HttpContext.Current.User.Identity.IsAuthenticated)
                            mc.identity.Add(System.Web.HttpContext.Current.User.Identity.Name);
                        mc.CompletedAt = DateTime.Now;
                        mc.ResponseMessage = response;
                        mc.responsesize = mc.ResponseMessage.Length;
                        mc.identity.Add(mc.ipaddress);
                        mc.agenttype = "org.miloss.fgsms.agents.AgentSoapExtension";
                        mc.IsFault = AgentSoapExtension.IsFault(response);
                        try
                        {
                            //mc.ResponseHeaders = HttpContext.Current.Response.
                            try
                            {

                                mc.ResponseHeaders = HttpContext.Current.Response.Headers;
                            }
                            catch
                            {
                                mc.ResponseHeaders = new NameValueCollection();
                                mc.ResponseHeaders.Add("charset", HttpContext.Current.Response.Charset);
                                mc.ResponseHeaders.Add("ContentEncoding", HttpContext.Current.Response.ContentEncoding.WebName);
                                mc.ResponseHeaders.Add("ContentType", HttpContext.Current.Response.ContentType);
                                mc.ResponseHeaders.Add("soapAction", mc.soapAction);
                            }
                            mc.ipaddress = HttpContext.Current.Request.UserHostAddress;
                            if (HttpContext.Current.Request.ClientCertificate != null)
                                mc.ClientCertificate = new X509Certificate2(HttpContext.Current.Request.ClientCertificate.Certificate);

                        }
                        catch (Exception ex)
                        {
                            Logger.warn(ex, this.GetType().FullName+" SniiferFilter configuration error for " + 
                                    mc.URL + " you'll have to enable IIS Intergrated Pipeline Mode when running IIS 7+. Items such as headers, remote requestor ips and certificate info will not be recorded");
                            
                        }


                        MessageProcessor.ProcessMessage(mc);
                        /*
                        MessageProcessor.ProcessMessage(System.Web.HttpContext.Current.Request.Url.ToString(),
                            start,
                            DateTime.Now,
                            action,
                            AgentMessageTable.GetRequestMessage(System.Web.HttpContext.Current.Request.GetHashCode()),
                            response,
                            System.Web.HttpContext.Current.Request.GetHashCode().ToString(),
                            AgentSoapExtension.IsFault(response),
                            System.Web.HttpContext.Current,
                            System.Web.HttpContext.Current.Request.UserHostAddress, "org.miloss.fgsms.agents.AgentSoapExtension",
                            user
                            );*/
                        AgentMessageTable.RemoveMessage(System.Web.HttpContext.Current.Request.GetHashCode());
                        Logger.debug(this.GetType().FullName +"SniiferFilter success to " + System.Web.HttpContext.Current.Request.Url.ToString());
                        
                    }
                }
                catch (Exception ex)
                {
                    Logger.error(ex, "Error aquiring data from asp.net sniffer filter agent. please report.");
                    
                    AgentMessageTable.RemoveMessage(System.Web.HttpContext.Current.Request.GetHashCode());
                }
                //byte[] data = System.Text.UTF8Encoding.UTF8.GetBytes(response);
            }
            responseStream.Write(buffer, offset, count);
        }
    }
        #endregion


    /// <summary>
    /// as of RC6.0.1, allows you to programmatically add a ASP.NET soap extension for FGSMS (i.e. monitor a specific endpoint)
    /// eh, it doesn't work anyhow
    /// </summary>
    [AttributeUsage(AttributeTargets.Method)]
    public class FGSMS_ASPNET_ExtensionAttribute : SoapExtensionAttribute
    {
        public override Type ExtensionType
        {
            get { return typeof(AgentSoapExtension); }
        }
        private int _pri = 1;
        public override int Priority
        {
            get
            {
                return _pri;
            }
            set
            {
                _pri = value;
            }
        }
    }
}