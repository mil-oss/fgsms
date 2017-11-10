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

package org.miloss.fgsms.presentation;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.miloss.fgsms.common.Constants.AuthMode;
import org.miloss.fgsms.services.interfaces.agentcallbackservice.RemoteAgentCallbackPort;
import org.miloss.fgsms.services.interfaces.agentcallbackservice.RemoteAgentCallbackService;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportingService;
import org.miloss.fgsms.services.interfaces.status.OpStatusService;
import org.miloss.fgsms.services.interfaces.status.StatusService;


/**
 * Proxy Loader interface used for web applications accessing fgsms
 * services<br><br>
 * This interface is used by the class ProxyLoader and serves
 * as an abstraction layer to support multiple soap stacks
 *
 * @author AO
 */
public interface IProxyLoader {

    public static final String JAVAXNETSSLKEY_STORE = "javax.net.ssl.keyStore";
    public static final String JAVAXNETSSLKEY_STORE_PASSWORD = "javax.net.ssl.keyStorePassword";
    public static final String JAVAXNETSSLTRUST_STORE = "javax.net.ssl.trustStore";
    public static final String JAVAXNETSSLTRUST_STORE_PASSWORD = "javax.net.ssl.trustStorePassword";
    public static final String FGSMS_AUTH_MODE = "fgsms.AuthMode";
    public static final String REPORT_PICKUP_LOCATION = "reportpickuplocation";
    public static final String POLICYCONFIG = "policyconfig";
    public static final String ARS = "ars";
    public static final String DATAACCESS = "dataaccess";
    public static final String REPORTING = "reporting";
    public static final String STATUS = "status";
    public static final String DCS = "datacollectors";
    public static final String UDDIURL_INQUIRY = "uddiurl_inquiry";
    public static final String UDDIURL_PUBLISH = "uddiurl_publish";
    public static final String UDDIURL_SECURITY = "uddiurl_security";
    public static final String UDDIUSE_HTTP_CLIENT_CERT = "uddi.useHttpClientCert";
    public static final String UDDIUSE_HTTP_USERNAME_PASSWORD = "uddi.useHttpUsernamePassword";
    public static final String UDDIUSE_UDDI_USERNAME_PASSWORD = "uddi.useUddiUsernamePassword";

    URL getRawConfigurationURL();

    Properties getRawConfiguration();

    AutomatedReportingService GetARS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;

    DataAccessService GetDAS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;

    PCS GetPCS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * only used from the login page, returns a PCS client proxy that should
     * only be used for authenticated the user
     *
     * @param application
     * @param username
     * @param password
     * @return
     */
    PCS GetPCSForUsernamePasswordLogin(ServletContext application, String username, String password);

    ReportingService GetRS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;

    StatusService GetSS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * returns configuration object representing the UDDI's servers
     * configuration, including client proxies
     *
     * @return
     */
    UDDIConfig GetUDDIInquiryConfig(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;

    AuthMode getAuthmode();

    /**
     * returns true if the UDDI browser parameters are currently configured
     *
     * @return
     */
    boolean uddiConfigured();

    /**
     * returns the directory containing the trust store and key stores
     */
    String getKeyStoreTrustStoreDirectory();
    
    /*
     * Gets an OpStat client for use with pinging fgsms 6.3+ services
     */
    OpStatusService GetOpStat(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;
    
    /**
     * Returns an instance of the agent callback service. Note: The invocation URL will NOT be set. Credentials will NOT be populated as the remote agent 
     * cannot validate the credentials
     * @param application
     * @param request
     * @param response
     * @return
     * @throws IOException 
     */
    RemoteAgentCallbackPort GetAgentCallBack(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;
    
    /**
     * Returns an instance of the agent callback service. Note: The invocation URL will NOT be set. Credentials will NOT be populated as the remote agent 
     * cannot validate the credentials 
     * @param application
     * @param request
     * @param response
     * @return
     * @throws IOException 
     */
    OpStatusService GetAgentCallBackOpStat(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException;
    
    boolean isSecure();
}
