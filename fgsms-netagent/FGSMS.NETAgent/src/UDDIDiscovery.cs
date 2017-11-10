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
using org.miloss.FGSMS.discovery;

namespace org.miloss.fgsms.agent
{
    /// <summary>
    /// Provides a discovery via UDDI
    /// </summary>
    public class UDDIDiscovery: IDisposable
    {
        private ConfigLoader config;
        /// <summary>
        /// loads config data from the configloader which comes from the machine.config file
        /// Besure to call Dispose when finished with this object
        /// </summary>
        /// <param name="config"></param>
        public UDDIDiscovery(ConfigLoader config)
        {
            this.config = config;
            uddi = new uddiv3(config.UddiInquiryUrl, config.UddiSecurityUrl, config.UddiUsername, config.UddiEncryptedPassword, config.UddiAuthRequired);

        }
        uddiv3 uddi = null;
        /// <summary>
        /// returns a list of urls to the PCS
        /// </summary>
        /// <returns></returns>
        public List<string> GetPCSURLs()
        {
            List<string> l = new List<string>();
            if (config.UddiEnabled)
            {
                string[] urls= null;
               
                switch (config.uddiFindType)
                {
                    case ConfigLoader.UddiFindType.EndpointBindingKey:
                        urls = uddi.DiscoverEndpointBindingKey(config.UddiPCSLookup, null);
                        break;
                    case ConfigLoader.UddiFindType.EndpointKeyWord:
                        urls = uddi.DiscoverEndpointKeyWord(new string[] {config.UddiPCSLookup}, null);
                        break;
                    case ConfigLoader.UddiFindType.ServiceKey:
                        urls = uddi.DiscoverByServiceKey(config.UddiPCSLookup, null);
                        break;
                }
                if (urls != null && urls.Length > 0)
                    for (int i = 0; i < urls.Length; i++)
                        l.Add(urls[i]);
                //return new List<string>().AddRange(urls);
            }
            
            return l;
        }

        /// <summary>
        /// returns a list of urls for the DCS service
        /// </summary>
        /// <returns></returns>
        public List<string> GetDCSURLs()
        {
            List<string> l = new List<string>();
            if (config.UddiEnabled)
            {
                string[] urls = null;
               
                switch (config.uddiFindType)
                {
                    case ConfigLoader.UddiFindType.EndpointBindingKey:
                        urls = uddi.DiscoverEndpointBindingKey(config.UddiPCSLookup, null);
                        break;
                    case ConfigLoader.UddiFindType.EndpointKeyWord:
                        urls = uddi.DiscoverEndpointKeyWord(new string[] { config.UddiDCSLookup }, null);
                        break;
                    case ConfigLoader.UddiFindType.ServiceKey:
                        urls = uddi.DiscoverByServiceKey(config.UddiDCSLookup, null);
                        break;
                }
                if (urls != null && urls.Length > 0)
                    for (int i = 0; i < urls.Length; i++)
                        l.Add(urls[i]);
                //return new List<string>().AddRange(urls);
            }
            return l;
        }
 
        /*
        public List<string> GetSSURLs()
        {
            return new List<string>();
        }

        public void PublishPCS(string[] urls)
        {
           // uddi.
        }

        public void PublishDCS(string[] urls)
        {
        }*/

        public void Dispose()
        {
            if (uddi!=null)
            uddi.Dispose();
            
            GC.SuppressFinalize(this);
        }

        public void Dispose(Boolean x)
        {
            Dispose();
        }
    }
}
