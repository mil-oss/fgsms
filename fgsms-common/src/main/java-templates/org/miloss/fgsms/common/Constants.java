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
package org.miloss.fgsms.common;

/**
 * A collection of constants, also versioning information
 *
 * @author AO
 */
public class Constants {
    public static final String CHARSET="UTF-8";
    /**
     * Used for OS Agent Property Pair callbacks
     * @since 6.3
     */
    public static final String PROPERTYPAIR_OS_AGENT_CALLBACK_URL = "org.miloss.fgsms.osagent.callbackaddress";
    /**
     * @since 6.3
     */
    public static final String PROPERTYPAIR_OS_AGENT_STARTED = "org.miloss.fgsms.osagent.startedat.epoch";
    public static final String oneway = "fgsms.oneway";
    public static final String DAS_META = "DASv8.wsdl";
    public static final String DCS_META = "DCS8.wsdl";
    public static final String PCS_META = "PCS8.wsdl";
    public static final String RS_META = "RSv6.wsdl";
    public static final String SS_META = "Statusv6.wsdl";
    public static final String ARS_META = "ARSv1.wsdl";
    public static final String ACS_META = "AgentConf.wsdl";
    public static final String ACSA_META = "AgentConfAdmin.wsdl";
    /**
     * @since 6.3
     */
    public static final String RACS_META = "RemoteAgentCallback.wsdl";
    /**
     * This is the current build number, it is replaced at build time by the
     * maven and includes the version, branch, timestamp and username of the person that build it
     */
    public static final String Version = "${project.parent.version} ${git.branch} ${timestamp}.";
    public static final String LoggerName = "fgsms.Agents";
    public static final String key = "fgsms.messagekey";
    public static final String urlKEY = "fgsms.requesturl";
    @Deprecated
    public static final String transactionRecordedKey = "fgsms.transactionrecord";
    /**
     * Http header
     */
    public static final String transactionthreadKey = "fgsms.transactionthreadid";
    /**
     * http header
     */
    public static final String relatedtransactionKey = "fgsms.relatedtransaction";
    @Deprecated
    public static final String MessageId = "fgsms.messageid";
    public static final String DCSaction = "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddData";
    public static final String PCSaction = "urn:org:miloss:fgsms:services:interfaces:policyConfiguration/policyConfigurationService/GetServicePolicy";
    public static final String DCSaction3 = "{urn:org:miloss:fgsms:services:interfaces:dataCollector}AddMoreData";
    public static final String DCSaction4 = "urn:org:miloss:fgsms:services:interfaces:dataCollector/dataCollectorService/AddMoreData";
    public static final String DCSaction2 = "{urn:org:miloss:fgsms:services:interfaces:dataCollector}AddData";
    public static final String PCSaction2 = "{urn:org:miloss:fgsms:services:interfaces:policyConfiguration}GetServicePolicy";
    public static final String PropertyAuthModeKey = "fgsms.AuthMode";
    public static final String HTTPQueryName = "QueryString";
    /**
     * Http header
     */
    public static final String CAC_DELEGATE_Authorization_Header = "fgsms.authorization";
    public static final String Undetermineable = "urn:undeterminable";
    /*
     * Roles
     */
    public static final String ROLES_GLOBAL_ADMINISTRATOR = "fgsms_GLOBAL_ADMINISTRATOR";
    public static final String ROLES_GLOBAL_AGENT = "fgsms_GLOBAL_AGENT";
    public static final String ROLES_GLOBAL_AUDITOR = "fgsms_GLOBAL_AUDITOR";
    public static final String ROLES_GLOBAL_WRITE = "fgsms_GLOBAL_WRITE";
    public static final String ROLES_GLOBAL_READ = "fgsms_GLOBAL_READ";
    
    public static final String WS_ADDRESSING_2004_08 = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    public static final String WS_ADDRESSING_2005_08 = "http://www.w3.org/2005/08/addressing";
    public static final String DomainUnspecified = "UNSPECIFIED";
    public static final String UNSPECIFIED="unspecified";

    public enum AuthMode {

        None, PKI, UsernamePassword
    }
    public static final String DiscoveryScopeUsernamePassword = "org.miloss.fgsms.authentication.usernamePassword";
    public static final String DiscoveryScopeCACPKI = "org.miloss.fgsms.authentication.X509PKI";
    /* for internal use only, primiarily for junit tests 
     @since 6.3*/
    public static final String INTERNAL_DEFAULT_CONFIG_DATABASE_NAME = "fgsms_config";
    /* for internal use only, primiarily for junit tests 
     @since 6.3*/
    public static final String INTERNAL_DEFAULT_PERFORMANCE_DATABASE_NAME = "fgsms_performance";
    /* for internal use only, primiarily for junit tests 
     @since 6.3*/
    public static final String INTERNAL_DEFAULT_QUARTZ_DATABASE_NAME = "fgsms_quartz";
    
   
    
}
