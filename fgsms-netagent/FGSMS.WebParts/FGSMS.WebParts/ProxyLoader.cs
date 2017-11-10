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
using System.Collections;
using System.Collections.Generic;
using System.Text;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.WebControls.WebParts;
using System.Web.UI.HtmlControls;
using System.Web.Configuration;
using System.Diagnostics;
using System.ComponentModel;
using System.Xml.Serialization;
using System.Runtime.InteropServices;
using System.Web;
//using Microsoft.SharePoint;
using System.Web.Caching;
using org.miloss.fgsms.agent;
using System.ServiceModel.Channels;
using System.ServiceModel;
using System.Net;
using System.Configuration;
using System.Security.Cryptography.X509Certificates;


namespace Fgsms.WebParts
{
    /// <summary>
    /// provides helper functions that create client proxies for all FGSMS services
    /// it is designed to be used from trusted processes meaning
    /// From SharePoint/IIS, it is assumed that the user is already logged in and authenticated, therefore
    /// credentials using
    /// </summary>
    public static  class ProxyLoader
    {
        public static statusServiceBinding GetSSProxy(string url, string username, string password, ConfigLoader.AuthMode authMode, string pkiinfo)
        {

            ServicePointManager.Expect100Continue = false;
            statusServiceBinding r = new statusServiceBinding();
            r.Url = url;
            switch (authMode)
            {
                case ConfigLoader.AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(username, (password));
                    break;
                case ConfigLoader.AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(pkiinfo));
                    break;
            }
            return r;

        
        }
        
        public static  PCSBinding GetPCSProxy(string url, string username, string password, ConfigLoader.AuthMode authMode, string pkiinfo)
        {
            ServicePointManager.Expect100Continue = false;
            PCSBinding r = new PCSBinding();
            r.Url = url;
            switch (authMode)
            {
                case ConfigLoader.AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(username, (password));
                    break;
                case ConfigLoader.AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(pkiinfo));
                    break;
            }
            return r;

        }
        public static dataAccessService GetDASProxy(string url, string username, string password, ConfigLoader.AuthMode authMode, string pkiinfo)
        {
            ServicePointManager.Expect100Continue = false;
            dataAccessService r = new dataAccessService();
            r.Url = url;
            switch (authMode)
            {
                case ConfigLoader.AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(username, (password));
                    break;
                case ConfigLoader.AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(pkiinfo));
                    break;
            }
            return r;
        
        }
		

        private static System.Security.Cryptography.X509Certificates.X509Certificate2 FindCert(string pkiinfo)
        {
            System.Security.Cryptography.X509Certificates.X509Store store = new System.Security.Cryptography.X509Certificates.X509Store(System.Security.Cryptography.X509Certificates.StoreName.My, System.Security.Cryptography.X509Certificates.StoreLocation.LocalMachine);
            store.Open(System.Security.Cryptography.X509Certificates.OpenFlags.ReadOnly);
            X509Certificate2Collection col = store.Certificates.Find(System.Security.Cryptography.X509Certificates.X509FindType.FindBySerialNumber, pkiinfo, true);
            store.Close();
            if (col != null && col.Count > 0)
            {
                //if (col.Count > 1)

                return col[0];
            }
            throw new ConfigurationErrorsException("no certificates were found matching that serial number");
        }


        public static reportingService GetRSProxy(string url, string username, string password, ConfigLoader.AuthMode authMode, string pkiinfo)
        {

            ServicePointManager.Expect100Continue = false;
            reportingService r = new reportingService();
            r.Url = url;
            switch (authMode)
            {
                case ConfigLoader.AuthMode.usernamePassword:
                    r.Credentials = new NetworkCredential(username, (password));
                    break;
                case ConfigLoader.AuthMode.PKI:
                    r.ClientCertificates.Add(FindCert(pkiinfo));
                    break;
            }
            return r;
            
        }
        
    }
}
