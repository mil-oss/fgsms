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

namespace Fgsms.WebParts
{
    /// <summary>
    /// This provides a base class for extension that uses ajax callbacks to get data and render it in a web part and automatically posts back with the selected interval
    /// be sure to add a guid to this class. 
    /// </summary>
    /// [System.Runtime.InteropServices.Guid("guid goes here")]
    /// 
    /// Implements Microsoft.SharePoint.WebPartPages.WebPart
    /// AjaxWebPart
    /// Microsoft.SharePoint.WebPartPages.WebPart
    /// System.Web.UI.ICallbackEventHandler
    /// 
    /// 
    /// Override RaiseCallbackEvent is required
    /// 
    /// 
    /// Set the variable 'ajaxdata' with the content of your data.
    /// 
    /// 
    /// Override RenderWebPart if necessary
    /// 
    [XmlRoot(Namespace = "org.miloss.FGSMS")]
    public abstract class FGSMSReloaderWebPart : AjaxAutoReloaderWebPart
    {
        protected const string greenlight = "/_layouts/images/kpipepperalarm-0.gif";
        protected const string redlight = "/_layouts/images/kpipepperalarm-2.gif";
        protected const string orangelight = "/_layouts/images/kpipepperalarm-1.gif";

        protected const string HOME_IMG = "/_layouts/images/HOME.GIF";
        protected const string SEARCH_IMG = "/_layouts/images/SEARCH.GIF";
        protected const string SERVERINFO_IMG = "/_layouts/images/PAGA_LOGO.GIF";
        protected const string TREEVIEW_IMG = "/_layouts/images/PORT_MAP.GIF";
        protected const string ERROR_IMG = "/_layouts/images/error32by32.gif";
        protected const string EMAIL_IMG = "/_layouts/images/MENUOUTL.GIF";



        protected ConfigLoader.AuthMode authMode = ConfigLoader.AuthMode.usernamePassword;
        [Personalizable(PersonalizationScope.Shared),
       WebBrowsable(true),
       WebDisplayName("FGSMS Authentication Style"),
       Category("Configuration"), Description("")]
        public ConfigLoader.AuthMode pubauthmod
        {
            get { return authMode; }
            set { authMode = (ConfigLoader.AuthMode)(value); }
        }



        protected string username = "";
        [Personalizable(PersonalizationScope.Shared),
       WebBrowsable(true),
       WebDisplayName("FGSMS Username"),
       Category("Configuration"), Description("")]
        public string pubusername
        {
            get { return username; }
            set { username = (string)(value); }
        }

        protected Util u = new Util();

        protected string password = "";
        [Personalizable(PersonalizationScope.Shared),
       WebBrowsable(true),
       WebDisplayName("FGSMS Password"),
       Category("Configuration"), Description("")]
        public string pubpass
        {
            get { return password; }
            set { password = ((string)(value)); }
        }

        protected string pkiinfo = "";
        [Personalizable(PersonalizationScope.Shared),
       WebBrowsable(true),
       WebDisplayName("The serial number of the certificate to use for PKI authentication"),
       Category("Configuration"), Description("")]
        public string pubpkiinfo
        {
            get { return pkiinfo; }
            set { pkiinfo = (string)(value); }
        }


        protected string dasurl = "";
        protected string ssurl = "";
        protected string rsurl = "";
        protected string pcsurl = "";

        [Personalizable(PersonalizationScope.Shared),
       WebBrowsable(true),
       WebDisplayName("FGSMS Reporting Service"),
       Category("Configuration"), Description("The URL to the service.")]
        public string pubrsurl
        {
            get { return rsurl; }
            set { rsurl = (string)(value); }
        }


        [Personalizable(PersonalizationScope.Shared),
          WebBrowsable(true),
          WebDisplayName("FGSMS Policy Configuration Service"),
          Category("Configuration"), Description("The URL to the service.")]
        public string pubpcsurl
        {
            get { return pcsurl; }
            set { pcsurl = (string)(value); }
        }


        [Personalizable(PersonalizationScope.Shared),
          WebBrowsable(true),
          WebDisplayName("FGSMS Data Access Service"),
          Category("Configuration"), Description("The URL to the service.")]
        public string pubdasurl
        {
            get { return dasurl; }
            set { dasurl = (string)(value); }
        }

        [Personalizable(PersonalizationScope.Shared),
         WebBrowsable(true),
         WebDisplayName("FGSMS Status Service"),
         Category("Configuration"), Description("The URL to the service.")]
        public string pubstatusurl
        {
            get { return ssurl; }
            set { ssurl = (string)(value); }
        }

        /// <summary>
        /// converts a duration/timespan to a more readable format
        /// 1d 2hr 5min 26sec
        /// </summary>
        /// <param name="ts"></param>
        /// <returns></returns>
        public string DurationToString(TimeSpan ts)
        {
            if (ts == null)
                return "";
            string s = "";
            if (ts.TotalDays > 0)
                s = ts.TotalDays + "d ";
            if (ts.TotalHours > 0)
                s = ts.TotalHours + "hr ";
            if (ts.TotalMinutes > 0)
                s = ts.TotalMinutes + "m ";
            if (ts.TotalSeconds > 0)
                s = ts.TotalSeconds + "s";
            return s.Trim();
        }
    }

}
