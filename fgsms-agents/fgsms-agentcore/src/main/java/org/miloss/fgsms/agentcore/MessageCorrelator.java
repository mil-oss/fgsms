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
 *  U.S. Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

package org.miloss.fgsms.agentcore;

import java.util.HashMap;


/**
 *Provides a simple container to store ws data temporarily
 * @author AO
 */
  public class MessageCorrelator
    {
        public String soapAction;
        public String URL;
        public String MessageID;
        public String RelatedMsgId;
        public String TransactionThreadId;
        public String RequestMessage;
        public String ResponseMessage;
        public long CompletedAt;
        public long RecievedAt;
        public int reqsize;
        public int ressize;
        public int currentMapsize;
        public boolean  IsFault;
        /*
         * request headers
         * <String, String> OR
         * <String, List<String>
         */
        public HashMap<String,Object> Headers;
        /*
         * response headers
         * <String, String> OR
         * <String, List<String>
         */
        public HashMap<String,Object> Header_Response;
        public String HttpIdentity;
        public String ipaddress;
        public String agent_class_name;
        public String originalurl;
    } // end class MessageCorrelator

