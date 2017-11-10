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
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportingService;
import org.miloss.fgsms.services.interfaces.status.OpStatusService;
import org.miloss.fgsms.services.interfaces.status.StatusService;

/**
 * Loads client proxy objects for the fgsms Web services using the
 * configuration file settings. This class will use the current thread's context
 * class loader to attempt to determine if we are running in a CXF or
 * JbossWS-Native environment. If it cannot be determined, an exception is
 * thrown. This class is similar to the factory pattern, however it is not
 * explicitly configurable
 *
 * @since 6.2
 * @author AO
 */
public class ProxyLoader implements IProxyLoader {

    public static final String PROXY_LOADER_SESSION_KEY = "org.miloss.fgsms.proxyloader";

    public static ProxyLoader getInstance(ServletContext application) throws Exception {
        Object attribute = application.getAttribute(PROXY_LOADER_SESSION_KEY);
        if (attribute != null && attribute instanceof ProxyLoader) {
            return (ProxyLoader) attribute;
        }
        ProxyLoader pl = new ProxyLoader(application);
        application.setAttribute(PROXY_LOADER_SESSION_KEY, pl);
        return pl;
    }

    private ProxyLoader(ServletContext application) throws Exception {

        //     this.application = application;
//determine if we are in jbossws-native or cxf
        //hooks for JbossWS Native
        /*try {
            Class c = Thread.currentThread().getContextClassLoader().loadClass("org.jboss.ws.core.StubExt");
            Class c2 = Thread.currentThread().getContextClassLoader().loadClass("org.jboss.ws.extensions.security.jaxws.WSSecurityHandlerClient");
            if (c != null && c2 != null) {
                object = new ProxyLoaderJbossWSNative(application);
                return;
            }
        } catch (ClassNotFoundException cnf) {
        }*/


        //hooks for JbossWS CXF
        try {
            Class<?> c = Thread.currentThread().getContextClassLoader().loadClass("org.apache.cxf.transport.http.HTTPConduit");
            if (c != null) {
                object = new ProxyLoaderCXF(application);
                return;
            }
        } catch (ClassNotFoundException cnf) {
        }
        throw new ClassNotFoundException("Could not accurately determine if we are operating in the CXF of JbossWS-Native environment");

    }
    IProxyLoader object;

    @Override
    public AuthMode getAuthmode() {
        return object.getAuthmode();
    }

    @Override
    public boolean uddiConfigured() {
        return object.uddiConfigured();
    }

	
    /**
     * only used from the login page, returns a PCS client proxy that should
     * only be used for authenticated the user
     *
     * @param application
     * @param username
     * @param password
     * @return
     */
    @Override
    public PCS GetPCSForUsernamePasswordLogin(ServletContext application, String username, String password) {
        return object.GetPCSForUsernamePasswordLogin(application, username, password);
    }

    @Override
    public PCS GetPCS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetPCS(application, request, response);
    }

    @Override
    public DataAccessService GetDAS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetDAS(application, request, response);
    }

    @Override
    public ReportingService GetRS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetRS(application, request, response);
    }

    @Override
    public AutomatedReportingService GetARS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetARS(application, request, response);
    }

    @Override
    public StatusService GetSS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetSS(application, request, response);
    }

    @Override
    public UDDIConfig GetUDDIInquiryConfig(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetUDDIInquiryConfig(application, request, response);
    }

    @Override
    public String getKeyStoreTrustStoreDirectory() {
        return object.getKeyStoreTrustStoreDirectory();
    }

    @Override
    public Properties getRawConfiguration() {
        return object.getRawConfiguration();
    }

    @Override
    public URL getRawConfigurationURL() {
        return object.getRawConfigurationURL();
    }

    @Override
    public OpStatusService GetOpStat(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetOpStat(application, request, response);
    }

    @Override
    public RemoteAgentCallbackPort GetAgentCallBack(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetAgentCallBack(application, request, response);
    }
    
        @Override
    public OpStatusService GetAgentCallBackOpStat(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return object.GetAgentCallBackOpStat(application, request, response);
    }

    @Override
    public boolean isSecure() {
        return object.isSecure();
    }
}
