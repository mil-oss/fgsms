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
using System.Text;
using DnDns.Records;
using System.Net.NetworkInformation;

namespace org.miloss.fgsms.agent
{
    /// <summary>
    /// Provides service discovery via DNS TXT records. Uses the code from open source DnDNS found on codeplex.com
    /// </summary>
    public static class DNSDiscovery
    {
        private static List<String> getSuffixes()
        {
            Logger.debug("Probing for network dns suffixes");
            List<String> suffix = new List<string>();

            NetworkInterface[] a = NetworkInterface.GetAllNetworkInterfaces();
            foreach (NetworkInterface ni in a)
            {
                if (ni.OperationalStatus == OperationalStatus.Up)
                {
                    IPInterfaceProperties ip = ni.GetIPProperties();
                    // if (ip.IsDnsEnabled) this is always false?
                    {
                        Logger.debug(ni.Name + " " + ip.DnsSuffix);
                        if (!String.IsNullOrEmpty(ip.DnsSuffix))
                        {
                            suffix.Add(ip.DnsSuffix);
                        }
                    }
                }
            }
            return suffix;
        }

        private static List<String> DnsProbeTXT(string value)
        {
            List<String> ret = new List<string>();
            DnDns.Query.DnsQueryRequest r = new DnDns.Query.DnsQueryRequest();
            DnDns.Query.DnsQueryResponse res = null;
            try
            {
                res = r.Resolve(value, DnDns.Enums.NsType.TXT, DnDns.Enums.NsClass.INET, System.Net.Sockets.ProtocolType.Udp);
            }
            catch (Exception ex)
            {

            }
            if (res == null)
                return ret;
            if (res.Answers == null)
                return ret;

            foreach (IDnsRecord r1 in res.Answers)
            {
                string s = r1.Answer;
                try
                {
                    if (!String.IsNullOrEmpty(s))
                    {
                        s = s.Trim();
                        if (!String.IsNullOrEmpty(s))
                        {
                            Uri u = null;
                            u = new Uri(s);
                            ret.Add(s);
                        }
                    }
                }
                catch
                {
                    Logger.warn("DNS Discovery, throwing out the value " + r1.Answer + " as it is not a valid uri");
                }
            }

            return ret;

        }
        public enum DiscoverType { DAS, PCS, RS, ARS, ACS, ACSA, SS, DCS }
        /// <summary>
        /// returns a list of URLs to the corresponding FGSMS services or an empty list if none are found
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static List<String> DiscoverViaDNS(DiscoverType type)
        {
            return discover("FGSMS-" + type.ToString().ToLower());
        }


        /// <summary>
        /// returns a list of URLs to any corresponding service or an empty list if none are found
        /// this function is not used within FGSMS but is provided free of charge
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static List<String> DiscoverViaDNS(String key)
        {
            return discover(key);
        }


        private static List<String> discover(string key)
        {
            List<String> ret = new List<string>();
            List<String> s = getSuffixes();
            for (int i = 0; i < s.Count; i++)
            {
                ret.AddRange(DnsProbeTXT(key + "." + s[i]));
            }
            return ret;
        }
    }
}
