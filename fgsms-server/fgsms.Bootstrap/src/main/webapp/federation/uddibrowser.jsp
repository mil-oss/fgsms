
<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="javax.xml.datatype.DatatypeFactory"%>
<%@page import="javax.xml.datatype.Duration"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormatSymbols"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Properties"%>

<%@page import="java.util.Map"%>
<%@page import="java.net.*"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../csrf.jsp" %>
<div class="well">
    <h1>UDDI</h1>
    <p>The standardized web service discovery service</p>
    <p><a class="btn btn-primary btn-large">Learn more &raquo;</a></p>
</div>
<div class="row-fluid">
    <div class="span12">

        <%
 
            SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");

            ProxyLoader pl = ProxyLoader.getInstance(application);
            if (pl.uddiConfigured()) {
                try {
                    UDDIConfig uconfig = pl.GetUDDIInquiryConfig(application, request, response);
                    out.write("<h3>Connected to: " + Utility.encodeHTML(uconfig.inquiryendpoint) + "</h3>");
                    UddiInquiry ui = new UddiInquiry(uconfig);
                    out.write(ui.InquiryMonitoredServices());
                } catch (Exception ex) {
                    LogHelper.getLog().log(Level.WARN, "Error fetching data from configured uddi registry. if one is not configured or available, ignore this message." + ex.getLocalizedMessage());
                    out.write("Not Availabile.");
                }
            } else {
                out.write("Not configured. <br>");
               %>
               Administrators can configure the UDDI browser here: 
               <a class="btn btn-primary" href="javascript:loadpage('admin/index.jsp','mainpane');">Configure</a>
               
               <%
            }
        %> </div></div>