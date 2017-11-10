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
    /// 
    /// </summary>
    public static class FGSMSConstants
    {
        /// <summary>
        /// application/atom+xml
        /// </summary>
        public static readonly string atom = "application/atom+xml";
        /// <summary>
        /// application/json
        /// </summary>
        public static readonly string json = "application/json";
        /// <summary>
        /// application/soap+xml
        /// </summary>
        public static readonly string soap = "application/soap+xml";
        /// <summary>
        /// text/xml
        /// </summary>
        public static readonly string xml = "text/xml";
      //  public static string transactionRecordedKey = "FGSMS.transactionrecord";

        /// <summary>
        /// 
        /// </summary>
       // public static bool log = true;
        public static readonly string CAC_DELEGATE_Authorization_Header = "FGSMS.authorization";
        public static readonly  string Version = " 7.0.0";
        public static readonly string LoggerName = "org.miloss.fgsms.agents";
        public static readonly string key = "FGSMS.messagekey";
        public static readonly string urlKEY = "FGSMS.requestLurl";
        public static readonly string transactionRecordedKey = "FGSMS.transactionrecord";
        public static readonly string transactionthreadKey = "FGSMS.transactionthreadid";
        public static readonly string MessageId = "FGSMS.messageid";

        public static readonly string DCSaction = "urn:org:miloss:FGSMS:services:interfaces:dataCollector/dataCollectorService/AddData";
        public static readonly string PCSaction = "urn:org:miloss:FGSMS:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy";
        public static readonly string DCSaction3 = "{urn:org:miloss:FGSMS:services:interfaces:dataCollector}AddMoreData";
        public static readonly string DCSaction4 = "urn:org:miloss:FGSMS:services:interfaces:dataCollector/dataCollectorService/AddMoreData";
        public static readonly string DCSaction2 = "{urn:org:miloss:FGSMS:services:interfaces:dataCollector}AddData";
        public static readonly string PCSaction2 = "{urn:org:miloss:FGSMS:services:interfaces:policyConfiguration}GetServicePolicy";

        public static readonly string SelfMonitoredAgentName = "org.miloss.fgsms.agents.SelfMonitored";
    }
}
