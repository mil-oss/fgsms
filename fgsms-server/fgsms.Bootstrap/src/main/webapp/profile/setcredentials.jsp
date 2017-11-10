<%-- 
    Document   : setcredentials
    Created on : Jan 19, 2012, 11:53:45 AM
    Author     : Administrator
--%>

<%@page import="java.net.URLEncoder"%>
<%@page import="org.miloss.fgsms.common.Constants"%>


<%@page import="org.miloss.fgsms.common.Constants.AuthMode"%>
<%@page import="org.miloss.fgsms.services.interfaces.common.SecurityWrapper"%>
<%@page import="org.miloss.fgsms.common.Logger"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="us.gov.ic.ism.v2.ClassificationType"%>
<%@page import="org.miloss.fgsms.services.interfaces.policyconfiguration.*"%>
<%@page import="java.util.Map"%>
<%@page import="javax.xml.ws.BindingProvider"%>

<%@page import="java.util.Properties"%>
<%@page import="java.net.URL"%>
<%@page import="org.miloss.fgsms.presentation.*"%>
<%@page import="org.miloss.fgsms.common.Utility"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">


 
<%


    if (Utility.stringIsNullOrEmpty(request.getParameter("uri"))) {
        response.sendRedirect("../index.jsp");
        return;
    }
    SecurityWrapper c = (SecurityWrapper) request.getSession().getAttribute("currentclassification");
    ProxyLoader pl = ProxyLoader.getInstance(application);

 
    PCS pcsport = pl.GetPCS(application, request, response);



    if (request.getMethod().equalsIgnoreCase("post")) {

        try {
            String error = "";
            TransportAuthenticationStyle tas = TransportAuthenticationStyle.NA;
            if (Utility.stringIsNullOrEmpty(request.getParameter("uname"))) {
                error += "Username cannot be blank<br>";
            }
            if (Utility.stringIsNullOrEmpty(request.getParameter("authtype"))) {
                error += "Auth type must be specified";
            } else {
                try {
                    tas = TransportAuthenticationStyle.fromValue(request.getParameter("authtype"));
                } catch (Exception ex) {
                }
            }
            if (Utility.stringIsNullOrEmpty(request.getParameter("pwd"))) {
                error += "Password cannot be blank<br>";
            }
            if (Utility.stringIsNullOrEmpty(request.getParameter("pwdverify"))) {
                error += "Password Verification cannot be blank<br>";
            }
            if (Utility.stringIsNullOrEmpty(error)) {
                if (!request.getParameter("pwd").equalsIgnoreCase(request.getParameter("pwdverify"))) {
                    error += "Passwords do not match<br>";
                }
            }
            if (!Utility.stringIsNullOrEmpty(error)) {
                out.write("<font color=red>" + error + "</font><Br>");
            } else {
                SetCredentialsRequestMsg req = new SetCredentialsRequestMsg();
                req.setUrl(request.getParameter("uri"));
                req.setClassification(c);
                req.setUsername(request.getParameter("uname"));
                req.setStyle(tas);
                req.setPassword(Utility.EN(request.getParameter("pwd")));
                req.setPasswordEncrypted(true);
                pcsport.setCredentials(req);
                out.write("<br>Credentials Successfully Saved.<Br>");
            }
        } catch (Exception ex) {
            out.write("<br><font color=#FF0000>There was a problem processing your request. Message:" + ex.getLocalizedMessage() + "</font><br>");
            LogHelper.getLog().log(Level.ERROR, "Error from removecredentials.jsp", ex);
        }
    } else if (Utility.stringIsNullOrEmpty(request.getParameter("uri"))){
        //http get with no uri
         //send redirect? hope this doesn't break the "make a new policy workflow"
        response.sendRedirect("../index.jsp");
        return;
        } 
        else {

%>
<div class="well">
    <h1>Set Credentials</h1>
    <p>FGSMS sometimes needs credentials to verify a connection or a service's status.</p>
</div>
<div class="row-fluid">
    <div class="span12">
<% 
   out.write("<h3>URL: " + Utility.encodeHTML(request.getParameter("uri")) + "</h3>");

    out.write("<a href=\"manage.jsp?url=" + Utility.encodeHTML(request.getParameter("uri")) + "\">Manage</a> | "
            + " <a href=\"performanceViewer.jsp?url=" + Utility.encodeHTML(request.getParameter("uri")) + "\">View Performance</a> | "
            + " <a href=\"serviceprofile.jsp?url=" + Utility.encodeHTML(request.getParameter("uri")) + "\">View Profile</a> | "
            + " <a href=\"TransactionLogViewer.jsp?url=" + Utility.encodeHTML(request.getParameter("uri")) + "\">View Transaction Log</a> | "
            + "<a href=\"availability.jsp?url=" + Utility.encodeHTML(request.getParameter("uri")) + "\">Availability</a> | "
            + "<a href=\"slarecords.jsp?url=" + Utility.encodeHTML(request.getParameter("uri")) + "\">SLA Violations</a><Br>");

    %>
        <script>
            function checkPassword()
            {
                var p0=document.getElementById("uname");
                var p1=document.getElementById("pwd");
                var p2=document.getElementById("pwdverify");
                if (p0=="")
                {
                    alert("The username cannot be blank.");
                    return false;
                }
                if (p1=="")
                {
                    alert("The password cannot be blank.");
                    return false;
                }
                if (p1.value == p2.value)
                {
                    document.forms["form1"].submit();
                }
                else
                {
                    alert("Passwords do not match");
                    return false;
                }
    
            }
        </script>

        <table>
            <tr><td>Username</td><td><input type="text" name="uname"/></td></tr>
            <tr><td>Password</td><td><input type="password" name="pwd"/></td></tr>
            <tr><td>Verify</td><td><input type="password"name="pwdverify"/></td></tr>
            <tr><td>Type</td><td>
                    <select name="authtype" id="authtype">

                        <%
                            for (int i = 0; i < TransportAuthenticationStyle.values().length; i++) {
                                out.write("<option value=\"" +Utility.encodeHTML(TransportAuthenticationStyle.values()[i].value()) + "\">" + Utility.encodeHTML(TransportAuthenticationStyle.values()[i].value()) + "</option>");
                            } 

                        %>

                    </select>


                </td></td></tr>
        </table>
        <a href="javascript:postBack('submit','profile/setcredentials.jsp?uri=<%= URLEncoder.encode(request.getParameter("uri"))%>');" class="btn btn-primary">Set Credential</a><br><Br>

        Description: For a variety of different types of services, FGSMS may need credentials in order to authenticate to it for the purposes of checking or testing the status of the object. This account should
        be a least privilege account. Credentials are stored in an encrypted format.




    </div>
</div>
<% 
}%>