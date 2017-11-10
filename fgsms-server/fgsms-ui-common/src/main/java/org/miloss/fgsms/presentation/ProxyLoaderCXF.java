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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.apache.juddi.v3.client.UDDIService;
import org.miloss.fgsms.common.Constants.AuthMode;
import org.miloss.fgsms.common.Utility;
import org.miloss.fgsms.services.interfaces.agentcallbackservice.RemoteAgentCallbackPort;
import org.miloss.fgsms.services.interfaces.agentcallbackservice.RemoteAgentCallbackService;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService;
import org.miloss.fgsms.services.interfaces.automatedreportingservice.AutomatedReportingService_Service;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService;
import org.miloss.fgsms.services.interfaces.dataaccessservice.DataAccessService_Service;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PCS;
import org.miloss.fgsms.services.interfaces.policyconfiguration.PolicyConfigurationService;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportingService;
import org.miloss.fgsms.services.interfaces.reportingservice.ReportingService_Service;
import org.miloss.fgsms.services.interfaces.status.OpStatusService;
import org.miloss.fgsms.services.interfaces.status.StatusService;
import org.miloss.fgsms.services.interfaces.status.StatusServiceService;
import org.apache.log4j.Level;
import org.miloss.fgsms.common.Logger;;
import org.miloss.fgsms.common.Constants;

/**
 * Loads client proxy objects for the fgsms Web services using the configuration
 * file settings using the JbossWS-CXF libraries
 *
 * @see ProxyLoader
 * @author AO
 * @since 6.2
 */
public class ProxyLoaderCXF implements IProxyLoader {

    private URL propertiesFile = null;
    static final Logger log = LogHelper.getLog();

    public ProxyLoaderCXF(ServletContext application) throws MalformedURLException, IOException {
     
        URL prop = application.getResource("/META-INF/config.properties");
        p = Helper.loadForJSP(prop);
        propertiesFile = prop;
        authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
        String tmp = System.getProperty("jboss.server.config.url");
        if (Utility.stringIsNullOrEmpty(tmp)) {
            //FIX for Jboss 7
            try {
                tmp = System.getProperty("jboss.server.config.dir");
                File f = new File(tmp);
                tmp = f.toURI().toURL().toString();
                tmp += File.separator;
            } catch (Exception e) {
                log.log(Level.DEBUG, null, e);
            }
        } 
        tmp = System.getenv("CATALINA_HOME");
        if (!Utility.stringIsNullOrEmpty(tmp)) {
            //we are in tomcat
                tmp = tmp + File.separator + "conf";
                //assume that the keystore/truststore is in tomcat/conf folder
        }
        storelocation = tmp;
        if (storelocation==null)
            storelocation="";
    }
    private String storelocation = "";
    private Properties p = null;
    //  private ServletContext application = null;
    private AuthMode authmode = AuthMode.None;

    @Override
    public AuthMode getAuthmode() {
        return authmode;
    }

    @Override
    public boolean uddiConfigured() {
        return p.containsKey(UDDIURL_INQUIRY);
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
        if (authmode != AuthMode.UsernamePassword) {
            throw new IllegalArgumentException("authmode is not username/password");
        }
        try {
            URL pcsurl2 = new URL(p.getProperty(POLICYCONFIG));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));

            PolicyConfigurationService svc = new PolicyConfigurationService();
            PCS p2 = svc.getPCSPort();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();
            //   StubExt sec = (StubExt) bpPCS1;    //jbossws specific thing
            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {
                //  sec.setSecurityConfig("fgsms-username-config.xml");
                contextPCS.put(BindingProvider.USERNAME_PROPERTY, username);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, password);
            }
            /*
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }*/
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading login proxy", ex);
        }
        return null;
    }

    @Override
    public PCS GetPCS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL pcsurl2 = null;
        String usernameheader = "";
        String passwordheader = "";

        try {
            //URL prop = application.getResource("/WEB-INF/config.properties");
            //Properties p = Helper.loadForJSP(prop);

            pcsurl2 = new URL(p.getProperty(POLICYCONFIG));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            PolicyConfigurationService svc = new PolicyConfigurationService();//application.getResource("/WEB-INF/" + org.miloss.fgsms.common.Constants.PCS_META));
            PCS p2 = svc.getPCSPort();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();
            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {
                contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }
            if (authmode == AuthMode.PKI) {
                List<Handler> currenthandlers = bpPCS1.getBinding().getHandlerChain();
                if (currenthandlers == null) {
                    currenthandlers = new ArrayList<Handler>();
                }
                currenthandlers.add(new PKIHandler(request.getUserPrincipal()));
                bpPCS1.getBinding().setHandlerChain(currenthandlers);

                contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));

            }
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }
        return null;
    }

    @Override
    public DataAccessService GetDAS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL pcsurl2 = null;
        String usernameheader = "";
        String passwordheader = "";

        try {
            //    URL prop = application.getResource("/WEB-INF/config.properties");
            //   Properties p = Helper.loadForJSP(prop);

            pcsurl2 = new URL(p.getProperty(DATAACCESS));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            DataAccessService_Service svc = new DataAccessService_Service();//application.getResource("/WEB-INF/" + org.miloss.fgsms.common.Constants.DAS_META));
            DataAccessService p2 = svc.getDASPort();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();

            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {

                contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }

            if (authmode == AuthMode.PKI) {
                List<Handler> currenthandlers = bpPCS1.getBinding().getHandlerChain();
                if (currenthandlers == null) {
                    currenthandlers = new ArrayList<Handler>();
                }
                currenthandlers.add(new PKIHandler(request.getUserPrincipal()));
                bpPCS1.getBinding().setHandlerChain(currenthandlers);
                contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
            }
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }
        return null;
    }

    @Override
    public ReportingService GetRS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL pcsurl2 = null;
        String usernameheader = "";
        String passwordheader = "";
        //    AuthMode authmode = AuthMode.None;
        try {
            //     URL prop = application.getResource("/WEB-INF/config.properties");
            // Properties p = Helper.loadForJSP(prop);

            pcsurl2 = new URL(p.getProperty(REPORTING));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            ReportingService_Service svc = new ReportingService_Service();//application.getResource("/WEB-INF/" + org.miloss.fgsms.common.Constants.RS_META));
            ReportingService p2 = svc.getReportingServicePort();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();

            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {
                contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }

            if (authmode == AuthMode.PKI) {
                List<Handler> currenthandlers = bpPCS1.getBinding().getHandlerChain();
                if (currenthandlers == null) {
                    currenthandlers = new ArrayList<Handler>();
                }
                currenthandlers.add(new PKIHandler(request.getUserPrincipal()));
                bpPCS1.getBinding().setHandlerChain(currenthandlers);
                contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
            }
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }
        return null;
    }

    @Override
    public AutomatedReportingService GetARS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL pcsurl2 = null;
        String usernameheader = "";
        String passwordheader = "";

        try {
            //     URL prop = application.getResource("/WEB-INF/config.properties");
            // Properties p = Helper.loadForJSP(prop);

            pcsurl2 = new URL(p.getProperty(ARS));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            AutomatedReportingService_Service svc = new AutomatedReportingService_Service();//application.getResource("/WEB-INF/" + org.miloss.fgsms.common.Constants.ARS_META));
            AutomatedReportingService p2 = svc.getAutomatedReportingServicePort();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();

            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {

                contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }

            if (authmode == AuthMode.PKI) {
                List<Handler> currenthandlers = bpPCS1.getBinding().getHandlerChain();
                if (currenthandlers == null) {
                    currenthandlers = new ArrayList<Handler>();
                }
                currenthandlers.add(new PKIHandler(request.getUserPrincipal()));
                bpPCS1.getBinding().setHandlerChain(currenthandlers);
                contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
            }
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }
        return null;
    }

    @Override
    public StatusService GetSS(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL pcsurl2 = null;
        String usernameheader = "";
        String passwordheader = "";
        //    AuthMode authmode = AuthMode.None;
        try {
            //       URL prop = application.getResource("/WEB-INF/config.properties");
            //   Properties p = Helper.loadForJSP(prop);

            pcsurl2 = new URL(p.getProperty(STATUS));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            StatusServiceService svc = new StatusServiceService();//application.getResource("/WEB-INF/" + org.miloss.fgsms.common.Constants.SS_META));
            StatusService p2 = svc.getStatusServicePort();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();
            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {
                contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }
            if (authmode == AuthMode.PKI) {
                List<Handler> currenthandlers = bpPCS1.getBinding().getHandlerChain();
                if (currenthandlers == null) {
                    currenthandlers = new ArrayList<Handler>();
                }
                currenthandlers.add(new PKIHandler(request.getUserPrincipal()));
                bpPCS1.getBinding().setHandlerChain(currenthandlers);
                contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
            }
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }
        return null;
    }

    @Override
    public UDDIConfig GetUDDIInquiryConfig(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        UDDIConfig config = new UDDIConfig();
        String usernameheader = "";
        String passwordheader = "";
        //  AuthMode authmode = AuthMode.None;
        if (Utility.stringIsNullOrEmpty(p.getProperty(UDDIURL_INQUIRY))) {
            return null;
        }
        try {
            //  URL prop = application.getResource("/WEB-INF/config.properties");
            //  Properties p = Helper.loadForJSP(prop);

            config.secendpoint = p.getProperty(UDDIURL_SECURITY);
            config.inquiryendpoint = p.getProperty(UDDIURL_INQUIRY);
            config.publishendpoint = p.getProperty(UDDIURL_PUBLISH);
            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            String t = p.getProperty(UDDIUSE_HTTP_CLIENT_CERT);
            if (!Utility.stringIsNullOrEmpty(t)) {
                try {
                    config.useHTTPClientCert = Boolean.parseBoolean(t);
                } catch (Exception ex) {
                    config.useHTTPClientCert = false;
                }
            }
            t = p.getProperty(UDDIUSE_UDDI_USERNAME_PASSWORD);
            if (!Utility.stringIsNullOrEmpty(t)) {
                try {
                    config.useUDDI = Boolean.parseBoolean(t);
                } catch (Exception ex) {
                    config.useUDDI = false;
                }
            }
            t = p.getProperty(UDDIUSE_HTTP_USERNAME_PASSWORD);
            if (!Utility.stringIsNullOrEmpty(t)) {
                try {
                    config.useHTTPUsernamePassword = Boolean.parseBoolean(t);
                } catch (Exception ex) {
                    config.useHTTPUsernamePassword = false;
                }
            }

            UDDIService uddi = new UDDIService();

            config.inquiry = uddi.getUDDIInquiryPort();
            BindingProvider bpPCS1 = (BindingProvider) config.inquiry;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();
            //    StubExt sec = (StubExt) bpPCS1;
            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.inquiryendpoint);

            if (config.inquiryendpoint.toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }

                if (config.useHTTPClientCert) {
                    if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLKEY_STORE))) {
                        contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                        contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
                    } else {
                        LogHelper.getLog().log(Level.ERROR, "presentation uddi config is set for client certificates, however one was not defined in the config file");
                    }
                }
            }
            if (config.useHTTPUsernamePassword) {
                contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }

            //setup publisher, but only if defined in config file
            if (!Utility.stringIsNullOrEmpty(config.publishendpoint)) {

                config.publish = uddi.getUDDIPublicationPort();
                bpPCS1 = (BindingProvider) config.publish;
                contextPCS = bpPCS1.getRequestContext();
                //     sec = (StubExt) bpPCS1;
                contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.publishendpoint);

                if (config.publishendpoint.toLowerCase().startsWith("https")) {
                    if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                        contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                        contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                    }

                    if (config.useHTTPClientCert) {
                        if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLKEY_STORE))) {
                            contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                            contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
                        } else {
                            LogHelper.getLog().log(Level.ERROR, "presentation uddi config is set for client certificates, however one was not defined in the config file");
                        }
                    }
                }
                if (config.useHTTPUsernamePassword) {
                    contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                    contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
                }
            }

            //switch to security endpoint, but only if we need it
            if (config.useUDDI) {
                config.encryptedPassword = passwordheader;
                config.username = usernameheader;

                config.security = uddi.getUDDISecurityPort();
                bpPCS1 = (BindingProvider) config.security;
                contextPCS = bpPCS1.getRequestContext();
                contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.secendpoint);
                if (config.secendpoint.toLowerCase().startsWith("https")) {
                    if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                        contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                        contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                    }

                    if (config.useHTTPClientCert) {
                        if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLKEY_STORE))) {
                            contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                            contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
                        } else {
                            LogHelper.getLog().log(Level.ERROR, "presentation uddi config is set for client certificates, however one was not defined in the config file");
                        }
                    }
                }
            }
            return config;

        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }

        return null;
    }
    //   private UDDISecurityPortType security = null;

    @Override
    public String getKeyStoreTrustStoreDirectory() {
        return storelocation;
    }

    @Override
    public Properties getRawConfiguration() {
        return p;
    }

    @Override
    public URL getRawConfigurationURL() {
        return propertiesFile;
    }

    @Override
    public OpStatusService GetOpStat(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL pcsurl2 = null;
        String usernameheader = "";
        String passwordheader = "";
        //  AuthMode authmode = AuthMode.None;
        try {
            //       URL prop = application.getResource("/WEB-INF/config.properties");
            //   Properties p = Helper.loadForJSP(prop);

            pcsurl2 = new URL(p.getProperty(STATUS));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            StatusServiceService svc = new StatusServiceService();//application.getResource("/WEB-INF/" + org.miloss.fgsms.common.Constants.SS_META));
            OpStatusService p2 = svc.getOpStatusServiceBinding();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();
            contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {
                contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }
            if (authmode == AuthMode.PKI) {
                List<Handler> currenthandlers = bpPCS1.getBinding().getHandlerChain();
                if (currenthandlers == null) {
                    currenthandlers = new ArrayList<Handler>();
                }
                currenthandlers.add(new PKIHandler(request.getUserPrincipal()));
                bpPCS1.getBinding().setHandlerChain(currenthandlers);
                contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
            }
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }
        return null;
    }

    @Override
    public RemoteAgentCallbackPort GetAgentCallBack(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        return null;
        
        /*URL pcsurl2 = null;
        String usernameheader = "";
        String passwordheader = "";
        //  AuthMode authmode = AuthMode.None;
        try {
            //       URL prop = application.getResource("/WEB-INF/config.properties");
            //   Properties p = Helper.loadForJSP(prop);

            pcsurl2 = new URL(p.getProperty(STATUS));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            RemoteAgentCallbackService svc = new RemoteAgentCallbackService();//application.getResource("/WEB-INF/" + org.miloss.fgsms.common.Constants.RACS_META));
            RemoteAgentCallbackPort p2 = svc.getRemoteAgentCallbackServicePort();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();
            //contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {
                //  contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                // contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }
            if (authmode == AuthMode.PKI) {
                List<Handler> currenthandlers = bpPCS1.getBinding().getHandlerChain();
                if (currenthandlers == null) {
                    currenthandlers = new ArrayList<Handler>();
                }
                currenthandlers.add(new PKIHandler(request.getUserPrincipal()));
                bpPCS1.getBinding().setHandlerChain(currenthandlers);
                contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
            }
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }
        return null;*/
    }

    @Override
    public OpStatusService GetAgentCallBackOpStat(ServletContext application, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return null;/*
        URL pcsurl2 = null;
        String usernameheader = "";
        String passwordheader = "";
        //  AuthMode authmode = AuthMode.None;
        try {
            //       URL prop = application.getResource("/WEB-INF/config.properties");
            //   Properties p = Helper.loadForJSP(prop);

            pcsurl2 = new URL(p.getProperty(STATUS));

            authmode = AuthMode.valueOf(p.getProperty(FGSMS_AUTH_MODE));
            if (authmode == AuthMode.UsernamePassword) {
                if (Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginusername")) || Utility.stringIsNullOrEmpty((String) request.getSession().getAttribute("loginpassword"))) {
                    response.sendRedirect("login.jsp");
                }
                if (authmode == AuthMode.UsernamePassword) {
                    usernameheader = (String) request.getSession().getAttribute("loginusername");
                    passwordheader = (String) request.getSession().getAttribute("loginpassword");
                }
            }

            StatusServiceService svc = new StatusServiceService();//application.getResource("/WEB-INF/" + org.miloss.fgsms.common.Constants.SS_META));
            OpStatusService p2 = svc.getOpStatusServiceBinding();
            BindingProvider bpPCS1 = (BindingProvider) p2;
            Map<String, Object> contextPCS = bpPCS1.getRequestContext();
            // contextPCS.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, pcsurl2.toString());
            if (authmode == AuthMode.UsernamePassword) {
                //       contextPCS.put(BindingProvider.USERNAME_PROPERTY, usernameheader);
                //     contextPCS.put(BindingProvider.PASSWORD_PROPERTY, Utility.DE(passwordheader));
            }
            if (pcsurl2.toString().toLowerCase().startsWith("https")) {
                if (!Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE)) && !Utility.stringIsNullOrEmpty(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD))) {
                    contextPCS.put(JAVAXNETSSLTRUST_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLTRUST_STORE_PASSWORD)));
                    contextPCS.put(JAVAXNETSSLTRUST_STORE, storelocation + p.getProperty(JAVAXNETSSLTRUST_STORE));
                }
            }
            if (authmode == AuthMode.PKI) {
                List<Handler> currenthandlers = bpPCS1.getBinding().getHandlerChain();
                if (currenthandlers == null) {
                    currenthandlers = new ArrayList<Handler>();
                }
                currenthandlers.add(new PKIHandler(request.getUserPrincipal()));
                bpPCS1.getBinding().setHandlerChain(currenthandlers);
                contextPCS.put(JAVAXNETSSLKEY_STORE_PASSWORD, Utility.DE(p.getProperty(JAVAXNETSSLKEY_STORE_PASSWORD)));
                contextPCS.put(JAVAXNETSSLKEY_STORE, storelocation + p.getProperty(JAVAXNETSSLKEY_STORE));
            }
            return p2;
        } catch (Exception ex) {
            LogHelper.getLog().log(Level.ERROR, "error caught loading proxy", ex);
            response.sendError(500);
        }
        return null;*/
    }

    @Override
    public boolean isSecure() {
        if (!((String) p.get(REPORTING)).toLowerCase().startsWith("https")) {
            return false;
        }
        if (!((String) p.get(DATAACCESS)).toLowerCase().startsWith("https")) {
            return false;
        }
        if (!((String) p.get(POLICYCONFIG)).toLowerCase().startsWith("https")) {
            return false;
        }
        if (!((String) p.get(STATUS)).toLowerCase().startsWith("https")) {
            return false;
        }
        if (!((String) p.get(ARS)).toLowerCase().startsWith("https")) {
            return false;
        }
        if (!((String) p.get(DCS)).toLowerCase().startsWith("https")) {
            return false;
        }
        return true;
    }
}
