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
using org.miloss.fgsms.agent;
using System.Xml.Serialization;
using System.Threading;
using System.Diagnostics;

namespace FGSMS.PersistentStorageAgent
{

    internal class PersistentStorageAgent
    {
        MessageProcessor mp = MessageProcessor.Instance;
        ConfigLoader config = null;
        public void Fire()
        {
            config = MessageProcessor.GetConfig;
            if (config == null)
                return;
                
            if (HasPersistenceFiles())
            {
                Logger.info("Stored files are present, attempting to restablish communications");
                System.Console.WriteLine("Stored files are present, attempting to restablish communications");
                //grab the first one
                AddDataRequestMsg rr = MessageProcessor.ReadItemsFromDisk();
                if (rr != null)
                {
                    bool online = MessageProcessor.ProcessSingleMessage(rr);
                    if (!online)
                    {
                        Logger.warn("Still offline, we'll try again later");
                        System.Console.WriteLine("Still offline, we'll try again later");
                        MessageProcessor.WriteToDisk(rr);
                    }
                    else
                    {
                        Logger.info("We're online, add all items to the cache.");
                        System.Console.WriteLine("We're online, add all items to the cache.");
                        while (HasPersistenceFiles())
                        {
                            rr = MessageProcessor.ReadItemsFromDisk();
                            if (rr != null)
                            {
                                //use standard built in functions to enqueue the data
                                MessageProcessor.ProcessMessage(rr);
                            }
                            //throttling
                            if (MessageProcessor.GetQueueSize() > 100)
                            {

                                Thread.Sleep(5000);
                            }
                        }
                    }
                }
            }
        }

        bool HasPersistenceFiles()
        {
            if (!String.IsNullOrEmpty(config.PersistLocation))
            {
                if (Directory.Exists(config.PersistLocation))
                {
                    String[] s = Directory.GetFiles(config.PersistLocation);
                    if (s != null && s.Length > 0)
                        return true;
                }
            }
            return false;
            //throw new NotImplementedException();
        }
    }
}
