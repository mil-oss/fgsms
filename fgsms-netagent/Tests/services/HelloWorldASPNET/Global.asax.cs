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
using System.Web;
using System.Web.Security;
using System.Web.SessionState;
using System.Reflection;
using System.Diagnostics;

namespace HelloWorldASPNET2
{
    public class Global : System.Web.HttpApplication
    {

        protected void Application_Start(object sender, EventArgs e)
        {

        }

        protected void Session_Start(object sender, EventArgs e)
        {

        }

        protected void Application_BeginRequest(object sender, EventArgs e)
        {

        }

        protected void Application_AuthenticateRequest(object sender, EventArgs e)
        {

        }

        protected void Application_Error(object sender, EventArgs e)
        {

        }

        protected void Session_End(object sender, EventArgs e)
        {

        }

        protected void Application_End(object sender, EventArgs e)
        {
            HttpRuntime runtime = (HttpRuntime)typeof(System.Web.HttpRuntime).InvokeMember("_theRuntime",
                BindingFlags.NonPublic | BindingFlags.Static | BindingFlags.GetField,
                null, null, null);

            if (runtime == null)
                return;

            string shutDownMessage =
               (string)runtime.GetType().InvokeMember("_shutDownMessage",
                   BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.GetField,
                   null, runtime, null);

            string shutDownStack =
               (string)runtime.GetType().InvokeMember("_shutDownStack",
                   BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.GetField,
                   null, runtime, null);


           org.miloss.fgsms.agent.Logger.warn(String.Format(
                  "\r\n\r\n_shutDownMessage={0}\r\n\r\n_shutDownStack={1}",
                  shutDownMessage, shutDownStack));
        }
    }
}