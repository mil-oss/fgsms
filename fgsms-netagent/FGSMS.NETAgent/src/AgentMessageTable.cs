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
using System.Diagnostics;
using System.Collections.Generic;
using System.Collections.Specialized;

namespace org.miloss.fgsms.agent
{
    /// <summary>
    /// Singleton Hash table for messages
    /// </summary>
    public sealed class AgentMessageTable
    {
        private static volatile AgentMessageTable instance;
        private static object syncRoot = new Object();
        private static TraceSource log;
        private static string id = "";
        const string name = "org.miloss.fgsms.agents.AgentMessageTable";
        private static Hashtable messageTable;

        private AgentMessageTable()
        {
            id = Guid.NewGuid().ToString();
            log = new TraceSource(name);
            Hashtable t = new Hashtable();
            messageTable = Hashtable.Synchronized(t);

        } // end MessageProcessor
        public static int GetSize()
        {
            if (messageTable == null)
                messageTable = new Hashtable();
            return messageTable.Count;

        }
        public static AgentMessageTable Instance
        {
            get
            {
                if (instance == null)
                {
                    lock (syncRoot)
                    {
                        if (instance == null)
                            instance = new AgentMessageTable();
                    }
                }

                return instance;
            }
        } // end Instance

        public static List<int> GetOldMessages(DateTime cutoff)
        {
            //if now is 01:10pm, cutoff is 01:00pm
            List<int> l = new List<int>();
            lock (messageTable)
            {
                IDictionaryEnumerator it = messageTable.GetEnumerator();
                while (it.MoveNext())
                {
                    MessageCorrelator ret = (MessageCorrelator)it.Entry.Value;
                    if (ret.RecievedAt < cutoff)
                        l.Add((int)it.Key);
                }

            }
            return l;
        }

        public static void AddRequest(MessageCorrelator mc, int hash)
        {
            messageTable.Add(hash, mc);
        }


        public static void AddRequest(int hash, DateTime time, string message, string url, NameValueCollection headers)
        {
            MessageCorrelator x = new MessageCorrelator();
            x.RequestMessage = message;
            x.RecievedAt = time;
            x.originalUrl = x.URL = url;
            x.MessageID = Guid.NewGuid().ToString();
            x.identity = new List<string>();
            messageTable.Add(hash, x);
        }


        public static MessageCorrelator GetRequestbyHash(int hash)
        {
            MessageCorrelator ret = (MessageCorrelator)messageTable[hash];
            if (ret == null) return null; 
            return ret;
        }
        /*
        public static string GetRequestMessage(int hash)
        {
            MessageCorrelator ret = (MessageCorrelator)messageTable[hash];
            return ret.;
        }
        public static string GetRequestURL(int hash)
        {
            MessageTimeKeeper ret = (MessageTimeKeeper)messageTable[hash];
            return ret.url;
        }*/
        public static MessageCorrelator RemoveMessage(int hash)
        {
            try
            {
                MessageCorrelator c = (MessageCorrelator)messageTable[hash];
                messageTable.Remove(hash);
                return c;
            }
            catch {
                return null;
            }
        }
    }
    /*
    public class MessageTimeKeeper
    {
        public DateTime time;
        public string message;
        public string url;
    }*/

}
