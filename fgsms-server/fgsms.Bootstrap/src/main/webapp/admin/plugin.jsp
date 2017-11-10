<%-- 
    Document   : plugins
    Created on : Dec 23, 2013, 2:45:49 PM
    Author     : Alex
--%>


<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.Properties"%>
<%@page import="org.miloss.fgsms.presentation.*"%>

<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>Plugins</h1>
</div>
<div class="row-fluid">
    <div class="span12"> 




        <%
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);

        %>

        <%
            out.write("The following is a listing of registered plugins.<br>");

            try {

                PCS pcs = pl.GetPCS(application, request, response);
                GetPluginList r = new GetPluginList();
                r.setRequest(new GetPluginListRequestMsg());
                r.getRequest().setClassification(c);

                GetPluginListResponse res = pcs.getPluginList(r);

                if (res != null && res.getResponse() != null) {
                    out.write("<table class=\"table table-hover\">");
                    for (int i = 0; i < res.getResponse().getPlugins().size(); i++) {
                        out.write("<tr>");
                        out.write("<td>" + Utility.encodeHTML(res.getResponse().getPlugins().get(i).getDisplayname()) + "</td>");
                        out.write("<td>" + Utility.encodeHTML(res.getResponse().getPlugins().get(i).getClassname()) + "</td>");
                        out.write("<td>" + Utility.encodeHTML(res.getResponse().getPlugins().get(i).getPlugintype()) + "</td>");
                        out.write("</tr>");
                    }
                    out.write("</table>");
                } else {
                    out.write("No results were returned.");
                }

            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getLocalizedMessage());
            }


        %>

    </div>
</div>