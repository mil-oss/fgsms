<%@page import="org.miloss.fgsms.presentation.LogHelper"%><%@page import="org.miloss.fgsms.presentation.ProxyLoader"%><%@page import="org.miloss.fgsms.common.Constants"%><%@page import="org.miloss.fgsms.common.Constants.AuthMode"%><%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%><%@page import="org.miloss.fgsms.common.Logger"%><%@page import="org.apache.log4j.Level"%><%@page import="us.gov.ic.ism.v2.ClassificationType"%><%@page import="javax.xml.bind.JAXBElement"%><%@page import="java.math.BigInteger"%><%@page import="java.util.UUID"%><%@page import="org.miloss.fgsms.services.interfaces.dataaccessservice.*"%><%@page import="java.net.URLEncoder"%><%@page import="java.util.Properties"%><%@page import="java.util.Enumeration"%><%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%><%@page import="org.miloss.fgsms.presentation.Helper"%><%@page import="java.net.URL"%><%@page import="org.miloss.fgsms.common.Utility"%><%@page import="java.util.Map"%><%@page import="javax.xml.ws.BindingProvider"%><%@page contentType="text/xml" pageEncoding="UTF-8"%><%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
ProxyLoader pl = ProxyLoader.getInstance(application);
    ServicePolicy pol = null;
    try {
        
        PCS pcsport = pl.GetPCS(application, request, response);
        
        ServicePolicyRequestMsg req = new ServicePolicyRequestMsg();
        req.setURI(request.getParameter("url"));
        req.setClassification(c);
        ServicePolicyResponseMsg res = pcsport.getServicePolicy(req);
        if (res != null && res.getPolicy() != null) {
            pol = res.getPolicy();
 
        }
        out.write(Helper.ExportServicePolicy(pol));
    } catch (Exception ex) {
      LogHelper.getLog().log(Level.ERROR, "error caught exporting policy. report this to the developer", ex);
      out.write("There was a problem proessing your request. Please contact an administrator to report this issue or visit https://github.com/mil-oss/fgsms and report it.");
    }
%>
