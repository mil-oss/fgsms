<%-- 
    Document   : dependencies
    Created on : Aug 13, 2012, 12:57:12 PM
    Author     : Administrator
--%>


<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>

<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.List"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
    if (Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
        out.write("Unknown URI");
    } else { 
        if (request.getUserPrincipal() != null) {
            LogHelper.getLog().log(Level.INFO, "Current User: " + request.getUserPrincipal().getName() + " by " + request.getAuthType() + " ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());

        } else {
            LogHelper.getLog().log(Level.INFO, "Current User: anonymous ip " + request.getRemoteAddr() + " request: " + request.getRequestURI() + " method: " + request.getMethod());
        }

        SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
        ProxyLoader pl = ProxyLoader.getInstance(application);
        try {

            DataAccessService dasport = pl.GetDAS(application, request, response);
            GetServiceDependenciesRequestMsg r = new GetServiceDependenciesRequestMsg();
            r.setClassification(c);
            r.setUri(request.getParameter("uri"));

            GetServiceDependenciesResponseMsg res = dasport.getServiceDependencies(r);
            boolean found = false;
            if (!res.getIdependon().isEmpty()) {
                out.write("This service depends on:<Br>");
                found = true;
                for (int i = 0; i < res.getIdependon().size(); i++) {
                    out.write(Utility.encodeHTML(res.getIdependon().get(i).getUrl() + " " + res.getIdependon().get(i).getAction()) + "<br>");
                }
            }
            if (!res.getIdependon().isEmpty()) {
                found = true;
                out.write("These services depend on me:<Br>");
                for (int i = 0; i < res.getDependonme().size(); i++) {
                    out.write(Utility.encodeHTML(res.getDependonme().get(i).getUrl() + " " + res.getDependonme().get(i).getAction()) + "<br>");
                }
            }
            if (!found) {
                out.write("No dependencies found.<Br>");
            }
        } catch (Exception ex) {
            out.write("There was an error processing your request<Br>");
            LogHelper.getLog().log(Level.INFO, "dependenies", ex);
        }
    }
%>
