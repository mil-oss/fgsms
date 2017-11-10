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
using System.Web.Caching;
using org.miloss.fgsms.agent;
using System.ServiceModel.Channels;
using System.ServiceModel;
using System.Net;
using System.Threading;

namespace Fgsms.WebParts
{
    /*
    [Guid("E0AA09AD-C2B1-4286-B8CF-CD7F44107817")]
    public class ExampleAuthReloaderWebPart : AjaxAutoReloaderWebPart
    {
        public override void RaiseCallbackEvent(string eventArgument)
        {
            this.ajaxdata = "Hello World";

        }

        public override string LogName
        {
            get
            {
                return "ExampleAuthReloaderWebPart";
            }
            
        }
    }

    [Guid("19AC1C8C-C25E-498D-B090-57D88B62048B")]
    public class ExampleWebPart : AjaxWebPart
    {
        /// <summary>
        /// get long running operation
        /// </summary>
        /// <param name="eventArgument"></param>
        public override void RaiseCallbackEvent(string eventArgument)
        {
            Thread.Sleep(5000);
            this.ajaxdata = "Hello World";

        }

        public override string LogName
        {
            get { return "ExampleWebPart"; }
        }
    }*/

    /// <summary>
    /// FGSMS 6.x web part
    /// Provides data from the FGSMS status service, which basically provides red/green light status indicators
    /// data is rendered via a callback handler which escentially makes this an ajax enabled webpart
    /// 
    /// This is a DEMO web part. not ment for production use due to the usage of a shared password which is browser viewable
    /// </summary>
    [Guid("0A5FE175-08BC-44C7-A194-727072C09EA7")]
    public class RedLightGreenLightWebPart : FGSMSReloaderWebPart
    {
        public override void RaiseCallbackEvent(string eventArgument)
        {
            statusServiceBinding ss = null;
            ajaxdata = "";
            try
            {
                ss = ProxyLoader.GetSSProxy(this.ssurl, username, password, authMode, pkiinfo);
                if (authMode == ConfigLoader.AuthMode.PKI)
                {
                    ss.useDoubleHopAuthWithPKI = true;
                    ss.actual_username = HttpContext.Current.User.Identity.Name;
                }
                GetStatusRequestMsg req = new GetStatusRequestMsg();
                
                req.classification = new SecurityWrapper();
                req.classification.caveats = "none";
                req.classification.classification = ClassificationType.U;
                GetStatusResponseMsg[] res = ss.GetAllStatus(req);
                if (res != null && res!= null && res.Length > 0)
                {
                    ajaxdata += "<table><th>Status</th><th>URI</th><th>Detail</th><th>Last Check In</th></tr>";
                    for (int i = 0; i < res.Length; i++)
                    {
                        TimeSpan ts = new TimeSpan(DateTime.Now.Ticks - res[i].TimeStamp.Ticks);
                        ajaxdata += "<tr><td>";
                        if (res[i].Operational)
                        {
                            if (ts.TotalHours > 1.0)
                                ajaxdata += "<img src=\"" + redlight + "\">";
                            else if (ts.TotalMinutes > 10)
                            {
                                ajaxdata += "<img src=\"" + orangelight + "\">";
                            }
                            else
                            {
                                ajaxdata += "<img src=\"" + greenlight + "\">";
                            }
                        }
                        else
                        {
                            ajaxdata += "<img src=\"" + redlight + "\">";
                        }
                        ajaxdata += "</td><td>";
                        ajaxdata += System.Web.HttpUtility.HtmlEncode(res[i].URI) +
                            //"</td><td>" + (res.GetStatusResponseResult[i].Operational ? "OK" : "NG") +
                            "</td><td>" + System.Web.HttpUtility.HtmlEncode(res[i].Message) +
                            "</td><td>";

                        if (ts.TotalHours > 1.0)

                            ajaxdata += "<font color=\"red\">" +
                                 DurationToString(ts)// res.GetStatusResponseResult[i].TimeStamp.ToString("o") 
                                + "</font></td></tr>";
                        else if (ts.TotalMinutes > 10)
                        {
                            ajaxdata += "<font color=\"#FF7D40\">" +
                             DurationToString(ts)//res.GetStatusResponseResult[i].TimeStamp.ToString("o") 
                             + "</font></td></tr>";
                        }
                        else ajaxdata += DurationToString(ts) +
                                //res.GetStatusResponseResult[i].TimeStamp.ToString("o") + 
                            "</td></tr>";

                    }
                    ajaxdata += "</table>";
                }
            }
            catch (Exception ex)
            {
                HandleError(ex, true);
            }
            finally
            {
               
            }


        }
        
        public override string LogName
        {
            get { return "RedLightGreenLightWebPart"; }
        }
    }
}
