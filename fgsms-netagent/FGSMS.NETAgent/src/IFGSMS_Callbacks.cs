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

namespace org.miloss.fgsms.agent
{
    /// <summary>
    /// a callback interface for users of agents to tie into to recieve notifications that the queue and the last message as been sent
    /// since RC6.1
    /// 
    /// If an exception is raised when processing callbacks, the standard behavior is to drop the registration
    /// 
    /// Use MessageProcessor.RegisterCallbacks(IFGSMS_Callbacks object);
    /// MessageProcessor.UnRegisterCallbacks(IFGSMS_Callbacks object);
    /// </summary>
    public interface IFGSMS_Callbacks
    {
        /// <summary>
        /// triggers when the queue is empty, since the messaage processor is multithreaded, this can be triggered multiple times as each thread is done doing work.
        /// </summary>
         void NotifyEmptyQueue();
        /// <summary>
        /// triggers when the last message processor thread terminates
        /// </summary>
        void NotifyEmptyQueueAllMessagesHaveBeenSent();
    }
}
