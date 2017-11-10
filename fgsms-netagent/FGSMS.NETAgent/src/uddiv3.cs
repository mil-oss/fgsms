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
using System.Text;

using System.IO;
using System.Xml.Serialization;
using System.Xml;
using System.Security.Cryptography;
using System.Security.Cryptography.Xml;
using mil.army.soa.aesoaf.discovery.uddi;
using mil.army.soa.aesoaf.discovery;
using System.ServiceModel;
using org.miloss.fgsms.agent;

namespace org.miloss.FGSMS.discovery
{
    /// <summary>
    /// Endpoint Discovery via UDDI v3 complaint endpoint
    /// Besure to call Dispose when finished with this object
    /// </summary>
    public class uddiv3: IDisposable
    {
        
        public uddiv3(string inquiryurl, string securityurl, string username, string encryptedPassword, bool authrequired)
        {
            inquiryclient = LoaderI(inquiryurl);
            if (authrequired)
            {
                securityclient = LoaderS(securityurl);
                this.authrequired = authrequired;
                this.username = username;
                this.encryptedpassword = encryptedPassword;
            }

        }
        private string username;
        private string encryptedpassword;
        private bool authrequired = false;
        private UDDI_Inquiry_PortType inquiryclient = null;
        private UDDI_Security_PortType securityclient = null;
        private UDDI_Inquiry_PortType LoaderI(string url)
        {
            BasicHttpBinding b = null;
            if (url.StartsWith("https://", StringComparison.CurrentCultureIgnoreCase))
                b = new BasicHttpBinding(BasicHttpSecurityMode.Transport);
            else b = new BasicHttpBinding(BasicHttpSecurityMode.None);
            b.MaxBufferSize = Int32.MaxValue;
            b.MaxReceivedMessageSize = Int32.MaxValue;
            b.MaxBufferPoolSize = Int32.MaxValue;
            b.TransferMode = TransferMode.Buffered;
            b.Name = "UddiBinding";
            b.ReaderQuotas.MaxArrayLength = Int32.MaxValue;
            b.ReaderQuotas.MaxBytesPerRead = Int32.MaxValue;
            b.ReaderQuotas.MaxDepth = Int32.MaxValue;
            b.ReaderQuotas.MaxNameTableCharCount = Int32.MaxValue;
            b.ReaderQuotas.MaxStringContentLength = Int32.MaxValue;
            b.ReceiveTimeout = new TimeSpan(0, 0, 30);
            b.SendTimeout = new TimeSpan(0, 0, 30);
            b.UseDefaultWebProxy = true;
            b.TextEncoding = Encoding.UTF8;
            b.OpenTimeout = new TimeSpan(0, 0, 5);
            //b.Namespace = "urn:org.miloss.FGSMS.services.interfaces.policyConfiguration";
            b.MessageEncoding = WSMessageEncoding.Text;
            b.CloseTimeout = new TimeSpan(0, 0, 5);

            ChannelFactory<UDDI_Inquiry_PortType> factory =
                new ChannelFactory<UDDI_Inquiry_PortType>(b, url);
            UDDI_Inquiry_PortType polservice = factory.CreateChannel();

            return polservice;
        }
        private UDDI_Security_PortType LoaderS(string url)
        {
            BasicHttpBinding b = null;
            if (url.StartsWith("https://", StringComparison.CurrentCultureIgnoreCase))
                b = new BasicHttpBinding(BasicHttpSecurityMode.Transport);
            else b = new BasicHttpBinding(BasicHttpSecurityMode.None);
            b.MaxBufferSize = Int32.MaxValue;
            b.MaxReceivedMessageSize = Int32.MaxValue;
            b.MaxBufferPoolSize = Int32.MaxValue;
            b.TransferMode = TransferMode.Buffered;
            b.Name = "UddiBinding";
            b.ReaderQuotas.MaxArrayLength = Int32.MaxValue;
            b.ReaderQuotas.MaxBytesPerRead = Int32.MaxValue;
            b.ReaderQuotas.MaxDepth = Int32.MaxValue;
            b.ReaderQuotas.MaxNameTableCharCount = Int32.MaxValue;
            b.ReaderQuotas.MaxStringContentLength = Int32.MaxValue;
            b.ReceiveTimeout = new TimeSpan(0, 0, 30);
            b.SendTimeout = new TimeSpan(0, 0, 30);
            b.UseDefaultWebProxy = true;
            b.TextEncoding = Encoding.UTF8;
            b.OpenTimeout = new TimeSpan(0, 0, 5);
            //b.Namespace = "urn:org.miloss.FGSMS.services.interfaces.policyConfiguration";
            b.MessageEncoding = WSMessageEncoding.Text;
            b.CloseTimeout = new TimeSpan(0, 0, 5);

            ChannelFactory<UDDI_Security_PortType> factory =
                new ChannelFactory<UDDI_Security_PortType>(b, url);
            UDDI_Security_PortType polservice = factory.CreateChannel();

            return polservice;
        }


        /// <summary>
        /// Endpoint discovery via UDDI binding key
        /// </summary>
        /// <param name="bindingKey"></param>
        /// <param name="discoveryEndpointAddress"></param>
        /// <param name="username"></param>
        /// <param name="password"></param>
        /// <param name="securityEndpointAddress"></param>
        /// <returns></returns>
        public string[] DiscoverEndpointBindingKey(string bindingKey, string authtokenb)
        {
            if (bindingKey == null)
                throw new ArgumentNullException("bindingKey");



            string authTok = null;
            if (authrequired && String.IsNullOrEmpty(authtokenb))
                authTok = getAuthToken();
            else authTok = authtokenb;

            get_bindingDetailRequest1 req = new get_bindingDetailRequest1();
            req.get_bindingDetail = new get_bindingDetail2();

            req.get_bindingDetail.bindingKey = new string[1];
            req.get_bindingDetail.bindingKey[0] = bindingKey;

            if ((authTok != null) && (authrequired == true))
            {
                req.get_bindingDetail.authInfo = authTok;
            }

            find_bindingResponse1 response = null;
            try
            {
                response = inquiryclient.get_bindingDetail(req);
            }
            catch (Exception ex)
            {
                throw new Exception(discoveryError, ex);
            }
            get_serviceDetail2 sreq = new get_serviceDetail2();


            List<string> ret = new List<string>();
            if (response != null && response.bindingDetail != null && response.bindingDetail.bindingTemplate != null)
                foreach (bindingTemplate2 bt in response.bindingDetail.bindingTemplate)
                {
                    //if (bt.Signature != null)
                    //    if (!VerifySignature(bt))
                    //        throw new Exception (signatureError);
                    accessPoint2 ap = bt.Item as accessPoint2;
                    if (ap != null)
                    {
                        string[] apList = parseResults(ap, authTok);
                        if (apList != null)
                            for (int k = 0; k < apList.Length; k++)
                                ret.Add(apList[k]);

                    }
                    hostingRedirector2 hr = bt.Item as hostingRedirector2;
                    if (hr != null)
                    {
                        string[] urls = DiscoverEndpointBindingKey(hr.bindingKey, authTok);
                        if (urls != null)
                            for (int i = 0; i < urls.Length; i++)
                                ret.Add(urls[i]);
                    }
                }

            return ret.ToArray();
        }
        private org.miloss.fgsms.agent.Util util = new org.miloss.fgsms.agent.Util();

        /// <summary>
        /// helper class to try and dicipher the complicated arrangements of the uddi type accessPoint
        /// </summary>
        /// <param name="ap"></param>
        /// <param name="username"></param>
        /// <param name="password"></param>
        /// <param name="discoveryEndpointAddress"></param>
        /// <returns></returns>
        private string[] parseResults(accessPoint2 ap, string authtoken)
        {
            List<string> ret = new List<string>();
            bool ok = false;
            if (!String.IsNullOrEmpty(ap.useType))
            {
                //useType is defined, now what?
                //lets try and figure out what it is
                switch (ap.useType.ToLower())
                {
                    case "endpoint":
                        //for systinet servers, they tend to have endpoints with the value
                        //equal to some java class file, why? who the hell knows, but it's against the 
                        //uddi spec and thus not really expected.
                        if (ap.Value.StartsWith("http", StringComparison.CurrentCultureIgnoreCase))
                            ret.Add(ap.Value);
                        //if (ap.Value.StartsWith("ftp", StringComparison.CurrentCultureIgnoreCase))
                        //ret.Add(ap.Value);
                        //if (ap.Value.StartsWith("smtp", StringComparison.CurrentCultureIgnoreCase))
                        //ret.Add(ap.Value);
                        // ret.Add(ap.Value);
                        ok = true;
                        break;
                    case "bindingtemplate":
                    case "hostingredirector":
                        //bindingKey
                        //another call out to the same registry
                        try
                        {
                            string[] r3 = this.DiscoverEndpointBindingKey(ap.Value, authtoken);
                            for (int k = 0; k < r3.Length; k++)
                                ret.Add(r3[k]);
                        }
                        catch (Exception ex)
                        {
                            //TODO log warning
                        }
                        ok = true;
                        break;
                    /* case "hostingredirector":
                         //another call out to a different registry
                         try
                         {
                             //remote auth not supported
                             string[] r2 = this.DiscoverEndpointKeyWord(keywords, ap.Value);
                             for (int k = 0; k < r2.Length; k++)
                                 ret.Add(r2[k]);
                         }
                         catch (Exception ex)
                         {
                             //TODO log warning
                         }

                         break;*/
                    case "wsdldeployment":

                        string[] urls = DiscoverEndpointByWSDL(ap.Value);
                        if (urls != null)
                        {
                            for (int a = 0; a < urls.Length; a++)
                                ret.Add(urls[a]);
                        }
                        ok = true;
                        break;
                    default:

                        //so basically, for systinet servers, if the useType is not of the four defined types
                        //the uddi "should" have a corresponding tmodel that matchines the useType value
                        //systinet does NOT do this

                        /*get_tModelDetail2 rt = new get_tModelDetail2();
                        rt.tModelKey = new string[1];
                        rt.tModelKey[0] = ap.useType;
                        tModelDetail2 rr = null;
                        try
                        {
                            rr = inquiry.get_tModelDetail(rt);
                        }
                        catch (Exception ex)
                        { }
                        */
                        ok = false;
                        break;
                }

            }
            if (!ok)
            {
                //this means it's an unknown useType OR
                //this means the useType is not defined. lets try to guess if the value is actually a url
                if (ap.Value.StartsWith("http", StringComparison.CurrentCultureIgnoreCase))
                    ret.Add(ap.Value);
                //if (ap.Value.StartsWith("ftp", StringComparison.CurrentCultureIgnoreCase))
                //ret.Add(ap.Value);
                //if (ap.Value.StartsWith("smtp", StringComparison.CurrentCultureIgnoreCase))
                //ret.Add(ap.Value);
                //else ignore it, log unknown type as warning
            }
            return ret.ToArray();
        }

        /// <summary>
        /// discover an url endpoint from a wsdl file. only supports anonymous access http gets
        /// </summary>
        /// <param name="URL"></param>
        /// <returns></returns>
        public string[] DiscoverEndpointByWSDL(string URL)
        {

            List<string> urls = new List<string>();
            System.Net.WebClient client = new System.Net.WebClient();
            string res = "";
            try
            {
                res = client.DownloadString(URL);
            }
            catch (Exception ex)
            {
                //todo log warning
                return null;
            }
            StringReader sw = new StringReader(res);
            System.Xml.XmlTextReader reader = new System.Xml.XmlTextReader(sw);
            while (reader.Read())
            {
                if (
                    (reader.NamespaceURI == "http://schemas.xmlsoap.org/wsdl/soap/" ||
                    reader.NamespaceURI == "http://www.w3.org/2003/01/wsdl/soap12")
                    && reader.Name == "address")
                    try
                    {
                        urls.Add(reader.GetAttribute("location"));
                    }
                    catch { }
            }
            sw.Dispose();
            return urls.ToArray();
        }


        /// <summary>
        /// get an authentication token from the uddi v3 security api
        /// </summary>
        /// <param name="username">required</param>
        /// <param name="password">optional</param>
        /// <param name="securityEndpointAddress">optional</param>
        /// <returns></returns>
        public string getAuthToken()
        {
            if (String.IsNullOrEmpty(username))
                throw new ArgumentNullException("username");


            try
            {

                get_authTokenRequest authN = new get_authTokenRequest();
                authN.get_authToken = new get_authToken1();
                authN.get_authToken.userID = username;
                authN.get_authToken.cred = util.DE(encryptedpassword);
                get_authTokenResponse authTok = securityclient.get_authToken(authN);
                return authTok.authToken.authInfo;
            }
            catch (Exception ex)
            {
                //throw new DiscoveryFailureException(ex);
                return null;
            }
        }
        private const string discoveryError = "There was an error discovering the service endpoint from the discovery service. Either the service is offline, an error occured, or access was denied.";
        private const string endpointError = "There was an error discoverying the service endpoint from the discovery service. Either the service is not registered or could not be found using the search criteria";
        private const string signatureError = "There was an error validating the UDDI signature for an element. Either the element was signed with an untrusted or invalid certificate, or the data was modified after being changed.";

        /// <summary>
        /// Discovers a service endpoint by keywords. Use (%) to indicate any value for any number of characters and an underscore (_) to indicate any value for a single character. The backslash character (\) 
        /// </summary>
        /// <param name="keywords"></param>
        /// <param name="discoveryEndpointAddress"></param>
        /// <param name="username"></param>
        /// <param name="password"></param>
        /// <param name="securityEndpointAddress"></param>
        /// <returns></returns>
        public string[] DiscoverEndpointKeyWord(string[] keywords, string authtoken)
        {
            if (keywords == null)
                throw new ArgumentNullException("keywords");



            string authTok = "";

            if (authrequired && String.IsNullOrEmpty(authtoken))
            {
                authTok = getAuthToken();
            }
            else authTok = authtoken;
            find_serviceRequest1 req = new find_serviceRequest1();
            req.find_service = new find_service2();
            req.find_service.name = new name1[keywords.Length];
            for (int i = 0; i < keywords.Length; i++)
            {
                req.find_service.name[i] = new name1();
                // ignored req.name[i].lang
                req.find_service.name[i].Value = keywords[i];
            }

            req.find_service.findQualifiers = new string[2];

            req.find_service.findQualifiers[0] = uddi_v3_findQualifiers.approximateMatch;
            req.find_service.findQualifiers[1] = uddi_v3_findQualifiers.caseInsensitiveMatch;

            if ((authTok != null) && (authrequired))
            {
                req.find_service.authInfo = authTok;
            }

            find_serviceResponse1 response = null;
            try
            {
                response = inquiryclient.find_service(req);
            }
            catch (Exception ex)
            {
                throw new Exception(discoveryError, ex);
            }
            get_serviceDetailRequest1 sreq = new get_serviceDetailRequest1();
            if (response == null || response.serviceList == null || response.serviceList.serviceInfos == null || response.serviceList.serviceInfos.Length == 0)
                return new string[] { };//no services returned

            sreq.get_serviceDetail = new get_serviceDetail2();
            sreq.get_serviceDetail.serviceKey = new string[response.serviceList.serviceInfos.Length];
            for (int k = 0; k < response.serviceList.serviceInfos.Length; k++)
            {
                sreq.get_serviceDetail.serviceKey[k] = response.serviceList.serviceInfos[k].serviceKey;
            }

            get_serviceDetailResponse1 sresponse = null;
            try
            {
                sresponse = inquiryclient.get_serviceDetail(sreq);
            }
            catch (Exception ex)
            {
                throw new Exception(discoveryError, ex);
            }
            List<string> ret = new List<string>();
            if (sresponse != null && sresponse.serviceDetail != null)
                foreach (businessService2 bs in sresponse.serviceDetail.businessService)
                {
                    //if (bs.Signature != null)
                    //    if (!VerifySignature(bs))
                    //        throw new Exception(signatureError);
                    if (bs.bindingTemplates != null)
                        foreach (bindingTemplate2 bt in bs.bindingTemplates)
                        {
                            //if (bt.Signature != null)
                            //    if (!VerifySignature(bt))
                            //        throw new Exception(signatureError);
                            accessPoint2 ap = bt.Item as accessPoint2;
                            if (ap != null)
                            {
                                string[] apList = parseResults(ap, authTok);
                                if (apList != null)
                                    for (int k = 0; k < apList.Length; k++)
                                        ret.Add(apList[k]);


                            }
                        }
                }
            return ret.ToArray();
        }







        /// <summary>
        /// discovers an endpoint by a UDDI service entity key
        /// </summary>
        /// <param name="uddiKey"></param>
        /// <param name="discoveryEndpointAddress"></param>
        /// <param name="username"></param>
        /// <param name="password"></param>
        /// <param name="securityEndpointAddress"></param>
        /// <returns></returns>
        public string[] DiscoverByServiceKey(string uddiKey, string token)
        {
            if (uddiKey == null)
                throw new ArgumentNullException("bindingKey");



            string authTok = null;
            if (authrequired && String.IsNullOrEmpty(token))
            {
                authTok = getAuthToken();
            }
            else authTok = token;



            get_serviceDetailRequest1 req = new get_serviceDetailRequest1();
            req.get_serviceDetail = new get_serviceDetail2();

            req.get_serviceDetail.serviceKey = new string[] { uddiKey };


            req.get_serviceDetail.authInfo = authTok;

            get_serviceDetailResponse1 response = null;
            try
            {
                response = inquiryclient.get_serviceDetail(req);
            }
            catch (Exception ex)
            {
                throw new Exception(discoveryError, ex);
            }

            List<string> ret = new List<string>();
            if (response == null || response.serviceDetail == null || response.serviceDetail.businessService == null || response.serviceDetail.businessService.Length == 0)
                return null;
            foreach (businessService2 bizs in response.serviceDetail.businessService)
            {
                //if (bizs.Signature != null)
                //    if (!VerifySignature(bizs))
                //        throw new Exception ( signatureError);
                if (bizs.bindingTemplates != null)
                    foreach (bindingTemplate2 bt in bizs.bindingTemplates)
                    {
                        //if (bt.Signature != null)
                        //    if (!VerifySignature(bt))
                        //        throw new Exception(signatureError);
                        accessPoint2 ap = bt.Item as accessPoint2;
                        if (ap != null)
                        {
                            string[] apList = parseResults(ap, authTok);
                            if (apList != null)
                                for (int k = 0; k < apList.Length; k++)
                                    ret.Add(apList[k]);

                        }
                    }
            }
            return ret.ToArray();
        }


        public void Dispose()
        {
            if (inquiryclient != null)
            {
                ((IClientChannel)inquiryclient).Close();
                ((IClientChannel)inquiryclient).Dispose();
            }
            if (securityclient != null)
            {
                ((IClientChannel)securityclient).Close();
                ((IClientChannel)securityclient).Dispose();
            }
        }
    }
    public class UddiPublisher 
    {
        public UddiPublisher(string publishurl, string securityurl, string username, string encryptedPassword, bool authrequired)
        {
            publishclient = LoaderP(publishurl);
            if (authrequired)
            {
                securityclient = LoaderS(securityurl);
                this.authrequired = authrequired;
                this.username = username;
                this.encryptedpassword = encryptedPassword;
            }
        }
        private string username;
        private string encryptedpassword;
        private bool authrequired = false;
        Util util = new Util();
        private UDDI_Publication_PortType publishclient = null;
        private UDDI_Security_PortType securityclient = null;
        private UDDI_Publication_PortType LoaderP(string url)
        {
            BasicHttpBinding b = null;
            if (url.StartsWith("https://", StringComparison.CurrentCultureIgnoreCase))
                b = new BasicHttpBinding(BasicHttpSecurityMode.Transport);
            else b = new BasicHttpBinding(BasicHttpSecurityMode.None);
            b.MaxBufferSize = Int32.MaxValue;
            b.MaxReceivedMessageSize = Int32.MaxValue;
            b.MaxBufferPoolSize = Int32.MaxValue;
            b.TransferMode = TransferMode.Buffered;
            b.Name = "UddiBinding";
            b.ReaderQuotas.MaxArrayLength = Int32.MaxValue;
            b.ReaderQuotas.MaxBytesPerRead = Int32.MaxValue;
            b.ReaderQuotas.MaxDepth = Int32.MaxValue;
            b.ReaderQuotas.MaxNameTableCharCount = Int32.MaxValue;
            b.ReaderQuotas.MaxStringContentLength = Int32.MaxValue;
            b.ReceiveTimeout = new TimeSpan(0, 0, 30);
            b.SendTimeout = new TimeSpan(0, 0, 30);
            b.UseDefaultWebProxy = true;
            b.TextEncoding = Encoding.UTF8;
            b.OpenTimeout = new TimeSpan(0, 0, 5);
            //b.Namespace = "urn:org.miloss.FGSMS.services.interfaces.policyConfiguration";
            b.MessageEncoding = WSMessageEncoding.Text;
            b.CloseTimeout = new TimeSpan(0, 0, 5);

            ChannelFactory<UDDI_Publication_PortType> factory =
                new ChannelFactory<UDDI_Publication_PortType>(b, url);
            UDDI_Publication_PortType polservice = factory.CreateChannel();

            return polservice;
        }
        private UDDI_Security_PortType LoaderS(string url)
        {
            BasicHttpBinding b = null;
            if (url.StartsWith("https://", StringComparison.CurrentCultureIgnoreCase))
                b = new BasicHttpBinding(BasicHttpSecurityMode.Transport);
            else b = new BasicHttpBinding(BasicHttpSecurityMode.None);
            b.MaxBufferSize = Int32.MaxValue;
            b.MaxReceivedMessageSize = Int32.MaxValue;
            b.MaxBufferPoolSize = Int32.MaxValue;
            b.TransferMode = TransferMode.Buffered;
            b.Name = "UddiBinding";
            b.ReaderQuotas.MaxArrayLength = Int32.MaxValue;
            b.ReaderQuotas.MaxBytesPerRead = Int32.MaxValue;
            b.ReaderQuotas.MaxDepth = Int32.MaxValue;
            b.ReaderQuotas.MaxNameTableCharCount = Int32.MaxValue;
            b.ReaderQuotas.MaxStringContentLength = Int32.MaxValue;
            b.ReceiveTimeout = new TimeSpan(0, 0, 30);
            b.SendTimeout = new TimeSpan(0, 0, 30);
            b.UseDefaultWebProxy = true;
            b.TextEncoding = Encoding.UTF8;
            b.OpenTimeout = new TimeSpan(0, 0, 5);
            //b.Namespace = "urn:org.miloss.FGSMS.services.interfaces.policyConfiguration";
            b.MessageEncoding = WSMessageEncoding.Text;
            b.CloseTimeout = new TimeSpan(0, 0, 5);

            ChannelFactory<UDDI_Security_PortType> factory =
                new ChannelFactory<UDDI_Security_PortType>(b, url);
            UDDI_Security_PortType polservice = factory.CreateChannel();

            return polservice;
        }
        public string PublishBusiness(string name)
        {
            try
            {
                get_authTokenRequest r = new get_authTokenRequest();
                r.get_authToken = new get_authToken1();
                r.get_authToken.cred = util.DE(encryptedpassword);
                r.get_authToken.userID = username;
                get_authTokenResponse res = securityclient.get_authToken(r);

                save_businessRequest
                    req = new save_businessRequest();
                req.save_business = new save_business1();
                req.save_business.businessEntity = new businessEntity2[1];
                req.save_business.businessEntity[0] = new businessEntity2();
                req.save_business.businessEntity[0].name = new name1[1];
                req.save_business.businessEntity[0].name[0] = new name1();
                req.save_business.businessEntity[0].name[0].Value = name;
                req.save_business.authInfo = res.authToken.authInfo;
                get_businessDetailResponse1 res2 = publishclient.save_business(req);
                return res2.businessDetail.businessEntity[0].businessKey;
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        public string PublishService(string name, string bizkey)
        {
            try
            {
                get_authTokenRequest r = new get_authTokenRequest();
                r.get_authToken = new get_authToken1();
                r.get_authToken.cred = util.DE(encryptedpassword);
                r.get_authToken.userID = username;
                get_authTokenResponse res = securityclient.get_authToken(r);

                save_serviceRequest req = new save_serviceRequest();
               

                req.save_service = new save_service1();
                req.save_service.authInfo = res.authToken.authInfo;
                req.save_service.businessService = new businessService2[1];
                req.save_service.businessService[0] = new businessService2();
                req.save_service.businessService[0].businessKey = bizkey;
                req.save_service.businessService[0].name = new name1[1];
                req.save_service.businessService[0].name[0] = new name1();
                req.save_service.businessService[0].name[0].Value = name;
                get_serviceDetailResponse1 res2 = publishclient.save_service(req);
                return res2.serviceDetail.businessService[0].serviceKey;
             
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        public void PublishBinding(string url, string serviceKey)
        {
            try
            {
                get_authTokenRequest r = new get_authTokenRequest();
                r.get_authToken = new get_authToken1();
                r.get_authToken.cred = util.DE(encryptedpassword);
                r.get_authToken.userID = username;
                get_authTokenResponse res = securityclient.get_authToken(r);

                save_bindingRequest req = new save_bindingRequest();
                req.save_binding = new save_binding1();
                req.save_binding.authInfo = res.authToken.authInfo;
                req.save_binding.bindingTemplate = new bindingTemplate2[1];
                req.save_binding.bindingTemplate[0] = new bindingTemplate2();
                req.save_binding.bindingTemplate[0].serviceKey = serviceKey;
                accessPoint2 ap = new accessPoint2();
                ap.useType = "endpoint";
                ap.Value = url;
                req.save_binding.bindingTemplate[0].Item = ap;


                find_bindingResponse1 res2 = publishclient.save_binding(req);
               


            }
            catch (Exception ex)
            {
                throw ex;
            }
        }


        public void Dispose()
        {
            
        }
    }
}
