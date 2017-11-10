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
using System.Security.Cryptography;

using System.Text.RegularExpressions;
using System.Configuration;
using System.Diagnostics;
using System.Web;

namespace org.miloss.fgsms.agent
{
    /// <summary>
    /// contains all logging data for FGSMS's .NET based agents
    /// </summary>
    public static class Logger
    {
        internal static TraceSource ts = new TraceSource("org.miloss.fgsms.agent");
        public static void debug(Exception ex)
        {
            debug(ex, null);

        }
        static int DEBUG_EVENT = 100;
        static int INFO_EVENT = 200;
        static int WARN_EVENT = 300;
        static int ERROR_EVENT = 400;

        public static void debug(Exception ex, string p)
        {
            MessageProcessor mp = MessageProcessor.Instance;
            if (MessageProcessor.GetConfig.DEBUG)
            {
                try
                {
                    while (ex != null)
                    {
                        p = p + Environment.NewLine + ex.Message + " " + ex.StackTrace + Environment.NewLine;
                        ex = ex.InnerException;
                    }
                    EventLog.WriteEntry("FGSMS", "DEBUG " + isInIIS() + p + FGSMSConstants.Version, EventLogEntryType.Information);
                }
                catch (Exception e)
                {

                }
                try
                {
                    ts.TraceEvent(TraceEventType.Verbose, DEBUG_EVENT, isInIIS() + p + FGSMSConstants.Version);
                }
                catch { }
            }
        }

        private static string isInIIS()
        {
            if (HttpContext.Current == null)
                return String.Empty;
            return " RUNNING IN IIS";
        }

        public static void debug(string p)
        {
            debug(null, p);
        }

        public static void error(Exception ex, string p)
        {
             MessageProcessor mp = MessageProcessor.Instance;
             if (MessageProcessor.GetConfig!=null && MessageProcessor.GetConfig.GetLogLevel <= org.miloss.fgsms.agent.ConfigLoader.LogLevel.ERROR)
             {
                 try
                 {
                     while (ex != null)
                     {
                         p = p + Environment.NewLine + ex.Message + " " + ex.StackTrace + Environment.NewLine;
                         ex = ex.InnerException;
                     }
                     EventLog.WriteEntry("FGSMS", "ERROR " + p + isInIIS() + FGSMSConstants.Version, EventLogEntryType.Error);
                 }
                 catch (Exception e)
                 {

                 }
                 try
                 {
                     ts.TraceEvent(TraceEventType.Verbose, ERROR_EVENT, isInIIS() + p + FGSMSConstants.Version);
                 }
                 catch { }
             } else
            {
                Console.Out.WriteLine("Config cannot be loaded, something must not be configured correctly");
            }
        }


        public static void warn(string p)
        {
            warn(null, p);
        }

        public static void info(Exception ex, string p)
        {
            if (MessageProcessor.GetConfig.GetLogLevel <= org.miloss.fgsms.agent.ConfigLoader.LogLevel.INFO)
            {
                try
                {
                    while (ex != null)
                    {
                        p = p + Environment.NewLine + ex.Message + " " + ex.StackTrace + Environment.NewLine;
                        ex = ex.InnerException;
                    }
                    EventLog.WriteEntry("FGSMS", "INFO " + p + isInIIS() + FGSMSConstants.Version, EventLogEntryType.Information);
                }
                catch (Exception e)
                {

                }
                try
                {
                    ts.TraceEvent(TraceEventType.Verbose, INFO_EVENT, isInIIS() + p + FGSMSConstants.Version);
                }
                catch { }
            }
        }

        public static void warn(Exception ex, string p)
        {
            if (MessageProcessor.GetConfig.GetLogLevel <= org.miloss.fgsms.agent.ConfigLoader.LogLevel.WARN)
            {
                try
                {
                    while (ex != null)
                    {
                        p = p + Environment.NewLine + ex.Message + " " + ex.StackTrace + Environment.NewLine;
                        ex = ex.InnerException;
                    }
                    EventLog.WriteEntry("FGSMS", "WARN " + p + isInIIS() + FGSMSConstants.Version, EventLogEntryType.Warning);
                }
                catch (Exception e)
                {

                }
                try
                {
                    ts.TraceEvent(TraceEventType.Verbose, WARN_EVENT, isInIIS() + p + FGSMSConstants.Version);
                }
                catch { }
            }
        }




        public static void error(string p)
        {
            error(null, p);
        }

        public static void info(string p)
        {
            info(null, p);
        }
    }
}
