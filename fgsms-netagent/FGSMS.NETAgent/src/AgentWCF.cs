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
using System.Configuration;
using System.Diagnostics;
using System.ServiceModel;
using System.ServiceModel.Configuration;
using System.ServiceModel.Description;
using System.ServiceModel.Dispatcher;
using System.Web;
using System.Web.Services.Protocols;
using System.ServiceModel.Channels;
using System.Collections;
using System.Collections.Generic;
using System.Net.NetworkInformation;
using System.Xml;
using System.Xml.XPath;
using System.IO;
using System.Net;
using System.Collections.Specialized;
using System.Threading;
using System.Reflection;
using System.Runtime.InteropServices;
using org.miloss.fgsms.agent;
using System.ServiceModel.Security;
using System.Text;
using System.Runtime.Serialization.Json;



namespace org.miloss.fgsms.agent.wcf
{
    /// <summary>
    /// AgentWCFServiceBehavior, provides WCF hook for centralized service monitoring
    /// </summary>
    public class AgentWCFServiceBehavior : IServiceBehavior
    {
        #region IServiceBehavior Members
        /// <summary>
        /// 
        /// </summary>
        /// <param name="serviceDescription"></param>
        /// <param name="serviceHostBase"></param>
        /// <param name="endpoints"></param>
        /// <param name="bindingParameters"></param>
        public void AddBindingParameters(System.ServiceModel.Description.ServiceDescription serviceDescription, System.ServiceModel.ServiceHostBase serviceHostBase, System.Collections.ObjectModel.Collection<System.ServiceModel.Description.ServiceEndpoint> endpoints, System.ServiceModel.Channels.BindingParameterCollection bindingParameters)
        {
        } // end AddBindingParameters
        /// <summary>
        /// 
        /// </summary>
        /// <param name="serviceDescription"></param>
        /// <param name="serviceHostBase"></param>
        public void ApplyDispatchBehavior(System.ServiceModel.Description.ServiceDescription serviceDescription, System.ServiceModel.ServiceHostBase serviceHostBase)
        {
            foreach (ChannelDispatcher dispatcher in serviceHostBase.ChannelDispatchers)
            {
                foreach (EndpointDispatcher endpoint in dispatcher.Endpoints)
                {
                    endpoint.DispatchRuntime.MessageInspectors.Add(new AgentWCFServiceMessageInspector());
                }
            }
        } // end ApplyDispatchBehavior
        /// <summary>
        /// 
        /// </summary>
        /// <param name="serviceDescription"></param>
        /// <param name="serviceHostBase"></param>
        public void Validate(System.ServiceModel.Description.ServiceDescription serviceDescription, System.ServiceModel.ServiceHostBase serviceHostBase)
        {
        } // end Validate

        #endregion IServiceBehavior Members
    } // end class AgentWCFServiceBehavior

    /// <summary>
    /// AgentWCFServiceMessageInspector, obtains and sends data to the centralized FGSMS Data Collector Service
    /// use extreme caution modifying this file and adhere to inline documentation
    /// logs to TraceSource "org.miloss.fgsms.agentWCF"
    /// </summary>
    public class AgentWCFServiceMessageInspector : IDispatchMessageInspector
    {
        const string name = "org.miloss.fgsms.agentWCF";

        //TraceSource log;
        /// <summary>
        /// default constructor
        /// </summary>
        public AgentWCFServiceMessageInspector()
        {
           // log = new TraceSource("org.miloss.fgsms.agents.AgentWCF");
        } // end AgentWCFServiceMessageInspector



        #region IDispatchMessageInspector Members
        /// <summary>
        /// service traffic inbound request
        /// </summary>
        /// <param name="request"></param>
        /// <param name="channel"></param>
        /// <param name="instanceContext"></param>
        /// <returns></returns>
        public object AfterReceiveRequest(ref System.ServiceModel.Channels.Message request, System.ServiceModel.IClientChannel channel, System.ServiceModel.InstanceContext instanceContext)
        {
            
            string u=channel.LocalAddress.Uri.ToString();
            
            WCFContainer w = new WCFContainer();
            w.thistid = "";
            w.relatedid = "";
            w.start = DateTime.Now;
            w.url = u;
            string query = String.Empty;
            try
            {
                //added
                w.action = request.Headers.Action;
                w.myinboundheaders = (HttpRequestMessageProperty)request.Properties[HttpRequestMessageProperty.Name];
                if (request != null)
                {
                    w.req = MessageHelper.MessagetoString(ref request);
                        //request.ToString();
                }
            }
            catch (Exception ex)
            {
                try
                {
                    //          if (FGSMSConstants.log) EventLog.WriteEntry(this.GetType().FullName, "Error from AfterRecieveRequest" + ex.Message);
                }
                catch { }
            }
            if (String.IsNullOrEmpty(w.url))
            try
            {
                w.url = request.Headers.To.ToString();
                query = request.Headers.To.Query;
                if (!String.IsNullOrEmpty(query))
                {
                    int idx2 = w.url.LastIndexOf(query);
                    w.url = w.url.Substring(0, idx2);
                    //w.url = w.url.Replace(query, String.Empty);
                }
                if (w.url.EndsWith("?wsdl", StringComparison.CurrentCultureIgnoreCase))
                    w.url = w.url.Replace("?wsdl", "");
                //w.url = w.url.Substring(0, w.url.Length - 5);
            }
            catch (Exception x)
            { }
            //AgentWCFClientMessageInspector.GetHttpRequestProp(request);
            try//added
            {
                if (ServiceSecurityContext.Current != null && !ServiceSecurityContext.Current.IsAnonymous)
                    w.user = ServiceSecurityContext.Current.PrimaryIdentity.Name;
            }
            catch (Exception ex)
            {
                Logger.debug(ex, this.GetType().FullName + " error from AfterRecieveRequest when obtaining the current user's identity");
            }
            if (Trace.CorrelationManager.ActivityId == Guid.Empty)
            {
                Guid newGuid = Guid.NewGuid();
                Trace.CorrelationManager.ActivityId = newGuid;
            }

            try
            {
                if (request.Headers == null || request.Headers.MessageId == null)
                {
                    w.thisid = Guid.NewGuid().ToString();
                    //      if (FGSMSConstants.log) log.TraceEvent(TraceEventType.Verbose, 0, (name + " AfterReceiveRequest Main Thread id " + System.Threading.Thread.CurrentThread.ManagedThreadId +
                    //        "Error, inbound message does not have a message id. Generating a new guid"));
                }
                else w.thisid = request.Headers.MessageId.ToString();
            }
            catch (Exception ex)
            {
                Logger.warn(ex, " error caught obtaining request headers, this is unexpected");
            }
            w.thistid = "";
            if (OperationContext.Current != null)
            {
                try
                {
                    HttpRequestMessageProperty myinboundheaders = OperationContext.Current.IncomingMessageProperties[HttpRequestMessageProperty.Name] as HttpRequestMessageProperty;
                    if (myinboundheaders != null)
                    {
                        w.relatedid = myinboundheaders.Headers[FGSMSConstants.MessageId];
                        w.thistid = myinboundheaders.Headers[FGSMSConstants.transactionthreadKey];
                        if (!String.IsNullOrEmpty(w.thistid))   //added
                            w.thistid = w.thistid.Trim();
                        w.myinboundheaders = myinboundheaders;

                    }
                }
                catch (Exception ex)
                {
                    Logger.debug(ex);
                }
            }

            int idx = request.Headers.FindHeader(FGSMSSoapHeaderRelatedMessageIdWCF.Name2, FGSMSSoapHeaderRelatedMessageIdWCF.Namespace2);
            if (idx >= 0)
            {
                MessageHeaderInfo info = request.Headers[idx] as MessageHeaderInfo;
                if (info.Name.Equals(FGSMSSoapHeaderRelatedMessageIdWCF.Name2, StringComparison.CurrentCultureIgnoreCase) && info.Namespace.Equals(FGSMSSoapHeaderRelatedMessageIdWCF.Namespace2, StringComparison.CurrentCultureIgnoreCase))
                {
                    XmlDictionaryReader reader = request.Headers.GetReaderAtHeader(idx);
                    FGSMSSoapHeaderRelatedMessageIdWCF h = FGSMSSoapHeaderRelatedMessageIdWCF.ReadHeader(reader);
                    reader.Close();

                    if (h != null)
                        w.relatedid = h.Id;
                }
            }
            idx = request.Headers.FindHeader(FGSMSSoapHeaderTransactionThreadIdWCF.Name2, FGSMSSoapHeaderTransactionThreadIdWCF.Namespace2);
            if (idx >= 0)
            {
                XmlDictionaryReader reader = request.Headers.GetReaderAtHeader(idx);
                FGSMSSoapHeaderTransactionThreadIdWCF h = FGSMSSoapHeaderTransactionThreadIdWCF.ReadHeader(reader);
                reader.Close();

                if (h != null)
                    w.thistid = h.Id;
            }
            

            if (String.IsNullOrEmpty(w.thistid))
                w.thistid = Guid.NewGuid().ToString();
            MessageProcessor.SetTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name, w.thistid);
            /*
             DependencyContextExtension x = DependencyContextExtension.Current;
             if (x == null)
                 DependencyContextExtension.Add(OperationContext.Current);
             */

            //c.Add(Thread.CurrentContext.ContextID.ToString() + ":" + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + ":" + Thread.CurrentThread.Name, w);


            return w;
        } // end AfterReceiveRequest

        /// <summary>
        /// service traffic outbound response
        /// </summary>
        /// <param name="reply"></param>
        /// <param name="correlationState"></param>
        public void BeforeSendReply(ref System.ServiceModel.Channels.Message reply, object correlationState)
        {
           // object prop;
           // string requestHeader = null;
            //Console.WriteLine("In {0}", MethodBase.GetCurrentMethod().Name);
            WCFContainer w = null;
            try
            {
                try
                {
                    w = (WCFContainer)correlationState;
                }
                catch (Exception ex)
                {
                    Logger.debug(ex, "can't get reference to the correlation object");
                }
                if (w == null)
                {
                    Logger.debug("can't get reference to the correlation object");
                    return;
                }

                /******************************
                 * BEGIN, do not remove this code, there's some kind of strange bug in wcf that causes services using this handler to hang after 10 invocations
                 * unless this code is execute. I believe it's related to obtaining http headers
                 */
                /*
                 if (OperationContext.Current.IncomingMessageProperties.TryGetValue(HttpRequestMessageProperty.Name, out prop))
                 {
                     HttpRequestMessageProperty reqProp = (HttpRequestMessageProperty)prop;
                     requestHeader = reqProp.Headers["X-MyHeader"];
                    // Console.WriteLine("Got the request header: {0}", requestHeader);
                 }

                 if (!OperationContext.Current.OutgoingMessageProperties.TryGetValue(HttpResponseMessageProperty.Name, out prop))
                 {
                     prop = new HttpResponseMessageProperty();
                     OperationContext.Current.OutgoingMessageProperties.Add(HttpResponseMessageProperty.Name, prop);
                 }
                 HttpResponseMessageProperty respProp = (HttpResponseMessageProperty)prop;
                 respProp.Headers["X-MyResponseHeader2"] = "Added from BeforeSendReply - " + requestHeader;
                 * */


                /**********************************
                 * END
                 */
            }
            catch (Exception ex)
            {
                Logger.debug(ex, "can't get reference to the correlation object");
            }

            //this is usually a ?wsdl request or a null request such a get

            MessageProcessor ctx = MessageProcessor.Instance;

            try
            {
                string use = string.Empty;
                use = w.thisid;

                String action = "urn:undeterminable";
                if (w.action != null)
                {
                    action = w.action;
                    action = action.Replace("\"", "");
                    action = action.Replace("'", "");
                    if (String.IsNullOrEmpty(action))
                        action = "urn:undeterminable";
                }
                if (action.Equals("urn:undeterminable") && w.myinboundheaders != null &&
                  !String.IsNullOrEmpty(w.myinboundheaders.Headers["SOAPAction"]))
                //   if (System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage.Headers != null &&
                //  !String.IsNullOrEmpty(System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage.Headers.Action))
                {
                    action = w.myinboundheaders.Headers["SOAPAction"];
                    //System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage.Headers.Action;
                    action = action.Replace("\"", "");
                    action = action.Replace("'", "");
                    if (String.IsNullOrEmpty(action))
                        action = "urn:undeterminable";
                }
                if (action.Equals("urn:undeterminable") && HttpContext.Current != null)
                {
                    action = HttpContext.Current.Request.Headers.Get("SOAPAction");
                    action = action.Replace("\"", "");
                    action = action.Replace("'", "");
                    if (String.IsNullOrEmpty(action))
                        action = "urn:undeterminable";
                }
                if (action.Equals("urn:undeterminable") && HttpContext.Current != null)
                {
                    action = HttpContext.Current.Request.HttpMethod;
                    action = action.Replace("\"", "");
                    action = action.Replace("'", "");
                    if (String.IsNullOrEmpty(action))
                        action = "urn:undeterminable";
                }
                string ip = string.Empty;
                NameValueCollection reqheaders = null;
                if (w.myinboundheaders != null && w.myinboundheaders.Headers != null)
                    reqheaders = w.myinboundheaders.Headers;
                NameValueCollection resheaders = new NameValueCollection();
                if (HttpContext.Current != null)
                {
                    ip = HttpContext.Current.Request.UserHostAddress;
                    resheaders = HttpContext.Current.Response.Headers;
                    reqheaders = HttpContext.Current.Request.Headers;
                }
                else
                {
                    if (OperationContext.Current != null)
                    {
                        try
                        {
                            MessageProperties messageProperties = OperationContext.Current.IncomingMessageProperties;

                            if (w.myinboundheaders == null)
                                w.myinboundheaders = messageProperties[HttpRequestMessageProperty.Name] as HttpRequestMessageProperty;
                            RemoteEndpointMessageProperty endpointProperty =
                           messageProperties[RemoteEndpointMessageProperty.Name]
                           as RemoteEndpointMessageProperty;
                            ip = endpointProperty.Address;
                        }
                        catch (Exception ex)
                        {
                            Logger.debug(ex);
                        }
                        if (w.myinboundheaders != null)
                        {
                            if (action.Equals("urn:undeterminable"))
                                action = w.myinboundheaders.Method + w.myinboundheaders.QueryString;
                            if (w.myinboundheaders.Headers != null)
                            {
                                reqheaders = w.myinboundheaders.Headers;
                            }
                        }



                    }
                }
                if (ip == "127.0.0.1" || ip == "::1")
                {
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
                    catch (Exception ex)
                    {
                        Logger.debug(ex);
                    }

                }

                string user = user = w.user;

                if (resheaders != null && MessageProcessor.GetConfig.DependencyInjectionEnabled)
                {
                    resheaders.Add(FGSMSConstants.MessageId, use);
                    resheaders.Add(FGSMSConstants.transactionthreadKey, w.thistid);

                }
                if (MessageProcessor.GetConfig.DependencyInjectionEnabled)// && !OperationContext.Current.OutgoingMessageProperties.TryGetValue(HttpResponseMessageProperty.Name, out prop))
                {
                    HttpResponseMessageProperty respProp = null;
                    try
                    {
                        respProp = (HttpResponseMessageProperty)OperationContext.Current.OutgoingMessageProperties[HttpResponseMessageProperty.Name];
                    }
                    catch { }
                    if (respProp != null)
                    {
                        respProp.Headers.Add(FGSMSConstants.MessageId, use);
                        respProp.Headers.Add(FGSMSConstants.transactionthreadKey, w.thistid);
                    }
                    if (reply != null && reply.Version != MessageVersion.None)
                    {
                        try
                        {
                            reply.Headers.Add(new FGSMSSoapHeaderRelatedMessageIdWCF(use));
                            reply.Headers.Add(new FGSMSSoapHeaderTransactionThreadIdWCF(w.thistid));
                        }
                        catch { }
                    }
                    //else can't add response http headers!
                }

                MessageProcessor.ProcessMessage(w.url, //System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage.Headers.To.ToString(),
                    w.start, DateTime.Now, action,
                    w.req,
                    //System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage.ToString(),
                    MessageHelper.MessagetoString(ref reply),
                  //  reply.ToString(), 
                    use, (reply == null) ? false : reply.IsFault, HttpContext.Current, ip, this.GetType().FullName, user, "Current .net thread: " + Thread.CurrentThread.ManagedThreadId,
                    //thread id
                         w.thistid,
                    //related transaction (id of the inbound message)
                         w.relatedid, reqheaders, resheaders);


            }
            catch (Exception ex)
            {
                Logger.error(ex,this.GetType().FullName+ " Error from FGSMS WCF Service Agent, BeforeSendReply. This typically means that the message traffic to the url " + w.url + " could not be added the queue: ");
            }
            try { MessageProcessor.ClearTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name); }
            catch (Exception ex){
                Logger.debug(ex);
            }
        } // end BeforeSendReply


        #endregion IDispatchMessageInspector Members
    } // end class AgentWCFServiceMessageInspector

    /// <summary>
    /// wrapper for configuration elements
    /// </summary>
    public class AgentWCFServiceBehaviorElement : BehaviorExtensionElement
    {
        public AgentWCFServiceBehaviorElement()
        {
        } // end AgentWCFServiceBehaviorElement

        public static string BehaviorName = "org.miloss.fgsms.agent";
        public override Type BehaviorType
        {
            get { return typeof(AgentWCFServiceBehavior); }
        } // end BehaviorType

        protected override object CreateBehavior()
        {
            return new AgentWCFServiceBehavior();
        } // end CreateBehavior

        ConfigurationPropertyCollection properties = null;
        protected override ConfigurationPropertyCollection Properties
        {
            get
            {
                if (this.properties == null)
                {
                    ConfigurationPropertyCollection propertys = new ConfigurationPropertyCollection();
                    this.properties = propertys;
                }
                return this.properties;
            }
        } // end Properties
    } // end class AgentWCFServiceBehaviorElement

    /// <summary>
    /// provides a temporary container for passing objects from request to responses
    /// </summary>
    class WCFContainer
    {
        public string req;
        public string action;
        public string url;
        public string thisid;
        public string thistid;
        public string relatedid;
        public DateTime start;
        public string user;
        public HttpRequestMessageProperty myinboundheaders;
    }


    /// <summary>
    /// Client message inspector, used to sniff request traffic 
    /// use extreme caution when modifing this class, the smallest change can break things
    /// </summary>
    public class AgentWCFClientMessageInspector : IClientMessageInspector
    {
        #region IClientMessageInspector Members


        /// <summary>
        /// Agent for client traffic inbound response
        /// </summary>
        /// <param name="reply"></param>
        /// <param name="correlationState"></param>
        public void AfterReceiveReply(ref System.ServiceModel.Channels.Message reply, object correlationState)
        {
            MessageProcessor mp = MessageProcessor.Instance;
            NameValueCollection requestheaders = new NameValueCollection();
            NameValueCollection responseheaders = new NameValueCollection();
            //  if (MessageProcessor.Enabled)

            /*if (FGSMSConstants.log)
                try
                {
                    if (FGSMSConstants.log) EventLog.WriteEntry(this.GetType().FullName, "Client hello from AfterRecieveReply");
                }
                catch { }*/



            // HttpRequestMessageProperty prop = new HttpRequestMessageProperty();
            // prop.Headers.Add("FGSMS.transactionrecord", Guid.NewGuid().ToString());

            WCFContainer w = null;
            try
            {
                w = (WCFContainer)correlationState;
            }
            catch (Exception ex)
            {
                Logger.debug(ex,"unable to get correlation object");
            }
            if (w == null)
            {
                Logger.debug("unable to get correlation object");
            }
            if (OperationContext.Current != null)
            {
                IEnumerator<string> it2 = OperationContext.Current.OutgoingMessageProperties.Keys.GetEnumerator();
                while (it2.MoveNext())
                {
                    if (it2.Current == HttpRequestMessageProperty.Name)
                    {
                        HttpRequestMessageProperty prop = OperationContext.Current.OutgoingMessageProperties[it2.Current] as HttpRequestMessageProperty;
                        if (prop != null)
                            requestheaders = prop.Headers;
                    }
                }
                if (OperationContext.Current.IncomingMessageProperties != null)
                {
                    IEnumerator<string> it = OperationContext.Current.IncomingMessageProperties.Keys.GetEnumerator();
                    while (it.MoveNext())
                    {
                        if (it.Current == HttpResponseMessageProperty.Name)
                        {
                            HttpResponseMessageProperty prop = OperationContext.Current.IncomingMessageProperties[it.Current] as HttpResponseMessageProperty;
                            if (prop != null)
                                responseheaders = prop.Headers;

                        }
                    }
                }
            }
            if (responseheaders != null && reply.Properties.ContainsKey(HttpResponseMessageProperty.Name))
            {
                HttpResponseMessageProperty rh = reply.Properties[HttpResponseMessageProperty.Name] as HttpResponseMessageProperty;
                responseheaders = rh.Headers;
            }


            if (w.myinboundheaders != null)
            {
                IEnumerator it = w.myinboundheaders.Headers.GetEnumerator();

                while (it.MoveNext())
                {
                    try
                    {
                        if (it != null && it.Current != null)
                        {
                            string name = it.Current as string;
                            if (name != null)
                                requestheaders.Add(name, w.myinboundheaders.Headers.Get(name));
                        }
                    }
                    catch (Exception ex) {
                        Logger.debug(ex);
                    }
                }
            }
            try
            {
                string myHost = String.Empty;
                string myIP = String.Empty;
                try
                {
                    myHost = System.Net.Dns.GetHostName();
                    myIP = System.Net.Dns.GetHostEntry(myHost).AddressList[0].ToString();
                }
                catch { }

                string relatedmsg = string.Empty;
                if (responseheaders != null)
                    relatedmsg = responseheaders[FGSMSConstants.MessageId];
                int idx = reply.Headers.FindHeader(FGSMSSoapHeaderRelatedMessageIdWCF.Name2, FGSMSSoapHeaderRelatedMessageIdWCF.Namespace2);
                if (idx >= 0)
                {

                    FGSMSSoapHeaderRelatedMessageIdWCF h = FGSMSSoapHeaderRelatedMessageIdWCF.ReadHeader(reply.Headers.GetReaderAtHeader(idx));
                    if (h != null)
                        relatedmsg = h.Id;
                }
                //since this is a client, the thread id is already known

                //string action = "urn:undeterminable";

                if (OperationContext.Current != null && OperationContext.Current.RequestContext != null)
                {
                    //typically a service as a client
                    string user = "";
                    if (ServiceSecurityContext.Current != null)
                        user = ServiceSecurityContext.Current.PrimaryIdentity.Name;
                    MessageProcessor.ProcessMessage(w.url,
                        //OperationContext.Current.RequestContext.RequestMessage.Headers.To.ToString(), \
                        w.start, DateTime.Now,
                        w.action,
                        //OperationContext.Current.RequestContext.RequestMessage.Headers.Action, 
                        w.req,
                        //OperationContext.Current.RequestContext.RequestMessage.ToString(),
                        reply.ToString(), w.thisid, reply.IsFault, HttpContext.Current, myIP, this.GetType().FullName + ".client",
                        user, string.Empty, w.thistid, relatedmsg, requestheaders, responseheaders);
                }
                else
                {
                    //stand alone client
                    MessageProcessor.ProcessMessage(w.url, w.start, DateTime.Now,
                        w.action, w.req,
                      MessageHelper.MessagetoString(ref  reply)
                      //.ToString()
                      , w.thisid, reply.IsFault, HttpContext.Current, myIP, this.GetType().FullName + ".client",
                        System.Environment.UserName, string.Empty, w.thistid, relatedmsg, requestheaders, responseheaders);
                }
            }

            catch (Exception ex)
            {
                Logger.error(ex, this.GetType().FullName + "Error from FGSMS WCF client agent, AfterReceiveReply, this typically means that a transaction could not be added to the queue, ");
            }


        } // end AfterReceiveReply

        /// <summary>
        /// agent for client traffic outbound request
        /// </summary>
        /// <param name="request"></param>
        /// <param name="channel"></param>
        /// <returns></returns>
        public object BeforeSendRequest(ref System.ServiceModel.Channels.Message request, IClientChannel channel)
        {

            WCFContainer w = new WCFContainer();
            w.thistid = "";
            w.thisid = Guid.NewGuid().ToString();
            w.start = DateTime.Now;

            //request.Headers.Add(new FGSMSAgentHeaderWCF());
            //  start = DateTime.Now;
            if (request != null)
              w.req=  MessageHelper.MessagetoString(ref request);
              //  w.req = request.ToString();

            w.url = "urn:undeterminable";
            w.action = "urn:undeterminable";
            if (System.ServiceModel.OperationContext.Current != null &&
                System.ServiceModel.OperationContext.Current.RequestContext != null &&
                System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage != null &&
                System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage.Headers != null &&
                !String.IsNullOrEmpty(System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage.Headers.Action))
            {
                w.action = System.ServiceModel.OperationContext.Current.RequestContext.RequestMessage.Headers.Action;
                w.action.Replace("\"", "");
                w.action.Replace("'", "");
                if (String.IsNullOrEmpty(w.action))
                    w.action = "urn:undeterminable";
            }
            if (w.action.Equals("urn:undeterminable"))
            {
                w.action = request.Headers.Action;
                w.action.Replace("\"", "");
                w.action.Replace("'", "");
                if (String.IsNullOrEmpty(w.action))
                    w.action = "urn:undeterminable";

            }
            if (w.action.Equals("urn:undeterminable") && HttpContext.Current != null)
            {
                w.action = HttpContext.Current.Request.Headers.Get("SOAPAction");
                w.action = w.action.Replace("\"", "");
                w.action = w.action.Replace("'", "");
                if (String.IsNullOrEmpty(w.action))
                    w.action = "urn:undeterminable";
            }
            if (w.action.Equals("urn:undeterminable") && HttpContext.Current != null)
            {
                w.action = HttpContext.Current.Request.HttpMethod;
                w.action = w.action.Replace("\"", "");
                w.action = w.action.Replace("'", "");
                if (String.IsNullOrEmpty(w.action))
                    w.action = "urn:undeterminable";
            }
            if (w.action.Equals("urn:undeterminable"))
            {
                w.action = getActionFromFirstXmlNode(request);
            }
            if (OperationContext.Current != null && request.Headers.To != null)
            {
                w.url = request.Headers.To.ToString();

            }
            if (w.url.Equals("urn:undeterminable"))
            {
                w.url = channel.RemoteAddress.Uri.ToString();
            }
            if (w.url.Equals("urn:undeterminable"))
            {

            }
            //changed
            w.thistid = MessageProcessor.GetTransactionThreadId(Thread.CurrentContext.ContextID.ToString() + Thread.CurrentThread.ManagedThreadId.ToString() + ":" + Thread.GetDomainID().ToString() + Thread.CurrentThread.Name);
            if (!String.IsNullOrEmpty(w.thistid))
                w.thistid = w.thistid.Trim();
            if (String.IsNullOrEmpty(w.thistid))
            {
                w.thistid = Guid.NewGuid().ToString();
            }
            // string relatedtransaction = "";
            w.myinboundheaders = GetHttpRequestProp(request);//request.Properties[HttpRequestMessageProperty.Name] as HttpRequestMessageProperty;
            if (w.myinboundheaders != null && MessageProcessor.GetConfig.DependencyInjectionEnabled)
            {
                //for some reason, the http request property is persisted across objects when the proxy object is reused.
                //its most likely an optimization from microsoft regarding http session reuse.

                //When in visual studuo, it added http headers to outbound connections called vscasultydata, it will also be repeated when 
                //persisting connections...
                w.myinboundheaders.Headers.Remove("VsDebuggerCausalityData");
                w.myinboundheaders.Headers.Remove(FGSMSConstants.MessageId);
                w.myinboundheaders.Headers.Remove(FGSMSConstants.transactionthreadKey);

                w.myinboundheaders.Headers.Add(FGSMSConstants.MessageId, w.thisid);
                w.myinboundheaders.Headers.Add(FGSMSConstants.transactionthreadKey, w.thistid);

            }
            if (MessageProcessor.GetConfig.DependencyInjectionEnabled)
            {
                //  MessageHeader mh = new MessageHeader("value").GetUnt
                request.Headers.Add(new FGSMSSoapHeaderTransactionThreadIdWCF(w.thistid));
                request.Headers.Add(new FGSMSSoapHeaderRelatedMessageIdWCF(w.thisid));
            }
            return w;
        }


        private string getActionFromFirstXmlNode(Message request)
        {

            StringReader r = new StringReader(request.ToString());
            XPathDocument doc = new XPathDocument(r);

            XPathNavigator nav = doc.CreateNavigator();
            nav.MoveToChild(XPathNodeType.Element);//envelope
            if (nav.Name.Contains("Envelope"))
            {
                nav.MoveToFirstChild();
                while (nav.MoveToNext(XPathNodeType.Element))
                {
                    if (nav.Name.Contains("Body"))
                    {
                        nav.MoveToFirstChild();
                        r.Dispose();
                        return nav.NamespaceURI + nav.Name;
                    }
                }
            }

            r.Dispose();

            return "urn:undeterminable";



        } // end BeforeSendRequest

        internal static HttpRequestMessageProperty GetHttpRequestProp(Message request)
        {
            if (!request.Properties.ContainsKey(HttpRequestMessageProperty.Name))
            {
                request.Properties.Add(HttpRequestMessageProperty.Name, new HttpRequestMessageProperty());
            }

            return request.Properties[HttpRequestMessageProperty.Name] as HttpRequestMessageProperty;
        } // end GetHttpRequestProp

        #endregion
    } // end class AgentWCFClientMessageInspector


    /// <summary>
    /// FGSMS WCF Client side handler/behavior, this is the hook
    /// Implements IEndpointBehavior
    /// </summary>
    public class AgentWCFClientEndpointBehavior : IEndpointBehavior
    {
        #region IEndpointBehavior Members
        /// <summary>
        /// 
        /// </summary>
        /// <param name="endpoint"></param>
        /// <param name="bindingParameters"></param>
        public void AddBindingParameters(ServiceEndpoint endpoint, System.ServiceModel.Channels.BindingParameterCollection bindingParameters)
        {
        } // end AddBindingParameters
        /// <summary>
        /// 
        /// </summary>
        /// <param name="endpoint"></param>
        /// <param name="clientRuntime"></param>
        public void ApplyClientBehavior(ServiceEndpoint endpoint, ClientRuntime clientRuntime)
        {
            clientRuntime.MessageInspectors.Add(new AgentWCFClientMessageInspector());
        } // end ApplyClientBehavior
        /// <summary>
        /// 
        /// </summary>
        /// <param name="endpoint"></param>
        /// <param name="endpointDispatcher"></param>
        public void ApplyDispatchBehavior(ServiceEndpoint endpoint, EndpointDispatcher endpointDispatcher)
        {
        } // end ApplyDispatchBehavior
        /// <summary>
        /// 
        /// </summary>
        /// <param name="endpoint"></param>
        public void Validate(ServiceEndpoint endpoint)
        {
        } // end Validate

        #endregion
    } // end class AgentWCFClientEndpoingBehavior

    /// <summary>
    /// config elements
    /// </summary>
    public class AgentWCFClientEndpointBehaviorElement : BehaviorExtensionElement
    {
        /// <summary>
        /// 
        /// </summary>
        public AgentWCFClientEndpointBehaviorElement()
        {
        } // end AgentWCFClientEndpointBehaviorElement
        /// <summary>
        /// 
        /// </summary>
        public override Type BehaviorType
        {
            get { return typeof(AgentWCFClientEndpointBehavior); }
        } // end BehaviorType
        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        protected override object CreateBehavior()
        {
            return new AgentWCFClientEndpointBehavior();
        } // end CreateBehavior

        ConfigurationPropertyCollection properties = null;
        /// <summary>
        /// 
        /// </summary>
        protected override ConfigurationPropertyCollection Properties
        {
            get
            {
                if (this.properties == null)
                {
                    ConfigurationPropertyCollection propertys = new ConfigurationPropertyCollection();
                    this.properties = propertys;
                }
                return this.properties;
            }
        } // end Properties
    } // end class AgentWCFClientEndpointBehaviorElement

    internal static class MessageHelper
    {
       internal static string MessagetoString(ref Message message)
        {
            if (message == null)
            {
                Logger.warn("Message Helper (which translates requests/responses to readable text) was passed a null value for the message. This is unexpected and message content will not be available.");
                return String.Empty;
            }
            WebContentFormat format = GetMessageContentFormat(ref message);
            MemoryStream ms = new MemoryStream();
            XmlDictionaryWriter writer = null;
            switch (format)
            {
                case WebContentFormat.Default:
                case WebContentFormat.Xml:
                    return message.ToString();
                case WebContentFormat.Json:
                    writer = JsonReaderWriterFactory.CreateJsonWriter(ms);
                    break;
                case WebContentFormat.Raw:
                    return ReadRawBody(ref message);
            }
            message.WriteMessage(writer);
            writer.Flush();
            String body = Encoding.UTF8.GetString(ms.ToArray());
            XmlDictionaryReader reader = null;
            if (format == WebContentFormat.Json)
            {
                reader = JsonReaderWriterFactory.CreateJsonReader(ms, XmlDictionaryReaderQuotas.Max);
            }
            else reader = XmlDictionaryReader.CreateTextReader(ms, XmlDictionaryReaderQuotas.Max);
            Message msg = Message.CreateMessage(reader, int.MaxValue, message.Version);
            msg.Properties.CopyProperties(message.Properties);
            message = msg;
            return body;
        }

        private static string ReadRawBody(ref Message message)
        {
            XmlDictionaryReader r = message.GetReaderAtBodyContents();
            r.ReadStartElement("Binary");
            byte[] bits = r.ReadContentAsBase64();
            string msg = Encoding.UTF8.GetString(bits);

            MemoryStream ms = new MemoryStream();
            XmlDictionaryWriter w = XmlDictionaryWriter.CreateBinaryWriter(ms);
            w.WriteStartElement("Binary");
            w.WriteBase64(bits, 0, bits.Length);
            w.WriteEndElement();
            w.Flush();
            ms.Position = 0;
            XmlDictionaryReader rt = XmlDictionaryReader.CreateBinaryReader(ms, XmlDictionaryReaderQuotas.Max);
            Message msg2 = Message.CreateMessage(rt, int.MaxValue, message.Version);
            msg2.Properties.CopyProperties(message.Properties);
            message = msg2;
            return msg;
        }

        private static WebContentFormat GetMessageContentFormat(ref Message message)
        {
            //WebContentFormat w = WebContentFormat.Default;
            if (message.Properties.ContainsKey(WebBodyFormatMessageProperty.Name))
            {
                WebBodyFormatMessageProperty v = (WebBodyFormatMessageProperty)message.Properties[WebBodyFormatMessageProperty.Name];
                return v.Format;
            }
            return WebContentFormat.Default;
        }
    }




} // end namespace org.miloss.fgsms.agents.AgentWCF
