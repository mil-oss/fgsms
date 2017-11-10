<%-- 
    Document   : agents
    Created on : May 17, 2011, 2:45:49 PM
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
    <h1>Agents</h1>
</div>
<div class="row-fluid">
    <div class="span12">




        <% 
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);

        %>

        <%
            out.write("The following is a listing of all agents that have reported back a specific web service transaction. "
                    + "This list only includes agents that are capable of reporting specific transactions. Message Broker and Operating System level"
                    + " agents are typically not listed.<br>");

            try {
                DataAccessService dasport = pl.GetDAS(application, request, response);



                GetAgentTypesRequestMsg req = new GetAgentTypesRequestMsg();
                req.setClassification(c);
                GetAgentTypesResponseMsg res = null;
                res = dasport.getAgentTypes(req);

                if (res != null && !res.getAgentType().isEmpty()) {
                    out.write("<ul>");
                    for (int i = 0; i < res.getAgentType().size(); i++) {
                        out.write("<li>" + Utility.encodeHTML(res.getAgentType().get(i)) + "</li>");
                    }
                    out.write("</ul>");
                } else {
                    out.write("No results were returned.");
                }

            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getLocalizedMessage());
            }


        %>

    </div>
</div>