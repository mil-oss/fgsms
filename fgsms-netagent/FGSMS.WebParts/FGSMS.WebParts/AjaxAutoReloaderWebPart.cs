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
using System.Collections;
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
    /// Provides a handy base class for rendering webpart info using ajax callbacks that automatically polls back to refresh data
    /// do not overrwrite renderwebpart or onload
    /// </summary>
    public abstract class AjaxAutoReloaderWebPart: AjaxWebPart
    {
        /// <summary>
        /// this will register the call back scripts for a part that automatically polls back, do not override
        /// </summary>
        /// <param name="e"></param>
        protected override void OnLoad(EventArgs e)
        {

            this.datadiv = this.ClientID + "content";
            hiddenDiv = this.ClientID + "hidden";
            ClientScriptManager cm = Page.ClientScript;
            String cbReference = cm.GetCallbackEventReference(this, "arg", "ReceiveServerData", "");
            String cbReference2 = cm.GetCallbackEventReference(this, "arg", "StartReceiveServerData", "");

            String callbackScript = "function CallServer(arg, context) {  var mydiv = document.getElementById('" + datadiv +
                    @"');     mydiv.innerHTML = " + pleaseWaitBlockjs() + "; " + cbReference + "; }";
            String callbackScript2 = "window.onload = CallServerStartup(); window.setInterval('CallServer();', " + interval + "); function CallServerStartup(arg, context) {  var mydiv = document.getElementById('" +
                datadiv + @"');     mydiv.innerHTML = " + pleaseWaitBlockjs() + "; " + cbReference2 + "; }";

            cm.RegisterClientScriptBlock(this.GetType(), "CallServer", callbackScript, true);
            cm.RegisterStartupScript(this.GetType(), "CallServerStartup", callbackScript2, true);


            string js1 = @"function ReceiveServerData(arg, context) { 
                var mydiv = document.getElementById('" + datadiv + @"'); 
                mydiv.innerHTML = arg; 
                }";
            string js2 = @"function StartReceiveServerData(arg, context) { 
                var mydiv = document.getElementById('" + datadiv + @"'); 
                mydiv.innerHTML = arg; 
                
                }";
            //window.setTimeout('ReceiveServerData(arg, context);', 30000);
            cm.RegisterClientScriptBlock(this.GetType(), "ReceiveServerData", js1, true);
            cm.RegisterStartupScript(this.GetType(), "StartReceiveServerData", js2, true);

            //cm.RegisterClientScriptInclude("uddisearch.js", "~/wpresources/UddiBrowserAjaxWebPart2/uddisearch.js");

            //base.OnLoad(e);

            //ReceiveServerData


        }



        /// <summary>
        /// represents the time interval from which to refresh the screen, aka the callback event
        /// </summary>
        protected int interval = 30000;
        [Personalizable(PersonalizationScope.Shared),
       WebBrowsable(true),
       WebDisplayName("Interval"),
       Category("Configuration"), Description("Number of milliseconds to wait before refreshing this web part's data. The default is 30000 which is 30 seconds.")]
        public int Interval
        {
            get { return interval; }
            set
            {
                try
                {
                    interval = (int)(value);
                }
                catch (Exception ex)
                {
                    interval = 30000;
                }

            }
        }


    }
}
