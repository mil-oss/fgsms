<%-- 
    Document   : getPluginParametersEditable
    Created on : Feb 27, 2017, 10:24:04 PM
    Author     : localadministrator
--%>

<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.GetPluginInformationResponseMsg"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<%

    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);

    String classname = request.getParameter("classname");
    String plugintype = request.getParameter("plugintype");
    
    if (classname != null && plugintype != null) {
        GetPluginInformationResponseMsg info = Helper.GetPluginInfo(classname, plugintype, pl.GetPCS(application, request, response), c);
        if (info==null){
            out.write("unknown");
            return;
        } else{
            out.write(Utility.encodeHTML(info.getDisplayName()));
            return;
        }
    } else {
        out.print("Huh?");
    }

%> 