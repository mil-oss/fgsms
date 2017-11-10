<%-- 
    Document   : securityLevel
    Created on : May 4, 2011, 1:11:40 PM
    Author     : Alex
--%>


<%@page import="org.miloss.fgsms.common.Constants"%>

<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%> 
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="javax.xml.ws.BindingProvider"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Properties"%>
<%@page import="javax.xml.namespace.QName"%>
<%@page import="javax.xml.bind.JAXBElement"%>
<%@page import="javax.xml.datatype.XMLGregorianCalendar"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.miloss.fgsms.common.Utility"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%> 
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URL"%>
<%@include file="../csrf.jsp" %>
<%
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");

    ProxyLoader pl = ProxyLoader.getInstance(application);



    PCS p2 = pl.GetPCS(application, request, response);


    //if postback, validate form, then set
    if (request.getMethod().equalsIgnoreCase("POST")) {

        String cstr = request.getParameter("classlevel");
        String cav = request.getParameter("caveat");
        if (!Utility.stringIsNullOrEmpty(cstr) && !Utility.stringIsNullOrEmpty(cav)) {
            org.miloss.fgsms.services.interfaces.policyconfiguration.ElevateSecurityLevelRequestMsg req =
                    new ElevateSecurityLevelRequestMsg();
            try {

                req.setClassification(new SecurityWrapper(ClassificationType.fromValue(cstr), cav));
                p2.elevateSecurityLevel(req);
                //response.sendRedirect("index.jsp");
                out.write("Security Level Changed. Please close your browser.");
                
            } catch (Exception ex) {
                response.setStatus(400);
                LogHelper.getLog().log(Level.WARN, "error caught when attempted to elevate security level", ex);
                out.write("There was a problem processing your request. Message:" + ex.getMessage());
                //response.sendRedirect("index.jsp");
            }
        }

    } else {
%>
<div class="well">
    <h1>Security Level</h1>
</div>
<div class="row-fluid">
    <div class="span12">

        Current classification level:

        <%
            String classlevel = "";
            String color = "";
            String cav = "";
            try {
                GetGlobalPolicyRequestMsg req = new GetGlobalPolicyRequestMsg();
                req.setClassification(c);
                GetGlobalPolicyResponseMsg pol = p2.getGlobalPolicy(req);
                classlevel = Utility.ICMClassificationToString(pol.getClassification().getClassification());
                color = Utility.ICMClassificationToColorCodeString(pol.getClassification().getClassification());
                cav = pol.getClassification().getCaveats();
            } catch (Exception ex) {
                out.write("There was an error processing your request: " + ex.getMessage() + "<br>");
            }

        %>
        <div style="background-color: <%=color%>; font-size: larger; text-align: center; width: 50%;"><%=classlevel%> Caveats: <%=Utility.encodeHTML(cav)%></div>
        <Br>
        WARNING: When changing to a classification level higher than confidential, you cannot change back to confidential or below.<br>
        Classification Level: 
        <select name="classlevel">
            <%
                ClassificationType[] v = ClassificationType.values();
                for (int i = 0; i < v.length; i++) {
                    out.write("<option style=\"background-color: " + Utility.ICMClassificationToColorCodeString(v[i])
                            + "\" value=\"" + v[i] + "\">" + Utility.ICMClassificationToString(v[i]) + "</option>");

                }

            %>

        </select><br>
        Caveats: 
        <input type="text" size="60" name="caveat" id="caveat" value="<%=Utility.encodeHTML(cav)%>" /><br>
        <input type="button" title="Elevate Security Level" style="font-size: 14" value="Elevate Security Level" name="raiseSecurityLevel" onclick="javascript:postBack('raiseSecurityLevel','admin/securityLevel.jsp');"
               id="raiseSecurityLevel">
      
    </div></div>
<%

    }

%>