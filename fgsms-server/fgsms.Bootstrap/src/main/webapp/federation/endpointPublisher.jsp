<%-- 
    Document   : endpointPublisher
    Created on : Feb 5, 2012, 9:38:00 PM
    Author     : Administrator
--%>


<%@page import="java.util.GregorianCalendar"%>
<%@page import="org.miloss.fgsms.presentation.UddiInquiry"%>
<%@page import="org.uddi.api_v3.FindBusiness"%>
<%@page import="org.miloss.fgsms.presentation.UDDIConfig"%>
<%@page import="org.miloss.fgsms.presentation.ProxyLoader"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.PolicyType"%>
<%@page import="java.util.Enumeration"%>
<%@page import="javax.xml.datatype.Duration"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="java.util.Collections"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="org.miloss.fgsms.common.Constants"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>

<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%>
<%@page import="org.miloss.fgsms.presentation.Helper"%>
<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>UDDI</h1>
    <p>The standardized web service discovery service</p>
    <p><a class="btn btn-primary btn-large" href="javascript:loadpage('help/uddi.jsp','mainpane');">Learn more &raquo;</a></p>
</div>
<div class="row-fluid">
    <div class="span12">
     

        This page will publish a set of endpoints that are monitored by FGSMS into a Discovery Service, such as UDDI.<br>
        <% 
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
            ProxyLoader pl = ProxyLoader.getInstance(application);
            if (pl.getAuthmode() == AuthMode.UsernamePassword) {
                //out.write("This will publish endpoint to uddi as you");
            }
        %><br>
        <%
            if (request.getMethod().equalsIgnoreCase("post")) {
                if (!Utility.stringIsNullOrEmpty(request.getParameter("toUddi"))) {
                    UDDIConfig cfg = pl.GetUDDIInquiryConfig(application, request, response);
                    out.write("<br>The next step is to identify which business entity in UDDI that you wish to attach the selected services to.<br>");
                    UddiInquiry ui = new UddiInquiry(cfg);
                    out.write(ui.InquiryUDDIBusnessesAsHtmlSelect());
                    out.write("<br><Br>");
                    DataAccessService port = pl.GetDAS(application, request, response);
                    GetMonitoredServiceListRequestMsg req = new GetMonitoredServiceListRequestMsg();
                    req.setClassification(c);;
                    GetMonitoredServiceListResponseMsg res = port.getMonitoredServiceList(req);
                    if (res != null && res.getServiceList() != null && res.getServiceList() != null) {
                        out.write("<table border=1><tr><th></th><th>URI</th><th>Type</th><th>Display Name</th></tr>");
                        for (int i = 0; i < res.getServiceList().getServiceType().size(); i++) {


                            out.write("<tr><td><input type=checkbox name=\"publish_item\" value=\"" + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "\"/></td><td>");
                            out.write(Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getURL()) + "</td><td>"
                                    + Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getPolicyType().value()) + "</td><td>");
                            if (Utility.stringIsNullOrEmpty((res.getServiceList().getServiceType().get(i).getDisplayName()))) {
                                out.write("(Not defined)");
                            } else {
                                out.write(Utility.encodeHTML(res.getServiceList().getServiceType().get(i).getDisplayName()));
                            }
                            out.write("</td></tr>");

                        }
                        out.write("</table>");
                        out.write("<input type=checkbox name=\"updatepolicies\" value=\"update\"/>Update or Create UDDI Federation on the selected policies?<Br>");
                        out.write("<a href=\"javascript:postBackReRender('submittoUddi','federation/endpointPublisher.jsp','mainpane');\" class=\"btn btn-primary\">Publish</a><Br>");
                    }
                }
           

             


                //go publish the endpoints already
                if (!Utility.stringIsNullOrEmpty(request.getParameter("submittoUddi"))) {
                    //out.write("<h1>Sorry, this function hasn't been written yet.</h1>");
                    //get the urls
                    String[] urls = request.getParameterValues("publish_item");
                    String businesskey = request.getParameter("uddibusinesskey");
                    if (urls == null || urls.length == 0) {
                        out.write("You must select at least one endpoint to publish");
                    } else {

                        UDDIConfig cfg = pl.GetUDDIInquiryConfig(application, request, response);
                        //          out.write("<br>The next step is to identify which business entity in UDDI that you wish to attach the selected services to.<br>");
                        UddiInquiry ui = new UddiInquiry(cfg);
                        if (!Utility.stringIsNullOrEmpty(request.getParameter("updatepolicies"))) {
                            out.write(ui.PublishEndpoints(urls, pl.GetPCS(application, request, response), c, businesskey, true));
                        } else {
                            out.write(ui.PublishEndpoints(urls, pl.GetPCS(application, request, response), c, businesskey, false));
                        }
                        //get policies for all of these objects
                        //make sure enough information is there
                    }
                }



            } else {

                if (pl.uddiConfigured()) {
                    //  out.write("<input type=\"submit\" name=\"toUddi\" value=\"Publish Endpoints to UDDI (Register a Service)\"><Br>");
                    out.write("<a href=\"javascript:postBackReRender('toUddi','federation/endpointPublisher.jsp','mainpane');\" class=\"btn btn-primary\">Publish Endpoints to UDDI (Register a Service)</a><Br>");
                }
               
                if (!pl.uddiConfigured()) {
                    out.write("<br><h3>Note: The web interface must be configured for UDDI. Endpoint publishing is not possible.</h3>");
                }
        %>
        Administrators can setup the connections here: <a class="btn btn-primary btn-large" href="javascript:loadpage('admin/index.jsp','mainpane');">Configure</a>
        <%
            }
            try {
            } catch (Exception ex) {
                out.write("There was an error processing your request. Message: " + ex.getLocalizedMessage());
            }


        %> 



    </div> </div>
