<%-- 
    Document   : MessageLogViewer
    Created on : Jan 6, 2011, 8:46:43 PM  
    Author     : Alex
--%>


<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.util.Properties"%>
<%@page import="org.miloss.fgsms.common.*"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>

<%@page contentType="text/xml" pageEncoding="UTF-8"%>

<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>

<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="org.miloss.fgsms.common.Utility"%> 
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@include file="../csrf.jsp" %>

<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);


    try {

        response.reset();

        DataAccessService dasport = pl.GetDAS(application, request, response);
        

        if (Utility.stringIsNullOrEmpty(request.getParameter("url"))) {
            response.setStatus(403);
            out.write("Check the query parameters and try again");
            return;
        }
        if (Utility.stringIsNullOrEmpty(request.getParameter("id"))) {
            response.setStatus(403);
            out.write("Check the query parameters and try again");
            return;
        }
        if ((Utility.stringIsNullOrEmpty(request.getParameter("response")))
                && (Utility.stringIsNullOrEmpty(request.getParameter("request")))) {
            response.setStatus(403);
            out.write("Check the query parameters and try again");
            return;
        }
        Boolean getresponse = false;
        Boolean getrequest = false;
        if (!Utility.stringIsNullOrEmpty(request.getParameter("response"))) {
            getresponse = Boolean.parseBoolean(request.getParameter("response"));
        }
        if (!Utility.stringIsNullOrEmpty(request.getParameter("request"))) {
            getrequest = Boolean.parseBoolean(request.getParameter("request"));
        }
        if (getresponse == false && getrequest == false) {
            response.setStatus(403);
            out.write("Check the query parameters and try again");
            return;
        }
        GetMessageTransactionLogRequestMsg req = new GetMessageTransactionLogRequestMsg();

        req.setClassification(c);

        req.setTransactionId(request.getParameter("id"));
        req.setURL(request.getParameter("url"));
        GetMessageTransactionLogResponseMsg res = dasport.getMessageTransactionLog(req);

        if (getrequest) {
            if (res != null && res.getRequestMessage() != null && !Utility.stringIsNullOrEmpty(res.getRequestMessage())) {

                response.setContentType("text/xml");
                response.setStatus(200);
                out.write(res.getRequestMessage().trim());
                return;
            } else {
                response.setStatus(500);
                out.write("The message was not found");
            }
        }
        if (getresponse) {
            if (res != null && res.getResponseMessage() != null && !Utility.stringIsNullOrEmpty(res.getResponseMessage())) {
                response.setContentType("text/xml");
                response.setStatus(200);
                out.write(res.getResponseMessage().trim());
                return;
            } else {
                response.setStatus(500);
                out.write("The message was not found");
            }
        }
    } catch (SecurityException ex) {
        response.setStatus(401);
        out.write("Access was denied to the requested resource.");
    } catch (Exception ex) {
        response.setStatus(503);
        LogHelper.getLog().log(Level.WARN, "Error caught", ex);
        out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
    }
%>