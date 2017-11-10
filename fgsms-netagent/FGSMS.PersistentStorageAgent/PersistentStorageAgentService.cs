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
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.ServiceProcess;
using System.Text;
using System.Threading;

namespace FGSMS.PersistentStorageAgent
{

    public partial class PersistentStorageAgentService : ServiceBase
    {
        public PersistentStorageAgentService()
        {
            InitializeComponent();
        }
        Thread t = null;
        PersistentStorageAgent agent = new PersistentStorageAgent();
        protected override void OnStart(string[] args)
        {
            t = new Thread(new ThreadStart(Run));
            t.Start();
        }
        Boolean running = true;
        void Run()
        {
            while (running)
            {
                agent.Fire();
                Thread.Sleep(1000);
            }
        }
        protected override void OnStop()
        {
            running = false;
            t.Join();
        }
    }
}
