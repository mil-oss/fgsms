<%-- 
    Document   : encryptor
    Created on : Aug 9, 2012, 11:18:07 AM
    Author     : Administrator
--%>



<%@page import="org.miloss.fgsms.presentation.LogHelper"%>
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
<!DOCTYPE html>
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Encryptor</h1>
</div>
<div class="row-fluid">
    <div class="span12">
        <% 
            ProxyLoader pl = ProxyLoader.getInstance(application);
            if (request.getUserPrincipal() != null) {
                LogHelper.getLog().log(Level.INFO, "Current User: " + request.getUserPrincipal().getName() + " by " + request.getAuthType() + " ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());

            } else {
                LogHelper.getLog().log(Level.INFO, "Current User: anonymous ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());
            }

            boolean ok = false;
            SecurityWrapper sc = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            if (sc == null) {
                response.sendRedirect("../index.jsp");
            } else {
                String classlevel = Utility.ICMClassificationToString(sc.getClassification());
                String color = Utility.ICMClassificationToColorCodeString(sc.getClassification());
                PCS pcsport = pl.GetPCS(application, request, response);
                try {
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
        %>

        <h1>Password encryptor</h1>
       
            <%
                if (request.getMethod().equalsIgnoreCase("POST")) {
                    if (!Utility.stringIsNullOrEmpty(request.getParameter("freetext"))) {
                        out.write("Cipher Text = " + Utility.EN(request.getParameter("freetext")) + "<br>");
                    }
                }
            %>
            Input <input type="password" name="freetext"><Br>
            <a href="javascript:postBackReRender('submit', 'admin/encryptor.jsp', 'mainpane');" class="btn btn-primary">Encrypt</a>
       
        <%
            }
        %>
</div></div>