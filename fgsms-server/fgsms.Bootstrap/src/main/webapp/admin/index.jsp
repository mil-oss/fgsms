<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="java.util.Set"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.util.Properties"%>
<%@page import="org.miloss.fgsms.presentation.IProxyLoader"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.SetAdministratorRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetAdministratorsResponseMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetAdministratorsRequestMsg"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.PCS"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>


<%    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Pragma", "no-cache");
    ProxyLoader pl = ProxyLoader.getInstance(application);
    if (request.getUserPrincipal() != null) {
        LogHelper.getLog().log(Level.INFO, "Current User: " + request.getUserPrincipal().getName() + " by " + request.getAuthType() + " ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());

    } else {
        LogHelper.getLog().log(Level.INFO, "Current User: anonymous ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());
    }

    boolean ok = false;
    SecurityWrapper sc = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    if (sc == null) {
        response.sendRedirect("index.jsp");
    } else {
        String classlevel = Utility.ICMClassificationToString(sc.getClassification());
        String color = Utility.ICMClassificationToColorCodeString(sc.getClassification());
        PCS pcsport = pl.GetPCS(application, request, response);
        try {
            //since only admins and set the admin list, this serves as sufficient access control
            GetAdministratorsRequestMsg req = new GetAdministratorsRequestMsg();
            req.setClassification(sc);
            GetAdministratorsResponseMsg res = pcsport.getAdministrators(req);
            SetAdministratorRequestMsg req2 = new SetAdministratorRequestMsg();
            req2.setClassification(sc);
            req2.setUserList(res.getUserList());
            pcsport.setAdministrator(req2);
            ok = true;
        } catch (Exception ex) {
            out.write("<h1 color=red>Access Denied</h1>");
            response.setStatus(403);
        }
    }

    if (ok) {

        if (request.getMethod().equalsIgnoreCase("POST")) {
            Properties p = pl.getRawConfiguration();
            p.setProperty(IProxyLoader.FGSMS_AUTH_MODE, request.getParameter(IProxyLoader.FGSMS_AUTH_MODE));
            p.setProperty(IProxyLoader.DATAACCESS, request.getParameter(IProxyLoader.DATAACCESS));
            p.setProperty(IProxyLoader.POLICYCONFIG, request.getParameter(IProxyLoader.POLICYCONFIG));
            p.setProperty(IProxyLoader.REPORTING, request.getParameter(IProxyLoader.REPORTING));
            p.setProperty(IProxyLoader.ARS, request.getParameter(IProxyLoader.ARS));
            p.setProperty(IProxyLoader.STATUS, request.getParameter(IProxyLoader.STATUS));
            p.setProperty(IProxyLoader.REPORT_PICKUP_LOCATION, request.getParameter(IProxyLoader.REPORT_PICKUP_LOCATION));
            p.setProperty(IProxyLoader.DCS, request.getParameter(IProxyLoader.DCS));
            p.setProperty(IProxyLoader.JAVAXNETSSLKEY_STORE, request.getParameter(IProxyLoader.JAVAXNETSSLKEY_STORE));
            p.setProperty(IProxyLoader.JAVAXNETSSLKEY_STORE_PASSWORD, request.getParameter(IProxyLoader.JAVAXNETSSLKEY_STORE_PASSWORD));

            p.setProperty(IProxyLoader.JAVAXNETSSLTRUST_STORE, request.getParameter(IProxyLoader.JAVAXNETSSLTRUST_STORE));
            p.setProperty(IProxyLoader.JAVAXNETSSLTRUST_STORE_PASSWORD, request.getParameter(IProxyLoader.JAVAXNETSSLTRUST_STORE_PASSWORD));
            if (!request.getParameter(IProxyLoader.UDDIURL_INQUIRY).equalsIgnoreCase("null")) {
                p.setProperty(IProxyLoader.UDDIURL_INQUIRY, request.getParameter(IProxyLoader.UDDIURL_INQUIRY));
            } else {
                p.remove(IProxyLoader.UDDIURL_INQUIRY);
            }
            if (!request.getParameter(IProxyLoader.UDDIURL_PUBLISH).equalsIgnoreCase("null")) {
                p.setProperty(IProxyLoader.UDDIURL_PUBLISH, request.getParameter(IProxyLoader.UDDIURL_PUBLISH));
            } else {
                p.remove(IProxyLoader.UDDIURL_PUBLISH);
            }
            if (!request.getParameter(IProxyLoader.UDDIURL_SECURITY).equalsIgnoreCase("null")) {
                p.setProperty(IProxyLoader.UDDIURL_SECURITY, request.getParameter(IProxyLoader.UDDIURL_SECURITY));
            } else {
                p.remove(IProxyLoader.UDDIURL_SECURITY);
            }
            p.setProperty(IProxyLoader.UDDIUSE_HTTP_USERNAME_PASSWORD, request.getParameter(IProxyLoader.UDDIUSE_HTTP_USERNAME_PASSWORD));
            p.setProperty(IProxyLoader.UDDIUSE_HTTP_CLIENT_CERT, request.getParameter(IProxyLoader.UDDIUSE_HTTP_CLIENT_CERT));
            p.setProperty(IProxyLoader.UDDIUSE_UDDI_USERNAME_PASSWORD, request.getParameter(IProxyLoader.UDDIUSE_UDDI_USERNAME_PASSWORD));

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(application.getRealPath("/META-INF/config.properties"));
                p.store(fos, "Last revised at " + request.getUserPrincipal().getName() + " by " + request.getAuthType() + " ip " + request.getRemoteAddr());
                fos.flush();
                fos.close();
                 out.write("Configuration saved successfully");
            } catch (Exception ex) {
                LogHelper.getLog().warn("failed to save configuration.properties changes", ex);
                out.write("<h1 color=red>Failed to save changes!</h1>");
                 out.write("See server log for details");
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception ex) {
                    }
                }
            }
        } else {
%>

<div class="well">
    <h1>Connections</h1>
    <%out.write("<p>Editing the file at " + application.getRealPath("/META-INF/config.properties") + "</p>");%>

</div>
<div class="row-fluid">
    <div class="span12">
        <form id="form2" runat="server"  method="post" autocomplete="off">
            <h1>Configuration</h1>
            This page will allow system administrators to modify the configuration of this instance of FGSMSWeb.war, the management web site. It MUST be deployed in an exploded form. For clustered/load balanced web guis, you <u>should</u> perform the same modifications on the other nodes.
            You are currently connected the server with the hostname: <%= Utility.getHostName()%><br>
            <h2>Connections to FGSMS Services</h2>
            <table class="table"><tr><th>Service</th><th>URL</th></tr>

                <tr><td>FGSMS's Authentication Mode*</td><td><%
                    //<input size="100" type="text" name="<%=IProxyLoader.FGSMS_AUTH_MODE% >" value="
                    out.write("<select name=\"" + IProxyLoader.FGSMS_AUTH_MODE + "\">");
                    for (int i = 0; i < Constants.AuthMode.values().length; i++) {
                        out.write("<option value=\"" + Constants.AuthMode.values()[i].name() + "\" ");
                        if (pl.getRawConfiguration().getProperty(IProxyLoader.FGSMS_AUTH_MODE).equalsIgnoreCase(Constants.AuthMode.values()[i].name())) {
                            out.write(" selected=\"selected\" ");
                        }
                        out.write(" >" + Constants.AuthMode.values()[i].name() + " </option>");
                    }
                    out.write(pl.getRawConfiguration().getProperty(IProxyLoader.FGSMS_AUTH_MODE));

                        %>"</td></tr>
                <tr><td>Data Access Service</td><td><input size="100" type="text" name="<%=IProxyLoader.DATAACCESS%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.DATAACCESS)%>"</td></tr>
                <tr><td>Policy Configuration Service</td><td><input size="100"  type="text" name="<%=IProxyLoader.POLICYCONFIG%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.POLICYCONFIG)%>"</td></tr>
                <tr><td>Reporting Service</td><td><input size="100"  type="text" name="<%=IProxyLoader.REPORTING%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.REPORTING)%>"</td></tr>
                <tr><td>Automated Reporting Service</td><td><input size="100"  type="text" name="<%=IProxyLoader.ARS%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.ARS)%>"</td></tr>
                <tr><td>Status Service</td><td><input size="100"  type="text" name="<%=IProxyLoader.STATUS%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.STATUS)%>"</td></tr>
                <tr><td>Data Collector Service(s)** </td><td><input size="100"  type="text" name="<%=IProxyLoader.DCS%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.DCS)%>"</td></tr>
                <tr><td>Report Pickup Location</td><td><input size="100"  type="text" name="<%=IProxyLoader.REPORT_PICKUP_LOCATION%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.REPORT_PICKUP_LOCATION)%>"</td></tr>
            </table>
            *When changing this value, other changes ARE required. Simply changing this will NOT have the desired affect unless changing the other files (jboss-web.xml, web.xml in this deployment and in the FGSMSServices.war deployments). See the documentation for details.<Br>
            **Used only for status validation, multiple URLs may be specified using the pipe symbol |<Br>
            <h2>SSL Settings</h2>
            <table  class="table"><tr><th>Service</th><th>URL</th></tr>
                <tr><td>Key Store Filename</td><td><input size="100"  type="text" name="<%=IProxyLoader.JAVAXNETSSLKEY_STORE%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.JAVAXNETSSLKEY_STORE)%>"</td></tr>
                <tr><td>Key Store Password***</td><td><input size="100"  type="text" name="<%=IProxyLoader.JAVAXNETSSLKEY_STORE_PASSWORD%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.JAVAXNETSSLKEY_STORE_PASSWORD)%>"</td></tr>
                <tr><td>Trust Store Filename</td><td><input size="100"  type="text" name="<%=IProxyLoader.JAVAXNETSSLTRUST_STORE%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.JAVAXNETSSLTRUST_STORE)%>"</td></tr>
                <tr><td>Trust Store Password***</td><td><input size="100"  type="text" name="<%=IProxyLoader.JAVAXNETSSLTRUST_STORE_PASSWORD%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.JAVAXNETSSLTRUST_STORE_PASSWORD)%>"</td></tr>
            </table>
            *** Passwords SHOULD be encrypted. <a href="javascript:loadpage('admin/encryptor.jsp', 'mainpane','admin/index.jsp');">Use this page</a> to encrypt.<br>
            <h2>Connections to UDDI Services (optional)</h2>
            <table  class="table"><tr><th>Service</th><th>URL</th></tr>
                <tr><td>Inquiry Service</td><td><input size="100"  type="text" name="<%=IProxyLoader.UDDIURL_INQUIRY%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.UDDIURL_INQUIRY)%>"</td></tr>
                <tr><td>Publish Service</td><td><input size="100"  type="text" name="<%=IProxyLoader.UDDIURL_PUBLISH%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.UDDIURL_PUBLISH)%>"</td></tr>
                <tr><td>Security Service</td><td><input size="100"  type="text" name="<%=IProxyLoader.UDDIURL_SECURITY%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.UDDIURL_SECURITY)%>"</td></tr>
                <tr><td>Use Http Username</td><td><input size="100"  type="text" name="<%=IProxyLoader.UDDIUSE_HTTP_USERNAME_PASSWORD%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.UDDIUSE_HTTP_USERNAME_PASSWORD)%>"</td></tr>
                <tr><td>Use Http CLIENT_CERT</td><td><input size="100"  type="text" name="<%=IProxyLoader.UDDIUSE_HTTP_CLIENT_CERT%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.UDDIUSE_HTTP_CLIENT_CERT)%>"</td></tr>
                <tr><td>Use Http UDDI Username</td><td><input size="100"  type="text" name="<%=IProxyLoader.UDDIUSE_UDDI_USERNAME_PASSWORD%>" value="<%=pl.getRawConfiguration().getProperty(IProxyLoader.UDDIUSE_UDDI_USERNAME_PASSWORD)%>"</td></tr>
            </table>


            <a class="btn btn-primary btn-large" href="javascript:postBack('submit','admin/index.jsp');">Save</a>
        </form>
    </div></div>
    <%                    }
        } else {
            out.write("<h1 color=red>Access Denied</h1>");
            response.setStatus(403);
        }
    %> 
