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
using System.Threading;
using System.Reflection;



namespace Fgsms.WebParts
{
    /// <summary>
    /// Provides a handy base class for rendering webpart info using ajax callbacks
    /// do not override renderwebpart or onload
    /// </summary>
    public abstract class AjaxWebPart : System.Web.UI.WebControls.WebParts.WebPart, System.Web.UI.ICallbackEventHandler
    {
        /// <summary>
        /// This will hold the name of your div tag
        /// </summary>
        protected string datadiv; //
        /// <summary>
        /// This will hold the data that is returned via ajax…
        /// </summary>
        protected string ajaxdata; //
        /// <summary>
        /// used for webpart to webpart ajax data sharing
        /// </summary>
        protected string hiddenDiv; //



        /// <summary>
        /// this will register the call back scripts, do not override
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
            String callbackScript2 = "window.onload = CallServerStartup(); function CallServerStartup(arg, context) {  var mydiv = document.getElementById('" +
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

            base.OnLoad(e);

            //ReceiveServerData


        }


        protected const string WARNING_IMG = "/_layouts/images/ERROR.GIF";



        /// <summary>
        /// does the rendering, overwrite only if you need user input
        /// </summary>
        /// <param name="writer"></param>
        protected override void Render(HtmlTextWriter writer)
        {
            if (this.WebPartManager.DisplayMode.AllowPageDesign)
            {
                writer.Write("<img src='" + WARNING_IMG + "'>This web part displays dynamic content and is not available in design mode.");
            }
            else if (this.IsStandalone || WebPartManager.GetCurrentWebPartManager(this.Page)==null)
            {
                writer.Write("This is preview mode");
            }
            else
            {
                writer.Write("<div id=\"" + datadiv + "\">" + pleaseWaitBlock() + "</div>");
                //this.RaiseCallbackEvent("");
                //writer.Write(ajaxdata);

            }
        }

        /// <summary>
        /// do not override
        /// </summary>
        /// <returns></returns>
        public string pleaseWaitBlock()
        {
            hiddenDiv = this.ClientID + "hidden";
            this.datadiv = this.ClientID + "content";  //Uses the client side id of the web part instance + a name we give it
            return "<div id=\"" + this.datadiv + "\">Loading content, Please wait...<br><img src=\"/_layouts/images/kpiprogressbar.GIF\" width=\"150\">" +
                 "</div><div style=\"display:none;\" id=\"" + hiddenDiv + "\">hidden content</div>";
        }

        /// <summary>
        ///  do not override, javascript encoded version of please wait
        /// </summary>
        /// <returns></returns>
        public string pleaseWaitBlockjs()
        {
            this.datadiv = this.ClientID + "content";  //Uses the client side id of the web part instance + a name we give it
            return "\"<div id=\\\"" + this.datadiv + "\\\">Loading content, Please wait...<br><img src=\\\"/_layouts/images/kpiprogressbar.GIF\\\" width=\\\"150\\\">" +

                "</div> <div style=\\\"display:none;\\\" id=\\\"" + hiddenDiv + "\\\">hidden content</div>\"";
        }

        /// <summary>
        /// do not override, this returns your data
        /// </summary>
        /// <returns></returns>
        public string GetCallbackResult()
        {
            return this.ajaxdata;
        }

        /// <summary>
        /// do your rendering here, required.
        /// The business logic
        /// 
        /// set the result this.ajaxdata
        /// 
        /// use HandleError(Exception, bool) for simplier error handling
        /// </summary>
        /// <param name="eventArgument">Something that you pass on to your event call backs, such as buttons and whatnot.</param>
        public abstract void RaiseCallbackEvent(string eventArgument);

        /// <summary>
        /// Overrite this to set the logging name, if you're using the HandleError function
        /// </summary>
        public abstract string LogName
        {
            get;
        }
      

        /// <summary>
        /// A simple helper function to handle error messages
        /// Exception messages and stack traces are processed recursively and will be attempted to be written to the following:
        /// <list type="bullet"  >
        /// <listheader>
        /// <term>Log</term>
        /// <description>Description</description>
        /// </listheader>
        /// <item><term> System.Diagnoistcs.TraveLog</term><description>if it's not configured, the error state will be lost</description></item>
        /// <item><term> Windows Application EventLog</term><description>will not always work depending on IA restrictions</description></item>
        /// <item><term> SharePoint Diagnostics Log</term><description>usually works</description></item>
        /// </list>
        /// Logs are logged under the name designated by LogName
        /// </summary>
        /// <param name="ex"></param>
        /// <param name="setajaxdata">if true, the returned ajaxdata will be set to a canned message along with the exceptions message</param>
        /// <returns>true if at least one logger was written to</returns>
        public bool HandleError(Exception ex, bool setajaxdata)
        {
            bool ret = false;
            string originalerror = ex.Message;
            if (ex == null)
                return ret;
            string s = "";
            while (ex != null)
            {
                s = ex.Message + Environment.NewLine + ex.StackTrace + Environment.NewLine;
                ex = ex.InnerException;
            }
            //System.Reflection.Assembly.GetCallingAssembly().FullName

            try
            {
                if (log == null)
                    log = new EventLog("Application");
                log.Source = LogName;
                log.WriteEntry("Error caught: " + s, EventLogEntryType.Error);
                ret = true;
            }
            catch { }
            try
            {
                if (ts == null)
                    ts = new TraceSource(LogName);
                ts.TraceEvent(TraceEventType.Error, 0, "Error caught: " + s, EventLogEntryType.Error);
                ret = true;
            }
            catch { }

            //this is dynamically loaded for cases of running this outside of sharepoint
            //feel free to replace it with this
           /* try
            {
                Microsoft.Office.Server.Diagnostics.PortalLog.LogString(LogName + "Error caught: " + s);
                ret = true;
            }
            catch { }*/
            try
            {
                if (assembly == null)
                {
                    assembly = Assembly.LoadFrom("Microsoft.Office.Server.dll");
                }
                foreach (Type type in assembly.GetTypes())
                {
                    if (type.IsClass == true)
                    {
                        if (type.FullName.EndsWith("Microsoft.Office.Server.Diagnostics.PortalLog"))
                        {
                            mosslog = type;
                        }
                    }
                }
                if (assembly != null && mosslog != null)
                {
                    // create an instance of the object
                    object ClassObj = Activator.CreateInstance(mosslog);
                    // Dynamically Invoke the method
                    object Result = mosslog.InvokeMember("Microsoft.Office.Server.Diagnostics.PortalLog.LogString",
                      BindingFlags.Default | BindingFlags.InvokeMethod,
                           null,
                           ClassObj,
                           new Object[] { LogName + "Error caught: " + s }
                           );
                 ret = true;
                }

                
            }
            catch { }


            if (setajaxdata)
            {
                if (ret)
                    ajaxdata = "<img src=\"" + WARNING_IMG + "\">" + "There was an error processing your request and it has been logged for review. Technical Details: " + System.Web.HttpUtility.HtmlEncode(originalerror);
                else
                    ajaxdata = "<img src=\"" + WARNING_IMG + "\">" + "There was an error processing your request. Unfortunately it could not be logged. Please report this to the administrator of this website. Technical Details: " + System.Web.HttpUtility.HtmlEncode(originalerror);
            }
            return ret;
        }
        private TraceSource ts = null;
        private EventLog log = null;
        private Assembly assembly = null;
        private Type mosslog = null;



    }
}
